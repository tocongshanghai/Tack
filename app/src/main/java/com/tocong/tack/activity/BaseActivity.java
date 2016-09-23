package com.tocong.tack.activity;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lyf.xlibrary.util.ActivityLifeManage;
import com.lyf.xlibrary.util.NetUtils;
import com.lyf.xlibrary.util.ScreenUtil;
import com.lyf.xlibrary.util.StringUtil;
import com.lyf.xlibrary.util.ToastUtil;
import com.tocong.tack.R;
import com.tocong.tack.fragment.LoginFragment;
import com.tocong.tack.util.Constants;
import com.tocong.tack.util.LoadDataExceptionIF;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

/**
 * 此类描述的是：activity基类
 * 作者：肖雷
 * 时间：2016/5/11 14:11
 * 公司：上海家乐宝真好电子商务有限公司
 */
public abstract class BaseActivity extends AppCompatActivity implements LoadDataExceptionIF {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    protected Handler mHandler = new Handler();
    protected ActivityLifeManage activityLifeManage;
    protected String TAG = "";
    private NetWorkReceiver netWorkRecevier;//网络连接与断开广播接收器
    private BroadcastReceiver scanReceiver;//扫描器广播接收器
    private IntentFilter mNetFilter;
    private IntentFilter mScanFilter;
    private boolean isNetWorkOKFirst;//是否是首次连接网络（由于断开网络系统会多次回调onRecevice）
    private boolean isNetWorkNOFirst;//是否是首次断开网络（由于断开网络系统会多次回调onRecevice）
    /**
     * 第一次点击返回的系统时间
     */
    private long mFirstClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preSetContentView();
        setContentView(getLayoutResID());
        ButterKnife.bind(this);
        initViews(savedInstanceState);
        activityLifeManage = ActivityLifeManage.getInstance();
        activityLifeManage.addActivity(this);
        TAG = StringUtil.getClassName(this);
        netWorkRecevier = new NetWorkReceiver();
        mNetFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mScanFilter = new IntentFilter("ACTION_BAR_SCAN");
        this.registerReceiver(netWorkRecevier, mNetFilter);
        isNetWorkOKFirst = true;
        isNetWorkNOFirst = true;
        scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //此处获取扫描结果信息
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                getScanResult(scanResult);
            }
        };
    }

    /**
     * 开启扫描
     */
    public void openScan() {
        Intent intent = new Intent("ACTION_BAR_TRIGSCAN");
        intent.putExtra("timeout", 3);
        sendBroadcast(intent);
    }

    /**
     * 此方法描述的是： 扫描后操作
     *
     * @param result 扫描结果
     */
    public void getScanResult(String result) {

    }

    /**
     * 此方法描述的是： setContentView方法调用前执行的一些操作
     */
    public abstract void preSetContentView();

    /**
     * 此方法描述的是： 返回布此Activity布局文件
     */
    public abstract int getLayoutResID();

    /**
     * 此方法描述的是：初始化
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 网络请求成功后对服务端返回来的JSONObject数据进行初步判断
     *
     * @param jsonObject json
     * @throws JSONException
     */
    public boolean loadDataException(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            //ToastUtil.makeText(this, Constants.ERRORMSG, 2).show();
            handlerToPost(Constants.ERRORMSG);
            return false;
        }
        int status = jsonObject.getInt(Constants.STATUS);
        if (status == 0) {
            return true;
        }
        String msg = jsonObject.getString(Constants.MSG);
        if (status == 1101) {
            sendLoginOutTime(this);
            return false;
        }
       // ToastUtil.makeText(this, msg == null ? "" : msg, 2).show();
        handlerToPost(msg);
        return false;
    }

    private void handlerToPost(final String msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.makeText(BaseActivity.this,msg==null?"":msg,2).show();
            }
        });
    }

    @Override
    public void getAcLoadDataExceptionMethod(JSONObject jsonObject) throws JSONException {
        loadDataException(jsonObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册广播
        this.registerReceiver(scanReceiver, mScanFilter);
    }

    @Override
    protected void onPause() {
        //注销获取扫描结果的广播
        if (scanReceiver != null) {
            this.unregisterReceiver(scanReceiver);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        scanReceiver = null;
        mScanFilter = null;
        if (netWorkRecevier != null) {
            this.unregisterReceiver(netWorkRecevier);
        }
        netWorkRecevier = null;
        mNetFilter = null;
        ButterKnife.unbind(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private class NetWorkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (NetUtils.isConnected(context.getApplicationContext())) {
                    if (isNetWorkOKFirst) {//只有首次回调才调用netWorkIsOk();
                        isNetWorkNOFirst = true;
                        netWorkIsOk();
                    }
                } else {
                    //网络断开
                    if (isNetWorkNOFirst) {//只有首次回调才调用netWorkIsNo();
                        isNetWorkOKFirst = true;
                        netWorkIsNo();
                    }
                }
            }
        }

    }

    protected void netWorkIsOk() {
        isNetWorkOKFirst = false;//防止多次回调
    }


    protected void netWorkIsNo() {
        isNetWorkNOFirst = false;//防止多次回调
        ToastUtil.makeText(this, "网络已断开", 1).show();
    }

    @Override
    public void finish() {
        super.finish();
        activityLifeManage.removeActivity(this);
        System.gc();
    }

    /**
     * 登录超时跳到登录界面
     */
    public void sendLoginOutTime(Context context) {
       // ToastUtil.makeText(this, "登录超时", 2).show();
        handlerToPost("登陆超时");
       /* Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);*/
        LoginFragment loginFragment =new LoginFragment();
        FragmentManager fm=getFragmentManager();
        fm.beginTransaction().replace(R.id.fl_content, loginFragment).commit();

    }

    /**
     * 双击退出
     */
    private boolean onDoubleClickExit(long timeSpace) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mFirstClickTime > timeSpace) {
            ToastUtil.makeText(this, "再按一次退出", 1).show();
            mFirstClickTime = currentTimeMillis;
            return false;
        } else {
            return true;
        }
    }

    /**
     * 双击退出，间隔时间为2000ms
     *
     * @return
     */
    public boolean onDoubleClickExit() {
        return onDoubleClickExit(2000);
    }

    /**
     * 设置状态栏沉浸式
     *
     * @param view
     */
    protected void setImmerseLayout(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            /*
             * window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			 * , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 */
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int statusBarHeight = ScreenUtil.getInstance().getStatusHeight(this.getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }
}
