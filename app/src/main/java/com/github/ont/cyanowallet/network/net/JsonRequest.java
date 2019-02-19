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

import com.github.ont.cyanowallet.network.volley.NetworkResponse;
import com.github.ont.cyanowallet.network.volley.ParseError;
import com.github.ont.cyanowallet.network.volley.Request;
import com.github.ont.cyanowallet.network.volley.Response;
import com.github.ont.cyanowallet.network.volley.VolleyError;
import com.github.ont.cyanowallet.network.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by liutao17 on 2016/6/22.
 */
public abstract class JsonRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> mListener;

    public JsonRequest(int method, String url, Response.ErrorListener listener) {
        super(method, "", listener);
    }
    protected abstract Map<String, String> getParams();

    public abstract String getUrl();

    public abstract int getMethod();

    public void setOnResultListener(Response.Listener listener)
    {
        mListener = listener;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        if(mListener != null)
        {
            mListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }
}
