/*
 * **************************************************************************************
 *  Copyright © 2014-2018 Ontology Foundation Ltd.
 *  All rights reserved.
 *
 *  This software is supplied only under the terms of a license agreement,
 *  nondisclosure agreement or other written agreement with Ontology Foundation Ltd.
 *  Use, redistribution or other disclosure of any parts of this
 *  software is prohibited except in accordance with the terms of such written
 *  agreement with Ontology Foundation Ltd. This software is confidential
 *  and proprietary information of Ontology Foundation Ltd.
 *
 * **************************************************************************************
 */

package com.github.ont.cyanowallet.dapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.connector.base.CyanoBaseActivity;
import com.github.ont.connector.update.ImageUtil;
import com.github.ont.connector.update.NetUtil;
import com.github.ont.cyano.CyanoWebView;
import com.github.ont.cyano.NativeJsBridge;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Wallet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/17.
 */
public class GameWebActivity extends BaseActivity implements View.OnClickListener {
    public static final int REQUEST_ALBUM_CODE = 103;
    private static final int REQUEST_FACE_CODE = 104;
    private final String TAG = "SocialMediaWebview";
    protected String mUrl = "";
    ProgressBar pg;
    FrameLayout frameLayout;
    private String ONTID;
    private LinearLayout layoutBack;
    private LinearLayout layoutFinish;
    private CyanoWebView mWebView = null;
    private static ValueCallback<Uri[]> filePathCallback;
    private String reqData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_web);
        initView();
        mWebView = new CyanoWebView(this);
        frameLayout.addView(mWebView);
        initWebView();
        initData();
    }

    private void initView() {
        pg = findViewById(R.id.progress_loading);
        frameLayout = findViewById(R.id.frame);
        layoutBack = findViewById(R.id.layout_back);
        layoutFinish = findViewById(R.id.layout_finish);
        layoutBack.setOnClickListener(this);
        layoutFinish.setOnClickListener(this);
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUrl = extras.getString(Constant.KEY);
        }
        ONTID = com.github.ont.connector.utils.SPWrapper.getDefaultOntId();
        mWebView.loadUrl(mUrl);
    }

    private void initWebView() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                showPayDialog(message);
                String[] split = message.split("params=");
                if (result != null) {
                    result.confirm("");
                }
                if (result != null) {
                    result.cancel();
                }
