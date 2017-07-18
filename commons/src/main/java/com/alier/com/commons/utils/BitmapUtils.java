package com.alier.com.commons.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

/**
 * bitmap工具类，主要用于更换图片的颜色，或在图片中增加文字
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2017年1月12日 下午2:45:18
 * @version 1.0
 */
public class BitmapUtils {

	/**
	 * @Description:在图片中添加文本
	 * @param gContext
	 * @param gResId
	 *            图片id
	 * @param mColor
	 *            指定颜色
	 * @param gText
	 *            需要添加的文本
	 * @param:@return 图片转换成的bitmap对象
	 */
	public static Bitmap drawTextToBitmap(Context gContext, int gResId, int mColor, String gText) {
		Resources resources = gContext.getResources();
		float scale = resources.getDisplayMetrics().density;
		Bitmap bitmapResourse = BitmapFactory.decodeResource(resources, gResId);
		Bitmap bitmap = getAlphaBitmap(bitmapResourse, mColor);
		Config bitmapConfig = bitmap.getConfig();
		if (bitmapConfig == null) {
			bitmapConfig = Config.ARGB_8888;
		}
		bitmap = bitmap.copy(bitmapConfig, true);

		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int) (15 * scale));
		paint.setDither(true); // 获取跟清晰的图像采样
		paint.setFilterBitmap(true);// 过滤一些
		Rect bounds = new Rect();
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		int x = (int) ((bitmap.getWidth() - bounds.width()) / 2 * 0.9);
		int y = (int) ((bitmap.getHeight() + bounds.height()) / 2 * 0.73);
		canvas.drawText(gText, x, y, paint);
		return bitmap;
	}

	/**
	 * @Description:提取图像AlFpha位图
	 * @param mBitmap
	 *            原始位图
	 * @param mColor
	 *            指定颜色
	 * @param:@return
	 * @return Bitmap 改变指定颜色的位图
	 */
	public static Bitmap getAlphaBitmap(Bitmap mBitmap, int mColor) {
		Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mAlphaBitmap);
		Paint mPaint = new Paint();
		mPaint.setColor(mColor);
		// 从原位图中提取只包含alpha的位图
		Bitmap alphaBitmap = mBitmap.extractAlpha();
		// 在画布上（mAlphaBitmap）绘制alpha位图
		mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);
		return mAlphaBitmap;
	}
}
