package cn.burgeon.core.ui.sales;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.Employee;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.bean.PayWay;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.UndoBarController;
import cn.burgeon.core.widget.UndoBarController.UndoListener;
import cn.burgeon.core.widget.UndoBarStyle;

public class SalesSettleActivity extends BaseActivity {

	private final String TAG = "SalesSettleActivity";
	ListView mListView;
	Button settleBtn;
	TextView payTV, counTV;
	EditText orginET, disCounET, realityET,salesSettleDescET;
	ArrayList<Product> products;
	String command, vipCardno,employeeName,employeeID;
	LinearLayout mPaywayLayout;
	Employee emp;
	
	public EditText getOrginET() {
		return orginET;
	}

	public void setOrginET(EditText orginET) {
		this.orginET = orginET;
	}

	public EditText getDisCounET() {
		return disCounET;
	}

	public void setDisCounET(EditText disCounET) {
		this.disCounET = disCounET;
	}

	public EditText getRealityET() {
		return realityET;
	}

	public void setRealityET(EditText realityET) {
		this.realityET = realityET;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_sales_settle);

		init();
		
		settle();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	float pay = 0;
	int count = 0;
	float discount = 0;
	String regex = "^[\\d]\\.[\\d]{1,2}$";
	boolean hasBack;

	private void settle() {
		IntentData iData = (IntentData) getIntent().getParcelableExtra(PAR_KEY);
		products = iData.getProducts();
		command = iData.getCommand();
		vipCardno = iData.getVipCardno();
		emp = iData.getEmployee();
		employeeName = emp.getName();
		employeeID = emp.getId();
		Log.d("zhang.h", "emp=" + emp.getId());
		for(Product pro : products){
			pay += Float.parseFloat(pro.getMoney()) * Integer.parseInt(pro.getCount());
			count += Integer.parseInt(pro.getCount());
			if(pro.getSalesType() == 2)hasBack = true;
		}
		payTV.setText(String.format(getResources().getString(R.string.sales_settle_pay),String.format("%.2f",pay)));
		counTV.setText(String.format(getResources().getString(R.string.sales_settle_count),count));
		
		disCounET.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					if(disCounET.getText().length() > 0){
						
						if(Pattern.compile(regex).matcher(disCounET.getText().toString()).find()){
							discount = Float.parseFloat(disCounET.getText().toString());
							float other = 0.0f;
							float total = pay * discount;
							realityET.setText(String.format("%.2f", pay * discount));
							EditText cashET = null;
							for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
								LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
								EditText value = (EditText) item.getChildAt(2);
								TextView name = (TextView) item.getChildAt(0);
								if(!"现金".equals(name.getText().toString())){
									if(value.getText().length() > 0)
										other += Float.parseFloat(value.getText().toString());
								}else{
									cashET = value;
								}
							}
							cashET.setText(String.format("%.2f", total-other));
						}else{
							//提示折扣不正确
							disCounET.setError("折扣错误");
						}
					}
				}
			}
		});
		realityET.setText(String.format("%.2f", pay));
		ArrayList<PayWay> pays = new ArrayList<PayWay>();
		if("unknow".equals(command) || null == command){
			Cursor c = db.rawQuery("select * from tc_payway where _id != ?", new String[]{"26"});
			while(c.moveToNext()){
				if(27 == c.getInt(0))
					pays.add(new PayWay(c.getInt(0), c.getString(1), String.format("%.2f",pay)));
				else
					pays.add(new PayWay(c.getInt(0), c.getString(1), ""/*String.format("%.2f",0.00f)*/));
			}
		}else{
			Cursor c = db.rawQuery("select * from c_payway_detail where settleUUID = ?", new String[]{command});
			while(c.moveToNext()){
				pays.add(new PayWay(c.getInt(c.getColumnIndex("paywayID")), c.getString(c.getColumnIndex("name")), String.format("%.2f",Float.parseFloat(c.getString(c.getColumnIndex("money"))))));
			}
			if(c != null && !c.isClosed())
				c.close();
		}
		for(PayWay payway : pays){
			LinearLayout item = (LinearLayout)LayoutInflater.from(SalesSettleActivity.this).inflate(R.layout.sales_settle_list_item, null);
			item.setTag(payway);
			((TextView)item.getChildAt(0)).setText(payway.getPayWay());
			EditText editText = (EditText)item.getChildAt(2);
			editText.setTag(payway.getId());
			editText.setOnTouchListener(touchListener);
			editText.setText(payway.getPayMoney());
			mPaywayLayout.addView(item);
		}
	}
	
	View.OnTouchListener touchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				Log.d(TAG, "on onTouch view tag = " + view.getTag());
				float total = 0.0f;
				float other = 0.0f;
				for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
					LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
					EditText editText = (EditText) item.getChildAt(2);
					if(view.getTag() != editText.getTag()){
						if(editText.getText().length() > 0)
							other += Float.parseFloat(editText.getText().toString());
					}
				}
				if(realityET.getText().length() > 0)
					total = Float.parseFloat(realityET.getText().toString());
				String currPayStr = "0.00".equals(String.format("%.2f", total - other))?"":String.format("%.2f", total - other);
				((EditText)view).setText(currPayStr);
			}
			return false;
		}
	};

	private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());
        
        mPaywayLayout = (LinearLayout) findViewById(R.id.wrapPayway);
		settleBtn = (Button) findViewById(R.id.settleJiezhangBtn);
		settleBtn.setOnClickListener(onClickListener);
		payTV = (TextView) findViewById(R.id.salesSettlePay);
		counTV = (TextView) findViewById(R.id.salesSettleCount);
		orginET = (EditText) findViewById(R.id.salesSettleOrginET);
		disCounET = (EditText) findViewById(R.id.salesSettleDiscountET);
		realityET = (EditText) findViewById(R.id.salesSettleRealityET);
		salesSettleDescET = (EditText) findViewById(R.id.salesSettleDescET);
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!validate()){
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		        UndoBarController.show(SalesSettleActivity.this, "付款金额不正确", null, MESSAGESTYLE);
		        return;
			}
