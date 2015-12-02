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
import android.support.v4.app.FragmentActivity;
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
import com.unseenspace.android.ScrollingFloatingActionButtonListener;

import java.util.Collections;

/**
 * list of all irc entries in the sql database
 * <p/>
 * also contains a floating action button to add an irc entry
 * Created by madsk_000 on 10/23/2015.
 */
public class IrcListFragment extends Fragment {
    /**
     * Intent that this fragment will listen for when you want it to refresh the list.
     */
    public static final String INTENT_REFRESH = "IrcListFragment.refresh()";
    /**
     * Its a magic number, so that checkstyle stops complaining about a magic number.
     */
    public static final int MAGIC_NUMBER = 100;
    /**
     * For logging.
     */
    private static final String TAG = "IrcListFragment";
    /**
     * Intent that this fragment will broadcast to move to IrcCreateFragment.
     */
    private static final String INTENT_CREATE_IRC = "IrcListFragment.create()";
    /**
     * The listener that will react to clicking an IrcEntry.
     */
    private IrcListener ircListener;
    /**
     * Database Open helper for irc table.
     */
    private IrcOpenHelper openHelper;

    /**
     * Animation for this fragment entering landscape.
     */
    private Animation enterLandscapeAnimation;

    /**
     * Animation for this fragment exiting portrait.
     */
    private Animation enterPortraitAnimation;
    /**
     * Animation for this fragment exiting portrait.
     */
    private Animation exitPortraitAnimation;


    /**
     * Animation for this fragment exiting portrait.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    /**
     * Animation for this fragment exiting portrait.
     */
    private IntentFilter filter = new IntentFilter(INTENT_REFRESH);

    /**
     * @{inheritDoc}
     */
    @Override
    public void onDetach() {
        super.onDetach();
        ircListener = null;
    }

    /**
     * @param context @{inheritDoc}
     * @{inheritDoc}
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IrcListener)
            ircListener = (IrcListener) context;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    /**
     * @param inflater           @{inheritDoc}
     * @param container          @{inheritDoc}
     * @param savedInstanceState @{inheritDoc}
     * @return @{inheritDoc}
     * @{inheritDoc}
     */
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

        AsyncTaskCompat.executeParallel(new PopulateListTask(view), openHelper);

