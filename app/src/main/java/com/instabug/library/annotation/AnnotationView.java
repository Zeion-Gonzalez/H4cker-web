package com.instabug.library.annotation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.instabug.library.annotation.p019a.C0611c;
import com.instabug.library.annotation.p019a.C0613e;
import com.instabug.library.annotation.p020b.AbstractC0623g;
import com.instabug.library.annotation.p020b.C0617a;
import com.instabug.library.annotation.p020b.C0618b;
import com.instabug.library.annotation.p020b.C0620d;
import com.instabug.library.annotation.p020b.C0621e;
import com.instabug.library.annotation.p020b.C0622f;
import com.instabug.library.annotation.p020b.C0624h;
import com.instabug.library.annotation.p021c.C0627b;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jcodec.codecs.mpeg12.MPEGConst;

/* loaded from: classes.dex */
public class AnnotationView extends ImageView {

    /* renamed from: A */
    private InterfaceC0598f f606A;

    /* renamed from: B */
    private InterfaceC0599g f607B;

    /* renamed from: C */
    private boolean f608C;

    /* renamed from: D */
    private AbstractC0623g f609D;

    /* renamed from: E */
    private C0616b f610E;

    /* renamed from: a */
    int f611a;

    /* renamed from: b */
    private final GestureDetector f612b;

    /* renamed from: c */
    private Path f613c;

    /* renamed from: d */
    private List<PointF> f614d;

    /* renamed from: e */
    private Paint f615e;

    /* renamed from: f */
    private int f616f;

    /* renamed from: g */
    private LinkedHashMap<Path, Integer> f617g;

    /* renamed from: h */
    private float f618h;

    /* renamed from: i */
    private float f619i;

    /* renamed from: j */
    private boolean f620j;

    /* renamed from: k */
    private Drawable f621k;

    /* renamed from: l */
    private PointF[] f622l;

    /* renamed from: m */
    private Bitmap f623m;

    /* renamed from: n */
    private int f624n;

    /* renamed from: o */
    private boolean f625o;

    /* renamed from: p */
    private Paint f626p;

    /* renamed from: q */
    private C0608a f627q;

    /* renamed from: r */
    private C0608a f628r;

    /* renamed from: s */
    private C0608a f629s;

    /* renamed from: t */
    private C0608a f630t;

    /* renamed from: u */
    private PointF f631u;

    /* renamed from: v */
    private EnumC0593a f632v;

    /* renamed from: w */
    private EnumC0594b f633w;

    /* renamed from: x */
    private C0628d f634x;

    /* renamed from: y */
    private C0625c f635y;

    /* renamed from: z */
    private InterfaceC0597e f636z;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.instabug.library.annotation.AnnotationView$a */
    /* loaded from: classes.dex */
    public enum EnumC0593a {
        NONE,
        DRAG,
        RESIZE_BY_TOP_LEFT_BUTTON,
        RESIZE_BY_TOP_RIGHT_BUTTON,
        RESIZE_BY_BOTTOM_RIGHT_BUTTON,
        RESIZE_BY_BOTTOM_LEFT_BUTTON,
        DRAW
    }

    /* renamed from: com.instabug.library.annotation.AnnotationView$b */
    /* loaded from: classes.dex */
    public enum EnumC0594b {
        NONE,
        DRAW_PATH,
        DRAW_RECT,
        DRAW_CIRCLE,
        DRAW_BLUR,
        DRAW_ZOOM
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.annotation.AnnotationView$d */
    /* loaded from: classes.dex */
    public enum EnumC0596d {
        HIGH,
        LOW
    }

    /* renamed from: com.instabug.library.annotation.AnnotationView$e */
    /* loaded from: classes.dex */
    public interface InterfaceC0597e {
        /* renamed from: a */
        void mo1023a();
    }

    /* renamed from: com.instabug.library.annotation.AnnotationView$f */
    /* loaded from: classes.dex */
    public interface InterfaceC0598f {
        /* renamed from: a */
        void mo1025a(boolean z);
    }

