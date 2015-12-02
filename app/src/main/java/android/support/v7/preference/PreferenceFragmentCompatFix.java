package android.support.v7.preference;

import android.support.v4.app.DialogFragment;

/**
 * Make it so that InputType is respected.
 */
public abstract class PreferenceFragmentCompatFix extends PreferenceFragmentCompat {
    /**
     * A tag for this class to find a preference dialog.
     */
    private static final String FRAGMENT_DIALOG_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    /**
     * Make it so that InputType is respected.
     * @param preference @{inheritDoc}
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (this.getFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG) == null) {
            Object f = null;

            if (preference instanceof EditTextPreferenceFix) {
                f = EditTextPreferenceDialogFragmentCompatFix.newInstance(preference.getKey());
            } else {
                super.onDisplayPreferenceDialog(preference);
            }

            if (f != null) {
                ((DialogFragment) f).setTargetFragment(this, 0);
                ((DialogFragment) f).show(this.getFragmentManager(), FRAGMENT_DIALOG_TAG);
            }
        }
    }
}
