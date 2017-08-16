package com.alier.com.androidtools.ui.uitest.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.ui.MPChart.notimportant.ContentItem;
import com.alier.com.androidtools.ui.MPChart.notimportant.MyAdapter;
import com.alier.com.androidtools.ui.uitest.GifShow;
import com.alier.com.androidtools.ui.uitest.ProgressDialogActivity;
import com.alier.com.androidtools.ui.uitest.VHTable.VHTableActivity;
import com.alier.com.androidtools.ui.uitest.treelist.TreeView;

import java.util.ArrayList;

/**
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017/8/9 17:19
 * @email gavin_fool@163.com
 */
public class ViewTestFragment extends Fragment implements AdapterView.OnItemClickListener{
    /**
     * fragment与activity的生命周期方法完全一样，fragment完全依赖于activity生存。
     * onAttach()/onCreat()/onCreateView()/onActivityCreated()、整个生命周期方法属于onCreat阶段，
     * 在activity被创建之前是不能获取到activity中的资源的（例如getActivity()返回的是null）。
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_mp, null);
        initViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initViews(View view) {
        ArrayList<ContentItem> objects = new ArrayList<ContentItem>();
        objects.add(new ContentItem("1.VHTable", "可左右、上下滑动的表格"));
        MyAdapter adapter = new MyAdapter(getContext(), objects);
        ListView lv = (ListView)view.findViewById(R.id.listView1);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position){
            case 0:
                intent= new Intent();
                intent.setClass(getContext(), VHTableActivity.class);
                startActivity(intent);
                break;
        }
    }
}
