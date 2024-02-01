package com.instabug.library.internal.video;

import android.net.Uri;
import android.support.annotation.Nullable;

/* loaded from: classes.dex */
public interface AutoScreenRecordingContract {
    void clear();

    void delete();

    @Nullable
    Uri getAutoScreenRecordingFileUri();

    boolean isEnabled();
}
