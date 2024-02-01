package com.instabug.library.p031ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/* loaded from: classes.dex */
public class InstabugGridView extends GridView {
    public InstabugGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public InstabugGridView(Context context) {
        super(context);
    }

    public InstabugGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.widget.GridView, android.widget.AbsListView, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE));
    }
}
