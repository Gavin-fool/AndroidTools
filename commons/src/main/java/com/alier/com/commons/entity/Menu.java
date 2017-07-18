package com.alier.com.commons.entity;

import java.io.Serializable;

/**
 * 菜单类  描述功能模块菜单信息  主菜单使用
 * @author GuannanYan
 *@version 2011-06-20
 */
public class Menu implements Serializable{
	/**模块ID*/
	private int Pkid;
	/**模块标题*/
	private String Caption;
	/**模块图标*/
	private String Icon;
	/**类型*/
	private String Level;
	/**模块序号*/
	private String Order;
	/**跳转目标页面*/
	private String Target;
	/**模块描述*/
	private String Descrption;
	
	public Menu() {
		
	}
	public int getPkid() {
		return Pkid;
	}
	public void setPkid(int pkid) {
		Pkid = pkid;
	}
	
	public String getLevel() {
		return Level;
	}
	public void setLevel(String level) {
		Level = level;
	}
	public String getCaption() {
		return Caption;
	}
	public void setCaption(String caption) {
		Caption = caption;
	}
	public String getIcon() {
		return Icon;
	}
	public void setIcon(String icon) {
		Icon = icon;
	}
	public String getOrder() {
		return Order;
	}
	public void setOrder(String order) {
		Order = order;
	}
	public String getTarget() {
		return Target;
	}
	public void setTarget(String target) {
		Target = target;
	}
	public String getDescrption() {
		return Descrption;
	}
	public void setDescrption(String descrption) {
		Descrption = descrption;
	}

}
