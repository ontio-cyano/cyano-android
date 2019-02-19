package com.github.ont.cyanowallet.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;

public class ExportWalletActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvKey;
    private TextView tvWif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_wallet);
        initView();
        initData();
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String key = getIntent().getExtras().getString(Constant.KEY);
            tvKey.setText(key);
            try {
                tvWif.setText(new com.github.ontio.account.Account(Helper.hexToBytes(key), SignatureScheme.SHA256WITHECDSA).exportWif());
            } catch (Exception e) {
                ToastUtil.showToast(baseActivity, "System error");
                finish();
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        View layoutBack = findViewById(R.id.layout_back);
        tvKey = findViewById(R.id.tv_key);
        tvWif = findViewById(R.id.tv_wif);
        View btnCopyKey = findViewById(R.id.btn_copy_key);
        View btnCopyWif = findViewById(R.id.btn_copy_wif);

        layoutBack.setOnClickListener(this);
        btnCopyKey.setOnClickListener(this);
        btnCopyWif.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_copy_key:
                copyAddress(tvKey.getText().toString(), "Wallet key copy success");
                break;
            case R.id.btn_copy_wif:
                copyAddress(tvWif.getText().toString(), "Wallet wif copy success");
                break;
            default:
        }
    }
}
