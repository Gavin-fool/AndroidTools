package com.alier.com.androidtools.adapter;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.entity.Menu;

/**
 * @Title:GridViewMenuAdapter
 * @description:主界面GridView适配器
 * @author:gavin_fool
 * @date:2016年11月4日下午4:16:59
 * @version:v1.0
 */
public class GridViewMenuAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Menu> menuList;
	private Class drawable = R.drawable.class;
	public GridViewMenuAdapter(Context context,List<Menu> menuList) {
		super();
		this.mContext = context;
		this.menuList = menuList;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return menuList.size();
	}

	@Override
	public Object getItem(int position) {
		return menuList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(null == convertView){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.main_gv_item, null);
			viewHolder.button = (Button)convertView.findViewById(R.id.ItemNew);
			viewHolder.imageView = (ImageView)convertView.findViewById(R.id.ItemImage);
			viewHolder.textView = (TextView)convertView.findViewById(R.id.ItemText);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//设置图片
		Field field;
		try {
			field = drawable.getField(menuList.get(position).getIcon());
			int iconId = field.getInt(field);
			viewHolder.imageView.setBackgroundResource(iconId);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		viewHolder.textView.setText(menuList.get(position).getCaption());
		return convertView;
	}

	class ViewHolder{
		ImageView imageView;
		Button button;
		TextView textView;
	}
}
