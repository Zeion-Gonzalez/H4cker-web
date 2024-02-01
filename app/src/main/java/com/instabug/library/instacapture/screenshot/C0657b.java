package com.instabug.library.instacapture.screenshot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.library.instacapture.p025a.C0649b;
import p045rx.Observable;
import p045rx.functions.Func0;

/* compiled from: ViewsBitmapObservable.java */
/* renamed from: com.instabug.library.instacapture.screenshot.b */
/* loaded from: classes.dex */
public final class C0657b {
    @NonNull
    /* renamed from: a */
    public static Observable<Bitmap> m1287a(@NonNull final Activity activity, @IdRes @Nullable final int[] iArr) {
        return Observable.defer(new Func0<Observable<Bitmap>>() { // from class: com.instabug.library.instacapture.screenshot.b.1
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public Observable<Bitmap> call() {
                Bitmap screenshotBitmap = ScreenshotTaker.getScreenshotBitmap(activity, iArr);
                return screenshotBitmap != null ? Observable.just(screenshotBitmap) : Observable.error(new C0649b());
            }
        });
    }
}
