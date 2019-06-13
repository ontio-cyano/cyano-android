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

package com.github.ont.cyanowallet.wake;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.ScanInvokeCallbackReq;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.DAppUtils;
import com.github.ont.cyanowallet.utils.ErrorUtils;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

import java.util.ArrayList;

/**
 * @author zhugang
 */
public class WakeWalletInvokeActivity extends WakeActivity implements View.OnClickListener {

    TextView name;
    TextView fromAddress;

    private static final String TAG = "ScanWalletLoginActivity";

    private String address;
    private String qrcodeUrl;
    private String callback;
    private String id;
    private String version;
    private String data;
    private ScanInvokeCallbackReq scanInvokeCallbackReq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_wallet_invoke);
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
            data = bundle.getString(Constant.KEY, "");
//            "login": true,
//                    "qrcodeUrl": "http://101.132.193.149:4027/qrcode/AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ",
//                    "message": "will pay 1 ONT in this transaction",
//                    "callback": "http://101.132.193.149:4027/invoke/callback",
            address = bundle.getString(Constant.ADDRESS, "");
            id = bundle.getString(Constant.ID, "");
            version = bundle.getString(Constant.VERSION, "");
            fromAddress.setText(address);
            try {
                JSONObject jsonObject = JSONObject.parseObject(data).getJSONObject("params");
                qrcodeUrl = jsonObject.getString("qrcodeUrl");
                callback = jsonObject.getString("callback");
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
        if (TextUtils.isEmpty(qrcodeUrl)) {
            if (DAppUtils.checkData(data, Constant.INVOKE)) {
                handleInvokeTransaction(data, password);
            } else {
                ToastUtil.showToast(this, R.string.param_error);
                finish();
            }
        } else {
            showLoading();
            SDKWrapper.scanAddSign(new SDKCallback() {
                @Override
                public void onSDKSuccess(String tag, final Object message) {
                    dismissLoading();
                    ArrayList<String> result = (ArrayList<String>) message;
                    if (result != null && result.size() > 1) {
                        JSONObject jsonObject = JSONObject.parseObject((String) result.get(0));
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
                    showAttention(ErrorUtils.getErrorResult(WakeWalletInvokeActivity.this, message));
                }
            }, TAG, qrcodeUrl, address, password);
        }
    }

    private void handleInvokeTransaction(final String data, String password) {
        showLoading();
        SDKWrapper.getSendAddress(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                Log.i(TAG, "onSDKSuccess: " + message);
                ArrayList<String> result = (ArrayList<String>) message;

                if (result != null && result.size() > 1) {
                    JSONObject jsonObject = JSONObject.parseObject((String) result.get(0));
                    JSONArray notify = jsonObject.getJSONArray("Notify");
                    if (notify != null && notify.size() > 0) {
                        dismissLoading();
                        showChooseDialog((String) result.get(0), (String) result.get(1), data);
                    } else {
                        dismissLoading();
                        sendTransaction((String) result.get(1), data);
                    }
                } else {
                    ToastUtil.showToast(WakeWalletInvokeActivity.this, R.string.param_error);
                    dismissLoading();
                    finish();
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(WakeWalletInvokeActivity.this, ErrorUtils.getErrorResult(WakeWalletInvokeActivity.this, message));
                finish();
            }
        }, TAG, data, password, SPWrapper.getDefaultAddress());
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
                overridePendingTransition(R.anim.out_top_to_bottom, R.anim.in_bottom_to_top);
                break;
            default:
        }
    }

    @Override
    public void setHash(String txData, String data) {
        if (txData != null) {
            if (TextUtils.isEmpty(callback)) {
                dismissLoading();
                ToastUtil.showToast(WakeWalletInvokeActivity.this, "Success");
                finish();
                overridePendingTransition(R.anim.out_top_to_bottom, R.anim.in_bottom_to_top);
            } else {
                JSONObject map = new JSONObject();
                map.put("action", JSONObject.parseObject(data).getString("action"));
                map.put("version", version);
                map.put("id", id);
                map.put("error", 0);
                map.put("desc", "SUCCESS");
                map.put("result", txData);
                try {
                    scanInvokeCallbackReq = new ScanInvokeCallbackReq(callback, new org.json.JSONObject(JSON.toJSONString(map)));
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
                scanInvokeCallbackReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        dismissLoading();
                        ToastUtil.showToast(WakeWalletInvokeActivity.this, "success");
                        finish();
                        overridePendingTransition(R.anim.out_top_to_bottom, R.anim.in_bottom_to_top);
                    }

                    @Override
                    public void onResultFail(Result error) {
                        dismissLoading();
                        ToastUtil.showToast(WakeWalletInvokeActivity.this, "success");
                        finish();
                        overridePendingTransition(R.anim.out_top_to_bottom, R.anim.in_bottom_to_top);
                    }
                });
                scanInvokeCallbackReq.excute();
            }
        } else {
            dismissLoading();
            ToastUtil.showToast(WakeWalletInvokeActivity.this, "fail");
        }
    }
}