// 返回布尔值：判断点击时确认还是取消
// true表示点击了确认；false表示点击了取消
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                https://blog.csdn.net/qq_34650238/article/details/79923661
                GameWebActivity.filePathCallback = filePathCallback;
                if (getPermissions()) {
                    ImageUtil.setImage(GameWebActivity.this);
                } else {
                    filePathCallback.onReceiveValue(new Uri[]{Uri.EMPTY});
                }
                return true;
                //                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pg.setVisibility(View.GONE);
                } else {
                    pg.setVisibility(View.VISIBLE);
                    pg.setProgress(newProgress);
                }
            }
        });
        mWebView.getNativeJsBridge().setHandleLogin(new NativeJsBridge.HandleLogin() {
            @Override
            public void handleAction(final String data) {
                setGetDialogPwd(new GetDialogPassword() {
                    @Override
                    public void handleDialog(String pwd) {
                        handleLogin(data, pwd);
                    }
                });
                showPasswordDialog("login sign");
            }
        });

        mWebView.getNativeJsBridge().setHandleInvoke(new NativeJsBridge.HandleInvoke() {
            @Override
            public void handleAction(final String data) {
                if (checkData(data, Constant.INVOKE)) {
                    setGetDialogPwd(new GetDialogPassword() {
                        @Override
                        public void handleDialog(String pwd) {
                            handleInvokeTransaction(data, pwd);
                        }
                    });
                    showPasswordDialog("invoke");
                } else {
                    JSONObject jsonObject = JSON.parseObject(data);
                    mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), "params error");
                }
            }
        });

        mWebView.getNativeJsBridge().setHandleGetAccount(new NativeJsBridge.HandleGetAccount() {
            @Override
            public void handleAction(String data) {
                getAccount(data);
            }
        });

        mWebView.getNativeJsBridge().setHandleInvokeRead(new NativeJsBridge.HandleInvokeRead() {
            @Override
            public void handleAction(String data) {
                if (checkData(data, Constant.INVOKE_READ)) {
                    handleInvokeRead(data);
                } else {
                    JSONObject jsonObject = JSON.parseObject(data);
                    mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), "params error");
                }
            }
        });

        mWebView.getNativeJsBridge().setHandleInvokePasswordFree(new NativeJsBridge.HandleInvokePasswordFree() {
            @Override
            public void handleAction(String data, String message) {
                if (checkData(data, Constant.INVOKE)) {
                    handleInvokePasswordFree(data, message);
                } else {
                    JSONObject jsonObject = JSON.parseObject(data);
                    mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), "params error");
                }
            }
        });


        mWebView.getNativeJsBridge().setAuthentication(new NativeJsBridge.HandleAuthentication() {
            @Override
            public void handleAction(String data) {
                JSONObject jsonObject = JSON.parseObject(data);
                String subAction = jsonObject.getJSONObject("params").getString("subaction");
                switch (subAction) {
                    case "faceRecognition":
                        handleFaceRecognition(data);
                        break;
                    case "submit":
                        handleSubmit(data);
                        break;
                    case "getIdentity":
                        handleGetIdentity(data);
                        break;
                    case "getRegistryOntidTx":
                        handleRegistry(data);
                    default:
                }
            }
        });


        mWebView.getNativeJsBridge().setAuthorization(new NativeJsBridge.HandleAuthorization() {
            @Override
            public void handleAction(String data) {
                JSONObject jsonObject = JSON.parseObject(data);
                String subAction = jsonObject.getJSONObject("params").getString("subaction");
                switch (subAction) {
                    case "requestAuthorization":
                        handleRequestAuthorization(data);
                        break;
                    case "getAuthorizationInfo":
                        handleAuthorization(data);
                        break;
                    case "decryptClaim":
                        handleDecryptMessage(data);
                        break;
                    case "exportOntid":
                        handleExport(data);
                        break;
                    case "deleteOntid":
                        handleDelete(data);
                        break;
                    default:
                }
            }
        });

        mWebView.getNativeJsBridge().setDecryptMessage(new NativeJsBridge.HandleDecryptMessage() {
            @Override
            public void handleAction(String data) {
                handleDecryptMessage(data);
            }
        });


        mWebView.getNativeJsBridge().setHandleGetIdentity(new NativeJsBridge.HandleGetIdentity() {
            @Override
            public void handleAction(String data) {
                handleGetIdentity(data);
            }
        });
    }

    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    List<String> mPermissionList = new ArrayList<>();
    private static final int MY_PERMISSIONS = 2;

    private boolean getPermissions() {
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (!mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (showRequestPermission) {
                        com.github.ont.connector.utils.ToastUtil.showToast(this, "permission refused");
//                        finish();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //
    private boolean checkData(String data, String tag) {
        OntSdk ontSdk = OntSdk.getInstance();
        Transaction[] transactions = ontSdk.makeTransactionByJson(data);
        Transaction transaction = transactions[0];
        try {
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

    private ArrayList<String> messages = new ArrayList<>();
    private String pwd = "";
    private String tempMessage = "";

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMessage() {
        return tempMessage;
    }

    public void setMessage(String message) {
        this.tempMessage = message;
    }

    private void handleInvokePasswordFree(final String data, String message) {
        setMessage(message);
        if (messages.contains(message)) {
            if (!TextUtils.isEmpty(pwd)) {
                handleInvokeTransaction(data, pwd);
                return;
            }
        }
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                setPwd(pwd);
                handleInvokeTransaction(data, pwd);
            }
        });
        showPasswordDialog("invoke password free");
    }

    private void handleInvokeRead(final String data) {
        final com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
        if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
            ToastUtil.showToast(this, "No wallet");
            mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), "No wallet");
            return;
        }
        SDKWrapper.handleInvokeRead(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), message);
            }

            @Override
            public void onSDKFail(String tag, String message) {
                showAttention(message);
                mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), message);
            }
        }, TAG, data);
    }


    private void getAccount(String reqJson) {
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(reqJson);
        mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), SPWrapper.getDefaultAddress());
    }

    private void handleInvokeTransaction(final String data, String password) {
        final com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
        final String action = jsonObject.getString("action");

        if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
            ToastUtil.showToast(this, "no wallet");
            mWebView.sendFailToWeb(action, com.github.ont.cyano.Constant.INTERNAL_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), "no wallet");
            return;
        }

        showLoading();
        SDKWrapper.getSendAddress(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                dismissLoading();
                Log.i(TAG, "onSDKSuccess: "+message);
                ArrayList<String> result = (ArrayList<String>) message;

                if (result != null && result.size() > 1) {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject((String) result.get(0));
                    JSONArray notify = jsonObject.getJSONArray("Notify");
                    if (notify != null && notify.size() > 0) {
                        if (action.equals(com.github.ont.cyano.Constant.INVOKE_PASSWORD_FREE) && messages.contains(getMessage())) {
                            sendTransaction((String) result.get(1), data);
                            return;
                        }
                        showChooseDialog((String) result.get(0), (String) result.get(1), data);
                    } else {
                        sendTransaction((String) result.get(1), data);
                    }
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
//                if (action.equals(com.github.ont.cyano.Constant.INVOKE_PASSWORD_FREE) && messages.contains(getMessage())) {
//                    return;
//                }
                showAttention((String) message);
                mWebView.sendFailToWeb(action, com.github.ont.cyano.Constant.INTERNAL_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), message);
            }
        }, TAG, data, password, SPWrapper.getDefaultAddress());
    }


    private void handleLogin(String data, String password) {
        showLoading();
        final com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
        SDKWrapper.getGameLogin(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                dismissLoading();
                mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), JSON.parseObject((String) message));
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(baseActivity, message);
                mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), message);
            }
        }, TAG, password, jsonObject.getJSONObject("params").getString("message"));
    }

    private void handleRequestAuthorization(String data) {
//        {"action":"authorization","version":"1.0.0","params":
//            {"subaction":"requestAuthorization","seqNo":"0001",
//                    "userOntid":"did:ont:AL2yjtLZJmRmQ4muFiVexYcyVsYb4DkyYL",
//                    "dappOntid":"did:ont:AL2yjtLZJmRmQ4muFiVexYcyVsYb4DkyYL",
//                    "dappName":"candy box","callback":"http://cybox.com/callbackand"
//                ,"dappUrl":"http://www.baidu.com/","authTemplete":"authtemplate_kyc01"},"id":"sob9y29b"}
        JSONObject jsonObject = (JSONObject) JSONObject.parse(data);
        params = jsonObject.getJSONObject("params");
        params.put("subaction", "getAuthorizationInfo");
        mWebView.loadUrl(com.github.ont.cyano.Constant.CYANO_AUTH_URL);
    }

    private JSONObject params;

    private void handleGetIdentity(String data) {
        final JSONObject jsonObject = JSON.parseObject(data);
        if (TextUtils.isEmpty(com.github.ont.connector.utils.SPWrapper.getDefaultOntId())) {
//            startActivity(new Intent(this, CreateOntIdActivity.class));
//            finish();
            createOntID();
        } else {
            mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), com.github.ont.connector.utils.SPWrapper.getDefaultOntId());
        }
    }

    private void createOntID() {
        final String defaultAccountAddress = OntSdk.getInstance().getWalletMgr().getWallet().getDefaultAccountAddress();
        if (TextUtils.isEmpty(defaultAccountAddress)) {
            com.github.ont.connector.utils.ToastUtil.showToast(baseActivity, getString(com.github.ont.connector.R.string.no_wallet));
        }
        setGetDialogPwd(new CyanoBaseActivity.GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                showLoading();
                com.github.ont.connector.utils.SDKWrapper.createIdentityWithAccount(new com.github.ont.connector.utils.SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        dismissLoading();
                        com.github.ont.connector.utils.SPWrapper.setDefaultOntId((String) message);
                        mWebView.loadUrl(com.github.ont.cyano.Constant.CYANO_MANAGER_URL + com.github.ont.connector.utils.SPWrapper.getDefaultOntId());
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                    }
                }, TAG, defaultAccountAddress, pwd);
            }
        });
        showPasswordDialog(getString(com.github.ont.connector.R.string.enter_your_wallet_password));

    }

    private void handleExport(final String data) {
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                final JSONObject jsonObject = JSON.parseObject(data);
                showLoading();
                com.github.ont.connector.utils.SDKWrapper.exportIdentity(new com.github.ont.connector.utils.SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        dismissLoading();
                        mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), (String) message);
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                        com.github.ont.connector.utils.ToastUtil.showToast(baseActivity, message);
                        mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), message);
                    }
                }, TAG, pwd);
            }
        });
        showPasswordDialog("Export Ontid");
    }

    private void handleDelete(final String data) {
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                final JSONObject jsonObject = JSON.parseObject(data);
                showLoading();
                com.github.ont.connector.utils.SDKWrapper.exportIdentity(new com.github.ont.connector.utils.SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        deleteIdentity(com.github.ont.connector.utils.SPWrapper.getDefaultOntId());
                        com.github.ont.connector.utils.SPWrapper.setDefaultOntId("");
                        dismissLoading();
                        finish();
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                        com.github.ont.connector.utils.ToastUtil.showToast(baseActivity, message);
                        mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), message);
                    }
                }, TAG, pwd);
            }
        });
        showPasswordDialog("Delete Ontid");
    }

    public static void deleteIdentity(String ontid) {
        WalletMgr walletMgr = OntSdk.getInstance().getWalletMgr();
        Wallet wallet = walletMgr.getWallet();
        wallet.removeIdentity(ontid);
        try {
            walletMgr.writeWallet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ALBUM_CODE:
                    if (data != null && filePathCallback != null) {
//                        ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        List<Uri> uris = ImageUtil.getChooseResult(data);

//                        String imgPath = photos.get(0);
                        // 返回信息给调用方
//                        Uri uri = Uri.parse(imgPath);
                        filePathCallback.onReceiveValue(new Uri[]{uris.get(0)});
                    }
                    break;
                case REQUEST_FACE_CODE:
                    JSONObject jsonObject = JSON.parseObject(reqData);
                    JSONObject map = new JSONObject();
                    map.put("subaction", "faceRecognition");
//                    map.put("data",getSenseData());
                    mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), map);
                    break;
                default:
                    break;
            }

        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_ALBUM_CODE && filePathCallback != null) {
            filePathCallback.onReceiveValue(new Uri[]{Uri.EMPTY});
        }
    }

    private void handleRegistry(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        JSONObject map = new JSONObject();
        map.put("subaction", "getRegistryOntidTx");
        map.put("ontid", ONTID);
        map.put("registryOntidTx", com.github.ont.connector.utils.SPWrapper.getOntIdTransaction());
        mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), map);
    }

    private void handleDecryptMessage(final String data) {
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                final JSONObject jsonObject = JSON.parseObject(data);
                JSONArray parse = jsonObject.getJSONObject("params").getJSONArray("message");
                String[] datas = new String[parse.size()];
                for (int i = 0; i < parse.size(); i++) {
                    datas[i] = parse.getString(i);
                }
                showLoading();
                com.github.ont.connector.utils.SDKWrapper.decryptData(new com.github.ont.connector.utils.SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        dismissLoading();
                        mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), (String) message);
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                        com.github.ont.connector.utils.ToastUtil.showToast(baseActivity, message);
                        mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), message);
                    }
                }, TAG, datas, pwd);
            }
        });
        showPasswordDialog("Decrypt Message");
    }

    private void handleAuthorization(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
//        JSONObject map = new JSONObject();
//        map.put("subaction", "getAuthorizationInfo");
//        map.put("seqNo", "0001");
//        map.put("userOntid", ONTID);
//        map.put("dappOntid", ONTID);
//        map.put("dappName", "Candy Box");
//        map.put("callback", "http://candybox.com/callback");
//        map.put("dappUrl", "http://www.baidu.com/");
//        map.put("authTemplate", "authtemplate_kyc01");
        if (params == null) {
            mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), com.github.ont.cyano.Constant.PARAMS_ERROR);
            return;
        }
        mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), params);
    }

    private void handleSubmit(final String data) {
        showLoading();
        final JSONObject jsonObject = JSON.parseObject(data);
        NetUtil.setCyanoNetResponse(new NetUtil.CyanoNetResponse() {
            @Override
            public void handleSuccessResponse(String data) {
                dismissLoading();
                mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), true);
            }

            @Override
            public void handleFailResponse(String data) {
                dismissLoading();
                mWebView.sendFailToWeb(jsonObject.getString("action"), com.github.ont.cyano.Constant.PARAMS_ERROR, jsonObject.getString("version"), jsonObject.getString("id"), data);
            }
        });
        //钱包服务器地址
        NetUtil.post(com.github.ont.cyano.Constant.WALLET_RECEIVER_URL, data);
    }

    private void handleFaceRecognition(String data) {
        reqData = data;
    }


    /**
     * 停止webview的加载
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.i("ansen","是否有上一个页面:"+webView.canGoBack());
        if (mWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            mWebView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destorySelf();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                if (mWebView.canGoBack()) {//点击返回按钮的时候判断有没有上一页
                    mWebView.goBack(); // goBack()表示返回webView的上一页面
                } else {
                    finish();
                }
                break;
            case R.id.layout_finish:
                finish();
                break;
            default:
        }
    }

    @Override
    public void setHash(String hashData, String data) {
        if (hashData != null) {
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
            String action = jsonObject.getString("action");
            mWebView.sendSuccessToWeb(action, jsonObject.getString("version"), jsonObject.getString("id"), hashData);
            switch (action) {
                case com.github.ont.cyano.Constant.INVOKE_PASSWORD_FREE:
                    if (!messages.contains(getMessage())) {
                        messages.add(getMessage());
                    }
                    break;
                default:
            }
            ToastUtil.showToast(baseActivity, "Success");
        } else {
            ToastUtil.showToast(baseActivity, "Fail");
        }
    }
}
