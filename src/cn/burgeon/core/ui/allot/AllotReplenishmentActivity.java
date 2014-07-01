package cn.burgeon.core.ui.allot;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.ReplenishmentAdapter;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class AllotReplenishmentActivity extends BaseActivity {

    private GridView replenishmentGV;
    private ReplenishmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_replenishment);

        init();
    }

    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        replenishmentGV = (GridView) findViewById(R.id.replenishmentGV);
        mAdapter = new ReplenishmentAdapter(this);
        replenishmentGV.setAdapter(mAdapter);
        replenishmentGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.replenishmentTextValues[0].equals(itemValue)) {
                    forwardActivity(AllotReplenishmentApplyActivity.class);
                } else if (itemValue != null && Constant.replenishmentTextValues[2].equals(itemValue)) {
                    forwardActivity(AllotReplenishmentQueryActivity.class);
                }
            }
        });
    }
}
