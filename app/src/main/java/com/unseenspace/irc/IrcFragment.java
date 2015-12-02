package com.unseenspace.irc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.io.IOException;
import java.util.Locale;

import hugo.weaving.DebugLog;

/**
 * a fragment showing the given irc
 * <p/>
 * TextView for the chat
 * EditText for messages to send
 * Created by madsk_000 on 10/23/2015.
 */
@SuppressWarnings("SameParameterValue")
public class IrcFragment extends Fragment implements TextToSpeech.OnInitListener {

    /**
     * A Tag for logging.
     */
    private static final String TAG = "IrcFragment";
    /**
     * An extra image for transitioning 6.0+.
     */
    private static final String EXTRA_IMAGE = "ShootActivity:image";
    /**
     * Parameter name for server in bundle.
     */
    private static final String SERVER = "SERVER_PARAMETER";
    /**
     * Parameter name for channels in bundle.
     */
    private static final String CHANNELS = "CHANNELS_PARAMETER";
    /**
     * Parameter name for username in bundle.
     */
    private static final String USERNAME = "USERNAME_PARAMETER";
    /**
     * Parameter name for password in bundle.
     */
    private static final String PASSWORD = "PASSWORD_PARAMETER";
    /**
     * Parameter name for template in bundle.
     */
    private static final String TEMPLATE = "TEMPLATE_PARAMETER";

    /**
     * Intent that this fragment will broadcast when Text-To-Speech (TTS) is initialized.
     */
    public static final String TTS_INITIALIZED = "TTS_INITIALIZED";
    /**
     * Intent that this fragment will broadcast when connected to an IRC.
     */
    public static final String IRC_CONNECTED = "IRC_CONNECTED";

    /**
     * the textView that will show the chat of the channel.
     */
    private TextView textBox;
    /**
     * the bot that will be handling IRC stuff.
     */
    private PircBotX bot;
    /**
     * the tts class that we use to convert text to speech.
     */
    private TextToSpeech tts;
    /**
     * the configuration that this fragment will follow.
     */
    private Configuration configuration;
    /**
     * the current channel that we are connected to.
     */
    private String channel;

    /**
     * Animation for this fragment entering landscape.
     */
    private Animation enterLandscapeAnimation;
    /**
     * Animation for this fragment exiting landscape.
     */
    private Animation exitLandscapeAnimation;

    /**
     * Animation for this fragment exiting portrait.
     */
    private Animation enterPortraitAnimation;
    /**
     * Animation for this fragment exiting portrait.
     */
    private Animation exitPortraitAnimation;

    /**
     * convenience method to create this fragment.
     *
     * @param template the template to use
     * @param username username/nick
     * @param password password
     * @return a new instance of IrcFragment
     */
    @DebugLog
    public static IrcFragment create(IrcEntry.Template template, String username, String password) {
        IrcFragment ircFragment = new IrcFragment();

        // Get arguments passed in, if any
        Bundle args = ircFragment.getArguments();
        if (args == null)
            args = new Bundle();

        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(TEMPLATE, template.name());
        ircFragment.setArguments(args);

        return ircFragment;
    }

    /**
     * convenience method to create this fragment.
     *
     * @param template the template to use
     * @param server   the ip of the server
     * @param channels channels split by a space and prepended with #
     * @param username username/nick
     * @param password password
     * @return a new instance of IrcFragment
     */
    @DebugLog
    public static IrcFragment create(IrcEntry.Template template, String server, String channels,
                                     String username, String password) {
        IrcFragment ircFragment = new IrcFragment();

        // Get arguments passed in, if any
        Bundle args = ircFragment.getArguments();
        if (args == null)
            args = new Bundle();

        args.putString(SERVER, server);
        args.putString(CHANNELS, channels);
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(TEMPLATE, template.name());
        ircFragment.setArguments(args);

        return ircFragment;
    }

