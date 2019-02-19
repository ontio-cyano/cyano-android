package com.github.ont.cyanowallet.mine;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.ont.connector.ontid.TestFrameActivity;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Wallet;

import java.io.IOException;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "MineFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        TextView tvVersion = (TextView) view.findViewById(R.id.tv_version);
        View btnSetting = view.findViewById(R.id.btn_setting);
        View btnOntId = view.findViewById(R.id.btn_ontid);

        btnSetting.setOnClickListener(this);
        btnOntId.setOnClickListener(this);
        String verName = "";
        try {
            verName = baseActivity.getPackageManager().
                    getPackageInfo(baseActivity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        tvVersion.append(verName);
        tvVersion.append(" Beta");
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                startActivity(new Intent(baseActivity, SettingNetActivity.class));
                break;
            case R.id.btn_ontid:
                if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
                    ToastUtil.showToast(baseActivity, "You need wallet first");
                } else {
                    Intent intent = new Intent(baseActivity, TestFrameActivity.class);
                    startActivity(intent);
                }
                break;
            default:
        }
    }

    @Override
    public void onStop() {
        baseActivity.dismissPwdDialog();
        super.onStop();
    }
}
