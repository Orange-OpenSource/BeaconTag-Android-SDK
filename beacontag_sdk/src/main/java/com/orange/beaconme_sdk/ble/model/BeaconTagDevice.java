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
package com.orange.beaconme_sdk.ble.model;

import android.bluetooth.BluetoothDevice;


import java.util.UUID;

/**
 *
 */
public class BeaconTagDevice {

    public static final UUID UUID_SERVICE_UUID
            = UUID.fromString("59EC0800-0B1E-4063-8B16-B00B50AA3A7E");
    private static final UUID UUID_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a00-0b1e-4063-8b16-b00b50aa3a7e");
    private static final UUID MAJOR_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a02-0b1e-4063-8b16-b00b50aa3a7e");
    private static final UUID MINOR_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a01-0b1e-4063-8b16-b00b50aa3a7e");
    public static final UUID TX_POWER_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a05-0b1e-4063-8b16-b00b50aa3a7e");
    public static final UUID ADV_INTERVAL_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a04-0b1e-4063-8b16-b00b50aa3a7e");

    public static final UUID WAKE_UP_SERVICE_UUID
            = UUID.fromString("59EC0802-0B1E-4063-8B16-B00B50AA3A7E");
    public static final UUID SLEEP_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a07-0b1e-4063-8b16-b00b50aa3a7e");
    public static final UUID TEMPERATURE_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a08-0b1e-4063-8b16-b00b50aa3a7e");
    public static final UUID ACCELERATION_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a0b-0b1e-4063-8b16-b00b50aa3a7e");
    public static final UUID ANGULAR_SPEED_CHARACTERISTIC_UUID
            = UUID.fromString("59ec0a09-0b1e-4063-8b16-b00b50aa3a7e");

    private BluetoothDevice bleDevice;
    private DeviceFootprint footprint;

    public BeaconTagDevice(BluetoothDevice device, DeviceFootprint footprint) {
        bleDevice = device;
        this.footprint = footprint;
    }

    public BluetoothDevice getBleDevice() {
        return bleDevice;
    }

    public DeviceFootprint getFootprint() {
        return footprint;
    }
}
