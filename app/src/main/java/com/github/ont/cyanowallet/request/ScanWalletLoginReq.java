/*
 * *************************************************************************************
 *   Copyright © 2014-2018 Ontology Foundation Ltd.
 *   All rights reserved.
 *
 *   This software is supplied only under the terms of a license agreement,
 *   nondisclosure agreement or other written agreement with Ontology Foundation Ltd.
 *   Use, redistribution or other disclosure of any parts of this
 *   software is prohibited except in accordance with the terms of such written
 *   agreement with Ontology Foundation Ltd. This software is confidential
 *   and proprietary information of Ontology Foundation Ltd.
 *
 * *************************************************************************************
 */

package com.github.ont.cyanowallet.request;



import com.github.ont.cyanowallet.network.net.BaseTask;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.network.volley.Request;
import com.github.ont.cyanowallet.network.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shenyin.sy on 2018/3/24.
 */

public class ScanWalletLoginReq extends BaseTask {

    private String url;
    private JSONObject jsonObject;

    public ScanWalletLoginReq(String url, JSONObject jsonObject) {
        this.url = url;
        this.jsonObject = jsonObject;
    }

    @Override
    public Result onSuccess(JSONObject jsonObject) {
        Result result = new Result(false);
        try {
            if (jsonObject.getInt("error") == 0) {
                result.isSuccess = true;
                result.info = jsonObject.getJSONObject("result").toString();
            } else {
                result.info = jsonObject.getString("desc");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result onFail(VolleyError error) {
        return new Result(false);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public JSONObject getBody() {
        return jsonObject;
    }
}
