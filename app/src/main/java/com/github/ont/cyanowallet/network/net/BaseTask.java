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

package com.github.ont.cyanowallet.network.net;

import com.github.ont.cyanowallet.network.volley.AuthFailureError;
import com.github.ont.cyanowallet.network.volley.DefaultRetryPolicy;
import com.github.ont.cyanowallet.network.volley.Request;
import com.github.ont.cyanowallet.network.volley.Response;
import com.github.ont.cyanowallet.network.volley.VolleyError;
import com.github.ont.cyanowallet.network.volley.VolleyLog;
import com.github.ont.cyanowallet.network.volley.toolbox.JsonObjectRequest;
import com.github.ont.cyanowallet.network.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liutao17 on 2016/6/23.
 */
public abstract class BaseTask implements ParseTool {
    public static String TAG = "BaseTask";
    private BaseRequest.ResultListener listener;
    private String requestTag;
    private VolleySingleton singleton;

    public abstract String getUrl();

    public abstract String getPath();

    public abstract int getMethod();

    public abstract JSONObject getBody();

    public String getRequestTag() {
        return requestTag;
    }

    public void setRequestTag(String requestTag) {
        this.requestTag = requestTag;
    }

    public void setOnResultListener(BaseRequest.ResultListener listener) {
        this.listener = listener;
    }

    public void excute() {
        Request request = new BaseRequest(getMethod(), getUrl(), this, getBody(), listener);
        AddToQueue(request);
    }



    private void AddToQueue(Request request) {
        if (requestTag != null) {
            request.setTag(requestTag);
        }
        request.setRetryPolicy(new DefaultRetryPolicy(10000,//默认超时时间，应设置一个稍微大点儿的，例如本处的 10000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        singleton = VolleySingleton.getInstance();
        singleton.clearCache();
        singleton.addToRequestQueue(request);
    }

    //this function is to test url, if you find volley behavior strange, you can test it here
    public void excute1() {
        String URL = "";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json;");
                headers.put("Accept", "*/*");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return BaseTask.this.getBody().toString().getBytes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        };


        VolleySingleton.getInstance().addToRequestQueue(req);
    }

    public void cancel() {
        if (singleton != null && requestTag != null) {
            singleton.cancle(requestTag);
        }

    }
}
