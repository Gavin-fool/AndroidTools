package com.alier.com.commons.utils;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.alier.com.commons.annotation.Mark;
import com.alier.com.commons.annotation.MarkTable;
import com.alier.com.commons.annotation.Primary;
import com.alier.com.commons.entity.FieldAtt;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author guannanYan
 * 
 * @param <T>
 *            example EventVal类要提前使用MarkTable Mark Primary 等注解数据库信息
 *            SqliteHelp<EventVal> sqliteHelp = new
 *            SqliteHelp<EventVal>(EventVal.class); //查询所有记录 ArrayList<EventVal>
 *            arr = sqliteHelp.retrieve();
 * 
 *            //根据id查询 EventVal eventVal=sqliteHelp.findById("2"); //添加一条新数据
 *            sqliteHelp.create(eventVal); //修改
 *            sqliteHelp.update(eventVal);eventVal id 不能为空 //删除一条记录
 *            sqliteHelp.delete(id);
 * 
 *            关闭数据 sqliteHelp.close();
 */
public class SqliteHelp<T> {

	private static Object lock = new Object();
	/*
	 * public SqliteHelp(T t) { this.t = t; }
	 */
	/**
	 * @param classType
	 *            被 Mark、MarkTable、Primary注解的类
	 * @throws Exception 
	 */
	public SqliteHelp(Class<T> classType,SQLiteDatabase d) throws Exception{
		this.classType = classType;
		this.db = d;
		if (null == db) {
			throw new Exception("from db NullPointerException");
		}
		MarkTable markTable = this.classType.getAnnotation(MarkTable.class);
		if (null == markTable || "".equals(markTable.value())) {
			throw new Exception("表名不能为空");
		} else {
			tableName = markTable.value();
		}
		Primary parmary = this.classType.getAnnotation(Primary.class);
		if (null == parmary || "".equals(parmary.value())) {
			throw new Exception("没有找到主键");
		} else {
			parmaryKeyName = parmary.value();
		}
	}

	private Class<T> classType = null;
	private T t = null;
	// 暂时支持int String boolean
	private Class[] baseClass = new Class[] { int.class, Integer.class,
			boolean.class, Boolean.class, String.class };
	private String tableName = null;
	private String parmaryKeyName = null;
	private int currentClassIndex = 0;
	private SQLiteDatabase db = null;
	private Cursor mCursor = null;

	/**
	 * 执行增、删、改
	 * 
	 * @param sqlStr
	 * @param condition
	 * @throws FileNotFoundException
	 */
	protected void execSqlStr(String sqlStr, String[] condition)
			throws FileNotFoundException {
		db.execSQL(sqlStr, condition);
	}

