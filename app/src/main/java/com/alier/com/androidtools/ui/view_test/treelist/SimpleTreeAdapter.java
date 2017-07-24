package com.alier.com.androidtools.ui.view_test.treelist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alier.com.androidtools.R;
import com.alier.com.commons.utils.T;
import com.alier.com.controllerlibrary.tree.bean.Node;
import com.alier.com.controllerlibrary.tree.bean.TreeHelper;
import com.alier.com.controllerlibrary.tree.bean.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fugua on 2017/7/13.
 */

public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T> {

    public SimpleTreeAdapter(ListView mTree, Context context, List<T> datas,
                             int defaultExpandLevel, boolean isHide) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel, isHide);
    }

    @Override
    public View getConvertView(final Node node, final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.id_treenode_icon);
            viewHolder.cusIcon = (ImageView) convertView.findViewById(R.id.id_treenode_custom_icon);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.id_treenode_label);
            viewHolder.cbCheck = (CheckBox) convertView.findViewById(R.id.cbTreenode);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (node.getIcon() == -1) {
            viewHolder.icon.setVisibility(View.GONE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }
        if (node.getCustomIcon() == -1) {
            viewHolder.cusIcon.setVisibility(View.GONE);
        } else {
            viewHolder.cusIcon.setVisibility(View.VISIBLE);
            viewHolder.cusIcon.setImageResource(node.getCustomIcon());
        }
        if (!node.isHideChecked()) {
//            if(viewHolder.level != node.getLevel()){
//                viewHolder.cbCheck.setChecked(false);
//            }
            viewHolder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!node.isChecked()&&!isChecked){
                        return;
                    }
                    TreeHelper.setNodeChecked(node, isChecked);
                    List<Node> checkedNodes = new ArrayList<Node>();
                    for (Node n : mAllNodes) {
                        if (n.isChecked()) {
                            checkedNodes.add(n);
                        }
                    }
                    onTreeNodeClickListener.onCheckChange(node, position, checkedNodes);
                    SimpleTreeAdapter.this.notifyDataSetChanged();
                }
            });
        }
        if (node.isHideChecked()) {
            viewHolder.cbCheck.setVisibility(View.GONE);
        } else {
            viewHolder.cbCheck.setVisibility(View.VISIBLE);
            setCheckBoxBg(viewHolder.cbCheck, node.isChecked());
        }
        viewHolder.label.setText(node.getName());
        viewHolder.label.setTextSize(node.getTextSize());
        viewHolder.label.setTextColor(node.getTextColor());
        return convertView;
    }

    /**
     * checkbox是否显示
     *
     * @param cb
     * @param isChecked
     */
    private void setCheckBoxBg(CheckBox cb, boolean isChecked) {
        if (isChecked) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
    }

    private final class ViewHolder {
        ImageView icon;
        ImageView cusIcon;
        TextView label;
        CheckBox cbCheck;
    }
}
