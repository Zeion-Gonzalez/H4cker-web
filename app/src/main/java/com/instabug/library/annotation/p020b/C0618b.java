package com.instabug.library.annotation.p020b;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.internal.view.SupportMenu;
import com.instabug.library.Instabug;
import com.instabug.library.annotation.C0608a;
import com.instabug.library.annotation.C0616b;
import com.instabug.library.annotation.p021c.C0626a;
import com.instabug.library.annotation.p021c.C0627b;

/* compiled from: BlurredRectShape.java */
/* renamed from: com.instabug.library.annotation.b.b */
/* loaded from: classes.dex */
public class C0618b extends AbstractC0619c {
    public C0618b(Bitmap bitmap, Context context) {
        super(SupportMenu.CATEGORY_MASK);
        this.f752a = C0626a.m1138a(bitmap, 18, context);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0619c
    /* renamed from: a */
    public void mo1105a(Canvas canvas, Bitmap bitmap, float f, float f2) {
        canvas.drawBitmap(bitmap, f, f2, (Paint) null);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1101a(C0616b c0616b, C0616b c0616b2, boolean z) {
        c0616b2.m1087a(c0616b);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1099a(Canvas canvas, C0616b c0616b, C0608a[] c0608aArr) {
        PointF[] m1092e = c0616b.m1092e();
        for (int i = 0; i < c0608aArr.length; i++) {
            c0608aArr[i].m1069a(m1092e[i]);
            c0608aArr[i].m1067a(Instabug.getPrimaryColor());
            c0608aArr[i].m1068a(canvas);
        }
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1097a(Canvas canvas, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4) {
        C0627b.m1148a(canvas, pointF, pointF2, this.f760c);
        C0627b.m1148a(canvas, pointF, pointF4, this.f760c);
        C0627b.m1148a(canvas, pointF2, pointF3, this.f760c);
        C0627b.m1148a(canvas, pointF3, pointF4, this.f760c);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public Path mo1095a(C0616b c0616b) {
        return null;
    }
}
