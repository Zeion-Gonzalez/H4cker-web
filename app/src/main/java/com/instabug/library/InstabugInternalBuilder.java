package com.instabug.library;

import android.content.Context;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

/* loaded from: classes.dex */
public class InstabugInternalBuilder {
    public static Instabug buildInstabug(Context context) {
        return new Instabug.Builder(context, "f501f761142981d54b1fdea93963a934", InstabugInvocationEvent.FLOATING_BUTTON).build();
    }
}
