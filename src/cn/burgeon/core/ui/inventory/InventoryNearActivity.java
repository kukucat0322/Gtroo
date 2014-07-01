package cn.burgeon.core.ui.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.InventoryNearAdapter;
import cn.burgeon.core.bean.InventoryNear;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class InventoryNearActivity extends BaseActivity {
	
	private final String TAG = "InventoryNearActivity";
	private ListView mListView;
	private InventoryNearAdapter mNearAdapter;
	private TextView statusStoreName;
	private TextView statusTime;
	private Button buttonHelp;
	private Button buttonSearch;
	private Button buttonBack;
	private EditText barCodeEditText;
	private EditText styleNumberEditText;
	private boolean barCodeInputing = false;
	private boolean barCodeScanned = false;
	private boolean styleNumberInputing = false;
	private boolean styleNumberScanned = false;
	private TextView inventoryCountRecordTextView;
	private int inventoryCountRecord = 0;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_inventory_near);

        init();
        //从扫描仪得到条码或者款号
        getDataFromScanner();
    }
    
	private void init(){
		mListView = (ListView) findViewById(R.id.inventoryListView);

		statusStoreName = (TextView) findViewById(R.id.statusStoreName);
		statusTime = (TextView) findViewById(R.id.statusTime);
		initStoreNameAndTime();
		
		buttonHelp = (Button) findViewById(R.id.inventoryButtonHelp);
    	buttonSearch = (Button) findViewById(R.id.inventoryButtonSearch);
    	buttonBack = (Button) findViewById(R.id.inventoryButtonBack);
    	
    	inventoryCountRecordTextView = (TextView) findViewById(R.id.inventoryCountRecord);
    	styleNumberEditText = (EditText) findViewById(R.id.inventoryStyleNumberEditText);
    	barCodeEditText = (EditText) findViewById(R.id.inventoryBarCodeEditText);
    	//初始化初始化款号跟条码框的输入状态
    	initInputStatus();
    	//监听回车键
    	listenInputEnterKey();
    	
    	buttonHelp.setOnClickListener(new ClickEvent());
    	buttonSearch.setOnClickListener(new ClickEvent());
    	buttonBack.setOnClickListener(new ClickEvent());
    }

	//监听回车键输入
	private void listenInputEnterKey(){
    	styleNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_DONE) {
                	startSearch(getInput());
                    return true;
                }
                return false;
			}
        });
    	
    	barCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_DONE) {
                	startSearch(getInput());
                    return true;
                }
                return false;
			}
        });
	}
	
	//从扫描仪输入数据
	private void getDataFromScanner(){
		//扫描到的是款号
		if(styleNumberScanned){
			barCodeScanned = false;
			startSearch(getScannedStyleNumber());
		}
		//扫描到的是条码
		else if(barCodeScanned){
			styleNumberScanned = false;
			startSearch(getScannedBarCode());			
		}
	}
	
	private String getScannedStyleNumber(){
		return null;
	}
	
	private String getScannedBarCode(){
		return null;
	}
	
    // 初始化门店信息
	private void initStoreNameAndTime(){
        statusStoreName.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
        statusTime.setText(getCurrDate());				
	}
	
	//初始化款号跟条码框的输入状态,要求同时只能录入一个
	private void initInputStatus(){
		//款号
    	styleNumberEditText.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if(arg0.length() > 0){
					styleNumberInputing = true;
					barCodeEditText.setEnabled(false);
				}else if(arg0.length() == 0){
					styleNumberInputing = false;
					barCodeEditText.setEnabled(true);
				}				
			}  		
    	});
    	//条码
    	barCodeEditText.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if(arg0.length() > 0){
					barCodeInputing = true;
					styleNumberEditText.setEnabled(false);
				}else if(arg0.length() == 0){
					barCodeInputing = false;
					styleNumberEditText.setEnabled(true);
				}				
			}  		
    	});	    	
		
	}
	
    private void bindList(List<InventoryNear> data) {
    	mNearAdapter = new InventoryNearAdapter(data, this);
    	mListView.setAdapter(mNearAdapter);
	}
 
    private void updateInventoryCountRecord(final String count){
    	inventoryCountRecordTextView.setText(count + " 条记录");
    }
    
	public class ClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.inventoryButtonHelp:
				startSearch("searchAll");
				break;				
			case R.id.inventoryButtonSearch:
				startSearch(getInput());
				break;
			case R.id.inventoryButtonBack:
				InventoryNearActivity.this.finish();
				break;
			}
		}
	}

	private String getInput(){
		if(barCodeInputing){
			return barCodeEditText.getText().toString();
		}else if(styleNumberInputing){
			return styleNumberEditText.getText().toString();
		}
		return null;
	}
	
	//响应 查询 按钮  param <searchWhat> to Identify search type
	private void startSearch(String searchWhat){
		if(searchWhat == null || searchWhat.equals("")){
			Log.d(TAG,searchWhat + "亲，啥也没输入，查个毛啊");
			return;
		}
		searchWhat = searchWhat.trim();
		
		Map<String,String> params = new HashMap<String, String>();
		
		JSONObject transactions;
		
		try {
			transactions = new JSONObject();
			//id
			transactions.put("id", 112);
			//command
			transactions.put("command", "Query");
			//params
			JSONObject paramsInTransactions = new JSONObject();
			paramsInTransactions.put("table", 15632);
			paramsInTransactions.put("columns", new JSONArray().put("C_STORE_ID").put("M_PRODUCT_ID").put("QTY").put("M_PRODUCTALIAS_ID"));
			
			//根据输入构造 params中的param
			if(!searchWhat.equals("searchAll")){
				Log.d(TAG,"亲，您刚才输入的是："+ searchWhat);
				String searchColumn = "";
				
				if(styleNumberInputing || styleNumberScanned){
					searchColumn = "M_PRODUCT_ID";
				}else if(barCodeInputing || barCodeScanned){
					searchColumn = "M_PRODUCTALIAS_ID";
				}

				paramsInTransactions.put("params",
						new JSONObject().put("column", searchColumn).put("condition", searchWhat));
			}
			
			paramsInTransactions.put("count", true);
			transactions.put("params", paramsInTransactions);
			
			params.put("transactions", new JSONArray().put(transactions).toString());
			
			//ok，baby 现在我们来发送请求
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d("onResponse", response);
                    // 取消进度条
                    stopProgressDialog();
                    
                    try {
                    	bindList(resJAToList(response));
                    	updateInventoryCountRecord( inventoryCountRecord + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
					
				}
			});
		} catch (JSONException e) {}		
	}

	//解析服务器返回数据
    private List<InventoryNear> resJAToList(String response) throws JSONException {
        ArrayList<InventoryNear> lists = new ArrayList<InventoryNear>();

        JSONArray resJA = new JSONArray(response);
        JSONObject resJO = resJA.getJSONObject(0);
        JSONArray rowsJA = resJO.getJSONArray("rows");
        inventoryCountRecord = rowsJA.length();
        for (int i = 0; i < rowsJA.length(); i++) {
            // ["TF0912140000005", 20091214, 3909, 11]
            String currRow = rowsJA.get(i).toString();
            String[] currRows = currRow.split(",");

            InventoryNear inventoryNear = new InventoryNear();
            inventoryNear.setStoreName(currRows[0].substring(1, currRows[0].length()));
            inventoryNear.setStyleNumber(currRows[1]);
            inventoryNear.setStyleCount(currRows[2]);
            inventoryNear.setBarCode(currRows[3].substring(0, currRows[3].length() - 1));
            lists.add(inventoryNear);
        }
        return lists;
    }
}
