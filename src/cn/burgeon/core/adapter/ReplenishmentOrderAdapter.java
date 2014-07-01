package cn.burgeon.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.burgeon.core.Constant;
import cn.burgeon.core.R;

/**
 * Created by Simon on 2014/4/16.
 */
public class ReplenishmentOrderAdapter extends JGGBaseAdapter {

    public ReplenishmentOrderAdapter(Context c) {
        super(c, Constant.replenishmentOrderTextValues, Constant.replenishmentOrderImgValues, R.layout.grid_item);
    }

}
