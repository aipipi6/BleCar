package com.freelink.blecar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;

import com.freelink.blecar.ble.BleDialog;
import com.freelink.blecar.ble.BleService;
import com.freelink.library.base.BaseToolBarActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseToolBarActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle bundle) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        showToolBar(false);

        Intent intent = new Intent(this, BleService.class);
        bindService(intent, bleServiceConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleService.InnerBinder binder = (BleService.InnerBinder) service;
            App.bleService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @OnClick(R.id.button)
    public void onViewClicked() {
        new BleDialog(context).show();
    }
}
