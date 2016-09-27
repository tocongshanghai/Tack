package com.tocong.tack.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.lyf.xlibrary.util.ActivityLifeManage;
import com.lyf.xlibrary.util.LogUtil;

/**
 * 此类描述的是：
 * 作者：肖雷
 * 时间：2016/5/11 16:49
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.LEVEL=Constants.LOG_LEVEL;
        ActivityLifeManage.init(this);
        ActivityLifeManage.getInstance().openCrashHandler();
        SharedPreferences sharedPreferences=getSharedPreferences("loginrecord", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(this,""+ IsLoginUtil.getUserName(this),Toast.LENGTH_SHORT).show();
    }
}
