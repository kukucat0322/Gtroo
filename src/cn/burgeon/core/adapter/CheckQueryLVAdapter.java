package cn.burgeon.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.burgeon.core.R;
import cn.burgeon.core.bean.Order;

/**
 * Created by Simon on 2014/4/16.
 */
public class CheckQueryLVAdapter extends BaseAdapter {

    private Context mContext;
    private List<Order> list;
    private int mLayoutRes;

    public CheckQueryLVAdapter(Context c, List<Order> l, int layoutRes) {
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
            holder.dateTV = (TextView) convertView.findViewById(R.id.dateTV);
            holder.noTV = (TextView) convertView.findViewById(R.id.noTV);
            holder.numTV = (TextView) convertView.findViewById(R.id.numTV);
            holder.typeTV = (TextView) convertView.findViewById(R.id.typeTV);
            holder.operatorTV = (TextView) convertView.findViewById(R.id.operatorTV);
            holder.stateTV = (TextView) convertView.findViewById(R.id.stateTV);
            holder.uploadstateTV = (TextView) convertView.findViewById(R.id.uploadstateTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Order order = list.get(position);
        // 赋值
        holder.dateTV.setText(order.getOrderDate());
        holder.noTV.setText(order.getOrderNo());
        holder.numTV.setText(order.getOrderCount());
        holder.typeTV.setText(order.getOrderType());
        holder.operatorTV.setText(order.getSaleAsistant());
        holder.stateTV.setText(order.getOrderState());
        holder.uploadstateTV.setText(order.getIsChecked());
        return convertView;
    }

    static class ViewHolder {
        TextView idTV;
        TextView dateTV;
        TextView noTV;
        TextView numTV;
        TextView typeTV;
        TextView operatorTV;
        TextView stateTV;
        TextView uploadstateTV;
    }

    public void setList(List<Order> data) {
        list = data;
        notifyDataSetChanged();
    }
}
