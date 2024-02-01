package com.instabug.library.screenshot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import com.instabug.library.C0577R;
import com.instabug.library.C0634c;
import com.instabug.library.instacapture.C0650b;
import com.instabug.library.instacapture.p026b.C0652b;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.OrientationUtils;

/* compiled from: ScreenshotProvider.java */
/* renamed from: com.instabug.library.screenshot.a */
/* loaded from: classes.dex */
public class C0735a {

    /* compiled from: ScreenshotProvider.java */
    /* renamed from: com.instabug.library.screenshot.a$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo1234a(Bitmap bitmap);

        /* renamed from: a */
        void mo1235a(Throwable th);
    }

    /* renamed from: a */
    public static synchronized void m1646a(Activity activity, final a aVar) {
        synchronized (C0735a.class) {
            InstabugSDKLogger.m1803v(C0735a.class, "start capture screenshot, time in MS: " + System.currentTimeMillis());
            if (activity != null && !activity.isFinishing()) {
                C0650b.m1273a(activity).m1277a(new C0652b() { // from class: com.instabug.library.screenshot.a.1
                    @Override // com.instabug.library.instacapture.p026b.C0652b, com.instabug.library.instacapture.p026b.InterfaceC0651a
                    /* renamed from: a */
                    public void mo1280a(Bitmap bitmap) {
                        a.this.mo1234a(bitmap);
                    }

                    @Override // com.instabug.library.instacapture.p026b.C0652b, com.instabug.library.instacapture.p026b.InterfaceC0651a
                    /* renamed from: a */
                    public void mo1281a(Throwable th) {
                        a.this.mo1235a(th);
                    }
                }, C0577R.id.instabug_decor_view, C0577R.id.instabug_extra_screenshot_button, C0577R.id.instabug_floating_button, C0577R.id.instabug_in_app_notification, C0577R.id.instabug_intro_dialog);
            }
        }
    }

    /* renamed from: a */
    public static void m1647a(final Activity activity, final C0634c.a[] aVarArr) {
        InstabugSDKLogger.m1803v(C0735a.class, "start capture screenshot as video frame, time in MS: " + System.currentTimeMillis());
        if (activity != null && !activity.isFinishing()) {
            OrientationUtils.lockScreenOrientation(activity);
            C0650b.m1273a(activity).m1277a(new C0652b() { // from class: com.instabug.library.screenshot.a.2
                @Override // com.instabug.library.instacapture.p026b.C0652b, com.instabug.library.instacapture.p026b.InterfaceC0651a
                /* renamed from: a */
                public void mo1280a(Bitmap bitmap) {
                    if (aVarArr != null) {
                        Paint paint = new Paint(1);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(-1711341568);
                        Canvas canvas = new Canvas(bitmap);
                        for (C0634c.a aVar : aVarArr) {
                            canvas.drawCircle(aVar.m1189a(), aVar.m1190b(), 30.0f, paint);
                        }
                    }
                    BitmapUtils.saveBitmap(bitmap, AttachmentsUtility.getVideoRecordingFramesDirectory(activity), new BitmapUtils.InterfaceC0751a() { // from class: com.instabug.library.screenshot.a.2.1
                        @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
                        /* renamed from: a */
                        public void mo1236a(Uri uri) {
                            InstabugSDKLogger.m1803v(C0735a.class, "capture screenshot as video frame done successfully, videoFrameUri :" + uri.getPath() + ", time in MS: " + System.currentTimeMillis());
                        }

                        @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
                        /* renamed from: a */
                        public void mo1237a(Throwable th) {
                            InstabugSDKLogger.m1801e(C0735a.class, "capture screenshot as video frame got error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
                        }
                    });
                }

                @Override // com.instabug.library.instacapture.p026b.C0652b, com.instabug.library.instacapture.p026b.InterfaceC0651a
                /* renamed from: a */
                public void mo1281a(Throwable th) {
                    InstabugSDKLogger.m1801e(C0735a.class, "capture screenshot as video frame got error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
                }
            }, C0577R.id.instabug_decor_view, C0577R.id.instabug_extra_screenshot_button, C0577R.id.instabug_floating_button, C0577R.id.instabug_in_app_notification, C0577R.id.instabug_video_mute_button, C0577R.id.instabug_video_stop_button);
        }
    }
}
