package com.github.ont.cyanowallet.ontid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.beans.DDOBean;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

/**
 * @author zhugang
 */
public class DDOAttriActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "DDOAttriActivity";

    private DDOBean.AttributesBean bean;
    private TextView tvKey;
    private TextView tvType;
    private TextView tvWallet;

    private EditText etValue;
    private EditText etOntidPwd;
    private EditText etWalletPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddo_attr);
        initView();
        initData();
    }

    private void initView() {
        View btnUpdate = findViewById(R.id.btn_update);
        View layoutBack = findViewById(R.id.layout_back);
        tvKey = (TextView) findViewById(R.id.tv_key);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvWallet = (TextView) findViewById(R.id.tv_wallet);
        etValue = (EditText) findViewById(R.id.et_value);
        etOntidPwd = (EditText) findViewById(R.id.et_ontid_pwd);
        etWalletPwd = (EditText) findViewById(R.id.et_wallet_pwd);

        btnUpdate.setOnClickListener(this);
        layoutBack.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String data = getIntent().getExtras().getString(Constant.KEY);
            bean = JSONObject.parseObject(data, DDOBean.AttributesBean.class);
            tvKey.setText(bean.getKey());
            tvType.setText(bean.getType());
            tvWallet.setText(SPWrapper.getDefaultAddress());

            etValue.setText(bean.getValue());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_update:
                update();
                break;
            default:
        }
    }

    private void update() {
        String value = etValue.getText().toString().trim();
        String walletPwd = etWalletPwd.getText().toString().trim();
        String ontidPwd = etOntidPwd.getText().toString().trim();

        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(walletPwd) || TextUtils.isEmpty(ontidPwd) || TextUtils.isEmpty(SPWrapper.getDefaultAddress()) || bean == null) {
            ToastUtil.showToast(this, "param error");
            return;
        }

        showLoading();
        SDKWrapper.updateDDOAttr(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                dismissLoading();
                ToastUtil.showToast(DDOAttriActivity.this, "success");
                finish();
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                showAttention(message);
            }
        }, TAG, ontidPwd, walletPwd, bean.getKey(), bean.getType(), value);
    }
}
