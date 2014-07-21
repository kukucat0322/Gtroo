package cn.burgeon.core.ui.check;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.GatherAdapter;
import cn.burgeon.core.ui.BaseActivity;

public class GatherActivity extends BaseActivity {

	private String checkno;
	private LinkedHashMap<String, ArrayList<ShelfData>> shelfDataMap = new LinkedHashMap<String, ArrayList<ShelfData>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gather);
		
		Intent intent = getIntent();
		if(intent != null)
			checkno = intent.getStringExtra("checkno");
		getShelfData();

		for (Map.Entry<String, ArrayList<ShelfData>> entry : shelfDataMap.entrySet()) {
			mCategoryAdapter.addCategory("货架    " + entry.getKey(), new GatherAdapter(this, entry.getValue()));
		}
		ListView categoryList = (ListView) findViewById(R.id.categoryList);
		categoryList.setAdapter(mCategoryAdapter);
	}

	private CategoryAdapter mCategoryAdapter = new CategoryAdapter() {
		@Override
		protected View getTitleView(String title, int index, View convertView, ViewGroup parent) {
			TextView titleView;
			if (convertView == null) {
				titleView = (TextView) getLayoutInflater().inflate(R.layout.gather_title, null);
			} else {
				titleView = (TextView) convertView;
			}
			titleView.setText(title);
			return titleView;
		}
	};

	private void getShelfData() {
		ArrayList<ShelfData> shelfDatas = new ArrayList<ShelfData>();
		String sql = "select shelf, barcode, sum(count) as count "
				+ "from c_check_detail where checkno = ? group by shelf, barcode";
		Cursor c = db.rawQuery(sql, new String[]{checkno});
		while (c.moveToNext()) {
			ShelfData shelfData = new ShelfData();
			shelfData.setShelf(c.getString(c.getColumnIndex("shelf")));
			shelfData.setBarcode(c.getString(c.getColumnIndex("barcode")));
			shelfData.setCount(c.getString(c.getColumnIndex("count")));
			shelfDatas.add(shelfData);
		}
		if (c != null && !c.isClosed())
			c.close();

		for (ShelfData data : shelfDatas) {
			// 含有当前key
			if (shelfDataMap.containsKey(data.getShelf())) {
				shelfDataMap.get(data.getShelf()).add(data);
			} else {
				ArrayList<ShelfData> list = new ArrayList<ShelfData>();
				list.add(data);
				shelfDataMap.put(data.getShelf(), list);
			}
		}
	}

}
