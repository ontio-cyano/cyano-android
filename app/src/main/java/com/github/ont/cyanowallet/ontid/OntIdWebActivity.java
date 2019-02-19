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

package com.github.ont.cyanowallet.ontid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.ontid.web.CyanoWebView;
import com.github.ont.cyanowallet.ontid.web.NativeJsBridge;
import com.github.ont.cyanowallet.utils.Constant;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.List;

/**
 * Created by Administrator on 2015/9/17.
 */
public class OntIdWebActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "OntIdWebActivity";
    private static final int REQUEST_ALBUM_CODE = 103;
    private static final int REQUEST_FACE_CODE = 104;

    //    protected String mUrl = "http://192.168.50.124:8080";
    protected String mUrl = "http://192.168.50.124:8080/#/authHome";

    ProgressBar pg;
    FrameLayout frameLayout;

    private static final String TX = "00d18f5dce45f401000000000000204e0000000000007e09b93deb82351ff54e4d9593ca683a15ed8df79800c66b2a6469643a6f6e743a415375455532554a4357764c5a6e7734697331456d377479716f78565069484279696a7cc82102db7302ad5f66ba845227d5d01fe5dc8ea2da186c04927bb1b5c3dac909dedc3c6a7cc86c127265674944576974685075626c69634b65791400000000000000000000000000000000000000030068164f6e746f6c6f67792e4e61746976652e496e766f6b650001424101fa8b2424fc5b5547885c6c66b27bf3ca6c1d5e3a5dea836e4491a318575b0a2b0407832608a2908c264d66fee83856d85d48001ecfbcf3391fbc91565634cac5232102db7302ad5f66ba845227d5d01fe5dc8ea2da186c04927bb1b5c3dac909dedc3cac";
    private static final String ONTID = "did:ont:ASuEU2UJCWvLZnw4is1Em7tyqoxVPiHByi";
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
        initData();
        mWebView = new CyanoWebView(this);
        frameLayout.addView(mWebView);
        initWebView();
        mWebView.loadUrl(mUrl);
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
    }

    private void initWebView() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                showPayDialog(message);
//                String[] split = message.split("params=");
//                if (result != null) {
//                    result.confirm("");
//                }
//                if (result != null) {
//                    result.cancel();
//                }
//// 返回布尔值：判断点击时确认还是取消
//// true表示点击了确认；false表示点击了取消
                return true;
//                return super.onJsPrompt(view)
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
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

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = consoleMessage.message();
                Log.i(TAG, "onConsoleMessage: " + message);
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                https://blog.csdn.net/qq_34650238/article/details/79923661
                OntIdWebActivity.filePathCallback = filePathCallback;
                showImagePick();
                return true;
                //                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
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
                        handleAuthorization(data);
                        break;
                    case "getAuthorizationInfo":
                        handleAuthorization(data);
                        break;
                    case "decryptClaim":
                        handleDecryptMessage(data);
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

    }


    private void showImagePick() {
//        PhotoPicker.builder().setPhotoCount(1).setShowCamera(false).setShowGif(false).setPreviewEnabled(true).start(this, REQUEST_ALBUM_CODE);
        Matisse.from(this).choose(MimeType.allOf()) // 选择 mime 的类型                             .countable(true)
                .maxSelectable(9) // 图片选择的最多数量
//                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED).thumbnailScale(0.85f) // 缩略图的比例
                .imageEngine(new GlideEngine()) // 使用的图片加载引擎
                .forResult(REQUEST_ALBUM_CODE); // 设置作为标记的请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ALBUM_CODE:
                    if (data != null) {
//                        ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        List<Uri> uris = Matisse.obtainResult(data);
//                        String imgPath = photos.get(0);
                        // 返回信息给调用方
//                        Uri uri = Uri.parse(imgPath);
                        filePathCallback.onReceiveValue(new Uri[]{uris.get(0)});
                    }
                    break;
                case REQUEST_FACE_CODE:
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(reqData);
                    JSONObject map = new JSONObject();
                    map.put("subaction", "faceRecognition");
//                    map.put("data",getSenseData());
                    mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), map);
                    break;
                default:
                    break;
            }

        }
    }

    private void handleRegistry(String data) {
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
        JSONObject map = new JSONObject();
        map.put("subaction", "getRegistryOntidTx");
        map.put("ontid", ONTID);
        map.put("registryOntidTx", TX);
        mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), map);
    }

    private void handleDecryptMessage(final String data) {
        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
                mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), true);
            }
        });
        showPasswordDialog("decrypt message");
    }

    private void handleAuthorization(String data) {
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
        JSONObject map = new JSONObject();
        map.put("subaction", "getAuthorizationInfo");
        map.put("seqno", "0001");
        map.put("user_ontid", ONTID);
        map.put("app_ontid", ONTID);
        map.put("to_ontid", ONTID);
        map.put("callback", "http://candybox.com/");
        map.put("auth_templete", "authtemplate_kyc01");
        mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), map);
    }

    private void handleSubmit(final String data) {
        showLoading();
        new MyHandle(this).postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoading();
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
                mWebView.sendSuccessToWeb(jsonObject.getString("action"), jsonObject.getString("version"), jsonObject.getString("id"), true);
            }
        }, 2000);
    }

    private void handleFaceRecognition(String data) {
        reqData = data;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            mWebView.goBack();
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
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
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
}
