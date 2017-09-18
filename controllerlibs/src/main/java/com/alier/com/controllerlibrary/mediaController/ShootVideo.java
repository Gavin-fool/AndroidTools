package com.alier.com.controllerlibrary.mediaController;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.Config;
import com.alier.com.commons.album.MediaUtils;
import com.alier.com.commons.utils.LogMgr;
import com.alier.com.commons.utils.ObjectUtils;
import com.alier.com.commons.utils.ScreenUtils;
import com.alier.com.commons.utils.WidgetUtils;
import com.alier.com.controllerlibrary.R;


/**
 * @Title:ShootVideo
 * @description:视频录制工具类
 * @author:gavin_fool
 * @date:2017年4月25日下午5:10:18
 * @version:v1.0
 */
public class ShootVideo extends BaseActivity implements OnClickListener {
    private static final String TAG = "ShootVideo";
    private static final int CAMERA_ID = 0;
    private SurfaceHolder mSurfaceHolder;// 用于显示录像界面
    private ImageButton mShutter;// 开始录像按钮
    private ImageButton imgCancle;// 取消录像按钮
    private ImageButton imgEnsure;// 确定录像按钮
    private ImageButton imgPlayVideo;// 播放录像按钮
    private TextView mMinutePrefix;// 显示录像时间
    private TextView mMinuteText;// 显示录像时间
    private TextView mSecondPrefix;// 显示录像时间
    private TextView mSecondText;// 显示录像时间
    private Camera mCamera;// 相机实体变量
    private MediaRecorder mRecorder;// 媒体录制实体
    private boolean mIsRecording = false;// 是否正在录制
    private boolean mIsSufaceCreated = false;// surface是否被创建
    private Handler mHandler = new Handler();
    private String currentVideoPath = null;// 记录当前拍摄视频的位置
    private Intent intent;

