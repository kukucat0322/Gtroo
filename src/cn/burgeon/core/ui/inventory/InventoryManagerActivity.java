package cn.burgeon.core.ui.inventory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.InventoryManagerAdapter;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.ui.inventory.InventoryQueryActivity;
import cn.burgeon.core.ui.inventory.InventoryRefreshActivity;
import cn.burgeon.core.ui.inventory.InventoryNearActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class InventoryManagerActivity extends BaseActivity{
	
	private final String TAG = "InventoryManagerActivity";
	private GridView inventoryGridView;
	private InventoryManagerAdapter inventoryAdapter; 
	
	//测试对话框
	private int clickCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_inventory_manager);
		
		init();
	} 
	
    private void init() {  	
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
        
    	inventoryGridView = (GridView) findViewById(R.id.inventoryGridView);
    	inventoryAdapter = new InventoryManagerAdapter(this);
    	inventoryGridView.setAdapter(inventoryAdapter);
    	inventoryGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.inventoryManagerTextValues[0].equals(itemValue)) {
                    forwardActivity(InventoryQueryActivity.class);
                } else if (itemValue != null && Constant.inventoryManagerTextValues[1].equals(itemValue)) {
                    //检测是否需要刷新库存
                	checkToRefreshInventory();
                	clickCount ++;
                } else if (itemValue != null && Constant.inventoryManagerTextValues[2].equals(itemValue)) {
                    forwardActivity(InventoryNearActivity.class);
                }
            }
        });
    }
 
    //检测是否需要刷新库存
    private void checkToRefreshInventory(){   	
    	boolean allDataUpLoaded = checkDataUploaded();
    	if(allDataUpLoaded){
    		refreshInventory();
    		showTips(R.string.inventoryRefreshingTips);  		
    	}else{
    		showTips(R.string.inventoryCanotRefreshTips);  		
    	}   	
    }
    
    //刷新库存
    private void refreshInventory(){
    	Log.d(TAG,"__Refreshing Inventory__");
    }
    
    //检测本地数据是否都已上传
    private boolean checkDataUploaded(){
    	//添加检测逻辑
    	Log.d(TAG,"_checkDataUploaded____:ClickCount = " + clickCount);
    	return clickCount % 2 == 0 ? true : false;
    }
    
    //显示对话框
    private void showTips(int whichTips){
    	LayoutInflater inflater = getLayoutInflater();
    	View tipsLayout = inflater.inflate(R.layout.inventory_refresh_tips, 
    			(ViewGroup)findViewById(R.id.inventoryRefreshTipsLayout));
    	TextView tipsText = (TextView) tipsLayout.findViewById(R.id.inventoryRefreshingTipsText);
    	tipsText.setText(whichTips);
    	
    	new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.inventoryRefreshing))
    		.setView(tipsLayout)
    		.setPositiveButton(getString(R.string.confirm),null)
    		.show();
    	
    }
}
