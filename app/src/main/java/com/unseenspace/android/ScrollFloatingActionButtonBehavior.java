package com.unseenspace.android;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.unseenspace.irc.R;

/**
 * make it so that a floating action button will disappear and reappear when scrolling.
 * from https://github.com/ianhanniballake/cheesesquare/commit/aefa8b57e61266e4ad51bef36e669d69f7fd749c
 */
public class ScrollFloatingActionButtonBehavior extends FloatingActionButton.Behavior {
    /**
     * the interpolator for fading in and out.
     */
    private final Interpolator interpolator;

    /**
     * flag to know if we are animating out.
     */
    private boolean mIsAnimatingOut = false;


    /**
     * expected constructor when put in xml.
     *
     * @param context current context
     * @param attrs the floating action button this listener will control
     */
    public ScrollFloatingActionButtonBehavior(Context context, AttributeSet attrs) {
        super();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in);
        else
            interpolator = new FastOutSlowInInterpolator();
    }

    /**
     * when the target scrolls, we only react on vertical scrolling.
     *
     * @param coordinatorLayout @{inheritDoc}
     * @param child @{inheritDoc}
     * @param directTargetChild @{inheritDoc}
     * @param target @{inheritDoc}
     * @param nestedScrollAxes @{inheritDoc}
     * @return true on vertical scrolling
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                                       View directTargetChild, View target, int nestedScrollAxes) {

        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /**
     * when scrolling, animate in or out the floating action button.
     *
     * @param coordinatorLayout @{inheritDoc}
     * @param child @{inheritDoc}
     * @param target @{inheritDoc}
     * @param dxConsumed @{inheritDoc}
     * @param dyConsumed @{inheritDoc}
     * @param dxUnconsumed @{inheritDoc}
     * @param dyUnconsumed @{inheritDoc}
     */
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyConsumed > 0 && !mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child);
        }
    }

    /**
     * Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits.
     *
     * @param button the current button
     */
    private void animateOut(final FloatingActionButton button) {
        Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_out);
        anim.setInterpolator(interpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                ScrollFloatingActionButtonBehavior.this.mIsAnimatingOut = true;
            }

            public void onAnimationEnd(Animation animation) {
                ScrollFloatingActionButtonBehavior.this.mIsAnimatingOut = false;
                button.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        button.startAnimation(anim);
    }

    /**
     * Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout enters.
     *
     * @param button the current button
     */
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_in);
        anim.setInterpolator(interpolator);
        button.startAnimation(anim);
    }
}
