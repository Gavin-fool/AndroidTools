package  com.alier.com.androidtools.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author 作者 : gavin_fool
 * @date 创建时间：2017年2月3日 上午11:52:32
 * @version 1.0
 */
public class SlidingMenuAdapter extends BaseAdapter {

    private Context mContext;
    private String[] slidingMenu;
	public SlidingMenuAdapter(Context context,String[] slidingMenu) {
		this.mContext = context;
        this.slidingMenu = slidingMenu;
	}
	
	@Override
	public int getCount() {
		return slidingMenu.length;
	}

	@Override
	public Object getItem(int position) {
		return slidingMenu[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if(null == convertView){
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1,null);
        }
        TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
        String[] item = slidingMenu[position].split(",");
        tv.setText(item[0]);
		tv.setTextSize(16f);
		return convertView;
	}
}
