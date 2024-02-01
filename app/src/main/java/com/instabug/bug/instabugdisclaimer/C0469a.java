package com.instabug.bug.instabugdisclaimer;

import com.instabug.bug.settings.C0482a;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.core.InstabugCore;

/* compiled from: InstabugDisclaimer.java */
/* renamed from: com.instabug.bug.instabugdisclaimer.a */
/* loaded from: classes.dex */
class C0469a {

    /* renamed from: a */
    private static String f75a = "Disclaimer: Once submitted, this feedback and [metadata](#metadata) will be sent to and stored on Instabug's servers.<P/><P/>[Learn more](http://grasshopper.codes).";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m110a() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        InstabugCore.setFeatureState(Feature.CONSOLE_LOGS, Feature.State.DISABLED);
        InstabugCore.setFeatureState(Feature.TRACK_USER_STEPS, Feature.State.DISABLED);
        InstabugCore.setFeatureState(Feature.REPRO_STEPS, Feature.State.DISABLED);
        InstabugCore.setFeatureState(Feature.VIEW_HIERARCHY, Feature.State.DISABLED);
        Instabug.setPromptOptionsEnabled(false, true, true);
        m111a(f75a);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m111a(String str) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("disclaimer").setType(CharSequence.class));
        if (InstabugCore.getFeatureState(Feature.DISCLAIMER) == Feature.State.ENABLED) {
            C0482a.m236a().m252c(str);
        }
    }
}
