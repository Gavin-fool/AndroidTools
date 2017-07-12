package com.alier.com.androidtools.commons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BaseApp.baseActivity == null){
        	BaseApp.baseActivity = BaseActivity.this;
        }
        init();
        BaseApp.getBaseInstance().addActivity(this);
        exec();
    }
    /**
     * 初始设置
     */
    public abstract void init();
    /**
     * 处理业务逻辑
     */
    public abstract void exec();

}
