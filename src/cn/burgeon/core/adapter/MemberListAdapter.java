package cn.burgeon.core.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.Member;

public class MemberListAdapter extends BaseAdapter {

	private List<Member> list;
	private Context context;

	public MemberListAdapter(List<Member> list, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.member_list_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.memberlist_cardno);
			holder.textView2 = (TextView) convertView.findViewById(R.id.memberlist_name);
			holder.textView3 = (TextView) convertView.findViewById(R.id.memberlist_type);
			holder.textView4 = (TextView) convertView.findViewById(R.id.memberlist_sex);
			holder.textView5 = (TextView) convertView.findViewById(R.id.memberlist_birthday);
			holder.textView6 = (TextView) convertView.findViewById(R.id.memberlist_upstate);
			holder.textView7 = (TextView) convertView.findViewById(R.id.memberlist_id);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Member member = list.get(position);
		holder.textView1.setText(member.getCardNum());
		holder.textView2.setText(member.getName());
		holder.textView3.setText(member.getType());
		holder.textView4.setText(member.getSex());
		holder.textView5.setText(member.getBirthday());
		holder.textView6.setText(member.getVipState());
		holder.textView7.setText(String.valueOf(member.getId()));
		
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
	}
	
    public void setList(List<Member> data) {
        list = data;
        notifyDataSetChanged();
    }

}
