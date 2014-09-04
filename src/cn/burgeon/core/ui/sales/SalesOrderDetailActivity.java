package cn.burgeon.core.ui.sales;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesNewOrderAdapter;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

public class SalesOrderDetailActivity extends BaseActivity {
	
	TextView commonRecordnum,commonCount,commonMoney;
	ListView mListView;
	SalesNewOrderAdapter mAdapter;
	ArrayList<Product> data = new ArrayList<Product>();
	String updateID = "unknow";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_report_detail);

        init();
        
    	updateID = getIntent().getExtras().getString("updateID");
    	Log.d("xxxx", "updateID = " + updateID ==null?"null" : updateID);
    	if(!"unknow".equals(updateID)){
    		query();
    	}
    	upateBottomBarInfo();
    }


	private void query() {
		Cursor c = db.rawQuery("select * from c_settle_detail where settleUUID = ?", new String[]{updateID});
		Log.d("zhang.h", "result size:" + c.getCount());
		Product product = null;
		while(c.moveToNext()){
			product = new Product();
			product.setUuid(updateID);
			product.setBarCode(c.getString(c.getColumnIndex("barcode")));
			product.setPrice(c.getString(c.getColumnIndex("price")));
			product.setDiscount(c.getString(c.getColumnIndex("discount")));
			product.setCount(c.getString(c.getColumnIndex("count")));
			product.setMoney(c.getString(c.getColumnIndex("money")));
			data.add(product);
		}
		mAdapter.notifyDataSetChanged();
		if(c != null && !c.isClosed())
			c.close();
    }
    
    
    @Override
    protected void onStop() {
    	super.onStop();
    	finish();
    }

	private void init() {
		Log.d("zhang.h", "=======init=======");
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

//        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
//        ViewGroup.LayoutParams params = hsv.getLayoutParams();
//        params.height = (int) ScreenUtils.getAllotInDetailLVHeight(this)-100;
        
        commonRecordnum = (TextView) findViewById(R.id.sales_common_recordnum);
        commonCount = (TextView) findViewById(R.id.sales_common_count);
        commonMoney = (TextView) findViewById(R.id.sales_common_money);
		mListView = (ListView) findViewById(R.id.salesReportDetailLV);
		mAdapter = new SalesNewOrderAdapter(data, this);
		mListView.setAdapter(mAdapter);
	}

	
	private void upateBottomBarInfo() {
		float pay = 0.0f;
		int count = 0;
		for(Product pro : data){
			pay += Float.parseFloat(pro.getMoney()) * Integer.parseInt(pro.getCount());
			count += Integer.parseInt(pro.getCount());
		}
		Log.d("zhang.h", "pay=" + pay+",count=" + count);
		
		commonMoney.setText(String.format(getResources().getString(R.string.sales_new_common_money),String.valueOf(pay)));
		commonCount.setText(String.format(getResources().getString(R.string.sales_new_common_count), count));
		commonRecordnum.setText(String.format(getResources().getString(R.string.sales_new_common_record), data.size()));
	}

}
