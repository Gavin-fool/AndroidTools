package com.alier.com.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alier.com.commons.Config;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * 操作文件工具类
 *
 * @author GuannanYan
 * @version 2011-04-20
 */
public class FileUtils {

    public static String CACHE_PATH = Config.ANDROID_TEST_PATH + File.separator + "photo" + File.separator
            + "cache" + File.separator;

    public FileUtils() {

    }

    private static int length = 0;

    public int getLength() {
        return length / 1024;
    }

    /**
     * 将inputstream中的数据写入SD卡
     *
     * @author
     */
    public File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            file = createSDFile(path);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int len = 0;
            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 根据路径创建一个文件
     */
    public static void createAfile(String path, String data) {
        BufferedReader bufferReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(path);
            if (distFile.exists()) {
                distFile.delete();
            }
            if (!distFile.getParentFile().exists())
                distFile.getParentFile().mkdirs();
            bufferReader = new BufferedReader(new StringReader(data));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024]; // 字符缓冲区
            int len;
            while ((len = bufferReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SD卡创建目录
     *
     * @author
     */
    public File createSDFile(String dirName) {
        File file = new File(dirName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * <p>
     * 将文件转成base64 字符串 效率比较低建议使用ediaFileUtils.encodeNative
     * </p>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        // return new android.util.Base64;
        // 去空格 换行
        return replaceBlank(Base64.encodeToString(buffer, Base64.DEFAULT));
    }

    public static String replaceBlank(String str) {

        String dest = "";

        if (str != null) {

            Pattern p = Pattern.compile("\\s*|\t|\r|\n");

            Matcher m = p.matcher(str);

            dest = m.replaceAll("");

        }

        return dest;

    }

    /**
     * 将二进制文件转换为字符串 用于到服务器端上传文件
     *
     * @param in
     * @return
     */
    public static String fileToString(FileInputStream in) {
        String data = "";
        try {
            if (in != null) {
                byte[] buffer = new byte[in.available()];

                data = Base64.encodeToString(buffer, 0);
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static boolean stringToFile(String path, byte[] byteArr) throws IOException {
        File file = new File(path);
        FileOutputStream os = new FileOutputStream(file);
        os.write(byteArr);
        os.flush();
        os.close();
        return true;
    }

    /**
     * @param path 文件路径
     * @param len  文件长度
     * @return bitmap对象和长度
     * @deprecated
     */
    public static Bitmap getBitmap(String path, StringBuffer len) {
        File file = new File(path);
        if (!isImg(file.getName()))
            return null;
        Bitmap bitmap = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            len.append(stream.available() + "");
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            stream = null;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 加载本地指定路径中的图片
     *
     * @param path 图片路径
     * @return 图片
     */
    public static Bitmap getBitmap(String path) {
        File file = new File(path);
        if (!isImg(file.getName()))
            return null;
        Bitmap bitmap = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(stream, null, options);
            stream.close();
            stream = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取缩略图
     *
     * @param path
     * @return
     */
    public static Bitmap getThumbnailBitmap(String path) {
        File file = new File(path);
        if (!isImg(file.getName()))
            return null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, opt);
            opt.inSampleSize = computeSampleSize(opt, -1, 128 * 96);
            opt.inJustDecodeBounds = false;
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, opt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 计算一个合适的SampleSize
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }

        } else {
            roundedSize = (initialSize + 7) / 8 * 8;

        }
        return roundedSize;

    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 生成缩略图
     *
     * @param fileInfo 图片文件
     * @return 返回指定文件生成的缩略图bitmap对象
     */
    public static Bitmap generateThum(Object fileInfo) {
        Bitmap bitmap = null;
        File file = getFile(fileInfo);
        if (file == null || !isImg(file.getName()))
            return null;
        try {
            FileInputStream stream = new FileInputStream(file);
            FileUtils.length = stream.available();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // 获取原图片的宽和高
            bitmap = BitmapFactory.decodeStream(stream, null, options);
            // Bitmap bitmap =
            // BitmapFactory.decodeFile(path+"/"+fileItem.getName(), options);
            // //此时返回bm为空
            options.inJustDecodeBounds = false;
            // 计算缩放比
            int be = (int) (options.outHeight / (float) 200);
            // 判断是否超过原始图片高度 //如果超过，则不进行缩放
            if (be <= 0)
                be = 1;
            options.inSampleSize = be;
            // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
            // bitmap=BitmapFactory.decodeStream(stream, null, options);
            stream.close();
            stream = null;
            bitmap = BitmapFactory.decodeFile(file.getPath(), options);
            // int w = bitmap.getWidth();
            // int h = bitmap.getHeight();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 读取路径下的图片
     *
     * @param fileInfo 图片文件路径
     * @param size     文件限定大小
     * @return 返回指定文件生成的缩略图bitmap对象
     */
    public static Bitmap generateBitmap(Object fileInfo, int size) {
        Bitmap bitmap = null;
        File file = getFile(fileInfo);
        if (file == null || !isImg(file.getName()))
            return null;
        try {
            FileInputStream stream = new FileInputStream(file);
            FileUtils.length = stream.available();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(stream, null, options);

            options.inJustDecodeBounds = false;
            if (file.length() > size) {
                options.inSampleSize = (int) (file.length() / size) + 1;
            } else {
                options.inSampleSize = 1;
            }

            stream.close();
            stream = null;
            bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将bitmap存储到文件 且当文件不存在时
     *
     * @param bitmap   图片对象
     * @param fileInfo 图片文件要存储的位置
     * @return 是否存储成功
     * @throws IOException 文件异常
     */
    public static boolean saveBitmap(Bitmap bitmap, Object fileInfo) throws IOException {
        File file = getFile(fileInfo);

        if (!file.exists()) {
            file.createNewFile();
            return saveBitmapHandler(file, bitmap);
        }
        return false;
    }

    /**
     * 将bitmap存储到文件 且当文件存在时
     *
     * @param bitmap   要存储的位图对象
     * @param fileInfo 存储到该文件
     * @return
     * @throws IOException
     */
    public static boolean saveBitmap2(Bitmap bitmap, Object fileInfo) throws IOException {
        File file = getFile(fileInfo);

        if (file.exists()) {
            return saveBitmapHandler(file, bitmap);
        } else {
            file.createNewFile();
            return saveBitmapHandler(file, bitmap);
        }

    }

    /**
     * 将bitmap存储到文件
     *
     * @param file   存储到该文件
     * @param bitmap 要存储的位图对象
     * @return
     */
    public static boolean saveBitmapHandler(File file, Bitmap bitmap) {
        try {
            /*
             * FileInputStream stream=null; FileOutputStream fOut=null;
			 */

            FileOutputStream fOut = new FileOutputStream(file);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);// 压缩
                // 100压缩质量
            }
            fOut.flush();
            fOut.close();
            fOut = null;
        } catch (FileNotFoundException e) {
            Log.e("FileUtils", "savebitmap  FileNotFoundException");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e("FileUtils", "savebitmap IOException");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param pathName 图片数据
     */
    public static void photoHandler(String pathName) {
        try {
            File file = new File(pathName);
            if (!file.exists() || !file.canRead() || file.canWrite()) {
                return;
            }
            if (file.length() > Runtime.getRuntime().freeMemory()) {
                System.gc();
                return;
            }
            // MemoryFile memoryFile=new MemoryFile(pathName, (int)
            // file.length());
            // FileInputStream in=(FileInputStream) memoryFile.getInputStream();
            //

            FileInputStream in = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            in.read(data);
            in.close();

            // Bitmap $bitmap = BitmapFactory.decodeByteArray(data, 0,
            // data.length);
            BitmapDrawable girl = new BitmapDrawable(in);
            // 通过BitmapDrawable对象获取Bitmap对象15
            Bitmap bitmap = girl.getBitmap();
            // 利用Bitmpa对象创建缩略图17
            Bitmap $bitmap = ThumbnailUtils.extractThumbnail(bitmap, 320, 480, 12);

			/*
             * int sizew=480; int sizeh=800; float scaleWidth = ((float) sizew)
			 * / $bitmap.getWidth(); float scaleHeight = ((float) sizeh) /
			 * $bitmap.getHeight(); Matrix matrix = new Matrix();
			 * matrix.postScale(scaleWidth, scaleHeight); // Bitmap
			 * resizedBitmap = Bitmap.createBitmap($bitmap, 0, 0,
			 * $bitmap.getWidth(), $bitmap.getHeight(), matrix, true);
			 */

            ByteArrayOutputStream out = new ByteArrayOutputStream($bitmap.toString().length());
            $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            byte[] array = out.toByteArray();
            try {
                FileOutputStream output = new FileOutputStream(pathName);
                output.write(array);
                output.flush();
                output.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            out.flush();
            out.close();

            System.gc();
        } catch (Exception ex) {

        }
    }

    public interface ICallBack {
        void handler(boolean isSuccess);
    }

    public static void ScaleImg(final String filePath, final Handler handler) {

        new Thread() {
            public void run() {

                int i = 0;
                while (i < 10) {
                    Bitmap resizedBitmap = null, bitmapOrg = null;
                    try {
                        File file = new File(filePath);

                        if (!file.exists() || !file.canRead() || !file.canWrite()) {
                            return;
                        }

                        if (file.length() > Runtime.getRuntime().freeMemory()) {
                            System.gc();
                            // return;
                        }
                        if (file.length() / 1024 < 512) {
                            // 图片小于512kb则不进行处理
                            return;
                        }
                        FileInputStream in = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        in.read(data);
                        in.close();

                        // 获取原图片的宽和高
                        bitmapOrg = BitmapFactory.decodeByteArray(data, 0, data.length);

                        // 获取这个图片的宽和高
                        int width = bitmapOrg.getWidth();
                        int height = bitmapOrg.getHeight();

                        // 定义预转换成的图片的宽度和高度
                        int newWidth = 600;
                        int newHeight = 600;

                        // 计算缩放率，新尺寸除原始尺寸
                        float scaleWidth = ((float) newWidth) / width;
                        float scaleHeight = ((float) newHeight) / height;

                        // 创建操作图片用的matrix对象
                        Matrix matrix = new Matrix();

                        // 缩放图片动作
                        matrix.postScale(scaleWidth, scaleHeight);

                        // 旋转图片 动作
                        // matrix.postRotate(45);

                        // 创建新的图片
                        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);

                        FileUtils.saveBitmap2(resizedBitmap, filePath);
                        bitmapOrg.recycle();
                        resizedBitmap.recycle();
                        i = 15;
                    } catch (OutOfMemoryError error) {
                        i++;
                        if (null != bitmapOrg) {
                            if (!bitmapOrg.isRecycled()) {
                                bitmapOrg.recycle();
                                bitmapOrg = null;
                            }

                        }
                        if (null != resizedBitmap) {
                            if (!resizedBitmap.isRecycled()) {
                                resizedBitmap.recycle();
                                resizedBitmap = null;
                            }

                        }
                        System.gc();

                    } catch (IOException e) {
                        i = 16;
                    }
                }
                Message msg = handler.obtainMessage();
                msg.arg1 = i;
                // i==15;
                msg.sendToTarget();

                // FileUtils.photoHandler(strImgPath, 200);
            }

            ;
        }.start();

    }

    /**
     * 根据图片路径压缩图片
     *
     * @param pathName 图片路径
     * @param option   缩比
     */
    public static void photoHandler(String pathName, int option) {
        try {
            File file = new File(pathName);
            if (!file.exists() || !file.canRead() || !file.canWrite()) {
                return;
            }
            if (file.length() > Runtime.getRuntime().freeMemory()) {
                System.gc();
            }
            if (file.length() / 1024 < 256) {
                // 图片小于256kb则不进行处理
                return;
            }
            FileInputStream in = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            in.read(data);
            in.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // 获取原图片的宽和高
            Bitmap $bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            // Bitmap bitmap =
            // BitmapFactory.decodeFile(path+"/"+fileItem.getName(), options);
            // //此时返回bm为空
            options.inJustDecodeBounds = false;
            // 计算缩放比
            int be = 2;
            if (options.outHeight < options.outWidth)
                be = (int) (options.outHeight / (float) option);
            else
                be = (int) (options.outWidth / (float) option);
            // 判断是否超过原始图片高度 //如果超过，则不进行缩放
            if (be < 2)
                be = 2;
            options.inSampleSize = be;
            // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
            // bitmap=BitmapFactory.decodeStream(stream, null, options);
            in.close();
            in = null;
            $bitmap = BitmapFactory.decodeFile(file.getPath(), options);
            ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
            $bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            byte[] array = out.toByteArray();
            try {
                FileOutputStream output = new FileOutputStream(pathName);
                output.write(array);
                output.flush();
                output.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if ($bitmap != null && !$bitmap.isRecycled()) {
                    $bitmap.recycle();
                }
            }
            out.flush();
            out.close();
            file = null;
            System.gc();
        } catch (IOException ex) {
            Log.e("FileUtils", "io exception photoHandler(String ,int) from FileUtils");
        } catch (OutOfMemoryError ome) {
            Log.e("FileUtils", "OutOfMemoryError photoHandler(String ,int) from FileUtils");
        }
    }

    /**
     * 根据文件信息返回File信息
     *
     * @param fileInfo 文件信息
     * @return File对象
     */
    private static File getFile(Object fileInfo) {
        if (fileInfo instanceof String) {
            return new File(fileInfo.toString());
        } else if (fileInfo instanceof File) {
            return (File) fileInfo;
        } else {
            return null;
        }
    }

    /**
     * 判断存储卡是否已插入
     *
     * @return
     */
    public static boolean isSdcardExists() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查是否是图片文件
     *
     * @param fName 文件名称
     * @return
     */
    public static boolean isImg(String fName) {
        return fName.endsWith(".jpg") | fName.endsWith(".png") | fName.endsWith(".gif") | fName.endsWith(".bmp")
                | fName.endsWith(".JPG") | fName.endsWith(".PNG") | fName.endsWith(".GIF") | fName.endsWith(".BMP");
    }

    public static boolean containsImg(String fName) {
        return fName.contains(".jpg") | fName.contains(".png") | fName.contains(".gif") | fName.contains(".bmp")
                | fName.contains(".JPG") | fName.contains(".PNG") | fName.contains(".GIF") | fName.contains(".BMP");
    }

    /**
     * @param fName   文件名称
     * @param @return
     * @Description:检查文件是否是视频文件
     * @return:boolean
     */
    public static boolean isVideo(String fName) {
        return fName.endsWith(".mp4") | fName.endsWith(".avi") | fName.endsWith(".mov") | fName.endsWith(".rmvb")
                | fName.endsWith(".MP4") | fName.endsWith(".AVI") | fName.endsWith(".MOV") | fName.endsWith(".RMVB");
    }

    /**
     * @param fName   文件名称
     * @param @return
     * @Description:检查文件是否是音频文件
     * @return:boolean
     */
    public static boolean isAudio(String fName) {
        return fName.endsWith(".mp3") | fName.endsWith(".3gp") | fName.endsWith(".wav") | fName.endsWith(".aac")
                | fName.endsWith(".ogg") | fName.endsWith(".MP3") | fName.endsWith(".3GP") | fName.endsWith(".WAV")
                | fName.endsWith(".AAC") | fName.endsWith(".OGG");
    }

    /**
     * @param bitmap    要缩放的bitmap
     * @param newWidth  想要缩放的宽
     * @param newHeight 想要缩放的高
     * @return 缩放后指定大小的bitmap
     */
    public static Bitmap scaleImg(Bitmap bitmap, int newWidth, int newHeight) {
        if (null == bitmap) {
            return null;
        }
        Matrix matrix = new Matrix();
        // 图片当前比例
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 删除文件夹 (包含文件夹中的所有子目录及子文件)
     *
     * @param folderPath 文件夹路径
     * @return 删除成功返回true 删除失败返回 false;
     */
    public static boolean delFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            return true;
        }
        File[] childPaths = file.listFiles();

        if (childPaths == null || childPaths != null && childPaths.length == 0) {

            return file.delete();

        } else {// delete folderPath of child file

            for (File childFile : childPaths) {
                if (childFile.isDirectory()) {
                    delFolder(childFile.getAbsolutePath());
                } else if (childFile.isFile()) {
                    childFile.delete();
                }
            }
            return file.delete();
        }
    }

    /**
     * 视图转换为bitmap对象
     *
     * @param view 被转换视图
     * @return转换后bitmap对象
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    /**
     * 视图转换为bitmap对象
     *
     * @param view         被转换视图
     * @param bitmapWidth  宽度
     * @param bitmapHeight 高度
     * @return转换后bitmap对象
     */
    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));

        return bitmap;
    }

    /**
     * @author guannanYan
     * @version 1.0 c实现文件 目录 Data/RecordImpl.c c头文件目录 h/RecordNative.h
     */
    class RecordNative {

    }

    /**
     * 复制文件
     *
     * @param sourceFile 原文件
     * @param targetFile 目标文件
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);
        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);
        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();
        // 关闭流
        outBuff.close();
        output.close();
        inBuff.close();
        input.close();
    }

    /**
     * 复制整个文件夹内容 （支持层级目录）
     *
     * @param oldPath String 原文件路径 如：c:/old
     * @param newPath String 复制后路径 如：f:/new
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 在SD卡中创建文件
     *
     * @param directory 文件路径
     * @param fileName 文件名
     * @return
     */
    public static int CreateFileInSDCard(String directory, String fileName) {
        if (SDCardUtils.isExistSDCard()) {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    return 0;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return -1;
                }
            } else {
                return 0;
            }
        } else {
            // SD卡不存在
            return 1;
        }
    }

    /**
     * 将字符串写入到文本文件中
     *
     * @param strcontent  内容
     * @param strFilePath 文件路径
     */
    public static void WriteTxtFile(String strcontent, String strFilePath) {
        // 每次写入时，都换行写
        String strContent = strcontent + "\n";
        try {
            File file = new File(strFilePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description:删除Log日志文件
     * @param:@param logPath
     * 日志文件路径
     * @param:@param dateStr
     * 日期格式字符串（20161107）
     * @param:@param df
     * 格式字符串（yyyyMMdd）
     * @throws:
     */
    public static void deleteLogFile(String logPath, String dateStr, SimpleDateFormat sf) {
        // 判断目录是否存在
        File dir = new File(logPath);
        if (!dir.exists()) {
            return;
        }
        // 判断子目录是否为null
        File[] flist = dir.listFiles();
        if (flist == null || flist.length == 0) {
            return;
        }
        for (File f : flist) {
            String fileName = f.getName();
            String filePath = f.getAbsolutePath();
            // 老版本的日志 写在crash，run，dev 文件夹下
            if (fileName.equals("crash")) {
                // 删除文件
                FileUtils.delFolder(filePath);
            }
            if (fileName.equals("run")) {
                // 删除文件
                FileUtils.delFolder(filePath);
            }
            if (fileName.equals("dev")) {
                FileUtils.delFolder(filePath);
            }
            // 比较字符串
            if (fileName.length() == 8 && ValidateUtils.isInteger(fileName)) {
                if (DateUtil.compareDate(dateStr, fileName, sf)) {
                    // 删除文件
                    FileUtils.delFolder(filePath);
                }
            }
        }
    }

    /**
     * @Description:列出路径下所有的子文件
     * @param:@param filePath
     * @param:@return
     * @return:File[] 返回文件夹下所有的子文件
     * @throws:
     */
    public static File[] listChildFiles(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {// 文件不存在
            return null;
        }
        if (!file.isDirectory()) {// 不是文件夹
            return null;
        }

        File[] childFiles = file.listFiles();// 得到子文件夹列表
        if (childFiles.length == 0) {// 空文件夹
            return null;
        }
        return childFiles;
    }

}
