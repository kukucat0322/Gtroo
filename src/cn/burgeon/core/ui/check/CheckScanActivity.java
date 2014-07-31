package cn.burgeon.core.ui.check;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mexxen.mx5010.barcode.BarcodeEvent;
import mexxen.mx5010.barcode.BarcodeListener;
import mexxen.mx5010.barcode.BarcodeManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.CheckScanLVAdapter;
import cn.burgeon.core.bean.Product;
import cn.burgeon.core.bean.RequestResult;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForCheck;

public class CheckScanActivity extends BaseActivity {
    private BarcodeManager bm;
    private ListView checkscanLV;
    private TextView recodeNumTV, totalCountTV;
    private EditText barcodeET, shelfET;
    private Button okBtn, gatherBtn, reviewBtn;
    private CheckScanLVAdapter mAdapter;

    private CustomDialogForCheck customDialogForCheck;
    private String shelfNo = "01";
    
    private HashMap<String, ArrayList<Product>> productMap = new HashMap<String, ArrayList<Product>>();
    private String no;
    private boolean hasDataNotSave = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_scan);

        Intent intent = getIntent();
		if (intent != null) {
			no = intent.getStringExtra("checkNo");
			if (no != null && no.length() > 0) {
				// 查数据(续盘过来的)
				ArrayList<Product> products = new ArrayList<Product>();
				String sql = "select shelf, count, stylename, barcode, color, size from c_check_detail where checkno = ? group by shelf, barcode";
				Cursor c = db.rawQuery(sql, new String[] { no });
				while (c.moveToNext()) {
					Product pro = new Product();
					pro.setShelf(c.getString(c.getColumnIndex("shelf")));
					pro.setCount(c.getString(c.getColumnIndex("count")));
					pro.setName(c.getString(c.getColumnIndex("stylename")));
					pro.setBarCode(c.getString(c.getColumnIndex("barcode")));
					pro.setColor(c.getString(c.getColumnIndex("color")));
					pro.setSize(c.getString(c.getColumnIndex("size")));
					products.add(pro);
				}
				if (c != null && !c.isClosed())
					c.close();

				for (Product product : products) {
					// 含有当前key
					if (productMap.containsKey(product.getShelf())) {
						productMap.get(product.getShelf()).add(product);
					} else {
						ArrayList<Product> list = new ArrayList<Product>();
						list.add(product);
						productMap.put(product.getShelf(), list);
					}
				}
				
				// 显示最后一个货架
				String sql2 = "select shelf, max(_id) from c_check_detail where checkno = ?";
				Cursor c2 = db.rawQuery(sql2, new String[] { no });
				if (c2.moveToFirst()) {
					String shelfStr = c2.getString(c.getColumnIndex("shelf"));
					shelfNo = shelfStr;
				}
				if (c != null && !c.isClosed())
					c.close();
			} else {
				no = getNo();
			}
		}
        
        init();
        bm = new BarcodeManager(this);
		bm.addListener(barcodeListener);
    }
    
    BarcodeListener barcodeListener = new BarcodeListener() {
 		// 重写 barcodeEvent 方法，获取条码事件
 		@Override
 		public void barcodeEvent(BarcodeEvent event) {
 			// 当条码事件的命令为“SCANNER_READ”时，进行操作
 			if (event.getOrder().equals("SCANNER_READ")) {
 				// 调用 getBarcode()方法读取条码信息
 				Log.d("check", "=======barcode========" + bm.getBarcode());
 				String barcode = bm.getBarcode() == null?"":bm.getBarcode().trim();
 				//barcodeET.setText(barcode);
 				verifyBarCode(barcode);
 			}
 		}
 	};

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());
        shelfET = (EditText) findViewById(R.id.shelfET);
        shelfET.setText(shelfNo);
        shelfET.setOnFocusChangeListener(new OnFocusChangeListener() {
        	@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// 失去焦点
				if (!hasFocus) {
					String currShelfET = shelfET.getText().toString();
					// 有值但值变了
					if (currShelfET.length() > 0 && !shelfNo.equals(currShelfET)) {
						shelfNo = currShelfET;
						mAdapter.setList(productMap.get(currShelfET));
						upateBottomBarInfo();
					}
				}
			}
        });
        barcodeET = (EditText) findViewById(R.id.barcodeET);
        barcodeET.setOnEditorActionListener(editorActionListener);

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);

        checkscanLV = (ListView) findViewById(R.id.checkscanLV);
        mAdapter = new CheckScanLVAdapter(productMap.get(shelfNo), this);
        checkscanLV.setAdapter(mAdapter);
        recodeNumTV = (TextView) findViewById(R.id.recodeNumTV);
        totalCountTV = (TextView) findViewById(R.id.totalCountTV);

        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(clickListener);

        gatherBtn = (Button) findViewById(R.id.gatherBtn);
        reviewBtn = (Button) findViewById(R.id.reviewBtn);
        gatherBtn.setOnClickListener(clickListener);
        reviewBtn.setOnClickListener(clickListener);
        
        // 续盘会有值
        upateBottomBarInfo();
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
        if(c.getCount() == 0){
        	barcodeET.setText(barcodeText);
        	showAlertMsg(R.string.nothatbarcode);
        	return;
        }
        if (c.moveToFirst()) {
            Product currProduct = parseSQLResult(c);
            // 生成ProductList
            generateProductList(currProduct);
            // 刷新列表
            mAdapter.setList(productMap.get(shelfNo));
            upateBottomBarInfo();
        }
        if (c != null && !c.isClosed())
            c.close();
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

	private void generateProductList(Product currProduct) {
		hasDataNotSave = true;
		shelfNo = currProduct.getShelf();
		
		if (productMap.size() > 0) {
			ArrayList<Product> products = productMap.get(shelfNo);
			if (products == null) {
				ArrayList<Product> list = new ArrayList<Product>();
				list.add(currProduct);
				productMap.put(shelfNo, list);
			} else {
				for (Product product : products) {
					// 同一个条码
					if (product.getBarCode().equals(currProduct.getBarCode())) {
						product.setCount(String.valueOf(Integer.valueOf(product.getCount()) + 1));
					} else {
						// 该条码已存在
						if (!isExsitBarCode(currProduct.getBarCode())) {
							products.add(currProduct);
						}
					}
				}
			}
		} else {
			ArrayList<Product> list = new ArrayList<Product>();
			list.add(currProduct);
			productMap.put(shelfNo, list);
		}
	}

	private boolean isExsitBarCode(String barCode) {
		boolean isFlag = false;
		for (Product product : productMap.get(shelfNo)) {
			if (barCode.equals(product.getBarCode())) {
				isFlag = true;
				break;
			}
		}
		return isFlag;
	}
	
	private void upateBottomBarInfo() {
		if (productMap.get(shelfNo) != null) {
			int count = 0;
			for (Product pro : productMap.get(shelfNo)) {
				count += Integer.parseInt(pro.getCount());
			}
			totalCountTV.setText("总数量：" + count + "件");
			recodeNumTV.setText("当前货架：" + productMap.get(shelfNo).size() + "件");
		} else {
			totalCountTV.setText("总数量：0件");
			recodeNumTV.setText("当前货架：0件");
		}
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
                	// 入库
					if (productMap.size() > 0) {
						Date currentTime = new Date();
						String checkTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(currentTime);
						updateCheckTable(checkTime, "未知类型", App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key), "未完成");
					}
                	
                	forwardActivity(GatherActivity.class, "checkno", no);
                    break;
                case R.id.reviewBtn:
                    // 若有数据
                    if (productMap.size() > 0) {
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
                            	Date currentTime = new Date();
                            	String checkTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(currentTime);
                            	
                                // 批量更新数据库
                                updateCheckTable(checkTime, customDialogForCheck.getCheckType(), customDialogForCheck.getChecker(), "已完成");
                                
                                // 上传
								if (App.getNetUtils().isNetworkAvailable()) {
									uploadCheck(checkTime.replace("-", ""), customDialogForCheck.getCheckType());
								}

                                // 关闭对话框
                                if (customDialogForCheck.isShowing())
                                    customDialogForCheck.dismiss();

                                // 清除
                                productMap.clear();

                                // 退出页面
                                finish();
                            }
                        }).setNegativeButton("取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (customDialogForCheck.isShowing())
                                    customDialogForCheck.dismiss();
                            }
                        }).setCheckTypeSpinner(new String[]{"随机盘", "全盘"}).setCheckerSpinner(new String[]{App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key)}).show();
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

    private void updateCheckTable(String checkTime, String type, String employee, String status) {
    	hasDataNotSave = false;
    	
        db.beginTransaction();
        try {
        	// 查找
			boolean isExsit = false;
			Cursor c = db.rawQuery("select count from c_check where checkno = ?", new String[] { no });
			if (c.moveToFirst()) {
				isExsit = true;
			}
			if (c != null && !c.isClosed())
				c.close();
    		
    		// 更新
			if (isExsit) {
				db.execSQL("update c_check set 'count' = ?, 'type' = ?, 'orderEmployee' = ?, 'status' = ? where checkno = ?", new Object[] { getCount(), type, employee, status, no });
			}
    		// 新增
    		else {
    			db.execSQL("insert into c_check('checkTime','checkno','count','type','orderEmployee','status','isChecked')" +
    					" values(?,?,?,?,?,?,?)",
   					new Object[]{
    					checkTime,
    					no, // IV305152 + 日期 + 流水号
    					getCount(),
    					type,
    					employee,
    					status,
    					"未上传"}
    				);
    		}
		    
			for(Map.Entry<String, ArrayList<Product>> entry : productMap.entrySet()) {
				String shelf = entry.getKey();
				ArrayList<Product> products = entry.getValue();
				for(Product product : products){
					// 查找
					boolean isExsitDetail = false;
					Cursor cDetail = db.rawQuery("select count from c_check_detail where checkno = ? and shelf = ? and barcode = ?", new String[] { no, shelf, product.getBarCode() });
					if (cDetail.moveToFirst()) {
						isExsitDetail = true;
					}
					if (cDetail != null && !cDetail.isClosed())
						cDetail.close();

		    		// 更新
					if (isExsitDetail) {
						db.execSQL("update c_check_detail set 'count' = ? where checkno = ? and shelf = ? and barcode = ?", new Object[] { product.getCount(), no, shelf, product.getBarCode() });
					}
		    		// 新增
					else {
						db.execSQL("insert into c_check_detail('shelf','barcode','count','color','size','stylename','checkno')" +
								" values(?,?,?,?,?,?,?)",
							new Object[]{
								shelf,
								product.getBarCode(),
								product.getCount(),
								product.getColor(),
								product.getSize(),
								product.getName(),
								no}
							);
					}
				}
			}
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }
    
	private int getCount() {
		int count = 0;
		for (Map.Entry<String, ArrayList<Product>> entry : productMap.entrySet()) {
			for (Product pro : entry.getValue()) {
				count += Integer.parseInt(pro.getCount());
			}
		}
		return count;
	}

    private String getNo() {
        StringBuffer sb = new StringBuffer();
        sb.append("IV305152");
        sb.append(new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date()));

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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        bm.removeListener(barcodeListener);
    	bm.dismiss();
    }
    
    @Override
	public void onBackPressed() {
		if (hasDataNotSave) {
			// 显示对话框
			AlertDialog dialog = null;
			AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.systemtips))
					.setMessage(R.string.hasdata).setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 入库
							if (productMap.size() > 0) {
								Date currentTime = new Date();
								String checkTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(currentTime);
								updateCheckTable(checkTime, "未知类型", App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.user_key), "未完成");
							}
							
							// 关闭对话框
							dialog.dismiss();

							// 清除
							productMap.clear();

							// 退出页面
							finish();
						}
					}).setNegativeButton("否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 关闭对话框
							dialog.dismiss();

							// 清除
							productMap.clear();
							
							// 退出页面
							finish();
						}
					});
			dialog = builder.create();
			dialog.show();
		} else {
			super.onBackPressed();
		}
	}

	protected void uploadCheck(String checkTime, String type) {
		Map<String, String> params = new HashMap<String, String>();
		JSONArray array;
		JSONObject transactions;

		try {
			array = new JSONArray();
			transactions = new JSONObject();
			transactions.put("id", 112);
			transactions.put("command", "ProcessOrder");

			// 第一个params
			JSONObject paramsInTransactions = new JSONObject();
			paramsInTransactions.put("submit", "true");

			// masterobj
			JSONObject masterObj = new JSONObject();
			masterObj.put("table", 12254);
			masterObj.put("BILLDATE", checkTime);
			masterObj.put("DOCTYPE", ("全盘".equals(type) ? "INF" : "INR"));
			masterObj.put("C_STORE_ID__NAME", App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));
			masterObj.put("DIFFREASON", "缺货");
			paramsInTransactions.put("masterobj", masterObj);

			// detailobjs
			JSONObject detailObjs = new JSONObject();
			// reftables

			// refobjs
			JSONArray refobjs = new JSONArray();

			// 明细
			JSONObject refobj = new JSONObject();
			refobj.put("table", 12255);
			JSONArray addList = new JSONArray();

			List<Product> detailsItems = getDetailsData();
			if (detailsItems != null && detailsItems.size() > 0) {
				// Log.d("check", "========明细=========");
				for (Product product : detailsItems) {
					JSONObject item = new JSONObject();
					item.put("QTYCOUNT", product.getCount());
					item.put("M_PRODUCT_ID__NAME", product.getBarCode());
					addList.put(item);
				}
			}
			refobj.put("addList", addList);
			refobjs.put(refobj);

			// 按货架扫描明细
			JSONObject refobj2 = new JSONObject();
			refobj2.put("table", 15743);
			JSONArray addList2 = new JSONArray();

			List<Product> shelfDetailsItems = getDetailsData();
			if (shelfDetailsItems != null && shelfDetailsItems.size() > 0) {
				// Log.d("check", "========按货架扫描明细=========");
				for (Product product : shelfDetailsItems) {
					JSONObject item2 = new JSONObject();
					item2.put("SHELFNO", product.getShelf());
					item2.put("QTYCOUNT", product.getCount());
					item2.put("M_PRODUCT_ID__NAME", product.getBarCode());
					addList2.put(item2);
				}
			}
			refobj2.put("addList", addList2);
			refobjs.put(refobj2);

			detailObjs.put("refobjs", refobjs);
			detailObjs.put("reftables", new JSONArray().put(319)/* .put(1274) */);
			paramsInTransactions.put("detailobjs", detailObjs);

			transactions.put("params", paramsInTransactions);
			array.put(transactions);
			Log.d("check", array.toString());
			params.put("transactions", array.toString());

			String tt = App.getInstance().getSDF().format(new Date());
			// appKey,时间戳,MD5签名
			params.put("sip_appkey", App.getSipkey());
			params.put("sip_timestamp", tt);
			params.put("sip_sign", App.getInstance().MD5(App.getSipkey() + tt + App.getInstance().getSIPPSWDMD5()));

			// 执行请求
			UploadTask uploadTask = new UploadTask(params);
			uploadTask.execute(App.getHosturl());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private List<Product> getDetailsData() {
		List<Product> details = new ArrayList<Product>();
		Cursor c = db.rawQuery("select barcode, count, shelf from c_check_detail where checkno = ?", new String[] { no });
		Product product = null;
		while (c.moveToNext()) {
			product = new Product();
			product.setBarCode(c.getString(c.getColumnIndex("barcode")));
			product.setCount(c.getString(c.getColumnIndex("count")));
			product.setShelf(c.getString(c.getColumnIndex("shelf")));
			details.add(product);
		}
		if (c != null && !c.isClosed())
			c.close();
		return details;
	}
	
	class UploadTask extends AsyncTask<String, Integer, String> {
		
		List<NameValuePair> pars = null;
		HttpPost httpRequest = null;
		HttpResponse httpResponse;

		public UploadTask(Map<String, String> params) {
			/* Post运作传送变数必须用NameValuePair[]阵列储存 */
			pars = new ArrayList<NameValuePair>();

			for (Map.Entry<String, String> entry : params.entrySet()) {
				pars.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		// 任务启动，可以在这里显示一个对话框，这里简单处理
		@Override
		protected void onPreExecute() {
		}

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			/* 建立HttpPost连接 */
			httpRequest = new HttpPost(params[0]);
			try {
				// 发出HTTP request
				httpRequest.setEntity(new UrlEncodedFormEntity(pars, HTTP.UTF_8));
				// 取得HTTP response
				httpResponse = new DefaultHttpClient().execute(httpRequest);
				// 若状态码为200
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// 得到返回字串
					String response = EntityUtils.toString(httpResponse.getEntity());
					RequestResult result = parseResult(response);
                    //请求成功，更新记录状态和销售单号
					if ("0".equals(result.getCode())) {
						updateOrderStatus(result);
					}
				}
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		}

		// 更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}
	
	private RequestResult parseResult(String response) {
		RequestResult result = null;
		try {
			JSONArray resJA = new JSONArray(response);
			JSONObject resJO = resJA.getJSONObject(0);
			result = new RequestResult(resJO.getString("code"), resJO.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void updateOrderStatus(RequestResult result) {
		db.beginTransaction();
		try {
			db.execSQL("update c_check set isChecked = ? where checkno = ?",
					new Object[] { getResources().getString(R.string.sales_settle_hasup), no });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	
}
