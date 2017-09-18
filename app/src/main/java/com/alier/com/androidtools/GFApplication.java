package com.alier.com.androidtools;

import android.app.Application;
import android.content.Context;

import com.alier.com.commons.utils.CrashHandler;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * 应用程序application，主要用于捕获全局异常
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017/7/18 17:02
 * @email gavin_fool@163.com
 */
public class GFApplication extends Application {

    private static RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        //全局异常捕获
        CrashHandler crashHandler = CrashHandler.getCrashHandler();
        crashHandler.init(getApplicationContext());
        //内存泄漏分析
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
        refWatcher = LeakCanary.install(this);
    }

    /**
     * 获取全局的refWatcher
     *
     * @param context
     * @return
     */
    public static RefWatcher getRefWatcher(Context context) {
        GFApplication application = (GFApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
