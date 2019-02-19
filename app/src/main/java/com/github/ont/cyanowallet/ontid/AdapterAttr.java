package com.github.ont.cyanowallet.ontid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.beans.DDOBean;
import com.github.ont.cyanowallet.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class AdapterAttr extends BaseAdapter {

    private List<DDOBean.AttributesBean> attributes = new ArrayList<>();

    private Context mContext;

    public AdapterAttr(List<DDOBean.AttributesBean> attributes, Context mContext) {
        this.attributes = attributes;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return attributes.size();
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
        final DDOBean.AttributesBean bean = attributes.get(i);
        ViewAttrHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_ddo_attr, viewGroup, false);
            holder = new ViewAttrHolder();
            holder.tvKey = view.findViewById(R.id.tv_key);
            holder.tvType = view.findViewById(R.id.tv_type);
            holder.btnDetail = view.findViewById(R.id.btn_detail);
            view.setTag(holder);
        } else {
            holder = (ViewAttrHolder) view.getTag();
        }
        holder.tvKey.setText(bean.getKey());
        holder.tvType.setText(bean.getType());
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DDOAttriActivity.class);
                intent.putExtra(Constant.KEY, JSON.toJSONString(bean));
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    class ViewAttrHolder {
        TextView tvKey;
        TextView tvType;
        Button btnDetail;
    }
}
