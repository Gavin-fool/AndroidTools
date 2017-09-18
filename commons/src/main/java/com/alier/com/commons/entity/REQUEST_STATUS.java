package com.alier.com.commons.entity;

/**
 * 请求返回结果枚举
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2017年7月11日 下午2:21:30
 * @version 1.0
 */
public enum REQUEST_STATUS {
	/**
	 * 查询成功
	 */
	SUCCESS(0),
	/**
	 * 查询失败
	 */
	FAILUE(1),
	/**
	 * 没有数据
	 */
	NO_DATA(2),
	/**
	 * 没有更多
	 */
	NO_MORE(3),
	/**
	 * 网络异常
	 */
	NETWORK_ERROR(4),
	/**
	 * 解析异常
	 */
	PARSE_ERROR(5);
	private final int val;

	private REQUEST_STATUS(int value) {
		val = value;
	}

	public int getValue() {
		return val;
	}

}
