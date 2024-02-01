package com.instabug.library.annotation.p020b;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v4.internal.view.SupportMenu;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.instabug.library.Instabug;
import com.instabug.library.annotation.C0608a;
import com.instabug.library.annotation.C0616b;
import com.instabug.library.annotation.p021c.C0626a;
import com.instabug.library.annotation.p021c.C0627b;

/* compiled from: ZoomedShape.java */
/* renamed from: com.instabug.library.annotation.b.h */
/* loaded from: classes.dex */
public class C0624h extends AbstractC0619c {

    /* renamed from: b */
    private PointF f762b;

    /* renamed from: e */
    private float f763e;

    /* renamed from: f */
    private float f764f;

    /* renamed from: g */
    private float f765g;

    public C0624h(Bitmap bitmap) {
        super(SupportMenu.CATEGORY_MASK);
        this.f752a = bitmap;
    }

    /* renamed from: a */
    public void m1121a(Bitmap bitmap) {
        this.f752a = bitmap;
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0619c
    /* renamed from: a */
    public void mo1105a(Canvas canvas, Bitmap bitmap, float f, float f2) {
        this.f765g = Math.min(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        this.f763e = (bitmap.getWidth() / 2.0f) + f;
        this.f764f = (bitmap.getHeight() / 2.0f) + f2;
        this.f762b = C0627b.m1144a(this.f765g, 45.0f, new PointF(this.f763e, this.f764f));
        canvas.drawBitmap(C0626a.m1136a(C0626a.m1137a(bitmap, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION)), f, f2, (Paint) null);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0619c, com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public boolean mo1103a(PointF pointF, C0616b c0616b) {
        Region region = new Region();
        RectF rectF = new RectF();
        Path path = new Path();
        path.addCircle(this.f763e, this.f764f, this.f765g, Path.Direction.CW);
        path.computeBounds(rectF, true);
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return region.contains((int) pointF.x, (int) pointF.y);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1101a(C0616b c0616b, C0616b c0616b2, boolean z) {
        if (Math.abs(c0616b2.width() - c0616b.width()) < 1.0f && Math.abs(c0616b2.height() - c0616b.height()) < 1.0f) {
            c0616b2.m1087a(c0616b);
            return;
        }
        float max = Math.max(c0616b.width() / c0616b2.width(), c0616b.height() / c0616b2.height());
        Matrix matrix = new Matrix();
        matrix.postScale(max, max, c0616b2.centerX(), c0616b2.centerY());
        matrix.mapRect(c0616b2);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1099a(Canvas canvas, C0616b c0616b, C0608a[] c0608aArr) {
        c0608aArr[2].m1069a(this.f762b);
        c0608aArr[2].m1067a(Instabug.getPrimaryColor());
        c0608aArr[2].m1068a(canvas);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1097a(Canvas canvas, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4) {
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public Path mo1095a(C0616b c0616b) {
        return null;
    }
}
