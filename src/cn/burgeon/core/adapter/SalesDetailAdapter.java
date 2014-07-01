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

public class SalesDetailAdapter extends BaseAdapter {

	private List<Order> list;
	private Context context;

	public SalesDetailAdapter(List<Order> list, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.sales_order_search_list_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.salesdetail_tiaoma);
			holder.textView2 = (TextView) convertView.findViewById(R.id.salesdetail_danjia);
			holder.textView3 = (TextView) convertView.findViewById(R.id.salesdetail_zhekou);
			holder.textView4 = (TextView) convertView.findViewById(R.id.salesdetail_shuliang);
			holder.textView5 = (TextView) convertView.findViewById(R.id.salesdetail_jine);
			holder.textView6 = (TextView) convertView.findViewById(R.id.salesdetail_pinming);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Order order = list.get(position);
		holder.textView1.setText(order.getOrderDate());
		holder.textView2.setText(order.getCardNum());
		holder.textView3.setText(order.getKuanHao());
		holder.textView4.setText(order.getKuanHao());
		holder.textView5.setText(order.getKuanHao());
		holder.textView6.setText(order.getSaleAsistant());
		
		int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色  
		  
		convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同 
		return convertView;
	}

	class ViewHolder{
		TextView textView1;
		TextView textView2;
		TextView textView3;
		TextView textView4;
		TextView textView5;
		TextView textView6;
	}

}
