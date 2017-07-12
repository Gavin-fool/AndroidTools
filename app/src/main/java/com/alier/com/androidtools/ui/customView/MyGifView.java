package com.alier.com.androidtools.ui.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.alier.com.androidtools.R;

/**
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年11月10日 下午3:32:55
 * @version 1.0
 */
public class MyGifView extends View {

	private long movieStart;
	private Movie movie;

	public MyGifView(Context context, AttributeSet attributeSet) {
		super(context);
		movie = Movie.decodeStream(getResources().openRawResource(R.drawable.start));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long curTime = android.os.SystemClock.uptimeMillis();
		// 第一次播放
		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
			// 强制重绘
			invalidate();
		}
		super.onDraw(canvas);
	}
}
