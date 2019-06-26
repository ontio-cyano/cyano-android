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
import com.github.ont.cyanowallet.request.OnsLoginListReq;
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
public class ScanOnsLoginActivity extends BaseActivity implements View.OnClickListener {
    private String data;
    private ScanOnsLoginBean scanOnsLoginBean;
    private TextView tvAddress;
    private ScanWalletResultReq scanWalletResultReq;

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
            data = getIntent().getExtras().getString(Constant.KEY);
            try {
                scanOnsLoginBean = com.alibaba.fastjson.JSONObject.parseObject(data, ScanOnsLoginBean.class);
                if (scanOnsLoginBean == null) {
                    ToastUtil.showToast(this, R.string.param_error);
                    finish();
                } else {
                    if (TextUtils.isEmpty(SPWrapper.getDefaultOntId())) {
                        ToastUtil.showToast(this, R.string.ontid_error);
                        finish();
                        return;
                    }
                    tvAddress.setText(SPWrapper.getDefaultOntId());
                }
            } catch (Exception e) {
                ToastUtil.showToast(this, R.string.param_error);
                finish();
            }
        }
    }

    private void sign(final String address) {
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                handle(address, pwd);
            }
        });
        showPasswordDialog("Ons Login");
    }

    private void handle(String address, String pwd) {
        final ScanOnsLoginBean.ParamsBean params = scanOnsLoginBean.getParams();
        SDKWrapper.scanLoginSignOns(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, final Object message) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", scanOnsLoginBean.getAction());
                    jsonObject.put("id", scanOnsLoginBean.getId());
                    jsonObject.put("version", scanOnsLoginBean.getVersion());
                    jsonObject.put("params", new JSONObject((String) message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                scanWalletResultReq = new ScanWalletResultReq(params.getCallback(), jsonObject);
                scanWalletResultReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        dismissLoading();
                        if (result.isSuccess) {
                            ToastUtil.showToast(ScanOnsLoginActivity.this, "login success ");
                            finish();
                        } else {
                            showAttention((String) result.info);
                        }
                    }

                    @Override
                    public void onResultFail(Result error) {
                        dismissLoading();
                        ToastUtil.showToast(ScanOnsLoginActivity.this, "net error ");
                    }
                });
                scanWalletResultReq.excute();
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                showAttention(ErrorUtils.getErrorResult(ScanOnsLoginActivity.this, message));
            }
        }, TAG, params.getMessage(), address, pwd, Constant.ONTID, params.getDomain());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_confirm:
                confirm();
                break;
            default:
        }
    }

    private void confirm() {
        ScanOnsLoginBean.ParamsBean params = scanOnsLoginBean.getParams();
        showLoading();
        OnsLoginListReq onsLoginListReq = new OnsLoginListReq(SPWrapper.getDefaultOntId(), params.getDomain());
        onsLoginListReq.setOnResultListener(new BaseRequest.ResultListener() {
            @Override
            public void onResult(Result result) {
                dismissLoading();
                if (result.isSuccess) {
                    ONSListBean onsListBean = com.alibaba.fastjson.JSONObject.parseObject((String) result.info, ONSListBean.class);
                    if (onsListBean != null && onsListBean.getCode() == 0) {
                        List<String> onsList = onsListBean.getResult();
                        ShowOnsListDialog showOnsListDialog = new ShowOnsListDialog(ScanOnsLoginActivity.this, onsList);
                        showOnsListDialog.setOnChooseListener(new ShowOnsListDialog.OnChooseListener() {
                            @Override
                            public void onChooseSuccess(String address) {
                                sign(address);
                            }
                        });
                        showOnsListDialog.show();
                    } else {
                        ToastUtil.showToast(ScanOnsLoginActivity.this, "get ons list fail");
                    }
                }
            }

            @Override
            public void onResultFail(Result error) {
                dismissLoading();
                ToastUtil.showToast(ScanOnsLoginActivity.this, "get ons list fail");
            }
        });
        onsLoginListReq.excute();
    }
}
