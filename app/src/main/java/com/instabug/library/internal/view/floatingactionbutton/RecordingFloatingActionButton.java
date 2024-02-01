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

/* loaded from: classes.dex */
public class RecordingFloatingActionButton extends C0694a {

    /* renamed from: f */
    private RecordingState f933f;

    /* renamed from: k */
    private Paint f934k;

    /* renamed from: l */
    private String f935l;

    /* renamed from: m */
    private float f936m;

    /* loaded from: classes.dex */
    public enum RecordingState {
        RECORDING,
        STOPPED
    }

    public RecordingFloatingActionButton(Context context) {
        this(context, null);
    }

    public RecordingFloatingActionButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.instabug.library.internal.view.floatingactionbutton.C0694a
    /* renamed from: a */
    public void mo1385a(Context context, AttributeSet attributeSet) {
        super.mo1385a(context, attributeSet);
        this.f934k = new Paint(1);
        this.f934k.setColor(-1);
        this.f934k.setTextAlign(Paint.Align.CENTER);
        this.f934k.setTextSize(context.getResources().getDimension(C0577R.dimen.instabug_fab_text_size));
        this.f936m = m1400b(C0577R.dimen.instabug_fab_circle_icon_stroke);
    }

    @Override // com.instabug.library.internal.view.floatingactionbutton.C0694a
    Drawable getIconDrawable() {
        final float b;
        final float b2;
        if (getSize() == 0) {
            b = m1400b(C0577R.dimen.instabug_fab_size_normal);
            b2 = m1400b(C0577R.dimen.instabug_fab_icon_size_normal);
        } else {
            b = m1400b(C0577R.dimen.instabug_fab_size_mini);
            b2 = m1400b(C0577R.dimen.instabug_fab_icon_size_mini);
        }
        final float f = b2 / 2.0f;
        final float b3 = m1400b(C0577R.dimen.instabug_fab_circle_icon_stroke);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new Shape() { // from class: com.instabug.library.internal.view.floatingactionbutton.RecordingFloatingActionButton.1
            @Override // android.graphics.drawable.shapes.Shape
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(SupportMenu.CATEGORY_MASK);
                if (RecordingFloatingActionButton.this.f933f == RecordingState.RECORDING) {
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setStrokeWidth(b3);
                    canvas.drawCircle(f, f, b / 2.0f, paint);
                } else {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(b3);
                    canvas.drawCircle(f, f, b / 2.0f, paint);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawOval(new RectF(0.0f, 0.0f, b2, b2), paint);
                }
            }
        });
        shapeDrawable.getPaint().setAntiAlias(true);
        return shapeDrawable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.view.IconView, android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f935l != null) {
            canvas.drawText(this.f935l, canvas.getWidth() / 2, (int) (((canvas.getHeight() / 2.0f) - ((this.f934k.descent() + this.f934k.ascent()) / 2.0f)) - this.f936m), this.f934k);
        }
    }

    public void setText(String str) {
        this.f935l = str;
        invalidate();
    }

    public void setRecordingState(RecordingState recordingState) {
        this.f933f = recordingState;
        m1399a();
    }
}
