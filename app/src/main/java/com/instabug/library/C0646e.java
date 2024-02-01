package com.instabug.library;

import android.content.IntentFilter;
import com.instabug.library.Feature;
import com.instabug.library.core.eventbus.CurrentActivityLifeCycleEventBus;
import com.instabug.library.core.eventbus.SessionStateEventBus;
import com.instabug.library.core.eventbus.coreeventbus.C0640a;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.internal.storage.cache.SessionsCacheManager;
import com.instabug.library.internal.storage.cache.UserAttributesCacheManager;
import com.instabug.library.logging.InstabugUserEventLogger;
import com.instabug.library.model.C0717b;
import com.instabug.library.model.Session;
import com.instabug.library.network.C0727b;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.tracking.ActivityLifeCycleEvent;
import com.instabug.library.user.UserEvent;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: SessionManager.java */
/* renamed from: com.instabug.library.e */
/* loaded from: classes.dex */
public class C0646e {

    /* renamed from: a */
    private static C0646e f813a;

    /* renamed from: b */
    private SettingsManager f814b;

    /* renamed from: c */
    private int f815c;

    /* renamed from: d */
    private C0727b f816d;

    /* renamed from: e */
    private Subscription f817e;

    private C0646e(SettingsManager settingsManager) {
        this.f814b = settingsManager;
        m1265p();
        this.f816d = new C0727b();
    }

    /* renamed from: a */
    public static C0646e m1249a() {
        return f813a;
    }

    /* renamed from: a */
    public static void m1252a(SettingsManager settingsManager) {
        if (f813a == null) {
            f813a = new C0646e(settingsManager);
        }
    }

    /* renamed from: b */
    public long m1266b() {
        if (this.f814b.getSessionStartedAt() == 0) {
            return 0L;
        }
        return (System.currentTimeMillis() / 1000) - this.f814b.getSessionStartedAt();
    }

    /* renamed from: c */
    public void m1267c() {
        if (C0629b.m1160a().m1170b(Feature.INSTABUG) == Feature.State.ENABLED) {
            m1257h();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public void m1254e() {
        if (C0629b.m1160a().m1170b(Feature.INSTABUG) == Feature.State.ENABLED && this.f815c == 0) {
            m1255f();
        }
        this.f815c++;
    }

    /* renamed from: f */
    private void m1255f() {
        m1263n();
        m1251a(Session.SessionState.START);
        if (this.f816d != null) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            if (!this.f816d.m1637a()) {
                this.f816d.m1636a(Instabug.getApplicationContext(), intentFilter);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: g */
    public void m1256g() {
        C0629b.m1160a().m1166a(Instabug.getApplicationContext());
        if (C0629b.m1160a().m1170b(Feature.INSTABUG) == Feature.State.ENABLED && this.f815c == 1) {
            m1257h();
        }
        this.f815c--;
    }

    /* renamed from: h */
    private void m1257h() {
        if (this.f814b.getSessionStartedAt() != 0) {
            m1258i();
            m1264o();
            m1251a(Session.SessionState.FINISH);
        } else {
            InstabugSDKLogger.m1799d(this, "Instabug is enabled after session started, Session ignored");
        }
        if (this.f816d != null) {
            try {
                this.f816d.m1635a(Instabug.getApplicationContext());
            } catch (IllegalArgumentException e) {
                InstabugSDKLogger.m1799d(this, "This app is not registered");
            }
        }
    }

    /* renamed from: i */
    private void m1258i() {
        SessionsCacheManager.addSession(m1259j());
        SessionsCacheManager.saveCacheToDisk();
    }

    /* renamed from: j */
    private Session m1259j() {
        return new Session(m1260k(), this.f814b.getSessionStartedAt(), (System.currentTimeMillis() / 1000) - this.f814b.getSessionStartedAt(), m1261l(), m1262m());
    }

    /* renamed from: k */
    private int m1260k() {
        if (!SettingsManager.getInstance().isFirstDismiss()) {
            return -1;
        }
        return 1;
    }

    /* renamed from: l */
    private String m1261l() {
        HashMap<String, String> all = UserAttributesCacheManager.getAll();
        if (all == null || all.size() == 0) {
            return "{}";
        }
        C0717b c0717b = new C0717b();
        c0717b.m1594a(all);
        try {
            return c0717b.toJson();
        } catch (JSONException e) {
            InstabugSDKLogger.m1801e(this, "parsing user attributes got error: " + e.getMessage(), e);
            return "{}";
        }
    }

    /* renamed from: m */
    private String m1262m() {
        String str;
        JSONException e;
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(InstabugUserEventLogger.getInstance().getUserEvents());
            str = UserEvent.toJson(arrayList).toString();
        } catch (JSONException e2) {
            str = "[]";
            e = e2;
        }
        try {
            InstabugUserEventLogger.getInstance().clearAll();
        } catch (JSONException e3) {
            e = e3;
            InstabugSDKLogger.m1801e(this, "parsing user events got error: " + e.getMessage(), e);
            return str;
        }
        return str;
    }

    /* renamed from: n */
    private void m1263n() {
        this.f814b.setSessionStartedAt(System.currentTimeMillis() / 1000);
        if (SettingsManager.getInstance().isFirstRun()) {
            this.f814b.setIsFirstRun(false);
        }
        if (SettingsManager.getInstance().getFirstRunAt().getTime() == 0) {
            this.f814b.setFirstRunAt(System.currentTimeMillis());
        }
        this.f814b.incrementSessionsCount();
    }

    /* renamed from: o */
    private void m1264o() {
        if (SettingsManager.getInstance().isFirstDismiss()) {
            SettingsManager.getInstance().setIsFirstDismiss(false);
        }
    }

    /* renamed from: a */
    private void m1251a(Session.SessionState sessionState) {
        if (sessionState.equals(Session.SessionState.FINISH)) {
            SettingsManager.getInstance().setIsAppOnForeground(false);
            C0640a.m1238a(new SDKCoreEvent(SDKCoreEvent.Session.TYPE_SESSION, SDKCoreEvent.Session.VALUE_FINISHED));
        } else {
            SettingsManager.getInstance().setIsAppOnForeground(true);
            C0640a.m1238a(new SDKCoreEvent(SDKCoreEvent.Session.TYPE_SESSION, SDKCoreEvent.Session.VALUE_STARTED));
        }
        SessionStateEventBus.getInstance().post(sessionState);
    }

    /* renamed from: p */
    private void m1265p() {
        this.f817e = CurrentActivityLifeCycleEventBus.getInstance().subscribe(new Action1<ActivityLifeCycleEvent>() { // from class: com.instabug.library.e.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                switch (AnonymousClass2.f819a[activityLifeCycleEvent.ordinal()]) {
                    case 1:
                        C0646e.this.m1254e();
                        return;
                    case 2:
                        C0646e.this.m1256g();
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: SessionManager.java */
    /* renamed from: com.instabug.library.e$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass2 {

        /* renamed from: a */
        static final /* synthetic */ int[] f819a = new int[ActivityLifeCycleEvent.values().length];

        static {
            try {
                f819a[ActivityLifeCycleEvent.STARTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f819a[ActivityLifeCycleEvent.STOPPED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* renamed from: d */
    public int m1268d() {
        return this.f815c;
    }
}
