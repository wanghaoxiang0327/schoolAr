package com.pomelo.schoolar.network;

import com.pomelo.schoolar.base.BaseModel;
import com.pomelo.schoolar.bean.ModelBean;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by wanghaoxiang on 2019-07-20.
 * 所有接口的封装
 */

public interface ApiService {
    /**
     * 注册
     *
     * @return
     */
    @Multipart
    @POST("Imageup/index")
    Observable<BaseModel<ModelBean>> scan(@Part MultipartBody.Part file);

    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url);
}
