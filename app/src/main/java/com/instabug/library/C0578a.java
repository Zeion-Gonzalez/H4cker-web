package com.instabug.library;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.broadcast.C0633a;
import com.instabug.library.core.eventbus.AutoScreenRecordingEventBus;
import com.instabug.library.core.eventbus.SessionStateEventBus;
import com.instabug.library.core.plugin.C0642a;
import com.instabug.library.internal.storage.cache.AssetsCacheManager;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.OnDiskCache;
import com.instabug.library.internal.storage.cache.SessionsCacheManager;
import com.instabug.library.internal.storage.cache.UserAttributesCacheManager;
import com.instabug.library.internal.storage.cache.p028a.C0672a;
import com.instabug.library.internal.storage.cache.p028a.C0673b;
import com.instabug.library.internal.video.AutoScreenRecordingService;
import com.instabug.library.internal.video.InternalAutoScreenRecorderHelper;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.migration.C0712c;
import com.instabug.library.model.C0717b;
import com.instabug.library.model.Session;
import com.instabug.library.network.worker.fetcher.InstabugFeaturesFetcherService;
import com.instabug.library.network.worker.uploader.InstabugSessionUploaderService;
import com.instabug.library.p018a.C0579a;
import com.instabug.library.p022b.DialogC0630a;
import com.instabug.library.p022b.DialogC0631b;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.user.C0750a;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.OrientationUtils;
import com.instabug.library.util.PlaceHolderUtils;
import java.lang.ref.WeakReference;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: InstabugDelegate.java */
/* renamed from: com.instabug.library.a */
/* loaded from: classes.dex */
public class C0578a implements C0633a.a {

    /* renamed from: a */
    private WeakReference<Context> f581a;

    /* renamed from: b */
    private Subscription f582b;

    /* renamed from: c */
    private Dialog f583c;

    /* renamed from: d */
    private final C0633a f584d = new C0633a(this);

    public C0578a(Context context) {
        this.f581a = new WeakReference<>(context);
        C0642a.m1242a(context);
        C0629b.m1160a().m1171b(context);
        m985m();
    }

    @Override // com.instabug.library.broadcast.C0633a.a
    /* renamed from: a */
    public void mo999a(boolean z) {
        InstabugSDKLogger.m1799d(this, "SDK Invoked: " + z);
        if (Instabug.getState() != InstabugState.TAKING_SCREENSHOT && Instabug.getState() != InstabugState.RECORDING_VIDEO && Instabug.getState() != InstabugState.TAKING_SCREENSHOT_FOR_CHAT && Instabug.getState() != InstabugState.RECORDING_VIDEO_FOR_CHAT && Instabug.getState() != InstabugState.IMPORTING_IMAGE_FROM_GALLERY_FOR_CHAT) {
            if (z) {
                Instabug.setState(InstabugState.INVOKED);
                m978a(AutoScreenRecordingService.Action.STOP_TRIM_KEEP);
                return;
            }
            Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
            if (currentActivity != null) {
                OrientationUtils.unlockOrientation(currentActivity);
            }
            if (C0629b.m1160a().m1169a(Feature.INSTABUG)) {
                Instabug.setState(InstabugState.ENABLED);
            } else {
                Instabug.setState(InstabugState.DISABLED);
            }
        }
    }

