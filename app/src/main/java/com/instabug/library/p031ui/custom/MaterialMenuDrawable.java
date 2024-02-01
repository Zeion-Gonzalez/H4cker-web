package com.instabug.library.p031ui.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;
import android.util.Property;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

@RequiresApi(api = 14)
/* loaded from: classes.dex */
public class MaterialMenuDrawable extends Drawable implements Animatable, MaterialMenu {
    private static final float ARROW_BOT_LINE_ANGLE = 225.0f;
    private static final float ARROW_MID_LINE_ANGLE = 180.0f;
    private static final float ARROW_TOP_LINE_ANGLE = 135.0f;
    private static final int BASE_CIRCLE_RADIUS = 18;
    private static final int BASE_DRAWABLE_HEIGHT = 40;
    private static final int BASE_DRAWABLE_WIDTH = 40;
    private static final int BASE_ICON_WIDTH = 20;
    private static final float CHECK_BOTTOM_ANGLE = -90.0f;
    private static final float CHECK_MIDDLE_ANGLE = 135.0f;
    private static final int DEFAULT_CIRCLE_ALPHA = 200;
    public static final int DEFAULT_COLOR = -1;
    public static final int DEFAULT_SCALE = 1;
    public static final int DEFAULT_TRANSFORM_DURATION = 800;
    public static final boolean DEFAULT_VISIBLE = true;
    private static final float TRANSFORMATION_END = 2.0f;
    private static final float TRANSFORMATION_MID = 1.0f;
    private static final float TRANSFORMATION_START = 0.0f;
    private static final float X_BOT_LINE_ANGLE = -44.0f;
    private static final float X_ROTATION_ANGLE = 90.0f;
    private static final float X_TOP_LINE_ANGLE = 44.0f;
    private IconState animatingIconState;
    private AnimationState animationState;
    private Animator.AnimatorListener animatorListener;
    private final Paint circlePaint;
    private final float circleRadius;
    private IconState currentIconState;
    private final float dip1;
    private final float dip2;
    private final float dip3;
    private final float dip4;
    private final float dip8;
    private final float diph;
    private final int height;
    private final Paint iconPaint;
    private final float iconWidth;
    private final Object lock;
    private MaterialMenuState materialMenuState;
    private boolean rtlEnabled;
    private final float sidePadding;
    private final Stroke stroke;
    private final float strokeWidth;
    private final float topPadding;
    private ObjectAnimator transformation;
    private Property<MaterialMenuDrawable, Float> transformationProperty;
    private boolean transformationRunning;
    private float transformationValue;
    private boolean visible;
    private final int width;

    /* loaded from: classes.dex */
    public enum IconState {
        BURGER,
        ARROW,
        X,
        CHECK
    }

    /* loaded from: classes.dex */
    public enum AnimationState {
        BURGER_ARROW,
        BURGER_X,
        ARROW_X,
        ARROW_CHECK,
        BURGER_CHECK,
        X_CHECK;

        public IconState getFirstState() {
            switch (this) {
                case BURGER_ARROW:
                    return IconState.BURGER;
                case BURGER_X:
                    return IconState.BURGER;
                case ARROW_X:
                    return IconState.ARROW;
                case ARROW_CHECK:
                    return IconState.ARROW;
                case BURGER_CHECK:
                    return IconState.BURGER;
                case X_CHECK:
                    return IconState.X;
                default:
                    return null;
            }
        }

        public IconState getSecondState() {
            switch (this) {
                case BURGER_ARROW:
                    return IconState.ARROW;
                case BURGER_X:
                    return IconState.X;
                case ARROW_X:
                    return IconState.X;
                case ARROW_CHECK:
                    return IconState.CHECK;
                case BURGER_CHECK:
                    return IconState.CHECK;
                case X_CHECK:
                    return IconState.CHECK;
                default:
                    return null;
            }
        }
    }

    /* loaded from: classes.dex */
    public enum Stroke {
        REGULAR(3),
        THIN(2),
        EXTRA_THIN(1);

        private final int strokeWidth;

        Stroke(int i) {
            this.strokeWidth = i;
        }

