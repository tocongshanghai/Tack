package com.lyf.xlibrary.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 此类描述的是：Toast 封装类
 * 作者：肖雷
 * 时间：2016/5/11 15:11
 * 公司：
 */

public class ToastUtil {
	private double time;
	private static Handler handler;
	private Timer showTimer;
	private Timer cancelTimer;

	private Toast toast;

	private ToastUtil() {
		showTimer = new Timer();
		cancelTimer = new Timer();
	}

	public void setTime(double time) {
		this.time = time;
	}

	public void setToast(Toast toast) {
		this.toast = toast;
	}

	public static ToastUtil makeText(Context context, String text, double time) {
		ToastUtil toast1 = new ToastUtil();
		toast1.setTime(time);
		toast1.setToast(Toast.makeText(context, text, Toast.LENGTH_SHORT));
		handler = new Handler(context.getMainLooper());
		return toast1;
	}

	public void show() {
		toast.show();
		if (time > 2) {
			showTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.post(new ShowRunnable());
				}
			}, 0, 1900);
		}
		cancelTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(new CancelRunnable());
			}
		}, (long) (time * 1000));
	}

	private class CancelRunnable implements Runnable {
		@Override
		public void run() {
			showTimer.cancel();
			toast.cancel();
		}
	}

	private class ShowRunnable implements Runnable {
		@Override
		public void run() {
			toast.show();
		}
	}
}
