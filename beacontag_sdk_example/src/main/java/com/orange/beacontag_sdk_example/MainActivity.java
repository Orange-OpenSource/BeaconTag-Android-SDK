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

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.orange.beaconme_sdk.control.BeaconMonitor;
import com.orange.beaconme_sdk.control.BeaconUpdatedCallback;
import com.orange.beaconme_sdk.control.model.AreaSettings;
import com.orange.beaconme_sdk.control.model.BeaconSettings;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            BeaconSettings beaconSettings = new BeaconSettings("3d4f13b4-d1fd-4049-80e5-d3edcc840b6f",
                    49837, 28051, AreaSettings.LEAVING);
            BeaconMonitor.getInstance().registerForBeaconDetection(beaconSettings, new BeaconUpdatedCallback() {
                @Override
                public void onBeaconUpdated(String uuid, int major, int minor) {
                    Toast.makeText(getApplicationContext(), uuid + " updated anew", Toast.LENGTH_LONG).show();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
