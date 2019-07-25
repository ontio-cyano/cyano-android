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

/*








 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ont.cyanowallet.scan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.request.ScanGetQRReq;
import com.github.ont.cyanowallet.scan.camera.CameraManager;
import com.github.ont.cyanowallet.scan.decode.DecodeThread;
import com.github.ont.cyanowallet.scan.utils.BeepManager;
import com.github.ont.cyanowallet.scan.utils.CaptureActivityHandler;
import com.github.ont.cyanowallet.scan.utils.InactivityTimer;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.SettingSingleton;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.sdk.wallet.Account;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Date;

import core.Result;

/**
 * This activity_check_claim opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 */
public final class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
    private static final String TAG = "CaptureActivity";

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private TextView tv1;
    private LinearLayout back;
    private TextView upload;
    private Bitmap scanBitmap;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_scan_code);

        scanPreview = (SurfaceView) findViewById(R.id.capture_surfaceview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        back = (LinearLayout) findViewById(R.id.line_back);
        upload = (TextView) findViewById(R.id.upload);
        tv1 = (TextView) findViewById(R.id.tv1);

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());
        handler = null;
        if (isHasSurface) {
            // The activity_check_claim was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }
        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        /**
         * 返回解析数据
         * 注：用真机测试
         */
        String json = rawResult.getText();
        Intent resultIntent = new Intent();
        bundle.putString("result", json);
        resultIntent.putExtras(bundle);
        CaptureActivity.this.setResult(RESULT_OK, resultIntent);
        if (json.startsWith(Constant.HTTP) || json.startsWith(Constant.HTTPS)) {
            showLoading();
            scanGetQRReq = new ScanGetQRReq(json);
            scanGetQRReq.setOnResultListener(new BaseRequest.ResultListener() {
                @Override
                public void onResult(com.github.ont.cyanowallet.network.net.Result result) {
                    dismissLoading();
                    solveData((String) result.info);
                }

                @Override
                public void onResultFail(com.github.ont.cyanowallet.network.net.Result error) {
                    dismissLoading();
                    ToastUtil.showToast(CaptureActivity.this, "QR ERROR");
                    restartPreviewAfterDelay(0);
                }
            });
            scanGetQRReq.excute();
        } else {
            solveData(json);
        }
    }

    ScanGetQRReq scanGetQRReq;

    private void solveData(String json) {
        try {
            final JSONObject jsonObject = new JSONObject(json);
            String action = jsonObject.getString("action");
            switch (action) {
                case "login":
                    toLogin(jsonObject);
                    break;
                case "signMessage":
                    toSign(jsonObject);
                    break;
                case "invoke":
                    toInvoke(jsonObject);
                    break;
                case "onsLogin":
                    Intent intent1 = new Intent(this, ScanOnsLoginActivity.class);
                    intent1.putExtra(Constant.KEY, json);
                    startActivity(intent1);
                    break;
                case "signTransaction":
                    Intent intent2 = new Intent(this, ScanSignTransactionActivity.class);
                    intent2.putExtra(Constant.KEY, json);
                    startActivity(intent2);
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void toLogin(JSONObject jsonObject) throws Exception {
        String defaultAddress = SPWrapper.getDefaultAddress();
        if (!TextUtils.isEmpty(defaultAddress)) {
            Intent intent = new Intent(this, ScanWalletLoginActivity.class);
            intent.putExtra(Constant.KEY, jsonObject.getJSONObject("params").toString());
            try {
                intent.putExtra(Constant.ID, jsonObject.getString(Constant.ID));
                intent.putExtra(Constant.VERSION, jsonObject.getString(Constant.VERSION));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            finish();
        } else {
            ToastUtil.showToast(CaptureActivity.this, "NO Wallet");
        }
        finish();
    }

    private void toSign(JSONObject jsonObject) throws Exception {
        String defaultAddress = SPWrapper.getDefaultAddress();
        if (!TextUtils.isEmpty(defaultAddress)) {
            Intent intent = new Intent(this, ScanWalletSignActivity.class);
            intent.putExtra(Constant.KEY, jsonObject.getJSONObject("params").toString());
            try {
                intent.putExtra(Constant.ID, jsonObject.getString(Constant.ID));
                intent.putExtra(Constant.VERSION, jsonObject.getString(Constant.VERSION));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            finish();
        } else {
            ToastUtil.showToast(CaptureActivity.this, "NO Wallet");
        }
        finish();
    }

    private void toInvoke(final JSONObject jsonObject) throws Exception {
        final JSONObject params = jsonObject.getJSONObject("params");
        boolean hasExpire = params.has("expire");
        if (hasExpire) {
            long expireTime = params.getLong("expire");
            if (expireTime != 0 && new Date().after(new Date(expireTime * 1000))) {
                ToastUtil.showToast(CaptureActivity.this, "QR expire");
                finish();
                return;
            }
        }
        showLoading();
        SDKWrapper.verifyTX(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                dismissLoading();
                String address = (String) message;
                if (TextUtils.isEmpty(address)) {
                    if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
                        showAttention("NO Wallet");
                    } else {
                        Intent intent = new Intent(CaptureActivity.this, ScanWalletInvokeActivity.class);
                        intent.putExtra(Constant.KEY, params.toString());
                        intent.putExtra(Constant.ADDRESS, SPWrapper.getDefaultAddress());
                        try {
                            intent.putExtra(Constant.ID, jsonObject.getString(Constant.ID));
                            intent.putExtra(Constant.VERSION, jsonObject.getString(Constant.VERSION));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Account account = SettingSingleton.getInstance().getWallet().getAccount(address);
                    if (account == null) {
                        showAttention("NO Wallet");
                    } else {
                        Intent intent = new Intent(CaptureActivity.this, ScanWalletInvokeActivity.class);
                        intent.putExtra(Constant.KEY, params.toString());
                        intent.putExtra(Constant.ADDRESS, address);
                        try {
                            intent.putExtra(Constant.ID, jsonObject.getString(Constant.ID));
                            intent.putExtra(Constant.VERSION, jsonObject.getString(Constant.VERSION));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(CaptureActivity.this, message);
                restartPreviewAfterDelay(0);
            }
        }, TAG, params.getString("qrcodeUrl"));
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.QRCODE_MODE);
            }
            initCrop();
        } catch (Exception ioe) {
            Log.w(TAG, ioe);
        }
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}