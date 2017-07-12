package com.alier.com.androidtools.commons.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.os.storage.StorageManager;

/**
 * 获取设配信息工具类
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年7月22日 上午11:02:07
 * @version 1.0
 */
public class DeviceInformation {

	/**
	 * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
	 * 
	 * @return 挂载点路径集合
	 */
	public static ArrayList<String> getDevMountList() {
		String[] toSearch = null;
		ArrayList<String> devMountList = new ArrayList<String>();
		try {
			toSearch = FileUtils.readFileToString(new File("/etc/vold.fstab")).split("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null != toSearch && toSearch.length > 0) {
			for (int i = 0; i < toSearch.length; i++) {
				if (toSearch[i].contains("dev_mount")) {
					if (new File(toSearch[i + 2]).exists()) {
						devMountList.add(toSearch[i + 2]);
					}
				}
			}
		}
		return devMountList;
	}

	/**
	 * @Description:获取当前设备所有挂载点
	 * @param:@param context
	 * @param:@return 
	 * @return:String[]
	 */
	public static String[] getVolumePaths(Context context) {
		String[] paths = null;
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		try {
			Method mMethodGetPaths = storageManager.getClass().getMethod("getVolumePaths");
			paths = (String[]) mMethodGetPaths.invoke(storageManager);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return paths;
	}
}
