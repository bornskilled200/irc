package com.unseenspace.irc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by chris.black on 6/11/15.
 */
public class IrcListFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View.OnClickListener clickListener;

    private Drawable getDrawable(int attr) {
        int[] attrs = new int[]{attr /* index 0 */};

        // Obtain the styled attributes. 'themedContext' is a context with a
        // theme, typically the current Activity (i.e. 'this')
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs);

        // To get the value of the 'listItemBackground' attribute that was
        // set in the theme used in 'themedContext'. The parameter is the index
        // of the attribute in the 'attrs' array. The returned Drawable
        // is what you are after
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);

        // Finally, free the resources used by TypedArray
        ta.recycle();

        return drawableFromTheme;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof View.OnClickListener)
            clickListener = (View.OnClickListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irc_list, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(new RecyclerView.Adapter<ShootCardHolder>() {
            private View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(v);
                }
            };
            private Drawable drawable = getDrawable(R.attr.itemImage);

            @Override
            public ShootCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ShootCardHolder shootCardHolder = new ShootCardHolder(parent);
                shootCardHolder.itemView.setOnClickListener(listener);
                return shootCardHolder;
            }

            @Override
            public void onBindViewHolder(ShootCardHolder holder, int position) {
                holder.target.setImageDrawable(drawable);//R.drawable.ic_adjust_white_48dp);
                holder.name.setText("Shoot " + position);
                holder.score.setText("Score: " + (int) (Math.random() * 360));
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contentView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return view;
    }

    private static class ShootCardHolder extends RecyclerView.ViewHolder {

        private final ImageView target;
        private final TextView name;
        private final TextView score;

        public ShootCardHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoot, parent, false));

            target = (ImageView) itemView.findViewById(R.id.shoot_target);
            name = (TextView) itemView.findViewById(R.id.shoot_name);
            score = (TextView) itemView.findViewById(R.id.shoot_score);
        }
    }
}
