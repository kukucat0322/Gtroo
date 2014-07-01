package cn.burgeon.core.adapter;

import java.util.List;

import cn.burgeon.core.R;
import cn.burgeon.core.bean.InventoryNear;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InventoryNearAdapter extends BaseAdapter {

	private List<InventoryNear> list;
	private Context context;

	public InventoryNearAdapter(List<InventoryNear> list, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.inventory_near_listview, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.inventoryStoreName);
			holder.textView2 = (TextView) convertView.findViewById(R.id.inventoryStyleNumber);
			holder.textView3 = (TextView) convertView.findViewById(R.id.inventoryStyleCount);
			holder.textView4 = (TextView) convertView.findViewById(R.id.inventoryBarCode);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		InventoryNear inventoryNear = list.get(position);
		holder.textView1.setText(inventoryNear.getStoreName());
		holder.textView2.setText(inventoryNear.getStyleNumber());
		holder.textView3.setText(inventoryNear.getStyleCount());
		holder.textView4.setText(inventoryNear.getBarCode());
		
		int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色  
		  
		convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同 
		return convertView;
	}

	class ViewHolder{
		TextView textView1;
		TextView textView2;
		TextView textView3;
		TextView textView4;
	}
}
