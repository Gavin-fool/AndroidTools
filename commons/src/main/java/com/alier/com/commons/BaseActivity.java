package com.alier.com.commons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    public void onClick(View v) {

    }
}
