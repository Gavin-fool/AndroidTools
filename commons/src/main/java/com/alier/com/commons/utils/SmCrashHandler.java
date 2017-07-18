package com.alier.com.commons.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Properties;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 崩溃异常处理 当崩溃异常产生时，写入本地日志文件 程序下次启动时，自动上传LOG日志文件到服务器
 * 
 * @author Lv
 * 
 */
public class SmCrashHandler implements UncaughtExceptionHandler {

	/** Debug Log tag */
	public static final String TAG = "SmCrashHandler";
	/**
	 * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 */
	public static final boolean DEBUG = false;
	/** 系统默认的UncaughtException处理类 */
	private UncaughtExceptionHandler mDefaultHandler;
	/** SmCrashHandler实例 */
	private static SmCrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;
	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private Properties mDeviceCrashInfo = new Properties();
	/** 版本名称 */
	private static final String VERSION_NAME = "versionName";
	/** 版本号 */
	private static final String VERSION_CODE = "versionCode";

	/** 保证只有一个SmCrashHandler实例 */
	private SmCrashHandler() {
	}

	/** 获取SmCrashHandler实例 ,单例模式 */
	public static SmCrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SmCrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LogMgr.error(TAG, e.toString());
			}
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			LogMgr.info(TAG, "handleException --- ex==null");
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		if (msg == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast toast = Toast.makeText(mContext, "程序出错了，请稍后重试:\r\n" + msg, Toast.LENGTH_LONG);
				LogMgr.error(TAG, "程序出错了，请稍后重试:\r\n" + msg);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				Looper.loop();
			}
		}.start();
		// 收集设备信息
		collectCrashDeviceInfo(mContext);
		// 保存错误报告文件
		saveCrashInfoToFile1(ex);
		//流量监控
		int appId = TrafficStatsUtils.getUid(mContext);
		if(appId!=-1){
			TrafficStatsUtils.trafficMonitor(mContext,appId);
		}
		return true;
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return
	 */
	private void saveCrashInfoToFile1(Throwable ex) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			LogMgr.error("SmCrashHandler", "错误日志为:" + sw.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pw != null) {
					pw.close();
				}
				if (sw != null) {
					sw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
				mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		// 具体信息请参考后面的截图
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}
	}

}
