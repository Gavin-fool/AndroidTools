package com.alier.com.androidtools.commons.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Android常用10大快速开发工具类之--HTTP辅助工具类HttpUtils
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年8月2日 上午11:07:22
 * @version 1.0
 */
public class HttpUtils {
	private static final int TIMEOUT_IN_MILLIONS = 5000;

	public interface CallBack {
		void onRequestComplete(String result);
	}

	/**
	 * Get请求，获取返回数据
	 * 
	 * @param urlStr
	 *            请求URL
	 * @return
	 */
	public static String doGet(String urlStr) {
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream is = null;
		ByteArrayOutputStream outputStream = null;
		try {
			url = new URL(urlStr);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			httpURLConnection.setReadTimeout(TIMEOUT_IN_MILLIONS);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			if (httpURLConnection.getResponseCode() == 200) {
				is = httpURLConnection.getInputStream();
				outputStream = new ByteArrayOutputStream();
				int len = -1;
				byte[] bytes = new byte[128];
				while ((len = is.read(bytes)) != -1) {
					outputStream.write(bytes);
				}
				return outputStream.toString();
			} else {
				throw new RuntimeException("请求结果不是200");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (null != outputStream) {
					outputStream.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			httpURLConnection.disconnect();
		}
		return null;
	}

	/**
	 * 向指定URL通过HttpURLConnection发送post请求
	 * 
	 * @param urlStr
	 *            发送请求的URL
	 * @param paras
	 *            请求参数，参数的格式应该是key1=value1&key2=value2的形式
	 * @return 响应结果,请求成功返回请求响应结果，请求失败或请求异常返回null
	 */
	public static String doPost(String urlStr, String paras) {
		BufferedReader in = null;
		PrintWriter out = null;
		String str_result = "";
		URL url = null;
		HttpURLConnection urlConnection = null;
		try {
			url = new URL(urlStr);
			urlConnection = (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		urlConnection.setRequestProperty("accept", "*/*");
		urlConnection.setRequestProperty("connection", "Keep-Alive");
		try {
			urlConnection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			e.printStackTrace();
			return null;
		}
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlConnection.setRequestProperty("charset", "utf-8");
		urlConnection.setUseCaches(false);
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setReadTimeout(TIMEOUT_IN_MILLIONS);
		urlConnection.setConnectTimeout(TIMEOUT_IN_MILLIONS);
		if (null != paras && !"".equals(paras.trim())) {
			// 获取URLConnextion对应的输出流
			try {
				out = new PrintWriter(urlConnection.getOutputStream());
				out.print(paras);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		try {
			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while((line=in.readLine())!=null){
				str_result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//关闭流
		finally {
			if(null != out){
				out.close();
			}
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return str_result;
	}
	
	/**
	 * 异步发送get请求
	 * @param strUrl
	 * @param callBack
	 */
	public static void doGetAsyn(final String strUrl,final CallBack callBack){
		new Thread(){
			@Override
			public void run() {
				String result = doGet(strUrl);
				if(null!=callBack){
					callBack.onRequestComplete(result);	
				}
			}
		}.start();;
	}
	
	/**
	 * 异步发送post请求
	 * @param strUrl
	 * @param paras
	 * @param callBack
	 */
	public static void doPostAsyn(final String strUrl,final String paras,final CallBack callBack){
		new Thread(){
			@Override
			public void run() {
				String result = doPost(strUrl, paras);
				if(null != callBack){
					callBack.onRequestComplete(result);
				}
			};
		}.start();
	}
}
