package com.alier.com.androidtools.ui.view_test.treelist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alier.com.androidtools.R;
import com.alier.com.controllerlibrary.tree.bean.Node;
import com.alier.com.controllerlibrary.tree.bean.TreeListViewAdapter;

import java.util.List;

/**
 * Created by fugua on 2017/7/13.
 */

public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T> {


    public SimpleTreeAdapter(ListView mTree, Context context, List<T> datas,
                             int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
    }

    @Override
    public View getConvertView(Node node, int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.id_treenode_icon);
            viewHolder.cusIcon = (ImageView) convertView.findViewById(R.id.id_treenode_custom_icon);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.id_treenode_label);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getIcon() == -1) {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }
        if(node.getCustomIcon() == -1){
            viewHolder.cusIcon.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.cusIcon.setVisibility(View.VISIBLE);
            viewHolder.cusIcon.setImageResource(node.getCustomIcon());
        }
        viewHolder.label.setText(node.getName());
        viewHolder.label.setTextSize(node.getTextSize());
        viewHolder.label.setTextColor(node.getTextColor());
        return convertView;
    }

    private final class ViewHolder {
        ImageView icon;
        ImageView cusIcon;
        TextView label;
    }


}
