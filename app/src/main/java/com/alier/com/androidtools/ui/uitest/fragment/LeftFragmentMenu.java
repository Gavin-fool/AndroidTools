package com.alier.com.androidtools.ui.uitest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.adapter.SlidingMenuAdapter;
import com.alier.com.androidtools.ui.uitest.MainLayout;
import com.alier.com.commons.utils.T;

/**
 * 侧边栏菜单
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017年2月1日 下午11:54:28
 */
public class LeftFragmentMenu extends Fragment {

    private ListView lvMenu;// 侧边栏菜单列表
    private String[] slidingMenu;//菜单栏填充内容

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.uitest_left_fragment_menu, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        slidingMenu = getResources().getStringArray(R.array.libraries);
        lvMenu = (ListView) view.findViewById(R.id.slidingmuneLv);
        lvMenu.setAdapter(new SlidingMenuAdapter(getContext(), slidingMenu));
        lvMenu.setOnItemClickListener(lvMenuItemOnclick);
    }

    /**
     * 单机菜单栏的一项的操作
     */
    OnItemClickListener lvMenuItemOnclick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] item = slidingMenu[position].split(",");
            String target = item[1];
            Fragment targetFragment = null;
            String title = item[0];
            if (target.equals("userdefined")) {
                targetFragment = new CustomViewFragment();
            } else if(target.equals("viewTest")){
                targetFragment = new ViewTestFragment();
            }else if (target.equals("listview")) {
                targetFragment = new CustomListViewFragment();
            } else if (target.equals("menu")) {
                targetFragment = new CustomMenuFragment();
            } else if (target.equals("chart")) {
                targetFragment = new CustomChartFragment();
            } else if (target.equals("animation")) {
                targetFragment = new CustomAnimationFragment();
            }
            if (null == targetFragment) {
                T.showShort(getContext(), "该部分代码你还没有完成哦，还不赶紧去完成");
                return;
            }
            switchFragment(targetFragment, title);
        }
    };

    private void switchFragment(Fragment fragment, String title) {
        if (null == getActivity()) {
            return;
        }
        if (getActivity() instanceof MainLayout) {
            MainLayout ml = (MainLayout) getActivity();
            ml.switchFragment(fragment, title);
        }
    }
}
