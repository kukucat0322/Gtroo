package cn.burgeon.core.ui.sales;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesReportAdapter;
import cn.burgeon.core.ui.BaseActivity;

public class SalesReportActivity extends BaseActivity {
	
    private GridView salesReportGV;
    private SalesReportAdapter mAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_report);
		
		init();
	}
	
    private void init() {
    	salesReportGV = (GridView) findViewById(R.id.salesReportGV);
        mAdapter = new SalesReportAdapter(this);
        salesReportGV.setAdapter(mAdapter);
        salesReportGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.salesReportTextValues[0].equals(itemValue)) {
                	Log.d("SalesManager", "==================="+itemValue);
                    forwardActivity(SalesOrderSearchActivity.class);
                } else if (itemValue != null && Constant.salesReportTextValues[1].equals(itemValue)) {
                    forwardActivity(SalesDailyReportActivity.class);
                } else if (itemValue != null && Constant.salesReportTextValues[2].equals(itemValue)) {
                    forwardActivity(SalesMonthlyReportActivity.class);
                } else if (itemValue != null && Constant.salesReportTextValues[3].equals(itemValue)) {
                    forwardActivity(SalesWareSummerActivity.class);
                }else if (itemValue != null && Constant.salesReportTextValues[4].equals(itemValue)) {
                    forwardActivity(SalesArchiveSummerActivity.class);
                }
            }
        });
    }

}
