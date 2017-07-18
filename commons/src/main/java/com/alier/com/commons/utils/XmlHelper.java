package com.alier.com.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/* 需要Dom4j支持 */

/**
 * 
 * <p>
 * xml解析帮助类
 * </p>
 * 
 * @version 6.5
 * 
 * @creation 2012-6-27
 * 
 * @author 风云再会
 * 
 * @since 6.0
 */
public class XmlHelper {
	/**
	 * 
	 * <p>
	 * 按照CLASSPATH路径解析XML
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param strResource
	 *            classpath路径
	 * @return 文档对象
	 */
	public static Document parseXml(String strResource) {
		InputStream is = new ByteArrayInputStream(strResource.getBytes());
		Document doc = null;
		try {
			doc = SAXReader.class.newInstance().read(is);
		} catch (DocumentException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return doc;
	}

	/**
	 * 
	 * <p>
	 * 按照物理路径解析XML
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param path
	 *            物理路径
	 * @return 文档对象
	 */
	public static Document parseXmlPath(String path) throws Exception{
		Document doc = null;
		try {
			doc = SAXReader.class.newInstance().read(new File(path));
		} catch (DocumentException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return doc;
	}

	/**
	 * 
	 * <p>
	 * 按照物理路径解析XML
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param path
	 *            物理路径
	 * @param encoding
	 *            编码格式
	 * @return 文档对象
	 */
	public static Document parseXmlPath(String path, String encoding) {
		Document doc = null;
		try {
			SAXReader rd = SAXReader.class.newInstance();

			FileReader fr = new FileReader(path);
			while (!fr.ready()) {
			}
			doc = rd.read(fr);
			fr.close();
		} catch (DocumentException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 
	 * <p>
	 * 创建XML文档
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param strPath
	 *            文件生成的物理路径
	 * @param strContent
	 *            XML内容
	 * @return 是否创建成功
	 */
	public static boolean writeXml(String strPath, String strContent) {
		boolean blnFlag = false;
		try {
			File file = new File(strPath);
			FileWriter fw = new FileWriter(file);
			fw.write(strContent);
			fw.close();
			return true;
		} catch (FileNotFoundException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			e.printStackTrace();
		}
		return blnFlag;
	}

	/**
	 * 
	 * <p>
	 * 解析XML字符串
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param strXml
	 *            xml字符串
	 * @return 文档对象
	 */
	public static Document parseXmlStr(String strXml) {
		Document doc = null;
		try {
			/*
			 * 处理XML字符串前言中有内容的问题 edit by 风云再会 2013-05-07
			 */
			int index = strXml.indexOf("<");
			if (index > 0) {
				// 临时将前言内容写入日志
				LogMgr.info("XmlHelper", strXml.substring(0, strXml.indexOf("<")));
				strXml = strXml.substring(strXml.indexOf("<"), strXml.length());
			}
			doc = DocumentHelper.parseText(strXml);
		} catch (DocumentException e) {
			LogMgr.error("XmlHelper", e.getMessage());
			//e.printStackTrace();
			return null;
		}
		return doc;
	}

	/**
	 * 
	 * <p>
	 * 获取Element对象
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 *            文档对象
	 * @param strXPath
	 *            XPath路径
	 * @return
	 */
	public static Element getSingleElement(Document doc, String strXPath) {
		return (Element) doc.selectSingleNode(strXPath);
	}

	/**
	 * 
	 * <p>
	 * 获取Element对象集
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 *            文档对象
	 * @param strXPath
	 *            XPath路径
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getElements(Document doc, String strXPath) {
		return (List<Element>) doc.selectNodes(strXPath);
	}

	/**
	 * 
	 * <p>
	 * 获取Element对象集
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param element
	 *            元素对象
	 * @param strXpath
	 *            XPath路径
	 * @return 元素对象集合
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getElements(Element element, String strXpath) {
		return (List<Element>) element.selectNodes(strXpath);
	}

	/**
	 * 
	 * <p>
	 * 获取当前元素的下一级元素
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param element
	 *            元素对象
	 * @return 元素对象集合
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getChildrens(Element element) {
		return (List<Element>) element.elements();
	}

	/**
	 * 
	 * <p>
	 * 获取Element对象的属性
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param element
	 *            元素对象
	 * @param strAttribute
	 *            属性名称
	 * @return 属性值
	 */
	public static String getAttribute(Element element, String strAttribute) {
		return element.attributeValue(strAttribute);
	}

	/**
	 * 
	 * <p>
	 * 获取Element对象的属性
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 *            文档对象
	 * @param strXPath
	 *            XPath路径
	 * @param strAttribute
	 *            属性名称
	 * @return 属性值
	 */
	public static String getAttribute(Document doc, String strXPath, String strAttribute) {
		return ((Element) doc.selectSingleNode(strXPath)).attributeValue(strAttribute);
	}

	/**
	 * 
	 * <p>
	 * 获取元素对象的节点值
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param element
	 *            元素对象
	 * @return 节点值
	 */
	public static String getText(Element element) {
		return element.getTextTrim();
	}

	/**
	 * 
	 * <p>
	 * 获取元素对象的节点值
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 *            文档对象
	 * @param strXPath
	 *            XPath
	 * @return
	 */
	public static String getText(Document doc, String strXPath) {
		return ((Element) doc.selectSingleNode(strXPath)).getTextTrim();
	}

	/**
	 * 
	 * <p>
	 * 根据属性值查询元素对象
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 *            文档对象
	 * @param strXPath
	 *            XPath路径
	 * @param attrName
	 *            属性名称
	 * @param attrValue
	 *            属性值
	 * @return 元素对象集合
	 */
	public static List<Element> getElementsByAttrValue(Document doc, String strXPath, String attrName, String attrValue) {
		List<Element> list = getElements(doc, strXPath);
		List<Element> returnList = new ArrayList<Element>();
		for (Element element : list) {
			if (getAttribute(element, attrName).equals(attrValue)) {
				returnList.add(element);
			}
		}
		return returnList;
	}

	/**
	 * 
	 * <p>
	 * 删除指定路径下指定位置的节点
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 * @param strPath
	 * @param seq
	 * @return
	 */
	public static Document deleteElementBySeq(Document doc, String strPath, int seq) {
		List list = doc.selectNodes(strPath);
		if (null != list && list.size() > 0) {
			Element element = (Element) list.get(seq);
			boolean b = element.getParent().remove(element);
		}
		return doc;
	}

	/**
	 * 
	 * <p>
	 * 指定节点增加一个子节点
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 * @param strPath
	 * @param element
	 * @return
	 */
	public static Document addElement(Document doc, String strPath, Element element) {
		Element parentElement = (Element) doc.selectSingleNode(strPath);
		parentElement.add(element);

		return doc;
	}

	/**
	 * 
	 * <p>
	 * 更新指定节点的属性
	 * </p>
	 * 
	 * @creation 2012-6-27
	 * 
	 * @author 风云再会
	 * 
	 * @param doc
	 * @param strPath
	 * @param strName
	 * @param strValue
	 * @return
	 */
	public static Document updateElement(Document doc, String strPath, String strName, String strValue) {
		Element parentElement = (Element) doc.selectSingleNode(strPath);
		Attribute attribute = parentElement.attribute(strName);
		attribute.setValue(strValue);
		return doc;
	}

	/**
	 * 判断xml文件是否完整
	 * @param file XML文件
	 * @return 完整 true 不完整 false
	 */
	public static boolean isXMLComplete(File file){
		org.w3c.dom.Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (Exception e) {
			return false;
		}finally{
			doc = null;
		}
		return true;
	}

	public static ArrayList<HashMap<String, String>> findXmlChildListVal(
			Object xmlObj, String levelPath, String filter) {
		if (xmlObj == null || levelPath == null) {
			return null;
		}
		ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
		org.dom4j.io.SAXReader reader = new SAXReader();
		InputStream in = null;
		org.dom4j.Document doc = null;
		try {
			if (xmlObj instanceof String) {
				in = new ByteArrayInputStream((xmlObj + "").getBytes());
			} else if (xmlObj instanceof File) {
				in = new FileInputStream((File) xmlObj);
			} else if (xmlObj instanceof FileInputStream) {
				in = (FileInputStream) xmlObj;
			}
			doc = reader.read(in);
			List node = null;
			if (filter == null) {
				node = doc.selectNodes(levelPath);
			} else {
				node = doc.selectNodes(levelPath + "[@" + filter + "]\"");
			}
			for (int i = 0; i < node.size(); i++) {
				org.dom4j.Element e = (org.dom4j.Element) node.get(i);
				if (e.elements().size() > 0) {
					List node2 = e.elements();
					HashMap map;
					for (int j = 0; j < node2.size(); j++) {
						org.dom4j.Element e2 = (org.dom4j.Element) node2.get(j);
						List as = e2.attributes();
						map = new HashMap<String, String>();
						for (int l = 0; l < as.size(); l++) {
							org.dom4j.Attribute item = (Attribute) as.get(l);
							map.put(item.getName(), item.getValue());
						}
						maps.add(map);
					}
				}

			}
			if (maps.size() != 0) {
				return maps;
			} else {
				return null;
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
}
