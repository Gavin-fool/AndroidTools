package com.alier.com.controllerlibrary.mediaController;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alier.com.commons.utils.LogMgr;
import com.alier.com.commons.utils.ObjectUtils;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.R;

/**
 * @Title:SpeakControl
 * @description:录音控制控件
 * @author:gavin_fool
 * @date:2017年4月26日下午2:22:43
 * @version:v1.0
 */
public class SpeakControl {

	private static final String TAG = "SpeakControl";
	private Activity m_activity = null;
	/** 录音文件存储路径 */
	private File home = null;
	private File path = null;
	/** 录音文件 */
	private String temp = "recaudio";
	private Button m_btnOk, m_btnCancel;
	private MediaRecorder mMediaRecorder = null;
	// 声音控制
	private PopupWindow alertSoundBox = null;
	private CheckBox m_ckAudio = null, m_ckAudioPlay = null;
	private View m_microphoneing = null, m_microphone = null;
	private TextView m_media_state = null, record_tip = null, play_tip = null;
	private MediaPlayer m_mediaPlayer = null;
	private RelativeLayout rlRecord = null, rlRecordOk = null, rlPlay = null, rlCancel = null;

	/**
	 * 初始化录音控件
	 * 
	 * @param activity
	 */
	public SpeakControl(Activity activity, String savedPath) {
		this.m_activity = activity;
		// 是否存在SD卡
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			home = new File(savedPath);
			if (!home.exists()) {
				home.mkdirs();
			}
			init();
		} else {
			T.showShort(m_activity, "请先插入SD卡");
			return;
		}
	}

	/**
	 * 初始化录音控件
	 * 
	 * @param activity
	 */
	public SpeakControl(Activity activity, String savedPath, String currentPath) {
		this.m_activity = activity;
		// 是否存在SD卡
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			home = new File(savedPath);
			if (!home.exists()) {
				home.mkdirs();
			}
			init();
		} else {
			T.showShort(m_activity, "请先插入SD卡");
			return;
		}
		if (!ObjectUtils.isEmpty(currentPath))
			path = new File(currentPath);
	}

	private void init() {
		// 声音控件
		alertSoundBox = new PopupWindow();
		View view = LayoutInflater.from(m_activity).inflate( R.layout.speak_box,null);
		m_ckAudio = (CheckBox) view.findViewById(R.id.ck_record);
		m_ckAudio.setChecked(false);
		m_ckAudio.setOnClickListener(audioListener);
		m_btnOk = (Button) view.findViewById(R.id.record_ok);
		record_tip = (TextView) view.findViewById(R.id.ck_record_tip);
		play_tip = (TextView) view.findViewById(R.id.ck_play_tip);
		m_btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				release();
				alertSoundBox.dismiss();
				if (null != m_ckAudio && m_ckAudio.isChecked()) {
					m_ckAudio.setChecked(false);
				}
			}
		});
		m_btnCancel = (Button) view.findViewById(R.id.record_cancel);
		m_btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				release();
				alertSoundBox.dismiss();
				if (null != m_ckAudio && m_ckAudio.isChecked()) {
					m_ckAudio.setChecked(false);
				}
				onCancelRecord();
			}
		});
		m_ckAudioPlay = (CheckBox) view.findViewById(R.id.ck_play);
		m_ckAudioPlay.setOnClickListener(audioListener);
		m_microphoneing = view.findViewById(R.id.microphoneing);
		m_microphone = view.findViewById(R.id.microphone_static);
		m_media_state = (TextView) view.findViewById(R.id.media_state);
		m_activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		rlRecord = (RelativeLayout) view.findViewById(R.id.rl_record);
		rlRecordOk = (RelativeLayout) view.findViewById(R.id.rl_record_ok);
		rlPlay = (RelativeLayout) view.findViewById(R.id.rl_ck_play);
		rlCancel = (RelativeLayout) view.findViewById(R.id.rl_record_cancel);
	}

	/**
	 * 开始录音
	 */
	public void startRecord() {
		try {
			// 创建录音临时文件
			path = File.createTempFile(temp, ".3gp", home);// 支持 .amr |.wav
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置数据来源，麦克风
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// 设置格式
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置编码
			mMediaRecorder.setOutputFile(path.getAbsolutePath());// 设置输出文件路径
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IOException e) {
			mMediaRecorder.release();
			e.printStackTrace();
		} catch (OutOfMemoryError error) {
			LogMgr.error("SpeakControl", "开始录音内存溢出");
		} catch (Exception ex) {
			LogMgr.error("SpeakControl", "开始录音异常");
		}
	}

	/**
	 * 结束录音
	 */
	public void stopRecord() {
		if (mMediaRecorder == null) {
			return;
		}
		try {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			onRecordCompletion(path.getAbsolutePath());
		} catch (OutOfMemoryError error) {
			LogMgr.error("SpeakControl", "结束录音内存溢出");
		} catch (Exception ex) {
			mMediaRecorder.release();// 释放资源
			LogMgr.error("SpeakControl", "结束录音异常");
		}
	}

	/**
	 * 暂停
	 */
	public void suspend() {
		try {
			if (m_mediaPlayer != null) {
				m_mediaPlayer.pause();
			}
		} catch (OutOfMemoryError error) {
			LogMgr.error("SpeakControl", "暂停录音内存溢出");
		} catch (Exception ex) {
			m_mediaPlayer.release();
			LogMgr.error("SpeakControl", "暂停录音异常");
		}
	}

	/**
	 * 释放资源
	 */
	public void release() {
		setAudioState(false);
		try {
			if (m_mediaPlayer != null) {
				m_mediaPlayer.release();
			}
			if (mMediaRecorder != null) {
				mMediaRecorder.release();
			}
		} catch (OutOfMemoryError error) {
			LogMgr.error("SpeakControl", "释放录音资源时内存溢出");
		} catch (Exception ex) {
		}
	}

	/**
	 * 录音完成回调函数
	 * 
	 * @param path
	 */
	public void onRecordCompletion(String path) {
	}

	/**
	 * 取消录音
	 */
	public void onCancelRecord() {
	};

	/**
	 * 音頻播放完囘調函数
	 */
	public void onMediaCompletion() {
		m_microphone.setVisibility(View.VISIBLE);
		m_microphoneing.setVisibility(View.GONE);
		m_ckAudioPlay.setChecked(false);
		m_media_state.setText("就绪");
		m_ckAudio.setEnabled(true);
	};

	/**
	 * 开始播放
	 */
	public void play() {
		try {
			m_mediaPlayer = new MediaPlayer();
			m_mediaPlayer.setDataSource(path.getAbsolutePath());
			m_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (null != mp) {
						mp.release();
					}
					onMediaCompletion();
				}
			});
			m_mediaPlayer.prepare();
			m_mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			m_mediaPlayer.release();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			m_mediaPlayer.release();
			e.printStackTrace();
		} catch (IOException e) {
			m_mediaPlayer.release();
			e.printStackTrace();
		} catch (OutOfMemoryError error) {
			LogMgr.error("SpeakControl", "开始播放录音内存溢出");
		} catch (Exception ex) {
			LogMgr.error("SpeakControl", "开始播放录音异常");
		}
	}

	boolean flag = false;

	/**
	 * 弹出录音对话框
	 */
	public void speakDialogShow() {
		alertSoundBox.showAtLocation(rlRecord, Gravity.CENTER, 0, 0);
		m_microphone.setVisibility(View.GONE);
		m_media_state.setText("准备就绪");
		m_ckAudioPlay.setChecked(true);
		m_ckAudio.setChecked(false);
		m_microphone.setVisibility(View.VISIBLE);
		m_microphoneing.setVisibility(View.GONE);
		m_ckAudioPlay.setChecked(false);
		m_ckAudio.setEnabled(true);
	}

	public void speakDialogClose() {
		release();
		alertSoundBox.dismiss();
	}

	/**
	 * @Description:录音控制操作按钮显示状态
	 * @param isShowRecord
	 * @param isShowOK
	 * @param isShowPlay
	 * @param isShowCancel
	 * @return:void
	 */
	public void speakControlShow(boolean isShowRecord, boolean isShowOK, boolean isShowPlay, boolean isShowCancel) {
		if (isShowRecord) {
			rlRecord.setVisibility(View.VISIBLE);
		} else {
			rlRecord.setVisibility(View.GONE);
		}
		if (isShowOK) {
			rlRecordOk.setVisibility(View.VISIBLE);
		} else {
			rlRecordOk.setVisibility(View.GONE);
		}
		if (isShowPlay) {
			rlPlay.setVisibility(View.VISIBLE);
		} else {
			rlPlay.setVisibility(View.GONE);
		}
		if (isShowCancel) {
			rlCancel.setVisibility(View.VISIBLE);
		} else {
			rlCancel.setVisibility(View.GONE);
		}
	}

	/**
	 * 是否正在听你说话 是返回true 否则返回false
	 */
	public boolean isListening() {
		if (null != m_ckAudio && m_ckAudio.isChecked()) {
			return true;
		}
		return false;
	}

	/**
	 * 设置录音状态 true 为正在录音 false 结束录音
	 * 
	 * @param flag
	 */
	public void setAudioState(boolean flag) {
		if (null != m_ckAudio && m_ckAudio.isChecked()) {
			m_ckAudio.setChecked(flag);
		}
	}

	OnClickListener audioListener = new OnClickListener() {
		CheckBox ckb = null;

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.ck_record) {
				ckb = (CheckBox) v;
				if (ckb.isChecked()) {
					record_tip.setText("停止");
					startRecord();
					m_ckAudioPlay.setEnabled(false);
					m_ckAudioPlay.setChecked(false);
					m_microphone.setVisibility(View.GONE);
					m_microphoneing.setVisibility(View.VISIBLE);
					m_media_state.setText("录音中...");
					suspend();
				} else {
					record_tip.setText("录制");
					stopRecord();
					m_ckAudioPlay.setEnabled(true);
					m_ckAudioPlay.setChecked(false);
					m_microphone.setVisibility(View.VISIBLE);
					m_microphoneing.setVisibility(View.GONE);
					m_media_state.setText("完毕");
				}
				ckb.setEnabled(false);
				// 录音操作间隔最小0.5秒
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ckb.setEnabled(true);

					}
				}, 1500);
			} else if (id == R.id.ck_play) {
				ckb = (CheckBox) v;
				if (path == null || !path.exists() || !path.canRead()) {
					T.showShort(m_activity, "没有选中音频文件");
					ckb.setChecked(false);
					return;
				}
				if (ckb.isChecked()) {
					play_tip.setText("暂停");
					play();
					m_microphone.setVisibility(View.GONE);
					m_microphoneing.setVisibility(View.VISIBLE);
					m_media_state.setText("播放中...");
				} else {
					play_tip.setText("播放");
					suspend();
					m_microphone.setVisibility(View.VISIBLE);
					m_microphoneing.setVisibility(View.GONE);
					m_media_state.setText("完毕");
					m_ckAudio.setEnabled(true);
				}
			}
		}
	};
}