package com.freelink.blecar.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.freelink.blecar.R;
import com.freelink.library.base.BaseDialogFragment;


public class SetupDialog extends BaseDialogFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_rocker_setup;
    }

    @Override
    protected int getAnimationStyle() {
        return R.style.FromUpAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), dm.heightPixels);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    public static SetupDialog show(AppCompatActivity activity) {
        SetupDialog dialog = new SetupDialog();
        dialog.show(activity.getSupportFragmentManager(), "");
        return dialog;
    }
}
