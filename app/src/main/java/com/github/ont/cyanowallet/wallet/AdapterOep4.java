package com.github.ont.cyanowallet.wallet;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.beans.Oep4ListBean;

import java.util.ArrayList;
import java.util.List;

public class AdapterOep4 extends BaseAdapter {
    private List<Oep4ListBean.ContractListBean> data = new ArrayList<>();
    private Fragment fragment;

    public AdapterOep4(Fragment fragment) {
        this.fragment = fragment;
    }

    public void notifyData(List<Oep4ListBean.ContractListBean> contractList) {
        if (contractList != null && contractList.size() > 0) {
            data.clear();
            data.addAll(contractList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //           * Description : contractsDescriptionTest
//                * Symbol : TNT
//                * CreateTime : 1530316800
//                * ABI : {"contractHash": "2a9cc8a5d0644283e7d7705abe5bbcb979c9bb03"}
//         * Creator : AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ
//                * TotalSupply : 1000000000
//                * Decimals : 8
//                * Code : 6a55c36a54c3936a00527ac462c8ff6161616a53c36c7566
//                * ContractHash : 49f0908f08b3ebce1e71dc5083cb9a8a54cc4a24
//                * Name : TNT coin
//         * Logo : https://luckynumber.one/index/img/logo.png
//         * OngCount : 0.000000000
//                * UpdateTime : 1545209968
//                * Addresscount : 8
//                * ContactInfo : {"Website":"https://github.com/ontio"}
//         * OntCount : 0.000000000
//                * TxCount : 851
        ViewHolderOep4 viewHolder;
        if (view == null) {
            view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.item_oep4, viewGroup, false);
            viewHolder = new ViewHolderOep4();
            viewHolder.imgLogo=view.findViewById(R.id.img_logo);
            viewHolder.tvName=view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderOep4) view.getTag();
        }
        Oep4ListBean.ContractListBean bean = data.get(i);
        Glide.with(fragment).load(bean.getLogo()).error(R.drawable.oep4_token).into(viewHolder.imgLogo);
        viewHolder.tvName.setText(bean.getSymbol());
        return view;
    }

    class ViewHolderOep4 {
        ImageView imgLogo;
        TextView tvName;
    }
}
