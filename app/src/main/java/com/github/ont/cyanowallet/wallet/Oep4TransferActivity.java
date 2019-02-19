package com.github.ont.cyanowallet.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.beans.Oep4ListBean;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.BalanceReq;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

import java.math.BigDecimal;

/**
 * @author zhugang
 */
public class Oep4TransferActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Oep4TransferActivity";

    private Oep4ListBean.ContractListBean bean;
    private TextView tvName;
    private TextView tvBalance;
    private TextView tvOng;
    private Button btnSend;
    private EditText etAmount;
    private EditText etAddress;

    private BalanceReq balanceReq;
    private String ongBalance;
    private String oep4Balance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oep4_transfer);
        initView();
        initData();
    }

    private void initView() {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvBalance = (TextView) findViewById(R.id.tv_balance);
        tvOng = (TextView) findViewById(R.id.tv_ong);
        btnSend = (Button) findViewById(R.id.btn_send);
        etAddress = (EditText) findViewById(R.id.et_address);
        etAmount = (EditText) findViewById(R.id.et_amount);
        View btnCancel =  findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String data = getIntent().getExtras().getString(Constant.KEY);
            bean = JSONObject.parseObject(data, Oep4ListBean.ContractListBean.class);
            tvName.setText(String.format("Send : %s", bean.getSymbol().toUpperCase()));
            showLoading();
            SDKWrapper.getOep4Balance(new SDKCallback() {
                @Override
                public void onSDKSuccess(String tag, Object message) {
                    dismissLoading();
                    if (TextUtils.equals((String) message, "0")) {
                        oep4Balance = "0";
                    } else {
                        BigDecimal bdBalance = new BigDecimal((String) message);
                        BigDecimal multiplicand = new BigDecimal(1);
                        for (int i = 0; i < bean.getDecimals(); i++) {
                            multiplicand = multiplicand.multiply(new BigDecimal(10));
                        }
                        bdBalance = bdBalance.divide(multiplicand, bean.getDecimals(), BigDecimal.ROUND_HALF_UP);
                        oep4Balance = bdBalance.toString();
                    }
                    if (tvBalance != null) {
                        tvBalance.setText(String.format("Balance : %s", oep4Balance));
                    }

                }

                @Override
                public void onSDKFail(String tag, String message) {
                    dismissLoading();
                    ToastUtil.showToast(baseActivity, message);
                }
            }, TAG, bean.getContractHash());

            if (balanceReq != null) {
                balanceReq.cancel();
            }
            balanceReq = new BalanceReq(SPWrapper.getDefaultAddress());
            balanceReq.setRequestTag(TAG);
            balanceReq.setOnResultListener(new BaseRequest.ResultListener() {
                @Override
                public void onResult(Result result) {
                    if (result.isSuccess && tvOng != null) {
                        JSONObject jsonObject = JSONObject.parseObject((String) result.info);
                        JSONArray array = jsonObject.getJSONArray("Result");
                        JSONObject bean;
                        for (int i = 0; i < array.size(); i++) {
                            bean = array.getJSONObject(i);
                            if (TextUtils.equals(bean.getString("AssetName"), Constant.ONG)) {
                                ongBalance = bean.getString("Balance");
                                tvOng.setText(ongBalance);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onResultFail(Result error) {

                }
            });
            balanceReq.excute();

            etAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            CommonUtil.setPoint(etAmount, bean.getDecimals());
        } else {
            finish();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                send();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
        }
    }

    private void send() {
        final String receiveAddress = etAddress.getText().toString();
        String amount = etAmount.getText().toString();
        if (!CommonUtil.isAddress(receiveAddress)) {
            ToastUtil.showToast(this, "address error");
            return;
        }
        if (!CommonUtil.checkIsMoney(amount)) {
            ToastUtil.showToast(this, "amount error");
            return;
        }

        if (ongBalance == null) {
            ToastUtil.showToast(this, "ong error,Please restart");
            return;
        }

        if (oep4Balance == null) {
            ToastUtil.showToast(this, "oep4 error,Please restart");
            return;
        }

        BigDecimal bdAmount = new BigDecimal(amount);
        BigDecimal bdOng = new BigDecimal(ongBalance);
        BigDecimal bdFee = new BigDecimal("0.01");
        BigDecimal bdOep4 = new BigDecimal(oep4Balance);

        if (bdFee.compareTo(bdOng) > 0) {
            ToastUtil.showToast(this, "not enough ong");
            return;
        }

        if (bdAmount.compareTo(bdOep4) > 0) {
            ToastUtil.showToast(this, "not enough balance");
            return;
        }

        for (int i = 0; i < bean.getDecimals(); i++) {
            bdAmount = bdAmount.multiply(new BigDecimal(10));
        }

        final long sendAmount = bdAmount.longValue();

        setGetDialogPwd(new GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                showLoading();
                SDKWrapper.sendOep4Amount(new SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        dismissLoading();
                        ToastUtil.showToast(Oep4TransferActivity.this, "transfer success");
                        finish();
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        dismissLoading();
                        showAttention(message);
                    }
                }, TAG, bean.getContractHash(), pwd, receiveAddress, sendAmount);
            }
        });
        showPasswordDialog("oep4 transfer");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (balanceReq != null) {
            balanceReq.cancel();
        }
    }
}
