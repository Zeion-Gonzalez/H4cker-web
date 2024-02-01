package com.instabug.chat;

import android.content.Context;
import android.content.Intent;
import com.instabug.chat.cache.C0518a;
import com.instabug.chat.network.InstabugMessageUploaderService;
import com.instabug.chat.p009d.C0521c;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;

/* compiled from: CoreEventsHandler.java */
/* renamed from: com.instabug.chat.b */
/* loaded from: classes.dex */
class C0511b {
    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m509a(Context context, SDKCoreEvent sDKCoreEvent) {
        if (m517f()) {
            String type = sDKCoreEvent.getType();
            char c = 65535;
            switch (type.hashCode()) {
                case 3599307:
                    if (type.equals(SDKCoreEvent.User.TYPE_USER)) {
                        c = 2;
                        break;
                    }
                    break;
                case 1738700944:
                    if (type.equals(SDKCoreEvent.Invocation.TYPE_INVOCATION)) {
                        c = 0;
                        break;
                    }
                    break;
                case 1843485230:
                    if (type.equals(SDKCoreEvent.Network.TYPE_NETWORK)) {
                        c = 3;
                        break;
                    }
                    break;
                case 1984987798:
                    if (type.equals(SDKCoreEvent.Session.TYPE_SESSION)) {
                        c = 1;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Invocation.VALUE_INVOKED)) {
                        m512c();
                    }
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Invocation.VALUE_DISMISSED)) {
                        m514d();
                        return;
                    }
                    return;
                case 1:
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_STARTED)) {
                        m508a(context);
                    }
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_FINISHED)) {
                        m511b(context);
                        return;
                    }
                    return;
                case 2:
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.User.VALUE_LOGGED_IN)) {
                        m507a();
                    }
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.User.VALUE_LOGGED_OUT)) {
                        m510b();
                        return;
                    }
                    return;
                case 3:
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Network.VALUE_ACTIVATED)) {
                        m513c(context);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: a */
    private static void m508a(Context context) {
        C0518a.m547a(context);
        m515d(context);
        C0521c.m571a().m593b();
    }

    /* renamed from: b */
    private static void m511b(Context context) {
        C0518a.m548b();
        m515d(context);
        C0521c.m571a().m594c();
    }

    /* renamed from: a */
    private static void m507a() {
        C0521c.m571a().m593b();
    }

    /* renamed from: b */
    private static void m510b() {
        m516e();
    }

    /* renamed from: c */
    private static void m513c(Context context) {
        C0518a.m548b();
        m515d(context);
        C0521c.m571a().m593b();
    }

    /* renamed from: c */
    private static void m512c() {
    }

    /* renamed from: d */
    private static void m514d() {
    }

    /* renamed from: d */
    private static void m515d(Context context) {
        context.startService(new Intent(context, InstabugMessageUploaderService.class));
    }

    /* renamed from: e */
    private static void m516e() {
        C0537a.m723a(0L);
    }

    /* renamed from: f */
    private static boolean m517f() {
        return InstabugCore.getFeatureState(Feature.IN_APP_MESSAGING) == Feature.State.ENABLED;
    }
}
