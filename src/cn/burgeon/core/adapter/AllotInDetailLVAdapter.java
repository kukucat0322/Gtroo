package cn.burgeon.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.burgeon.core.R;
import cn.burgeon.core.bean.AllotIn;
import cn.burgeon.core.bean.AllotInDetail;

/**
 * Created by Simon on 2014/4/16.
 */
public class AllotInDetailLVAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AllotInDetail> list;
    private int mLayoutRes;

    public AllotInDetailLVAdapter(Context c, ArrayList<AllotInDetail> l, int layoutRes) {
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
            holder.tmTV = (TextView) convertView.findViewById(R.id.tmTV);
            holder.colorTV = (TextView) convertView.findViewById(R.id.colorTV);
            holder.sizeTV = (TextView) convertView.findViewById(R.id.sizeTV);
            holder.outTV = (TextView) convertView.findViewById(R.id.outTV);
            holder.inTV = (TextView) convertView.findViewById(R.id.inTV);
            holder.priceTV = (TextView) convertView.findViewById(R.id.priceTV);
            holder.styleTV = (TextView) convertView.findViewById(R.id.styleTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 赋值
        holder.tmTV.setText(list.get(position).getM_PRODUCTALIAS_ID());
        holder.colorTV.setText(list.get(position).getM_ATTRIBUTESETINSTANCE_ID_VALUE1());
        holder.sizeTV.setText(list.get(position).getM_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE());
        holder.outTV.setText(list.get(position).getQTYOUT());
        holder.inTV.setText(list.get(position).getQTYIN());
        holder.priceTV.setText(list.get(position).getPRICELIST());
        holder.styleTV.setText(list.get(position).getM_PRODUCT_ID_VALUE());
        return convertView;
    }

    static class ViewHolder {
        TextView tmTV;
        TextView colorTV;
        TextView sizeTV;
        TextView outTV;
        TextView inTV;
        TextView priceTV;
        TextView styleTV;
    }
}
