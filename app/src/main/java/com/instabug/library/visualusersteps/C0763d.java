package com.instabug.library.visualusersteps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import com.instabug.library.Instabug;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEventSubscriber;
import com.instabug.library.screenshot.C0735a;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.DiskUtils;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.visualusersteps.C0760a;
import com.instabug.library.visualusersteps.C0761b;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import p045rx.functions.Action1;

/* compiled from: VisualUserStepsProvider.java */
/* renamed from: com.instabug.library.visualusersteps.d */
/* loaded from: classes.dex */
public class C0763d {

    /* renamed from: a */
    private static int f1300a = 0;

    /* renamed from: b */
    private static C0763d f1301b;

    /* renamed from: c */
    private C0762c f1302c = new C0762c();

    private C0763d() {
        DiskUtils.getCleanDirectoryObservable(VisualUserStepsHelper.getVisualUserStepsDirectory(Instabug.getApplicationContext())).subscribe(new Action1<List<File>>() { // from class: com.instabug.library.visualusersteps.d.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(List<File> list) {
                if (list != null && !list.isEmpty()) {
                    InstabugSDKLogger.m1804w(C0763d.class, "Can't clean visual user steps directory");
                }
            }
        });
        SDKCoreEventSubscriber.subscribe(new Action1<SDKCoreEvent>() { // from class: com.instabug.library.visualusersteps.d.2
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(SDKCoreEvent sDKCoreEvent) {
                if (sDKCoreEvent.getType().equals(SDKCoreEvent.Session.TYPE_SESSION) && sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_FINISHED)) {
                    C0763d.this.m1893d();
                }
            }
        });
    }

    /* renamed from: a */
    public static C0763d m1884a() {
        if (f1301b == null) {
            f1301b = new C0763d();
        }
        return f1301b;
    }

    /* renamed from: a */
    public void m1896a(String str, String str2, String str3) {
        if (!InstabugCore.isForegroundBusy()) {
            char c = 65535;
            switch (str.hashCode()) {
                case -1853253192:
                    if (str.equals("activity_created")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1784260441:
                    if (str.equals("activity_resumed")) {
                        c = 2;
                        break;
                    }
                    break;
                case -353343821:
                    if (str.equals("fragment_attached")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1761507048:
                    if (str.equals("fragment_resumed")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                    m1890a(str2);
                    m1891b(str, str2, str3);
                    return;
                case 2:
                case 3:
                    if (this.f1302c.m1878b() == null || this.f1302c.m1878b().m1827g()) {
                        m1890a(str2);
                    }
                    if (SettingsManager.getInstance().isReproStepsScreenshotEnabled()) {
                        m1892c();
                    }
                    m1891b(str, str2, str3);
                    return;
                default:
                    m1891b(str, str2, str3);
                    return;
            }
        }
    }

    /* renamed from: a */
    private void m1890a(String str) {
        C0762c c0762c = this.f1302c;
        int i = f1300a + 1;
        f1300a = i;
        c0762c.m1876a(new C0760a(String.valueOf(i), str));
    }

    /* renamed from: b */
    private void m1891b(String str, String str2, String str3) {
        if (this.f1302c.m1878b() == null) {
            m1890a(str2);
        }
        this.f1302c.m1877a(C0761b.m1831a(str).m1863b(str2).m1861a(this.f1302c.m1878b().m1825e()).m1866e(str3).m1862a());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1886a(C0760a.a aVar) {
        if (this.f1302c.m1878b() != null) {
            this.f1302c.m1878b().m1820a(aVar);
        }
    }

    /* renamed from: c */
    private void m1892c() {
        final Activity targetActivity = InstabugInternalTrackingDelegate.getInstance().getTargetActivity();
        new Handler().postDelayed(new Runnable() { // from class: com.instabug.library.visualusersteps.d.3
            @Override // java.lang.Runnable
            public void run() {
                C0735a.m1646a(targetActivity, new C0735a.a() { // from class: com.instabug.library.visualusersteps.d.3.1
                    @Override // com.instabug.library.screenshot.C0735a.a
                    /* renamed from: a */
                    public void mo1234a(Bitmap bitmap) {
                        C0763d.this.m1885a(targetActivity, bitmap);
                    }

                    @Override // com.instabug.library.screenshot.C0735a.a
                    /* renamed from: a */
                    public void mo1235a(Throwable th) {
                        InstabugSDKLogger.m1801e(C0763d.class, "capturing VisualUserStep failed error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
                    }
                });
            }
        }, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1885a(final Activity activity, Bitmap bitmap) {
        BitmapUtils.saveBitmapAsPNG(bitmap, 70, VisualUserStepsHelper.getVisualUserStepsDirectory(activity), "step" + this.f1302c.m1878b().m1825e(), new BitmapUtils.InterfaceC0751a() { // from class: com.instabug.library.visualusersteps.d.4
            @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
            /* renamed from: a */
            public void mo1236a(Uri uri) {
                C0760a.a aVar = new C0760a.a(uri.getLastPathSegment());
                if (activity != null && activity.getResources().getConfiguration().orientation == 2) {
                    aVar.m1829a("landscape");
                } else {
                    aVar.m1829a("portrait");
                }
                C0763d.this.m1886a(aVar);
            }

            @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
            /* renamed from: a */
            public void mo1237a(Throwable th) {
                InstabugSDKLogger.m1801e(C0763d.class, "capturing VisualUserStep failed error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
            }
        });
    }

    /* renamed from: b */
    public ArrayList<C0761b> m1897b() {
        m1893d();
        ArrayList<C0761b> arrayList = new ArrayList<>();
        Iterator<C0760a> it = this.f1302c.m1874a().iterator();
        while (it.hasNext()) {
            C0760a next = it.next();
            C0761b.a m1865d = C0761b.m1831a((String) null).m1863b(next.m1819a()).m1861a((String) null).m1865d(next.m1825e());
            if (next.m1826f() != null) {
                m1865d.m1864c(next.m1826f().m1828a()).m1867f(next.m1826f().m1830b());
            }
            arrayList.add(m1865d.m1862a());
            arrayList.addAll(next.m1822b());
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public void m1893d() {
        if (SettingsManager.getInstance().isReproStepsScreenshotEnabled()) {
            m1894e();
        }
        m1895f();
    }

    /* renamed from: e */
    private void m1894e() {
        if (this.f1302c.m1881e() > 20) {
            this.f1302c.m1875a(this.f1302c.m1881e() - 20);
        }
    }

    /* renamed from: f */
    private void m1895f() {
        while (this.f1302c.m1880d() > 100) {
            this.f1302c.m1879c();
        }
    }
}
