package cn.burgeon.core.ui.sales;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesDailyAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.ui.check.CheckQueryActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForCheckQuery;
import cn.burgeon.core.widget.CustomDialogForSalesQuery;

public class DailySalesActivity extends BaseActivity {
	
	private static final String TAG = "DailySalesActivity";
	TextView recodeNumTV;
	ListView mList;
	SalesDailyAdapter mAdapter;
	Button btnAdd, btnUpdate, btnSearch, btnDelete;
	CustomDialogForSalesQuery dialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_manager_dailysales);

        init();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	bindList();
    }
    
    private void bindList() {
    	List<Order> data = fetchData();
    	mAdapter = new SalesDailyAdapter(data, this);
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
    	mList = (ListView) findViewById(R.id.dailySalesLV);
    	mList.setOnItemClickListener(onItemClickListener);
    	btnAdd = (Button) findViewById(R.id.sales_daily_btn_add);
    	btnUpdate = (Button) findViewById(R.id.sales_daily_btn_update);
    	btnSearch = (Button) findViewById(R.id.sales_daily_btn_search);
    	btnDelete = (Button) findViewById(R.id.sales_daily_btn_delete);
    	btnAdd.setOnClickListener(onClickListener);
    	btnUpdate.setOnClickListener(onClickListener);
    	btnSearch.setOnClickListener(onClickListener);
    	btnDelete.setOnClickListener(onClickListener);
    }
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sales_daily_btn_add:
				forwardActivity(SalesNewOrderActivity.class);
				break;
			case R.id.sales_daily_btn_update:
				if(currentSelectedOrder != null){
					if(getString(R.string.sales_settle_hasup).equals(currentSelectedOrder.getOrderState())){
						showTips();
					}else{
						forwardActivity(SalesNewOrderActivity.class,"updateID",currentSelectedOrder.getUuid());
					}
				}
				break;
			case R.id.sales_daily_btn_search:
				dialog = new CustomDialogForSalesQuery.Builder(DailySalesActivity.this).setPositiveButton("确定", new View.OnClickListener() {
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
			case R.id.sales_daily_btn_delete:
				delete();
				//queryForUpdate();
				//querySku();
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
	
    private void showTips(){
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.systemtips))
    		.setMessage(R.string.no_update_uploaded_data)
    		.setPositiveButton(getString(R.string.confirm),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}});
    	dialog = builder.create();
    	dialog.show();
    }
	
    private void querySku() {
		Cursor c = db.rawQuery("select * from tc_styleprice", null);
		Log.d("zhang.h", "result size:" + c.getCount());
    }
    
    private void queryForUpdate() {
		try {
			Cursor c = db.rawQuery("select * from c_settle_detail", null);
			Log.d("zhang.h", "result size:" + c.getCount());
			while(c.moveToNext()){
				Log.d("zhang.h", "settleUUID = " + c.getString(c.getColumnIndex("settleUUID")) + 
						" | price = " + c.getString(c.getColumnIndex("price")) +
						" | discount = " + c.getString(c.getColumnIndex("discount")) + 
						" | count = " + c.getString(c.getColumnIndex("count")) +
						" | name = " + c.getString(c.getColumnIndex("pdtname")) +
						" | settleDate = " + c.getString(c.getColumnIndex("settleDate")) +
						" | money = " + c.getString(c.getColumnIndex("money")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	private void delete() {
		if(currentSelectedOrder != null){new String();
			db.execSQL("delete from c_settle where settleUUID = ?", new Object[]{currentSelectedOrder.getUuid()});
			db.execSQL("delete from c_settle_detail where settleUUID = ?", new Object[]{currentSelectedOrder.getUuid()});
		}
		bindList();
	}
	
	private List<Order> fetchData() {
		Order order = null;
		List<Order> data = new ArrayList<Order>();
		Cursor c = db.rawQuery("select * from c_settle", null);
		Log.d(TAG, "cursor size===========" + c.getCount());
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
		return data;
	}
	
	Order currentSelectedOrder;
	View previous;
	int selectedPosition;
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			
			if(previous != null) previous.setBackgroundDrawable(view.getBackground());
			view.setBackgroundResource(R.drawable.button_bg);
			previous = view;
			currentSelectedOrder = (Order) parent.getItemAtPosition(position);
		}
	};
}
