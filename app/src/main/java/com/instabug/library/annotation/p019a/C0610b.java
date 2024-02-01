package com.instabug.library.annotation.p019a;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import com.instabug.library.annotation.p019a.C0613e;
import com.instabug.library.annotation.p021c.C0627b;
import java.util.ArrayList;
import java.util.List;

/* compiled from: PathFactory.java */
/* renamed from: com.instabug.library.annotation.a.b */
/* loaded from: classes.dex */
public class C0610b {

    /* renamed from: a */
    private static List<C0612d> f701a;

    /* renamed from: b */
    private static List<C0612d> f702b;

    /* renamed from: a */
    public static Path m1078a() {
        PointF pointF = new PointF(0.0f, 14.0f);
        PointF pointF2 = new PointF(28.0f, 14.0f);
        float m1142a = C0627b.m1142a(pointF2.x, pointF2.y, pointF.x, pointF.y);
        PointF m1144a = C0627b.m1144a(9.0f, 225.0f + m1142a, pointF2);
        PointF m1144a2 = C0627b.m1144a(9.0f, m1142a + 135.0f, pointF2);
        Path path = new Path();
        path.moveTo(pointF.x, pointF.y);
        path.lineTo(pointF2.x, pointF2.y);
        path.moveTo(m1144a.x, m1144a.y);
        path.lineTo(pointF2.x, pointF2.y);
        path.lineTo(m1144a2.x, m1144a2.y);
        return path;
    }

    /* renamed from: a */
    public static List<C0612d> m1079a(C0613e.a aVar) {
        if (aVar == C0613e.a.RECT) {
            if (f701a != null) {
                return f701a;
            }
            f701a = new ArrayList();
        } else if (aVar == C0613e.a.OVAL) {
            if (f702b != null) {
                return f702b;
            }
            f702b = new ArrayList();
        }
        RectF rectF = new RectF(0.0f, 0.0f, 28.0f, 28.0f);
        for (int i = 0; i < 12; i++) {
            C0612d c0612d = new C0612d();
            rectF.left += 1.0f;
            rectF.right -= 1.0f;
            if (aVar == C0613e.a.RECT) {
                c0612d.f706a.addRect(rectF, Path.Direction.CW);
                f701a.add(c0612d);
            } else if (aVar == C0613e.a.OVAL) {
                c0612d.f706a.addOval(rectF, Path.Direction.CW);
                f702b.add(c0612d);
            }
            c0612d.f707b = rectF.left / 28.0f;
        }
        if (aVar == C0613e.a.RECT) {
            return f701a;
        }
        if (aVar == C0613e.a.OVAL) {
            return f702b;
        }
        return null;
    }
}
