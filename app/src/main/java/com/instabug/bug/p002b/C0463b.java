package com.instabug.bug.p002b;

import android.app.Activity;
import com.instabug.bug.C0461b;
import com.instabug.bug.C0468d;
import com.instabug.bug.cache.BugsCacheManager;
import com.instabug.bug.model.Bug;
import com.instabug.library.core.eventbus.ScreenRecordingEventBus;
import com.instabug.library.core.eventbus.VideoProcessingServiceEventBus;
import com.instabug.library.internal.video.InternalScreenRecordHelper;
import com.instabug.library.internal.video.ScreenRecordEvent;
import com.instabug.library.internal.video.VideoProcessingService;
import com.instabug.library.model.Attachment;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.util.ArrayList;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: ExternalScreenRecordHelper.java */
/* renamed from: com.instabug.bug.b.b */
/* loaded from: classes.dex */
public class C0463b {

    /* renamed from: a */
    private static C0463b f63a;

    /* renamed from: b */
    private Subscription f64b;

    /* renamed from: a */
    public static C0463b m56a() {
        if (f63a == null) {
            f63a = new C0463b();
        }
        return f63a;
    }

    /* renamed from: a */
    public void m68a(final String str) {
        InternalScreenRecordHelper.getInstance().init();
        if (this.f64b == null || this.f64b.isUnsubscribed()) {
            this.f64b = ScreenRecordingEventBus.getInstance().subscribe(new Action1<ScreenRecordEvent>() { // from class: com.instabug.bug.b.b.1
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(ScreenRecordEvent screenRecordEvent) {
                    Bug bug;
                    Bug bug2;
                    if (screenRecordEvent.getStatus() == 1) {
                        if (str != null && (bug2 = BugsCacheManager.getBug(str)) != null) {
                            C0463b.this.m67a(C0463b.this.m58a(bug2.m131e()), screenRecordEvent.getVideoUri().getPath());
                            bug2.m113a(Bug.BugState.READY_TO_BE_SENT);
                            BugsCacheManager.addBug(bug2);
                        }
                        C0463b.this.m65d();
                        return;
                    }
                    if (screenRecordEvent.getStatus() == 0) {
                        C0463b.this.m61a(screenRecordEvent);
                        C0463b.this.m64c();
                    } else if (screenRecordEvent.getStatus() == 2) {
                        if (str != null && (bug = BugsCacheManager.getBug(str)) != null) {
                            C0463b.this.m66a(bug, C0463b.this.m58a(bug.m131e()));
                            bug.m113a(Bug.BugState.READY_TO_BE_SENT);
                            BugsCacheManager.addBug(bug);
                        }
                        C0463b.this.m65d();
                    }
                }
            });
        }
        VideoProcessingServiceEventBus.getInstance().subscribe(new Action1<VideoProcessingService.Action>() { // from class: com.instabug.bug.b.b.2
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(VideoProcessingService.Action action) {
                if (C0463b.this.f64b != null) {
                    C0463b.this.m65d();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m61a(ScreenRecordEvent screenRecordEvent) {
        C0468d.m86a().m103d().m112a(screenRecordEvent.getVideoUri(), Attachment.Type.VIDEO);
        C0468d.m86a().m103d().m113a(Bug.BugState.WAITING_VIDEO);
    }

    /* renamed from: a */
    public void m67a(Attachment attachment, String str) {
        attachment.setLocalPath(str);
        attachment.setVideoEncoded(true);
    }

    /* renamed from: a */
    public void m66a(Bug bug, Attachment attachment) {
        bug.m131e().remove(attachment);
        File file = new File(attachment.getLocalPath());
        if (Attachment.Type.VIDEO.equals(attachment.getType())) {
            InstabugSDKLogger.m1799d(this, "removing video attachment");
            bug.setHasVideo(false);
            VideoProcessingServiceEventBus.getInstance().post(VideoProcessingService.Action.STOP);
        }
        if (file.delete()) {
            InstabugSDKLogger.m1802i(this, "attachment removed successfully");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public Attachment m58a(ArrayList<Attachment> arrayList) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Attachment attachment = arrayList.get(size);
            if (attachment.getType() == Attachment.Type.VIDEO) {
                return attachment;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m64c() {
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(C0461b.m53c(currentActivity.getApplicationContext()));
        }
    }

    /* renamed from: b */
    public boolean m69b() {
        return InternalScreenRecordHelper.getInstance().isRecording();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public void m65d() {
        if (!this.f64b.isUnsubscribed()) {
            this.f64b.unsubscribe();
        }
    }
}
