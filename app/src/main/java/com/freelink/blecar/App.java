package com.freelink.blecar;

import android.app.Application;
import android.view.Gravity;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.freelink.blecar.bean.RockerConfig;
import com.freelink.blecar.ble.BleService;
import com.google.gson.Gson;

/**
 * Created by chenjun on 2018/7/31.
 */


public class App extends Application {

    public static BleService bleService;
    public static RockerConfig rockerConfig;

    private static final String SP_PARAMS_ROCKER_CONFIG = "RockerConfig";
    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        CrashUtils.init();

        initToast();

        initRockerConfig();
    }



    private void initToast() {
        ToastUtils.setBgResource(R.drawable.bg_toast);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.setMsgColor(0xFFFFFFFF);
        ToastUtils.setMsgSize(SizeUtils.dp2px(15));
    }

    private void initRockerConfig() {
        String rockerConfigStr = SPUtils.getInstance().getString(SP_PARAMS_ROCKER_CONFIG);
        if(!StringUtils.isEmpty(rockerConfigStr)) {
            rockerConfig = new Gson().fromJson(rockerConfigStr, RockerConfig.class);
        } else {
            rockerConfig = new RockerConfig();
            rockerConfigStr = new Gson().toJson(rockerConfig);
            SPUtils.getInstance().put(SP_PARAMS_ROCKER_CONFIG, rockerConfigStr);
        }
    }

    public static void saveRockerConfig(RockerConfig rockerConfig) {
        String rockerConfigStr = new Gson().toJson(rockerConfig);
        SPUtils.getInstance().put(SP_PARAMS_ROCKER_CONFIG, rockerConfigStr);
    }

}

