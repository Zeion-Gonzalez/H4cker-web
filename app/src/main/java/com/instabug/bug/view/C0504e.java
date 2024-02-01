package com.instabug.bug.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import com.instabug.bug.BugPlugin;
import com.instabug.bug.C0468d;
import com.instabug.bug.extendedbugreport.ExtendedBugReport;
import com.instabug.bug.model.Bug;
import com.instabug.bug.p002b.C0463b;
import com.instabug.bug.screenshot.C0477a;
import com.instabug.bug.screenshot.viewhierarchy.C0480c;
import com.instabug.bug.screenshot.viewhierarchy.utilities.ViewHierarchyInspectorEventBus;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.view.InterfaceC0503d;
import com.instabug.bug.view.p004b.C0498a;
import com.instabug.library.C0577R;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.VideoProcessingServiceEventBus;
import com.instabug.library.core.p024ui.BasePresenter;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.internal.storage.cache.Cache;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.video.VideoProcessingService;
import com.instabug.library.model.Attachment;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PermissionsUtils;
import com.instabug.library.util.PlaceHolderUtils;
import java.io.File;
import java.util.ArrayList;
import p045rx.functions.Action1;
import p045rx.subscriptions.CompositeSubscription;

/* compiled from: BugReportingFragmentPresenter.java */
/* renamed from: com.instabug.bug.view.e */
/* loaded from: classes.dex */
public class C0504e extends BasePresenter<InterfaceC0503d.b> implements InterfaceC0503d.a {

    /* renamed from: a */
    private CompositeSubscription f244a;

    /* renamed from: b */
    private a f245b;

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: BugReportingFragmentPresenter.java */
    /* renamed from: com.instabug.bug.view.e$a */
    /* loaded from: classes.dex */
    public enum a {
        NONE,
        SEND_BUG,
        TAKE_EXTRA_SCREENSHOT,
        RECORD_VIDEO
    }