    /* renamed from: k */
    private void m983k() {
        this.f582b = SessionStateEventBus.getInstance().subscribe(new Action1<Session.SessionState>() { // from class: com.instabug.library.a.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Session.SessionState sessionState) {
                if (sessionState.equals(Session.SessionState.FINISH)) {
                    C0578a.this.m986n();
                }
                C0578a.this.m989q();
            }
        });
    }

    /* renamed from: l */
    private void m984l() {
        if (this.f582b != null && !this.f582b.isUnsubscribed()) {
            this.f582b.unsubscribe();
        }
    }

    /* renamed from: m */
    private void m985m() {
        new Thread(new Runnable() { // from class: com.instabug.library.a.2
            @Override // java.lang.Runnable
            public void run() {
                C0578a.this.m987o();
                C0578a.this.m988p();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: n */
    public void m986n() {
        new Thread(new Runnable() { // from class: com.instabug.library.a.3
            @Override // java.lang.Runnable
            public void run() {
                Context context;
                InstabugSDKLogger.m1799d(this, "Dumping caches");
                UserAttributesCacheManager.saveCacheToDisk();
                if (C0578a.this.f581a != null && (context = (Context) C0578a.this.f581a.get()) != null) {
                    AssetsCacheManager.cleanUpCache(context);
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: o */
    public void m987o() {
        InstabugSDKLogger.m1803v(this, "Creating sessions disk cache");
        CacheManager.getInstance().addCache(new OnDiskCache(m1007i(), SessionsCacheManager.SESSIONS_DISK_CACHE_KEY, SessionsCacheManager.SESSIONS_DISK_CACHE_FILE_NAME, Session.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: p */
    public void m988p() {
        InstabugSDKLogger.m1803v(this, "Creating UserAttributes disk cache");
        CacheManager.getInstance().addCache(new OnDiskCache(m1007i(), UserAttributesCacheManager.USER_ATTRIBUTES_DISK_CACHE_KEY, UserAttributesCacheManager.USER_ATTRIBUTES_DISK_CACHE_FILE_NAME, C0717b.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: q */
    public void m989q() {
        Context context;
        if (this.f581a != null && (context = this.f581a.get()) != null) {
            context.startService(new Intent(context, InstabugFeaturesFetcherService.class));
            context.startService(new Intent(context, InstabugSessionUploaderService.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m998a() {
        if (C0629b.m1160a().m1169a(Feature.INSTABUG)) {
            m1000b();
        } else {
            Instabug.setState(InstabugState.DISABLED);
        }
        InstabugSDKLogger.m1803v(this, "Initializing invocation manager");
        m992t();
    }

    /* renamed from: b */
    void m1000b() {
        m983k();
        InstabugSDKLogger.m1799d(this, "Initializing the exception handler");
        m990r();
        InstabugSDKLogger.m1799d(this, "Starting Instabug SDK functionality");
        Instabug.setState(InstabugState.ENABLED);
        InstabugSDKLogger.m1803v(this, "show intro dialog if valid");
        m995w();
        InstabugSDKLogger.m1803v(this, "Initializing Session manager");
        m1002d();
        InstabugSDKLogger.m1803v(this, "Initializing Internal tracking delegate");
        m1001c();
        InstabugSDKLogger.m1803v(this, "Initializing surveys manager");
        InstabugSDKLogger.m1803v(this, "Initializing database manager");
        m993u();
        InstabugSDKLogger.m1803v(this, "run valid migration");
        m994v();
        InstabugSDKLogger.m1803v(this, "Registering broadcasts");
        m1003e();
        InstabugSDKLogger.m1803v(this, "Preparing user state");
        m991s();
        InstabugSDKLogger.m1803v(this, "Initializing auto screen recording");
        m997y();
        this.f583c = null;
    }

    /* renamed from: r */
    private void m990r() {
        Thread.setDefaultUncaughtExceptionHandler(new C0579a());
    }

    /* renamed from: c */
    void m1001c() {
        InstabugInternalTrackingDelegate.getInstance();
    }

    /* renamed from: d */
    void m1002d() {
        C0646e.m1252a(SettingsManager.getInstance());
    }

    /* renamed from: e */
    public void m1003e() {
        LocalBroadcastManager.getInstance(m1007i()).registerReceiver(this.f584d, new IntentFilter("SDK invoked"));
    }

    /* renamed from: s */
    private void m991s() {
        C0750a.m1791e();
    }

    /* renamed from: t */
    private void m992t() {
        InstabugSDKLogger.m1803v(this, "initialize Instabug InvocationMode Manager");
        C0704b.m1510b();
    }

    /* renamed from: u */
    private void m993u() {
        C0672a.m1310a(new C0673b(m1007i()));
    }

    /* renamed from: v */
    private void m994v() {
        new Thread(new Runnable() { // from class: com.instabug.library.a.4
            @Override // java.lang.Runnable
            public void run() {
                C0712c.m1570a(C0578a.this.m1007i());
            }
        }).start();
    }

    /* renamed from: w */
    private void m995w() {
        InstabugSDKLogger.m1803v(this, "Checking if should show intro dialog, firstRun " + SettingsManager.getInstance().isFirstRun() + ", SettingsManager.getInstance().isIntroMessageEnabled() " + SettingsManager.getInstance().isIntroMessageEnabled());
        if (SettingsManager.getInstance().isFirstRun()) {
            InstabugSDKLogger.m1803v(this, "Showing Intro Message");
            new Handler().postDelayed(new Runnable() { // from class: com.instabug.library.a.5
                @Override // java.lang.Runnable
                public void run() {
                    if (SettingsManager.getInstance().isIntroMessageEnabled()) {
                        C0578a.this.m1004f();
                    }
                }
            }, 10000L);
        }
    }

    /* renamed from: f */
    public void m1004f() {
        if (!Instabug.isEnabled()) {
            Log.e("Instabug", "Cannot show intro message while SDK is Disabled");
            return;
        }
        Activity targetActivity = InstabugInternalTrackingDelegate.getInstance().getTargetActivity();
        if (targetActivity != null && !targetActivity.isFinishing()) {
            InstabugInvocationEvent m1523d = C0704b.m1513c().m1523d();
            Resources resources = targetActivity.getResources();
            AssetManager assets = resources.getAssets();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = new Configuration(resources.getConfiguration());
            configuration.locale = Instabug.getLocale(targetActivity);
            Resources resources2 = new Resources(assets, displayMetrics, configuration);
            if (m1523d == InstabugInvocationEvent.SHAKE) {
                this.f583c = new DialogC0630a(targetActivity, PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.SHAKE_HINT, resources2.getString(C0577R.string.instabug_str_shake_hint)));
            } else if (m1523d == InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT) {
                this.f583c = new DialogC0631b(targetActivity, PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.SWIPE_HINT, resources2.getString(C0577R.string.instabug_str_swipe_hint)));
            }
            if (this.f583c != null) {
                this.f583c.setCanceledOnTouchOutside(true);
                this.f583c.show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: g */
    public void m1005g() {
        InstabugSDKLogger.m1799d(this, "Stopping Instabug SDK functionality");
        Instabug.setState(InstabugState.DISABLED);
        InstabugSDKLogger.m1803v(this, "Un-registering broadcasts");
        m1006h();
        m984l();
        m996x();
        C0642a.m1241a();
        try {
            if (this.f583c != null && this.f583c.isShowing()) {
                InstabugSDKLogger.m1803v(this, "Dismissing instabug dialog");
                this.f583c.dismiss();
            }
        } catch (Exception e) {
            InstabugSDKLogger.m1799d(this, Log.getStackTraceString(e.getCause()));
        }
    }

    /* renamed from: h */
    public void m1006h() {
        LocalBroadcastManager.getInstance(m1007i()).unregisterReceiver(this.f584d);
    }

    /* renamed from: x */
    private void m996x() {
        InstabugSDKLogger.m1799d(this, "Stopping Instabug SDK invocation listeners");
        C0704b.m1513c().m1526g();
    }

    /* renamed from: i */
    public Context m1007i() {
        if (this.f581a.get() == null) {
            InstabugSDKLogger.m1800e(this, "Application context instance equal null");
        }
        return this.f581a.get();
    }

    /* renamed from: j */
    public void m1008j() {
    }

    /* renamed from: a */
    private void m978a(AutoScreenRecordingService.Action action) {
        if (SettingsManager.getInstance().autoScreenRecordingEnabled()) {
            InstabugSDKLogger.m1802i(this, "Sending auto event: " + action.toString());
            AutoScreenRecordingEventBus.getInstance().post(action);
        }
    }

    /* renamed from: y */
    private void m997y() {
        InternalAutoScreenRecorderHelper.getInstance().start();
    }
}
