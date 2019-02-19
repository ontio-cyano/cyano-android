package com.github.ont.cyanowallet.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Account;

import java.io.IOException;
import java.util.List;

/**
 * @author zhugang
 */
public class WalletManageActivity extends BaseActivity implements View.OnClickListener {

    private AdapterWalletManager adapter;
    private List<Account> accounts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_manage);
        initView();
    }

    private void initView() {
        View layoutBack = findViewById(R.id.layout_back);
        View btnCreate = findViewById(R.id.btn_create);
        View btnImport = findViewById(R.id.btn_import);
        ListView lv = (ListView) findViewById(R.id.lv);

        adapter = new AdapterWalletManager(this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (accounts == null) {
                    return;
                }
                Intent intent = new Intent(WalletManageActivity.this, WalletDetailActivity.class);
                intent.putExtra(Constant.KEY, accounts.get(i).address);
                startActivity(intent);
            }
        });

        layoutBack.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnImport.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_create:
                startActivity(new Intent(baseActivity, CreateWalletActivity.class));
                break;
            case R.id.btn_import:
                startActivity(new Intent(baseActivity, ImportWalletActivity.class));
                break;
            default:
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            try {
                OntSdk.getInstance().openWalletFile(SPWrapper.getSharedPreferences());
                accounts = OntSdk.getInstance().getWalletMgr().getWallet().getAccounts();
            } catch (IOException e) {
                e.printStackTrace();
            }
            adapter.notify(accounts);
        }
    }
}
