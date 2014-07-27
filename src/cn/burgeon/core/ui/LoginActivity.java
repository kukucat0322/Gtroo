package cn.burgeon.core.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.Employee;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.system.SystemConfigurationActivity;
import cn.burgeon.core.utils.PreferenceUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
 
	private final String TAG = "LoginActivity";
    private EditText storeSpinner;
    private EditText  pswET;
    private Spinner userSpinner;
    private ImageView configBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_login);

        // 初始化布局控件
        init();
        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// 初始化门店
        initStoreData();
    	if(!"".equals(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key)))
    		storeSpinner.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
    	Log.d(TAG, "employee size:"+mApp.getEmployees().size());
    }

    private String[] getEmployees() {
		Cursor c = db.rawQuery("select name from employee",null);
		if(c!=null){
			int i = 0;
			String[] employees = new String[c.getCount()];
			while(c.moveToNext()){
				employees[i++] = c.getString(c.getColumnIndex("name"));
			}
			if(!c.isClosed()) c.close();
			return employees;
		}
		return new String[]{};
	}

	private void init() {
        storeSpinner = (EditText) findViewById(R.id.storeSpin);    
        userSpinner = (Spinner) findViewById(R.id.userET);
        pswET = (EditText) findViewById(R.id.pswET);

        configBtn = (ImageView) findViewById(R.id.configBtn);
        configBtn.setOnClickListener(this);
        loginBtn = (ImageView) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }
	
    private void initStoreData() {
    	String store = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.storeNumberKey);
    	if(!TextUtils.isEmpty(store)){
    		//检测网络状态
    		if(!networkReachable()){
    			ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_item, getEmployees());
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        userSpinner.setAdapter(adapter);
    		}else{
	    		startProgressDialog();
		    	sendRequest(constructParams(store),new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, response);
						if(!TextUtils.isEmpty(response)){
							RequestResult result = parseResult(response);
							//请求成功，更新记录状态
							if("0".equals(result.getCode())){
								parseResponse(response);
								mHandler.sendEmptyMessage(1);
							}
						}
					}
				},new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "error response======="+error.networkResponse.statusCode+"");
					}
				});
    		}
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.configBtn:
            	//forwardActivity(SystemConfigurationActivity.class);
            	showTips(0);
                break;
            case R.id.loginBtn:
            	if(hasConfigured()){
	            	 // 存入本地
	                App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.store_key, storeSpinner.getText().toString());
	                if(userSpinner.getAdapter().getCount() > 0)
	                App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.user_key, userSpinner.getSelectedItem().toString());
	                if(pswET.getText().length() > 0 || userSpinner.getAdapter().getCount() == 0){
	                	showAlertMsg(R.string.pswderror);
	                }else{
	                	forwardActivity(SystemActivity.class);
	                }
            	}else{
            		showAlertMsg(R.string.tipsNeedConfigure);
            	}
                break;
        }
    }

	private boolean hasConfigured() {
    	File f = new File(this.getFilesDir().toString() + "/myDataDownload/");
    	if (!f.exists()) {
	    	return false;
	    }
    	if (userSpinner.getAdapter().getCount() == 0) return false;
		return true;
	}

	private String[] resJAToList(String response) throws JSONException {
        String[] stores = null;

        JSONArray resJA = new JSONArray(response);
        JSONObject resJO = resJA.getJSONObject(0);
        JSONArray rowsJA = resJO.getJSONArray("rows");
        int len = rowsJA.length();
        stores = new String[len];
        for (int i = 0; i < len; i++) {
            // ["D江苏扬州陈勇"]
            String currRow = rowsJA.get(i).toString();
            stores[i] = currRow.substring(2, currRow.length() - 2);
        }
        return stores;
    }

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
			case 0:
				isExit = false;
				break;
			case 1:
		    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_item, getEmployees());
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        userSpinner.setAdapter(adapter);
		        stopProgressDialog();
				break;
			default:
				break;
			}
            
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(LoginActivity.this, "再按一次退出伯俊POS", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
    
    //显示对话框
    private void showTips(int whichTips){
    	LayoutInflater inflater = getLayoutInflater();
    	View tipsLayout = inflater.inflate(R.layout.configure_pswd_tips, 
    			(ViewGroup)findViewById(R.id.configureTipsLayout));
    	final EditText tipsText = (EditText) tipsLayout.findViewById(R.id.configureTipsText);
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    	.setTitle("系统提示")
    		.setView(tipsLayout)
    		.setPositiveButton(getString(R.string.confirm),new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					String configPswd = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.config_pswd);
					String editText = tipsText.getText().length() == 0?"":tipsText.getText().toString();
					if(editText.equals(configPswd)){
						forwardActivity(SystemConfigurationActivity.class);
						try {
				    		Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
				    	    field.setAccessible(true);
				    	    field.set(dialog,true);
				    	    dialog.dismiss();
				         }catch (Exception e){}
					}else{
						tipsText.setError("密码错误");
				    	try {
				    		Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
				    	    field.setAccessible(true);
				    	    field.set(dialog, false);
				         }catch (Exception e){}
					}
				}
			}).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					try {
			    		Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			    	    field.setAccessible(true);
			    	    field.set(dialog,true);
			    	    dialog.dismiss();
			         }catch (Exception e){}
				}
			});
    	dialog = builder.create();
    	dialog.show();
    }

}
