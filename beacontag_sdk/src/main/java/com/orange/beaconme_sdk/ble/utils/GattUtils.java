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
package com.orange.beaconme_sdk.ble.utils;

import com.orange.beaconme_sdk.ble.model.BeaconTagDevice;
import com.orange.beaconme_sdk.ble.model.WriteCharacteristicCommand;
import com.orange.beaconme_sdk.control.model.BeaconSettings;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public final class GattUtils {


    private GattUtils() {
    }

    public static List<WriteCharacteristicCommand> beaconSettingsToCommands(BeaconSettings settings) {
        List<WriteCharacteristicCommand> commands = new ArrayList<>();

        if (settings.isTxPowerEnabled()) {
            commands.add(new WriteCharacteristicCommand(BeaconTagDevice.UUID_SERVICE_UUID,
                    BeaconTagDevice.TX_POWER_CHARACTERISTIC_UUID, new byte[]{settings.getTxPower()}));
        }

        if (settings.isAdvertisingIntervalEnabled()) {
            commands.add(new WriteCharacteristicCommand(BeaconTagDevice.UUID_SERVICE_UUID,
                    BeaconTagDevice.ADV_INTERVAL_CHARACTERISTIC_UUID, intTo2BytesLittleEndianArray(
                    settings.getAdvertisingInterval())));
        }

        commands.add(getSleepDelayCommnand(settings));
        commands.add(getTemperatureCommand(settings));
        commands.add(getAccelerationCommand(settings));
        commands.add(getAngularSpeedCommand(settings));

        return commands;
    }

    private static WriteCharacteristicCommand getAccelerationCommand(BeaconSettings settings) {
        if(settings.isAccelerationEnabled()) {
            return new WriteCharacteristicCommand(BeaconTagDevice.WAKE_UP_SERVICE_UUID,
                    BeaconTagDevice.ACCELERATION_CHARACTERISTIC_UUID,
                    floatToWritableByteArray(settings.getAcceleration()));
        } else {
            return new WriteCharacteristicCommand(BeaconTagDevice.WAKE_UP_SERVICE_UUID,
                    BeaconTagDevice.ACCELERATION_CHARACTERISTIC_UUID, false);
        }
    }

    private static WriteCharacteristicCommand getAngularSpeedCommand(BeaconSettings settings) {
        if(settings.isAngularSpeedEnabled()) {
            return new WriteCharacteristicCommand(BeaconTagDevice.WAKE_UP_SERVICE_UUID,
                    BeaconTagDevice.ANGULAR_SPEED_CHARACTERISTIC_UUID,
                    floatToWritableByteArray(settings.getAngularSpeed()));
        } else {
            return new WriteCharacteristicCommand(BeaconTagDevice.WAKE_UP_SERVICE_UUID,
                    BeaconTagDevice.ANGULAR_SPEED_CHARACTERISTIC_UUID, false);
        }
    }

    private static WriteCharacteristicCommand getTemperatureCommand(BeaconSettings settings) {
        if (settings.isTemperatureEnabled()) {
            return new WriteCharacteristicCommand(BeaconTagDevice.WAKE_UP_SERVICE_UUID,
                    BeaconTagDevice.TEMPERATURE_CHARACTERISTIC_UUID,
                    getTemperatureWritableByteArray(settings.getLowerTemperatureBoundary(),
                            settings.getUpperTemperatureBoundary()));
        } else {
            return new WriteCharacteristicCommand(BeaconTagDevice.WAKE_UP_SERVICE_UUID,
                    BeaconTagDevice.TEMPERATURE_CHARACTERISTIC_UUID, false);
        }
    }

    private static WriteCharacteristicCommand getSleepDelayCommnand(BeaconSettings settings) {
        return new WriteCharacteristicCommand(BeaconTagDevice.WAKE_UP_SERVICE_UUID,
                BeaconTagDevice.SLEEP_CHARACTERISTIC_UUID,
                getSleepDelayWritableByteArray(settings.getSleepDelay()));
    }

    private static byte[] floatToWritableByteArray(float i) {
        ByteBuffer b = ByteBuffer.allocate(5);
        b.put((byte) 1);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putFloat(i);
        return b.array();
    }

    private static byte[] getTemperatureWritableByteArray(byte lower, byte upper) {
        ByteBuffer b = ByteBuffer.allocate(3);
        b.put((byte) 1);
        b.put(lower);
        b.put(upper);
        return b.array();
    }

    private static byte[] getSleepDelayWritableByteArray(int sleepDelay) {
        ByteBuffer b = ByteBuffer.allocate(3);
        b.put((byte) 1);
        b.put((byte) sleepDelay);
        b.put((byte) (sleepDelay >> 8));
        return b.array();
    }

    private static byte[] intTo2BytesLittleEndianArray(int i) {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.put((byte) i);
        b.put((byte) (i >> 8));
        return b.array();
    }
}
