package com.instabug.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class ScaleImageView extends ImageView implements View.OnTouchListener {
    private float MAX_SCALE;
    private boolean isScaling;
    private Context mContext;
    private GestureDetector mDetector;
    private int mHeight;
    private int mIntrinsicHeight;
    private int mIntrinsicWidth;
    private Matrix mMatrix;
    private final float[] mMatrixValues;
    private float mMinScale;
    private float mPrevDistance;
    private int mPrevMoveX;
    private int mPrevMoveY;
    private float mScale;
    private int mWidth;

    public ScaleImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.MAX_SCALE = 5.0f;
        this.mMatrixValues = new float[9];
        this.mContext = context;
        initialize();
    }

    public ScaleImageView(Context context) {
        super(context);
        this.MAX_SCALE = 5.0f;
        this.mMatrixValues = new float[9];
        this.mContext = context;
        initialize();
    }

    @Override // android.widget.ImageView
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        initialize();
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i) {
        super.setImageResource(i);
        initialize();
    }

    private void initialize() {
        setScaleType(ImageView.ScaleType.MATRIX);
        this.mMatrix = new Matrix();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            this.mIntrinsicWidth = drawable.getIntrinsicWidth();
            this.mIntrinsicHeight = drawable.getIntrinsicHeight();
            setOnTouchListener(this);
        }
        this.mDetector = new GestureDetector(this.mContext, new GestureDetector.SimpleOnGestureListener() { // from class: com.instabug.library.view.ScaleImageView.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent motionEvent) {
                ScaleImageView.this.maxZoomTo((int) motionEvent.getX(), (int) motionEvent.getY());
                ScaleImageView.this.cutting();
                return super.onDoubleTap(motionEvent);
            }
        });
    }

    @Override // android.widget.ImageView
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        int i5;
        int i6 = 0;
        this.mWidth = i3 - i;
        this.mHeight = i4 - i2;
        this.mMatrix.reset();
        this.mScale = (i3 - i) / this.mIntrinsicWidth;
        if (this.mScale * this.mIntrinsicHeight > this.mHeight) {
            this.mScale = this.mHeight / this.mIntrinsicHeight;
            this.mMatrix.postScale(this.mScale, this.mScale);
            i5 = (i3 - this.mWidth) / 2;
        } else {
            this.mMatrix.postScale(this.mScale, this.mScale);
            i6 = (i4 - this.mHeight) / 2;
            i5 = 0;
        }
        this.mMatrix.postTranslate(i5, i6);
        setImageMatrix(this.mMatrix);
        this.mMinScale = this.mScale;
        zoomTo(this.mScale, this.mWidth / 2, this.mHeight / 2);
        cutting();
        return super.setFrame(i, i2, i3, i4);
    }

    protected float getValue(Matrix matrix, int i) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[i];
    }

    protected float getScale() {
        return getValue(this.mMatrix, 0);
    }

    public float getTranslateX() {
        return getValue(this.mMatrix, 2);
    }

    protected float getTranslateY() {
        return getValue(this.mMatrix, 5);
    }

    protected void maxZoomTo(int i, int i2) {
        if (this.mMinScale != getScale() && getScale() - this.mMinScale > 0.1f) {
            zoomTo(this.mMinScale / getScale(), i, i2);
        } else {
            zoomTo(this.MAX_SCALE / getScale(), i, i2);
        }
    }

    public void zoomTo(float f, int i, int i2) {
        if (getScale() * f < this.mMinScale) {
            return;
        }
        if (f < 1.0f || getScale() * f <= this.MAX_SCALE) {
            this.mMatrix.postScale(f, f);
            this.mMatrix.postTranslate((-((this.mWidth * f) - this.mWidth)) / 2.0f, (-((this.mHeight * f) - this.mHeight)) / 2.0f);
            this.mMatrix.postTranslate((-(i - (this.mWidth / 2))) * f, 0.0f);
            this.mMatrix.postTranslate(0.0f, (-(i2 - (this.mHeight / 2))) * f);
            setImageMatrix(this.mMatrix);
        }
    }

    public void cutting() {
        int scale = (int) (this.mIntrinsicWidth * getScale());
        int scale2 = (int) (this.mIntrinsicHeight * getScale());
        if (getTranslateX() < (-(scale - this.mWidth))) {
            this.mMatrix.postTranslate(-((getTranslateX() + scale) - this.mWidth), 0.0f);
        }
        if (getTranslateX() > 0.0f) {
            this.mMatrix.postTranslate(-getTranslateX(), 0.0f);
        }
        if (getTranslateY() < (-(scale2 - this.mHeight))) {
            this.mMatrix.postTranslate(0.0f, -((getTranslateY() + scale2) - this.mHeight));
        }
        if (getTranslateY() > 0.0f) {
            this.mMatrix.postTranslate(0.0f, -getTranslateY());
        }
        if (scale < this.mWidth) {
            this.mMatrix.postTranslate((this.mWidth - scale) / 2.0f, 0.0f);
        }
        if (scale2 < this.mHeight) {
            this.mMatrix.postTranslate(0.0f, (this.mHeight - scale2) / 2.0f);
        }
        setImageMatrix(this.mMatrix);
    }

    private float distance(float f, float f2, float f3, float f4) {
        float f5 = f - f2;
        float f6 = f3 - f4;
        return (float) Math.sqrt((f5 * f5) + (f6 * f6));
    }

    private float dispDistance() {
        return (float) Math.sqrt((this.mWidth * this.mWidth) + (this.mHeight * this.mHeight));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mDetector.onTouchEvent(motionEvent)) {
            int pointerCount = motionEvent.getPointerCount();
            switch (motionEvent.getAction()) {
                case 0:
                case 5:
                case 261:
                    if (pointerCount >= 2) {
                        this.mPrevDistance = distance(motionEvent.getX(0), motionEvent.getX(1), motionEvent.getY(0), motionEvent.getY(1));
                        this.isScaling = true;
                        break;
                    } else {
                        this.mPrevMoveX = (int) motionEvent.getX();
                        this.mPrevMoveY = (int) motionEvent.getY();
                        break;
                    }
                case 1:
                case 6:
                case 262:
                    if (motionEvent.getPointerCount() <= 1) {
                        this.isScaling = false;
                        break;
                    }
                    break;
                case 2:
                    if (pointerCount >= 2 && this.isScaling) {
                        float distance = distance(motionEvent.getX(0), motionEvent.getX(1), motionEvent.getY(0), motionEvent.getY(1));
                        float dispDistance = (distance - this.mPrevDistance) / dispDistance();
                        this.mPrevDistance = distance;
                        float f = 1.0f + dispDistance;
                        zoomTo(f * f, this.mWidth / 2, this.mHeight / 2);
                        cutting();
                        break;
                    } else if (!this.isScaling) {
                        int x = this.mPrevMoveX - ((int) motionEvent.getX());
                        int y = this.mPrevMoveY - ((int) motionEvent.getY());
                        this.mPrevMoveX = (int) motionEvent.getX();
                        this.mPrevMoveY = (int) motionEvent.getY();
                        this.mMatrix.postTranslate(-x, -y);
                        cutting();
                        break;
                    }
                    break;
            }
        }
        return true;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }
}
