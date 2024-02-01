package com.instabug.library.invocation;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import com.instabug.library.C0629b;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugState;
import com.instabug.library.core.InitialScreenshotHelper;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.CurrentActivityLifeCycleEventBus;
import com.instabug.library.core.plugin.C0642a;
import com.instabug.library.core.plugin.PluginPromptOption;
import com.instabug.library.invocation.p029a.C0701d;
import com.instabug.library.invocation.p029a.C0702e;
import com.instabug.library.invocation.p029a.C0703f;
import com.instabug.library.invocation.p029a.InterfaceC0698a;
import com.instabug.library.invocation.p029a.ViewOnClickListenerC0699b;
import com.instabug.library.p023c.C0636b;
import com.instabug.library.p031ui.promptoptions.PromptOptionsActivity;
import com.instabug.library.tracking.ActivityLifeCycleEvent;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.Iterator;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: InvocationManager.java */
/* renamed from: com.instabug.library.invocation.b */
/* loaded from: classes.dex */
public class C0704b implements InterfaceC0697a {

    /* renamed from: a */
    private static C0704b f1070a;

    /* renamed from: d */
    private InterfaceC0698a f1073d;

    /* renamed from: e */
    private Subscription f1074e;

    /* renamed from: f */
    private boolean f1075f = true;

    /* renamed from: b */
    private C0705c f1071b = new C0705c();

    /* renamed from: c */
    private InstabugInvocationEvent f1072c = InstabugInvocationEvent.SHAKE;

    private C0704b() {
        m1517n();
    }

    /* renamed from: b */
    public static void m1510b() {
        InstabugSDKLogger.m1803v(C0704b.class, "initializing invocationManager");
        if (f1070a == null) {
            f1070a = new C0704b();
        } else {
            f1070a.m1515l();
        }
    }

    /* renamed from: c */
    public static C0704b m1513c() {
        if (f1070a != null) {
            return f1070a;
        }
        throw new IllegalStateException("calling InvocationManager.getInstance() before calling InvocationManager.init()");
    }

    /* renamed from: a */
    public void m1522a(InstabugInvocationEvent instabugInvocationEvent) {
        this.f1072c = instabugInvocationEvent;
        if (this.f1073d != null) {
            this.f1073d.mo1408b();
        }
        switch (instabugInvocationEvent) {
            case NONE:
                this.f1073d = null;
                break;
            case SHAKE:
                this.f1073d = new C0702e(Instabug.getApplicationContext(), this);
                ((C0702e) this.f1073d).m1498a(this.f1071b.m1540d());
                break;
            case FLOATING_BUTTON:
                this.f1073d = new ViewOnClickListenerC0699b(this);
                break;
            case TWO_FINGER_SWIPE_LEFT:
                this.f1073d = new C0703f(Instabug.getApplicationContext(), this);
                break;
            case SCREENSHOT_GESTURE:
                this.f1073d = new C0701d(this);
                break;
        }
        if (this.f1073d != null) {
            this.f1073d.mo1407a();
        }
    }

    /* renamed from: d */
    public InstabugInvocationEvent m1523d() {
        return this.f1072c;
    }

