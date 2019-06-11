package com.github.ont.cyanowallet.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;

public class ErrorUtils {
    public static String getErrorResult(Context context, String data) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (jsonObject == null) {
                return context.getString(R.string.system_error) + data;
            }
            int errorCode = jsonObject.getIntValue("Error");
            String errorDes = "";
            switch (errorCode) {
                case 58018:
                case 51015:
                    errorDes = context.getString(R.string.password_error);
                    break;
                case 61010:
                    errorDes = context.getString(R.string.tran_exceed);
                    break;
                case 58004:
                    errorDes = context.getString(R.string.address_error);
                    break;
                case 58005:
                    errorDes = context.getString(R.string.existed);
                    break;
                case 47001:
                case -1:
//                    errorDes = context.getString(R.string.insufficient_balance);
                    String errorInfo = jsonObject.getString("Result");
                    if (errorInfo.contains(Constant.INSUFFICIENT)) {
                        if (errorInfo.contains(Constant.ONT_CONTRACT)) {
                            errorDes = "ONT : " + context.getString(R.string.insufficient_balance);
                        } else if (errorInfo.contains(Constant.ONG_CONTRACT)) {
                            errorDes = "ONG : " + context.getString(R.string.insufficient_balance);
                        } else {
                            errorDes = context.getString(R.string.insufficient_balance);
                        }
                    } else if (errorInfo.contains(Constant.CONTRACT_NOT_EXIST)) {
                        errorDes = context.getString(R.string.contract_not_exist);
                    } else {
                        errorDes = errorInfo;
                    }
                    break;
                default:
                    if (jsonObject.getString("Desc") != null) {
                        errorDes = context.getString(R.string.system_error) + ":" + jsonObject.getString("Desc");
                    } else {
                        errorDes = data;
                    }
                    break;
            }
            return errorDes;
        } catch (Exception e) {
            if (!TextUtils.isEmpty(data) && data.contains(Constant.UNSOLVE_HOST)) {
                return context.getString(R.string.net_error);
            } else {
                return context.getString(R.string.system_error) + data;
            }
        }

    }
}
