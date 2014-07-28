package cn.burgeon.core.ui.sales;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesOrderSearchAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForSalesQuery;

public class SalesOrderSearchActivity extends BaseActivity {
	
	ListView mList;
	SalesOrderSearchAdapter mAdapter;
	Button btnViewDetail, btnSearch;
	TextView recodeNumTV;
	CustomDialogForSalesQuery dialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_order_search);

        init();
        bindList();
    }
    
    private void bindList() {
    	List<Order> data = fetchData();
    	mAdapter = new SalesOrderSearchAdapter(data, this);
    	mList.setAdapter(mAdapter);
    	recodeNumTV.setText(String.format(getResources().getString(R.string.sales_new_common_record),data.size())); 
	}

	private void init(){
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);
        
        recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);
    	mList = (ListView) findViewById(R.id.salesOrderLV);
    	mList.setOnItemClickListener(onItemClickListener);
    	btnViewDetail = (Button) findViewById(R.id.searchDetailBtn);
    	btnSearch = (Button) findViewById(R.id.searchQueryBtn);
    	btnViewDetail.setOnClickListener(onClickListener);
    	btnSearch.setOnClickListener(onClickListener);
    }

	private List<Order> fetchData() {
		Order order = null;
		List<Order> data = new ArrayList<Order>();
		Cursor c = db.rawQuery("select * from c_settle", null);
		Log.d("zhang.h", "cursor size===========" + c.getCount());
		while(c.moveToNext()){
			order = new Order();
			order.setUuid(c.getString(c.getColumnIndex("settleUUID")));
			order.setOrderNo(c.getString(c.getColumnIndex("orderno")));
			order.setOrderDate(c.getString(c.getColumnIndex("settleTime")));
			order.setOrderType(c.getString(c.getColumnIndex("type")));
			order.setOrderCount(c.getString(c.getColumnIndex("count")));
			order.setOrderMoney(c.getString(c.getColumnIndex("money")));
			order.setOrderState(c.getString(c.getColumnIndex("status")));
			order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
			data.add(order);
		}
		if(c != null && !c.isClosed())
			c.close();
		return data;
	}
	
	Order currentSelectedOrder;
	View previous;
	int selectedPosition;
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
			if(previous != null) previous.setBackgroundDrawable(view.getBackground());
			view.setBackgroundResource(R.drawable.button_bg);
			previous = view;
			currentSelectedOrder = (Order) parent.getItemAtPosition(position);
		}
	};
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.searchDetailBtn:
				if(currentSelectedOrder != null)
					forwardActivity(SalesOrderDetailActivity.class,"updateID",currentSelectedOrder.getUuid());
				break;
			case R.id.searchQueryBtn:
				dialog = new CustomDialogForSalesQuery.Builder(SalesOrderSearchActivity.this).setPositiveButton("确定", new View.OnClickListener() {
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

        String sql = "select * from c_settle where " +
                "settleTime between " + "'" + finalStartTime + "'" + " and " + "'" + finalEndTime + "'";
        if (dialog.getOrderNo().length() > 0){
        	sql += " and orderno = '" + dialog.getOrderNo() + "'";
        }
        if (!"所有".equals(dialog.getState())) {//上传、未上传
            sql += " and status = " + "'" + dialog.getState() + "'";
        }
        
        Log.d("DailySalesActivity", "sql = " + sql);//SA30515214061100001
        
        Cursor c = db.rawQuery(sql, null);
        Order order = null;
        List<Order> data = new ArrayList<Order>();
    	while(c.moveToNext()){
			order = new Order();
			order.setId(c.getInt(c.getColumnIndex("_id")));
			order.setUuid(c.getString(c.getColumnIndex("settleUUID")));
			order.setOrderNo(c.getString(c.getColumnIndex("orderno")));
			order.setOrderDate(c.getString(c.getColumnIndex("settleTime")));
			order.setOrderType(c.getString(c.getColumnIndex("type")));
			order.setOrderCount(c.getString(c.getColumnIndex("count")));
			order.setOrderMoney(c.getString(c.getColumnIndex("money")));
			order.setOrderState(c.getString(c.getColumnIndex("status")));
			order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
			data.add(order);
		}
		if(c != null && !c.isClosed())
			c.close();
        mAdapter.setList(data);
        recodeNumTV.setText(String.format(getResources().getString(R.string.sales_new_common_record),data.size())); 
	}
	
}
