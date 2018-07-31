package com.freelink.blecar.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.freelink.blecar.R;
import com.freelink.blecar.ble.observer.BleConnectionObserver;
import com.freelink.blecar.ble.observer.BleScanObserver;
import com.freelink.library.dialog.BaseNormalDialog;
import com.freelink.library.viewHelper.DividerItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by chenjun on 2018/7/31.
 */

public class BleDialog extends BaseNormalDialog {

    SmartRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    private BleListAdapter bleListAdapter;
    private List<String> bleMacList = new ArrayList<>();
    private View loadingView;
    private TextView tvLoading;

    public BleDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_ble;
    }

    @Override
    protected int getGravity() {
        return Gravity.RIGHT;
    }

    @Override
    protected ViewGroup.LayoutParams getContentLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                context.getResources().getDisplayMetrics().heightPixels);
    }

    @Override
    protected int getWindowAnimations() {
        return  R.style.FromRightAnimation;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.itv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.refreshLayout);

        loadingView = findViewById(R.id.ll_loading);
        tvLoading = findViewById(R.id.tv_loading);

        bleListAdapter = new BleListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(bleListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context,  RecyclerView.HORIZONTAL, 1, 0xFFCDCDCD));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                bleListAdapter.getData().clear();
                bleListAdapter.notifyDataSetChanged();
                addConnectDevice();
                BleUtil.startDiscovery();
            }
        });
        refreshLayout.autoRefresh();
        refreshLayout.setEnableLoadMore(false);

        bleListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    ToastUtils.showShort("请先打开蓝牙设备");
                    return;
                }
                if(BleUtil.isConnecting()) {
                    ToastUtils.showShort("连接其他设备中，请等待");
                    return;
                }

                if(BleUtil.isConnected()) {
                    ToastUtils.showShort("请先断开当前连接的设备后，再重新连接");
                    return;
                }
                BluetoothDevice device = bleListAdapter.getItem(position);
                int majosDeviceClass = device.getBluetoothClass().getMajorDeviceClass();
                BleUtil.connect(bleListAdapter.getItem(position));
            }
        });

        bleListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                showLoadingDialog("正在断开设备");
                BleUtil.disConnect();
            }
        });

        BleUtil.registerBleScanObserver(true, onBleScanListener);
        BleUtil.registerBleConnectionObserver(true, onBleConnectionListener);

        addConnectDevice();
    }

    private void addConnectDevice() {
        if (BleUtil.getConnectDevice() != null) {
            bleListAdapter.addData(BleUtil.getConnectDevice());
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        BleUtil.cancelDiscovery();
        BleUtil.registerBleScanObserver(false, onBleScanListener);
        BleUtil.registerBleConnectionObserver(false, onBleConnectionListener);
    }

    BleScanObserver onBleScanListener = new BleScanObserver() {
        @Override
        public void onScanStart() {
        }

        @Override
        public void onScanDevice(BluetoothDevice device) {
            if (!bleListAdapter.getData().contains(device)) {
                bleListAdapter.addData(device);
            }
            String mac = device.getAddress();
            if(!BleUtil.isConnected()
                    && bleMacList.contains(mac)) {
                BleUtil.connect(device);
            }
        }

        @Override
        public void onScanFinished() {
            refreshLayout.finishRefresh();
            if(bleListAdapter.getData().isEmpty()) {
                ToastUtils.showShort("附近未搜索到蓝牙设备");
            }
        }
    };

    BleConnectionObserver onBleConnectionListener = new BleConnectionObserver() {
        @Override
        public void onStartConnect() {
            showLoadingDialog("连接设备中");
        }

        @Override
        public void onConnectSuccess(BluetoothDevice device) {
            hideLoadingDialog();
            ToastUtils.showShort("连接成功");
            List<BluetoothDevice> devices = bleListAdapter.getData();
            for(int i = 0; i < devices.size(); i++) {
                if(devices.get(i).getAddress().equals(device.getAddress())) {
                    devices.remove(i);
                    devices.add(0, device);
                    bleListAdapter.notifyItemChanged(i);
                    bleListAdapter.notifyItemMoved(i, 0);

                    break;
                }
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 100);
        }

        @Override
        public void onConnectFailed(BluetoothDevice device) {
            hideLoadingDialog();
            ToastUtils.showShort("连接失败");
        }

        @Override
        public void onDisConnected(BluetoothDevice device) {
            hideLoadingDialog();
            ToastUtils.showShort("断开连接");
            List<BluetoothDevice> devices = bleListAdapter.getData();
            for(int i = 0; i < devices.size(); i++) {
                if(devices.get(i).getAddress().equals(device.getAddress())) {
                    bleListAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    };

    private void hideLoadingDialog() {
        loadingView.setVisibility(View.GONE);
    }

    private void showLoadingDialog(String text) {
        loadingView.setVisibility(View.VISIBLE);
        tvLoading.setText(text);
    }


    class BleListAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

        public BleListAdapter() {
            super(R.layout.item_ble_device, null);
        }

        @Override
        protected void convert(BaseViewHolder holder, BluetoothDevice device) {
            holder.setText(R.id.tv_device_name, StringUtils.isEmpty(device.getName()) ? device.getAddress() : device.getName());
            holder.setText(R.id.tv_device_mac, device.getAddress());

            ImageView ivDisconnect = holder.getView(R.id.iv_device_disconnect);
            if(isConnectDevice(device)) {
                ivDisconnect.setVisibility(View.VISIBLE);
                holder.addOnClickListener(R.id.iv_device_disconnect);
            } else {
                ivDisconnect.setVisibility(View.GONE);
            }
            TextView tvDeviceName = holder.getView(R.id.tv_device_name);
            int majosDeviceClass = device.getBluetoothClass().getMajorDeviceClass();
            int iconRes = R.mipmap.ic_bluetooth;
            switch (majosDeviceClass) {
                case BluetoothClass.Device.Major.COMPUTER:
                    iconRes = R.mipmap.ic_computer;
                    break;

                case BluetoothClass.Device.Major.PHONE:
                    iconRes = R.mipmap.ic_phone;
                    break;

                case BluetoothClass.Device.Major.AUDIO_VIDEO:
                    iconRes = R.mipmap.ic_computer;
                    break;

                default:
                    iconRes = R.mipmap.ic_bluetooth;
                    break;
            }
            holder.setImageResource(R.id.iv_device_icon, iconRes);

//            if(device.getAddress().toLowerCase().startsWith("98:d3")) {
            if(iconRes == R.mipmap.ic_bluetooth) {
                holder.itemView.setEnabled(true);
                tvDeviceName.setTextColor(0xFF333333);
            } else {
                holder.itemView.setEnabled(false);
                tvDeviceName.setTextColor(0xFF999999);
            }

        }
    }

    private boolean isConnectDevice(BluetoothDevice device) {
        BluetoothDevice connectDevice = BleUtil.getConnectDevice();
        if(connectDevice == null || device == null) {
            return false;
        }

        return connectDevice.getAddress().equals(device.getAddress());
    }
}
