package com.alier.com.controllerlibrary.ui_photo_album;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alier.com.commons.album.BitmapCache;
import com.alier.com.commons.album.MediaItem;
import com.alier.com.controllerlibrary.R;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 */
public class AlbumGridViewAdapter extends BaseAdapter {
	private static final String TAG = "AlbumGridViewAdapter";
	private Context mContext;
	private List<MediaItem> dataList;
	private List<MediaItem> selectedDataList;
	private DisplayMetrics dm;
	private BitmapCache cache;

	public AlbumGridViewAdapter(Context c, List<MediaItem> dataList, List<MediaItem> selectedDataList) {
		mContext = c;
		cache = new BitmapCache();
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
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
					imageView.setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		private ImageView imageView;
		private ToggleButton toggleButton;
		private Button choosetoggle;
		private ImageView imgVideoIcon;
		private TextView tvVideoTime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_select_imageview, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			viewHolder.toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
			viewHolder.choosetoggle = (Button) convertView.findViewById(R.id.choosedbt);
			viewHolder.imgVideoIcon = (ImageView) convertView.findViewById(R.id.img_video_icon);
			viewHolder.tvVideoTime = (TextView) convertView.findViewById(R.id.tv_video_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position).getMediaPath();
		else
			path = "camera_default";
		if (path.contains("camera_default")) {
			viewHolder.imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
		} else {
			final MediaItem item = dataList.get(position);
			viewHolder.imageView.setTag(item.getMediaPath());
			cache.displayBmp(viewHolder.imageView, item.getMediaThumbPath(), item.getMediaPath(), callback);
		}
		if (dataList.get(position).getMediaType() == MediaItem.VIDEO) {
			viewHolder.imgVideoIcon.setVisibility(View.VISIBLE);
			long time = dataList.get(position).getVideoTime();
			SimpleDateFormat format = new SimpleDateFormat("mm:ss");
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(time);
			viewHolder.tvVideoTime.setText(format.format(gc.getTime()));
			viewHolder.tvVideoTime.setVisibility(View.VISIBLE);
		}else{
			viewHolder.imgVideoIcon.setVisibility(View.GONE);
			viewHolder.tvVideoTime.setVisibility(View.GONE);
		}
		viewHolder.toggleButton.setTag(position);
		viewHolder.choosetoggle.setTag(position);
		viewHolder.choosetoggle.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
		viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
		if (isContains(position)) {
			viewHolder.toggleButton.setChecked(true);// 背景色的改变
			viewHolder.choosetoggle.setVisibility(View.VISIBLE);// 显示绿色的对勾
		} else {
			viewHolder.toggleButton.setChecked(false);
			viewHolder.choosetoggle.setVisibility(View.GONE);
		}
		return convertView;
	}

	private boolean isContains(int position) {
		for (int i = 0; i < selectedDataList.size(); i++) {
//			if (selectedDataList.get(i).getMediaPath().equals(dataList.get(position).getMediaPath())) {
//				return true;
//			}
			//按照图片名称匹配 （草稿按照路径无法匹配）
			if (selectedDataList.get(i).getMediaPath().contains(getMediaName(dataList.get(position).getMediaPath()))) {
				return true;
			}
		}
		return false;
	}

	private  String getMediaName(String mediaPath) {
		return mediaPath.substring(mediaPath.lastIndexOf("/"), mediaPath.length());
	}
	
	private class ToggleClickListener implements OnClickListener {
		Button chooseBt;

		public ToggleClickListener(Button choosebt) {
			this.chooseBt = choosebt;
		}

		@Override
		public void onClick(View view) {
			if (view instanceof ToggleButton) {
				ToggleButton toggleButton = (ToggleButton) view;
				int position = (Integer) toggleButton.getTag();
				if (dataList != null && mOnItemClickListener != null && position < dataList.size()) {
					// 回调
					mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(), chooseBt);
				}
			}
		}
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(ToggleButton view, int position, boolean isChecked, Button chooseBt);
	}

}
