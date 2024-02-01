package com.instabug.library.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

/* compiled from: BitmapWorkerTask.java */
/* renamed from: com.instabug.library.util.a */
/* loaded from: classes.dex */
public class AsyncTaskC0753a extends AsyncTask<String, Void, Bitmap> {

    /* renamed from: a */
    private final WeakReference<ImageView> f1251a;

    /* renamed from: b */
    private float f1252b;

    /* renamed from: c */
    private float f1253c;

    /* renamed from: d */
    private boolean f1254d;

    /* renamed from: e */
    private a f1255e;

    /* compiled from: BitmapWorkerTask.java */
    /* renamed from: com.instabug.library.util.a$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo1027a();
    }

    public AsyncTaskC0753a(ImageView imageView) {
        this.f1251a = new WeakReference<>(imageView);
    }

    public AsyncTaskC0753a(ImageView imageView, float f, float f2) {
        this(imageView);
        this.f1252b = f;
        this.f1253c = f2;
        this.f1254d = true;
    }

    public AsyncTaskC0753a(ImageView imageView, a aVar) {
        this(imageView);
        this.f1255e = aVar;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public Bitmap doInBackground(String... strArr) {
        Bitmap decodeSampledBitmapFromLocalPath = BitmapUtils.decodeSampledBitmapFromLocalPath(strArr[0]);
        if (this.f1254d) {
            return BitmapUtils.resizeBitmap(decodeSampledBitmapFromLocalPath, this.f1252b, this.f1253c);
        }
        return decodeSampledBitmapFromLocalPath;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public void onPostExecute(Bitmap bitmap) {
        ImageView imageView;
        if (bitmap != null && (imageView = this.f1251a.get()) != null) {
            imageView.setImageBitmap(bitmap);
            if (this.f1255e != null) {
                this.f1255e.mo1027a();
            }
        }
    }
}
