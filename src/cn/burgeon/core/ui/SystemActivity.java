package cn.burgeon.core.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SystemAdapter;
import cn.burgeon.core.bean.Employee;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.allot.AllotManagerActivity;
import cn.burgeon.core.ui.check.CheckManagerActivity;
import cn.burgeon.core.ui.inventory.InventoryManagerActivity;
import cn.burgeon.core.ui.member.MemberManagerActivity;
import cn.burgeon.core.ui.sales.SalesManagerActivity;
import cn.burgeon.core.ui.system.SystemManagerActivity;
import cn.burgeon.core.utils.PreferenceUtils;

import com.android.volley.Response;

public class SystemActivity extends BaseActivity {
	
	private final String TAG = "SystemActivity";
    private GridView sysGV;
    private SystemAdapter mAdapter;
    private TextView storeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_system);

        init();

        initStoreData(storeTV);
        
        /*sendRequest(constructParams(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.storeNumberKey)),new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("xxee", response);
				if(!"null".equals(response) && !TextUtils.isEmpty(response)){
					RequestResult result = parseResult(response);
					//请求成功，更新记录状态
					if("0".equals(result.getCode())){
						parseResponse(response);
					}
				}
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
				UndoBarController.show(LoginActivity.this, "登录失败，请检测网络！", null, MESSAGESTYLE);
			}
		});*/
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
    
	private void parseResponse(String response) {
		SQLiteDatabase db = mApp.getDB();
        try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			JSONArray rowsJA = resJO.getJSONArray("rows");
			int len = rowsJA.length();
			db.beginTransaction();
			db.execSQL("delete from employee");
			Employee employee = null;
			for (int i = 0; i < len; i++) {
			    // ["BURGEON1108001","权威全额","苏州经销商","苏州001"]
			    String currRow = rowsJA.get(i).toString();
			    String[] currRows = currRow.split(",");

			    employee = new Employee();
			    employee.setId(currRows[0].substring(2, currRows[0].length() - 1));
			    employee.setName(currRows[1].substring(1, currRows[1].length() - 1));
			    employee.setAgency(currRows[2].substring(1, currRows[2].length() - 1));
			    employee.setStore(currRows[3].substring(1, currRows[3].length() - 2));

			    db.execSQL("insert into employee(id,name,agency,store) values(?,?,?,?)", 
			    		new Object[]{employee.getId(),employee.getName(),employee.getAgency(),employee.getStore()});
			}
			App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.agency_key, employee == null? "":employee.getAgency());
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
		}
	}
    
    private Map<String, String> constructParams(String storeNo) {
    	Map<String,String> params = new HashMap<String, String>();
		JSONArray array;
		JSONObject transactions;
		try {
			array = new JSONArray();
			transactions = new JSONObject();
			transactions.put("id", 112);
			transactions.put("command", "Query");
			JSONObject paramsInTransactions = new JSONObject();
			paramsInTransactions.put("table", 14630);
			paramsInTransactions.put("columns", new JSONArray().put("no")
					.put("name").put("C_CUSTOMER_ID:name").put("C_STORE_ID:name"));
			
			//查询条件的params
			JSONObject queryParams = new JSONObject();
			queryParams.put("column", "C_STORE_ID");
			queryParams.put("condition", "=" + storeNo);
			paramsInTransactions.put("params", queryParams);
			
			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			Log.d(TAG, array.toString());
			params.put("transactions", array.toString());
			
		} catch (JSONException e) {}
		return params;
	}

    private void init() {
        sysGV = (GridView) findViewById(R.id.sysGV);
        mAdapter = new SystemAdapter(this);
        sysGV.setAdapter(mAdapter);
        sysGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.sysTextValues[0].equals(itemValue)) {
                    forwardActivity(SalesManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[1].equals(itemValue)) {
                    forwardActivity(MemberManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[2].equals(itemValue)) {
                    forwardActivity(AllotManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[3].equals(itemValue)) {
                    forwardActivity(CheckManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[4].equals(itemValue)) {
                    forwardActivity(InventoryManagerActivity.class);
                }  else if (itemValue != null && Constant.sysTextValues[5].equals(itemValue)) {
                    forwardActivity(SystemManagerActivity.class);
                } 
            }
        });

        storeTV = (TextView) findViewById(R.id.storeTV);
    }
}
