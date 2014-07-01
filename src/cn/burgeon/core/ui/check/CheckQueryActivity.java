package cn.burgeon.core.ui.check;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.CheckQueryLVAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForCheckQuery;

public class CheckQueryActivity extends BaseActivity {

    private TextView recordCountTV, totalOutCountTV;
    private ListView checkQueryLV;
    private Button queryBtn;
    CheckQueryLVAdapter mAdapter;
    List<Order> data;
    CustomDialogForCheckQuery customDialogForCheckQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_query);

        init();

        initLVData();
    }

    private void initLVData() {
        data = fetchData();
        mAdapter = new CheckQueryLVAdapter(this, data, R.layout.check_query_item);
        checkQueryLV.setAdapter(mAdapter);
        if (data.size() > 0)
            upateBottomBarInfo(data);
    }

    private void upateBottomBarInfo(List<Order> data) {
        int count = 0;
        for (Order pro : data) {
            count += Integer.parseInt(pro.getOrderCount());
        }
        recordCountTV.setText(String.format(getResources().getString(R.string.sales_new_common_record), data.size()));
        totalOutCountTV.setText("数量" + count);
    }

    private List<Order> fetchData() {
        Order order = null;
        List<Order> data = new ArrayList<Order>();
        Cursor c = db.rawQuery("select * from c_check", null);
        while (c.moveToNext()) {
            order = new Order();
            order.setOrderDate(c.getString(c.getColumnIndex("checkTime")));
            order.setOrderNo(c.getString(c.getColumnIndex("checkno")));
            order.setOrderCount(c.getString(c.getColumnIndex("count")));
            order.setOrderType(c.getString(c.getColumnIndex("type")));
            order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
            order.setOrderState(c.getString(c.getColumnIndex("status")));
            order.setIsChecked(c.getString(c.getColumnIndex("isChecked")));
            data.add(order);
        }
        if (c != null && !c.isClosed())
            c.close();
        return data;
    }

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);

        checkQueryLV = (ListView) findViewById(R.id.checkQueryLV);
        recordCountTV = (TextView) findViewById(R.id.recordCountTV);
        totalOutCountTV = (TextView) findViewById(R.id.totalOutCountTV);

        queryBtn = (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出对话框
                customDialogForCheckQuery = new CustomDialogForCheckQuery.Builder(CheckQueryActivity.this).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.size() > 0)
                            queryCheck();

                        if (customDialogForCheckQuery.isShowing())
                            customDialogForCheckQuery.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (customDialogForCheckQuery.isShowing())
                            customDialogForCheckQuery.dismiss();
                    }
                }).setCheckTypeSpinner(new String[]{"所有", "随机盘", "全盘"}).setStateSpinner(new String[]{"所有", "未上传", "已上传"}).show();
            }
        });
    }

    private void queryCheck() {
        String startTime = customDialogForCheckQuery.getStartTime();
        String startYear = startTime.substring(0, 4);
        String startMonth = startTime.substring(4, 6);
        String startDay = startTime.substring(6, 8);
        String finalStartTime = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";

        String endTime = customDialogForCheckQuery.getEndTime();
        String endYear = endTime.substring(0, 4);
        String endMonth = endTime.substring(4, 6);
        String endDay = endTime.substring(6, 8);
        String finalEndTime = endYear + "-" + endMonth + "-" + endDay + " 23:59:59";

        String sql = "select * from c_check where " +
                "checkno = " + ((customDialogForCheckQuery.getCheckNo().length() > 0) ? ("'" + customDialogForCheckQuery.getCheckNo() + "'") : "''") + " and " +
                "checkTime between " + "'" + finalStartTime + "'" + " and " + "'" + finalEndTime + "'";
        if (!"所有".equals(customDialogForCheckQuery.getCheckType())) {
            sql += " and type = " + "'" + customDialogForCheckQuery.getCheckType() + "'";
        }
        if (!"所有".equals(customDialogForCheckQuery.getState())) {
            sql += " and isChecked = " + "'" + customDialogForCheckQuery.getState() + "'";
        }
        Log.i("zhang.h", sql);

        Cursor c = db.rawQuery(sql, null);
        List<Order> data = new ArrayList<Order>();
        while (c.moveToNext()) {
            Order order = new Order();
            order.setOrderDate(c.getString(c.getColumnIndex("checkTime")));
            order.setOrderNo(c.getString(c.getColumnIndex("checkno")));
            order.setOrderCount(c.getString(c.getColumnIndex("count")));
            order.setOrderType(c.getString(c.getColumnIndex("type")));
            order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
            order.setOrderState(c.getString(c.getColumnIndex("status")));
            order.setIsChecked(c.getString(c.getColumnIndex("isChecked")));
            data.add(order);
        }
        mAdapter.setList(data);
        upateBottomBarInfo(data);
        if (c != null && !c.isClosed())
            c.close();
    }

/*    private void initLVData() {
        Map<String, String> params = new HashMap<String, String>();
        try {
            JSONArray array = new JSONArray();
            JSONObject transactions = new JSONObject();
            transactions.put("id", 112);
            transactions.put("command", "Query");

            JSONObject paramsInTransactions = new JSONObject();
            paramsInTransactions.put("table", "M_INVENTORY");
            paramsInTransactions.put("columns", new JSONArray().put("ID").put("BILLDATE").put("DOCNO").put("DOCTYPE"));
            transactions.put("params", paramsInTransactions);

            array.put(transactions);
            params.put("transactions", array.toString());
            sendRequest(params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 取消进度条
                    stopProgressDialog();

                    try {
                        ArrayList<CheckQuery> lists = resJAToList(response);
                        CheckQueryLVAdapter mAdapter = new CheckQueryLVAdapter(CheckQueryActivity.this, lists, R.layout.check_query_item);
                        checkQueryLV.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<CheckQuery> resJAToList(String response) throws JSONException {
        ArrayList<CheckQuery> lists = new ArrayList<CheckQuery>();

        JSONArray resJA = new JSONArray(response);
        JSONObject resJO = resJA.getJSONObject(0);
        JSONArray rowsJA = resJO.getJSONArray("rows");
        int len = rowsJA.length();
        recordCountTV.setText("记录数：" + len);
        for (int i = 0; i < len; i++) {
            // [65608,20091212,"IV09121200001","历史盘点"]
            String currRow = rowsJA.get(i).toString();
            String[] currRows = currRow.split(",");

            CheckQuery checkQuery = new CheckQuery();
            checkQuery.setID(currRows[0].substring(1, currRows[0].length()));
            checkQuery.setBILLDATE(currRows[1]);
            checkQuery.setDOCNO(currRows[2].substring(1, currRows[2].length() - 1));
            checkQuery.setDOCTYPE(currRows[3].substring(1, currRows[3].length() - 2));
            lists.add(checkQuery);
        }
        return lists;
    }*/
}
