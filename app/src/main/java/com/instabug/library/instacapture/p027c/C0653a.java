package com.instabug.library.instacapture.p027c;

import android.support.annotation.NonNull;
import android.util.Log;

/* compiled from: Logger.java */
/* renamed from: com.instabug.library.instacapture.c.a */
/* loaded from: classes.dex */
public final class C0653a {

    /* renamed from: a */
    private static boolean f826a;

    /* renamed from: a */
    public static void m1282a(@NonNull CharSequence charSequence) {
        if (f826a) {
            Log.d("InstaCapture", charSequence.toString());
        }
    }

    /* renamed from: b */
    public static void m1284b(@NonNull CharSequence charSequence) {
        if (f826a) {
            Log.e("InstaCapture", charSequence.toString());
        }
    }

    /* renamed from: a */
    public static void m1283a(@NonNull Throwable th) {
        if (f826a) {
            Log.e("InstaCapture", "Logging caught exception", th);
        }
    }
}
