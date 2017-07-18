package com.alier.com.androidtools.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioButton;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.adapter.GridViewMenuAdapter;
import com.alier.com.androidtools.receiver.NetWorkReceiver;
import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.BaseApp;
import com.alier.com.commons.Global;

public class MainActivity extends BaseActivity implements OnClickListener {

	private GridView gvMainMenu = null;
	private GridViewMenuAdapter gvMenuAdapter = null;
	private RadioButton rb_exit_app = null;
	private Toolbar toolbar = null;
	@Override
	public void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		NetWorkReceiver netWorkReceiver = new NetWorkReceiver();
		registerReceiver(netWorkReceiver, iFilter);
		BaseApp.getBaseInstance().addActivity(MainActivity.this);
		BaseApp.getBaseInstance().addReceiver(this, netWorkReceiver);
		initView();
	}

	// 初始化布局文件
	private void initView() {
		gvMainMenu = (GridView) findViewById(R.id.gvMainMenu);
		gvMainMenu.setPadding(5, 40, 5, 5);
		rb_exit_app = (RadioButton) findViewById(R.id.exit_app);
		rb_exit_app.setOnClickListener(this);
        toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        setTitle("主界面");
        setSupportActionBar(toolbar);
	}

	@Override
	public void exec() {
		gvMenuAdapter = new GridViewMenuAdapter(MainActivity.this,BaseApp.modulesItems);
		gvMainMenu.setAdapter(gvMenuAdapter);
		gvMainMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				String str_Target = BaseApp.modulesItems.get(position).getTarget();
				intent.setClassName(MainActivity.this, Global.SYS_ACTIVITIES
						.get(str_Target).get(2));
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exit_app:
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApp.getBaseInstance().exit();
	}

}
