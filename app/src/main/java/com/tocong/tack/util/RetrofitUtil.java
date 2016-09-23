package com.tocong.tack.util;

import com.tocong.tack.service.RetrofitService;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-22.16:28
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class RetrofitUtil {
    private static RetrofitUtil mInstance;
    private OkHttpClient mOkHttpClient;
    Retrofit mRetrofit;
    RetrofitService mRetrofitService;

    private RetrofitUtil() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        mOkHttpClient = new OkHttpClient().newBuilder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(Constants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        mRetrofitService = mRetrofit.create(RetrofitService.class);
    }

    public static RetrofitUtil getmInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtil.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitUtil();
                }
            }
        }
        return mInstance;
    }

    public static Call<String> asynPost(Map<String,String> params){
        return getmInstance()._asynPost(params);
    }

    private Call<String> _asynPost(Map<String, String> params) {
            return  mRetrofitService.getData(params);
    }

}
