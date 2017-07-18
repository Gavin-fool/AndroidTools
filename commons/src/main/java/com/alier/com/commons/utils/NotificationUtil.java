package com.alier.com.commons.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.alier.com.commons.R;

public class NotificationUtil {
    private static String iconName = "icon";

    /**
     * 生成一个通知,这个是断网的时候调用的，点击通知会进入到网络设置的界面
     *
     * @param mContext 上下文对象
     */
    public void createNotification(Context mContext, String contentTitile, String contentText) {
        Notification noti = new Notification.Builder(mContext)
                .setContentTitle(contentTitile)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(666, noti);
    }

    /**
     * 生成一个通知
     *
     * @param context  上下文对象
     * @param activity 点击通知时，进入到的Activity，Activity 是包括包名在内的全路径
     * @param notiMes  通知栏看到的信息
     * @param title    通知的标题
     * @param msg      通知的内容
     */
    public void createNotification(Context context, String activity,
                                   String notiMes, String title, String msg) {
        Intent intent = new Intent();
        intent.setClassName(context, activity);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(notiMes)
                .setContentIntent(mPendingIntent)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(888, notification);
    }
}
