package com.alier.com.commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alier.com.commons.utils.DBHelper;
import com.alier.com.commons.entity.FieldAtt;
import com.alier.com.commons.utils.ObjectUtils;

/**
 * 
 * @Title:DBUtil
 * @description:数据库工具类
 * @author:fengao
 * @date:2016年10月22日下午4:28:18
 * @version:v1.0
 */
public class DBUtil {
	
	/**
	 * 
	 * @Description:往已有的表中添加字段 可以添加多个字段
	 * @param:@param tableName
	 *                   表名
	 * @param:@param map
	 *                   key 字段名称 value 字段属性实体类 
	 *                    					
	 * @return:boolean  true 添加字段成功 添加多个字段（多个字段中必须包含表中没有的字段） 
	 * 					false 添加字段失败
	 * 			
	 * @throws:
	 */
	public static boolean alterTable(String tableName, Map<String, FieldAtt> map) {
		if (map == null) {
			return false;
		}
		DBHelper dbHelper = null;
		Cursor cursor = null;
		// TODO: try 的位置
		try {
			dbHelper = new DBHelper(Thread.currentThread().getName());
			SQLiteDatabase db = dbHelper.getDb();

			// 判断该表是否存在
			if (!tableExistance(tableName, db)) {// 不存在
				return false;
			}
			// 判断map中数据是否为空
			Set<String> fields = map.keySet();
			if (fields.isEmpty()) {
				return false;
			}
			// 判断map中的字段 原表是否存在此字段
			Map<String, FieldAtt> mapFilter = new HashMap<String, FieldAtt>();
			for (String field : fields) {
				if (isFieldInTable(db, field, tableName)) {// 存在
					continue;
				} else {
					mapFilter.put(field, map.get(field));
				}
			}
			Set<String> fieldsFilter = mapFilter.keySet();
			
			// 判断过滤后的字段集合是否为空
			if (fieldsFilter.isEmpty()) {
				return false;
			}
			dbHelper.beginTransaction();
			for (String field : fieldsFilter) {
				FieldAtt mFieldAtt = mapFilter.get(field);
				String type = mFieldAtt.getType();
				String defaultValue = mFieldAtt.getDefaultValue();
				String defaultNull = mFieldAtt.getDefaultNull();

				StringBuffer sb = new StringBuffer();
				sb.append("alter table ");
				sb.append(tableName);
				sb.append(" add column ");
				sb.append(field);
				sb.append(" ");
				sb.append(type);
				sb.append(defaultNull);
				sb.append(defaultValue);
				
				String sql = sb.toString();
				db.execSQL(sql);
			}
			dbHelper.commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			dbHelper.rollbackTransaction();
			return false;
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
			dbHelper.CloseDB(Thread.currentThread().getName());
		}
		return true;
	}
	
	/**
	 * 
	 * @Description:判断数据库中是否有某个表
	 * @param:@param tableName 表名
	 * @param:@param db 数据库
	 * @return:boolean 
	 * 		true 数据库中存在此表
	 * 		false 数据库不存在此表
	 * @throws:
	 */
	public static boolean tableExistance(String tableName, SQLiteDatabase db) {
		if(ObjectUtils.isEmpty(tableName)){
			return false;
		}
		if (null == db) {
			return false;
		}
		boolean res = false;
		String sqlStr = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + tableName.trim() + "'";
		Cursor cursor = db.rawQuery(sqlStr, null);
		if (cursor.moveToNext()) {
			int result = cursor.getInt(0);
			if (result > 0) {
				res = true;
			}
		}
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		return res;
	}
	
	/**
	 * 
	 * @Description:判断表中是否含有某个字段
	 * @param:@param db  数据库
	 * @param:@param field 字段名
	 * @param:@param tabName 表名
	 * @return:boolean 
	 * 			true 表中包含此字段
	 * 			false 表中不包含此字段
	 * 				
	 * @throws:
	 */
	public static boolean isFieldInTable(SQLiteDatabase db, String field, String tabName) {
		if(ObjectUtils.isEmpty(tabName)){
			return false;
		}
		if(ObjectUtils.isEmpty(field)){
			return false;
		}
		if (null == db) {
			return false;
		}
		boolean result = false;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM " + tabName + " LIMIT 0", null);
			result = (cursor != null && cursor.getColumnIndex(field) != -1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}
}
