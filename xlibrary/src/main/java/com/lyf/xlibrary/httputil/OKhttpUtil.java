package com.lyf.xlibrary.httputil;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.internal.$Gson$Types;
import com.lyf.xlibrary.util.LogUtil;
import com.lyf.xlibrary.util.StringUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody.Builder;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 此类描述的是：OKhttp封装
 * 作者：肖雷
 * 时间：2016/5/11 15:11
 * 公司：
 */

public class OKhttpUtil {
    private static OKhttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private final static String TAG = "OKhttpUtil";

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    private OKhttpUtil() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        // 保存Session
        mOkHttpClient = new OkHttpClient().newBuilder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static OKhttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (OKhttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OKhttpUtil();
                }
            }
        }
        return mInstance;
    }

    public static void asynPost(String url, Map<String, String> params, CallResult clallResult) {
        getInstance()._asynPost(url, params, clallResult);
    }

    private void _asynPost(String url, Map<String, String> params, CallResult mcallResult) {
        Builder builder = new Builder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                String key = param.getKey();
                String value = param.getValue();
                if (!StringUtil.stringIsNull(key)) {
                    builder.add(key, value == null ? "" : value);
                }
            }
        }
        final CallResult callResult = mcallResult;
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    int responseCode = response.code();
                    LogUtil.d(TAG, "responseCode=" + responseCode);
                    final String json = response.body().string();
                    if (json.equals("") || json == null) {
                        throw new Exception("返回数据为空," + "responseCode=" + responseCode);
                    }
                    if (callResult.mType == String.class) {
                        sendSuccessResultCallback(json, callResult);
                    } else {
                        sendSuccessResultCallback(new Gson().fromJson(json, callResult.mType), callResult);
                    }
                } catch (final IOException e) {
                    sendFailedStringCallback(response.request(), e, callResult);
                } catch (JsonParseException e) {// Json解析的错误
                    sendFailedStringCallback(response.request(), e, callResult);
                } catch (Exception e) {// 返回数据为空
                    sendFailedStringCallback(response.request(), e, callResult);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, "network is not connection");
                sendFailedStringCallback(call.request(), e, callResult);
            }
        });
    }

    private void sendSuccessResultCallback(final Object o, final CallResult callResult) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callResult != null) {
                    callResult.onSuccess(o);
                }
            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final CallResult callResult) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callResult != null) {
                    callResult.onError(request, e);
                }
            }
        });
    }

    public static abstract class CallResult<T> {

        public Type mType;

        public CallResult() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onSuccess(T response);

    }

}
