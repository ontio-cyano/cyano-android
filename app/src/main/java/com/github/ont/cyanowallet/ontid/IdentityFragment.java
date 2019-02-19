package com.github.ont.cyanowallet.ontid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.beans.DDOBean;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.BalanceReq;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.view.NestedListView;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhugang
 */
public class IdentityFragment extends BaseFragment implements View.OnClickListener, BaseRequest.ResultListener {
    private static final String TAG = "IdentityFragment";

    private BalanceReq balanceReq;
    private LinearLayout layoutNoIdentity;
    private LinearLayout layoutHasIdentity;
    private TextView tvOntId;
    private TextView tvRecover;
    private NestedListView lvOwner;
    private NestedListView lvAttr;
    private View btnModify;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_identity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutNoIdentity = (LinearLayout) view.findViewById(R.id.layout_no_identity);
        layoutHasIdentity = (LinearLayout) view.findViewById(R.id.layout_has_identity);
        initViewNoIdentity(view);
        initViewHasIdentity(view);
    }

    private void initViewNoIdentity(View view) {
        Button btnNew = (Button) view.findViewById(R.id.btn_new);
        Button btnImport = (Button) view.findViewById(R.id.btn_import);
        btnNew.setOnClickListener(this);
        btnImport.setOnClickListener(this);
    }

    private void initViewHasIdentity(View view) {
        tvOntId = (TextView) view.findViewById(R.id.tv_ontId);
        lvOwner = view.findViewById(R.id.lv_owner);
        lvAttr = view.findViewById(R.id.lv_attr);
        View btnAdd = view.findViewById(R.id.btn_add);
        View btnClaim = view.findViewById(R.id.btn_claim);
        btnModify = view.findViewById(R.id.btn_modify);
        tvRecover = (TextView) view.findViewById(R.id.tv_recover);
        RelativeLayout layoutKYC = (RelativeLayout) view.findViewById(R.id.layout_kyc);


        btnAdd.setOnClickListener(this);
        btnClaim.setOnClickListener(this);
        tvOntId.setOnClickListener(this);
        layoutKYC.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new:
//                if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
//                    baseActivity.showAttention("you need to create a wallet to pay");
//                } else {
//                    testOngBalance();
//                }
                startActivity(new Intent(baseActivity, CreateOntIdActivity.class));
                break;
            case R.id.btn_import:
                startActivity(new Intent(baseActivity, ImportOntIdActivity.class));
                break;
            case R.id.tv_ontId:
                startActivity(new Intent(baseActivity, ReceiverOntIdActivity.class));
                break;
            case R.id.btn_add:
                Intent intent = new Intent(baseActivity, DDOActivity.class);
                intent.putExtra(Constant.KEY, DDOActivity.ADD_CONTROLER);
                startActivity(intent);
                break;
            case R.id.btn_modify:
                String recover = tvRecover.getText().toString();
                Intent intent1 = new Intent(baseActivity, DDOActivity.class);
                intent1.putExtra(Constant.KEY, TextUtils.isEmpty(recover) ? DDOActivity.ADD_RECOVER : DDOActivity.UPDATE_RECOVER);
                intent1.putExtra(Constant.ADDRESS, recover);
                startActivity(intent1);
                break;
            case R.id.layout_kyc:
                startActivity(new Intent(baseActivity, OntIdWebActivity.class));
                break;
            case R.id.btn_claim:
                Intent intent2 = new Intent(baseActivity, OntIdWebActivity.class);
                intent2.putExtra(Constant.KEY, "http://192.168.50.123:8080");
//                intent2.putExtra(Constant.KEY, "http://192.168.50.124:8080/#/identityHome?ontid="+SPWrapper.getDefaultOntId());
                startActivity(intent2);
                break;
            default:

        }
    }


    private void testOngBalance() {
        baseActivity.showLoading();
        if (balanceReq != null) {
            balanceReq.cancel();
        }
        balanceReq = new BalanceReq(SPWrapper.getDefaultAddress());
        balanceReq.setRequestTag(TAG);
        balanceReq.setOnResultListener(this);
        balanceReq.excute();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (TextUtils.isEmpty(com.github.ont.connector.utils.SPWrapper.getDefaultOntId())) {
            layoutNoIdentity.setVisibility(View.VISIBLE);
            layoutHasIdentity.setVisibility(View.GONE);
        } else {
            layoutNoIdentity.setVisibility(View.GONE);
            layoutHasIdentity.setVisibility(View.VISIBLE);
            refresh();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        baseActivity.dismissLoading();
        if (balanceReq != null) {
            balanceReq.cancel();
        }
    }

    private void refresh() {
        tvOntId.setText(com.github.ont.connector.utils.SPWrapper.getDefaultOntId());
        SDKWrapper.getDDO(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                DDOBean ddoBean = JSONObject.parseObject((String) message, DDOBean.class);
                if (ddoBean == null) {
                    return;
                }
                btnModify.setOnClickListener(IdentityFragment.this);
                if (ddoBean.getRecovery() != null) {
                    tvRecover.setText(ddoBean.getRecovery());
                }
                List<DDOBean.OwnersBean> owners = ddoBean.getOwners();
                if (owners != null && owners.size() > 0) {
                    String[] data = new String[owners.size()];
                    for (int i = 0; i < owners.size(); i++) {
                        DDOBean.OwnersBean bean = owners.get(i);
                        try {
                            data[i] = Address.addressFromPubKey(bean.getValue()).toBase58();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    lvOwner.setAdapter(new ArrayAdapter<String>(baseActivity, android.R.layout.simple_list_item_1, data));
                }

                List<DDOBean.AttributesBean> attributes = ddoBean.getAttributes();
                if (attributes != null && attributes.size() > 0) {
                    lvAttr.setAdapter(new AdapterAttr(attributes, baseActivity));
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {

            }
        }, TAG);
    }

    @Override
    public void onResult(Result result) {
        baseActivity.dismissLoading();
        if (result.isSuccess) {
            JSONObject jsonObject = JSONObject.parseObject((String) result.info);
            JSONArray array = jsonObject.getJSONArray("Result");
            JSONObject bean;
            for (int i = 0; i < array.size(); i++) {
                bean = array.getJSONObject(i);
                switch (bean.getString("AssetName")) {
                    case "ong":
                        long balance = CommonUtil.formatDoubleOngToLong(bean.getString("Balance"));
                        if (balance > Constant.FEE) {
                            startActivity(new Intent(baseActivity, CreateOntIdActivity.class));
                        } else {
                            baseActivity.showAttention("Your wallet balance is not enough");
                        }
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public void onResultFail(Result error) {
        baseActivity.dismissLoading();
        baseActivity.showAttention("NET ERROR");
    }
}
