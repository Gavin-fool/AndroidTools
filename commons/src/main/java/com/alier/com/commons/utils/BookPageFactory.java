package com.alier.com.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

/*
 * 长文本分页实现
 */
public class BookPageFactory {

	private File book_file = null;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	//编码
	//private String m_strCharsetName = "GBK";
	private String m_strCharsetName = "UTF-8";
	//背景图片
	private Bitmap m_book_bg = null;
	//屏幕实际宽度和高度
	private int mWidth;
	private int mHeight;

	private Vector<String> m_lines = new Vector<String>();
	//文本属性，大小，颜色，背景颜色，边距
	private int m_fontSize = 24;
	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffff9e85; // 背景颜色
	private int marginWidth = 15; // 左右与边缘的距离
	private int marginHeight = 20; // 上下与边缘的距离
	//页属性
	private int mLineCount; // 每页可以显示的行数
	private float mVisibleHeight; // 绘制内容的宽 可视内容高度
	private float mVisibleWidth; // 绘制内容的宽 可视内容宽度
	private boolean m_isfirstPage,m_islastPage;

	// private int m_nLineSpaceing = 5;
	
	//声明画笔
	private Paint mPaint;

	/*
	 * 构造函数
	 * param 屏幕宽度，屏幕高度
	 * 赋值屏幕宽度，高度，画笔属性，可视内容宽度，高度，每屏显示行数
	 */
	public BookPageFactory(int w, int h) {
		// TODO Auto-generated constructor stub
		//屏幕实际宽度和高度
		mWidth = w;
		mHeight = h;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(m_fontSize);
		mPaint.setColor(m_textColor);
		//可视内容宽度和高度
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight * 2;
		/*
		 * 每屏显示行数
		 */
		mLineCount = (int) (mVisibleHeight / m_fontSize); // 可显示的行数
	}

	/*
	 * 打开要读取的文件
	 * param 要读取的文件的路径
	 * 赋值文件字节长度，建立映射到内存的缓冲文件
	 */
	public void openbook(String strFilePath) throws IOException {
		//打开文件
		book_file = new File(strFilePath);
		//获取文件的字节长度
		long lLen = book_file.length();
		m_mbBufLen = (int) lLen;
		//建立MappedByteBuffer缓冲区
		m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, lLen);
	}
	
	public void close(){
		m_mbBuf.clear();
	}
	
	/*
	 * 往回读取段落
	 * param 任意一个位置
	 * return byte[] 返回指定位置之前的一个段落
	 */
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;//当前截止位置
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) { //换行
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;//段落大小
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}


	/*
	 * 向前读取段落
	 * param 任意一个位置
	 * return byte[] 返回指定位置之后的一个段落
	 */
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// 根据编码格式判断换行
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}
	
	//下一页
	protected Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {//21,399 字节
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 向前读取段落
			m_mbBufEnd += paraBuf.length;//更新结束位置
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//是否需要换行
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}
			//读取到了末尾
			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			//从得到的文本段落中读取一屏的内容
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			//获取当前页面的末尾字符位置 m_mbBufEnd
			if (strParagraph.length() != 0) {
				try {
					//当前的显示内容截止位置
					//m_mbBufEnd = m_mbBufEnd - (strParagraph + strReturn).getBytes(m_strCharsetName).length;
					//1836=21400-19564
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}
	
	//上一页
	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		m_mbBufEnd = m_mbBufBegin;
		return;
	}
	//上一页
	public void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			m_isfirstPage=true;
			return;
		}else m_isfirstPage=false;
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
	}
	//下一页
	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage=true;
			return;
		}else m_islastPage=false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
	}
	
	public String strText(){
		StringBuilder resultStr =new StringBuilder();
		if (m_lines.size() == 0)
			m_lines = pageDown();
		if (m_lines.size() > 0) {
			for (String strLine : m_lines) {
				resultStr.append(strLine+"\n");
			}
		}
		return resultStr.toString();
	}
	//绘制页面
	public void onDraw(Canvas c) {
		if (m_lines.size() == 0)
			m_lines = pageDown();
		if (m_lines.size() > 0) {
			if (m_book_bg == null)
				c.drawColor(m_backColor);
			else
				c.drawBitmap(m_book_bg, 0, 0, null);
			int y = marginHeight;
			for (String strLine : m_lines) {
				y += m_fontSize;
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
	}
	//设置页面背景
	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}
	//是否是首页
	public boolean isfirstPage() {
		return m_isfirstPage;
	}
	//是否是末页
	public boolean islastPage() {
		return m_islastPage;
	}
}
