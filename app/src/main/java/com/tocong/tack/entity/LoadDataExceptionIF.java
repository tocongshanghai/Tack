package com.tocong.tack.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 此类描述的是：初步解析数据接口
 * 作者：肖雷
 * 时间：2016/5/27 18:04
 * 公司：上海家乐宝真好电子商务有限公司
 */
public interface LoadDataExceptionIF {
    /**
     * fragment 调activity方法
     * @param jsonObject son
     * @throws JSONException
     */
    void getAcLoadDataExceptionMethod(JSONObject jsonObject) throws JSONException;
}
