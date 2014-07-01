package cn.burgeon.core.ui.system;

import java.io.File;

import com.android.volley.toolbox.UnZip;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class SystemDataCleanActivity extends BaseActivity{
	private final String TAG = "SystemDataCleanActivity";
	private boolean LocalDebug = true;
	
	private String dataSavePath;
	
	private CheckBox userDataCheckBox;
	private CheckBox productDataCheckBox;
	private CheckBox vipTypeCheckBox;
	private CheckBox itemStrategyCheckBox;
	private CheckBox systemParamCheckBox;
	
	private TextView statusStoreName;
	private TextView statusTime;

	
	//用户选择项记录标志
	private boolean userDataChecked;
	private boolean productDataChecked;
	private boolean vipTypeChecked;
	private boolean itemStrategyChecked;
	private boolean systemParamChecked;
	
	//删除键
	private Button mDeleteButton;

	//下载完之后保存的文件名字
	//用户资料
	private final String userDataDownloadFileName                    = "userData.zip";
	private final String[]userDataDownloadFileNames                  = {userDataDownloadFileName};
	//商品资料
	private final String productDataDownload_tc_sku_FileName         = "tc_sku.zip";
	private final String productDataDownload_tc_style_FileName       = "tc_style.zip";
	private final String productDataDownload_tc_styleprice_FileName  = "tc_styleprice.zip";
	private final String productDataDownload_TdefClr_FileName        = "TdefClr.zip";
	private final String productDataDownload_TdefSize_FileName       = "TdefSize.zip";
	private final String productDataDownload_tc_payway_FileName      = "tc_payway.zip";
	private final String[]productDataDownloadFileNames = {
			productDataDownload_tc_sku_FileName,
			productDataDownload_tc_style_FileName,
			productDataDownload_tc_styleprice_FileName,
			productDataDownload_TdefClr_FileName,
			productDataDownload_TdefSize_FileName,
			productDataDownload_tc_payway_FileName
	};
	
	//会员类型
	private final String vipTypeDownloadFileName        = "vipType.zip";
	private final String[]vipTypeDownloadFileNames      = {vipTypeDownloadFileName};
	
	//单品策略
	private final String itemStrategyDownloadFileName   = "itemStrategy.zip";
	private final String[]itemStrategyDownloadFileNames = {itemStrategyDownloadFileName};
	
	//系统参数
	private final String systemParamDownloadFileName    = "systemParam.zip";
	private final String[]systemParamDownloadFileNames  = {systemParamDownloadFileName};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_system_data_clean);
		
		//设置资料保存路径
		setDownloadPath();
		init();
	}
	
	private void init(){
		//初始化门店信息
		statusStoreName = (TextView) findViewById(R.id.statusStoreName);
		statusTime = (TextView) findViewById(R.id.statusTime);
		initStoreNameAndTime();
		
		userDataCheckBox = (CheckBox) findViewById(R.id.userDataCheckBox);
		productDataCheckBox = (CheckBox) findViewById(R.id.productDataCheckBox);
		vipTypeCheckBox = (CheckBox) findViewById(R.id.vipTypeCheckBox);
		itemStrategyCheckBox = (CheckBox) findViewById(R.id.itemStrategyCheckBox);
		systemParamCheckBox = (CheckBox) findViewById(R.id.systemParamCheckBox);
		
		mDeleteButton = (Button) findViewById(R.id.deleteButton);
		
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkToStartDelete();
			}
		});		

	}


	//设置资料保存路径
	private void setDownloadPath(){
		if(LocalDebug) Log.d(TAG,"this.getFilesDir().toString()");
		dataSavePath = this.getFilesDir().toString() + "/myDataDownload/";
	}
	
	//获取资料保存路径
	private String getDownloadPath(){
		return dataSavePath;
	}
	
    //检测文件是否存在
    public boolean fileExist(String[]fileName){
    	if(fileName == null) return false;
    	for(String name:fileName){
    		File file = new File(getDownloadPath() + name);
        	if(file.exists()) return true;
    	}
    	return false;
    }
    
    // 初始化门店信息
	private void initStoreNameAndTime(){
		statusStoreName.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
		statusTime.setText(getCurrDate());				
	}
	
	
	private void checkToStartDelete(){		
		//检测用户是否有选择至少一个下载项
		if( !userMadeAChoice() ){
			showTips(R.string.tipsCanYouChooseOne$_$);
			return;
		}
		
		if(systemParamChecked){
			if(fileExist(systemParamDownloadFileNames)){
				Toast.makeText(this, R.string.tipsStartDeleteSystemParam$_$, Toast.LENGTH_SHORT).show();
				deleteFiles(systemParamDownloadFileNames);
				showTips(R.string.tipsFinishDeleteSystemParam$_$);
			}else{
				showTips(R.string.tipsSystemParamNotExist$_$);
			}
			
		}		
		if(itemStrategyChecked){
			if(fileExist(itemStrategyDownloadFileNames)){
				Toast.makeText(this, R.string.tipsStartDeleteItemStrategy$_$, Toast.LENGTH_SHORT).show();
				deleteFiles(itemStrategyDownloadFileNames);
				showTips(R.string.tipsFinishDeleteItemStrategy$_$);
			}else{
				showTips(R.string.tipsItemStrategyNotExist$_$);
			}
			
		}
		if(vipTypeChecked){
			if(fileExist(vipTypeDownloadFileNames)){
				Toast.makeText(this, R.string.tipsStartDeleteVipType$_$, Toast.LENGTH_SHORT).show();
				deleteFiles(vipTypeDownloadFileNames);
				showTips(R.string.tipsFinishDeleteVipType$_$);
			}else{
				showTips(R.string.tipsVipTypeNotExist$_$);
			}
			
		}		
		if(productDataChecked){
			if(fileExist(productDataDownloadFileNames)){
				Toast.makeText(this, R.string.tipsStartDeleteProductData$_$, Toast.LENGTH_SHORT).show();
				deleteFiles(productDataDownloadFileNames);
				showTips(R.string.tipsFinishDeleteProductData$_$);
			}else{
				showTips(R.string.tipsProductDataNotExist$_$);
			}
			
		}		
		if(userDataChecked){
			if(fileExist(userDataDownloadFileNames)){
				Toast.makeText(this, R.string.tipsStartDeleteUserData$_$, Toast.LENGTH_SHORT).show();
				deleteFiles(userDataDownloadFileNames);
				showTips(R.string.tipsFinishDeleteUserData$_$);
			}else{
				showTips(R.string.tipsUserDataNotExist$_$);
			}
			
		}
	}

	//删除文件,如果不能删除就再删一遍
	private void deleteFiles(String[]files){
		for(int i = 0;i < files.length;i++){
			File file = new File(getDownloadPath() + files[i]);
			if( !file.delete() ) file.delete();
		}
	}
	
	//检测用户是否有选择至少一个下载项
	private boolean userMadeAChoice(){
		//init flag status every Download
		userDataChecked = false;
		productDataChecked = false;
		vipTypeChecked = false;
		itemStrategyChecked = false;
		systemParamChecked = false;		
		
		if(userDataCheckBox.isChecked()){ 
			userDataChecked = true;
			if(LocalDebug) Log.d(TAG,"userDataChecked" + userDataChecked);
		}
		
		if(productDataCheckBox.isChecked()){
			productDataChecked = true;
			if(LocalDebug) Log.d(TAG,"productDataChecked" + productDataChecked);
		}
		
		if(vipTypeCheckBox.isChecked()){ 
			vipTypeChecked = true;
			if(LocalDebug) Log.d(TAG,"vipTypeChecked" + vipTypeChecked);
		}
		
		if(itemStrategyCheckBox.isChecked()){
			itemStrategyChecked = true;
			if(LocalDebug) Log.d(TAG,"itemStrategyChecked" + itemStrategyChecked);
		}
		
		if(systemParamCheckBox.isChecked()){ 
			systemParamChecked = true;
			if(LocalDebug) Log.d(TAG,"systemParamChecked" + systemParamChecked);
		}
		
		return (userDataChecked || productDataChecked || vipTypeChecked
					||itemStrategyChecked || systemParamChecked);
	}
	

	
    //显示对话框
    private void showTips(int whichTips){
    	LayoutInflater inflater = getLayoutInflater();
    	//布局文件待添加！！！！！！！！！！
    	View tipsLayout = inflater.inflate(R.layout.inventory_refresh_tips, 
    			(ViewGroup)findViewById(R.id.inventoryRefreshTipsLayout));
    	TextView tipsText = (TextView) tipsLayout.findViewById(R.id.inventoryRefreshingTipsText);
    	tipsText.setText(whichTips);
    	
    	new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.tipsDataDownload))
    		.setView(tipsLayout)
    		.setPositiveButton(getString(R.string.confirm),null)
    		.show();
    	
    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
}
