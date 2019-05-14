package com.github.ont.cyanowallet.wallet;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.beans.Oep4ListBean;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.BalanceReq;
import com.github.ont.cyanowallet.request.Oep4ListReq;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Account;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends BaseFragment implements View.OnClickListener, BaseRequest.ResultListener {
    private static final String TAG = "WalletFragment";

    private TextView tvAddress;

    private LinearLayout layoutHasWallet;
    private LinearLayout layoutNoWallet;
    private TextView tvOnt;
    private TextView tvOng;
    private TextView tvClaim;
    private BalanceReq balanceReq;
    private String claimOng;
    private String ongBalance;
    private ListView lv;
    private Oep4ListReq oep4ListReq;
    private AdapterOep4 adapter;
    private List<Oep4ListBean.ContractListBean> contractList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutNoWallet = (LinearLayout) view.findViewById(R.id.layout_no_wallet);
        layoutHasWallet = (LinearLayout) view.findViewById(R.id.layout_has_wallet);
        initViewNoWallet(view);
        initViewHasWallet(view);
    }

    private void initViewHasWallet(View view) {
        tvAddress = (TextView) view.findViewById(R.id.tv_address);
        tvOnt = (TextView) view.findViewById(R.id.tv_ont);
        tvOng = (TextView) view.findViewById(R.id.tv_ong);
        tvClaim = (TextView) view.findViewById(R.id.tv_claim);
        Button btnSend = (Button) view.findViewById(R.id.btn_send);
        View btnReceiver =  view.findViewById(R.id.btn_receiver);
        View btnRecord = view.findViewById(R.id.btn_record);
        Button btnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        Button btnAdd = (Button) view.findViewById(R.id.btn_add);
        View btnManage =  view.findViewById(R.id.btn_manage);
        View layoutSendOnt = view.findViewById(R.id.layout_send_ont);
        View layoutSendOng = view.findViewById(R.id.layout_send_ong);
        lv = (ListView) view.findViewById(R.id.lv);

        adapter = new AdapterOep4(this);
        lv.setAdapter(adapter);
        btnSend.setOnClickListener(this);
        btnReceiver.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        tvClaim.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnManage.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        layoutSendOnt.setOnClickListener(this);
        layoutSendOng.setOnClickListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (contractList.size() > 0 && contractList.size() > i) {
                    Oep4ListBean.ContractListBean bean = contractList.get(i);
                    Intent intent = new Intent(baseActivity, Oep4TransferActivity.class);
                    intent.putExtra(Constant.KEY, JSON.toJSONString(bean));
                    startActivity(intent);
                }
            }
        });
    }

    public void refresh() {
        tvAddress.setText(SPWrapper.getDefaultAddress());
//        baseActivity.showLoading();
        if (balanceReq != null) {
            balanceReq.cancel();
        }
        balanceReq = new BalanceReq(SPWrapper.getDefaultAddress());
        balanceReq.setRequestTag(TAG);
        balanceReq.setOnResultListener(this);
        balanceReq.excute();


        //获取资产列表
        if (oep4ListReq != null) {
            oep4ListReq.cancel();
        }
        oep4ListReq = new Oep4ListReq();
        oep4ListReq.setRequestTag("OEP4");
        oep4ListReq.setOnResultListener(new BaseRequest.ResultListener() {
            @Override
            public void onResult(Result result) {
                if (result.isSuccess) {
                    Oep4ListBean oep4ListBean = JSONObject.parseObject((String) result.info, Oep4ListBean.class);
                    if (oep4ListBean.getTotal() > 0) {
                        contractList.clear();
                        contractList.addAll(oep4ListBean.getContractList());
                        adapter.notifyData(contractList);
                    }
                }
            }

            @Override
            public void onResultFail(Result error) {

            }
        });
        oep4ListReq.excute();
    }

    private void initViewNoWallet(View view) {
        Button btnNew = (Button) view.findViewById(R.id.btn_new);
        Button btnImport = (Button) view.findViewById(R.id.btn_import);
        btnNew.setOnClickListener(this);
        btnImport.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new:
                startActivity(new Intent(baseActivity, CreateWalletActivity.class));
                break;
            case R.id.btn_import:
                startActivity(new Intent(baseActivity, ImportWalletActivity.class));
                break;
            case R.id.btn_send:
                startActivity(new Intent(baseActivity, SendWalletActivity.class));
                break;
            case R.id.layout_send_ont:
                Intent intent1 = new Intent(baseActivity, SendWalletActivity.class);
                intent1.putExtra(Constant.KEY, Constant.ONT);
                startActivity(intent1);
                break;
            case R.id.layout_send_ong:
                Intent intent2 = new Intent(baseActivity, SendWalletActivity.class);
                intent2.putExtra(Constant.KEY, Constant.ONG);
                startActivity(intent2);
                break;
            case R.id.btn_receiver:
                startActivity(new Intent(baseActivity, ReceiverWalletActivity.class));
                break;
            case R.id.btn_record:
                Intent intent = new Intent(baseActivity, WebActivity.class);
                if (SPWrapper.getDefaultNet().contains("dappnode")) {
                    intent.putExtra(Constant.KEY, String.format("https://explorer.ont.io/address/%s", SPWrapper.getDefaultAddress()));
                } else if (SPWrapper.getDefaultNet().contains("polaris")) {
                    intent.putExtra(Constant.KEY, String.format("https://explorer.ont.io/address/%s/testnet", SPWrapper.getDefaultAddress()));
                } else {
                    intent.putExtra(Constant.KEY, "");
                }
                startActivity(intent);
                break;
            case R.id.btn_refresh:
                refresh();
                break;
            case R.id.tv_claim:
                if (!TextUtils.equals(claimOng, "0") && CommonUtil.formatDoubleOngToLong(ongBalance) >= Constant.FEE) {
                    claim();
                }
//                testOep4();
                break;
            case R.id.btn_add:
                break;
            case R.id.btn_manage:
                startActivity(new Intent(baseActivity, WalletManageActivity.class));
                break;
            case R.id.tv_address:
                break;
            default:
        }
    }

    private void testOep4() {
        Oep4ListBean.ContractListBean bean =new Oep4ListBean.ContractListBean();
        /**
         * Description : contractsDescriptionTest
         * Symbol : TNT
         * CreateTime : 1530316800
         * ABI : {"contractHash": "2a9cc8a5d0644283e7d7705abe5bbcb979c9bb03"}
         * Creator : AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ
         * TotalSupply : 1000000000
         * Decimals : 8
         * Code : 6a55c36a54c3936a00527ac462c8ff6161616a53c36c7566
         * ContractHash : 49f0908f08b3ebce1e71dc5083cb9a8a54cc4a24
         * Name : TNT coin
         * Logo : https://luckynumber.one/index/img/logo.png
         * OngCount : 0.000000000
         * UpdateTime : 1545209968
         * Addresscount : 8
         * ContactInfo : {"Website":"https://github.com/ontio"}
         * OntCount : 0.000000000
         * TxCount : 851
         */
        bean.setSymbol("ALV");
        bean.setDecimals(18);
        bean.setContractHash("55e02438c938f6f4eb15a9cb315b26d0169b7fd7");
        Intent intent = new Intent(baseActivity, Oep4TransferActivity.class);
        intent.putExtra(Constant.KEY, JSON.toJSONString(bean));
        startActivity(intent);
    }

    private PopupWindow popupWindow;
    private ListView lvName;

    private void initPop(View view) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(baseActivity);
            View contentView = LayoutInflater.from(baseActivity).inflate(R.layout.pop_wallet_name, null);
            lvName = (ListView) contentView.findViewById(R.id.lv_name);
            popupWindow.setContentView(contentView);
            popupWindow.setBackgroundDrawable(new ColorDrawable());
        }
        final ArrayList<String> popName = new ArrayList<>();
        List<Account> accounts = OntSdk.getInstance().getWalletMgr().getWallet().getAccounts();
        if (accounts != null && accounts.size() > 1) {
            for (int i = 0; i < accounts.size(); i++) {
                popName.add(accounts.get(i).address);
            }
        } else {
            return;
        }
        lvName.setAdapter(new ArrayAdapter<String>(baseActivity, R.layout.item_pop_name, popName));
        lvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SPWrapper.setDefaultAddress(popName.get(i));
                popupWindow.dismiss();
                refresh();
            }
        });
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(view);
        }
    }

    private void claim() {
        baseActivity.setGetDialogPwd(new BaseActivity.GetDialogPassword() {
            @Override
            public void handleDialog(String pwd) {
                baseActivity.showLoading();
                SDKWrapper.claimOng(new SDKCallback() {
                    @Override
                    public void onSDKSuccess(String tag, Object message) {
                        baseActivity.dismissLoading();
                        ToastUtil.showToast(baseActivity, "success");
                    }

                    @Override
                    public void onSDKFail(String tag, String message) {
                        baseActivity.dismissLoading();
                        baseActivity.showAttention(message);
                    }
                }, TAG, pwd, CommonUtil.formatDoubleOngToLong(claimOng));
            }
        });
        baseActivity.showPasswordDialog("claim ong");
    }

    @Override
    public void onStop() {
        super.onStop();
        baseActivity.dismissPwdDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
            layoutHasWallet.setVisibility(View.GONE);
            layoutNoWallet.setVisibility(View.VISIBLE);
        } else {
            layoutHasWallet.setVisibility(View.VISIBLE);
            layoutNoWallet.setVisibility(View.GONE);
            refresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        baseActivity.dismissLoading();
        if (balanceReq != null) {
            balanceReq.cancel();
        }
        if (oep4ListReq != null) {
            oep4ListReq.cancel();
        }
    }


    @Override
    public void onResult(Result result) {
        baseActivity.dismissLoading();
        if (result.isSuccess && tvOng != null) {
            JSONObject jsonObject = JSONObject.parseObject((String) result.info);
            JSONArray array = jsonObject.getJSONArray("Result");
            JSONObject bean;
            for (int i = 0; i < array.size(); i++) {
                bean = array.getJSONObject(i);
                switch (bean.getString("AssetName")) {
                    case "ong":
                        ongBalance = bean.getString("Balance");
                        tvOng.setText(ongBalance);
                        break;
                    case "ont":
                        tvOnt.setText(bean.getString("Balance"));
                        break;
                    case "unboundong":
                        claimOng = bean.getString("Balance");
//                        tvClaim.setText(String.format("%s(Claim)", claimOng));
                        tvClaim.setText("Claim ONG : " + claimOng);
                        break;
                    case "waitboundong":
                    default:
                }
            }
        }
    }

    @Override
    public void onResultFail(Result error) {
        baseActivity.dismissLoading();
    }

}
