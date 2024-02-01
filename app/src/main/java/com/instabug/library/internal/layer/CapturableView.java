package com.instabug.library.internal.layer;

import android.graphics.Bitmap;
import android.support.annotation.Size;

/* loaded from: classes.dex */
public interface CapturableView {

    /* loaded from: classes.dex */
    public interface SnapshotPreparationCallback {
        void onSnapshotFailed();

        void onSnapshotReady(Bitmap bitmap);
    }

    void getLocationOnScreen(@Size(2) int[] iArr);

    boolean isVisible();

    void snapshot(SnapshotPreparationCallback snapshotPreparationCallback);
}
