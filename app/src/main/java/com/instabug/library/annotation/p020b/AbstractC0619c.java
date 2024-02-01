package com.instabug.library.annotation.p020b;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import com.instabug.library.annotation.C0616b;

/* compiled from: CroppedRectShape.java */
/* renamed from: com.instabug.library.annotation.b.c */
/* loaded from: classes.dex */
public abstract class AbstractC0619c extends AbstractC0623g {

    /* renamed from: a */
    protected Bitmap f752a;

    /* renamed from: a */
    public abstract void mo1105a(Canvas canvas, Bitmap bitmap, float f, float f2);

    public AbstractC0619c(@ColorInt int i) {
        super(i, 0.0f);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1098a(Canvas canvas, C0616b c0616b, C0616b c0616b2) {
        float f;
        float f2;
        if (this.f752a != null) {
            float f3 = c0616b.left;
            float f4 = c0616b.top;
            float width = c0616b.width();
            float height = c0616b.height();
            if (f3 < 0.0f) {
                f = f3 + width;
                f2 = 0.0f;
            } else {
                f = width;
                f2 = f3;
            }
            if (f4 < 0.0f) {
                height = f4 + height;
                f4 = 0.0f;
            }
            if (f2 + f > this.f752a.getWidth()) {
                f = this.f752a.getWidth() - f2;
            }
            if (f4 + height > this.f752a.getHeight()) {
                height = this.f752a.getHeight() - f4;
            }
            if (f > 0.0f && height > 0.0f) {
                Bitmap createBitmap = Bitmap.createBitmap(this.f752a, (int) f2, (int) f4, (int) f, (int) height);
                float f5 = c0616b.left;
                float f6 = c0616b.top;
                if (c0616b.left < 0.0f) {
                    f5 = c0616b.right - createBitmap.getWidth();
                }
                mo1105a(canvas, createBitmap, f5, c0616b.top < 0.0f ? c0616b.bottom - createBitmap.getHeight() : f6);
            }
        }
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public boolean mo1103a(PointF pointF, C0616b c0616b) {
        float a = m1120a() + 20.0f;
        RectF rectF = new RectF(c0616b);
        rectF.inset(-a, -a);
        return rectF.contains((int) pointF.x, (int) pointF.y);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1100a(C0616b c0616b, C0616b c0616b2, int i, int i2) {
        c0616b.left = c0616b2.left + i;
        c0616b.top = c0616b2.top + i2;
        c0616b.right = c0616b2.right + i;
        c0616b.bottom = c0616b2.bottom + i2;
    }
}
