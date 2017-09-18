package com.alier.com.commons.entity;

/**
 * 选择媒体类型枚举
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2017年4月14日 上午10:34:19
 * @version 1.0
 */
public enum CHOOSEMEDIATYPE {
	/**
	 * 全部类型
	 */
	ALL(1),
	/**
	 * 图片
	 */
	PHOTO(2),
	/**
	 * 视频
	 */
	VIDEO(3),
	/**
	 * 音频
	 */
	AUDIO(4),
	/**
	 * 不选择任何类型
	 */
	NONE(5);
	private final int val;

	private CHOOSEMEDIATYPE(int value) {
		val = value;
	}

	public int getValue() {
		return val;
	}
}
