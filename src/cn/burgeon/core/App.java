package cn.burgeon.core;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.util.DisplayMetrics;
import cn.burgeon.core.bean.Employee;
import cn.burgeon.core.db.DbHelper;
import cn.burgeon.core.ic.ImageCacheManager;
import cn.burgeon.core.ic.ImageCacheManager.CacheType;
import cn.burgeon.core.net.RequestManager;
import cn.burgeon.core.net.SimonHttpStack;
import cn.burgeon.core.utils.NetUtils;
import cn.burgeon.core.utils.PreferenceUtils;

public class App extends Application {
    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so quality is ignored but must be provided
    private static String HOSTURL = "http://g.burgeon.cn:90/servlets/binserv/Rest";

    private DbHelper helper;
    protected SQLiteDatabase db;
    private ArrayList<Employee> employees = new ArrayList<Employee>();
    
    private static App singleton;

    public static App getInstance() {
        return singleton;
    }

    private static NetUtils netUtils;

    public static NetUtils getNetUtils() {
        return netUtils;
    }
    
    private static SimonHttpStack simonHttpStack;

    public static SimonHttpStack getHttpStack() {
        return simonHttpStack;
    }

    private static PreferenceUtils preferenceUtils;

    public static PreferenceUtils getPreferenceUtils() {
        return preferenceUtils;
    }

    private static DisplayMetrics DM;

    public static DisplayMetrics getDM() {
        return DM;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    /**
     * Intialize the request manager and the image cache
     */
    private void init() {
    	singleton = this;
    	
    	netUtils = new NetUtils(getApplicationContext());
    	
        simonHttpStack = new SimonHttpStack();
        RequestManager.init(this, simonHttpStack);

        createImageCache();

        preferenceUtils = new PreferenceUtils(getApplicationContext());
        DM = getResources().getDisplayMetrics();

        helper = new DbHelper(this);
        db = helper.getWritableDatabase();
        App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.config_pswd, "87654321");
        App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.interactiveURLAddressKeySuffix, "/servlets/binserv/Rest");
        App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.downloadURLAddressKeySuffix, "/DownloadFiles");
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.
     */
    private void createImageCache() {
        ImageCacheManager.getInstance().init(this, this.getPackageCodePath(), DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY, CacheType.MEMORY);
    }
   
    public static String getHosturl() {
    	return App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.interactiveURLAddressKey) + App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.interactiveURLAddressKeySuffix);
    }

    public SQLiteDatabase getDB(){
    	return db;
    }
    
    public void clearEmployee(){
    	if(!employees.isEmpty())
    		employees.clear();
    }
    
    public ArrayList<Employee> getEmployees(){
    	return employees;
    }

}
