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
import android.text.TextUtils;
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
import cn.burgeon.core.adapter.MemberSearchAdapter;
import cn.burgeon.core.bean.Member;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForVIPQuery;
import cn.burgeon.core.widget.UndoBarController;
import cn.burgeon.core.widget.UndoBarStyle;

import com.android.volley.Response;
import com.android.volley.VolleyError;

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
                        //search();
                    	viaNet();
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
	
	
	private void viaNet() {
		if(!networkReachable()){
			showAlertMsg(R.string.tipsNetworkUnReachable$_$);
			return;
		}
		startProgressDialog();
		try {
			Map<String, String> params = new HashMap<String, String>();
            JSONArray array = new JSONArray();

            JSONObject transactions = new JSONObject();
            transactions.put("id", 112);
            transactions.put("command", "Query");

            JSONObject paramsTable = new JSONObject();
            paramsTable.put("table", "12899");
            paramsTable.put("columns", new JSONArray().put("cardno").put("vipname")
            		.put("C_VIPTYPE_ID:DISCOUNT").put("VIPSTATE").put("birthday").put("C_VIPTYPE_ID"));
            JSONObject paramsCombine = new JSONObject();
            
/*            {
                "params": {
                    "combine": "and",
                    "expr1": {
                        "combine": "and",
                        "expr1": {
                            "column": "cardno",
                            "condition": "=577775"
                        },
                        "expr2": {
                            "column": "mobil",
                            "condition": "=13678965466"
                        }
                    },
                    "expr2": {
                        "combine": "and",
                        "expr1": {
                            "column": "OPENCARDDATE",
                            "condition": ">=20140909"
                        },
                        "expr2": {
                            "column": "OPENCARDDATE",
                            "condition": "<=20140912"
                        }
                    }
                },
            "columns":["CARDNO","MOBIL","OPENCARDDATE"],
                "table": "12899"
            }*/
            paramsCombine.put("combine", "and");
            JSONObject expr1JO = new JSONObject();
            expr1JO.put("combine", "and");
            
            JSONObject innerExpr11 = new JSONObject();
            innerExpr11.put("column", "cardno");
            innerExpr11.put("condition", dialog.getCardNo().length() == 0 ?"":"=" + dialog.getCardNo());
            
            JSONObject innerExpr12 = new JSONObject();
            innerExpr12.put("column", "mobil");
            innerExpr12.put("condition", dialog.getMobileNo().length() == 0?"":"=" + dialog.getMobileNo());
            
            expr1JO.put("expr1", innerExpr11);
            expr1JO.put("expr2", innerExpr12);
            paramsCombine.put("expr1", expr1JO);
            
            JSONObject expr2JO = new JSONObject();
        	expr2JO.put("combine", "and");
            JSONObject innerExpr21 = new JSONObject();
            innerExpr21.put("column", "OPENCARDDATE");
            innerExpr21.put("condition", ">=" + dialog.getStartTime());
            
            JSONObject innerExpr22 = new JSONObject();
            innerExpr22.put("column", "OPENCARDDATE");
            innerExpr22.put("condition", "<=" + dialog.getEndTime());
            expr2JO.put("expr1", innerExpr21);
            expr2JO.put("expr2", innerExpr22);
            paramsCombine.put("expr2", expr2JO);
            
            paramsTable.put("params", paramsCombine);
            transactions.put("params", paramsTable);
            array.put(transactions);
            Log.d("MemberSearch", array.toString());
            params.put("transactions", array.toString());
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d("zhang.h", response);
					if(!TextUtils.isEmpty(response)){
						RequestResult result = parseResult(response);
						//请求成功，更新记录状态
						if("0".equals(result.getCode())){
							//mAdapter = new MemberListAdapter(parseResponse(response), MemberListActivity.this);
							mAdapter.setList(parseResponse(response));
							stopProgressDialog();
						}else{
							stopProgressDialog();
							UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
					        UndoBarController.show(MemberListActivity.this, "找不到该会员", null, MESSAGESTYLE);
						}
					}

				}
			},new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					stopProgressDialog();
					UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
					UndoBarController.show(MemberListActivity.this, "网络异常，请检测网络", null, MESSAGESTYLE);
				}
			});
		} catch (JSONException e) {}
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
	
	private List<Member> parseResponse(String result){
		List<Member> data = new ArrayList<Member>();
		try {
			JSONArray array = new JSONArray(result);
			JSONObject obj = array.getJSONObject(0);
			JSONArray rows = obj.getJSONArray("rows");
			Member member = null;
			for(int i = 0; i < rows.length(); i++){
				String row = rows.get(i).toString();
				String[] rowArr = row.split(",");
				//["123321","yyuu",0.5,"是",null,67]
				member = new Member();
				member.setCardNum(rowArr[0].substring(2,rowArr[0].length()-1));
				member.setName(rowArr[1].substring(1,rowArr[1].length()-1));
				member.setDiscount(rowArr[2]);
				member.setVipState(rowArr[3].substring(1,rowArr[3].length()-1));
				member.setBirthday("null".equals(rowArr[4])?"":rowArr[4]);
				member.setTypeid("null".equals(rowArr[5].substring(0,rowArr[5].length()-1))?"":rowArr[5].substring(0,rowArr[5].length()-1));
				data.add(member);
			}
		} catch (JSONException e) {
			Log.d("MemberListActivity", e.toString());
		}
		return data;
	}
	
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
