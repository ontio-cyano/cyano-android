package com.github.ont.cyanowallet.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ont.connector.base.CyanoBaseActivity;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ont.cyanowallet.view.ChooseDialog;
import com.github.ont.cyanowallet.view.PasswordDialog;

import java.lang.ref.WeakReference;

public abstract class BaseActivity extends CyanoBaseActivity {
    public static final String TAG = "BaseActivity";

    public Activity baseActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }



    private Dialog dialogAttention;
    private TextView dialogContent = null;
    private ImageView imgLogo = null;

    public void showAttention(int content, int drawResource) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (imgLogo != null) {
            imgLogo.setImageResource(drawResource);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    public void showAttention(String content, int drawResource) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (imgLogo != null) {
            imgLogo.setImageResource(drawResource);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    public void showAttention(int content) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    public void showAttention(String content) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    private ChooseDialog chooseDialog;

    public void showChooseDialog(String showMessage, final String hexMessage, final String data) {
        chooseDialog = new ChooseDialog(this, R.style.dialog, showMessage, hexMessage);
        chooseDialog.setActionSure(new ChooseDialog.ActionSure() {
            @Override
            public void setActionSure() {
                sendTransaction(hexMessage, data);
            }
        });
        if (chooseDialog != null && !chooseDialog.isShowing()) {
            chooseDialog.show();
        }

    }

    public void sendTransaction(String transactionHex, final String data) {
        showLoading();
        SDKWrapper.sendTransactionHex(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, final Object message) {
                dismissLoading();
                setHash((String) message, data);
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(baseActivity, " Fail : " + message);
            }
        }, TAG, transactionHex);
    }

    public void setHash(String txHash, String data) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
        dismissPwdDialog();
    }

    public static class MyHandle extends Handler{
        WeakReference<Activity> mActivityReference;

        public MyHandle(Activity activity){
            mActivityReference=new WeakReference<>(activity);
        }
    }
}
