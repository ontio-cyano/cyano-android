package com.github.ont.cyanowallet.game;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.beans.GameListBean;

import java.util.List;

public class RecycleImageAdapter extends RecyclerView.Adapter<RecycleImageAdapter.ImageViewHolder> {
    private Activity mActivity;
    private List<GameListBean.ResultBean.AppsBean> bannerBeans;
    private OnItemClick onItemClick;

    public RecycleImageAdapter(Activity mActivity, List<GameListBean.ResultBean.AppsBean> bannerBeans) {
        this.mActivity = mActivity;
        this.bannerBeans = bannerBeans;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_game_fragment, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder imageViewHolder, int i) {
        imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClick!=null){
                    onItemClick.onItemClick(imageViewHolder.getAdapterPosition());
                }
            }
        });
        GameListBean.ResultBean.AppsBean bean = bannerBeans.get(i);
        Glide.with(mActivity).load(bean.getIcon()).into(imageViewHolder.img);
        imageViewHolder.tvName.setText(bean.getName());
    }

    @Override
    public int getItemCount() {
        if (bannerBeans != null && bannerBeans.size() > 0) {
            return bannerBeans.size();
        } else {
            return 0;
        }
    }

    public void setOnItemClickListern(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        public void onItemClick(int position);
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView tvName;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
