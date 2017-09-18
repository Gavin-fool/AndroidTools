package com.alier.com.controllerlibrary.mediaController;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alier.com.commons.Config;
import com.alier.com.commons.album.MediaUtils;
import com.alier.com.commons.entity.CHOOSEMEDIATYPE;
import com.alier.com.commons.entity.ListSerializable;
import com.alier.com.commons.entity.MEDIATYPE;
import com.alier.com.commons.entity.MediaInfo;
import com.alier.com.commons.utils.CameraUtil;
import com.alier.com.commons.utils.DeviceInformation;
import com.alier.com.commons.utils.FileUtils;
import com.alier.com.commons.utils.ImageHelper;
import com.alier.com.commons.utils.LogMgr;
import com.alier.com.commons.utils.ObjectUtils;
import com.alier.com.commons.utils.T;
import com.alier.com.commons.utils.WidgetUtils;
import com.alier.com.controllerlibrary.custom_small.CircleImageView;
import com.alier.com.controllerlibrary.R;
import com.alier.com.controllerlibrary.ui_photo_album.MediaChooseActivity;
import com.squareup.picasso.Picasso;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * 自定义多媒体控件,通过该控件四个按钮分别进入媒体选择、拍照、拍摄视频等录音功能。其中媒体选择、拍照、
 * 拍摄返回的结果通过调用该多媒体控件的activity的onActivityResult接收。再通过媒体控件提供的set**()
 * 方法将返回的参数在媒体控件中显示。最后通过媒体控件提供的回调接口将所有选择的媒体信息返回给调用者
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017年4月6日 上午11:20:01
 */
public class MediaController extends LinearLayout {
    private static final String TAG = "MediaController";
    /**
     * 声明多媒体控件中需要用到的控件
     */
    private LinearLayout llShowMediaThumb;// 用于装载显示缩略图的布局
    private PopupWindow popupWindow;// 添加媒体底部弹出框
    private SpeakControl mSpeakControl = null;// 声音控制
    /**
     * 自定义属性值 ,从attrs中获取并赋值
     */
    private boolean isOnlyShow = false;// 是选择媒体还是展示媒体，控制整个控件是用来做多媒体选择功能，还是接受已存在媒体从来展示（例如历史记录中只做展示）。True:忽略其他自定义属性，翻页浏览图片，直接播放视频、音频
    // False：根据其他自定义属性再控制，默认为false
    private int chooseMediaType;// 选择媒体类型
    private boolean isTakePhoto = true;// 是否展示拍照功能。True：打开相机拍照功能；默认为true
    // false：屏蔽相机拍照功能
    private boolean isShootVideo = true;// 是否打开拍摄视频功能。True：打开拍摄视频功能；默认为true
    // false：屏蔽相机拍摄视频功能
    private boolean isShowAudio = true;// 是否打开录音功能。True：打开录音功能；默认为true
    // false：屏蔽录音功能
    private boolean addWatermark = true;// 是否需要给选择的图片添加时间水印。True：添加（默认为true）；false：不添加
    private int maxPhotoNum = 6;// 最大选择图片数量，如果不设置，默认为6
    /**
     * 数据参数
     */
    private String strImgPath = Config.MEDIA_PATH + "/pic";// 手机端图片存放路径
    private List<String> mediaPaths = null;// 媒体选择路径集合，包含图片与视频
    private ArrayList<String> imagePaths = null;// 图片选择的图片路径集合,如果没有选择图片则为null
    private ArrayList<String> checkedImgPath = null;// 要上报的图片集
    private String videoPath = null;// 视频选择路径，如果没有选择视频则为null
    private String audioPath = null;// 音频选择路径，如果没有选择音频则为null
    private String curTakePhotoPath = null;// 当前拍照的路径
    private Context controllerContext;// 控件context
    private Context mContext;// 调用该控件activity的context
    // 工具参数
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式
    private IMediaControllerCallback controllerCallback;
    private MediaControllerShowMedia mediaControllerShowMedia = null;
    private final int cicleViewSize = 60;// 添加控件的大小
    private final int cicleViewMargin = 10;// 添加控件的大小
    private CircleImageView btAddMedia;

    /**
     * @Title:IMediaControllerListener
     * @description:多媒体控件回调监听
     * @author:gavin_fool
     * @date:2017年4月12日下午4:44:15
     * @version:v1.0
     */
    public interface IMediaControllerCallback {
        /**
         * @Description:获取被选择的图片
         */
        void getCheckedImgPath(List<String> checkedImgPath);

