package com.alier.com.controllerlibrary.tree.bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fugua on 2017/7/13.
 */

public class Node {

    /**
     * 本节点ID
     */
    private int id;
    /**
     * 父节点ID，根节点pId为0
     */
    private int pId = 0;

    /**
     * 在列表中显示的名称
     */
    private String name;

    /**
     * 列表中当前item对应的取值
     */
    private Object value;
    /**
     * 当前的级别
     */
    private int level;

    /**
     * 是否展开
     */
    private boolean isExpand = false;
    /**
     * 是否选中
     */
    private boolean isChecked = false;
    /**
     * CheckBox是否隐藏
     */
    private boolean isHideChecked = true;
    /**
     * 展开折叠图标
     */
    private int icon = -1;

    /**
     * 指定列表中显示的图标,默认为-1，此时用默认图标，设置图标后使用设置的图标
     */
    private int customIcon = -1;

    /**
     * 指定item文本显示的颜色，默认为-1，此时用默认颜色黑色，设置字体大小后使用设置的大小
     */
    private int textColor = -1;

    /**
     * 指定item文本显示的大小，默认为-1，此时用默认字体14sp，设置字体大小后使用设置的大小
     */
    private float textSize = -1;
    /**
     * 下一级的子Node
     */
    private List<Node> children = new ArrayList<Node>();

    /**
     * 父Node
     */
    private Node parent;

    public Node() {
    }

    public Node(int id, int pId, String name) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    /**
     * @param id
     *            ID
     * @param pId
     *            父ID
     * @param name
     *            显示名称
     * @param customIcon
     *            图标
     * @param textColor
     *            显示文本颜色
     * @param textSize
     *            显示文本大小
     * @param value
     *            对应值
     */
    public Node(int id, int pId, String name, int customIcon, int textColor, float textSize, Object value) {
        this.customIcon = customIcon;
        this.id = id;
        this.name = name;
        this.pId = pId;
        this.textColor = textColor;
        this.textSize = textSize;
        this.value = value;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(int customIcon) {
        this.customIcon = customIcon;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {
            for (Node node : children) {
                node.setExpand(isExpand);
            }
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isHideChecked() {
        return isHideChecked;
    }

    public void setHideChecked(boolean hideChecked) {
        isHideChecked = hideChecked;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * 是否为跟节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    /**
     * 是否是叶子界点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 获取level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    @Override
    public String toString() {
        return "Node [name=" + name + ", isExpand=" + isExpand + ", children=" + children.size() + ", parent="
                + (parent == null ? "" : parent.name) + ", icon=" + icon + "]";
    }
}
