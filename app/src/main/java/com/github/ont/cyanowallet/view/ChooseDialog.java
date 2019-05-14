package com.github.ont.cyanowallet.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;

public class ChooseDialog extends Dialog {
    private String showMessage;
    private String transactionHex;

    public ChooseDialog(Context context) {
        super(context);
    }

    public ChooseDialog(@NonNull Context context, int themeResId, String showMessage, String transactionHex) {
        super(context, themeResId);
        this.showMessage = showMessage;
        this.transactionHex = transactionHex;
        init(context, transactionHex);
    }


    private void init(Context context, String transactionHex) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_choose, null);
        TextView tvContent = (TextView) inflate.findViewById(R.id.tv_content);
        TextView tv_pay = (TextView) inflate.findViewById(R.id.tv_pay);
        TextView tv_address_from = (TextView) inflate.findViewById(R.id.tv_address_from);
        TextView tvToTag = (TextView) inflate.findViewById(R.id.tv_to_tag);
        TextView tvFee = (TextView) inflate.findViewById(R.id.tv_fee);
        JSONObject jsonObject = JSONObject.parseObject(showMessage);
        JSONArray notify = jsonObject.getJSONArray("Notify");

        if (notify != null && notify.size() > 0) {
            for (int i = 0; i < notify.size(); i++) {
                String contractAddress = notify.getJSONObject(i).getString("ContractAddress");
                if (TextUtils.equals(contractAddress, Constant.ONT_CONTRACT)) {
                    JSONArray states = notify.getJSONObject(i).getJSONArray("States");
                    if (states != null && states.size() > 3 && TextUtils.equals(states.getString(0), "transfer") && TextUtils.equals(states.getString(1), SPWrapper.getDefaultAddress())) {
                        tv_pay.setText(String.format("%s ONT", states.getLong(3)));
                        tvContent.setText(states.getString(2));
                    }
                } else if (TextUtils.equals(contractAddress, Constant.ONG_CONTRACT)) {
                    JSONArray states = notify.getJSONObject(i).getJSONArray("States");
                    if (states != null && states.size() > 3 && TextUtils.equals(states.getString(0), "transfer") && TextUtils.equals(states.getString(1), SPWrapper.getDefaultAddress())) {
                        tv_pay.setText(String.format("%s ONG", CommonUtil.formatONG(states.getLong(3) + "")));
                        tvContent.setText(states.getString(2));
                    }
                }
            }
        }
        if (TextUtils.isEmpty(tvContent.getText().toString())) {
            tvContent.setVisibility(View.GONE);
            tvToTag.setVisibility(View.GONE);
        }
        tv_address_from.setText(SPWrapper.getDefaultAddress());
        try {
            Transaction transaction = Transaction.deserializeFrom(Helper.hexToBytes(transactionHex));
            Long gasLimit = jsonObject.getLong("Gas");
            long fee;
            if (gasLimit != 0) {
                fee = gasLimit * transaction.gasPrice;
            } else {
                fee = transaction.gasLimit * transaction.gasPrice;
            }
            tvFee.setText(String.format("%s ONG", CommonUtil.formatMoneyFee(CommonUtil.formatONG(fee + ""))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView tvSure = (TextView) inflate.findViewById(R.id.tv_sure);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (actionSure != null) {
                    actionSure.setActionSure();
                }
            }
        });
        setContentView(inflate);
    }

    private ActionSure actionSure;

    public void setActionSure(ActionSure actionSure) {
        this.actionSure = actionSure;
    }

    public interface ActionSure {
        public void setActionSure();
    }
}
