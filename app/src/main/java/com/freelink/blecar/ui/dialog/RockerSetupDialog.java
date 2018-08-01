package com.freelink.blecar.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.freelink.blecar.App;
import com.freelink.blecar.R;
import com.freelink.blecar.bean.RockerConfig;
import com.freelink.library.dialog.BaseNormalDialog;
import com.freelink.library.widget.SwitchButton;
import com.google.gson.Gson;

/**
 * Created by chenjun on 2018/8/1.
 */

public class RockerSetupDialog extends BaseNormalDialog {
    private SwitchButton sbtnLeftGravity;
    private SwitchButton sbtnLeftLockX;
    private SwitchButton sbtnLeftLockY;
    private SwitchButton sbtnLeftRecoverY;
    private SwitchButton sbtnRightGravity;
    private SwitchButton sbtnRightLockX;
    private SwitchButton sbtnRightLockY;
    private SwitchButton sbtnRightRecoverY;

    private OnRockerSetupChangeListener onRockerSetupChangeListener;

    public RockerSetupDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_rocker_setup;
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
        return R.style.FromRightAnimation;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sbtnLeftGravity = findViewById(R.id.sbtn_left_rocker_gravity);
        sbtnLeftLockX = findViewById(R.id.sbtn_left_rocker_lock_x);
        sbtnLeftLockY = findViewById(R.id.sbtn_left_rocker_lock_y);
        sbtnLeftRecoverY = findViewById(R.id.sbtn_left_rocker_recover_y);

        sbtnRightGravity = findViewById(R.id.sbtn_right_rocker_gravity);
        sbtnRightLockX = findViewById(R.id.sbtn_right_rocker_lock_x);
        sbtnRightLockY = findViewById(R.id.sbtn_right_rocker_lock_y);
        sbtnRightRecoverY = findViewById(R.id.sbtn_right_rocker_recover_y);

        sbtnLeftGravity.setOpened(App.rockerConfig.isLeftGravity());
        sbtnLeftLockX.setOpened(App.rockerConfig.isLeftLockX());
        sbtnLeftLockY.setOpened(App.rockerConfig.isLeftLockY());
        sbtnLeftRecoverY.setOpened(App.rockerConfig.isLeftRecoverY());

        sbtnRightGravity.setOpened(App.rockerConfig.isRightGravity());
        sbtnRightLockX.setOpened(App.rockerConfig.isRightLockX());
        sbtnRightLockY.setOpened(App.rockerConfig.isRightLockY());
        sbtnRightRecoverY.setOpened(App.rockerConfig.isRightRecoverY());

        sbtnLeftGravity.setOnClickListener(onSwitchButtonClickListener);
        sbtnLeftLockX.setOnClickListener(onSwitchButtonClickListener);
        sbtnLeftLockY.setOnClickListener(onSwitchButtonClickListener);
        sbtnLeftRecoverY.setOnClickListener(onSwitchButtonClickListener);

        sbtnRightGravity.setOnClickListener(onSwitchButtonClickListener);
        sbtnRightLockX.setOnClickListener(onSwitchButtonClickListener);
        sbtnRightLockY.setOnClickListener(onSwitchButtonClickListener);
        sbtnRightRecoverY.setOnClickListener(onSwitchButtonClickListener);
    }

    View.OnClickListener onSwitchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            App.rockerConfig.setLeftGravity(sbtnLeftGravity.isOpened());
            App.rockerConfig.setLeftLockX(sbtnLeftLockX.isOpened());
            App.rockerConfig.setLeftLockY(sbtnLeftLockY.isOpened());
            App.rockerConfig.setLeftRecoverY(sbtnLeftRecoverY.isOpened());

            App.rockerConfig.setRightGravity(sbtnRightGravity.isOpened());
            App.rockerConfig.setRightLockX(sbtnRightLockX.isOpened());
            App.rockerConfig.setRightLockY(sbtnRightLockY.isOpened());
            App.rockerConfig.setRightRecoverY(sbtnRightRecoverY.isOpened());

            App.saveRockerConfig(App.rockerConfig);

            if(onSwitchButtonClickListener != null) {
                onRockerSetupChangeListener.onRockerSetupChange(App.rockerConfig);
            }
        }
    };

    public void setOnSwitchButtonClickListener(View.OnClickListener onSwitchButtonClickListener) {
        this.onSwitchButtonClickListener = onSwitchButtonClickListener;
    }

    public interface OnRockerSetupChangeListener {
        void onRockerSetupChange(RockerConfig rockerConfig);
    }
}
