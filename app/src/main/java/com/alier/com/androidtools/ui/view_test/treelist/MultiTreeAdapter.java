package com.alier.com.androidtools.ui.view_test.treelist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alier.com.controllerlibrary.tree.bean.Node;
import com.alier.com.controllerlibrary.tree.bean.TreeListViewAdapter;

import java.util.List;

/**
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017/7/19 9:29
 * @email gavin_fool@163.com
 */
public class MultiTreeAdapter<T> extends TreeListViewAdapter<T> {

    public MultiTreeAdapter(ListView mTree, Context context, List<T> datas,
                            int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
    }

    @Override
    public View getConvertView(Node node, int position, View convertView, ViewGroup parent) {
        return null;
    }
}
