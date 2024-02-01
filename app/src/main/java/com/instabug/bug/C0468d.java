package com.instabug.bug;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.bug.OnSdkDismissedCallback;
import com.instabug.bug.cache.BugsCacheManager;
import com.instabug.bug.model.Bug;
import com.instabug.bug.settings.C0482a;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.internal.storage.DiskUtils;
import com.instabug.library.internal.storage.cache.UserAttributesCacheManager;
import com.instabug.library.logging.InstabugLog;
import com.instabug.library.logging.InstabugUserEventLogger;
import com.instabug.library.model.Attachment;
import com.instabug.library.user.UserEvent;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;

/* compiled from: LiveBugManager.java */
/* renamed from: com.instabug.bug.d */
/* loaded from: classes.dex */
public class C0468d {

    /* renamed from: a */
    private static C0468d f68a = new C0468d();

    /* renamed from: b */
    private Bug f69b;

    /* renamed from: c */
    private boolean f70c;

    /* renamed from: d */
    private OnSdkDismissedCallback.DismissType f71d = null;

    private C0468d() {
    }

    /* renamed from: a */
    public static C0468d m86a() {
        return f68a;
    }

    /* renamed from: a */
    public void m97a(Bug bug) {
        this.f69b = bug;
        this.f70c = false;
        this.f71d = OnSdkDismissedCallback.DismissType.CANCEL;
    }

    /* renamed from: b */
    public void m99b() {
        this.f69b = null;
    }

    /* renamed from: c */
    public void m101c() {
        if (this.f69b != null && this.f69b.m131e() != null) {
            Iterator<Attachment> it = this.f69b.m131e().iterator();
            while (it.hasNext()) {
                DiskUtils.deleteFile(it.next().getLocalPath());
            }
        }
        m99b();
    }

    /* renamed from: d */
    public Bug m103d() {
        return this.f69b;
    }

    /* renamed from: a */
    public void m93a(Context context) {
        if (this.f69b == null) {
            m97a(new Bug.C0470a().m142a(context));
        }
    }

    /* renamed from: a */
    public void m94a(Context context, Uri uri) {
        m95a(context, uri, null, Attachment.Type.IMAGE);
    }

    /* renamed from: a */
    public void m95a(Context context, Uri uri, String str, Attachment.Type type) {
        m103d().m112a(AttachmentsUtility.getNewFileAttachmentUri(context, uri, str), type);
        m100b(context);
    }

    /* renamed from: b */
    public void m100b(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("refresh.attachments"));
    }

    /* renamed from: a */
    public void m96a(OnSdkDismissedCallback.DismissType dismissType) {
        this.f71d = dismissType;
    }

    /* renamed from: e */
    public OnSdkDismissedCallback.DismissType m104e() {
        return this.f71d;
    }

    /* renamed from: f */
    public boolean m105f() {
        return this.f70c;
    }

    /* renamed from: a */
    public void m98a(boolean z) {
        this.f70c = z;
    }

    /* renamed from: c */
    public void m102c(final Context context) {
        C0482a m236a = C0482a.m236a();
        if (m236a.m259g() != null) {
            try {
                m236a.m259g().run();
            } catch (Exception e) {
                InstabugSDKLogger.m1801e("LiveBugManager", "Pre sending runnable failed to run.", e);
            }
        }
        new Thread(new Runnable() { // from class: com.instabug.bug.d.1
            @Override // java.lang.Runnable
            public void run() {
                C0468d.this.m91e(context);
                C0468d.this.m90d(context);
                C0468d.this.m106g();
                C0468d.this.m96a(OnSdkDismissedCallback.DismissType.SUBMIT);
                if (!C0468d.this.m103d().hasVideo() || (C0468d.this.m103d().hasVideo() && C0468d.this.m103d().isVideoEncoded())) {
                    InstabugSDKLogger.m1799d("LiveBugManager", "Sending bug..");
                    BugsCacheManager.addBug(C0468d.this.m103d().m113a(Bug.BugState.READY_TO_BE_SENT));
                } else {
                    BugsCacheManager.addBug(C0468d.this.m103d().m113a(Bug.BugState.WAITING_VIDEO));
                }
                Looper.prepare();
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.instabug.bug.d.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        C0468d.this.m92i();
                        C0468d.this.m99b();
                    }
                });
            }
        }).start();
    }

    /* renamed from: g */
    public void m106g() {
        if (this.f69b.getState() != null) {
            this.f69b.getState().setTags(InstabugCore.getTagsAsString());
            this.f69b.getState().setUserAttributes(UserAttributesCacheManager.getUserAttributes());
            this.f69b.getState().updateConsoleLog();
            if (InstabugCore.getFeatureState(Feature.USER_DATA) == Feature.State.ENABLED) {
                this.f69b.getState().setUserData(InstabugCore.getUserData());
            }
            if (InstabugCore.getFeatureState(Feature.INSTABUG_LOGS) == Feature.State.ENABLED) {
                this.f69b.getState().setInstabugLog(InstabugLog.getLogs());
            }
            if (InstabugCore.getFeatureState(Feature.USER_EVENTS) == Feature.State.ENABLED) {
                try {
                    this.f69b.getState().setUserEvents(UserEvent.toJson(InstabugUserEventLogger.getInstance().getUserEvents()).toString());
                } catch (JSONException e) {
                    InstabugSDKLogger.m1801e(this, "Got error while parsing user events logs", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public void m90d(Context context) {
        Iterator<Attachment> it = m103d().m131e().iterator();
        while (it.hasNext()) {
            Attachment next = it.next();
            if (next.getType().equals(Attachment.Type.IMAGE) || next.getType().equals(Attachment.Type.MAIN_SCREENSHOT)) {
                try {
                    BitmapUtils.compressBitmapAndSave(AttachmentsUtility.getAttachmentFile(context, next.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                    InstabugSDKLogger.m1800e(C0468d.class, "Failed to compress MAIN_SCREENSHOT or IMAGE & save original as it is");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public void m91e(Context context) {
        for (Map.Entry<Uri, String> entry : InstabugCore.getExtraAttachmentFiles().entrySet()) {
            m95a(context, entry.getKey(), entry.getValue(), Attachment.Type.ATTACHMENT_FILE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: i */
    public void m92i() {
        C0482a m236a = C0482a.m236a();
        if (m236a.m260h() != null) {
            m236a.m260h().onSdkDismissed(m104e(), m103d().m126c());
        }
    }

    /* renamed from: h */
    public void m107h() {
        m98a(true);
        m96a(OnSdkDismissedCallback.DismissType.ADD_ATTACHMENT);
        m92i();
    }
}
