package com.instabug.bug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.instabug.bug.cache.C0467a;
import com.instabug.bug.network.InstabugBugsUploaderService;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.view.actionList.service.ReportCategoriesService;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: CoreEventsHandler.java */
/* renamed from: com.instabug.bug.c */
/* loaded from: classes.dex */
public class C0464c {
    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m74a(Context context, SDKCoreEvent sDKCoreEvent) {
        InstabugSDKLogger.m1803v(C0464c.class, "receive new core event: " + sDKCoreEvent.toString());
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
                    m77c();
                }
                if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Invocation.VALUE_DISMISSED)) {
                    m72a();
                    return;
                }
                return;
            case 1:
                if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_STARTED)) {
                    m76b(context);
                    ReportCategoriesService.m355a(context);
                    return;
                } else {
                    if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_FINISHED)) {
                        m75b();
                        return;
                    }
                    return;
                }
            case 2:
                if (sDKCoreEvent.getValue().equals(SDKCoreEvent.User.VALUE_LOGGED_OUT)) {
                    m79e();
                    return;
                }
                return;
            case 3:
                if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Network.VALUE_ACTIVATED)) {
                    m73a(context);
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* renamed from: b */
    private static void m75b() {
        C0467a.m84a();
    }

    /* renamed from: a */
    private static void m73a(Context context) {
        m76b(context);
    }

    /* renamed from: b */
    private static void m76b(Context context) {
        if (context != null) {
            context.startService(new Intent(context, InstabugBugsUploaderService.class));
        }
    }

    /* renamed from: c */
    private static void m77c() {
    }

    /* renamed from: a */
    public static void m72a() {
        InstabugSDKLogger.m1799d(C0464c.class, "SDK dismissed Handle sdk dismissing");
        m81g();
        m78d();
        Activity targetActivity = InstabugInternalTrackingDelegate.getInstance().getTargetActivity();
        if (targetActivity != null) {
            targetActivity.startActivity(C0461b.m54d(InstabugInternalTrackingDelegate.getInstance().getCurrentActivity()));
        }
    }

    /* renamed from: d */
    private static void m78d() {
        C0468d.m86a().m101c();
    }

    /* renamed from: e */
    private static void m79e() {
        m80f();
    }

    /* renamed from: f */
    private static void m80f() {
        C0482a.m236a().m238a(0L);
    }

    /* renamed from: g */
    private static void m81g() {
        if (C0482a.m236a().m260h() != null) {
            C0482a.m236a().m260h().onSdkDismissed(C0468d.m86a().m104e(), C0468d.m86a().m103d().m126c());
        }
    }
}
