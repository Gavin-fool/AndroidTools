package com.alier.com.androidtools.commons.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Android常用10大快速开发工具类之--SharedPreferences封装类SPUtils
 * 对SharedPreference的使用做了建议的封装，对外公布出put，get，remove，clear等等方法；
 * 注意一点，里面所有的commit操作使用了SharedPreferencesCompat.apply进行了替代，目的是尽可能的使用apply代替commit
 * 首先说下为什么，因为commit方法是同步的，并且我们很多时候的commit操作都是UI线程中，毕竟是IO操作，尽可能异步；
 * 所以我们使用apply进行替代，apply异步的进行写入；
 * 但是apply相当于commit来说是new API呢，为了更好的兼容，我们做了适配；
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年7月22日 下午2:25:53
 * @version 1.0
 */
public class SPUtils {

	/**
	 * 存储App配置文件信息
	 */
	public static final String APP_CONFIG = "appConfig";
	/**
	 * 存储软件在使用过程中需要用到的缓存，用户信息等等
	 */
	public static final String COOKIE = "cookie";

	/**
	 * 保存数据的方法，需要指定数据保存到的文件名spName,需要判断保存数据的具体类型，根据不同的类型用不同的方法去存储数据
	 * 
	 * @param spName
	 *            :指定需要保存到文件名
	 * @param context
	 *            ：上下文对象
	 * @param key
	 *            : 保存数据的key
	 * @param object
	 *            ： 需要保存的数据对象
	 */
	public static void put(String spName, Context context, String key, Object object) {
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * 
	 * @param spName
	 *            :指定需要取数据文件名
	 * @param context
	 *            ：上下文
	 * @param key
	 *            ：想要取得数据的key
	 * @param defaultObject
	 *            ：如果没有想要的数据，设定默认值
	 * @return Object
	 */
	public static Object get(String spName, Context context, String key, Object defaultObject) {
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if (defaultObject instanceof String) {
			return sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Float) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) {
			return sp.getLong(key, (Long) defaultObject);
		} else {
			return null;
		}
	}

	/**
	 * 移除某个key值对应的值
	 * 
	 * @param spName
	 *            :指定需要移除数据文件名
	 * @param context
	 *            ：上下文
	 * @param key
	 *            ：需要移除值的key
	 */
	public static void remove(String spName, Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 清空指定sp中的所有数据
	 * 
	 * @param spName
	 *            ：sp名称
	 * @param context
	 *            ：上下文
	 */
	public static void clear(String spName, Context context) {
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 判断sp中是否包含某个key
	 * 
	 * @param spName
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean contains(String spName, Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	public static Map<String, ?> getAll(String spName, Context context) {
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		return sp.getAll();
	}

	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = getApplyMethod();

		/**
		 * 反射查找apply方法
		 * 
		 * @return
		 */
		private static Method getApplyMethod() {
			Class clz = SharedPreferences.Editor.class;
			try {
				return clz.getMethod("apply", null);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}

		public static void apply(SharedPreferences.Editor editor) {

			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			editor.commit();
		}
	}
}
