package com.unseenspace.irc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by madsk_000 on 11/4/2015.
 */
public class IrcOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String IRC_TABLE_CREATE =
            "CREATE TABLE " + IrcEntry.TABLE_NAME + " (" +
                    IrcEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    IrcEntry.COLUMN_TEMPLATE + " TEXT," +
                    IrcEntry.COLUMN_NAME + " TEXT," +
                    IrcEntry.COLUMN_IP + " TEXT," +
                    IrcEntry.COLUMN_CHANNEL + " TEXT," +
                    IrcEntry.COLUMN_USERNAME + " TEXT," +
                    IrcEntry.COLUMN_PASSWORD + " TEXT)";

    public IrcOpenHelper(Context context) {
        super(context, IrcEntry.TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(IRC_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + IrcEntry.TABLE_NAME);
    }
}
