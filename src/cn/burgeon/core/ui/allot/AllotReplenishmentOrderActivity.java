package cn.burgeon.core.ui.allot;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.ReplenishmentOrderAdapter;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class AllotReplenishmentOrderActivity extends BaseActivity {

    private GridView replenishmentOrderGV;
    private ReplenishmentOrderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_replenishment_order);

        init();
    }

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        replenishmentOrderGV = (GridView) findViewById(R.id.replenishmentOrderGV);
        mAdapter = new ReplenishmentOrderAdapter(this);
        replenishmentOrderGV.setAdapter(mAdapter);
        replenishmentOrderGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.replenishmentOrderTextValues[0].equals(itemValue)) {
                    forwardActivity(AllotReplenishmentOrderApplyActivity.class);
                } else if (itemValue != null && Constant.replenishmentOrderTextValues[2].equals(itemValue)) {
                    forwardActivity(AllotReplenishmentOrderQueryActivity.class);
                }
            }
        });
    }
}
