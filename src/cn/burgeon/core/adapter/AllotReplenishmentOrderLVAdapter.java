package cn.burgeon.core.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.AllotReplenishmentOrder;

/**
 * Created by Simon on 2014/4/16.
 */
public class AllotReplenishmentOrderLVAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AllotReplenishmentOrder> list;
    private int mLayoutRes;

    public AllotReplenishmentOrderLVAdapter(Context c, ArrayList<AllotReplenishmentOrder> l, int layoutRes) {
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
            holder.docnoTV = (TextView) convertView.findViewById(R.id.docnoTV);
            holder.uploadStateTV = (TextView) convertView.findViewById(R.id.uploadStateTV);
            holder.billdateIV = (TextView) convertView.findViewById(R.id.billdateIV);
            holder.cdestidIV = (TextView) convertView.findViewById(R.id.cdestidIV);
            holder.statuserTV = (TextView) convertView.findViewById(R.id.statuserTV);
            holder.descTV = (TextView) convertView.findViewById(R.id.descTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 赋值
        AllotReplenishmentOrder allotReplenishmentOrder = list.get(position);
        holder.docnoTV.setText(allotReplenishmentOrder.getDOCNO());
        holder.uploadStateTV.setText(allotReplenishmentOrder.getUPLOAD_STATUS());
        holder.billdateIV.setText(allotReplenishmentOrder.getDOCDATE());
        holder.cdestidIV.setText(allotReplenishmentOrder.getOUT_STORE());
        holder.statuserTV.setText(allotReplenishmentOrder.getAPPLY_PEOPLE());
        holder.descTV.setText(allotReplenishmentOrder.getREMARK());
        return convertView;
    }

    static class ViewHolder {
        TextView docnoTV;
        TextView uploadStateTV;
        TextView billdateIV;
        TextView cdestidIV;
        TextView statuserTV;
        TextView descTV;
    }

	public void setList(ArrayList<AllotReplenishmentOrder> lists) {
		list = lists;
        notifyDataSetChanged();
	}
}
