package com.instabug.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.widget.TextView;
import com.instabug.library.C0577R;
import com.instabug.library.settings.C0736a;

/* loaded from: classes.dex */
public class IconView extends TextView {

    /* renamed from: a */
    private Typeface f1264a;
    @ColorInt

    /* renamed from: g */
    int f1265g;

    /* renamed from: h */
    float f1266h;
    @ColorInt

    /* renamed from: i */
    int f1267i;

    /* renamed from: j */
    Paint f1268j;

    public IconView(Context context) {
        this(context, null);
    }

    public IconView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public IconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f1265g = 0;
        this.f1266h = 0.0f;
        this.f1267i = 0;
        if (!isInEditMode()) {
            if (this.f1264a == null) {
                this.f1264a = Typeface.createFromAsset(context.getAssets(), "font_icons.ttf");
            }
            setTypeface(this.f1264a);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0577R.styleable.IconView);
            if (!obtainStyledAttributes.hasValue(C0577R.styleable.IconView_android_textSize)) {
                m1815a();
            }
            if (!obtainStyledAttributes.hasValue(C0577R.styleable.IconView_android_padding)) {
                m1816b();
            }
            if (!obtainStyledAttributes.hasValue(C0577R.styleable.IconView_android_textColor)) {
                setTextColor(C0736a.m1649b().m1678k());
            }
            int i2 = obtainStyledAttributes.getInt(C0577R.styleable.IconView_instabug_icon, -1);
            if (i2 != -1) {
                setText(C0758a.m1817a(i2));
            }
            obtainStyledAttributes.recycle();
            this.f1268j = new Paint(1);
        }
    }

    /* renamed from: a */
    private void m1815a() {
        setTextSize(1, 24.0f);
    }

    /* renamed from: b */
    private void m1816b() {
        setPadding(ViewUtils.convertDpToPx(getContext(), 1.0f));
    }

    public void setPadding(int i) {
        setPadding(i, i, i, i);
    }

    @Override // android.view.View
    public void setBackgroundColor(@ColorInt int i) {
        this.f1265g = i;
        invalidate();
    }

    public void setStrokeWidth(float f) {
        this.f1266h = f;
        invalidate();
    }

    public void setStrokeColor(@ColorInt int i) {
        this.f1267i = i;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        this.f1268j.setColor(this.f1265g);
        this.f1268j.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, Math.min(getWidth() / 2.0f, getHeight() / 2.0f) - (this.f1266h / 2.0f), this.f1268j);
        this.f1268j.setStrokeWidth(this.f1266h);
        this.f1268j.setColor(this.f1267i);
        this.f1268j.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, Math.min(getWidth() / 2.0f, getHeight() / 2.0f) - (this.f1266h / 2.0f), this.f1268j);
        super.onDraw(canvas);
    }
}
