package cn.burgeon.core.adapter;

import android.content.Context;

import cn.burgeon.core.Constant;
import cn.burgeon.core.R;

/**
 * Created by Simon on 2014/4/16.
 */
public class AllotManagerAdapter extends JGGBaseAdapter {

    public AllotManagerAdapter(Context c) {
        super(c, Constant.allotManagerTextValues, Constant.allotManagerImgValues, R.layout.grid_item);
    }

}
