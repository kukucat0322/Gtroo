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
import cn.burgeon.core.adapter.InventoryQueryAdapter;
import cn.burgeon.core.bean.InventorySelf;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class InventoryQueryActivity extends BaseActivity {
	
	private final String TAG = "InventoryQueryActivity";
	private ListView mListView;
	private InventoryQueryAdapter mQueryAdapter;
	private TextView statusStoreName;
	private TextView statusTime;
	private Button buttonHelp;
	private Button buttonSearch;
	private Button buttonDetail;
	private Button buttonBack;
	private EditText styleNumberEditText;
	private TextView inventoryCountRecordTextView;
	private int inventoryCountRecord = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_inventory_query);

        init();
    }
    
	private void init(){
		mListView = (ListView) findViewById(R.id.inventoryListView);
		statusStoreName = (TextView) findViewById(R.id.statusStoreName);
		statusTime = (TextView) findViewById(R.id.statusTime);
		initStoreNameAndTime();
		
		buttonHelp = (Button) findViewById(R.id.inventoryButtonHelp);
		buttonSearch = (Button) findViewById(R.id.inventoryButtonSearch);
		buttonDetail = (Button) findViewById(R.id.inventoryButtonDetail);
		buttonBack = (Button) findViewById(R.id.inventoryButtonBack);
    	
    	styleNumberEditText = (EditText) findViewById(R.id.inventoryStyleNumberEditText);
    	inventoryCountRecordTextView = (TextView) findViewById(R.id.inventoryCountRecord); 
    	listenEnterKey();
    	
    	buttonHelp.setOnClickListener(new ClickEvent());
    	buttonSearch.setOnClickListener(new ClickEvent());
    	buttonDetail.setOnClickListener(new ClickEvent());
    	buttonBack.setOnClickListener(new ClickEvent());
    }

    // 初始化门店信息
	private void initStoreNameAndTime(){
		statusStoreName.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
		statusTime.setText(getCurrDate());				
	}
	
	//监听回车键输入
	private void listenEnterKey(){
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
	}
	
    private void bindList(List<InventorySelf> data) {
    	mQueryAdapter = new InventoryQueryAdapter(data, this);
    	mListView.setAdapter(mQueryAdapter);
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
			case R.id.inventoryButtonDetail:				;
				startSearch("searchAll");
				break;
			case R.id.inventoryButtonBack:
				startSearch("searchAll");
				break;
			}
		}
	}

	private String getInput(){
		return styleNumberEditText.getText().toString();
	}
	
	//响应 查询 按钮
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
			paramsInTransactions.put("columns", new JSONArray().put("M_PRODUCT_ID").put("QTY").put("AD_ORG_ID"));
			
			//在params中的params
			//根据输入的款号构造查询参数
			if(!searchWhat.equals("searchAll")){
				Log.d(TAG,"亲，您刚才输入的是："+searchWhat);
				paramsInTransactions.put("params",
						new JSONObject().put("column", "M_PRODUCT_ID").put("condition", searchWhat));
			}
			
			paramsInTransactions.put("count", true);
			transactions.put("params", paramsInTransactions);
			
			params.put("transactions", new JSONArray().put(transactions).toString());
			
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
    private List<InventorySelf> resJAToList(String response) throws JSONException {
        ArrayList<InventorySelf> lists = new ArrayList<InventorySelf>();

        JSONArray resJA = new JSONArray(response);
        JSONObject resJO = resJA.getJSONObject(0);
        JSONArray rowsJA = resJO.getJSONArray("rows");
        inventoryCountRecord = rowsJA.length();
        for (int i = 0; i < rowsJA.length(); i++) {
            // ["TF0912140000005", 20091214, 3909, 11]
            String currRow = rowsJA.get(i).toString();
            String[] currRows = currRow.split(",");

            InventorySelf inventorySelf = new InventorySelf();
            inventorySelf.setStyleNumber(currRows[0].substring(1, currRows[0].length()));
            inventorySelf.setStyleCount(currRows[1]);
            inventorySelf.setStyleName(currRows[2].substring(0, currRows[2].length() - 1));
            lists.add(inventorySelf);
        }
        return lists;
    }
}
