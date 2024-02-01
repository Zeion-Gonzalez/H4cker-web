package com.instabug.survey.p034c;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/* compiled from: KeyboardUtils.java */
/* renamed from: com.instabug.survey.c.b */
/* loaded from: classes.dex */
public class C0774b {
    /* renamed from: a */
    public static void m1989a(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService("input_method");
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    /* renamed from: a */
    public static void m1990a(Activity activity, EditText editText) {
        ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(editText, 1);
    }
}
