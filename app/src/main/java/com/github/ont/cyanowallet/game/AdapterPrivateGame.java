package com.github.ont.cyanowallet.game;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class AdapterPrivateGame extends RecyclerView.Adapter<AdapterPrivateGame.ViewPrivateHolder> {
    private String[] data;


    @NonNull
    @Override
    public ViewPrivateHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPrivateHolder viewPrivateHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewPrivateHolder extends RecyclerView.ViewHolder {
        public ViewPrivateHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
