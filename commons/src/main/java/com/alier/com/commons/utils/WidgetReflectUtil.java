package com.alier.com.commons.utils;

import java.lang.reflect.Field;

public class WidgetReflectUtil {
	/**
	 * 通过Layout的名称获取资源Id
	 * 
	 * @param packageName
	 *            包名
	 * @param layoutName
	 *            布局文件的名称
	 * @return 布局文件的资源Id ,异常时返回0
	 */
	public int getLayoutByReflect(String packageName, String layoutName) {
		try {
			Field field = Class.forName(packageName + ".R$layout").getField(
					layoutName);
			return field.getInt(field);
		} catch (Exception e) {
		}
		return 0;

	}

	/**
	 * 通过Image的名称获取资源Id
	 * 
	 * @param packageName
	 *            包名
	 * @param imageName
	 *            图片文件的名称
	 * @return 图片文件的资源Id ,异常时返回0
	 */
	public int getImageByReflect(String packageName, String imageName) {
		try {
			Field field = Class.forName(packageName + ".R$drawable").getField(
					imageName);
			return field.getInt(field);
		} catch (Exception e) {
		}
		return 0;

	}

	/**
	 * 通过字符串的名称获取资源Id
	 * 
	 * @param packageName
	 *            包名
	 * @param stringName
	 *            字符串文件的名称
	 * @return 字符串的资源Id ,异常时返回0
	 */
	public int getStringByReflect(String packageName, String stringName) {
		try {
			Field field = Class.forName(packageName + ".R$string").getField(
					stringName);
			return field.getInt(field);
		} catch (Exception e) {
		}
		return 0;

	}
	/**
	 * 通过控件的名称获取资源Id
	 * 
	 * @param packageName
	 *            包名
	 * @param stringName
	 *           控件的名称
	 * @return 控件的资源Id ,异常时返回0
	 */
	public int getIdByReflect(String packageName, String idName) {
		try {
			Field field = Class.forName(packageName + ".R$id").getField(
					idName);
			return field.getInt(field);
		} catch (Exception e) {
		}
		return 0;
	}
}
