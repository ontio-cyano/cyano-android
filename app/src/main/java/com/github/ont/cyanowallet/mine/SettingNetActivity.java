package com.github.ont.cyanowallet.mine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

public class SettingNetActivity extends BaseActivity implements View.OnClickListener {

    private EditText etNet;
    private TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_net);
        initView();
    }

    private void initView() {
        View layoutBack = findViewById(R.id.layout_back);
        View btnCustom = findViewById(R.id.btn_custom);
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText("Node Choose");
        etNet = (EditText) findViewById(R.id.et_net);
        layoutBack.setOnClickListener(this);
        btnCustom.setOnClickListener(this);
        ListView lv = (ListView) findViewById(R.id.listview);
        ListView lvTest = (ListView) findViewById(R.id.listview_test);

        String[] mainNets = getResources().getStringArray(R.array.list);
        String[] testNets = getResources().getStringArray(R.array.list_test);

        final NetSettingAdapter netSettingAdapter = new NetSettingAdapter(this, mainNets);
        final NetSettingAdapter netSettingAdapter1 = new NetSettingAdapter(this, testNets);

        lv.setAdapter(netSettingAdapter);
        lvTest.setAdapter(netSettingAdapter1);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                setNet(result);
                netSettingAdapter.notifyDataSetChanged();
                netSettingAdapter1.notifyDataSetChanged();
            }
        });
        lvTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                setNet(result);
                netSettingAdapter.notifyDataSetChanged();
                netSettingAdapter1.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_custom:
                String net = etNet.getText().toString();
                if (TextUtils.isEmpty(net)) {
                    ToastUtil.showToast(baseActivity, "net can not be null");
                } else {
                    setNet(net);
                }
                break;
            case R.id.layout_back:
                finish();
                break;
            default:
        }
    }

    private void setNet(String net) {
        SPWrapper.setDefaultNet(net + ":20334");
        SharedPreferences sp = getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
        SDKWrapper.initOntSDK(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {

            }

            @Override
            public void onSDKFail(String tag, String message) {

            }
        }, TAG, SPWrapper.getDefaultNet(), sp);
        ToastUtil.showToast(baseActivity, "Set Net Success");
    }
}
