package com.github.ont.cyanowallet.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.utils.SPWrapper;

public class NetSettingAdapter extends BaseAdapter {
    private Context mContext;
    private String[] nets;

    public NetSettingAdapter(Context mContext, String[] nets) {
        this.mContext = mContext;
        this.nets = nets;
    }

    @Override
    public int getCount() {
        if (nets==null){
            return 0;
        }
        return nets.length;
    }

    @Override
    public Object getItem(int i) {
        return nets[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String net = nets[i];
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
        viewHolderManager.imageView.setVisibility(SPWrapper.getDefaultNet().contains(net) ? View.VISIBLE : View.GONE);
        viewHolderManager.tvAddress.setText(net);
        return view;
    }

    static class ViewHolderManager {
        TextView tvAddress;
        ImageView imageView;
    }
}
