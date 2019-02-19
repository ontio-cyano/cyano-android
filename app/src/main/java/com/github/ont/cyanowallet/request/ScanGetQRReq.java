package com.github.ont.cyanowallet.request;

import com.github.ont.cyanowallet.network.net.BaseTask;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.network.volley.Request;
import com.github.ont.cyanowallet.network.volley.VolleyError;

import org.json.JSONObject;

/**
 * @author zhugang
 */
public class ScanGetQRReq extends BaseTask {

    private String mUrl;

    public ScanGetQRReq(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public JSONObject getBody() {
        return null;
    }

    @Override
    public Result onSuccess(JSONObject jsonObject) {
        Result result = new Result(true);
        result.info = jsonObject.toString();
        return result;
    }

    @Override
    public Result onFail(VolleyError error) {
        Result result = new Result(false);
        return result;
    }
}
