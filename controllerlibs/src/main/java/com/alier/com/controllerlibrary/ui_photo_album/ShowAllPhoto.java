package com.alier.com.controllerlibrary.ui_photo_album;

import java.util.ArrayList;
import java.util.List;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.album.Bimp;
import com.alier.com.commons.album.MediaItem;
import com.alier.com.commons.album.MediaUtils;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.R;

/**
 * @Title:ShowAllPhoto
 * @description:这个是显示一个文件夹里面的所有图片时的界面
 * @author:gavin_fool
 * @date:2017年4月12日上午9:57:21
 * @version:v1.0
 */

public class ShowAllPhoto extends BaseActivity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// 确定按钮
	private Button sure;
	// 标题
	private TextView headTitle;
	private Intent intent;
	public static List<MediaItem> dataList = new ArrayList<MediaItem>();

	@Override
	public void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.plugin_camera_show_all_photo);
		sure = (Button) findViewById(R.id.showallphoto_ok);
		headTitle = (TextView) findViewById(R.id.showallphoto_headtitle);
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
		headTitle.setText(folderName);
		sure.setOnClickListener(new SureListener());
		initView();
		initListener();
	}

	@Override
	public void exec() {

	}

	@Override
	protected void onStart() {
		super.onStart();
		gridImageAdapter.notifyDataSetChanged();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	private class SureListener implements OnClickListener {// 确定按钮的监听

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(ShowAllPhoto.this, MediaChooseActivity.class);
			startActivity(intent);
		}
	}

	private void initView() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		progressBar = (ProgressBar) findViewById(R.id.showallphoto_progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.showallphoto_myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
	}

	private void initListener() {
		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
			public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, Button button) {
				if (dataList.get(position).getMediaType() == MediaItem.IMAGE) {// 选择照片时
					if (Bimp.selectBitmapSize >= MediaUtils.num) {// 判断最大的图片选择个数
						toggleButton.setChecked(false);
						button.setVisibility(View.GONE);
						if (!removeOneData(dataList.get(position))) {
							T.showShort(ShowAllPhoto.this, getString(R.string.only_choose_num));
						}
						return;
					}
					if (isChecked) {// 如果选择图片
						button.setVisibility(View.VISIBLE);
						// 添加已选图片的个数
						Bimp.tempSelectBitmap.add(dataList.get(position));
						Bimp.selectBitmapSize++;
					} else {// 如果取消图片
						button.setVisibility(View.GONE);
						removeOneData(dataList.get(position));
					}
				} else {// 选择视频时
					if (Bimp.selectVideoSize >= 1) {// 判断最大的图片选择个数
						toggleButton.setChecked(false);
						button.setVisibility(View.GONE);
						if (!removeOneData(dataList.get(position))) {
							T.showShort(ShowAllPhoto.this, getString(R.string.video_chooose_num));
						}
						return;
					}
					if (isChecked) {// 如果选择图片
						button.setVisibility(View.VISIBLE);
						// 添加已选图片的个数
						Bimp.tempSelectBitmap.add(dataList.get(position));
						Bimp.selectVideoSize++;
					} else {// 如果取消图片
						button.setVisibility(View.GONE);
						removeOneData(dataList.get(position));
					}
				}
			}
		});
	}

	/**
	 * @Description:移除一个已选媒体
	 * @param imageItem
	 * @param @return
	 * @return:boolean
	 */
	private boolean removeOneData(MediaItem imageItem) {
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			if (Bimp.tempSelectBitmap.get(i).getMediaPath().equals(imageItem.getMediaPath())) {
				if (Bimp.tempSelectBitmap.get(i).getMediaType() == MediaItem.IMAGE) {
					Bimp.selectBitmapSize--;
				} else {
					Bimp.selectVideoSize--;
				}
				Bimp.tempSelectBitmap.remove(i);
				return true;
			}
		}
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bimp.tempSelectBitmap.clear();
			Bimp.selectBitmapSize = 0;
			Bimp.selectVideoSize = 0;
			finish();
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
}
