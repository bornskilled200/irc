package com.unseenspace.irc;

import android.provider.BaseColumns;

import org.pircbotx.Configuration;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.Listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * table name and column names of an IRC row in the sql database.
 * <p/>
 * Created by madsk_000 on 11/5/2015.
 */
public class IrcEntry implements BaseColumns {
    /**
     * table name for Irc's.
     */
    public static final String TABLE_NAME = "irc";
    /**
     * Column name for template.
     */
    public static final String COLUMN_TEMPLATE = "template";
    /**
     * Column name for name.
     */
    public static final String COLUMN_NAME = "name";
    /**
     * Column name for ip.
     */
    public static final String COLUMN_IP = "ip";
    /**
     * Column name for channel.
     */
    public static final String COLUMN_CHANNEL = "channel";
    /**
     * Column name for username.
     */
    public static final String COLUMN_USERNAME = "username";
    /**
     * Column name for password.
     */
    public static final String COLUMN_PASSWORD = "password";

    /**
     * Field for template.
     */
    private Template template;
    /**
     * Field for name.
     */
    private String name;
    /**
     * Field for ip.
     */
    private String ip;
    /**
     * Field for channels.
     */
    private String channels;
    /**
     * Field for username.
     */
    private String username;
    /**
     * Field for password.
     */
    private String password;

    /**
     * Default constructor for IrcEntry.
     * Everything will be set to null except for template which will be Template.IRC
     *
     * @see com.unseenspace.irc.IrcEntry.Template#IRC
     */
    public IrcEntry() {
        template = Template.IRC;
    }

    /**
     * will never be null.
     *
     * @return the current template
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * cannot be null, will be set to Template.IRC instead.
     *
     * @param template the template to change to
     * @see com.unseenspace.irc.IrcEntry.Template#IRC
     */
    public void setTemplate(Template template) {
        if (template == null)
            template = Template.IRC;
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * can return any kind of URL.
     *
     * @return returns the current ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * can be any kind of URL.
     *
     * @param ip the given ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }


    /**
     * split up by space and each channel prepended with #.
     *
     * @return returns the current channels
     */
    public String getChannels() {
        return channels;
    }

    /**
     * split up by space and each channel prepended with #.
     *
     * @param channels the channels
     */
    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Template for irc's, certain IRC servers require specific configuration or handy defaults.
     */
    @SuppressWarnings("SameParameterValue")
    public enum Template {
        /**
         * the template for twitch, enables membership and other nice stuff.
         * if you do not specify a channel, * we assume your username is it
         */
        TWITCH {
            @Override
            public Configuration createConfiguration(String ip, String channels,
                                                     String username, String password, Listener listener) {
                Collection<String> channelCollection;
                if (channels == null || channels.length() == 0)
                    channelCollection = Collections.singleton("#" + username);
                else
                    channelCollection = Arrays.asList(channels.split(" "));

                return new Configuration.Builder()
                        .setAutoNickChange(false) //Twitch doesn't support multiple users
                        .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
                        .setCapEnabled(true)
                                //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it,
                                //see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
                        .addCapHandler(new EnableCapHandler("twitch.tv/membership"))

                        .addServer("irc.twitch.tv")
                        .setName(username) //Your twitch.tv username
                        .setServerPassword(password) //Your oauth password from http://twitchapps.com/tmi
                        .addAutoJoinChannels(channelCollection)
                        .addListener(listener).buildConfiguration();
            }
        },
        /**
         * Just a plain old irc server.
         */
        IRC {
            @Override
            public Configuration createConfiguration(String ip, String channels,
                                                     String username, String password, Listener listener) {
                return new Configuration.Builder()
                        .addServer(ip)
                        .setName(username) //Your twitch.tv username
                        .setServerPassword(password) //Your oauth password from http://twitchapps.com/tmi
                        .addAutoJoinChannel(channels) //Some twitch channel

                        .addListener(listener).buildConfiguration();
            }
        };

        /**
         * Return a Configuration given these parameters.
         *
         * @param ip       any kind of url
         * @param channels channels split by a space and prepended with #
         * @param username username/nick
         * @param password password
         * @param listener listener for the irc actions
         * @return Configuration to build the PircBotX
         */
        public abstract Configuration createConfiguration(String ip, String channels,
                                                          String username, String password, Listener listener);
    }
}
