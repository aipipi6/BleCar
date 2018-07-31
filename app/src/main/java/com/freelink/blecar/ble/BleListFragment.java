package com.freelink.blecar.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.freelink.blecar.R;
import com.freelink.blecar.ble.observer.BleConnectionObserver;
import com.freelink.blecar.ble.observer.BleScanObserver;
import com.freelink.library.base.BaseNomalFragment;
import com.freelink.library.viewHelper.DividerItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class BleListFragment extends BaseNomalFragment {


    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private BleListAdapter bleListAdapter;
    private Handler handler = new Handler();
    private List<String> bleMacList = new ArrayList<>();

    public void  refreshList() {
        bleListAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_refresh_recyclerview;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        bleListAdapter = new BleListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(bleListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext,  RecyclerView.HORIZONTAL, 1, 0xFFCDCDCD));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                bleListAdapter.getData().clear();
                bleListAdapter.notifyDataSetChanged();
                // 蓝牙搜索到设备需要用到定位服务，所以在开发中 targetSdkVersion 大于等于23（6.0） 需要在代码中进行权限获取
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    BleListFragmentPermissionsDispatcher.scanDeviceWithPermissionCheck(BleListFragment.this);
                } else {
                    scanDevice();
                }
            }
        });
        refreshLayout.autoRefresh();
        refreshLayout.setEnableLoadMore(false);

        bleListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    showToast("请先打开蓝牙设备");
                    return;
                }
                if(BleManager.getInstance().isConnecting()) {
                    showToast("连接其他设备中，请等待");
                    return;
                }

                if(BleManager.getInstance().isConnected()) {
                    showToast("请先断开当前连接的设备后，再重新连接");
                    return;
                }
                BluetoothDevice device = bleListAdapter.getItem(position);
                int majosDeviceClass = device.getBluetoothClass().getMajorDeviceClass();
                BleManager.getInstance().connect(bleListAdapter.getItem(position));
            }
        });

        bleListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                showLoadingDialog("正在断开设备");
                BleManager.getInstance().disConnect();
            }
        });

        BleManager.getInstance().registerBleScanObserver(true, onBleScanListener);
        BleManager.getInstance().registerBleConnectionObserver(true, onBleConnectionListener);
    }


    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public void scanDevice() {
        BleManager.getInstance().startDiscovery();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BleManager.getInstance().cancelDiscovery();
        BleManager.getInstance().registerBleScanObserver(false, onBleScanListener);
        BleManager.getInstance().registerBleConnectionObserver(false, onBleConnectionListener);
    }

    BleScanObserver onBleScanListener = new BleScanObserver() {
        @Override
        public void onScanStart() {
            if (BleManager.getInstance().getConnectDevice() != null) {
                bleListAdapter.addData(BleManager.getInstance().getConnectDevice());
            }
        }

        @Override
        public void onScanDevice(BluetoothDevice device) {
            if (!bleListAdapter.getData().contains(device)) {
                bleListAdapter.addData(device);
            }
            String mac = device.getAddress();
            if(!BleManager.getInstance().isConnected()
                    && bleMacList.contains(mac)) {
                BleManager.getInstance().connect(device);
            }
        }

        @Override
        public void onScanFinished() {
            refreshLayout.finishRefresh();
            if(bleListAdapter.getData().isEmpty()) {
                showToast("附近未搜索到蓝牙设备");
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
            showToast("连接成功");
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
                    getActivity().onBackPressed();
                }
            }, 100);
        }

        @Override
        public void onConnectFailed(BluetoothDevice device) {
            hideLoadingDialog();
            showToast("连接失败");
        }

        @Override
        public void onDisConnected(BluetoothDevice device) {
            hideLoadingDialog();
            showToast("断开连接");
            List<BluetoothDevice> devices = bleListAdapter.getData();
            for(int i = 0; i < devices.size(); i++) {
                if(devices.get(i).getAddress().equals(device.getAddress())) {
                    bleListAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    };

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
        BluetoothDevice connectDevice = BleManager.getInstance().getConnectDevice();
        if(connectDevice == null || device == null) {
            return false;
        }

        return connectDevice.getAddress().equals(device.getAddress());
    }
}
