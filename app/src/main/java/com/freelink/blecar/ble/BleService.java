package com.freelink.blecar.ble;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.freelink.blecar.ble.observer.BleConnectionObserver;
import com.freelink.blecar.ble.observer.BleReceiveObserver;
import com.freelink.blecar.ble.observer.BleScanObserver;


public class BleService extends Service {

    private final IBinder binder = new InnerBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BleManager.getInstance().init(this, null);
    }

    @Override
    public void onDestroy() {
        BleManager.getInstance().release();
        super.onDestroy();
        LogUtils.e("service onDestroy");
    }

    public boolean isConnected() {
        return BleManager.getInstance().isConnected();
    }

    public boolean isConnecting() {
        return BleManager.getInstance().isConnecting();
    }

    public void startDiscovery() {
        BleManager.getInstance().startDiscovery();
    }

    public void cancelDiscovery() {
        BleManager.getInstance().cancelDiscovery();
    }

    public void connect(BluetoothDevice remoteDevice) {
        BleManager.getInstance().connect(remoteDevice);
    }

    public void connect(String deviceAddr) {
        BleManager.getInstance().connect(deviceAddr);
    }

    public void disConnect() {
        BleManager.getInstance().disConnect();
    }

    public BluetoothDevice getConnectDevice(){
        return BleManager.getInstance().getConnectDevice();
    }

    public void send(byte... bytes) {
        BleManager.getInstance().send(bytes);
    }

    public void registerBleScanObserver(boolean isRegister, BleScanObserver bleScanObserver) {
        BleManager.getInstance().registerBleScanObserver(isRegister, bleScanObserver);
    }

    public void registerBleConnectionObserver(boolean isRegister, BleConnectionObserver bleConnectionObserver) {
        BleManager.getInstance().registerBleConnectionObserver(isRegister, bleConnectionObserver);
    }

    public void registerBleReceiveObserver(boolean isRegister, BleReceiveObserver bleReceiveObserver) {
        BleManager.getInstance().registerBleReceiveObserver(isRegister, bleReceiveObserver);
    }

    public class InnerBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }
}
