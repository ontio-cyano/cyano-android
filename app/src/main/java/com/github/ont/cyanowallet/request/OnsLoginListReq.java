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
import com.github.ont.cyanowallet.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shenyin.sy on 2018/3/24.
 */

public class OnsLoginListReq extends BaseTask {

    private String ontid;
    private String domain;

    public OnsLoginListReq(String ontid, String domain) {
        this.ontid = ontid;
        this.domain = domain;
    }

    @Override
    public Result onSuccess(JSONObject jsonObject) {
        Result result = new Result(true);
        result.info = jsonObject.toString();
        return result;
    }

    @Override
    public Result onFail(VolleyError error) {
        return new Result(false);
    }

    @Override
    public String getUrl() {
        return Constant.ONGS_URL;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public JSONObject getBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ontid", ontid);
            jsonObject.put("domain", domain);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