    @Override
    public void init() {
        setContentView(R.layout.shoot_video);
        initUI();
        currentVideoPath = getIntent().getStringExtra(MediaUtils.VIDEO_PATH);
        if (ObjectUtils.isEmpty(currentVideoPath)) {
            currentVideoPath = Config.MEDIA_PATH + File.separator + "VID_"
                    + new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date()) + "_"
                    + WidgetUtils.generateRandomNum(6) + ".mp4";
        }
        intent = getIntent();
    }

    private void initUI() {
        SurfaceView mCameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        mMinutePrefix = (TextView) findViewById(R.id.timestamp_minute_prefix);
        mMinuteText = (TextView) findViewById(R.id.timestamp_minute_text);
        mSecondPrefix = (TextView) findViewById(R.id.timestamp_second_prefix);
        mSecondText = (TextView) findViewById(R.id.timestamp_second_text);
        mSurfaceHolder = mCameraPreview.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mShutter = (ImageButton) findViewById(R.id.record_shutter);
        mShutter.setOnClickListener(this);
        imgCancle = (ImageButton) findViewById(R.id.cancel);
        imgCancle.setOnClickListener(this);
        imgEnsure = (ImageButton) findViewById(R.id.ensure);
        imgEnsure.setOnClickListener(this);
        imgPlayVideo = (ImageButton) findViewById(R.id.img_play);
        imgPlayVideo.setOnClickListener(this);
    }

    @Override
    public void exec() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record_shutter) {// 拍照按钮
            if (mIsRecording) {// 正在录像则停止录像
                stopRecording();
            } else {
                initMediaRecorder();
                startRecording();
            }
        } else if (v.getId() == R.id.cancel) {
            mShutter.setVisibility(View.VISIBLE);
            imgCancle.setVisibility(View.GONE);
            imgEnsure.setVisibility(View.GONE);
            imgPlayVideo.setVisibility(View.GONE);
            mMinutePrefix.setVisibility(View.VISIBLE);
            mMinuteText.setText("0");
            mSecondPrefix.setVisibility(View.VISIBLE);
            mSecondText.setText("0");
        } else if (v.getId() == R.id.ensure) {
            intent.putExtra(MediaUtils.VIDEO_PATH, currentVideoPath);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.img_play) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(currentVideoPath), "video/mp4");
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsRecording) {
            stopRecording();
        }
        stopPreview();
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mIsSufaceCreated = false;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mIsSufaceCreated = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            startPreview();
        }
    };

    // 启动预览,开启相机，设置相机预览参数
    private void startPreview() {
        // 保证只有一个Camera对象
        if (mCamera != null || !mIsSufaceCreated) {
            return;
        }
        mCamera = Camera.open(CAMERA_ID);
        Parameters parameters = mCamera.getParameters();
        Size size = getBestPreviewSize(ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this), parameters);
        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
        }
        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.setPreviewFrameRate(30);
        // 设置相机预览方向
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (Exception e) {
            LogMgr.error(TAG, "给相机设置预览视图失败" + e.toString());
        }
        mCamera.startPreview();
    }

    // 关闭预览界面
    private void stopPreview() {
        // 释放Camera对象
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(null);
            } catch (Exception e) {
                LogMgr.error(TAG, "取消相机预览图失败" + e.toString());
            }
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Size getBestPreviewSize(int width, int height, Parameters parameters) {
        Size result = null;

        for (Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width > width || size.height > height)
                continue;
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;
                if (newArea > resultArea) {
                    result = size;
                }
            }
        }
        return result;
    }

    private MediaRecorder getMeidaRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        return mRecorder;
    }

    /**
     * @Description:初始化录制参数
     */
    private void initMediaRecorder() {
        mRecorder = getMeidaRecorder();// 实例化
        mCamera.unlock();
        // 给Recorder设置Camera对象，保证录像跟预览的方向保持一致
        mRecorder.setCamera(mCamera);
        mRecorder.setOrientationHint(90); // 改变保存后的视频文件播放时是否横屏(不加这句，视频文件播放的时候角度是反的)
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 设置从麦克风采集声音
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 设置从摄像头采集图像
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 设置视频的输出格式为MP4
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); // 设置音频的编码格式
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); // 设置视频的编码格式
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
        mRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight); // 设置视频大小
        mRecorder.setVideoFrameRate(profile.videoFrameRate); // 设置帧率
        mRecorder.setVideoEncodingBitRate(2000000);// 设置码率，决定存储视频的大小
        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mRecorder.setOutputFile(currentVideoPath);
    }

    /**
     * 开始录制
     */
    private void startRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.prepare();// 准备
                mRecorder.start();// 开始
            } catch (Exception e) {
                mIsRecording = false;
                LogMgr.error(TAG, "启动相机失败" + e.toString());
            }
        }
        mShutter.setImageDrawable(getResources().getDrawable(R.drawable.bg_count_new_event2));
        mShutter.setEnabled(false);
        mIsRecording = true;
        // 开始录像后，每隔1s去更新录像的时间戳
        mHandler.postDelayed(mTimestampRunnable, 1000);
    }

    /**
     * 停止录制
     */
    private void stopRecording() {
        if (mCamera != null) {
            mCamera.lock();
        }
        if (mRecorder != null) {
            try {
                mRecorder.stop();// 停止
                mRecorder.release();// 释放
                mRecorder = null;
            } catch (Exception e) {
                LogMgr.error(TAG, "停止录制异常" + e.toString());
            }
        }
        mShutter.setImageDrawable(getResources().getDrawable(R.drawable.bg_count_new_event));
        mShutter.setVisibility(View.GONE);
        mIsRecording = false;
        mHandler.removeCallbacks(mTimestampRunnable);
        imgCancle.setVisibility(View.VISIBLE);
        imgEnsure.setVisibility(View.VISIBLE);
        imgPlayVideo.setVisibility(View.VISIBLE);
    }

    private Runnable mTimestampRunnable = new Runnable() {
        @Override
        public void run() {
            int second = Integer.parseInt(mSecondText.getText().toString());
            int minute = Integer.parseInt(mMinuteText.getText().toString());
            second++;
            if (second <= 1) {
                mShutter.setEnabled(false);
            } else {
                mShutter.setEnabled(true);
            }
            if (second < 10) {
                mSecondText.setText(String.valueOf(second));
            } else if (second >= 10 && second < 60) {
                mSecondPrefix.setVisibility(View.GONE);
                mSecondText.setText(String.valueOf(second));
            } else if (second >= 60) {
                mSecondPrefix.setVisibility(View.VISIBLE);
                mSecondText.setText("0");
                minute++;
                mMinuteText.setText(String.valueOf(minute));
            } else if (minute >= 60) {
                mMinutePrefix.setVisibility(View.GONE);
            }
            if (second >= 30 && mIsRecording) {
                stopRecording();
                return;
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onBackPressed() {
        if (mIsRecording) {
            stopRecording();
        }
        currentVideoPath = null;
        intent.putExtra(MediaUtils.VIDEO_PATH, currentVideoPath);
        setResult(RESULT_OK, intent);
        finish();
    }
}