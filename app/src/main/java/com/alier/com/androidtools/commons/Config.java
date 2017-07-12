package com.alier.com.androidtools.commons;

/**
 * @Title:Config
 * @description:系統配置工具類，主要配置系統各種文件存放位置
 * @author:gavin_fool
 * @date:2016年10月25日上午10:23:27
 * @version:v1.0
 */
public class Config {
	/**
	 * 軟件根目錄所在挂載點路徑
	 */
//	public static final String ROOT_PATH = Global.getRootDataPath(BaseApp.mContext);
	public static final String ROOT_PATH = Global.getAbsolutePath();
	/**
	 * 軟件根目錄路勁
	 */
	public static final String ANDROID_TEST_PATH = ROOT_PATH + "/androidTest";
	/**
	 * 地图存储位置
	 */
	public static final String MAP_ROOT_PATH = ANDROID_TEST_PATH + "/MapData/World.smwu";
	/**
	 * 地图许可位置
	 */
	public static final String MAP_LIC_PATH = ANDROID_TEST_PATH + "/supermap/lic/";
}