    /* renamed from: com.instabug.library.annotation.AnnotationView$g */
    /* loaded from: classes.dex */
    public interface InterfaceC0599g {
        /* renamed from: a */
        void mo1024a(Path path, Path path2);
    }

    public AnnotationView(Context context) {
        this(context, null);
    }

    public AnnotationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AnnotationView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f617g = new LinkedHashMap<>();
        this.f622l = new PointF[5];
        this.f631u = new PointF();
        this.f632v = EnumC0593a.NONE;
        this.f633w = EnumC0594b.NONE;
        this.f634x = new C0628d();
        this.f612b = new GestureDetector(context, new C0595c());
        this.f626p = new Paint(1);
        this.f626p.setColor(-65281);
        this.f627q = new C0608a();
        this.f628r = new C0608a();
        this.f629s = new C0608a();
        this.f630t = new C0608a();
        m1043e();
        for (int i2 = 0; i2 < this.f622l.length; i2++) {
            this.f622l[i2] = new PointF();
        }
    }

    public void setDrawingColor(int i) {
        this.f616f = i;
        this.f615e.setColor(this.f616f);
    }

    /* renamed from: e */
    private void m1043e() {
        this.f615e = new Paint();
        this.f615e.setAntiAlias(true);
        this.f615e.setDither(true);
        this.f616f = SupportMenu.CATEGORY_MASK;
        this.f615e.setColor(this.f616f);
        this.f615e.setStyle(Paint.Style.STROKE);
        this.f615e.setStrokeJoin(Paint.Join.ROUND);
        this.f615e.setStrokeCap(Paint.Cap.ROUND);
        this.f615e.setStrokeWidth(4.0f * getContext().getResources().getDisplayMetrics().density);
    }

    /* renamed from: a */
    private void m1031a(float f, float f2) {
        this.f613c = new Path();
        this.f614d = new ArrayList();
        this.f617g.put(this.f613c, Integer.valueOf(this.f616f));
        this.f613c.reset();
        this.f613c.moveTo(f, f2);
        this.f614d.add(new PointF(f, f2));
        this.f618h = f;
        this.f619i = f2;
        m1039b(f, f2);
    }

    /* renamed from: b */
    private void m1039b(float f, float f2) {
        for (PointF pointF : this.f622l) {
            pointF.x = f;
            pointF.y = f2;
        }
    }

    /* renamed from: c */
    private void m1040c(float f, float f2) {
        float abs = Math.abs(f - this.f618h);
        float abs2 = Math.abs(f2 - this.f619i);
        if (abs >= 8.0f || abs2 >= 8.0f) {
            this.f613c.quadTo(this.f618h, this.f619i, (this.f618h + f) / 2.0f, (this.f619i + f2) / 2.0f);
            this.f618h = f;
            this.f619i = f2;
            this.f614d.add(new PointF(f, f2));
        }
    }

    /* renamed from: f */
    private void m1044f() {
        this.f613c.lineTo(this.f618h, this.f619i);
        if (new PathMeasure(this.f613c, false).getLength() < 20.0f) {
            this.f617g.remove(this.f613c);
            return;
        }
        this.f635y = new C0625c(new C0621e(this.f613c, this.f615e.getStrokeWidth(), this.f615e, this.f614d));
        C0616b c0616b = new C0616b();
        this.f613c.computeBounds(c0616b, true);
        this.f635y.m1128a(new C0616b(c0616b));
        this.f634x.m1153a(this.f635y);
        this.f617g.remove(this.f613c);
        invalidate();
        m1036a(c0616b);
    }

