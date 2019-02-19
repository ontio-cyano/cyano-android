package com.github.ont.cyanowallet.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Wallet;

import java.io.IOException;
import java.util.List;

/**
 * @author zhugang
 */
public class WalletDetailActivity extends BaseActivity implements View.OnClickListener {

    private String address;
    private TextView tvAddress;
    private View btnDefault;
    private View btnDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_detail);
        initView();
        initData();
    }

    private void initView() {
        View layoutBack = findViewById(R.id.layout_back);
        tvAddress = findViewById(R.id.tv_address);
        btnDefault = findViewById(R.id.btn_default);
        View btnExport = findViewById(R.id.btn_export);
        btnDelete = findViewById(R.id.btn_delete);

        layoutBack.setOnClickListener(this);
        btnDefault.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            address = getIntent().getExtras().getString(Constant.KEY);
            tvAddress.setText(address);
            try {
                OntSdk.getInstance().openWalletFile(SPWrapper.getSharedPreferences());
                boolean isDefault = OntSdk.getInstance().getWalletMgr().getWallet().getAccount(address).isDefault;
                btnDefault.setVisibility(isDefault ? View.GONE : View.VISIBLE);
                btnDelete.setVisibility(isDefault ? View.GONE : View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_default:
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.showToast(baseActivity, "wallet error");
                } else {
                    SPWrapper.setDefaultAddress(address);
                    ToastUtil.showToast(baseActivity, "Set Default Success");
                    btnDefault.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_export:
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.showToast(baseActivity, "wallet error");
                } else {
                    setGetDialogPwd(new BaseActivity.GetDialogPassword() {
                        @Override
                        public void handleDialog(String pwd) {
                            showLoading();
                            SDKWrapper.getWalletKey(new SDKCallback() {
                                @Override
                                public void onSDKSuccess(String tag, Object message) {
                                    dismissLoading();
                                    Intent intent = new Intent(baseActivity, ExportWalletActivity.class);
                                    intent.putExtra(Constant.KEY, (String) message);
                                    startActivity(intent);
                                }

                                @Override
                                public void onSDKFail(String tag, String message) {
                                    dismissLoading();
                                    showAttention(message);
                                }
                            }, TAG, pwd, address);
                        }
                    });
                    showPasswordDialog(getString(R.string.enter_your_wallet_password));
                }
                break;
            case R.id.btn_delete:
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.showToast(baseActivity, "wallet error");
                } else {
                    setGetDialogPwd(new BaseActivity.GetDialogPassword() {
                        @Override
                        public void handleDialog(String pwd) {
                            showLoading();
                            SDKWrapper.getWalletKey(new SDKCallback() {
                                @Override
                                public void onSDKSuccess(String tag, Object message) {
                                    dismissLoading();
                                    deleteAccount(address);
                                    finish();
                                }

                                @Override
                                public void onSDKFail(String tag, String message) {
                                    dismissLoading();
                                    showAttention(message);
                                }
                            }, TAG, pwd, address);
                        }
                    });
                    showPasswordDialog(getString(R.string.enter_your_wallet_password));
                }
                break;
            default:
        }
    }

    public static void deleteAccount(String address) {
        WalletMgr walletMgr = OntSdk.getInstance().getWalletMgr();
        Wallet wallet = walletMgr.getWallet();
        wallet.removeAccount(address);
        try {
            walletMgr.writeWallet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Account> accounts = wallet.getAccounts();
        if (accounts != null && accounts.size() > 0) {
            SPWrapper.setDefaultAddress(accounts.get(0).address);
        } else {
            SPWrapper.setDefaultAddress("");
        }
    }

}
