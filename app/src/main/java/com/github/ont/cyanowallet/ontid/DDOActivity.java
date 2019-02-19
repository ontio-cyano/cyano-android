package com.github.ont.cyanowallet.ontid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.common.Address;

/**
 * @author zhugang
 */
public class DDOActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "DDOActivity";

    public static final int ADD_CONTROLER = 0;
    public static final int ADD_RECOVER = 1;
    public static final int UPDATE_RECOVER = 2;
    private int key;
    private TextView tvDes;
    private TextView tvAddress;
    private TextView tvAddDes;
    private View btnConfirm;
    private EditText etWallet;
    private EditText etPubkey;
    private EditText etOntidPwd;
    private String oldAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddo);
        initView();
        initData();
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            key = getIntent().getExtras().getInt(Constant.KEY);
            oldAddress = getIntent().getExtras().getString(Constant.ADDRESS);
        }
        switch (key) {
            case ADD_CONTROLER:
                tvDes.setText("Add controller");
                tvAddDes.setText("Public key");
                break;
            case ADD_RECOVER:
                tvDes.setText("Add recover");
                tvAddDes.setText("Recover address");
                break;
            case UPDATE_RECOVER:
                tvDes.setText("Update recover");
                tvAddDes.setText("Recover address");
                break;
            default:
        }
        if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
            showAttention("need wallet");
            btnConfirm.setVisibility(View.GONE);
        } else {
            tvAddress.setText(SPWrapper.getDefaultAddress());
        }
    }

    private void initView() {
        View viewBack = findViewById(R.id.layout_back);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvDes = (TextView) findViewById(R.id.tv_des);
        tvAddDes = (TextView) findViewById(R.id.tv_add_des);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        etWallet = (EditText) findViewById(R.id.et_wallet);
        etPubkey = (EditText) findViewById(R.id.et_pubkey);
        etOntidPwd = (EditText) findViewById(R.id.et_ontid_pwd);

        viewBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_confirm:
                confirm();
                break;
            default:
        }
    }

    private void confirm() {
        String ontidPwd = etOntidPwd.getText().toString().trim();
        String addData = etPubkey.getText().toString().trim();
        String walletPwd = etWallet.getText().toString().trim();
        if (TextUtils.isEmpty(ontidPwd) || TextUtils.isEmpty(addData) || TextUtils.isEmpty(walletPwd)) {
            ToastUtil.showToast(this, "please fill in all");
            return;
        }



        switch (key) {
            case ADD_CONTROLER:
                try {
                    Address.addressFromPubKey(addData).toBase58();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(this, "public key error");
                    return;
                }
                showLoading();
                SDKWrapper.addDDOController(new SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        dismissLoading();
                        ToastUtil.showToast(DDOActivity.this, "Success");
                        finish();
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                        showAttention(message);
                    }
                }, TAG, addData, ontidPwd, walletPwd);
                break;
            case ADD_RECOVER:
                if (!CommonUtil.isAddress(addData)) {
                    ToastUtil.showToast(this, "Address error");
                    return;
                }
                showLoading();
                SDKWrapper.addDDORecover(new SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        dismissLoading();
                        ToastUtil.showToast(DDOActivity.this, "Success");
                        finish();
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                        showAttention(message);
                    }
                }, TAG, addData, ontidPwd, walletPwd);
                break;
            case UPDATE_RECOVER:
                if (!CommonUtil.isAddress(addData)) {
                    ToastUtil.showToast(this, "Address error");
                    return;
                }
                showLoading();
                SDKWrapper.updateDDORecover(new SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        dismissLoading();
                        ToastUtil.showToast(DDOActivity.this, "Success");
                        finish();
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                        showAttention(message);
                    }
                }, TAG, addData, oldAddress, ontidPwd, walletPwd);
                break;
            default:
                dismissLoading();
        }

    }
}
