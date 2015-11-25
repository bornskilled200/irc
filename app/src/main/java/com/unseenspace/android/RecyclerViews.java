package com.unseenspace.android;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by madsk_000 on 11/23/2015.
 */
public class RecyclerViews {
    public static class NullAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
