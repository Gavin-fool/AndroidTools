package com.alier.com.commons.album;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Title:ImageItem
 * @description:图片实体类
 * @author:gavin_fool
 * @date:2017年4月11日下午3:44:12
 * @version:v1.0
 */
public class MediaItem implements Parcelable, Comparable<Object> {
	public static final int IMAGE = 0;
	public static final int VIDEO = 1;
	private int mediaType;// 媒体类型
	private String mediaId;// 媒体id
	private String mediaThumbPath;// 媒体缩略图路径
	private String mediaPath;// 媒体路径
	private long videoTime;// 视频长度
	private long mediaTakenTime;// 媒体创建时间
	private boolean isSelected = false;// 是否被选中

	public MediaItem() {
		super();
	}

	public MediaItem(int mediaType, String imageId, String thumbnailPath, String imagePath, boolean isSelected) {
		super();
		this.mediaType = mediaType;
		this.mediaId = imageId;
		this.mediaThumbPath = thumbnailPath;
		this.mediaPath = imagePath;
		this.isSelected = isSelected;
	}

	public MediaItem(int mediaType, String mediaId, String mediaThumbPath, String mediaPath, long videoTime,
			long mediaTakenTime, boolean isSelected) {
		super();
		this.mediaType = mediaType;
		this.mediaId = mediaId;
		this.mediaThumbPath = mediaThumbPath;
		this.mediaPath = mediaPath;
		this.videoTime = videoTime;
		this.mediaTakenTime = mediaTakenTime;
		this.isSelected = isSelected;
	}

	private MediaItem(Parcel in) {
		mediaType = in.readInt();
		mediaId = in.readString();
		mediaThumbPath = in.readString();
		mediaPath = in.readString();
		isSelected = in.readByte() != 0;
		videoTime = in.readLong();
		mediaTakenTime = in.readLong();
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getMediaThumbPath() {
		return mediaThumbPath;
	}

	public void setMediaThumbPath(String mediaThumbPath) {
		this.mediaThumbPath = mediaThumbPath;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public long getVideoTime() {
		return videoTime;
	}

	public void setVideoTime(long videoTime) {
		this.videoTime = videoTime;
	}

	public long getMediaTakenTime() {
		return mediaTakenTime;
	}

	public void setMediaTakenTime(long mediaTakenTime) {
		this.mediaTakenTime = mediaTakenTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mediaType);
		dest.writeString(mediaId);
		dest.writeString(mediaThumbPath);
		dest.writeString(mediaPath);
		dest.writeByte((byte) (isSelected ? 1 : 0));
		dest.writeLong(videoTime);
		dest.writeLong(mediaTakenTime);
	}

	@Override
	public String toString() {
		return "MediaItem [mediaType=" + mediaType + ", mediaId=" + mediaId + ", mediaThumbPath=" + mediaThumbPath
				+ ", mediaPath=" + mediaPath + ", videoTime=" + videoTime + ", mediaTakenTime=" + mediaTakenTime
				+ ", isSelected=" + isSelected + "]";
	}

	public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {

		@Override
		public MediaItem[] newArray(int size) {
			return new MediaItem[size];
		}

		@Override
		public MediaItem createFromParcel(Parcel source) {
			return new MediaItem(source);
		}
	};

	@Override
	public int compareTo(Object o) {
		if (o instanceof MediaItem) {
			MediaItem s = (MediaItem) o;
			if (this.mediaTakenTime > s.mediaTakenTime) {
				return -1;
			} else if (this.mediaTakenTime < s.mediaTakenTime) {
				return 1;
			} else {
				return 0;
			}
		}
		return -1;
	}

}
