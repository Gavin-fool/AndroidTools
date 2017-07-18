package com.alier.com.commons.album;

import java.util.List;

/**
 * @Title:ImageFloder
 * @description:媒体文件夹
 * @author:gavin_fool
 * @date:2017年4月11日下午3:06:31
 * @version:v1.0
 */
public class MediaFloder {
	// 文件夹数量，默认为0
	private int count = 0;
	// 文件夹名称
	private String floderName;
	// 文件夹中存在的媒体集合
	private List<MediaItem> imageList;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getFloderName() {
		return floderName;
	}

	public void setFloderName(String floderName) {
		this.floderName = floderName;
	}

	public List<MediaItem> getImageList() {
		return imageList;
	}

	public void setImageList(List<MediaItem> imageList) {
		this.imageList = imageList;
	}

}
