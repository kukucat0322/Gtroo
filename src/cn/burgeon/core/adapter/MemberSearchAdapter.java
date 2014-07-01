package cn.burgeon.core.adapter;

import java.util.List;

import cn.burgeon.core.R;
import cn.burgeon.core.bean.Member;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MemberSearchAdapter extends BaseAdapter {

	private List<Member> list;
	private Context context;

	public MemberSearchAdapter(List<Member> list, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.member_search_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.membersearch_cardno);
			holder.textView2 = (TextView) convertView.findViewById(R.id.membersearch_name);
			holder.textView3 = (TextView) convertView.findViewById(R.id.membersearch_discount);
			holder.textView4 = (TextView) convertView.findViewById(R.id.membersearch_money);
			holder.textView5 = (TextView) convertView.findViewById(R.id.membersearch_birthday);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Member member = list.get(position);
		holder.textView1.setText(member.getCardNum());
		holder.textView2.setText(member.getName());
		holder.textView3.setText(member.getDiscount());
		holder.textView4.setText(member.getYue());
		holder.textView5.setText(member.getBirthday());
		return convertView;
	}

	class ViewHolder{
		TextView textView1;
		TextView textView2;
		TextView textView3;
		TextView textView4;
		TextView textView5;
	}

}
