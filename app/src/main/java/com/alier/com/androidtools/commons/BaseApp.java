package com.alier.com.androidtools.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.Configuration;

import com.alier.com.androidtools.entity.Menu;

public class BaseApp extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	public static Context mContext;
	/**判断网络是否连接正常*/
	public static boolean g_b_netOk = false;
	/**app实例*/
	public static BaseApp g_baseApp = null;
	/**程序中所有的activity，在程序结束时全部finish*/
	private static HashMap<String, Activity> s_activityMap = new HashMap<String, Activity>();
	private static ArrayList<Context> mActivities=new ArrayList<Context>();
	private static ArrayList<BroadcastReceiver> mBroadcastReceivers=new ArrayList<BroadcastReceiver>();
	/**baseActivity实例*/
	public static BaseActivity baseActivity = null;
	/** 加载的模块集合 */
	public static List<Menu> modulesItems = new ArrayList<>();
	/**
	 * baseapp单利
	 * @return BaseApp 
	 */
	public static BaseApp getBaseInstance() {
		if (null == g_baseApp) {
			g_baseApp = new BaseApp();
		}
		return g_baseApp;
	}
	/**
	 *将所有activity集中管理
	 * @param activity 
	 */
	public void addActivity(Activity activity){
		s_activityMap.put(activity.getLocalClassName(), activity);
	}
	/**
	 * 将所有广播集中管理
	 * @param context
	 * @param bReceiver
	 */
	public void addReceiver(Context context,BroadcastReceiver bReceiver){
		mActivities.add(context);
		mBroadcastReceivers.add(bReceiver);
	}
	/**
	 * 当前Appactivity的数量
	 * @return int当前Appactivity的数量
	 */
	public int getActivityCount(){
		if(null != s_activityMap){
			return s_activityMap.keySet().size();
		}else{
			return 0;
		}
	}
	/**
	 * app退出时注销所有activity和服务，广播
	 */
	public void exit(){
		if(mBroadcastReceivers.size()>0){
			for(int i = 0;i<mBroadcastReceivers.size();i++){
				mActivities.get(i).unregisterReceiver(mBroadcastReceivers.get(i));
			}
		}
		Iterator<String> iterator = s_activityMap.keySet().iterator(); 
		Activity activity = null;
		while(iterator.hasNext()){
			activity = s_activityMap.get(iterator.next());
			if(null != activity){
				activity.finish();
			}
		}
		System.exit(0);//系统退出
	}
}
