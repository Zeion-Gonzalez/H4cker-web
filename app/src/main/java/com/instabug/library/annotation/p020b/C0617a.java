package com.instabug.library.annotation.p020b;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.ColorInt;
import com.instabug.library.annotation.C0608a;
import com.instabug.library.annotation.C0616b;
import com.instabug.library.annotation.p021c.C0627b;

/* compiled from: ArrowShape.java */
/* renamed from: com.instabug.library.annotation.b.a */
/* loaded from: classes.dex */
public class C0617a extends AbstractC0623g {

    /* renamed from: a */
    String f742a;

    /* renamed from: b */
    private final Paint f743b;

    /* renamed from: e */
    private final PointF f744e;

    /* renamed from: f */
    private final PointF f745f;

    public C0617a(PointF pointF, PointF pointF2, @ColorInt int i, float f) {
        super(i, f);
        this.f743b = new Paint(1);
        this.f743b.setColor(i);
        this.f743b.setStyle(Paint.Style.STROKE);
        this.f743b.setStrokeWidth(f);
        this.f744e = pointF;
        this.f745f = pointF2;
    }

    /* renamed from: a */
    public void m1102a(String str) {
        this.f742a = str;
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1098a(Canvas canvas, C0616b c0616b, C0616b c0616b2) {
        if (c0616b.f732a == C0616b.a.RIGHT) {
            this.f744e.x = c0616b.right;
        } else if (c0616b.f732a == C0616b.a.LEFT) {
            this.f744e.x = c0616b.left;
        }
        if (c0616b.f733b == C0616b.b.TOP) {
            this.f744e.y = c0616b.top;
        } else if (c0616b.f733b == C0616b.b.BOTTOM) {
            this.f744e.y = c0616b.bottom;
        }
        if (c0616b.f734c == C0616b.a.RIGHT) {
            this.f745f.x = c0616b.right;
        } else if (c0616b.f734c == C0616b.a.LEFT) {
            this.f745f.x = c0616b.left;
        }
        if (c0616b.f735d == C0616b.b.TOP) {
            this.f745f.y = c0616b.top;
        } else if (c0616b.f735d == C0616b.b.BOTTOM) {
            this.f745f.y = c0616b.bottom;
        }
        canvas.drawPath(mo1095a(c0616b), this.f743b);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public boolean mo1103a(PointF pointF, C0616b c0616b) {
        m1094b(c0616b);
        float m1142a = C0627b.m1142a(this.f745f.x, this.f745f.y, this.f744e.x, this.f744e.y);
        PointF m1144a = C0627b.m1144a(60.0f, m1142a + 90.0f, this.f744e);
        PointF m1144a2 = C0627b.m1144a(60.0f, m1142a + 270.0f, this.f744e);
        PointF m1144a3 = C0627b.m1144a(60.0f, m1142a + 270.0f, this.f745f);
        PointF m1144a4 = C0627b.m1144a(60.0f, m1142a + 90.0f, this.f745f);
        Region region = new Region();
        RectF rectF = new RectF();
        Path path = new Path();
        path.moveTo(m1144a.x, m1144a.y);
        path.lineTo(m1144a2.x, m1144a2.y);
        path.lineTo(m1144a3.x, m1144a3.y);
        path.lineTo(m1144a4.x, m1144a4.y);
        path.close();
        path.computeBounds(rectF, true);
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return region.contains((int) pointF.x, (int) pointF.y);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1101a(C0616b c0616b, C0616b c0616b2, boolean z) {
        c0616b2.m1087a(c0616b);
    }

    /* renamed from: a */
    public void m1096a(float f, float f2, C0616b c0616b) {
        this.f744e.set(f, f2);
        m1094b(c0616b);
    }

    /* renamed from: b */
    public void m1104b(float f, float f2, C0616b c0616b) {
        this.f745f.set(f, f2);
        m1094b(c0616b);
    }

    /* renamed from: b */
    private void m1094b(C0616b c0616b) {
        if (this.f744e.x < this.f745f.x) {
            c0616b.left = this.f744e.x;
            c0616b.right = this.f745f.x;
            c0616b.f732a = C0616b.a.LEFT;
            c0616b.f734c = C0616b.a.RIGHT;
        } else {
            c0616b.right = this.f744e.x;
            c0616b.left = this.f745f.x;
            c0616b.f732a = C0616b.a.RIGHT;
            c0616b.f734c = C0616b.a.LEFT;
        }
        if (this.f744e.y < this.f745f.y) {
            c0616b.top = this.f744e.y;
            c0616b.bottom = this.f745f.y;
            c0616b.f733b = C0616b.b.TOP;
            c0616b.f735d = C0616b.b.BOTTOM;
            return;
        }
        c0616b.bottom = this.f744e.y;
        c0616b.top = this.f745f.y;
        c0616b.f733b = C0616b.b.BOTTOM;
        c0616b.f735d = C0616b.b.TOP;
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1099a(Canvas canvas, C0616b c0616b, C0608a[] c0608aArr) {
        int color = this.f743b.getColor();
        c0608aArr[0].m1069a(this.f744e);
        c0608aArr[1].m1069a(this.f745f);
        for (int i = 0; i < 2; i++) {
            c0608aArr[i].m1067a(color);
            c0608aArr[i].m1068a(canvas);
        }
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1097a(Canvas canvas, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4) {
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1100a(C0616b c0616b, C0616b c0616b2, int i, int i2) {
        c0616b.left = c0616b2.left + i;
        c0616b.top = c0616b2.top + i2;
        c0616b.right = c0616b2.right + i;
        c0616b.bottom = c0616b2.bottom + i2;
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public Path mo1095a(C0616b c0616b) {
        Path path = new Path();
        float m1142a = C0627b.m1142a(this.f745f.x, this.f745f.y, this.f744e.x, this.f744e.y);
        PointF m1144a = C0627b.m1144a(60.0f, 225.0f + m1142a, this.f745f);
        PointF m1144a2 = C0627b.m1144a(60.0f, m1142a + 135.0f, this.f745f);
        path.moveTo(this.f744e.x, this.f744e.y);
        path.lineTo(this.f745f.x + 1.0f, this.f745f.y + 1.0f);
        if ("arrow".equals(this.f742a)) {
            path.moveTo(m1144a.x, m1144a.y);
            path.lineTo(this.f745f.x, this.f745f.y);
            path.lineTo(m1144a2.x, m1144a2.y);
        }
        return path;
    }
}
