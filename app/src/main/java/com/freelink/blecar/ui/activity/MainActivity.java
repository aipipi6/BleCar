package com.freelink.blecar.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;

import com.freelink.blecar.App;
import com.freelink.blecar.R;
import com.freelink.blecar.ui.dialog.BleSetupDialog;
import com.freelink.blecar.ble.BleService;
import com.freelink.blecar.ui.dialog.RockerSetupDialog;
import com.freelink.blecar.ui.dialog.SetupDialog;
import com.freelink.library.base.BaseToolBarActivity;

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


    @OnClick(R.id.button1)
    public void onViewClicked() {
        new BleSetupDialog(context).show();
    }

    @OnClick(R.id.button2)
    public void onViewClicked2() {
        new SetupDialog().show(getSupportFragmentManager(), "");
    }
}
