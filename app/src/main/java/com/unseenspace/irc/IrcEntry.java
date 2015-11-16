package com.unseenspace.irc;

import android.provider.BaseColumns;

/**
 * table name and column names of an IRC row in the sql database
 * Created by madsk_000 on 11/5/2015.
 */
public class IrcEntry implements BaseColumns{
    public static final String TABLE_NAME = "irc";
    public static final String COLUMN_TEMPLATE = "template";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_CHANNEL = "channel";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
}
