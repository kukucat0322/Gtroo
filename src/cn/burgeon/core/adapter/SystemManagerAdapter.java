package cn.burgeon.core.adapter;

import android.content.Context;
import cn.burgeon.core.Constant;
import cn.burgeon.core.R;

public class SystemManagerAdapter extends JGGBaseAdapter{
	public SystemManagerAdapter(Context c) {
        super(c, Constant.systemManagerTextValues, Constant.systemManagerImgValues, R.layout.grid_item);
    }
}