        protected static Stroke valueOf(int i) {
            switch (i) {
                case 1:
                    return EXTRA_THIN;
                case 2:
                    return THIN;
                case 3:
                    return REGULAR;
                default:
                    return THIN;
            }
        }
    }

    public MaterialMenuDrawable(Context context, int i, Stroke stroke) {
        this(context, i, stroke, 1, DEFAULT_TRANSFORM_DURATION);
    }

    public MaterialMenuDrawable(Context context, int i, Stroke stroke, int i2) {
        this(context, i, stroke, 1, i2);
    }

    public MaterialMenuDrawable(Context context, int i, Stroke stroke, int i2, int i3) {
        this.lock = new Object();
        this.iconPaint = new Paint();
        this.circlePaint = new Paint();
        this.transformationValue = 0.0f;
        this.transformationRunning = false;
        this.currentIconState = IconState.BURGER;
        this.animationState = AnimationState.BURGER_ARROW;
        this.transformationProperty = new Property<MaterialMenuDrawable, Float>(Float.class, "transformation") { // from class: com.instabug.library.ui.custom.MaterialMenuDrawable.1
            @Override // android.util.Property
            public Float get(MaterialMenuDrawable materialMenuDrawable) {
                return materialMenuDrawable.getTransformationValue();
            }

            @Override // android.util.Property
            public void set(MaterialMenuDrawable materialMenuDrawable, Float f) {
                materialMenuDrawable.setTransformationValue(f);
            }
        };
        Resources resources = context.getResources();
        this.dip1 = dpToPx(resources, TRANSFORMATION_MID) * i2;
        this.dip2 = dpToPx(resources, TRANSFORMATION_END) * i2;
        this.dip3 = dpToPx(resources, 3.0f) * i2;
        this.dip4 = dpToPx(resources, 4.0f) * i2;
        this.dip8 = dpToPx(resources, 8.0f) * i2;
        this.diph = this.dip1 / TRANSFORMATION_END;
        this.stroke = stroke;
        this.visible = true;
        this.width = (int) (dpToPx(resources, 40.0f) * i2);
        this.height = (int) (dpToPx(resources, 40.0f) * i2);
        this.iconWidth = dpToPx(resources, 20.0f) * i2;
        this.circleRadius = dpToPx(resources, 18.0f) * i2;
        this.strokeWidth = dpToPx(resources, stroke.strokeWidth) * i2;
        this.sidePadding = (this.width - this.iconWidth) / TRANSFORMATION_END;
        this.topPadding = (this.height - (5.0f * this.dip3)) / TRANSFORMATION_END;
        initPaint(i);
        initAnimations(i3);
        this.materialMenuState = new MaterialMenuState();
    }

    private MaterialMenuDrawable(int i, Stroke stroke, long j, int i2, int i3, float f, float f2, float f3, float f4) {
        this.lock = new Object();
        this.iconPaint = new Paint();
        this.circlePaint = new Paint();
        this.transformationValue = 0.0f;
        this.transformationRunning = false;
        this.currentIconState = IconState.BURGER;
        this.animationState = AnimationState.BURGER_ARROW;
        this.transformationProperty = new Property<MaterialMenuDrawable, Float>(Float.class, "transformation") { // from class: com.instabug.library.ui.custom.MaterialMenuDrawable.1
            @Override // android.util.Property
            public Float get(MaterialMenuDrawable materialMenuDrawable) {
                return materialMenuDrawable.getTransformationValue();
            }

            @Override // android.util.Property
            public void set(MaterialMenuDrawable materialMenuDrawable, Float f5) {
                materialMenuDrawable.setTransformationValue(f5);
            }
        };
        this.dip1 = f4;
        this.dip2 = f4 * TRANSFORMATION_END;
        this.dip3 = 3.0f * f4;
        this.dip4 = 4.0f * f4;
        this.dip8 = 8.0f * f4;
        this.diph = f4 / TRANSFORMATION_END;
        this.stroke = stroke;
        this.width = i2;
        this.height = i3;
        this.iconWidth = f;
        this.circleRadius = f2;
        this.strokeWidth = f3;
        this.sidePadding = (i2 - f) / TRANSFORMATION_END;
        this.topPadding = (i3 - (5.0f * this.dip3)) / TRANSFORMATION_END;
        initPaint(i);
        initAnimations((int) j);
        this.materialMenuState = new MaterialMenuState();
    }

