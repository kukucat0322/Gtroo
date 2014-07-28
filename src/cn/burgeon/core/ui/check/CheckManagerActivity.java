package cn.burgeon.core.ui.check;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.AsyncTask;
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
import cn.burgeon.core.adapter.CheckManagerAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.UndoBarController;
import cn.burgeon.core.widget.UndoBarStyle;

public class CheckManagerActivity extends BaseActivity {

    private GridView checkGV;
    private CheckManagerAdapter mAdapter;
    private String lastOrderNo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_check_manager);

        init();
    }

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        checkGV = (GridView) findViewById(R.id.checkGV);
        mAdapter = new CheckManagerAdapter(this);
        checkGV.setAdapter(mAdapter);
        checkGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.checkManagerTextValues[0].equals(itemValue)) {
                    forwardActivity(CheckScanActivity.class);
                } else if (itemValue != null && Constant.checkManagerTextValues[1].equals(itemValue)) {
                    forwardActivity(CheckDocManagerActivity.class);
                } else if (itemValue != null && Constant.checkManagerTextValues[2].equals(itemValue)) {
                    forwardActivity(CheckQueryActivity.class);
                } else if (itemValue != null && Constant.checkManagerTextValues[3].equals(itemValue)) {
                	ArrayList<Order> orders = fetchData();
                	int orderSize = orders.size();
					if (orders != null && orderSize > 0) {
						lastOrderNo = orders.get(orderSize - 1).getOrderNo();
						for (Order order : orders) {
							uploadSalesOrder(order);
						}
					} else {
						UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
						UndoBarController.show(CheckManagerActivity.this, "没有数据可以上传", null, MESSAGESTYLE);
					}
                }
            }
        });
    }

    // 上传已审核的
    private ArrayList<Order> fetchData() {
        Order order = null;
        ArrayList<Order> data = new ArrayList<Order>();
        Cursor c = db.rawQuery("select * from c_check where isChecked = ? and status = '已完成'", new String[]{getString(R.string.sales_settle_noup)});
        while (c.moveToNext()) {
            order = new Order();
            order.setOrderNo(c.getString(c.getColumnIndex("checkno")));
            order.setOrderDate(c.getString(c.getColumnIndex("checkTime")).replace("-", ""));
            order.setOrderType(c.getString(c.getColumnIndex("type")));
            order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
            order.setOrderCount(c.getString(c.getColumnIndex("count")));
            order.setUuid(c.getString(c.getColumnIndex("checkUUID")));
            data.add(order);
        }
        if (c != null && !c.isClosed())
            c.close();
        return data;
    }

    private void uploadSalesOrder(Order order) {
        if (!"未知类型".equals(order.getOrderType())) {
            Map<String, String> params = new HashMap<String, String>();
            JSONArray array;
            JSONObject transactions;

            try {
                array = new JSONArray();
                transactions = new JSONObject();
                transactions.put("id", 112);
                transactions.put("command", "ProcessOrder");

                //第一个params
                JSONObject paramsInTransactions = new JSONObject();
                paramsInTransactions.put("submit", "true");

                //masterobj
                JSONObject masterObj = new JSONObject();
                masterObj.put("table", 12254);
                masterObj.put("BILLDATE", order.getOrderDate());
                masterObj.put("DOCTYPE", ("全盘".equals(order.getOrderType()) ? "INF" : "INR"));
                masterObj.put("C_STORE_ID__NAME", App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
                masterObj.put("DIFFREASON", "缺货");
                paramsInTransactions.put("masterobj", masterObj);

                //detailobjs
                JSONObject detailObjs = new JSONObject();
                //reftables
               
                //refobjs
                JSONArray refobjs = new JSONArray();
                
                //明细
                JSONObject refobj = new JSONObject();
                refobj.put("table", 12255);
                JSONArray addList = new JSONArray();

                List<Product> detailsItems = getDetailsData(order.getOrderNo());
    			if(detailsItems != null && detailsItems.size() > 0){
    				// Log.d("check", "========明细=========");
    				for(Product product : detailsItems){
    					JSONObject item = new JSONObject();
    					item.put("QTYCOUNT", product.getCount());
    					item.put("M_PRODUCT_ID__NAME", product.getBarCode());
    					addList.put(item);
    				}
    			}
                refobj.put("addList", addList);
                refobjs.put(refobj);
                
                //按货架扫描明细
                JSONObject refobj2 = new JSONObject();
                refobj2.put("table", 15743);
                JSONArray addList2 = new JSONArray();

                List<Product> shelfDetailsItems = getDetailsData(order.getOrderNo());
    			if(shelfDetailsItems != null && shelfDetailsItems.size() > 0){
    				// Log.d("check", "========按货架扫描明细=========");
    				for(Product product : shelfDetailsItems){
    					JSONObject item2 = new JSONObject();
    					item2.put("SHELFNO", product.getShelf());
    					item2.put("QTYCOUNT", product.getCount());
    					item2.put("M_PRODUCT_ID__NAME", product.getBarCode());
    					addList2.put(item2);
    				}
    			}
                refobj2.put("addList", addList2);
                refobjs.put(refobj2);

                detailObjs.put("refobjs", refobjs);
                detailObjs.put("reftables", new JSONArray().put(319)/*.put(1274)*/);
                paramsInTransactions.put("detailobjs", detailObjs);

                transactions.put("params", paramsInTransactions);
                array.put(transactions);
                Log.d("check", array.toString());
                params.put("transactions", array.toString());
                
                String tt = App.getInstance().getSDF().format(new Date());
                //appKey,时间戳,MD5签名
                params.put("sip_appkey", App.getSipkey());
                params.put("sip_timestamp", tt);
                params.put("sip_sign", App.getInstance().MD5(App.getSipkey() + tt + App.getInstance().getSIPPSWDMD5()));

                // 执行请求
                UploadTask uploadTask = new UploadTask(order, params);
                uploadTask.execute(App.getHosturl());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
	
    // 总params
	/*
	{
		sip_sign=06fa087cff61659baef64235b4c0e786, 
		transactions=[{
			"command":"ProcessOrder",
			"id":112,
			"params":{
				"submit":"true",
				"detailobjs":{
					"refobjs":[{
						"addList":[{
							"M_PRODUCT_ID__NAME":"AS001BL",
							"QTYCOUNT":"1"
						}],
						"table":12255
					}, {
						"addList":[{
							"M_PRODUCT_ID__NAME":"AS001BL",
							"QTYCOUNT":"1",
							"SHELFNO":"01"
						}],
						"table":15743
					}],
					"reftables":[319]
				},
				"masterobj":{
					"C_STORE_ID__NAME":"LILY门店001",
					"DIFFREASON":"缺货",
					"table":12254,
					"DOCTYPE":"INR",
					"BILLDATE":"20140717 09:48:34"
				}
			}
		}], 
		sip_timestamp=2014-07-17 09:53:37.587, 
		sip_appkey=nea@burgeon.com.cn
	}
	*/
	// Params 启动任务执行的输入参数，比如HTTP请求的URL。
    // Progress 后台任务执行的百分比。
    // Result 后台执行任务最终返回的结果，比如String。
	// 设置三种类型参数分别为String,Integer,String
	class UploadTask extends AsyncTask<String, Integer, String> {
		
		Order currOrder;
		
		List<NameValuePair> pars = null;
		HttpPost httpRequest = null;
		HttpResponse httpResponse;

		public UploadTask(Order order, Map<String, String> params) {
			this.currOrder = order;
			
			/* Post运作传送变数必须用NameValuePair[]阵列储存 */
			pars = new ArrayList<NameValuePair>();

			for (Map.Entry<String, String> entry : params.entrySet()) {
				pars.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		// 任务启动，可以在这里显示一个对话框，这里简单处理
		@Override
		protected void onPreExecute() {
			CheckManagerActivity.this.startProgressDialog();
		}

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			/* 建立HttpPost连接 */
			httpRequest = new HttpPost(params[0]);
			try {
				// 发出HTTP request
				httpRequest.setEntity(new UrlEncodedFormEntity(pars, HTTP.UTF_8));
				// 取得HTTP response
				httpResponse = new DefaultHttpClient().execute(httpRequest);
				// 若状态码为200
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// 得到返回字串
					String response = EntityUtils.toString(httpResponse.getEntity());
					Log.d("check", response);
					RequestResult result = parseResult(response);
                    //请求成功，更新记录状态和销售单号
					if ("0".equals(result.getCode())) {
						updateOrderStatus(result, currOrder);
					}
				}
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// 判断是否是最后一个, 是则提示上传成功信息
			if (lastOrderNo.equals(currOrder.getOrderNo())) {
				CheckManagerActivity.this.stopProgressDialog();

				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
				UndoBarController.show(CheckManagerActivity.this, "盘点上传成功", null, MESSAGESTYLE);
			}
		}

		// 更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}
	
	private RequestResult parseResult(String response) {
		RequestResult result = null;
		try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			result = new RequestResult(resJO.getString("code"), resJO.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void updateOrderStatus(RequestResult result, Order order) {
		db.beginTransaction();
		try {
			db.execSQL("update c_check set isChecked = ? where checkno = ?",
					new Object[] { getResources().getString(R.string.sales_settle_hasup), order.getOrderNo() });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
    
	private List<Product> getDetailsData(String primaryKey) {
		List<Product> details = new ArrayList<Product>();
		Cursor c = db.rawQuery("select barcode, count,  shelf from c_check_detail where checkno = ?", new String[] { primaryKey });
		Product product = null;
		while (c.moveToNext()) {
			product = new Product();
			product.setBarCode(c.getString(c.getColumnIndex("barcode")));
			product.setCount(c.getString(c.getColumnIndex("count")));
			product.setShelf(c.getString(c.getColumnIndex("shelf")));
			details.add(product);
		}
		if (c != null && !c.isClosed())
			c.close();
		return details;
	}
	
}
