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
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ont.connector.utils.SPWrapper;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.beans.ONSListBean;
import com.github.ont.cyanowallet.beans.ScanOnsLoginBean;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.GetOnsListReq;
import com.github.ont.cyanowallet.request.OnsLoginListReq;
import com.github.ont.cyanowallet.request.ScanGetTransactionReq;
import com.github.ont.cyanowallet.request.ScanWalletResultReq;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.ErrorUtils;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ont.cyanowallet.view.dialog.ShowOnsListDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * @author zhugang
 */
public class ScanSignTransactionActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvAddress;
    private ScanWalletResultReq scanWalletResultReq;
    private JSONObject transJson;
    private String callbackUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_sign);
        initView();
        initData();
    }

    private void initView() {
        ImageView imgBack = findViewById(R.id.img_back);
        TextView tvConfirm = findViewById(R.id.tv_confirm);
        imgBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvAddress = findViewById(R.id.tv_address);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String data = getIntent().getExtras().getString(Constant.KEY);
            try {
                transJson = new JSONObject(data);
                if (TextUtils.isEmpty(SPWrapper.getDefaultOntId())) {
                    ToastUtil.showToast(this, R.string.ontid_error);
                    finish();
                    return;
                }
                tvAddress.setText(SPWrapper.getDefaultOntId());

            } catch (Exception e) {
                ToastUtil.showToast(this, R.string.param_error);
                finish();
            }
        }
    }

    private void getData() throws JSONException {
        showLoading();
        ScanGetTransactionReq scanGetTransactionReq = new ScanGetTransactionReq(transJson.getJSONObject("params").getString("qrcodeUrl"));
        scanGetTransactionReq.setOnResultListener(new BaseRequest.ResultListener() {
            @Override
            public void onResult(Result originData) {
                dismissLoading();
                String info = (String) originData.info;
                if (info.contains("%ontid")) {
                    String defaultOntId = SPWrapper.getDefaultOntId();
                    if (TextUtils.isEmpty(defaultOntId)) {
                        dismissLoading();
                        ToastUtil.showToast(ScanSignTransactionActivity.this, R.string.ontid_error);
                        finish();
                        return;
                    }
                    info = info.replaceAll("%ontid", defaultOntId);
                }
                if (info.contains("%address")) {
                    String defaultAddress = com.github.ont.cyanowallet.utils.SPWrapper.getDefaultAddress();
                    if (TextUtils.isEmpty(defaultAddress)) {
                        dismissLoading();
                        ToastUtil.showToast(ScanSignTransactionActivity.this, R.string.address_error);
                        finish();
                        return;
                    }
                    info = info.replaceAll("%address", defaultAddress);
                }
                try {
                    boolean isOntidSign = new JSONObject(info).getJSONObject("params").getBoolean("ontidSign");
                    callbackUrl = new JSONObject(info).getJSONObject("params").getString("callback");
                    sign(info,isOntidSign);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToast(ScanSignTransactionActivity.this, R.string.param_error);
                }
            }

            @Override
            public void onResultFail(Result error) {
                dismissLoading();
                ToastUtil.showToast(ScanSignTransactionActivity.this, R.string.net_error);
                finish();
            }
        });
        scanGetTransactionReq.excute();
    }

    private void sign(final String transactionInfo, final boolean isOntidSign) throws JSONException {
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(final String walletPwd) {
                if (isOntidSign) {
                    setGetDialogPwd(new GetDialogPassword() {
                        @Override
                        public void handleDialog(String ontidPwd) {
                            handle(transactionInfo, walletPwd, ontidPwd, true);
                        }
                    });
                    showPasswordDialog("Sign Ontid");
                } else {
                    handle(transactionInfo, walletPwd, "", false);
                }
            }
        });
        showPasswordDialog("Sign Wallet");
    }

    private void handle(String transactionInfo, String walletPwd, String ontidPwd, boolean isOntidSign) {
        showLoading();
        SDKWrapper.scanTransaction(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, final Object message) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", transJson.getString("action"));
                    jsonObject.put("id", transJson.getString("id"));
                    jsonObject.put("version", transJson.getString("version"));
                    jsonObject.put("params", new JSONObject((String) message));
                    scanWalletResultReq = new ScanWalletResultReq(callbackUrl, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToast(ScanSignTransactionActivity.this, R.string.param_error);
                    dismissLoading();
                    return;
                }
                scanWalletResultReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        dismissLoading();
                        if (result.isSuccess) {
                            ToastUtil.showToast(ScanSignTransactionActivity.this, "Sign success ");
                            finish();
                        } else {
                            showAttention((String) result.info);
                        }
                    }

                    @Override
                    public void onResultFail(Result error) {
                        dismissLoading();
                        ToastUtil.showToast(ScanSignTransactionActivity.this, "net error ");
                    }
                });
                scanWalletResultReq.excute();
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                showAttention(ErrorUtils.getErrorResult(ScanSignTransactionActivity.this, message));
            }
        }, TAG, transactionInfo, walletPwd, ontidPwd, isOntidSign);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_confirm:
                try {
                    getData();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToast(this, R.string.param_error);
                }
                break;
            default:
        }
    }
}
