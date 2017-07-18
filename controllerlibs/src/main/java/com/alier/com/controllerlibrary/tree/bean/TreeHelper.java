package com.alier.com.controllerlibrary.tree.bean;

import com.alier.com.controllerlibrary.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fugua on 2017/7/13.
 */

public class TreeHelper {


    /**
     * 过滤出所有可见的Node
     *
     * @param nodes
     * @return
     */
    public static List<Node> filterVisibleNode(List<Node> nodes) {
        List<Node> result = new ArrayList<Node>();

        for (Node node : nodes) {
            // 如果为跟节点，或者上层目录为展开状态
            if (node.isRoot() || node.isParentExpand()) {
                setNodeIcon(node);
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    public static void addNode(List<Node> nodes, Node node,
                               int defaultExpandLeval, int currentLevel) {

        nodes.add(node);
//        if (defaultExpandLeval >= currentLevel) {
//            node.setExpand(true);
//        }

        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(nodes, node.getChildren().get(i), defaultExpandLeval,
                    currentLevel + 1);
        }
    }

    public static List<Node> getRootNodes(List<Node> nodes) {
        List<Node> root = new ArrayList<Node>();
        for (Node node : nodes) {
            if (node.isRoot())
                root.add(node);
        }
        return root;
    }

    /**
     * 将我们的数据转化为树的节点
     *
     * @param datas
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static <T> List<Node> convetData2Node(List<T> datas)
            throws IllegalArgumentException, IllegalAccessException

    {
        List<Node> nodes = new ArrayList<Node>();
        Node node = null;

        for (T t : datas) {
            int id = -1;
            int pId = -1;
            String label = null;
            Object value = null;
            int textColor = -1;
            int cusIcon = -1;
            float textSize = -1;
            Class<? extends Object> clazz = t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            node = new Node();
            for (Field f : declaredFields) {
                if (f.getAnnotation(TreeNodeId.class) != null) {
                    f.setAccessible(true);
                    id = f.getInt(t);
                    node.setId(id);
                }
                if (f.getAnnotation(TreeNodePid.class) != null) {
                    f.setAccessible(true);
                    pId = f.getInt(t);
                    node.setpId(pId);
                }
                if (f.getAnnotation(TreeNodeLabel.class) != null) {
                    f.setAccessible(true);
                    label = (String) f.get(t);
                    node.setName(label);
                }
                if (f.getAnnotation(TreeNodeValue.class) != null) {
                    f.setAccessible(true);
                    value = f.get(t);
                    node.setValue(value);
                }
                if (f.getAnnotation(TreeNodeCusIcon.class) != null) {
                    f.setAccessible(true);
                    cusIcon = f.getInt(t);
                    if(cusIcon != -1){
                        node.setCustomIcon(cusIcon);
                    }
                }
                if (f.getAnnotation(TreeNodeTextColor.class) != null) {
                    f.setAccessible(true);
                    textColor = f.getInt(t);
                    node.setTextColor(textColor);
                }
                if (f.getAnnotation(TreeNodeTextSize.class) != null) {
                    f.setAccessible(true);
                    textSize = f.getFloat(t);
                    node.setTextSize(textSize);
                }
            }
            nodes.add(node);
        }

        /**
         * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
         */
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node m = nodes.get(j);
                if (m.getpId() == n.getId()) {
                    n.getChildren().add(m);
                    m.setParent(n);
                } else if (m.getId() == n.getpId()) {
                    m.getChildren().add(n);
                    n.setParent(m);
                }
            }
        }

        // 设置图片
        for (Node n : nodes) {
            setNodeIcon(n);
        }
        return nodes;
    }

    /**
     * 传入我们的普通bean，转化为我们排序后的Node
     *
     * @param datas
     * @return
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static <T> List<Node> getSortedNodes(List<T> datas,
                                                int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException

    {
        List<Node> result = new ArrayList<Node>();
        List<Node> nodes = convetData2Node(datas);
        List<Node> rootNodes = getRootNodes(nodes);
        for (Node node : rootNodes) {
            addNode(result, node, defaultExpandLevel, 1);
        }
        return result;
    }

    /**
     * 设置节点的图标
     *
     * @param node
     */
    public static void setNodeIcon(Node node) {
        if (node.getChildren().size() > 0 && node.isExpand()) {
            node.setIcon(R.drawable.tree_ex);
        } else if (node.getChildren().size() > 0 && !node.isExpand()) {
            node.setIcon(R.drawable.tree_ec);
        } else
            node.setIcon(-1);
    }
}
