package cn.burgeon.core.ui.sales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mexxen.mx5010.barcode.BarcodeEvent;
import mexxen.mx5010.barcode.BarcodeListener;
import mexxen.mx5010.barcode.BarcodeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SalesNewOrderAdapter;
import cn.burgeon.core.bean.Employee;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.bean.Member;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.ui.member.MemberSearchActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

public class SalesNewOrderActivity extends BaseActivity {
	
	private static final String TAG = "SalesNewOrderActivity";
	private BarcodeManager bm;
	Button vipBtn, accountBtn;
	EditText cardNoET, styleBarcodeET,newSalesOrderDateET;
	TextView commonRecordnum,commonCount,commonMoney;
	ListView mListView;
	Spinner salesTypeSP,salesAssistantSP;
	SalesNewOrderAdapter mAdapter;
	ArrayList<Product> data = new ArrayList<Product>();
	String updateID = "unknow";
	boolean flag = false;
	static ArrayList<Product> temp = new ArrayList<Product>();
	private static final int SALESSETTLE = 101;
	Member searchedMember;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_sales_new_order);

        init();
        Log.d(TAG, "=========onCreate==========");
        dealForward(getIntent());
        
        bm = new BarcodeManager(this);
		bm.addListener(barcodeListener);
    }

	private void dealForward(Intent intent) {
		Bundle bundle = intent.getExtras();
        if(bundle != null){
        	searchedMember = bundle.getParcelable("searchedMember");
        	Log.d(TAG, "searchedMember = " + searchedMember ==null?"null" : searchedMember + "");
        	Log.d(TAG, "viptypeid = " + searchedMember.getTypeid());
        	if(searchedMember != null){
        		cardNoET.setText(searchedMember.getCardNum() + "\\" + searchedMember.getDiscount());
        		if(data.size() > 0){
        			for(int i = 0; i < mAdapter.getCount(); i++){
        				Product pro = (Product) mAdapter.getItem(i);
        				float price = Float.parseFloat(pro.getPrice());
        				//商品折扣
        				float proDiscount = Float.parseFloat(pro.getDiscount()) / 100;
        				//会员折扣
        				float vipDiscount = 0.0f;
        				if(cardNoET.getText().length() > 0) 
        					vipDiscount = Float.parseFloat(searchedMember.getDiscount());
        				pro.setDiscount(searchedMember.getDiscount());
        				pro.setMoney(String.format("%.2f",price * vipDiscount));
        				mAdapter.notifyDataSetChanged();
        				upateBottomBarInfo();
        			}
        		}
        	}
        	updateID = bundle.getString("updateID");
        	Log.d("xxxx", "updateID = " + updateID ==null?"null" : updateID + "");
        	if(!"unknow".equals(updateID) && (updateID != null) && !flag){
        		queryForUpdate();
        	}
        }
	}
    
    BarcodeListener barcodeListener = new BarcodeListener() {
		// 重写 barcodeEvent 方法，获取条码事件
		@Override
		public void barcodeEvent(BarcodeEvent event) {
			// 当条码事件的命令为“SCANNER_READ”时，进行操作
			if (event.getOrder().equals("SCANNER_READ")) {
				// 调用 getBarcode()方法读取条码信息
				Log.d(TAG, "=======barcode========" + bm.getBarcode());
				String baString = bm.getBarcode() != null?bm.getBarcode().trim():"";
				styleBarcodeET.setText("");
				verifyBarCode(baString);
			}
		}
	};


	private void queryForUpdate() {
		Log.d(TAG, "========queryForUpdate=========");
		Cursor c = db.rawQuery("select * from c_settle_detail where settleUUID = ?", new String[]{updateID});
		Log.d("zhang.h", "result size:" + c.getCount());
		Product product = null;
		while(c.moveToNext()){
			product = new Product();
			product.setId(c.getInt(c.getColumnIndex("_id")));
			product.setUuid(updateID);
			product.setBarCode(c.getString(c.getColumnIndex("barcode")));
			product.setName(c.getString(c.getColumnIndex("pdtname")));
			product.setPrice(c.getString(c.getColumnIndex("price")));
			product.setDiscount(c.getString(c.getColumnIndex("discount")));
			product.setCount(c.getString(c.getColumnIndex("count")));
			product.setMoney(c.getString(c.getColumnIndex("money")));
			data.add(product);
		}
		mAdapter.notifyDataSetChanged();
		upateBottomBarInfo();
		if(c != null && !c.isClosed())
			c.close();
    }

	@Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	Log.d(TAG, "=========onNewIntent==========");
        dealForward(intent);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);  
		if(requestCode == SALESSETTLE && resultCode == RESULT_OK){
			this.data.clear();
			temp.clear();
			mAdapter.notifyDataSetChanged();
			upateBottomBarInfo();
		}
	}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	bm.removeListener(barcodeListener);
    	bm.dismiss();
    }

	private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInDetailLVHeight(this)-100;
        
        salesTypeSP = (Spinner) findViewById(R.id.salesType);
