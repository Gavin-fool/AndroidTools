package com.alier.com.androidtools.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alier.com.androidtools.R;
import com.alier.com.commons.entity.Menu;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2017/7/29 18:13
 * @email gavin_fool@163.com
 */
public class ImobileRecyclerViewAdapter extends RecyclerView.Adapter<ImobileRecyclerViewAdapter.viewHolder> {

    private Context mContext;
    private List<Menu> imobileModules = null;

    /**
     * @param imobileModules imobile所有子模块
     * @param mContext 上下文对象
     */
    public ImobileRecyclerViewAdapter(List<Menu> imobileModules, Context mContext) {
        this.imobileModules = imobileModules;
        this.mContext = mContext;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.main_gv_item,null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        try {
            Field field = R.drawable.class.getField(imobileModules.get(position).getIcon());
            int iconid = field.getInt(field);
            holder.ivIcon.setImageResource(iconid);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        holder.tvDes.setText(imobileModules.get(position).getCaption());
    }

    @Override
    public int getItemCount() {
        return imobileModules == null ? 0 : imobileModules.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvDes;
        private ImageView ivIcon;

        public viewHolder(View itemView) {
            super(itemView);
            tvDes = (TextView)itemView.findViewById(R.id.ItemText);
            ivIcon = (ImageView)itemView.findViewById(R.id.ItemImage);
        }
    }
}
