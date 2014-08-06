package cn.burgeon.core.ui.sales;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesDailyReportAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.ui.QueryDialog;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.CustomDialogForReportQuery;
import cn.burgeon.core.widget.CustomDialogForSalesQuery;

public class SalesDailyReportActivity extends BaseActivity {
	
	private final String TAG = "SalesDailyReportActivity";
	ListView mList;
	SalesDailyReportAdapter mAdapter;
	Button btnSearch;
	TextView commonRecordnum,commonCount,commonMoney;
	String settleMonth;
	CustomDialogForReportQuery dialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_daily_report);

        init();
        bindList();
        settleMonth = getIntent().getStringExtra("settleMonth");
        Log.d(TAG, "======settleMonth=====" + settleMonth);
        //queryForUpdate();
    }
    
    private void bindList() {
    	List<Order> data = fetchData();
    	mAdapter = new SalesDailyReportAdapter(data, this);
    	mList.setAdapter(mAdapter);
    	upateBottomBarInfo(data);
	}
    
	private void upateBottomBarInfo(List<Order> data) {
		float pay = 0.0f;
		int count = 0;
		for(Order pro : data){
			pay += Float.parseFloat(pro.getOrderMoney());
			count += Integer.parseInt(pro.getOrderCount());
		}
		Log.d("zhang.h", "pay=" + pay+",count=" + count);
		
		commonMoney.setText(String.format(getResources().getString(R.string.sales_new_common_money),String.valueOf(pay)));
		commonCount.setText(String.format(getResources().getString(R.string.sales_new_common_count), count));
		commonRecordnum.setText(String.format(getResources().getString(R.string.sales_new_common_record), data.size()));
	}

	private void init(){
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

/*        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);*/
        commonRecordnum = (TextView) findViewById(R.id.sales_common_recordnum);
        commonCount = (TextView) findViewById(R.id.sales_common_count);
        commonMoney = (TextView) findViewById(R.id.sales_common_money);
    	mList = (ListView) findViewById(R.id.salesDailyReportLV);
    	btnSearch = (Button) findViewById(R.id.salesDailyReportQueryBtn);
    	btnSearch.setOnClickListener(onClickListener);
    }
	
    private void queryForUpdate() {
		Cursor c = db.rawQuery("select * from c_settle", null);
		Log.d("zhang.h", "result size:" + c.getCount());
		while(c.moveToNext()){
			Log.d("zhang.h", "settleUUID = " + c.getString(c.getColumnIndex("settleUUID")) + 
					" | settleDate = " + c.getString(c.getColumnIndex("settleDate")) +
					" | count = " + c.getString(c.getColumnIndex("count")) +
					" | money = " + c.getString(c.getColumnIndex("money")));
		}
    }

	private List<Order> fetchData() {
		String sql = null;
		if(settleMonth != null)
			sql = "select settleDate, sum(count) as totalCount,sum(money) as totalMoney from c_settle where settleDate like '"+settleMonth+"%' group by settleDate";
		else
			sql = "select settleDate, sum(count) as totalCount,sum(money) as totalMoney from c_settle  group by settleDate";
		Order order = null;
		List<Order> data = new ArrayList<Order>();
		Cursor c = db.rawQuery(sql,null);
		Log.d("zhang.h", "cursor size===========" + c.getCount());
		while(c.moveToNext()){
			order = new Order();
			order.setOrderDate(c.getString(c.getColumnIndex("settleDate")));
			order.setOrderCount(c.getString(c.getColumnIndex("totalCount")));
			order.setOrderMoney(c.getString(c.getColumnIndex("totalMoney")));
			data.add(order);
		}
		if(c != null && !c.isClosed())
			c.close();
		return data;
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.salesDailyReportQueryBtn:
				dialog = new CustomDialogForReportQuery.Builder(SalesDailyReportActivity.this).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search();
                        if (dialog.isShowing())
                        	dialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing())
                        	dialog.dismiss();
                    }
                }).setStateSpinner(new String[]{"所有", "未上传", "已上传"}).show();
				break;
			default:
				break;
			}
		}
	};
	
	private void search(){
		String startTime = dialog.getStartTime();
        String startYear = startTime.substring(0, 4);
        String startMonth = startTime.substring(4, 6);
        String startDay = startTime.substring(6, 8);
        String finalStartTime = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";

        String endTime = dialog.getEndTime();
        String endYear = endTime.substring(0, 4);
        String endMonth = endTime.substring(4, 6);
        String endDay = endTime.substring(6, 8);
        String finalEndTime = endYear + "-" + endMonth + "-" + endDay + " 23:59:59";

        String sql = null;
        if(settleMonth != null)
			sql = "select settleDate, sum(count) as totalCount,sum(money) as totalMoney from c_settle where settleDate like '"+settleMonth+"%' "
					+ " and settleTime between " + "'" + finalStartTime + "'" + " and " + "'" + finalEndTime + "'";
		else
			sql = "select settleDate, sum(count) as totalCount,sum(money) as totalMoney from c_settle"
					+ " where settleTime between " + "'" + finalStartTime + "'" + " and " + "'" + finalEndTime + "'";
        if (!"所有".equals(dialog.getState())) {//上传、未上传
            sql += " and status = " + "'" + dialog.getState() + "'";
        }
        
        sql += " group by settleDate";
        
        Log.d("DailySalesActivity", "sql = " + sql);//SA30515214061100001
        
        Cursor c = db.rawQuery(sql, null);
        Order order = null;
        List<Order> data = new ArrayList<Order>();
    	while(c.moveToNext()){
    		order = new Order();
			order.setOrderDate(c.getString(c.getColumnIndex("settleDate")));
			order.setOrderCount(c.getString(c.getColumnIndex("totalCount")));
			order.setOrderMoney(c.getString(c.getColumnIndex("totalMoney")));
			data.add(order);
		}
		if(c != null && !c.isClosed())
			c.close();
        mAdapter.setList(data);
        upateBottomBarInfo(data);
	}
	
}
