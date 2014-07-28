package cn.burgeon.core.ui.sales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesWareSummerAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class SalesWareSummerActivity extends BaseActivity {
	
	ListView mList;
	SalesWareSummerAdapter mAdapter;
	Button btnDetail, btnQuery;
	TextView commonRecordnum,commonCount,commonMoney;
	EditText starDateET,endDateET;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_ware_summer);

        init();
        bindList();
    }
    
    private void bindList() {
    	List<Order> data = fetchData();
    	mAdapter = new SalesWareSummerAdapter(data, this);
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
        
		starDateET = (EditText) findViewById(R.id.sales_waresummer_starttime);
		endDateET = (EditText) findViewById(R.id.sales_waresummer_endtime);
		starDateET.setOnClickListener(onClickListener);
		endDateET.setOnClickListener(onClickListener);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		starDateET.setText(sdf.format(c.getTime()));
		endDateET.setText(sdf.format(new Date()));
        commonRecordnum = (TextView) findViewById(R.id.sales_common_recordnum);
        commonCount = (TextView) findViewById(R.id.sales_common_count);
        commonMoney = (TextView) findViewById(R.id.sales_common_money);
    	mList = (ListView) findViewById(R.id.salesWareSummerLV);
    	mList.setOnItemClickListener(itemClickListener);
    	btnDetail = (Button) findViewById(R.id.sales_ware_summer_minxibtn);
    	btnQuery = (Button) findViewById(R.id.sales_ware_summer_query);
    	btnQuery.setOnClickListener(onClickListener);
    	btnDetail.setOnClickListener(onClickListener);
    }

	private List<Order> fetchData() {
		Order order = null;
		List<Order> data = new ArrayList<Order>();
		Cursor c = db.rawQuery("select style, pdtname,sum(count) as totalCount,sum(count*money) as totalMoney "
				+ "from c_settle_detail  where settleDate "
				+ "between '"+starDateET.getText().toString()+"' and '"+endDateET.getText().toString()
				+ "' group by style", null);
		Log.d("zhang.h", "cursor size===========" + c.getCount());
		while(c.moveToNext()){
			order = new Order();
			order.setStyle(c.getString(c.getColumnIndex("style")));
			order.setName(c.getString(c.getColumnIndex("pdtname")));
			order.setOrderCount(c.getString(c.getColumnIndex("totalCount")));
			order.setOrderMoney(c.getString(c.getColumnIndex("totalMoney")));
			data.add(order);
		}
		if(c != null && !c.isClosed())
			c.close();
		return data;
	}
	
	Calendar c = Calendar.getInstance();
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
	        case R.id.sales_waresummer_starttime:
	            int startmYear = c.get(Calendar.YEAR);
	            int startmMonth = c.get(Calendar.MONTH);
	            int startmDay = c.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog startdialog = new DatePickerDialog(SalesWareSummerActivity.this, new startmDateSetListener(), startmYear, startmMonth, startmDay);
	            startdialog.show();
	            break;
	        case R.id.sales_waresummer_endtime:
	            int endmYear = c.get(Calendar.YEAR);
	            int endmMonth = c.get(Calendar.MONTH);
	            int endmDay = c.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog enddialog = new DatePickerDialog(SalesWareSummerActivity.this, new endmDateSetListener(), endmYear, endmMonth, endmDay);
	            enddialog.show();
	            break;
            case R.id.sales_ware_summer_minxibtn:
            	if(currentSelectedOrder != null){
            		Intent intent = new Intent(SalesWareSummerActivity.this,SalesWareSummerDetailActivity.class);
            		Bundle bundle = new Bundle();
            		bundle.putString("barCode", currentSelectedOrder.getStyle());
            		bundle.putString("startDate", starDateET.getText().toString());
            		bundle.putString("endDate", endDateET.getText().toString());
            		intent.putExtras(bundle);
            		startActivity(intent);
            	}
            	break;
            case R.id.sales_ware_summer_query:
            	bindList();
	            break;
			}
		}
	};
	
	Order currentSelectedOrder;
	View previous;
	int selectedPosition;
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			if(previous != null) previous.setBackgroundDrawable(view.getBackground());
			view.setBackgroundResource(R.drawable.button_bg);
			previous = view;
			currentSelectedOrder = (Order) parent.getItemAtPosition(position);
		}
	};
	
    class startmDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            // Month is 0 based so add 1

            String month = String.valueOf(mMonth + 1).length() == 2 ? String.valueOf(mMonth + 1) : "0" + String.valueOf(mMonth + 1);
            String day = String.valueOf(mDay).length() == 2 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            starDateET.setText(new StringBuilder().append(mYear).append('-').append(month).append('-').append(day));
        }
    }

    class endmDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            // Month is 0 based so add 1

            String month = String.valueOf(mMonth + 1).length() == 2 ? String.valueOf(mMonth + 1) : "0" + String.valueOf(mMonth + 1);
            String day = String.valueOf(mDay).length() == 2 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            endDateET.setText(new StringBuilder().append(mYear).append('-').append(month).append('-').append(day));
        }
    }
}
