package cn.burgeon.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;

import cn.burgeon.core.App;
import cn.burgeon.core.R;


public class ScreenUtils {

    public static float density() {
        return App.getDM().density;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static float screenWidth() {
        return App.getDM().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static float screenHeight() {
        return App.getDM().heightPixels;
    }

    /**
     * dp转化为px
     *
     * @param context
     * @param dp
     * @return
     */
    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * density();
    }

    /**
     * px转化为dp
     *
     * @param context
     * @param px
     * @return
     */
    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / density();
    }

    public static float dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + 0.5f);
    }

    public static float pxToDpCeilInt(Context context, float px) {
        return (int) (pxToDp(context, px) + 0.5f);
    }

    /**
     * 获取调拨入库listview高度(总高度-通知栏高度-标题栏高度-门店显示区高度-记录条数区高度-操作按钮高度-padding补差高度)
     *
     * @param activity
     * @return
     */
    public static float getAllotInLVHeight(Activity activity) {
        Resources res = activity.getResources();
        int allotInHeightNotLV = getStatusBarHeight(activity) +
                (int) res.getDimension(R.dimen.title_height) +
                (int) res.getDimension(R.dimen.store_height) +
                (int) res.getDimension(R.dimen.store_height) +
                (int) res.getDimension(R.dimen.operate_height) +
                (int) res.getDimension(R.dimen.padding_reserve);
        return ScreenUtils.screenHeight() - allotInHeightNotLV;
    }

    /**
     * 获取调拨入库明细listview高度(总高度-通知栏高度-标题栏高度-门店显示区高度-发货方-条码-记录条数区高度-操作按钮高度-padding补差高度)
     *
     * @param activity
     * @return
     */
    public static float getAllotInDetailLVHeight(Activity activity) {
        Resources res = activity.getResources();
        int allotInHeightNotLV = getStatusBarHeight(activity) +
                (int) res.getDimension(R.dimen.title_height) +
                (int) res.getDimension(R.dimen.store_height) +
                (int) res.getDimension(R.dimen.shipper_height) +
                (int) res.getDimension(R.dimen.scan_height) +
                (int) res.getDimension(R.dimen.store_height) +
                (int) res.getDimension(R.dimen.operate_height) +
                (int) res.getDimension(R.dimen.padding_reserve1);
        return ScreenUtils.screenHeight() - allotInHeightNotLV;
    }

    /**
     * 通知栏的高度
     *
     * @param activity
     * @return
     */
    private static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
}
