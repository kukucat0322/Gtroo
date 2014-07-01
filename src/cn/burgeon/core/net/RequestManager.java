package cn.burgeon.core.net;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

import android.content.Context;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Manager for the queue
 *
 * @author Trey Robinson
 */
public class RequestManager {

    /**
     * the queue :-)
     */
    private static RequestQueue mRequestQueue;

    /**
     * Nothing to see here.
     */
    private RequestManager() {
        // no instances
    }

    /**
     * @param context application context
     */
    public static void init(Context context, HttpStack httpStack) {
        mRequestQueue = Volley.newRequestQueue(context, httpStack);
    }

    /**
     * @return instance of the queue
     * @throws IllegalStatException if init has not yet been called
     */
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }
}
