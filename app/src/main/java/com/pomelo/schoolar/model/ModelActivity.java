package com.pomelo.schoolar.model;

import android.annotation.SuppressLint;
import android.util.Log;

import com.dyman.easyshow3d.ModelFactory;
import com.dyman.easyshow3d.bean.ModelObject;
import com.dyman.easyshow3d.imp.ModelLoaderListener;
import com.dyman.easyshow3d.view.ShowModelView;
import com.pomelo.schoolar.R;
import com.pomelo.schoolar.base.BaseMVPActivity;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wanghaoxiang on 2020-03-05.
 */

public class ModelActivity extends BaseMVPActivity<ModelPresenter> implements ModelView {
    @BindView(R.id.showModelView)
    ShowModelView showModelView;
    String fileName;


    @Override
    public int getLayoutId() {
        return R.layout.activity_model;
    }

    @Override
    public void initView() {
        fileName = getIntent().getStringExtra("fileName");
    }

    @Override
    public void initData() {
        super.initData();
        load();
    }

    private void load() {
        ModelFactory.decodeFile(this, fileName, new ModelLoaderListener() {
            @Override
            public void loadedUpdate(float progress) {
                Log.i("", "模型解析进度： " + progress);
            }

            @SuppressLint("CheckResult")
            @Override
            public void loadedFinish(ModelObject modelObject) {
                if (modelObject != null) {
                    showModelView.setModelObject(modelObject);
                }
            }

            @Override
            public void loaderCancel() {
            }

            @Override
            public void loaderFailure() {
            }
        });
    }

    @Override
    protected ModelPresenter createPresenter() {
        return new ModelPresenter(this);
    }


}