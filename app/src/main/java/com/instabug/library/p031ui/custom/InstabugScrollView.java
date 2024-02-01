package com.instabug.library.p031ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/* loaded from: classes.dex */
public class InstabugScrollView extends ScrollView {
    OnScrollListener onScrollListener;

    /* loaded from: classes.dex */
    public interface OnScrollListener {
        void onBottomReached();

        void onScrolling();

        void onTopReached();
    }

    public InstabugScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public InstabugScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public InstabugScrollView(Context context) {
        super(context);
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        int bottom = getChildAt(getChildCount() - 1).getBottom() - (getHeight() + getScrollY());
        if (bottom == 0 && this.onScrollListener != null) {
            this.onScrollListener.onBottomReached();
        } else if (getScrollY() == 0 && this.onScrollListener != null) {
            this.onScrollListener.onTopReached();
        } else if (bottom > 0 && this.onScrollListener != null) {
            this.onScrollListener.onScrolling();
        }
        super.onScrollChanged(i, i2, i3, i4);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }
}
