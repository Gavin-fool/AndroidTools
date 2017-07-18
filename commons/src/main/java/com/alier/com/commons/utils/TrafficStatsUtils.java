package com.alier.com.commons.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;

import com.alier.com.commons.entity.CookieKeyBean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;

/**
 * @ClassName: TrafficStatsUtils
 * @Description: 流量统计的工具类
 * @date 2017年4月26日 上午9:44:37
 */
public class TrafficStatsUtils {

    /**
     * @param uid 程序的uid
     * @return 上传的流量（tcp+udp）  返回-1 表示不支持得机型
     * @Description: 获取uid上传的流量(wifi+3g/4g)
     * @author fengao
     */
    public static long getTxTraffic(int uid) {
        return TrafficStats.getUidTxBytes(uid);
    }

    /**
     * @param uid 程序的uid
     * @return 上传的流量（tcp）  返回-1 表示出现异常
     * @Description: 获取uid上传的流量(wifi+3g/4g)  通过读取/proc/uid_stat/uid/tcp_snd文件获取
     */
    public static long getTxTcpTraffic(int uid) {
        RandomAccessFile rafSnd = null;
        String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
        long sndTcpTraffic;
        try {
            rafSnd = new RandomAccessFile(sndPath, "r");
            sndTcpTraffic = Long.parseLong(rafSnd.readLine());
        } catch (FileNotFoundException e) {
            sndTcpTraffic = -1;
        } catch (IOException e) {
            e.printStackTrace();
            sndTcpTraffic = -1;
        } finally {
            try {
                if (rafSnd != null) {
                    rafSnd.close();
                }
            } catch (IOException e) {
                sndTcpTraffic = -1;
            }
        }
        return sndTcpTraffic;
    }

    /**
     * @param uid 程序的uid
     * @return 下載的流量(tcp+udp) 返回-1表示不支持的机型
     * @Description: 获取uid下載的流量(wifi+3g/4g)
     */
    public static long getRxTraffic(int uid) {
        return TrafficStats.getUidRxBytes(uid);
    }

    /**
     * @param uid 程序的uid
     * @return 下载的流量（tcp）  返回-1 表示出现异常
     * @Description: 获取uid上传的流量(wifi+3g/4g) 通过读取/proc/uid_stat/uid/tcp_rcv文件获取
     * @author fengao
     */
    public static long getRxTcpTraffic(int uid) {
        RandomAccessFile rafRcv = null; // 用于访问数据记录文件
        String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
        long rcvTcpTraffic;
        try {
            rafRcv = new RandomAccessFile(rcvPath, "r");
            rcvTcpTraffic = Long.parseLong(rafRcv.readLine()); // 读取流量统计
        } catch (FileNotFoundException e) {
            rcvTcpTraffic = -1;
        } catch (IOException e) {
            rcvTcpTraffic = -1;
        } finally {
            try {
                if (rafRcv != null) {
                    rafRcv.close();
                }
            } catch (IOException e) {
                rcvTcpTraffic = -1;
            }
        }
        return rcvTcpTraffic;
    }

    /**
     * @param uid 程序的uid
     * @return uid的总流量   当设备不支持方法且没有权限访问/proc/uid_stat/uid时 返回-1
     * @Description: 得到uid的总流量（上传+下载）
     * @author fengao
     */
    public static long getTotalTraffic(int uid) {
        long txTraffic = (getTxTraffic(uid) == -1) ? getTxTcpTraffic(uid) : getTxTraffic(uid);
        if (txTraffic == -1) {
            return -1;
        }
        long rxTraffic = (getRxTraffic(uid) == -1) ? getRxTcpTraffic(uid) : getRxTraffic(uid);
        if (rxTraffic == -1) {
            return -1;
        }
        return txTraffic + rxTraffic;
    }

    /**
     * @param context 上下文
     * @return 当前程序的uid  返回-1表示出现异常
     * @Description: 取得程序的uid
     */
    public static int getUid(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), packageManager.GET_ACTIVITIES);
            return applicationInfo.uid;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @param context 上下文
     * @Description: 流量监控 往cookie中写时间与登录前时流量
     * @author fengao
     */
    public static void trafficInitMonitor(Context context) {
        // 流量监控
        Cookie appConfig = Cookie.getInstance(context, Cookie.APP_CFG);
        long totalTraffic = -1;
        int appUid = TrafficStatsUtils.getUid(context);
        if (appUid != -1) {
            totalTraffic = TrafficStatsUtils.getTotalTraffic(appUid);
        }
        //将初始流量保存到APP_CFG的cookie中 （没有取到默认-1）
        appConfig.putVal(CookieKeyBean.APP_NET_START, totalTraffic);
    }

    /**
     * @param context 上下文
     * @param uid     程序的uid
     * @Description: 退出程序时处理网络流量
     * @author fengao
     */
    public static void trafficMonitor(Context context, int uid) {
        Cookie appConfig = Cookie.getInstance(context, Cookie.APP_CFG);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //退出程序时间
        String appEndDate = DateUtil.getDate(dateFormat, 0, 0, 0);
        //进入程序时间
//		String appStartDate = appConfig.getVal(CookieKeyBean.APP_DATE_START);
        //进入程序的流量
        long totalStartTraffic = appConfig.getLong(CookieKeyBean.APP_NET_START);
        if (totalStartTraffic == -1) {
            return;
        }
        //退出程序的流量
        long totalEndTraffic = TrafficStatsUtils.getTotalTraffic(uid);
        long totalAddedTraffic = totalEndTraffic - totalStartTraffic;
        //cookie中記錄的总流量
        long totalTraffic = appConfig.getLong(CookieKeyBean.APP_NET_TOTAL, 0);
        //更新cookie中的总流量
        appConfig.putVal(CookieKeyBean.APP_NET_TOTAL, totalTraffic + totalAddedTraffic);
        //今日的流量
        long todayTraffic = appConfig.getLong(CookieKeyBean.APP_NET_TODAY, 0);
        //上一次退出系统的日期
        String lastEndDate = appConfig.getVal(CookieKeyBean.APP_DATE_END, "").trim();
        if (lastEndDate.equals(appEndDate)) {
            //更新今日的流量
            todayTraffic = todayTraffic + totalAddedTraffic;
        } else {
            if (lastEndDate.equals("")) {//第一次进入流量监控
                todayTraffic = totalAddedTraffic;
            } else {
                String monthDate = lastEndDate.substring(0, 6);
                String monthTraffic = appConfig.getVal(monthDate, "").trim();
                if (monthTraffic.equals("")) {//第一次记录月份
                    monthTraffic = monthTraffic + todayTraffic;
                }
                monthTraffic = monthTraffic + "," + todayTraffic;
                //更新cookie中对应月份的流量  201704:120,130,140
                appConfig.putVal(monthDate, monthTraffic);
                todayTraffic = totalAddedTraffic;
            }
        }
        //更新cookie中的今日流量
        appConfig.putVal(CookieKeyBean.APP_NET_TODAY, todayTraffic);
        //更新cookie中退出系统的时间
        appConfig.putVal(CookieKeyBean.APP_DATE_END, appEndDate);
    }
}
