package com.instabug.chat.p011ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.instabug.library.C0577R;

/* loaded from: classes.dex */
public class CircularImageView extends ImageView {

    /* renamed from: a */
    private static final ImageView.ScaleType f505a = ImageView.ScaleType.CENTER_CROP;

    /* renamed from: b */
    private static final Bitmap.Config f506b = Bitmap.Config.ARGB_8888;

    /* renamed from: c */
    private final RectF f507c;

    /* renamed from: d */
    private final RectF f508d;

    /* renamed from: e */
    private final Matrix f509e;

    /* renamed from: f */
    private final Paint f510f;

    /* renamed from: g */
    private final Paint f511g;

    /* renamed from: h */
    private final Paint f512h;

    /* renamed from: i */
    private int f513i;

    /* renamed from: j */
    private int f514j;

    /* renamed from: k */
    private int f515k;

    /* renamed from: l */
    private Bitmap f516l;

    /* renamed from: m */
    private BitmapShader f517m;

    /* renamed from: n */
    private int f518n;

    /* renamed from: o */
    private int f519o;

    /* renamed from: p */
    private float f520p;

    /* renamed from: q */
    private float f521q;

    /* renamed from: r */
    private ColorFilter f522r;

    /* renamed from: s */
    private boolean f523s;

    /* renamed from: t */
    private boolean f524t;

    /* renamed from: u */
    private boolean f525u;

    public CircularImageView(Context context) {
        super(context);
        this.f507c = new RectF();
        this.f508d = new RectF();
        this.f509e = new Matrix();
        this.f510f = new Paint();
        this.f511g = new Paint();
        this.f512h = new Paint();
        this.f513i = ViewCompat.MEASURED_STATE_MASK;
        this.f514j = 0;
        this.f515k = 0;
        m912a();
    }

