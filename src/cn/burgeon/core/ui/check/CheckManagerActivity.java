package cn.burgeon.core.ui.check;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.CheckManagerAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class CheckManagerActivity extends BaseActivity {

    private GridView checkGV;
    private CheckManagerAdapter mAdapter;

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
                    List<Order> orders = fetchData();
                    if (orders != null && orders.size() > 0) {
                        for (Order order : orders) {
                            uploadSalesOrder(order);
                        }
                    }
                }
            }
        });
    }

    private List<Order> fetchData() {
        Order order = null;
        List<Order> data = new ArrayList<Order>();
        Cursor c = db.rawQuery("select * from c_check where isChecked = ?", new String[]{getString(R.string.sales_settle_noup)});
        while (c.moveToNext()) {
            order = new Order();
            order.setOrderNo(c.getString(c.getColumnIndex("checkno")));
            order.setOrderDate(c.getString(c.getColumnIndex("checkTime")).replace("-", ""));
            order.setOrderType(c.getString(c.getColumnIndex("type")));
            order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
            order.setOrderCount(c.getString(c.getColumnIndex("count")));
            order.setBarCode(c.getString(c.getColumnIndex("barcode")));
            data.add(order);
        }
        if (c != null && !c.isClosed())
            c.close();
        return data;
    }

    /*
    {
        "masterobj":{
            "table":"M_INVENTORY",
            "BILLDATE":"20120427 00:00:00",
            "DOCTYPE":"INF",
             "C_STORE_ID__NAME":"苏州001"
        },
        "detailobjs":{
            "reftables":[319],
            "refobjs":[{
                "table":"M_INVENTORYITEM",
                "addList":[{
                    "M_PRODUCT_ID__NAME":"108234A091-18",
                    "QTYCOUNT":100,
                }]
            }]
        },
        "submit":"true"
    }
    */
    private void uploadSalesOrder(final Order order) {
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
                detailObjs.put("reftables", new JSONArray().put(319));
                //refobjs
                JSONArray refobjs = new JSONArray();
                JSONObject refobj = new JSONObject();
                refobj.put("table", 12255);
                JSONArray addList = new JSONArray();

                JSONObject item = new JSONObject();
                item.put("QTYCOUNT", Integer.valueOf(order.getOrderCount()));
                item.put("M_PRODUCT_ID__NAME", "108234A091-18");
                addList.put(item);
                refobj.put("addList", addList);
                refobjs.put(refobj);

                detailObjs.put("refobjs", refobjs);
                paramsInTransactions.put("detailobjs", detailObjs);

                transactions.put("params", paramsInTransactions);
                array.put(transactions);
                params.put("transactions", array.toString());
                sendRequest(params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        RequestResult result = parseResult(response);
                        //请求成功，更新记录状态和销售单号
                        if ("0".equals(result.getCode())) {
                            updateOrderStatus(result, order);
                        }

                        // 取消进度条
                        stopProgressDialog();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    new Object[]{getResources().getString(R.string.sales_settle_hasup),
                            order.getOrderNo()}
            );
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

}
