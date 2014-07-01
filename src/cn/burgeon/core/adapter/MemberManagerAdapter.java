package cn.burgeon.core.adapter;

import android.content.Context;

import cn.burgeon.core.Constant;
import cn.burgeon.core.R;

/**
 * Created by Simon on 2014/4/16.
 */
public class MemberManagerAdapter extends JGGBaseAdapter {

    public MemberManagerAdapter(Context c) {
        super(c, Constant.memberManagerTextValues, Constant.memberManagerImgValues, R.layout.grid_item);
    }


}
