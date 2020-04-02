package com.pomelo.schoolar.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.gyf.immersionbar.ImmersionBar;
import com.pomelo.schoolar.R;
import com.pomelo.schoolar.dialog.CommonLoadingDialog;
import com.pomelo.schoolar.utils.ToastUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by wanghaoxiang on 2019-07-20.
 * activity基类
 */

public abstract class BaseActivity extends AppCompatActivity {
    public String TAG = getClass().getSimpleName();
    public Context mContext;
    Unbinder unbinder;
    private boolean isImmersion = true;
    CommonLoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        unbinder = ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        ///沉浸式状态栏
        if (isImmersion) {
            ImmersionBar.with(this).statusBarColor(R.color.color_4f97f3).fitsSystemWindows(true).init();
        }
    }

    public void setImmersionBar(boolean immersionBar) {
        isImmersion = immersionBar;
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    public void initListener() {
    }


    //显示toast
    public void showToast(String s) {
        ToastUtils.showMessage(mContext, s);
    }

    //关闭进度框
    public void closeLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showLoadingDialog() {
        if (dialog == null) {
            dialog = new CommonLoadingDialog(mContext);
        }
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
