package com.unseenspace.android;

/*
 * Copyright (C) 2015 tuanchauict@gmail.com
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
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * created by tuanchauict <a href="https://gist.github.com/tuanchauict/c6c1eda617523de224c5">link</a>
 * Created by tuanchauict on 11/4/15.
 *
 * @param <VH> @{inheritDoc}
 */
public abstract class HeaderCursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends
        CursorRecyclerViewAdapter<VH> {

    /**
     * type of header for the ViewHolder.
     */
    public static final int TYPE_HEADER = -1;
    /**
     * type of normal for the ViewHolder.
     */
    public static final int TYPE_NORMAL_ITEM = 0;

    /**
     * a section.
     */
    public static class Section {
        /**
         * an item count.
         */
        private int itemCount;
        /**
         * the text of the section.
         */
        private String text;

        /**
         * @param itemCount number item of section. Last section can be anything.
         * @param text some text
         */
        public Section(int itemCount, String text) {
            this.itemCount = itemCount;
            this.text = text;
        }
    }

    /**
     * index of the sections.
     */
    private SparseArray<String> mSectionsIndexer;

    /**
     * @param context current context
     * @param cursor  cursor from a database query
     * @param sections a list of sections.
     */
    public HeaderCursorRecyclerViewAdapter(Context context, Cursor cursor, List<Section> sections) {
        super(context, cursor);
        mSectionsIndexer = new SparseArray<>();
        convertSectionList(sections);
    }

    /**
     * @param sections given sections
     */
    private void convertSectionList(List<Section> sections) {
        mSectionsIndexer.clear();
        if (sections != null) {
            int count = 0;
            for (Section section : sections) {
                mSectionsIndexer.put(count, section.text);
                count += section.itemCount + 1;
            }
        }
    }

    /**
     * @param cursor the cursor to be changed to
     * @param sections new set of sections
     */
    public void changeCursor(Cursor cursor, List<Section> sections) {
        super.changeCursor(cursor);
        convertSectionList(sections);
    }

    /**
     * @param sections new sections to be changed to
     */
    public void setSections(List<Section> sections) {
        convertSectionList(sections);
    }

    /**
     * If you have to custom this function, remember to avoid return the value of TYPE_HEADER (-1).
     * none header position
     *
     * @param position position of the item
     * @return the type of the item
     */
    @Override
    public int getItemViewType(int position) {
        if (mSectionsIndexer.indexOfKey(position) >= 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL_ITEM;
        }
    }

    /**
     * @param viewHolder @{inheritDoc}
     * @param position   @{inheritDoc}
     */
    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (viewHolder.getItemViewType() == TYPE_HEADER) {
            onBindSectionHeaderViewHolder(viewHolder, mSectionsIndexer.get(position));
        } else {
            getCursor().moveToPosition(position - countNumberSectionsBefore(position));
            onBindItemViewHolder(viewHolder, getCursor());
        }
    }

    /**
     * @param position the position
     * @return number of sections before the given position
     */
    private int countNumberSectionsBefore(int position) {
        int count = 0;
        for (int i = 0; i < mSectionsIndexer.size(); i++) {
            if (position > mSectionsIndexer.keyAt(i))
                count++;
        }

        return count;
    }

    /**
     * @param headerHolder view holder for the header
     * @param header header's string
     */
    public abstract void onBindSectionHeaderViewHolder(VH headerHolder, String header);

    /**
     * @param itemHolder view holder for an item
     * @param cursor cursor pointing at an row
     */
    public abstract void onBindItemViewHolder(VH itemHolder, Cursor cursor);

    /**
     * @{inheritDoc}
     * @param parent @{inheritDoc}
     * @param viewType @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return onCreateSectionHeaderViewHolder(parent);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    /**
     * @param parent the parent
     * @return a view holder corresponding to the header
     */
    public abstract VH onCreateSectionHeaderViewHolder(ViewGroup parent);

    /**
     * @param parent the parent
     * @param viewType the type of the view
     * @return a view holder corresponding to the header
     */
    public abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    /**
     * @{inheritDoc}
     * @param viewHolder the view holder
     * @param cursor the cursor pointing at a position
     */
    @Override
    public final void onBindViewHolder(VH viewHolder, Cursor cursor) {
        //do nothing
    }

    /**
     * @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if (count > 0) {
            return count + mSectionsIndexer.size();
        } else {
            return 0;
        }
    }
}
