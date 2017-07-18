package com.alier.com.commons.entity;


import com.alier.com.commons.utils.ObjectUtils;

/**
 * 
 * @Title:FieldAtt
 * @description:字段属性实体类
 * @author:fengao
 * @date:2016年10月22日下午12:39:12
 * @version:v1.0
 */
public class FieldAtt {
	
	private String name;//字段名称
	private String type;//字段数据类型
	private String defaultValue = null;//字段默认值 为null
	private boolean isKey;//是否主键
	private boolean isNull;//是否允许为null
	
	private String defaultKey = null;//默认为 PRIMARY KEY AUTOINCREMENT
	private String defaultNull = null;//默认为 NOT NULL
	
	/**
	 * 
	 * @Description:构造函数
	 * @param name 字段名称
	 * @param type 字段类型
	 * @param defaultValue 字段默认值
	 * @param isKey 是否主键
	 * 		true： 是主键
	 * 		false：不是主键
	 * @param isNull 是否为空
	 * 		true：为空
	 * 		false：不为空
	 * 
	 * 使用alter 添加字段的时候 isNull = false 时 defaultValue 必须有值 不能为null
	 */
	public FieldAtt(String name, String type,String defaultValue,boolean isKey,boolean isNull) {
		super();
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.isKey = isKey;
		this.isNull = isNull;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}
	
	/**
	 * 
	 * @Description: 得到默认值所对应的 字符串
	 * @param:@return
	 * @return:String
	 * @throws:
	 */
	public String getDefaultValue() {
		if(ObjectUtils.isEmpty(defaultValue)){
			defaultValue = "";
		}else{
			defaultValue = " default "+defaultValue;
		}
		return defaultValue;
	}
	
	/**
	 * 
	 * @Description: 根据 iskey 得到默认的 主键描述
	 * @param:@return
	 * @return:String 
	 * @throws:
	 */
	public String getDefaultKey(){
		if(isKey){
			defaultKey = " PRIMARY KEY AUTOINCREMENT";
		}else{
			defaultKey = "";
		}
		return defaultKey;
	}
		
	/**
	 * 
	 * @Description: 根据 isNull 得到默认的 非空描述
	 * @param:@return
	 * @return:String
	 * @throws:
	 */
	public String getDefaultNull(){
		if(isNull){
			defaultNull = ""; 
		}else{
			defaultNull = " NOT NULL";
		}
		return defaultNull;
	}
}
