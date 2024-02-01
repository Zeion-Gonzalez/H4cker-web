package com.instabug.library.instacapture.screenshot;

import android.view.View;
import android.view.WindowManager;

/* loaded from: classes.dex */
public class RootViewInfo {
    private final WindowManager.LayoutParams layoutParams;
    private final int left;
    private final int top;
    private final View view;

    public RootViewInfo(View view, WindowManager.LayoutParams layoutParams) {
        this.view = view;
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        this.left = iArr[0];
        this.top = iArr[1];
        this.layoutParams = layoutParams;
    }

    public View getView() {
        return this.view;
    }

    public int getTop() {
        return this.top;
    }

    public int getLeft() {
        return this.left;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return this.layoutParams;
    }
}
