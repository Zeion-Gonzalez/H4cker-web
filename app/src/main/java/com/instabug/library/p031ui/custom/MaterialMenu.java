package com.instabug.library.p031ui.custom;

import android.animation.Animator;
import android.view.animation.Interpolator;
import com.instabug.library.p031ui.custom.MaterialMenuDrawable;

/* loaded from: classes.dex */
public interface MaterialMenu {
    void animateIconState(MaterialMenuDrawable.IconState iconState);

    MaterialMenuDrawable.IconState getIconState();

    void setAnimationListener(Animator.AnimatorListener animatorListener);

    void setColor(int i);

    void setIconState(MaterialMenuDrawable.IconState iconState);

    void setInterpolator(Interpolator interpolator);

    void setRTLEnabled(boolean z);

    void setTransformationDuration(int i);

    MaterialMenuDrawable.IconState setTransformationOffset(MaterialMenuDrawable.AnimationState animationState, float f);

    void setVisible(boolean z);
}
