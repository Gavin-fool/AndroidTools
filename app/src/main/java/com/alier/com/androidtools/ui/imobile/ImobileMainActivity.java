package com.alier.com.androidtools.ui.imobile;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.adapter.ImobileRecyclerViewAdapter;
import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.entity.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 测试imobile主界面
 */
public class ImobileMainActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Menu> modulesLists = new ArrayList<Menu>();

    @Override
    public void init() {
        setContentView(R.layout.content_imobile_main);
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String[] imobileModules = getResources().getStringArray(R.array.ImobileModeles);
        if (null == imobileModules)
            return;
        String[] meunItem = null;
        for (int i = 0; i < imobileModules.length; i++) {
            meunItem = imobileModules[i].split(",");
            if(!meunItem[4].equals("0"))
                continue;
            Menu menu = new Menu();
            menu.setTarget(meunItem[0]);
            menu.setCaption(meunItem[1]);
            menu.setPkid(Integer.parseInt(meunItem[2]));
            menu.setOrder(meunItem[3]);
            menu.setLevel("0");
            menu.setIcon(meunItem[5]);
            modulesLists.add(menu);
            if (modulesLists.size() > 0) {
                Collections.sort(modulesLists, new sortModules());
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
    public void exec() {
        ImobileRecyclerViewAdapter recyclerViewAdapter = new ImobileRecyclerViewAdapter(modulesLists,ImobileMainActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        /**
         * 设置布局：
         * *第一个参数：上下文
         * 第二参数：方向
         * 第三个参数：排序低到高还是高到低显示，false是低到高显示
         */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ImobileMainActivity.this,3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

    }

}