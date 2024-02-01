package com.instabug.library.annotation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Size;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.instabug.library.C0577R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ColorPickerPopUpView extends View {

    /* renamed from: a */
    private static final int f658a = Color.argb(255, 247, 247, 247);

    /* renamed from: b */
    private static final int f659b = Color.argb(255, 190, 190, 190);

    /* renamed from: c */
    private RectF f660c;

    /* renamed from: d */
    private EnumC0602c f661d;

    /* renamed from: e */
    private int f662e;

    /* renamed from: f */
    private int f663f;

    /* renamed from: g */
    private InterfaceC0601b f664g;
    @Size(7)

    /* renamed from: h */
    private int[] f665h;

    /* renamed from: i */
    private int f666i;

    /* renamed from: j */
    private List<C0600a> f667j;

    /* renamed from: com.instabug.library.annotation.ColorPickerPopUpView$b */
    /* loaded from: classes.dex */
    public interface InterfaceC0601b {
        /* renamed from: a */
        void mo1026a(@ColorInt int i, int i2);
    }

    /* renamed from: com.instabug.library.annotation.ColorPickerPopUpView$c */
    /* loaded from: classes.dex */
    public enum EnumC0602c {
        VERTICAL,
        HORIZONTAL
    }

    public ColorPickerPopUpView(Context context) {
        super(context);
        this.f665h = new int[]{SupportMenu.CATEGORY_MASK, -15925503, -65028, -15893761, -33280, -1024, -4737097};
        m1053a((AttributeSet) null, 0);
    }

    public ColorPickerPopUpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f665h = new int[]{SupportMenu.CATEGORY_MASK, -15925503, -65028, -15893761, -33280, -1024, -4737097};
        m1053a(attributeSet, 0);
    }

    public ColorPickerPopUpView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f665h = new int[]{SupportMenu.CATEGORY_MASK, -15925503, -65028, -15893761, -33280, -1024, -4737097};
        m1053a(attributeSet, i);
    }

    /* renamed from: a */
    private void m1053a(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, C0577R.styleable.ColorPickerPopUpView, i, 0);
        if (obtainStyledAttributes.getInt(C0577R.styleable.ColorPickerPopUpView_view_orientation, -1) == 0) {
            this.f661d = EnumC0602c.VERTICAL;
        } else {
            this.f661d = EnumC0602c.HORIZONTAL;
        }
        obtainStyledAttributes.recycle();
        this.f662e = f658a;
        this.f663f = f659b;
        this.f667j = new ArrayList();
        for (int i2 : this.f665h) {
            this.f667j.add(new C0600a(i2));
        }
        setSelectedColor(0);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int measuredWidth;
        int i3;
        super.onMeasure(i, i2);
        if (this.f661d == EnumC0602c.VERTICAL) {
            i3 = getMeasuredHeight();
            measuredWidth = (i3 * 55) / 426;
        } else {
            measuredWidth = getMeasuredWidth();
            i3 = (measuredWidth * 55) / 426;
        }
        setMeasuredDimension(measuredWidth, i3);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (actionMasked) {
            case 1:
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 < this.f667j.size()) {
                        if (!this.f667j.get(i2).m1056a().contains(x, y)) {
                            i = i2 + 1;
                        } else {
                            setSelectedColor(i2);
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            default:
                return true;
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.f661d == EnumC0602c.VERTICAL) {
            this.f660c = new RectF(0.0f, 0.0f, (i2 * 55) / 426.0f, i2);
            this.f667j.get(0).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.15603f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.04429f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.69504f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.09857f) + 0.5f)));
            this.f667j.get(1).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.15603f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.18786f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.69504f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.24214f) + 0.5f)));
            this.f667j.get(2).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.15603f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.33071f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.69504f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.385f) + 0.5f)));
            this.f667j.get(3).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.15603f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.47357f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.69504f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.52786f) + 0.5f)));
            this.f667j.get(4).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.15603f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.61643f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.69504f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.67071f) + 0.5f)));
            this.f667j.get(5).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.15603f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.75929f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.69504f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.81357f) + 0.5f)));
            this.f667j.get(6).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.15603f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.90357f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.69504f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.95786f) + 0.5f)));
            return;
        }
        this.f660c = new RectF(0.0f, 0.0f, i, (i * 55) / 426.0f);
        this.f667j.get(0).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.04429f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.15603f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.09857f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.69504f) + 0.5f)));
        this.f667j.get(1).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.18714f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.15603f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.24143f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.69504f) + 0.5f)));
        this.f667j.get(2).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.33f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.15603f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.38429f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.69504f) + 0.5f)));
        this.f667j.get(3).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.47286f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.15603f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.52714f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.69504f) + 0.5f)));
        this.f667j.get(4).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.61571f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.15603f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.67f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.69504f) + 0.5f)));
        this.f667j.get(5).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.75857f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.15603f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.81286f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.69504f) + 0.5f)));
        this.f667j.get(6).f668a = new RectF(this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.90143f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.15603f) + 0.5f)), this.f660c.left + ((float) Math.floor((this.f660c.width() * 0.95571f) + 0.5f)), this.f660c.top + ((float) Math.floor((this.f660c.height() * 0.69504f) + 0.5f)));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f661d == EnumC0602c.VERTICAL) {
            m1055a(canvas);
        } else {
            m1054b(canvas);
        }
    }

    /* renamed from: a */
    private void m1052a(Canvas canvas, C0600a c0600a) {
        RectF rectF = c0600a.f668a;
        int i = c0600a.f669b;
        RectF rectF2 = new RectF(rectF.left, rectF.top, rectF.left + ((float) Math.floor(rectF.height() + 0.5f)), rectF.top + ((float) Math.floor(rectF.height() + 0.5f)));
        Path path = new Path();
        path.addOval(rectF2, Path.Direction.CW);
        Paint paint = new Paint(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(i);
        canvas.drawPath(path, paint);
        if (c0600a.f670c) {
            Path path2 = new Path();
            path2.moveTo(rectF.left + (rectF.height() * 0.20313f), rectF.top + (rectF.height() * 0.51758f));
            path2.lineTo(rectF.left + (rectF.height() * 0.39844f), rectF.top + (rectF.height() * 0.71875f));
            path2.lineTo(rectF.left + (rectF.height() * 0.79492f), rectF.top + (rectF.height() * 0.33008f));
            path2.lineTo(rectF.left + (rectF.height() * 0.74805f), rectF.top + (rectF.height() * 0.28125f));
            path2.lineTo(rectF.left + (rectF.height() * 0.39844f), rectF.top + (rectF.height() * 0.625f));
            path2.lineTo(rectF.left + (rectF.height() * 0.25f), rectF.top + (rectF.height() * 0.47266f));
            path2.lineTo(rectF.left + (rectF.height() * 0.20313f), (rectF.height() * 0.51758f) + rectF.top);
            path2.close();
            Paint paint2 = new Paint(1);
            paint2.setStyle(Paint.Style.FILL);
            paint2.setColor(-1);
            canvas.drawPath(path2, paint2);
        }
    }

    /* renamed from: b */
    private void m1054b(Canvas canvas) {
        Path path = new Path();
        path.moveTo(this.f660c.left + (this.f660c.width() * 0.99964f), this.f660c.top + (this.f660c.height() * 0.09474f));
        path.lineTo(this.f660c.left + (this.f660c.width() * 0.99964f), this.f660c.top + (this.f660c.height() * 0.75789f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.99964f), this.f660c.top + (this.f660c.height() * 0.81021f), this.f660c.left + (this.f660c.width() * 0.99538f), this.f660c.top + (this.f660c.height() * 0.85263f), this.f660c.left + (this.f660c.width() * 0.99012f), this.f660c.top + (this.f660c.height() * 0.85263f));
        path.lineTo(this.f660c.left + (this.f660c.width() * 0.13866f), this.f660c.top + (this.f660c.height() * 0.85274f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.13198f), this.f660c.top + (this.f660c.height() * 0.91794f), this.f660c.left + (this.f660c.width() * 0.12496f), this.f660c.top + (this.f660c.height() * 0.98936f), this.f660c.left + (this.f660c.width() * 0.12496f), this.f660c.top + (this.f660c.height() * 0.98936f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.12496f), this.f660c.top + (this.f660c.height() * 0.98936f), this.f660c.left + (this.f660c.width() * 0.11802f), this.f660c.top + (this.f660c.height() * 0.91774f), this.f660c.left + (this.f660c.width() * 0.11125f), this.f660c.top + (this.f660c.height() * 0.85274f));
        path.lineTo(this.f660c.left + (this.f660c.width() * 0.00952f), this.f660c.top + (this.f660c.height() * 0.85263f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.00426f), this.f660c.top + (this.f660c.height() * 0.85263f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.81021f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.75789f));
        path.lineTo(this.f660c.left, this.f660c.top + (this.f660c.height() * 0.09474f));
        path.cubicTo(this.f660c.left, this.f660c.top + (this.f660c.height() * 0.04241f), this.f660c.left + (this.f660c.width() * 0.00426f), this.f660c.top, this.f660c.left + (this.f660c.width() * 0.00952f), this.f660c.top);
        path.lineTo(this.f660c.left + (this.f660c.width() * 0.99012f), this.f660c.top);
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.99538f), this.f660c.top, this.f660c.left + (this.f660c.width() * 0.99964f), this.f660c.top + (this.f660c.height() * 0.04241f), this.f660c.left + (this.f660c.width() * 0.99964f), this.f660c.top + (this.f660c.height() * 0.09474f));
        path.close();
        Paint paint = new Paint(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.f662e);
        canvas.drawPath(path, paint);
        Paint paint2 = new Paint(1);
        paint2.setStrokeWidth(1.0f);
        paint2.setStrokeMiter(10.0f);
        canvas.save();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(this.f663f);
        paint2.setStrokeWidth(1.0f * getContext().getResources().getDisplayMetrics().density);
        canvas.drawPath(path, paint2);
        canvas.restore();
        Iterator<C0600a> it = this.f667j.iterator();
        while (it.hasNext()) {
            m1052a(canvas, it.next());
        }
    }

    /* renamed from: a */
    void m1055a(Canvas canvas) {
        Path path = new Path();
        path.moveTo(this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.00929f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.00929f), this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.04834f), this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.11085f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.91373f), this.f660c.top + (this.f660c.height() * 0.11701f), this.f660c.left + (this.f660c.width() * 0.99193f), this.f660c.top + (this.f660c.height() * 0.12488f), this.f660c.left + (this.f660c.width() * 0.99193f), this.f660c.top + (this.f660c.height() * 0.12488f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.99193f), this.f660c.top + (this.f660c.height() * 0.12488f), this.f660c.left + (this.f660c.width() * 0.90758f), this.f660c.top + (this.f660c.height() * 0.13338f), this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.13891f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.40548f), this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.99036f), this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.99036f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.99549f), this.f660c.left + (this.f660c.width() * 0.81135f), this.f660c.top + (this.f660c.height() * 0.99964f), this.f660c.left + (this.f660c.width() * 0.76043f), this.f660c.top + (this.f660c.height() * 0.99964f));
        path.lineTo(this.f660c.left + (this.f660c.width() * 0.0922f), this.f660c.top + (this.f660c.height() * 0.99964f));
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.04128f), this.f660c.top + (this.f660c.height() * 0.99964f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.99549f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.99036f));
        path.cubicTo(this.f660c.left, this.f660c.top + (this.f660c.height() * 0.99036f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.98973f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.9885f));
        path.cubicTo(this.f660c.left, this.f660c.top + (this.f660c.height() * 0.94121f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.00929f), this.f660c.left, this.f660c.top + (this.f660c.height() * 0.00929f));
        path.cubicTo(this.f660c.left, this.f660c.top + (this.f660c.height() * 0.00416f), this.f660c.left + (this.f660c.width() * 0.04128f), this.f660c.top, this.f660c.left + (this.f660c.width() * 0.0922f), this.f660c.top);
        path.lineTo(this.f660c.left + (this.f660c.width() * 0.76043f), this.f660c.top);
        path.cubicTo(this.f660c.left + (this.f660c.width() * 0.81135f), this.f660c.top, this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.00416f), this.f660c.left + (this.f660c.width() * 0.85262f), this.f660c.top + (this.f660c.height() * 0.00929f));
        path.close();
        Paint paint = new Paint(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.f662e);
        canvas.drawPath(path, paint);
        Paint paint2 = new Paint(1);
        paint2.setStrokeWidth(1.0f);
        paint2.setStrokeMiter(10.0f);
        canvas.save();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(this.f663f);
        paint2.setStrokeWidth(1.0f * getContext().getResources().getDisplayMetrics().density);
        canvas.drawPath(path, paint2);
        canvas.restore();
        Iterator<C0600a> it = this.f667j.iterator();
        while (it.hasNext()) {
            m1052a(canvas, it.next());
        }
    }

    public void setColors(@Size(7) int[] iArr) {
        this.f665h = Arrays.copyOf(iArr, iArr.length);
    }

    public void setPopUpBackgroundColor(int i) {
        this.f662e = i;
        invalidate();
    }

    public void setPopUpBorderColor(int i) {
        this.f663f = i;
        invalidate();
    }

    public void setOrientation(EnumC0602c enumC0602c) {
        this.f661d = enumC0602c;
    }

    public int getSelectedColor() {
        return this.f666i;
    }

    private void setSelectedColor(int i) {
        this.f666i = this.f665h[i];
        for (int i2 = 0; i2 < this.f667j.size(); i2++) {
            if (i2 == i) {
                this.f667j.get(i2).f670c = true;
            } else {
                this.f667j.get(i2).f670c = false;
            }
        }
        invalidate();
        if (this.f664g != null) {
            this.f664g.mo1026a(this.f666i, i);
        }
    }

    public void setOnColorSelectionListener(InterfaceC0601b interfaceC0601b) {
        this.f664g = interfaceC0601b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.annotation.ColorPickerPopUpView$a */
    /* loaded from: classes.dex */
    public static class C0600a {

        /* renamed from: a */
        RectF f668a = new RectF();

        /* renamed from: b */
        int f669b;

        /* renamed from: c */
        boolean f670c;

        public C0600a(int i) {
            this.f669b = i;
        }

        /* renamed from: a */
        public RectF m1056a() {
            RectF rectF = new RectF();
            rectF.set(this.f668a);
            rectF.inset((-this.f668a.width()) / 3.0f, (-this.f668a.height()) / 3.0f);
            return rectF;
        }
    }
}
