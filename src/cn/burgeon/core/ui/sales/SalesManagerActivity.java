package cn.burgeon.core.ui.sales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
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
                	Log.d("SalesManager", "==================="+itemValue);
                    forwardActivity(DailySalesActivity.class);
                } else if (itemValue != null && Constant.salesTopMenuTextValues[1].equals(itemValue)) {
                    forwardActivity(SalesReportActivity.class);
                } else if (itemValue != null && Constant.salesTopMenuTextValues[2].equals(itemValue)) {
                    List<Order> orders = fetchData();
                    if(orders != null && orders.size() > 0){
                    	for(Order order : orders){
                    		uploadSalesOrder(order);
                    	}
                    }
                    UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 3000);
		        	UndoBarController.show(SalesManagerActivity.this, "销售上传成功", null, MESSAGESTYLE);
                }
            }

        });
    }

	private void uploadSalesOrder(final Order order) {
		Map<String,String> params = new HashMap<String, String>();
		JSONArray array;
		JSONObject transactions;
		
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
					item.put("M_PRODUCT_ID__NAME", product.getBarCode());
					addList.put(item);
				}
			}
			//JSONObject item = new JSONObject();
			//item.put("QTY", 2);
			//item.put("M_PRODUCT_ID__NAME", "108234A091-18");
			//addList.put(item);
			refobj.put("addList",addList);
			refobjs.put(refobj);
			
			//refobjs2-支付方式
			JSONObject refobj2 = new JSONObject();
			refobj2.put("table", 14434);
			JSONArray addList2 = new JSONArray();
			
			//获取明细表数据集
			/*List<PayWay> paydetailsItems = getPayWayDetailsData(order.getUuid());
			if(detailsItems != null && detailsItems.size() > 0){
				for(PayWay payway : paydetailsItems){
					JSONObject payitem = new JSONObject();
					payitem.put("PAYAMOUNT", payway.getPayMoney());
					payitem.put("C_PAYWAY_ID__NAME", payway.getPayWay());
					addList2.put(payitem);
				}
			}*/
			//JSONObject item2 = new JSONObject();
			//item2.put("PAYAMOUNT", 369.60);
			//item2.put("C_PAYWAY_ID__NAME", "现金");
			//addList2.put(item2);
			
			//refobj2.put("addList",addList2);
			//refobjs.put(refobj2);
			
			detailObjs.put("refobjs", refobjs);
			detailObjs.put("reftables", new JSONArray().put(710)/*.put(774)*/);
			paramsInTransactions.put("detailobjs", detailObjs);
			
			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			Log.d(TAG, array.toString());
			params.put("transactions", array.toString());
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d(TAG, response);
					RequestResult result = parseResult(response);
					//请求成功，更新记录状态
					if("0".equals(result.getCode())){
						updateOrderStatus(result,order);
					}
					// 取消进度条
                    stopProgressDialog();
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
			data.add(order);
		}
		if(c != null && !c.isClosed())
			c.close();
		return data;
	}
	
	private List<Product> getDetailsData(String primaryKey) {
		List<Product> details = new ArrayList<Product>();
		Cursor c = db.rawQuery("select barcode, count from c_settle_detail where settleUUID = ?", new String[]{primaryKey});
		Product product = null;
		while(c.moveToNext()){
			product = new Product();
			product.setBarCode(c.getString(c.getColumnIndex("barcode")));
			product.setCount(c.getString(c.getColumnIndex("count")));
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
	
}
