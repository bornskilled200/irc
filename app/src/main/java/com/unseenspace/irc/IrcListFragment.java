package com.unseenspace.irc;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.unseenspace.android.HeaderCursorRecyclerViewAdapter;
import com.unseenspace.android.RecyclerViews;
import com.unseenspace.android.ScrollFloatingActionButtonScrollingListener;

import java.util.Collections;

/**
 * list of all irc entries in the sql database
 * <p/>
 * also contains a floating action button to add an irc entry
 * Created by madsk_000 on 10/23/2015.
 */
public class IrcListFragment extends Fragment {
    private static final String TAG = "IrcListFragment";
    public static final String INTENT_REFRESH = "IrcListFragment.refresh()";
    private static final String INTENT_CREATE_IRC = "IrcListFragment.create()";

    private IrcListener ircListener;
    private IrcOpenHelper openHelper;

    private Animation enterLandscapeAnimation;
    private Animation exitPortraitAnimation;
    private Animation enterPortraitAnimation;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private IntentFilter filter = new IntentFilter(INTENT_REFRESH);

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

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_irc_list, container, false);

        if (savedInstanceState == null) {
            openHelper = new IrcOpenHelper(getContext());

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
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new RecyclerViews.NullAdapter());

        AsyncTaskCompat.executeParallel(new PopulateList(view), openHelper);

        return view;
    }

    public void refresh()
    {
        View view = getView();

        AsyncTaskCompat.executeParallel(new RefreshList(view), openHelper);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        int panes = getResources().getInteger(R.integer.panes);
        if (panes == 1)
            return enter ? enterPortraitAnimation : exitPortraitAnimation;
        else if (panes == 2)
            return enter ? enterLandscapeAnimation : null;
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void createIrc() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(new Intent(INTENT_CREATE_IRC));
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase writable = openHelper.getWritableDatabase();
                writable.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put(IrcEntry.COLUMN_NAME, Math.random()*100);

                    // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                    writable.insert(IrcEntry.TABLE_NAME, null, values);
                    writable.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.d(TAG, "Error while trying to add post to database");
                } finally {
                    writable.endTransaction();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(new Intent(INTENT_REFRESH));

                Toast.makeText(getActivity(), "creating irc not really", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface IrcListener {
        boolean onClick(IrcEntry ircEntry);
    }

    private static class IrcItemHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final IrcEntry irc;
        private final TextView name;
        private final TextView score;

        public IrcItemHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_irc, parent, false));

            irc = null;
            image = (ImageView) itemView.findViewById(R.id.item_image);
            name = (TextView) itemView.findViewById(R.id.item_name);
            score = (TextView) itemView.findViewById(R.id.item_subtitle);
        }
    }

    private static class IrcRecyclerAdapter extends HeaderCursorRecyclerViewAdapter<IrcItemHolder> {
        private final ColorGenerator generator;
        private final TextDrawable.IBuilder builder;

        public IrcRecyclerAdapter(Context context, Cursor cursor) {
            super(context, cursor, Collections.<Section>emptyList());
            builder = TextDrawable.builder().round();
            generator = ColorGenerator.DEFAULT;

        }

        @Override
        public void onBindSectionHeaderViewHolder(IrcItemHolder headerHolder, String header) {

        }

        @Override
        public void onBindItemViewHolder(IrcItemHolder itemHolder, Cursor cursor) {
            Resources resources = getContext().getResources();
            String name = resources.getString(R.string.item_name, cursor.getString(cursor.getColumnIndex(IrcEntry.COLUMN_NAME)));
            itemHolder.image.setImageDrawable(builder.build(name.length() == 0 ? "U" : name.substring(0, 1), generator.getColor(name)));//R.drawable.ic_adjust_white_48dp);
            itemHolder.name.setText(name);
            itemHolder.score.setText(resources.getString(R.string.item_score, cursor.getInt(cursor.getColumnIndex(IrcEntry._ID))));
        }

        @Override
        public IrcItemHolder onCreateSectionHeaderViewHolder(ViewGroup parent) {
            return new IrcItemHolder(parent);
        }

        @Override
        public IrcItemHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            return new IrcItemHolder(parent);
        }
    }

    private class PopulateList extends AsyncTask<IrcOpenHelper, Void, Cursor> {
        private final View view;
        private View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                createIrc();
            }
        };

        public PopulateList(View view) {
            this.view = view;
        }

        @Override
        protected Cursor doInBackground(IrcOpenHelper... params) {
            SQLiteDatabase readable = params[0].getReadableDatabase();
            return readable.query(IrcEntry.TABLE_NAME, null, null, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            ProgressBar progress = (ProgressBar) view.findViewById(R.id.progress);
            progress.setVisibility(View.GONE);

            FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(onClickListener);

            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setAdapter(new IrcRecyclerAdapter(IrcListFragment.this.getContext(), cursor));
            recyclerView.addOnScrollListener(new ScrollFloatingActionButtonScrollingListener(getContext(), floatingActionButton));
            recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        IrcItemHolder ircItemHolder = (IrcItemHolder) recyclerView.getChildViewHolder(recyclerView.findChildViewUnder(e.getX(), e.getY()));
                        return ircListener.onClick(ircItemHolder.irc);
                    }
                });

                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    gestureDetector.onTouchEvent(e);
                    return false;
                }
            });


        }
    }

    private static class RefreshList extends AsyncTask<IrcOpenHelper, Void, Cursor> {
        private final View view;

        public RefreshList(View view) {
            this.view = view;
        }

        @Override
        protected Cursor doInBackground(IrcOpenHelper... params) {
            SQLiteDatabase readable = params[0].getReadableDatabase();
            return readable.query(IrcEntry.TABLE_NAME, null, null, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setVisibility(View.VISIBLE);

            IrcRecyclerAdapter adapter = (IrcRecyclerAdapter) recyclerView.getAdapter();
            adapter.changeCursor(cursor);
        }
    }
}
