package com.unseenspace.irc;

import android.provider.BaseColumns;

import org.pircbotx.Configuration;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.Listener;

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

    @SuppressWarnings("SameParameterValue")
    public enum Template {
        TWITCH {
            @Override
            public Configuration createConfiguration(String ip, String channel, String username, String password, Listener listener) {
                return new Configuration.Builder()
                        .setAutoNickChange(false) //Twitch doesn't support multiple users
                        .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
                        .setCapEnabled(true)
                        .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership

                        .addServer("irc.twitch.tv")
                        .setName(username) //Your twitch.tv username
                        .setServerPassword(password) //Your oauth password from http://twitchapps.com/tmi
                        .addAutoJoinChannel("#" + username) //Some twitch channel

                        .addListener(listener).buildConfiguration();
            }

            @Override
            public String getChannel(String channel, String username) {
                return (channel == null || channel.length() == 0) ? "#" + username : channel;
            }
        }, IRC {
            @Override
            public Configuration createConfiguration(String ip, String channel, String username, String password, Listener listener) {
                return new Configuration.Builder()
                        .addServer(ip)
                        .setName(username) //Your twitch.tv username
                        .setServerPassword(password) //Your oauth password from http://twitchapps.com/tmi
                        .addAutoJoinChannel(channel) //Some twitch channel

                        .addListener(listener).buildConfiguration();
            }

            @Override
            public String getChannel(String channel, String username) {
                return channel;
            }
        };

        public abstract Configuration createConfiguration(String ip, String channel, String username, String password, Listener listener);

        public abstract String getChannel(String channel, String username);
    }
}
