package cn.burgeon.core.ui.sales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.bean.PayWay;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.UndoBarController;
import cn.burgeon.core.widget.UndoBarController.UndoListener;
import cn.burgeon.core.widget.UndoBarStyle;

public class SalesSettleActivity extends BaseActivity {

	private final String TAG = "SalesSettleActivity";
	ListView mListView;
	Button settleBtn;
	TextView payTV, counTV;
	EditText orginET, disCounET, realityET;
	ArrayList<Product> products;
	String command, vipCardno,employee;
	LinearLayout mPaywayLayout;
	
	public EditText getOrginET() {
		return orginET;
	}

	public void setOrginET(EditText orginET) {
		this.orginET = orginET;
	}

	public EditText getDisCounET() {
		return disCounET;
	}

	public void setDisCounET(EditText disCounET) {
		this.disCounET = disCounET;
	}

	public EditText getRealityET() {
		return realityET;
	}

	public void setRealityET(EditText realityET) {
		this.realityET = realityET;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_sales_settle);

		init();
		
		settle();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	float pay = 0;
	int count = 0;
	float discount = 0;
	String regex = "^[\\d]\\.[\\d]{1,2}$";

	private void settle() {
		IntentData iData = (IntentData) getIntent().getParcelableExtra(PAR_KEY);
		products = iData.getProducts();
		command = iData.getCommand();
		vipCardno = iData.getVipCardno();
		employee = iData.getEmployee();
		Log.d("zhang.h", "command=" + command);
		for(Product pro : products){
			pay += Float.parseFloat(pro.getMoney()) * Integer.parseInt(pro.getCount());
			count += Integer.parseInt(pro.getCount());
		}
		payTV.setText(String.format(getResources().getString(R.string.sales_settle_pay),String.format("%.2f",pay)));
		counTV.setText(String.format(getResources().getString(R.string.sales_settle_count),count));
		
		disCounET.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					if(disCounET.getText().length() > 0){
						
						if(Pattern.compile(regex).matcher(disCounET.getText().toString()).find()){
							discount = Float.parseFloat(disCounET.getText().toString());
							float other = 0.0f;
							float total = pay * discount;
							realityET.setText(String.format("%.2f", pay * discount));
							EditText cashET = null;
							for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
								LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
								EditText value = (EditText) item.getChildAt(2);
								TextView name = (TextView) item.getChildAt(0);
								if(!"现金".equals(name.getText().toString())){
									if(value.getText().length() > 0)
										other += Float.parseFloat(value.getText().toString());
								}else{
									cashET = value;
								}
							}
							cashET.setText(String.format("%.2f", total-other));
						}else{
							//提示折扣不正确
							disCounET.setError("折扣错误");
						}
					}
				}
			}
		});
		realityET.setText(String.format("%.2f", pay));
		ArrayList<PayWay> pays = new ArrayList<PayWay>();
		if("unknow".equals(command) || null == command){
			Cursor c = db.rawQuery("select * from tc_payway where _id != ?", new String[]{"26"});
			while(c.moveToNext()){
				if(27 == c.getInt(0))
					pays.add(new PayWay(c.getInt(0), c.getString(1), String.format("%.2f",pay)));
				else
					pays.add(new PayWay(c.getInt(0), c.getString(1), ""/*String.format("%.2f",0.00f)*/));
			}
		}else{
			Cursor c = db.rawQuery("select * from c_payway_detail where settleUUID = ?", new String[]{command});
			while(c.moveToNext()){
				pays.add(new PayWay(c.getInt(c.getColumnIndex("paywayID")), c.getString(c.getColumnIndex("name")), String.format("%.2f",Float.parseFloat(c.getString(c.getColumnIndex("money"))))));
			}
			if(c != null && !c.isClosed())
				c.close();
		}
		for(PayWay payway : pays){
			LinearLayout item = (LinearLayout)LayoutInflater.from(SalesSettleActivity.this).inflate(R.layout.sales_settle_list_item, null);
			item.setTag(payway);
			((TextView)item.getChildAt(0)).setText(payway.getPayWay());
			EditText editText = (EditText)item.getChildAt(2);
			editText.setTag(payway.getId());
			editText.setOnFocusChangeListener(focusChangeListener);
			editText.setText(payway.getPayMoney());
			mPaywayLayout.addView(item);
		}
	}
	
	
	View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if(hasFocus){
				Log.d(TAG, "view tag = " + view.getTag());
				float total = 0.0f;
				float other = 0.0f;
				for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
					LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
					EditText editText = (EditText) item.getChildAt(2);
					if(view.getTag() != editText.getTag()){
						if(editText.getText().length() > 0)
							other += Float.parseFloat(editText.getText().toString());
					}
				}
				if(realityET.getText().length() > 0)
					total = Float.parseFloat(realityET.getText().toString());
				((EditText)view).setText("0.00".equals(String.format("%.2f", total - other))?"":String.format("%.2f", total - other));
			}
		}
	};

	private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());
        
        mPaywayLayout = (LinearLayout) findViewById(R.id.wrapPayway);
		settleBtn = (Button) findViewById(R.id.settleJiezhangBtn);
		settleBtn.setOnClickListener(onClickListener);
		payTV = (TextView) findViewById(R.id.salesSettlePay);
		counTV = (TextView) findViewById(R.id.salesSettleCount);
		orginET = (EditText) findViewById(R.id.salesSettleOrginET);
		disCounET = (EditText) findViewById(R.id.salesSettleDiscountET);
		realityET = (EditText) findViewById(R.id.salesSettleRealityET);
		//mListView = (ListView) findViewById(R.id.settleLV);
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!validate()){
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		        UndoBarController.show(SalesSettleActivity.this, "金额不正确", null, MESSAGESTYLE);
		        return;
			}
			if("unknow".equals(command) || null == command){
				showTips(1);
			}
			else{
				showTips(2);
			}
		}
	};
	
    //显示对话框
    private void showTips(final int what){
    	AlertDialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
    		.setTitle(getString(R.string.systemtips))
    		.setMessage(R.string.sales_settle_dialogmsg)
    		.setPositiveButton(getString(R.string.confirm),new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					discoutDetail();
					if(what == 1){
						save();
					}
					else if(what == 2)update();
					UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
			        UndoBarController.show(SalesSettleActivity.this, "结账成功", new UndoListener() {
						
						@Override
						public void onUndo(Parcelable token) {
							setResult(RESULT_OK);
							finish();
						}
					}, MESSAGESTYLE);
			        
				}})
			.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}});
    	dialog = builder.create();
    	dialog.show();
    }
    
    private void discoutDetail(){
    	if(Pattern.compile(regex).matcher(disCounET.getText().toString()).find()){
    		for(Product pro : products){
    			pro.setMoney(String.format("%.2f", Float.parseFloat(pro.getMoney()) * Float.parseFloat(disCounET.getText().toString())));
    		}
    	}
    }
    
    private boolean validate(){
    	float money = 0.0f;
    	for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
    		LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
    		EditText editText = (EditText) item.getChildAt(2);
    		if(editText.getText().length() > 0){
    			money += Float.parseFloat(editText.getText().toString());
    		}
    	}
    	Log.d(TAG, "====validate money" + money);
    	Log.d(TAG, "====validate realityET" + realityET.getText().toString());
    	return money == Float.parseFloat(realityET.getText().toString());
    }
	
	public void save(){
		db.beginTransaction();
        try {
        	String uuid = UUID.randomUUID().toString();
        	Date currentTime = new Date();
        	db.execSQL("insert into c_settle('settleTime','type','count','money','employeeID','orderEmployee',"
        			+ "'status','settleDate','settleMonth','vipCardno','orderno','settleUUID')"+
        				" values(?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime),
								getResources().getString(R.string.sales_settle_type),
								count,
								realityET.getText(),
								employee,
								employee,
								getString(R.string.sales_settle_noup),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime).substring(0, 7),
								vipCardno,
								getNo(),//销售单号
								uuid});
        	for(Product pro : products){
        		db.execSQL("insert into c_settle_detail('style','barcode','price','discount'"
        				+ ",'count','money','settleUUID','pdtname','color','size','settleDate','salesType')"
        				+ " values(?,?,?,?,?,?,?,?,?,?,?,?)",
    					new Object[]{pro.getStyle(),pro.getBarCode(),pro.getPrice(), pro.getDiscount(),
        						pro.getCount(), pro.getMoney(), uuid, pro.getName(),
        						pro.getColor(),pro.getSize(),
        						new SimpleDateFormat("yyyy-MM-dd").format(currentTime),pro.getSalesType()});
        	}
        	for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
        		LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
        		PayWay payway = (PayWay) item.getTag();
        		EditText editText = (EditText) item.getChildAt(2);
        		if(editText.getText().length() > 0){
        			if(editText.getText().toString().indexOf(".") > 0){
        				float moneyp = Float.parseFloat(editText.getText().toString());
        				//if(moneyp > 0){
        					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
        							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString(),uuid});
        				//}
        			}else{
        				int moneyt = Integer.parseInt(editText.getText().toString());
        				//if(moneyt > 0){
        					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
        							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString()+".00",uuid});
        				//}
        			}
        		}
        	}
            db.setTransactionSuccessful();
        } catch(Exception e){}
        finally {  
            db.endTransaction();
        } 
	}
	
	public void update(){
		db.beginTransaction();
		Cursor c = null;
        try {
        	Date currentTime = new Date();
        	db.execSQL("update c_settle set 'settleTime' = ?,'type' = ?,'count' = ?,"
        			+ "'money' = ?,"
        			+ "'status' = ?,'settleDate' = ?,'settleMonth' = ? "
        			+ " where settleUUID = ?",
					new Object[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime),
								getResources().getString(R.string.sales_settle_type),
								count,
								realityET.getText(),
								getResources().getString(R.string.sales_settle_noup),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime),
								new SimpleDateFormat("yyyy-MM-dd").format(currentTime).substring(0, 7),
								command});
        	
        	for(Product pro : products){
        		if(pro.getUuid() != null){
	        		db.execSQL("update c_settle_detail "
	        				+ "set 'price' = ?,'discount' = ?,'settleDate' = ?,"
	        				+ "'count' = ?,'money' = ?,'salesType' = ? where settleUUID = ? and _id = ?",
	    					new Object[]{pro.getPrice(), pro.getDiscount(), 
	        						new SimpleDateFormat("yyyy-MM-dd").format(currentTime),
	        						pro.getCount(), pro.getMoney(), pro.getSalesType(),command, pro.getId()});
        		}else{
            		db.execSQL("insert into c_settle_detail('style','barcode','price','discount'"
            				+ ",'count','money','settleUUID','pdtname','color','size','settleDate','salesType')"
            				+ " values(?,?,?,?,?,?,?,?,?,?,?,?)",
        					new Object[]{pro.getStyle(),pro.getBarCode(),pro.getPrice(), pro.getDiscount(),
            						pro.getCount(), pro.getMoney(), command, pro.getName(),
            						pro.getColor(),pro.getSize(),
            						new SimpleDateFormat("yyyy-MM-dd").format(currentTime),pro.getSalesType()});
        		}
        	}
        	c = db.rawQuery("select * from c_payway_detail where settleUUID = ?", new String[]{command});
        	for(int i = 0; i < mPaywayLayout.getChildCount(); i++){
        		LinearLayout item = (LinearLayout) mPaywayLayout.getChildAt(i);
        		PayWay payway = (PayWay) item.getTag();
        		EditText editText = (EditText) item.getChildAt(2);
        		if(editText.getText().length() > 0){
        			while(c.moveToNext()){
        				if(payway.getId() == c.getInt(c.getColumnIndex("paywayID"))){
        					db.execSQL("update c_payway_detail set money = ? "
                					+ "where _id = ? and settleUUID = ?", 
                					new Object[]{editText.getText().toString(),c.getInt(c.getColumnIndex("_id")),command});
        				}else{
        					if(editText.getText().toString().indexOf(".") > 0){
                				float moneyp = Float.parseFloat(editText.getText().toString());
                				if(moneyp > 0){
                					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
                							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString(),command});
                				}
                			}else{
                				int moneyt = Integer.parseInt(editText.getText().toString());
                				if(moneyt > 0){
                					db.execSQL("insert into c_payway_detail('paywayID','name','money','settleUUID')"
                							+" values(?,?,?,?)",new Object[]{payway.getId(),payway.getPayWay(),editText.getText().toString(),command});
                				}
                			}
        				}
        			}
        			
        		}
        	}
            db.setTransactionSuccessful();
        } catch(Exception e){}
        finally {  
            db.endTransaction();
            if(c != null && !c.isClosed())
            	c.close();
        } 
	}
	
    private String getNo() {
        StringBuffer sb = new StringBuffer();
        sb.append("SA305152");
        sb.append(new SimpleDateFormat("yyMMdd").format(new Date()));

        // 保存checkNo至SP
        int checkNo = App.getPreferenceUtils().getPreferenceInt("salesNo");
        int i = 0;
        if (checkNo > 0) {
            i = checkNo + 1;
        } else {
            i = 1;
        }
        App.getPreferenceUtils().savePreferenceInt("salesNo", i);

        StringBuffer finalCheckNo = new StringBuffer();
        for (int j = 0; j < 5 - String.valueOf(i).length(); j++) {
            finalCheckNo.append(0);
        }
        finalCheckNo.append(i);
        sb.append(finalCheckNo.toString());
        return sb.toString();
    }

}
