package cn.burgeon.core.ui.sales;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesWareSummerDetailAdapter;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class SalesWareSummerDetailActivity extends BaseActivity {

	private final String TAG = "SalesWareSummerDetailActivity";
	TextView commonRecordnum,commonCount,commonMoney;
	ListView mListView;
	SalesWareSummerDetailAdapter mAdapter;
	ArrayList<Product> data = new ArrayList<Product>();
	String barCode;
	String startDate;
	String endDate;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_order_detail);

        init();
        
        Bundle bundle =  getIntent().getExtras();
        if(bundle != null){
	        barCode = bundle.getString("barCode");
	        startDate = bundle.getString("startDate");
	        endDate = bundle.getString("endDate");
	    	if(barCode != null){
	    		query();
	    	}
        }
    	upateBottomBarInfo();
    }


	private void query() {
		Cursor c = db.rawQuery("select * from c_settle_detail where barcode = ? and settleDate between '"+startDate+"' and '"+endDate+"'", new String[]{barCode});
		Log.d("zhang.h", "result size:" + c.getCount());
		Product product = null;
		while(c.moveToNext()){
			product = new Product();
			product.setBarCode(c.getString(c.getColumnIndex("barcode")));
			product.setName(c.getString(c.getColumnIndex("pdtname")));
			product.setPrice(c.getString(c.getColumnIndex("price")));
			product.setDiscount(c.getString(c.getColumnIndex("discount")));
			product.setCount(c.getString(c.getColumnIndex("count")));
			product.setMoney(c.getString(c.getColumnIndex("money")));
			product.setColor(c.getString(c.getColumnIndex("color")));
			product.setSize(c.getString(c.getColumnIndex("size")));
			data.add(product);
		}
		mAdapter.notifyDataSetChanged();
		if(c != null && !c.isClosed())
			c.close();
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "==========onDestroy=========");
	}

	private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());
        
        commonRecordnum = (TextView) findViewById(R.id.sales_common_recordnum);
        commonCount = (TextView) findViewById(R.id.sales_common_count);
        commonMoney = (TextView) findViewById(R.id.sales_common_money);
		mListView = (ListView) findViewById(R.id.orderDetailLV);
		mAdapter = new SalesWareSummerDetailAdapter(data, this);
		mListView.setAdapter(mAdapter);
	}

	
	private void upateBottomBarInfo() {
		float pay = 0.0f;
		int count = 0;
		if(data.size() > 0){
			for(Product pro : data){
				pay += Float.parseFloat(pro.getMoney());
				count += Integer.parseInt(pro.getCount());
			}
		}
		Log.d("zhang.h", "pay=" + pay+",count=" + count);
		
		commonMoney.setText(String.format(getResources().getString(R.string.sales_new_common_money),String.valueOf(pay)));
		commonCount.setText(String.format(getResources().getString(R.string.sales_new_common_count), count));
		commonRecordnum.setText(String.format(getResources().getString(R.string.sales_new_common_record), data.size()));
	}
}
