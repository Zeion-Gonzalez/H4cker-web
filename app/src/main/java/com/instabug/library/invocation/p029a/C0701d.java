package com.instabug.library.invocation.p029a;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import com.instabug.library.core.eventbus.SessionStateEventBus;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.invocation.InterfaceC0697a;
import com.instabug.library.model.Session;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PermissionsUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import p045rx.functions.Action1;

/* compiled from: ScreenshotGestureInvoker.java */
/* renamed from: com.instabug.library.invocation.a.d */
/* loaded from: classes.dex */
public class C0701d implements InterfaceC0698a<Void> {

    /* renamed from: a */
    private boolean f1051a = true;

    /* renamed from: b */
    private List<a> f1052b = new ArrayList();

    public C0701d(InterfaceC0697a interfaceC0697a) {
        this.f1052b.add(new a(m1483a(Environment.DIRECTORY_PICTURES), interfaceC0697a));
        this.f1052b.add(new a(m1483a(Environment.DIRECTORY_DCIM), interfaceC0697a));
        m1488f();
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: a */
    public void mo1407a() {
        if (this.f1051a && !m1486d()) {
            m1487e();
        } else {
            m1485c();
        }
    }

    /* renamed from: c */
    private void m1485c() {
        if (m1486d()) {
            Iterator<a> it = this.f1052b.iterator();
            while (it.hasNext()) {
                it.next().m1496a();
            }
        }
    }

    /* renamed from: d */
    private boolean m1486d() {
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            return PermissionsUtils.isPermissionGranted(currentActivity, "android.permission.WRITE_EXTERNAL_STORAGE");
        }
        return false;
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: b */
    public void mo1408b() {
        Iterator<a> it = this.f1052b.iterator();
        while (it.hasNext()) {
            it.next().m1497b();
        }
    }

    /* renamed from: a */
    private String m1483a(String str) {
        return new File(Environment.getExternalStoragePublicDirectory(str), "Screenshots").getAbsolutePath();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ScreenshotGestureInvoker.java */
    /* renamed from: com.instabug.library.invocation.a.d$a */
    /* loaded from: classes.dex */
    public static class a extends FileObserver {

        /* renamed from: a */
        private String f1054a;

        /* renamed from: b */
        private long f1055b;

        /* renamed from: c */
        private final Handler f1056c;

        /* renamed from: d */
        private Runnable f1057d;

        /* renamed from: e */
        private InterfaceC0697a f1058e;

        /* renamed from: f */
        private String f1059f;

        /* renamed from: g */
        private String f1060g;

        a(String str, InterfaceC0697a interfaceC0697a) {
            super(str);
            this.f1054a = str;
            this.f1056c = new Handler();
            this.f1058e = interfaceC0697a;
        }

        @Override // android.os.FileObserver
        public void onEvent(int i, String str) {
            InstabugSDKLogger.m1802i(this, "Event:" + i + "\t" + str);
            if (str == null) {
                InstabugSDKLogger.m1802i(this, "Ignore this event.");
                return;
            }
            if (i == 256 || i == 32) {
                this.f1059f = str;
            }
            if (i == 2 && this.f1059f.equals(str)) {
                this.f1060g = str;
            }
            if (i == 8 && this.f1060g.equals(this.f1059f) && this.f1060g.equals(str) && m1492a(str)) {
                InstabugSDKLogger.m1799d(this, "Screenshot taken: " + this.f1054a + "/" + str + ", invoking SDK");
                this.f1058e.mo1406a(Uri.fromFile(new File(this.f1054a + "/" + str)));
            }
        }

        /* renamed from: a */
        public void m1496a() {
            if (Build.VERSION.SDK_INT == 23) {
                startWatching();
            } else {
                super.startWatching();
            }
        }

        /* renamed from: b */
        public void m1497b() {
            if (Build.VERSION.SDK_INT == 23) {
                stopWatching();
            } else {
                super.stopWatching();
            }
        }

        @Override // android.os.FileObserver
        public void startWatching() {
            this.f1057d = new Runnable() { // from class: com.instabug.library.invocation.a.d.a.1
                @Override // java.lang.Runnable
                public void run() {
                    File[] listFiles;
                    File file = new File(a.this.f1054a);
                    if (file.listFiles() != null) {
                        InstabugSDKLogger.m1799d(this, "The path is directory: " + file.isDirectory());
                        if (file.isDirectory() && (listFiles = file.listFiles()) != null) {
                            for (File file2 : listFiles) {
                                InstabugSDKLogger.m1799d(this, "File name: " + file2.getName() + "File last modified: " + file2.lastModified());
                                if (a.this.m1492a(file2.getName())) {
                                    InstabugSDKLogger.m1799d(this, "Screenshot taken: " + file2.getPath() + ", invoking SDK");
                                    a.this.f1058e.mo1406a(Uri.fromFile(file2));
                                }
                            }
                        }
                    }
                    a.this.f1056c.postDelayed(a.this.f1057d, 1000L);
                }
            };
            this.f1056c.post(this.f1057d);
        }

        @Override // android.os.FileObserver
        public void stopWatching() {
            this.f1056c.removeCallbacks(this.f1057d);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: a */
        public boolean m1492a(String str) {
            if (!str.toLowerCase().contains("screenshot")) {
                return false;
            }
            File file = new File(this.f1054a + "/" + str);
            if (this.f1055b == file.lastModified()) {
                return false;
            }
            this.f1055b = file.lastModified();
            long currentTimeMillis = System.currentTimeMillis() - this.f1055b;
            InstabugSDKLogger.m1799d(this, "Difference time between file lastUpdate and currentTime: " + currentTimeMillis);
            if (currentTimeMillis >= 1800) {
                return false;
            }
            InstabugSDKLogger.m1799d(this, "Send event to listener.");
            return true;
        }
    }

    /* renamed from: e */
    private void m1487e() {
        Activity currentActivity;
        if (C0704b.m1513c().m1523d() == InstabugInvocationEvent.SCREENSHOT_GESTURE && (currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity()) != null) {
            PermissionsUtils.requestPermission(currentActivity, "android.permission.WRITE_EXTERNAL_STORAGE", 1, (Runnable) null, (Runnable) null);
            this.f1051a = false;
        }
    }

    /* renamed from: f */
    private void m1488f() {
        SessionStateEventBus.getInstance().subscribe(new Action1<Session.SessionState>() { // from class: com.instabug.library.invocation.a.d.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Session.SessionState sessionState) {
                if (sessionState.equals(Session.SessionState.START)) {
                    C0701d.this.f1051a = true;
                }
            }
        });
    }
}
