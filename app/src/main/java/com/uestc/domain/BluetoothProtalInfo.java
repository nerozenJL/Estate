package com.uestc.domain;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Nerozen on 2016/3/26.
 */
public class BluetoothProtalInfo {
    private String Ssid, accessSecret;

    public int getSignalStrength() {
        return signalStrength;
    }

    int signalStrength;

    private BluetoothDevice bluetoothDevice;

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getSsid() {
        return Ssid;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public BluetoothProtalInfo(String ssid, String accessSecret) {
        Ssid = ssid;
        this.accessSecret = accessSecret;
    }

    public BluetoothProtalInfo(String ssid, int signalStrength) {
        Ssid = ssid;
        this.signalStrength = signalStrength;
    }

    public void setSsid(String ssid) {

        Ssid = ssid;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
}
