package cn.burgeon.core.ui.sales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesArchiveSummerAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class SalesArchiveSummerActivity extends BaseActivity {
	
	ListView mList;
	SalesArchiveSummerAdapter mAdapter;
	TextView commonRecordnum,commonCount,commonMoney;
	EditText starDateET,endDateET;
	Button queryBtn;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_archivement_summer);

        init();
        bindList();
    }
    
    private void bindList() {
    	List<Order> data = fetchData();
    	mAdapter = new SalesArchiveSummerAdapter(data, this);
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
        
		queryBtn = (Button) findViewById(R.id.query);
		queryBtn.setOnClickListener(onClickListener);
		starDateET = (EditText) findViewById(R.id.sales_archivesummer_starttime);
		endDateET = (EditText) findViewById(R.id.sales_archivesummer_endtime);
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
    	mList = (ListView) findViewById(R.id.salesArchiveSummerLV);
    	mList.setAdapter(mAdapter);
    }

	private List<Order> fetchData() {
		Order order = null;
		List<Order> data = new ArrayList<Order>();
		Cursor c = db.rawQuery("select employeeID,orderEmployee,sum(count) as totalCount,sum(money) as totalMoney "
				+ "from c_settle where settleDate between ? and ? group by employeeID",new String[]{starDateET.getText().toString(),endDateET.getText().toString()});
		Log.d("zhang.h", "cursor size===========" + c.getCount());
		while(c.moveToNext()){
			order = new Order();
			order.setSaleAsistantID(c.getString(c.getColumnIndex("employeeID")));
			order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
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
	        case R.id.sales_archivesummer_starttime:
	            int startmYear = c.get(Calendar.YEAR);
	            int startmMonth = c.get(Calendar.MONTH);
	            int startmDay = c.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog startdialog = new DatePickerDialog(SalesArchiveSummerActivity.this, new startmDateSetListener(), startmYear, startmMonth, startmDay);
	            startdialog.show();
	            break;
	        case R.id.sales_archivesummer_endtime:
	            int endmYear = c.get(Calendar.YEAR);
	            int endmMonth = c.get(Calendar.MONTH);
	            int endmDay = c.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog enddialog = new DatePickerDialog(SalesArchiveSummerActivity.this, new endmDateSetListener(), endmYear, endmMonth, endmDay);
	            enddialog.show();
	            break;
	           
	        case  R.id.query:
	        	bindList();
	            break;
			}
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