        /**
         * @Description:获取被选择的视频
         */
        void getVideoPath(String videoPath);

        /**
         * @Description:获取被选择的录音
         */
        void getAudioPath(String audioPath);
    }

    /**
     * @param ControllerCallback
     * @Description:设置控件回调接口
     */
    public void setControllerListener(IMediaControllerCallback ControllerCallback) {
        this.controllerCallback = ControllerCallback;
    }

    /**
     * @param context
     * @Description:初始化媒体控件参数
     */
    public void initMediaController(Context context) {
        this.mContext = context;
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.controllerContext = context;
        initAttrs(context, attrs);
        initChildController(context);
    }

    /**
     * @Description:设置图片路径集合，并显示选择图片缩略图
     */
    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
        disposeChooseImage();
    }

    /**
     * @param @return
     * @Description:获取多媒体集合
     * @return:List<String>
     */
    public List<String> getMediaPaths() {
        return mediaPaths;
    }

    /**
     * @param mediaPaths
     * @Description:设置媒体集合
     */
    public void setMediaPaths(List<String> mediaPaths) {
        this.mediaPaths = mediaPaths;
    }

    /**
     * @Description:获取当前拍照图片路径
     * @return:String 当前拍照图片路径
     */
    public String getCurTakePhotoPath() {
        return curTakePhotoPath;
    }

    /**
     * @Description:获取视频选择路径
     * @return:String
     */
    public String getVideoPath() {
        return videoPath;
    }

    /**
     * @param @param videoPath
     * @Description:设置视频路径，并显示选择视频缩略图
     */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        showChooseMedia();
    }

    /**
     * @Description:获取音频选择路径
     * @author:fuguang
     * @return:String
     */
    public String getAudioPath() {
        return audioPath;
    }

    /**
     * @param audioPath
     * @Description:设置音频路径，并显示选择音频缩略图
     * @author:fuguang
     * @return:void
     */
    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
        showChooseMedia();
    }

    /**
     * @param images
     * @Description:加载网络图片，显示在多媒体控件中
     */
    public void loadImg(List<MediaInfo> images) {
        mediaControllerShowMedia.loadImageOnThumb(images, controllerContext, llShowMediaThumb);
    }

    /**
     * @param videoInfo
     * @Description:加载网络视频，缩略图显示在多媒体控件中，点击缩略图下载视频
     */
    public void loadVideo(MediaInfo videoInfo) {
        mediaControllerShowMedia.loadVideoOnThumb(videoInfo, controllerContext, llShowMediaThumb);
    }

    /**
     * @param audioInfo
     * @Description:加载网络音频，缩略图显示在多媒体控件中，点击缩略图下载音频
     */
    public void loadAudio(MediaInfo audioInfo) {
        mediaControllerShowMedia.loadAudioOnThumb(audioInfo, controllerContext, llShowMediaThumb);
    }

    /**
     * onActivityResult方法获取到返回值后调用该方法进行填充数据，resultCode == RESULT_OK时才有效
     *
     * @param requestCode 请求码
     * @param data        onActivityResult方法返回的 Intent
     */
    public void loadReusltDataFromActivity(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1)
            return;
        if (requestCode == CameraUtil.RESULT_ALBUM_IMAGE && data != null) {// 选择照片处理
            if (null != imagePaths) {
                imagePaths.clear();
            } else {
                imagePaths = new ArrayList<String>();
            }
            ArrayList<String> chooseImagePaths = new ArrayList<String>();
            String strVideoPath = null;
            List<String> chooseMediaPaths = data.getStringArrayListExtra(MediaUtils.MEDIA_PATHS);
            for (int i = 0; i < chooseMediaPaths.size(); i++) {
                if ("mp4".equals(chooseMediaPaths.get(i).substring(chooseMediaPaths.get(i).lastIndexOf(".") + 1))
                        || "3gp".equals(chooseMediaPaths.get(i).substring(chooseMediaPaths.get(i).lastIndexOf(".") + 1))) {
                    strVideoPath = chooseMediaPaths.get(i);
                } else {
                    chooseImagePaths.add(chooseMediaPaths.get(i));
                }
            }
            setMediaPaths(chooseMediaPaths);
            setVideoPath(strVideoPath);
            setImagePaths(chooseImagePaths);
        } else if (requestCode == CameraUtil.RESULT_CAPTURE_IMAGE) {// 拍照返回
            String takephotoPath = getCurTakePhotoPath();
            File out = new File(takephotoPath);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(out)));
            if (null == imagePaths) {
                imagePaths = new ArrayList<String>();
            }
            imagePaths.add(takephotoPath);
            if (null == mediaPaths) {
                mediaPaths = new ArrayList<String>();
            }
            mediaPaths.add(takephotoPath);
            setMediaPaths(mediaPaths);
            setImagePaths(imagePaths);
        } else if (requestCode == CameraUtil.RESULT_CODE_TAKE_VIDEO && data != null) {// 拍摄返回
            String videoPath = data.getStringExtra(MediaUtils.VIDEO_PATH);
            if (null == mediaPaths) {
                mediaPaths = new ArrayList<String>();
            }
            mediaPaths.add(videoPath);
            setMediaPaths(mediaPaths);
            setVideoPath(videoPath);
        }
    }

    /**
     * 更新多媒体的图片，这个地方只支持更新图片
     *
     * @param notifyMedias 要更新的图片的多媒体的集合
     * @return true:更新成功 ，false: 更新失败
     */
    public boolean notifyMediaInfo(List<MediaInfo> notifyMedias) {
        return mediaControllerShowMedia.notifyImageOnThumb(notifyMedias, controllerContext, llShowMediaThumb);
    }

    /**
     * 更新多媒体的图片，这个地方只支持更新图片
     *
     * @param notifyMedia 要更新的图片的多媒体
     * @return true:更新成功 ，false: 更新失败
     */
    public boolean notifyMediaInfo(MediaInfo notifyMedia) {
        return mediaControllerShowMedia.notifyImageOnThumb(notifyMedia, controllerContext, llShowMediaThumb);
    }

    /**
     * @Description:处理被选择的图片，将图片拷贝到城管通相册中
     */
    private void disposeChooseImage() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (null == checkedImgPath) {
                    checkedImgPath = new ArrayList<String>();
                }
                checkedImgPath.clear();
                if (imagePaths == null || imagePaths.isEmpty()) {
                    imageListHandler.sendEmptyMessage(0);
                    return;
                }
                // 对图片进行压缩、添加水印并存储到应用程序相册目录
                for (int i = 0; i < imagePaths.size(); i++) {
                    // 获取图片地址
                    String pic = imagePaths.get(i);
                    String fileName = pic.substring(pic.lastIndexOf("/"));
                    File file = new File(strImgPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (new File(strImgPath + fileName).exists()) {
                        checkedImgPath.add(strImgPath + fileName);
                        continue;
                    }
                    Bitmap bitmap = null;
                    try {
                        // 系统相册图片，拷贝到应用程序相册目录
                        FileUtils.copyFile(new File(pic), new File(strImgPath + fileName));
                        FileUtils.photoHandler(strImgPath + fileName, 800);
                        // 添加水印
                        if (addWatermark) {
                            Date createDate = new Date();
                            String strCreateDate = df.format(createDate);
                            bitmap = ImageHelper.getLoacalBitmap(strImgPath + fileName);
                            bitmap = ImageHelper.addWatermarkBitmap(bitmap, strCreateDate);
                            ImageHelper.saveFile(bitmap, strImgPath + fileName);
                        }
                        checkedImgPath.add(strImgPath + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogMgr.error(TAG, e.toString());
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        LogMgr.error(TAG, "选取图片返回到上报界面出错了：" + "内存不足，超出内存");
                        imageListHandler.sendEmptyMessage(1);
                        System.gc();
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                }
                imageListHandler.sendEmptyMessage(0);
            }
        };
        thread.start();
    }

    /**
     * 主要是因为从相册返回后如果重新处理图片时从造成程序无法响应错误
     */
    Handler imageListHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 选择图片成功
                    showChooseMedia();
                    controllerCallback.getCheckedImgPath(checkedImgPath);
                    break;
                case 1:// 异常情况
                    T.showShort(mContext, "内存不足,请清理内存后重新登录");
                    break;
            }
            return false;
        }

    });

    /**
     * @Description:处理已选图片长按事件
     */
    private void disposeImgLongClick(final View img, final MEDIATYPE mediaType) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("确定删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        {
                            String strImg = img.getTag().toString();
                            if (mediaType == MEDIATYPE.IMAGE) {
                                Iterator iterator = checkedImgPath.iterator();
                                while (iterator.hasNext()) {
                                    // 获取图片地址
                                    String pic = (String) iterator.next();
                                    if (pic.equals(strImg)) {
                                        iterator.remove();
                                    }
                                }
                                controllerCallback.getCheckedImgPath(checkedImgPath);
                            }
                            if (mediaType == MEDIATYPE.VEDIO) {
                                videoPath = null;
                                controllerCallback.getVideoPath(videoPath);
                            }
                            if (mediaType == MEDIATYPE.SOUND) {
                                audioPath = null;
                                controllerCallback.getAudioPath(audioPath);
                            }
                            deleteMediaPath(strImg);
                            llShowMediaThumb.removeView(img);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();

    }

    /**
     * @param path 被删除的文件路径
     * @Description:删除mediaPaths中的同名媒体
     */
    private void deleteMediaPath(String path) {
        if (mediaPaths == null || mediaPaths.isEmpty()) {
            return;
        }
        String pathName = path.substring(path.lastIndexOf("/") + 1);
        for (int i = 0; i < mediaPaths.size(); i++) {
            String name = mediaPaths.get(i).substring(mediaPaths.get(i).lastIndexOf("/") + 1);
            if (name.equals(pathName)) {
                mediaPaths.remove(i);
            }
        }
        for (int i = 0; i < imagePaths.size(); i++) {
            String name = imagePaths.get(i).substring(imagePaths.get(i).lastIndexOf("/") + 1);
            if (name.equals(pathName)) {
                imagePaths.remove(i);
                return;
            }
        }
    }

    /**
     * 点击查看图片
     *
     * @param currPath
     */
    private void imgOnClick(String currPath) {
        Intent intent = new Intent();
        intent.setClass(mContext, DynViewPager.class);
        ListSerializable serializable = new ListSerializable();
        serializable.setList(checkedImgPath);
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", serializable);
        bundle.putString("currPath", currPath);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    /**
     * @Description:显示选择图片到界面
     */
    private void showCheckedImageOnThumb() {
        if (null == checkedImgPath || checkedImgPath.size() <= 0)
            return;
        LayoutParams layoutParams = new LayoutParams(getPixelsFromDp(controllerContext, cicleViewSize), getPixelsFromDp(controllerContext, cicleViewSize));
        layoutParams.setMargins(0, 0, getPixelsFromDp(controllerContext, cicleViewMargin), 0);
        // 迭代显示图片到水平滚动栏中
        for (int i = 0; i < checkedImgPath.size(); i++) {
            // 声明一个IMG组件
            CircleImageView img = (CircleImageView) LayoutInflater.from(mContext).inflate(R.layout.circleimage, null);
            img.setScaleType(ScaleType.CENTER_CROP);
            // 获取图片地址
            final String pic = checkedImgPath.get(i);
            // 如果文件存在，则赋值给Img控件
            if (new File(pic).exists()) {
                // 图片压缩处理
                Picasso.with(controllerContext).load(new File(pic)).into(img);
                img.setTag(pic);
                if (!isOnlyShow) {
                    img.setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(final View v) {
                            disposeImgLongClick(v, MEDIATYPE.IMAGE);
                            return false;
                        }
                    });
                }
                img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgOnClick(pic);
                    }
                });
            } else {
                Picasso.with(controllerContext).load(R.drawable.no_exists_pic).into(img);
            }
            // 将Img控件添加到水平滚动栏中
            llShowMediaThumb.addView(img, layoutParams);
        }
    }

    /**
     * @Description:显示视频缩略图到界面
     */
    private void showCheckedVideoOnThumb() {
        if (ObjectUtils.isEmpty(videoPath))
            return;
        CircleImageView videoImg = (CircleImageView) LayoutInflater.from(mContext).inflate(R.layout.circleimage, null);
        LayoutParams params = new LayoutParams(getPixelsFromDp(controllerContext, cicleViewSize), getPixelsFromDp(controllerContext, cicleViewSize));
        params.setMargins(0, 0, getPixelsFromDp(controllerContext, cicleViewMargin), 0);
        videoImg.setImageResource(R.drawable.video);
        // 如果文件存在，则赋值给Img控件
        if (new File(videoPath).exists()) {
            videoImg.setTag(videoPath);
            if (!isOnlyShow) {
                videoImg.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(final View v) {
                        disposeImgLongClick(v, MEDIATYPE.VEDIO);
                        return false;
                    }
                });
            }
            videoImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(videoPath)), "video/*");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("oneshot", 0);
                    intent.putExtra("configchange", 0);
                    mContext.startActivity(intent);
                }
            });
        } else {
            videoImg.setImageResource(R.drawable.no_exists_pic);
            videoPath = null;
        }
        // 将Img控件添加到水平滚动栏中
        llShowMediaThumb.addView(videoImg, params);
        controllerCallback.getVideoPath(videoPath);
    }

    /**
     * @Description:显示音频缩略图到界面
     */
    private void showCheckedAudioOnThumb() {
        if (ObjectUtils.isEmpty(audioPath))
            return;
        CircleImageView audioImg = (CircleImageView) LayoutInflater.from(mContext).inflate(R.layout.circleimage, null);
        audioImg.setImageResource(R.drawable.audio);
        // 如果文件存在，则赋值给audioImg控件
        if (new File(audioPath).exists()) {
            audioImg.setTag(audioPath);
            if (!isOnlyShow) {
                audioImg.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(final View v) {
                        disposeImgLongClick(v, MEDIATYPE.SOUND);
                        return false;
                    }
                });
            }
            audioImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == mSpeakControl) {
                        mSpeakControl = new SpeakControl((Activity) mContext, Config.MEDIA_PATH + "/videoData/", audioPath);
                    }
                    mSpeakControl.speakDialogShow();
                    mSpeakControl.play();
                }
            });
        } else {
            audioImg.setImageResource(R.drawable.no_exists_pic);
            audioPath = null;
        }
        // 将audioImg控件添加到水平滚动栏中
        LayoutParams params = new LayoutParams(getPixelsFromDp(controllerContext, cicleViewSize), getPixelsFromDp(controllerContext, cicleViewSize));
        params.setMargins(0, 0, getPixelsFromDp(controllerContext, cicleViewMargin), 0);
        llShowMediaThumb.addView(audioImg, params);
        controllerCallback.getAudioPath(audioPath);
    }

    /**
     * @Description:显示选择的媒体
     */
    private void showChooseMedia() {
        if ((imagePaths == null || imagePaths.isEmpty()) && ObjectUtils.isEmpty(videoPath) && ObjectUtils.isEmpty(audioPath)) {
            if (null != llShowMediaThumb) {
                llShowMediaThumb.removeAllViews();
            }
            return;
        }
        llShowMediaThumb.removeAllViews();
        showCheckedImageOnThumb();
        showCheckedVideoOnThumb();
        showCheckedAudioOnThumb();
    }

    /**
     * @param context
     * @param attrs
     * @Description:获取自定义属性值
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MediaController);
        try {
            isOnlyShow = typedArray.getBoolean(R.styleable.MediaController_isOnlyShow, false);
            chooseMediaType = typedArray.getInt(R.styleable.MediaController_chooseMediaType, 1);
            isTakePhoto = typedArray.getBoolean(R.styleable.MediaController_isTakePhoto, true);
            isShootVideo = typedArray.getBoolean(R.styleable.MediaController_isShootVideo, true);
            isShowAudio = typedArray.getBoolean(R.styleable.MediaController_isShowAudio, true);
            addWatermark = typedArray.getBoolean(R.styleable.MediaController_addWatermark, true);
            maxPhotoNum = typedArray.getInteger(R.styleable.MediaController_maxPhotoNum, 6);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * @param context
     * @Description:初始化子控件
     */
    private void initChildController(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.mediacontroller_control, this, true);
        llShowMediaThumb = (LinearLayout) view.findViewById(R.id.ll_media_show);
        // CircleImageView btAddMedia = (CircleImageView)
        // view.findViewById(R.id.bt_add_media);
        btAddMedia = (CircleImageView) view.findViewById(R.id.bt_add_media);
        if (isOnlyShow) {
            btAddMedia.setVisibility(View.GONE);
        }
        btAddMedia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupWindow(v);
            }
        });
        mediaControllerShowMedia = new MediaControllerShowMedia(controllerContext);
    }

    /**
     * @param flag
     * @Description 设置isOnlyShow
     * @user fengao
     */
    public void setIsOnlyShow(boolean flag) {
        isOnlyShow = flag;
        if (isOnlyShow) {
            btAddMedia.setVisibility(View.GONE);
        }
        showChooseMedia();
    }

    private void openPopupWindow(View v) {
        // 防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        // 设置PopupWindow的View
        View view = LayoutInflater.from(mContext).inflate(R.layout.choose_media_popup, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 设置背景,这个没什么效果，不添加会报错
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击弹窗外隐藏自身
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        // 设置位置
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        // 设置消失监听
        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                setBackgroundAlpha(1);
            }
        });
        // 设置PopupWindow的View点击事件
        setOnPopupViewClick(view);
        // 设置背景色
        setBackgroundAlpha(0.5f);
    }

    // 设置屏幕背景透明效果
    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = alpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    /**
     * @param view
     * @Description:点击弹出框具体选项
     */
    private void setOnPopupViewClick(View view) {
        TextView tvChooseMedia = (TextView) view.findViewById(R.id.tv_choose_media);
        if (chooseMediaType != CHOOSEMEDIATYPE.NONE.getValue()) {
            tvChooseMedia.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, MediaChooseActivity.class);
                    if (mediaPaths == null) {
                        mediaPaths = new ArrayList<String>();
                    }
                    intent.putStringArrayListExtra(MediaUtils.MEDIA_PATHS, (ArrayList<String>) mediaPaths);
                    intent.putExtra(MediaUtils.MAX_PIC_NUM, maxPhotoNum);
                    ((Activity) mContext).startActivityForResult(intent, CameraUtil.RESULT_ALBUM_IMAGE);
                    popupWindow.dismiss();
                }
            });
        } else {
            tvChooseMedia.setVisibility(View.GONE);
        }

        TextView tvTakePhoto = (TextView) view.findViewById(R.id.tv_take_photo);
        if (isTakePhoto) {
            tvTakePhoto.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(EasyPermissions.hasPermissions(mContext,Manifest.permission.CAMERA)){
                        openCamera();
                    }else {
                        EasyPermissions.requestPermissions((Activity) mContext, "允许访问相机？", 123, Manifest.permission.CAMERA);
                    }
                }
            });
        } else {
            tvTakePhoto.setVisibility(View.GONE);
        }

        TextView tvShootVideo = (TextView) view.findViewById(R.id.tv_shooting);
        if (isShootVideo) {
            tvShootVideo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!ObjectUtils.isEmpty(videoPath)) {
                        T.showShort(mContext, "请先删除已选视频再拍摄");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(mContext, ShootVideo.class);
                    File file = new File(Config.MEDIA_PATH + File.separator + "video");
                    if (!file.exists()) {
                        // 多级文件夹的创建
                        file.mkdirs();
                    }
                    videoPath = file.getPath() + File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date()) + "_" + WidgetUtils.generateRandomNum(6) + ".mp4";
                    intent.putExtra(MediaUtils.VIDEO_PATH, videoPath);
                    ((Activity) mContext).startActivityForResult(intent, CameraUtil.RESULT_CODE_TAKE_VIDEO);
                    popupWindow.dismiss();
                }
            });
        } else {
            tvShootVideo.setVisibility(View.GONE);
        }

        TextView tvShowAudio = (TextView) view.findViewById(R.id.tv_recording);
        if (isShowAudio) {
            tvShowAudio.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSpeakControl = new SpeakControl((Activity) mContext, Config.MEDIA_PATH + File.separator + "videoData/", audioPath) {
                        @Override
                        public void onRecordCompletion(String path) {
                            audioPath = path;
                            showChooseMedia();
                        }

                        @Override
                        public void onCancelRecord() {
                            super.onCancelRecord();
                            audioPath = null;
                            showChooseMedia();
                        }
                    };
                    mSpeakControl.speakDialogShow();
                    popupWindow.dismiss();
                }
            });
        } else {
            tvShowAudio.setVisibility(View.GONE);
        }
        // 取消按钮
        TextView tvDismiss = (TextView) view.findViewById(R.id.tv_cancel);
        tvDismiss.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * @param @param  context
     * @param @param  size dp数量
     * @param @return
     * @Description:将dp转成像素单位
     */
    private int getPixelsFromDp(Context context, int size) {
        return (size * DeviceInformation.getScreenDensityDPI((Activity) context)) / DisplayMetrics.DENSITY_DEFAULT;
    }

    public void openCamera(){
        if (null != checkedImgPath && checkedImgPath.size() >= maxPhotoNum) {
            T.showShort(mContext, "已经达到最高选择数量");
            return;
        }
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date()) + "_" + WidgetUtils.generateRandomNum(6) + ".jpg";// 照片命名
        curTakePhotoPath = Config.MEDIA_PATH + File.separator + fileName;
        File out = new File(curTakePhotoPath);
        // 该照片的绝对路径
        Uri uri = Uri.fromFile(out);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);// 指定拍照保存路径
        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        ((Activity) mContext).startActivityForResult(imageCaptureIntent, CameraUtil.RESULT_CAPTURE_IMAGE);
        popupWindow.dismiss();
    }

}
