package cn.burgeon.core.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.Product;

public class SalesNewOrderAdapter extends BaseAdapter {

	private List<Product> list;
	private Context context;

	public SalesNewOrderAdapter(List<Product> list, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.sales_new_order_list_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.neworderitem_barcode);
			holder.textView2 = (TextView) convertView.findViewById(R.id.neworderitem_price);
			holder.textView3 = (TextView) convertView.findViewById(R.id.neworderitem_discount);
			holder.textView4 = (TextView) convertView.findViewById(R.id.ordersearch_count);
			holder.textView5 = (TextView) convertView.findViewById(R.id.ordersearch_money);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Product product = list.get(position);
		holder.textView1.setText(product.getBarCode());
		holder.textView2.setText(product.getPrice());
		holder.textView3.setText(product.getDiscount());
		holder.textView4.setText(product.getCount());
		holder.textView5.setText(product.getMoney());
		
		//int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色  
		  
		//convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同 
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
