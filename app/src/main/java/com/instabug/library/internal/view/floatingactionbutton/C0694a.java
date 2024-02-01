package com.instabug.library.internal.view.floatingactionbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.TextView;
import com.instabug.library.C0577R;
import com.instabug.library.view.IconView;

/* compiled from: FloatingActionButton.java */
/* renamed from: com.instabug.library.internal.view.floatingactionbutton.a */
/* loaded from: classes.dex */
public class C0694a extends IconView {

    /* renamed from: a */
    int f942a;

    /* renamed from: b */
    int f943b;

    /* renamed from: c */
    int f944c;

    /* renamed from: d */
    String f945d;

    /* renamed from: e */
    boolean f946e;
    @DrawableRes

    /* renamed from: f */
    private int f947f;

    /* renamed from: k */
    private Drawable f948k;

    /* renamed from: l */
    private int f949l;

    /* renamed from: m */
    private float f950m;

    /* renamed from: n */
    private float f951n;

    /* renamed from: o */
    private float f952o;

    /* renamed from: p */
    private int f953p;

    public C0694a(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mo1385a(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void mo1385a(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0577R.styleable.FloatingActionButton, 0, 0);
        this.f942a = obtainStyledAttributes.getColor(C0577R.styleable.FloatingActionButton_instabug_fab_colorNormal, -1);
        this.f943b = obtainStyledAttributes.getColor(C0577R.styleable.FloatingActionButton_instabug_fab_colorPressed, -3355444);
        this.f944c = obtainStyledAttributes.getColor(C0577R.styleable.FloatingActionButton_instabug_fab_colorDisabled, m1398a(17170432));
        this.f949l = obtainStyledAttributes.getInt(C0577R.styleable.FloatingActionButton_instabug_fab_size, 0);
        this.f947f = obtainStyledAttributes.getResourceId(C0577R.styleable.FloatingActionButton_instabug_fab_icon, 0);
        this.f945d = obtainStyledAttributes.getString(C0577R.styleable.FloatingActionButton_instabug_fab_title);
        this.f946e = obtainStyledAttributes.getBoolean(C0577R.styleable.FloatingActionButton_instabug_fab_stroke_visible, true);
        obtainStyledAttributes.recycle();
        m1394c();
        this.f951n = m1400b(C0577R.dimen.instabug_fab_shadow_radius);
        this.f952o = m1400b(C0577R.dimen.instabug_fab_shadow_offset);
        m1390b();
        m1399a();
    }

    /* renamed from: b */
    private void m1390b() {
        this.f953p = (int) (this.f950m + (2.0f * this.f951n));
    }

    /* renamed from: c */
    private void m1394c() {
        this.f950m = m1400b(this.f949l == 0 ? C0577R.dimen.instabug_fab_size_normal : C0577R.dimen.instabug_fab_size_mini);
    }

    public void setSize(int i) {
        if (i != 1 && i != 0) {
            throw new IllegalArgumentException("Use @FAB_SIZE constants only!");
        }
        if (this.f949l != i) {
            this.f949l = i;
            m1394c();
            m1390b();
            m1399a();
        }
    }

    public int getSize() {
        return this.f949l;
    }

    public void setIcon(@DrawableRes int i) {
        if (this.f947f != i) {
            this.f947f = i;
            this.f948k = null;
            m1399a();
        }
    }

    public void setIconDrawable(@NonNull Drawable drawable) {
        if (this.f948k != drawable) {
            this.f947f = 0;
            this.f948k = drawable;
            m1399a();
        }
    }

    public int getColorNormal() {
        return this.f942a;
    }

    public void setColorNormalResId(@ColorRes int i) {
        setColorNormal(m1398a(i));
    }

    public void setColorNormal(int i) {
        if (this.f942a != i) {
            this.f942a = i;
            m1399a();
        }
    }

    public int getColorPressed() {
        return this.f943b;
    }

    public void setColorPressedResId(@ColorRes int i) {
        setColorPressed(m1398a(i));
    }

    public void setColorPressed(int i) {
        if (this.f943b != i) {
            this.f943b = i;
            m1399a();
        }
    }

    public int getColorDisabled() {
        return this.f944c;
    }

    public void setColorDisabledResId(@ColorRes int i) {
        setColorDisabled(m1398a(i));
    }

    public void setColorDisabled(int i) {
        if (this.f944c != i) {
            this.f944c = i;
            m1399a();
        }
    }

    public void setStrokeVisible(boolean z) {
        if (this.f946e != z) {
            this.f946e = z;
            m1399a();
        }
    }

    /* renamed from: a */
    int m1398a(@ColorRes int i) {
        return getResources().getColor(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public float m1400b(@DimenRes int i) {
        return getResources().getDimension(i);
    }

    public void setTitle(String str) {
        this.f945d = str;
        TextView labelView = getLabelView();
        if (labelView != null) {
            labelView.setText(str);
        }
    }

    TextView getLabelView() {
        return (TextView) getTag(C0577R.id.instabug_fab_label);
    }

    public String getTitle() {
        return this.f945d;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(this.f953p, this.f953p);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1399a() {
        int m1400b;
        float m1400b2 = m1400b(C0577R.dimen.instabug_fab_stroke_width);
        float f = m1400b2 / 2.0f;
        Drawable[] drawableArr = new Drawable[4];
        drawableArr[0] = getResources().getDrawable(this.f949l == 0 ? C0577R.drawable.instabug_fab_bg_normal : C0577R.drawable.instabug_fab_bg_mini);
        drawableArr[1] = m1387a(m1400b2);
        drawableArr[2] = m1389b(m1400b2);
        drawableArr[3] = getIconDrawable();
        LayerDrawable layerDrawable = new LayerDrawable(drawableArr);
        if (this.f949l == 0) {
            m1400b = ((int) (this.f950m - m1400b(C0577R.dimen.instabug_fab_icon_size_normal))) / 2;
        } else {
            m1400b = ((int) (this.f950m - m1400b(C0577R.dimen.instabug_fab_icon_size_mini))) / 2;
        }
        int i = (int) this.f951n;
        int i2 = (int) (this.f951n - this.f952o);
        int i3 = (int) (this.f951n + this.f952o);
        layerDrawable.setLayerInset(1, i, i2, i, i3);
        layerDrawable.setLayerInset(2, (int) (i - f), (int) (i2 - f), (int) (i - f), (int) (i3 - f));
        layerDrawable.setLayerInset(3, i + m1400b, i2 + m1400b, i + m1400b, i3 + m1400b);
        setBackgroundCompat(layerDrawable);
    }

    Drawable getIconDrawable() {
        if (this.f948k != null) {
            return this.f948k;
        }
        if (this.f947f != 0) {
            return getResources().getDrawable(this.f947f);
        }
        return new ColorDrawable(0);
    }

    /* renamed from: a */
    private StateListDrawable m1387a(float f) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-16842910}, m1386a(this.f944c, f));
        stateListDrawable.addState(new int[]{16842919}, m1386a(this.f943b, f));
        stateListDrawable.addState(new int[0], m1386a(this.f942a, f));
        return stateListDrawable;
    }

    /* renamed from: a */
    private Drawable m1386a(int i, float f) {
        int alpha = Color.alpha(i);
        int m1397f = m1397f(i);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setColor(m1397f);
        Drawable[] drawableArr = {shapeDrawable, m1393c(m1397f, f)};
        LayerDrawable layerDrawable = (alpha == 255 || !this.f946e) ? new LayerDrawable(drawableArr) : new a(alpha, drawableArr);
        int i2 = (int) (f / 2.0f);
        layerDrawable.setLayerInset(1, i2, i2, i2, i2);
        return layerDrawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: FloatingActionButton.java */
    /* renamed from: com.instabug.library.internal.view.floatingactionbutton.a$a */
    /* loaded from: classes.dex */
    public static class a extends LayerDrawable {

        /* renamed from: a */
        private final int f960a;

        public a(int i, Drawable... drawableArr) {
            super(drawableArr);
            this.f960a = i;
        }

        @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, this.f960a, 31);
            super.draw(canvas);
            canvas.restore();
        }
    }

