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

package com.github.ont.cyanowallet.scan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.ScanInvokeCallbackReq;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScanWalletInvokeActivity extends BaseActivity implements View.OnClickListener {

    TextView name;
    TextView fromAddress;

    private static final String TAG = "ScanWalletLoginActivity";

    private String address;
    private String qrcodeUrl;
    private String callback;
    private String id;
    private String version;
    private ScanInvokeCallbackReq scanInvokeCallbackReq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wallet_invoke);
        initView();
        initData();
    }

    private void initView() {
        View confirm = findViewById(R.id.confirm);
        View layout_back = findViewById(R.id.layout_back);
        fromAddress = findViewById(R.id.from_address);
        confirm.setOnClickListener(this);
        layout_back.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String data = bundle.getString(Constant.KEY, "");
//            "login": true,
//                    "qrcodeUrl": "http://101.132.193.149:4027/qrcode/AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ",
//                    "message": "will pay 1 ONT in this transaction",
            address = bundle.getString(Constant.ADDRESS, "");
            fromAddress.setText(address);
            try {
                JSONObject jsonObject = new JSONObject(data);
                qrcodeUrl = jsonObject.getString("qrcodeUrl");
                callback = jsonObject.getString("callback");
                id = jsonObject.getString("id");
                version = jsonObject.getString("version");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void showSecretDialog() {
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                req(pwd);
            }
        });
        showPasswordDialog("scan invoke");
    }

    private void req(String password) {
        showLoading();
        SDKWrapper.scanAddSign(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, final Object message) {
                dismissLoading();
                ArrayList<String> result = (ArrayList<String>) message;
                if (result != null && result.size() > 1) {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject((String) result.get(0));
                    JSONArray notify = jsonObject.getJSONArray("Notify");
                    if (notify != null && notify.size() > 0) {
                        showChooseDialog((String) result.get(0), (String) result.get(1), "");
                    } else {
                        sendTransaction((String) result.get(1), "");
                    }
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                String error = null;
                try {
                    int errorCode = new JSONObject(message).optInt("Error");
                    switch (errorCode) {
                        case 51015:
                        case 58018:
                            error = "password error";
                            break;
                        case 58004:
                            error = "address error";
                            break;
                        case 47001:
                            error = "insufficient balance";
                            break;
                        default:
                            error = "system error " + errorCode;
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error == null) {
                    error = "system error ";
                }
                showAttention(error);
            }
        }, TAG, qrcodeUrl, address, password);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanInvokeCallbackReq != null) {
            scanInvokeCallbackReq.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                showSecretDialog();
                break;
            case R.id.layout_back:
                finish();
                break;
            default:
        }
    }

    @Override
    public void setHash(String txData, String data) {
        if (txData != null) {
            if (TextUtils.isEmpty(callback)) {
                dismissLoading();
                ToastUtil.showToast(ScanWalletInvokeActivity.this, "Success");
                finish();
            } else {
                Map map = new HashMap();
                try {
                    map.put("action", new JSONObject(data).getString("action"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("version", version);
                map.put("id", id);
                map.put("error", 0);
                map.put("desc", "SUCCESS");
                map.put("result", txData);
                try {
                    scanInvokeCallbackReq = new ScanInvokeCallbackReq(callback, new JSONObject(JSON.toJSONString(map)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                scanInvokeCallbackReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        dismissLoading();
                        ToastUtil.showToast(ScanWalletInvokeActivity.this, "success");
                        finish();
                    }

                    @Override
                    public void onResultFail(Result error) {
                        dismissLoading();
                        ToastUtil.showToast(ScanWalletInvokeActivity.this, "success");
                        finish();
                    }
                });
                scanInvokeCallbackReq.excute();
            }
        } else {
            dismissLoading();
            ToastUtil.showToast(ScanWalletInvokeActivity.this, "fail");
        }
    }
}
