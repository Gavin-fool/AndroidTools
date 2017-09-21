package com.alier.com.commons;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;

import com.alier.com.commons.entity.Menu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BaseApp {
    /**
     * 当前安装的apk的版本号
     */
    public static String currentSoftVersion = "";
    /**
     * 日志生成时间间隔
     */
    public static int writeLogTime = 2;

    public static Context mContext;
    /**
     * 判断网络是否连接正常
     */
    public static boolean g_b_netOk = false;
    /**
     * app实例
     */
    public static BaseApp g_baseApp = null;
    /**
     * 使用软引用，避免activity在销毁时造成的内存泄漏。程序中所有的activity，在程序结束时全部finish
     */
    private static HashMap<String, WeakReference<Activity>> s_activityMap = new HashMap<String, WeakReference<Activity>>();
    private static ArrayList<Context> mActivities = new ArrayList<Context>();
    private static ArrayList<BroadcastReceiver> mBroadcastReceivers = new ArrayList<BroadcastReceiver>();
    /**
     * baseActivity实例
     */
    public static BaseActivity baseActivity = null;
    /**
     * 加载的模块集合
     */
    public static List<Menu> modulesItems = new ArrayList<>();

    /**
     * baseapp单利
     *
     * @return BaseApp
     */
    public static BaseApp getBaseInstance() {
        if (null == g_baseApp) {
            g_baseApp = new BaseApp();
        }
        return g_baseApp;
    }

    /**
     * 将所有activity集中管理
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        s_activityMap.put(activity.getLocalClassName(), new WeakReference<>(activity));
    }

    /**
     * 将所有广播集中管理
     *
     * @param context
     * @param bReceiver
     */
    public void addReceiver(Context context, BroadcastReceiver bReceiver) {
        mActivities.add(context);
        mBroadcastReceivers.add(bReceiver);
    }

    /**
     * 当前Appactivity的数量
     *
     * @return int当前Appactivity的数量
     */
    public int getActivityCount() {
        if (null != s_activityMap) {
            return s_activityMap.keySet().size();
        } else {
            return 0;
        }
    }

    /**
     * app退出时注销所有activity和服务，广播
     */
    public void exit() {
        if (mBroadcastReceivers.size() > 0) {
            for (int i = 0; i < mBroadcastReceivers.size(); i++) {
                mActivities.get(i).unregisterReceiver(mBroadcastReceivers.get(i));
            }
        }
        Iterator<String> iterator = s_activityMap.keySet().iterator();
        WeakReference<Activity> activity = null;
        while (iterator.hasNext()) {
            activity = s_activityMap.get(iterator.next());
            if (null != activity) {
                activity.get().finish();
            }
        }
        System.exit(0);//系统退出
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
