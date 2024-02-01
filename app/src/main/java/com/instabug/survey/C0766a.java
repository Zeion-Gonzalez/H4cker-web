package com.instabug.survey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugState;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.internal.device.InstabugDeviceProperties;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.cache.SurveysCacheManager;
import com.instabug.survey.network.C0781a;
import com.instabug.survey.p032a.C0769c;
import com.instabug.survey.p033b.C0772c;
import com.instabug.survey.p034c.C0777e;
import com.instabug.survey.p034c.C0778f;
import com.instabug.survey.p036ui.SurveyActivity;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

/* compiled from: SurveysManager.java */
/* renamed from: com.instabug.survey.a */
/* loaded from: classes.dex */
public class C0766a implements C0781a.a {

    /* renamed from: a */
    private static C0766a f1316a;

    /* renamed from: b */
    private WeakReference<Context> f1317b;

    /* renamed from: d */
    private C0778f f1319d;

    /* renamed from: e */
    private boolean f1320e = false;

    /* renamed from: c */
    private C0781a f1318c = new C0781a(this);

    private C0766a(Context context) {
        this.f1317b = new WeakReference<>(context);
        this.f1319d = new C0778f(InstabugDeviceProperties.getAppVersion(context), InstabugDeviceProperties.getAppVersionName(context));
    }

    /* renamed from: b */
    private static void m1904b(Context context) {
        f1316a = new C0766a(context);
    }

    /* renamed from: a */
    public static C0766a m1901a(Context context) {
        if (f1316a == null) {
            m1904b(context);
        }
        return f1316a;
    }

    /* renamed from: a */
    public void m1911a() {
        if (this.f1317b.get() != null) {
            try {
                this.f1318c.m2026a(this.f1317b.get());
            } catch (IOException | JSONException e) {
                InstabugSDKLogger.m1801e(C0781a.class.getAnnotations(), e.getMessage(), e);
            }
        }
    }

    /* renamed from: b */
    public boolean m1916b() {
        C0769c m1909d;
        if (!Instabug.isEnabled()) {
            InstabugSDKLogger.m1799d(C0766a.class, "Instabug SDK is disabled.");
            return false;
        }
        try {
            if (!Instabug.getState().equals(InstabugState.ENABLED) || !C0777e.m1994a() || !Instabug.isAppOnForeground() || (m1909d = m1909d()) == null) {
                return false;
            }
            m1910d(m1909d);
            return true;
        } catch (ParseException e) {
            InstabugSDKLogger.m1801e(C0781a.class.getAnnotations(), e.getMessage(), e);
            return false;
        }
    }

    /* renamed from: a */
    public boolean m1915a(String str) {
        C0769c m1906c;
        if (!Instabug.getState().equals(InstabugState.ENABLED) || !C0777e.m1994a() || !Instabug.isAppOnForeground() || (m1906c = m1906c(str)) == null || m1906c.m1960g() || m1906c.m1965l()) {
            return false;
        }
        m1910d(m1906c);
        return true;
    }

    @Nullable
    /* renamed from: c */
    private C0769c m1906c(String str) {
        for (C0769c c0769c : SurveysCacheManager.getSurveys()) {
            if (c0769c.m1964k() != null && c0769c.m1964k().equals(str)) {
                InstabugSDKLogger.m1802i(this, "Showing survey With token " + str);
                return c0769c;
            }
        }
        InstabugSDKLogger.m1802i(this, "No Survey With token " + str);
        return null;
    }

    /* renamed from: c */
    public boolean m1918c() {
        try {
            if (Instabug.getState().equals(InstabugState.ENABLED) && C0777e.m1994a()) {
                return this.f1319d.m2019c();
            }
            return false;
        } catch (ParseException e) {
            InstabugSDKLogger.m1801e(C0781a.class.getAnnotations(), e.getMessage(), e);
            return false;
        }
    }

    @Override // com.instabug.survey.network.C0781a.a
    /* renamed from: a */
    public void mo1913a(List<C0769c> list) {
        C0769c m1909d;
        m1907c(m1903b(list));
        if (!Instabug.isEnabled()) {
            InstabugSDKLogger.m1799d(C0766a.class, "Instabug SDK is disabled.");
            return;
        }
        try {
            Thread.sleep(10000L);
            if (C0772c.m1983a() && Instabug.isAppOnForeground() && (m1909d = m1909d()) != null) {
                m1910d(m1909d);
            }
        } catch (InterruptedException | ParseException e) {
            InstabugSDKLogger.m1801e(C0781a.class.getAnnotations(), e.getMessage(), e);
        }
    }

    @Override // com.instabug.survey.network.C0781a.a
    /* renamed from: a */
    public void mo1912a(Throwable th) {
        InstabugSDKLogger.m1801e(C0781a.class.getAnnotations(), th.getMessage(), th);
    }

    /* renamed from: b */
    private List<C0769c> m1903b(List<C0769c> list) {
        ArrayList arrayList = new ArrayList();
        for (C0769c c0769c : list) {
            if (m1902a(c0769c) && (m1905b(c0769c) || m1908c(c0769c))) {
                InMemoryCache<Long, C0769c> cache = SurveysCacheManager.getCache();
                if (cache != null) {
                    arrayList.add(cache.get(Long.valueOf(c0769c.m1943a())));
                }
            } else {
                arrayList.add(c0769c);
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    private boolean m1902a(C0769c c0769c) {
        InMemoryCache<Long, C0769c> cache = SurveysCacheManager.getCache();
        return (cache == null || cache.get(Long.valueOf(c0769c.m1943a())) == null) ? false : true;
    }

    /* renamed from: b */
    private boolean m1905b(C0769c c0769c) {
        InMemoryCache<Long, C0769c> cache = SurveysCacheManager.getCache();
        return cache != null && cache.get(Long.valueOf(c0769c.m1943a())).m1960g();
    }

    /* renamed from: c */
    private boolean m1908c(C0769c c0769c) {
        InMemoryCache<Long, C0769c> cache = SurveysCacheManager.getCache();
        return cache != null && cache.get(Long.valueOf(c0769c.m1943a())).m1965l();
    }

    /* renamed from: c */
    private void m1907c(List<C0769c> list) {
        InMemoryCache<Long, C0769c> cache = SurveysCacheManager.getCache();
        if (cache != null) {
            cache.invalidate();
        }
        SurveysCacheManager.addSurveys(list);
    }

    /* renamed from: d */
    private C0769c m1909d() throws ParseException {
        return this.f1319d.m2007a();
    }

    @VisibleForTesting
    /* renamed from: d */
    private void m1910d(C0769c c0769c) {
        if (!this.f1320e && !InstabugCore.isForegroundBusy() && Instabug.isEnabled()) {
            C0777e.m1995b();
            Activity targetActivity = InstabugInternalTrackingDelegate.getInstance().getTargetActivity();
            if (targetActivity != null) {
                Intent intent = new Intent(targetActivity, SurveyActivity.class);
                intent.putExtra("survey", c0769c);
                targetActivity.startActivity(intent);
            }
        }
    }

    /* renamed from: a */
    public void m1914a(boolean z) {
        this.f1320e = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public boolean m1917b(String str) {
        C0769c m1906c = m1906c(str);
        if (m1906c != null) {
            return m1906c.m1960g();
        }
        InstabugSDKLogger.m1800e(this, "No survey with token=" + str + " was found.");
        return false;
    }
}
