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

/**
 * Enum which describes different behavior of tag detection handlers.
 */
public enum AreaSettings {
    /**
     * ENTER - event will occur within a few seconds after phone enters beacon area,
     * EXIT - event will occur after 30 seconds after phone has left the beacon area,
     * APPROACHING - event will occur within a few seconds after phone enters Immidiate and/or Near beacon area,
     * LEAVING - event will occur after phone leave Immidiate and/or Near beacon area within a few
     *           seconds if phone in Far area, or after 30 seconds, if beacon no longer visible to the phone,
     * ENTER_AND_EXIT - event will occur for conditions described either in ENTER or EXIT,
     * APPROACHING_AND_LEAVING - event will occur for conditions described either in APPROACHING or LEAVING.
     */
    ENTER,
    EXIT,
    ENTER_AND_EXIT,
    APPROACHING,
    LEAVING,
    APPROACHING_AND_LEAVING;
}
