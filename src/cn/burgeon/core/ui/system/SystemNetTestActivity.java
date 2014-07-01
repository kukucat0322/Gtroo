
package cn.burgeon.core.ui.system;

import java.io.IOException; 
import java.io.InputStream ;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;  
import java.net.URL;
import java.util.HashMap;
import java.util.Map; 
import java.util.Random; 

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;  
import org.apache.http.client.methods.HttpGet ; 
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import com.android.volley.Response;

import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils ; 
import android.util.Log ; 
import android.view.LayoutInflater ; 
import android.view.View ; 
import android.view.View.OnClickListener;
import android.view.ViewGroup; 
import android.widget.Button ;  
import android.widget.ScrollView  ; 
import android.widget.TextView; 
import android.widget.Toast;
import cn.burgeon.core.App ; 
import cn.burgeon.core.R ;
import cn.burgeon.core.ui.BaseActivity  ; 
import cn.burgeon.core.utils.PreferenceUtils;
                                                                                                                 	
                               ;;;;;;;;;;;
	        ;;;;;;;;;;;;;;;;  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	     ;;;;;;;;;;;;;;;;   ;public class SystemNetTestActivity extends BaseActivity{;;;
	  ;;;;;;;;;;;;;;;;   ;;;private final static String TAG = "SystemNetTestActivity";
	;;;;;;;;;;;;;;;;  ;;;;;;;;;;;              ;;;;;;;;;;;;              ;;;;;;;;;;;    
	   ;;;;;;;;;;;                              ;;;;;;;;;;;;             ;;;;;;;;;
	   ;;;;;;;;;;;                               ;;;;;;;;;;;;            
	   ;;;;;;;;;;;                                ;;;;;;;;;;;;
	   ;;;;;;;;;;;              ;;;;;;;;           ;;;;;;;;;;;;      ;;;;;;;;;
	   ;;;;;;;;;;;             ;;;;;;;;            ;;;;;;;;;;;;       ;;;;;;;;;
	   ;;;;;;;;;;;            ;;;;;;;;             ;;;;;;;;;;;;        ;;;;;;;;;
	   ;;;;;;;;;;;           ;;;;;;;;              ;;;;;;;;;;;;         ;;;;;;;;;
	   ;;;;;;;;;;;          ;;;;;;;;              ;;;;;;;;;;;;          ;;;;;;;;;
	   ;;;;;;;;;;;                               ;;;;;;;;;;;;           ;;;;;;;;;
	   ;;;;;;;;;;;                             ;;;;;;;;;;;;           
	   ;;;;;;;;;;;                 ;;;;;;;;;;;;;;;;;;;;;;;            
	   ;;;;;;;;;;;                   ;;;;;;;;;;;;;;;;;;;;
	                                   ;;;;;;;;;;;;;;;;
	
	private final int downloadURLAvailableMsg      = 1;
	private final int downloadURLUnAvailableMsg    = 2;
	private final int interactiveURLAvailableMsg   = 3;
	private final int interactiveURLUnAvailableMsg = 4;
	private final int networkUnAvailableMsg = 5;
	private final int URLAddressNotSetMsg   = 6;
	private final int startTestDownloadServerMsg    = 7;
	private final int startTestInteractiveServerMsg = 8;
        
    private TextView downloadURLAddressTitleTv;
    private TextView downloadURLAddressTv;
    private TextView interactiveURLAddressTitleTv;
    private TextView interactiveURLAddressTv;
    private TextView niuBEffectText;
    private Button startButton;
    
    private boolean testing;
    private boolean interactiveServerAvailable = false;
	private String downloadURLAddress;
    private String interactiveURLAddress;
    private ScrollView niuBSv;
    App mApp;
	          
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupFullscreen();
		mApp = (App) getApplication();
		setContentView(R.layout.activity_system_net_test);
		getSettedURLAddress();
		initViews();
	}

	@Override
	public void onBackPressed() {
		if(	testing ){
			showTips(R.string.tipsServerTesting);
			return;			
		}
		super.onBackPressed();
	}
	
	//取得已经设置好的URLAddress
	private void getSettedURLAddress(){
		downloadURLAddress = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.downloadURLAddressKey);
		interactiveURLAddress = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.interactiveURLAddressKey);
	}
		
	private String getDownloadURLAddress(){
		return downloadURLAddress;
	}

	private String getInteractiveURLAddress(){
		return interactiveURLAddress;
	}
	
	private void initViews(){
		downloadURLAddressTitleTv = (TextView) findViewById(R.id.downloadURLAddressTitle);
		downloadURLAddressTv = (TextView) findViewById(R.id.downloadURLAddress);
		interactiveURLAddressTitleTv = (TextView) findViewById(R.id.interactiveURLAddressTitle);
		interactiveURLAddressTv = (TextView) findViewById(R.id.interactiveURLAddress);
		niuBEffectText = (TextView) findViewById(R.id.niuBEffectText);
		niuBSv = (ScrollView) findViewById(R.id.niuBSv);
		
		downloadURLAddressTv.setText( getDownloadURLAddress() );
		interactiveURLAddressTv.setText( getInteractiveURLAddress() );
		
		startButton = (Button) findViewById(R.id.startButton);
		
		//服务器地址未设置
		if( TextUtils.isEmpty( getDownloadURLAddress() )){
			downloadURLAddressTv.setTextColor(android.graphics.Color.RED);
			downloadURLAddressTv.setText(getString(R.string.downloadServerURLNotSet));
		}
		
		if(TextUtils.isEmpty( getInteractiveURLAddress() )){
			interactiveURLAddressTv.setTextColor(android.graphics.Color.RED);
			interactiveURLAddressTv.setText(getString(R.string.interactiveServerURLNotSet));;
		}
		
		if( TextUtils.isEmpty( getDownloadURLAddress() ) && TextUtils.isEmpty( getInteractiveURLAddress() )){
			showTips(R.string.tipsServerURLNotSet);

			startButton.setText(R.string.back);;
			startButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SystemNetTestActivity.this.finish();
				}
			});
			return;
		}
		
		startButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!testing){
					new Thread(netRunnable).start();
				}else{
					showTips(R.string.tipsServerTesting);
				}
			}
		});
	}
	
	//检测网络状态 Runnable
	private Runnable netRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			testing = true;
			checkServer();
			testing = false;
		}
	
	};
	
	//检测服务器地址
	private void checkServer(){
				
		if( !networkAvailable() ){
			netHandler.sendEmptyMessage(networkUnAvailableMsg);
			return;
		}
		
		showNiuBEffect();
	}
	
	//更新UI Hanlder
	private Handler netHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == startTestDownloadServerMsg){
				Toast.makeText(SystemNetTestActivity.this, R.string.tipsStartTestDownloadServer, Toast.LENGTH_SHORT).show();
			}
			if(msg.what == startTestInteractiveServerMsg){
				Toast.makeText(SystemNetTestActivity.this, R.string.tipsStartTestInteractiveServer, Toast.LENGTH_SHORT).show();
			}
			if(msg.what == downloadURLUnAvailableMsg){
				showTips(R.string.tipsDownloadURLUnAvailable);
			}
			if(msg.what == downloadURLAvailableMsg){
				showTips(R.string.tipsDownloadURLAvailable);
			}
			if(msg.what == interactiveURLAvailableMsg){
				showTips(R.string.tipsInteractiveURLAvailable);
			}
			if(msg.what == interactiveURLUnAvailableMsg){
				showTips(R.string.tipsInteractiveURLUnAvailable);
			}
			if(msg.what == networkUnAvailableMsg){
				showTips(R.string.tipsNetworkUnReachable$_$);
			}
		}
	};
	
	//处理输入URL
	private String processURL(String url){
		String processedUrl = "";
		if(!url.startsWith("http://")){
			processedUrl = "http://" + url;
		}else{
			processedUrl = url;
		}
		return processedUrl;
	}
	
	
	//检测URL有效:方法1
	private boolean checkURL(String url){
		HttpGet getMethod = new HttpGet( url ); 
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 2*1000);
		HttpConnectionParams.setSoTimeout(httpParams, 2*1000);
		HttpClient httpClient = new DefaultHttpClient(httpParams);  
		boolean result = false;
		try {
		    HttpResponse response = httpClient.execute(getMethod);   
		    int code = response.getStatusLine().getStatusCode();
		    Log.d(TAG,"code:" + code);
		    if(code == 200){
		    	result = true;
		    }
		} catch (Exception e) {
		    e.printStackTrace();  
		} 
		return result;
	}

	//检测URL有效:方法2
	private boolean checkURL2(String url){
		try {
			HttpURLConnection conn=(HttpURLConnection)new URL( url ).openConnection();
			int code=conn.getResponseCode();
			if(code!=200){
				return false;
			}else{
				return true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//检测URL有效:方法3
	private boolean checkURL3(String url){ 
        try {  
            URL urll = new URL(url);  
            InputStream in = urll.openStream();  
            return true;
        }catch (Exception e1) {    
            return false;
        } 	
	}
	
	//测试交互服务器
	private void checkURL4(){
		interactiveServerAvailable = false;
		Map<String,String> params = new HashMap<String, String>();
		sendRequest(params,new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("onResponse", response);
				stopProgressDialog();
				interactiveServerAvailable = true;        			
			}
		});
	}
	
	//检测网络状态
	private boolean networkAvailable(){
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())  return true;
		return false;	
	}
	
    private void showTips(int whichTips){
    	LayoutInflater inflater = getLayoutInflater();

    	View tipsLayout = inflater.inflate(R.layout.inventory_refresh_tips, 
    			(ViewGroup)this.findViewById(R.id.inventoryRefreshTipsLayout));
    	TextView tipsText = (TextView) tipsLayout.findViewById(R.id.inventoryRefreshingTipsText);
    	tipsText.setText(whichTips);
    	
    	new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.tipsDataDownload))
    		.setView(tipsLayout)
    		.setPositiveButton(getString(R.string.confirm),null)
    		.show();   	
    }
     
	private void showNiuBEffect(){		
		if( initTestDownloadServer() ){
			netHandler.sendEmptyMessage(startTestDownloadServerMsg);
			niuBDownloadTest();
		}
		if( initTestInteractiveServer() ){
			netHandler.sendEmptyMessage(startTestInteractiveServerMsg);
			niuBInteractiveTest();
		}
		showResult();
	}

	private boolean initTestDownloadServer(){
		if( !TextUtils.isEmpty( getDownloadURLAddress() ) ){ 
			if( checkURL2( getDownloadURLAddress() ) ){
				niuBColorHandler.sendEmptyMessage('G');
			}else{
				niuBColorHandler.sendEmptyMessage('R');
			}
			return true;
		}
		return false;
	}
	
	private boolean initTestInteractiveServer(){
		if( !TextUtils.isEmpty( getInteractiveURLAddress() ) ){						
			return true;
		}
		return false;		
	}
	
	private void showResult(){
		if( !networkAvailable() ){
			netHandler.sendEmptyMessage(networkUnAvailableMsg);
			return;
		}
		if( !TextUtils.isEmpty( getDownloadURLAddress() ) ){
			if( checkURL2( getDownloadURLAddress() ) ){
				netHandler.sendEmptyMessage(downloadURLAvailableMsg);
			}else{
				netHandler.sendEmptyMessage(downloadURLUnAvailableMsg);
			}
		}
		if( !TextUtils.isEmpty( getInteractiveURLAddress() ) ){
			if( this.interactiveServerAvailable ){
				netHandler.sendEmptyMessage(interactiveURLAvailableMsg);
			}else{
				netHandler.sendEmptyMessage(interactiveURLUnAvailableMsg);
			}
		}
	}
	
	private void niuBDownloadTest(){
		svBgColorHandler.sendEmptyMessage('B');
		for(int i = 0;i < 80;i ++){
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			niuBHandler.sendEmptyMessage('X');
		}
		svBgColorHandler.sendEmptyMessage('W');		
	}

	private void niuBInteractiveTest(){
		svBgColorHandler.sendEmptyMessage('B');
		this.checkURL4();
		int line = 0;
		for(;;){
			if( this.interactiveServerAvailable ){
				niuBColorHandler.sendEmptyMessage('G');
			}else{
				niuBColorHandler.sendEmptyMessage('R');
			}				
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			niuBHandler.sendEmptyMessage('X');
			line ++;
			if( ( this.interactiveServerAvailable && line > 100 )|| line == 300){
				if(line == 300){
					stopProgressDialog();
				}
				break;
			}			
		}
		svBgColorHandler.sendEmptyMessage('W');		
	}
	
	public String getRandomString(int length) {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			if ("char".equalsIgnoreCase(charOrNum)) {
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}

	private Handler niuBColorHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 'G'){
				niuBEffectText.setTextColor(android.graphics.Color.GREEN);				
			}else if(msg.what == 'R'){
				niuBEffectText.setTextColor(android.graphics.Color.RED);
			}
		}
	};
	
	private Handler svBgColorHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 'B'){
				niuBSv.setBackgroundColor(android.graphics.Color.BLACK);				
			}else if(msg.what == 'W'){
				niuBEffectText.setText("");;
				niuBSv.setBackgroundColor(android.graphics.Color.WHITE);
			}
		}
	};

	private void scroll(){
        int offset = niuBEffectText.getMeasuredHeight() - niuBSv.getMeasuredHeight();
        if (offset < 0) {
                offset = 0;
        }
        niuBSv.scrollTo(0, offset);
	}
		
                                                                          ;;;;private Handler niuBHandler = new Handler()
                                      {;;;;                               ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
              ;;;;                    ;;;;;                               ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
              ;;;;   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;              ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
              ;;;;   public void handleMessage(Message msg){;             ;;;;;;;;;;;;;;;;;          ;;;;;;;;;;;;;;;;;;;;;;;;;
            ;;;;                      ;;;;;                               ;;;;;;;;;;;;;;;;           ;;;;;;;;;;;;;;;;;;;;;;;;;;
                    ;;;;;;super.handleMessage(msg);;;;;;;;;;;;;           ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
       niuBEffectText.setText(niuBEffectText.getText() + "\n" +           getRandomString(new Random().nextInt(100)));;;;;;;;
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;           ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                      ;;;;;                               ;;;;;;;;;;;;;;;;;;;;;
                                      ;;;;;                               ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                      ;;;;;                               ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                      ;;;;;                               ;;;;;;;;;;;;;;;;           ;;;;;;;;;;;;;;;;;;;;;;;;
                                      ;;;;;                               ;;;;;;;;;;;;;;;;;          ;;;;;;;;;;;;;;;;;;;;;;;;;
                                      ;;;;;                               ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                      ;;;;;                               ;;;;;;;;;;;;;;;;;;;scroll();;;;;;;;;;;;;;;;;;;;}};
                                      
			
		
		                        	
}
