package com.pomelo.schoolar.scan;

import android.util.Log;

import com.pomelo.schoolar.base.BaseModel;
import com.pomelo.schoolar.base.BaseObserver;
import com.pomelo.schoolar.base.BasePresenter;
import com.pomelo.schoolar.bean.ModelBean;
import com.pomelo.schoolar.utils.LogUtils;
import com.pomelo.schoolar.utils.StringUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MultipartBody;

/**
 * Created by wanghaoxiang on 2020-03-05.
 */

public class ScanARPresenter extends BasePresenter<ScanARView> {
    public ScanARPresenter(ScanARView baseView) {
        super(baseView);
    }

    public void scan(MultipartBody.Part file) {
        addDisposite(apiService.scan(file), new BaseObserver<BaseModel<ModelBean>>(baseView, false) {
            @Override
            public void onSuccess(BaseModel<ModelBean> o) {
                if (o != null && o.data != null && !StringUtil.isEmpty(o.data.url)) {
                    baseView.uploadSuccess(o.data.url);
                }
            }

            @Override
            public void onError(String msg) {
            }
        });
    }


    private static int sBufferSize = 1024 * 1024 * 10;

    //将输入流写入文件
    public void writeFileFromIS(File file, InputStream is, long totalLength) {
        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
            }
            LogUtils.e("保存成功", "sss");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
