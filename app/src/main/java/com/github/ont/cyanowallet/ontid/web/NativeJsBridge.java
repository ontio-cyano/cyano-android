package com.github.ont.cyanowallet.ontid.web;

import android.net.Uri;
import android.util.Base64;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;


public class NativeJsBridge {
    private HandleAuthentication handleAuthentication;
    private HandleAuthorization handleAuthorization;
    private HandleDecryptMessage handleDecryptMessage;

    private CyanoWebView cyanoWebView;

    public NativeJsBridge(CyanoWebView cyanoWebView) {
        this.cyanoWebView = cyanoWebView;
    }

    @JavascriptInterface
    public void postMessage(String userInfo) {
        if (userInfo.contains("ontprovider://ont.io")) {
            final String[] split = userInfo.split("params=");
            if (cyanoWebView != null) {
                cyanoWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        handleAction(split[split.length - 1]);
                    }
                });
            }
        }
    }


    private void handleAction(String message) {
        byte[] decode = Base64.decode(message, Base64.NO_WRAP);
        String result = Uri.decode(new String(decode));
//        {"action":"login","params":{"type":"account","dappName":"My dapp","message":"test message","expired":"201812181000","callback":""}}
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            String action = jsonObject.getString("action");
            switch (action) {
                case "authentication":
                    if (handleAuthentication != null) {
                        handleAuthentication.handleAction(result);
                    }
                    break;
                case "authorization":
                    if (handleAuthorization != null) {
                        handleAuthorization.handleAction(result);
                    }
                    break;
                case "decryptMessage":
                    if (handleDecryptMessage != null) {
                        handleDecryptMessage.handleAction(result);
                    }
                    break;
                default:
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setAuthentication(HandleAuthentication handleAuthentication) {
        this.handleAuthentication = handleAuthentication;
    }

    public void setAuthorization(HandleAuthorization handleAuthorization) {
        this.handleAuthorization = handleAuthorization;
    }

    public void setDecryptMessage(HandleDecryptMessage handleDecryptMessage) {
        this.handleDecryptMessage = handleDecryptMessage;
    }

    public interface HandleAuthentication {
        public void handleAction(String data);
    }

    public interface HandleAuthorization {
        public void handleAction(String data);
    }

    public interface HandleDecryptMessage {
        public void handleAction(String data);
    }


}
