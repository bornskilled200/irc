package com.unseenspace.irc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * sql database opener for irc entries
 *
 * Created by madsk_000 on 11/4/2015.
 */
public class IrcOpenHelper extends SQLiteOpenHelper {
    /**
     * The database version of Irc.
     */
    private static final int DATABASE_VERSION = 4;

    /**
     * The file name of irc database.
     */
    public static final String DATABASE_NAME = "UnseenIrc.db";

    /**
     * sql command for creating irc table.
     */
    private static final String IRC_TABLE_CREATE =
            "CREATE TABLE " + IrcEntry.TABLE_NAME + " ("
                    + IrcEntry._ID + " INTEGER PRIMARY KEY,"
                    + IrcEntry._COUNT + " INTEGER,"
                    + IrcEntry.COLUMN_TEMPLATE + " TEXT,"
                    + IrcEntry.COLUMN_NAME + " TEXT,"
                    + IrcEntry.COLUMN_IP + " TEXT,"
                    + IrcEntry.COLUMN_CHANNEL + " TEXT,"
                    + IrcEntry.COLUMN_USERNAME + " TEXT,"
                    + IrcEntry.COLUMN_PASSWORD + " TEXT)";

    /**
     * @{inheritDoc}
     * @param context @{inheritDoc}
     */
    public IrcOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @{inheritDoc}
     * @param db @{inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(IRC_TABLE_CREATE);
    }

    /**
     * @{inheritDoc}
     * @param db @{inheritDoc}
     * @param oldVersion @{inheritDoc}
     * @param newVersion @{inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + IrcEntry.TABLE_NAME);

        onCreate(db);
    }
}
