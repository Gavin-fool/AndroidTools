package com.alier.com.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences的使用， 用于存储程序配置参数。
 *
 * @author gavin_fool
 */
public class Cookie {
    /**
     * 存贮cookie集合
     */
    private static HashMap<String, Cookie> mHashMapCookie = new HashMap<String, Cookie>();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    /**
     * 存储应用程序配置参数
     */
    public static final String APP_CFG = "appConfig";
    /**
     * 存储用户信息
     */
    public static final String USER_DATA = "initUserData";
    private static Cookie mCookie;

    private Cookie(Context mContext, String name) {
        sp = mContext.getSharedPreferences(name, 0);
        editor = sp.edit();
    }

    /**
     * @param mContext 上下文对象
     * @param name     生成SharedPreference的name,本实例中使用APP_CFG,USER_DATA
     * @return 一个SharedPreference作用的 Cookie
     */
    public static Cookie getInstance(Context mContext, String name) {
        if (mHashMapCookie.get(name) == null) {
            mCookie = new Cookie(mContext, name);
            mHashMapCookie.put(name, mCookie);
        } else {
            mCookie = mHashMapCookie.get(name);
        }
        return mCookie;
    }

    /**
     * 向cookie中存储数据
     * @param key
     * @param value
     */
    public void putVal(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putVal(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putVal(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putVal(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putObject(String key, Object value) {
        ByteArrayOutputStream toByte = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(toByte);
            oos.writeObject(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对byte[]进行Base64编码
        String payCityMapBase64 = new String(Base64Coder.encode(toByte.toByteArray()));
        // 存储
        editor.putString(key, payCityMapBase64);
        editor.commit();
    }

    public Object getObject(String key) {
        String object = sp.getString(key, null);
        if (object == null) {
            return object;
        }
        byte[] base64Bytes = Base64Coder.decode(object);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * @param key
     * @return 没有值时返回 null
     */
    public String getVal(String key) {
        return sp.getString(key, null);
    }

    public String getVal(String key, String defaultVal) {
        return sp.getString(key, defaultVal);
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    /**
     *
     * @param key
     * @return 如果没有对应的key，返回false
     */
    public Boolean getBool(String key) {
        return sp.getBoolean(key, false);
    }

    public Boolean getBool(String key, boolean defaultVal) {
        return sp.getBoolean(key, defaultVal);
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public long getLong(String key, long defaultVal) {
        return sp.getLong(key, defaultVal);
    }

    /**
     * 移除对应key的值
     * @param key
     */
    public void removeVal(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清空cookie
     */
    public void clearAll() {
        editor.clear();
        editor.commit();
    }
}
