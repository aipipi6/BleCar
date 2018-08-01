package com.freelink.library.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.freelink.library.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author chenjun
 */
public abstract class BaseDialogFragment extends DialogFragment {

    protected Handler handler = new Handler();

    private Unbinder unbinder;
    protected Context context;
    protected View contentView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    protected int getAnimationStyle() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getActivity();

        if(getAnimationStyle() > 0) {
            getDialog().getWindow().setWindowAnimations(getAnimationStyle());
        }

        contentView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        unbinder = ButterKnife.bind(this, contentView);
        return contentView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder != null && unbinder != Unbinder.EMPTY) {
            unbinder.unbind();
        }
    }


    protected abstract int getLayoutId();
    protected abstract void initData(Bundle savedInstanceState);
}
