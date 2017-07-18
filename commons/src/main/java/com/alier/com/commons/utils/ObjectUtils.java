package com.alier.com.commons.utils;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Node;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ObjectUtils {
	private static String errorMsg = "";
	private static String suss = "校验成功";

	/**
	 * 如果bean对象为空的话，返回true , 否者返回false
	 * 
	 * @param bean
	 *            普通的对象是否为空
	 * @return
	 */
	public static boolean isEmpty(String bean) {
		if (bean == null || "".equals(bean.trim())
				|| "null".equalsIgnoreCase(bean)) {
			return true;
		}
		return false;
	}

	/**
	 * 监测node是否是xml格式
	 * 
	 * @param node
	 *            xml格式的节点
	 * @return node 为 空 或者是不符合和xml格式时，返回false ，其他返回true
	 */
	public static boolean checkNodeAccordXML(Node node) {
		if (node == null) {
			return false;
		}
		if (isEmpty(node.toString())) {
			return false;
		}
		try {
			node.asXML();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 校验信息
	 * 
	 * @param type
	 *            0：正则表达式校验，regexp 必须有值;1：电话号码校验;2:身份证校验;3:邮箱校验
	 * @param regexp
	 *            校验的正则表达式
	 * @param msg
	 *            需要校验的信息
	 * @return true:校验成功 ;false:校验失败
	 */
	public static boolean checkMsgValidity(int type, String regexp, String msg) {
		if (isEmpty(msg)) {
			errorMsg = "不能为空";
			return false;
		}
		switch (type) {
		case 0:
			if (isEmpty(regexp)) {
				errorMsg = "校验正则式不能为空";
				return false;
			}
			Pattern p = Pattern.compile(regexp.trim());
			Matcher m = p.matcher(msg.trim());
			if (!m.matches()){
				errorMsg = "校验不通过";
				return false;
			}
			break;
		case 1:
			if (!ValidateUtils.checkMobile(msg) && !ValidateUtils.checkPhone(msg)) {
				errorMsg = "电话号码有误";
				return false;
			}
			break;
		case 2:
			IdCard oIdCard = new IdCard(msg);
			if (oIdCard.isCorrect() != 0) {
				errorMsg = oIdCard.getErrMsg();
				return false;
			}
			break;
		case 3:
			boolean flag = ValidateUtils.checkEmail(msg);
			if (!flag) {
				errorMsg = "邮箱格式输入错误!";
				return false;
			}
			break;
		default:
			break;
		}
		errorMsg = suss;
		return true;
	}

	/**
	 * checkMsgValidity 校验不同过时，可能的原因
	 * 
	 * @return
	 */
	public static String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * 获取版本号
	 */
	public static String getVersion(Context context) {
		String version = "";
		PackageManager manager = context.getPackageManager();
		;
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		if (null != info) {
			String appVersion = info.versionName;
			version = appVersion.substring(appVersion.lastIndexOf("v") + 1);
		}
		return version;
	}

	/**
	 * 判断字符串是否是JSONObject的格式
	 * 
	 * @param strObject
	 *            需要判断的字符串
	 * @return true:是JSONObject格式，false:不是JSONObject格式
	 */
	public static boolean isJson(String strObject) {
		if (isEmpty(strObject)) {
			return false;
		}
		try {
			new JSONObject(strObject);
		} catch (JSONException e) {
			LogMgr.warn("ObjectUtils", strObject + "判断是否是JSONObject格式时异常");
			return false;
		}
		return true;
	}

	/**
	 * 解析一个JSON格式的字符串，整理成需要的字符串格式，在json的每个节点后面拼接一个换行符
	 * 
	 * @param strObject
	 *            需要转换的字符串
	 * @return 
	 *         如果传入的字符串为空或者不是JSONObject的格式，传入什么值则返回什么值，不做处理，如果返回的是JSONObject格式，则正常拼接
	 *         （加换行符）
	 */
	public static String parseJsonToString(String strObject) {
		if (isEmpty(strObject)) {
			return strObject;
		}
		JSONObject mJsonObject = null;
		try {
			mJsonObject = new JSONObject(strObject);
		} catch (JSONException e) {
			LogMgr.warn("ObjectUtils", strObject + "判断是否是JSONObject格式时异常");
			return strObject;
		}
		StringBuffer resInfo = new StringBuffer();
		Iterator<String> mIterator = mJsonObject.keys();
		String value = null;
		while (mIterator.hasNext()) {
			try {
				value = mJsonObject.getString(mIterator.next());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!isEmpty(value)) {
				resInfo.append(value).append("\n");
			}
		}
		if (resInfo.length() > 0) {
			return resInfo.substring(0, resInfo.length() - 1);
		}
		return strObject;
	}

	/**
	 *
	 * @param str
	 *            验证的字符串是否是数字
	 * @return 是数字返回真 不是则返回假
	 */
	public static boolean isNum(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}

		return true;
	}

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}

	/**
	 * 判断字符串是否是整数
	 *
	 * @param value
	 *            待判断字符串
	 * @return 是整数返回true 否则false
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 *
	 * @param value
	 *            待判断字符串
	 * @return 是浮点数返回true 否则false
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			if (value.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
