package com.alier.com.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alier.com.commons.annotation.Mark;

/**
 * @author gavin_fool
 *         将过make标注的对象  到xml或json格式的转化
 */
public class BeanUtil {
    public static final int TASKID = 21;
    public static final int UPLOADDATE = 15;
    public String sTaskid, sUploaddate;

    public BeanUtil() {
        super();
    }

    public final static String QUOT = "\"";

    /**
     * @param obj
     * @return
     */
    public static String toXML(Object obj) {
        Class<?> objClass = obj.getClass();
        StringBuffer objXmlStr = new StringBuffer("<").append(objClass.getName()).append(" ");
        try {
            for (Method method : objClass.getDeclaredMethods()) {
                Mark mark = method.getAnnotation(Mark.class);
                if (mark != null) {
                    objXmlStr.append(mark.value()).append("=\"").append(method.invoke(obj, null)).append("\"");
                }
            }
            objXmlStr.append("/>");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return objXmlStr.toString();
    }

    /**
     * 按类字段获取相关信息，但字段必须是 public违反了开放封闭原则，故不推荐使用
     *
     * @param obj
     * @return
     */
    public static String toXML2(Object obj) {
        StringBuffer sb = new StringBuffer("<P ");
        Class<?> objClass = obj.getClass();
        try {
            for (Field field : objClass.getDeclaredFields()) {
                Mark mark = field.getAnnotation(Mark.class);
                String str = field.get(obj).toString();
                sb.append(mark.value()).append("=\"").append(str).append("\"");
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 该方法已测试
     *
     * @param list
     * @return javaScriptObjectNonation
     */
    public static String toJSON(List<?> list) {
        StringBuffer objJsonData = new StringBuffer("[");
        try {
            for (Object obj : list) {
                try {
                    Class<?> objClass = obj.getClass();
                    objJsonData.append("{");
                    for (Method method : objClass.getDeclaredMethods()) {
                        Mark mark = method.getAnnotation(Mark.class);//如etcid
                        if (mark != null) {
                            objJsonData.append(QUOT + mark.value() + QUOT)
                                    .append(":");
                            String methodVal = "";
                            if (method.invoke(obj, null) != null) {
                                methodVal = method.invoke(obj, null).toString();//如73
                            } else {
                                methodVal = "";
                            }
                            if (isJSON(methodVal)) {
                                objJsonData.append(methodVal);
                            } else {
                                objJsonData.append(QUOT + methodVal + QUOT);
                            }
                            objJsonData.append(",");
                        }
                    }
                    objJsonData.replace(objJsonData.length() - 1, objJsonData.length(), "");
                    objJsonData.append("},");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            objJsonData.replace(objJsonData.length() - 1, objJsonData.length(), "]");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("BeanUtil", ex.toString());
        }
        return objJsonData.toString();
    }


    /**
     * @param obj
     * @return
     */
    public static String toJSON(Object obj) {
        StringBuffer objJsonData = new StringBuffer("{");
        Class<?> objClass = obj.getClass();
        try {
            for (Method method : objClass.getDeclaredMethods()) {
                Mark mark = method.getAnnotation(Mark.class);
                if (mark != null) {
                    objJsonData.append(QUOT + mark.value() + QUOT)
                            .append(":");
                    String methodVal = method.invoke(obj, null).toString();
                    if (isJSON(methodVal)) {
                        objJsonData.append(methodVal);
                    } else {
                        objJsonData.append(QUOT + methodVal + QUOT);
                    }
                    objJsonData.append(",");
                }
            }
            objJsonData.replace(objJsonData.length() - 1, objJsonData.length(), "}");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return objJsonData.toString();
    }

    /**
     * ֻ֧�ּ�POJO  ��֧�ְ���϶����POJO
     *
     * @param jsonData  jsonֵ
     * @param classInfo POJO����Ϣ
     * @return
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List<?> toList(String jsonData, Class<?> classInfo) throws JSONException,
            IllegalAccessException, InstantiationException {
        if (null == jsonData || "".equals(jsonData.trim())) {
            return null;
        }
        List<Object> list = new ArrayList<Object>();
        Field[] fields = classInfo.getFields();
        JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            //�¶���
            Object newObj = classInfo.newInstance();
            for (Field field : fields) {
                //�ֶθ�ֵ
                if (jsonObj.has(field.getName())) {
                    Object val = jsonObj.get(field.getName());
                    if (field.getType() == boolean.class) {
                        if ("true".equalsIgnoreCase(val + "".trim())) {
                            field.set(newObj, true);
                        } else {
                            field.set(newObj, false);
                        }
                        //field.set(newObj, );
                    }
                }
            }
            list.add(newObj);
        }
        return list;
    }

    /**
     * 检查是否是json串
     *
     * @param jsonObj    jsonֵ
     * @param newObj     Ҫ��ֵ�Ķ���
     * @param field      ��������
     * @param fieldClass �����������ͼ���
     * @return ��ֵ����true�����򷵻�false��
     * @throws IllegalArgumentException
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static boolean fieldObjSetVal(JSONObject jsonObj, Object newObj, Field field,
                                          HashMap<String, Class<? extends Object>> fieldClass) throws IllegalArgumentException,
            JSONException, IllegalAccessException, InstantiationException {
        //��������
        if (jsonObj.getString(field.getName()).trim().startsWith("[")) {
            if (null == fieldClass) {
                return false;
            }
            if (fieldClass.containsKey(field.getName())) {
                //��ȡ���������Ե�����
                Class<? extends Object> fieldType = (Class<? extends Object>) fieldClass.get(field.getName());
                //��ɶ���������ʵ��
                Object fieldNewObj = fieldType.newInstance();
                //����������Ը�ֵ
                fieldNewObj = toList(jsonObj.getString(field.getName().trim()), fieldType, fieldClass);
                //��ǰ���Ը�ֵ
                field.set(newObj, fieldNewObj);
            }
            //����
        } else if (jsonObj.getString(field.getName()).trim().startsWith("{")) {
            if (null == fieldClass) {
                return false;
            }
            if (fieldClass.containsKey(field.getName())) {
                //��ȡ���������Զ�������
                Class<? extends Object> fieldType = (Class<? extends Object>) fieldClass.get(field.getName());
                //��ɶ��������Զ���ʵ��
                Object fieldNewObj = fieldType.newInstance();
                //����������Զ���ֵ
                fieldNewObj = toObj(jsonObj.getString(field.getName().trim()), fieldType, fieldClass);
                //��ǰ���Ը�ֵ
                field.set(newObj, fieldNewObj);
            }
            //������
        } else {
            Object val = jsonObj.get(field.getName());
            if (field.getType() == boolean.class) {
                if ("true".equalsIgnoreCase(val + "".trim())) {
                    field.set(newObj, true);
                } else {
                    field.set(newObj, false);
                }
            } else {
                field.set(newObj, jsonObj.get(field.getName()));
            }
            //JLS  d;
        }
        return true;
    }

    /**
     * @param jsonData   json���
     * @param classInfo  json��ݶ�Ӧ��POJO����
     * @param fieldClass POJO��϶������ͼ���
     * @return �Ѽ�����ݵ�POJOʵ�����
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws JSONException
     */
    public static Object toObj(String jsonData, Class<? extends Object> classInfo, HashMap<String, Class<? extends Object>> fieldClass) throws IllegalAccessException, InstantiationException, JSONException {
        if (null == jsonData || "".equals(jsonData.trim())) {
            return null;
        }
        Field[] fields = classInfo.getDeclaredFields();
        Object newObj = classInfo.newInstance();
        JSONObject jsonObj = new JSONObject(jsonData);
        for (Field field : fields) {
            //�ֶθ�ֵ
            if (jsonObj.has(field.getName())) {
                if (!fieldObjSetVal(jsonObj, newObj, field, fieldClass)) {
                    //��ֵ�쳣
                    continue;
                }
                ;
            }
        }
        return newObj;
    }

    /**
     * @param jsonData   json���
     * @param classInfo  json��ݶ�Ӧ��POJO����
     * @param fieldClass POJO��϶������ͼ���
     * @return �Ѽ�����ݵ�POJOʵ����󼯺�
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List<? extends Object> toList(String jsonData, Class<? extends Object> classInfo, HashMap<String, Class<? extends Object>> fieldClass) throws JSONException, IllegalAccessException, InstantiationException {
        if (null == jsonData || "".equals(jsonData.trim())) {
            return null;
        }
        List<Object> list = new ArrayList<Object>();
        Field[] fields = classInfo.getDeclaredFields();
        JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            //�¶���
            Object newObj = classInfo.newInstance();
            for (Field field : fields) {
                //�ֶθ�ֵ
                if (jsonObj.has(field.getName())) {
                    if (!fieldObjSetVal(jsonObj, newObj, field, fieldClass)) {
                        //��ֵ�쳣
                        continue;
                    }
                    ;
                }
            }
            list.add(newObj);
        }
        return list;
    }

    /**
     * 判断是否是JSON串
     *
     * @param val
     * @return
     */
    public static boolean isJSON(String val) {
        if (val == null || "".equals(val)) {
            return false;
        }
        return val.trim().startsWith("{") && val.trim().endsWith("}")
                || val.trim().startsWith("[") && val.trim().endsWith("]");
    }

    public static String MapToJson(HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder("{");
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            sb.append("\"" + key.toString() + "\":\"" + val.toString() + "\",");
        }
        return sb.subSequence(0, sb.length() - 1).toString() + "}";
    }
}
