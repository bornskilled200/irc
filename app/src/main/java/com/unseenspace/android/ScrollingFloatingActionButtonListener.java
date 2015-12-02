package com.unseenspace.android;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.unseenspace.irc.R;

/**
 * Alternative approach to animation FloatingActionButton out when scrolled.
 * Benefits is that, if you are not using any other CoordinatorLayout.Behavior that return true on onStartNestedScroll
 * (like AppBarLayout), You will get the edge glow from over scrolling
 *
 * code converted from https://github.com/ianhanniballake/cheesesquare/commit/aefa8b57e61266e4ad51bef36e669d69f7fd749c
 */
public class ScrollingFloatingActionButtonListener extends RecyclerView.OnScrollListener {
    /**
     * the interpolator for fading in and out.
     */
    private final Interpolator interpolator;

    /**
     * flag to know if we are animating out.
     */
    private boolean mIsAnimatingOut = false;

    /**
     * the floating action button this class is controlling.
     */
    private FloatingActionButton floatingActionButton;

    /**
     *
     * @param context current context
     * @param floatingActionButton the floating action button this listener will control
     */
    public ScrollingFloatingActionButtonListener(Context context, FloatingActionButton floatingActionButton) {
        this.floatingActionButton = floatingActionButton;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in);
        else
            interpolator = new FastOutSlowInInterpolator();
    }

    /**
     * @{inheritDoc}
     * @param recyclerView @{inheritDoc}
     * @param dx @{inheritDoc}
     * @param dy @{inheritDoc}
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0 && !mIsAnimatingOut && floatingActionButton.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(floatingActionButton);
        } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(floatingActionButton);
        }
    }


    /**
     * Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits.
     * @param button the current button
     */
    private void animateOut(final FloatingActionButton button) {
        Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_out);
        anim.setInterpolator(interpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                ScrollingFloatingActionButtonListener.this.mIsAnimatingOut = true;
            }

            public void onAnimationEnd(Animation animation) {
                ScrollingFloatingActionButtonListener.this.mIsAnimatingOut = false;
                button.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        button.startAnimation(anim);
    }

    /**
     * Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters.
     * @param button the current button
     */
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_in);
        anim.setInterpolator(interpolator);
        button.startAnimation(anim);
    }
}
