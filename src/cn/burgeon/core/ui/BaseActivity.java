package cn.burgeon.core.ui;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.net.RequestManager;
import cn.burgeon.core.net.SimonHttpStack;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.CustomProgressDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Simon on 2014/4/16.
 */
public class BaseActivity extends Activity {
    public final static String PAR_KEY = "ParcelableKey";
    //private static final String SIPKEY = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key);
    //private static final String SIPPSWD = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_pswd);
    private SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String SIPPSWDMD5;

    App mApp;
    protected SQLiteDatabase db;

    // 进度条
    protected CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", "=======onCreate======");
        setupFullscreen();
        mApp = (App) getApplication();
        db = mApp.getDB();
        SDF.setLenient(false);
        //SIPPSWDMD5 = MD5(SIPPSWD);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	/*if(db != null)
    		db.close();*/
    }

    // 设置程序全屏显示
    public void setupFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // 跳转
    public void forwardActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }
    
    public void forwardActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode);
    }

    // 跳转(可传递对象数据)
    public void forwardActivity(Class<?> cls, IntentData intentData) {
        Intent intent = new Intent();
        intent.setClass(this, cls);

        // 传递对象数据
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(PAR_KEY, intentData);
        intent.putExtras(mBundle);

        startActivity(intent);
    }
    
    public void forwardActivity(Class<?> cls, IntentData intentData, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);

        // 传递对象数据
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(PAR_KEY, intentData);
        intent.putExtras(mBundle);

        startActivityForResult(intent,requestCode);
    }
    
    public void forwardActivity(Class<?> cls,String key,String value) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        
        Bundle mBundle = new Bundle();
        mBundle.putString(key, value);
        intent.putExtras(mBundle);
        startActivity(intent);
    }
    
    public void sendRequest(final Map<String, String> params, Response.Listener<String> successListener) {
    	sendRequest(params,successListener,null);
    }

    public void sendRequest(final Map<String, String> params, Response.Listener<String> successListener,Response.ErrorListener errorListener) {

        App.getHttpStack().setOnStartListener(new SimonHttpStack.OnStartListener() {
            @Override
            public void onStart(HttpUriRequest request) {
                // show loading
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // mProgress = ProgressDialog.show(BaseActivity.this, null, "加载中...", true, true);
                        // startProgressDialog();
                    }
                });
            }
        });

        StringRequest request = new StringRequest(Request.Method.POST, App.getHosturl(), successListener, errorListener) {
            protected Map<String, String> getParams() throws AuthFailureError {
                String tt = getSDF().format(new Date());

                //appKey,时间戳,MD5签名
                Log.d("Base", "sip_appkey:" + getSipkey());
                Log.d("Base", "sip_timestamp:" + tt);
                Log.d("Base", "sip_sign:" + MD5(getSipkey() + tt + getSIPPSWDMD5()));
                params.put("sip_appkey", App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key));
                params.put("sip_timestamp", tt);
                params.put("sip_sign", MD5(getSipkey() + tt + MD5(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_pswd))));
                return params;
            }
        };
        RequestManager.getRequestQueue().add(request);
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
    }
    

    public String getCurrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
            // 设置加载文字
            // progressDialog.setMessage("正在加载中...");
        }
        progressDialog.show();
    }

    public void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    // 初始化门店和用户信息
    public void initData(IntentData iData, TextView tv) {
        tv.setText("门店：" + iData.getStore() + "+" + iData.getUser());
    }
    
    // 初始化门店和用户信息
    public void initStoreData(TextView tv) {
        tv.setText("门店：" + App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
    }
    
    public void showAlertMsg(int message){
    	AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.systemtips))
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (BaseActivity.this.getCurrentFocus() != null) {
				if (BaseActivity.this.getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return true;
	}
	
	//检测网络状态
	public boolean networkReachable(){
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())  return true;
		return false;	
	}
	
	public static String getSipkey() {
        return App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key);
    }

    public static String getSippswd() {
        return App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_pswd);
    }

    public SimpleDateFormat getSDF() {
        return SDF;
    }

    public String getSIPPSWDMD5() {
        return MD5(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_pswd));
    }
    
    public String MD5(String s) {
        String r = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            r = buf.toString();
        } catch (Exception e) {
        }
        return r;
    }
}
