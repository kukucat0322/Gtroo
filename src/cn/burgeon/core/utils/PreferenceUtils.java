package cn.burgeon.core.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

public class PreferenceUtils {

    public static final String Burgeon_PREF = "BurgeonPreference";
    public static final String store_key = "storeKey";
    public static final String user_key = "userKey";

    //系统配置：网络配置 关键字
    	//交互服务器
    public static final String interactiveURLAddressKey = "interactiveURLAddressKey";
    	//下载服务器
    public static final String downloadURLAddressKey = "downloadURLAddressKey";
    //系统配置：门店信息  关键字
    	//门店编号
    public static final String storeNumberKey = "storeNumberKey";
    	//顾客名称
    public static final String customerNameKey = "customerNameKey";
    	//中断编号
    public static final String terminalNumberKey = "terminalNumberKey";
    	//盘点显示行数
    public static final String inventoryRowCountKey = "inventoryRowCountKey";
    
    //系统配置：参数配置 关键字
    	//扫描条码截取位数
    public static final String scanBarcodeInterceptNumberKey = "scanBarcodeInterceptNumberKey";
    	//金额数据精度
    public static final String	moneyDataAccuracyKey = "moneyDataAccuracyKey";
    	//金额取整方式
    public static final String	moenyRoundingStyleKey = "moenyRoundingStyleKey";
    	//启用商场结算
    public static final String	enableMallsSettlementKey = "enableMallsSettlementKey";
    	//控制营业员最低折扣
    public static final String	controlSalesmanLowestRebateKey = "controlSalesmanLowestRebateKey";
    	//使用外部条码
    public static final String	useOutsideBarcodeKey = "useOutsideBarcodeKey";
    
    public static Context mContext;
    public static final int PRIVATE_MODE = ContextWrapper.MODE_PRIVATE;

    public PreferenceUtils(Context context) {
        mContext = context;
    }

    public boolean savePreferenceStr(String PrefKey, String PrefValue) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            SharedPreferences.Editor editor = cbhPref.edit();
            editor.putString(PrefKey, PrefValue);
            editor.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean savePreferenceInt(String PrefKey, Integer PrefValue) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            SharedPreferences.Editor editor = cbhPref.edit();
            editor.putInt(PrefKey, PrefValue);
            editor.commit();
            return true;
        } catch (Exception ee) {
            return false;
        }
    }

    public boolean savePreferenceLong(String PrefKey, long PrefValue) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            SharedPreferences.Editor editor = cbhPref.edit();
            editor.putLong(PrefKey, PrefValue);
            editor.commit();
            return true;
        } catch (Exception ee) {
            return false;
        }
    }

    public boolean savePreferenceBoolean(String PrefKey, boolean PrefValue) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            SharedPreferences.Editor editor = cbhPref.edit();
            editor.putBoolean(PrefKey, PrefValue);
            editor.commit();
            return true;
        } catch (Exception ee) {
            return false;
        }
    }
    
    public String getPreferenceStr(String RefKey) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            return cbhPref.getString(RefKey, "");
        } catch (Exception ee) {
            return "";
        }
    }

    public Integer getPreferenceInt(String RefKey) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            return cbhPref.getInt(RefKey, 0);
        } catch (Exception ee) {
            return 0;
        }
    }

    public long getPreferenceLong(String RefKey) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            return cbhPref.getLong(RefKey, 0);
        } catch (Exception ee) {
            return 0;
        }
    }

    public boolean getPreferenceBoolean(String RefKey) {
        try {
            SharedPreferences cbhPref = new ContextWrapper(mContext).getSharedPreferences(Burgeon_PREF, PRIVATE_MODE);
            return cbhPref.getBoolean(RefKey, false);
        } catch (Exception ee) {
            return false;
        }
    }

}