package  com.alier.com.androidtools.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 主布局list适配器
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年10月25日 上午11:09:54
 * @version 1.0
 */
public class MainLayoutListAdapter extends BaseAdapter {

	/**
	 * 需要展示的数据集合
	 */
	private ArrayList<String> uiLists = new ArrayList<String>();
	private Context mContext;

	public MainLayoutListAdapter(Context context, String[] lists) {
		super();
		this.mContext = context;
		for (String strlist : lists) {
			uiLists.add(strlist);
		}
	}

	@Override
	public int getCount() {
		return uiLists.size();
	}

	@Override
	public Object getItem(int position) {
		return uiLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
