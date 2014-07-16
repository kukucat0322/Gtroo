package cn.burgeon.core.ui.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cn.burgeon.core.R;
import cn.burgeon.core.adapter.GatherAdapter;
import cn.burgeon.core.ui.BaseActivity;

public class GatherActivity extends BaseActivity {

	private LinkedHashMap<String, ArrayList<ShelfData>> shelfDataMap = new LinkedHashMap<String, ArrayList<ShelfData>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gather);

		getShelf();

		for (int i = 0; i < shelfDatas.size(); i++) {
			ShelfData currShelfData = shelfDatas.get(i);
			mCategoryAdapter.addCategory(currShelfData.getShelf(), new GatherAdapter(this, shelfDatas));
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

	private void getShelf() {
		ArrayList<ShelfData> shelfDatas = new ArrayList<ShelfData>();

		ShelfData shelfData = new ShelfData();
		shelfData.setShelf("01");
		shelfData.setBarcode("AS001BL");
		shelfData.setCount("1");
		shelfDatas.add(shelfData);

		shelfData = new ShelfData();
		shelfData.setShelf("01");
		shelfData.setBarcode("AS002BL");
		shelfData.setCount("2");
		shelfDatas.add(shelfData);

		shelfData = new ShelfData();
		shelfData.setShelf("02");
		shelfData.setBarcode("AS003BL");
		shelfData.setCount("3");
		shelfDatas.add(shelfData);

		shelfData = new ShelfData();
		shelfData.setShelf("02");
		shelfData.setBarcode("AS004BL");
		shelfData.setCount("4");
		shelfDatas.add(shelfData);

		ArrayList<ShelfData> tempList = new ArrayList<ShelfData>();
		for (ShelfData data : shelfDatas) {
			// 含有当前key
			if (shelfDataMap.containsKey(data.getShelf())) {
				shelfDataMap.get(data.getShelf()).add(data);
			} else {
				tempList.add(data);
				shelfDataMap.put(data.getShelf(), tempList);
			}
		}
	}

}
