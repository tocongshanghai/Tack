package com.lyf.xlibrary.util;

import android.util.Log;

/**
 * 此类描述的是：日志工具类
 * 作者：肖雷
 * 时间：2016/5/11 15:11
 * 公司：
 */

public class LogUtil {
	/** 显示日志的等级 1->verbose; 2->DEBUG;3->INFO;4->WARN;5->ERROR;6->不显示 */
	public static int LEVEL =1;
	public static int VERBOSE = 1;
	public static int DEBUG = 2;
	public static int INFO = 3;
	public static int WARM = 4;
	public static int ERROR = 5;

	public static void v(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LEVEL <= DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LEVEL <= INFO) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LEVEL <= WARM) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LEVEL <= ERROR) {
			Log.e(tag, msg);
		}
	}
}
