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

public class SalesWareSummerDetailAdapter extends BaseAdapter {

	private List<Product> list;
	private Context context;

	public SalesWareSummerDetailAdapter(List<Product> list, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.sales__order_detail_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.neworderitem_barcode);
			holder.textView2 = (TextView) convertView.findViewById(R.id.neworderitem_price);
			holder.textView3 = (TextView) convertView.findViewById(R.id.neworderitem_discount);
			holder.textView4 = (TextView) convertView.findViewById(R.id.ordersearch_count);
			holder.textView5 = (TextView) convertView.findViewById(R.id.ordersearch_money);
			holder.textView6 = (TextView) convertView.findViewById(R.id.ordersearch_name);
			holder.textView7 = (TextView) convertView.findViewById(R.id.ordersearch_color);
			holder.textView8 = (TextView) convertView.findViewById(R.id.ordersearch_size);
			holder.textView9 = (TextView) convertView.findViewById(R.id.ordersearch_type);
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
		holder.textView6.setText(product.getName());
		holder.textView7.setText(product.getColor());
		holder.textView8.setText(product.getSize());
		holder.textView9.setText("正常销售");
		
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
		TextView textView7;
		TextView textView8;
		TextView textView9;
	}

}
