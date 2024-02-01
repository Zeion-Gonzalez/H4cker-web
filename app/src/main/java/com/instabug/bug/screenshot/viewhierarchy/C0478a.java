package com.instabug.bug.screenshot.viewhierarchy;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import com.instabug.bug.C0468d;
import com.instabug.bug.model.Bug;
import com.instabug.bug.screenshot.viewhierarchy.C0480c;
import com.instabug.bug.screenshot.viewhierarchy.utilities.BitmapUtils;
import com.instabug.bug.screenshot.viewhierarchy.utilities.ViewHierarchyDiskUtils;
import com.instabug.bug.screenshot.viewhierarchy.utilities.ViewHierarchyInspectorEventBus;
import com.instabug.library.C0577R;
import com.instabug.library.instacapture.screenshot.FieldHelper;
import com.instabug.library.instacapture.screenshot.RootViewInfo;
import com.instabug.library.internal.storage.DiskUtils;
import com.instabug.library.model.Attachment;
import com.instabug.library.util.InstabugMemoryUtils;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.android.schedulers.AndroidSchedulers;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Func1;
import p045rx.schedulers.Schedulers;

/* compiled from: ActivityViewInspector.java */
/* renamed from: com.instabug.bug.screenshot.viewhierarchy.a */
/* loaded from: classes.dex */
public class C0478a {

    /* renamed from: a */
    private static Subscription f121a;

