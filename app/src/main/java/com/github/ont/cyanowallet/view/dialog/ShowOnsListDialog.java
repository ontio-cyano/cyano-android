package com.github.ont.cyanowallet.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.view.dialog.adapter.ShowOnsListAdapter;
import com.github.ontio.sdk.wallet.Account;

import java.util.ArrayList;
import java.util.List;


public class ShowOnsListDialog extends Dialog {

    private Context mContext;
    private List<String> data = new ArrayList<>();
    private ShowOnsListAdapter adapter;

    public ShowOnsListDialog(Context context,List<String> originData) {
        super(context);
        this.mContext = context;
        data.clear();
        data.addAll(originData);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_ons_show, null);
        ListView listView = view.findViewById(R.id.lv);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        setContentView(view);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        adapter = new ShowOnsListAdapter(mContext,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (onChooseListener != null) {
                    onChooseListener.onChooseSuccess(data.get(i));
                    dismiss();
                }
            }
        });

        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.AnimationBottomFade);
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            attributes.gravity = Gravity.BOTTOM;
            // 一定要重新设置, 才能生效
            window.setAttributes(attributes);
            window.setBackgroundDrawableResource(R.color.transparent);
        }
    }


    private OnChooseListener onChooseListener;

    public void setOnChooseListener(OnChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
    }

    public interface OnChooseListener {
        public void onChooseSuccess(String address);

    }

}
