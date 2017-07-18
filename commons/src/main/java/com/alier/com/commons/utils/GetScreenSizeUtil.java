package com.alier.com.commons.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * 获取屏幕尺寸工具类
 * @author mw
 *
 */
public class GetScreenSizeUtil {
	
	private Activity mActivity;
	
	public GetScreenSizeUtil(Activity activity){
		mActivity = activity;
	}

	/**
	 * 获取设备屏幕尺寸
	 * @return 屏幕尺寸
	 */
	public double srceenSize(){
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		double a = Math.sqrt(Math.pow(metrics.widthPixels, 2)+Math.pow(metrics.heightPixels, 2))/(160*metrics.density);
		return a;
	}
}
