package cn.burgeon.core.ui.allot;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.AllotInLVAdapter;
import cn.burgeon.core.bean.AllotIn;
import cn.burgeon.core.bean.AllotInDetail;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

public class AllotInActivity extends BaseActivity {

    private ListView allotinLV;
    private TextView recodeNumTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_allot_in);

        // 初始化布局控件
        init();

        // 初始化列表数据
        initLVData();
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

        allotinLV = (ListView) findViewById(R.id.allotinLV);
        allotinLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllotIn allotIn = (AllotIn) allotinLV.getItemAtPosition(position);
                // Toast.makeText(AllotInActivity.this, allotIn.getID() + "", Toast.LENGTH_LONG).show();
                // 获取明细并跳转
                Map<String, String> params = new HashMap<String, String>();
                JSONArray array = new JSONArray();
                try {
                    JSONObject transactions = new JSONObject();
                    transactions.put("id", 112);
                    transactions.put("command", "Query");

                    JSONObject paramsTable = new JSONObject();
                    paramsTable.put("table", "M_TRANSFERITEM");
                    paramsTable.put("columns", new JSONArray().put("M_PRODUCTALIAS_ID")
                                    .put("M_ATTRIBUTESETINSTANCE_ID;VALUE1")
                                    .put("M_ATTRIBUTESETINSTANCE_ID;VALUE2_CODE")
                                    .put("QTYOUT")
                                    .put("QTYIN")
                                    .put("PRICELIST")
                                    .put("M_PRODUCT_ID;VALUE")
                    );
                    JSONObject paramsCondition = new JSONObject();
                    paramsCondition.put("condition", allotIn.getID());
                    paramsCondition.put("column", "M_TRANSFER_ID");
                    paramsTable.put("params", paramsCondition);

                    transactions.put("params", paramsTable);
                    array.put(transactions);
                    params.put("transactions", array.toString());

                    sendRequest(params, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // 取消进度条
                            stopProgressDialog();

                            try {
                                JSONArray resJA = new JSONArray(response);
                                JSONObject resJO = resJA.getJSONObject(0);
                                JSONArray rowsJA = resJO.getJSONArray("rows");
                                int len = rowsJA.length();

                                ArrayList<AllotInDetail> lists = new ArrayList<AllotInDetail>();
                                for (int i = 0; i < len; i++) {
                                    // [94448,"53","2",2,2,3499,"109430F196"]
                                    String currRow = rowsJA.get(i).toString();
                                    String[] currRows = currRow.split(",");

                                    AllotInDetail allotInDetail = new AllotInDetail();
                                    allotInDetail.setM_PRODUCTALIAS_ID(currRows[0].substring(1, currRows[0].length()));
                                    allotInDetail.setM_ATTRIBUTESETINSTANCE_ID_VALUE1(currRows[1].substring(1, currRows[1].length() - 1));
                                    allotInDetail.setM_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE(currRows[2].substring(1, currRows[2].length() - 1));
                                    allotInDetail.setQTYOUT(currRows[3]);
                                    allotInDetail.setQTYIN(currRows[4]);
                                    allotInDetail.setPRICELIST(currRows[5]);
                                    allotInDetail.setM_PRODUCT_ID_VALUE(currRows[6].substring(1, currRows[6].length() - 2));
                                    lists.add(allotInDetail);
                                }
                                IntentData intentData = new IntentData();
                                intentData.setAllotInDetails(lists);
                                forwardActivity(AllotInDetailActivity.class, intentData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);
    }

    private void initLVData() {
        Map<String, String> params = new HashMap<String, String>();
        try {
            JSONArray array = new JSONArray();
            JSONObject transactions = new JSONObject();
            transactions.put("id", 112);
            transactions.put("command", "Query");

            JSONObject paramsInTransactions = new JSONObject();
            paramsInTransactions.put("table", "M_TRANSFER");
            paramsInTransactions.put("columns", new JSONArray().put("ID").put("DOCNO").put("BILLDATE").put("C_ORIG_ID").put("TOT_QTYOUT"));
            transactions.put("params", paramsInTransactions);

            array.put(transactions);
            params.put("transactions", array.toString());
            sendRequest(params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 取消进度条
                    stopProgressDialog();

                    try {
                        ArrayList<AllotIn> lists = resJAToList(response);
                        AllotInLVAdapter mAdapter = new AllotInLVAdapter(AllotInActivity.this, lists, R.layout.allot_in_item);
                        allotinLV.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<AllotIn> resJAToList(String response) throws JSONException {
        ArrayList<AllotIn> lists = new ArrayList<AllotIn>();

        JSONArray resJA = new JSONArray(response);
        JSONObject resJO = resJA.getJSONObject(0);
        JSONArray rowsJA = resJO.getJSONArray("rows");
        int len = rowsJA.length();
        recodeNumTV.setText(len + "条记录");
        for (int i = 0; i < len; i++) {
            // [2766,"TF0912140000005",20091214,3909,11]
            String currRow = rowsJA.get(i).toString();
            String[] currRows = currRow.split(",");

            AllotIn allotIn = new AllotIn();
            allotIn.setID(currRows[0].substring(1, currRows[0].length()));
            allotIn.setDOCNO(currRows[1].substring(1, currRows[1].length() - 1));
            allotIn.setBILLDATE(currRows[2]);
            allotIn.setC_ORIG_ID(currRows[3]);
            allotIn.setTOT_QTYOUT(currRows[4].substring(0, currRows[4].length() - 1));
            lists.add(allotIn);
        }
        return lists;
    }

    class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            }
        }
    }
}
