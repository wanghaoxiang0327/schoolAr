package com.pomelo.schoolar.utils;

import com.pomelo.schoolar.BuildConfig;
import com.pomelo.schoolar.network.ApiService;

import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by wanghaoxiang on 2020-03-06.
 */

public class DownLoadUtils {
    public DownLoadInterface downLoadInterface;
    Call<ResponseBody> call;

    public void setDownLoadInterface(DownLoadInterface downLoadInterface) {
        this.downLoadInterface = downLoadInterface;
    }

    public void downLoadFile(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                //通过线程池获取一个线程，指定callback在子线程中运行。
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .baseUrl("http://game.yunsouke.top/")
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        call = apiService.download(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                downLoadInterface.downLoadSuccess(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public interface DownLoadInterface {
        void downLoadSuccess(Response<ResponseBody> response);
    }

    public void cancleReq() {
        call.cancel();
    }


}
