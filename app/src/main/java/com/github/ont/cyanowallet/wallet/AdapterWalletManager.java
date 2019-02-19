package com.github.ont.cyanowallet.wallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Account;

import java.io.IOException;
import java.util.List;

public class AdapterWalletManager extends BaseAdapter {
    private Context mContext;
    private List<Account> accounts;

    public AdapterWalletManager(Context mContext) {
        this.mContext = mContext;
    }

    public void notify(List<Account> data) {
        if (accounts == null) {
            accounts = data;
        } else {
            accounts.clear();
            accounts.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (accounts == null) {
            return 0;
        }
        return accounts.size();
    }

    @Override
    public Object getItem(int i) {
        return accounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Account account = accounts.get(i);
        ViewHolderManager viewHolderManager;
        if (view == null) {
            viewHolderManager = new ViewHolderManager();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_wallet_manage, viewGroup, false);
            viewHolderManager.tvAddress = view.findViewById(R.id.tv_address);
            viewHolderManager.imageView = view.findViewById(R.id.img);
            view.setTag(viewHolderManager);
        } else {
            viewHolderManager = (ViewHolderManager) view.getTag();
        }
        viewHolderManager.imageView.setVisibility(account.isDefault ? View.VISIBLE : View.GONE);
        viewHolderManager.tvAddress.setText(account.address);
        return view;
    }

    static class ViewHolderManager {
        TextView tvAddress;
        ImageView imageView;
    }
}
