package cn.burgeon.core.ui.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.MemberListAdapter;
import cn.burgeon.core.bean.Member;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForVIPQuery;

import com.android.volley.Response;

public class MemberListActivity extends BaseActivity {
	
	ListView mListView;
	Button addBtn,queryBtn,updateBtn,delBtn;
	MemberListAdapter mAdapter;
	TextView commonRecordnum;
	CustomDialogForVIPQuery dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_member_list);
		
		init();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}

	private void init() {
	     // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);
        
        commonRecordnum = (TextView) findViewById(R.id.sales_common_recordnum);
		addBtn = (Button) findViewById(R.id.memberListAdd);
		queryBtn = (Button) findViewById(R.id.memberListQuery);
		updateBtn = (Button) findViewById(R.id.memberListUpdate);
		addBtn.setOnClickListener(onClickListener);
		queryBtn.setOnClickListener(onClickListener);
		updateBtn.setOnClickListener(onClickListener);
		mListView = (ListView) findViewById(R.id.memberLV);
		mListView.setOnItemClickListener(OnItemSelectedListener);
		mAdapter = new MemberListAdapter(new ArrayList<Member>(), this);
		mListView.setAdapter(mAdapter);
		commonRecordnum.setText(String.format(getResources().getString(R.string.sales_new_common_record), mAdapter.getCount()));

	}
	
	public List<Member> query(){
		List<Member> data = new ArrayList<Member>();
		Member member = null;
		Cursor c = db.rawQuery("select * from c_vip", null);
		while(c.moveToNext()){
			member = new Member();
			member.setId(c.getInt(c.getColumnIndex("_id")));
			member.setCardNum(c.getString(c.getColumnIndex("cardno")));
			member.setName(c.getString(c.getColumnIndex("name")));
			member.setiDentityCardNum("null".equals(c.getString(c.getColumnIndex("idno")))?"":c.getString(c.getColumnIndex("idno")));
			member.setPhoneNum(c.getString(c.getColumnIndex("mobile")));
			member.setBirthday("null".equals(c.getString(c.getColumnIndex("birthday")))?"":c.getString(c.getColumnIndex("birthday")));
			member.setEmployee(c.getString(c.getColumnIndex("employee")));
			member.setEmail("null".equals(c.getString(c.getColumnIndex("email")))?"":c.getString(c.getColumnIndex("email")));
			member.setCreateCardDate(c.getString(c.getColumnIndex("createTime")));
			member.setType(c.getString(c.getColumnIndex("type")));
			member.setSex(c.getString(c.getColumnIndex("sex")));
			member.setStatus(c.getString(c.getColumnIndex("status")));
			data.add(member);
		}
		if(c != null && !c.isClosed())
		c.close();
		return data;
	}

	private List<Member> postRequest() {
		final List<Member> data = new ArrayList<Member>();
		Map<String,String> params = new HashMap<String, String>();
		JSONArray array;
		JSONObject transactions;
		try {
			array = new JSONArray();
			transactions = new JSONObject();
			transactions.put("id", 112);
			transactions.put("command", "Query");
			
			//第一个params
			JSONObject paramsInTransactions = new JSONObject();
			paramsInTransactions.put("table", 12899);
			paramsInTransactions.put("columns", new JSONArray().put("cardno").put("vipname").put("sex").put("birthday").put("C_VIPTYPE_ID;name"));
			//在params中的params
			paramsInTransactions.put("params", new JSONObject().put("column", "C_STORE_ID").put("condition", 3865));
			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			params.put("transactions", array.toString());
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d("zhang.h", response);
					parseResult(response,data);
					mListView.setAdapter(mAdapter);
				}
			});
		} catch (JSONException e) {}
		return data;
	}
	
	private void parseResult(String result,List<Member> data){
		try {
			JSONArray array = new JSONArray(result);
			JSONObject obj = array.getJSONObject(0);
			JSONArray rows = obj.getJSONArray("rows");
			Member member = null;
			for(int i = 0; i < rows.length(); i++){
				String row = rows.get(i).toString();
				String[] rowArr = row.split(",");
				
				member = new Member();
				member.setCardNum(rowArr[0]);
				member.setName(rowArr[1]);
				member.setSex(rowArr[2]);
				member.setBirthday(rowArr[3]);
				member.setType(rowArr[4]);
				data.add(member);
			}
		} catch (JSONException e) {
			Log.d("MemberListActivity", e.toString());
		}
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.memberListAdd:
				forwardActivity(MemberRegistActivity.class);
				break;
			case R.id.memberListUpdate:
				if(selectedMember != null){
					if(getString(R.string.sales_settle_hasup).equals(selectedMember.getStatus())){
						showTips();
					}else{
						Intent intent = new Intent(MemberListActivity.this,MemberRegistActivity.class);
						intent.putExtra("_id", _id);
						startActivity(intent);
					}
				}
				break;
			case R.id.memberListQuery:
				dialog = new CustomDialogForVIPQuery.Builder(MemberListActivity.this).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search();
                        if (dialog.isShowing())
                        	dialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing())
                        	dialog.dismiss();
                    }
                }).show();
				break;
			default:
				break;
			}
		}
	}; 
	
    private void showTips(){
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.systemtips))
    		.setMessage(R.string.no_update_uploaded_data)
    		.setPositiveButton(getString(R.string.confirm),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}});
    	dialog = builder.create();
    	dialog.show();
    }
    
	public void search(){
		String startTime = dialog.getStartTime();
        String endTime = dialog.getEndTime();

		List<Member> data = new ArrayList<Member>();
		Member member = null;
		String sql = "select * from c_vip where createTime "
				+ "between " + "'" + startTime + "'" + " and " + "'" + endTime + "'";
		if (dialog.getCardNo().length() > 0)
			sql += " and cardno = '" + dialog.getCardNo().toString() + "'";
		if (dialog.getMobileNo().length() > 0)
			sql += " and mobile = '" + dialog.getMobileNo().toString() + "'";
		
		Log.d("MemberSearch", "sql = " + sql);
		Cursor c = db.rawQuery(sql, null);
		while(c.moveToNext()){
			member = new Member();
			member.setId(c.getInt(c.getColumnIndex("_id")));
			member.setCardNum(c.getString(c.getColumnIndex("cardno")));
			member.setName(c.getString(c.getColumnIndex("name")));
			member.setiDentityCardNum(c.getString(c.getColumnIndex("idno")));
			member.setPhoneNum(c.getString(c.getColumnIndex("mobile")));
			member.setBirthday(c.getString(c.getColumnIndex("birthday")));
			member.setEmployee(c.getString(c.getColumnIndex("employee")));
			member.setEmail(c.getString(c.getColumnIndex("email")));
			member.setCreateCardDate(c.getString(c.getColumnIndex("createTime")));
			member.setType(c.getString(c.getColumnIndex("type")));
			member.setSex(c.getString(c.getColumnIndex("sex")));
			member.setStatus(c.getString(c.getColumnIndex("status")));
			data.add(member);
		}
		if(c != null && !c.isClosed()) 
			c.close();
		mAdapter.setList(data);
	}
	
	int _id;
	Member selectedMember;
	View previous;
	
	OnItemClickListener OnItemSelectedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(previous != null) previous.setBackgroundDrawable(view.getBackground());
			view.setBackgroundResource(R.drawable.button_bg);
			selectedMember = (Member) parent.getAdapter().getItem(position);
			previous = view;
			_id = selectedMember.getId();
			Log.d("MemberListActivity", "_id=" +_id);
		}

	};
}
