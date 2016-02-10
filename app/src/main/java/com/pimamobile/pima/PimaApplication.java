package com.pimamobile.pima;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class PimaApplication extends Application {

    private RequestQueue mRequestQueue;
    private static PimaApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized PimaApplication getmInstance() {
        return mInstance;
    }

    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToReqQueue(Request<T> request, String tag) {
        getmRequestQueue().add(request);
    }

    public void cancelPendingReq(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
