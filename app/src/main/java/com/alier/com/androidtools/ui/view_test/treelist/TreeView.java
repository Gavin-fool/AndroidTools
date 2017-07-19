package com.alier.com.androidtools.ui.view_test.treelist;

import android.widget.ListView;

import com.alier.com.androidtools.R;
import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.tree.bean.Node;
import com.alier.com.controllerlibrary.tree.bean.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TreeView extends BaseActivity {
    private List<FileBean> mDatas = new ArrayList<FileBean>();
    private ListView mTree;
    private TreeListViewAdapter mAdapter;

    @Override
    public void init() {
        setContentView(R.layout.activity_tree_view);
        initDatas();
        mTree = (ListView) findViewById(R.id.id_tree);
        try {
            mAdapter = new SimpleTreeAdapter<FileBean>(mTree, this, mDatas, 10);

            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    if (node.isLeaf()) {
                        T.showShort(TreeView.this,String.valueOf(node.getValue()));
                    }
                }

            });
            mTree.setAdapter(mAdapter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exec() {

    }

    private void initDatas() {

        // id , pid , label , 其他属性
        mDatas.add(new FileBean(1, 0, "文件管理系统","value1",R.color.colorPrimaryDark,28,-1));
        mDatas.add(new FileBean(2, 1, "游戏","value2",R.color.light_blue,20,-1));
        mDatas.add(new FileBean(3, 1, "文档","value3",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(4, 1, "程序","value4",R.color.light_blue,0,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(5, 2, "war3","value5",R.color.light_blue,0,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(6, 2, "刀塔传奇","value6",R.color.light_blue,20,R.drawable.main_icon_zoomin));

        mDatas.add(new FileBean(7, 4, "面向对象","value7",R.color.light_blue,10,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(8, 4, "非面向对象","value8",R.color.light_blue,10,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(9, 4, "非面向对象","value9",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(10, 4, "非面向对象","value10",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(11, 4, "非面向对象","value11",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(12, 4, "非面向对象","value12",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(13, 4, "非面向对象","value13",R.color.light_blue,20,R.drawable.main_icon_zoomin));

        mDatas.add(new FileBean(14, 7, "C++","value14",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(15, 7, "JAVA","value15",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(16, 7, "Javascript","value16",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(17, 14, "C++1","value17",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(18, 14, "C++2","value18",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(19, 14, "C++3","value19",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(20, 17, "C++3","value20",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(21, 17, "C++3","value21",R.color.light_blue,20,R.drawable.main_icon_zoomin));
        mDatas.add(new FileBean(22, 17, "C++3","value22",R.color.light_blue,20,R.drawable.main_icon_zoomin));
    }
}
