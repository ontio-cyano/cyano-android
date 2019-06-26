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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.ScanWalletResultReq;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ont.cyanowallet.view.PasswordDialog;

import org.json.JSONException;
import org.json.JSONObject;


public class WakeWalletLoginActivity extends WakeActivity implements View.OnClickListener {

    TextView name;
    TextView fromAddress;
    ImageView imgAnchor;

    private static final String TAG = "ScanWalletLoginActivity";

    //    private ArrayList<String> addrs = new ArrayList<>();
    private PasswordDialog passwordDialog;
    private String address;
    private String message;
    private String url;
    private String id;
    private String version;
    private String type;
    private ScanWalletResultReq scanWalletLoginReq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_wallet_login);
        initData();
        initView();
    }

    private void initView() {
//        List<Account> accounts = SettingSingleton.getInstance().getAccounts();
//        if (accounts != null && accounts.size() > 1) {
//            imgAnchor.setVisibility(View.VISIBLE);
//        } else {
//        }

        RelativeLayout layoutBack = findViewById(R.id.layout_back);
        TextView confirm = findViewById(R.id.confirm);
        name = findViewById(R.id.name);

        fromAddress = findViewById(R.id.from_address);
        imgAnchor = findViewById(R.id.img_anchor);

        imgAnchor.setVisibility(View.GONE);
        if (TextUtils.isEmpty(type)) {
            type="account";
            address = SPWrapper.getDefaultAddress();
        }
        switch (type) {
            case "ontid":
                if (TextUtils.isEmpty(com.github.ont.connector.utils.SPWrapper.getDefaultOntId())) {
                    showAttention("NO ONT ID,please register");
                }
                address = com.github.ont.connector.utils.SPWrapper.getDefaultOntId();
                break;
            default:
                if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
                    showAttention("NO Address,please register");
                }
                address = SPWrapper.getDefaultAddress();
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtil.showToast(this, "No " + type);
            finish();
            overridePendingTransition(R.anim.out_top_to_bottom, R.anim.in_bottom_to_top);
        }
        fromAddress.setText(address);
        layoutBack.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String data = bundle.getString(Constant.KEY, "");
//            "type": "ontid or account",
//                    "dappName": "dapp Name",
//                    "dappIcon": "dapp Icon",
//                    "message": "helloworld",
//                    "expire": 1546415363,
//                    "callback": "http://127.0.0.1:80/login/callback"

            version = bundle.getString(Constant.VERSION, "");
            id = bundle.getString(Constant.ID, "");
            try {
                JSONObject jsonObject = new JSONObject(data);
                message = jsonObject.getString("message");
                url = jsonObject.getString("callback");
                type = jsonObject.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        List<Account> accounts = SettingSingleton.getInstance().getAccounts();
//        for (Account ac : accounts) {
////            if (!TextUtils.equals(ac.address, SettingSingleton.getInstance().getDefaultAddress())) {
//            addrs.add(ac.address);
////            }
//        }
    }

    private void showSecretDialog() {
        if (passwordDialog != null && passwordDialog.isShowing()) {
            passwordDialog.dismiss();
        }
        if (passwordDialog == null) {
            passwordDialog = new PasswordDialog(this);
            passwordDialog.setConfirmListener(new PasswordDialog.ConfirmListener() {
                @Override
                public void passwordConfirm(String password) {
                    passwordDialog.dismiss();
                    showLoading();
                    req(password);
                }
            });
        }
        passwordDialog.show();
    }

    private void req(String password) {
        SDKWrapper.scanLoginSign(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, final Object message) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "login");
                    jsonObject.put("id", id);
                    jsonObject.put("version", version);
                    jsonObject.put("params", new JSONObject((String) message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                scanWalletLoginReq = new ScanWalletResultReq(url, jsonObject);
                scanWalletLoginReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        dismissLoading();
                        if (result.isSuccess) {
                            ToastUtil.showToast(WakeWalletLoginActivity.this, "success");
                            finish();
                            overridePendingTransition(R.anim.out_top_to_bottom, R.anim.in_bottom_to_top);
                        } else {
                            if (result.info != null) {
                                showAttention(result.info.toString());
                            }
                        }
                    }

                    @Override
                    public void onResultFail(Result error) {
                        dismissLoading();
                        if (error.info != null) {
                            showAttention(error.info.toString());
                        }
                        ToastUtil.showToast(WakeWalletLoginActivity.this, "net error ");
                    }
                });
                scanWalletLoginReq.excute();
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                String error = null;
                try {
                    int errorCode = new JSONObject(message).optInt("Error");
                    switch (errorCode) {
                        case 51015:
                            error = "password error ";
                            break;
                        case 58004:
                            error = "address error ";
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
        }, TAG, message, address, password, type);
    }
//多钱包
//    private PopupWindow pop;
//
//    private void showAddresses(View view) {
//        if (pop == null) {
//            pop = new PopupWindow(this);
//            View v = LayoutInflater.from(this).inflate(R.layout.address_pop, null);
//            ListView listView = v.findViewById(R.id.lv);
//            AddressSelectWithNameAdapter adapter = new AddressSelectWithNameAdapter(this, addrs);
//            listView.setAdapter(adapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    address = addrs.get(position);
//                    fromAddress.setText(address);
//                    if (pop != null && pop.isShowing()) {
//                        pop.dismiss();
//                    }
//                }
//            });
//            if (adapter.getCount() > 2) {
//                int totalHeight = 0;
//                View listItem = adapter.getView(1, null, listView);
//                listItem.measure(0, 0);
//                totalHeight = (int) Math.floor(listItem.getMeasuredHeight() * 2.5);
//                ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
//                layoutParams.height = totalHeight;
//                listView.setLayoutParams(layoutParams);
//            }
//            pop.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
//            pop.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
//            pop.setContentView(v);
//            pop.setBackgroundDrawable(new ColorDrawable());
//            pop.setOutsideTouchable(true);
//        }
//        pop.setFocusable(true);
//        if (pop.isShowing()) {
//            pop.dismiss();
//        } else {
//            pop.showAsDropDown(view);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (passwordDialog != null) {
            passwordDialog.dismiss();
        }
        if (scanWalletLoginReq != null) {
            scanWalletLoginReq.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_anchor:
//                showAddresses(view);
                break;
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
}