    /* renamed from: b */
    private Drawable m1389b(float f) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint.setAlpha(m1391c(0.02f));
        return shapeDrawable;
    }

    /* renamed from: c */
    private int m1391c(float f) {
        return (int) (255.0f * f);
    }

    /* renamed from: c */
    private int m1392c(int i) {
        return m1388b(i, 0.9f);
    }

    /* renamed from: d */
    private int m1395d(int i) {
        return m1388b(i, 1.1f);
    }

    /* renamed from: b */
    private int m1388b(int i, float f) {
        Color.colorToHSV(i, r0);
        float[] fArr = {0.0f, 0.0f, Math.min(fArr[2] * f, 1.0f)};
        return Color.HSVToColor(Color.alpha(i), fArr);
    }

    /* renamed from: e */
    private int m1396e(int i) {
        return Color.argb(Color.alpha(i) / 2, Color.red(i), Color.green(i), Color.blue(i));
    }

    /* renamed from: f */
    private int m1397f(int i) {
        return Color.rgb(Color.red(i), Color.green(i), Color.blue(i));
    }

    /* renamed from: c */
    private Drawable m1393c(final int i, float f) {
        if (!this.f946e) {
            return new ColorDrawable(0);
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        final int m1392c = m1392c(i);
        final int m1396e = m1396e(m1392c);
        final int m1395d = m1395d(i);
        final int m1396e2 = m1396e(m1395d);
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(f);
        paint.setStyle(Paint.Style.STROKE);
        shapeDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() { // from class: com.instabug.library.internal.view.floatingactionbutton.a.1
            @Override // android.graphics.drawable.ShapeDrawable.ShaderFactory
            public Shader resize(int i2, int i3) {
                return new LinearGradient(i2 / 2.0f, 0.0f, i2 / 2.0f, i3, new int[]{m1395d, m1396e2, i, m1396e, m1392c}, new float[]{0.0f, 0.2f, 0.5f, 0.8f, 1.0f}, Shader.TileMode.CLAMP);
            }
        });
        return shapeDrawable;
    }

    @SuppressLint({"NewApi"})
    private void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        TextView labelView = getLabelView();
        if (labelView != null) {
            labelView.setVisibility(i);
        }
        super.setVisibility(i);
    }
}
