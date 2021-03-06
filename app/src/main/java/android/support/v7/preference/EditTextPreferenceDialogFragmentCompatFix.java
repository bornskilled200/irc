/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * MODIFICATIONS:
 * - getEditTextPreference() returns EditTextPreferenceFix instead of EditTextPreference
 * - onBindDialogView(View view) retrieves the EditText from EditTextPreferenceFix
 */
package android.support.v7.preference;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

/**
 * A fix for EditTextPreference so that its respects inputType.
 */
@SuppressWarnings("WeakerAccess")
public class EditTextPreferenceDialogFragmentCompatFix extends PreferenceDialogFragmentCompat {
    /**
     * unknown, maybe for layout.
     */
    public static final int MAGIC_NUMBER_1 = -1;
    /**
     * unknown, maybe for layout.
     */
    public static final int MAGIC_NUMBER_2 = -2;

    /**
     * fix for edit Text.
     */
    private EditText mEditText;

    /**
     * Simple constructor.
     */
    public EditTextPreferenceDialogFragmentCompatFix() {
    }

    /**
     * Convenience method to create this instance.
     * @param key unknown
     * @return an instance of EditTextPreferenceDialogFragmentCompatFix
     */
    public static EditTextPreferenceDialogFragmentCompatFix newInstance(String key) {
        EditTextPreferenceDialogFragmentCompatFix fragment = new EditTextPreferenceDialogFragmentCompatFix();
        Bundle b = new Bundle(1);
        b.putString("key", key);
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * @{inheritDoc}
     * @param view @{inheritDoc}
     */
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        this.mEditText = getEditTextPreference().getEditText();
        this.mEditText.setText(this.getEditTextPreference().getText());

        Editable text = mEditText.getText();
        if (text != null) {
            mEditText.setSelection(text.length(), text.length());
        }

        ViewParent oldParent = this.mEditText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(this.mEditText);
            }

            this.onAddEditTextToDialogView(view, this.mEditText);
        }
    }

    /**
     * what this does is unknown to me.
     * @return unknown
     */
    private EditTextPreferenceFix getEditTextPreference() {
        return (EditTextPreferenceFix) this.getPreference();
    }

    /**
     * what this does is unknown to me.
     * @return unknown
     */
    protected boolean needInputMethod() {
        return true;
    }

    /**
     * what this does is unknown to me.
     * @param dialogView unknown
     * @param editText unknown
     */
    protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
        View oldEditText = dialogView.findViewById(android.R.id.edit);
        if (oldEditText != null) {
            ViewGroup container = (ViewGroup) (oldEditText.getParent());
            if (container != null) {
                container.removeView(oldEditText);
                container.addView(editText, MAGIC_NUMBER_1, MAGIC_NUMBER_2);
            }
        }
    }

    /**
     * what this does is unknown to me.
     * @param positiveResult unknown
     */
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = this.mEditText.getText().toString();
            if (this.getEditTextPreference().callChangeListener(value)) {
                this.getEditTextPreference().setText(value);
            }
        }

    }
}
