package com.freelink.blecar.ble;


import android.bluetooth.BluetoothDevice;

import com.freelink.blecar.App;
import com.freelink.blecar.ble.observer.BleConnectionObserver;
import com.freelink.blecar.ble.observer.BleReceiveObserver;
import com.freelink.blecar.ble.observer.BleScanObserver;

/**
 * Created by chenjun on 2018/7/20.
 */

public class BleUtil {

    /**
     * 获取校验码
     */
    public static byte calcCheckCode(int offset, int len, byte[] datas) {

        byte checkCode = 0;
        for(int i = 0; i < len; i++) {
            checkCode ^= datas[i + offset];
        }
        return checkCode;
    }

    public static void startDiscovery() {
        if(App.bleService == null) {
            return ;
        }
        App.bleService.startDiscovery();
    }

    public static void cancelDiscovery() {
        if(App.bleService == null) {
            return ;
        }
        App.bleService.cancelDiscovery();
    }

    public static boolean isConnected() {
        if(App.bleService == null) {
            return false;
        }
        return App.bleService.isConnected();
    }

    public static boolean isConnecting() {
        if(App.bleService == null) {
            return false;
        }

        return App.bleService.isConnecting();
    }

    public static void connect(BluetoothDevice device) {
        if(App.bleService == null) {
            return;
        }
        App.bleService.connect(device);
    }

    public static void connect(String mac) {
        if(App.bleService == null) {
            return;
        }
        App.bleService.connect(mac);
    }

    public static void disConnect() {
        if(App.bleService == null) {
            return;
        }
        App.bleService.disConnect();
    }

    public static BluetoothDevice getConnectDevice() {
        if(App.bleService == null) {
            return null;
        }
        return App.bleService.getConnectDevice();
    }

    public static void registerBleScanObserver(boolean isRegister, BleScanObserver bleScanObserver) {
        if(App.bleService == null) {
            return;
        }
        App.bleService.registerBleScanObserver(isRegister, bleScanObserver);
    }

    public static void registerBleConnectionObserver(boolean isRegister, BleConnectionObserver bleConnectionObserver) {
        if(App.bleService == null) {
            return;
        }
        App.bleService.registerBleConnectionObserver(isRegister, bleConnectionObserver);
    }

    public static void registerBleReceiveObserver(boolean isRegister, BleReceiveObserver bleReceiveObserver) {
        if(App.bleService == null) {
            return;
        }
        App.bleService.registerBleReceiveObserver(isRegister, bleReceiveObserver);
    }



    public static void send(byte... bytes) {
        App.bleService.send(bytes);
    }

    public static void sendWithCmd(int cmdType, byte... datas) {
        if(datas == null) {
            datas = new byte[0];
        }

        byte[] sendDatas = new byte[4 + datas.length + 1];
        sendDatas[0] = 0x23;
        sendDatas[1] = 0x23;
        sendDatas[2] = (byte) (cmdType & 0xFF);
        sendDatas[3] = (byte) (datas.length & 0xFF);
        System.arraycopy(datas, 0, sendDatas, 4, datas.length);
        sendDatas[sendDatas.length - 1] =
                calcCheckCode(0, sendDatas.length - 1, sendDatas);

        send(sendDatas);
    }
}
