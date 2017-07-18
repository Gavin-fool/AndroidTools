package com.alier.com.controllerlibrary.tree.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by fugua on 2017/7/13.
 */

public abstract class TreeListViewAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<Node> mNodes;
    protected LayoutInflater mInflater;
    protected List<Node> mAllNodes;

    private OnTreeNodeClickListener onTreeNodeClickListener;

    public interface OnTreeNodeClickListener {
        void onClick(Node node, int position);
    }

    public void setOnTreeNodeClickListener(
            OnTreeNodeClickListener onTreeNodeClickListener) {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }

    /**
     * @param mTree
     * @param context
     * @param datas
     * @param defaultExpandLevel 默认展开几级树
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public TreeListViewAdapter(ListView mTree, Context context, List<T> datas,
                               int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException {
        mContext = context;
        mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);
        mNodes = TreeHelper.filterVisibleNode(mAllNodes);
        mInflater = LayoutInflater.from(context);

        mTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                expandOrCollapse(position);

                if (onTreeNodeClickListener != null) {
                    onTreeNodeClickListener.onClick(mNodes.get(position),
                            position);
                }
            }
        });
    }

    /**
     * 相应ListView的点击事件
     *
     * @param position
     */
    public void expandOrCollapse(int position) {
        Node n = mNodes.get(position);

        if (n != null)// 排除传入参数错误异常
        {
            if (!n.isLeaf()) {
                n.setExpand(!n.isExpand());
                mNodes = TreeHelper.filterVisibleNode(mAllNodes);
                notifyDataSetChanged();// 刷新视图
            }
        }
    }

    @Override
    public int getCount() {
        return mNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        Node node = mNodes.get(position);
        convertView = getConvertView(node, position, convertView, parent);
        convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
        return convertView;
    }

    public abstract View getConvertView(Node node, int position, View convertView,
                                        ViewGroup parent);
}
