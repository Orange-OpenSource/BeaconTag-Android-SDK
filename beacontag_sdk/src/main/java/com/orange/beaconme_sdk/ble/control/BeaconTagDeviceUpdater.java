/*
 * Copyright (c) 2015 Orange.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License, which can be found in the file 'LICENSE.txt' in
 * this package distribution or at 'http://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html'
 * for more details.
 *
 * Created by Orange Beacon on 08/6/15.
 */
package com.orange.beaconme_sdk.ble.control;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.orange.beaconme_sdk.ble.model.BeaconTagDevice;
import com.orange.beaconme_sdk.ble.model.WriteCharacteristicCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class BeaconTagDeviceUpdater extends BLEDeviceGattController {
    private final String TAG = this.getClass().getSimpleName();

    public static final UUID UUID_SERVICE_UUID
            = UUID.fromString("59EC0800-0B1E-4063-8B16-B00B50AA3A7E");
    public static final UUID TX_POWER_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a05-0b1e-4063-8b16-b00b50aa3a7e");


    private List<UUID> charUUIDs = Arrays.asList(BeaconTagDevice.SLEEP_CHARACTERISTIC_UUID,
            BeaconTagDevice.TEMPERATURE_CHARACTERISTIC_UUID, BeaconTagDevice.ACCELERATION_CHARACTERISTIC_UUID,
            BeaconTagDevice.ANGULAR_SPEED_CHARACTERISTIC_UUID);

    private Map<UUID, Boolean> uploadingCompletion;

    private List<WriteCharacteristicCommand> commands;
    boolean allowSkipAdvancedService;

    public BeaconTagDeviceUpdater(BeaconTagDevice device, Context context,
                                  List<WriteCharacteristicCommand> commands) {
        this(device, context, commands, false);
    }

    public BeaconTagDeviceUpdater(BeaconTagDevice device, Context context,
                                  List<WriteCharacteristicCommand> commands,
                                  boolean allowSkipAdvancedService) {
        super(device, context);
        Log.i("Updater", "created");
        this.commands = commands;
        this.allowSkipAdvancedService = allowSkipAdvancedService;
        uploadingCompletion = new HashMap<>();
        for (WriteCharacteristicCommand command : commands) {
            uploadingCompletion.put(command.getCharacteristicUUID(), false);
        }
    }

    @Override
    protected BluetoothGattCallback getCallback() {
        return new BeaconUpdateGattCallback();
    }

    private void readCharacteristics() {
        Log.i("Updater", "readCharacteristics");
        for (WriteCharacteristicCommand command : commands) {
            final UUID characteristicUUID = command.getCharacteristicUUID();
            BluetoothGattService service = getGatt().getService(command.getServiceUUID());
            if (service != null) {
                BluetoothGattCharacteristic c = service.getCharacteristic(command.getCharacteristicUUID());
                if (c == null) {
                    doneUploadingUuid(characteristicUUID);
                } else {
                    queue(getReadCharacteristicOperation(c));
                }
            } else if (command.getServiceUUID()
                    .equals(BeaconTagDevice.WAKE_UP_SERVICE_UUID) && allowSkipAdvancedService) {
                doneUploadingUuid(characteristicUUID);
            } else {
                close();
                return;
            }
        }
    }

    private void onReadCharacteristic(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt, int status) {
        Log.i("Updater", "onReadCharacteristic");
        UUID charUUID = characteristic.getUuid();
        for (WriteCharacteristicCommand command : commands) {
            if (command.getCharacteristicUUID().equals(charUUID)) {
                if (characteristic.getValue() == null) {
                    Log.e(TAG, String.format("read characteristic %s value=%s, status=%d",
                            characteristic.getUuid(), Arrays.toString(characteristic.getValue()), status));
                } else {
                    byte[] newValue;
                    if (command.getSwitchState() != WriteCharacteristicCommand.SwitchState.NONE) {
                        newValue = getEnablingValueForChar(characteristic,
                                command.getSwitchState() == WriteCharacteristicCommand.SwitchState.ENABLE);

                    } else {
                        newValue = command.getBytesToUpload();
                    }
                    if (!Arrays.equals(characteristic.getValue(), newValue)) {
                        characteristic.setValue(newValue);
                        queue(getWriteCharacteristicOperation(characteristic));
                    } else {
                        doneUploadingUuid(characteristic.getUuid());
                    }
                }
            }
        }
    }

    private byte[] getEnablingValueForChar(BluetoothGattCharacteristic characteristic,
                                         boolean enable) {
        byte[] newValue = Arrays.copyOf(characteristic.getValue(),
                characteristic.getValue().length);
        if (characteristic.getUuid().equals(BeaconTagDevice.SLEEP_CHARACTERISTIC_UUID)) {
            newValue[0] = 0;
            newValue[1] = 0;
        } else {
            newValue[0] = (byte) (enable ? 1 : 0);
        }
        return newValue;
    }

    private void checkIfUploadingComplete() {
        for (UUID uuid : uploadingCompletion.keySet()) {
            if (!uploadingCompletion.get(uuid)) {
                return;
            }
        }
        onComplete();
    }


    private void onComplete() {
        Log.i("Updater", "onComplete");
        close();
        Intent intent = new Intent(BLEDeviceManager.ACTION_DEVICE_UPDATED);
        intent.putExtra(BLEDeviceManager.FOOTPRING_TAG, getDevice().getFootprint());
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

    }

    private void doneUploadingUuid(UUID uuid) {
        if (uploadingCompletion.containsKey(uuid)) {
            uploadingCompletion.put(uuid, true);
        }
        checkIfUploadingComplete();
    }

    class BeaconUpdateGattCallback extends BluetoothGattCallback {

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            logOnWrittenCharacteristic(characteristic);
            doneUploadingUuid(characteristic.getUuid());
            completeCurrentOperation(status, characteristic);
        }

        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            logOnReadCharacteristic(characteristic);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                reconnect();
            }
            onReadCharacteristic(characteristic, gatt, status);
            completeCurrentOperation(status, characteristic);
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            Log.d(TAG, String.format(
                    "onConnectionStateChange status=%d, newState=%d", status,
                    newState));

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (gatt.discoverServices()) {
                    Log.d(TAG,
                            String.format("discoverServices failed"));
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                reconnect();
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered status=" + status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                reconnect();
            } else {
                readCharacteristics();
            }
        }
    }
}
