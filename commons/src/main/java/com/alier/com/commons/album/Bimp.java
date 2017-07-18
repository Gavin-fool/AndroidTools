package com.alier.com.commons.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title:Bimp
 * @description:选择图片临时工具类
 * @author:gavin_fool
 * @date:2017年4月13日上午9:54:18
 * @version:v1.0
 */
public class Bimp {
	private Bimp() {

	}

	public static int selectBitmapSize = 0;
	public static int selectVideoSize = 0;
	/**
	 * 选择的图片的临时列表
	 */
	public static List<MediaItem> tempSelectBitmap = new ArrayList<MediaItem>();

	/**
	 * @Description:修改图片的大小
	 * @param @param
	 *            path
	 * @param @return
	 * @param @throws
	 *            IOException
	 * @return:Bitmap
	 */
	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
}
