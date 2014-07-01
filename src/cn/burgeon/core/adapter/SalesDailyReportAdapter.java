package cn.burgeon.core.adapter;

import java.util.List;

import cn.burgeon.core.R;
import cn.burgeon.core.bean.Order;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SalesDailyReportAdapter extends BaseAdapter {

	private List<Order> list;
	private Context context;

	public SalesDailyReportAdapter(List<Order> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.sales_daily_report_list_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.dailyreport_date);
			holder.textView2 = (TextView) convertView.findViewById(R.id.dailyreport_count);
			holder.textView3 = (TextView) convertView.findViewById(R.id.dailyreport_money);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Order order = list.get(position);
		holder.textView1.setText(order.getOrderDate());
		holder.textView2.setText(order.getOrderCount());
		holder.textView3.setText(order.getOrderMoney());
		
		return convertView;
	}

	class ViewHolder{
		TextView textView1;
		TextView textView2;
		TextView textView3;
	}
	
    public void setList(List<Order> data) {
        list = data;
        notifyDataSetChanged();
    }

}
