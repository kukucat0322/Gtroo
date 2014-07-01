package cn.burgeon.core.adapter;

import android.content.Context;

import cn.burgeon.core.Constant;
import cn.burgeon.core.R;

public class InventoryManagerAdapter extends JGGBaseAdapter {

    public InventoryManagerAdapter(Context c) {
        super(c, Constant.inventoryManagerTextValues, Constant.inventoryManagerImgValues, R.layout.grid_item);
    }

}
