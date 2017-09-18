package com.alier.com.controllerlibrary.ui_photo_album;


import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import com.alier.com.commons.BaseActivity;
import com.alier.com.controllerlibrary.R;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:06
 */
public class MediaFolderActivity extends BaseActivity {

	@Override
	public void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.plugin_camera_image_file);
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText(R.string.photo);
		FolderAdapter folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	@Override
	public void exec() {
		
	}
}
