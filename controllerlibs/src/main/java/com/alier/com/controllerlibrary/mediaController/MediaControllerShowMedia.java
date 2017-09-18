package com.alier.com.controllerlibrary.mediaController;

import java.io.File;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.alier.com.commons.Config;
import com.alier.com.commons.entity.MediaInfo;
import com.alier.com.commons.utils.DeviceInformation;
import com.alier.com.commons.utils.LogMgr;
import com.alier.com.commons.utils.ObjectUtils;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.custom_small.CircleImageView;
import com.alier.com.controllerlibrary.R;
import com.alier.com.controllerlibrary.ui_utils.Alert;
import com.squareup.picasso.Picasso;

/**
 * 媒体控件显示缩略图
 *
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017年5月23日 下午7:10:33
 */
public class MediaControllerShowMedia {
    private final int marginSize = 10;// 图片的间距
    private Context mContext;

    public MediaControllerShowMedia(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /**
     * @Description:加载网络缩略图
     */
    public void loadImageOnThumb(final List<MediaInfo> checkedImg, Context controllerContext,
                                 LinearLayout llShowMediaThumb) {
        if (null == checkedImg || checkedImg.isEmpty())
            return;

        // 迭代显示图片到水平滚动栏中
        for (int i = 0; i < checkedImg.size(); i++) {
            View thumbView = LayoutInflater.from(controllerContext).inflate(R.layout.thumb_item, null);
            CircleImageView img = (CircleImageView) thumbView.findViewById(R.id.img_thumb);
            TextView txDes = (TextView) thumbView.findViewById(R.id.tv_thumb_description);
            final MediaInfo mediaInfo = checkedImg.get(i);
            if (ObjectUtils.isEmpty(mediaInfo.getStrDiscript())) {
                txDes.setVisibility(View.GONE);
            } else {
                txDes.setVisibility(View.VISIBLE);
                txDes.setText(mediaInfo.getStrDiscript());
            }
            LayoutParams params = new LayoutParams(
                    (mediaInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                    (mediaInfo.getdY() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT);
            params.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
            img.setLayoutParams(params);
            byte[] bm = mediaInfo.getBmMedia();
            if (null != bm) {
                img.setImageBitmap(BitmapFactory.decodeByteArray(bm, 0, bm.length));
            } else {
                Picasso.with(controllerContext).load(R.drawable.no_exists_pic).into(img);
            }
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgOnClick(mContext, mediaInfo, checkedImg);
                }
            });
            // 将Img控件添加到水平滚动栏中
            LayoutParams layoutParams = new LayoutParams(
                    (mediaInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
            thumbView.setTag(mediaInfo.getUuid());
            llShowMediaThumb.addView(thumbView, layoutParams);
        }
    }

    /**
     * @param mContext
     * @param checkedImg
     * @Description:点击查看网络图片
     * @author:fuguang
     */
    private void imgOnClick(Context mContext, MediaInfo mediainfo, List<MediaInfo> checkedImg) {
//        ArrayList<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
//        mediaInfos.addAll(checkedImg);
//        Intent intent = new Intent();
//        intent.setClass(mContext, LoadWebImageView.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("images", mediaInfos);
//        bundle.putSerializable("curMediaInfo", mediainfo);
//        intent.putExtras(bundle);
//        mContext.startActivity(intent);
    }

    /**
     * @param videoInfo         需要加载视频信息
     * @param controllerContext
     * @param llShowMediaThumb
     * @Description:加载网络视频
     * @author:fuguang
     */
    public void loadVideoOnThumb(final MediaInfo videoInfo, Context controllerContext, LinearLayout llShowMediaThumb) {
        if (null == videoInfo)
            return;
        View thumbView = LayoutInflater.from(controllerContext).inflate(R.layout.thumb_item, null);
        CircleImageView videoImg = (CircleImageView) thumbView.findViewById(R.id.img_thumb);
        TextView txDes = (TextView) thumbView.findViewById(R.id.tv_thumb_description);
        if (ObjectUtils.isEmpty(videoInfo.getStrDiscript())) {
            txDes.setVisibility(View.GONE);
        } else {
            txDes.setVisibility(View.VISIBLE);
            txDes.setText(videoInfo.getStrDiscript());
        }
        LayoutParams params = new LayoutParams(
                (videoInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                (videoInfo.getdY() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT);
        params.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
        videoImg.setLayoutParams(params);
        videoImg.setImageResource(R.drawable.video);
        videoImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideo(mContext, videoInfo);
            }
        });
        // 将Img控件添加到水平滚动栏中
        LayoutParams thumbParams = new LayoutParams(
                (videoInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                LayoutParams.WRAP_CONTENT);
        thumbParams.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
        llShowMediaThumb.addView(thumbView, thumbParams);
    }

    private void getVideo(final Context mContext, final MediaInfo videoInfo) {
//		Alert.progresShow((Activity) mContext, "正在下载视频文件，请稍等");
//		Thread getVideoFileThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Message msg = mHandler.obtainMessage(1);
//				MediaBiz mMediaBiz = new MediaBiz(mContext);
//				int result = mMediaBiz.getPreView(videoInfo, MEDIATYPE.VEDIO);
//				msg.arg1 = result;
//				msg.obj = videoInfo;
//				msg.sendToTarget();
//			}
//		});
//		getVideoFileThread.start();
    }

    /**
     * @param audioInfo         需要加载音频资源信息
     * @param controllerContext
     * @param llShowMediaThumb
     * @Description:加载网络音频资源
     * @author:fuguang
     */
    public void loadAudioOnThumb(final MediaInfo audioInfo, Context controllerContext, LinearLayout llShowMediaThumb) {
        if (null == audioInfo)
            return;
        View thumbView = LayoutInflater.from(controllerContext).inflate(R.layout.thumb_item, null);
        CircleImageView audioImg = (CircleImageView) thumbView.findViewById(R.id.img_thumb);
        TextView txDes = (TextView) thumbView.findViewById(R.id.tv_thumb_description);
        if (ObjectUtils.isEmpty(audioInfo.getStrDiscript())) {
            txDes.setVisibility(View.GONE);
        } else {
            txDes.setVisibility(View.VISIBLE);
            txDes.setText(audioInfo.getStrDiscript());
        }
        audioImg.setImageResource(R.drawable.audio);
        LayoutParams params = new LayoutParams(
                (audioInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                (audioInfo.getdY() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT);
        params.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
        audioImg.setLayoutParams(params);
        // 如果文件存在，则赋值给audioImg控件
        audioImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getAudio(mContext, audioInfo);
            }
        });
        // 将thumbView控件添加到水平滚动栏中
        LayoutParams thumbParams = new LayoutParams(
                (audioInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                LayoutParams.WRAP_CONTENT);
        thumbParams.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
        try {
            llShowMediaThumb.addView(thumbView, thumbParams);
        } catch (Exception e) {
            LogMgr.error("medidacontroller", e.toString());
        }

    }

    private void getAudio(final Context mContext, final MediaInfo audioInfo) {
//		Alert.progresShow((Activity) mContext, "正在下载音频文件，请稍等");
//		Thread getVideoFileThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Message msg = mHandler.obtainMessage(2);
//				MediaBiz mMediaBiz = new MediaBiz(mContext);
//				int result = mMediaBiz.getPreView(audioInfo, MEDIATYPE.SOUND);
//				msg.arg1 = result;
//				msg.obj = audioInfo;
//				msg.sendToTarget();
//			}
//		});
//		getVideoFileThread.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (Alert.isProgressShow()) {
                Alert.progressClose();
            }
            switch (msg.what) {
                case 1:// 视频下载
                    if (msg.arg1 == 0) {// 下载成功
                        MediaInfo videoinfo = (MediaInfo) msg.obj;
                        String videoPath = Config.MEDIA_PATH + "downloads/mediaCache/" + videoinfo.getUuid()
                                + "_" + videoinfo.getMediaName();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(videoPath)), "video/*");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("oneshot", 0);
                        intent.putExtra("configchange", 0);
                        mContext.startActivity(intent);
                    } else if (msg.arg1 == 1) {// 没有视频文件
                       T.showShort(mContext, "视频下载失败");
                    }
                    break;
                case 2:// 音频下载
                    if (msg.arg1 == -1) {// 网络异常
                        T.showShort(mContext, "网络异常，请检查网络");
                    } else if (msg.arg1 == 0) {// 下载成功
                        MediaInfo audioinfo = (MediaInfo) msg.obj;
                        String audioPath = Config.MEDIA_PATH + "downloads/mediaCache/" + audioinfo.getUuid()
                                + "_" + audioinfo.getMediaName();
                        SpeakControl mSpeakControl = new SpeakControl((Activity) mContext, audioPath, audioPath);
                        mSpeakControl.speakDialogShow();
                        mSpeakControl.speakControlShow(false, false, true, true);
                    } else if (msg.arg1 == 1) {// 没有视频文件
                        T.showShort(mContext, "没有音频文件");
                    } else {// 服务端查询异常
                        T.showShort(mContext, "服务端查询音频文件失败");
                    }
                    break;
            }
        }
    };

    /**
     * @Description:加载网络缩略图
     */
    public boolean notifyImageOnThumb(List<MediaInfo> notifyImg, Context controllerContext,
                                      LinearLayout llShowMediaThumb) {
        if (null == notifyImg || notifyImg.isEmpty())
            return false;
        MediaInfo mediaInfo = null;
        // 迭代显示图片到水平滚动栏中
        for (int i = 0; i < notifyImg.size(); i++) {
            mediaInfo = notifyImg.get(i);
            int isSameFlag = isSamed(mediaInfo, llShowMediaThumb);
            if (isSameFlag == -1)
                continue;
            addView(isSameFlag, mediaInfo, controllerContext, llShowMediaThumb);
        }
        return true;
    }

    /**
     * @Description:加载网络缩略图
     */
    public boolean notifyImageOnThumb(MediaInfo mediaInfo, Context controllerContext, LinearLayout llShowMediaThumb) {
        if (null == mediaInfo)
            return false;
        // 迭代显示图片到水平滚动栏中
        int isSameFlag = isSamed(mediaInfo, llShowMediaThumb);
        if (isSameFlag == -1)
            return false;
        addView(isSameFlag, mediaInfo, controllerContext, llShowMediaThumb);
        return true;
    }

    /**
     * 判定是不是指的相同的多媒体
     *
     * @param mediaInfo        要更新的多媒体
     * @param llShowMediaThumb 已经显示的控件列表的父控件
     * @return >=0:相同 ，-1:不同
     */
    private int isSamed(MediaInfo mediaInfo, LinearLayout llShowMediaThumb) {
        int chileCount = llShowMediaThumb.getChildCount();
        for (int i = 0; i < chileCount; i++) {
            View checkdView = llShowMediaThumb.getChildAt(i);
            String uuid = mediaInfo.getUuid();
            String viewTag = (String) checkdView.getTag();
            if (!ObjectUtils.isEmpty(uuid) && !ObjectUtils.isEmpty(viewTag) && uuid.equalsIgnoreCase(viewTag))
                return i;
        }
        return -1;
    }

    /**
     * 往布局控件中添加图片view
     *
     * @param isSameFlag        添加的位置
     * @param mediaInfo         图片多媒体实体
     * @param controllerContext
     * @param llShowMediaThumb  布局控件
     */
    private void addView(int isSameFlag, MediaInfo mediaInfo, Context controllerContext,
                         LinearLayout llShowMediaThumb) {
        llShowMediaThumb.removeViewAt(isSameFlag);
        View thumbView = LayoutInflater.from(controllerContext).inflate(R.layout.thumb_item, null);
        CircleImageView img = (CircleImageView) thumbView.findViewById(R.id.img_thumb);
        TextView txDes = (TextView) thumbView.findViewById(R.id.tv_thumb_description);
        if (ObjectUtils.isEmpty(mediaInfo.getStrDiscript())) {
            txDes.setVisibility(View.GONE);
        } else {
            txDes.setVisibility(View.VISIBLE);
            txDes.setText(mediaInfo.getStrDiscript());
        }
        LayoutParams params = new LayoutParams(
                (mediaInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                (mediaInfo.getdY() * DeviceInformation.getScreenDensityDPI((Activity) mContext)) / DisplayMetrics.DENSITY_DEFAULT);
        params.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
        img.setLayoutParams(params);
        byte[] bm = mediaInfo.getBmMedia();

        if (null != bm) {
            img.setImageBitmap(BitmapFactory.decodeByteArray(bm, 0, bm.length));
        } else {
            Picasso.with(controllerContext).load(R.drawable.no_exists_pic).into(img);
        }
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                // imgOnClick(pic);
            }
        });
        // 将Img控件添加到水平滚动栏中
        LayoutParams layoutParams = new LayoutParams(
                (mediaInfo.getdX() * DeviceInformation.getScreenDensityDPI((Activity)mContext)) / DisplayMetrics.DENSITY_DEFAULT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, getPixelsFromDp(controllerContext, marginSize), 0);
        thumbView.setTag(mediaInfo.getUuid());
        llShowMediaThumb.addView(thumbView, isSameFlag, layoutParams);
    }

    private int getPixelsFromDp(Context context, int size) {
        return (size * DeviceInformation.getScreenDensityDPI((Activity)mContext)) / DisplayMetrics.DENSITY_DEFAULT;
    }
}
