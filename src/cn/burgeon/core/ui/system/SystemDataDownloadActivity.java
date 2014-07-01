package cn.burgeon.core.ui.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.volley.toolbox.UnZip;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class SystemDataDownloadActivity extends BaseActivity{
		
	private final String TAG = "SystemDataDownloadActivity";
	//调试标志
	private boolean LocalDebug = false;
	//文件下载和解压路径 程序data目录的 myDataDownload文件夹里
	private String downloadPath;
	
	//用户资料下载地址
	private final String userDataURL     =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/sys_user.zip";
	private final String[]userDataURLs   =    {userDataURL};
	
	//商品资料下载地址
	private final String productData_tc_sku_URL         =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/tc_sku.zip";
	private final String productData_tc_style_URL       =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/tc_style.zip";
	private final String productData_tc_styleprice_URL  =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/tc_styleprice.zip";
	private final String productData_TdefClr_URL        =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/TdefClr.zip";
	private final String productData_TdefSize_URL       =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/TdefSize.zip";
	private final String productData_tc_payway_URL      =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/tc_payway.zip";
	private final String[]productDataURLs = {
		productData_tc_sku_URL,
		productData_tc_style_URL,
		productData_tc_styleprice_URL,
		productData_TdefClr_URL,
		productData_TdefSize_URL,
		productData_tc_payway_URL
	};
	
	//会员类型下载地址								
	private final String vipTypeURL        =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/tc_vip.zip";
	private final String[]vipTypeURLs      =    {vipTypeURL};
	
	//单品策略下载地址
	private final String itemStrategyURL   =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/tc_stuff.zip";
	private final String[]itemStrategyURLs =    {itemStrategyURL};
	
	//系统参数下载地址
	private final String systemParamURL    =    "http://g.burgeon.cn:2080/portalpospda/DownloadFiles/sys_parm.zip";
	private final String[]systemParamURLs  =    {systemParamURL};
	
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

	//任务总量
	private long userDataTotalSize;
	private long productDataTotalSize;
	private long vipTypeTotalSize;
	private long itemStrategyTotalSize;
	private long systemParamTotalSize;
	
	//已下载数据总量
	private long userDataTotalRead;
	private long productDataTotalRead;
	private long vipTypeTotalRead;
	private long itemStrategyTotalRead;
	private long systemParamTotalRead;		
		
	//解压完之后返回的绝对路径
	private String[]userDataUnZipFiles     = new String[userDataURLs.length];
	private String[]productDataUnZipFiles  = new String[productDataURLs.length];
	private String[]vipTypeUnZipFiles      = new String[vipTypeURLs.length];
	private String[]itemStrategyUnZipFiles = new String[itemStrategyURLs.length];
	private String[]systemParamUnZipFiles  = new String[systemParamURLs.length];
	
	//开始下载消息
	private final int userDataDownloadStartMsg        = 0x1;
	private final int productDataDownloadStartMsg     = 0x2;
	private final int vipTypeDownloadStartMsg      	  = 0x3;
	private final int itemStrategyDownloadStartMsg    = 0x4;
	private final int systemParamDownloadStartMsg     = 0x5;	

	//下载进度消息
	private final int userDataDownloadProgressMsg     = 0x10;
	private final int productDataDownloadProgressMsg  = 0x11;
	private final int vipTypeDownloadProgressMsg      = 0x12;
	private final int itemStrategyDownloadProgressMsg = 0x13;
	private final int systemParamDownloadProgressMsg  = 0x14;
	
	//下载结束消息
	private final int userDataDownloadFinishMsg       = 0x20;
	private final int productDataDownloadFinishMsg    = 0x21;
	private final int vipTypeDownloadFinishMsg        = 0x22;
	private final int itemStrategyDownloadFinishMsg   = 0x23;
	private final int systemParamDownloadFinishMsg    = 0x24;

	//开始解压消息
	private final int userDataStartUnZipMsg           = 0x30;
	private final int productDataStartUnZipMsg        = 0x31;
	private final int vipTypeStartUnZipMsg            = 0x32;
	private final int itemStrategyStartUnZipMsg       = 0x33;
	private final int  systemParamStartUnZipMsg       = 0x34;

	//解压结束消息
	private final int userDataUnZipFinishMsg          = 0x40;
	private final int productDataUnZipFinishMsg       = 0x41;
	private final int vipTypeUnZipFinishMsg           = 0x42;
	private final int itemStrategyUnZipFinishMsg      = 0x43;
	private final int systemParamUnZipFinishMsg       = 0x44;

	//网络状态变化消息
	private final int networkAvailableMsg          	  = 0x50;
	private final int networkUnAvailableMsg           = 0x51;
	
	//下载线程名字
	private final String USER_DATA_THREAD     = "userDataThread";
	private final String PRODUCT_DATA_THREAD  = "productDataThread";
	private final String VIP_TYPE_THREAD      = "vipTypeThread";
	private final String ITEM_STRATEGY_THREAD = "itemStrategyThread";
	private final String SYSTEM_PARAM_THREAD  = "systemParamThread";

	//线程正在下载标志
	private boolean userDataDownloading;
	private boolean productDataDownloading;
	private boolean vipTypeDownloading;
	private boolean itemStrategyDownloading;
	private boolean systemParamDownloading;

	//下载进度条
	private ProgressBar userDataProgressBar;
	private ProgressBar productDataProgressBar;
	private ProgressBar vipTypeProgressBar;
	private ProgressBar itemStrategyProgressBar;
	private ProgressBar systemParamProgressBar;
	
	//下载百分比
	private TextView userDataPercentTextView;
	private TextView productDataPercentTextView;
	private TextView vipTypePercentTextView;
	private TextView itemStrategyPercentTextView;
	private TextView systemParamPercentTextView;
	
	//用户选择框
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
	
	//下载按钮
	private Button mDownloadButton;
	//解压
	private UnZip unZip;
	//网络状态变化Receiver
	private NetworkReceiver networkReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_system_data_download);	
		//设置下载路径
		setDownloadPath();
		createDownloadDir();
		initZipTool();
		initViews();
		registerNetReceiver();
	}
	
	private void initViews(){
		// 初始化门店信息
		statusStoreName = (TextView) findViewById(R.id.statusStoreName);
		statusTime = (TextView) findViewById(R.id.statusTime);
		initStoreNameAndTime();
		
		userDataCheckBox = (CheckBox) findViewById(R.id.userDataCheckBox);
		productDataCheckBox = (CheckBox) findViewById(R.id.productDataCheckBox);
		vipTypeCheckBox = (CheckBox) findViewById(R.id.vipTypeCheckBox);
		itemStrategyCheckBox = (CheckBox) findViewById(R.id.itemStrategyCheckBox);
		systemParamCheckBox = (CheckBox) findViewById(R.id.systemParamCheckBox);

		userDataProgressBar = (ProgressBar) findViewById(R.id.userDataProgressBar);
		productDataProgressBar = (ProgressBar) findViewById(R.id.productDataProgressBar);
		vipTypeProgressBar = (ProgressBar) findViewById(R.id.vipTypeProgressBar);
		itemStrategyProgressBar = (ProgressBar) findViewById(R.id.itemStrategyProgressBar);
		systemParamProgressBar = (ProgressBar) findViewById(R.id.systemParamProgressBar);
		
		userDataPercentTextView = (TextView) findViewById(R.id.userDataPercentTextView);
		productDataPercentTextView = (TextView) findViewById(R.id.productDataPercentTextView);
		vipTypePercentTextView = (TextView) findViewById(R.id.vipTypePercentTextView);
		itemStrategyPercentTextView = (TextView) findViewById(R.id.itemStrategyPercentTextView);
		systemParamPercentTextView = (TextView) findViewById(R.id.systemParamPercentTextView);
		
		mDownloadButton = (Button) findViewById(R.id.downloadButton);
		
		mDownloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){ 
				checkToStartDownload();
			}
		});			
	}

	//设置下载路径
	private void setDownloadPath(){
		if(LocalDebug) Log.d(TAG,"this.getFilesDir().toString()");
		downloadPath = this.getFilesDir().toString() + "/myDataDownload/";
	}

	//获取下载路径
	private String getDownloadPath(){
		return downloadPath;
	}
	
	//创建下载目录
	private void createDownloadDir(){
		File destDir = new File(getDownloadPath());
	    if (!destDir.exists()) {
	    	destDir.mkdirs();
	    	if(LocalDebug) Log.d(TAG,"Create Download dir success!");
	    }else{
	    	if(LocalDebug) Log.d(TAG,"Dir already exist!");
	    }	   
	}

	//初始化解压工具
	private void initZipTool(){
		unZip = new UnZip();
	}
	
    // 初始化门店信息
	private void initStoreNameAndTime(){
		statusStoreName.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
		statusTime.setText(getCurrDate());				
	}

	//注册网络状态接收器
	private void registerNetReceiver(){
		if(networkReceiver == null){
			IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
			networkReceiver = new NetworkReceiver();
			registerReceiver(networkReceiver,intentFilter);
		}
	}
	
	private void checkToStartDownload(){
		
		//检测用户是否有选择至少一个下载项
		if( !userMadeAChoice() ){
			showTips(R.string.tipsCanYouChooseOne$_$);
			return;
		}
			
		//检查网络连接状态
		if( !networkReachable() ){
			showTips(R.string.tipsNetworkUnReachable$_$);
			return;
		}
		
		//检测电池电量
		if( !batteryEnough() ){
			showTips(R.string.tipsBatteryNotEnough$_$);
			return;
		}
		
		//访问服务器并等待服务器响应
		if( !serverReachable() ){
			showTips(R.string.tipsServerUnReachable$_$);
			return;
		}

		//检测存储器剩余存储空间
		if( !memoryEnough() ){
			showTips(R.string.tipsMemoryNotEnough$_$);
			return;
		}
		
		//创建下载线程
		startDownload();
		
		//检测下载文件是否完整
		if( !checkDownloadFiles()){
			showTips(R.string.tipsPleaseDownloadAgain$_$);
			return;
		}
		
		//放到下载线程里干
		//解压下载文件
		//unZipDownloadFiles();
		
		//解析下载内容
		//parseDownloadFiles();
		
		//将下载文件存入数据库
		//saveDownloadFilesToSqlite();
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
	
	//检测网络状态
	private boolean networkReachable(){
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())  return true;
		return false;	
	}
	
	//检测电池电量
	private boolean batteryEnough(){
		//即将添加，敬请期待！
		return true;		
	}
	
	//检测服务器状态
	private boolean serverReachable(){
		//即将添加，敬请期待！
		return true;
	}
	
	//获取即将下载的文件大小
	private void checkTheSizeOfDownloadData(){
		//即将添加，敬请期待！				
	}
	
	//检测设备存储空间大小
	private boolean memoryEnough(){
		//计算应下载数据包的大小
		checkTheSizeOfDownloadData();
		return true;
	}
	
	//检测文件是否存在
    public boolean fileExist(String fileName){
    	if(fileName == null) return false;
        File file = new File(getDownloadPath() + fileName);
        return file.exists();
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

    //网络状态变化接收器
	private class NetworkReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			sendNetStateTips();
		}
	}
    
	//用户所有选择同时下载
	private void startDownload(){	
		
		//开始下载系统参数
		if(systemParamChecked && !fileExist(systemParamDownloadFileNames) && !systemParamDownloading){ 
			//干活线程
			new Thread(new downloadFileRunnable(systemParamURLs,getDownloadPath(),systemParamDownloadFileNames),
					SYSTEM_PARAM_THREAD)
					.start();
		}else if(systemParamChecked && fileExist(systemParamDownloadFileNames) && !systemParamDownloading){
			showTips(R.string.tipsSystemParamDownloadFileExist);
		}else if(systemParamChecked && fileExist(systemParamDownloadFileNames) && systemParamDownloading){
			showTips(R.string.tipsSystemParamDownloading);
		}
		
		//开始下载单品策略
		if(itemStrategyChecked && !fileExist(itemStrategyDownloadFileNames) && !itemStrategyDownloading){ 
			//干活线程
			new Thread(new downloadFileRunnable(itemStrategyURLs,getDownloadPath(),itemStrategyDownloadFileNames),
					ITEM_STRATEGY_THREAD)
					.start();			
		}else if(itemStrategyChecked && fileExist(itemStrategyDownloadFileNames) && !itemStrategyDownloading){
			showTips(R.string.tipsItemStrategyDownloadFileExist);
		}else if(itemStrategyChecked && fileExist(itemStrategyDownloadFileNames) && itemStrategyDownloading){
			showTips(R.string.tipsItemStrategyDownloading);
		}
		
		//开始下载会员类型
		if(vipTypeChecked && !fileExist(vipTypeDownloadFileNames) && !vipTypeDownloading){ 
			//干活线程
			new Thread(new downloadFileRunnable(vipTypeURLs,getDownloadPath(),vipTypeDownloadFileNames),
					VIP_TYPE_THREAD)
					.start();
		}else if(vipTypeChecked && fileExist(vipTypeDownloadFileNames) && !vipTypeDownloading){
			showTips(R.string.tipsVipTypeDownloadFileExist);
		}else if(vipTypeChecked && fileExist(vipTypeDownloadFileNames) && vipTypeDownloading){
			showTips(R.string.tipsVipTypeDownloading);
		}
		
		//开始下载商品资料
		if(productDataChecked && !fileExist(productDataDownloadFileNames) && !productDataDownloading){ 
			//干活线程
			new Thread(new downloadFileRunnable(productDataURLs,getDownloadPath(),productDataDownloadFileNames),
					PRODUCT_DATA_THREAD)
					.start();
		}else if(productDataChecked && fileExist(productDataDownloadFileNames) && !productDataDownloading){
			showTips(R.string.tipsProductDataDownloadFileExist);
		}else if(productDataChecked && fileExist(productDataDownloadFileNames) && productDataDownloading){
			showTips(R.string.tipsProductDataDownloading);
		}
		
		//开始下载用户资料
		if(userDataChecked && !fileExist(userDataDownloadFileNames) && !userDataDownloading){ 
			//干活线程
			new Thread(new downloadFileRunnable(userDataURLs,getDownloadPath(),userDataDownloadFileNames),
					USER_DATA_THREAD)
					.start();
		}else if(userDataChecked && fileExist(userDataDownloadFileNames) && !userDataDownloading){
			showTips(R.string.tipsUserDataDownloadFileExist);
		}else if(userDataChecked && fileExist(userDataDownloadFileNames) && userDataDownloading){
			showTips(R.string.tipsUserDataDownloading);
		}
	}
	
	//DownLoad File Runnable
	class downloadFileRunnable implements Runnable{
		private String[]url;
		private String savePath;
		private String[]saveName;
				
		downloadFileRunnable(String[]url,String savePath,String[]saveName){
			downloadFileRunnable.this.url = url;
			downloadFileRunnable.this.savePath = savePath;
			downloadFileRunnable.this.saveName = saveName;
		}
		
		@Override
		public void run() {
			//下载
			
            //*****发送下载开始消息*****//
            sendDownloadStartMsg();
            //初始化已下载文件大小
            initTotalReadSize();
            //计算每个任务总下载大小
            countCurrentTaskTotalSize();
            //开始下载
			for(int i = 0;i < url.length;i ++){
				downLoadFile(url[i],savePath,saveName[i]);
			}
            //******发送下载结束信息******//
            sendDownloadFinishMsg();
            
			//解压
			try {
				unZipDownloadFiles();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			//解析并插入数据库
			parseDownloadFiles();
			
		}
	}

	//检测下载的文件
	private boolean checkDownloadFiles(){
		//即将添加，敬请期待
		return true;		
	}
	
	//解压下载文件
	private void unZipDownloadFiles() throws IOException{
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,"USER_DATA_UNZIP_THREAD" + "Start");
			sendUnZipMsg(userDataStartUnZipMsg);
			for(int i = 0;i < userDataDownloadFileNames.length;i++){
				userDataUnZipFiles[i] =  unZip.unZip(getDownloadPath() + userDataDownloadFileNames[i],getDownloadPath());
			}
			sendUnZipMsg(userDataUnZipFinishMsg);

		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,"PRODUCT_DATA_UNZIP_THREAD" + "Start");
			sendUnZipMsg(productDataStartUnZipMsg);
			for(int i = 0;i < productDataDownloadFileNames.length;i ++){
				productDataUnZipFiles[i] =  unZip.unZip(getDownloadPath() + productDataDownloadFileNames[i],getDownloadPath());
			}
			sendUnZipMsg(productDataUnZipFinishMsg);

		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			if(LocalDebug) Log.d(TAG,"VIP_TYPE_UNZIP_THREAD" + "Start");
			sendUnZipMsg(vipTypeStartUnZipMsg);
			for(int i = 0;i < vipTypeDownloadFileNames.length;i++){
				vipTypeUnZipFiles[i] =  unZip.unZip(getDownloadPath() + vipTypeDownloadFileNames[i],getDownloadPath());
			}
			sendUnZipMsg(vipTypeUnZipFinishMsg);

		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			if(LocalDebug) Log.d(TAG,"ITEM_STRATEGY_UNZIP_THREAD" + "Start");
			sendUnZipMsg(itemStrategyStartUnZipMsg);
			for(int i = 0;i < itemStrategyDownloadFileNames.length;i++){
				itemStrategyUnZipFiles[i] =  unZip.unZip(getDownloadPath() + itemStrategyDownloadFileNames[i],getDownloadPath());
			}
			sendUnZipMsg(itemStrategyUnZipFinishMsg);

		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			if(LocalDebug) Log.d(TAG,"SYSTEM_PARAM_UNZIP_THREAD" + "Start");
			sendUnZipMsg(systemParamStartUnZipMsg);
			for(int i = 0;i < systemParamDownloadFileNames.length;i++){
				systemParamUnZipFiles[i] =  unZip.unZip(getDownloadPath() + systemParamDownloadFileNames[i],getDownloadPath());
			}
			sendUnZipMsg(systemParamUnZipFinishMsg);
			
		}		
	}

	//解析下载文件
	private void parseDownloadFiles(){
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,"USER_DATA_PARSE_THREAD" + "Start");
			//sendUnZipMsg(userDataStartParseMsg);
			for(int i = 0;i < userDataUnZipFiles.length;i++){
				parseFile(userDataUnZipFiles[i]);
			}
			//sendUnZipMsg(userDataParseFinishMsg);

		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,"PRODUCT_DATA_PARSE_THREAD" + "Start");
			//sendUnZipMsg(productDataStartParseMsg);
			for(int i = 0;i < productDataUnZipFiles.length;i++){
				parseFile(productDataUnZipFiles[i]);
			}
			//sendUnZipMsg(productDataParseFinishMsg);

		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			if(LocalDebug) Log.d(TAG,"VIP_TYPE_PARSE_THREAD" + "Start");
			//sendUnZipMsg(vipTypeStartParseMsg);
			for(int i = 0;i < vipTypeUnZipFiles.length;i++){
				parseFile(vipTypeUnZipFiles[i]);
			}
			//sendUnZipMsg(vipTypeParseFinishMsg);

		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			if(LocalDebug) Log.d(TAG,"ITEM_STRATEGY_PARSE_THREAD" + "Start");
			//sendUnZipMsg(itemStrategyStartParseMsg);
			for(int i = 0;i < itemStrategyUnZipFiles.length;i++){
				parseFile(itemStrategyUnZipFiles[i]);
			}
			//sendUnZipMsg(itemStrategyParseFinishMsg);

		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			if(LocalDebug) Log.d(TAG,"SYSTEM_PARAM_PARSE_THREAD" + "Start");
			//sendUnZipMsg(systemParamStartParseMsg);
			for(int i = 0;i < systemParamUnZipFiles.length;i++){
				parseFile(systemParamUnZipFiles[i]);
			}
			//sendUnZipMsg(systemParamParseFinishMsg);
			
		}
	}

	//开始解析
	private void parseFile(String filePath){
        BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"gbk"));
    		String line = null; 
    		String[]data = null;
           
    		db.beginTransaction();
            while ((line = reader.readLine()) != null) { 
            	data = line.split(",");
  		        if(LocalDebug) Log.d(TAG,"____" + data.length );
  		        //让我们来插表吧
			              插表(filePath,data);               
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {}
            }
        }
	
	}

	//插表！
	private void 插表(String filePath,String[]temp){
		if(LocalDebug) Log.d(TAG,"插表！！");
		if(filePath.equals(userDataUnZipFiles[0])){
			if(LocalDebug) Log.d(TAG,"插user表");
		}
		
		if(filePath.equals(productDataUnZipFiles[0])){
			if(LocalDebug) Log.d(TAG,"插tc_sku表");
			try{
				db.execSQL("insert into tc_sku(sku,style,clr,sizeid,pname) values (?,?,?,?,?)", 
						new Object[]{temp[0],temp[1].substring(2),temp[2].substring(2),
						temp[3].substring(2),temp[4].substring(2)});
			}catch(Exception e){
				if(LocalDebug) Log.d(TAG,"插tc_sku表失败！");
			}				
		}
		
		if(filePath.equals(productDataUnZipFiles[1])){
			if(LocalDebug) Log.d(TAG,"插tc_style表");
			try{
				db.execSQL("insert into tc_style(style,style_name,attrib1,attrib2,attrib3,attrib4,attrib5,attrib6,attrib7,attrib8,attrib9,attrib10) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?)", 
						new Object[]{temp[0],temp[1].substring(2),temp[2].substring(2),
						temp[3].substring(2),temp[4].substring(2),temp[5].substring(2)
						,temp[6].substring(2),temp[7].substring(2),temp[8].substring(2)
						,temp[9].substring(2),temp[10].substring(2),temp[11].substring(2)});	
			}catch(Exception e){
				if(LocalDebug) Log.d(TAG,"插tc_style表失败！");
			}
		}
		
		if(filePath.equals(productDataUnZipFiles[2])){
			if(LocalDebug) Log.d(TAG,"插styleprice表");
			try{
				db.execSQL("insert into tc_styleprice(style,store,fprice) values (?,?,?)", 
						new Object[]{temp[0],temp[1].substring(2),temp[2].substring(2)});
			}catch(Exception e){
				if(LocalDebug) Log.d(TAG,"插styleprice表失败！");
			}			
		}
		
		if(filePath.equals(productDataUnZipFiles[3])){
			if(LocalDebug) Log.d(TAG,"插TdefClr表");
			try{
				db.execSQL("insert into tdefclr(clr,clrname) values (?,?)", 
					new Object[]{temp[0],temp[1].substring(2)});
			}catch(Exception e){
				if(LocalDebug) Log.d(TAG,"插TdefClr表失败！");
			}
		}
		
		if(filePath.equals(productDataUnZipFiles[4])){
			if(LocalDebug) Log.d(TAG,"插TdefSize表");
			try{
				db.execSQL("insert into tdefsize(sizeid,sizename) values (?,?)", 
						new Object[]{temp[0],temp[1].substring(2)});
			}catch(Exception e){
				if(LocalDebug) Log.d(TAG,"插TdefClr表失败！");
			}			
		}

		if(filePath.equals(productDataUnZipFiles[5])){
			if(LocalDebug) Log.d(TAG,"插tc_payway表");
			try{
				db.execSQL("insert into tc_payway(_id,name) values (?,?)", 
						new Object[]{temp[0],temp[1].substring(2)});
			}catch(Exception e){
				if(LocalDebug) Log.d(TAG,"插tc_payway表失败！");
			}			
		}
		
		if(filePath.equals(vipTypeUnZipFiles[0])){
			if(LocalDebug) Log.d(TAG,"插vipType表");
			try{
				db.execSQL("insert into tc_vip(_id,name,discount,rate) values (?,?,?,?)", 
						new Object[]{temp[0],temp[1].substring(2),temp[2].substring(2),temp[3].substring(2)});
			}catch(Exception e){
				if(LocalDebug) Log.d(TAG,"插VipType表失败！");
			}
			
		}
		
		if(filePath.equals(itemStrategyUnZipFiles[0])){
			if(LocalDebug) Log.d(TAG,"插itemStrategy表");
			
		}
		
		if(filePath.equals(systemParamUnZipFiles[0])){
			if(LocalDebug) Log.d(TAG,"插systemParam表");
			
		}
	}
	
	
	public void downLoadFile(String urlstr,String savePath,String saveName){
	     InputStream inputStream = null;
	     int fileSize = 0;
	     
	     if(fileExist(saveName)){
	    	 processExistFiles();
	     }else{
	    	 
	 		 try {
				URL url=new URL(urlstr);
				HttpURLConnection urlConn=(HttpURLConnection) url.openConnection();
				inputStream=urlConn.getInputStream();
				fileSize = urlConn.getContentLength();
				if(LocalDebug) Log.d(TAG,"__FILESIZE__" + fileSize);
			 } catch (MalformedURLException e) {
				e.printStackTrace();
				showTips(R.string.tipsPleaseDownloadAgain$_$);
			 } catch (IOException e) {
				e.printStackTrace();
				showTips(R.string.tipsPleaseDownloadAgain$_$);
			 }
	    	 
	         File resultFile = writeToFilefromInput(savePath, saveName, inputStream,fileSize);
	         if(resultFile == null){
	             return ;
	         }
	     }
	}
	
	//发送下载开始消息
	private void sendDownloadStartMsg(){
		//创建消息
        Message msg = new Message();
        
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,USER_DATA_THREAD + "Start");
			msg.what = userDataDownloadStartMsg;
			//设置下载开始标志
			userDataDownloading = true;
		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,PRODUCT_DATA_THREAD + "Start");
			msg.what = productDataDownloadStartMsg;
			//设置下载开始标志
			productDataDownloading = true;
		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			if(LocalDebug) Log.d(TAG,VIP_TYPE_THREAD + "Start");
			msg.what = vipTypeDownloadStartMsg;
			//设置下载开始标志				
			vipTypeDownloading = true;
		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			if(LocalDebug) Log.d(TAG,ITEM_STRATEGY_THREAD + "Start");
			msg.what = itemStrategyDownloadStartMsg;
			//设置下载开始标志
			itemStrategyDownloading = true;
		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			if(LocalDebug) Log.d(TAG,SYSTEM_PARAM_THREAD + "Start");
			msg.what = systemParamDownloadStartMsg;
			//设置下载开始标志
			systemParamDownloading = true;			
		}
		//发送！
		updateTipsHandler.sendMessage(msg);
	}

	//发送下载进度消息
	private void sendDownloadProgressMsg(){
		//创建消息
		Message msg = new Message();
		
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,USER_DATA_THREAD + "Progress");
			msg.what = userDataDownloadProgressMsg;
		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,PRODUCT_DATA_THREAD + "Progress");
			msg.what = productDataDownloadProgressMsg;
		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			if(LocalDebug) Log.d(TAG,VIP_TYPE_THREAD + "Progress");
			msg.what = vipTypeDownloadProgressMsg;
		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			if(LocalDebug) Log.d(TAG,ITEM_STRATEGY_THREAD + "Progress");
			msg.what = itemStrategyDownloadProgressMsg;
		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			if(LocalDebug) Log.d(TAG,SYSTEM_PARAM_THREAD + "Progress");
			msg.what = systemParamDownloadProgressMsg;		
		}
		//OK baby，we send Message now
		updateTipsHandler.sendMessage(msg);
	}
	
	//发送下载结束消息
	private void sendDownloadFinishMsg(){
		//创建消息
		Message msg = new Message();
		
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,USER_DATA_THREAD + "OVER");
			msg.what = userDataDownloadFinishMsg;
			//设置下载完毕标志
			userDataDownloading = false;
		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			if(LocalDebug) Log.d(TAG,PRODUCT_DATA_THREAD + "OVER");
			msg.what = productDataDownloadFinishMsg;
			//设置下载完毕标志
			productDataDownloading = false;
		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			if(LocalDebug) Log.d(TAG,VIP_TYPE_THREAD + "OVER");
			msg.what = vipTypeDownloadFinishMsg;
			//设置下载完毕标志				
			vipTypeDownloading = false;
		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			if(LocalDebug) Log.d(TAG,ITEM_STRATEGY_THREAD + "OVER");
			msg.what = itemStrategyDownloadFinishMsg;
			//设置下载完毕标志
			itemStrategyDownloading = false;
		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			if(LocalDebug) Log.d(TAG,SYSTEM_PARAM_THREAD + "OVER");
			msg.what = systemParamDownloadFinishMsg;
			//设置下载完毕标志
			systemParamDownloading = false;			
		}
		//OK，BABY，We send Message Now
		updateTipsHandler.sendMessage(msg);
	}

	//发送解压消息
	private void sendUnZipMsg(final int tipsMsg){
		//创建消息
        Message msg = new Message();
        msg.what = tipsMsg;
		//发送！
		updateTipsHandler.sendMessage(msg);
	}

	//发送网络状态消息
	private void sendNetStateTips(){
        Message msg = new Message();
        if(this.networkReachable()){
        	msg.what = networkAvailableMsg;
        }else if(!this.networkReachable()){
        	msg.what = networkUnAvailableMsg;
        }
        updateTipsHandler.sendMessage(msg);
	}

	
	//根据网址得到输入流
	public InputStream getInputStreamFormUrl(String urlstr){
		
		InputStream inputStream = null;
		
		try {
			URL url=new URL(urlstr);
			HttpURLConnection urlConn=(HttpURLConnection) url.openConnection();
			inputStream=urlConn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			showTips(R.string.tipsPleaseDownloadAgain$_$);
		} catch (IOException e) {
			e.printStackTrace();
			showTips(R.string.tipsPleaseDownloadAgain$_$);
		}
		
		return inputStream;
    }
	
	private void processExistFiles(){
		//即将添加，敬请期待！
	}
	    
    //创建下载文件
    public File createDownloadFile(String fileName){   	
        File file = new File(fileName);
        
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return file;
    }
    
    //将一个inputStream里面的数据写到下载文件中
    public File writeToFilefromInput(String path,String fileName,InputStream inputStream,long fileSize){
        
        File file = createDownloadFile(path+fileName);
        OutputStream outStream = null;
        
        try {
        	//获取输出文件流
            outStream = new FileOutputStream(file);          
            //缓冲大小
            byte[] buffer = new byte[4*1024];
            
            //每次读取大小
            int hasRead = 0;
            //累计读取大小
            long totalRead = 0;
            //将下载内容写入到文件中
                       
            //循环读取网络内容
            while((hasRead = inputStream.read(buffer) ) > 0){
            	totalRead += hasRead;
            	
            	//打印显示当前正在下载的文件已下载百分比
            	if(LocalDebug) Log.d(TAG,"__Current File __Downloaded____:" + (int)(((float)totalRead / (float)fileSize) * 100) + " %");
            	
            	//记录当前任务的已下载量
            	recordCurrentTaskDownloadSize(hasRead);
            	
            	//******发送进度条更新消息*****//
            	sendDownloadProgressMsg();
            	
            	//写文件
                outStream.write(buffer,0,hasRead);
            }
              
            outStream.flush();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	//关闭输出流
            try {
            	if(outStream != null){
            		outStream.close();
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
  
    //初始化下载记录
    private void initTotalReadSize(){
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){ 
			userDataTotalRead = 0;
		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			productDataTotalRead = 0;
		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			vipTypeTotalRead = 0;
		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			itemStrategyTotalRead = 0;
		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			systemParamTotalRead = 0;	
		}    	
    }

    //记录总下载量,因为一次下载有好多文件
    private void recordCurrentTaskDownloadSize(final long hasRead){
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){ 
			userDataTotalRead += hasRead;
		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			productDataTotalRead += hasRead;
		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			vipTypeTotalRead += hasRead;
		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			itemStrategyTotalRead += hasRead;
		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			systemParamTotalRead += hasRead;	
		}    	
    }
 
    //计算给定网址下载文件大小
    private void countCurrentTaskTotalSize(){
		if(Thread.currentThread().getName().equals(USER_DATA_THREAD)){
			for(int i = 0;i < userDataURLs.length;i++){
				userDataTotalSize += countDownloadFileSize(userDataURLs[i]);
			}
			if(LocalDebug) Log.d(TAG,"USER DATA TOTAL DOWNLOAD SIZE:" + userDataTotalSize);
		}
		if(Thread.currentThread().getName().equals(PRODUCT_DATA_THREAD)){ 
			for(int i = 0;i < productDataURLs.length;i++){
				productDataTotalSize += countDownloadFileSize(productDataURLs[i]);
			}
			if(LocalDebug) Log.d(TAG,"PRODUCT DATA TOTAL DOWNLOAD SIZE:" + productDataTotalSize);
		}
		if(Thread.currentThread().getName().equals(VIP_TYPE_THREAD)){
			for(int i = 0;i < vipTypeURLs.length;i++){
				vipTypeTotalSize += countDownloadFileSize(vipTypeURLs[i]);
			}
			if(LocalDebug) Log.d(TAG,"VIP TYPE TOTAL DOWNLOAD SIZE:" + vipTypeTotalSize);
		}
		if(Thread.currentThread().getName().equals(ITEM_STRATEGY_THREAD)) {
			for(int i = 0;i < itemStrategyURLs.length;i++){
				itemStrategyTotalSize += countDownloadFileSize(itemStrategyURLs[i]);
			}
			if(LocalDebug) Log.d(TAG,"ITEM STRATEGY TOTAL DOWNLOAD SIZE:" + itemStrategyTotalSize);
		}
		if(Thread.currentThread().getName().equals(SYSTEM_PARAM_THREAD)) {
			for(int i = 0;i < systemParamURLs.length;i++){
				systemParamTotalSize += countDownloadFileSize(systemParamURLs[i]);
			}
			if(LocalDebug) Log.d(TAG,"SYSTEM PARAM TOTAL DOWNLOAD SIZE:" + systemParamTotalSize);
		}    	
    }
 
    //得到给定网址资源的大小
    private int countDownloadFileSize(final String urlstr){
	     int fileSize = 0;
	     
		 try {
			URL url = new URL(urlstr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			fileSize = urlConn.getContentLength();
			if(LocalDebug) Log.d(TAG,"_This Download File Size__" + fileSize);
		 } catch (MalformedURLException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		 }
		 return fileSize;    	
    }
    
    private long disPlayBlockStatus(){
    	StatFs statfs=new StatFs("/sdcard/"); 
	    //获取block的SIZE 	    
	    long blockSize = statfs.getBlockSize(); 
	    if(LocalDebug) Log.d(TAG,"BlockSize:" + blockSize);
	    //获取BLOCK数量 
	    long totalBlocks = statfs.getBlockCount(); 
	    if(LocalDebug) Log.d(TAG,"totalBocks:" + totalBlocks);
	    //可用的Block的数量 
	    long availableBlock = statfs.getAvailableBlocks(); 
	    if(LocalDebug) Log.d(TAG,"avilable bolck:" + availableBlock);
	    if(LocalDebug) Log.d(TAG,"Size:" + blockSize * availableBlock / 1024 /1024);
	    return (long) blockSize * availableBlock;
    }
 
	
	//显示各种提示，更新进度条
	Handler updateTipsHandler = new Handler(){				
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			
				//显示下载开始消息
				case userDataDownloadStartMsg:
					userDataProgressBar.setVisibility(View.VISIBLE);
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartDownloadUserData, Toast.LENGTH_SHORT).show();
					break;
				case productDataDownloadStartMsg:
					productDataProgressBar.setVisibility(View.VISIBLE);
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartDownloadProductData, Toast.LENGTH_SHORT).show();
					break;
				case vipTypeDownloadStartMsg:
					vipTypeProgressBar.setVisibility(View.VISIBLE);
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartDownloadVipType, Toast.LENGTH_SHORT).show();
					break;
				case itemStrategyDownloadStartMsg:
					itemStrategyProgressBar.setVisibility(View.VISIBLE);
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartDownloadItemStrategy, Toast.LENGTH_SHORT).show();
					break;
				case systemParamDownloadStartMsg:
					systemParamProgressBar.setVisibility(View.VISIBLE);
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartDownloadSystemParam, Toast.LENGTH_SHORT).show();
					break;
					
				//显示下载完成消息
				case userDataDownloadFinishMsg:
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsUserDataDownloadFinish, Toast.LENGTH_SHORT).show();
					break;
				case productDataDownloadFinishMsg:
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsProductDataDownloadFinish, Toast.LENGTH_SHORT).show();
					break;
				case vipTypeDownloadFinishMsg:
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsVipTypeDownloadFinish, Toast.LENGTH_SHORT).show();
					break;
				case itemStrategyDownloadFinishMsg:
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsItemStrategyDownloadFinish, Toast.LENGTH_SHORT).show();
					break;
				case systemParamDownloadFinishMsg:
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsSystemParamDownloadFinish, Toast.LENGTH_SHORT).show();
					break;
					
				//设置下载进度		
				case userDataDownloadProgressMsg:{
					int progress = (int)(((float)userDataTotalRead / (float)userDataTotalSize) * 100);
					userDataProgressBar.setProgress(progress);
					userDataPercentTextView.setText(progress + "%");
					break;
				}
				case productDataDownloadProgressMsg:{
					int progress = (int)(((float)productDataTotalRead / (float)productDataTotalSize) * 100);
					productDataProgressBar.setProgress(progress);
					productDataPercentTextView.setText(progress + "%");
					break;
				}
				case vipTypeDownloadProgressMsg:{
					int progress = (int)(((float)vipTypeTotalRead / (float)vipTypeTotalSize) * 100);
					vipTypeProgressBar.setProgress(progress);
					vipTypePercentTextView.setText(progress + "%");
					break;
				}
				case itemStrategyDownloadProgressMsg:{
					int progress = (int)(((float)itemStrategyTotalRead / (float)itemStrategyTotalSize) * 100);
					itemStrategyProgressBar.setProgress(progress);
					itemStrategyPercentTextView.setText(progress + "%");
					break;
				}
				case systemParamDownloadProgressMsg:{
					int progress = (int)(((float)systemParamTotalRead / (float)systemParamTotalSize) * 100);
					systemParamProgressBar.setProgress(progress);
					systemParamPercentTextView.setText(progress + "%");
					break;	
				}
				
				//显示开始解压信息
				case userDataStartUnZipMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartUnZipUserData, Toast.LENGTH_SHORT).show();
					break;	
				}
				case productDataStartUnZipMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartUnZipProductData, Toast.LENGTH_SHORT).show();
					break;	
				}
				case vipTypeStartUnZipMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartUnZipVipType, Toast.LENGTH_SHORT).show();
					break;	
				}
				case itemStrategyStartUnZipMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartUnZipItemStrategy, Toast.LENGTH_SHORT).show();
					break;	
				}
				case systemParamStartUnZipMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsStartUnZipSystemParam, Toast.LENGTH_SHORT).show();
					break;	
				}

				//显示解压结束信息
				case userDataUnZipFinishMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsUnZipUserDataFinish, Toast.LENGTH_SHORT).show();
					break;	
				}
				case productDataUnZipFinishMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsUnZipProductDataFinish, Toast.LENGTH_SHORT).show();
					break;	
				}
				case vipTypeUnZipFinishMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsUnZipVipTypeFinish, Toast.LENGTH_SHORT).show();
					break;	
				}
				case itemStrategyUnZipFinishMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsUnZipItemStrategyFinish, Toast.LENGTH_SHORT).show();
					break;	
				}
				case systemParamUnZipFinishMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsUnZipSystemParamFinish, Toast.LENGTH_SHORT).show();
					break;	
				}
				//显示网络状态消息
				case networkAvailableMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsNetworkAvailable, Toast.LENGTH_SHORT).show();
					break;	
				}
				case networkUnAvailableMsg:{
					Toast.makeText(SystemDataDownloadActivity.this, R.string.tipsNetworkUnAvailable, Toast.LENGTH_SHORT).show();
					break;	
				}
			}
			
		}
		
	};
	
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
	protected void onDestroy(){
		super.onDestroy();
		if(networkReceiver != null){
			unregisterReceiver(networkReceiver);
		}
	}
	@Override
	public void onBackPressed() {
		if(	userDataDownloading || productDataDownloading || vipTypeDownloading || itemStrategyDownloading ||systemParamDownloading){
			showTips(R.string.downloding);
			return;			
		}
		super.onBackPressed();
	}
}

	