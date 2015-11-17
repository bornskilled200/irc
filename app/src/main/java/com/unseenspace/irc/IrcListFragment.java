package com.unseenspace.irc;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unseenspace.android.Themes;

/**
 * list of all irc entries in the sql database
 *
 * also contains a floating action button to add an irc entry
 * Created by madsk_000 on 10/23/2015.
 */
public class IrcListFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private IrcListener ircListener;
    private IrcOpenHelper openHelper;
    private GestureDetector gestureDetector;
    private RecyclerView recyclerView;
    private Animation enterLandscapeAnimation;
    private Animation exitPortraitAnimation;
    private Animation enterPortraitAnimation;

    @Override
    public void onDetach() {
        super.onDetach();
        ircListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IrcListener)
            ircListener = (IrcListener) context;
    }

    private long addIrc(IrcEntry.Template template, String name, String ip, String channel, String username, String password)
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

        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                IrcItemHolder ircItemHolder = (IrcItemHolder) recyclerView.getChildViewHolder(recyclerView.findChildViewUnder(e.getX(), e.getY()));
                return ircListener.onClick(ircItemHolder.irc);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        });
        recyclerView.setAdapter(new RecyclerView.Adapter<IrcItemHolder>() {
            private final Drawable drawable = Themes.getDrawable(getContext(), R.attr.itemImage);

            @Override
            public IrcItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new IrcItemHolder(parent);
            }

            @Override
            public void onBindViewHolder(IrcItemHolder holder, int position) {
                Resources resources = getResources();
                holder.target.setImageDrawable(drawable);//R.drawable.ic_adjust_white_48dp);
                holder.name.setText(resources.getString(R.string.item_shoot, position));
                holder.score.setText(resources.getString(R.string.item_score, (int) (Math.random() * 360)));
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

        Interpolator interpolator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            interpolator = AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.fast_out_slow_in);
        else
            interpolator = new FastOutSlowInInterpolator();


        enterPortraitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left_full);
        enterPortraitAnimation.setInterpolator(interpolator);

        exitPortraitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left_full);
        exitPortraitAnimation.setInterpolator(interpolator);

        enterLandscapeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_child_bottom);
        enterLandscapeAnimation.setInterpolator(interpolator);
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        int panes = getResources().getInteger(R.integer.panes);
        if (panes == 1)
            return enter?enterPortraitAnimation:exitPortraitAnimation;
        else if (panes == 2)
            return enter?enterLandscapeAnimation: null;
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void createIrc() {
        Toast.makeText(getActivity(), "creating irc not really", Toast.LENGTH_SHORT).show();
    }

    private static class IrcItemHolder extends RecyclerView.ViewHolder {

        private final ImageView target;
        private final IrcEntry irc;
        private final TextView name;
        private final TextView score;

        public IrcItemHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoot, parent, false));

            irc = null;
            target = (ImageView) itemView.findViewById(R.id.shoot_target);
            name = (TextView) itemView.findViewById(R.id.shoot_name);
            score = (TextView) itemView.findViewById(R.id.shoot_score);
        }
    }

    public interface IrcListener {
        boolean onClick(IrcEntry ircEntry);
    }
}
