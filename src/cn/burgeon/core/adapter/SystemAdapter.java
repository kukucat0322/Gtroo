package cn.burgeon.core.adapter;

import android.content.Context;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;

/**
 * Created by Simon on 2014/4/16.
 */
public class SystemAdapter extends JGGBaseAdapter {

    public SystemAdapter(Context c) {
        super(c, Constant.sysTextValues, Constant.sysImgValues, R.layout.grid_item);
    }
}
