package com.github.ont.cyanowallet.ontid;

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

public class CreateOntIdActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CreateOntIdActivity";

    private EditText etPwd;
    private EditText etConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ontid);
        initView();
    }

    private void initView() {
        Button btnCreate = (Button) findViewById(R.id.btn_create);
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etConfirm = (EditText) findViewById(R.id.et_confirm);
        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                final String pwd = etPwd.getText().toString();
                String pwdConfirm = etConfirm.getText().toString();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdConfirm)) {
                    ToastUtil.showToast(this, "Please fill in the blanks.");
                } else if (!pwd.equals(pwdConfirm)) {
                    ToastUtil.showToast(this, "Passwords must be the same.");
                } else {
                    setGetDialogPwd(new GetDialogPassword() {
                        @Override
                        public void handleDialog(String walletPwd) {
                            createWallet(pwd,walletPwd);
                        }
                    });
                    showPasswordDialog("pay for create ontid");
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
        }
    }

    private void createWallet(String password,String walletPwd) {
        showLoading();
        SDKWrapper.createIdentity(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                dismissLoading();
                com.github.ont.connector.utils.SPWrapper.setDefaultOntId((String) message);
                ToastUtil.showToast(CreateOntIdActivity.this, "Create Success");
                finish();
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                showAttention(message);
            }
        }, TAG, password,walletPwd);
    }
}