    /* renamed from: a */
    private void m1036a(C0616b c0616b) {
        C0611c.a m1080a = new C0611c().m1080a(this.f613c);
        AbstractC0623g abstractC0623g = null;
        if (m1080a.f703a == C0613e.a.ARROW || m1080a.f703a == C0613e.a.LINE) {
            float max = Math.max(c0616b.width(), c0616b.height());
            float centerX = c0616b.centerX() - (max / 2.0f);
            float centerX2 = (max / 2.0f) + c0616b.centerX();
            PointF pointF = new PointF(centerX, c0616b.centerY());
            PointF pointF2 = new PointF(centerX2, c0616b.centerY());
            C0627b.m1143a(c0616b.centerX(), c0616b.centerY(), m1080a.f704b, pointF);
            C0627b.m1143a(c0616b.centerX(), c0616b.centerY(), m1080a.f704b, pointF2);
            abstractC0623g = new C0617a(pointF, pointF2, this.f616f, this.f615e.getStrokeWidth());
            if (m1080a.f703a == C0613e.a.ARROW) {
                ((C0617a) abstractC0623g).m1102a("arrow");
            }
            c0616b.set(Math.min(pointF.x, pointF2.x), Math.min(pointF.y, pointF2.y), Math.max(pointF.x, pointF2.x), Math.max(pointF.y, pointF2.y));
        } else if (m1080a.f703a == C0613e.a.RECT) {
            float max2 = Math.max(c0616b.width(), c0616b.height());
            c0616b.set(c0616b.centerX() - (max2 / 2.0f), c0616b.centerY() - (max2 / 2.0f), c0616b.centerX() + (max2 / 2.0f), (max2 / 2.0f) + c0616b.centerY());
            float width = c0616b.width() * m1080a.f705c;
            int i = m1080a.f704b;
            if (i <= 20) {
                i = 0;
            } else if (i >= 70 && i <= 110) {
                i = 90;
            } else if (i >= 160) {
                i = MPEGConst.SEQUENCE_ERROR_CODE;
            }
            if (i == 0 || i == 180) {
                c0616b.left += width;
                c0616b.right -= width;
            } else if (i == 90) {
                c0616b.top += width;
                c0616b.bottom -= width;
            } else if (i > 90 && i < 180) {
                i -= 90;
                c0616b.top += width;
                c0616b.bottom -= width;
            } else {
                c0616b.left += width;
                c0616b.right -= width;
            }
            if ((m1080a.f704b >= 20 && m1080a.f704b <= 70) || (m1080a.f704b >= 110 && m1080a.f704b <= 160)) {
                float width2 = c0616b.width() * 0.1f;
                float height = c0616b.height() * 0.1f;
                c0616b.left += width2;
                c0616b.right -= width2;
                c0616b.top += height;
                c0616b.bottom -= height;
            }
            abstractC0623g = new C0622f(this.f616f, this.f615e.getStrokeWidth(), i);
        } else if (m1080a.f703a == C0613e.a.OVAL) {
            float max3 = Math.max(c0616b.width(), c0616b.height());
            c0616b.set(c0616b.centerX() - (max3 / 2.0f), c0616b.centerY() - (max3 / 2.0f), c0616b.centerX() + (max3 / 2.0f), (max3 / 2.0f) + c0616b.centerY());
            float width3 = c0616b.width() * m1080a.f705c;
            int i2 = m1080a.f704b;
            if (i2 <= 20) {
                i2 = 0;
            } else if (i2 >= 70 && i2 <= 110) {
                i2 = 90;
            }
            if (i2 >= 90) {
                i2 -= 90;
                c0616b.top += width3;
                c0616b.bottom -= width3;
            } else {
                c0616b.left += width3;
                c0616b.right -= width3;
            }
            abstractC0623g = new C0620d(this.f616f, this.f615e.getStrokeWidth(), i2);
        }
        this.f609D = abstractC0623g;
        this.f610E = c0616b;
        if (abstractC0623g != null) {
            m1032a(this.f613c, abstractC0623g.mo1095a(this.f610E));
        }
    }

