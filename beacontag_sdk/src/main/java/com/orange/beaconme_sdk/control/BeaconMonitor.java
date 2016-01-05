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
package com.orange.beaconme_sdk.control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.orange.beaconme_sdk.ble.control.BLEDeviceManager;
import com.orange.beaconme_sdk.ble.control.BLEDeviceScanner;
import com.orange.beaconme_sdk.ble.model.DeviceFootprint;
import com.orange.beaconme_sdk.ble.model.IBeaconDetect;
import com.orange.beaconme_sdk.control.detection_handlers.DetectionHandlerFactory;
import com.orange.beaconme_sdk.control.detection_handlers.TagDetectionHandler;
import com.orange.beaconme_sdk.control.model.BeaconSettings;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class BeaconMonitor {

    /**
     * When event is triggered broadcast with TRIGGER_EVENT_ACTION as an action will be send.
     */
    public static final String TRIGGER_EVENT_ACTION = "com.orange.beaconme_sdk.action.TRIGGER_EVENT";

    /**
     * When event is triggered broadcast intent will contain DeviceFootpring object under
     * DEVICE_FOOTPRINT_TAG key
     */
    public static final String DEVICE_FOOTPRINT_TAG = "Device Footprint Tag";

    private static BeaconMonitor instance;
    
    private TagDetectionHandler.OnTriggerFiredListener triggerListener =
            new TagDetectionHandler.OnTriggerFiredListener() {
        @Override
        public void onFired(DeviceFootprint footprint) {
            alertDetection(footprint);
        }
    };
    
    private final ConcurrentHashMap<DeviceFootprint, TagDetectionHandler> tagsOnDetection
            = new ConcurrentHashMap<>();


    private final ConcurrentHashMap<DeviceFootprint, BeaconUpdatedCallback> updateCallbacks =
            new ConcurrentHashMap<>();
    private BroadcastReceiver deviceUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DeviceFootprint footprint = (DeviceFootprint) intent.getSerializableExtra(
                    BLEDeviceManager.FOOTPRING_TAG);
            if (updateCallbacks.containsKey(footprint)) {
                updateCallbacks.get(footprint).onBeaconUpdated(footprint.getUuid(),
                        footprint.getMajor(), footprint.getMinor());
            }
        }
    };

    /**
     * Initialize the Beacon Monitor
     * @param context Application Context
     * @return initialized instance of Beacon Monitor
     */
    public static BeaconMonitor init(Context context) {
        BLEDeviceManager.init(context);
        instance = new BeaconMonitor(context);
        return instance;
    }

    /**
     * Return an instance of Beacon Monitor if it was initialized, null else.
     * @return instance of Beacon Monitor
     */
    public static BeaconMonitor getInstance() {
        return instance;
    }

    private Context context;

    private BeaconMonitor(Context context) {
        this.context = context;
        LocalBroadcastManager.getInstance(context).registerReceiver(deviceUpdatedReceiver,
                new IntentFilter(BLEDeviceManager.ACTION_DEVICE_UPDATED));
    }

    /**
     * According to provided settings, register beacon for detection. If beacon appears in
     * configuration mode, then it will be reconfigurated according to settings. Once event
     * defined in settings.getAreaSettings() occurs, LocalBroadcast with
     * TRIGGER_EVENT_ACTION action will be send, with a DeviceFootprint in extras under
     * DEVICE_FOOTPRINT_TAG key.
     *
     * @param settings device settings to detect.
     * @param callback will be invoked when device will be updated
     */
    public void registerForBeaconDetection(BeaconSettings settings, BeaconUpdatedCallback callback) {
        DeviceFootprint footprint = settings.getDeviceFootprint();
        if (tagsOnDetection.contains(footprint)) {
            unregisterForBeaconDetection(footprint);
        }

        if (tagsOnDetection.isEmpty()) {
            startScan();
        }
        TagDetectionHandler handler = DetectionHandlerFactory.getHandler(footprint,
                triggerListener, settings.getAreaSettings());
        if (handler != null) {
            tagsOnDetection.put(footprint, handler);
        }
        updateCallbacks.put(footprint, callback);
        BLEDeviceManager.getInstance().addDeviceForDetection(settings);
    }

    /**
     * When iBeacon is detected this method is invoked with appropriate information in detection object
     * @param detection object with information about detection
     */
    public void onDetect(IBeaconDetect detection) {
        DeviceFootprint footprint = detection.getFootprint();
        if (tagsOnDetection.containsKey(footprint)) {
            tagsOnDetection.get(footprint).onDetect(detection);
        }
    }

    /**
     * Remove beacon from detection.
     * @param footprint unifies device which no longer should be detected.
     */
    private void unregisterForBeaconDetection(DeviceFootprint footprint) {
        if (tagsOnDetection.containsKey(footprint)) {
            tagsOnDetection.remove(footprint).deactivate();
            updateCallbacks.remove(footprint);
            BLEDeviceManager.getInstance().removeDeviceForDetection(footprint);
        }
    }

    /**
     * Check if Bluetooth is enabled. If it is enabled then start scanner, ask to enable Bluetooth otherwise.
     */
    private void startScan() {
        BluetoothAdapter btAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (btAdapter == null || !btAdapter.isEnabled())  {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
        } else {
            context.sendBroadcast(new Intent(BLEDeviceScanner.START_SCAN_SERVICE_ACTION));
        }
    }

    /**
     * Send broadcast with TRIGGER_EVENT_ACTION as an action and DeviceFootprint in extra under
     * DEVICE_FOOTPRINT_TAG key. Notifies all broadcastReceivers when event is triggered.
     * @param footprint DeviceFootprint to send broadcast with
     */
    private void alertDetection(DeviceFootprint footprint) {
        Intent event = new Intent(TRIGGER_EVENT_ACTION);
        event.putExtra(DEVICE_FOOTPRINT_TAG, footprint);
        context.sendBroadcast(event);
    }
}
