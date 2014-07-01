package cn.burgeon.core.ui.allot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.AllotOutQueryLVAdapter;
import cn.burgeon.core.bean.AllotOut;
import cn.burgeon.core.bean.AllotOutQuery;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

import com.android.volley.Response;

public class AllotOutQueryActivity extends BaseActivity implements OnClickListener {

	private EditText startDateET, endDateET;
	private ListView allotoutqueryLV;
	private TextView recodeNumTV;
	private Button queryBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_allot_out_query);

		init();

		initLVData();
	}

	private void init() {
		// 初始化门店信息
		TextView storeTV = (TextView) findViewById(R.id.storeTV);
		storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

		TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
		currTimeTV.setText(getCurrDate());

        startDateET = (EditText) findViewById(R.id.startDateET);
        startDateET.setOnClickListener(this);
        endDateET = (EditText) findViewById(R.id.endDateET);
        endDateET.setOnClickListener(this);

		HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
		ViewGroup.LayoutParams params = hsv.getLayoutParams();
		params.height = (int) ScreenUtils.getAllotInLVHeight(this);

		allotoutqueryLV = (ListView) findViewById(R.id.allotoutqueryLV);
		recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);

		queryBtn = (Button) findViewById(R.id.queryBtn);
		queryBtn.setOnClickListener(this);
	}

	private void initLVData() {

	}

    Calendar c = Calendar.getInstance();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.startDateET:
            int startmYear = c.get(Calendar.YEAR);
            int startmMonth = c.get(Calendar.MONTH);
            int startmDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog startdialog = new DatePickerDialog(AllotOutQueryActivity.this, new startmDateSetListener(), startmYear, startmMonth, startmDay);
            startdialog.show();
            break;
        case R.id.endDateET:
            int endmYear = c.get(Calendar.YEAR);
            int endmMonth = c.get(Calendar.MONTH);
            int endmDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog enddialog = new DatePickerDialog(AllotOutQueryActivity.this, new endmDateSetListener(), endmYear, endmMonth, endmDay);
            enddialog.show();
            break;
		case R.id.queryBtn:
			/*
			 * { "table":"M_TRANSFER", "columns":["DOCNO","DATEOUT"], "params":{"column":"DATEOUT","condition":"20091217~20091219"} }
			 */
			Map<String, String> params = new HashMap<String, String>();
			JSONArray array = new JSONArray();
			try {
				JSONObject transactions = new JSONObject();
				transactions.put("id", 112);
				transactions.put("command", "Query");

				JSONObject paramsTable = new JSONObject();
				paramsTable.put("table", "M_TRANSFER");
				paramsTable.put("columns", new JSONArray().put("ID").put("DOCNO").put("BILLDATE").put("C_DEST_ID").put("TOT_QTYOUT"));
				JSONObject paramsCondition = new JSONObject();
				paramsCondition.put("column", "DATEOUT");
				paramsCondition.put("condition", startDateET.getText() + "~" + endDateET.getText());
				paramsTable.put("params", paramsCondition);

				transactions.put("params", paramsTable);
				array.put(transactions);
				params.put("transactions", array.toString());

				sendRequest(params, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// 取消进度条
						stopProgressDialog();

						ArrayList<AllotOutQuery> lists = new ArrayList<AllotOutQuery>();
						try {
							JSONArray resJA = new JSONArray(response);
							JSONObject resJO = resJA.getJSONObject(0);
							JSONArray rowsJA = resJO.getJSONArray("rows");
							int len = rowsJA.length();

							for (int i = 0; i < len; i++) {
								// [2924,"TF0912140000163",20091214,3865,13]
								String currRow = rowsJA.get(i).toString();
								String[] currRows = currRow.split(",");

								AllotOutQuery allotOutQuery = new AllotOutQuery();
								allotOutQuery.setID(currRows[0].substring(1, currRows[0].length()));
								allotOutQuery.setDOCNO(currRows[1].substring(1, currRows[1].length() - 1));
								allotOutQuery.setBILLDATE(currRows[2]);
								allotOutQuery.setC_DEST_ID(currRows[3]);
								allotOutQuery.setTOT_QTYOUT(currRows[4].substring(0, currRows[4].length() - 1));
								lists.add(allotOutQuery);
							}

							// 记录数
							recodeNumTV.setText(lists.size() + "条记录");

							AllotOutQueryLVAdapter mAdapter = new AllotOutQueryLVAdapter(AllotOutQueryActivity.this, lists,
									R.layout.allot_out_query_item);
							allotoutqueryLV.setAdapter(mAdapter);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}

    class startmDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            // Month is 0 based so add 1

            String month = String.valueOf(mMonth + 1).length() == 2 ? String.valueOf(mMonth + 1) : "0" + String.valueOf(mMonth + 1);
            String day = String.valueOf(mDay).length() == 2 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            startDateET.setText(new StringBuilder().append(mYear).append(month).append(day));
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
            endDateET.setText(new StringBuilder().append(mYear).append(month).append(day));
        }
    }
}
