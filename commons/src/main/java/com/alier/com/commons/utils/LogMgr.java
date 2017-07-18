package com.alier.com.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class LogMgr {
	private static LogMgrWorker worker = new LogMgrWorker();
	private static String LOG_INFO = "DCM_INFO";
	private static String LOG_WARN = "DCM_WARN";
	private static String LOG_ERROR = "DCM_ERROR";
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	
	private static void writeLog(String content){
		worker.addJob(content);
		if(!worker.getStatus()){
			worker.startWorking();
		}
	}
	
	/**
	 * 信息日志:记录普通信息,如:获取数据,登录成功等
	 * @param origin 日志来源:类名
	 * @param content 日志内容
	 */
	public static void info(String origin, String content){
		writeLog(formatLogString(LOG_INFO, origin, content));
	}
	
	/**
	 * 警告日志:记录警告信息,如:获取数据失败,登录失败等
	 * @param origin 日志来源:类名
	 * @param content 日志内容
	 */
	public static void warn(String origin, String content){
		writeLog(formatLogString(LOG_WARN, origin, content));
	}
	
	/**
	 * 异常日志:记录异常信息,在catch中用
	 * @param origin 日志来源:类名
	 * @param content 日志内容
	 */
	public static void error(String origin, String content){
		writeLog(formatLogString(LOG_ERROR, origin, content));
	}
	
	/**
	 * 格式化要上传的日志
	 * @param origin 日志来源:类名
	 * @param content 日志内容
	 * @return 格式化了的字符串
	 */
	private static String formatLogString(String logType, String origin, String content) {
		// 待格式化的字符串格式
		/**
		 * logType：日志类型  DCM_INFO：信息；DCM_WARN：警告；DCM_ERROR：异常
		 * writeLogTime:日志记录时间
		 * logOrigin:日志来源
		 * logInfo:日志信息
		 */
		String logFormatString = "<Request logType=\"\" writeLogTime=\"\" logOrigin=\"\" logInfo=\"\"/>";
		try {
			Document doc = null;
			InputStream in = new ByteArrayInputStream(
					logFormatString.getBytes("UTF-8"));
			doc = SAXReader.class.newInstance().read(in);
			Element root = (Element) doc.selectSingleNode("//Request");
			Attribute attribute = null;
			attribute = root.attribute("logType");
			attribute.setValue(logType);
			attribute  = root.attribute("writeLogTime");
			attribute.setValue(format.format(new Date()));
			attribute = root.attribute("logOrigin");
			attribute.setValue(origin);
			attribute = root.attribute("logInfo");
			attribute.setValue(content);
			OutputFormat formats = OutputFormat.createPrettyPrint();
			StringWriter out = new StringWriter();
			XMLWriter writer = new XMLWriter(out, formats);
			writer.write(doc);
			writer.close();
			logFormatString = out.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logFormatString;
	}
}
