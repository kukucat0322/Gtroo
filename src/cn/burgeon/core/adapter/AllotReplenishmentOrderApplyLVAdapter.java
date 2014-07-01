package cn.burgeon.core.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.Product;

/**
 * Created by Simon on 2014/4/16.
 */
public class AllotReplenishmentOrderApplyLVAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Product> list;
	private int mLayoutRes;

	public AllotReplenishmentOrderApplyLVAdapter(Context c, ArrayList<Product> l, int layoutRes) {
		this.mContext = c;
		this.list = l;
		this.mLayoutRes = layoutRes;
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
			convertView = LayoutInflater.from(mContext).inflate(mLayoutRes, null);
			holder = new ViewHolder();
			holder.barcodeTV = (TextView) convertView.findViewById(R.id.barcodeTV);
			holder.colorTV = (TextView) convertView.findViewById(R.id.colorTV);
			holder.sizeTV = (TextView) convertView.findViewById(R.id.sizeTV);
			holder.numET = (TextView) convertView.findViewById(R.id.numET);
			holder.styleTV = (TextView) convertView.findViewById(R.id.styleTV);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 赋值
		Product product = list.get(position);
        holder.barcodeTV.setText(product.getBarCode());
        holder.colorTV.setText(product.getColor());
        holder.sizeTV.setText(product.getSize());
        holder.numET.setText(product.getCount());
        holder.styleTV.setText(product.getStyle());
		return convertView;
	}

	static class ViewHolder {
        TextView barcodeTV;
        TextView colorTV;
        TextView sizeTV;
        TextView numET;
        TextView styleTV;
	}
}
