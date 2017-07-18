package com.alier.com.commons.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 * @Title:MediaChooseHelper
 * @description:媒体查询工具类，查询图片与视频返回
 * @author:gavin_fool
 * @date:2017年4月13日上午9:58:50
 * @version:v1.0
 */
public class MediaChooseHelper {
	private Context context;
	private ContentResolver cr;
	/** 图片缩略图列表 */
	private HashMap<String, String> thumbnailList = new HashMap<String, String>();
	/** 视频缩略图列表 */
	private HashMap<String, String> videoThumbList = new HashMap<String, String>();
	/** 文件夹列表 */
	private static HashMap<String, MediaFloder> bucketMap = new HashMap<String, MediaFloder>();
	public static List<MediaFloder> bucketList = new ArrayList<MediaFloder>();
	private static MediaChooseHelper instance;

	private MediaChooseHelper() {
	}

	public static MediaChooseHelper getHelper() {
		if (instance == null) {
			instance = new MediaChooseHelper();
		}
		return instance;
	}

	/**
	 * @Description:初始化
	 * @param context
	 * @return:void
	 */
	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			cr = context.getContentResolver();
		}
	}

	public List<MediaItem> getMediaList(){
		thumbnailList.clear();
		videoThumbList.clear();
		bucketMap.clear();
		bucketList.clear();
		buildMediaBucketList();
		List<MediaItem> tmpList = new ArrayList<MediaItem>();
		Iterator<Entry<String, MediaFloder>> itr = bucketMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, MediaFloder> entry = (Entry<String, MediaFloder>) itr.next();
			if(entry.getValue().getImageList().isEmpty()){
				continue;
			}
			tmpList.addAll(entry.getValue().getImageList());
			bucketList.add(entry.getValue());
		}
		//对查询的结果排序
		Collections.sort(tmpList);
		return tmpList;
	}
	/**
	 * @Description:获取视频文件夹列表
	 */
	private void buildVideoBucketList() {
		Cursor cursor = cr.query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		if (cursor.moveToFirst()) {
			int videoIdIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media._ID);
			int videoPathIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media.DATA);
			int videoDisplayNameIndex = cursor
					.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
			int videoDuration = cursor.getColumnIndex(android.provider.MediaStore.Video.Media.DURATION);
			int videoDateTaken = cursor.getColumnIndex(android.provider.MediaStore.Video.Media.DATE_TAKEN);
			do {
				String videoId = cursor.getString(videoIdIndex);
				String videoPath = cursor.getString(videoPathIndex);
				String bucketName = cursor.getString(videoDisplayNameIndex);
				long dateTaken = cursor.getLong(videoDateTaken);
				long duration = cursor.getLong(videoDuration);
				if (!"Camera".equals(bucketName) && !"DCIM".equals(bucketName)) {
					continue;
				}
				MediaFloder floder = bucketMap.get(bucketName);
				if (floder == null) {
					floder = new MediaFloder();
					bucketMap.put(bucketName, floder);
					floder.setImageList(new ArrayList<MediaItem>());
					floder.setFloderName(bucketName);
				}
				floder.setCount(floder.getCount() + 1);
				MediaItem mediaItem = new MediaItem();
				mediaItem.setMediaType(MediaItem.VIDEO);
				mediaItem.setMediaId(videoId);
				mediaItem.setMediaPath(videoPath);
				mediaItem.setMediaThumbPath(videoThumbList.get(videoId));
				mediaItem.setVideoTime(duration);
				mediaItem.setMediaTakenTime(dateTaken);
				floder.getImageList().add(mediaItem);
			} while (cursor.moveToNext());
		}
	}

	/**
	 * 创建文件夹信息列表＋缩略图处理列表
	 */
	private void buildMediaBucketList() {
		// 获取缩略图
		getImageThumbnail();
		getVideoThumbnail();
		buildVideoBucketList();
		String columns[] = new String[] { Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA, Media.MIME_TYPE,
				Media.DISPLAY_NAME, Media.TITLE, Media.SIZE, Media.DATE_TAKEN, Media.BUCKET_DISPLAY_NAME };
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, null,
				columns[6] + ">100000 AND " + columns[4] + "=? OR " + columns[4] + "=? OR " + columns[4] + "=? ",
				new String[] { "image/jpeg", "image/png", "image/jpg" }, columns[8] + " DESC");
		if (cur.moveToFirst()) {
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int photoDateTaken = cur.getColumnIndex(Media.DATE_TAKEN);
			do {
				String imgId = cur.getString(photoIDIndex);
				String path = cur.getString(photoPathIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				long dateTaken = cur.getLong(photoDateTaken);
				if (!"Camera".equals(bucketName) && !"DCIM".equals(bucketName) && !"Screenshots".equals(bucketName)) {
					continue;
				}
				MediaFloder floder = bucketMap.get(bucketName);
				if (floder == null) {
					floder = new MediaFloder();
					bucketMap.put(bucketName, floder);
					floder.setImageList(new ArrayList<MediaItem>());
					floder.setFloderName(bucketName);
				}
				floder.setCount(floder.getCount() + 1);
				MediaItem mediaItem = new MediaItem();
				mediaItem.setMediaType(MediaItem.IMAGE);
				mediaItem.setMediaId(imgId);
				mediaItem.setMediaPath(path);
				mediaItem.setMediaThumbPath(thumbnailList.get(imgId));
				mediaItem.setMediaTakenTime(dateTaken);
				Log.i("TAG", "thumbPath:" + imgId + ":" + thumbnailList.get(imgId));
				floder.getImageList().add(mediaItem);

			} while (cur.moveToNext());
		}
		cur.close();
		cur = null;
	}

	/**
	 * @Description:获取图片缩略图
	 */
	private void getImageThumbnail() {
		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
		if (cursor.moveToFirst()) {
			int imageId;
			String imagePath;// 缩略图路径
			int imageIdColumn = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cursor.getColumnIndex(Thumbnails.DATA);
			do {
				imageId = cursor.getInt(imageIdColumn);
				imagePath = cursor.getString(dataColumn);
				thumbnailList.put(Integer.toString(imageId), imagePath);
				// TODO图片缩略图找不到
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
	}

	/**
	 * @Description:获取视频缩略图
	 * @param
	 * @return:void
	 */
	private void getVideoThumbnail() {
		Cursor cursor = cr.query(android.provider.MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null,
				null);
		if (cursor.moveToFirst()) {
			int videoId;
			String videoPath;// 缩略图路径
			int videoIdColumn = cursor.getColumnIndex(android.provider.MediaStore.Video.Thumbnails.VIDEO_ID);
			int dataColumn = cursor.getColumnIndex(android.provider.MediaStore.Video.Thumbnails.DATA);
			do {
				videoId = cursor.getInt(videoIdColumn);
				videoPath = cursor.getString(dataColumn);
				videoThumbList.put(Integer.toString(videoId), videoPath);
				// TODO图片缩略图找不到
				Log.i("TAG", "thumb:" + videoId + ":" + videoPath);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
	}
}
