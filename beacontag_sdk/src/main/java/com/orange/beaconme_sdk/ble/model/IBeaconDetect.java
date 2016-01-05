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


import java.util.Date;

/**
 *
 */
public class IBeaconDetect {
    
    private final DeviceFootprint footprint;
    private final int rssi;
    private final Date detectTime;
    private final int txPower;

    public IBeaconDetect(String uuid, int major, int minor, int rssi, int txPower) {
        footprint = new DeviceFootprint(uuid.toLowerCase(), major, minor);
        this.rssi = rssi;
        this.detectTime = new Date();
        this.txPower = txPower;
    }

    public DeviceFootprint getFootprint() {
        return footprint;
    }

    public int getRssi() {
        return rssi;
    }

    public Date getDetectTime() {
        return detectTime;
    }
    
    public double getDistance() {
        return Math.sqrt(Math.pow(10, (txPower - rssi) / 10.0));
    }
    
    public BLERange getRange(){
        return BLERange.getRange(getDistance());
        
    }
    
}
