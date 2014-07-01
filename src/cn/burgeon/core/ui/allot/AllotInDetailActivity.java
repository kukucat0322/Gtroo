package cn.burgeon.core.ui.allot;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.AllotInDetailLVAdapter;
import cn.burgeon.core.bean.AllotInDetail;
import cn.burgeon.core.bean.IntentData;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;
import cn.burgeon.core.utils.ScreenUtils;

public class AllotInDetailActivity extends BaseActivity {

    private TextView recordCountTV, totalOutCountTV;
    private IntentData iData;
    private ListView allotindetaiLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allot_in_detail);

        iData = (IntentData) getIntent().getParcelableExtra(PAR_KEY);

        init();
    }

    private void init() {
        // 初始化门店信息和当前时间
        TextView storeTV = (TextView) findViewById(R.id.allotindetailstoreTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        TextView currTimeTV = (TextView) findViewById(R.id.allotindetailcurrTimeTV);
        currTimeTV.setText(getCurrDate());

        // 记录数
        recordCountTV = (TextView) findViewById(R.id.recordCountTV);
        recordCountTV.setText("记录数：" + iData.getAllotInDetails().size());

        // 出库数量
        totalOutCountTV = (TextView) findViewById(R.id.totalOutCountTV);
        int count = 0;
        for (AllotInDetail allotInDetail : iData.getAllotInDetails()) {
            count += Integer.valueOf(allotInDetail.getQTYOUT());
        }
        totalOutCountTV.setText("数量：" + count);

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        ViewGroup.LayoutParams params = hsv.getLayoutParams();
        params.height = (int) ScreenUtils.getAllotInDetailLVHeight(this);

        allotindetaiLV = (ListView) findViewById(R.id.allotindetaiLV);
        AllotInDetailLVAdapter mAdapter = new AllotInDetailLVAdapter(AllotInDetailActivity.this, iData.getAllotInDetails(), R.layout.allot_in_detail_item);
        allotindetaiLV.setAdapter(mAdapter);
    }


}
