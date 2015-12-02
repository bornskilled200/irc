package android.support.v7.preference;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * A fix for EditTextPreference so that its respects inputType.
 */
@SuppressWarnings("ALL")
public class EditTextPreferenceFix extends EditTextPreference {
    /**
     * edit text fix.
     */
    private EditText editText;

    /**
     * Expected constructor for xml.
     *
     * @param context @{inheritDoc}
     */
    public EditTextPreferenceFix(Context context) {
        this(context, null);
    }

    /**
     * Expected constructor for xml.
     *
     * @param context @{inheritDoc}
     * @param attrs   @{inheritDoc}
     */
    public EditTextPreferenceFix(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.preference.R.attr.editTextPreferenceStyle);
    }

    /**
     * Expected constructor for xml.
     *
     * @param context      @{inheritDoc}
     * @param attrs        @{inheritDoc}
     * @param defStyleAttr @{inheritDoc}
     */
    public EditTextPreferenceFix(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Expected constructor for xml.
     *
     * @param context      @{inheritDoc}
     * @param attrs        @{inheritDoc}
     * @param defStyleAttr @{inheritDoc}
     * @param defStyleRes  @{inheritDoc}
     */
    public EditTextPreferenceFix(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        editText = new AppCompatEditText(context, attrs);
        editText.setId(android.R.id.edit);
    }

    /**
     * @return editText
     */
    public EditText getEditText() {
        return editText;
    }
}
