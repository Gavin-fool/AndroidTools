package com.alier.com.commons;

import android.content.Context;
import android.os.Environment;


import com.alier.com.commons.utils.DeviceInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author gavin_fool
 *
 */
public class Global{
	public static Context context;
	
	public Global(Context mContext) {
		super();
		Global.context = mContext;
	}

	public static String g_MapPath = getRootDataPath(BaseApp.mContext)+"/AndroidTest/MapData/world.smwu";
	/*
	 * 所有的activity
	 */
	public static HashMap<String, ArrayList<String>> SYS_ACTIVITIES = null;

	/*
	 * 需要加载显示的模块
	 */
	public static HashMap<String, ArrayList<String>> LOAD_MODULES = null;

	/**
	 * 获取手机外置挂载点路径
	 * 
	 * @return
	 */
	public static String getAbsolutePath() {
		File sdCardPath = Environment.getExternalStorageDirectory();
		return sdCardPath.getAbsolutePath();
	}

	/**
	 * @Description:从手机中获取软件根目录
	 * @param:@param context
	 * @param:@return
	 * @return:String 如果全都没有，返回null
	 */
	public static String getRootDataPath(Context context) {
		String[] paths = DeviceInformation.getVolumePaths(context);
		File file = null;
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i]+"/AndroidTest/";
			file = new File(path);
			if(file.exists()){
				return paths[i];
			}
		}
		return null;
	}
}
