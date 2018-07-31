package com.freelink.blecar.ble.parser;


public interface IBleParser {

    int doParse(byte[] datas, int len, BleCallBack callBack);
}
