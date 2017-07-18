package com.alier.com.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public final class ZipFileUtils {
	private static final int BUFFER = 4096;

	private ZipFileUtils() {
	}

	/**
	 * 将SD卡上ZIP文件里面的内容复制到软件目录下
	 * 
	 * @param zipFile
	 *            ZIP路径
	 * @param targetDir
	 *            手机上的路径
	 */
	public static boolean Unzip(String zipFile, String targetDir) {
		try {
			// 文件输入流
			FileInputStream fis = new FileInputStream(zipFile);
			// ZIP文件输入流
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			// ZIP里面文件的对象
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[BUFFER];
				File entryFile = new File(targetDir + entry.getName());
				File entryDir = new File(entryFile.getParent());
				// 如果没这个文件就创建这个文件
				if (!entryDir.exists()) {
					entryDir.mkdirs();
				}
				if (entryFile.isDirectory()) {
					continue;
				}
				// 文件输出流
				FileOutputStream fos = new FileOutputStream(entryFile);
				// 缓冲输出流
				BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					bos.write(data, 0, count);
				}
				bos.flush();
				bos.close();
				fos.close();
			}
			zis.close();
			fis.close();

			return true;
		} catch (Exception e) {
			System.out.println("复制皮肤出错!" + e.toString());
		}
		return false;
	}

	/** */
	/**
	 * 定义解压缩zip文件的方法
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 */
	public static boolean unzip(String zipFileName, String outputDirectory) {
		try {
			FileInputStream inStream = new FileInputStream(zipFileName);
			ZipInputStream in = new ZipInputStream(inStream);
			// 获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry，
			// 当getNextEntry方法的返回值为null，则代表ZipInputStream中没有下一个ZipEntry，
			// 输入流读取完成；
			ZipEntry z = in.getNextEntry();
			while (z != null) {
				// 创建以zip包文件名为目录名的根目录
				File f = new File(outputDirectory);
				f.mkdir();
				if (z.isDirectory()) {
					String name = z.getName();
					name = name.substring(0, name.length() - 1);
					f = new File(outputDirectory + File.separator + name);
					f.mkdir();
				} else {
					f = new File(outputDirectory + File.separator + z.getName());
					f.createNewFile();
					FileOutputStream out = new FileOutputStream(f);
					// 缓冲输出流
					BufferedOutputStream bos = new BufferedOutputStream(out, BUFFER);
					int b;
					byte data[] = new byte[BUFFER];
					while ((b = in.read(data)) != -1) {
						bos.write(data, 0, b);
					}
					bos.flush();
					bos.close();
					out.close();
				}
				// 读取下一个ZipEntry
				z = in.getNextEntry();
			}
			in.close();
			inStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获得皮肤包下的配置文件信息
	 * 
	 * @param zipFile
	 * @return
	 */
	public static String getSkinInfo(String zipFile) {
		// 皮肤文件的配置信息
		String skinInfo = "";
		try {
			// 文件输入流
			FileInputStream fis = new FileInputStream(zipFile);
			// ZIP文件输入流
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			// ZIP里面文件的对象
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[1024];

				if (entry.getName().equals("skininfo.txt")) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					zis.skip(3);
					while ((count = zis.read(data)) != -1) {
						baos.write(data, 0, count);
					}
					skinInfo = new String(baos.toByteArray());
					baos.close();
					return skinInfo;
				} else {
					continue;
				}
			}
			zis.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return skinInfo;
	}

	public static void zipMutiFiles(List<File> files,String descDir) throws IOException{
		if(files==null||files.isEmpty()){
			return;
		}
		if(ObjectUtils.isEmpty(descDir)){
			return;
		}
		for(File file:files){
			if(!file.exists()){
				return;
			}
		}
//		//拷贝file到temp目录下
//		if(!tempFile.exists()){
//			tempFile.mkdirs();
//		}
		ZipOutputStream zos = null;
		// 创建一个zip输出流
		for(File file:files){
			try {
				zos = new ZipOutputStream(new FileOutputStream(descDir));
				startZip(zos, "", file.getAbsolutePath());
				System.out.println("压缩完毕");
			} catch (FileNotFoundException e) {
				// 压缩失败，则删除创建的文件
				File zipFile = new File(descDir);
				if (zipFile.exists()) {
					zipFile.delete();
				}
				e.printStackTrace();
			} finally {
				if (zos != null) {
					try {
						zos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 压缩文操作（支持多级目录）
	 * 
	 * @param filePath
	 *            要压缩的文件路径
	 * @param descDir
	 *            压缩文件的保存路径
	 * @throws IOException
	 */
	public static void zipFiles(String filePath, String descDir) throws IOException {
		ZipOutputStream zos = null;
		try {
			// 创建一个zip输出流
			zos = new ZipOutputStream(new FileOutputStream(descDir));
			// 启动压缩
			startZip(zos, "", filePath);
			System.out.println("压缩完毕");
		} catch (FileNotFoundException e) {
			// 压缩失败，则删除创建的文件
			File zipFile = new File(descDir);
			if (zipFile.exists()) {
				zipFile.delete();
			}
			e.printStackTrace();
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 对目录中所有文件递归遍历进行压缩
	 * 
	 * @param zos
	 *            压缩输出流
	 * @param oppositePath
	 *            在zip文件中的相对路径
	 * @param filePath
	 *            要压缩的文件路径
	 * @throws IOException
	 */
	private static void startZip(ZipOutputStream zos, String oppositePath, String filePath) throws IOException {
		File file = new File(filePath);
		if (file.isDirectory()) {// 如果是压缩目录
			File[] files = file.listFiles();// 列出所有目录
			for (int i = 0; i < files.length; i++) {
				File aFile = files[i];
				if (aFile.isDirectory()) {// 如果是目录，修改相对地址
					String newoppositePath = oppositePath + aFile.getName() + "/";
					// 压缩目录，这是关键，创建一个目录的条目时，需要在目录名后面加多一个"/"
					ZipEntry entry = new ZipEntry(newoppositePath);
					zos.putNextEntry(entry);
					zos.closeEntry();
					startZip(zos, newoppositePath, aFile.getPath());
				} else {// 如果不是目录，则进行压缩
					zipFile(zos, oppositePath, aFile);
				}
			}
		} else {// 如果是压缩文件，直接调用压缩方法进行压缩
			zipFile(zos, oppositePath, file);
		}
	}

	/**
	 * 压缩单个文件到目录中
	 * 
	 * @param zos
	 *            zip输出流
	 * @param oppositePath
	 *            在zip文件中的相对路径
	 * @param file
	 *            要压缩的文件
	 */
	private static void zipFile(ZipOutputStream zos, String oppositePath, File file) {
		// 创建一个zip条目，每个zip条目都必须是相对于跟路径
		InputStream is = null;

		try {
			ZipEntry entry = new ZipEntry(oppositePath + file.getName());
			// 将条目保存到zip压缩文件当中
			zos.putNextEntry(entry);
			// 从文件输入流当中读取数据，并将数据写到输出流当中
			is = new FileInputStream(file);
			// 这种压缩速度很快
			int length = 0;
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			while ((length = is.read(buffer, 0, bufferSize)) >= 0) {
				zos.write(buffer, 0, length);
			}

			// 以下压缩速度很慢
			// int temp=0;
			//
			// while((temp=is.read())!=-1){
			// zos.write(temp);
			// }
			zos.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 解压文件操作
	 * 
	 * @param zipFilePath
	 *            zip文件路径
	 * @param descDir
	 *            解压出来的文件保存的目录
	 */
	public static void unZiFiles(String zipFilePath, String descDir) {
		File zipFile = new File(zipFilePath);
		File pathFile = new File(descDir);

		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = null;
		InputStream in = null;
		OutputStream out = null;

		try {
			zip = new ZipFile(zipFile);
			Enumeration<?> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String zipEntryName = entry.getName();
				in = zip.getInputStream(entry);

				String outPath = (descDir + "/" + zipEntryName).replace("\\*", "/");
				// 判断路径是否存在，不存在则创建文件路径
				File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
				if (!file.exists()) {
					file.mkdirs();
				}
				// 判断文件全路径是否为文件夹,如果是上面已经创建,不需要解压
				if (new File(outPath).isDirectory()) {
					continue;
				}
				out = new FileOutputStream(outPath);

				byte[] buf = new byte[4 * 1024];
				int len;
				while ((len = in.read(buf)) >= 0) {
					out.write(buf, 0, len);
				}
				in.close();

				System.out.println("解压完毕");
			}
		} catch (Exception e) {
			System.out.println("解压失败");
			e.printStackTrace();
		} finally {
			try {
				if (zip != null) {
					zip.close();
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
