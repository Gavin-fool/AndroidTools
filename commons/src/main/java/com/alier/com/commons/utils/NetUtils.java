package com.alier.com.commons.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Android常用10大快速开发工具类之--网络工具类NetUtils
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年8月2日 上午10:41:16
 * @version 1.0
 */
public class NetUtils {

	/**
	 * 判断是否连接网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != connectivityManager) {
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否是WiFi连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null)
			return false;
		return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 打开网络设置设置
	 * @param activity
	 */
	public static void openSetting(Activity activity) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}
}
