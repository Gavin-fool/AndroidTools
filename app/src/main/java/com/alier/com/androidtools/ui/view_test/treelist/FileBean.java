package com.alier.com.androidtools.ui.view_test.treelist;

import com.alier.com.controllerlibrary.tree.bean.TreeNodeCusIcon;
import com.alier.com.controllerlibrary.tree.bean.TreeNodeId;
import com.alier.com.controllerlibrary.tree.bean.TreeNodeLabel;
import com.alier.com.controllerlibrary.tree.bean.TreeNodePid;
import com.alier.com.controllerlibrary.tree.bean.TreeNodeTextColor;
import com.alier.com.controllerlibrary.tree.bean.TreeNodeTextSize;
import com.alier.com.controllerlibrary.tree.bean.TreeNodeValue;

/**
 * Created by fugua on 2017/7/13.
 */

public class FileBean {
    @TreeNodeId
    private int _id;
    @TreeNodePid
    private int parentId;
    @TreeNodeLabel
    private String name;
    @TreeNodeValue
    private String value;
    @TreeNodeTextColor
    private int textColor;
    @TreeNodeTextSize
    private float textSize;
    @TreeNodeCusIcon
    private int icon;

    public FileBean(int _id, int parentId, String name, String value, int color, float size, int icon) {
        super();
        this._id = _id;
        this.parentId = parentId;
        this.name = name;
        this.value = value;
        this.textColor = color;
        this.textSize = size;
        this.icon = icon;
    }
}
