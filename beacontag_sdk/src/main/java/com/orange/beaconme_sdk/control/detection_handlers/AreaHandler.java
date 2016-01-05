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

import android.util.Log;

import com.orange.beaconme_sdk.ble.model.BLERange;
import com.orange.beaconme_sdk.ble.model.BLERangeChange;
import com.orange.beaconme_sdk.ble.model.DeviceFootprint;
import com.orange.beaconme_sdk.ble.model.IBeaconDetect;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Superclass of all handlers which works with areas, like enter, exit area or proximity zone.
 */
public abstract class AreaHandler extends TagDetectionHandler {

    private static final int SAFE_INTERVAL = 2*1000;
    private static final int VISIBILITY_DELAY = 30*1000;

    private BLERange range = null;
    private List<BLERangeChange> rangeChangesStack = new ArrayList<>();

    private TimerTask makeInvisibleTask;

    private Timer timer = new Timer();

    protected AreaHandler(DeviceFootprint footprint, OnTriggerFiredListener listener) {
        super(footprint, listener);
    }

    private void rescheduleInvisibilityTask() {
        if (makeInvisibleTask != null) {
            makeInvisibleTask.cancel();
        }
        timer.purge();
        makeInvisibleTask = getMakeInvisibleTask();
        timer.schedule(makeInvisibleTask, VISIBILITY_DELAY);
    }


    @Override
    protected void handleDetection(IBeaconDetect detection) {
        rescheduleInvisibilityTask();
        onNewRangeCome(detection.getRange());
    }

    private void onNewRangeCome(BLERange newRange) {
        rangeChangesStack.add(new BLERangeChange(newRange, System.currentTimeMillis()));
        BLERangeChange rangeChange = rangeChangesStack.get(0);
        while (rangeChange.getTimestamp() < System.currentTimeMillis() - SAFE_INTERVAL) {
            rangeChangesStack.remove(rangeChange);
            rangeChange = rangeChangesStack.get(0);
        }

        checkRangeChanging();
    }

    private void checkRangeChanging() {
        BLERange lowestRange = rangeChangesStack.get(0).getRange();
        BLERange highestRange = rangeChangesStack.get(0).getRange();

        for (BLERangeChange rangeChange: rangeChangesStack) {
            BLERange itemRange = rangeChange.getRange();
            if (itemRange.compareTo(highestRange) > 0) highestRange = itemRange;
            if (itemRange.compareTo(lowestRange) < 0) lowestRange = itemRange;
        }

        if (range == null) {
            changeRange(highestRange);
        } else if((range.compareTo(highestRange) > 0) && (range.compareTo(lowestRange) > 0)) {
            changeRange(highestRange);
        } else if(range.compareTo(highestRange) < 0 && range.compareTo(lowestRange) < 0) {
            changeRange(lowestRange);
        }
    }

    private void changeRange(BLERange newRange) {
        onRangeChanged(range, newRange);
        range = newRange;
    }

    protected void onRangeChanged(BLERange oldRange, BLERange newRange) {
        Log.d("RANGE CHANGED", oldRange + " to " + newRange);
    }

    /**
     * Make task which will set range to null, basically means that phone exits beacon area
     * @return make invisible task
     */
    private TimerTask getMakeInvisibleTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (range != null) {
                    onRangeChanged(range, null);
                }
                range = null;
            }
        };
    }

    protected BLERange getRange() {
        return range;
    }
}
