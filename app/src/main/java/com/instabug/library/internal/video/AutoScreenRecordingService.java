package com.instabug.library.internal.video;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.instabug.library.core.eventbus.AutoScreenRecordingEventBus;
import com.instabug.library.core.eventbus.SessionStateEventBus;
import com.instabug.library.internal.video.C0687b;
import com.instabug.library.model.Session;
import com.instabug.library.settings.SettingsManager;
import p045rx.functions.Action1;
import p045rx.subscriptions.CompositeSubscription;

/* loaded from: classes.dex */
public class AutoScreenRecordingService extends Service {

    /* renamed from: a */
    private final C0687b.a f856a = new C0687b.a() { // from class: com.instabug.library.internal.video.AutoScreenRecordingService.1
        @Override // com.instabug.library.internal.video.C0687b.a
        /* renamed from: a */
        public void mo1328a() {
        }

        @Override // com.instabug.library.internal.video.C0687b.a
        /* renamed from: b */
        public void mo1329b() {
        }
    };

    /* renamed from: b */
    private C0687b f857b;

    /* renamed from: c */
    private CompositeSubscription f858c;

    /* loaded from: classes.dex */
    public enum Action {
        STOP_DELETE,
        STOP_KEEP,
        STOP_TRIM_KEEP
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static Intent m1325a(Context context, int i, Intent intent) {
        Intent intent2 = new Intent(context, AutoScreenRecordingService.class);
        intent2.putExtra("result-code", i);
        intent2.putExtra("data", intent);
        return intent2;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            stopSelf();
        } else {
            this.f858c = new CompositeSubscription();
            m1327a();
            int intExtra = intent.getIntExtra("result-code", 0);
            Intent intent2 = (Intent) intent.getParcelableExtra("data");
            if (intExtra == 0 || intent2 == null) {
                throw new IllegalStateException("Result code or data missing.");
            }
            if (!SettingsManager.getInstance().isScreenCurrentlyRecorded()) {
                this.f857b = new C0687b(this, this.f856a, intExtra, intent2);
                SettingsManager.getInstance().setScreenCurrentlyRecorded(true);
            }
        }
        return super.onStartCommand(intent, i, i2);
    }

    /* renamed from: a */
    private void m1327a() {
        this.f858c.add(AutoScreenRecordingEventBus.getInstance().subscribe(new Action1<Action>() { // from class: com.instabug.library.internal.video.AutoScreenRecordingService.2
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Action action) {
                switch (C06784.f862a[action.ordinal()]) {
                    case 1:
                        if (SettingsManager.getInstance().isScreenCurrentlyRecorded()) {
                            SettingsManager.getInstance().setScreenCurrentlyRecorded(false);
                            AutoScreenRecordingService.this.f857b.m1353a();
                            AutoScreenRecordingService.this.f857b.m1355b();
                            return;
                        }
                        return;
                    case 2:
                        if (SettingsManager.getInstance().isScreenCurrentlyRecorded()) {
                            SettingsManager.getInstance().setScreenCurrentlyRecorded(false);
                            AutoScreenRecordingService.this.f857b.m1353a();
                            AutoScreenRecordingService.this.f857b.m1356c();
                            return;
                        }
                        return;
                    case 3:
                        if (SettingsManager.getInstance().isScreenCurrentlyRecorded()) {
                            SettingsManager.getInstance().setScreenCurrentlyRecorded(false);
                            AutoScreenRecordingService.this.f857b.m1353a();
                            AutoScreenRecordingService.this.f857b.m1354a(SettingsManager.getInstance().autoScreenRecordingMaxDuration());
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }));
        this.f858c.add(SessionStateEventBus.getInstance().subscribe(new Action1<Session.SessionState>() { // from class: com.instabug.library.internal.video.AutoScreenRecordingService.3
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Session.SessionState sessionState) {
                if (sessionState == Session.SessionState.FINISH) {
                    AutoScreenRecordingService.this.f857b.m1353a();
                }
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.internal.video.AutoScreenRecordingService$4 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C06784 {

        /* renamed from: a */
        static final /* synthetic */ int[] f862a = new int[Action.values().length];

        static {
            try {
                f862a[Action.STOP_DELETE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f862a[Action.STOP_KEEP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f862a[Action.STOP_TRIM_KEEP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        if (this.f858c != null) {
            this.f858c.clear();
        }
    }
}
