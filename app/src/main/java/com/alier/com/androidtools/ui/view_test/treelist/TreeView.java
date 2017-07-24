package com.alier.com.androidtools.ui.view_test.treelist;

import android.view.View;
import android.widget.Button;
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
    private Button checkSwitchBtn;
    private TreeListViewAdapter mAdapter;
    //标记是显示Checkbox还是隐藏
    private boolean isHide = true;
    @Override
    public void init() {
        setContentView(R.layout.activity_tree_view);
        initDatas();
        mTree = (ListView) findViewById(R.id.id_tree);
        checkSwitchBtn = (Button)this.findViewById(R.id.btnShowCheckBox);
        checkSwitchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isHide){
                    isHide = false;
                    checkSwitchBtn.setText("隐藏CHECKBOX");
                }else{
                    isHide = true;
                    checkSwitchBtn.setText("显示CHECKBOX");
                }
                //切换checkbox
                mAdapter.updateView(isHide);
            }
        });
        try {
            mAdapter = new SimpleTreeAdapter<FileBean>(mTree, this, mDatas, 10,isHide);

            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    if (node.isLeaf()) {
                        T.showShort(TreeView.this,String.valueOf(node.getValue()));
                    }
                }

                @Override
                public void onCheckChange(Node node, int position, List<Node> checkedNodes) {
                    StringBuffer sb = new StringBuffer();
                    for (Node n : checkedNodes) {
                        sb.append(String.valueOf(n.getValue())).append("---")
                                .append(n.getId()).append(";");
                    }
                    T.showShort(TreeView.this,sb);
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
        mDatas.add(new FileBean(3, 1, "文档","value3",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(4, 1, "程序","value4",R.color.light_blue,10,R.drawable.user_change));
        mDatas.add(new FileBean(5, 2, "war3","value5",R.color.red,10,R.drawable.user_change));
        mDatas.add(new FileBean(6, 2, "刀塔传奇","value6",R.color.light_blue,20,R.drawable.user_change));

        mDatas.add(new FileBean(7, 4, "面向对象","value7",R.color.light_blue,10,R.drawable.user_change));
        mDatas.add(new FileBean(8, 4, "非面向对象","value8",R.color.light_blue,10,R.drawable.user_change));
        mDatas.add(new FileBean(9, 4, "非面向对象","value9",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(10, 4, "非面向对象","value10",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(11, 4, "非面向对象","value11",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(12, 4, "非面向对象","value12",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(13, 4, "非面向对象","value13",R.color.light_blue,20,R.drawable.user_change));

        mDatas.add(new FileBean(14, 7, "C++","value14",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(15, 7, "JAVA","value15",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(16, 7, "Javascript","value16",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(17, 14, "C++1","value17",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(18, 14, "C++2","value18",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(19, 14, "C++3","value19",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(20, 17, "C++3","value20",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(21, 17, "C++3","value21",R.color.light_blue,20,R.drawable.user_change));
        mDatas.add(new FileBean(22, 17, "C++3","value22",R.color.light_blue,20,R.drawable.user_change));
    }
}
