package com.alier.com.androidtools.ui.uitest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.libresource.slidingmenu.SlidingMenu;
import com.alier.com.androidtools.libresource.slidingmenu.app.SlidingFragmentActivity;
import com.alier.com.androidtools.ui.uitest.fragment.CustomViewFragment;
import com.alier.com.androidtools.ui.uitest.fragment.LeftFragmentMenu;

/**
 * 主布局界面，模仿Android开源项目集合，点击某一项进入具体布局效果
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年10月25日 上午10:57:16
 * @version 1.0
 */
public class MainLayout extends SlidingFragmentActivity implements OnClickListener {

	private ImageView topButton;// 顶部按钮
	private Fragment mFragment;
	private TextView topTextView;// 顶部标题
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uitest_main_layout);
        initView();
		initSlidingMenu(savedInstanceState);
	}

	/**
	 * 初始化侧边栏
	 * @param savedInstanceState
     */
	private void initSlidingMenu(Bundle savedInstanceState) {
        // 如果保存的状态不为空则得到之前保存的Fragment，否则实例化mFragment
		if (savedInstanceState != null) {
			mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "mFragment");
		}
		if (null == mFragment) {
			mFragment = new CustomViewFragment();
		}
		// 设置左侧菜单栏
		setBehindContentView(R.layout.uitest_menu_frame_left);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new LeftFragmentMenu()).commit();
		// 实例化滑动菜单对象
		SlidingMenu sm = getSlidingMenu();
		// 设置可以左右滑动的菜单
		sm.setMode(SlidingMenu.LEFT);
		// 设置滑动阴影的宽度
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动菜单阴影的图像资源
		sm.setShadowDrawable(null);
		// 设置滑动菜单视图的宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		sm.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式,这里设置为全屏
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 设置下方视图的在滚动时的缩放比例
		sm.setBehindScrollScale(0.0f);
        switchFragment(mFragment,"自定义View汇总");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mFragment", mFragment);
	}

	/**
	 * @Description:初始化UI
	 */
	private void initView() {
		topButton = (ImageView) this.findViewById(R.id.topButton);
		topButton.setOnClickListener(this);
		topTextView = (TextView) this.findViewById(R.id.topTv);
	}

	/**
	 * @Description:切换fragment
	 * @param fragment
	 *            ：切换到的fragment
	 * @param title
	 *            ：切换后fragment的标题
	 * @return:void
	 */
	public void switchFragment(Fragment fragment, String title) {
		mFragment = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
		topTextView.setText(title);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.topButton) {
			toggle();
		}
	}
}
