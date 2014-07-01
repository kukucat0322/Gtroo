package cn.burgeon.core.ui.allot;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.AllotReplenishmentLVAdapter;
import cn.burgeon.core.bean.AllotReplenishment;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

public class AllotReplenishmentQueryActivity extends BaseActivity {

    private ListView allotreplenishmentLV;
    private TextView recodeNumTV;

    private ArrayList<AllotReplenishment> lists;
    private AllotReplenishmentLVAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allot_replenishment_query);

        init();

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

        allotreplenishmentLV = (ListView) findViewById(R.id.allotreplenishmentLV);
        recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);
        
        itemOnLongClick();
    }

    private void initLVData() {
        lists = fetchData();
        mAdapter = new AllotReplenishmentLVAdapter(AllotReplenishmentQueryActivity.this, lists, R.layout.allot_replenishment_item);
        allotreplenishmentLV.setAdapter(mAdapter);

        if (lists.size() > 0)
            upateBottomBarInfo(lists);
    }
    
    private void upateBottomBarInfo(ArrayList<AllotReplenishment> lists) {
    	recodeNumTV.setText(String.format(getResources().getString(R.string.replenishment_record), lists.size()));
	}

    private ArrayList<AllotReplenishment> fetchData() {
        AllotReplenishment allotReplenishment = null;
        ArrayList<AllotReplenishment> allotReplenishments = new ArrayList<AllotReplenishment>();
        Cursor c = db.rawQuery("select * from c_replenishment", null);
        while (c.moveToNext()) {
            allotReplenishment = new AllotReplenishment();
            allotReplenishment.setID(c.getInt(c.getColumnIndex("_id")));
            allotReplenishment.setDOCNO(c.getString(c.getColumnIndex("dj_no")));
            allotReplenishment.setUPLOAD_STATUS(c.getString(c.getColumnIndex("upload_status")));
            allotReplenishment.setDOCDATE(c.getString(c.getColumnIndex("dj_date")));
            allotReplenishment.setOUT_STORE(c.getString(c.getColumnIndex("out_store")));
            allotReplenishment.setAPPLY_PEOPLE(c.getString(c.getColumnIndex("apply_people")));
            allotReplenishment.setREMARK(c.getString(c.getColumnIndex("remark")));
            allotReplenishments.add(allotReplenishment);
        }
        if(c != null && !c.isClosed())
            c.close();
        return allotReplenishments;
    }

    private void itemOnLongClick() {
    	allotreplenishmentLV.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "删除");
            }
        });
    }
    
    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {
        // String no = ((TextView) menuInfo.targetView.findViewById(R.id.noTV)).getText().toString();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AllotReplenishment allotReplenishment = lists.get(menuInfo.position);

        switch (item.getItemId()) {
            case 0:
                // 更新数据库
                delWithNo(allotReplenishment.getDOCNO());

                // 更改data
                lists.remove(allotReplenishment);
                upateBottomBarInfo(lists);

                // 刷新列表
                mAdapter.setList(lists);
                break;
        }
        return super.onContextItemSelected(item);
    }
    
    private void delWithNo(String no) {
        db.beginTransaction();
        try {
            db.execSQL("delete from c_replenishment where dj_no = ?", new String[]{no});
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }
    
    /*
    private void initLVData() {
        Map<String, String> params = new HashMap<String, String>();
        try {
            JSONArray array = new JSONArray();
            JSONObject transactions = new JSONObject();
            transactions.put("id", 112);
            transactions.put("command", "Query");

            JSONObject paramsInTransactions = new JSONObject();
            paramsInTransactions.put("table", "M_TRANSFER");
            paramsInTransactions.put("columns", new JSONArray().put("ID").put("DOCNO").put("BILLDATE").put("C_DEST_ID").put("STATUSERID").put("DESCRIPTION"));
            transactions.put("params", paramsInTransactions);

            array.put(transactions);
            params.put("transactions", array.toString());
            sendRequest(params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 取消进度条
                    stopProgressDialog();

                    try {
                        ArrayList<AllotReplenishment> lists = resJAToList(response);
                        AllotReplenishmentLVAdapter mAdapter = new AllotReplenishmentLVAdapter(AllotReplenishmentQueryActivity.this, lists, R.layout.allot_replenishment_item);
                        allotreplenishmentLV.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<AllotReplenishment> resJAToList(String response) throws JSONException {
        ArrayList<AllotReplenishment> lists = new ArrayList<AllotReplenishment>();

        JSONArray resJA = new JSONArray(response);
        JSONObject resJO = resJA.getJSONObject(0);
        JSONArray rowsJA = resJO.getJSONArray("rows");
        int len = rowsJA.length();
        recodeNumTV.setText("共" + len + "个补货单");
        for (int i = 0; i < len; i++) {
            // [2766,"TF0912140000005",20091214,3860,1692,null]
            String currRow = rowsJA.get(i).toString();
            String[] currRows = currRow.split(",");

            AllotReplenishment allotReplenishment = new AllotReplenishment();
            allotReplenishment.setID(currRows[0].substring(1, currRows[0].length()));
            allotReplenishment.setDOCNO(currRows[1].substring(1, currRows[1].length() - 1));
            allotReplenishment.setBILLDATE(currRows[2]);
            allotReplenishment.setC_DEST_ID(currRows[3]);
            allotReplenishment.setSTATUSERID(currRows[4]);
            allotReplenishment.setDESCRIPTION(("null".equals(currRows[5].substring(0, currRows[5].length() - 1)) ? "" : (currRows[5].substring(0, currRows[5].length() - 1))));
            lists.add(allotReplenishment);
        }
        return lists;
    }
    */
}
