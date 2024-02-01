package com.instabug.library.annotation.p019a;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import com.instabug.library.annotation.p019a.C0613e;
import com.instabug.library.annotation.p019a.C0615g;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jcodec.codecs.mpeg12.MPEGConst;

/* compiled from: Matcher.java */
/* renamed from: com.instabug.library.annotation.a.a */
/* loaded from: classes.dex */
public class C0609a {

    /* renamed from: a */
    private int f693a;

    /* renamed from: b */
    private int f694b;

    /* renamed from: c */
    private int f695c;

    /* renamed from: d */
    private int f696d;

    /* renamed from: e */
    private int f697e;

    /* renamed from: f */
    private float f698f;

    /* renamed from: g */
    private final Path f699g;

    /* renamed from: h */
    private Paint f700h = new Paint();

    public C0609a(Path path) {
        this.f700h.setStyle(Paint.Style.STROKE);
        this.f700h.setStrokeWidth(2.0f);
        this.f699g = C0614f.m1081a(path);
        m1074a(this.f699g);
    }

    /* renamed from: a */
    private void m1074a(Path path) {
        this.f698f = new PathMeasure(path, false).getLength();
        Bitmap createBitmap = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.f700h.setColor(SupportMenu.CATEGORY_MASK);
        canvas.drawPath(path, this.f700h);
        int[] iArr = new int[784];
        createBitmap.getPixels(iArr, 0, 28, 0, 0, 28, 28);
        for (int i = 0; i < iArr.length; i++) {
            if (iArr[i] == -65536) {
                this.f693a++;
                if (i < iArr.length / 2) {
                    this.f695c++;
                } else {
                    this.f697e++;
                }
                if (i % 28 < 14) {
                    this.f694b++;
                } else {
                    this.f696d++;
                }
            }
        }
    }

    /* renamed from: a */
    private C0615g m1072a(Path path, Path path2) {
        Bitmap createBitmap = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.f700h.setColor(ViewCompat.MEASURED_STATE_MASK);
        canvas.drawPath(path, this.f700h);
        int[] iArr = new int[784];
        int i = 0;
        createBitmap.getPixels(iArr, 0, 28, 0, 0, 28, 28);
        int length = iArr.length;
        int i2 = 0;
        while (i2 < length) {
            int i3 = iArr[i2] == -16777216 ? i + 1 : i;
            i2++;
            i = i3;
        }
        this.f700h.setColor(-2130706433);
        canvas.drawPath(path2, this.f700h);
        int[] iArr2 = new int[784];
        createBitmap.getPixels(iArr2, 0, createBitmap.getWidth(), 0, 0, 28, 28);
        C0615g c0615g = new C0615g();
        float f = 0.0f;
        float f2 = 0.0f;
        int i4 = 0;
        for (int i5 : iArr2) {
            if (i5 == -8355712) {
                i4++;
            } else if (i5 == -2130706433) {
                f += 1.0f;
            } else if (i5 == -16777216) {
                f2 += 1.0f;
            }
        }
        c0615g.f715a = i4;
        c0615g.f720f = f / this.f693a;
        c0615g.f721g = f2 / i;
        c0615g.f717c = ((((1.0f - c0615g.f721g) + 1.0f) - c0615g.f720f) + (i4 / i)) / 3.0f;
        c0615g.f722h = this.f694b;
        c0615g.f723i = this.f695c;
        c0615g.f724j = this.f696d;
        c0615g.f725k = this.f697e;
        c0615g.f726l = this.f698f;
        return c0615g;
    }

    /* renamed from: b */
    private List<C0615g> m1075b(Path path) {
        ArrayList arrayList = new ArrayList();
        Path m1078a = C0610b.m1078a();
        for (int i = 0; i < 36; i++) {
            int i2 = i * 10;
            C0615g m1072a = m1072a(C0614f.m1082a(m1078a, i2), path);
            m1072a.f716b = i2;
            arrayList.add(m1072a);
        }
        return arrayList;
    }

    /* renamed from: a */
    private List<C0615g> m1073a(C0613e.a aVar, int i, int i2, int i3) {
        ArrayList arrayList = new ArrayList();
        for (C0612d c0612d : C0610b.m1079a(aVar)) {
            for (int i4 = i; i4 < i2; i4++) {
                int i5 = i4 * i3;
                C0615g m1072a = m1072a(C0614f.m1082a(c0612d.f706a, i5), this.f699g);
                m1072a.f716b = i5;
                m1072a.f719e = c0612d.f707b;
                arrayList.add(m1072a);
                if (aVar != C0613e.a.OVAL || c0612d.f707b != 0.0f) {
                }
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    public C0615g m1077a(C0613e.a aVar) {
        List<C0615g> m1073a = m1073a(aVar, 0, 18, 10);
        C0615g c0615g = (C0615g) Collections.max(m1073a);
        c0615g.f718d = m1071a(m1073a);
        return c0615g;
    }

    /* renamed from: a */
    public C0615g m1076a() {
        List<C0615g> m1075b = m1075b(this.f699g);
        C0615g c0615g = (C0615g) Collections.max(m1075b);
        if ((c0615g.f716b < 0 || c0615g.f716b > 20) && ((c0615g.f716b > 360 || c0615g.f716b < 340) && ((c0615g.f716b < 160 || c0615g.f716b > 200) && ((c0615g.f716b <= 180 || c0615g.m1084a() != C0615g.a.TOP) && (c0615g.f716b >= 180 || c0615g.m1084a() != C0615g.a.BOTTOM))))) {
            if (c0615g.f716b < 180) {
                c0615g = m1075b.get((c0615g.f716b + MPEGConst.SEQUENCE_ERROR_CODE) / 10);
            } else {
                c0615g = m1075b.get((c0615g.f716b - 180) / 10);
            }
            c0615g.f718d = m1071a(m1075b);
        }
        return c0615g;
    }

    /* renamed from: a */
    private float m1071a(List<C0615g> list) {
        float f = 0.0f;
        Iterator<C0615g> it = list.iterator();
        while (true) {
            float f2 = f;
            if (it.hasNext()) {
                f = it.next().f717c + f2;
            } else {
                return f2 / list.size();
            }
        }
    }
}
