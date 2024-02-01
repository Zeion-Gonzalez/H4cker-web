package com.instabug.library.internal.video;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugState;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.AutoScreenRecordingEventBus;
import com.instabug.library.core.eventbus.CurrentActivityLifeCycleEventBus;
import com.instabug.library.core.eventbus.SessionStateEventBus;
import com.instabug.library.internal.video.AutoScreenRecordingService;
import com.instabug.library.model.Session;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.tracking.ActivityLifeCycleEvent;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import java.io.File;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public class InternalAutoScreenRecorderHelper implements AutoScreenRecordingContract {
    private static InternalAutoScreenRecorderHelper INSTANCE;
    private Subscription activityLifeCycleSubscription;
    private Subscription sessionSubscription;
    private boolean isCrashOccurred = false;
    private AutoScreenRecordingFileHolder fileHolder = new AutoScreenRecordingFileHolder();

    public static InternalAutoScreenRecorderHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InternalAutoScreenRecorderHelper();
        }
        return INSTANCE;
    }

    public InternalAutoScreenRecorderHelper() {
        subscribeToSessionEvents();
        subscribeToActivityLifeCycleEvents();
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    public Uri getAutoScreenRecordingFileUri() {
        return this.fileHolder.getAutoScreenRecordingFileUri();
    }

    public void setAutoScreenRecordingFile(File file) {
        this.fileHolder.setAutoScreenRecordingFile(file);
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    public void delete() {
        this.fileHolder.delete();
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    public void clear() {
        this.fileHolder.clear();
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    public boolean isEnabled() {
        return SettingsManager.getInstance().autoScreenRecordingEnabled();
    }

    public boolean isCrashOccurred() {
        return this.isCrashOccurred;
    }

    public void setCrashOccurred(boolean z) {
        this.isCrashOccurred = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void subscribeToSessionEvents() {
        if (this.sessionSubscription == null || this.sessionSubscription.isUnsubscribed()) {
            this.sessionSubscription = SessionStateEventBus.getInstance().subscribe(new Action1<Session.SessionState>() { // from class: com.instabug.library.internal.video.InternalAutoScreenRecorderHelper.1
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(Session.SessionState sessionState) {
                    if (sessionState == Session.SessionState.FINISH && !InternalAutoScreenRecorderHelper.this.isCrashOccurred()) {
                        AutoScreenRecordingEventBus.getInstance().post(AutoScreenRecordingService.Action.STOP_DELETE);
                        SettingsManager.getInstance().setAutoScreenRecordingDenied(false);
                        InternalAutoScreenRecorderHelper.this.unsubscribeFromSessionEvents();
                    }
                }
            });
        }
    }

    private void subscribeToActivityLifeCycleEvents() {
        if (this.activityLifeCycleSubscription == null || this.activityLifeCycleSubscription.isUnsubscribed()) {
            this.activityLifeCycleSubscription = CurrentActivityLifeCycleEventBus.getInstance().subscribe(new Action1<ActivityLifeCycleEvent>() { // from class: com.instabug.library.internal.video.InternalAutoScreenRecorderHelper.2
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                    switch (C06813.f866a[activityLifeCycleEvent.ordinal()]) {
                        case 1:
                            InternalAutoScreenRecorderHelper.this.subscribeToSessionEvents();
                            new Handler().postDelayed(new Runnable() { // from class: com.instabug.library.internal.video.InternalAutoScreenRecorderHelper.2.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    InternalAutoScreenRecorderHelper.getInstance().start();
                                }
                            }, 500L);
                            return;
                        default:
                            return;
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.internal.video.InternalAutoScreenRecorderHelper$3 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C06813 {

        /* renamed from: a */
        static final /* synthetic */ int[] f866a = new int[ActivityLifeCycleEvent.values().length];

        static {
            try {
                f866a[ActivityLifeCycleEvent.RESUMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unsubscribeFromSessionEvents() {
        if (!this.sessionSubscription.isUnsubscribed()) {
            this.sessionSubscription.unsubscribe();
        }
    }

    public void start() {
        Activity targetActivity;
        if (!SettingsManager.getInstance().isScreenCurrentlyRecorded() && !SettingsManager.getInstance().isAutoScreenRecordingDenied() && isEnabled() && (targetActivity = InstabugInternalTrackingDelegate.getInstance().getTargetActivity()) != null && !(targetActivity instanceof _InstabugActivity) && SettingsManager.getInstance().isAppOnForeground() && Instabug.getState() == InstabugState.ENABLED && !InstabugCore.isForegroundBusy()) {
            targetActivity.startActivity(new Intent(targetActivity, RequestPermissionActivity.class));
            targetActivity.overridePendingTransition(0, 0);
        }
    }
}
