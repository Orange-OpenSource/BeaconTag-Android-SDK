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

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.orange.beaconme_sdk.ble.listeners.GATTCharacteristicListener;
import com.orange.beaconme_sdk.ble.listeners.IGATTDescriptorListener;


public class GATTOperation {
	public enum OperationType {
		READ_CHARACTERISTIC, WRITE_CHARACTERISTIC, READ_DESCRIPTOR, WRITE_DESCRIPTOR, NOTIFY_START, NOTIFY_END,
	}

	protected OperationType mType;
	protected BluetoothGattCharacteristic mCharacteristic;
	protected BluetoothGattDescriptor mDescriptor;
	protected GATTCharacteristicListener mCharListener;
	protected IGATTDescriptorListener mDescListener;

	public GATTOperation(OperationType type,
                         BluetoothGattCharacteristic characteristic,
                         GATTCharacteristicListener listener) {
		mType = type;
		mCharacteristic = characteristic;
		mCharListener = listener;
	}

	public GATTOperation(OperationType type,
                         BluetoothGattCharacteristic characteristic) {
		mType = type;
		mCharacteristic = characteristic;
	}

	public GATTOperation(OperationType type,
                         BluetoothGattDescriptor descriptor, IGATTDescriptorListener listener) {
		mType = type;
		mDescriptor = descriptor;
		mDescListener = listener;
	}

	public GATTOperation(OperationType type, BluetoothGattDescriptor descriptor) {
		mType = type;
		mDescriptor = descriptor;
	}

	public OperationType getType() {
		return mType;
	}

	public BluetoothGattDescriptor getDescriptor() {
		return mDescriptor;
	}

	public BluetoothGattCharacteristic getCharacteristic() {
		return mCharacteristic;
	}

	public void complete(int status, BluetoothGattCharacteristic c) {
        if (mCharListener != null) {
            if (mType == OperationType.READ_CHARACTERISTIC)
                mCharListener.read(status, c);
            else if (mType == OperationType.WRITE_CHARACTERISTIC)
                mCharListener.written(status, c);
        }
	}

	public void complete(int status, BluetoothGattDescriptor d) {
        if (mDescListener != null) {
            if (mType == OperationType.READ_CHARACTERISTIC)
                mDescListener.read(status, d);
            else if (mType == OperationType.WRITE_CHARACTERISTIC)
                mDescListener.written(status, d);
        }
	}
}
