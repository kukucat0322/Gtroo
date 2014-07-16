package cn.burgeon.core.ui.check;

import android.widget.Adapter;

public class Category {
	private String mTitle;

	private Adapter mAdapter;

	public Category(String title, Adapter adapter) {
		mTitle = title;
		mAdapter = adapter;
	}

	public void setTile(String title) {
		mTitle = title;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
	}

	public Adapter getAdapter() {
		return mAdapter;
	}

}