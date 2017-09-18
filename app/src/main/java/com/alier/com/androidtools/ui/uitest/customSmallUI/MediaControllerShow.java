package com.alier.com.androidtools.ui.uitest.customSmallUI;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.alier.com.androidtools.R;
import com.alier.com.commons.BaseActivity;
import com.alier.com.controllerlibrary.mediaController.MediaController;

import java.util.List;

/**
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017/8/31 15:54
 * @email gavin_fool@163.com
 */
public class MediaControllerShow extends BaseActivity {
    private MediaController mediaController = null;
    @Override
    public void init() {
        setContentView(R.layout.activity_mediacontroller);
        // 测试多媒体控件
        mediaController = (MediaController) this.findViewById(R.id.mediacontroller);
        mediaController.initMediaController(this);
        mediaController.setControllerListener(new MediaController.IMediaControllerCallback() {

            @Override
            public void getVideoPath(String videoPath) {

            }

            @Override
            public void getCheckedImgPath(List<String> checkedImgPath) {

            }

            @Override
            public void getAudioPath(String audioPath) {

            }
        });
    }

    @Override
    public void exec() {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaController.loadReusltDataFromActivity(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123){
            mediaController.openCamera();
        }
    }
}
