package com.unseenspace.archery;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
public class ShootsFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context context;
    //protected RecyclerViewAdapter.OnItemClickListener mCallback;

    public static ShootsFragment create() {
        Bundle args = new Bundle();
        ShootsFragment fragment = new ShootsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Drawable getDrawable(int attr)
    {
        int[] attrs = new int[] { attr /* index 0 */};

// Obtain the styled attributes. 'themedContext' is a context with a
// theme, typically the current Activity (i.e. 'this')
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RecyclerView.Adapter<ShootCardHolder> adapter = new RecyclerView.Adapter<ShootCardHolder>() {
            @Override
            public ShootCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ShootCardHolder(parent);
            }

            @Override
            public void onBindViewHolder(ShootCardHolder holder, int position) {
                holder.target.setImageDrawable(getDrawable(R.attr.cardImage));//R.drawable.ic_adjust_white_48dp);
                holder.name.setText("Shoot "  + position);
                holder.score.setText("Score: " + (int) (Math.random()*360));
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        };
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contentView);
        //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
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
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_shoot, parent, false));

            target = (ImageView) itemView.findViewById(R.id.shoot_target);
            name = (TextView) itemView.findViewById(R.id.shoot_name);
            score = (TextView) itemView.findViewById(R.id.shoot_score);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        //if(activity instanceof RecyclerViewAdapter.OnItemClickListener) {
        //    mCallback = (RecyclerViewAdapter.OnItemClickListener)activity;
        //}
    }

    /**
     * Clear callback on detach to prevent null reference errors after the view has been
     */
    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        //mCallback = null;
    }
}
