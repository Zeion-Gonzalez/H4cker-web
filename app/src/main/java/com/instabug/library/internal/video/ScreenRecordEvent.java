package com.instabug.library.internal.video;

import android.net.Uri;

/* loaded from: classes.dex */
public class ScreenRecordEvent {
    public static final int ENCODED = 1;
    public static final int ENCODED_FAILED = 2;
    public static final int SNAPPED_AND_ENCODING = 0;
    private int status;
    private Uri videoUri;

    public ScreenRecordEvent(int i, Uri uri) {
        this.status = i;
        this.videoUri = uri;
    }

    public int getStatus() {
        return this.status;
    }

    public Uri getVideoUri() {
        return this.videoUri;
    }
}
