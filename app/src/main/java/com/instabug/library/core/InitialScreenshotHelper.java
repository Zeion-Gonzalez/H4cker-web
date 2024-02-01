package com.instabug.library.core;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.instabug.library.screenshot.C0735a;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.InstabugSDKLogger;

/* loaded from: classes.dex */
public class InitialScreenshotHelper {

    /* loaded from: classes.dex */
    public interface InitialScreenshotCapturingListener {
        void onScreenshotCapturedSuccessfully(Uri uri);

        void onScreenshotCapturingFailed(Throwable th);
    }

    public static void captureScreenshot(@NonNull final InitialScreenshotCapturingListener initialScreenshotCapturingListener) {
        C0735a.m1646a(InstabugInternalTrackingDelegate.getInstance().getTargetActivity(), new C0735a.a() { // from class: com.instabug.library.core.InitialScreenshotHelper.1
            @Override // com.instabug.library.screenshot.C0735a.a
            /* renamed from: a */
            public void mo1234a(Bitmap bitmap) {
                BitmapUtils.saveBitmap(bitmap, InstabugInternalTrackingDelegate.getInstance().getTargetActivity(), new BitmapUtils.InterfaceC0751a() { // from class: com.instabug.library.core.InitialScreenshotHelper.1.1
                    @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
                    /* renamed from: a */
                    public void mo1236a(Uri uri) {
                        InitialScreenshotCapturingListener.this.onScreenshotCapturedSuccessfully(uri);
                    }

                    @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
                    /* renamed from: a */
                    public void mo1237a(Throwable th) {
                        InstabugSDKLogger.m1801e(this, "initial screenshot capturing got error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
                        InitialScreenshotCapturingListener.this.onScreenshotCapturingFailed(th);
                    }
                });
            }

            @Override // com.instabug.library.screenshot.C0735a.a
            /* renamed from: a */
            public void mo1235a(Throwable th) {
                InstabugSDKLogger.m1801e(this, "initial screenshot capturing got error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
                InitialScreenshotCapturingListener.this.onScreenshotCapturingFailed(th);
            }
        });
    }
}
