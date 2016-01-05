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

import java.util.UUID;

/**
 *
 */
public class WriteCharacteristicCommand {
    private UUID serviceUUID;
    private UUID characteristicUUID;
    private SwitchState switchState = SwitchState.NONE;
    private byte[] bytesToUpload;

    public WriteCharacteristicCommand(UUID serviceUUID, UUID characteristicUUID, boolean enable) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.switchState = enable ? SwitchState.ENABLE : SwitchState.DISABLE;
    }

    public WriteCharacteristicCommand(UUID serviceUUID, UUID characteristicUUID, byte[] bytesToUpload) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.bytesToUpload = bytesToUpload;
    }

    public UUID getServiceUUID() {
        return serviceUUID;
    }

    public UUID getCharacteristicUUID() {
        return characteristicUUID;
    }

    public SwitchState getSwitchState() {
        return switchState;
    }

    public byte[] getBytesToUpload() {
        return bytesToUpload;
    }

    public enum SwitchState {
        ENABLE, DISABLE, NONE
    }
}
