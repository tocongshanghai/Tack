package com.tocong.tack.util;

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
    }
}
