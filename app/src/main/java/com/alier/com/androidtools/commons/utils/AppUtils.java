package  com.alier.com.androidtools.commons.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/** 
 * Android常用10大快速开发工具类之--App相关辅助类AppUtilsvb
 * @author  作者 : gavin_fool
 * @date 创建时间：2016年8月2日 上午10:11:18 
 * @version 1.0 
 */
public class AppUtils {

	/**
	 * 获取应用程序名称
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context){
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取应用程序版本名称
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context){
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
