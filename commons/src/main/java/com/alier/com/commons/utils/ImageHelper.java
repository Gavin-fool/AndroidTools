package com.alier.com.commons.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

/**
 * 图片处理帮助类
 * 
 * @author lvjunwei@supermap.com
 * 
 */
public class ImageHelper {
	// 图片缓存数据 /mnt/sdcard/urban_problems_support/photo/cache/
	public static String CACHE_PATH = com.alier.com.commons.Config.ANDROID_TEST_PATH + "/photos" + File.separator + "watercache";

	/**
	 * 生成圆角图片
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		try {
			Canvas canvas = null;
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			if (output != null) {
				canvas = new Canvas(output);
				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
				final RectF rectF = new RectF(rect);
				final float roundPx = pixels;

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);
				return output;
			} else {
				return bitmap;
			}

		} catch (OutOfMemoryError e) {
			Log.e("ImageHelper", "Can't create bitmap with rounded corners. Not enough memory.", e);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LogMgr.error("ImageHelper", "生成圆角图片出错了:：" + sw.toString() + bitmap);
			return bitmap;
		}
	}

	/**
	 * 获取图片缩小的图片
	 * 
	 * @param src
	 *            图片来源
	 * @param max
	 *            图片缩放比例
	 * @return
	 */
	public static Bitmap scaleBitmap(String src, int max) {
		// 获取图片的高和宽
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 这一个设置使 BitmapFactory.decodeFile获得的图片是空的,但是会将图片信息写到options中
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(src, options);
		// 计算比例 为了提高精度,本来是要640 这里缩为64
		max = max / 10;
		int be = options.outWidth / max;
		if (be % 10 != 0)
			be += 10;
		be = be / 10;
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		// 设置可以获取数据
		options.inJustDecodeBounds = false;
		// 获取图片
		return BitmapFactory.decodeFile(src, options);
	}

