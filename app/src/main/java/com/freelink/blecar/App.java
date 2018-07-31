package com.freelink.blecar;

import android.app.Application;
import android.view.Gravity;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.freelink.blecar.ble.BleService;

/**
 * Created by chenjun on 2018/7/31.
 */


public class App extends Application {

    public static BleService bleService;

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        CrashUtils.init();

        initToast();
    }



    private void initToast() {
        ToastUtils.setBgResource(R.drawable.bg_toast);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.setMsgColor(0xFFFFFFFF);
        ToastUtils.setMsgSize(SizeUtils.dp2px(15));
    }
}

