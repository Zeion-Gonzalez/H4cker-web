package com.instabug.library.p031ui.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/* loaded from: classes.dex */
public class InstabugViewPager extends ViewPager {
    private View mCurrentView;
    private boolean swipeable;

    public InstabugViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.swipeable = true;
    }

    @Override // android.support.v4.view.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.swipeable) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.view.ViewPager, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    @Override // android.support.v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.swipeable) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    public void setSwipeable(boolean z) {
        this.swipeable = z;
    }
}
