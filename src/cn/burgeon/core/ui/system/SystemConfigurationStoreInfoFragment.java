package cn.burgeon.core.ui.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.Employee;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.net.RequestManager;
import cn.burgeon.core.net.SimonHttpStack;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SystemConfigurationStoreInfoFragment extends Fragment {
    private static final String TAG = "SystemConfigurationStoreInfoFragment";
  
    //输入控件
    private AutoCompleteTextView mStoreNoEditor;
    private EditText mCustomerNameEditor;
    private EditText mTerminalNoEditor;
    private EditText mInventoryRowCountEditor;
    //正在输入内容
    private String storeNo;
    private String customerName;
    private String terminalNo;
    private String inventoryRowCount;
    //上次输入内容
    private String storeNoLastInput;
    private String customerNameLastInput;
    private String terminalNoLastInput;
    private String inventoryRowCountLastInput;
    
    //没内容瞎点次数
    private int noInputClickCount;
    
    //保存按钮    
    private Button mSave;
    ArrayAdapter adapter;
    App mApp;

    static SystemConfigurationStoreInfoFragment newInstance() {
        SystemConfigurationStoreInfoFragment newFragment = new SystemConfigurationStoreInfoFragment();
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=====onCreate====");
        mApp = (App) getActivity().getApplication();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "=====onResume====");
	    	adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line, ((SystemConfigurationActivity)getActivity()).getStoreData());
	    	mStoreNoEditor.setAdapter(adapter);
    }



	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.system_configuration_store_info_fragment, container, false);
        
        mStoreNoEditor = (AutoCompleteTextView) view.findViewById(R.id.storeNoEditText);
        
        mCustomerNameEditor = (EditText) view.findViewById(R.id.customerNameEditText);
        mTerminalNoEditor = (EditText) view.findViewById(R.id.terminalNoEditText);
        mInventoryRowCountEditor = (EditText) view.findViewById(R.id.inventoryRowCountEditText);
        
        mSave = (Button) view.findViewById(R.id.saveButton);
        
        mSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkToSave();
			}
		});
        

        return view;

    }
	

    //保存门店信息
    private void checkToSave(){
    	//获取用户输入数据
    	getInputData();
    	//检测用户输入数据合法性
    	if(noInput()){
    		if(noInputClickCount > 30){
    			showTips(R.string.twoB);
    		}else if(noInputClickCount > 20 && noInputClickCount <= 30){
    			showTips(R.string.tipsCannotUnderstand);
    		}else if(20 >= noInputClickCount && noInputClickCount > 10){
    			showTips(R.string.tipsPleaseInput);
    		}else{
    			showTips(R.string.tipsNoInputData);    			
    		}
    		noInputClickCount++;
    		return;
    	}
    	if(sameData()){
    		showTips(R.string.tipsInputDataNotChanged);
    		return;
    	}
    	//保存到设备中
    	saveToMemery();
    	//记录上次输入内容
    	saveLastInput();
    }
    
    //获取用户输入数据
    private void getInputData(){
    	storeNo = mStoreNoEditor.getText().toString();
    	customerName = mCustomerNameEditor.getText().toString();
    	terminalNo = mTerminalNoEditor.getText().toString();
    	inventoryRowCount = mInventoryRowCountEditor.getText().toString();
    	
    	storeNo = storeNo != null ? storeNo.trim() : "";
    	customerName = customerName != null ? customerName.trim() : "";
    	terminalNo = terminalNo != null ? terminalNo.trim() : "";
    	inventoryRowCount = inventoryRowCount != null ? inventoryRowCount.trim() : "";    	
    }
    
    //检测重复数据
    private boolean sameData(){    	
    	if(saveOnce()
    			&& storeNo.equals(storeNoLastInput)
    			&& customerName.equals(customerNameLastInput)
    			&& terminalNo.equals(terminalNoLastInput)
    			&& inventoryRowCount.equals(inventoryRowCountLastInput)){
    		return true;
    	}
    	return false;
    }

    //没有输入内容
    private boolean noInput(){
    	if(TextUtils.isEmpty(storeNo) &&
    			TextUtils.isEmpty(customerName) &&
    			TextUtils.isEmpty(terminalNo) &&
    			TextUtils.isEmpty(inventoryRowCount)){
    		return true;
    	}
    	return false;
    }
    
    private String getStoreNo(String storeName) {
    	String store = "-9999";
		Cursor c = mApp.getDB().rawQuery("select store from tc_store where st_name = ?", new String[]{storeName});
		if(c.moveToFirst()){
			store = c.getString(0);
		}
		if(c != null && !c.isClosed()) c.close();
		return store;
	}
    
    //保存到设备
    private void saveToMemery(){
    	
    	//保存门店编号
    	if(!TextUtils.isEmpty(storeNo)){
    		String store = getStoreNo(storeNo);
			sendRequest(constructParams(store),new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d(TAG, response);
					if(!TextUtils.isEmpty(response)){
						parseResult(response);
					}
				}
			});
			App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.storeNumberKey, store);
			App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.store_key, storeNo);
    	}
    	//保存顾客名称
    	if(!TextUtils.isEmpty(customerName)){
    		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.customerNameKey, customerName);
    	}
    	//保存终端编号
    	if(!TextUtils.isEmpty(terminalNo)){
    		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.terminalNumberKey, terminalNo);
    	}
    	//保存盘点显示行数
    	if(!TextUtils.isEmpty(inventoryRowCount)){
    		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.inventoryRowCountKey, inventoryRowCount);
    	}
    	showTips(R.string.tipsSaveSucess);
    }
    
	//是否保存过一次
    private boolean saveOnce(){
    	return !TextUtils.isEmpty(storeNoLastInput) ||
    		   !TextUtils.isEmpty(customerNameLastInput) ||
    		   !TextUtils.isEmpty(terminalNoLastInput) ||
    		   !TextUtils.isEmpty(inventoryRowCountLastInput);
    }
    
    //记录上次输入内容
    private void saveLastInput(){
    	storeNoLastInput = storeNo;
    	customerNameLastInput = customerName;
    	terminalNoLastInput = terminalNo;
    	inventoryRowCountLastInput = inventoryRowCount;
    }

    //显示对话框
    private void showTips(int whichTips){
    	LayoutInflater inflater = this.getActivity().getLayoutInflater();
    	//布局文件待添加！！！！！！！！！！
    	View tipsLayout = inflater.inflate(R.layout.inventory_refresh_tips, 
    			(ViewGroup)this.getActivity().findViewById(R.id.inventoryRefreshTipsLayout));
    	TextView tipsText = (TextView) tipsLayout.findViewById(R.id.inventoryRefreshingTipsText);
    	tipsText.setText(whichTips);
    	
    	new AlertDialog.Builder(this.getActivity())
    		.setTitle(getString(R.string.tipsDataDownload))
    		.setView(tipsLayout)
    		.setPositiveButton(getString(R.string.confirm),null)
    		.show();
    	
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
    }
    
    public void sendRequest(final Map<String, String> params, Response.Listener<String> successListener) {

        StringRequest request = new StringRequest(Request.Method.POST, App.getHosturl(), successListener, createMyReqErrorListener()) {
            protected Map<String, String> getParams() throws AuthFailureError {
                String tt = mApp.getSDF().format(new Date());

                //appKey,时间戳,MD5签名
                params.put("sip_appkey", App.getSipkey());
                params.put("sip_timestamp", tt);
                params.put("sip_sign", mApp.MD5(App.getSipkey() + tt + mApp.getSIPPSWDMD5()));
                return params;
            }
        };
        RequestManager.getRequestQueue().add(request);
    }
    
	private void parseResult(String response) {
		mApp.clearEmployee();
		ArrayList<Employee> employees = mApp.getEmployees();
        try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			JSONArray rowsJA = resJO.getJSONArray("rows");
			int len = rowsJA.length();
			for (int i = 0; i < len; i++) {
			    // ["BURGEON1108001","权威全额","苏州经销商","苏州001"]
			    String currRow = rowsJA.get(i).toString();
			    String[] currRows = currRow.split(",");

			    Employee employee = new Employee();
			    employee.setId(currRows[0].substring(2, currRows[0].length() - 1));
			    employee.setName(currRows[1].substring(1, currRows[1].length() - 1));
			    employee.setAgency(currRows[2].substring(1, currRows[2].length() - 1));
			    employee.setStore(currRows[3].substring(1, currRows[3].length() - 2));
			    employees.add(employee);
			}
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
}
