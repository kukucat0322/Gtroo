package cn.burgeon.core.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.ui.system.SystemConfigurationActivity;
import cn.burgeon.core.utils.PreferenceUtils;

import com.android.volley.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
 
	private final String TAG = "LoginActivity";
    private Spinner storeSpinner;
    private EditText userET, pswET;
    private ImageView configBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_login);

        // 初始化门店
        //initStoreData();

        // 初始化布局控件
        init();
        
    }

    private void init() {
        storeSpinner = (Spinner) findViewById(R.id.storeSpin);
        userET = (EditText) findViewById(R.id.userET);
        pswET = (EditText) findViewById(R.id.pswET);

        configBtn = (ImageView) findViewById(R.id.configBtn);
        configBtn.setOnClickListener(this);
        loginBtn = (ImageView) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }

    private void initStoreData() {
        Map<String, String> params = new HashMap<String, String>();
        JSONArray array;
        JSONObject transactions;
        try {
            array = new JSONArray();
            transactions = new JSONObject();
            transactions.put("id", 112);
            transactions.put("command", "Query");

            JSONObject paramsInTransactions = new JSONObject();
            paramsInTransactions.put("table", "C_V_RESTORE");
            paramsInTransactions.put("columns", new JSONArray().put("NAME"));
            transactions.put("params", paramsInTransactions);

            array.put(transactions);
            params.put("transactions", array.toString());

            sendRequest(params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 取消进度条
                    stopProgressDialog();

                    try {
                        String[] stores = resJAToList(response);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_item, stores);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        storeSpinner.setAdapter(adapter);

                        // 初始化门店名
                        String storeInDBVal = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key);
                        if (storeInDBVal != null && storeInDBVal.length() > 0) {
                            int position = adapter.getPosition(storeInDBVal);
                            storeSpinner.setSelection(position, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.configBtn:
            	forwardActivity(SystemConfigurationActivity.class);
                break;
            case R.id.loginBtn:
            	 // 存入本地
                App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.store_key, storeSpinner.getSelectedItem().toString());
                App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.user_key, userET.getText().toString());
            	// 跳转并传递数据
                //IntentData intentData = new IntentData();
                //intentData.setStore(storeSpinner.getSelectedItem().toString());
                //intentData.setUser(userET.getText().toString());
                
                /*
                new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStream is = getResources().openRawResource(R.raw.tc_sku);;
							BufferedReader br = new BufferedReader(new InputStreamReader(is,"gbk"));
							String line = null;
							String[] temp = null;
							db.beginTransaction();
							while((line = br.readLine()) != null){
								temp = line.split(",");
								db.execSQL("insert into tc_sku(sku,style,clr,sizeid,pname) values (?,?,?,?,?)", 
										new Object[]{temp[0],temp[1].substring(2),temp[2].substring(2),
										temp[3].substring(2),temp[4].substring(2)});	
								
							}
							db.setTransactionSuccessful();
							db.endTransaction();
							Log.d(TAG, "tc_sku done");
						} catch (Exception e) {
						}
					}
				}).start();;
               
                new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStream is = getResources().openRawResource(R.raw.tc_style);;
							BufferedReader br = new BufferedReader(new InputStreamReader(is,"gbk"));
							String line = null;
							while((line = br.readLine()) != null){
								String[] temp = line.split(",");
								db.execSQL("insert into tc_style(style,style_name,attrib1,attrib2,attrib3,attrib4,attrib5,attrib6,attrib7,attrib8,attrib9,attrib10) "
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?)", 
										new Object[]{temp[0],temp[1].substring(2),temp[2].substring(2),
										temp[3].substring(2),temp[4].substring(2),temp[5].substring(2)
										,temp[6].substring(2),temp[7].substring(2),temp[8].substring(2)
										,temp[9].substring(2),temp[10].substring(2),temp[11].substring(2)});	
							}
							Log.d(TAG, "tc_style done");
						} catch (Exception e) {
						}
					}
				}).start();;
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStream is = getResources().openRawResource(R.raw.tc_styleprice);;
							BufferedReader br = new BufferedReader(new InputStreamReader(is,"gbk"));
							String line = null;
							while((line = br.readLine()) != null){
								String[] temp = line.split(",");
								db.execSQL("insert into tc_styleprice(style,store,fprice) values (?,?,?)", 
										new Object[]{temp[0],temp[1].substring(2),temp[2].substring(2)});	
							}
							Log.d(TAG, "tc_styleprice done");
						} catch (Exception e) {
						}
					}
				}).start();;
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStream is = getResources().openRawResource(R.raw.tdefclr);;
							BufferedReader br = new BufferedReader(new InputStreamReader(is,"gbk"));
							String line = null;
							while((line = br.readLine()) != null){
								String[] temp = line.split(",");
								db.execSQL("insert into tdefclr(clr,clrname) values (?,?)", 
										new Object[]{temp[0],temp[1].substring(2)});	
							}
							Log.d(TAG, "tdefclr done");
						} catch (Exception e) {
						}
					}
				}).start();;
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStream is = getResources().openRawResource(R.raw.tdefsize);;
							BufferedReader br = new BufferedReader(new InputStreamReader(is,"gbk"));
							String line = null;
							while((line = br.readLine()) != null){
								String[] temp = line.split(",");
								db.execSQL("insert into tdefsize(sizeid,sizename) values (?,?)", 
										new Object[]{temp[0],temp[1].substring(2)});
							}
							Log.d(TAG, "tdefsize done");
						} catch (Exception e) {
						}
					}
				}).start();;*/
				
				forwardActivity(SystemActivity.class);
                
                /*
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    JSONArray array = new JSONArray();

                    JSONObject transactions = new JSONObject();
                    transactions.put("id", 112);
                    transactions.put("command", "Query");

                    JSONObject paramsTable = new JSONObject();
                    paramsTable.put("table", "14630");
                    paramsTable.put("columns", new JSONArray().put("name").put("C_STORE_ID"));
                    JSONObject paramsCombine = new JSONObject();
                    paramsCombine.put("combine", "and");
                    JSONObject expr1JO = new JSONObject();
                    expr1JO.put("column", "name");
                    expr1JO.put("condition", userET.getText());


                    paramsCombine.put("expr1", expr1JO);
                    JSONObject expr2JO = new JSONObject();
                    expr2JO.put("column", "C_STORE_ID");
                    expr2JO.put("condition", "3890");
                    paramsCombine.put("expr2", expr2JO);
                    paramsTable.put("params", paramsCombine);

                    transactions.put("params", paramsTable);
                    array.put(transactions);
                    params.put("transactions", array.toString());
                    sendRequest(params, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // 取消进度条
                            stopProgressDialog();

                            try {
                                JSONArray resJA = new JSONArray(response);
                                JSONObject resJO = resJA.getJSONObject(0);
                                JSONArray rowsJA = resJO.getJSONArray("rows");
                                int len = rowsJA.length();
                                // 有效用户
                                if (len > 0) {
                                    // 跳转并传递数据
                                    IntentData intentData = new IntentData();
                                    intentData.setStore(storeSpinner.getSelectedItem().toString());
                                    intentData.setUser(userET.getText().toString());
                                    forwardActivity(SystemActivity.class, intentData);
                                } else {
                                    // 提示用户不存在
                                    // Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_LONG).show();
                                    UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 3000);
                                    UndoBarController.show(LoginActivity.this, "用户不存在", null, MESSAGESTYLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
                break;
        }
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
            isExit = false;
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

}
