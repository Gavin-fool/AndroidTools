package com.alier.com.androidtools.ui.uitest;


import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.alier.com.androidtools.commons.BaseActivity;

public class DynFrom extends BaseActivity {
	Handler sonHandler;
	@Override
	public void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				sonHandler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
					}
				};
			}
		});
		thread.start();
		//sonHandler为null
		sonHandler.obtainMessage(1);
		Message msg = new Message();
		sonHandler.sendMessage(msg);
	}

	@Override
	public void exec() {
		// TODO Auto-generated method stub
		
	}

}
