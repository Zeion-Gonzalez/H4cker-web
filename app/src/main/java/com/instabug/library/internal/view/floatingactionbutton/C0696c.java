package com.instabug.library.internal.view.floatingactionbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import com.instabug.library.C0577R;

/* compiled from: StopFloatingActionButton.java */
/* renamed from: com.instabug.library.internal.view.floatingactionbutton.c */
/* loaded from: classes.dex */
public class C0696c extends C0694a {
    public C0696c(Context context) {
        this(context, null);
    }

    public C0696c(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.instabug.library.internal.view.floatingactionbutton.C0694a
    /* renamed from: a */
    public void mo1385a(Context context, AttributeSet attributeSet) {
        super.mo1385a(context, attributeSet);
        setSize(1);
        setId(C0577R.id.instabug_video_stop_button);
    }

    @Override // com.instabug.library.internal.view.floatingactionbutton.C0694a
    Drawable getIconDrawable() {
        final float b = m1400b(C0577R.dimen.instabug_fab_icon_size_mini);
        final float b2 = m1400b(C0577R.dimen.instabug_fab_size_mini);
        final float f = b / 2.0f;
        final float b3 = m1400b(C0577R.dimen.instabug_fab_circle_icon_stroke);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new Shape() { // from class: com.instabug.library.internal.view.floatingactionbutton.c.1
            @Override // android.graphics.drawable.shapes.Shape
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(SupportMenu.CATEGORY_MASK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(b3);
                canvas.drawCircle(f, f, b2 / 2.0f, paint);
                paint.setStyle(Paint.Style.FILL);
                RectF rectF = new RectF(0.0f, 0.0f, b, b);
                rectF.inset(b3 / 2.0f, b3 / 2.0f);
                canvas.drawRoundRect(rectF, 4.0f, 4.0f, paint);
            }
        });
        shapeDrawable.getPaint().setAntiAlias(true);
        return shapeDrawable;
    }
}
