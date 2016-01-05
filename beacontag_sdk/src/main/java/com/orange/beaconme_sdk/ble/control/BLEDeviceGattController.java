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
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.orange.beaconme_sdk.ble.model.BeaconTagDevice;
import com.orange.beaconme_sdk.ble.model.GATTOperation;

import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public abstract class BLEDeviceGattController {
    protected static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final String TAG = this.getClass().getSimpleName();
    private static final long DELAY = 500;


    private volatile BluetoothGatt mGatt;
    protected Handler handler = new Handler(Looper.getMainLooper());

    private Context context;
    private BeaconTagDevice device;

    protected Queue<GATTOperation> mOperations = new ConcurrentLinkedQueue<>();
    protected GATTOperation mCurrentOperation;

    protected boolean forceClosed = false;

    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Try to reconnect");
            connect();
        }
    };

    public BLEDeviceGattController(BeaconTagDevice device, Context context) {
        this.context = context;
        this.device = device;
    }

    public void connect() {
        mGatt = device.getBleDevice().connectGatt(context, false, getCallback());
    }

    protected abstract BluetoothGattCallback getCallback();

    public void forceClose() {
        forceClosed = true;
        close();
    }

    public void close() {
        Log.d(TAG, "Close GATT connection");
        mOperations.clear();
        handler.removeCallbacks(reconnectRunnable);

        if (mGatt != null) {
            mGatt.close();
        }
    }

    protected void reconnect() {
        close();
        if (!forceClosed) {
            Log.d(TAG, "Post reconnect task");
            handler.postDelayed(reconnectRunnable, DELAY);
        }
    }

    public void queue(final GATTOperation op) {
        mOperations.add(op);
        if (mCurrentOperation == null) {
            handleOperation();
        }
    }

    protected void handleOperation() {
        GATTOperation op = mOperations.poll();

        if (op == null)
            return;

        if (mCurrentOperation != null) {
            Log.e(TAG, "Operation not null!");
            return;
        }

        BluetoothGattCharacteristic c = op.getCharacteristic();
        BluetoothGattDescriptor d = op.getDescriptor();

        switch (op.getType()) {
            case READ_CHARACTERISTIC:
                mCurrentOperation = op;
                mGatt.readCharacteristic(c);
                break;
            case WRITE_CHARACTERISTIC:
                mCurrentOperation = op;
                mGatt.writeCharacteristic(c);
                break;
            case READ_DESCRIPTOR:
                mCurrentOperation = op;
                mGatt.readDescriptor(d);
                break;
            case WRITE_DESCRIPTOR:
                mCurrentOperation = op;
                mGatt.writeDescriptor(d);
                break;
            case NOTIFY_START:
                BluetoothGattDescriptor ntfDescriptor = c
                        .getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                if (ntfDescriptor != null) {
                    mGatt.setCharacteristicNotification(c, true);
                    GATTOperation decsriptorWriteOp = new GATTOperation(
                            GATTOperation.OperationType.WRITE_DESCRIPTOR, ntfDescriptor);
                    queue(decsriptorWriteOp);
                }
                break;
            case NOTIFY_END:
                break;
        }

        if (mCurrentOperation == null) {
            processNextOperation();
        }
    }

    protected void processNextOperation() {
        handleOperation();
    }

    protected void completeCurrentOperation(final int status,
                                            final BluetoothGattCharacteristic c) {
        mCurrentOperation.complete(status, c);
        mCurrentOperation = null;
        processNextOperation();
    }

    public GATTOperation getReadCharacteristicOperation(BluetoothGattCharacteristic c) {
        return new GATTOperation(
                GATTOperation.OperationType.READ_CHARACTERISTIC, c);
    }

    public GATTOperation getWriteCharacteristicOperation(BluetoothGattCharacteristic c) {
        return new GATTOperation(GATTOperation.OperationType.WRITE_CHARACTERISTIC, c);
    }

    protected BluetoothGatt getGatt() {
        return mGatt;
    }

    protected Context getContext() {
        return context;
    }

    protected BeaconTagDevice getDevice() {
        return device;
    }

    protected void logOnReadCharacteristic(BluetoothGattCharacteristic c) {
        Log.d(TAG, "  Discovered characteristic " + c.getUuid());
        Log.d(TAG, "  Value = " + Arrays.toString(c.getValue()));
        for (BluetoothGattDescriptor d : c.getDescriptors()) {
            Log.d(TAG, "    Discovered descriptor " + d.getUuid());
        }
    }

    protected void logOnWrittenCharacteristic(BluetoothGattCharacteristic c) {
        Log.d(TAG, "  Wrote characteristic " + c.getUuid());
        Log.d(TAG, "  Value = " + Arrays.toString(c.getValue()));
    }
}
