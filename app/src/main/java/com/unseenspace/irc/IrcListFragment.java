package com.unseenspace.irc;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by chris.black on 6/11/15.
 */
public class IrcListFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View.OnClickListener clickListener;
    private IrcOpenHelper openHelper;

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

    private long addIrc(IrcFragment.Template template, String name, String ip, String channel, String username, String password)
    {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(IrcEntry.COLUMN_TEMPLATE, template.name());
        values.put(IrcEntry.COLUMN_NAME, name);
        values.put(IrcEntry.COLUMN_IP, ip);
        values.put(IrcEntry.COLUMN_CHANNEL, template.getChannel(channel, username));
        values.put(IrcEntry.COLUMN_USERNAME, username);
        values.put(IrcEntry.COLUMN_PASSWORD, password);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(IrcEntry.TABLE_NAME, null, values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irc_list, container, false);

        openHelper = new IrcOpenHelper(getActivity());
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contentView);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(new RecyclerView.Adapter<IrcItemHolder>() {
            private View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(v);
                }
            };
            private Drawable drawable = getDrawable(R.attr.itemImage);

            @Override
            public IrcItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                IrcItemHolder shootCardHolder = new IrcItemHolder(parent);
                shootCardHolder.itemView.setOnClickListener(listener);
                return shootCardHolder;
            }

            @Override
            public void onBindViewHolder(IrcItemHolder holder, int position) {
                holder.target.setImageDrawable(drawable);//R.drawable.ic_adjust_white_48dp);
                holder.name.setText("Shoot " + position);
                holder.score.setText("Score: " + (int) (Math.random() * 360));
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        });

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

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createIrc();
            }
        });

        return view;
    }

    private void createIrc() {
        Toast.makeText(getActivity(), "creating irc not really", Toast.LENGTH_SHORT).show();
    }

    private static class IrcItemHolder extends RecyclerView.ViewHolder {

        private final ImageView target;
        private final TextView name;
        private final TextView score;

        public IrcItemHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoot, parent, false));

            target = (ImageView) itemView.findViewById(R.id.shoot_target);
            name = (TextView) itemView.findViewById(R.id.shoot_name);
            score = (TextView) itemView.findViewById(R.id.shoot_score);
        }
    }
}
