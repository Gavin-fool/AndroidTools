package com.alier.com.androidtools.commons.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Android常用10大快速开发工具类之--单位转换类DensityUtils
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年8月1日 下午3:04:15
 * @version 1.0
 */
public class DensityUtils {
	/**
	 * 设配独立像素（dip/dp）转像素
	 * 
	 * @param context
	 *            ：上下文
	 * @param dpVal
	 *            ：设配独立像素值
	 * @return
	 */
	public static int dp2px(Context context, float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * sp(scale pixel:放大像素，主要用于字体显示)
	 * @param context
	 * @param spVal ：sp像素值
	 * @return
	 */
	public static int sp2px(Context context, float spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
				context.getResources().getDisplayMetrics());
	}
	
	/**
	 * 像素转设配独立像素
	 * @param context
	 * @param pxVal ：像素值
	 * @return
	 */
	public static float px2dp(Context context,float pxVal){
		return pxVal/context.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * 像素转放大像素（sp）
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2sp(Context context,float pxVal){
		return pxVal/context.getResources().getDisplayMetrics().scaledDensity;
	}
	
	
}
