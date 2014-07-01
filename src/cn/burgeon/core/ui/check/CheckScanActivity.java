package cn.burgeon.core.ui.check;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView.OnEditorActionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.CheckScanLVAdapter;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForCheck;

public class CheckScanActivity extends BaseActivity {
    ArrayList<Product> products = new ArrayList<Product>();

    private ListView checkscanLV;
    private TextView recodeNumTV, totalCountTV;
    private EditText barcodeET, shelfET;
    private Button okBtn, gatherBtn, reviewBtn;
    private CheckScanLVAdapter mAdapter;

    private CustomDialogForCheck customDialogForCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_scan);

        init();
    }

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());
        shelfET = (EditText) findViewById(R.id.shelfET);
        barcodeET = (EditText) findViewById(R.id.barcodeET);
        barcodeET.setOnEditorActionListener(editorActionListener);

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);

        checkscanLV = (ListView) findViewById(R.id.checkscanLV);
        mAdapter = new CheckScanLVAdapter(products, this);
        checkscanLV.setAdapter(mAdapter);
        recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);
        totalCountTV = (TextView) findViewById(R.id.totalCountTV);

        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(clickListener);

        gatherBtn = (Button) findViewById(R.id.gatherBtn);
        reviewBtn = (Button) findViewById(R.id.reviewBtn);
        gatherBtn.setOnClickListener(clickListener);
        reviewBtn.setOnClickListener(clickListener);
    }

    private void verifyBarCode(String barcodeText) {
        //从本地获取
        varLocal(barcodeText);

        //从网络获取
        //varNet();
    }

    private void varLocal(String barcodeText) {
        String sql = "select b.style_name,c.clrname,d.sizename,e.fprice"
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
            // 生成ProductList
            generateProductList(currProduct);
            // 刷新列表
            mAdapter.notifyDataSetChanged();
            upateBottomBarInfo();
        }
        if (c != null && !c.isClosed())
            c.close();
    }

    private void generateProductList(Product currProduct) {
        if (products.size() > 0) {
            boolean isFlag = false;
            for (Product product : products) {
                // 同一件
                if (currProduct.getBarCode().equals(product.getBarCode())) {
                    isFlag = true;
                    product.setCount(String.valueOf(Integer.valueOf(product.getCount()) + 1));
                }
            }

            if (!isFlag) {
                products.add(currProduct);
            }
        } else {
            products.add(currProduct);
        }
    }

    private void upateBottomBarInfo() {
        int count = 0;
        for (Product pro : products) {
            count += Integer.parseInt(pro.getCount());
        }
        totalCountTV.setText("总数量：" + count + "件");
        recodeNumTV.setText("当前货架：" + products.size() + "件");
    }

    private Product parseSQLResult(Cursor c) {
        Product pro = new Product();
        pro.setShelf(shelfET.getText().toString());
        pro.setBarCode(barcodeET.getText().toString());
        pro.setName(c.getString(c.getColumnIndex("style_name")));
        pro.setPrice(c.getString(c.getColumnIndex("fprice")));
        pro.setColor(c.getString(c.getColumnIndex("clrname")));
        pro.setSize(c.getString(c.getColumnIndex("sizename")));
        pro.setDiscount("0");
        pro.setCount("1");
        return pro;
    }

    OnEditorActionListener editorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    if (barcodeET.getText() != null && barcodeET.getText().toString().length() > 0) {
                        verifyBarCode(barcodeET.getText().toString());
                    }
                    break;
                default:
                    break;
            }
            return true;
        }

    };

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.okBtn:
                    if (barcodeET.getText() != null && barcodeET.getText().toString().length() > 0) {
                        verifyBarCode(barcodeET.getText().toString());
                    }
                    break;
                case R.id.gatherBtn:
                    break;
                case R.id.reviewBtn:
                    // 若有数据
                    if (products.size() > 0) {
                        showTips();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //显示对话框
    private void showTips() {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.systemtips))
                .setMessage(R.string.checktips)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 弹出对话框
                        customDialogForCheck = new CustomDialogForCheck.Builder(CheckScanActivity.this).setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 批量更新数据库
                                for (Product product : products) {
                                    updateState(product);
                                }

                                // 关闭对话框
                                if (customDialogForCheck.isShowing())
                                    customDialogForCheck.dismiss();

                                // 清除
                                products.clear();

                                // 退出页面
                                finish();
                            }
                        }).setNegativeButton("取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (customDialogForCheck.isShowing())
                                    customDialogForCheck.dismiss();
                            }
                        }).setCheckTypeSpinner(new String[]{"随机盘", "全盘"}).setCheckerSpinner(new String[]{"Rain"}).show();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void updateState(Product product) {
        db.beginTransaction();
        try {
            String uuid = UUID.randomUUID().toString();
            Date currentTime = new Date();
            db.execSQL("insert into c_check('barcode', 'shelf','checkTime','checkno','count','type','orderEmployee',"
                            + "'status','isChecked','checkUUID')" +
                            " values(?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{
                            product.getBarCode(),
                            shelfET.getText().toString(),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime),
                            getNo(),
                            product.getCount(),
                            customDialogForCheck.getCheckType(),
                            customDialogForCheck.getChecker(),
                            "已完成",
                            "未上传",
                            uuid}
            );
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 若有未审核的数据，则存入数据库
        if (products.size() > 0) {
            for (Product product : products) {
                // 插入数据
                updateCheckTable(product);
            }
        }
    }

    private void updateCheckTable(Product currProduct) {
        db.beginTransaction();
        try {
            String uuid = UUID.randomUUID().toString();
            Date currentTime = new Date();
            db.execSQL("insert into c_check('barcode', 'shelf','checkTime','checkno','count','type','orderEmployee',"
                            + "'status','isChecked','checkUUID')" +
                            " values(?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{
                            currProduct.getBarCode(),
                            shelfET.getText().toString(),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime),
                            getNo(), // IV305152 + 日期 + 流水号
                            currProduct.getCount(),
                            "未知类型",
                            App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key),
                            "未完成",
                            "未上传",
                            uuid}
            );
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }

    private String getNo() {
        StringBuffer sb = new StringBuffer();
        sb.append("IV305152");
        sb.append(new SimpleDateFormat("yyMMdd").format(new Date()));

        // 保存checkNo至SP
        int checkNo = App.getPreferenceUtils().getPreferenceInt("checkNo");
        int i = 0;
        if (checkNo > 0) {
            i = checkNo + 1;
        } else {
            i = 1;
        }
        App.getPreferenceUtils().savePreferenceInt("checkNo", i);

        StringBuffer finalCheckNo = new StringBuffer();
        for (int j = 0; j < 5 - String.valueOf(i).length(); j++) {
            finalCheckNo.append(0);
        }
        finalCheckNo.append(i);
        sb.append(finalCheckNo.toString());
        return sb.toString();
    }

    /*@Override
    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okBtn:
			
			 * { "table":"12668", "columns":[ "NO", "M_PRODUCT_ID;COLORS", "M_PRODUCT_ID;SIZES", "M_PRODUCT_ID;PRICELIST",
			 * "M_PRODUCT_ID;NAME" ], "params":{"column":"NO","condition":"109454D334620"} }
			 
			Map<String, String> params = new HashMap<String, String>();
			JSONArray array = new JSONArray();
			try {
				JSONObject transactions = new JSONObject();
				transactions.put("id", 112);
				transactions.put("command", "Query");

				JSONObject paramsTable = new JSONObject();
				paramsTable.put("table", "12668");
				paramsTable.put("columns",
						new JSONArray().put("NO").put("M_PRODUCT_ID;COLORS").put("M_PRODUCT_ID;SIZES").put("M_PRODUCT_ID;NAME"));
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
								// ["109454D334620","1596","1634,1635,1636,1639,1640,1638","109454D334"]
								String currRow = rowsJA.get(i).toString();
								String[] currRows = currRow.split("\",");

								CheckScan checkScan = new CheckScan();
								checkScan.setBarcode(currRows[0].substring(2, currRows[0].length()));
								checkScan.setColor(currRows[1].substring(1, currRows[1].length()));
								checkScan.setSize(currRows[2].substring(1, currRows[2].length()));
								checkScan.setStyle(currRows[3].substring(1, currRows[3].length() - 2));
								checkScan.setNum("1");
								lists.add(checkScan);
							}

							// 记录数
							recodeNumTV.setText("记录数：" + lists.size());
							int num = 0;
							for (CheckScan checkScan : lists) {
								num += Integer.valueOf(checkScan.getNum());
							}
							totalCountTV.setText("数量：" + num);

							CheckScanLVAdapter mAdapter = new CheckScanLVAdapter(
									CheckScanActivity.this, lists, R.layout.check_scan_item);
							checkscanLV.setAdapter(mAdapter);
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
	}*/
}
