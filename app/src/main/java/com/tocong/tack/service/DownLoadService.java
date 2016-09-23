package com.tocong.tack.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.webkit.MimeTypeMap;

import com.lyf.xlibrary.util.LogUtil;
import com.lyf.xlibrary.util.ToastUtil;
import com.tocong.tack.util.Constants;

import java.io.File;

/**
 * 此类描述的是：下载服务
 * 作者：肖雷
 * 时间：2016/7/7 10:16
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class DownLoadService extends Service {

    private long mTaskId;
    private DownloadManager downloadManager;
    private static String TAG = "DownLoadService";
    private String versionName;
    private DownLoadChangeObserver observer;
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("updateUrl");
        if (url != null) {
            versionName = url.substring(url.lastIndexOf("/") + 1);
            download(url, versionName);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void download(String url, String versionName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));//创建下载任务
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);


        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        request.setTitle("正在下载PDA最新版本...");

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir(Constants.DOWN_LOACD_ADDR, versionName);
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
//        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //注册观察者
        observer = new DownLoadChangeObserver(null);
        getContentResolver().registerContentObserver(CONTENT_URI, true, observer);

    }

    //广播接受者，接收下载状态
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            checkDownloadStatus();//检查下载状态
//        }
//    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        try {


            if (c.moveToFirst()) {
                //获取下载进度
                int fileSizeIdx = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int bytesDLIdx = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int fileSize = c.getInt(fileSizeIdx);
                int bytesDL = c.getInt(bytesDLIdx);
                intent = new Intent();
                intent.setAction("DownLoadService.GetFileSize");
                intent.putExtra("progress",(bytesDL*100)/fileSize);
                sendBroadcast(intent);

                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        LogUtil.i(TAG, ">>>下载暂停");
                    case DownloadManager.STATUS_PENDING:
                        LogUtil.i(TAG, ">>>下载延迟");
                    case DownloadManager.STATUS_RUNNING:
                        LogUtil.i(TAG, ">>>正在下载");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        LogUtil.i(TAG, ">>>下载完成");
                        //下载完成安装APK
                        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
                        installAPK(new File(downloadPath));
                        break;
                    case DownloadManager.STATUS_FAILED:
                        ToastUtil.makeText(this, "下载更新失败", 2).show();
                        stopSelf();
                        LogUtil.i(TAG, ">>>下载失败");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }

    }

    //下载到本地后执行安装
    protected void installAPK(File file) {
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//            receiver = null;
//        }
        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
        }

        super.onDestroy();
    }

    class DownLoadChangeObserver extends ContentObserver {


        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DownLoadChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            checkDownloadStatus();
        }
    }

}