    /* renamed from: a */
    public static synchronized void m172a(final Activity activity) {
        synchronized (C0478a.class) {
            if (C0468d.m86a().m103d() != null) {
                C0468d.m86a().m103d().m114a(Bug.ViewHierarchyInspectionState.IN_PROGRESS);
            }
            final a aVar = new a();
            ViewHierarchyInspectorEventBus.getInstance().post(C0480c.a.STARTED);
            InstabugSDKLogger.m1803v(C0478a.class, "inspect activity view start, time in MS: " + System.currentTimeMillis());
            final C0479b c0479b = new C0479b();
            c0479b.m187a(activity.getWindow().getDecorView());
            try {
                c0479b.m196b(C0480c.m216a(activity, m173b(activity)));
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(C0478a.class, "inspect activity frame got error" + e.getMessage() + ", time in MS: " + System.currentTimeMillis(), e);
            }
            List<RootViewInfo> rootViews = FieldHelper.getRootViews(activity, new int[]{C0577R.id.instabug_decor_view, C0577R.id.instabug_in_app_notification, C0577R.id.instabug_intro_dialog});
            InstabugSDKLogger.m1803v(C0478a.class, "root views size: " + rootViews.size());
            if (rootViews.size() > 0) {
                c0479b.m191a(true);
            }
            Observable[] observableArr = new Observable[rootViews.size()];
            for (int i = 0; i < rootViews.size(); i++) {
                C0479b c0479b2 = new C0479b();
                c0479b2.m189a(String.valueOf(i));
                c0479b2.m187a(rootViews.get(i).getView());
                c0479b2.m197b(true);
                c0479b2.m183a(m173b(activity));
                observableArr[i] = C0480c.m217a(c0479b2);
            }
            if (f121a != null && !f121a.isUnsubscribed()) {
                f121a.unsubscribe();
            }
            f121a = Observable.merge(observableArr).concatMap(new Func1<C0479b, Observable<C0479b>>() { // from class: com.instabug.bug.screenshot.viewhierarchy.a.7
                @Override // p045rx.functions.Func1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public Observable<C0479b> call(C0479b c0479b3) {
                    C0479b.this.m194b(c0479b3);
                    if (InstabugMemoryUtils.isLowMemory()) {
                        return null;
                    }
                    return Observable.from(C0480c.m222b(c0479b3));
                }
            }).concatMap(new Func1<C0479b, Observable<C0479b>>() { // from class: com.instabug.bug.screenshot.viewhierarchy.a.6
                @Override // p045rx.functions.Func1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public Observable<C0479b> call(C0479b c0479b3) {
                    if (InstabugMemoryUtils.isLowMemory()) {
                        return null;
                    }
                    return BitmapUtils.captureViewHierarchyRx(c0479b3).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.m2140io());
                }
            }).doOnNext(new Action1<C0479b>() { // from class: com.instabug.bug.screenshot.viewhierarchy.a.5
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(C0479b c0479b3) {
                    InstabugSDKLogger.m1803v(C0478a.class, "doOnNext called, time in MS: " + System.currentTimeMillis());
                    if (a.this.m181c() && c0479b3.m206j() != null) {
                        InstabugSDKLogger.m1803v(C0478a.class, "viewHierarchy image not equal null, starting save image on disk, viewHierarchyId: " + c0479b3.m182a() + ", time in MS: " + System.currentTimeMillis());
                        ViewHierarchyDiskUtils.saveViewHierarchyImage(c0479b3);
                        c0479b3.m207k();
                    }
                }
            }).doOnCompleted(new Action0() { // from class: com.instabug.bug.screenshot.viewhierarchy.a.4
                @Override // p045rx.functions.Action0
                public void call() {
                    InstabugSDKLogger.m1803v(C0478a.class, "doOnCompleted called, time in MS: " + System.currentTimeMillis());
                    if (a.this.m181c()) {
                        Uri zipViewHierarchyImages = ViewHierarchyDiskUtils.zipViewHierarchyImages(c0479b);
                        if (zipViewHierarchyImages != null) {
                            InstabugSDKLogger.m1803v(C0478a.class, "viewHierarchy images zipped successfully, zip file uri: " + zipViewHierarchyImages.toString() + ", time in MS: " + System.currentTimeMillis());
                        }
                        if (C0468d.m86a().m103d() != null && zipViewHierarchyImages != null) {
                            C0468d.m86a().m103d().m112a(zipViewHierarchyImages, Attachment.Type.VIEW_HIERARCHY);
                        }
                    }
                }
            }).doOnSubscribe(new Action0() { // from class: com.instabug.bug.screenshot.viewhierarchy.a.3
                @Override // p045rx.functions.Action0
                public void call() {
                    InstabugSDKLogger.m1803v(C0478a.class, "subscribe called, time in MS: " + System.currentTimeMillis());
                    a.this.m179a();
                }
            }).doOnUnsubscribe(new Action0() { // from class: com.instabug.bug.screenshot.viewhierarchy.a.2
                @Override // p045rx.functions.Action0
                public void call() {
                    InstabugSDKLogger.m1803v(C0478a.class, "un-subscribe called, time in MS: " + System.currentTimeMillis());
                    a.this.m180b();
                    if (!a.this.m181c()) {
                        DiskUtils.cleanDirectory(ViewHierarchyDiskUtils.getViewHierarchyImagesDirectory(activity));
                    }
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.m2140io()).subscribe((Subscriber) new Subscriber<C0479b>() { // from class: com.instabug.bug.screenshot.viewhierarchy.a.1
                @Override // p045rx.Observer
                public void onCompleted() {
                    InstabugSDKLogger.m1803v(C0478a.class, "activity view inspection done successfully, time in MS: " + System.currentTimeMillis());
                    if (C0468d.m86a().m103d() != null) {
                        C0468d.m86a().m103d().m130e(C0478a.m174b(C0479b.this).toString());
                        C0468d.m86a().m103d().m114a(Bug.ViewHierarchyInspectionState.DONE);
                    }
                    ViewHierarchyInspectorEventBus.getInstance().post(C0480c.a.COMPLETED);
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                    InstabugSDKLogger.m1801e(C0478a.class, "activity view inspection got error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
                    if (C0468d.m86a().m103d() != null) {
                        C0468d.m86a().m103d().m114a(Bug.ViewHierarchyInspectionState.FAILED);
                    }
                    ViewHierarchyInspectorEventBus.getInstance().post(C0480c.a.FAILED);
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(C0479b c0479b3) {
                    InstabugSDKLogger.m1803v(C0478a.class, "view hierarchy image saved successfully, uri: " + c0479b3.m208l());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public static JSONObject m174b(C0479b c0479b) {
        JSONObject jSONObject = new JSONObject();
        try {
            if (c0479b.m182a() != null) {
                jSONObject.put("id", c0479b.m182a());
            }
            if (c0479b.m192b() != null) {
                jSONObject.put("icon", c0479b.m192b());
            }
            if (c0479b.m198c() != null) {
                jSONObject.put("type", c0479b.m198c());
            }
            if (c0479b.m200d() != null) {
                jSONObject.put("properties", c0479b.m200d());
            }
            if (c0479b.m201e() != null) {
                jSONObject.put("frame", c0479b.m201e());
            }
            if (c0479b.m203g() != null && c0479b.m204h()) {
                JSONArray jSONArray = new JSONArray();
                Iterator<C0479b> it = c0479b.m203g().iterator();
                while (it.hasNext()) {
                    jSONArray.put(m174b(it.next()));
                }
                jSONObject.put("nodes", jSONArray);
            }
        } catch (JSONException e) {
            InstabugSDKLogger.m1801e(C0478a.class, "convert seed view hierarchy to json got json exception: " + e.getMessage() + ", time in MS: " + System.currentTimeMillis(), e);
        }
        return jSONObject;
    }

    /* renamed from: b */
    private static int m173b(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int height = decorView.getHeight() > decorView.getWidth() ? decorView.getHeight() : decorView.getWidth();
        if (height > 640) {
            return height / 640;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ActivityViewInspector.java */
    /* renamed from: com.instabug.bug.screenshot.viewhierarchy.a$a */
    /* loaded from: classes.dex */
    public static class a {

        /* renamed from: a */
        private int f130a;

        private a() {
            this.f130a = 0;
        }

        /* renamed from: a */
        void m179a() {
            this.f130a++;
        }

        /* renamed from: b */
        void m180b() {
            this.f130a--;
        }

        /* renamed from: c */
        boolean m181c() {
            return this.f130a > 0;
        }
    }
}