	/**
	 * 缩放图像（按高度和宽度缩放）
	 *
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int w, int h) {
		// 获取原始图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 计算缩放比率，新尺寸除以原始尺寸
		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		// 旋转图片 动作
		// matrix.postRotate(45);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * 给图片添加水印
	 * 
	 * @param src
	 *            图片来源
	 * @param watermark
	 *            水印图片
	 * @param title
	 *            水印文字
	 * @return
	 */
	public static Bitmap addWatermarkBitmap(Bitmap src, Bitmap watermark, String title) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		// 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		if (newb == null) {
			newb = Bitmap.createBitmap(w, h, Config.ARGB_4444);// 创建一个新的和SRC长度宽度一样的位图
		}
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		Paint paint = new Paint();
		// 加入图片
		if (watermark != null) {
			int ww = watermark.getWidth();
			int wh = watermark.getHeight();
			paint.setAlpha(50);
			cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, paint);// 在src的右下角画入水印
		}
		// 加入文字
		if (title != null) {
			String familyName = "宋体";
			Typeface font = Typeface.create(familyName, Typeface.BOLD);
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(Color.RED);
			textPaint.setTypeface(font);
			textPaint.setTextSize(40);
			// 当前日期
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式
			String dateNow = df.format(new Date());
			// 这里是自动换行的
			// StaticLayout layout = new
			// StaticLayout(title+dateNow,textPaint,w,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
			// layout.draw(cv);
			// 文字就加左上角算了
			cv.drawText(title + " " + dateNow, 0, 80, textPaint);
			// cv.drawText(title+"-"+dateNow, w - 50, h - 20, paint);
		}
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		if (src != null & !src.isRecycled()) {
			src.recycle();
			System.gc();
		}
		return newb;
	}

	/**
	 * 给图片添加水印
	 * 
	 * @param src
	 *            图片来源
	 * @param title
	 *            水印文字
	 * @return
	 */
	public static Bitmap addWatermarkBitmap(Bitmap src, String title) {
		if (src == null) {
			return null;
		}
		try {
			int w = src.getWidth();
			int h = src.getHeight();
			// 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
			Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
			if (newb == null) {
				newb = Bitmap.createBitmap(w, h, Config.ARGB_4444);// 创建一个新的和SRC长度宽度一样的位图
			}
			Canvas cv = new Canvas(newb);
			cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
			Paint paint = new Paint();
			// 加入文字
			if (title != null) {
				String familyName = "宋体";
				Typeface font = Typeface.create(familyName, Typeface.BOLD);
				TextPaint textPaint = new TextPaint();
				textPaint.setColor(Color.RED);
				textPaint.setTypeface(font);
				textPaint.setTextSize(40);
				// 这里是自动换行的
				// StaticLayout layout = new
				// StaticLayout(title+dateNow,textPaint,w,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
				// layout.draw(cv);
				// 文字就加左上角算了
				cv.drawText(title, 0, 40, textPaint);
				// cv.drawText(title+"-"+dateNow, w - 50, h - 20, paint);
			}
			cv.save(Canvas.ALL_SAVE_FLAG);// 保存
			cv.restore();// 存储
			if (src != null & !src.isRecycled()) {
				src.recycle();
				System.gc();
			}
			return newb;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LogMgr.error("ImageHelper", "图片添加水印出错了:：" + sw.toString());
			return src;
		}
	}

	/**
	 * 
	 * @Description:给图片加水印
	 * @param src
	 *            图片来源
	 * @param title
	 *            水印文字
	 * @param color
	 *            水印颜色
	 * @return
	 * @throws:
	 */
	public static Bitmap addWatermarkBitmap(Bitmap src, String title, int color) {
		if (src == null) {
			return null;
		}
		try {
			int w = src.getWidth();
			int h = src.getHeight();
			// 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
			Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
			if (newb == null) {
				newb = Bitmap.createBitmap(w, h, Config.ARGB_4444);// 创建一个新的和SRC长度宽度一样的位图
			}
			Canvas cv = new Canvas(newb);
			cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
			Paint paint = new Paint();
			// 加入文字
			if (title != null) {
				String familyName = "宋体";
				Typeface font = Typeface.create(familyName, Typeface.BOLD);
				TextPaint textPaint = new TextPaint();
				textPaint.setColor(color);
				textPaint.setTypeface(font);
				textPaint.setTextSize(35);
				// 这里是自动换行的
				StaticLayout layout = new StaticLayout(title, textPaint, w, Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
				cv.translate(0, h - 140);
				layout.draw(cv);
				// 文字就加左上角算了
				// cv.drawText(title, 0, h-80, textPaint);
				// cv.drawText(title+"-"+dateNow, w - 50, h - 20, paint);
			}
			cv.save(Canvas.ALL_SAVE_FLAG);// 保存
			cv.restore();// 存储
			if (src != null & !src.isRecycled()) {
				src.recycle();
				System.gc();
			}
			return newb;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LogMgr.error("ImageHelper", "图片添加水印出错了:：" + sw.toString());
			return src;
		}
	}

	/**
	 * 压缩并添加水印
	 * 
	 * @param pathName
	 * @param width
	 * @param waterText
	 * @return
	 */
	public static boolean compressAndAddWaterCode(String pathName, int width, String waterText) {
		boolean val = true;
		try {
			File file = new File(pathName);
			if (!file.exists() || !file.canRead() || !file.canWrite()) {
				return false;
			}
			if (file.length() > Runtime.getRuntime().freeMemory()) {
				System.gc();
			}
			if (file.length() / 1024 > 256) {
				// 图片小于256kb则不进行处理
				FileInputStream in = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				in.read(data);
				in.close();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				// 获取原图片的宽和高
				Bitmap $bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				// Bitmap bitmap =
				// BitmapFactory.decodeFile(path+"/"+fileItem.getName(),
				// options);
				// //此时返回bm为空
				options.inJustDecodeBounds = false;
				// 计算缩放比
				int be = 2;
				if (options.outHeight < options.outWidth)
					be = (int) (options.outHeight / (float) width);
				else
					be = (int) (options.outWidth / (float) width);
				// 判断是否超过原始图片高度 //如果超过，则不进行缩放
				if (be < 2)
					be = 2;
				options.inSampleSize = be;
				// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
				// bitmap=BitmapFactory.decodeStream(stream, null, options);
				in.close();
				in = null;
				$bitmap = BitmapFactory.decodeFile(file.getPath(), options);
				ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
				$bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
				byte[] array = out.toByteArray();
				try {
					FileOutputStream output = new FileOutputStream(pathName);
					output.write(array);
					output.flush();
					output.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if ($bitmap != null && !$bitmap.isRecycled()) {
						$bitmap.recycle();
					}
				}
				out.flush();
				out.close();
				System.gc();
			}
			// 加载图片
			FileInputStream fis = new FileInputStream(pathName);
			// 把流转化为Bitmap图片
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			// 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
			Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
			Canvas cv = new Canvas(newb);
			cv.drawBitmap(bitmap, 0, 0, null);// 在 0，0坐标开始画入src
			Paint paint = new Paint();
			// 加入文字
			if (waterText != null) {
				String familyName = "宋体";
				Typeface font = Typeface.create(familyName, Typeface.BOLD);
				TextPaint textPaint = new TextPaint();
				textPaint.setColor(Color.RED);
				textPaint.setTypeface(font);
				textPaint.setTextSize(40);
				// 文字就加左上角算了
				cv.drawText(waterText, 0, 80, textPaint);
			}
			cv.save(Canvas.ALL_SAVE_FLAG);// 保存
			cv.restore();// 存储
			if (bitmap != null & !bitmap.isRecycled()) {
				bitmap.recycle();
				System.gc();
			}
			File dirFile = new File(pathName);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			File myCaptureFile = new File(CACHE_PATH);
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
				bos.flush();
				bos.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					System.gc();
				}
			}
		} catch (IOException ex) {
			Log.e("FileUtils", "io exception photoHandler(String ,int) from FileUtils");
			return false;
		} catch (OutOfMemoryError ome) {
			Log.e("FileUtils", "OutOfMemoryError photoHandler(String ,int) from FileUtils");
			return false;
		}
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			// 把流转化为Bitmap图片
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 保存图片文件到本地
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveFile(Bitmap bitmap, String fileName) throws IOException {
		File dirFile = new File(CACHE_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File myCaptureFile = new File(fileName);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LogMgr.error("ImageHelper", "图片保存出错了:：" + sw.toString());
		} finally {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				System.gc();
			}
		}
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap;
		try {
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizedBitmap;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}