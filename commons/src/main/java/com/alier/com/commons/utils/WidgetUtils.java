package com.alier.com.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.ViewGroup;
import android.widget.TextView;

public class WidgetUtils {

	   public static void settingClickable(ViewGroup vg,boolean clickable){
	    	
	    	TextView text=null;
	    	ViewGroup childVg=null;
	    	  for(int i=0;i<vg.getChildCount();i++){
	          	if(vg.getChildAt(i) instanceof TextView){
	          		text=(TextView) vg.getChildAt(i);
	          		text.setClickable(clickable);
	          		continue;
	          	}
	          	
	          	if(vg.getChildAt(i) instanceof ViewGroup){
	          		childVg=(ViewGroup) vg.getChildAt(i);
	          		settingClickable(childVg,clickable);
	          	}

	          }
	    }
	   /**
		 * 获取当前手机号码
		 * @return当期手机号码
		 */
		public static String getPhoneNumber(Activity activity){  
		    TelephonyManager mTelephonyMgr;  
		    mTelephonyMgr = (TelephonyManager)  activity.getSystemService(Context.TELEPHONY_SERVICE);   
		    if(null==mTelephonyMgr.getLine1Number()||"".equals(mTelephonyMgr.getLine1Number().trim())){
		    	return "18900000000";
		    }else{
		    	return mTelephonyMgr.getLine1Number().substring(3);  
		    }
		    
		}
		
		/**
		 * 生成随机字符串
		 * @int 生成随机字符串的长度
		 * @return
		 */
		public static String generateRandom(int length){
			StringBuffer buf = new StringBuffer("a,b,c,d,e,f,g,h,i,g,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z");//26
			buf.append(",A,B,C,D,E,F,G,H,I,G,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z");//26
			//buf.append(",~,@,#,$,%,^,&,*,(,),_,+,|,`,.");//15
			buf.append(",1,2,3,4,5,6,7,8,9,0");//10

			String[] arr = buf.toString().split(",");
			
			StringBuffer b = new StringBuffer();
			java.util.Random r;
			int k ;
			for(int i=0;i<length;i++){
				 r = new java.util.Random();
				 k = r.nextInt();
				 b.append(String.valueOf(arr[Math.abs(k % 61)]));
			}
			return b.toString();
		}
		/**
		 * 生成随机字符串
		 * @int 生成随机字符串的长度
		 * @return
		 */
		public static String generateRandomNum(int length){
			StringBuffer buf = new StringBuffer("0,1,2,3,4,5,6,7,8,9");
			String[] arr = buf.toString().split(",");
			StringBuffer b = new StringBuffer();
			java.util.Random r;
			int k ;
			for(int i=0;i<length;i++){
				 r = new java.util.Random();
				 k = r.nextInt();
				 b.append(String.valueOf(arr[Math.abs(k % 9)]));
			}
			return b.toString();
		}
}
