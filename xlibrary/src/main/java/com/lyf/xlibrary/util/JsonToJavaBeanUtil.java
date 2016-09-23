package com.lyf.xlibrary.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 此类描述的是：
 * 作者：肖雷
 * 时间：2016/5/11 16:14
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class JsonToJavaBeanUtil {
    /**
     * 此方法描述的是：把Json类型的string转成javabean或list(list用数组方式 比如目标要返回List<Object>，可以先返回Object[]，再通过Arrays.asList(Object[])转成集合)
     *
     * @param jsonString
     * @param cls 类.class 返回集合的话用数组.class
     * @return T
     */
    public static <T> T getFromJsonToJavabean(String jsonString, Class<T> cls) {
        T t = null;
        Gson gson = new Gson();
        t = gson.fromJson(jsonString, cls);
        return t;
    }
    /**
     * 此方法描述的是：把Json类型的JsonElement转成javabean或list(list用数组方式 比如目标要返回List<Object>，可以先返回Object[]，再通过Arrays.asList(Object[])转成集合)
     *
     * @param jsonElement
     * @param cls 类.class 返回集合的话用数组.class
     * @return T
     */
    public static <T> T getFromJsonToJavabean(JsonElement jsonElement, Class<T> cls) {
        T t = null;
        Gson gson = new Gson();
        t = gson.fromJson(jsonElement, cls);
        return t;
    }
}
