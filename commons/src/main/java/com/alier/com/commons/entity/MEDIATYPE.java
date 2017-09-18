package com.alier.com.commons.entity;

/**
 * 多媒体的类型的枚举
 * 
 * @author yxx
 *
 */
public enum MEDIATYPE {
	/**
	 * 图片
	 */
	IMAGE(0),
	/**
	 * 声音
	 */
	SOUND(1),
	/**
	 * 视频
	 */
	VEDIO(2),
	/**
	 * 文档
	 */
	FILE(3);

	private final int val;

	private MEDIATYPE(int value) {
		val = value;
	}

	public int getValue() {
		return val;
	}
}
