package com.instabug.bug.screenshot;

import android.content.Context;
import android.net.Uri;
import com.instabug.bug.C0461b;
import com.instabug.bug.C0468d;
import com.instabug.library.model.Attachment;
import com.instabug.library.screenshot.ExtraScreenshotHelper;
import com.instabug.library.util.InstabugSDKLogger;
import java.lang.ref.WeakReference;

/* compiled from: ScreenshotHelper.java */
/* renamed from: com.instabug.bug.screenshot.a */
/* loaded from: classes.dex */
public class C0477a implements ExtraScreenshotHelper.OnCaptureListener {

    /* renamed from: a */
    private static C0477a f118a;

    /* renamed from: b */
    private WeakReference<Context> f119b;

    /* renamed from: c */
    private ExtraScreenshotHelper f120c = new ExtraScreenshotHelper();

    private C0477a() {
    }

    /* renamed from: a */
    public static C0477a m168a() {
        if (f118a == null) {
            f118a = new C0477a();
        }
        return f118a;
    }

    /* renamed from: a */
    public void m170a(Context context) {
        this.f119b = new WeakReference<>(context);
        this.f120c.init(this);
    }

    @Override // com.instabug.library.screenshot.ExtraScreenshotHelper.OnCaptureListener
    public void onExtraScreenshotCaptured(Uri uri) {
        Context context;
        InstabugSDKLogger.m1803v(C0477a.class, "Uri: " + uri);
        this.f120c.release();
        C0468d.m86a().m103d().m112a(uri, Attachment.Type.IMAGE);
        if (this.f119b != null && (context = this.f119b.get()) != null) {
            m169b(context);
        }
    }

    /* renamed from: b */
    private void m169b(Context context) {
        context.startActivity(C0461b.m53c(context));
    }
}
