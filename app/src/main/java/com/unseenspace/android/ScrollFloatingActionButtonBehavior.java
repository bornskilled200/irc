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

public class ScrollFloatingActionButtonBehavior extends FloatingActionButton.Behavior {
    private final Interpolator interpolator;
    private boolean mIsAnimatingOut = false;

    public ScrollFloatingActionButtonBehavior(Context context, AttributeSet attrs) {
        super();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in);
        else
            interpolator = new FastOutSlowInInterpolator();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes) ||
                nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child);
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
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

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_in);
        anim.setInterpolator(interpolator);
        button.startAnimation(anim);
    }
}