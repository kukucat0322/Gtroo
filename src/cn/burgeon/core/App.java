package cn.burgeon.core;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.util.DisplayMetrics;
import cn.burgeon.core.db.DbHelper;
import cn.burgeon.core.ic.ImageCacheManager;
import cn.burgeon.core.ic.ImageCacheManager.CacheType;
import cn.burgeon.core.net.RequestManager;
import cn.burgeon.core.net.SimonHttpStack;
import cn.burgeon.core.utils.PreferenceUtils;

public class App extends Application {
    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so quality is ignored but must be provided
    private static String HOSTURL = "http://g.burgeon.cn:90/servlets/binserv/Rest";
    private static final String SIPKEY = "nea@burgeon.com.cn";
    private static final String SIPPSWD = "pbdev";
    private SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String SIPPSWDMD5;
    private DbHelper helper;
    protected SQLiteDatabase db;

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
        simonHttpStack = new SimonHttpStack();
        RequestManager.init(this, simonHttpStack);

        createImageCache();

        preferenceUtils = new PreferenceUtils(getApplicationContext());
        DM = getResources().getDisplayMetrics();

        SDF.setLenient(false);
        SIPPSWDMD5 = MD5(SIPPSWD);
        
        helper = new DbHelper(this);
        db = helper.getWritableDatabase();
    }

    public String MD5(String s) {
        String r = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            r = buf.toString();
        } catch (Exception e) {
        }
        return r;
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.
     */
    private void createImageCache() {
        ImageCacheManager.getInstance().init(this, this.getPackageCodePath(), DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY, CacheType.MEMORY);
    }
   
    public static String getHosturl() {
    	return App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.interactiveURLAddressKey);
    }

    public static String getSipkey() {
        return SIPKEY;
    }

    public static String getSippswd() {
        return SIPPSWD;
    }

    public SimpleDateFormat getSDF() {
        return SDF;
    }

    public String getSIPPSWDMD5() {
        return SIPPSWDMD5;
    }
    
    public SQLiteDatabase getDB(){
    	return db;
    }

}
