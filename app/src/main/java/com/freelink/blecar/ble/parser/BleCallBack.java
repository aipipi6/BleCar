package com.freelink.blecar.ble.parser;


import com.freelink.blecar.ble.entity.BaseBleMsgEntity;

/**
 * Created by chenjun on 2018/5/25.
 */

public interface BleCallBack {
    void callBack(BaseBleMsgEntity data);
}
