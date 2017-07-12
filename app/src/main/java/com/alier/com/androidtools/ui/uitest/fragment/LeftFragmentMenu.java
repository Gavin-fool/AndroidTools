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

/**
 * 侧边栏菜单
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2017年2月1日 下午11:54:28
 * @version 1.0
 */
public class LeftFragmentMenu extends Fragment {

	private ListView lvMenu;// 侧边栏菜单列表

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
		String[] slidingMenu = getResources().getStringArray(R.array.libraries);
		lvMenu = (ListView) view.findViewById(R.id.slidingmuneLv);
		lvMenu.setAdapter(new SlidingMenuAdapter());
		lvMenu.setOnItemClickListener(lvMenuItemOnclick);
	}

	/**
	 * 单机菜单栏的一项的操作
	 */
	OnItemClickListener lvMenuItemOnclick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
		}
	};
}
