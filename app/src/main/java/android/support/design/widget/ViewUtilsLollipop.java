package android.support.design.widget;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RequiresApi;
import android.support.design.C0018R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

@RequiresApi(21)
/* loaded from: classes.dex */
class ViewUtilsLollipop {
    private static final int[] STATE_LIST_ANIM_ATTRS = {16843848};

    ViewUtilsLollipop() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setBoundsViewOutlineProvider(View view) {
        view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setStateListAnimatorFromAttrs(View view, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Context context = view.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, STATE_LIST_ANIM_ATTRS, defStyleAttr, defStyleRes);
        try {
            if (a.hasValue(0)) {
                android.animation.StateListAnimator sla = AnimatorInflater.loadStateListAnimator(context, a.getResourceId(0, 0));
                view.setStateListAnimator(sla);
            }
        } finally {
            a.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setDefaultAppBarLayoutStateListAnimator(View view, float elevation) {
        int dur = view.getResources().getInteger(C0018R.integer.app_bar_elevation_anim_duration);
        android.animation.StateListAnimator sla = new android.animation.StateListAnimator();
        sla.addState(new int[]{16842766, C0018R.attr.state_collapsible, -C0018R.attr.state_collapsed}, ObjectAnimator.ofFloat(view, "elevation", 0.0f).setDuration(dur));
        sla.addState(new int[]{16842766}, ObjectAnimator.ofFloat(view, "elevation", elevation).setDuration(dur));
        sla.addState(new int[0], ObjectAnimator.ofFloat(view, "elevation", 0.0f).setDuration(0L));
        view.setStateListAnimator(sla);
    }
}
