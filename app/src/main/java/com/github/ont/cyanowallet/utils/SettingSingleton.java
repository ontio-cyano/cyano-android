package com.github.ont.cyanowallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.ont.cyanowallet.main.AppApplication;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Wallet;

import java.io.IOException;

public class SettingSingleton {
    private static SettingSingleton mInstance;

    public static synchronized SettingSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new SettingSingleton();
//            mInstance.getWallet(); //很多操作依赖钱包
        }
        return mInstance;
    }

    public Wallet getWallet() {
        return getWalletMgr().getWallet();
    }

    private WalletMgr getWalletMgr() {
        resetOntSdk();
        return OntSdk.getInstance().getWalletMgr();
    }

    private void resetOntSdk() {
        SharedPreferences sp = AppApplication.getContext().getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
        try {
            OntSdk.getInstance().openWalletFile(sp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