    /* renamed from: e */
    public C0705c m1524e() {
        return this.f1071b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: f */
    public InterfaceC0698a m1525f() {
        return this.f1073d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: l */
    public void m1515l() {
        if (Instabug.getState().equals(InstabugState.ENABLED) && this.f1075f && m1519p() && this.f1073d != null) {
            this.f1073d.mo1407a();
        }
    }

    /* renamed from: a */
    public void m1521a(MotionEvent motionEvent) {
        if (Instabug.getState().equals(InstabugState.ENABLED) && !InstabugCore.isForegroundBusy() && (this.f1073d instanceof C0703f)) {
            ((C0703f) this.f1073d).m1505a(motionEvent);
        }
    }

    /* renamed from: g */
    public void m1526g() {
        if (this.f1073d != null) {
            this.f1073d.mo1408b();
        }
    }

    /* renamed from: h */
    public void m1527h() {
        this.f1075f = false;
    }

    /* renamed from: i */
    public void m1528i() {
        this.f1075f = true;
    }

    /* renamed from: j */
    public void m1529j() {
        if (!InstabugCore.isForegroundBusy()) {
            m1520a(this.f1071b.m1532a());
        }
    }

    /* renamed from: a */
    public void m1520a(int i) {
        if (m1518o()) {
            switch (i) {
                case 0:
                case 3:
                case 4:
                    m1506a(i, (Uri) null);
                    return;
                case 1:
                case 2:
                    m1511b(i);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1506a(int i, @Nullable Uri uri) {
        switch (i) {
            case 0:
                m1512b(uri);
                return;
            case 1:
            case 2:
            case 4:
                PluginPromptOption promptOptionByInvocationMode = PluginPromptOption.getPromptOptionByInvocationMode(i);
                if (promptOptionByInvocationMode != null) {
                    promptOptionByInvocationMode.invoke(uri);
                    return;
                } else {
                    InstabugSDKLogger.m1804w(C0704b.class, "'" + i + "' invocationMode isn't available");
                    return;
                }
            case 3:
                C0636b.m1215b();
                return;
            default:
                return;
        }
    }

    /* renamed from: b */
    private void m1512b(Uri uri) {
        ArrayList<PluginPromptOption> m1530k = m1530k();
        if (m1530k.size() >= 2) {
            if (uri == null) {
                m1516m();
                return;
            } else {
                m1514c(uri);
                return;
            }
        }
        if (m1530k.size() == 1) {
            PluginPromptOption pluginPromptOption = m1530k.get(0);
            int invocationMode = pluginPromptOption.getInvocationMode();
            switch (invocationMode) {
                case 1:
                case 2:
                    if (uri == null) {
                        m1511b(invocationMode);
                        return;
                    } else {
                        m1506a(invocationMode, uri);
                        return;
                    }
                case 3:
                default:
                    return;
                case 4:
                    pluginPromptOption.invoke();
                    return;
            }
        }
        InstabugSDKLogger.m1804w(C0704b.class, "No plugins prompt options available");
    }

    /* renamed from: k */
    public ArrayList<PluginPromptOption> m1530k() {
        ArrayList<PluginPromptOption> arrayList = new ArrayList<>();
        Iterator<PluginPromptOption> it = C0642a.m1245c().iterator();
        while (it.hasNext()) {
            PluginPromptOption next = it.next();
            if (this.f1071b.m1543e()[next.getInvocationMode()]) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    /* renamed from: m */
    private void m1516m() {
        InitialScreenshotHelper.captureScreenshot(new InitialScreenshotHelper.InitialScreenshotCapturingListener() { // from class: com.instabug.library.invocation.b.1
            @Override // com.instabug.library.core.InitialScreenshotHelper.InitialScreenshotCapturingListener
            public void onScreenshotCapturedSuccessfully(Uri uri) {
                C0704b.this.m1514c(uri);
            }

            @Override // com.instabug.library.core.InitialScreenshotHelper.InitialScreenshotCapturingListener
            public void onScreenshotCapturingFailed(Throwable th) {
                C0704b.this.m1514c(null);
            }
        });
    }

    /* renamed from: b */
    private void m1511b(final int i) {
        InitialScreenshotHelper.captureScreenshot(new InitialScreenshotHelper.InitialScreenshotCapturingListener() { // from class: com.instabug.library.invocation.b.2
            @Override // com.instabug.library.core.InitialScreenshotHelper.InitialScreenshotCapturingListener
            public void onScreenshotCapturedSuccessfully(Uri uri) {
                C0704b.this.m1506a(i, uri);
            }

            @Override // com.instabug.library.core.InitialScreenshotHelper.InitialScreenshotCapturingListener
            public void onScreenshotCapturingFailed(Throwable th) {
                C0704b.this.m1506a(i, (Uri) null);
            }
        });
    }

    @Override // com.instabug.library.invocation.InterfaceC0697a
    /* renamed from: a */
    public void mo1406a(Uri uri) {
        m1506a(this.f1071b.m1532a(), uri);
    }

    @Override // com.instabug.library.invocation.InterfaceC0697a
    /* renamed from: a */
    public void mo1405a() {
        m1529j();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m1514c(Uri uri) {
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(PromptOptionsActivity.m1757a(currentActivity, uri));
        }
    }

    /* renamed from: n */
    private void m1517n() {
        this.f1074e = CurrentActivityLifeCycleEventBus.getInstance().subscribe(new Action1<ActivityLifeCycleEvent>() { // from class: com.instabug.library.invocation.b.3
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                switch (AnonymousClass4.f1081b[activityLifeCycleEvent.ordinal()]) {
                    case 1:
                        InstabugSDKLogger.m1803v(this, "current activity resumed");
                        C0704b.this.m1515l();
                        return;
                    case 2:
                        InstabugSDKLogger.m1803v(this, "current activity paused");
                        C0704b.this.m1526g();
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: InvocationManager.java */
    /* renamed from: com.instabug.library.invocation.b$4  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass4 {

        /* renamed from: b */
        static final /* synthetic */ int[] f1081b = new int[ActivityLifeCycleEvent.values().length];

        static {
            try {
                f1081b[ActivityLifeCycleEvent.RESUMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f1081b[ActivityLifeCycleEvent.PAUSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            f1080a = new int[InstabugInvocationEvent.values().length];
            try {
                f1080a[InstabugInvocationEvent.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f1080a[InstabugInvocationEvent.SHAKE.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f1080a[InstabugInvocationEvent.FLOATING_BUTTON.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f1080a[InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f1080a[InstabugInvocationEvent.SCREENSHOT_GESTURE.ordinal()] = 5;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    /* renamed from: o */
    private boolean m1518o() {
        return C0629b.m1160a().m1170b(Feature.INSTABUG) == Feature.State.ENABLED && C0629b.m1160a().m1169a(Feature.INSTABUG);
    }

    /* renamed from: p */
    private boolean m1519p() {
        return m1530k().size() > 0;
    }
}
