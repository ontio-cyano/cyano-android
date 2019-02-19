package com.github.ont.cyanowallet.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

public class ImportWalletActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ImportWalletActivity";

    private EditText etPwd;
    private EditText etConfirm;
    private EditText etKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        initView();
    }

    private void initView() {
        Button btnCreate = (Button) findViewById(R.id.btn_create);
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etConfirm = (EditText) findViewById(R.id.et_confirm);
        etKey = (EditText) findViewById(R.id.et_key);
        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                String pwd = etPwd.getText().toString();
                String pwdConfirm = etConfirm.getText().toString();
                String key = etKey.getText().toString();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdConfirm) || TextUtils.isEmpty(key)) {
                    ToastUtil.showToast(this, "Please fill in the blanks.");
                } else if (!pwd.equals(pwdConfirm)) {
                    ToastUtil.showToast(this, "Passwords must be the same.");
                } else if (key.length() != 64 && key.length() != 52) {
                    showAttention("The length of key should be 64 or 52.");
                } else {
                    importWallet(key, pwd);
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
        }
    }

    private void importWallet(String key, String password) {
        showLoading();
        SDKWrapper.importWallet(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                dismissLoading();
                SPWrapper.setDefaultAddress((String) message);
                ToastUtil.showToast(ImportWalletActivity.this, "Import Success");
                finish();
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(ImportWalletActivity.this, "Import Fail");
            }
        }, TAG, key, password);
    }
}
