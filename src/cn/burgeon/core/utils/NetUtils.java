package cn.burgeon.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Simon on 2014/5/22.
 */
public class NetUtils {
	private Context context;
	private TelephonyManager mTelephonyMgr;

	public NetUtils(Context context) {
		this.context = context;
		mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isWifiConnected() {
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi.toString().equals("CONNECTED")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检测服务器的接口是否有改变
	 * 
	 * @param urlStr
	 * @return
	 */
	public long getUrlFileLastModifiedTime(String urlStr) {
		boolean connected = isNetworkAvailable();
		if (connected) {
			long lastModifiedTime = 0;
			try {
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				lastModifiedTime = conn.getLastModified();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return lastModifiedTime;
		} else {
			return 0;
		}
	}

	public String getNetworkType() {
		String typeStr = "";
		int networkType = mTelephonyMgr.getNetworkType();
		if (networkType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
			typeStr = "UNKNOWN";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_GPRS) {
			typeStr = "GPRS";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_EDGE) {
			typeStr = "EDGE";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS) {
			typeStr = "UMTS";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_CDMA) {
			typeStr = "CDMA";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_EVDO_0) {
			typeStr = "EVDO_0";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_EVDO_A) {
			typeStr = "EVDO_A";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_1xRTT) {
			typeStr = "1xRTT";
		} else {
			typeStr = "UNKNOWN";
		}
		return typeStr;
	}

	public String getIMEI() {
		String IMEI = mTelephonyMgr.getDeviceId();
		if (IMEI == null) {
			IMEI = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		return IMEI;
	}

	// 获取手机的型号
	public String getMODEL() {
		return android.os.Build.MODEL;
	}

	// 获取版本号，例如1.5，1.6，2.0，2.1，2.2
	public String getPlatform() {
		return android.os.Build.VERSION.RELEASE;
	}

}
