package com.freelink.blecar.ble.observer;


import com.freelink.blecar.ble.entity.BaseBleMsgEntity;

/**
 * Created by chenjun on 2018/4/24.
 */

public interface BleReceiveObserver {
    void onReceive(BaseBleMsgEntity data);
}
