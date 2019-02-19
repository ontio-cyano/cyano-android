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
import com.github.ont.cyanowallet.network.volley.VolleyError;
import com.github.ont.cyanowallet.network.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liutao17 on 2016/6/23.
 */
public class BaseRequest extends JsonObjectRequest {

    private static final String UTF8 = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json;");
    private ParseTool parser;
    private JSONObject params;
    private ResultListener listener;

    public BaseRequest(int method, String url, ParseTool parser, JSONObject json, ResultListener listener) {
        super(method, url, null, null, null);
        this.parser = parser;
        this.listener = listener;
        this.params = json;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;");
        headers.put("Accept", "*/*");
        return headers;
    }

    @Override
    public byte[] getBody(){
        try {
            return params.toString().getBytes(UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        if (parser != null) {
            Result result = parser.onSuccess(response);
            if (result != null && listener != null) {
                listener.onResult(result);
            }
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if (parser != null) {
            Result result = parser.onFail(error);
            if (listener != null) {
                listener.onResultFail(result);
            }
        }
    }

    public interface ResultListener {
        void onResult(Result result);
        void onResultFail(Result error);
    }
}
