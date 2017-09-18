package com.alier.com.controllerlibrary.ui_photo_album;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.album.Bimp;
import com.alier.com.commons.album.MediaItem;
import com.alier.com.commons.album.MediaUtils;
import com.alier.com.commons.utils.LogMgr;
import com.alier.com.commons.zoom.PhotoView;
import com.alier.com.commons.zoom.ViewPagerFixed;
import com.alier.com.controllerlibrary.R;

/**
 * @Title:GalleryActivity
 * @description:这个是用于进行图片浏览时的界面
 * @author:gavin_fool
 * @date:2017年4月13日上午9:26:55
 * @version:v1.0
 */
public class GalleryActivity extends BaseActivity {
	private static final String TAG = "GalleryActivity";
	// 发送按钮
	private Button btSend;
	// 当前的位置
	private int location = 0;
	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	@Override
	public void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.plugin_camera_gallery);// 切屏到主界面
		btSend = (Button) findViewById(R.id.send_button);
		Button btDel = (Button) findViewById(R.id.gallery_del);
		btSend.setOnClickListener(new GallerySendListener());
		btDel.setOnClickListener(new DelListener());
		isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			initListViews(Bimp.tempSelectBitmap.get(i));
		}

		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int) getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
		int id = getIntent().getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}

	@Override
	public void exec() {

	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			location = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void initListViews(final MediaItem mediaItem) {
		String path = null;
		Bitmap bm = null;
		if (mediaItem.getMediaType() == MediaItem.IMAGE) {
			path = mediaItem.getMediaPath();
			try {
				bm = Bimp.revitionImageSize(path);
			} catch (IOException e) {
				LogMgr.error(TAG, "修改图片大小异常" + e.toString());
				e.printStackTrace();
			}
		} else {
			path = mediaItem.getMediaThumbPath();
			if (null == path) {
				bm = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
			} else {
				try {
					bm = Bimp.revitionImageSize(path);
				} catch (IOException e) {
					LogMgr.error(TAG, "修改图片大小异常" + e.toString());
					e.printStackTrace();
				}
			}
		}
		if (listViews == null)
			listViews = new ArrayList<View>();

		if (mediaItem.getMediaType() == MediaItem.VIDEO) {
			View view = getLayoutInflater().inflate(R.layout.video_browse, null);
			ImageView imgSrc = (ImageView) view.findViewById(R.id.img_src);
			imgSrc.setImageBitmap(bm);
			Button imgPlay = (Button) view.findViewById(R.id.img_play);
			imgPlay.setBackgroundResource(R.drawable.video_play);
			imgPlay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(mediaItem.getMediaPath()), "video/mp4");
					startActivity(intent);
				}
			});
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			listViews.add(view);
		} else {
			PhotoView img = new PhotoView(this);
			img.setBackgroundColor(0xff000000);
			img.setImageBitmap(bm);
			img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			listViews.add(img);
		}

	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (listViews.size() == 1) {
				Bimp.tempSelectBitmap.clear();
				Bimp.selectBitmapSize = 0;
				Bimp.selectVideoSize = 0;
				btSend.setText(getString(R.string.camera_sure) + "(" + Bimp.tempSelectBitmap.size() + "/"
						+ MediaUtils.num + ")");
				finish();
			} else {
				if (Bimp.tempSelectBitmap.get(location).getMediaType() == MediaItem.IMAGE) {
					Bimp.selectBitmapSize--;
				} else {
					Bimp.selectVideoSize--;
				}
				Bimp.tempSelectBitmap.remove(location);
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				btSend.setText(getString(R.string.camera_sure) + "(" + Bimp.tempSelectBitmap.size() + "/"
						+ MediaUtils.num + ")");
				adapter.notifyDataSetChanged();
			}
		}
	}

	// 完成按钮的监听
	private class GallerySendListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(GalleryActivity.this, MediaChooseActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(GalleryActivity.this, MediaChooseActivity.class);
			startActivity(intent);
			finish();
		}
		return true;
	}

	public void isShowOkBt() {
		if (!Bimp.tempSelectBitmap.isEmpty()) {
			btSend.setText(
					getString(R.string.camera_sure) + "(" + Bimp.tempSelectBitmap.size() + "/" + MediaUtils.num + ")");
			btSend.setPressed(true);
			btSend.setClickable(true);
			btSend.setTextColor(Color.WHITE);
		} else {
			btSend.setPressed(false);
			btSend.setClickable(false);
			btSend.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;

		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		@Override
		public int getCount() {
			return size;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
				LogMgr.error(TAG, "显示图片异常" + e.toString());
			}
			return listViews.get(arg1 % size);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
