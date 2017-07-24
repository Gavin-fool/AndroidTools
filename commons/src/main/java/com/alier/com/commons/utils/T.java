package com.alier.com.commons.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Android常用10大快速开发工具类之--Toast统一管理类
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2016年7月22日 下午2:18:25
 */
public class T {

    public static boolean isShow = true;
    private static Toast mToast = null;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (!isShow)
            return;
        if (null == mToast) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        if (!isShow)
            return;
        if (null == mToast) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (!isShow)
            return;
        if (null == mToast) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (!isShow)
            return;
        if (null == mToast) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (!isShow)
            return;
        if (null == mToast) {
            mToast = Toast.makeText(context, message, duration);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (!isShow)
            return;
        if (null == mToast) {
            mToast = Toast.makeText(context, message, duration);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

}
