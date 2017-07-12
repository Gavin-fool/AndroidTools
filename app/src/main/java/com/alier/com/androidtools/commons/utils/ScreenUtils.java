package com.alier.com.androidtools.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Android常用10大快速开发工具类之--屏幕相关辅助类ScreenUtils
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年8月1日 下午4:24:51
 * @version 1.0
 */
public class ScreenUtils {

	/**
	 * 设配屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 设配屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @return 如果获取到了返回正常值，获取失败返回-1
	 */
	public static int getStatusHeight(Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.gavin.androidtesttools.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
	
	/**
	 * 获取当前屏幕截图，包含状态栏 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapshotWithStatusBar(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		//上面2行必须加入，如果不加如view.getDrawingCache()返回null
		Bitmap bmp = view.getDrawingCache();//截图
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;
	}
	
	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * @param activity
	 * @return
	 */
	public static Bitmap snapshotWithOutStatusBar(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height-statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}
}
