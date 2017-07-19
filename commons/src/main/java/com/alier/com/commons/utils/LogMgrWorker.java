package com.alier.com.commons.utils;

import com.alier.com.commons.BaseApp;
import com.alier.com.commons.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LogMgrWorker {
	private FileOutputStream foutStream = null;
	private OutputStreamWriter osw = null;
	private boolean WORKER_STATUS = false; // 工作对象的工作状态
	private List<String> contentList = null; // 工作对象的工作任务清单
	private int timeBlock = 0;
	private int curDay = 1;

	private class WorkThread implements Runnable {
		@Override
		public void run() {
			WORKER_STATUS = true;
			foutStream = openFileforOut(BaseApp.writeLogTime);
			osw = new OutputStreamWriter(foutStream);
			int count = 0;
			while (count < 10) {
				// 检查时间对不对
				Calendar c = Calendar.getInstance(TimeZone
						.getTimeZone("GMT+08:00"));
				int h = c.get(Calendar.HOUR_OF_DAY);
				int d = c.get(Calendar.DAY_OF_MONTH);
				if ((timeBlock != 24 && h > timeBlock) || (curDay != d)) {
					// 重新打开文件
					try {
						if (osw != null)
							osw.close();
						if (foutStream != null)
							foutStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						osw = null;
						foutStream = null;
					}
					foutStream = openFileforOut(BaseApp.writeLogTime);
					osw = new OutputStreamWriter(foutStream);
					count = 0;
				}
				if (contentList == null || contentList.size() == 0) { // 无任务
					count++;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else { // 有任务
					count = 0;
					String sLog = contentList.get(0);
					if (sLog != null && sLog.trim().length() > 0) {
						try {
							boolean bRes = writeLogtoFile(sLog);
							if (bRes)
								contentList.remove(0);
						} catch (Exception ex) {
							count = 10;
						}
					}
				}
			}
			try {
				if (osw != null)
					osw.close();
				if (foutStream != null)
					foutStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				osw = null;
				foutStream = null;
			}
			WORKER_STATUS = false;
		}

	}

	private WorkThread workThread = new WorkThread();
	private Thread workth = null;

	public LogMgrWorker() {
	}

	public boolean getStatus() {
		return WORKER_STATUS;
	}

	public synchronized void startWorking() {
		if (!WORKER_STATUS) {
			workth = new Thread(workThread);
			workth.start();
		}
	}

	public void addJob(String content) {
		if (contentList == null) {
			contentList = new ArrayList<String>();
		}
		contentList.add(content);
	}

	/**
	 * 具体的网文件中写日志的方法
	 * 
	 * @param content
	 */
	private boolean writeLogtoFile(String content) {
		if (foutStream == null)
			return false;
		Date dt = new Date();
		try {
			osw.write("#(" + dt.toString() + ")");
			osw.write("\r\n");
			osw.write(content);
			osw.write("\r\n");
			osw.write("@(" + dt.toString() + ")");
			osw.write("\r\n");//
			osw.flush();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	// 打开文件
	private FileOutputStream openFileforOut(int byHour) {
		try {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
			String month = null;
			String day = null;
			if ((c.get(Calendar.MONTH) + 1) < 10) {
				month = "0" + (c.get(Calendar.MONTH) + 1);
			} else {
				month = "" + (c.get(Calendar.MONTH) + 1);
			}
			if (c.get(Calendar.DAY_OF_MONTH) < 10) {
				day = "0" + c.get(Calendar.DAY_OF_MONTH);
			} else {
				day = c.get(Calendar.DAY_OF_MONTH) + "";
			}
			String date2 = c.get(Calendar.YEAR) + month + day + "";
			String headOfHour = "";
			if (c.get(Calendar.HOUR_OF_DAY) < 10) {
				headOfHour = "0";
			}
			String logPath = Config.ANDROID_TEST_PATH + File.separator
					+ "log" + File.separator + date2 + "/";
			File dir = new File(logPath);
			// 如果目录中不存在，创建这个目录
			if (!dir.exists())
				dir.mkdirs();
			// 按两小时一次生成日志文件
			int tempHour = c.get(Calendar.HOUR_OF_DAY)
					- (c.get(Calendar.HOUR_OF_DAY) % byHour);
			String temp = "" + (tempHour + byHour);
			if (Integer.valueOf(temp) < 10) {
				temp = "0" + temp;
			}
			String fileName = logPath + File.separator
					+ (headOfHour + tempHour + "-" + temp + ".txt");
			timeBlock = Integer.valueOf(temp);
			curDay = Integer.valueOf(day);
			FileOutputStream fout = null;
			File file = new File(fileName);
			try {
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				fout = new FileOutputStream(file, true); // true表示追加到已存在的文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return fout;
		}catch  (Exception ex) {
			return null;
		}
	}
}
