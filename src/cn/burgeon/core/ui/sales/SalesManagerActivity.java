package cn.burgeon.core.ui.sales;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesManagerAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.bean.PayWay;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.UndoBarController;
import cn.burgeon.core.widget.UndoBarStyle;

import com.android.volley.Response;

@SuppressLint("SimpleDateFormat") public class SalesManagerActivity extends BaseActivity {
	
	private final String TAG = "SalesManagerActivity";
    private GridView salesGV;
    private SalesManagerAdapter mAdapter;
    TextView storeTV;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_manager);
		
		init();
		initStoreData(storeTV);
	}
	
    private void init() {
    	storeTV = (TextView) findViewById(R.id.storeTV);
    	salesGV = (GridView) findViewById(R.id.salesGV);
        mAdapter = new SalesManagerAdapter(this);
        salesGV.setAdapter(mAdapter);
        salesGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.salesTopMenuTextValues[0].equals(itemValue)) {
                    forwardActivity(DailySalesActivity.class);
                } else if (itemValue != null && Constant.salesTopMenuTextValues[1].equals(itemValue)) {
                    forwardActivity(SalesReportActivity.class);
                } else if (itemValue != null && Constant.salesTopMenuTextValues[2].equals(itemValue)) {
                	startProgressDialog();
                	List<Order> orders = fetchData();
                    new Thread(new RequestRunable(orders)).start();
                }
            }
        });
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
	private RequestResult parseResult(String response) {
		RequestResult result = null;
	    try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			result = new RequestResult(resJO.getString("code"), resJO.getString("message"));
		} catch (JSONException e) {}
		return result;
	}
	
	private List<Order> fetchData() {
		Order order = null;
		List<Order> data = new ArrayList<Order>();
		Cursor c = db.rawQuery("select * from c_settle where status = ?", new String[]{getString(R.string.sales_settle_noup)});
		Log.d(TAG, "cursor size===========" + c.getCount());
		while(c.moveToNext()){
			order = new Order();
			order.setUuid(c.getString(c.getColumnIndex("settleUUID")));
			order.setOrderNo(c.getString(c.getColumnIndex("orderno")));
			order.setOrderDate(c.getString(c.getColumnIndex("settleDate")).replace("-", ""));
			order.setOrderType(c.getString(c.getColumnIndex("type")));
			order.setOrderMoney(c.getString(c.getColumnIndex("money")));
			order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
			order.setVipCardno(c.getString(c.getColumnIndex("vipCardno")));
			data.add(order);
		}
		if(c != null && !c.isClosed())
			c.close();
		return data;
	}
	
	private List<Product> getDetailsData(String primaryKey) {
		List<Product> details = new ArrayList<Product>();
		Cursor c = db.rawQuery("select money,barcode, count,salesType from c_settle_detail where settleUUID = ?", new String[]{primaryKey});
		Product product = null;
		while(c.moveToNext()){
			product = new Product();
			product.setBarCode(c.getString(c.getColumnIndex("barcode")));
			product.setCount(c.getString(c.getColumnIndex("count")));
			product.setMoney(c.getString(c.getColumnIndex("money")));
			product.setSalesType(c.getInt(c.getColumnIndex("salesType")));
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
	
	class RequestRunable implements Runnable {
		
		private List<Order> orders = new ArrayList<Order>();
		
		public RequestRunable(List<Order> orders) {
			this.orders = orders;
		}

		@Override
		public void run() {
			ArrayList<String> failures = new ArrayList<String>();
			for(Order order : orders){
				App mApp = ((App)getApplication());
				String tt = mApp.getSDF().format(new Date());
		        String uriAPI = App.getHosturl();
		        HttpPost httpRequest = new HttpPost(uriAPI);
		        httpRequest.addHeader("Content-Type", getBodyContentType());
		        Map<String,String> map = construct(order);
		        map.put("sip_appkey", App.getSipkey());
		        map.put("sip_timestamp", tt);
		        map.put("sip_sign", mApp.MD5(App.getSipkey() + tt + mApp.getSIPPSWDMD5()));
		        
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
							failures.add(order.getOrderNo()+":"+result.getMessage());
					}}
		          }else{
		        	  UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
					  UndoBarController.show(SalesManagerActivity.this, "网络异常，上传失败", null, MESSAGESTYLE);
		          }
	            } catch(Exception e) {
	               e.printStackTrace();
	            }	
			}
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.obj = failures;
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
//						if(product.getSalesType() == 2){
//							item.put("ORGDOCNO", order.getOrderNo());
//						}
						item.put("M_PRODUCT_ID__NAME", product.getBarCode());
						item.put("PRICEACTUAL", product.getMoney());
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
	};
	
    Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			stopProgressDialog();
			ArrayList<String> falures = (ArrayList<String>) msg.obj;
			if(falures.size() > 0){
				StringBuilder sb = new StringBuilder();
				for(String str : falures)
					sb.append(str).append("\n");
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
	        	UndoBarController.show(SalesManagerActivity.this, sb.toString(), null, MESSAGESTYLE);
			}else{
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
				UndoBarController.show(SalesManagerActivity.this, "上传成功", null, MESSAGESTYLE);
			}
		}
    };
}
