package cn.burgeon.core.ui.member;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.bean.Member;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.ui.LoginActivity;
import cn.burgeon.core.ui.sales.SalesNewOrderActivity;
import cn.burgeon.core.ui.sales.SalesSettleActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.widget.UndoBarController;
import cn.burgeon.core.widget.UndoBarController.UndoListener;
import cn.burgeon.core.widget.UndoBarStyle;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class MemberRegistActivity extends BaseActivity {
	
	private final String TAG = "MemberRegistActivity";
	Button saveBtn,veryfiyBtn;
	EditText cardNOET,nameET,identityET,emailET;
	EditText createDateET,mobilePhoneET,birthdayET;
	Spinner typeSp,employeeSP;
	RadioGroup radioGroup;
	int _id = -1;
	String from = "";
	boolean isVerifyed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_member_regist);
		
		init();
		
		Intent intent = getIntent();
		if(intent != null){
			_id = intent.getIntExtra("_id", -1);
			Log.d("MemberRegistActivity", "_id=" +_id);
			if(-1 != _id)
				getMemberInfo();
			from = intent.getStringExtra("from");
			if("search".equals(from)){
				saveBtn.setText(getString(R.string.useNewMember));
			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		_id = intent.getIntExtra("_id", -1);
		Log.d("MemberRegistActivity", "_id=" +_id);
		getMemberInfo();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
	
	private void getMemberInfo() {
		query();
	}

	private void init() {      
		saveBtn = (Button) findViewById(R.id.memberRegistSaveBtn);
		veryfiyBtn = (Button) findViewById(R.id.memberRegistVerifyBtn);
		cardNOET = (EditText) findViewById(R.id.memberRegistCardNumET);
		nameET = (EditText) findViewById(R.id.memberRegistCardNameET);
		identityET = (EditText) findViewById(R.id.memberRegistIdentityNumET);
		createDateET = (EditText) findViewById(R.id.memberRegistCreateDateET);
		mobilePhoneET = (EditText) findViewById(R.id.memberRegistPhoneNumET);
		birthdayET = (EditText) findViewById(R.id.memberRegistBirthdayET);
		birthdayET.setOnClickListener(mOnclickListener);
		radioGroup = (RadioGroup) findViewById(R.id.memberRegistRG);
		emailET = (EditText) findViewById(R.id.memberRegistEmailET);
		employeeSP = (Spinner) findViewById(R.id.memberRegistSalesAssistantSP);
		ArrayAdapter<String> epadapter = new ArrayAdapter<String>(MemberRegistActivity.this, android.R.layout.simple_spinner_item, getEmployees());
		epadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		employeeSP.setAdapter(epadapter);
		SimpleAdapter adapter = new SimpleAdapter(MemberRegistActivity.this, 
				fetchData(), 
				android.R.layout.simple_spinner_item, 
				new String[]{"key"}, new int[]{android.R.id.text1});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSp = (Spinner) findViewById(R.id.memberRegistVipTypeSP);
		typeSp.setAdapter(adapter);
		saveBtn.setOnClickListener(mOnclickListener);
		veryfiyBtn.setOnClickListener(mOnclickListener);
	}
	
    private String[] getEmployees() {
		Cursor c = db.rawQuery("select name from employee",null);
		if(c!=null){
			int i = 0;
			String[] employees = new String[c.getCount()];
			while(c.moveToNext()){
				employees[i++] = c.getString(c.getColumnIndex("name"));
			}
			if(!c.isClosed()) c.close();
			return employees;
		}
		return new String[]{};
	}
	
	private List<HashMap<String, String>> fetchData() {
		HashMap<String, String> item = null;
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		Cursor c = db.rawQuery("select name, discount from tc_vip", null);
		while(c.moveToNext()){
			item = new HashMap<String, String>();
			item.put("key", c.getString(c.getColumnIndex("name")));
			item.put("value", c.getString(c.getColumnIndex("discount")));
			data.add(item);
		}
		if(c != null && !c.isClosed()) c.close();
		Log.d(TAG, "list size:" + data.size());
		return data;
	}


	class VipType{
		String name;
		String discount;
		public VipType(String name, String discount) {
			super();
			this.name = name;
			this.discount = discount;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	OnClickListener mOnclickListener = new OnClickListener() {
	
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.memberRegistSaveBtn:
				
				if(-1 != _id){
					update();
					UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		        	UndoBarController.show(MemberRegistActivity.this, "更新会员成功", null, MESSAGESTYLE);
				}else{
					save();
				}
				
	        	break;
			case R.id.memberRegistVerifyBtn:
				InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
					
				if(cardNOET.getText().length() == 0){
					UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		        	UndoBarController.show(MemberRegistActivity.this, "请输入卡号", null, MESSAGESTYLE);
					return;
				}
				verifyNet();
				break;
			case R.id.memberRegistBirthdayET:
				Calendar c = Calendar.getInstance();
				 int startmYear = c.get(Calendar.YEAR);
			     int startmMonth = c.get(Calendar.MONTH);
			     int startmDay = c.get(Calendar.DAY_OF_MONTH);
			     DatePickerDialog startdialog = new DatePickerDialog(MemberRegistActivity.this, new startmDateSetListener(), startmYear, startmMonth, startmDay);
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
            birthdayET.setText(new StringBuilder().append(mYear).append(month).append(day));
        }
    }
	
    String phoneRegExp = "^[1]([3][0-9]{1}|50|59|58|88|89)[0-9]{8}$";
    String identityRegExp = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$";
	
	private boolean isRequired(Editable src){
		return src.length() > 0;
	}
	
	private boolean validate(){//31011519850322
		if(!isRequired(cardNOET.getText())){   
			UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
        	UndoBarController.show(MemberRegistActivity.this, "卡号不能为空", null, MESSAGESTYLE);
        	return false;
		}else if(!isRequired(nameET.getText())){
			UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
        	UndoBarController.show(MemberRegistActivity.this, "姓名不能为空", null, MESSAGESTYLE);
        	return false;
		}else if(!Pattern.compile(phoneRegExp).matcher(mobilePhoneET.getText()).find()){
			UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
        	UndoBarController.show(MemberRegistActivity.this, "手机号码不正确", null, MESSAGESTYLE);
        	return false;
		}else if(isRequired(identityET.getText())){
			if(!Pattern.compile(identityRegExp).matcher(identityET.getText()).find()){
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
	        	UndoBarController.show(MemberRegistActivity.this, "身份证号码不正确", null, MESSAGESTYLE);
	        	return false;
			}
			if(!verifyDate(identityET.getText().toString())){
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
	        	UndoBarController.show(MemberRegistActivity.this, "身份证中出生日期不正确", null, MESSAGESTYLE);
	        	return false;
			}
		}else if(!birthday()){
			UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
        	UndoBarController.show(MemberRegistActivity.this, "出生日期和身份证不匹配", null, MESSAGESTYLE);
        	return false;
		}else if(!isVerifyed){
			UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
        	UndoBarController.show(MemberRegistActivity.this, "卡号尚未验证通过", null, MESSAGESTYLE);
        	return false;
		}
		return true;
	}
	
	private boolean birthday(){
		if(birthdayET.getText().length() == 0) return true;
		String birthday = birthdayET.getText().length() > 0?birthdayET.getText().toString():"";
		String identity = identityET.getText().toString();
		String idno = identity.substring(0,8);
		return idno.equals(birthday);
	}
	
	Member vip = null;
	public void save(){
		if(validate()){
			saveBtn.setClickable(false);
			vip = new Member();
	        vip.setCardNum(cardNOET.getText().toString().trim());
	        vip.setName(nameET.getText().toString().trim());
	        vip.setiDentityCardNum(identityET.getText().toString().trim());
	        vip.setPhoneNum(mobilePhoneET.getText().toString().trim());
	        vip.setSex(radioGroup.getCheckedRadioButtonId()==R.id.radioMale?getResources().getString(R.string.male):getResources().getString(R.string.female));
	        vip.setEmail(emailET.getText().toString().trim());
	        vip.setEmployee(employeeSP.getSelectedItem().toString().trim());   
	        vip.setBirthday(birthdayET.getText().toString().trim());
	        vip.setType(((HashMap<String, String>)typeSp.getAdapter().getItem(typeSp.getSelectedItemPosition())).get("key"));
	        vip.setDiscount(((HashMap<String, String>)typeSp.getAdapter().getItem(typeSp.getSelectedItemPosition())).get("value"));
			/*if("search".equals(from)){
				vip.setStatus(getString(R.string.sales_settle_hasup));
		        startProgressDialog();
	        	uploadV1ip(vip);
			}else{
				vip.setStatus(getString(R.string.sales_settle_noup));
				insert(vip);
		        popToast();
			}*/
			vip.setStatus(getString(R.string.sales_settle_noup));
			insert(vip);
			if(!networkReachable()){
				popToast();
			}else{
				uploadV1ip(vip);
			}
			
		}
	}

	private void popToast() {
		UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		UndoBarController.show(MemberRegistActivity.this, "注册会员成功", new UndoListener() {
									@Override
									public void onUndo(Parcelable token) {
							        		forwardActivity(MemberListActivity.class);
									}
								}, MESSAGESTYLE);
	}
	
	private void insert(Member vip){
		try {
			db.beginTransaction();
        	db.execSQL("insert into c_vip('cardno','name','idno','mobile','sex','email','birthday','createTime','employee','type','status','discount','customerID')"+
        				" values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[]{vip.getCardNum(),
								vip.getName(),
								vip.getiDentityCardNum(),
								vip.getPhoneNum(),
								vip.getSex(),
								vip.getEmail(),
								vip.getBirthday(),
								new SimpleDateFormat("yyyyMMdd").format(new Date()),
								vip.getEmployee(),
								vip.getType(),
								vip.getStatus(),
								vip.getDiscount(),
								App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.customerid)});
            db.setTransactionSuccessful();
        } finally {  
            db.endTransaction();
        }
	}
	
	private void uploadV1ip(Member vip){
		startProgressDialog();
		Map<String,String> params = new HashMap<String, String>();
		JSONArray array;
		JSONObject transactions;
		try {
			array = new JSONArray();
			transactions = new JSONObject();
			transactions.put("id", 112);
			transactions.put("command", "ObjectCreate");
			
			//第一个params
			JSONObject paramsInTransactions = new JSONObject();
			paramsInTransactions.put("table", 12899);
			paramsInTransactions.put("CARDNO",vip.getCardNum());
			paramsInTransactions.put("C_VIPTYPE_ID__NAME",vip.getType());
			paramsInTransactions.put("C_CUSTOMER_ID__NAME",App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.agency_key));
			paramsInTransactions.put("C_STORE_ID__NAME",App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
			paramsInTransactions.put("HR_EMPLOYEE_ID__NAME",vip.getEmployee());
			paramsInTransactions.put("VIPNAME",vip.getName());
			paramsInTransactions.put("MOBIL",vip.getPhoneNum());
			paramsInTransactions.put("IDNO",vip.getiDentityCardNum());
			paramsInTransactions.put("SEX",vip.getSex());
			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			Log.d(TAG, array.toString());
			params.put("transactions", array.toString());
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d(TAG, response);
					RequestResult result = parseResult2(response);
					//请求成功，更新记录状态和销售单号
					if("0".equals(result.getCode())){
						Message msg = handler.obtainMessage();
						msg.obj = result;
						msg.what = 2;
						handler.dispatchMessage(msg);
					}else{
						stopProgressDialog();
						UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
						UndoBarController.show(MemberRegistActivity.this, result.getMessage(), null, MESSAGESTYLE);
					}
				}
			},new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					stopProgressDialog();
					UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
					UndoBarController.show(MemberRegistActivity.this, "网络异常，上传失败", null, MESSAGESTYLE);
				}
			});
		} catch (JSONException e) {}
	}
	
	private RequestResult parseResult2(String response) {
		RequestResult result = null;
	    try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			result = new RequestResult(resJO.getString("code"), resJO.getString("message"));
		} catch (JSONException e) {}
		return result;
	}
	
	private void update() {
		if(validate()){
			db.beginTransaction();
	        try {
	        	db.execSQL("update c_vip set 'cardno'=?,'name'=?,'idno'=?,'mobile'=?,'sex'=?,"
	        			+ "'email'=?,'birthday'=?,'createTime'=?,'employee'=?,'type'=?,'discount'=?"+
	        				" where _id = ?",
						new Object[]{cardNOET.getText().toString().trim(),
									nameET.getText().toString().trim(),
									identityET.getText().toString().trim(),
									mobilePhoneET.getText().toString().trim(),
									radioGroup.getCheckedRadioButtonId()==R.id.radioMale?getResources().getString(R.string.male):getResources().getString(R.string.female),
									emailET.getText().toString().trim(),
									birthdayET.getText().toString().trim(),
									new SimpleDateFormat("yyyyMMdd").format(new Date()),
									App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key),
									((HashMap<String, String>)typeSp.getAdapter().getItem(typeSp.getSelectedItemPosition())).get("key"),
									((HashMap<String, String>)typeSp.getAdapter().getItem(typeSp.getSelectedItemPosition())).get("value"),
									_id
									});
	            db.setTransactionSuccessful();
	        } finally {  
	            db.endTransaction();
	        }
		}
	}
	
	public void query(){
		Cursor c = db.rawQuery("select * from c_vip where _id = ?", new String[]{String.valueOf(_id)});
		Log.d("zhang.h", "record number::::::::;" + c.getCount());
		while(c.moveToNext()){
			cardNOET.setText(c.getString(c.getColumnIndex("cardno")));
			nameET.setText(c.getString(c.getColumnIndex("name")));
			identityET.setText(c.getString(c.getColumnIndex("idno")));
			mobilePhoneET.setText(c.getString(c.getColumnIndex("mobile")));
			radioGroup.check(getResources().getString(R.string.male).equals(c.getString(c.getColumnIndex("sex")))?R.id.radioMale:R.id.radioFemale);
			emailET.setText(c.getString(c.getColumnIndex("email")));
			birthdayET.setText(c.getString(c.getColumnIndex("birthday")));
			createDateET.setText(c.getString(c.getColumnIndex("createTime")));
		}
	}
	
	private boolean verifyNet(){
		isVerifyed = false;
		Map<String,String> params = new HashMap<String, String>();
		JSONArray array;
		JSONObject transactions;
		try {
			array = new JSONArray();
			transactions = new JSONObject();
			transactions.put("id", 112);
			transactions.put("command", "Query");
			JSONObject paramsInTransactions = new JSONObject();
			paramsInTransactions.put("table", 12899);
			
			//查询条件的params
			JSONObject queryParams = new JSONObject();
			queryParams.put("column", "cardno");
			queryParams.put("condition", "="+cardNOET.getText().toString().trim());
			paramsInTransactions.put("params", queryParams);
			
			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			Log.d(TAG, array.toString());
			params.put("transactions", array.toString());
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d(TAG, response);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = response;
					handler.dispatchMessage(msg);
				}
			});
		} catch (JSONException e) {}

		return false;
	}
	
	private boolean parseResult(String response) {
	    try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			JSONArray rowsJA = resJO.getJSONArray("rows");
			return rowsJA.length() > 0;
		} catch (JSONException e) {}
		return false;
	}

	private boolean verifyLocal() {
		boolean flag = false;
		Cursor c = db.rawQuery("select * from c_vip where cardno = ?", new String[]{cardNOET.getText().toString()});
		if(c.moveToFirst())
			flag = true;
		c.close();
		return flag;
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String response = (String) msg.obj;
				stopProgressDialog();
				if(parseResult(response)){
					isVerifyed = false;
					UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
		        	UndoBarController.show(MemberRegistActivity.this, "对不起，此卡号已被使用", null, MESSAGESTYLE);
				}else{
					if(verifyLocal()){
						isVerifyed = false;
						UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
			        	UndoBarController.show(MemberRegistActivity.this, "对不起，此卡号已被使用", null, MESSAGESTYLE);
					}else{
						isVerifyed = true;
						UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
			        	UndoBarController.show(MemberRegistActivity.this, "此卡号可以使用", null, MESSAGESTYLE);
					}
				}
				break;
			case 2:
				saveBtn.setClickable(true);
				updateStatus();
                stopProgressDialog();
				RequestResult result =  (RequestResult) msg.obj;
				UndoBarStyle MESSAGESTYLE = new UndoBarStyle(-1, -1, 2000);
	    	    UndoBarController.show(MemberRegistActivity.this, result.getMessage(), new UndoListener() {
				@Override
				public void onUndo(Parcelable token) {
					if("search".equals(from)){
						Member member = new Member();
						member.setCardNum(cardNOET.getText().toString());
						member.setDiscount(((HashMap<String, String>)typeSp.getAdapter().getItem(typeSp.getSelectedItemPosition())).get("value"));
						Intent intent = new Intent(MemberRegistActivity.this,SalesNewOrderActivity.class);
						Bundle bundle = new Bundle();
						bundle.putParcelable("searchedMember", member);
						intent.putExtras(bundle);
		                startActivity(intent);
		        		}
				}
			}, MESSAGESTYLE);
				break;
			default:
				break;
			}
			
		}
	};
	
	private void updateStatus() {
		db.beginTransaction();
        try {
        	db.execSQL("update c_vip set status = ? where cardno = ?",
					new Object[]{
								getResources().getString(R.string.sales_settle_hasup),
								vip.getCardNum()});
            db.setTransactionSuccessful();
        } catch(Exception e){}
        finally {  
            db.endTransaction();
        } 
	}
	
	public boolean verifyDate(String eighteencardid) {
		String date = eighteencardid.substring(6, 10);
		date += "-";
		date += eighteencardid.substring(10, 12);
		date += "-";
		date += eighteencardid.substring(12, 14);
		// System.out.println(date);
		// 判断年月日的正则表达式，接受输入格式为2010-12-24，可接受平年闰年的日期
		String v = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
		Pattern p = Pattern.compile(v);
		Matcher m = p.matcher(date);
		return m.matches();
	}
	
}
