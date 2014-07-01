package cn.burgeon.core.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.AllotOutQuery;

/**
 * Created by Simon on 2014/4/16.
 */
public class AllotOutQueryLVAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<AllotOutQuery> list;
	private int mLayoutRes;

	public AllotOutQueryLVAdapter(Context c, ArrayList<AllotOutQuery> l, int layoutRes) {
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
			holder.idTV = (TextView) convertView.findViewById(R.id.idTV);
			holder.docnoTV = (TextView) convertView.findViewById(R.id.docnoTV);
			holder.uploadStateTV = (TextView) convertView.findViewById(R.id.uploadStateTV);
			holder.billdateTV = (TextView) convertView.findViewById(R.id.billdateTV);
			holder.cdestidTV = (TextView) convertView.findViewById(R.id.cdestidTV);
			holder.totqtyoutTV = (TextView) convertView.findViewById(R.id.totqtyoutTV);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 赋值
		holder.idTV.setText(list.get(position).getID());
		holder.docnoTV.setText(list.get(position).getDOCNO());
		holder.billdateTV.setText(list.get(position).getBILLDATE());
		holder.cdestidTV.setText(list.get(position).getC_DEST_ID());
		holder.totqtyoutTV.setText(list.get(position).getTOT_QTYOUT());
		return convertView;
	}

	static class ViewHolder {
		TextView idTV;
		TextView docnoTV;
		TextView uploadStateTV;
		TextView billdateTV;
		TextView cdestidTV;
		TextView totqtyoutTV;
	}
}
