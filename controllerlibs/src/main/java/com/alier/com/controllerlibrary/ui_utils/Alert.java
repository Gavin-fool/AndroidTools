package com.alier.com.controllerlibrary.ui_utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alier.com.controllerlibrary.R;

/**
 * 加载框弹出
 */
public class Alert {
    private static Activity mActivity;
    private static boolean isProgressShow = false;
    private static View progressView = null;
    private static PopupWindow progressPopup = null;
    private static View mSender = null;

    public static boolean isProgressShow() {
        return isProgressShow;
    }

    /**
     * 模式加载条
     *
     * @param activity 当前activity
     * @param msg      加载提示消息
     */
    public static void progresShow(Activity activity, String msg) {
        mActivity = activity;
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        progressView = layoutInflater.inflate(R.layout.progress_layout, null, false);
        TextView txtMsg = (TextView) progressView.findViewById(R.id.txt_msg_progress);
        txtMsg.setText(msg);
        txtMsg.requestFocus();
        txtMsg.setFocusable(true);
        progressPopup = new PopupWindow(progressView, -1, -1);
        progressPopup.setAnimationStyle(R.anim.fade);
        progressPopup.showAtLocation(progressView, Gravity.CENTER, 0, 0);
        isProgressShow = true;
    }

    /**
     * <p>弹出正在加载提示框</p>
     *
     * @param msg    提示消息
     * @param sender 消息触发者
     */
    public static void progresShow(String msg, View sender) {
        progresShow((Activity) sender.getContext(), msg);
        mSender = sender;
        mSender.setEnabled(false);
    }

    /**
     * <p>关闭正在加载提示框</p>
     */
    public static void progressClose() {
        if (null != progressPopup) {
            if (mActivity != null && !mActivity.isFinishing()) {
                progressPopup.dismiss();
                progressPopup = null;
            }
        }
        if (null != mSender) {
            mSender.setEnabled(true);
            mSender = null;
        }
        isProgressShow = false;
    }

}
