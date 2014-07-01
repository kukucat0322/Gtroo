package cn.burgeon.core.ui.allot;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.AllotOutLVAdapter;
import cn.burgeon.core.bean.AllotOut;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialog;

public class AllotOutActivity extends BaseActivity {

    private ListView allotOutLV;
    private TextView recodeNumTV;
    private Button addBtn, queryBtn;

    private ArrayList<AllotOut> lists;
    private AllotOutLVAdapter mAdapter;

    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allot_out);

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

        allotOutLV = (ListView) findViewById(R.id.allotoutLV);
        recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);

        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new ClickEvent());
        queryBtn = (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(new ClickEvent());
        
        itemOnLongClick();
    }

    private void initLVData() {
        lists = fetchData();
        mAdapter = new AllotOutLVAdapter(AllotOutActivity.this, lists, R.layout.allot_out_item);
        allotOutLV.setAdapter(mAdapter);

        if (lists.size() > 0)
            upateBottomBarInfo(lists);
    }

    private void upateBottomBarInfo(ArrayList<AllotOut> lists) {
    	recodeNumTV.setText(String.format(getResources().getString(R.string.sales_new_common_record), lists.size()));
	}

	private ArrayList<AllotOut> fetchData() {
        AllotOut allotOut = null;
        ArrayList<AllotOut> allotOuts = new ArrayList<AllotOut>();
        Cursor c = db.rawQuery("select * from c_allot_out", null);
        while (c.moveToNext()) {
            allotOut = new AllotOut();
            allotOut.setID(c.getInt(c.getColumnIndex("_id")));
            allotOut.setDOCNO(c.getString(c.getColumnIndex("dj_no")));
            allotOut.setUPLOAD_STATUS(c.getString(c.getColumnIndex("upload_status")));
            allotOut.setDOC_STATUS(c.getString(c.getColumnIndex("dj_status")));
            allotOut.setBILLDATE(c.getString(c.getColumnIndex("dj_date")));
            allotOut.setC_DEST_ID(c.getString(c.getColumnIndex("in_store")));
            allotOut.setTOT_QTYOUT(c.getString(c.getColumnIndex("num")));
            allotOuts.add(allotOut);
        }
        if (c != null && !c.isClosed())
            c.close();
        return allotOuts;
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	
    	initLVData();
    }
    
    private void itemOnLongClick() {
    	allotOutLV.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
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
        AllotOut allotOut = lists.get(menuInfo.position);

        switch (item.getItemId()) {
            case 0:
                // 更新数据库
                delWithNo(allotOut.getDOCNO());

                // 更改data
                lists.remove(allotOut);
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
            db.execSQL("delete from c_allot_out where dj_no = ?", new String[]{no});
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
            paramsInTransactions.put("columns", new JSONArray().put("ID").put("DOCNO").put("BILLDATE").put("C_DEST_ID").put("TOT_QTYOUT"));
            transactions.put("params", paramsInTransactions);

            array.put(transactions);
            params.put("transactions", array.toString());
            sendRequest(params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 取消进度条
                    stopProgressDialog();

                    try {
                        ArrayList<AllotOut> lists = resJAToList(response);
                        AllotOutLVAdapter mAdapter = new AllotOutLVAdapter(AllotOutActivity.this, lists, R.layout.allot_out_item);
                        allotOutLV.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<AllotOut> resJAToList(String response) throws JSONException {
        ArrayList<AllotOut> lists = new ArrayList<AllotOut>();

        JSONArray resJA = new JSONArray(response);
        JSONObject resJO = resJA.getJSONObject(0);
        JSONArray rowsJA = resJO.getJSONArray("rows");
        int len = rowsJA.length();
        recodeNumTV.setText(len + "条记录");
        for (int i = 0; i < len; i++) {
            // ["TF0912140000005",20091214,3909,11]
            String currRow = rowsJA.get(i).toString();
            String[] currRows = currRow.split(",");

            AllotOut allotOut = new AllotOut();
            allotOut.setID(currRows[0].substring(1, currRows[0].length()));
            allotOut.setDOCNO(currRows[1].substring(1, currRows[1].length() - 1));
            allotOut.setBILLDATE(currRows[2]);
            allotOut.setC_DEST_ID(currRows[3]);
            allotOut.setTOT_QTYOUT(currRows[4].substring(0, currRows[4].length() - 1));
            lists.add(allotOut);
        }
        return lists;
    }
    */

    class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addBtn:
                    forwardActivity(AllotOutApplyActivity.class);
                    break;
                case R.id.queryBtn:
                    customDialog = new CustomDialog.Builder(AllotOutActivity.this).setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customDialog.dismiss();
                        }
                    }).setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customDialog.dismiss();
                        }
                    }).setState("卧槽:").setSpinner(new String[]{"AAA", "BBB", "CCC", "DDD"}).show();
                    break;
            }
        }
    }

}
