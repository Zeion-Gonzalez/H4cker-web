package com.instabug.library.annotation;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import com.instabug.library.C0577R;
import com.instabug.library.util.AttrResolver;

/* loaded from: classes.dex */
public class ShapeSuggestionsLayout extends LinearLayout {

    /* renamed from: a */
    private float f674a;

    /* renamed from: b */
    private InterfaceC0606a f675b;

    /* renamed from: c */
    private boolean f676c;

    /* renamed from: d */
    private final Runnable f677d;

    /* renamed from: e */
    private Handler f678e;

    /* renamed from: com.instabug.library.annotation.ShapeSuggestionsLayout$a */
    /* loaded from: classes.dex */
    interface InterfaceC0606a {
        /* renamed from: a */
        void mo1022a(int i);
    }

    public ShapeSuggestionsLayout(Context context) {
        this(context, null);
    }

    public ShapeSuggestionsLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    @TargetApi(11)
    public ShapeSuggestionsLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f677d = new Runnable() { // from class: com.instabug.library.annotation.ShapeSuggestionsLayout.1
            @Override // java.lang.Runnable
            public void run() {
                ShapeSuggestionsLayout.this.m1064b();
            }
        };
        this.f678e = new Handler();
        m1059a(context, attributeSet, i);
    }

    /* renamed from: a */
    private void m1059a(Context context, AttributeSet attributeSet, int i) {
        setVisibility(4);
        setGravity(17);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.f674a = TypedValue.applyDimension(1, 4.0f, displayMetrics);
        int applyDimension = (int) TypedValue.applyDimension(1, 1.5f, displayMetrics);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(AttrResolver.getBackgroundColor(getContext()));
        gradientDrawable.setCornerRadius(this.f674a);
        gradientDrawable.setStroke(applyDimension, AttrResolver.getDividerColor(getContext()));
        if (Build.VERSION.SDK_INT >= 16) {
            setBackground(gradientDrawable);
        } else {
            setBackgroundDrawable(gradientDrawable);
        }
    }

    public void setOnShapeSelectedListener(InterfaceC0606a interfaceC0606a) {
        this.f675b = interfaceC0606a;
    }

    /* renamed from: a */
    public void m1062a() {
        if (this.f676c) {
            this.f676c = false;
            setVisibility(0);
            if (Build.VERSION.SDK_INT >= 12) {
                animate().alpha(1.0f).setDuration(400L).setListener(null);
            }
            this.f678e.postDelayed(this.f677d, 3000L);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.f678e.removeCallbacks(this.f677d);
        this.f678e = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public void m1064b() {
        this.f678e.removeCallbacks(this.f677d);
        if (!this.f676c) {
            this.f676c = true;
            if (Build.VERSION.SDK_INT >= 12) {
                animate().alpha(0.0f).setDuration(400L).setListener(new Animator.AnimatorListener() { // from class: com.instabug.library.annotation.ShapeSuggestionsLayout.2
                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        ShapeSuggestionsLayout.this.setVisibility(4);
                        ShapeSuggestionsLayout.this.removeAllViews();
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        }
    }

    /* renamed from: a */
    public void m1063a(Path path) {
        C0607b c0607b = new C0607b(getContext(), path, AttrResolver.getTintingColor(getContext()));
        c0607b.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.library.annotation.ShapeSuggestionsLayout.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int indexOfChild = ShapeSuggestionsLayout.this.indexOfChild(view);
                ShapeSuggestionsLayout.this.m1058a(indexOfChild);
                if (ShapeSuggestionsLayout.this.f675b != null) {
                    ShapeSuggestionsLayout.this.f675b.mo1022a(indexOfChild);
                }
                ShapeSuggestionsLayout.this.m1064b();
            }
        });
        addView(c0607b);
        m1058a(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1058a(int i) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            getChildAt(i2).setBackgroundColor(0);
        }
        getChildAt(i).setBackgroundColor(AttrResolver.getDividerColor(getContext()));
    }

    /* renamed from: com.instabug.library.annotation.ShapeSuggestionsLayout$b */
    /* loaded from: classes.dex */
    private final class C0607b extends View {

        /* renamed from: b */
        private Path f683b;

        /* renamed from: c */
        private Path f684c;

        /* renamed from: d */
        private Paint f685d;

        /* renamed from: e */
        private RectF f686e;

        /* renamed from: f */
        private RectF f687f;
        @ColorInt

        /* renamed from: g */
        private int f688g;
        @ColorInt

        /* renamed from: h */
        private int f689h;

        public C0607b(Context context, Path path, int i) {
            super(context);
            this.f683b = path;
            this.f685d = new Paint(1);
            this.f685d.setStrokeWidth(4.0f);
            this.f689h = i;
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int dimension = (int) getResources().getDimension(C0577R.dimen.instabug_path_view_margin);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
            layoutParams.setMargins(dimension, dimension, dimension, dimension);
            setLayoutParams(layoutParams);
            int dimension2 = (int) getResources().getDimension(C0577R.dimen.instabug_path_view_dimen);
            setMeasuredDimension(dimension2, dimension2);
            this.f686e = new RectF(0.0f, 0.0f, dimension2, dimension2);
            this.f687f = new RectF(this.f686e);
            this.f687f.inset(8.0f, 8.0f);
            this.f684c = m1065a(this.f683b, this.f687f);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.f685d.setColor(this.f688g);
            this.f685d.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(this.f686e, ShapeSuggestionsLayout.this.f674a, ShapeSuggestionsLayout.this.f674a, this.f685d);
            this.f685d.setColor(this.f689h);
            this.f685d.setStyle(Paint.Style.STROKE);
            canvas.drawPath(this.f684c, this.f685d);
        }

        @Override // android.view.View
        public void setBackgroundColor(@ColorInt int i) {
            this.f688g = i;
            invalidate();
        }

        /* renamed from: a */
        public Path m1065a(Path path, RectF rectF) {
            Path path2 = new Path(path);
            RectF rectF2 = new RectF();
            path2.computeBounds(rectF2, true);
            Matrix matrix = new Matrix();
            matrix.setRectToRect(rectF2, rectF, Matrix.ScaleToFit.CENTER);
            path2.transform(matrix);
            return path2;
        }
    }
}