    public CircularImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircularImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f507c = new RectF();
        this.f508d = new RectF();
        this.f509e = new Matrix();
        this.f510f = new Paint();
        this.f511g = new Paint();
        this.f512h = new Paint();
        this.f513i = ViewCompat.MEASURED_STATE_MASK;
        this.f514j = 0;
        this.f515k = 0;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0577R.styleable.CircleImageView, i, 0);
        this.f514j = obtainStyledAttributes.getDimensionPixelSize(C0577R.styleable.CircleImageView_ibg_civ_border_width, 0);
        this.f513i = obtainStyledAttributes.getColor(C0577R.styleable.CircleImageView_ibg_civ_border_color, ViewCompat.MEASURED_STATE_MASK);
        this.f525u = obtainStyledAttributes.getBoolean(C0577R.styleable.CircleImageView_ibg_civ_border_overlay, false);
        this.f515k = obtainStyledAttributes.getColor(C0577R.styleable.CircleImageView_ibg_civ_fill_color, 0);
        obtainStyledAttributes.recycle();
        m912a();
    }

    /* renamed from: a */
    private void m912a() {
        super.setScaleType(f505a);
        this.f523s = true;
        if (this.f524t) {
            m913b();
            this.f524t = false;
        }
    }

    @Override // android.widget.ImageView
    public ImageView.ScaleType getScaleType() {
        return f505a;
    }

    @Override // android.widget.ImageView
    public void setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType != f505a) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override // android.widget.ImageView
    public void setAdjustViewBounds(boolean z) {
        if (z) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.f516l != null) {
            if (this.f515k != 0) {
                canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, this.f520p, this.f512h);
            }
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, this.f520p, this.f510f);
            if (this.f514j != 0) {
                canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, this.f521q, this.f511g);
            }
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        m913b();
    }

    public int getBorderColor() {
        return this.f513i;
    }

    public void setBorderColor(@ColorInt int i) {
        if (i != this.f513i) {
            this.f513i = i;
            this.f511g.setColor(this.f513i);
            invalidate();
        }
    }

    public void setBorderColorResource(@ColorRes int i) {
        setBorderColor(getContext().getResources().getColor(i));
    }

    public int getFillColor() {
        return this.f515k;
    }

    public void setFillColor(@ColorInt int i) {
        if (i != this.f515k) {
            this.f515k = i;
            this.f512h.setColor(i);
            invalidate();
        }
    }

    public void setFillColorResource(@ColorRes int i) {
        setFillColor(getContext().getResources().getColor(i));
    }

    public int getBorderWidth() {
        return this.f514j;
    }

    public void setBorderWidth(int i) {
        if (i != this.f514j) {
            this.f514j = i;
            m913b();
        }
    }

    public void setBorderOverlay(boolean z) {
        if (z != this.f525u) {
            this.f525u = z;
            m913b();
        }
    }

    @Override // android.widget.ImageView
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        this.f516l = bitmap;
        m913b();
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        this.f516l = m911a(drawable);
        m913b();
    }

    @Override // android.widget.ImageView
    public void setImageResource(@DrawableRes int i) {
        super.setImageResource(i);
        this.f516l = m911a(getDrawable());
        m913b();
    }

    @Override // android.widget.ImageView
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        this.f516l = uri != null ? m911a(getDrawable()) : null;
        m913b();
    }

    @Override // android.widget.ImageView
    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter != this.f522r) {
            this.f522r = colorFilter;
            this.f510f.setColorFilter(this.f522r);
            invalidate();
        }
    }

    /* renamed from: a */
    private Bitmap m911a(Drawable drawable) {
        Bitmap createBitmap;
        Bitmap bitmap = null;
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            if (drawable instanceof ColorDrawable) {
                createBitmap = Bitmap.createBitmap(2, 2, f506b);
            } else {
                createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), f506b);
            }
            Canvas canvas = new Canvas(createBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            bitmap = createBitmap;
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /* renamed from: b */
    private void m913b() {
        if (!this.f523s) {
            this.f524t = true;
            return;
        }
        if (getWidth() != 0 || getHeight() != 0) {
            if (this.f516l == null) {
                invalidate();
                return;
            }
            this.f517m = new BitmapShader(this.f516l, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.f510f.setAntiAlias(true);
            this.f510f.setShader(this.f517m);
            this.f511g.setStyle(Paint.Style.STROKE);
            this.f511g.setAntiAlias(true);
            this.f511g.setColor(this.f513i);
            this.f511g.setStrokeWidth(this.f514j);
            this.f512h.setStyle(Paint.Style.FILL);
            this.f512h.setAntiAlias(true);
            this.f512h.setColor(this.f515k);
            this.f519o = this.f516l.getHeight();
            this.f518n = this.f516l.getWidth();
            this.f508d.set(0.0f, 0.0f, getWidth(), getHeight());
            this.f521q = Math.min((this.f508d.height() - this.f514j) / 2.0f, (this.f508d.width() - this.f514j) / 2.0f);
            this.f507c.set(this.f508d);
            if (!this.f525u) {
                this.f507c.inset(this.f514j, this.f514j);
            }
            this.f520p = Math.min(this.f507c.height() / 2.0f, this.f507c.width() / 2.0f);
            m914c();
            invalidate();
        }
    }

    /* renamed from: c */
    private void m914c() {
        float width;
        float f;
        float f2 = 0.0f;
        this.f509e.set(null);
        if (this.f518n * this.f507c.height() > this.f507c.width() * this.f519o) {
            width = this.f507c.height() / this.f519o;
            f = (this.f507c.width() - (this.f518n * width)) * 0.5f;
        } else {
            width = this.f507c.width() / this.f518n;
            f = 0.0f;
            f2 = (this.f507c.height() - (this.f519o * width)) * 0.5f;
        }
        this.f509e.setScale(width, width);
        this.f509e.postTranslate(((int) (f + 0.5f)) + this.f507c.left, ((int) (f2 + 0.5f)) + this.f507c.top);
        this.f517m.setLocalMatrix(this.f509e);
    }
}
