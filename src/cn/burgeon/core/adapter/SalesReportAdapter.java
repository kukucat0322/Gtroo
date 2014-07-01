package cn.burgeon.core.adapter;

import android.content.Context;

import cn.burgeon.core.Constant;
import cn.burgeon.core.R;

/**
 * Created by Simon on 2014/4/16.
 */
public class SalesReportAdapter extends JGGBaseAdapter {

    public SalesReportAdapter(Context c) {
        super(c, Constant.salesReportTextValues, Constant.salesReportImgValues, R.layout.grid_item);
    }

}
