package cn.burgeon.core.net;

import android.net.http.AndroidHttpClient;

import com.android.volley.toolbox.HttpClientStack;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class SimonHttpStack extends HttpClientStack {

    public SimonHttpStack() {
        super(AndroidHttpClient.newInstance("simon"));
    }

    public interface OnStartListener {
        void onStart(HttpUriRequest request);
    }

    OnStartListener mOnStartListener;

    @Override
    protected void onPrepareRequest(HttpUriRequest request) throws IOException {
        super.onPrepareRequest(request);
        if (mOnStartListener != null)
            mOnStartListener.onStart(request);
    }

    public void setOnStartListener(OnStartListener listener) {
        this.mOnStartListener = listener;
    }

}