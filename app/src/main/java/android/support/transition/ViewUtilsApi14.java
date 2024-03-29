package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewParent;

@RequiresApi(14)
/* loaded from: classes.dex */
class ViewUtilsApi14 implements ViewUtilsImpl {
    private float[] mMatrixValues;

    @Override // android.support.transition.ViewUtilsImpl
    public ViewOverlayImpl getOverlay(@NonNull View view) {
        return ViewOverlayApi14.createFrom(view);
    }

    @Override // android.support.transition.ViewUtilsImpl
    public WindowIdImpl getWindowId(@NonNull View view) {
        return new WindowIdApi14(view.getWindowToken());
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void setTransitionAlpha(@NonNull View view, float alpha) {
        Float savedAlpha = (Float) view.getTag(C0104R.id.save_non_transition_alpha);
        if (savedAlpha != null) {
            view.setAlpha(savedAlpha.floatValue() * alpha);
        } else {
            view.setAlpha(alpha);
        }
    }

    @Override // android.support.transition.ViewUtilsImpl
    public float getTransitionAlpha(@NonNull View view) {
        Float savedAlpha = (Float) view.getTag(C0104R.id.save_non_transition_alpha);
        return savedAlpha != null ? view.getAlpha() / savedAlpha.floatValue() : view.getAlpha();
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void saveNonTransitionAlpha(@NonNull View view) {
        if (view.getTag(C0104R.id.save_non_transition_alpha) == null) {
            view.setTag(C0104R.id.save_non_transition_alpha, Float.valueOf(view.getAlpha()));
        }
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void clearNonTransitionAlpha(@NonNull View view) {
        if (view.getVisibility() == 0) {
            view.setTag(C0104R.id.save_non_transition_alpha, null);
        }
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void transformMatrixToGlobal(@NonNull View view, @NonNull Matrix matrix) {
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            View vp = (View) parent;
            transformMatrixToGlobal(vp, matrix);
            matrix.preTranslate(-vp.getScrollX(), -vp.getScrollY());
        }
        matrix.preTranslate(view.getLeft(), view.getTop());
        Matrix vm = view.getMatrix();
        if (!vm.isIdentity()) {
            matrix.preConcat(vm);
        }
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void transformMatrixToLocal(@NonNull View view, @NonNull Matrix matrix) {
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            View vp = (View) parent;
            transformMatrixToLocal(vp, matrix);
            matrix.postTranslate(vp.getScrollX(), vp.getScrollY());
        }
        matrix.postTranslate(view.getLeft(), view.getTop());
        Matrix vm = view.getMatrix();
        if (!vm.isIdentity()) {
            Matrix inverted = new Matrix();
            if (vm.invert(inverted)) {
                matrix.postConcat(inverted);
            }
        }
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void setAnimationMatrix(@NonNull View view, Matrix matrix) {
        if (matrix == null || matrix.isIdentity()) {
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(view.getHeight() / 2);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            view.setRotation(0.0f);
            return;
        }
        float[] values = this.mMatrixValues;
        if (values == null) {
            values = new float[9];
            this.mMatrixValues = values;
        }
        matrix.getValues(values);
        float sin = values[3];
        float cos = ((float) Math.sqrt(1.0f - (sin * sin))) * (values[0] < 0.0f ? -1 : 1);
        float rotation = (float) Math.toDegrees(Math.atan2(sin, cos));
        float scaleX = values[0] / cos;
        float scaleY = values[4] / cos;
        float dx = values[2];
        float dy = values[5];
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.setTranslationX(dx);
        view.setTranslationY(dy);
        view.setRotation(rotation);
        view.setScaleX(scaleX);
        view.setScaleY(scaleY);
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void setLeftTopRightBottom(View v, int left, int top, int right, int bottom) {
        v.setLeft(left);
        v.setTop(top);
        v.setRight(right);
        v.setBottom(bottom);
    }
}
