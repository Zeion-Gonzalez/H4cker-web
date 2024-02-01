package com.instabug.survey.p034c;

import android.os.Handler;
import android.os.Looper;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.p033b.C0772c;

/* compiled from: SurveysUtils.java */
/* renamed from: com.instabug.survey.c.e */
/* loaded from: classes.dex */
public class C0777e {
    /* renamed from: a */
    public static boolean m1994a() {
        return InstabugCore.getFeatureState(Feature.SURVEYS) == Feature.State.ENABLED;
    }

    /* renamed from: b */
    public static void m1995b() {
        if (C0772c.m1984b() != null) {
            try {
                new Handler(Looper.getMainLooper()).post(C0772c.m1984b());
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(C0777e.class, "AfterShowingSurveyRunnable has been failed to run.", e);
            }
        }
    }

    /* renamed from: c */
    public static void m1996c() {
        if (C0772c.m1986c() != null) {
            try {
                new Handler(Looper.getMainLooper()).post(C0772c.m1986c());
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(C0777e.class, "AfterShowingSurveyRunnable has been failed to run.", e);
            }
        }
    }
}
