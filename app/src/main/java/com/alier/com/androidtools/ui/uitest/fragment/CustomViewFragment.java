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
import com.alier.com.androidtools.ui.uitest.customSmallUI.GifShow;
import com.alier.com.androidtools.ui.uitest.customSmallUI.MediaControllerShow;
import com.alier.com.androidtools.ui.uitest.customSmallUI.ProgressDialogActivity;
import com.alier.com.androidtools.ui.uitest.VHTable.VHTableActivity;
import com.alier.com.androidtools.ui.uitest.treelist.TreeView;

import java.util.ArrayList;

/**
 * 自定义view汇总fragment
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017年2月2日 下午8:49:19
 */
public class CustomViewFragment extends Fragment implements AdapterView.OnItemClickListener {

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
        objects.add(new ContentItem("2.TreeView","可无限级拓展的树形控件"));
        objects.add(new ContentItem("3.GIFView","GIF图片显示"));
        objects.add(new ContentItem("4.ProgressDialog","对话框式进度条"));
        objects.add(new ContentItem("5.mediaController","多媒体控件"));
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
            case 1:
                intent = new Intent();
                intent.setClass(getContext(), TreeView.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent();
                intent.setClass(getContext(), GifShow.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent();
                intent.setClass(getContext(), ProgressDialogActivity.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent();
                intent.setClass(getContext(), MediaControllerShow.class);
                startActivity(intent);
                break;
        }
    }
}
