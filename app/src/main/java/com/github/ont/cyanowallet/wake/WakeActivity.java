package com.github.ont.cyanowallet.wake;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ont.connector.utils.ToastUtil;
import com.github.ont.connector.view.PasswordDialog;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.view.ChooseDialog;

import java.lang.ref.WeakReference;

class WakeActivity extends Activity {
    public static final String TAG = "BaseActivity";

    private Dialog loadingDialog;
    public Activity baseActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
        dismissPwdDialog();
        dismissLoading();
    }

    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this, com.github.ont.connector.R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(com.github.ont.connector.R.layout.dialog_cyano_loading, null);
            loadingDialog.setContentView(inflate);
        }
        if (loadingDialog.isShowing()) {
            return;
        }
        loadingDialog.show();
    }

    public void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }


    public void copyAddress(String data, String des) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) baseActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("key", data);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
        ToastUtil.showToast(this, des);
    }

    private GetDialogPassword getDialogPassword;

    public void setGetDialogPwd(GetDialogPassword getDialogPwd) {
        this.getDialogPassword = getDialogPwd;
    }

    private PasswordDialog passwordDialog;

    //显示密码输入
    public void showPasswordDialog(String des) {
        if (getDialogPassword == null) {
            ToastUtil.showToast(baseActivity, "System error ,Please restart");
            return;
        }
        if (passwordDialog != null && passwordDialog.isShowing()) {
            return;
        }
        passwordDialog = new PasswordDialog(this);
        passwordDialog.setDes(des).setConfirmListener(new PasswordDialog.ConfirmListener() {
            @Override
            public void passwordConfirm(String password) {
                passwordDialog.dismiss();
                if (getDialogPassword != null) {
                    getDialogPassword.handleDialog(password);
                }
            }
        });
        passwordDialog.show();
    }

    public interface GetDialogPassword {
        public void handleDialog(String pwd);
    }

    //隐藏付款
    public void dismissPwdDialog() {
        if (passwordDialog != null) {
            passwordDialog.dismiss();
        }
    }


    public static class MyHandle extends Handler {
        WeakReference<Activity> mActivityReference;

        public MyHandle(Activity activity) {
            mActivityReference = new WeakReference<>(activity);
        }
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
        chooseDialog = new ChooseDialog(this,showMessage, hexMessage);
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
                com.github.ont.cyanowallet.utils.ToastUtil.showToast(baseActivity, " Fail : " + message);
            }
        }, TAG, transactionHex);
    }

    public void setHash(String txHash, String data) {

    }


}
