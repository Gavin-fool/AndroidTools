package com.alier.com.androidtools.ui.map;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.alier.com.androidtools.R;
import com.alier.com.commons.BaseActivity;

public class MapBrowerActivity extends BaseActivity implements OnClickListener {
	/**
	 * 地图显示控件
	 */
	public View m_mapView;
	private RadioButton rb_entire;
	private RadioButton rb_back;
	public RelativeLayout rlbody;
	private RelativeLayout layout;
	private View progressLayout = null;

	private IM_BaseMap baseMap = null;
	@Override
	public void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.base_map);
		initView();
	}

	private void initView() {
		rb_entire = (RadioButton) findViewById(R.id.rdo_entire);
		rb_entire.setOnClickListener(this);
		rb_back = (RadioButton) findViewById(R.id.rdo_exit_map);
		rb_back.setOnClickListener(this);
		rlbody = (RelativeLayout) findViewById(R.id.body);
		layout = (RelativeLayout) this.findViewById(R.id.setmapview);
		progressLayout = this.findViewById(R.id.map_prg);
		progressLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void exec() {
		baseMap = new IM_BaseMap(this);
		m_mapView = baseMap.getMapView();
		layout.addView(m_mapView);
		baseMap.initMap();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