    /**
     * convenience method to check for null and empty string.
     * if null , will return empty string
     *
     * @return will always return a String, never null
     */
    @DebugLog
    private String getServer() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return "";
        String string = arguments.getString(SERVER);
        return string == null ? "" : string;
    }

    /**
     * convenience method to check for null and empty string.
     * if null , will return empty string
     *
     * @return will always return a String, never null
     */
    @DebugLog
    private String getChannels() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return "";
        String string = arguments.getString(CHANNELS);
        return string == null ? "" : string;
    }

    /**
     * convenience method to check for null and empty string.
     * if null , will return empty string
     *
     * @return will always return a String, never null
     */
    @DebugLog
    private String getUserName() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return "";
        String string = arguments.getString(USERNAME);
        return string == null ? "" : string;
    }

    /**
     * convenience method to check for null and empty string.
     * if null , will return empty string
     *
     * @return will always return a String, never null
     */
    @DebugLog
    private String getPassword() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return "";
        String string = arguments.getString(PASSWORD);
        return string == null ? "" : string;
    }

    /**
     * convenience method to check for null and empty string.
     * if null or empty string, will return Template.IRC
     * however it will throw an exception if Enum.valueOf is invalid
     *
     * @return will always the given template in the arguments
     * @see com.unseenspace.irc.IrcEntry.Template#IRC
     */
    @DebugLog
    private IrcEntry.Template getTemplate() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return IrcEntry.Template.IRC;
        String string = arguments.getString(TEMPLATE);
        return string == null ? IrcEntry.Template.IRC : IrcEntry.Template.valueOf(string);
    }

    /**
     * @param inflater           @{inheritDoc}
     * @param container          @{inheritDoc}
     * @param savedInstanceState @{inheritDoc}
     * @return @{inheritDoc}
     * @{inheritDoc}
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irc, container, false);

        textBox = (TextView) view.findViewById(R.id.textBox);
        EditText messageBox = (EditText) view.findViewById(R.id.messageBox);

        if (savedInstanceState == null) {
            tts = new TextToSpeech(getActivity(), this);

            configuration = getTemplate().createConfiguration(getServer(), getChannels(),
                    getUserName(), getPassword(), new ListenerAdapter() {
                @Override
                public void onConnect(ConnectEvent event) throws Exception {
                    alert("Connected", true, new Runnable() {
                        @Override
                        public void run() {
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(IRC_CONNECTED));
                        }
                    });
                }

                @Override
                public void onJoin(JoinEvent event) throws Exception {
                    alert(event.getUser(), "joined", true);
                }

                @Override
                public void onDisconnect(DisconnectEvent event) throws Exception {
                    alert("Disconnected", true);
                }

                @Override
                public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
                    alert(event.getMessage(), true);
                }

                @Override
                public void onMessage(MessageEvent event) throws Exception {
                    alert(event.getMessage(), true);
                }

                private void alert(User user, String text, final boolean add) {
                    alert((user == null ? "Unknown " : (user.getNick() + " ")) + text, add);
                }

                private void alert(final String text, final boolean add) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IrcFragment.this.alert(text, add);
                        }
                    });
                }

                private void alert(final String text, final boolean add, final Runnable runnable) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IrcFragment.this.alert(text, add);
                            runnable.run();
                        }
                    });
                }
            });

            channel = configuration.getAutoJoinChannels().keySet().iterator().next();

            messageBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        sendMessage(v.getText());
                        return true;
                    }
                    return false;
                }
            });
        }

        Interpolator interpolator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            interpolator = AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.fast_out_slow_in);
        else
            interpolator = new FastOutSlowInInterpolator();

        enterPortraitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_full);
        enterPortraitAnimation.setInterpolator(interpolator);

        exitPortraitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right_full);
        exitPortraitAnimation.setInterpolator(interpolator);

        enterLandscapeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_child_bottom);
        enterLandscapeAnimation.setInterpolator(interpolator);

        exitLandscapeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_child_bottom);
        exitLandscapeAnimation.setInterpolator(interpolator);
        return view;
    }

    /**
     * @{inheritDoc}
     * @param transit @{inheritDoc}
     * @param enter @{inheritDoc}
     * @param nextAnim @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        int panes = getResources().getInteger(R.integer.panes);
        if (panes == 1)
            return enter ? enterPortraitAnimation : exitPortraitAnimation;
        else if (panes == 2)
            return enter ? enterLandscapeAnimation : exitLandscapeAnimation;
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    /**
     * convenience method to send a message to the irc.
     *
     * @param message the message to be sent
     */
    private void sendMessage(CharSequence message) {
        textBox.append(message);
        textBox.append("\n");
        bot.send().message(channel, String.valueOf(message));
    }

    /**
     * Convenience method for speaking that takes account for API differences.
     * Pre Lollipop vs Lollipop
     * <p/>
     * will also take consideration of whether it should speak or just have a bell or no alert
     *
     * @param text the text that will be spoken
     * @param add  whether to add or delete previous messages
     */
    @SuppressWarnings("deprecation")
    private void alert(final String text, final boolean add) {
        textBox.append(text);
        textBox.append("\n");
        if (tts == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tts.speak(text, add ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, null, null);
        else
            tts.speak(text, add ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bot != null && bot.isConnected())
            bot.send().quitServer();
        bot = null;
        if (tts != null)
            tts.shutdown();
        tts = null;
    }

    /**
     * @{inheritDoc}
     * @param status @{inheritDoc}
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(getActivity(), "Text To Speech failed to initialize", Toast.LENGTH_LONG).show();
        } else {
            int code = tts.setLanguage(Locale.US);
            if (code == TextToSpeech.LANG_NOT_SUPPORTED || code == TextToSpeech.LANG_MISSING_DATA)
                Toast.makeText(getActivity(), "Text To Speech failed to initialize", Toast.LENGTH_LONG).show();
            else { // EVERYTHING IS OKAY
                alert("Initialized", true);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(TTS_INITIALIZED));
                bot = new PircBotX(configuration);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bot.startBot();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IrcException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
}
