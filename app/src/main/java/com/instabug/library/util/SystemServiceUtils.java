package com.instabug.library.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/* loaded from: classes.dex */
public class SystemServiceUtils {
    private SystemServiceUtils() {
    }

    public static void hideInputMethod(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}
