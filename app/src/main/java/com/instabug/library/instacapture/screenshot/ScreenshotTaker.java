package com.instabug.library.instacapture.screenshot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.instabug.library.instacapture.p027c.C0653a;
import com.instabug.library.instacapture.p027c.C0654b;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes.dex */
public final class ScreenshotTaker {
    private ScreenshotTaker() {
    }

    public static Bitmap getScreenshotBitmap(Activity activity, @IdRes @Nullable int[] iArr) {
        Bitmap createBitmap;
        if (activity == null) {
            throw new IllegalArgumentException("Parameter activity cannot be null.");
        }
        View decorView = activity.getWindow().getDecorView();
        List<RootViewInfo> rootViews = FieldHelper.getRootViews(activity, iArr);
        C0653a.m1282a("viewRoots count: " + rootViews.size());
        try {
            if (decorView.getWidth() * decorView.getHeight() * 4 < C0654b.m1285a(activity)) {
                createBitmap = Bitmap.createBitmap(decorView.getWidth(), decorView.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                createBitmap = Bitmap.createBitmap(decorView.getWidth(), decorView.getHeight(), Bitmap.Config.RGB_565);
            }
            drawRootsToBitmap(rootViews, createBitmap, iArr);
            return createBitmap;
        } catch (IllegalArgumentException | OutOfMemoryError e) {
            return null;
        }
    }

    private static void drawRootsToBitmap(List<RootViewInfo> list, Bitmap bitmap, @IdRes @Nullable int[] iArr) {
        Iterator<RootViewInfo> it = list.iterator();
        while (it.hasNext()) {
            drawRootToBitmap(it.next(), bitmap, iArr);
        }
    }

    private static void drawRootToBitmap(RootViewInfo rootViewInfo, Bitmap bitmap, @IdRes @Nullable int[] iArr) {
        int[] iArr2;
        if ((rootViewInfo.getLayoutParams().flags & 2) == 2) {
            new Canvas(bitmap).drawARGB((int) (255.0f * rootViewInfo.getLayoutParams().dimAmount), 0, 0, 0);
        }
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(rootViewInfo.getLeft(), rootViewInfo.getTop());
        if (iArr == null) {
            iArr2 = null;
        } else {
            iArr2 = new int[iArr.length];
        }
        if (iArr != null) {
            for (int i = 0; i < iArr.length; i++) {
                View findViewById = rootViewInfo.getView().findViewById(iArr[i]);
                if (findViewById != null) {
                    iArr2[i] = findViewById.getVisibility();
                    findViewById.setVisibility(4);
                }
            }
        }
        rootViewInfo.getView().draw(canvas);
        drawUnDrawableViews(rootViewInfo.getView(), canvas);
        if (iArr != null) {
            for (int i2 = 0; i2 < iArr.length; i2++) {
                View findViewById2 = rootViewInfo.getView().findViewById(iArr[i2]);
                if (findViewById2 != null) {
                    findViewById2.setVisibility(iArr2[i2]);
                }
            }
        }
    }

    private static ArrayList<View> drawUnDrawableViews(View view, Canvas canvas) {
        if (!(view instanceof ViewGroup)) {
            ArrayList<View> arrayList = new ArrayList<>();
            arrayList.add(view);
            return arrayList;
        }
        ArrayList<View> arrayList2 = new ArrayList<>();
        ViewGroup viewGroup = (ViewGroup) view;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= viewGroup.getChildCount()) {
                return arrayList2;
            }
            View childAt = viewGroup.getChildAt(i2);
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(view);
            arrayList3.addAll(drawUnDrawableViews(childAt, canvas));
            int[] iArr = new int[2];
            childAt.getLocationOnScreen(iArr);
            if (Build.VERSION.SDK_INT >= 14 && (childAt instanceof TextureView)) {
                drawTextureView((TextureView) childAt, iArr, canvas);
            }
            if (childAt instanceof GLSurfaceView) {
                drawGLSurfaceView((GLSurfaceView) childAt, iArr, canvas);
            }
            if (Build.VERSION.SDK_INT >= 11 && (childAt instanceof WebView)) {
                drawWebView((WebView) childAt, canvas);
            }
            arrayList2.addAll(arrayList3);
            i = i2 + 1;
        }
    }

    public static void drawGLSurfaceView(GLSurfaceView gLSurfaceView, int[] iArr, Canvas canvas) {
        C0653a.m1282a("Drawing GLSurfaceView");
        if (gLSurfaceView.getWindowToken() != null) {
            final int width = gLSurfaceView.getWidth();
            final int height = gLSurfaceView.getHeight();
            int[] iArr2 = new int[(height + 0) * width];
            final IntBuffer wrap = IntBuffer.wrap(iArr2);
            wrap.position(0);
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            gLSurfaceView.queueEvent(new Runnable() { // from class: com.instabug.library.instacapture.screenshot.ScreenshotTaker.1
                @Override // java.lang.Runnable
                public void run() {
                    EGL10 egl10 = (EGL10) EGLContext.getEGL();
                    egl10.eglWaitGL();
                    GL10 gl10 = (GL10) egl10.eglGetCurrentContext().getGL();
                    gl10.glFinish();
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gl10.glReadPixels(0, 0, width, height + 0, 6408, 5121, wrap);
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int[] iArr3 = new int[width * height];
            int i = 0;
            int i2 = 0;
            while (i2 < height) {
                for (int i3 = 0; i3 < width; i3++) {
                    int i4 = iArr2[(i2 * width) + i3];
                    iArr3[(((height - i) - 1) * width) + i3] = (i4 & (-16711936)) | ((i4 << 16) & 16711680) | ((i4 >> 16) & 255);
                }
                i2++;
                i++;
            }
            Bitmap createBitmap = Bitmap.createBitmap(iArr3, width, height, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
            canvas.drawBitmap(createBitmap, iArr[0], iArr[1], paint);
            createBitmap.recycle();
        }
    }

    @TargetApi(14)
    public static void drawTextureView(TextureView textureView, int[] iArr, Canvas canvas) {
        C0653a.m1282a("Drawing TextureView");
        Bitmap bitmap = textureView.getBitmap();
        if (bitmap != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
            canvas.drawBitmap(bitmap, iArr[0], iArr[1], paint);
            bitmap.recycle();
        }
    }

    @TargetApi(11)
    public static void drawWebView(WebView webView, Canvas canvas) {
        int layerType = webView.getLayerType();
        if (layerType == 2) {
            webView.setLayerType(0, null);
            webView.setDrawingCacheEnabled(true);
            webView.buildDrawingCache(true);
            Bitmap drawingCache = webView.getDrawingCache();
            if (drawingCache != null) {
                Paint paint = new Paint();
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
                webView.getLocationOnScreen(new int[2]);
                canvas.drawBitmap(drawingCache, r3[0], r3[1], paint);
                drawingCache.recycle();
            }
            webView.setDrawingCacheEnabled(false);
            webView.setLayerType(layerType, null);
        }
    }
}
