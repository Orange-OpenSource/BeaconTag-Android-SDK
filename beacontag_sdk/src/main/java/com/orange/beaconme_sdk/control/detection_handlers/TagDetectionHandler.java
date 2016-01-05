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
package com.orange.beaconme_sdk.control.detection_handlers;


import com.orange.beaconme_sdk.ble.model.DeviceFootprint;
import com.orange.beaconme_sdk.ble.model.IBeaconDetect;

/**
 * Super class of detection handlers register listener and invoke appropriate method when
 * onFire method is invoked
 */
public abstract class TagDetectionHandler {
    
    private final DeviceFootprint footprint;
    private volatile OnTriggerFiredListener listener;

    protected TagDetectionHandler(DeviceFootprint footprint, OnTriggerFiredListener listener) {
        this.footprint = footprint;
        this.listener = listener;
    }
    
    public void onDetect(IBeaconDetect detection) {
        if (detection.getFootprint().equals(footprint)) {
            handleDetection(detection);
        }
    }

    /**
     * Override this method to handle detection
     * @param detection detection to handle
     */
    protected void handleDetection(IBeaconDetect detection) {

    }
    
    protected void onFired() {
        if (listener != null) {
            listener.onFired(footprint);
        }
    }

    public void deactivate() {
        listener = null;
    }

    public interface OnTriggerFiredListener {
        void onFired(DeviceFootprint deviceFootprint);
    }
}
