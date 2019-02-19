package com.github.ont.cyanowallet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.github.ont.cyanowallet.main.AppApplication;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Wallet;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class SPWrapper {
    public static SharedPreferences getSharedPreferences() {
        return AppApplication.getContext().getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
    }

    public static String getDefaultAddress() {
        Wallet wallet = OntSdk.getInstance().getWalletMgr().getWallet();
        if (wallet == null) {
            try {
                OntSdk.getInstance().openWalletFile(getSharedPreferences());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wallet.getDefaultAccountAddress();
//        return getSharedPreferences().getString(Constant.DEFAULT_ADDRESS, "");
    }

    public static void setDefaultAddress(String address) {
        Wallet wallet = OntSdk.getInstance().getWalletMgr().getWallet();
        if (wallet == null) {
            try {
                OntSdk.getInstance().openWalletFile(getSharedPreferences());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        wallet.setDefaultAccount(address);
        try {
            OntSdk.getInstance().getWalletMgr().writeWallet();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        getSharedPreferences().edit().putString(Constant.DEFAULT_ADDRESS, address).apply();
    }

//    public static String getDefaultOntId() {
//        Wallet wallet = OntSdk.getInstance().getWalletMgr().getWallet();
//        if (wallet == null) {
//            try {
//                OntSdk.getInstance().openWalletFile(getSharedPreferences());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return wallet.getDefaultOntid();
////        return getSharedPreferences().getString(Constant.DEFAULT_ONTID, "");
//    }

//    public static void setDefaultOntId(String address) {
////        getSharedPreferences().edit().putString(Constant.DEFAULT_ONTID, address).apply();
//        Wallet wallet = OntSdk.getInstance().getWalletMgr().getWallet();
//        if (wallet == null) {
//            try {
//                OntSdk.getInstance().openWalletFile(getSharedPreferences());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        wallet.setDefaultIdentity(address);
//        try {
//            OntSdk.getInstance().getWalletMgr().writeWallet();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    public static String getDefaultNet() {
        return getSharedPreferences().getString(Constant.DEFAULT_NET, "");
    }

    public static void setDefaultNet(String address) {
        getSharedPreferences().edit().putString(Constant.DEFAULT_NET, address).apply();
    }

    public static void addTestNet(String testNet) {
        JSONArray jsonArray = getTestNets();
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (TextUtils.equals(jsonArray.getString(i), testNet)) {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonArray.put(testNet);
        getSharedPreferences().edit().putString(Constant.TEST_PRIVATE_NETS, jsonArray.toString()).apply();
    }

    public static JSONArray getTestNets() {
        try {
            return new JSONArray(getSharedPreferences().getString(Constant.TEST_PRIVATE_NETS, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
