package com.alier.com.controllerlibrary.ui_photo_album;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.album.Bimp;
import com.alier.com.commons.album.MediaChooseHelper;
import com.alier.com.commons.album.MediaItem;
import com.alier.com.commons.album.MediaUtils;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.R;

/**
 * @Title:MediaChooseActivity
 * @description:这个是进入相册显示所有图片的界面
 * @author:gavin_fool
 * @date:2017年4月12日上午10:10:47
 * @version:v1.0
 */
public class MediaChooseActivity extends BaseActivity implements View.OnClickListener {
    // gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    // 完成按钮
    private Button okButton;
    private Intent intent;
    // 预览按钮
    private Button preview;
    /**
     * 对应所有的图片列表
     */
    private List<MediaItem> imageList;

    @Override
    public void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plugin_camera_album);
        BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
        initView();
        initListener();
        // 这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }

    @Override
    public void exec() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        gridImageAdapter.notifyDataSetChanged();
    }

    // 初始化，给一些对象赋值
    private void initView() {
        // 初始化MediaChooseHepler对象
        MediaChooseHelper helper = MediaChooseHelper.getHelper();
        helper.init(getApplicationContext());
        imageList = helper.getMediaList();
        ArrayList<String> mediaPaths = getIntent().getStringArrayListExtra(MediaUtils.MEDIA_PATHS);
        MediaUtils.num = getIntent().getIntExtra(MediaUtils.MAX_PIC_NUM, 6);
        Bimp.tempSelectBitmap.clear();
        Bimp.selectBitmapSize = 0;
        Bimp.selectVideoSize = 0;
        MediaItem item;
        if (null != mediaPaths) {
            for (int i = 0; i < mediaPaths.size(); i++) {
                if ("mp4".equals(mediaPaths.get(i).substring(mediaPaths.get(i).lastIndexOf(".") + 1))
                        || "3gp".equals(mediaPaths.get(i).substring(mediaPaths.get(i).lastIndexOf(".") + 1))) {
                    item = new MediaItem();
                    item.setMediaPath(mediaPaths.get(i));
                    item.setMediaType(MediaItem.VIDEO);
                    Bimp.tempSelectBitmap.add(item);
                    Bimp.selectVideoSize++;
                } else {
                    item = new MediaItem();
                    item.setMediaPath(mediaPaths.get(i));
                    item.setMediaType(MediaItem.IMAGE);
                    Bimp.tempSelectBitmap.add(item);
                    Bimp.selectBitmapSize++;
                }
            }
        }
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        Button btCancel = (Button) findViewById(R.id.cancel);
        btCancel.setOnClickListener(this);
        preview = (Button) findViewById(R.id.preview);
        preview.setOnClickListener(this);
        intent = getIntent();
        GridView gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this, imageList, Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        gridView.setEmptyView((TextView) findViewById(R.id.myText));
        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(this);
        okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + MediaUtils.num + ")");
    }

    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, Button chooseBt) {
                if (imageList.get(position).getMediaType() == MediaItem.IMAGE) {// 选择照片时
                    if (Bimp.selectBitmapSize >= MediaUtils.num) {// 判断最大的图片选择个数
                        toggleButton.setChecked(false);
                        chooseBt.setVisibility(View.GONE);
                        if (!removeOneData(imageList.get(position))) {
                            T.showShort(MediaChooseActivity.this, getString(R.string.only_choose_num));
                        }
                        return;
                    }
                    if (isChecked) {// 如果选择图片
                        chooseBt.setVisibility(View.VISIBLE);
                        // 添加已选图片的个数
                        Bimp.tempSelectBitmap.add(imageList.get(position));
                        Bimp.selectBitmapSize++;
                        okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + MediaUtils.num + ")");
                    } else {// 如果取消图片
                        removeOneData(imageList.get(position));
                        chooseBt.setVisibility(View.GONE);
                    }
                } else {// 选择视频时
                    if (Bimp.selectVideoSize >= 1) {// 判断最大的图片选择个数
                        toggleButton.setChecked(false);
                        chooseBt.setVisibility(View.GONE);
                        if (!removeOneData(imageList.get(position))) {
                            T.showShort(MediaChooseActivity.this, getString(R.string.video_chooose_num));
                        }
                        return;
                    }
                    if (isChecked) {// 如果选择图片
                        chooseBt.setVisibility(View.VISIBLE);
                        // 添加已选图片的个数
                        Bimp.tempSelectBitmap.add(imageList.get(position));
                        Bimp.selectVideoSize++;
                        okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + MediaUtils.num + ")");
                    } else {// 如果取消图片
                        removeOneData(imageList.get(position));
                        chooseBt.setVisibility(View.GONE);
                    }
                }
                isShowOkBt();
            }
        });

    }

    /**
     * @param imageItem
     * @param @return
     * @Description:移除一个已选媒体
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
                okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + MediaUtils.num + ")");
                return true;
            }
        }
        return false;
    }

    // 设置按钮的状态
    private void isShowOkBt() {
        if (!Bimp.tempSelectBitmap.isEmpty()) {
            okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + MediaUtils.num + ")");
            preview.setPressed(true);
            okButton.setPressed(true);
            preview.setClickable(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
            preview.setTextColor(Color.WHITE);
        } else {
            okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + MediaUtils.num + ")");
            preview.setPressed(false);
            preview.setClickable(false);
            okButton.setPressed(false);
            okButton.setClickable(false);
            okButton.setTextColor(Color.parseColor("#E1E0DE"));
            preview.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }

    // 返回到主界面 传递数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancle();
            finish();
        }
        return false;
    }

    // 点击取消 或者 后退时 调用此方法
    private void cancle() {
        Bimp.tempSelectBitmap.clear();
        Bimp.selectBitmapSize = 0;
        Bimp.selectVideoSize = 0;
        ArrayList<String> paths = processImageItemToImagePath();
        intent.putStringArrayListExtra(MediaUtils.MEDIA_PATHS, paths);
        setResult(RESULT_OK, intent);
    }

    /**
     * @param @return imagePaths
     * @Title: processImageItemToImagePath
     * @auther:fengao
     * @Description: 将ImageItem转化为ImagePath
     */
    private ArrayList<String> processImageItemToImagePath() {
        ArrayList<String> imagePaths = new ArrayList<String>();
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            imagePaths.add(Bimp.tempSelectBitmap.get(i).getMediaPath());
        }
        return imagePaths;
    }

    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.preview) {// 预览图
            if (!Bimp.tempSelectBitmap.isEmpty()) {
                intent.setClass(MediaChooseActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.cancel) {// 取消按钮
            cancle();
            finish();
        } else if (v.getId() == R.id.back) {// 返回按钮
            intent.setClass(MediaChooseActivity.this, MediaFolderActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.ok_button) {
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            ArrayList<String> paths = processImageItemToImagePath();
            intent.putStringArrayListExtra(MediaUtils.MEDIA_PATHS, paths);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