    /* renamed from: a */
    private void m1034a(AbstractC0623g abstractC0623g, C0616b c0616b) {
        this.f634x.m1159e(this.f635y);
        this.f635y.m1127a(abstractC0623g, c0616b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1047a() {
        m1034a(this.f609D, this.f610E);
        invalidate();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f621k != null) {
            this.f621k.draw(canvas);
        }
        if (!this.f625o) {
            this.f624n = this.f634x.m1152a().size();
        }
        for (int i = 0; i < this.f624n; i++) {
            C0625c m1151a = this.f634x.m1151a(i);
            if (m1151a != null) {
                m1151a.m1125a(canvas);
            }
        }
        if (!this.f625o && this.f635y != null) {
            if (this.f608C) {
                this.f635y.m1131b(canvas);
            }
            this.f635y.m1126a(canvas, this.f627q, this.f630t, this.f628r, this.f629s);
        }
        if (!this.f617g.isEmpty()) {
            Iterator<Map.Entry<Path, Integer>> it = this.f617g.entrySet().iterator();
            do {
                Map.Entry<Path, Integer> next = it.next();
                this.f615e.setColor(next.getValue().intValue());
                canvas.drawPath(next.getKey(), this.f615e);
            } while (it.hasNext());
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.f612b.onTouchEvent(motionEvent)) {
            int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            switch (actionMasked) {
                case 0:
                    this.f608C = true;
                    getOriginalBitmap();
                    if (this.f636z != null) {
                        this.f636z.mo1023a();
                    }
                    this.f631u.set(x, y);
                    if (this.f628r.m1070b(this.f631u) && this.f635y != null) {
                        this.f632v = EnumC0593a.RESIZE_BY_BOTTOM_RIGHT_BUTTON;
                    } else if (this.f629s.m1070b(this.f631u) && this.f635y != null) {
                        this.f632v = EnumC0593a.RESIZE_BY_BOTTOM_LEFT_BUTTON;
                    } else if (this.f627q.m1070b(this.f631u) && this.f635y != null) {
                        this.f632v = EnumC0593a.RESIZE_BY_TOP_LEFT_BUTTON;
                    } else if (this.f630t.m1070b(this.f631u) && this.f635y != null) {
                        this.f632v = EnumC0593a.RESIZE_BY_TOP_RIGHT_BUTTON;
                    } else {
                        this.f635y = getSelectedMarkUpDrawable();
                        if (this.f635y == null) {
                            switch (this.f633w) {
                                case DRAW_RECT:
                                    this.f635y = new C0625c(new C0622f(this.f616f, this.f615e.getStrokeWidth(), 0));
                                    this.f634x.m1153a(this.f635y);
                                    invalidate();
                                    break;
                                case DRAW_CIRCLE:
                                    this.f635y = new C0625c(new C0620d(this.f616f, this.f615e.getStrokeWidth(), 0));
                                    this.f634x.m1153a(this.f635y);
                                    invalidate();
                                    break;
                                case DRAW_BLUR:
                                    this.f635y = new C0625c(new C0618b(getOriginalBitmap(), getContext()));
                                    this.f634x.m1155b(this.f635y);
                                    invalidate();
                                    break;
                            }
                            this.f632v = EnumC0593a.DRAW;
                        } else {
                            this.f632v = EnumC0593a.DRAG;
                        }
                    }
                    m1045g();
                    invalidate();
                    break;
                case 1:
                    this.f608C = false;
                    if ((this.f632v == EnumC0593a.DRAG || this.f632v == EnumC0593a.RESIZE_BY_TOP_LEFT_BUTTON || this.f632v == EnumC0593a.RESIZE_BY_TOP_RIGHT_BUTTON || this.f632v == EnumC0593a.RESIZE_BY_BOTTOM_RIGHT_BUTTON || this.f632v == EnumC0593a.RESIZE_BY_BOTTOM_LEFT_BUTTON) && this.f635y != null) {
                        this.f634x.m1159e(this.f635y);
                        this.f635y.m1123a();
                    }
                    this.f631u.set(x, y);
                    if (this.f633w != EnumC0594b.DRAW_PATH) {
                        this.f632v = EnumC0593a.NONE;
                        invalidate();
                        break;
                    }
                    break;
                case 2:
                    m1033a(motionEvent);
                    m1045g();
                    invalidate();
                    break;
            }
            if (this.f632v != EnumC0593a.RESIZE_BY_TOP_LEFT_BUTTON && this.f632v != EnumC0593a.RESIZE_BY_TOP_RIGHT_BUTTON && this.f632v != EnumC0593a.RESIZE_BY_BOTTOM_RIGHT_BUTTON && this.f632v != EnumC0593a.RESIZE_BY_BOTTOM_LEFT_BUTTON && this.f632v != EnumC0593a.DRAG && this.f632v == EnumC0593a.DRAW && this.f633w == EnumC0594b.DRAW_PATH) {
                switch (motionEvent.getAction()) {
                    case 0:
                        this.f620j = false;
                        m1031a(x, y);
                        break;
                    case 1:
                        m1044f();
                        if (!this.f620j) {
                            performClick();
                        }
                        invalidate();
                        break;
                    case 2:
                        this.f620j = true;
                        m1040c(x, y);
                        invalidate();
                        break;
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: g */
    public void m1045g() {
        if (this.f632v != EnumC0593a.DRAW) {
            int i = 1;
            while (true) {
                int i2 = i;
                if (i2 < this.f634x.m1154b()) {
                    C0625c m1151a = this.f634x.m1151a(i2);
                    if (this.f634x.m1158d(this.f635y) <= i2 && (m1151a.m1134c() instanceof C0624h) && m1151a.m1135d()) {
                        ((C0624h) m1151a.m1134c()).m1121a(m1028a(i2));
                    }
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: a */
    private void m1033a(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (this.f632v) {
            case DRAG:
                if (this.f635y != null) {
                    this.f635y.m1124a((int) (x - this.f631u.x), (int) (y - this.f631u.y));
                    return;
                }
                return;
            case RESIZE_BY_BOTTOM_RIGHT_BUTTON:
                if (this.f635y != null) {
                    C0616b c0616b = new C0616b();
                    if (x < this.f635y.f769d.left) {
                        c0616b.left = this.f635y.f769d.right + ((int) (x - this.f631u.x));
                        c0616b.right = this.f635y.f769d.left;
                    } else {
                        c0616b.left = this.f635y.f769d.left;
                        c0616b.right = this.f635y.f769d.right + ((int) (x - this.f631u.x));
                    }
                    if (y < this.f635y.f769d.top) {
                        c0616b.top = this.f635y.f769d.bottom + ((int) (y - this.f631u.y));
                        c0616b.bottom = this.f635y.f769d.top;
                    } else {
                        c0616b.top = this.f635y.f769d.top;
                        c0616b.bottom = this.f635y.f769d.bottom + ((int) (y - this.f631u.y));
                    }
                    this.f635y.m1132b(c0616b);
                    if (this.f635y.m1134c() instanceof C0622f) {
                        ((C0622f) this.f635y.m1134c()).m1118c(x, y, this.f635y.f768c);
                        return;
                    }
                    return;
                }
                return;
            case RESIZE_BY_BOTTOM_LEFT_BUTTON:
                if (this.f635y != null) {
                    C0616b c0616b2 = new C0616b();
                    if (x > this.f635y.f769d.right) {
                        c0616b2.left = this.f635y.f769d.right;
                        c0616b2.right = this.f635y.f769d.left + ((int) (x - this.f631u.x));
                    } else {
                        c0616b2.left = this.f635y.f769d.left + ((int) (x - this.f631u.x));
                        c0616b2.right = this.f635y.f769d.right;
                    }
                    if (y < this.f635y.f769d.top) {
                        c0616b2.top = this.f635y.f769d.bottom + ((int) (y - this.f631u.y));
                        c0616b2.bottom = this.f635y.f769d.top;
                    } else {
                        c0616b2.top = this.f635y.f769d.top;
                        c0616b2.bottom = this.f635y.f769d.bottom + ((int) (y - this.f631u.y));
                    }
                    this.f635y.m1132b(c0616b2);
                    if (this.f635y.m1134c() instanceof C0622f) {
                        ((C0622f) this.f635y.m1134c()).m1119d(x, y, this.f635y.f768c);
                        return;
                    }
                    return;
                }
                return;
            case RESIZE_BY_TOP_LEFT_BUTTON:
                if (this.f635y != null) {
                    if (this.f635y.m1134c() instanceof C0617a) {
                        ((C0617a) this.f635y.m1134c()).m1096a(x, y, this.f635y.f768c);
                        return;
                    }
                    C0616b c0616b3 = new C0616b();
                    if (x > this.f635y.f769d.right) {
                        c0616b3.left = this.f635y.f769d.right;
                        c0616b3.right = this.f635y.f769d.left + ((int) (x - this.f631u.x));
                    } else {
                        c0616b3.left = this.f635y.f769d.left + ((int) (x - this.f631u.x));
                        c0616b3.right = this.f635y.f769d.right;
                    }
                    if (y > this.f635y.f769d.bottom) {
                        c0616b3.top = this.f635y.f769d.bottom;
                        c0616b3.bottom = this.f635y.f769d.top + ((int) (y - this.f631u.y));
                    } else {
                        c0616b3.top = this.f635y.f769d.top + ((int) (y - this.f631u.y));
                        c0616b3.bottom = this.f635y.f769d.bottom;
                    }
                    this.f635y.m1132b(c0616b3);
                    if (this.f635y.m1134c() instanceof C0622f) {
                        ((C0622f) this.f635y.m1134c()).m1116a(x, y, this.f635y.f768c);
                        return;
                    }
                    return;
                }
                return;
            case RESIZE_BY_TOP_RIGHT_BUTTON:
                if (this.f635y != null) {
                    if (this.f635y.m1134c() instanceof C0617a) {
                        ((C0617a) this.f635y.m1134c()).m1104b(x, y, this.f635y.f768c);
                        return;
                    }
                    C0616b c0616b4 = new C0616b();
                    if (x < this.f635y.f769d.left) {
                        c0616b4.left = this.f635y.f769d.right + ((int) (x - this.f631u.x));
                        c0616b4.right = this.f635y.f769d.left;
                    } else {
                        c0616b4.left = this.f635y.f769d.left;
                        c0616b4.right = this.f635y.f769d.right + ((int) (x - this.f631u.x));
                    }
                    if (y > this.f635y.f769d.bottom) {
                        c0616b4.top = this.f635y.f769d.bottom;
                        c0616b4.bottom = this.f635y.f769d.top + ((int) (y - this.f631u.y));
                    } else {
                        c0616b4.top = this.f635y.f769d.top + ((int) (y - this.f631u.y));
                        c0616b4.bottom = this.f635y.f769d.bottom;
                    }
                    this.f635y.m1132b(c0616b4);
                    if (this.f635y.m1134c() instanceof C0622f) {
                        ((C0622f) this.f635y.m1134c()).m1117b(x, y, this.f635y.f768c);
                        return;
                    }
                    return;
                }
                return;
            case DRAW:
                if (this.f635y != null) {
                    C0616b c0616b5 = new C0616b();
                    if (x < this.f631u.x) {
                        c0616b5.left = (int) x;
                        c0616b5.right = (int) this.f631u.x;
                    } else {
                        c0616b5.left = (int) this.f631u.x;
                        c0616b5.right = (int) x;
                    }
                    if (y < this.f631u.y) {
                        c0616b5.top = (int) y;
                        c0616b5.bottom = (int) this.f631u.y;
                    } else {
                        c0616b5.top = (int) this.f631u.y;
                        c0616b5.bottom = (int) y;
                    }
                    this.f635y.m1128a(c0616b5);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private C0625c getSelectedMarkUpDrawable() {
        for (int m1154b = this.f634x.m1154b() - 1; m1154b >= 0; m1154b--) {
            C0625c m1151a = this.f634x.m1151a(m1154b);
            if (m1151a.m1130a(this.f631u)) {
                return m1151a;
            }
        }
        return null;
    }

    /* renamed from: a */
    public void m1048a(AbstractC0623g abstractC0623g) {
        int min = Math.min(getWidth(), getHeight()) / 2;
        m1035a(abstractC0623g, new C0616b((getWidth() - min) / 2, (float) (((getHeight() - min) / 2) - 30), r1 + min, min + r2 + 30), EnumC0596d.HIGH);
    }

    /* renamed from: a */
    private void m1035a(AbstractC0623g abstractC0623g, C0616b c0616b, EnumC0596d enumC0596d) {
        C0625c c0625c = new C0625c(abstractC0623g);
        c0625c.m1128a(c0616b);
        m1037a(c0625c, enumC0596d);
    }

    /* renamed from: a */
    private void m1037a(C0625c c0625c, EnumC0596d enumC0596d) {
        getOriginalBitmap();
        this.f635y = c0625c;
        if (enumC0596d == EnumC0596d.LOW) {
            this.f634x.m1155b(c0625c);
        } else {
            this.f634x.m1153a(c0625c);
        }
        invalidate();
    }

    private Bitmap getOriginalBitmap() {
        if (this.f623m == null) {
            this.f623m = m1050c();
        }
        return this.f623m;
    }

    /* renamed from: b */
    public void m1049b() {
        if (this.f611a < 5) {
            m1048a(new C0624h(m1050c()));
            this.f611a++;
        }
        if (this.f611a == 5 && this.f606A != null) {
            this.f606A.mo1025a(false);
        }
    }

    public void setScreenshot(Drawable drawable) {
        this.f621k = drawable;
    }

    @Nullable
    /* renamed from: c */
    public Bitmap m1050c() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return null;
        }
        return m1028a(this.f634x.m1154b());
    }

    /* renamed from: a */
    private Bitmap m1028a(int i) {
        this.f624n = i;
        Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.f625o = true;
        invalidate();
        draw(canvas);
        this.f625o = false;
        invalidate();
        return createBitmap;
    }

    public void setDrawingMode(EnumC0594b enumC0594b) {
        this.f633w = enumC0594b;
    }

    /* renamed from: d */
    public void m1051d() {
        C0625c m1156c = this.f634x.m1156c();
        if (m1156c != null && (m1156c.m1134c() instanceof C0624h)) {
            this.f611a--;
            m1046h();
        }
        this.f635y = null;
        m1045g();
        invalidate();
    }

    public void setOnActionDownListener(InterfaceC0597e interfaceC0597e) {
        this.f636z = interfaceC0597e;
    }

    /* renamed from: setOnNewMagnifierAddingAِِِbilityChangedListener  reason: contains not printable characters */
    public void m2149setOnNewMagnifierAddingAbilityChangedListener(InterfaceC0598f interfaceC0598f) {
        this.f606A = interfaceC0598f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: h */
    public void m1046h() {
        if (this.f606A != null) {
            if (this.f611a == 5) {
                this.f606A.mo1025a(false);
            }
            if (this.f611a == 4) {
                this.f606A.mo1025a(true);
            }
        }
    }

    /* renamed from: com.instabug.library.annotation.AnnotationView$c */
    /* loaded from: classes.dex */
    private class C0595c extends GestureDetector.SimpleOnGestureListener {
        private C0595c() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent motionEvent) {
            if (AnnotationView.this.f635y != null) {
                AnnotationView.this.f634x.m1159e(AnnotationView.this.f635y);
                AnnotationView.this.f635y.m1129a(false);
                if (AnnotationView.this.f635y.m1134c() instanceof C0624h) {
                    AnnotationView annotationView = AnnotationView.this;
                    annotationView.f611a--;
                    AnnotationView.this.m1046h();
                }
                AnnotationView.this.f635y = null;
                AnnotationView.this.m1045g();
                AnnotationView.this.invalidate();
                return true;
            }
            return true;
        }
    }

    /* renamed from: a */
    private void m1032a(Path path, Path path2) {
        if (this.f607B != null) {
            this.f607B.mo1024a(path, path2);
        }
    }

    public void setOnPathRecognizedListener(InterfaceC0599g interfaceC0599g) {
        this.f607B = interfaceC0599g;
    }
}
