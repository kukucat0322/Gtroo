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
public class ReplenishmentAdapter extends JGGBaseAdapter {

    public ReplenishmentAdapter(Context c) {
        super(c, Constant.replenishmentTextValues, Constant.replenishmentImgValues, R.layout.grid_item);
    }

}
