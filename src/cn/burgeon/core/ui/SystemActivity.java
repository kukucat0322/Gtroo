package cn.burgeon.core.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SystemAdapter;
import cn.burgeon.core.ui.allot.AllotManagerActivity;
import cn.burgeon.core.ui.check.CheckManagerActivity;
import cn.burgeon.core.ui.inventory.InventoryManagerActivity;
import cn.burgeon.core.ui.member.MemberManagerActivity;
import cn.burgeon.core.ui.sales.SalesManagerActivity;
import cn.burgeon.core.ui.system.SystemManagerActivity;

public class SystemActivity extends BaseActivity {

    private GridView sysGV;
    private SystemAdapter mAdapter;
    private TextView storeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_system);

        init();

        initStoreData(storeTV);
    }

    private void init() {
        sysGV = (GridView) findViewById(R.id.sysGV);
        mAdapter = new SystemAdapter(this);
        sysGV.setAdapter(mAdapter);
        sysGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.sysTextValues[0].equals(itemValue)) {
                    forwardActivity(SalesManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[1].equals(itemValue)) {
                    forwardActivity(MemberManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[2].equals(itemValue)) {
                    forwardActivity(AllotManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[3].equals(itemValue)) {
                    forwardActivity(CheckManagerActivity.class);
                } else if (itemValue != null && Constant.sysTextValues[4].equals(itemValue)) {
                    forwardActivity(InventoryManagerActivity.class);
                }  else if (itemValue != null && Constant.sysTextValues[5].equals(itemValue)) {
                    forwardActivity(SystemManagerActivity.class);
                } 
            }
        });

        storeTV = (TextView) findViewById(R.id.storeTV);
    }
}
