package cn.burgeon.core.ui.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.MemberSearchAdapter;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.bean.Member;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.ui.sales.SalesNewOrderActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

import com.android.volley.Response;

public class MemberSearchActivity extends BaseActivity {
	
	ListView mListView;
	Button addBtn,queryBtn,confirmBtn;
	EditText cardNoET,mobileET;
	MemberSearchAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_member_search);
		
		init();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}

	private void init() {
	     // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);
        
        cardNoET = (EditText) findViewById(R.id.memberSearchCardNoET);
        cardNoET.setOnEditorActionListener(editorActionListener);
        mobileET = (EditText) findViewById(R.id.memberSearchPhoneET);
        mobileET.setOnEditorActionListener(editorActionListener);
		addBtn = (Button) findViewById(R.id.memberNewBtn);
		addBtn.setOnClickListener(onClickListener);
		queryBtn = (Button) findViewById(R.id.memberSearchBtn);
		queryBtn.setOnClickListener(onClickListener);
		confirmBtn = (Button) findViewById(R.id.memberSearchConfirmBtn);
		confirmBtn.setOnClickListener(onClickListener);
		mListView = (ListView) findViewById(R.id.memberSearchLV);
		//mAdapter = new MemberSearchAdapter(postRequest(), this);
		//mListView.setAdapter(mAdapter);
		//mListView.setOnItemSelectedListener(OnItemSelectedListener);
	}
	
	OnEditorActionListener editorActionListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			switch (actionId) {
			case EditorInfo.IME_ACTION_SEARCH:
				postRequest();
				break;

			default:
				break;
			}
			return true;
		}
	};

	private void postRequest() {
		viaLocal();
		//viaNet();
	}
	
	public List<Member> query(){
		List<Member> data = new ArrayList<Member>();
		Member member = null;
		Cursor c = null;
		if(cardNoET.getText().length() > 0 && mobileET.getText().length() > 0)
			c = db.rawQuery("select * from c_vip where cardno = ? or mobile = ?",
					new String[]{cardNoET.getText().toString(),mobileET.getText().toString()});
		else if(cardNoET.getText().length() > 0 && mobileET.getText().length() == 0)
			c = db.rawQuery("select * from c_vip where cardno = ?",new String[]{cardNoET.getText().toString()});
		else if(cardNoET.getText().length() == 0 && mobileET.getText().length() > 0)
			c = db.rawQuery("select * from c_vip where mobile = ?",new String[]{mobileET.getText().toString()});
		Log.d("MemeberSearch", "size=" + c.getCount());
		while(c.moveToNext()){
			member = new Member();
			//member.setId(c.getInt(c.getColumnIndex("_ID")));
			member.setCardNum(c.getString(c.getColumnIndex("cardno")));
			member.setName(c.getString(c.getColumnIndex("name")));
			member.setiDentityCardNum(c.getString(c.getColumnIndex("idno")));
			member.setPhoneNum(c.getString(c.getColumnIndex("mobile")));
			member.setBirthday(c.getString(c.getColumnIndex("birthday")));
			member.setEmployee(c.getString(c.getColumnIndex("employee")));
			member.setEmail(c.getString(c.getColumnIndex("email")));
			member.setCreateCardDate(c.getString(c.getColumnIndex("createTime")));
			member.setType(c.getString(c.getColumnIndex("type")));
			member.setSex(Integer.toString(c.getInt(c.getColumnIndex("sex"))));
			member.setDiscount(c.getString(c.getColumnIndex("discount")));
			data.add(member);
		}
		if(member != null)
			selectedMember = member;
		if(c != null && !c.isClosed())
			c.close();
		return data;
	}

	private void viaLocal() {
		mAdapter = new MemberSearchAdapter(query(), MemberSearchActivity.this);
		mListView.setAdapter(mAdapter);
	}

	private void viaNet() {
		try {
			Map<String, String> params = new HashMap<String, String>();
            JSONArray array = new JSONArray();

            JSONObject transactions = new JSONObject();
            transactions.put("id", 112);
            transactions.put("command", "Query");

            JSONObject paramsTable = new JSONObject();
            paramsTable.put("table", "12899");
            paramsTable.put("columns", new JSONArray().put("cardno").put("vipname").put("birthday"));
            JSONObject paramsCombine = new JSONObject();
            paramsCombine.put("combine", "or");
            JSONObject expr1JO = new JSONObject();
            expr1JO.put("column", "cardno");
            expr1JO.put("condition", cardNoET.getText());


            paramsCombine.put("expr1", expr1JO);
            JSONObject expr2JO = new JSONObject();
            expr2JO.put("column", "mobil");
            expr2JO.put("condition", mobileET.getText());
            paramsCombine.put("expr2", expr2JO);
            paramsTable.put("params", paramsCombine);

            transactions.put("params", paramsTable);
            array.put(transactions);
            params.put("transactions", array.toString());
			sendRequest(params,new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d("zhang.h", response);
					mAdapter = new MemberSearchAdapter(parseResult(response), MemberSearchActivity.this);
					mListView.setAdapter(mAdapter);
				}
			});
		} catch (JSONException e) {}
	}
	
	private List<Member> parseResult(String result){
		List<Member> data = new ArrayList<Member>();
		try {
			JSONArray array = new JSONArray(result);
			JSONObject obj = array.getJSONObject(0);
			JSONArray rows = obj.getJSONArray("rows");
			Member member = null;
			for(int i = 0; i < rows.length(); i++){
				String row = rows.get(i).toString();
				String[] rowArr = row.split(",");
				
				member = new Member();
				member.setCardNum(rowArr[0].substring(2,rowArr[0].length()-1));
				member.setName(rowArr[1].replace("\"", ""));
				member.setBirthday(rowArr[2].substring(1,rowArr[2].length()-2));
				
				intentValue = member.getCardNum()+ "\\100";
				data.add(member);
			}
		} catch (JSONException e) {
			Log.d("MemberListActivity", e.toString());
		}
		return data;
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.memberNewBtn:
				forwardActivity(MemberRegistActivity.class, "from","search");
				break;
			case R.id.memberSearchBtn:
				postRequest();
				break;
			case R.id.memberSearchConfirmBtn:
				Intent intent = new Intent(MemberSearchActivity.this,SalesNewOrderActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("searchedMember", selectedMember);
				intent.putExtras(bundle);
                startActivity(intent);
				break;
			default:
				break;
			}
		}
	}; 
	 
	String intentValue;
	Member selectedMember = null;
	
	OnItemSelectedListener OnItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			selectedMember = (Member) parent.getAdapter().getItem(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
}
