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
import com.github.ont.cyanowallet.utils.SPWrapper;

import org.json.JSONObject;

/**
 * Created by shenyin.sy on 2018/3/24.
 */

public class Oep4ListReq extends BaseTask {


    @Override
    public Result onSuccess(JSONObject jsonObject) {
        Result result = new Result(false);
        if (null == jsonObject) {
            return result;
        }
        int error = jsonObject.optInt("Error");
        if (error == 0) {
            result.isSuccess = true;
            result.info = jsonObject.optJSONObject("Result").toString();
        } else {
            result.isSuccess = false;
        }
        return result;
    }

    @Override
    public Result onFail(VolleyError error) {
        return new Result(false);
    }

    @Override
    public String getUrl() {
        if (SPWrapper.getDefaultNet().contains("dappnode")) {
            return Constant.EXPLORER_SERVER + getPath();
        } else if (SPWrapper.getDefaultNet().contains("polaris")) {
            return Constant.EXPLORER_TEST_SERVER + getPath();
        } else {
            return "";
        }
    }

    @Override
    public String getPath() {
        return Constant.GET_OEP4_LIST;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public JSONObject getBody() {
        return null;
    }
}
