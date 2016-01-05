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

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.orange.beaconme_sdk.ble.model.BeaconTagDevice;
import com.orange.beaconme_sdk.ble.model.IBeaconDetect;
import com.orange.beaconme_sdk.ble.model.ScanRecord;
import com.orange.beaconme_sdk.ble.utils.ByteArrayUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class BLEDeviceScanner extends Service {

    private final String TAG = this.getClass().getSimpleName();

    private static final ParcelUuid UUID_SERVICE_UUID = new ParcelUuid(BeaconTagDevice.UUID_SERVICE_UUID);
    public static String START_SCAN_SERVICE_ACTION = "com.orange.beaconconnect.START_SCAN_SERVICE_ACTION";
    public static String STOP_SCAN_SERVICE_ACTION = "com.orange.beaconconnect.STOP_SCAN_SERVICE_ACTION";

    private BluetoothAdapter mAdapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }

    };

    private IntentFilter intentFilter;

    public BLEDeviceScanner() {
        intentFilter = new IntentFilter(STOP_SCAN_SERVICE_ACTION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bluetoothManager.getAdapter();

        if (mAdapter != null && mAdapter.isEnabled()) {
            startScanning();
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .registerReceiver(receiver, intentFilter);
        } else {
            stopSelf();
        }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopScanning();
        stopReScanTimer();
        mReScanTimer.cancel();
    }

    BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            ScanRecord sr = ScanRecord.parseFromBytes(scanRecord);
            Log.d("SCAN RECORD", sr.toString());
            boolean isInConnectionState = sr.getServiceUuids() != null && sr.getServiceUuids()
                    .contains(UUID_SERVICE_UUID);
            if (!isInConnectionState) {
                getDeviceManager().removeDeviceFromConfigurationCache(device.getAddress());
            }

            IBeaconDetect detection = isBeaconScan(rssi, scanRecord);
            if (detection != null) {
                getDeviceManager().onDetect(detection);
            }

            if (isInConnectionState) {
                getDeviceManager().onDeviceFound(device.getAddress(),
                        new BeaconTagDevice(device, detection.getFootprint()), detection);
            }
        }
    };

    private boolean startScanning() {
        if (mAdapter.startLeScan(mScanCallback)) {
            startReScanTimer();
            return true;
        }
        return false;
    }

    private void stopScanning() {
        stopReScanTimer();
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (bluetoothAdapter != null) {
            try {
                bluetoothAdapter.stopLeScan(mScanCallback);
            } catch (NullPointerException ex) {
                //prevent OBGM-215 issue. For proper fix more information needed
            }
        }
        getDeviceManager().clear();
    }


    private Timer mReScanTimer = new Timer();
    private TimerTask mReScanTimerTask;

    private void startReScanTimer() {

        mReScanTimerTask = new TimerTask() {

            @Override
            public void run() {
                if (mAdapter != null) {
                    try {
                        mAdapter.stopLeScan(mScanCallback);
                    } catch (NullPointerException ex) {
                        //prevent bug on Samsung devices : mAdapter is not null but raise NPE on stop method
                    }
                    mAdapter.startLeScan(mScanCallback);
                }
            }

        };
        mReScanTimer.schedule(mReScanTimerTask, 0L, 2000L);
    }

    private void stopReScanTimer() {
        if (mReScanTimerTask != null)
            mReScanTimerTask.cancel();
        mReScanTimer.purge();
    }


    private IBeaconDetect isBeaconScan(int rssi, byte[] scanRecord) {
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String uuid = ByteArrayUtils.bytesToUUID(uuidBytes);
            int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
            int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
            int txPower = scanRecord[startByte + 24];
            IBeaconDetect detection = new IBeaconDetect(uuid, major, minor, rssi, txPower);
            Log.d("RANGE", detection.getRange() + " range for distance of " + detection.getDistance() + "m to " + uuid);
            return detection;
        }
        return null;
    }

    private BLEDeviceManager getDeviceManager() {
        return BLEDeviceManager.getInstance();
    }

}
