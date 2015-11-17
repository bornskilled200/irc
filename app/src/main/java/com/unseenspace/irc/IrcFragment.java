package com.unseenspace.irc;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
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

/**
 * a fragment showing the given irc
 * <p/>
 * TextView for the chat
 * EditText for messages to send
 * Created by madsk_000 on 10/23/2015.
 */
@SuppressWarnings("SameParameterValue")
public class IrcFragment extends Fragment implements TextToSpeech.OnInitListener {

    private final static String TAG = "IrcFragment";
    private static final String EXTRA_IMAGE = "ShootActivity:image";
    private static final String USERNAME = "USERNAME_PARAMETER";
    private static final String PASSWORD = "PASSWORD_PARAMETER";
    private static final String TEMPLATE = "TEMPLATE_PARAMETER";

    private TextView textBox;
    private PircBotX bot;
    private TextToSpeech tts;
    private Configuration configuration;
    private String channel;
    private Animation enterPortraitAnimation;
    private Animation exitPortraitAnimation;
    private Animation enterLandscapeAnimation;
    private Animation exitLandscapeAnimation;

    public static IrcFragment create(String username, String password, IrcEntry.Template template) {
        Log.v(TAG, "create(" + username + ", String password, " + template + ")");

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

    private String getPassword() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return "";
        String string = arguments.getString(PASSWORD);
        return string == null ? "" : string;
    }

    private IrcEntry.Template getTemplate() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return IrcEntry.Template.IRC;
        String string = arguments.getString(TEMPLATE);
        return string == null ? IrcEntry.Template.IRC : IrcEntry.Template.valueOf(string);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irc, container, false);

        textBox = (TextView) view.findViewById(R.id.textBox);
        EditText messageBox = (EditText) view.findViewById(R.id.messageBox);

        if (savedInstanceState == null) {
            tts = new TextToSpeech(getActivity(), this);

            configuration = getTemplate().createConfiguration(null, null, "unseenspace", getPassword(), new ListenerAdapter() {
                @Override
                public void onConnect(ConnectEvent event) throws Exception {
                    alert("Connected", true);
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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        int panes = getResources().getInteger(R.integer.panes);
        if (panes == 1)
            return enter ? enterPortraitAnimation : exitPortraitAnimation;
        else if (panes == 2)
            return enter ? enterLandscapeAnimation : exitLandscapeAnimation;
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void sendMessage(CharSequence message) {
        textBox.append(message);
        textBox.append("\n");
        bot.send().message(channel, String.valueOf(message));
    }

    /**
     * Convenience method for speaking that takes account for API differences between
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
