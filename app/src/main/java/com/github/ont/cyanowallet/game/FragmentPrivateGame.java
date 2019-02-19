package com.github.ont.cyanowallet.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.dapp.GameWebActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author zhugang
 */

public class FragmentPrivateGame extends BaseFragment implements View.OnClickListener {
    private EditText etNet;
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(baseActivity, android.R.layout.simple_list_item_1, data);
            lv.setAdapter(adapter);
            setMeasure(lv,adapter);
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

    private void setMeasure(ListView lv,ArrayAdapter<String> adapter) {
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, lv);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lv.getLayoutParams();

        params.height = totalHeight
                + (lv.getDividerHeight() * (adapter.getCount() - 1));


        lv.setLayoutParams(params);

    }

    private void initView(View view) {
        etNet = (EditText) view.findViewById(R.id.et_net);
        View btnSure = view.findViewById(R.id.btn_sure);
        btnSure.setOnClickListener(this);
        lv = (ListView) view.findViewById(R.id.lv);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
