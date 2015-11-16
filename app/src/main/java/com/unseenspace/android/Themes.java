package com.unseenspace.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * utility class for getting attributes from the current theme
 *
 * Created by madsk_000 on 11/9/2015.
 */
@SuppressWarnings("SameParameterValue")
public class Themes {
    private Themes(){}

    /**
     *
     * @param context from activity (this) or fragment (getContext())
     * @param attr the attribute you want to get
     * @return the drawable that the attr points to
     */
    public static Drawable getDrawable(Context context, int attr) {
        int[] attrs = new int[]{attr /* index 0 */};

        // Obtain the styled attributes. 'themedContext' is a context with a
        // theme, typically the current Activity (i.e. 'this')
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs);

        // To get the value of the 'listItemBackground' attribute that was
        // set in the theme used in 'themedContext'. The parameter is the index
        // of the attribute in the 'attrs' array. The returned Drawable
        // is what you are after
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);

        // Finally, free the resources used by TypedArray
        ta.recycle();

        return drawableFromTheme;
    }

    public static int getColor(Context context, int attr) {
        int[] attrs = new int[]{attr /* index 0 */};

        // Obtain the styled attributes. 'themedContext' is a context with a
        // theme, typically the current Activity (i.e. 'this')
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs);

        // To get the value of the 'listItemBackground' attribute that was
        // set in the theme used in 'themedContext'. The parameter is the index
        // of the attribute in the 'attrs' array. The returned Drawable
        // is what you are after
        int color = ta.getColor(0 /* index */, Color.BLACK);

        // Finally, free the resources used by TypedArray
        ta.recycle();

        return color;
    }
}
