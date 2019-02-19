/*
 * **************************************************************************************
 *  Copyright © 2014-2018 Ontology Foundation Ltd.
 *  All rights reserved.
 *
 *  This software is supplied only under the terms of a license agreement,
 *  nondisclosure agreement or other written agreement with Ontology Foundation Ltd.
 *  Use, redistribution or other disclosure of any parts of this
 *  software is prohibited except in accordance with the terms of such written
 *  agreement with Ontology Foundation Ltd. This software is confidential
 *  and proprietary information of Ontology Foundation Ltd.
 *
 * **************************************************************************************
 */

package com.github.ont.cyanowallet.network;

import android.content.Context;

import com.github.ont.cyanowallet.main.AppApplication;
import com.github.ont.cyanowallet.network.volley.Request;
import com.github.ont.cyanowallet.network.volley.RequestQueue;
import com.github.ont.cyanowallet.network.volley.toolbox.Volley;

/**
 * Created by shenyin.sy on 2018/2/18.
 */

public class VolleySingleton {
    private static String TAG = "VolleySingleton";
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;


    private VolleySingleton() {
        mRequestQueue = getRequestQueue();
    }

    public static void setContext(Context context) {
        mCtx = context;
    }

    public static synchronized VolleySingleton getInstance() {
        if (mInstance == null) {
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(AppApplication.getContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


    public void cancle(String tag) {
        if (tag != null && mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void clearCache() {
        if (mRequestQueue != null) {
            mRequestQueue.getCache().clear();
        }
    }
}
