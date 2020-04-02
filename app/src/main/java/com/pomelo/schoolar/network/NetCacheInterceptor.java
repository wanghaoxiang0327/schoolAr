package com.pomelo.schoolar.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wanghaoxiang on 2019-07-17.
 * 网络拦截器进行网络缓存
 */

public class NetCacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int onLineCacheTime = 60;
        return response.newBuilder().header("Cache-Control", "public, max-age=" + onLineCacheTime)
                .removeHeader("Pragma")
                .build();
    }
}
