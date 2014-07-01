package cn.burgeon.core.ui.system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.burgeon.core.App;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SystemManagerAdapter;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class SystemManagerActivity extends BaseActivity{
	
    private GridView systemManagerGridView;
    private SystemManagerAdapter systemManagerAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupFullscreen();
		setContentView(R.layout.activity_system_manager);
		
		init();
	}
	
    private void init() {
        // 初始化门店信息
        TextView storeTV = (TextView) findViewById(R.id.storeTV);
        storeTV.setText(App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.store_key));

        systemManagerGridView = (GridView) findViewById(R.id.systemManagerGridView);
        systemManagerAdapter = new SystemManagerAdapter(this);
        systemManagerGridView.setAdapter(systemManagerAdapter);
        systemManagerGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                if (itemValue != null && Constant.systemManagerTextValues[0].equals(itemValue)) {
                    forwardActivity(SystemConfigurationActivity.class);
                } else if (itemValue != null && Constant.systemManagerTextValues[1].equals(itemValue)) {
                    forwardActivity(SystemNetTestActivity.class);
                } else if (itemValue != null && Constant.systemManagerTextValues[2].equals(itemValue)) {
                    forwardActivity(SystemUpdateActivity.class);
                } else if (itemValue != null && Constant.systemManagerTextValues[3].equals(itemValue)) {
                    forwardActivity(SystemDataDownloadActivity.class);
                } else if (itemValue != null && Constant.systemManagerTextValues[4].equals(itemValue)) {
                    forwardActivity(SystemDataCleanActivity.class);
                }
            }
        });
    }
}
