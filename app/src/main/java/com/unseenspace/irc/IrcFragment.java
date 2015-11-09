package com.unseenspace.irc;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by madsk_000 on 10/23/2015.
 */
public class IrcFragment extends Fragment implements TextToSpeech.OnInitListener {

    private final static String TAG = "IrcFragment";
    private static final String EXTRA_IMAGE = "ShootActivity:image";
    private static final String USERNAME = "USERNAME_PARAMETER";
    private static final String PASSWORD = "PASSWORD_PARAMETER";
    private static final String TEMPLATE = "TEMPLATE_PARAMETER";

    private TextView textBox;
    private EditText messageBox;
    private PircBotX bot;
    private TextToSpeech tts;
    private Configuration configuration;
    private String channel;

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
                return (channel == null || channel.length() == 0)?"#" + username:channel;
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

    public static IrcFragment create(String username, String password, Template template) {
        Log.v(TAG, "create(" + username + ", String password)");

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

    private String getPassword()
    {
        Bundle arguments = getArguments();
        if (arguments == null)
            return "";
        String string = arguments.getString(PASSWORD);
        return string == null? "" : string;
    }

    private Template getTemplate()
    {
        Bundle arguments = getArguments();
        if (arguments == null)
            return Template.IRC;
        String string = arguments.getString(TEMPLATE);
        return string == null? Template.IRC : Template.valueOf(string);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irc, container, false);

        textBox = (TextView) view.findViewById(R.id.textBox);
        messageBox = (EditText) view.findViewById(R.id.messageBox);

        if (savedInstanceState == null) {
            tts = new TextToSpeech(getActivity(), this);

            configuration = getTemplate().createConfiguration(null, null, "unseenspace", getPassword(), new ListenerAdapter() {
                @Override
                public void onConnect(ConnectEvent event) throws Exception {
                    alert("Connected", true);
                }

                @Override
                public void onJoin(JoinEvent event) throws Exception {
                    alert(event.getUser().getNick() + " joined", true);
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

        return view;
    }

    private void sendMessage(CharSequence message) {
        textBox.append(message);
        textBox.append("\n");
        bot.send().message(channel, String.valueOf(message));
    }

    /**
     * Convenience method for speaking that takes account for API differences between
     * Pre Lollipop vs Lollipop
     *
     * will also take consideration of whether it should speak or just have a bell or no alert
     * @param text the text that will be spoken
     * @param add whether to add or delete previous messages
     */
    @SuppressWarnings("deprecation")
    private void alert(final String text, final boolean add)
    {
        textBox.append(text);
        textBox.append("\n");
        if (tts == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tts.speak(text, add?TextToSpeech.QUEUE_ADD:TextToSpeech.QUEUE_FLUSH, null, null);
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
