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
import com.orange.beaconme_sdk.control.model.AreaSettings;

/**
 *
 */
public final class DetectionHandlerFactory {

    private DetectionHandlerFactory() {}

    public static TagDetectionHandler getHandler(DeviceFootprint footprint,
                                                 TagDetectionHandler.OnTriggerFiredListener triggerFiredListener,
                                                 AreaSettings areaSettings) {
        switch (areaSettings) {
            case ENTER:
                return new EnterAreaHandler(footprint, triggerFiredListener);
            case EXIT:
                return new ExitAreaHandler(footprint, triggerFiredListener);
            case ENTER_AND_EXIT:
                return new EnterAndExitAreaHandler(footprint, triggerFiredListener);
            case APPROACHING:
                return new EnterNearAreaHandler(footprint, triggerFiredListener);
            case LEAVING:
                return new ExitNearAreaHandler(footprint, triggerFiredListener);
            case APPROACHING_AND_LEAVING:
                return new EnterNearAndExitNearAreaHandler(footprint, triggerFiredListener);
            default:
                return new EnterAreaHandler(footprint, triggerFiredListener);
        }
    }
}
