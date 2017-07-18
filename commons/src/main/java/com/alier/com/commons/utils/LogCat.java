package com.alier.com.commons.utils;

import android.util.Log;

/**
 * Android常用10大快速开发工具类之--日志打印工具类，用于打印调试日志
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年7月22日 下午1:54:35
 * @version 1.0
 */
public class LogCat {

	/**
	 * 是否需要打印bug，可以在application的oncreate中初始化，从而控制整个程序的日志打印
	 */
	public static boolean isDebug = true;
	private static final String tag = "TAG";

	// 以下是默认tag="TAG"的四个函数
	public static void i(String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void d(String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	public static void e(String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	public static void v(String msg) {
		if (isDebug) {
			Log.v(tag, msg);
		}
	}

	// 以下是可自选tag的四个函数
	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (isDebug) {
			Log.v(tag, msg);
		}
	}
}