	/**
	 * 查询T所有记录
	 * 
	 * @return返回数据表所有记录
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public ArrayList<T> retrieve() throws IllegalAccessException,
			InstantiationException, ClassNotFoundException {
		return execQuery(String.format(" select * from %s", tableName), null);
	}

	/**
	 * 按id查询记录
	 * 
	 * @param id
	 *            数据库主机、自增、唯一
	 * @return 返回与传入id对应记录实体对象
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public T findById(String id) throws IllegalAccessException,
			InstantiationException, ClassNotFoundException {
		ArrayList<? extends T> resultList = execQuery(String.format(
				" select * from %s where %s=? ", tableName, parmaryKeyName),
				new String[] { id });
		if (null == resultList) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

	/**
	 * 按where查询记录
	 * 
	 * @param where
	 *            操作条件 数据库主机、自增、唯一
	 * @return返回与传入id对应记录实体对象
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public ArrayList<T> findByWhere(String where)
			throws IllegalAccessException, InstantiationException,
			ClassNotFoundException {
		ArrayList<T> resultList = execQuery(" select * from " + tableName + " "
				+ where, null);
		if (0 == resultList.size()) {
			return null;
		} else {
			return resultList;
		}
	}
	/**
	 * 按where查询记录
	 * 
	 * @param lines 查询的列
	 * @param where
	 *            操作条件 数据库主机、自增、唯一
	 * @return返回与传入id对应记录实体对象
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public ArrayList<T> findByWhere(ArrayList<String> lines,String where)
			throws IllegalAccessException, InstantiationException,
			ClassNotFoundException {
		StringBuilder sbf=new StringBuilder();
		String line ="";
		if (lines==null || lines.size()<0) {
			line=" * ";
		}else{
			for (String string : lines) {
				sbf.append(" "+string).append(" ,");
			}	
		}
		if (sbf.length()>0) {
			line=sbf.substring(0, sbf.length()-1);
		}
		
		ArrayList<T> resultList = execQuery(" select "+line+" from " + tableName + " "
				+ where, null);
		if (0 == resultList.size()) {
			return null;
		} else {
			return resultList;
		}
	}
	/**
	 * 查询当前条件下检索到的条目的数量
	 * @param sqlStr SQL查询语句
	 * @param condition 查询条件
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @return 条目数量 ，没有返回0
	 */
    public long execQueryResultCount(String sqlStr , String [] condition)
			throws IllegalAccessException, InstantiationException,
			ClassNotFoundException{
    	long resultCount=0;
		// 执行查询
		Cursor cursor = db.rawQuery(" select * from " + tableName + " "
				+ sqlStr, condition);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			resultCount++;
		}
		return resultCount;
    	
    }
	/**
	 * 执行sql查询
	 * 
	 * @paramsqlStr SQL查询语句
	 * @paramcondition 查询条件
	 * @return返回对应实体集合
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public ArrayList<T> execQuery(String sqlStr, String[] condition)
			throws IllegalAccessException, InstantiationException,
			ClassNotFoundException {
		// 获取传入类型
		// Class<?> objClass = t.getClass(); objClass.toString().substring(6)
		// 根据类型实例化对象
		// T newInstance = (T) Class.forName(classType.getName()).newInstance();
		String columnVal, setMethodName;
		Method setMethod = null;
		// 执行查询
		Cursor cursor = db.rawQuery(sqlStr, condition);
		ArrayList<T> arraylist = new ArrayList<T>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			T obj = (T) Class.forName(classType.getName()).newInstance();
			for (Method method : classType.getDeclaredMethods()) {
				Mark mark = method.getAnnotation(Mark.class);
				if (mark != null) {
					int index=cursor.getColumnIndex(mark
							.value());
					if (index<0) {
						continue;
					}
					columnVal = cursor.getString(index);
					setMethodName = String.format("s%s", method.getName()
							.substring(1));
					// 获取set方法
					setMethod = getMethod(classType, setMethodName);
					// set方法赋值
					methodSetVal(obj, setMethod, columnVal);
				}
			}
			arraylist.add(obj);
		}
		cursor.close();
		return arraylist;
	}

	private Method getMethod(Class<?> objClass, String methodName) {
		Method method = null;
		for (int i = 0; i < baseClass.length; i++) {
			try {
				method = objClass.getMethod(methodName, baseClass[i]);
				currentClassIndex = i;
				break;
			} catch (SecurityException e) {
				continue;
			} catch (NoSuchMethodException e) {
				continue;
			}
		}
		return method;
	}

	/**
	 * 方法赋值
	 * 
	 * @param obj
	 * @param method
	 * @param val
	 * 
	 * @return
	 */
	private boolean methodSetVal(Object obj, Method method, String val) {
		if (null == method) {
			return false;
		} else {
			try {
				switch (currentClassIndex) {
				case 0:
				case 1:
					method.invoke(obj, Integer.parseInt(val));
					break;
				case 2:
				case 3:
					method.invoke(obj, Boolean.getBoolean(val));
					break;
				case 4:
					method.invoke(obj, val);
					break;
				}
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			} catch (InvocationTargetException e) {
				return false;
			} catch (IllegalAccessException e) {
				return false;
			}
		}

	}

	/**
	 * 删除对象记录
	 * 
	 * @param id
	 *            主键编号
	 * @throws FileNotFoundException
	 */
	public void delete(String id) throws FileNotFoundException {
		synchronized (lock) {
			db.execSQL(String.format("delete from %s where %s= %s", tableName,
					parmaryKeyName, id));
		}
	}

	/**
	 * 删除对象记录
	 * 
	 * @param condition
	 *            过滤条件 如： id>5 and id<2 ...
	 * @throws FileNotFoundException
	 */
	public void deleteByWhere(String condition) throws FileNotFoundException {
		synchronized (lock) {
			db.execSQL(String.format("delete from %s %s", tableName,
					condition));
		}
	}

	public boolean updateByWhere(T obj,String condition) throws
		IllegalArgumentException, IllegalAccessException,
		InvocationTargetException, FileNotFoundException { 
		synchronized (lock) {
			try{ 
				StringBuilder sb = new StringBuilder("update "); 
				sb.append(tableName); 
				sb.append(" set ");
				Primary primary = classType.getAnnotation(Primary.class); 
				String primaryVal = ""; 
				for (Method method : classType.getDeclaredMethods()) {
					Mark mark = method.getAnnotation(Mark.class);
					if (null != mark && !mark.value().equalsIgnoreCase((primary.value()))) {
						try { 
							sb.append(mark.value()); 
							sb.append(" = ");
							if (null == method.invoke(obj, null)) {
								continue;
							}
							Object methodVal = method.invoke(obj, null).toString() .trim(); 
							if ("int".equals(method.getReturnType().getName()) 
									|| "java.lang.Integer".equals(method.getReturnType().getName())){
								sb.append(methodVal); 
							}else{ 
								sb.append(" '"); 
								sb.append(methodVal); 
								sb.append("' "); 
							}
							sb.append(","); 
						} catch (IllegalArgumentException e) {
							continue; 
						} catch (IllegalAccessException e) { 
							continue; 
						} catch (InvocationTargetException e) { 
							continue; 
						} 
					} else if (
						null != mark && mark.value().equalsIgnoreCase((primary.value()))) { 
						primaryVal = method.invoke(obj, null).toString().trim(); 
					} 
				}
				sb.replace(sb.lastIndexOf(","), sb.length(), " where ");
				sb.append(condition);
				synchronized(this){
					db.execSQL(sb.toString()); 
				}
				return true; 
			}catch (Exception ex){ 
				return false; 
			} 
		}
	}


	/**
	 * 修改对象
	 * 
	 * @param obj
	 *            被修改对象 （主键不能为空）
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public void update(T obj) throws IllegalArgumentException,
			IllegalAccessException,
			InvocationTargetException, FileNotFoundException {
		synchronized (lock) {
			StringBuilder sb = new StringBuilder("update ");
			sb.append(tableName);
			sb.append(" set ");
			Primary primary = classType.getAnnotation(Primary.class);
			String primaryVal = "";
			for (Method method : classType.getDeclaredMethods()) {
				Mark mark = method.getAnnotation(Mark.class);
				if (null != mark
						&& !mark.value().equalsIgnoreCase((primary.value()))) {
					try {
						sb.append(mark.value());
						sb.append(" = ");
						if (null == method.invoke(obj, null)) {
							continue;
						}
						Object methodVal = method.invoke(obj, null).toString()
								.trim();
						if ("int".equals(method.getReturnType().getName())
								|| "java.lang.Integer"
								.equals(method.getReturnType().getName())) {
							sb.append(methodVal);
						} else {
							sb.append(" '");
							sb.append(methodVal);
							sb.append("' ");
						}
						sb.append(",");
					} catch (IllegalArgumentException e) {
						continue;
					} catch (IllegalAccessException e) {
						continue;
					} catch (InvocationTargetException e) {
						continue;
					}
				} else if (null != mark
						&& mark.value().equalsIgnoreCase((primary.value()))) {
					primaryVal = method.invoke(obj, null).toString().trim();
				}
			}
			sb.replace(sb.lastIndexOf(","), sb.length(), " where ");
			sb.append(primary.value());
			sb.append(" = ");
			sb.append(primaryVal);
			db.execSQL(sb.toString());	
		}
	}
	
	/**
	 * 查询最新插入记录的ID
	 * @return
	 */
	public int lastId(){
		int newid = -1;
		Cursor cursor = db.rawQuery(String.format("select MAX(PKID) from %s", tableName),null);
		if(cursor.moveToFirst())
			newid = cursor.getInt(0);
		cursor.close();
		return newid;
	}
	/**
	 * 查询最新插入记录的ID
	 * @return
	 */
	public int lastId(String id){
		int newid = -1;
		Cursor cursor = db.rawQuery(String.format("select MAX("+id+") from %s", tableName),null);
		if(cursor.moveToFirst())
			newid = cursor.getInt(0);
		cursor.close();
		return newid;
	}

	/**
	 * 添加新记录
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public void create(T obj) throws Exception {
		synchronized (lock) {
			StringBuilder sb = new StringBuilder("insert into ");
			sb.append(tableName + " ");
			sb.append("()#%select%# #%;%# ");
			Primary primary = classType.getAnnotation(Primary.class);
			sb.insert(sb.indexOf(")#%select%#"), primary.value() + ",");
			sb.insert(sb.indexOf("#%;%#"), "null, ");
			for (Method method : classType.getDeclaredMethods()) {
				Mark mark = method.getAnnotation(Mark.class);
				if (null != mark
						&& !mark.value().equalsIgnoreCase((primary.value()))) {
					try {
						if (null == method.invoke(obj, null)) {
							continue;
						}
						Object methodVal = method.invoke(obj, null).toString()
								.trim();
						if ("int".equals(method.getReturnType().getName())
								|| "java.lang.Integer"
								.equals(method.getReturnType().getName())) {
							sb.insert(sb.indexOf("#%;%#"), methodVal + ", ");
						} else {
							sb.insert(sb.indexOf("#%;%#"), "'" + methodVal + "', ");
						}
						sb.insert(sb.indexOf(")#%select%#"), mark.value() + ",");
					} catch (IllegalArgumentException e) {
						continue;
					} catch (IllegalAccessException e) {
						continue;
					} catch (InvocationTargetException e) {
						continue;
					}
				}
			}
			sb.deleteCharAt(sb.indexOf(")") - 1);
			sb.deleteCharAt(sb.indexOf("#%;%#") - 2);
			String sqlStr = sb.toString();
			sqlStr = sqlStr.replace("#%;%#", ";");
			sqlStr = sqlStr.replace("#%select%#", "select");
			db.execSQL(sqlStr);
		}
	}

	/**
	 * 添加新记录
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public int createAndReturnId(T obj) throws Exception {
		synchronized (lock) {
			this.create(obj);
			mCursor = db.rawQuery("select last_insert_rowid() newid;", null);
			mCursor.moveToFirst();
			int newId = mCursor.getInt(0);
			mCursor.close();
			return newId;
		}
	}

	/**
	 * 创建class<T> 所对应的表
	 * @param map 
	 * 		key： 字段名
	 * 		value： 字段的实体类
	 * @return
	 */
	public boolean createTable(Map<String,FieldAtt> map){
		if(map==null){
			return false;
		}
		synchronized (lock) {
			//判断该表是否存在
			if(DBUtil.tableExistance(tableName, db)){//存在
				return false;
			};
			//通过类注解 判断 类中的字段名称 与map中的key 是否对应
			Method[] methods = classType.getDeclaredMethods(); 
			if(methods.length<=0){
				return false;
			}
			for (Method method : methods) {
				Mark mark = method.getAnnotation(Mark.class);//通过方法注解取到所有的 字段名称
				if (null != mark) {
					String markField = mark.value(); 
					if(map.get(markField)==null){
						return false;
					}
				}
			}
			//构建sql
			Set<String> keys= map.keySet();
			StringBuffer sb = new StringBuffer();
			sb.append("create table ");
			sb.append(tableName);
			sb.append("( ");
			for(String key:keys){
				FieldAtt mFieldAtt = map.get(key);
				String name = mFieldAtt.getName();//字段名称
				String type = mFieldAtt.getType();//字段类型 integer
				String defaultValue = mFieldAtt.getDefaultValue();// 2
				String defaultNull = mFieldAtt.getDefaultNull();// NOT NULL
				String defaultKey = mFieldAtt.getDefaultKey();//PRIMARY KEY AUTOINCREMENT
						
				sb.append(name);
				sb.append(" ");
				sb.append(type);
				sb.append(defaultValue);
				sb.append(defaultNull);
				sb.append(defaultKey);
				sb.append(" , ");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(")");
			String sql = sb.toString();
			try{
				db.execSQL(sql);
			}catch(SQLException e){
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
	
}
