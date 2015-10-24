package com.unseenspace.irc;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.VoiceEvent;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by madsk_000 on 10/23/2015.
 */
public class IrcActivity extends BaseActivity implements TextToSpeech.OnInitListener {

    private final static String TAG = "IrcActivity";
    private static final String EXTRA_IMAGE = "ShootActivity:image";
    private DrawerLayout drawerLayout;

    private TextView textBox;
    private EditText messageBox;
    private PircBotX bot;
    private boolean ttsInitialized;
    private TextToSpeech tts;
    private Configuration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irc);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab != null)
            ab.setDisplayHomeAsUpEnabled(true);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            setupDrawerContent(navigationView, drawerLayout, this);


        textBox = (TextView) findViewById(R.id.textBox);
        messageBox = (EditText) findViewById(R.id.messageBox);

        tts = new TextToSpeech(this, this);

        configuration = new Configuration.Builder()
                .setAutoNickChange(false) //Twitch doesn't support multiple users
                .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
                .setCapEnabled(true)
                .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership

                .addServer("irc.twitch.tv")
                .setName("unseenspace") //Your twitch.tv username
                .setServerPassword(preferences.getString("password", "default")) //Your oauth password from http://twitchapps.com/tmi
                .addAutoJoinChannel("#unseenspace") //Some twitch channel

                .addListener(new ListenerAdapter(){
                    @Override
                    public void onConnect(ConnectEvent event) throws Exception {
                        speak("Connected", true);
                    }

                    @Override
                    public void onJoin(JoinEvent event) throws Exception {
                        speak(event.getUser().getNick() + " joined", true);
                    }

                    @Override
                    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
                        speak(event.getMessage(), true);
                    }

                    @Override
                    public void onMessage(MessageEvent event) throws Exception {
                        speak(event.getMessage(), true);
                    }
                }).buildConfiguration();
    }

    @SuppressWarnings("deprecation")
    public void speak(String text, boolean add)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tts.speak(text, add?TextToSpeech.QUEUE_ADD:TextToSpeech.QUEUE_FLUSH, null, null);
        else
            tts.speak(text, add?TextToSpeech.QUEUE_ADD:TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bot != null)
            bot.send().quitServer();
        bot = null;
        if (tts != null)
            tts.shutdown();
        tts = null;
    }

    public static void launch(Activity activity, View transitionView) {
        Intent intent = new Intent(activity, IrcActivity.class);

        Bundle bundle = null;
        if (transitionView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, EXTRA_IMAGE);

            int drawable = R.drawable.ic_adjust_white_48dp;
            if (transitionView.getTag() instanceof Integer)
                drawable = (Integer) transitionView.getTag();
            intent.putExtra(EXTRA_IMAGE, drawable);

            bundle = options.toBundle();
        }

        ActivityCompat.startActivity(activity, intent, bundle);
    }

    @Override
    public void onInit(int status) {
        ttsInitialized = true;
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Text To Speech failed to initialize", Toast.LENGTH_LONG).show();
        } else {
            int code = tts.setLanguage(Locale.US);
            if (code == TextToSpeech.LANG_NOT_SUPPORTED || code == TextToSpeech.LANG_MISSING_DATA)
                Toast.makeText(this, "Text To Speech failed to initialize", Toast.LENGTH_LONG).show();
            else { // EVERYTHING IS OKAY
                speak("Initialized", true);
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
