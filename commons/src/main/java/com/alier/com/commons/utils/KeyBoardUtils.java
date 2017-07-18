package com.alier.com.commons.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Android常用10大快速开发工具类之--软键盘工具类KeyBoardUtils
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年8月2日 上午10:29:56
 * @version 1.0
 */
public class KeyBoardUtils {

	/**
	 * 打开软键盘
	 * @param mEditText
	 * @param context
	 */
	public static void openKeyBoard(EditText mEditText, Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	/**
	 * 关闭软键盘
	 * @param mEditText
	 * @param context
	 */
	public static void closeKeyBoard(EditText mEditText,Context context){
		InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}
}
