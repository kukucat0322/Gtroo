package cn.burgeon.core.adapter;

import java.util.List;

import cn.burgeon.core.R;
import cn.burgeon.core.bean.InventorySelf;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InventoryQueryAdapter extends BaseAdapter {

	private List<InventorySelf> list;
	private Context context;

	public InventoryQueryAdapter(List<InventorySelf> list, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.inventory_query_listview, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.inventoryStyleNumber);
			holder.textView2 = (TextView) convertView.findViewById(R.id.inventoryStyleCount);
			holder.textView3 = (TextView) convertView.findViewById(R.id.inventoryStyleName);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		InventorySelf inventorySelf = list.get(position);
		holder.textView1.setText(inventorySelf.getStyleNumber());
		holder.textView2.setText(inventorySelf.getStyleCount());
		holder.textView3.setText(inventorySelf.getStyleName());
		
		int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色  
		  
		convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同 
		return convertView;
	}

	class ViewHolder{
		TextView textView1;
		TextView textView2;
		TextView textView3;
	}

}
