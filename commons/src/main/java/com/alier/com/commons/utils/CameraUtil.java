package com.alier.com.commons.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.alier.com.commons.Config;

public class CameraUtil {


    public static final int RESULT_CAPTURE_IMAGE = 1;// 照相的requestCode
    public static final int RESULT_CODE_TAKE_VIDEO = 2;// 摄像的照相的requestCode
    public static final int RESULT_CAPTURE_RECORDER_SOUND = 3;// 录音的requestCode
    public static final int RESULT_ALBUM_IMAGE = 8;//从相册选择照片的requestCode
    private String strImgPath = Config.MEDIA_PATH;// 照片文件绝对路径
    private Activity activity;

    public CameraUtil(Activity activity) {
        this.activity = activity;
    }

    /**
     * 拍摄照片
     */
    public void cameraMethod() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        strImgPath = Config.MEDIA_PATH;//存放照片的文件夹
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";//照片命名
        File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(strImgPath, fileName);
        strImgPath = strImgPath + fileName;//该照片的绝对路径
        Uri uri = Uri.fromFile(out);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        activity.startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);
    }

    /**
     * 拍摄录像
     */
    private void videoMethod() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        activity.startActivityForResult(intent, RESULT_CODE_TAKE_VIDEO);
    }

}