    public C0504e(InterfaceC0503d.b bVar) {
        super(bVar);
        m436h();
        this.f245b = a.NONE;
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: a */
    public void mo419a() {
        this.f244a = new CompositeSubscription();
        m439k();
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: b */
    public void mo425b() {
        this.f244a.unsubscribe();
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: a */
    public void mo423a(String str) {
        C0468d.m86a().m103d().getState().setUserEmail(str);
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: b */
    public void mo426b(String str) {
        C0468d.m86a().m103d().m128d(str);
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: f */
    public void mo431f() {
        final InterfaceC0503d.b bVar;
        C0468d.m86a().m98a(true);
        if (this.view != null && (bVar = (InterfaceC0503d.b) this.view.get()) != null) {
            PermissionsUtils.requestPermission(bVar.getViewContext(), "android.permission.WRITE_EXTERNAL_STORAGE", 3873, (Runnable) null, new Runnable() { // from class: com.instabug.bug.view.e.1
                @Override // java.lang.Runnable
                public void run() {
                    InstabugSDKLogger.m1802i(C0504e.this, "Permission granted");
                    C0468d.m86a().m107h();
                    bVar.mo404g();
                }
            });
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: e */
    public void mo430e() {
        if (this.view != null) {
            InterfaceC0503d.b bVar = (InterfaceC0503d.b) this.view.get();
            if (C0468d.m86a().m103d().m138k() && C0468d.m86a().m103d().m139l() == Bug.ViewHierarchyInspectionState.IN_PROGRESS) {
                this.f245b = a.TAKE_EXTRA_SCREENSHOT;
                if (bVar != null) {
                    bVar.mo405h();
                    return;
                }
                return;
            }
            C0468d.m86a().m107h();
            C0468d.m86a().m103d().m113a(Bug.BugState.IN_PROGRESS);
            BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
            if (bugPlugin != null) {
                bugPlugin.setState(2);
                C0477a.m168a().m170a(bugPlugin.getAppContext());
            }
            if (bVar != null) {
                bVar.finishActivity();
            }
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: d */
    public void mo429d() {
        if (this.view != null) {
            InterfaceC0503d.b bVar = (InterfaceC0503d.b) this.view.get();
            if (C0468d.m86a().m103d().m138k() && C0468d.m86a().m103d().m139l() == Bug.ViewHierarchyInspectionState.IN_PROGRESS) {
                this.f245b = a.RECORD_VIDEO;
                if (bVar != null) {
                    bVar.mo405h();
                    return;
                }
                return;
            }
            C0468d.m86a().m107h();
            C0463b.m56a().m68a(C0468d.m86a().m103d().getId());
            if (bVar != null) {
                bVar.finishActivity();
            }
            BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
            if (bugPlugin != null) {
                bugPlugin.setState(2);
            }
        }
    }

    /* renamed from: h */
    private void m436h() {
        InterfaceC0503d.b bVar;
        if (this.view != null && (bVar = (InterfaceC0503d.b) this.view.get()) != null && Build.VERSION.SDK_INT < 23 && !PermissionsUtils.isPermissionGranted(bVar.getViewContext().getContext(), "android.permission.RECORD_AUDIO")) {
            Instabug.setShouldAudioRecordingOptionAppear(false);
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: a */
    public void mo421a(Bundle bundle) {
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: a */
    public void mo420a(int i, int i2, Intent intent) {
        InterfaceC0503d.b bVar;
        switch (i) {
            case 3862:
                if (i2 == -1 && intent != null) {
                    if (this.view != null && (bVar = (InterfaceC0503d.b) this.view.get()) != null) {
                        String galleryImagePath = AttachmentsUtility.getGalleryImagePath(bVar.getViewContext().getActivity(), intent.getData());
                        if (galleryImagePath == null) {
                            galleryImagePath = intent.getData().getPath();
                        }
                        if (galleryImagePath != null) {
                            C0468d.m86a().m94a(bVar.getViewContext().getContext(), Uri.fromFile(new File(galleryImagePath)));
                        }
                    }
                    C0468d.m86a().m98a(false);
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: a */
    public void mo422a(@NonNull Attachment attachment) {
        C0468d.m86a().m103d().m131e().remove(attachment);
        File file = new File(attachment.getLocalPath());
        if (Attachment.Type.VIDEO.equals(attachment.getType())) {
            InstabugSDKLogger.m1802i(this, "removing video attachment");
            Cache cache = CacheManager.getInstance().getCache(CacheManager.DEFAULT_IN_MEMORY_CACHE_KEY);
            if (cache != null && cache.delete("video.path") != null) {
                InstabugSDKLogger.m1802i(this, "video attachment removed successfully");
            }
            C0468d.m86a().m103d().setHasVideo(false);
            VideoProcessingServiceEventBus.getInstance().post(VideoProcessingService.Action.STOP);
        }
        if (file.delete()) {
            InstabugSDKLogger.m1802i(this, "attachment removed successfully");
        }
        m440b(attachment);
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: c */
    public void mo428c() {
        InterfaceC0503d.b bVar;
        Bug m103d = C0468d.m86a().m103d();
        if (m103d != null && this.view != null && (bVar = (InterfaceC0503d.b) this.view.get()) != null) {
            bVar.mo393a(m103d.m131e());
        }
    }

    /* renamed from: b */
    public void m440b(Attachment attachment) {
        InterfaceC0503d.b bVar;
        if (this.view != null && (bVar = (InterfaceC0503d.b) this.view.get()) != null) {
            bVar.mo391a(attachment);
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: c */
    public String mo427c(String str) {
        return C0498a.m364a(C0498a.m365a(str, InstabugCore.getPrimaryColor()));
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: g */
    public void mo432g() {
        InterfaceC0503d.b bVar;
        if (this.view != null && (bVar = (InterfaceC0503d.b) this.view.get()) != null) {
            if (C0468d.m86a().m103d().m138k() && C0468d.m86a().m103d().m139l() == Bug.ViewHierarchyInspectionState.IN_PROGRESS) {
                this.f245b = a.SEND_BUG;
                bVar.mo405h();
            } else if (m437i() && m438j()) {
                if (!((C0482a.m236a().m268p().isEmpty() && C0482a.m236a().m269q() == ExtendedBugReport.State.DISABLED) ? false : true)) {
                    C0468d.m86a().m102c(bVar.getViewContext().getContext());
                    bVar.mo402e();
                } else {
                    bVar.mo403f();
                }
                bVar.mo399c(false);
            }
        }
    }

    /* renamed from: i */
    private boolean m437i() {
        InterfaceC0503d.b bVar = (InterfaceC0503d.b) this.view.get();
        String userEmail = C0468d.m86a().m103d().getState().getUserEmail();
        if (C0482a.m236a().m258f() && C0482a.m236a().m261i() && (userEmail == null || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())) {
            bVar.mo395b(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.INVALID_EMAIL_MESSAGE, bVar.getViewContext().getString(C0577R.string.instabug_err_invalid_email)));
            return false;
        }
        C0482a.m236a().m249b(userEmail);
        return true;
    }

    /* renamed from: j */
    private boolean m438j() {
        InterfaceC0503d.b bVar = (InterfaceC0503d.b) this.view.get();
        String m129d = C0468d.m86a().m103d().m129d();
        if (!C0482a.m236a().m263k() || (m129d != null && m129d.trim().length() != 0)) {
            return true;
        }
        bVar.mo398c(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.INVALID_COMMENT_MESSAGE, bVar.getViewContext().getString(C0577R.string.instabug_err_invalid_comment)));
        return false;
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: a */
    public Attachment mo418a(ArrayList<Attachment> arrayList) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Attachment attachment = arrayList.get(size);
            if (attachment.getType() == Attachment.Type.VIDEO) {
                return attachment;
            }
        }
        return null;
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.a
    /* renamed from: a */
    public void mo424a(ArrayList<Attachment> arrayList, String str) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Attachment attachment = arrayList.get(size);
            if (attachment.getType() == Attachment.Type.VIDEO) {
                attachment.setLocalPath(str);
                attachment.setVideoEncoded(true);
                return;
            }
        }
    }

    /* renamed from: k */
    private void m439k() {
        this.f244a.add(ViewHierarchyInspectorEventBus.getInstance().subscribe(new Action1<C0480c.a>() { // from class: com.instabug.bug.view.e.2
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(C0480c.a aVar) {
                final InterfaceC0503d.b bVar;
                InstabugSDKLogger.m1803v(this, "receive a view hierarchy inspection action, action value: " + aVar);
                if ((aVar == C0480c.a.COMPLETED || aVar == C0480c.a.FAILED) && C0504e.this.view != null && (bVar = (InterfaceC0503d.b) C0504e.this.view.get()) != null) {
                    bVar.getViewContext().getActivity().runOnUiThread(new Runnable() { // from class: com.instabug.bug.view.e.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            bVar.mo406i();
                            switch (AnonymousClass3.f251a[C0504e.this.f245b.ordinal()]) {
                                case 1:
                                    C0504e.this.mo432g();
                                    return;
                                case 2:
                                    C0504e.this.mo430e();
                                    return;
                                case 3:
                                    C0504e.this.mo429d();
                                    return;
                                default:
                                    return;
                            }
                        }
                    });
                }
            }
        }));
    }

    /* compiled from: BugReportingFragmentPresenter.java */
    /* renamed from: com.instabug.bug.view.e$3  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass3 {

        /* renamed from: a */
        static final /* synthetic */ int[] f251a = new int[a.values().length];

        static {
            try {
                f251a[a.SEND_BUG.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f251a[a.TAKE_EXTRA_SCREENSHOT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f251a[a.RECORD_VIDEO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}
