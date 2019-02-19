package com.github.ont.cyanowallet.wallet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.scan.encoding.EncodingUtils;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReceiverWalletActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvAddress;
    private TextView tvKey;
    private ImageView imgAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_wallet);
        initView();
        initData();
    }

    private void initData() {
        tvAddress.setText(SPWrapper.getDefaultAddress());
        tvKey.setText(OntSdk.getInstance().getWalletMgr().getDefaultAccount().publicKey);
        Bitmap qrCode = EncodingUtils.createQRCode(SPWrapper.getDefaultAddress(), 500, 500);

        //encode base64 from byte array use following method
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        qrCode.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        imgAddress.setImageBitmap(qrCode);
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        View layoutBack = findViewById(R.id.layout_back);
        tvAddress = findViewById(R.id.tv_address);
        tvKey = findViewById(R.id.tv_key);
        imgAddress = findViewById(R.id.img_address);
        View btnCopyKey = findViewById(R.id.btn_copy_key);
        View btnCopyAddress = findViewById(R.id.btn_copy_address);

        layoutBack.setOnClickListener(this);
        btnCopyKey.setOnClickListener(this);
        btnCopyAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_copy_key:
                copyAddress(tvKey.getText().toString(), "Public key copy success");
                break;
            case R.id.btn_copy_address:
                copyAddress(tvAddress.getText().toString(), "Address copy success");
                break;
            default:
        }
    }
}
