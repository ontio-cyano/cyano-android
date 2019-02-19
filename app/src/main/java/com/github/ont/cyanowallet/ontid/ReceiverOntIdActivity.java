package com.github.ont.cyanowallet.ontid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.scan.encoding.EncodingUtils;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ontio.OntSdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zhugang
 */
public class ReceiverOntIdActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvAddress;
    private TextView tvKey;
    private ImageView imgAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_ontid);
        initView();
        initData();
    }

    private void initData() {
        tvAddress.setText(com.github.ont.connector.utils.SPWrapper.getDefaultOntId());
        tvKey.setText(OntSdk.getInstance().getWalletMgr().getDefaultIdentity().controls.get(0).publicKey);
        Bitmap qrCode = EncodingUtils.createQRCode(com.github.ont.connector.utils.SPWrapper.getDefaultOntId(), 500, 500);

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
                copyAddress(tvAddress.getText().toString(), "ONT ID copy success");
                break;
            default:
        }
    }
}
