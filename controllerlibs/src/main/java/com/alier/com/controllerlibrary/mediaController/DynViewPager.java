package com.alier.com.controllerlibrary.mediaController;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.Config;
import com.alier.com.commons.entity.ListSerializable;
import com.alier.com.commons.utils.DepthPageTransformer;
import com.alier.com.commons.utils.FileUtils;
import com.alier.com.commons.utils.ObjectUtils;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.R;
import com.alier.com.controllerlibrary.custom_small.PinchImageView;

public class DynViewPager extends BaseActivity {
	private ViewPager viewPager;
	private ImageView[] mImageViews = null;//
	private List<String> checkedImgPath = null;
	private List<HashMap<String, String>> noExitImageMapList = null;
	private String currPath = null;
	private DynViewPagerAdater mViewPagerAdapter = null;
	private boolean hasUpdate = false;// 返回到采集记录界面是否需要刷新，true:需要，false:不需要

	@Override
	public void init() {
		setContentView(R.layout.dyn_viewpager);
		viewPager = (ViewPager) findViewById(R.id.viewPage);
		viewPager.setPageTransformer(true, new DepthPageTransformer());
	}

	@SuppressWarnings("all")
	@Override
	public void exec() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			ListSerializable listData = (ListSerializable) bundle.get("images");
			currPath = bundle.getString("currPath");
			checkedImgPath = listData.getList();
			noExitImageMapList = listData.getHashMapList();
		}
		if (null == checkedImgPath || checkedImgPath.size() < 1) {
			Toast.makeText(this, "没有获取到要查看的图片", Toast.LENGTH_SHORT).show();
			return;
		}
		mImageViews = new ImageView[checkedImgPath.size()];
		mViewPagerAdapter = new DynViewPagerAdater();
		viewPager.setAdapter(mViewPagerAdapter);
		viewPager.setCurrentItem(checkedImgPath.indexOf(currPath));
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				int failCount = loadImage();
				if (failCount == 0) {
					dynHandler.sendEmptyMessage(0);
				} else {
					dynHandler.sendEmptyMessage(-1);
				}

			}
		});
		thread.start();
	}

	class DynViewPagerAdater extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PinchImageView imageView = new PinchImageView(DynViewPager.this);
			imageView = setImageView(imageView, checkedImgPath.get(position));
			mImageViews[position] = imageView;
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mImageViews[position]);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}

	private Handler dynHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case -1:// 图片下载失败
				T.showShort(DynViewPager.this, "部分图片下载失败");
				break;
			}
			mViewPagerAdapter.notifyDataSetChanged();
			return false;
		}
	});

	public void closeActivity(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (hasUpdate) {
			setResult(0);
		} else {
			setResult(1);
		}
		this.finish();
	}

	/**
	 * 设置图片
	 */
	private PinchImageView setImageView(PinchImageView imageView, String path) {
		if (new File(path).exists()) {
			imageView.setImageBitmap(FileUtils.generateBitmap(path,1024*512));
		} else {
			imageView.setImageResource(R.drawable.no_exists_pic);
		}
		return imageView;
	}

	/**
	 * 
	 * @return 下载失败的图片的数量 0：图片都下载成功 ;返回值=n>0:n张图片下载失败
	 */
	private int loadImage() {
		int failCount = 0;// 下载失败的数量
		if (noExitImageMapList == null) {
			return failCount;
		}
		for (HashMap<String, String> imageHashMap : noExitImageMapList) {
			int flag = loadImageOpe(imageHashMap);
			if (flag != 0) {
				failCount++;
			}
		}
		return failCount;
	}

	/**
	 * 
	 * @param imageHashMap 包含多媒体路径信息的map
	 * @return 0:下载成功 -1：下载失败
	 */
	private int loadImageOpe(HashMap<String, String> imageHashMap) {
		int index = 0;
		String localPath = imageHashMap.get("localPath");
		String remotePath = imageHashMap.get("remotePath");
		try {
			index = Integer.parseInt(imageHashMap.get("index"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ObjectUtils.isEmpty(remotePath))
			return -1;
		if (ObjectUtils.isEmpty(localPath)) {
			String remoteFileName = remotePath.substring(
					remotePath.lastIndexOf("/") + 1, remotePath.length());
			String localFileName = remoteFileName.substring(
					remoteFileName.indexOf("_") + 1, remoteFileName.length());
			localPath = Config.MEDIA_PATH + "/photos"
					+ File.separator + localFileName;

			// 判断本地存在不存在，如果存在就返回继续下个循环，不存在继续往下走
			if (new File(localPath).exists()) {
				checkedImgPath.set(index, localPath);
				hasUpdate = true;
				return 0;
			}
		}
		return 0;
	}
}
