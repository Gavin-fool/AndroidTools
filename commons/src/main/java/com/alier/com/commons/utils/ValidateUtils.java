package com.alier.com.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {

	/**
	 * 
	 * @param str 验证的字符串是否是整形数字
	 * @return 是数字返回true,否则返回false.
	 */
	public  static boolean isNum(String str){ 
		for(int i=str.length();--i>=0;){ 
			int chr=str.charAt(i); 
			if(chr<48 || chr>57) 
			return false;
		} 
		return true;  
	}
	/**
	 * 判断传入的客串是否是整形数字
	 * @param string
	 * @return 是数字返回true，否则返回false.
	 */
	public static boolean isNumeric(String string){
		for(int i=string.length();--i>=0;){ 
			int chr=string.charAt(i); 
			if(chr<48 || chr>57) 
			return false;
		} 
		return true;
	}
	/**
	 * 判断传入的字符串是否是浮点数，整数也可以
	 * @param str 需要验证的字符串
	 * @return 是浮点数字返回true，不是返回false
	 */
	public static boolean isFloat(String str){
		return str.matches("[0-9]+\\.?[0-9]*");
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

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}

	/**
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkMobile(String mobile) {
		String regex = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		return Pattern.matches(regex, mobile);
	}

	/**
	 * 验证固定电话号码
	 *
	 * @param phone
	 *            电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
	 *            <p>
	 *            <b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9
	 *            的一位或多位数字， 数字之后是空格分隔的国家（地区）代码。
	 *            </p>
	 *            <p>
	 *            <b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
	 *            对不使用地区或城市代码的国家（地区），则省略该组件。
	 *            </p>
	 *            <p>
	 *            <b>电话号码：</b>这包含从 0 到 9 的一个或多个数字
	 *            </p>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPhone(String phone) {
		String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
		return Pattern.matches(regex, phone);
	}

	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

}