/*        salesTypeSP.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(styleBarcodeET.getText().length() > 0)
				verifyBarCode(styleBarcodeET.getText().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});*/
        salesAssistantSP = (Spinner) findViewById(R.id.salesAssistantET);
        ArrayAdapter<Employee> adapter = new ArrayAdapter<Employee>(SalesNewOrderActivity.this, android.R.layout.simple_spinner_item, getEmployees());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesAssistantSP.setAdapter(adapter);
        salesAssistantSP.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.d(TAG, ((Employee)salesAssistantSP.getSelectedItem()).getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        commonRecordnum = (TextView) findViewById(R.id.sales_common_recordnum);
        commonCount = (TextView) findViewById(R.id.sales_common_count);
        commonMoney = (TextView) findViewById(R.id.sales_common_money);
		vipBtn = (Button) findViewById(R.id.salesNewVIPbtn);
		accountBtn = (Button) findViewById(R.id.salesNewJiezhangBtn);
		vipBtn.setOnClickListener(onClickListener);
		accountBtn.setOnClickListener(onClickListener);
		cardNoET = (EditText) findViewById(R.id.cardnoDiscountET);
		newSalesOrderDateET = (EditText) findViewById(R.id.newSalesOrderDateET);
		newSalesOrderDateET.setOnClickListener(onClickListener);
		newSalesOrderDateET.setText(getCurrDate());
		styleBarcodeET = (EditText) findViewById(R.id.styleBarcodeET);
		styleBarcodeET.setOnEditorActionListener(editorActionListener);
		//styleBarcodeET.setText("AS001BL");
		mListView = (ListView) findViewById(R.id.newOrderLV);
		mAdapter = new SalesNewOrderAdapter(data, this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemLongClickListener(itemLongClickListener);
	}

	Calendar c = Calendar.getInstance();
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.salesNewVIPbtn:
				//跳转到会员注册页面
				if(!"unknow".equals(updateID) && (updateID != null)){
					showTips(R.string.sales_settle_clear2);
				}else{
					//temp.clear();
					//temp.addAll(data);
					if(data.size() == 0)
					forwardActivity(MemberSearchActivity.class);
				}
				break;
			case R.id.salesNewJiezhangBtn:
				// 跳转并传递数据
				if(data.size() > 0){
	                IntentData intentData = new IntentData();
	                intentData.setProducts(data);
	            	intentData.setCommand(updateID);
	            	intentData.setEmployee((Employee)salesAssistantSP.getSelectedItem());
	            	intentData.setVipCardno(searchedMember != null?searchedMember.getCardNum():"");
	                forwardActivity(SalesSettleActivity.class, intentData,SALESSETTLE);
				}else{
					showTips(R.string.sales_settle_nodata);
				}
                break;
/*			case R.id.verifyBarCodeBtn:
				verifyBarCode();
				break;*/
			case R.id.newSalesOrderDateET:
	            int startmYear = c.get(Calendar.YEAR);
	            int startmMonth = c.get(Calendar.MONTH);
	            int startmDay = c.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog startdialog = new DatePickerDialog(SalesNewOrderActivity.this, new startmDateSetListener(), startmYear, startmMonth, startmDay);
	            startdialog.show();
				break;
			default:
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
            newSalesOrderDateET.setText(new StringBuilder().append(mYear).append('-').append(month).append('-').append(day));
        }
    }
	
	private void upateBottomBarInfo() {
		float pay = 0.0f;
		int count = 0;
		if(data != null && data.size() > 0){
			for(Product pro : data){
				pay += Float.parseFloat(pro.getMoney()) * Integer.parseInt(pro.getCount());
				count += Integer.parseInt(pro.getCount());
			}
		}
		Log.d("zhang.h", "pay=" + pay+",count=" + count);
		
		commonMoney.setText(String.format(getResources().getString(R.string.sales_new_common_money),String.valueOf(pay)));
		commonCount.setText(String.format(getResources().getString(R.string.sales_new_common_count), count));
		commonRecordnum.setText(String.format(getResources().getString(R.string.sales_new_common_record), data.size()));
	}
	
	private void verifyBarCode(String barcode) {
		//从本地获取
		varLocal(barcode);
	/*	try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		styleBarcodeET.setText("");*/
		//从网络获取
		//varNet();
	}

	private void varLocal(String barcode) {
		Log.d(TAG, "barcode2======" + barcode);
		if(salesTypeSP.getSelectedItemPosition() == 3){
			showOrginOrdernoTips(barcode);
		}else{
			normalSales(barcode);
		}
	}
	
	private void normalSales(String barcode){
			String sql = "select a.style,b.attrib1,b.style_name,c.clrname,d.sizename,e.fprice"
					+" from tc_sku as a"
					+" left join tc_style as b"
					+" on a.style = b.style"
					+" left join tdefclr as c"
					+" on a.clr = c.clr"
					+" left join tdefsize as d"
					+" on a.sizeid = d.sizeid"
					+" left join tc_styleprice as e"
					+" on a.style = e.style"
					+" where a.sku = ?";
		Cursor c = db.rawQuery(sql, new String[]{barcode});
		Log.d(TAG, "result size = " + c.getCount());
	    if(c.getCount() == 0){
	    	styleBarcodeET.setText(barcode);
	    	showAlertMsg(R.string.nothatbarcode);
	    	return;
	    }
		if(c.moveToFirst()){
			List<Product> list = parseSQLResult(c,barcode);
			data.addAll(list);
			mAdapter.notifyDataSetChanged();
			upateBottomBarInfo();
		}
		if(c != null && !c.isClosed())
			c.close();
	}

	private List<Product> parseSQLResult(Cursor c, String barcode) {
		List<Product> items = new ArrayList<Product>(1);
		Product pro = new Product();
		pro.setBarCode(barcode);
		pro.setEmployee(salesAssistantSP.getSelectedItem().toString());
		pro.setBrand(c.getString(c.getColumnIndex("attrib1")));
		pro.setStyle(c.getString(c.getColumnIndex("style")));
		pro.setName(c.getString(c.getColumnIndex("style_name")));
		pro.setPrice(c.getString(c.getColumnIndex("fprice")));
		pro.setColor(c.getString(c.getColumnIndex("clrname")));
		pro.setSize(c.getString(c.getColumnIndex("sizename")));
/*		if(cardNoET.getText().length() > 0)
			pro.setDiscount(searchedMember.getDiscount());
		else
			pro.setDiscount("1.0");*/
		pro.setCount("1");
		float price = Float.parseFloat(pro.getPrice());
		float vipDiscount = 0.0f;
		if(cardNoET.getText().length() > 0){
			Cursor c1 = db.rawQuery("select discount from tc_vipTypeDis where vtpid = ? and brandid = ?", new String[]{searchedMember.getTypeid(),pro.getBrand()});
			if(c1.moveToFirst()){
				vipDiscount = Float.parseFloat(c1.getString(0));
			}else{
				vipDiscount = Float.parseFloat(searchedMember.getDiscount());
			}
			if(c1!=null && !c1.isClosed()) c1.close();
		}
		//   1:正常零售,2:退货,
		//   3:赠品,4:全额,
		switch (salesTypeSP.getSelectedItemPosition()) {
			case 0://正常
				if(cardNoET.getText().length() > 0){
					pro.setMoney(String.format("%.2f",price * vipDiscount));
				}else{
					//策略
					if(!isFitPolicy(pro))
						pro.setMoney(String.format("%.2f",price));
				}
				pro.setSalesType(1);
				break;
			case 1://全额 
//				if(cardNoET.getText().length() > 0)
//					pro.setMoney(String.format("%.2f",price * vipDiscount));
//				else
				pro.setMoney(String.format("%.2f",price));
				pro.setSalesType(4);
				break;
			case 2://赠送
				pro.setMoney("0.00");
				pro.setSalesType(3);
				break;
			case 3://退货
				pro.setCount("1");
				pro.setSalesType(2);
				if(cardNoET.getText().length() > 0){
					pro.setMoney(String.format("%.2f",price * vipDiscount));
				}else{
					//策略
					if(!isFitPolicy(pro))
					pro.setMoney("-"+String.format("%.2f",price));
				}
				break;
			default:
				break;
		}
		pro.setDiscount(String.format("%.2f", Float.parseFloat(pro.getMoney())/price));
		items.add(pro);
		return items;
	}
	
	private boolean isFitPolicy(Product pro){
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		String flowNO = null;
		String money = null;
		//1.是否在策略中
		Cursor c = db.rawQuery("select * from TdefPosSkuDt where sku = ?", new String[]{pro.getStyle()});
		if(c.moveToFirst()){
			flag1 = true;
			flowNO = c.getString(c.getColumnIndex("flowno"));
			money = c.getString(c.getColumnIndex("exexcontent"));
			pro.setMoney(String.format("%.2f", Float.parseFloat(money)));
		} else return false;
		//2.策略有没有本店
		c = db.rawQuery("select * from TdefPosSkuRel where store = ?", new String[]{App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.storeNumberKey)});
		if(c.getCount() > 0) flag2 = true;
		else return false;
		//3.策略有没有过期
		c = db.rawQuery("select * from TdefPosSku where flowno = ?", new String[]{flowNO});
		if(c.moveToFirst()){
			try {
				String start = c.getString(c.getColumnIndex("datebeg"));
				String end = c.getString(c.getColumnIndex("dateEnd"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date date1 = sdf.parse(start); 
				Date date2 = sdf.parse(end);
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				Calendar now = Calendar.getInstance();
				c1.setTime(date1);
				c2.setTime(date2);
				now.setTime(new Date());
				if(c1.before(now) && now.before(c2))
					flag3 = true;
			} catch (ParseException e) {}
		}else return false;
		return flag1 && flag2 && flag3;
	}


	private void varNet() {
		Map<String,String> params = new HashMap<String, String>();
		JSONArray array;
		JSONObject transactions;
		try {
			array = new JSONArray();
			transactions = new JSONObject();
			transactions.put("id", 112);
			transactions.put("command", "Query");
			
			//第一个params
			JSONObject paramsInTransactions = new JSONObject();
			paramsInTransactions.put("table", 12668);
			paramsInTransactions.put("columns", new JSONArray().put("NO")
					.put("M_PRODUCT_ID;PRICELIST")
					.put("M_PRODUCT_ID;value")
					.put("M_PRODUCT_ID;name"));
			//在params中的params
			paramsInTransactions.put("params", new JSONObject().put("column", "NO")
					.put("condition", styleBarcodeET.getText().toString()));
			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			
			Log.d("zhang.h", array.toString());
			params.put("transactions", array.toString());
/*			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					// 取消进度条
                    stopProgressDialog();
					Log.d("zhang.h", response);
					//response = "";109454D334620  650012110 53785267
					List<Product> list = parseResult(response);
					data.addAll(list);
					//mListView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					upateBottomBarInfo();
				}
			});*/
			String response = "[{'message':'完成:0.127 seconds','id':'112','code':0,'rows':[['109454D334620',1099.85,'109454D334','109454D334']]}]";
			List<Product> list = parseResult(response);
			data.addAll(list);
			mAdapter.notifyDataSetChanged();
			upateBottomBarInfo();
		} catch (JSONException e) {}
	}
	
	private List<Product> parseResult(String response) {
		Product product = null;
		List<Product> list = null;
	    try {
	    	list = new ArrayList<Product>();
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			JSONArray rowsJA = resJO.getJSONArray("rows");
			for(int i = 0; i < rowsJA.length(); i++){
				String row = rowsJA.get(i).toString();
				String[] rowArr = row.split(",");
				
				product = new Product();
				product.setBarCode(rowArr[0].substring(2,rowArr[0].length()-1));
				product.setDiscount("0");
				product.setPrice(rowArr[1].replace("\"", ""));
				product.setCount("1");
				product.setMoney(String.valueOf((Integer.parseInt(product.getCount()) * Float.parseFloat(product.getPrice()))));
				product.setName(rowArr[3].substring(1,rowArr[3].length()-2));
				list.add(product);
			}
		} catch (JSONException e) {}
		return list;
	}
	
	OnEditorActionListener editorActionListener = new OnEditorActionListener(){

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			switch (actionId) {
			case EditorInfo.IME_ACTION_SEARCH:
				verifyBarCode(styleBarcodeET.getText().toString());
				break;

			default:
				break;
			}
			return true;
		}
		
	};
	
	OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			showDetailTips(position);
			return true;
		}
	};
	
	 //显示对话框
    private void showTips(int msgResId){
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.systemtips))
    		.setMessage(msgResId)
    		.setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}});
    	dialog = builder.create();
    	dialog.show();
    }
    
    //显示对话框
    private void showDetailTips(final int position){
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.systemtips))
    		.setMessage(R.string.sales_del_item)
    		.setPositiveButton(getString(R.string.confirm),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					data.remove(position);
					mAdapter.notifyDataSetChanged();
					upateBottomBarInfo();
				}})
			.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
				@Override  
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}});
    	dialog = builder.create();
    	dialog.show();
    }
    
    private List<Employee> getEmployees() {
		Cursor c = db.rawQuery("select id, name from employee",null);
		List<Employee> employees = new ArrayList<Employee>();
		if(c!=null){
			while(c.moveToNext()){
				employees.add(new Employee(c.getString(c.getColumnIndex("id")),c.getString(c.getColumnIndex("name"))));
			}
			if(!c.isClosed()) c.close();
			return employees;
		}
		return employees;
	}
    
    private void showOrginOrdernoTips(final String barcode){
    	Log.d(TAG, "=====showOrginOrdernoTips======");
    	LayoutInflater inflater = getLayoutInflater();
    	View tipsLayout = inflater.inflate(R.layout.orgin_orderno_tips, 
    			(ViewGroup)findViewById(R.id.orginOrdernoTipsLayout));
    	final EditText tipsText = (EditText) tipsLayout.findViewById(R.id.orginOrdernoTipsText);
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    	.setTitle("请输入原单号")
    		.setView(tipsLayout)
    		.setPositiveButton(getString(R.string.confirm),new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					if(tipsText.getText().length() > 0){
						String orginOrderno = tipsText.getText().toString();
						String sql = "select b.* from c_settle as a, c_settle_detail as b"
									+" where a.settleUUID = b.settleUUID"
									+" and b.barcode = '"+barcode+"' and a.orderno = '"+orginOrderno+"' limit 1";
						Log.d(TAG, "sql====" + sql);
						Cursor c = db.rawQuery(sql, null);
						Log.d(TAG, "size==" + c.getCount());
						if(c.moveToNext()){
							Product pro = new Product();
							pro.setCount("-1");
							pro.setSalesType(2);
							pro.setBarCode(barcode);
							pro.setOrgorderNO(orginOrderno);
							pro.setEmployee(salesAssistantSP.getSelectedItem().toString());
							pro.setDiscount(c.getString(c.getColumnIndex("discount")));
							pro.setStyle(c.getString(c.getColumnIndex("style")));
							pro.setName(c.getString(c.getColumnIndex("pdtname")));
							pro.setPrice(c.getString(c.getColumnIndex("price")));
							pro.setColor(c.getString(c.getColumnIndex("color")));
							pro.setSize(c.getString(c.getColumnIndex("size")));
							pro.setMoney(c.getString(c.getColumnIndex("money")));
							data.add(pro);//SA30515214073000001
							mAdapter.notifyDataSetChanged();
							upateBottomBarInfo();
						}
						if(c !=null && !c.isClosed()) c.close();
					}
				}
			}).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					normalSales(barcode);
			    	dialog.dismiss();
				}
			});
    	dialog = builder.create();
    	dialog.show();
    }

}
