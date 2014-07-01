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
public class AllotOutInAdapter extends JGGBaseAdapter {

    public AllotOutInAdapter(Context c) {
        super(c, Constant.allotOutInTextValues, Constant.allotOutInImgValues, R.layout.grid_item);
    }

}
