package com.instabug.chat.p008c;

import android.content.Context;
import android.net.Uri;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.p011ui.C0540a;
import com.instabug.library.screenshot.ExtraScreenshotHelper;
import com.instabug.library.util.InstabugSDKLogger;
import java.lang.ref.WeakReference;

/* compiled from: ScreenshotHelper.java */
/* renamed from: com.instabug.chat.c.a */
/* loaded from: classes.dex */
public class C0513a implements ExtraScreenshotHelper.OnCaptureListener {

    /* renamed from: a */
    private static C0513a f315a;

    /* renamed from: b */
    private WeakReference<Context> f316b;

    /* renamed from: c */
    private ExtraScreenshotHelper f317c = new ExtraScreenshotHelper();

    /* renamed from: d */
    private String f318d;

    private C0513a() {
    }

    /* renamed from: a */
    public static C0513a m538a() {
        if (f315a == null) {
            f315a = new C0513a();
        }
        return f315a;
    }

    /* renamed from: a */
    public void m541a(Context context, String str) {
        this.f316b = new WeakReference<>(context);
        this.f318d = str;
        this.f317c.init(this);
    }

    @Override // com.instabug.library.screenshot.ExtraScreenshotHelper.OnCaptureListener
    public void onExtraScreenshotCaptured(Uri uri) {
        Context context;
        InstabugSDKLogger.m1803v(C0513a.class, "Uri: " + uri);
        this.f317c.release();
        if (this.f316b != null && (context = this.f316b.get()) != null) {
            m540a(context, this.f318d, m539a(uri));
        }
    }

    /* renamed from: a */
    private void m540a(Context context, String str, Attachment attachment) {
        context.startActivity(C0540a.m788a(context, str, attachment));
    }

    /* renamed from: a */
    private Attachment m539a(Uri uri) {
        Attachment attachment = new Attachment();
        attachment.setState(Attachment.STATE_OFFLINE);
        attachment.setType(Attachment.TYPE_IMAGE).setLocalPath(uri.getPath()).setName(uri.getLastPathSegment());
        return attachment;
    }
}
