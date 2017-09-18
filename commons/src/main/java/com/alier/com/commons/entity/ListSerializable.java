package com.alier.com.commons.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ListSerializable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> list;
	private List<HashMap<String,String>> hashMapList;
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public List<HashMap<String, String>> getHashMapList() {
		return hashMapList;
	}
	public void setHashMapList(List<HashMap<String, String>> hashMapList) {
		this.hashMapList = hashMapList;
	}

}
