package com.instabug.library.internal.video;

import android.net.Uri;
import com.instabug.library.Instabug;
import com.instabug.library.core.eventbus.ScreenRecordingEventBus;
import com.instabug.library.core.eventbus.SessionStateEventBus;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.internal.video.C0688c;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.p029a.ViewOnClickListenerC0700c;
import com.instabug.library.model.Session;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.C0755c;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public class InternalScreenRecordHelper implements C0688c.b, ViewOnClickListenerC0700c.c {
    private static InternalScreenRecordHelper INSTANCE;
    private ViewOnClickListenerC0700c fab;
    private Subscription sessionSubscription;

    private InternalScreenRecordHelper() {
    }

    public static InternalScreenRecordHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InternalScreenRecordHelper();
        }
        return INSTANCE;
    }

    public void init() {
        C0704b.m1513c().m1527h();
        if (this.fab == null) {
            this.fab = new ViewOnClickListenerC0700c(this);
            this.fab.m1471a();
        } else {
            this.fab.m1471a();
        }
    }

    @Override // com.instabug.library.invocation.p029a.ViewOnClickListenerC0700c.c
    public void start() {
        subscribeToSessionEvents();
        startSnapping();
    }

    public void pause() {
    }

    @Override // com.instabug.library.invocation.p029a.ViewOnClickListenerC0700c.c
    public void stop() {
        C0688c.m1357a().m1362b();
        release();
    }

    public void release() {
        if (this.fab != null) {
            this.fab.m1472b();
        }
        unsubscribeFromSessionEvents();
        C0704b.m1513c().m1528i();
        C0755c.m1810b(Instabug.getApplicationContext());
    }

    public void cancel() {
        if (isRecording()) {
            C0688c.m1357a().m1363c();
            C0755c.m1810b(Instabug.getApplicationContext());
            if (this.fab != null) {
                this.fab.m1472b();
                this.fab.m1471a();
            }
            InstabugSDKLogger.m1799d(this, "Cancelling screen recording");
            SettingsManager.getInstance().setVideoProcessorBusy(false);
        }
    }

    private void startSnapping() {
        if (!C0688c.m1357a().m1364d()) {
            C0688c.m1357a().m1359a(this);
        }
    }

    public boolean isRecording() {
        return C0688c.m1357a().m1364d() || SettingsManager.getInstance().isVideoProcessorBusy();
    }

    private void subscribeToSessionEvents() {
        if (this.sessionSubscription == null || this.sessionSubscription.isUnsubscribed()) {
            this.sessionSubscription = SessionStateEventBus.getInstance().subscribe(new Action1<Session.SessionState>() { // from class: com.instabug.library.internal.video.InternalScreenRecordHelper.1
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(Session.SessionState sessionState) {
                    if (sessionState == Session.SessionState.FINISH) {
                        InternalScreenRecordHelper.this.cancel();
                    }
                }
            });
        }
    }

    private void unsubscribeFromSessionEvents() {
        if (!this.sessionSubscription.isUnsubscribed()) {
            this.sessionSubscription.unsubscribe();
        }
    }

    @Override // com.instabug.library.internal.video.C0688c.b
    public void onFramesCapturingFinished(String str) {
        File videoFile = AttachmentsUtility.getVideoFile(Instabug.getApplicationContext());
        ScreenRecordingEventBus.getInstance().post(new ScreenRecordEvent(0, Uri.fromFile(videoFile)));
        InstabugSDKLogger.m1802i(this, "Encoding...");
        VideoProcessingService.m1338a(Instabug.getApplicationContext(), videoFile.getPath(), str);
    }
}
