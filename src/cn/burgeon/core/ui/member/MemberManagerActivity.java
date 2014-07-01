package cn.burgeon.core.ui.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.MemberManagerAdapter;
import cn.burgeon.core.bean.Member;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.UndoBarController;
import cn.burgeon.core.widget.UndoBarStyle;

public class MemberManagerActivity extends BaseActivity {
	
	private final String TAG = "MemberManagerActivity";
	private GridView memberGV;
	private MemberManagerAdapter mAdapter;
	private TextView storeTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_member_manager);

		init();
		initStoreData(storeTV);
	}

	private void init() {
		storeTV = (TextView) findViewById(R.id.storeTV);
		memberGV = (GridView) findViewById(R.id.memberGV);
		mAdapter = new MemberManagerAdapter(this);
		memberGV.setAdapter(mAdapter);
		memberGV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String itemValue = (String) parent.getItemAtPosition(position);
				if (itemValue != null
						&& Constant.memberManagerTextValues[0].equals(itemValue)) {
					forwardActivity(MemberRegistActivity.class);
				} else if (itemValue != null
						&& Constant.memberManagerTextValues[1].equals(itemValue)) {
					forwardActivity(MemberListActivity.class);
				} else if (itemValue != null
						&& Constant.memberManagerTextValues[2].equals(itemValue)) {
					List<Member> vips = query();
					if(vips != null && vips.size() > 0){
						for(Member vip : vips){
							upload(vip);
						}
					}
					UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 3000);
		        	UndoBarController.show(MemberManagerActivity.this, "会员上传成功", null, MESSAGESTYLE);
				}else if (itemValue != null
						&& Constant.memberManagerTextValues[2].equals(itemValue)) {
				}
			}
		});
	}
	
	private void upload(final Member vip) {
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
			paramsInTransactions.put("CARDNO",vip.getCardNum());
			paramsInTransactions.put("C_VIPTYPE_ID__NAME",vip.getType());
			paramsInTransactions.put("C_CUSTOMER_ID__NAME","苏州经销商");
			paramsInTransactions.put("C_STORE_ID__NAME",App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
			paramsInTransactions.put("HR_EMPLOYEE_ID__NAME",vip.getEmployee());
			paramsInTransactions.put("VIPNAME",vip.getName());
			paramsInTransactions.put("MOBIL",vip.getPhoneNum());
			paramsInTransactions.put("SEX","男".equals(vip.getCardNum())?"M":"W");
			paramsInTransactions.put("M_DIM1_ID__ATTRIBNAME","品牌AS0015");
			
			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			Log.d(TAG, array.toString());
			params.put("transactions", array.toString());
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d(TAG, response);
					RequestResult result = parseResult(response);
					//请求成功，更新记录状态和销售单号
					if("0".equals(result.getCode())){
						updateOrderStatus(vip);
					}
					// 取消进度条
                    stopProgressDialog();
				}
			});
		} catch (JSONException e) {}
	}
	
	private void updateOrderStatus(Member vip) {
		db.beginTransaction();
        try {
        	db.execSQL("update c_vip set status = ? where _id = ?",
					new Object[]{
								getResources().getString(R.string.sales_settle_hasup),
								vip.getId()});
        	
            db.setTransactionSuccessful();
        } catch(Exception e){}
        finally {  
            db.endTransaction();
        } 
	}
	
	private RequestResult parseResult(String response) {
		RequestResult result = null;
	    try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			result = new RequestResult(resJO.getString("code"), resJO.getString("message"));
		} catch (JSONException e) {}
		return result;
	}
	
	public List<Member> query(){
		List<Member> data = new ArrayList<Member>();
		Member member = null;
		Cursor c = db.rawQuery("select * from c_vip where status = ?", new String[]{getString(R.string.sales_settle_noup)});
		while(c.moveToNext()){
			member = new Member();
			member.setId(c.getInt(c.getColumnIndex("_id")));
			member.setCardNum(c.getString(c.getColumnIndex("cardno")));
			member.setName(c.getString(c.getColumnIndex("name")));
			member.setiDentityCardNum(c.getString(c.getColumnIndex("idno")));
			member.setPhoneNum(c.getString(c.getColumnIndex("mobile")));
			member.setBirthday(c.getString(c.getColumnIndex("birthday")));
			member.setEmployee(c.getString(c.getColumnIndex("employee")));
			member.setType(c.getString(c.getColumnIndex("type")));
			member.setSex(c.getString(c.getColumnIndex("sex")));
			data.add(member);
		}
		if(c != null && !c.isClosed())
		c.close();
		return data;
	}
}
