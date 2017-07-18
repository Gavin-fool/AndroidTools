package com.alier.com.androidtools.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Iterator;


import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.ui.map.IM_BaseMap;
import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.BaseApp;
import com.alier.com.commons.Global;
import com.alier.com.commons.entity.CookieKeyBean;
import com.alier.com.commons.entity.Menu;
import com.alier.com.commons.utils.Cookie;
import com.alier.com.commons.utils.MD5Util;
import com.alier.com.commons.utils.ObjectUtils;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.lockview.LockView;

/**
 * 软件初始化界面，用于完成在进入主界面前程序需要完成所有需要的初始化加载工作
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2016年8月22日 上午11:07:48
 */
public class AppInit extends BaseActivity {
    private Button btn_begin;
    private Button btnSetPassword;
    private Button btnForgetPassword;
    private boolean isLogin = false;
    private String password = "";
    private boolean isSetPassword = false;

    // 应用初始化
    public void init() {
        initView();
        BaseApp.mContext = getApplicationContext();
        new Thread(AppInitRunnable).start();// 在子线程中初始化应用数据
    }

    private void initView() {
        setContentView(R.layout.activity_app_init);
        btn_begin = (Button) findViewById(R.id.begin_app);
        btn_begin.setOnClickListener(this);
        btnSetPassword = (Button) this.findViewById(R.id.btn_set_password);
        btnSetPassword.setOnClickListener(this);
        btnForgetPassword = (Button) this.findViewById(R.id.btn_forget_password);
        btnForgetPassword.setOnClickListener(this);
        LockView lockView = (LockView) this.findViewById(R.id.lockView);
        final Cookie cookie = Cookie.getInstance(this, Cookie.USER_DATA);
        password = cookie.getVal(CookieKeyBean.APP_PASSWORD);
        if (ObjectUtils.isEmpty(password)) {
            btnForgetPassword.setVisibility(View.GONE);
        }
        lockView.setOnDrawFinishedListener(new LockView.OnDrawFinishedListener() {
            @Override
            public boolean onDrawFinished(List<Integer> passPositions) {
                StringBuilder sb = new StringBuilder();
                for (Integer i : passPositions) {
                    sb.append(i.intValue());
                }
                String md5Str = MD5Util.toMD5(sb.toString());//  把字符串md5
                if (isSetPassword) {
                    cookie.removeVal(CookieKeyBean.APP_PASSWORD);
                    cookie.putVal(CookieKeyBean.APP_PASSWORD, md5Str);
                    T.showShort(AppInit.this, "设置密码成功");
                    btnSetPassword.setVisibility(View.VISIBLE);
                    btnForgetPassword.setVisibility(View.VISIBLE);
                    btn_begin.setVisibility(View.VISIBLE);
                    isSetPassword = false;
                } else { // 比较路径是否一致
                    if (md5Str.equals(password)) {
                        T.showShort(AppInit.this, "手势密码正确！");
                        toTarget();
                    } else {
                        T.showShort(AppInit.this, "手势密码错误，请重试！");
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void exec() {

    }

    Runnable AppInitRunnable = new Runnable() {

        @Override
        public void run() {
            // 加载应用中所有的activity和需要加载的模块
            if (null == Global.SYS_ACTIVITIES) {
                Global.SYS_ACTIVITIES = new HashMap<String, ArrayList<String>>();
            } else {
                Global.SYS_ACTIVITIES.clear();
            }
            if (null == Global.LOAD_MODULES) {
                Global.LOAD_MODULES = new HashMap<String, ArrayList<String>>();
            } else {
                Global.LOAD_MODULES.clear();
            }
            ArrayList<String> arrays = null;
            String[] itemInfo = null;
            String[] mactivitys = getResources().getStringArray(R.array.activities);
            if (null != mactivitys && mactivitys.length > 0) {
                for (int i = 0; i < mactivitys.length; i++) {
                    itemInfo = mactivitys[i].split(",");
                    arrays = new ArrayList<String>();
                    arrays.add(itemInfo[0]);// 类的标记名称
                    arrays.add(itemInfo[1]);// 类的分类
                    arrays.add(itemInfo[2]);// 类的路径
                    Global.SYS_ACTIVITIES.put(itemInfo[0], arrays);
                }
            }
            String[] modules = getResources().getStringArray(R.array.modules);
            if (null != modules && modules.length > 0) {
                for (int i = 0; i < modules.length; i++) {
                    itemInfo = modules[i].split(",");
                    arrays = new ArrayList<String>();
                    arrays.add(itemInfo[0]);// 模块名称
                    arrays.add(itemInfo[1]);// 模块显示的名称
                    arrays.add(itemInfo[2]);// pkid
                    arrays.add(itemInfo[3]);// 排序
                    arrays.add(itemInfo[4]);// 是否显示（0：显示；1：不显示）
                    arrays.add(itemInfo[5]);// 模块显示引用的资源图片
                    Global.LOAD_MODULES.put(itemInfo[0], arrays);
                }
            }
            loadLocalModuls();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AppInitHandler.sendEmptyMessage(1);
        }
    };

    // 加载本地模块
    private void loadLocalModuls() {
        Iterator<Entry<String, ArrayList<String>>> iterator = Global.LOAD_MODULES.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, ArrayList<String>> entry = iterator.next();
            ArrayList<String> vals = entry.getValue();
            if (!vals.get(4).equals("0")) {
                iterator.remove();
                continue;
            }
            Menu menu = new Menu();
            menu.setTarget(vals.get(0));
            menu.setCaption(vals.get(1));
            menu.setPkid(Integer.parseInt(vals.get(2)));
            menu.setOrder(vals.get(3));
            menu.setLevel("0");
            menu.setIcon(vals.get(5));
            BaseApp.modulesItems.add(menu);
            if (BaseApp.modulesItems.size() > 0) {
                Collections.sort(BaseApp.modulesItems, new sortModules());
            }
        }
    }

    /**
     * 给modules排序
     *
     * @author fuguang
     */
    private class sortModules implements Comparator<Menu> {

        @Override
        public int compare(Menu lhs, Menu rhs) {
            int result = Integer.parseInt(lhs.getOrder()) - Integer.parseInt(rhs.getOrder());
            return result;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_set_password) {
            btn_begin.setVisibility(View.GONE);
            btnForgetPassword.setVisibility(View.GONE);
            btnSetPassword.setVisibility(View.GONE);
            isSetPassword = true;
        } else if (id == R.id.btn_forget_password) {

        } else if (id == R.id.begin_app) {// 点击开始按钮，进入主界面
            if (ObjectUtils.isEmpty(password)) {
                isLogin = true;
                toTarget();
            } else {
                T.showShort(this, "请先验证密码");
            }

        }
    }

    Handler AppInitHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (!isLogin) {
//				toTarget();
            }
        }

        ;
    };

    private void toTarget() {
        IM_BaseMap.initMapEnvironment(BaseApp.mContext);
        Intent intent = new Intent(AppInit.this, MainActivity.class);
        startActivity(intent);
        AppInit.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        BaseApp.getBaseInstance().exit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
