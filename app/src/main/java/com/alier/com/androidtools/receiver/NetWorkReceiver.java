package com.alier.com.androidtools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alier.com.androidtools.commons.BaseApp;

/**
 * 
 * @author  作者 : gavin_fool
 * @date 创建时间：2016年8月22日 上午11:33:21 
 * @version 1.0
 */
public class NetWorkReceiver extends BroadcastReceiver {
	private ConnectivityManager connectManager = null;//����������������صĲ���
	private NetworkInfo networkInfo = null;//������Ϣ��
	private Context mContext = null;
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connectManager.getActiveNetworkInfo();
		String action = intent.getAction();
		if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			if(null != networkInfo&&networkInfo.isAvailable()){
				BaseApp.g_b_netOk = true;
			}else{
				BaseApp.g_b_netOk = false;
			}
		}
	}

}
