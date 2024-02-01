package com.instabug.library.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;

/* loaded from: classes.dex */
public class ViewUtils {
    public static int convertDpToPx(@NonNull Context context, float f) {
        return (int) TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }
}
