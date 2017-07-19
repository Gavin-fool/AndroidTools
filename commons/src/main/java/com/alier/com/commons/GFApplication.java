package com.alier.com.commons;

import android.app.Application;

import com.alier.com.commons.utils.CrashHandler;

/**
 * 应用程序application，主要用于捕获全局异常
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017/7/18 17:02
 * @email gavin_fool@163.com
 */

public class GFApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getCrashHandler();
        crashHandler.init(getApplicationContext());
    }
}