        return view;
    }

    /**
     * convenience method to refreshing the list.
     */
    public void refresh() {
        View view = getView();

        AsyncTaskCompat.executeParallel(new RefreshList(view), openHelper);
    }

    /**
     * @param transit  @{inheritDoc}
     * @param enter    @{inheritDoc}
     * @param nextAnim @{inheritDoc}
     * @return @{inheritDoc}
     * @{inheritDoc}
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        int panes = getResources().getInteger(R.integer.panes);
        if (panes == 1)
            return enter ? enterPortraitAnimation : exitPortraitAnimation;
        else if (panes == 2)
            return enter ? enterLandscapeAnimation : null;
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    /**
     * convenience method for loading up all the data.
     */
    private void createIrc() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(new Intent(INTENT_CREATE_IRC));
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase writable = openHelper.getWritableDatabase();
                writable.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put(IrcEntry.COLUMN_NAME, Math.random() * MAGIC_NUMBER);

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

    /**
     * listener that listens for clicking on an IrcEntry.
     */
    public interface IrcListener {
        /**
         * gets called when and irc entry was clicked.
         *
         * @param ircEntry the irc that was clicked on
         * @return true if the event was consumed
         */
        boolean onClick(IrcEntry ircEntry);
    }

    /**
     * A view holder for an IrcEntry.
     */
    private static class IrcItemHolder extends RecyclerView.ViewHolder {
        /**
         * the ircEntry that represents this viewholder.
         */
        private final IrcEntry irc;

        /**
         * the image to the left/start of the item.
         */
        private final ImageView image;
        /**
         * the bold text at the top of the item.
         */
        private final TextView name;

        /**
         * the subtitle/body of the text below name.
         */
        private final TextView score;

        /**
         * normal constructor of this viewholder, insanities and assigns the field variables pertaining to the view.
         *
         * @param parent @{inheritDoc}
         */
        public IrcItemHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_irc, parent, false));

            irc = null;
            image = (ImageView) itemView.findViewById(R.id.item_image);
            name = (TextView) itemView.findViewById(R.id.item_name);
            score = (TextView) itemView.findViewById(R.id.item_subtitle);
        }
    }

    /**
     * The actual recyclerAdapter that pulls its info from the database.
     */
    private static class IrcRecyclerAdapter extends HeaderCursorRecyclerViewAdapter<IrcItemHolder> {
        /**
         * The color generator for default icons.
         */
        private final ColorGenerator generator;
        /**
         * Builder for default icons.
         */
        private final TextDrawable.IBuilder builder;

        /**
         * @param context @{inheritDoc}
         * @param cursor  @{inheritDoc}
         * @{inheritDoc}
         */
        public IrcRecyclerAdapter(Context context, Cursor cursor) {
            super(context, cursor, Collections.<Section>emptyList());
            builder = TextDrawable.builder().round();
            generator = ColorGenerator.DEFAULT;

        }

        /**
         * currently unsued.
         *
         * @param headerHolder @{inheritDoc}
         * @param header       @{inheritDoc}
         */
        @Override
        public void onBindSectionHeaderViewHolder(IrcItemHolder headerHolder, String header) {

        }

        /**
         * binds the ItemHolder to IrcEntry that the cursor points to.
         *
         * @param itemHolder @{inheritDoc}
         * @param cursor     @{inheritDoc}
         */
        @Override
        public void onBindItemViewHolder(IrcItemHolder itemHolder, Cursor cursor) {
            Resources resources = getContext().getResources();
            String name = cursor.getString(cursor.getColumnIndex(IrcEntry.COLUMN_NAME));
            String processedName = resources.getString(R.string.item_name, name);
            int score = cursor.getInt(cursor.getColumnIndex(IrcEntry._ID));

            String firstCharacter = processedName.length() == 0 ? "U" : processedName.substring(0, 1);
            itemHolder.image.setImageDrawable(builder.build(firstCharacter, generator.getColor(processedName)));
            itemHolder.name.setText(processedName);
            itemHolder.score.setText(resources.getString(R.string.item_score, score));
        }

        /**
         * @param parent @{inheritDoc}
         * @return @{inheritDoc}
         * @{inheritDoc}
         */
        @Override
        public IrcItemHolder onCreateSectionHeaderViewHolder(ViewGroup parent) {
            return new IrcItemHolder(parent);
        }

        /**
         * @param parent   @{inheritDoc}
         * @param viewType @{inheritDoc}
         * @return @{inheritDoc}
         * @{inheritDoc}
         */
        @Override
        public IrcItemHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            return new IrcItemHolder(parent);
        }
    }

    /**
     * Async Task to refresh the list.
     */
    private static class RefreshList extends AsyncTask<IrcOpenHelper, Void, Cursor> {
        /**
         * The current view of this fragment.
         */
        private final View view;

        /**
         * refreshes the RecyclerView in the given view.
         *
         * @param view the current view of the fragment
         */
        public RefreshList(View view) {
            this.view = view;
        }

        /**
         * opens the readable database then return the query for all ircEntry's.
         *
         * @param params @{inheritDoc}
         * @return @{inheritDoc}
         */
        @Override
        protected Cursor doInBackground(IrcOpenHelper... params) {
            SQLiteDatabase readable = params[0].getReadableDatabase();
            return readable.query(IrcEntry.TABLE_NAME, null, null, null, null, null, null);
        }

        /**
         * @param cursor @{inheritDoc}
         * @{inheritDoc}
         */
        @Override
        protected void onPostExecute(Cursor cursor) {
            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setVisibility(View.VISIBLE);

            IrcRecyclerAdapter adapter = (IrcRecyclerAdapter) recyclerView.getAdapter();
            adapter.changeCursor(cursor);
        }
    }

    /**
     * Async Task to populate and the list and show everything afterwords.
     */
    private class PopulateListTask extends AsyncTask<IrcOpenHelper, Void, Cursor> {
        /**
         * The current view of this fragment.
         */
        private final View view;

        /**
         * floating action button listener.
         */
        private View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                createIrc();
            }
        };

        /**
         * refreshes the RecyclerView in the given view.
         *
         * @param view the current view of the fragment
         */
        public PopulateListTask(View view) {
            this.view = view;
        }

        /**
         * opens the readable database then return the query for all ircEntry's.
         *
         * @param params @{inheritDoc}
         * @return @{inheritDoc}
         */
        @Override
        protected Cursor doInBackground(IrcOpenHelper... params) {
            SQLiteDatabase readable = params[0].getReadableDatabase();
            return readable.query(IrcEntry.TABLE_NAME, null, null, null, null, null, null);
        }

        /**
         * show everything correctly in the fragment.
         * makes progressbar invisible
         * makes floating action button visible
         * sets the adapter and other stuff on RecyclerView
         *
         * @param cursor @{inheritDoc}
         */
        @Override
        protected void onPostExecute(Cursor cursor) {
            final FragmentActivity activity = getActivity();
            final Context context = getContext();

            ProgressBar progress = (ProgressBar) view.findViewById(R.id.progress);
            progress.setVisibility(View.GONE);

            FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(onClickListener);

            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setAdapter(new IrcRecyclerAdapter(context, cursor));
            recyclerView.addOnScrollListener(new ScrollingFloatingActionButtonListener(context, floatingActionButton));
            recyclerView.addOnItemTouchListener(new SimpleOnItemTouchListener(activity, recyclerView));


        }

        /**
         * Simple listener that reacts to an item being clicked on.
         */
        private class SimpleOnItemTouchListener extends RecyclerView.SimpleOnItemTouchListener {
            /**
             * GestureListener to listen for all sorts of gestures, for now its only for click.
             */
            private GestureDetector gestureDetector;

            /**
             *
             * @param activity the current activity
             * @param recyclerView the recyclerView that this listener will bind to
             */
            public SimpleOnItemTouchListener(FragmentActivity activity, final RecyclerView recyclerView) {
                gestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        IrcItemHolder ircItemHolder = (IrcItemHolder) recyclerView.getChildViewHolder(view);
                        return ircListener.onClick(ircItemHolder.irc);
                    }
                });
            }

            /**
             * @{inheritDoc}
             * @param rv @{inheritDoc}
             * @param e @{inheritDoc}
             * @return @{inheritDoc}
             */
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        }
    }
}
