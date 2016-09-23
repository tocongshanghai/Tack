package com.tocong.tack.service;

import com.tocong.tack.util.Constants;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-22.16:30
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public interface RetrofitService {
    @FormUrlEncoded
    @POST(Constants.IF_mgr_food)
    Call<String>  getData(@FieldMap(encoded = true) Map<String,String> params);
}
