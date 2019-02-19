package com.github.ont.cyanowallet.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.dapp.GameWebActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author zhugang
 */
public class PrivateGameActivity extends BaseActivity implements View.OnClickListener {

    private EditText etNet;
    private ListView lv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_game);
        initView();
        initData();
    }

    private void initData() {
        JSONArray testNets = SPWrapper.getTestNets();
        if (testNets != null) {
            final String[] data = new String[testNets.length()];
            for (int i = 0; i < testNets.length(); i++) {
                try {
                    data[i] = testNets.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            lv.setAdapter(new ArrayAdapter<String>(baseActivity, android.R.layout.simple_list_item_1, data));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(baseActivity, GameWebActivity.class);
                    intent.putExtra(Constant.KEY, data[i]);
                    startActivity(intent);
                }
            });
        }
    }

    private void initView() {
        View layoutBack = findViewById(R.id.layout_back);
        etNet = (EditText) findViewById(R.id.et_net);
        View btnSure = findViewById(R.id.btn_sure);
        layoutBack.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.lv);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_sure:
                String netAddress = etNet.getText().toString();
                if (TextUtils.isEmpty(netAddress)) {
                    ToastUtil.showToast(baseActivity, "No Net Address");
                } else {
                    SPWrapper.addTestNet(netAddress);
                    Intent intent = new Intent(baseActivity, GameWebActivity.class);
                    intent.putExtra(Constant.KEY, netAddress);
                    startActivity(intent);
                }
                break;
            default:
        }
    }
}
