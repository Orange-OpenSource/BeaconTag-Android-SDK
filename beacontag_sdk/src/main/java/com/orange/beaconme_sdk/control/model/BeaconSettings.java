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
package com.orange.beaconme_sdk.control.model;

import com.orange.beaconme_sdk.ble.model.DeviceFootprint;

import java.util.Arrays;
import java.util.List;

/**
 * Contains all information about tag for detection.
 */
public class BeaconSettings {

    private static final int MAX_SLEEP_DELAY = 65535;

    private static final float MIN_ACCELERATION = 0.1569064f;
    private static final float MAX_ACCELERATION = 156.9064f;

    private static final List<Byte> TX_POWERS = Arrays.asList(new Byte[]{-62, -52, -48, -44, -40,
            -36, -32, -30, -20, -16, -12, -8, -4, 0, 4});

    private static final int MIN_ADVERTISING_INTERVAL = 160;
    private static final int MAX_ADVERTISING_INTERVAL = 16000;

    private final DeviceFootprint footprint;
    private final AreaSettings areaSettings;

    private int sleepDelay;

    private boolean temperatureEnabled = false;
    private byte lowerTemperatureBoundary;
    private byte upperTemperatureBoundary;

    private boolean accelerationEnabled = false;
    private float acceleration;

    private boolean angularSpeedEnabled = false;
    private float angularSpeed;

    private boolean txPowerEnabled = false;
    private byte txPower;

    private boolean advertisingIntervalEnabled = false;
    private int advertisingInterval;

    /**
     * Create a BeaconSettings for chosen devices
     * @param uuid Beacon uuid.
     * @param major Beacon major.
     * @param minor Beacon minor.
     * @param areaSettings settings reflect type of event to occur: enter area, exit area, etc.
     */
    public BeaconSettings(String uuid, int major, int minor, AreaSettings areaSettings) {
        footprint = new DeviceFootprint(uuid, major, minor);
        this.areaSettings = areaSettings;
    }

    public DeviceFootprint getDeviceFootprint() {
        return footprint;
    }

    public AreaSettings getAreaSettings() {
        return areaSettings;
    }

    /**
     * Sleep delay in seconds (ranges from 1 to 65535) A value of 0 disables sleeping. After device
     * was activated because of acceleration or temperature it will advertise for given period of time,
     * the it will be deactivated.
     * @param sleepDelay sleep delay in seconds. If sleep delay not within range it will set to 0;
     */
    public void setSleepDelay(int sleepDelay) {
        this.sleepDelay = sleepDelay > 0 ? sleepDelay < MAX_SLEEP_DELAY ? sleepDelay : 0 : 0;
    }

    /**
     * Set device to be activated when acceleration reaches given value
     * @param acceleration acceleration in m/s², Min = 0,1569064 m/s², Max = 156,9064 m/s², if it
     *                     not within range it will be ignored
     */
    public void setAcceleration(float acceleration) {
        if (MIN_ACCELERATION <= acceleration && acceleration <= MAX_ACCELERATION) {
            accelerationEnabled = true;
            this.acceleration = acceleration;
        }
    }


    /**
     * Set device's tx output power. If not set current device's value will be kept
     * @param txPower tx output power in dBm (allowed values: -62, -52, -48, -44, -40, -36, -32,
     *                -30, -20, -16, -12, -8, -4, 0, 4). If invalid value is provided, it will be ignored.
     */
    public void setTxPower(byte txPower) {
        if (TX_POWERS.contains(txPower)) {
            txPowerEnabled = true;
            this.txPower = txPower;
        }
    }

    /**
     * Set device's advertising interval in units of 625μs (ranges from 160 to 16000)
     * @param advertisingInterval advertising interval in units of 625μs
     */
    public void setAdvertisingInterval(int advertisingInterval) {
        if (MIN_ADVERTISING_INTERVAL <= advertisingInterval
                && advertisingInterval <= MAX_ADVERTISING_INTERVAL) {
            advertisingIntervalEnabled = true;
            this.advertisingInterval = advertisingInterval;
        }
    }

    /**
     * Set device to be activated when temperature will be withing given range. Upper boundary must
     * be greater than or equal to lower boundary, in other case boundaries will be ignored.
     * Boundaries are inclusive.
     * @param lowerTemperatureBoundary lower boundary in °C
     * @param upperTemperatureBoundary upper boundary in °C
     */
    public void setTemperatureBoundaries(byte lowerTemperatureBoundary, byte upperTemperatureBoundary) {
        if (upperTemperatureBoundary > lowerTemperatureBoundary) {
            temperatureEnabled = true;
            this.lowerTemperatureBoundary = lowerTemperatureBoundary;
            this.upperTemperatureBoundary = upperTemperatureBoundary;
        }
    }

    public DeviceFootprint getFootprint() {
        return footprint;
    }

    public int getSleepDelay() {
        return sleepDelay;
    }

    public boolean isTemperatureEnabled() {
        return temperatureEnabled;
    }

    public byte getLowerTemperatureBoundary() {
        return lowerTemperatureBoundary;
    }

    public byte getUpperTemperatureBoundary() {
        return upperTemperatureBoundary;
    }

    public boolean isAccelerationEnabled() {
        return accelerationEnabled;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public boolean isAngularSpeedEnabled() {
        return angularSpeedEnabled;
    }

    public float getAngularSpeed() {
        return angularSpeed;
    }

    public boolean isTxPowerEnabled() {
        return txPowerEnabled;
    }

    public byte getTxPower() {
        return txPower;
    }

    public boolean isAdvertisingIntervalEnabled() {
        return advertisingIntervalEnabled;
    }

    public int getAdvertisingInterval() {
        return advertisingInterval;
    }
}
