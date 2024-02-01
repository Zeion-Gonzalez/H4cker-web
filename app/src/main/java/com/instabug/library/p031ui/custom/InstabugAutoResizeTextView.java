package com.instabug.library.p031ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/* loaded from: classes.dex */
public class InstabugAutoResizeTextView extends TextView {
    private static final int NO_LINE_LIMIT = -1;
    private final RectF _availableSpaceRect;
    private boolean _initialized;
    private int _maxLines;
    private float _maxTextSize;
    private float _minTextSize;
    private TextPaint _paint;
    private final SizeTester _sizeTester;
    private float _spacingAdd;
    private float _spacingMult;
    private int _widthLimit;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface SizeTester {
        int onTestSize(int i, RectF rectF);
    }

    public InstabugAutoResizeTextView(Context context) {
        this(context, null, 16842884);
    }

    public InstabugAutoResizeTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public InstabugAutoResizeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this._availableSpaceRect = new RectF();
        this._spacingMult = 1.0f;
        this._spacingAdd = 0.0f;
        this._initialized = false;
        this._minTextSize = TypedValue.applyDimension(2, 12.0f, getResources().getDisplayMetrics());
        this._maxTextSize = getTextSize();
        this._paint = new TextPaint(getPaint());
        if (this._maxLines == 0) {
            this._maxLines = -1;
        }
        this._sizeTester = new SizeTester() { // from class: com.instabug.library.ui.custom.InstabugAutoResizeTextView.1
            final RectF textRect = new RectF();

            @Override // com.instabug.library.ui.custom.InstabugAutoResizeTextView.SizeTester
            @TargetApi(16)
            public int onTestSize(int i2, RectF rectF) {
                String charSequence;
                InstabugAutoResizeTextView.this._paint.setTextSize(i2);
                TransformationMethod transformationMethod = InstabugAutoResizeTextView.this.getTransformationMethod();
                if (transformationMethod != null) {
                    charSequence = transformationMethod.getTransformation(InstabugAutoResizeTextView.this.getText(), InstabugAutoResizeTextView.this).toString();
                } else {
                    charSequence = InstabugAutoResizeTextView.this.getText().toString();
                }
                if (InstabugAutoResizeTextView.this.getMaxLines() == 1) {
                    this.textRect.bottom = InstabugAutoResizeTextView.this._paint.getFontSpacing();
                    this.textRect.right = InstabugAutoResizeTextView.this._paint.measureText(charSequence);
                } else {
                    StaticLayout staticLayout = new StaticLayout(charSequence, InstabugAutoResizeTextView.this._paint, InstabugAutoResizeTextView.this._widthLimit, Layout.Alignment.ALIGN_NORMAL, InstabugAutoResizeTextView.this._spacingMult, InstabugAutoResizeTextView.this._spacingAdd, true);
                    if (InstabugAutoResizeTextView.this.getMaxLines() != -1 && staticLayout.getLineCount() > InstabugAutoResizeTextView.this.getMaxLines()) {
                        return 1;
                    }
                    this.textRect.bottom = staticLayout.getHeight();
                    int lineCount = staticLayout.getLineCount();
                    int i3 = -1;
                    for (int i4 = 0; i4 < lineCount; i4++) {
                        int lineEnd = staticLayout.getLineEnd(i4);
                        if (i4 < lineCount - 1 && lineEnd > 0 && lineEnd - 1 < charSequence.length() && !InstabugAutoResizeTextView.this.isValidWordWrap(charSequence.charAt(lineEnd - 1))) {
                            return 1;
                        }
                        if (i3 < staticLayout.getLineRight(i4) - staticLayout.getLineLeft(i4)) {
                            i3 = ((int) staticLayout.getLineRight(i4)) - ((int) staticLayout.getLineLeft(i4));
                        }
                    }
                    this.textRect.right = i3;
                }
                this.textRect.offsetTo(0.0f, 0.0f);
                return rectF.contains(this.textRect) ? -1 : 1;
            }
        };
        this._initialized = true;
    }

    public boolean isValidWordWrap(char c) {
        return c == ' ' || c == '-' || c == '\n';
    }

    @Override // android.widget.TextView
    public void setAllCaps(boolean z) {
        super.setAllCaps(z);
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public void setTypeface(Typeface typeface) {
        super.setTypeface(typeface);
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public void setTextSize(float f) {
        this._maxTextSize = f;
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public void setMaxLines(int i) {
        super.setMaxLines(i);
        this._maxLines = i;
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public int getMaxLines() {
        return this._maxLines;
    }

    @Override // android.widget.TextView
    public void setSingleLine() {
        super.setSingleLine();
        this._maxLines = 1;
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public void setSingleLine(boolean z) {
        super.setSingleLine(z);
        if (z) {
            this._maxLines = 1;
        } else {
            this._maxLines = -1;
        }
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public void setLines(int i) {
        super.setLines(i);
        this._maxLines = i;
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public void setTextSize(int i, float f) {
        Resources resources;
        Context context = getContext();
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        this._maxTextSize = TypedValue.applyDimension(i, f, resources.getDisplayMetrics());
        adjustTextSize();
    }

    @Override // android.widget.TextView
    public void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
        this._spacingMult = f2;
        this._spacingAdd = f;
    }

    public void setMinTextSize(float f) {
        this._minTextSize = f;
        adjustTextSize();
    }

    private void adjustTextSize() {
        if (this._initialized) {
            int i = (int) this._minTextSize;
            int measuredHeight = (getMeasuredHeight() - getCompoundPaddingBottom()) - getCompoundPaddingTop();
            this._widthLimit = (getMeasuredWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight();
            if (this._widthLimit > 0) {
                this._paint = new TextPaint(getPaint());
                this._availableSpaceRect.right = this._widthLimit;
                this._availableSpaceRect.bottom = measuredHeight;
                superSetTextSize(i);
            }
        }
    }

    private void superSetTextSize(int i) {
        super.setTextSize(0, binarySearch(i, (int) this._maxTextSize, this._sizeTester, this._availableSpaceRect));
    }

    private int binarySearch(int i, int i2, SizeTester sizeTester, RectF rectF) {
        int i3;
        int i4 = i2 - 1;
        int i5 = i;
        int i6 = i;
        while (i5 <= i4) {
            int i7 = (i5 + i4) >>> 1;
            int onTestSize = sizeTester.onTestSize(i7, rectF);
            if (onTestSize < 0) {
                i3 = i7 + 1;
            } else {
                if (onTestSize <= 0) {
                    return i7;
                }
                i4 = i7 - 1;
                i3 = i5;
                i5 = i4;
            }
            int i8 = i3;
            i6 = i5;
            i5 = i8;
        }
        return i6;
    }

    @Override // android.widget.TextView
    protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        adjustTextSize();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            adjustTextSize();
        }
    }
}
