package com.alier.com.commons.entity;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * 多媒体实体类
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2017年5月23日 下午3:09:52
 * @version 1.0
 */
public class MediaInfo implements Serializable, Comparable<Object> {

	/**
	 * 实体唯一标识
	 */
	private static final long serialVersionUID = -2486305447444969560L;
	public static final int IMAGE = 1;
	public static final int AUDIO = 2;
	public static final int VIDEO = 3;
	/** 媒体uuid（与多媒体服务器中mediaid一样） */
	private String uuid;
	/** 媒体类型 */
	private int mediaType;
	/** 媒体名称（全名） */
	private String mediaName;
	/** 媒体来源 1:上报；2：核实；3：核查 ；4：专业部门上报 */
	private int mediaSource = 0;
	/** 当前媒体属于那个任务的任务ID */
	private String taskId;
	/** 存储在本地路径 */
	private String localPath = null;
	/** 存储在本地缩略图路径 */
	private String localThumbPath = null;
	/** 存储在服务端远程路径 */
	private String remotePath = null;
	/** 存储在服务端远程缩略图路径 */
	private String remoteThumbPath = null;
	/** 媒体描述 */
	private String strDiscript = null;
	/** 表示该媒体缩略图的bitmap */
	private byte[] bmMedia = null;
	/** 缩略图显示的宽度（单位dp），默认为60 */
	private int dX = 60;
	/** 缩略图显示的高度（单位dp），默认为60 */
	private int dY = 60;
	/** 缩略图的时间 */
	private String time;

	public MediaInfo() {
		super();
	}

	/**
	 * @param mediaType
	 *            媒体类型
	 * @param localPath
	 *            存储在本地路径
	 * @param localThumbPath
	 *            存储在本地缩略图路径
	 * @param remotePath
	 *            存储在服务端远程路径
	 * @param remoteThumbPath
	 *            存储在服务端远程缩略图路径
	 * @param strDiscript
	 *            媒体描述
	 */
	public MediaInfo(int mediaType, String localPath, String localThumbPath, String remotePath, String remoteThumbPath,
			String strDiscript) {
		super();
		this.mediaType = mediaType;
		this.localPath = localPath;
		this.localThumbPath = localThumbPath;
		this.remotePath = remotePath;
		this.remoteThumbPath = remoteThumbPath;
		this.strDiscript = strDiscript;
	}

	/**
	 * @param uuid
	 *            媒体uuid
	 * @param mediaType
	 *            媒体类型
	 * @param localPath
	 *            存储在本地路径
	 * @param localThumbPath
	 *            存储在本地缩略图路径
	 * @param remotePath
	 *            存储在服务端远程路径
	 * @param remoteThumbPath
	 *            存储在服务端远程缩略图路径
	 * @param strDiscript
	 *            媒体描述
	 */
	public MediaInfo(String uuid, int mediaType, String localPath, String localThumbPath, String remotePath,
			String remoteThumbPath, String strDiscript) {
		super();
		this.uuid = uuid;
		this.mediaType = mediaType;
		this.localPath = localPath;
		this.localThumbPath = localThumbPath;
		this.remotePath = remotePath;
		this.remoteThumbPath = remoteThumbPath;
		this.strDiscript = strDiscript;
	}

	/**
	 * @param uuid
	 *            媒体uuid
	 * @param mediaType
	 *            媒体类型
	 * @param localPath
	 *            存储在本地路径
	 * @param localThumbPath
	 *            存储在本地缩略图路径
	 * @param remotePath
	 *            存储在服务端远程路径
	 * @param remoteThumbPath
	 *            存储在服务端远程缩略图路径
	 * @param strDiscript
	 *            媒体描述
	 * @param dX
	 *            缩略图显示的宽度（单位dp），默认为50
	 * @param dY
	 *            缩略图显示的高度（单位dp），默认为50
	 */
	public MediaInfo(String uuid, int mediaType, String localPath, String localThumbPath, String remotePath,
			String remoteThumbPath, String strDiscript, int dX, int dY) {
		super();
		this.uuid = uuid;
		this.mediaType = mediaType;
		this.localPath = localPath;
		this.localThumbPath = localThumbPath;
		this.remotePath = remotePath;
		this.remoteThumbPath = remoteThumbPath;
		this.strDiscript = strDiscript;
		this.dX = dX;
		this.dY = dY;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	public int getMediaSource() {
		return mediaSource;
	}

	public void setMediaSource(int mediaSource) {
		this.mediaSource = mediaSource;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getLocalThumbPath() {
		return localThumbPath;
	}

	public void setLocalThumbPath(String localThumbPath) {
		this.localThumbPath = localThumbPath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getRemoteThumbPath() {
		return remoteThumbPath;
	}

	public void setRemoteThumbPath(String remoteThumbPath) {
		this.remoteThumbPath = remoteThumbPath;
	}

	public String getStrDiscript() {
		return strDiscript;
	}

	public void setStrDiscript(String strDiscript) {
		this.strDiscript = strDiscript;
	}

	public byte[] getBmMedia() {
		return bmMedia;
	}

	public void setBmMedia(byte[] bmMedia) {
		this.bmMedia = bmMedia;
	}

	public int getdX() {
		return dX;
	}

	public void setdX(int dX) {
		this.dX = dX;
	}

	public int getdY() {
		return dY;
	}

	public void setdY(int dY) {
		this.dY = dY;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "MediaInfo [uuid=" + uuid + ", mediaType=" + mediaType + ", localPath=" + localPath + ", localThumbPath="
				+ localThumbPath + ", remotePath=" + remotePath + ", remoteThumbPath=" + remoteThumbPath
				+ ", strDiscript=" + strDiscript + ", bmMedia=" + bmMedia + "]";
	}

	@Override
	public int compareTo(Object obj) {
		// 按照mediaSource排序
		if (obj instanceof MediaInfo) {
			MediaInfo info = (MediaInfo) obj;
			if (this.mediaSource > info.getMediaSource()) {
				return 1;
			} else if (this.mediaSource < info.getMediaSource()) {
				return -1;
			} else {
				return 0;
			}
		}
		return -1;
	}
}
