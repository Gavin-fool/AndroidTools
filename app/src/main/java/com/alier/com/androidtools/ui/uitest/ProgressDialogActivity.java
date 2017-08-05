package com.alier.com.androidtools.ui.uitest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.alier.com.androidtools.R;
import com.alier.com.commons.BaseActivity;

/**
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年10月27日 下午4:36:46
 * @version 1.0
 */
public class ProgressDialogActivity extends BaseActivity {

	public Handler handler;
	private static final int MAX_PROGRESS = 100;
	private static final int PRO = 10;
	private int progress = 10;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dislog);
	}

	@SuppressWarnings("deprecation")
	public void openDialog(View v) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case PRO:
					if (progress >= MAX_PROGRESS) {
						// 重新设置
						progress = 0;
						progressDialog.dismiss();// 销毁对话框
					} else {
						progress++;
						progressDialog.incrementProgressBy(1);
						// 延迟发送消息
						handler.sendEmptyMessageDelayed(PRO, 100);
					}
					break;

				default:
					break;
				}
			}
		};

		progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("正在处理数据。。。");
		progressDialog.setMessage("请稍后。。");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）

		// 进度最大值
		progressDialog.setMax(MAX_PROGRESS);
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "暂停", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.setProgress(50);
			}
		});
		
		
		progressDialog.setButton("暂停", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除消息队列
				handler.removeMessages(PRO);

			}
		});

		progressDialog.setButton2("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除消息队列
				handler.removeMessages(PRO);
				// 恢复进度条初始值
				progress = 0;
				progressDialog.setProgress(progress);
			}
		});

		// 显示
		progressDialog.show();
		// 必须设置到show之后
		progress = (progress > 0) ? progress : 0;
		progressDialog.setProgress(progress);
		// 线程
		handler.sendEmptyMessage(PRO);

	}

	public void test1() {
		new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("xxxx").setMessage("是否创建文件")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 创建文件
						new AlertDialog.Builder(ProgressDialogActivity.this).setMessage("文件已经被创建").show();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new AlertDialog.Builder(ProgressDialogActivity.this).setMessage("您已经选择了取消的按钮,该文件不会被创建").create().show();
					}
				}).show();
	}

	public void test2() {
		// 创建对话框
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		// 设置对话框的标题
		alertDialog.setTitle("xxxx");
		// 设置对话框的内容
		alertDialog.setMessage("消息");
		// 显示对话框
		alertDialog.show();
	}

	public void test3() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("xxxx").setMessage("啊大声大声道").show();
	}

	public void test4() {
		final String items[] = { "Java", "Php", "3G", ".Net" };
		new AlertDialog.Builder(this).setTitle("简单列表对话框").setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 第一个参数dialog int which 条目
				Toast.makeText(getApplicationContext(), "aaa" + items[which], Toast.LENGTH_LONG).show();

			}
		}).show();

	}

	public void test5() {
		final String items[] = { "JAVA", ".NET", "3G", "PHP" };
		new AlertDialog.Builder(this).setTitle("单选列表对话框")
				// 数字2代表的是数组的下标
				.setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 第一个参数 dialog int which 那个条目
						Toast.makeText(getApplicationContext(), "xxxxx" + items[which], Toast.LENGTH_LONG).show();

					}
				}).show();
	}

	public void test6() {
		final String items[] = { "JAVA", ".NET", "PHP", "3G" };
		new AlertDialog.Builder(this).setTitle("多选列表对话框的简单实现").setMultiChoiceItems(items,
				new boolean[] { false, true, true, true }, new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						Toast.makeText(getApplicationContext(), "sad" + items[which], Toast.LENGTH_LONG).show();

					}
				}).show();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exec() {
		// TODO Auto-generated method stub
		
	}

}
