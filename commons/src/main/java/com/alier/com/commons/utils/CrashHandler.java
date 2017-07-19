package com.alier.com.commons.utils;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 全局异常捕获
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017/7/18 17:09
 * @email gavin_fool@163.com
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static CrashHandler crashHandler;
    private Context mContext;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private CrashHandler() {

    }

    /**
     * 使用单例模式，保证只有一个crashHandler
     *
     * @return
     */
    public static CrashHandler getCrashHandler() {
        if (null == crashHandler) {
            crashHandler = new CrashHandler();
        }
        return crashHandler;
    }

    public void init(Context ctx) {
        this.mContext = ctx;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handlerExceptionInfo(e) && null != uncaughtExceptionHandler) {
            uncaughtExceptionHandler.uncaughtException(t, e);
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                LogMgr.error(TAG, "线程异常：" + e1.toString());
            }
        }
    }

    private boolean handlerExceptionInfo(Throwable e) {
        if (null == e) {
            LogMgr.info(TAG, "异常捕获为空");
            return true;
        }
        final String msg = e.getLocalizedMessage();
        if (msg == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();//创建looper，保证toast能在子线程中显示
                Toast toast = Toast.makeText(mContext, "程序出错了，请稍后重试:\r\n" + msg, Toast.LENGTH_LONG);
//                LogMgr.error(TAG, "程序出错了，请稍后重试:\r\n" + msg);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Looper.loop();
            }
        }.start();
        // 保存错误报告文件
        saveCrashInfoToFile1(e);
        //流量监控
        int appId = TrafficStatsUtils.getUid(mContext);
        if (appId != -1) {
            TrafficStatsUtils.trafficMonitor(mContext, appId);
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
            LogMgr.error(TAG, "错误日志为:" + sw.toString());
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
}
