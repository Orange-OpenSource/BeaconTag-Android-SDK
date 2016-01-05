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
package com.orange.beacontag_sdk_example;

import android.app.Application;
import android.widget.Toast;

import com.orange.beaconme_sdk.control.BeaconMonitor;
import com.orange.beaconme_sdk.control.BeaconUpdatedCallback;
import com.orange.beaconme_sdk.control.model.AreaSettings;
import com.orange.beaconme_sdk.control.model.BeaconSettings;

/**
 *
 */
public class BeaconTagSdkExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize Beacon Monitor
        BeaconMonitor monitor = BeaconMonitor.init(getApplicationContext());

        //Create callback for beacon updated event
        BeaconUpdatedCallback beaconUpdatedCallback = new BeaconUpdatedCallback() {
            @Override
            public void onBeaconUpdated(String uuid, int major, int minor) {
                Toast.makeText(getApplicationContext(), uuid + " updated", Toast.LENGTH_LONG).show();
            }
        };

        /*
            Create beacon settings for first beacon, with UUID = "3d4f13b4-d1fd-4049-80e5-d3edcc840b6c",
            major =  41163 and minor == 12448. Event will be triggered when phone approaches beacon area.
         */
        BeaconSettings beaconSettings1 = new BeaconSettings("3d4f13b4-d1fd-4049-80e5-d3edcc840b6d",
                37598, 23972, AreaSettings.APPROACHING);
        /*
            Passing beaconSettings1 and beaconUpdatedCallback, to start detection of the first beacon
         */
        monitor.registerForBeaconDetection(beaconSettings1, beaconUpdatedCallback);

        /*
            Create beacon settings for second beacon, with UUID = "3d4f13b4-d1fd-4049-80e5-d3edcc840b6c",
            major = 20189 and minor == 26571. Event will be triggered when phone enter beacon area.
         */
        BeaconSettings beaconSettings2 = new BeaconSettings("3d4f13b4-d1fd-4049-80e5-d3edcc840b67",
                33872, 23535,  AreaSettings.ENTER);
        //Set acceleration to be 2.45 m/s2
        beaconSettings2.setAcceleration(2.45f);
        //Set tx power to 4 dBm
        beaconSettings2.setTxPower((byte) 4);

        //Set advertising interval to be 1000*625us = 0,625s
        beaconSettings2.setAdvertisingInterval(1000);

        //Set sleep delay to be 10s.
        beaconSettings2.setSleepDelay(10);

        //Device will be advertise if temperature will be between -20 degree Celsius and +50 degree Celsius
        beaconSettings2.setTemperatureBoundaries((byte) -20, (byte) 50);

        //Passing beaconSettings2 and beaconUpdatedCallback, to start detection of the second beacon
        monitor.registerForBeaconDetection(beaconSettings2, beaconUpdatedCallback);
    }
}
