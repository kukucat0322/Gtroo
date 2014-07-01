package cn.burgeon.core.ui.allot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.AllotOutApplyLVAdapter;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

public class AllotOutApplyActivity extends BaseActivity implements OnClickListener {

    private ListView allotoutapplyLV;
    private TextView recodeNumTV, totalCountTV;
    private EditText shipperET;
    private EditText descET, barcodeET;
    private Button uploadBtn, okBtn;

    private ArrayList<Product> data = new ArrayList<Product>();
    private AllotOutApplyLVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allot_out_apply);

        init();

        initLVData();
    }

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());
        
        shipperET = (EditText) findViewById(R.id.shipperET);
        descET = (EditText) findViewById(R.id.descET);

        barcodeET = (EditText) findViewById(R.id.barcodeET);
        barcodeET.setOnEditorActionListener(editorActionListener);

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);

        allotoutapplyLV = (ListView) findViewById(R.id.allotoutapplyLV);
        mAdapter = new AllotOutApplyLVAdapter(AllotOutApplyActivity.this, data, R.layout.allot_out_apply_item);
        allotoutapplyLV.setAdapter(mAdapter);
        recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);
        totalCountTV = (TextView) findViewById(R.id.totalCountTV);

        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(this);

        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(this);
    }

    private void initLVData() {
        // nothing
    }

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                	if (barcodeET.getText() != null && barcodeET.getText().toString().length() > 0) {
                        verifyBarCode(barcodeET.getText().toString());
                    }
                    break;
            }
            return true;
        }
    };

    private void verifyBarCode(String barcodeText) {
        //从本地获取
        varLocal(barcodeText);

        //从网络获取
        //varNet();
    }

    private void varLocal(String barcodeText) {
        String sql = "select a.style, b.style_name,c.clrname,d.sizename,e.fprice"
                + " from tc_sku as a"
                + " left join tc_style as b"
                + " on a.style = b.style"
                + " left join tdefclr as c"
                + " on a.clr = c.clr"
                + " left join tdefsize as d"
                + " on a.sizeid = d.sizeid"
                + " left join tc_styleprice as e"
                + " on a.style = e.style"
                + " where a.sku = ?";
        Cursor c = db.rawQuery(sql, new String[]{barcodeText});
        if (c.moveToFirst()) {
        	Product currProduct = parseSQLResult(c);
            // 生成List
            generateList(currProduct);
            // 刷新列表
            mAdapter.notifyDataSetChanged();
            upateBottomBarInfo();
        }
        if(c != null && !c.isClosed())
            c.close();
    }

    private Product parseSQLResult(Cursor c) {
        Product pro = new Product();
        pro.setBarCode(barcodeET.getText().toString());
        pro.setStyle(c.getString(c.getColumnIndex("style")));
        pro.setColor(c.getString(c.getColumnIndex("clrname")));
        pro.setSize(c.getString(c.getColumnIndex("sizename")));
        pro.setPrice(c.getString(c.getColumnIndex("fprice")));
        pro.setCount("1");
        return pro;
    }

    private void generateList(Product currProduct) {
        if (data.size() > 0) {
            boolean isFlag = false;
            for (Product product : data) {
                // 同一件
                if (currProduct.getBarCode().equals(product.getBarCode())) {
                    isFlag = true;
                    product.setCount(String.valueOf(Integer.valueOf(product.getCount()) + 1));
                }
            }

            if (!isFlag) {
                data.add(currProduct);
            }
        } else {
            data.add(currProduct);
        }
    }
    
    private void upateBottomBarInfo() {
        int count = 0;
        for (Product pro : data) {
            count += Integer.parseInt(pro.getCount());
        }
        totalCountTV.setText(String.format(getResources().getString(R.string.sales_new_common_count), count));
        recodeNumTV.setText(String.format(getResources().getString(R.string.sales_new_common_record), data.size()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okBtn:
            	if (barcodeET.getText() != null && barcodeET.getText().toString().length() > 0) {
            		verifyBarCode(barcodeET.getText().toString());
            	}
                break;
            case R.id.uploadBtn:
                db.beginTransaction();
                try {
                    String uuid = UUID.randomUUID().toString();
                    Date currentTime = new Date();
                    db.execSQL("insert into c_allot_out('dj_no','upload_status','dj_status','dj_date','in_store','num','checkUUID')" +
                                    " values(?,?,?,?,?,?,?)",
                            new Object[]{
                                    getDJNo(),
                                    "已上传",
                                    "已完成",
                                    new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentTime),
                                    shipperET.getText(),
                                    data.size(),//数量count
                                    uuid
                            }
                    );
                    for (Product pro : data) {
                        db.execSQL("insert into c_allot_out_detail('checkUUID','fahuofang','remark','barcode','num','color','size','price','style') " +
                                        "values (?,?,?,?,?,?,?,?,?)",
                                new Object[]{uuid, shipperET.getText(), descET.getText(), pro.getBarCode(), pro.getCount(), pro.getColor(), pro.getSize(), pro.getPrice(), pro.getStyle()}
                        );
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                
                // 清除
                data.clear();
                
                finish();
                break;
        /*
        case R.id.okBtn:
			{
			   "table":"12668",
			   "columns":[
			              "NO",
			              "M_PRODUCT_ID;COLORS",
			              "M_PRODUCT_ID;SIZES",
			              "M_PRODUCT_ID;PRICELIST",
			              "M_PRODUCT_ID;NAME"
			   ],
			   "params":{"column":"NO","condition":"109454D334620"}
			}
			Map<String, String> params = new HashMap<String, String>();
            JSONArray array = new JSONArray();
            try {
                JSONObject transactions = new JSONObject();
                transactions.put("id", 112);
                transactions.put("command", "Query");

                JSONObject paramsTable = new JSONObject();
                paramsTable.put("table", "12668");
                paramsTable.put("columns", new JSONArray().put("NO")
                                .put("M_PRODUCT_ID;COLORS")
                                .put("M_PRODUCT_ID;SIZES")
                                .put("M_PRODUCT_ID;PRICELIST")
                                .put("M_PRODUCT_ID;NAME")
                );
                JSONObject paramsCondition = new JSONObject();
                paramsCondition.put("column", "NO");
                paramsCondition.put("condition", barcodeET.getText().toString());
                paramsTable.put("params", paramsCondition);

                transactions.put("params", paramsTable);
                array.put(transactions);
                params.put("transactions", array.toString());

                sendRequest(params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 取消进度条
                        stopProgressDialog();

                        try {
                            JSONArray resJA = new JSONArray(response);
                            JSONObject resJO = resJA.getJSONObject(0);
                            JSONArray rowsJA = resJO.getJSONArray("rows");
                            int len = rowsJA.length();

							for (int i = 0; i < len; i++) {
								// ["109454D334620","1596","1634,1635,1636,1639,1640,1638",1099.85,"109454D334"]
								String currRow = rowsJA.get(i).toString();
								String[] currRows = currRow.split("\",");

								AllotOutApply allotOutApply = new AllotOutApply();
								allotOutApply.setBarcode(currRows[0].substring(2, currRows[0].length()));
								allotOutApply.setNum("1");
								allotOutApply.setColor(currRows[1].substring(1, currRows[1].length()));
								allotOutApply.setSize(currRows[2].substring(1, currRows[2].length()));

								// 1099.85,"109454D334"]
								String[] lastRows = currRows[3].split(",");
								allotOutApply.setPrice(lastRows[0]);
								allotOutApply.setStyle(lastRows[1].substring(1, lastRows[1].length() - 2));
								lists.add(allotOutApply);
							}
                            
							// 记录数
							recodeNumTV.setText("记录数：" + lists.size());
							int num = 0;
							for(AllotOutApply allotOutApply : lists) {
								num += Integer.valueOf(allotOutApply.getNum());
							}
							totalCountTV.setText("数量：" + num);
							
                            AllotOutApplyLVAdapter mAdapter = new AllotOutApplyLVAdapter(AllotOutApplyActivity.this, lists, R.layout.allot_out_apply_item);
                            allotoutapplyLV.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
			break;
        */
        }
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	// 若有未审核的数据，则存入数据库
        if (data.size() > 0) {
        	db.beginTransaction();
            try {
                String uuid = UUID.randomUUID().toString();
                Date currentTime = new Date();
                db.execSQL("insert into c_allot_out('dj_no','upload_status','dj_status','dj_date','in_store','num','checkUUID')" +
                                " values(?,?,?,?,?,?,?)",
                        new Object[]{
                                getDJNo(),
                                "未上传",
                                "未完成",
                                new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentTime),
                                shipperET.getText(),
                                data.size(),//数量count
                                uuid
                        }
                );
                for (Product pro : data) {
                    db.execSQL("insert into c_allot_out_detail('checkUUID','fahuofang','remark','barcode','num','color','size','price','style') " +
                                    "values (?,?,?,?,?,?,?,?,?)",
                            new Object[]{uuid, shipperET.getText(), descET.getText(), pro.getBarCode(), pro.getCount(), pro.getColor(), pro.getSize(), pro.getPrice(), pro.getStyle()}
                    );
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }
    
	private String getDJNo() {
		StringBuffer sb = new StringBuffer();
		sb.append("IV305152");
		sb.append(new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date()));

		// 保存checkNo至SP
		int checkNo = App.getPreferenceUtils().getPreferenceInt("djNo");
		int i = 0;
		if (checkNo > 0) {
			i = checkNo + 1;
		} else {
			i = 1;
		}
		App.getPreferenceUtils().savePreferenceInt("djNo", i);

		StringBuffer finalCheckNo = new StringBuffer();
		for (int j = 0; j < 5 - String.valueOf(i).length(); j++) {
			finalCheckNo.append(0);
		}
		finalCheckNo.append(i);
		sb.append(finalCheckNo.toString());
		return sb.toString();
	}

}