/*			if(isSettled()){
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		        UndoBarController.show(SalesSettleActivity.this, "已结账，不能重复结账", null, MESSAGESTYLE);
		        return;
			}*/
			if("unknow".equals(command) || null == command){
				showTips(1);
			}
			else{
				showTips(2);
			}
		}
	};
	
    //显示对话框
    private void showTips(final int what){
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.systemtips))
    		.setMessage(R.string.sales_settle_dialogmsg)
    		.setPositiveButton(getString(R.string.confirm),new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					settleBtn.setClickable(false);
					discoutDetail();
					if(what == 1){
						save();
					}
					//else if(what == 2)update();
				}})
			.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}});
    	dialog = builder.create();
    	dialog.setCanceledOnTouchOutside(false);
    	dialog.show();
    }
    
    private boolean isSettled(){
    	if(uuid != null){
    		Cursor c = db.rawQuery("select * from c_settle where settleUUID = ?", new String[]{uuid});
    		if(c != null){
	    		int result = c.getCount();
	    		if(!c.isClosed()) c.close();
	    		return result > 0;
    		}else{return false;}
    	}else{
    		return false;
    	}
    }
    
    //整单折扣更新明细价格
    private void discoutDetail(){
    	if(Pattern.compile(regex).matcher(disCounET.getText().toString()).find()){
    		for(Product pro : products){
    			//退货和赠送不打折
    			if(pro.getSalesType() == 1 || pro.getSalesType() == 4){
    				pro.setMoney(String.format("%.2f", Float.parseFloat(pro.getMoney()) * Float.parseFloat(disCounET.getText().toString())));
    				pro.setDiscount(String.format("%.2f", Float.parseFloat(pro.getDiscount()) * Float.parseFloat(disCounET.getText().toString())));
    			}
    		}
    	}
    }
    
    private boolean validate(){
    	float money = 0.0f;
    	float temp = 0.0f;
    	for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
    		LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
    		EditText editText = (EditText) item.getChildAt(2);
    		if(editText.getText().length() > 0){
    			temp = Float.parseFloat(editText.getText().toString());
    			if(!hasBack){//无退货不能有负数
	    			if(temp < 0){
	    				editText.setError("不正确");
	    				return false;
	    			}else{
	    				money += temp;
	    			}
    			}else{
    				money += temp;
    			}
    		}
    	}
    	Log.d(TAG, "====validate money" + money);
    	Log.d(TAG, "====validate realityET" + realityET.getText().toString());
    	return money == Float.parseFloat(realityET.getText().toString());
    }
	
    String uuid = null;
	public void save(){
		db.beginTransaction();
        try {
        	uuid = UUID.randomUUID().toString();
        	Date currentTime = new Date();
        	db.execSQL("insert into c_settle('settleTime','type','count','money','employeeID','orderEmployee',"
        			+ "'status','settleDate','settleMonth','vipCardno','orderno','description','settleUUID')"+
        				" values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime),
								getResources().getString(R.string.sales_settle_type),
								count,
								realityET.getText(),
								employeeID,
								employeeName,
								getString(R.string.sales_settle_noup),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime).substring(0, 7),
								vipCardno,
								getNo(),//销售单号
								salesSettleDescET.getText().length() == 0?"":salesSettleDescET.getText().toString(),
								uuid});
        	for(Product pro : products){
        		db.execSQL("insert into c_settle_detail('style','barcode','price','discount','orgdocno',"
        				+ "'count','money','settleUUID','pdtname','color','size','settleDate','salesType','employee')"
        				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
    					new Object[]{pro.getStyle(),pro.getBarCode(),pro.getPrice(), pro.getDiscount(),pro.getOrgorderNO(),
        						pro.getCount(), pro.getMoney(), uuid, pro.getName(),
        						pro.getColor(),pro.getSize(),
        						new SimpleDateFormat("yyyy-MM-dd").format(currentTime),
        						pro.getSalesType(),pro.getEmployee()});
        	}
        	for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
        		LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
        		PayWay payway = (PayWay) item.getTag();
        		EditText editText = (EditText) item.getChildAt(2);
        		if(editText.getText().length() > 0){
        			if(editText.getText().toString().indexOf(".") > 0){
        				float moneyp = Float.parseFloat(editText.getText().toString());
        				//if(moneyp > 0){
        					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
        							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString(),uuid});
        				//}
        			}else{
        				int moneyt = Integer.parseInt(editText.getText().toString());
        				//if(moneyt > 0){
        					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
        							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString()+".00",uuid});
        				//}
        			}
        		}
        	}
            db.setTransactionSuccessful();
        } catch(Exception e){}
        finally {  
            db.endTransaction();
        } 
        if(!networkReachable()){
        	popToast();
        }else{
        	upload(uuid);
        }
	}

	private void popToast() {
		UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		UndoBarController.show(SalesSettleActivity.this, "结账成功", new UndoListener() {
			
			@Override
			public void onUndo(Parcelable token) {
				setResult(RESULT_OK);
				finish();
			}
		}, MESSAGESTYLE);
	}
	
	private void upload(String uuid){
		Cursor c = db.rawQuery("select * from c_settle where settleUUID = ?", new String[]{uuid});
		if(c.getCount() > 0){
			if(c.moveToFirst()){
				Order order = new Order();
				order.setUuid(c.getString(c.getColumnIndex("settleUUID")));
				order.setOrderNo(c.getString(c.getColumnIndex("orderno")));
				order.setOrderDate(c.getString(c.getColumnIndex("settleDate")).replace("-", ""));
				order.setOrderType(c.getString(c.getColumnIndex("type")));
				order.setOrderMoney(c.getString(c.getColumnIndex("money")));
				order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
				order.setVipCardno(c.getString(c.getColumnIndex("vipCardno")));
				order.setDesc(c.getString(c.getColumnIndex("description")));
				new Thread(new RequestRunable(order)).start();
			}
		}
		if(c != null && !c.isClosed())
			c.close();
	}
	
	public void update(){
		db.beginTransaction();
		Cursor c = null;
        try {
        	Date currentTime = new Date();
        	db.execSQL("update c_settle set 'settleTime' = ?,'type' = ?,'count' = ?,"
        			+ "'money' = ?,"
        			+ "'status' = ?,'settleDate' = ?,'settleMonth' = ? "
        			+ " where settleUUID = ?",
					new Object[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime),
								getResources().getString(R.string.sales_settle_type),
								count,
								realityET.getText(),
								getResources().getString(R.string.sales_settle_noup),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime).substring(0, 7),
								command});
        	
        	for(Product pro : products){
        		if(pro.getUuid() != null){
	        		db.execSQL("update c_settle_detail "
	        				+ "set 'price' = ?,'discount' = ?,'settleDate' = ?,"
	        				+ "'count' = ?,'money' = ?,'salesType' = ? where settleUUID = ? and _id = ?",
	    					new Object[]{pro.getPrice(), pro.getDiscount(), 
	        						new SimpleDateFormat("yyyy-MM-dd").format(currentTime),
	        						pro.getCount(), pro.getMoney(), pro.getSalesType(),command, pro.getId()});
        		}else{
            		db.execSQL("insert into c_settle_detail('style','barcode','price','discount','orgdocno',"
            				+ "'count','money','settleUUID','pdtname','color','size','settleDate','salesType')"
            				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
        					new Object[]{pro.getStyle(),pro.getBarCode(),pro.getPrice(), pro.getDiscount(),orginET.getText().length()==0?"":orginET.getText().toString(),
            						pro.getCount(), pro.getMoney(), command, pro.getName(),
            						pro.getColor(),pro.getSize(),
            						new SimpleDateFormat("yyyy-MM-dd").format(currentTime),pro.getSalesType()});
        		}
        	}
        	c = db.rawQuery("select * from c_payway_detail where settleUUID = ?", new String[]{command});
        	for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
        		LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
        		PayWay payway = (PayWay) item.getTag();
        		EditText editText = (EditText) item.getChildAt(2);
        		if(editText.getText().length() > 0){
        			while(c.moveToNext()){
        				if(payway.getId() == c.getInt(c.getColumnIndex("paywayID"))){
        					db.execSQL("update c_payway_detail set money = ? "
                					+ "where _id = ? and settleUUID = ?", 
                					new Object[]{editText.getText().toString(),c.getInt(c.getColumnIndex("_id")),command});
        				}else{
        					if(editText.getText().toString().indexOf(".") > 0){
                				float moneyp = Float.parseFloat(editText.getText().toString());
                				if(moneyp > 0){
                					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
                							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString(),command});
                				}
                			}else{
                				int moneyt = Integer.parseInt(editText.getText().toString());
                				if(moneyt > 0){
                					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
                							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString(),command});
                				}
                			}
        				}
        			}
        			
        		}
        	}
            db.setTransactionSuccessful();
        } catch(Exception e){}
        finally {  
            db.endTransaction();
            if(c != null && !c.isClosed())
            	c.close();
        } 
	}
	
    private String getNo() {
        StringBuffer sb = new StringBuffer();
        sb.append("SA305152");
        sb.append(new SimpleDateFormat("yyMMdd").format(new Date()));

        // 保存checkNo至SP
        int checkNo = App.getPreferenceUtils().getPreferenceInt("salesNo");
        int i = 0;
        if (checkNo > 0) {
            i = checkNo + 1;
        } else {
            i = 1;
        }
        App.getPreferenceUtils().savePreferenceInt("salesNo", i);

        StringBuffer finalCheckNo = new StringBuffer();
        for (int j = 0; j < 5 - String.valueOf(i).length(); j++) {
            finalCheckNo.append(0);
        }
        finalCheckNo.append(i);
        sb.append(finalCheckNo.toString());
        return sb.toString();
    }
    
    class RequestRunable implements Runnable {
		
		private Order order;
		
		public RequestRunable(Order order) {
			this.order = order;
		}

		@Override
		public void run() {
			ArrayList<String> failures = new ArrayList<String>();
			App mApp = ((App)getApplication());
			String tt = getSDF().format(new Date());
	        String uriAPI = App.getHosturl();
	        HttpPost httpRequest = new HttpPost(uriAPI);
	        httpRequest.addHeader("Content-Type", getBodyContentType());
	        Map<String,String> map = construct(order);
	        map.put("sip_appkey", getSipkey());
	        map.put("sip_timestamp", tt);
	        map.put("sip_sign", MD5(getSipkey() + tt + getSIPPSWDMD5()));
	        
	        try{
	          HttpEntity entity = new ByteArrayEntity(encodeParameters(map, "UTF-8"));
	          httpRequest.setEntity(entity);
	          HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
	          if(httpResponse.getStatusLine().getStatusCode() == 200){
	            String response = EntityUtils.toString(httpResponse.getEntity());
	            Log.d(TAG, "====" + response);
	            if(!TextUtils.isEmpty(response)){
					RequestResult result = parseResult(response);
					//请求成功，更新记录状态
					if("0".equals(result.getCode())){
						updateOrderStatus(result,order);
					}else{
						//delete(order.getUuid());
				}}
	          }else{
	        	  UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
				  UndoBarController.show(SalesSettleActivity.this, "网络异常，上传失败", null, MESSAGESTYLE);
	          }
            } catch(Exception e) {
               e.printStackTrace();
            }	
	        Message msg = handler.obtainMessage();
			msg.what = 1;
			handler.sendMessage(msg);
		}

		private Map<String,String> construct(Order order) {
			Map<String,String> params = new HashMap<String, String>();
			JSONArray array = null;
			JSONObject transactions = null;
			
			try {
				array = new JSONArray();
				transactions = new JSONObject();
				transactions.put("id", 112);
				transactions.put("command", "ProcessOrder");
				
				//第一个params
				JSONObject paramsInTransactions = new JSONObject();
				paramsInTransactions.put("submit", true);
				
				//masterobj
				JSONObject masterObj = new JSONObject();
				masterObj.put("id", -1);
				masterObj.put("REFNO", order.getOrderNo());
				masterObj.put("SALESREP_ID__NAME", order.getSaleAsistant());
				masterObj.put("DOCTYPE", order.getOrderType());
				masterObj.put("C_STORE_ID__NAME", App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
				masterObj.put("table", 12964);
				masterObj.put("BILLDATE", order.getOrderDate());
				masterObj.put("C_RETAILTYPE_ID__NAME", order.getOrderType());
				masterObj.put("C_VIP_ID__CARDNO", order.getVipCardno());
				masterObj.put("DESCRIPTION", order.getDesc());
				paramsInTransactions.put("masterobj", masterObj);  
				
				//detailobjs
				JSONObject detailObjs = new JSONObject();
				//reftables
				JSONArray refobjs = new JSONArray();
				JSONObject refobj = new JSONObject();
				refobj.put("table", 13019);
				JSONArray addList = new JSONArray();
				
				//获取明细表数据集
				List<Product> detailsItems = getDetailsData(order.getUuid());
				if(detailsItems != null && detailsItems.size() > 0){
					for(Product product : detailsItems){
						JSONObject item = new JSONObject();
						item.put("QTY", product.getCount());
						item.put("TYPE", product.getSalesType());
						item.put("SALESREP_ID__NAME", product.getEmployee());
						if(product.getSalesType() == 2)
							item.put("ORGDOCNO", product.getOrgorderNO());
						item.put("M_PRODUCT_ID__NAME", product.getBarCode());
						if(product.getSalesType() == 2)
							item.put("PRICEACTUAL", "-"+product.getMoney());
						item.put("TOT_AMT_ACTUAL", String.format("%.2f", (Float.parseFloat(product.getMoney()) * Integer.parseInt(product.getCount()))));
						addList.put(item);
					}
				}
				refobj.put("addList",addList);
				refobjs.put(refobj);
				
				//refobjs2-支付方式
				JSONObject refobj2 = new JSONObject();
				refobj2.put("table", 14434);
				JSONArray addList2 = new JSONArray();
				
				//获取明细表数据集
				List<PayWay> paydetailsItems = getPayWayDetailsData(order.getUuid());
				if(detailsItems != null && detailsItems.size() > 0){
					for(PayWay payway : paydetailsItems){
						//if(Float.parseFloat(payway.getPayMoney()) > 0){
							JSONObject payitem = new JSONObject();
							payitem.put("PAYAMOUNT", payway.getPayMoney());
							payitem.put("C_PAYWAY_ID__NAME", payway.getPayWay());
							addList2.put(payitem);
						//}
					}
				}
				refobj2.put("addList",addList2);
				refobjs.put(refobj2);
				
				detailObjs.put("refobjs", refobjs);
				detailObjs.put("reftables", new JSONArray().put(710).put(774));
				paramsInTransactions.put("detailobjs", detailObjs);
				
				transactions.put("params", paramsInTransactions);
				array.put(transactions);
				Log.d(TAG, array.toString());
				params.put("transactions", array.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return params;
		}
		
	    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
	        StringBuilder encodedParams = new StringBuilder();
	        try {
	            for (Map.Entry<String, String> entry : params.entrySet()) {
	                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
	                encodedParams.append('=');
	                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
	                encodedParams.append('&');
	            }
	            return encodedParams.toString().getBytes(paramsEncoding);
	        } catch (UnsupportedEncodingException uee) {
	            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
	        }
	    }
	    
	    private String getBodyContentType() {
	        return "application/x-www-form-urlencoded; charset=UTF-8";
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
		
		private List<Product> getDetailsData(String primaryKey) {
			List<Product> details = new ArrayList<Product>();
			Cursor c = db.rawQuery("select employee,money,barcode, count,salesType,orgdocno from c_settle_detail where settleUUID = ?", new String[]{primaryKey});
			Product product = null;
			while(c.moveToNext()){
				product = new Product();
				product.setBarCode(c.getString(c.getColumnIndex("barcode")));
				product.setCount(c.getString(c.getColumnIndex("count")));
				product.setMoney(c.getString(c.getColumnIndex("money")));
				product.setSalesType(c.getInt(c.getColumnIndex("salesType")));
				product.setOrgorderNO(c.getString(c.getColumnIndex("orgdocno")));
				product.setEmployee(c.getString(c.getColumnIndex("employee")));
				details.add(product);
			}
			if(c != null && !c.isClosed())
				c.close();
			return details;
	    }
		
		private List<PayWay> getPayWayDetailsData(String primaryKey) {
			List<PayWay> details = new ArrayList<PayWay>();
			Cursor c = db.rawQuery("select name, money from c_payway_detail where settleUUID = ?", new String[]{primaryKey});
			PayWay payway = null;
			while(c.moveToNext()){
				payway = new PayWay();
				payway.setPayWay(c.getString(c.getColumnIndex("name")));
				payway.setPayMoney(c.getString(c.getColumnIndex("money")));
				details.add(payway);
			}
			if(c != null && !c.isClosed())
				c.close();
			return details;
	    }
		
		private void updateOrderStatus(RequestResult result, Order order) {
			db.beginTransaction();
	        try {
	        	db.execSQL("update c_settle set status = ? where settleUUID = ?",
						new Object[]{
									getResources().getString(R.string.sales_settle_hasup),
									order.getUuid()});
	            db.setTransactionSuccessful();
	        } catch(Exception e){}
	        finally {  
	            db.endTransaction();
	        } 
		}
		
		private void delete(String uuid) {
			if(uuid != null){
				db.execSQL("delete from c_settle where settleUUID = ?", new Object[]{uuid});
				db.execSQL("delete from c_settle_detail where settleUUID = ?", new Object[]{uuid});
			}
		}
		
	}
    
    Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			popToast();
		}
    };

}
