package com.alier.com.commons.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * 手机网络相关
 * 
 * @author mw
 * 
 */
public class MobileInternetUtil {

	private static Context mContext;
	public static final Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	public static final Uri APN_LIST_URI = Uri.parse("content://telephony/carriers");

	/**
	 * 更改手机APN
	 */
	public static String changeAPN(Context context) {
		mContext = context;
		return getAPNFromPhone();
	}

	/**
	 * 获取手机上的接入点
	 */
	private static String getAPNFromPhone() {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return info.getExtraInfo();
	}

	/**
	 * 切换APN
	 * 
	 * @param resolver
	 * @param newAPN 新的APN
	 * @return
	 */
	public static int updateCurrentAPN(ContentResolver resolver, String newAPN) {
		Cursor cursor = null;
		try {
			cursor = resolver.query(APN_LIST_URI, null,
					" apn = ? and current = 1",
					new String[] { newAPN.toLowerCase() }, null);
			String apnId = null;
			if (cursor != null && cursor.moveToFirst()) {
				apnId = cursor.getString(cursor.getColumnIndex("_id"));
			}
			cursor.close();
			if (apnId != null) {
				ContentValues values = new ContentValues();
				values.put("apn_id", apnId);
				resolver.update(CURRENT_APN_URI, values, null, null);
			} else {
				return 0;
			}
		} catch (SQLException e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 1;
	}

	/**
	 * 得到当前的手机网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentNetType(Context context) {
		String type = "";
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			type = "null";
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			type = "wifi";
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA
					|| subType == TelephonyManager.NETWORK_TYPE_GPRS
					|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				type = "2g";
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
					|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				type = "3g";
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
				type = "4g";
			}
		}
		return type;
	}

}
