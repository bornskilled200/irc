package com.unseenspace.android;

/*
 * Copyright (C) 2014 skyfish.jy@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

/**
 * Created by skyfishjy on 10/31/14.
 * <a href="https://gist.github.com/skyfishjy/443b7448f59be978bc59">Gist</a>
 *
 * @param <VH> @{inheritDoc}
 */
public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    /**
     * the current context.
     */
    private Context mContext;
    /**
     * cursor of the database query.
     */
    private Cursor mCursor;
    /**
     * flag to make sure the data is valid.
     */
    private boolean mDataValid;
    /**
     * the row id column.
     */
    private int mRowIdColumn;
    /**
     * observer for the dataset.
     */
    private DataSetObserver mDataSetObserver;

    /**
     * @param context current context
     * @param cursor  cursor from a database query
     */
    public CursorRecyclerViewAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = newDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    /**
     * convenience method and can be overridden by an extending class to change the observer.
     *
     * @return a new DataSetObserver
     */
    protected DataSetObserver newDataSetObserver() {
        return new NotifyingDataSetObserver();
    }

    public DataSetObserver getDataSetObserver() {
        return mDataSetObserver;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * @return @{inheritDoc}
     * @{inheritDoc}
     */
    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    /**
     * @param position @{inheritDoc}
     * @return @{inheritDoc}
     * @{inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    /**
     * @param hasStableIds @{inheritDoc}
     * @{inheritDoc}
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    /**
     * override to display data at the current position where the cursor is.
     *
     * @param viewHolder the view holder
     * @param cursor the cursor pointing at a position
     */
    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    /**
     * @param viewHolder @{inheritDoc}
     * @param position   @{inheritDoc}
     * @{inheritDoc}
     */
    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     * @param cursor the cursor to be changed to
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor the cursor that will replace the current one
     * @return the current cursor which is not closed
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow(BaseColumns._ID);
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    /**
     * simple observer that will notify us when a change happened.
     */
    private class NotifyingDataSetObserver extends DataSetObserver {
        /**
         * @{inheritDoc}
         */
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        /**
         * @{inheritDoc}
         */
        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
