package com.instabug.bug.screenshot.viewhierarchy.utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.instabug.bug.screenshot.viewhierarchy.C0479b;
import com.instabug.library.instacapture.screenshot.ScreenshotTaker;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.IllegalFormatCodePointException;
import p045rx.Observable;
import p045rx.functions.Func0;

/* loaded from: classes.dex */
public class BitmapUtils {
    public static Observable<C0479b> captureViewHierarchyRx(final C0479b c0479b) {
        return Observable.defer(new Func0<Observable<C0479b>>() { // from class: com.instabug.bug.screenshot.viewhierarchy.utilities.BitmapUtils.1
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public Observable<C0479b> call() {
                return Observable.just(BitmapUtils.captureViewWithoutChildren(C0479b.this));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static C0479b captureViewWithoutChildren(C0479b c0479b) {
        Bitmap captureView;
        InstabugSDKLogger.m1803v(BitmapUtils.class, "staring capture viewHierarchy: " + c0479b.m182a());
        if (c0479b.m211o() instanceof ViewGroup) {
            boolean[] hideViewChildren = hideViewChildren((ViewGroup) c0479b.m211o());
            Bitmap captureView2 = captureView(c0479b);
            showViewChildren((ViewGroup) c0479b.m211o(), hideViewChildren);
            captureView = captureView2;
        } else {
            captureView = captureView(c0479b);
        }
        c0479b.m184a(captureView);
        InstabugSDKLogger.m1803v(BitmapUtils.class, "capture viewHierarchy done successfully: " + c0479b.m182a());
        return c0479b;
    }

    private static boolean[] hideViewChildren(ViewGroup viewGroup) {
        boolean[] zArr = new boolean[viewGroup.getChildCount()];
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i).getVisibility() == 0) {
                zArr[i] = true;
                viewGroup.getChildAt(i).setVisibility(4);
            } else {
                zArr[i] = false;
            }
        }
        return zArr;
    }

    private static void showViewChildren(ViewGroup viewGroup, boolean[] zArr) {
        for (int i = 0; i < zArr.length; i++) {
            if (zArr[i]) {
                viewGroup.getChildAt(i).setVisibility(0);
            }
        }
    }

    @Nullable
    private static Bitmap captureView(C0479b c0479b) {
        if (c0479b.m211o() == null || c0479b.m209m() == null || c0479b.m211o().getHeight() <= 0 || c0479b.m211o().getWidth() <= 0 || c0479b.m209m().height() <= 0 || c0479b.m209m().width() <= 0) {
            return null;
        }
        return scaleBitmap(cropImageToVisiblyArea(captureView(c0479b.m211o()), c0479b.m209m(), c0479b.m210n()), c0479b.m212p());
    }

    private static Bitmap captureView(View view) {
        Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        view.draw(canvas);
        int[] iArr = {0, 0};
        if (Build.VERSION.SDK_INT >= 14 && (view instanceof TextureView)) {
            ScreenshotTaker.drawTextureView((TextureView) view, iArr, canvas);
        }
        if (view instanceof GLSurfaceView) {
            ScreenshotTaker.drawGLSurfaceView((GLSurfaceView) view, iArr, canvas);
        }
        if (Build.VERSION.SDK_INT >= 11 && (view instanceof WebView)) {
            ScreenshotTaker.drawWebView((WebView) view, canvas);
        }
        return createBitmap;
    }

    @Nullable
    private static Bitmap cropImageToVisiblyArea(Bitmap bitmap, Rect rect, Rect rect2) throws IllegalFormatCodePointException {
        if (bitmap == null || rect == null || rect.width() <= 0 || rect.height() <= 0) {
            return null;
        }
        InstabugSDKLogger.m1803v(BitmapUtils.class, "visible rect: " + rect.toString() + ", original rect" + rect2.toString());
        InstabugSDKLogger.m1803v(BitmapUtils.class, "bitmap width: " + bitmap.getWidth() + ", bitmap height: " + bitmap.getHeight());
        int i = rect.left - rect2.left;
        int i2 = rect.top - rect2.top;
        if (i < 0 || i2 < 0 || rect.width() > bitmap.getWidth() || rect.height() > bitmap.getHeight()) {
            return null;
        }
        try {
            return Bitmap.createBitmap(bitmap, i, i2, rect.width(), rect.height());
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    @Nullable
    private static Bitmap scaleBitmap(Bitmap bitmap, int i) {
        if (bitmap == null || bitmap.getWidth() <= i || bitmap.getHeight() <= i) {
            return null;
        }
        InstabugSDKLogger.m1803v(BitmapUtils.class, "scale: " + i + ", bitmap width: " + bitmap.getWidth() + ", bitmap height: " + bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / i, bitmap.getHeight() / i, false);
    }
}
