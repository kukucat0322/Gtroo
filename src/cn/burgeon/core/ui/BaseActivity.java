package cn.burgeon.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.client.methods.HttpUriRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cn.burgeon.core.App;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.db.DbHelper;
import cn.burgeon.core.net.RequestManager;
import cn.burgeon.core.net.SimonHttpStack;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.CustomProgressDialog;

/**
 * Created by Simon on 2014/4/16.
 */
public class BaseActivity extends Activity {
    public final static String PAR_KEY = "ParcelableKey";

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
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
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

        App.getHttpStack().setOnStartListener(new SimonHttpStack.OnStartListener() {
            @Override
            public void onStart(HttpUriRequest request) {
                // show loading
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // mProgress = ProgressDialog.show(BaseActivity.this, null, "加载中...", true, true);
                        startProgressDialog();
                    }
                });
            }
        });

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

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
    }

    public String getCurrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
}
