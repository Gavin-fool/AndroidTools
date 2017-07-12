package com.alier.com.androidtools.ui.threadTest;

import android.view.Window;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.commons.BaseActivity;

public class ThreadTestActivity extends BaseActivity {

	@Override
	public void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.thread_test_activity);
	}

	@Override
	public void exec() {
		
	}

}
