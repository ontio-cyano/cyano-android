package com.github.ont.cyanowallet.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.beans.GameListBean;
import com.github.ont.cyanowallet.dapp.GameWebActivity;
import com.github.ont.cyanowallet.utils.Constant;

import java.util.List;

/**
 * @author zhugang
 */

public class FragmentCommonGame extends BaseFragment {
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_common_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        String data = getArguments().getString(Constant.KEY);
        initData(data);
    }

    private void initData(String data) {
        GameListBean gameListBean = JSONObject.parseObject(data, GameListBean.class);

        //小列表
        final List<GameListBean.ResultBean.AppsBean> apps = gameListBean.getResult().getApps();
        recyclerView.setLayoutManager(new GridLayoutManager(baseActivity, 4));
        RecycleImageAdapter adapter = new RecycleImageAdapter(baseActivity, apps);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListern(new RecycleImageAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(baseActivity, GameWebActivity.class);
                intent.putExtra(Constant.KEY, apps.get(position).getLink());
                startActivity(intent);
            }
        });
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);


    }
}
