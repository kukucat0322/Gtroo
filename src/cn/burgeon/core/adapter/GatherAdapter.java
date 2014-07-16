package cn.burgeon.core.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.ui.check.ShelfData;

/**
 * Created by Simon on 2014/4/16.
 */
public class GatherAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<ShelfData> list;

	public GatherAdapter(Context c, ArrayList<ShelfData> l) {
		this.mContext = c;
		this.list = l;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.gather_item, null);
			holder = new ViewHolder();
			holder.barcodeTV = (TextView) convertView.findViewById(R.id.barcodeTV);
			holder.countTV = (TextView) convertView.findViewById(R.id.countTV);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 赋值
		ShelfData shelfData = list.get(position);
		holder.barcodeTV.setText(shelfData.getBarcode());
		holder.countTV.setText(shelfData.getCount());
		return convertView;
	}

	static class ViewHolder {
		TextView barcodeTV;
		TextView countTV;
	}

}
