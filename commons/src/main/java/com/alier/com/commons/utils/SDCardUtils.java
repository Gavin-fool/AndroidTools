package com.alier.com.commons.utils;

import android.os.Environment;
import android.os.StatFs;

import com.alier.com.commons.Config;

public class SDCardUtils {

    /**
     * 判断SD卡是否存在
     *
     * @return
     */
    public static boolean isExistSDCard() {
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * SD卡可用空间
     *
     * @return
     */
    public static long getSDFreeSize() {
        //取得SD卡文件路径
        String path = Config.ROOT_PATH;
        StatFs sf = new StatFs(path);
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * SD卡空间容量
     *
     * @return
     */
    public static long getSDAllSize() {
        //取得SD卡文件路径
        String path = Config.ROOT_PATH;
        StatFs sf = new StatFs(path);
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; //单位MB
    }
}
