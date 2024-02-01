package com.instabug.library.internal.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.util.Arrays;

/* loaded from: classes.dex */
public class AnimatedImageView extends ImageView {

    /* renamed from: a */
    private C0690a[] f915a;

    /* renamed from: b */
    private Drawable f916b;

    /* renamed from: c */
    private final Handler f917c;

    /* renamed from: d */
    private boolean f918d;

    /* renamed from: e */
    private Thread f919e;

    /* renamed from: f */
    private final Runnable f920f;

    /* renamed from: com.instabug.library.internal.view.AnimatedImageView$a */
    /* loaded from: classes.dex */
    public static class C0690a {

        /* renamed from: a */
        public int f922a;

        /* renamed from: b */
        public int f923b;
    }

    public AnimatedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f916b = null;
        this.f917c = new Handler();
        this.f918d = false;
        this.f920f = new Runnable() { // from class: com.instabug.library.internal.view.AnimatedImageView.1
            @Override // java.lang.Runnable
            public void run() {
                if (AnimatedImageView.this.f916b != null) {
                    AnimatedImageView.this.setImageDrawable(AnimatedImageView.this.f916b);
                }
            }
        };
    }

    public AnimatedImageView(Context context) {
        super(context);
        this.f916b = null;
        this.f917c = new Handler();
        this.f918d = false;
        this.f920f = new Runnable() { // from class: com.instabug.library.internal.view.AnimatedImageView.1
            @Override // java.lang.Runnable
            public void run() {
                if (AnimatedImageView.this.f916b != null) {
                    AnimatedImageView.this.setImageDrawable(AnimatedImageView.this.f916b);
                }
            }
        };
    }

    public void setFrames(C0690a[] c0690aArr) {
        this.f915a = (C0690a[]) Arrays.copyOf(c0690aArr, c0690aArr.length);
        try {
            this.f916b = getResources().getDrawable(c0690aArr[0].f922a);
            setImageDrawable(this.f916b);
            m1382a();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            this.f915a = null;
        }
    }

    /* renamed from: a */
    public void m1382a() {
        this.f918d = true;
        if (m1377b()) {
            this.f919e = new Thread(new RunnableC0691b());
            this.f919e.start();
        }
    }

    /* renamed from: b */
    private boolean m1377b() {
        return this.f918d && this.f915a != null && this.f919e == null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.instabug.library.internal.view.AnimatedImageView$b */
    /* loaded from: classes.dex */
    public class RunnableC0691b implements Runnable {
        private RunnableC0691b() {
        }

        @Override // java.lang.Runnable
        public void run() {
            int length = AnimatedImageView.this.f915a.length;
            do {
                for (int i = 0; i < length; i++) {
                    Drawable drawable = AnimatedImageView.this.f916b;
                    AnimatedImageView.this.f916b = AnimatedImageView.this.getResources().getDrawable(AnimatedImageView.this.f915a[i].f922a);
                    if (drawable != null) {
                        drawable.setCallback(null);
                    }
                    try {
                        Thread.sleep(AnimatedImageView.this.f915a[i].f923b);
                        AnimatedImageView.this.f917c.post(AnimatedImageView.this.f920f);
                    } catch (InterruptedException e) {
                    }
                }
            } while (AnimatedImageView.this.f918d);
        }
    }
}
