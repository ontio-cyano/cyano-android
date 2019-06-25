package com.github.ont.cyanowallet.view.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.ont.cyanowallet.R;

import java.util.ArrayList;
import java.util.List;

public class ShowOnsListAdapter extends BaseAdapter {
    private List<String> data = new ArrayList<>();
    private Context mContext;

    public ShowOnsListAdapter(Context mContext,List<String> originData) {
        this.mContext = mContext;
        data.clear();
        data.addAll(originData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ShowOnsHolder showOnsHolder;
        if (view == null) {
            showOnsHolder = new ShowOnsHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_show_ons, viewGroup, false);
            showOnsHolder.tvShow = view.findViewById(R.id.tv);
            view.setTag(showOnsHolder);
        } else {
            showOnsHolder = (ShowOnsHolder) view.getTag();
        }
        showOnsHolder.tvShow.setText(data.get(i));
        return view;
    }

    static class ShowOnsHolder {
        TextView tvShow;
    }
}
