package cn.burgeon.core.ui.check;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.CheckQueryLVAdapter;
import cn.burgeon.core.bean.Order;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;
import cn.burgeon.core.widget.CustomDialogForCheckQuery;

public class CheckDocManagerActivity extends BaseActivity {

    private TextView totalOutCountTV, recordCountTV;
    private Button queryBtn;
    CheckQueryLVAdapter mAdapter;
    ListView mList;
    List<Order> data;
    CustomDialogForCheckQuery customDialogForCheckQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_doc_manager);

        init();

        initLVData();
    }

    private void initLVData() {
        data = fetchData();
        mAdapter = new CheckQueryLVAdapter(this, data, R.layout.check_query_item);
        mList.setAdapter(mAdapter);
        if (data.size() > 0)
            upateBottomBarInfo(data);
    }

    private void upateBottomBarInfo(List<Order> data) {
        int count = 0;
        for (Order pro : data) {
            count += Integer.parseInt(pro.getOrderCount());
        }
        recordCountTV.setText(String.format(getResources().getString(R.string.sales_new_common_record), data.size()));
    }

    private List<Order> fetchData() {
        Order order = null;
        List<Order> data = new ArrayList<Order>();
        Cursor c = db.rawQuery("select * from c_check", null);
        while (c.moveToNext()) {
            order = new Order();
            order.setOrderDate(c.getString(c.getColumnIndex("checkTime")));
            order.setOrderNo(c.getString(c.getColumnIndex("checkno")));
            order.setOrderCount(c.getString(c.getColumnIndex("count")));
            order.setOrderType(c.getString(c.getColumnIndex("type")));
            order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
            order.setOrderState(c.getString(c.getColumnIndex("status")));
            order.setIsChecked(c.getString(c.getColumnIndex("isChecked")));
            data.add(order);
        }
        if (c != null && !c.isClosed())
            c.close();
        return data;
    }

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(
                PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.currTimeTV);
        currTimeTV.setText(getCurrDate());

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInLVHeight(this);

        mList = (ListView) findViewById(R.id.docManagerLV);
        recordCountTV = (TextView) findViewById(R.id.recordCountTV);
        totalOutCountTV = (TextView) findViewById(R.id.totalOutCountTV);

        queryBtn = (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出对话框
                customDialogForCheckQuery = new CustomDialogForCheckQuery.Builder(CheckDocManagerActivity.this).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.size() > 0)
                            queryCheck();

                        if (customDialogForCheckQuery.isShowing())
                            customDialogForCheckQuery.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (customDialogForCheckQuery.isShowing())
                            customDialogForCheckQuery.dismiss();
                    }
                }).setCheckTypeSpinner(new String[]{"所有", "随机盘", "全盘"}).setStateSpinner(new String[]{"所有", "未上传", "已上传"}).show();
            }
        });

        itemOnLongClick();
    }

    private void queryCheck() {
        String startTime = customDialogForCheckQuery.getStartTime();
        String startYear = startTime.substring(0, 4);
        String startMonth = startTime.substring(4, 6);
        String startDay = startTime.substring(6, 8);
        String finalStartTime = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";

        String endTime = customDialogForCheckQuery.getEndTime();
        String endYear = endTime.substring(0, 4);
        String endMonth = endTime.substring(4, 6);
        String endDay = endTime.substring(6, 8);
        String finalEndTime = endYear + "-" + endMonth + "-" + endDay + " 23:59:59";

        String sql = "select * from c_check where " +
                "checkno = " + ((customDialogForCheckQuery.getCheckNo().length() > 0) ? ("'" + customDialogForCheckQuery.getCheckNo() + "'") : "''") + " and " +
                "checkTime between " + "'" + finalStartTime + "'" + " and " + "'" + finalEndTime + "'";
        if (!"所有".equals(customDialogForCheckQuery.getCheckType())) {
            sql += " and type = " + "'" + customDialogForCheckQuery.getCheckType() + "'";
        }
        if (!"所有".equals(customDialogForCheckQuery.getState())) {
            sql += " and isChecked = " + "'" + customDialogForCheckQuery.getState() + "'";
        }
        Log.i("zhang.h", sql);

        Cursor c = db.rawQuery(sql, null);
        List<Order> data = new ArrayList<Order>();
        while (c.moveToNext()) {
            Order order = new Order();
            order.setOrderDate(c.getString(c.getColumnIndex("checkTime")));
            order.setOrderNo(c.getString(c.getColumnIndex("checkno")));
            order.setOrderCount(c.getString(c.getColumnIndex("count")));
            order.setOrderType(c.getString(c.getColumnIndex("type")));
            order.setSaleAsistant(c.getString(c.getColumnIndex("orderEmployee")));
            order.setOrderState(c.getString(c.getColumnIndex("status")));
            order.setIsChecked(c.getString(c.getColumnIndex("isChecked")));
            data.add(order);
        }
        mAdapter.setList(data);
        upateBottomBarInfo(data);
        if (c != null && !c.isClosed())
            c.close();
    }

    private void itemOnLongClick() {
        mList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "续盘");
                menu.add(0, 1, 0, "删除");
            }
        });
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {
        // String no = ((TextView) menuInfo.targetView.findViewById(R.id.noTV)).getText().toString();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Order currOrder = data.get(menuInfo.position);

        switch (item.getItemId()) {
            case 0:
                // 续盘操作
                if ("已完成".equals(currOrder.getOrderState())) {
                    Toast.makeText(this, "单据已经审核，不能续盘！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                // 更新数据库
                delWithNo(currOrder.getOrderNo());

                // 更改data
                data.remove(currOrder);
                upateBottomBarInfo(data);

                // 刷新列表
                mAdapter.setList(data);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void delWithNo(String no) {
        db.beginTransaction();
        try {
            db.execSQL("delete from c_check where checkno = ?", new String[]{no});
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }

}
