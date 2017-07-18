package com.alier.com.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * 加载assets文件夹下的文件
 * @author miaowei
 *
 */
public class LoadAssetsFilesUtil {

	private Context mContext;
	private AssetManager am;
	/**某个文件夹下文件的个数*/
	public int fileCounts;
	/**存放文件列表*/
	public String[] paths;
	/**文件夹下子文件名称数组*/
	public String[] dirChilds;
	/**要读取的文件的输入流*/
	private InputStream inStream;
	/**存放文件输入流的集合*/
	private ArrayList<InputStream> inStreams = new ArrayList<InputStream>();
	/**文件夹下子文件数目*/
	private int childCounts;
	/**文件夹下子文件名称数组*/
	private String[] fileNames;
	int k = 0;
	
	public LoadAssetsFilesUtil() {
		
	}

	public LoadAssetsFilesUtil(Context mContext) {
		this.mContext = mContext;
		
		initAssetManager();
	}
	
	/**
	 * 初始化asset管理器
	 */
	private void initAssetManager(){
		am = mContext.getAssets();
	}

	/**
	 * 加载assets文件夹下的文件，单个文件小于1M的时候
	 * @param dirName assets下的一级目录名 如：help
	 * @param fileName 需要访问的文件名或者文件夹名 如：caseup/...或者caseup1.png
	 * @throws IOException 
	 */
	public ArrayList<InputStream> loadAssetsFiles_Small(String dirName, String fileName) throws IOException{
		paths = am.list(dirName+"/"+fileName);
		inStreams.clear();
		if(paths.length > 0){//fileName是二级文件夹 help/caseup/...
			fileCounts = paths.length;//记录子文件夹下文件的个数
			for(int i = 0; i<paths.length; i++){
				inStream = am.open(dirName+"/"+fileName+"/"+paths[i]);
				inStreams.add(inStream);
			}
		}else{//fileName是一级目录下的文件 help/caseup1.png
			dirChilds = am.list(dirName);
			for(int i=0; i<dirChilds.length; i++){
				if(fileName.equals(dirChilds[i])){
					break;
				}
				k++;
			}
//			if(k == dirChilds.length){
//				fileName = "icon_query_logo.png";
//			}
			inStream = am.open(dirName+"/"+fileName);
			inStreams.add(inStream);
		}
		return inStreams;
	}
	
	/**
	 * 返回指定文件夹下的文件个数
	 * @param dirName 顶级路径名称
	 * @return
	 * @throws IOException
	 */
	public int getCounts(String dirName) throws IOException {
		if(childCounts == 0){
			childCounts = am.list(dirName).length;
			return am.list(dirName).length;
		}else{
			return childCounts;
		}
		
		
	}
	
	/**
	 * 返回指定文件夹下文件列表
	 * @param dirName 顶级路径名
	 * @return
	 * @throws IOException
	 */
	public String[] getChileFileName(String dirName) throws IOException{
		if(fileNames==null){
			fileNames = am.list(dirName);
			return am.list(dirName);
		}else{
			return fileNames;
		}

	}
	
	/**
	 * 加载assets文件夹下的文件，单个文件大于1M的时候
	 * 在2.3版本之前读取apk压缩文件不能大于1M,解决办法可以先把大文件切割成一个个的小于1M的小文件
	 * 再上传到assets文件夹中,读取的时候再合并这些小文件
	 * @param dirName assets下的一级目录名
	 * @param fileName 需要访问的文件名
	 */
	public void loadAssetsFiles_Large(String dirName, String fileName){
		
	}
	
	/**
	 * 关闭文件流
	 */
	public void closeStream(){
		if(inStreams!=null && inStreams.size()>0){
			
			for(int i = 0; i<inStreams.size(); i++){
				try {
					inStreams.get(i).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