    private void initPaint(int i) {
        this.iconPaint.setAntiAlias(true);
        this.iconPaint.setStyle(Paint.Style.STROKE);
        this.iconPaint.setStrokeWidth(this.strokeWidth);
        this.iconPaint.setColor(i);
        this.circlePaint.setAntiAlias(true);
        this.circlePaint.setStyle(Paint.Style.FILL);
        this.circlePaint.setColor(i);
        this.circlePaint.setAlpha(200);
        setBounds(0, 0, this.width, this.height);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.visible) {
            float f = this.transformationValue <= TRANSFORMATION_MID ? this.transformationValue : TRANSFORMATION_END - this.transformationValue;
            if (this.rtlEnabled) {
                canvas.save();
                canvas.scale(-1.0f, TRANSFORMATION_MID, 0.0f, 0.0f);
                canvas.translate(-getIntrinsicWidth(), 0.0f);
            }
            drawTopLine(canvas, f);
            drawMiddleLine(canvas, f);
            drawBottomLine(canvas, f);
            if (this.rtlEnabled) {
                canvas.restore();
            }
        }
    }

    private void drawMiddleLine(Canvas canvas, float f) {
        int i;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        canvas.restore();
        canvas.save();
        float f7 = this.width / 2;
        float f8 = this.width / 2;
        float f9 = this.sidePadding;
        float f10 = this.topPadding + ((this.dip3 / TRANSFORMATION_END) * 5.0f);
        float f11 = this.width - this.sidePadding;
        float f12 = this.topPadding + ((this.dip3 / TRANSFORMATION_END) * 5.0f);
        switch (this.animationState) {
            case BURGER_ARROW:
                if (isMorphingForward()) {
                    f6 = ARROW_MID_LINE_ANGLE * f;
                } else {
                    f6 = ARROW_MID_LINE_ANGLE + ((TRANSFORMATION_MID - f) * ARROW_MID_LINE_ANGLE);
                }
                f11 -= (resolveStrokeModifier(f) * f) / TRANSFORMATION_END;
                f3 = f9;
                f4 = f7;
                f2 = f6;
                i = 255;
                break;
            case BURGER_X:
                i = (int) ((TRANSFORMATION_MID - f) * 255.0f);
                f3 = f9;
                f4 = f7;
                f2 = 0.0f;
                break;
            case ARROW_X:
                i = (int) ((TRANSFORMATION_MID - f) * 255.0f);
                f3 = ((TRANSFORMATION_MID - f) * this.dip2) + f9;
                f4 = f7;
                f2 = 0.0f;
                break;
            case ARROW_CHECK:
                if (isMorphingForward()) {
                    f5 = 135.0f * f;
                } else {
                    f5 = 135.0f - (135.0f * (TRANSFORMATION_MID - f));
                }
                float f13 = f9 + (((this.dip3 / TRANSFORMATION_END) + this.dip4) - ((TRANSFORMATION_MID - f) * this.dip2));
                f11 += this.dip1 * f;
                f3 = f13;
                f4 = (this.width / 2) + this.dip3 + this.diph;
                f2 = f5;
                i = 255;
                break;
            case BURGER_CHECK:
                f2 = f * 135.0f;
                float f14 = ((this.dip4 + (this.dip3 / TRANSFORMATION_END)) * f) + f9;
                f11 += this.dip1 * f;
                f4 = (this.width / 2) + this.dip3 + this.diph;
                f3 = f14;
                i = 255;
                break;
            case X_CHECK:
                i = (int) (255.0f * f);
                f2 = f * 135.0f;
                f3 = ((this.dip4 + (this.dip3 / TRANSFORMATION_END)) * f) + f9;
                f11 += this.dip1 * f;
                f4 = (this.width / 2) + this.dip3 + this.diph;
                break;
            default:
                i = 255;
                f3 = f9;
                f4 = f7;
                f2 = 0.0f;
                break;
        }
        this.iconPaint.setAlpha(i);
        canvas.rotate(f2, f4, f8);
        canvas.drawLine(f3, f10, f11, f12, this.iconPaint);
        this.iconPaint.setAlpha(255);
    }

    private void drawTopLine(Canvas canvas, float f) {
        float f2;
        int i;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        canvas.save();
        float f8 = (this.width / 2) + (this.dip3 / TRANSFORMATION_END);
        float f9 = this.topPadding + this.dip2;
        float f10 = this.sidePadding;
        float f11 = this.topPadding + this.dip2;
        float f12 = this.width - this.sidePadding;
        float f13 = this.topPadding + this.dip2;
        switch (this.animationState) {
            case BURGER_ARROW:
                if (isMorphingForward()) {
                    f7 = ARROW_BOT_LINE_ANGLE * f;
                } else {
                    f7 = ARROW_BOT_LINE_ANGLE + ((TRANSFORMATION_MID - f) * 135.0f);
                }
                float f14 = this.width / 2;
                float f15 = this.height / 2;
                f12 -= resolveStrokeModifier(f);
                f2 = f10 + (this.dip3 * f);
                f3 = 0.0f;
                f4 = f15;
                f5 = f14;
                f6 = f7;
                i = 255;
                break;
            case BURGER_X:
                float f16 = X_TOP_LINE_ANGLE * f;
                float f17 = X_ROTATION_ANGLE * f;
                float f18 = this.sidePadding + this.dip4;
                float f19 = this.dip3 + this.topPadding;
                float f20 = (this.dip3 * f) + f10;
                f3 = f17;
                f4 = f19;
                f5 = f18;
                f6 = f16;
                f2 = f20;
                i = 255;
                break;
            case ARROW_X:
                float f21 = ARROW_BOT_LINE_ANGLE + ((-181.0f) * f);
                float f22 = X_ROTATION_ANGLE * f;
                float f23 = (this.width / 2) + (((this.sidePadding + this.dip4) - (this.width / 2)) * f);
                float f24 = (((this.topPadding + this.dip3) - (this.height / 2)) * f) + (this.height / 2);
                f12 -= resolveStrokeModifier(f);
                float f25 = this.dip3 + f10;
                f3 = f22;
                f4 = f24;
                f5 = f23;
                f6 = f21;
                f2 = f25;
                i = 255;
                break;
            case ARROW_CHECK:
                i = (int) ((TRANSFORMATION_MID - f) * 255.0f);
                float f26 = this.width / 2;
                float f27 = this.height / 2;
                f12 -= resolveStrokeModifier(TRANSFORMATION_MID);
                f2 = this.dip3 + f10;
                f3 = 0.0f;
                f4 = f27;
                f5 = f26;
                f6 = 225.0f;
                break;
            case BURGER_CHECK:
                i = (int) ((TRANSFORMATION_MID - f) * 255.0f);
                f2 = f10;
                f3 = 0.0f;
                f4 = 0.0f;
                f5 = 0.0f;
                f6 = 0.0f;
                break;
            case X_CHECK:
                float f28 = this.sidePadding + this.dip4;
                float f29 = this.topPadding + this.dip3;
                f12 += this.dip3 - (this.dip3 * (TRANSFORMATION_MID - f));
                f2 = f10 + this.dip3;
                i = (int) ((TRANSFORMATION_MID - f) * 255.0f);
                f3 = 90.0f;
                f4 = f29;
                f5 = f28;
                f6 = 44.0f;
                break;
            default:
                i = 255;
                f2 = f10;
                f3 = 0.0f;
                f4 = 0.0f;
                f5 = 0.0f;
                f6 = 0.0f;
                break;
        }
        this.iconPaint.setAlpha(i);
        canvas.rotate(f6, f5, f4);
        canvas.rotate(f3, f8, f9);
        canvas.drawLine(f2, f11, f12, f13, this.iconPaint);
        this.iconPaint.setAlpha(255);
    }

    private void drawBottomLine(Canvas canvas, float f) {
        float f2;
        float f3;
        float f4;
        canvas.restore();
        canvas.save();
        float f5 = 0.0f;
        float f6 = 0.0f;
        float f7 = (this.width / 2) + (this.dip3 / TRANSFORMATION_END);
        float f8 = (this.height - this.topPadding) - this.dip2;
        float f9 = this.sidePadding;
        float f10 = (this.height - this.topPadding) - this.dip2;
        float f11 = this.width - this.sidePadding;
        float f12 = (this.height - this.topPadding) - this.dip2;
        switch (this.animationState) {
            case BURGER_ARROW:
                if (isMorphingForward()) {
                    f4 = 135.0f * f;
                } else {
                    f4 = 135.0f + ((TRANSFORMATION_MID - f) * ARROW_BOT_LINE_ANGLE);
                }
                float f13 = this.width / 2;
                float f14 = this.height / 2;
                f11 = (this.width - this.sidePadding) - resolveStrokeModifier(f);
                f9 = this.sidePadding + (this.dip3 * f);
                f3 = f14;
                f6 = f13;
                f5 = f4;
                f2 = 0.0f;
                break;
            case BURGER_X:
                if (isMorphingForward()) {
                    f2 = CHECK_BOTTOM_ANGLE * f;
                } else {
                    f2 = X_ROTATION_ANGLE * f;
                }
                f5 = X_BOT_LINE_ANGLE * f;
                f6 = this.dip4 + this.sidePadding;
                f3 = (this.height - this.topPadding) - this.dip3;
                f9 += this.dip3 * f;
                break;
            case ARROW_X:
                f5 = 135.0f + (181.0f * f);
                f2 = CHECK_BOTTOM_ANGLE * f;
                f6 = (((this.sidePadding + this.dip4) - (this.width / 2)) * f) + (this.width / 2);
                f3 = (this.height / 2) + ((((this.height / 2) - this.topPadding) - this.dip3) * f);
                f11 -= resolveStrokeModifier(f);
                f9 += this.dip3;
                break;
            case ARROW_CHECK:
                f5 = 135.0f + (CHECK_BOTTOM_ANGLE * f);
                f6 = (this.dip3 * f) + (this.width / 2);
                float f15 = (this.height / 2) - (this.dip3 * f);
                f11 -= resolveStrokeModifier(TRANSFORMATION_MID);
                f9 += this.dip3 + ((this.dip4 + this.dip1) * f);
                f3 = f15;
                f2 = 0.0f;
                break;
            case BURGER_CHECK:
                f5 = f * 45.0f;
                f6 = (this.dip3 * f) + (this.width / 2);
                float f16 = (this.height / 2) - (this.dip3 * f);
                f9 += this.dip8 * f;
                f11 -= resolveStrokeModifier(f);
                f3 = f16;
                f2 = 0.0f;
                break;
            case X_CHECK:
                f2 = CHECK_BOTTOM_ANGLE * (TRANSFORMATION_MID - f);
                f5 = X_BOT_LINE_ANGLE + (89.0f * f);
                f6 = (((((this.width / 2) + this.dip3) - this.sidePadding) - this.dip4) * f) + this.sidePadding + this.dip4;
                f3 = ((this.height - this.topPadding) - this.dip3) + (((this.topPadding + (this.height / 2)) - this.height) * f);
                f9 += this.dip8 - ((this.dip4 + this.dip1) * (TRANSFORMATION_MID - f));
                f11 -= resolveStrokeModifier(TRANSFORMATION_MID - f);
                break;
            default:
                f3 = 0.0f;
                f2 = 0.0f;
                break;
        }
        canvas.rotate(f5, f6, f3);
        canvas.rotate(f2, f7, f8);
        canvas.drawLine(f9, f10, f11, f12, this.iconPaint);
    }

    private boolean isMorphingForward() {
        return this.transformationValue <= TRANSFORMATION_MID;
    }

    private float resolveStrokeModifier(float f) {
        switch (this.stroke) {
            case REGULAR:
                if (this.animationState == AnimationState.ARROW_X || this.animationState == AnimationState.X_CHECK) {
                    return this.dip3 - (this.dip3 * f);
                }
                return this.dip3 * f;
            case THIN:
                if (this.animationState == AnimationState.ARROW_X || this.animationState == AnimationState.X_CHECK) {
                    return (this.dip3 + this.diph) - ((this.dip3 + this.diph) * f);
                }
                return (this.dip3 + this.diph) * f;
            case EXTRA_THIN:
                if (this.animationState == AnimationState.ARROW_X || this.animationState == AnimationState.X_CHECK) {
                    return this.dip4 - ((this.dip3 + this.dip1) * f);
                }
                return this.dip4 * f;
            default:
                return 0.0f;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.iconPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.iconPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public void setColor(int i) {
        this.iconPaint.setColor(i);
        this.circlePaint.setColor(i);
        invalidateSelf();
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    @RequiresApi(api = 11)
    public void setTransformationDuration(int i) {
        this.transformation.setDuration(i);
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public void setInterpolator(Interpolator interpolator) {
        this.transformation.setInterpolator(interpolator);
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public void setAnimationListener(Animator.AnimatorListener animatorListener) {
        if (this.animatorListener != null) {
            this.transformation.removeListener(this.animatorListener);
        }
        if (animatorListener != null) {
            this.transformation.addListener(animatorListener);
        }
        this.animatorListener = animatorListener;
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public void setIconState(IconState iconState) {
        synchronized (this.lock) {
            if (this.transformationRunning) {
                this.transformation.cancel();
                this.transformationRunning = false;
            }
            if (this.currentIconState != iconState) {
                switch (iconState) {
                    case BURGER:
                        this.animationState = AnimationState.BURGER_ARROW;
                        this.transformationValue = 0.0f;
                        break;
                    case ARROW:
                        this.animationState = AnimationState.BURGER_ARROW;
                        this.transformationValue = TRANSFORMATION_MID;
                        break;
                    case X:
                        this.animationState = AnimationState.BURGER_X;
                        this.transformationValue = TRANSFORMATION_MID;
                        break;
                    case CHECK:
                        this.animationState = AnimationState.BURGER_CHECK;
                        this.transformationValue = TRANSFORMATION_MID;
                        break;
                }
                this.currentIconState = iconState;
                invalidateSelf();
            }
        }
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public void animateIconState(IconState iconState) {
        synchronized (this.lock) {
            if (this.transformationRunning) {
                this.transformation.end();
            }
            this.animatingIconState = iconState;
            start();
        }
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public IconState setTransformationOffset(AnimationState animationState, float f) {
        boolean z = true;
        if (f < 0.0f || f > TRANSFORMATION_END) {
            throw new IllegalArgumentException(String.format("Value must be between %s and %s", Float.valueOf(0.0f), Float.valueOf((float) TRANSFORMATION_END)));
        }
        this.animationState = animationState;
        if (f >= TRANSFORMATION_MID && f != TRANSFORMATION_END) {
            z = false;
        }
        this.currentIconState = z ? animationState.getFirstState() : animationState.getSecondState();
        this.animatingIconState = z ? animationState.getSecondState() : animationState.getFirstState();
        setTransformationValue(Float.valueOf(f));
        return this.currentIconState;
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public void setVisible(boolean z) {
        this.visible = z;
        invalidateSelf();
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public void setRTLEnabled(boolean z) {
        this.rtlEnabled = z;
        invalidateSelf();
    }

    @Override // com.instabug.library.p031ui.custom.MaterialMenu
    public IconState getIconState() {
        return this.currentIconState;
    }

    public boolean isDrawableVisible() {
        return this.visible;
    }

    public Float getTransformationValue() {
        return Float.valueOf(this.transformationValue);
    }

    public void setTransformationValue(Float f) {
        this.transformationValue = f.floatValue();
        invalidateSelf();
    }

    private void initAnimations(int i) {
        this.transformation = ObjectAnimator.ofFloat(this, this.transformationProperty, 0.0f);
        this.transformation.setInterpolator(new DecelerateInterpolator(3.0f));
        this.transformation.setDuration(i);
        this.transformation.addListener(new AnimatorListenerAdapter() { // from class: com.instabug.library.ui.custom.MaterialMenuDrawable.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                MaterialMenuDrawable.this.transformationRunning = false;
                MaterialMenuDrawable.this.setIconState(MaterialMenuDrawable.this.animatingIconState);
            }
        });
    }

    private boolean resolveTransformation() {
        boolean z = this.currentIconState == IconState.BURGER;
        boolean z2 = this.currentIconState == IconState.ARROW;
        boolean z3 = this.currentIconState == IconState.X;
        boolean z4 = this.currentIconState == IconState.CHECK;
        boolean z5 = this.animatingIconState == IconState.BURGER;
        boolean z6 = this.animatingIconState == IconState.ARROW;
        boolean z7 = this.animatingIconState == IconState.X;
        boolean z8 = this.animatingIconState == IconState.CHECK;
        if ((z && z6) || (z2 && z5)) {
            this.animationState = AnimationState.BURGER_ARROW;
            return z;
        }
        if ((z2 && z7) || (z3 && z6)) {
            this.animationState = AnimationState.ARROW_X;
            return z2;
        }
        if ((z && z7) || (z3 && z5)) {
            this.animationState = AnimationState.BURGER_X;
            return z;
        }
        if ((z2 && z8) || (z4 && z6)) {
            this.animationState = AnimationState.ARROW_CHECK;
            return z2;
        }
        if ((z && z8) || (z4 && z5)) {
            this.animationState = AnimationState.BURGER_CHECK;
            return z;
        }
        if ((z3 && z8) || (z4 && z7)) {
            this.animationState = AnimationState.X_CHECK;
            return z3;
        }
        throw new IllegalStateException(String.format("Animating from %s to %s is not supported", this.currentIconState, this.animatingIconState));
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        float f = TRANSFORMATION_MID;
        if (!this.transformationRunning) {
            if (this.animatingIconState != null && this.animatingIconState != this.currentIconState) {
                this.transformationRunning = true;
                boolean resolveTransformation = resolveTransformation();
                ObjectAnimator objectAnimator = this.transformation;
                float[] fArr = new float[2];
                fArr[0] = resolveTransformation ? 0.0f : 1.0f;
                if (!resolveTransformation) {
                    f = TRANSFORMATION_END;
                }
                fArr[1] = f;
                objectAnimator.setFloatValues(fArr);
                this.transformation.start();
            }
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        if (isRunning() && this.transformation.isRunning()) {
            this.transformation.end();
        } else {
            this.transformationRunning = false;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.transformationRunning;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.width;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.height;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.materialMenuState.changingConfigurations = getChangingConfigurations();
        return this.materialMenuState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        this.materialMenuState = new MaterialMenuState();
        return this;
    }

    /* loaded from: classes.dex */
    private final class MaterialMenuState extends Drawable.ConstantState {
        private int changingConfigurations;

        private MaterialMenuState() {
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            MaterialMenuDrawable materialMenuDrawable = new MaterialMenuDrawable(MaterialMenuDrawable.this.circlePaint.getColor(), MaterialMenuDrawable.this.stroke, MaterialMenuDrawable.this.transformation.getDuration(), MaterialMenuDrawable.this.width, MaterialMenuDrawable.this.height, MaterialMenuDrawable.this.iconWidth, MaterialMenuDrawable.this.circleRadius, MaterialMenuDrawable.this.strokeWidth, MaterialMenuDrawable.this.dip1);
            materialMenuDrawable.setIconState(MaterialMenuDrawable.this.animatingIconState != null ? MaterialMenuDrawable.this.animatingIconState : MaterialMenuDrawable.this.currentIconState);
            materialMenuDrawable.setVisible(MaterialMenuDrawable.this.visible);
            materialMenuDrawable.setRTLEnabled(MaterialMenuDrawable.this.rtlEnabled);
            return materialMenuDrawable;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.changingConfigurations;
        }
    }

    static float dpToPx(Resources resources, float f) {
        return TypedValue.applyDimension(1, f, resources.getDisplayMetrics());
    }
}
