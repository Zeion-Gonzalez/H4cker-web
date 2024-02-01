package com.instabug.library.annotation.p020b;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import com.instabug.library.annotation.C0608a;
import com.instabug.library.annotation.C0616b;
import com.instabug.library.annotation.p021c.C0627b;
import org.jcodec.codecs.mpeg12.MPEGConst;

/* compiled from: RectShape.java */
/* renamed from: com.instabug.library.annotation.b.f */
/* loaded from: classes.dex */
public class C0622f extends AbstractC0623g {

    /* renamed from: a */
    protected int f758a;

    /* renamed from: b */
    protected Path f759b;

    public C0622f(@ColorInt int i, float f, int i2) {
        super(i, f);
        this.f759b = new Path();
        this.f758a = i2;
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1098a(Canvas canvas, C0616b c0616b, C0616b c0616b2) {
        if (m1112b() && !c0616b.f740i) {
            c0616b2.f740i = true;
            float centerX = c0616b.centerX();
            float centerY = c0616b.centerY();
            PointF m1143a = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.left, c0616b.top));
            PointF m1143a2 = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.right, c0616b.top));
            PointF m1143a3 = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.right, c0616b.bottom));
            PointF m1143a4 = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.left, c0616b.bottom));
            c0616b2.f736e.set(m1143a);
            c0616b2.f737f.set(m1143a2);
            c0616b2.f738g.set(m1143a3);
            c0616b2.f739h.set(m1143a4);
        }
        mo1107a(canvas, c0616b);
    }

    /* renamed from: a */
    protected void mo1107a(Canvas canvas, C0616b c0616b) {
        m1111b(canvas, c0616b);
    }

    /* renamed from: b */
    private void m1111b(Canvas canvas, C0616b c0616b) {
        canvas.drawPath(mo1095a(c0616b), this.f761d);
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public boolean mo1103a(PointF pointF, C0616b c0616b) {
        if (!m1112b()) {
            RectF rectF = new RectF(c0616b);
            rectF.inset(50.0f, 50.0f);
            RectF rectF2 = new RectF(c0616b);
            rectF2.inset(-50.0f, -50.0f);
            return rectF2.contains(pointF.x, pointF.y) && !rectF.contains(pointF.x, pointF.y);
        }
        RectF rectF3 = new RectF(pointF.x - 50.0f, pointF.y - 50.0f, pointF.x + 50.0f, pointF.y + 50.0f);
        for (PointF pointF2 : C0627b.m1146a(this.f759b)) {
            if (rectF3.contains(pointF2.x, pointF2.y)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1101a(C0616b c0616b, C0616b c0616b2, boolean z) {
        if (!m1112b() || z) {
            c0616b2.m1087a(c0616b);
        }
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1099a(Canvas canvas, C0616b c0616b, C0608a[] c0608aArr) {
        if (!m1112b()) {
            c0608aArr[0].m1066a(c0616b.left, c0616b.top);
            c0608aArr[1].m1066a(c0616b.right, c0616b.top);
            c0608aArr[2].m1066a(c0616b.right, c0616b.bottom);
            c0608aArr[3].m1066a(c0616b.left, c0616b.bottom);
        } else {
            c0608aArr[0].m1066a(c0616b.f736e.x, c0616b.f736e.y);
            c0608aArr[1].m1066a(c0616b.f737f.x, c0616b.f737f.y);
            c0608aArr[2].m1066a(c0616b.f738g.x, c0616b.f738g.y);
            c0608aArr[3].m1066a(c0616b.f739h.x, c0616b.f739h.y);
        }
        int color = this.f761d.getColor();
        for (int i = 0; i < c0608aArr.length; i++) {
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
        c0616b.f736e.set(c0616b2.f736e.x + i, c0616b2.f736e.y + i2);
        c0616b.f737f.set(c0616b2.f737f.x + i, c0616b2.f737f.y + i2);
        c0616b.f738g.set(c0616b2.f738g.x + i, c0616b2.f738g.y + i2);
        c0616b.f739h.set(c0616b2.f739h.x + i, c0616b2.f739h.y + i2);
        c0616b.left = c0616b2.left + i;
        c0616b.top = c0616b2.top + i2;
        c0616b.right = c0616b2.right + i;
        c0616b.bottom = c0616b2.bottom + i2;
    }

    /* renamed from: a */
    public void m1116a(float f, float f2, C0616b c0616b) {
        if (m1112b()) {
            m1109a(f, f2, c0616b, true);
            m1114c(c0616b);
        }
    }

    /* renamed from: a */
    private void m1109a(float f, float f2, C0616b c0616b, boolean z) {
        c0616b.f736e.set(f, f2);
        float m1139a = (float) C0627b.m1139a(c0616b.f738g, c0616b.f737f, c0616b.f736e);
        if (c0616b.f736e.x < c0616b.f737f.x || c0616b.f736e.y < c0616b.f737f.y) {
            C0627b.m1147a(m1139a, this.f758a, c0616b.f736e, c0616b.f737f);
        } else if (c0616b.f736e.x > c0616b.f737f.x || c0616b.f736e.y > c0616b.f737f.y) {
            C0627b.m1147a(m1139a, this.f758a + MPEGConst.SEQUENCE_ERROR_CODE, c0616b.f736e, c0616b.f737f);
        }
        float m1139a2 = (float) C0627b.m1139a(c0616b.f738g, c0616b.f739h, c0616b.f736e);
        if (c0616b.f736e.y < c0616b.f739h.y || c0616b.f736e.x > c0616b.f739h.x) {
            C0627b.m1147a(m1139a2, this.f758a + 90, c0616b.f736e, c0616b.f739h);
        } else if (c0616b.f736e.y > c0616b.f739h.y || c0616b.f736e.x < c0616b.f739h.x) {
            C0627b.m1147a(m1139a2, this.f758a + 270, c0616b.f736e, c0616b.f739h);
        }
        if (z) {
            m1113c(c0616b.f738g.x, c0616b.f738g.y, c0616b, false);
        }
    }

    /* renamed from: b */
    public void m1117b(float f, float f2, C0616b c0616b) {
        if (m1112b()) {
            m1110b(f, f2, c0616b, true);
            m1114c(c0616b);
        }
    }

    /* renamed from: b */
    private void m1110b(float f, float f2, C0616b c0616b, boolean z) {
        c0616b.f737f.set(f, f2);
        float m1139a = (float) C0627b.m1139a(c0616b.f739h, c0616b.f736e, c0616b.f737f);
        if (c0616b.f737f.x > c0616b.f736e.x || c0616b.f737f.y > c0616b.f736e.y) {
            C0627b.m1147a(m1139a, this.f758a + MPEGConst.SEQUENCE_ERROR_CODE, c0616b.f737f, c0616b.f736e);
        } else if (c0616b.f737f.x < c0616b.f736e.x || c0616b.f737f.y < c0616b.f736e.y) {
            C0627b.m1147a(m1139a, this.f758a, c0616b.f737f, c0616b.f736e);
        }
        float m1139a2 = (float) C0627b.m1139a(c0616b.f739h, c0616b.f738g, c0616b.f737f);
        if (c0616b.f737f.y < c0616b.f738g.y || c0616b.f737f.x > c0616b.f738g.x) {
            C0627b.m1147a(m1139a2, this.f758a + 90, c0616b.f737f, c0616b.f738g);
        } else if (c0616b.f737f.y > c0616b.f738g.y || c0616b.f737f.x < c0616b.f738g.x) {
            C0627b.m1147a(m1139a2, this.f758a + 270, c0616b.f737f, c0616b.f738g);
        }
        if (z) {
            m1115d(c0616b.f739h.x, c0616b.f739h.y, c0616b, false);
        }
    }

    /* renamed from: c */
    public void m1118c(float f, float f2, C0616b c0616b) {
        if (m1112b()) {
            m1113c(f, f2, c0616b, true);
            m1114c(c0616b);
        }
    }

    /* renamed from: c */
    private void m1113c(float f, float f2, C0616b c0616b, boolean z) {
        c0616b.f738g.set(f, f2);
        float m1139a = (float) C0627b.m1139a(c0616b.f736e, c0616b.f737f, c0616b.f738g);
        if (c0616b.f738g.y > c0616b.f737f.y || c0616b.f738g.x < c0616b.f737f.x) {
            C0627b.m1147a(m1139a, this.f758a + 270, c0616b.f738g, c0616b.f737f);
        } else if (c0616b.f738g.y < c0616b.f737f.y || c0616b.f738g.x > c0616b.f737f.x) {
            C0627b.m1147a(m1139a, this.f758a + 90, c0616b.f738g, c0616b.f737f);
        }
        float m1139a2 = (float) C0627b.m1139a(c0616b.f736e, c0616b.f739h, c0616b.f738g);
        if (c0616b.f738g.x > c0616b.f739h.x || c0616b.f738g.y > c0616b.f739h.y) {
            C0627b.m1147a(m1139a2, this.f758a + MPEGConst.SEQUENCE_ERROR_CODE, c0616b.f738g, c0616b.f739h);
        } else if (c0616b.f738g.x < c0616b.f739h.x || c0616b.f738g.y < c0616b.f739h.y) {
            C0627b.m1147a(m1139a2, this.f758a, c0616b.f738g, c0616b.f739h);
        }
        if (z) {
            m1109a(c0616b.f736e.x, c0616b.f736e.y, c0616b, false);
        }
    }

    /* renamed from: d */
    public void m1119d(float f, float f2, C0616b c0616b) {
        if (m1112b()) {
            m1115d(f, f2, c0616b, true);
            m1114c(c0616b);
        }
    }

    /* renamed from: d */
    private void m1115d(float f, float f2, C0616b c0616b, boolean z) {
        c0616b.f739h.set(f, f2);
        float m1139a = (float) C0627b.m1139a(c0616b.f737f, c0616b.f736e, c0616b.f739h);
        if (c0616b.f739h.y > c0616b.f736e.y || c0616b.f739h.x < c0616b.f736e.x) {
            C0627b.m1147a(m1139a, this.f758a + 270, c0616b.f739h, c0616b.f736e);
        } else if (c0616b.f739h.y < c0616b.f736e.y || c0616b.f739h.x > c0616b.f736e.x) {
            C0627b.m1147a(m1139a, this.f758a + 90, c0616b.f739h, c0616b.f736e);
        }
        float m1139a2 = (float) C0627b.m1139a(c0616b.f737f, c0616b.f738g, c0616b.f739h);
        if (c0616b.f739h.x < c0616b.f738g.x || c0616b.f739h.y < c0616b.f738g.y) {
            C0627b.m1147a(m1139a2, this.f758a, c0616b.f739h, c0616b.f738g);
        } else if (c0616b.f739h.x > c0616b.f738g.x || c0616b.f739h.y > c0616b.f738g.y) {
            C0627b.m1147a(m1139a2, this.f758a + MPEGConst.SEQUENCE_ERROR_CODE, c0616b.f739h, c0616b.f738g);
        }
        if (z) {
            m1110b(c0616b.f737f.x, c0616b.f737f.y, c0616b, false);
        }
    }

    /* renamed from: c */
    private void m1114c(C0616b c0616b) {
        RectF rectF = new RectF();
        this.f759b.computeBounds(rectF, true);
        c0616b.set(rectF);
    }

    /* renamed from: b */
    private boolean m1112b() {
        return (this.f758a == 0 || this.f758a == 180 || this.f758a == 90) ? false : true;
    }

    @Override // com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public Path mo1095a(C0616b c0616b) {
        if (m1112b() && !c0616b.f740i) {
            c0616b.f740i = true;
            float centerX = c0616b.centerX();
            float centerY = c0616b.centerY();
            PointF m1143a = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.left, c0616b.top));
            PointF m1143a2 = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.right, c0616b.top));
            PointF m1143a3 = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.right, c0616b.bottom));
            PointF m1143a4 = C0627b.m1143a(centerX, centerY, this.f758a, new PointF(c0616b.left, c0616b.bottom));
            c0616b.f736e.set(m1143a);
            c0616b.f737f.set(m1143a2);
            c0616b.f738g.set(m1143a3);
            c0616b.f739h.set(m1143a4);
        }
        mo1108b(c0616b);
        return this.f759b;
    }

    /* renamed from: b */
    protected void mo1108b(C0616b c0616b) {
        this.f759b.reset();
        if (!m1112b()) {
            this.f759b.addRect(c0616b, Path.Direction.CW);
            return;
        }
        this.f759b.moveTo(c0616b.f736e.x, c0616b.f736e.y);
        this.f759b.lineTo(c0616b.f737f.x, c0616b.f737f.y);
        this.f759b.lineTo(c0616b.f738g.x, c0616b.f738g.y);
        this.f759b.lineTo(c0616b.f739h.x, c0616b.f739h.y);
        this.f759b.close();
    }
}
