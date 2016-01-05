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

/**
 *
 */
public enum BLERange {
    IMMIDIATE(0, 1), NEAR(1, 10),  FAR(10, Double.MAX_VALUE);

    public static BLERange getRange(double distance) {
        for (BLERange range: BLERange.values()) {
            if (range.isWithinRange(distance)) return range;
        }
        return FAR;
    }
    
    BLERange(double bottomThreshold, double topThreshold) {
        this.topThreshold = topThreshold;
        this.bottomThreshold = bottomThreshold;
    }

    private final double topThreshold;
    private final double bottomThreshold;
    
    public boolean isWithinRange(double distance) {
        return (distance <= topThreshold) && (distance > bottomThreshold);
    }
}
