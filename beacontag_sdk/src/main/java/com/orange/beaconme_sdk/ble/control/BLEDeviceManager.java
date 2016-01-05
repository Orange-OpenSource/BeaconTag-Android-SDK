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

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.orange.beaconme_sdk.ble.model.BeaconTagDevice;
import com.orange.beaconme_sdk.ble.model.DeviceFootprint;
import com.orange.beaconme_sdk.ble.model.IBeaconDetect;
import com.orange.beaconme_sdk.ble.model.WriteCharacteristicCommand;
import com.orange.beaconme_sdk.ble.utils.GattUtils;
import com.orange.beaconme_sdk.control.BeaconMonitor;
import com.orange.beaconme_sdk.control.model.BeaconSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 */
public class BLEDeviceManager {
    private final String TAG = this.getClass().getSimpleName();

    public static final String ACTION_CONNECT_TO_DEVICE = "com.orange.beacon_sdk.CONNECT_TO_DEVICE";
    public static final String ACTION_DEVICE_CONNECTED = "com.orange.beacon_sdk.DEVICE_CONNECTED";
    public static final String ACTION_DEVICE_UPDATE_COMPLETE = "com.orange.beacon_sdk.DEVICE_UPDATE_COMPLETE";
    public static final String ACTION_DEVICE_UPDATED = "com.orange.beacon_sdk.DEVICE_UPDATED";
    public static final String ACTION_DEVICE_CHOSEN = "com.orange.beacon_sdk.DEVICE_CHOSEN";
    public static final String ACTION_DEVICE_CONNECTION_ABORT = "com.orange.beacon_sdk.DEVICE_CONNECTION_ABORT";
    public static final String FOOTPRING_TAG = "footprint_tag";

    private static BLEDeviceManager instance;

    private Context context;

    private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if (bluetoothState == BluetoothAdapter.STATE_ON) {
                launchService();
            }
        }
    };

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private Map<String, BeaconTagDevice> devices = new ConcurrentHashMap<>();
    private Map<String, BLEDeviceGattController> deviceControllers = new ConcurrentHashMap<>();
    private Map<DeviceFootprint, List<WriteCharacteristicCommand>> devicesConfigurations = new HashMap<>();

    public static BLEDeviceManager getInstance() {
        return instance;
    }

    /**
     * Initialize the BLEDeviceManager
     * @param context Application Context
     */
    public static void init(Context context) {
        instance = new BLEDeviceManager(context);
    }

    private BLEDeviceManager(Context context) {
        this.context = context.getApplicationContext();
        launchService();
        context.registerReceiver(bluetoothBroadcastReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    /**
     * Method invoked when device in cofiguration mode was detected with
     * BLEDeviceScanner.UUID_SERVICE_UUID in a list of services
     * @param address physical address of the iBeacon
     * @param device object represents the iBeacon
     * @param detection corresponding detection object with detection information
     */
    public void onDeviceFound(String address, final BeaconTagDevice device, IBeaconDetect detection) {
        if (!devices.containsKey(address) && devicesConfigurations.containsKey(detection.getFootprint())) {
            final BLEDeviceGattController controller = new BeaconTagDeviceUpdater(device, context,
                    devicesConfigurations.get(detection.getFootprint()));
            Log.d(TAG, "found device " + address);
            mainThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    controller.connect();
                }
            }, 100);
            deviceControllers.put(address, controller);
            devices.put(address, device);
        }
    }

    /**
     * Method invoked when device is no longer in configuration mode, results in removing device
     * from configuration cache
     * @param address address of the device
     * @return device information if device was removed, null else
     */
    public BeaconTagDevice removeDeviceFromConfigurationCache(String address) {
        if (devices.containsKey(address)) {
            deviceControllers.remove(address).forceClose();
            return devices.remove(address);
        }
        return null;
    }

    /**
     * Clear configuration cache and stops all device updating.
     * Indicates that scanner is going to stop.
     */
    public void clear() {
        for (String key : devices.keySet()) {
            deviceControllers.remove(key).forceClose();
            devices.remove(key);
        }
    }


    /**
     * Method invokes when iBeacon device was detected
     * @param detection information about detection
     */
    public void onDetect(IBeaconDetect detection) {
        BeaconMonitor.getInstance().onDetect(detection);
    }

    /**
     * Add device for detection. When such a device will be detected in configuration mode it will
     * be updated accodring to settings
     * @param settings settings for device to be updated with.
     */
    public void addDeviceForDetection(BeaconSettings settings) {
        devicesConfigurations.put(settings.getDeviceFootprint(), GattUtils.beaconSettingsToCommands(settings));
    }

    /**
     * Remove device from detection.
     * @param footprint footprint of the device
     */
    public void removeDeviceForDetection(DeviceFootprint footprint) {
        devicesConfigurations.remove(footprint);
    }

    /**
     * Starts BLEDeviceScanner Service
     */
    private void launchService() {
        context.startService(new Intent(context, BLEDeviceScanner.class));
    }
}
