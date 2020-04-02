package com.pomelo.schoolar.network;



import com.pomelo.schoolar.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by wanghaoxiang on 2019-07-20.
 * 初始化retrofit
 */

public class ApiRetrofit {
    private OkHttpClient okHttpClient;
    private ApiService apiService;
    private static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;
    private final int TIME_SECOND = 15;//设置连接超时时间15s

    public ApiRetrofit() {
        initOkHttp();
        initRetrofit();
    }

    /**
     * 初始化okhttp
     */
    private void initOkHttp() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_SECOND, TimeUnit.SECONDS)//连接超时时间
                .readTimeout(TIME_SECOND, TimeUnit.SECONDS)//读取超时时间
                .addInterceptor(new LoggingInterceptor())//日志拦截器
                .addInterceptor(new NetCacheInterceptor())//设置缓存
                .build();
    }

    /**
     * 初始化retrofit
     */
    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_NB_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持rxjava2
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (Object.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit;
    }



    public ApiService getApiService() {
        return apiService;
    }

}
