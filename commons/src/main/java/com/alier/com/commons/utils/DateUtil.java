package com.alier.com.commons.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;

/**
 * 时间工具类
 *
 */
public class DateUtil {
	
	public final static String START_TIME = "00:00:00";//案卷起始时分秒
	public final static String END_TIME = "23:59:59";//案卷结束时分秒
	private static String[] start_end_time = new String[2];//案卷起止时间【0】起始时间【1】终止时间
	private static String[] start_end_date = new String[2];// 案卷起止时间【0】起始日期【1】终止日期
	
	public static String DateNow(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));    //获取东八区时间
		int year = c.get(Calendar.YEAR);    //获取年
		int month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
		int day = c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
		return year+month+day+"";
	}
	
	public static String TimeNow(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));    //获取东八区时间
		int time = c.get(Calendar.HOUR_OF_DAY);       //获取当前小时
		int min = c.get(Calendar.MINUTE);          //获取当前分钟
		int xx = c.get(Calendar.SECOND);          //获取当前秒
		return time+":"+min+":"+xx;
	}
	
	@SuppressWarnings({ "unused", "static-access" })
	public static String DateTimeNow(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));    //获取东八区时间
		int year = c.get(Calendar.YEAR);    //获取年
		int month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
		int day = c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
		int first = c.getActualMinimum(c.DAY_OF_MONTH);    //获取本月最小天数
		int last = c.getActualMaximum(c.DAY_OF_MONTH);    //获取本月最大天数
		int time = c.get(Calendar.HOUR_OF_DAY);       //获取当前小时
		int min = c.get(Calendar.MINUTE);          //获取当前分钟
		int xx = c.get(Calendar.SECOND);          //获取当前秒
		return year+"-"+month+"-"+day+" "+time+":"+min+":"+xx;
	}
	
	/**
	 * 计算统计案卷起止时间
	 * @param timetype 要获取的起止时间类型
	 *  0：今天  
	 *  1：前三天  
	 *  2：本周  
	 *  3：前两周 
	 *  4：本月 
	 *  5：前两月
	 * @return 包含起止时间的数组
	 * @author miaowei
	 */
	@SuppressLint("SimpleDateFormat")
	public static String[] start_end_Time(int timetype){
		String dateFormatString = null;
		int today, start_day, end_day, today_month, today_year, end_year, start_month, end_month;
		int weekofday, dumpSomeDay, dumpEndSomeDay;
		String start_monthString = null;
		String end_monthString = null ;
		boolean modifyStartMonth = false;
		boolean modifyEndMonth = false;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(new Date()+""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		dateFormatString = dateFormat.format(new Date());
		switch (timetype) {
		case 0://当天案卷
			start_end_time[0] = dateFormatString+START_TIME;
			start_end_time[1] = dateFormatString+END_TIME;
			break;
		case 1://前三天
			
			today = Integer.parseInt(dateFormatString.substring(dateFormatString.lastIndexOf("-")+1).trim());//今天几号 如28号
			today_month = Integer.parseInt(dateFormatString.substring(5,7));//当前月份 如 8月
			end_year = today_year = Integer.parseInt(dateFormatString.substring(0,4));
			calendar.add(Calendar.DATE, -3);
			start_day = calendar.get(Calendar.DATE);//起始时间
			calendar = Calendar.getInstance();//重置日期为当前
			calendar.add(Calendar.DATE, -1);
			end_day = calendar.get(Calendar.DATE);//终止时间
			
			if(start_day >today){//如果起始时间大于今天的时间，说明这是上一个月末尾时间，要把当前月份加一 组成如 2013-09-13~2013-10-25
				modifyStartMonth = true;
				start_month = today_month - 1;
				
				if(start_month<10 && start_month>0){
					start_monthString = "0"+start_month;
				}else{
					if(start_month == 0){//如果当前是1月1号 前一个月应该是去年的12月
						start_month = 12;//月份置为去年的12月
						today_year = today_year - 1;//当年减去一年表示去年
					}
					start_monthString = String.valueOf(start_month);
				}
				
			}
			if(end_day >today){//同理
				modifyEndMonth = true;
				end_month = today_month -1;
				if(end_month<10 && end_month>0){
					end_monthString = "0"+end_month;
				}else{
					if(end_month == 0){
						end_month = 12;
						end_year = today_year - 1;
					}
					end_monthString = String.valueOf(end_month);
				}
			}
			if(modifyStartMonth){
				start_end_time[0] =today_year+"-"+ start_monthString+"-"+(start_day<10?"0"+start_day:start_day)+" "+START_TIME;
			}else{
				start_end_time[0] =dateFormatString.substring(0, dateFormatString.lastIndexOf("-")+1)+(start_day<10?"0"+start_day:start_day)+" "+START_TIME;
			}
			if(modifyEndMonth){
				start_end_time[1] =end_year+"-"+ end_monthString+"-"+(end_day<10?"0"+end_day:end_day)+" "+END_TIME;
			}else{
				start_end_time[1] =dateFormatString.substring(0, dateFormatString.lastIndexOf("-")+1)+(end_day<10?"0"+end_day:end_day)+" "+END_TIME;
			}
			break;
		case 2://本周
			weekofday = calendar.get(Calendar.DAY_OF_WEEK) - 1;//获取今天是一周中的第几天 星期日 第一天  星期一 第二天,这里按照习惯让星期一 为第一天 
			if(weekofday == 1){//如果今天是星期一的话 本周第一天就是今天不用跳转日期
				dumpSomeDay = 0;
			}else{
				dumpSomeDay = 1 - weekofday;
			}
			calendar.add(Calendar.DATE, dumpSomeDay);
			start_end_time[0] = dateFormat.format(calendar.getTime())+START_TIME;
			start_end_time[1] = dateFormatString+END_TIME;
			break;
		case 3://前两周  往前跳14天+本周天数
			weekofday = calendar.get(Calendar.DAY_OF_WEEK)-1;
			if(weekofday == 1){
				dumpSomeDay = -14;
				dumpEndSomeDay = -1;
			}else{
				dumpSomeDay = -(weekofday + 13);
				dumpEndSomeDay = -weekofday;
			}
			calendar.add(Calendar.DATE, dumpSomeDay);
			start_end_time[0] = dateFormat.format(calendar.getTime())+START_TIME;
			calendar = Calendar.getInstance();//重置日期为当前
			calendar.add(Calendar.DATE, dumpEndSomeDay);
			start_end_time[1] = dateFormat.format(calendar.getTime())+END_TIME;
			break;
		case 4://本月
			start_end_time[0] = dateFormatString.substring(0, dateFormatString.lastIndexOf("-")+1)+"01 "+START_TIME;
			start_end_time[1] = dateFormatString.substring(0, dateFormatString.lastIndexOf("-")+1)+calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+" "+END_TIME;
			break;
		case 5://前两月
			calendar.add(Calendar.MONTH, -2);//得到前两个月的月份值如 6月实际返回的数值是5
			String lastTwoTime = dateFormat.format(calendar.getTime()).substring(0, dateFormatString.lastIndexOf("-")+1)+"01 ";
			calendar.add(Calendar.MONTH, 1);//在回退两个月的基础上在前进一个月，相当于前一个月的月份
			try {
				calendar.setTime(dateFormat.parse(calendar.getTime()+""));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String lastOneTime = dateFormat.format(calendar.getTime()).substring(0, dateFormatString.lastIndexOf("-")+1)+calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+" ";
			
			start_end_time[0] = lastTwoTime + START_TIME;
			start_end_time[1] = lastOneTime + END_TIME;
			break;

		default:
			break;
		}
		return start_end_time;
	}
	/**
	 * 查询指定范围内的开始日期与结束日期，如果不是查询指定月的时候，year,month等字段可以默认传0
	 * 
	 * @param datetype
	 *            1:本周，2：指定的年月
	 * 
	 * @return
	 */
	public static String[] obtainStartToEndDate(int datetype, int year,
			int month) {
		int startDate = 0;
		int endDate = 0;
		String startDateString = "";
		String endDateString = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
		Calendar calendar = Calendar.getInstance();
		switch (datetype) {
		case 1:// 本周的开始日期和结束日期
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
			startDateString = dateFormat.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			// 增加一个星期，才是我们中国人理解的本周日的日期
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
			endDateString = dateFormat.format(calendar.getTime());
			start_end_date[0] = startDateString;
			start_end_date[1] = endDateString;
			break;
		case 2:// 获取指定年月的第一天和最后一天的日期
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			start_end_date[1] = dateFormat.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			start_end_date[0] = dateFormat.format(calendar.getTime());
		default:
			break;
		}
		return start_end_date;
	}
    /** 
     * 两个时间相差距离多少秒 
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00 
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00 
     * @return long  秒
     */  
    public static long getDistanceTimes(String str1, String str2) {  
    	if(str1==null || str1.equals("")|| str2==null ||str2.equals("")){
    		return 0;  
    	}
    	long diff = 0;  
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);  
        Date one;  
        Date two;  
        try {  
            one = df.parse(str1);  
            two = df.parse(str2);  
            long time1 = one.getTime();  
            long time2 = two.getTime();  
            if(time1<time2) {  
                diff = time2 - time1;  
            } else {  
                diff = time1 - time2;  
            }  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
        return diff/1000;  
    }  
    
    /**
	 * 比较两个日期之间的天数
	 * @param smdate
	 * @param bdate
	 * @return 天数
	 */
	public static int daysBetween(String smdate,String bdate){
		if (10 == smdate.length()){
			smdate = smdate + " 00:00:00";
		}
		if (10 == bdate.length()) {
			bdate = bdate + " 00:00:00";
		}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
			cal.setTime(sdf.parse(smdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        long time1 = cal.getTimeInMillis();
        try {
			cal.setTime(sdf.parse(bdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        long time2 = cal.getTimeInMillis();
        long between_days = 0 ;
        if(time2>time1){
        	between_days=(time2-time1)/(1000*3600*24);
        }else{
        	between_days=(time1-time2)/(1000*3600*24);
        }
       return Integer.parseInt(String.valueOf(between_days));   
    }
	
	/**
	 * 
	 * @Description:得到偏移的时间（偏移量以当前时间为准）
	 * @param:@param sdf
	 * 				格式字符串 （yyyyMMdd）
	 * @param:@param yearOffSet
	 * 				year 偏移量
	 * @param:@param monthOffSet
	 * 				month 偏移量
	 * @param:@param dayOffSet
	 * 				day 偏移量
	 * @return:String
	 * 				按照格式处理的时间字符串
	 * @throws:
	 */
	public static String getDate(DateFormat sdf,int yearOffSet,int monthOffSet,int dayOffSet){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
		c.add(Calendar.YEAR, yearOffSet);
		c.add(Calendar.MONTH, monthOffSet);
		c.add(Calendar.DAY_OF_MONTH, dayOffSet);
		String dateStr = sdf.format(c.getTime());
		return dateStr;
	}
	
	/**
	 * 比较两个日期前后
	 * @param smdate
	 * @param bdate
	 * @return 
	 * 		true:已过期 
	 * 		false:未过期 /String 无法转化为 Date
	 */
	public static boolean compareDate(String smdate, String bdate){
		boolean result = false;
		if (10 == smdate.length()){
			smdate = smdate + " 00:00:00";
		}
		if (10 == bdate.length()) {
			bdate = bdate + " 00:00:00";
		}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date d_smdate = null;
    	Date d_bdate = null;
    	try {
			d_smdate = sdf.parse(smdate);
			d_bdate = sdf.parse(bdate);
		} catch (ParseException e) {
			return result;
		}
    	if (null == d_smdate || null == d_bdate){
    		return result;
    	}
    	if (d_smdate.getTime() > d_bdate.getTime()){
    		result = true;
    	}
          
       return result;   
    }
	
	/**
	 * @author fengao
	 * @Description:比较标准时间与用户选择时间的大小
	 * @param:@param pickDate
	 *                   标准时间
	 * @param:@param standDate
	 *                   选择时间
	 * @return:boolean 
	 * 				true：标准时间>=选择时间 false：标准时间<选择时间
	 * @throws:
	 */
	public static boolean compareDate(Date standDate, Date pickDate) {
		long result = standDate.getTime() - pickDate.getTime();
		if (result < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * @author fengao
	 * @Description:比较两个日期前后
	 * @param:@param smdate
	 * @param:@param bdate
	 * @param:@param sdf 日期格式
	 * @param:@return
	 * @return:boolean
	 * 			true:已过期 
	 * 			false:未过期 /String 无法转化为 Date
	 * @throws:
	 */
	public static boolean compareDate(String smdate, String bdate,SimpleDateFormat sdf){
		boolean result = false;
    	Date d_smdate = null;
    	Date d_bdate = null;
    	try {
			d_smdate = sdf.parse(smdate);
			d_bdate = sdf.parse(bdate);
		} catch (ParseException e) {
			return result;
		}
    	if (null == d_smdate || null == d_bdate){
    		return result;
    	}
    	if (d_smdate.getTime() > d_bdate.getTime()){
    		result = true;
    	}
        return result;   
    }
	
	public static String formatDate(String dateStr,SimpleDateFormat sdf1,SimpleDateFormat sdf2){
		Date mDate = null;
		try {
			mDate = sdf1.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		String result = sdf2.format(mDate);
		return result;
	}
}
