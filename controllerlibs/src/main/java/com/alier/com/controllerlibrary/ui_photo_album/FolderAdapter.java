package com.alier.com.controllerlibrary.ui_photo_album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alier.com.commons.album.BitmapCache;
import com.alier.com.commons.album.MediaChooseHelper;
import com.alier.com.commons.album.MediaItem;
import com.alier.com.controllerlibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是显示所有包含图片的文件夹的适配器
 */
public class FolderAdapter extends BaseAdapter {
	private static final String TAG = "FolderAdapter";
	private Context mContext;
	private Intent mIntent;
	private DisplayMetrics dm;
	private BitmapCache cache;

	public FolderAdapter(Context c) {
		cache = new BitmapCache();
		init(c);
	}

	// 初始化
	public void init(Context c) {
		mContext = c;
		mIntent = ((Activity) mContext).getIntent();
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	@Override
	public int getCount() {
		return MediaChooseHelper.bucketList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	private class ViewHolder {
		// 封面
		public ImageView imageView;
		public ImageView choose_back;
		// 文件夹名称
		public TextView folderName;
		// 文件夹里面的图片数量
		public TextView fileNum;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_select_folder, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.file_image);
			holder.choose_back = (ImageView) convertView.findViewById(R.id.choose_back);
			holder.folderName = (TextView) convertView.findViewById(R.id.name);
			holder.fileNum = (TextView) convertView.findViewById(R.id.filenum);
			holder.imageView.setAdjustViewBounds(true);
			holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		String path;
		if (MediaChooseHelper.bucketList.get(position).getImageList() != null) {
			// 封面图片路径
			path = MediaChooseHelper.bucketList.get(position).getImageList().get(0).getMediaPath();
			holder.folderName.setText(MediaChooseHelper.bucketList.get(position).getFloderName());
			// 给fileNum设置文件夹内图片数量
			holder.fileNum.setText("" + MediaChooseHelper.bucketList.get(position).getCount());

		} else {
			path = "android_hybrid_camera_default";
		}
		if (path.contains("android_hybrid_camera_default")) {
			holder.imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
		} else {
			final MediaItem item = MediaChooseHelper.bucketList.get(position).getImageList().get(0);
			holder.imageView.setTag(item.getMediaPath());
			cache.displayBmp(holder.imageView, item.getMediaThumbPath(), item.getMediaPath(), callback);
		}
		// 为封面添加监听
		holder.imageView.setOnClickListener(new ImageViewClickListener(position, mIntent, holder.choose_back));

		return convertView;
	}

	// 为每一个文件夹构建的监听器
	private class ImageViewClickListener implements OnClickListener {
		private int position;
		private ImageView choose_back;

		public ImageViewClickListener(int position, Intent intent, ImageView choose_back) {
			this.position = position;
			this.choose_back = choose_back;
		}

		@Override
		public void onClick(View v) {
			ShowAllPhoto.dataList = (ArrayList<MediaItem>) MediaChooseHelper.bucketList.get(position).getImageList();
			Intent intent = new Intent();
			String folderName = MediaChooseHelper.bucketList.get(position).getFloderName();
			intent.putExtra("folderName", folderName);
			intent.setClass(mContext, ShowAllPhoto.class);
			mContext.startActivity(intent);
			choose_back.setVisibility(View.VISIBLE);
		}
	}
}
