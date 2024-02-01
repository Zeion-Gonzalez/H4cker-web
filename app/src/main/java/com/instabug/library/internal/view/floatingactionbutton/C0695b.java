package com.instabug.library.internal.view.floatingactionbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import com.instabug.library.C0577R;
import com.instabug.library.view.C0758a;

/* compiled from: MuteFloatingActionButton.java */
/* renamed from: com.instabug.library.internal.view.floatingactionbutton.b */
/* loaded from: classes.dex */
public class C0695b extends C0694a {

    /* renamed from: f */
    private boolean f961f;

    public C0695b(Context context) {
        this(context, null);
    }

    public C0695b(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.instabug.library.internal.view.floatingactionbutton.C0694a
    /* renamed from: a */
    public void mo1385a(Context context, AttributeSet attributeSet) {
        super.mo1385a(context, attributeSet);
        setSize(1);
        setId(C0577R.id.instabug_video_mute_button);
        setText(C0758a.m1817a(1));
        setGravity(17);
        m1404d();
    }

    @Override // com.instabug.library.internal.view.floatingactionbutton.C0694a
    Drawable getIconDrawable() {
        final float b = m1400b(C0577R.dimen.instabug_fab_icon_size_mini);
        final float b2 = m1400b(C0577R.dimen.instabug_fab_size_mini);
        final float f = b / 2.0f;
        final float b3 = m1400b(C0577R.dimen.instabug_fab_circle_icon_stroke);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new Shape() { // from class: com.instabug.library.internal.view.floatingactionbutton.b.1
            @Override // android.graphics.drawable.shapes.Shape
            public void draw(Canvas canvas, Paint paint) {
                if (C0695b.this.f961f) {
                    paint.setColor(SupportMenu.CATEGORY_MASK);
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setStrokeWidth(b3);
                    canvas.drawCircle(f, f, b2 / 2.0f, paint);
                    return;
                }
                paint.setColor(ViewCompat.MEASURED_STATE_MASK);
                paint.setStrokeWidth(b3);
                canvas.drawLine(0.0f, b3, b, b3 + b, paint);
            }
        });
        shapeDrawable.getPaint().setAntiAlias(true);
        return shapeDrawable;
    }

    /* renamed from: b */
    public boolean m1402b() {
        if (this.f961f) {
            m1404d();
        } else {
            m1403c();
        }
        return this.f961f;
    }

    /* renamed from: c */
    public void m1403c() {
        this.f961f = true;
        m1399a();
        setTextColor(-1);
    }

    /* renamed from: d */
    public void m1404d() {
        this.f961f = false;
        m1399a();
        setTextColor(ViewCompat.MEASURED_STATE_MASK);
    }
}
