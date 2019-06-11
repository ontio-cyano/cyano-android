package com.github.ont.cyanowallet.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.core.transaction.Transaction;

public class DAppUtils {
    public static String getDappName(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        if (jsonObject == null) {
            return "DApp";
        }
        JSONObject params = jsonObject.getJSONObject("params");
        if (params == null) {
            return "DApp";
        }
        String dappName = params.getString("dappName");
        if (TextUtils.isEmpty(dappName)) {
            return "DApp";
        }
        return dappName;
    }

    public static String getDappIcon(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        if (jsonObject == null) {
            return "";
        }
        JSONObject params = jsonObject.getJSONObject("params");
        if (params == null) {
            return "";
        }
        String dappName = params.getString("dappIcon");
        if (TextUtils.isEmpty(dappName)) {
            return "";
        }
        return dappName;
    }

    public static boolean checkData(String data, String tag) {
        try {
            OntSdk ontSdk = OntSdk.getInstance();
            Transaction[] transactions = ontSdk.makeTransactionByJson(data);
            if (transactions == null) {
                return false;
            }
            Transaction transaction = transactions[0];
            if (TextUtils.equals(tag, Constant.INVOKE)) {
                if (transaction.payer.equals(new Address())) {
                    return true;
                }
                return TextUtils.equals(transaction.payer.toBase58(), SPWrapper.getDefaultAddress());
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
