package com.unseenspace.irc;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

/**
 * a fragment showing the given irc
 * <p/>
 * TextView for the chat
 * EditText for messages to send
 * Created by madsk_000 on 10/23/2015.
 */
@SuppressWarnings("SameParameterValue")
public class IrcCreateFragment extends Fragment {

    private final static String TAG = "IrcCreateFragment";
    private static final String EXTRA_IMAGE = "ShootActivity:image";
    private static final String USERNAME = "USERNAME_PARAMETER";
    private static final String PASSWORD = "PASSWORD_PARAMETER";
    private static final String TEMPLATE = "TEMPLATE_PARAMETER";

    private TextView textBox;
    private String channel;
    private Animation enterPortraitAnimation;
    private Animation exitPortraitAnimation;
    private Animation enterLandscapeAnimation;
    private Animation exitLandscapeAnimation;

    @SuppressWarnings("SameParameterValue")

    public static IrcCreateFragment create(IrcEntry.Template template) {
        Log.v(TAG, "create(" + template + ")");

        IrcCreateFragment ircFragment = new IrcCreateFragment();

        // Get arguments passed in, if any
        Bundle args = ircFragment.getArguments();
        if (args == null)
            args = new Bundle();

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
        View view = inflater.inflate(R.layout.fragment_create_irc, container, false);

        textBox = (TextView) view.findViewById(R.id.textBox);
        EditText messageBox = (EditText) view.findViewById(R.id.messageBox);

        if (savedInstanceState == null) {
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
}
