package com.instabug.bug.p002b;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.instabug.library.internal.video.AutoScreenRecordingContract;
import com.instabug.library.internal.video.InternalAutoScreenRecorderHelper;

/* compiled from: ExternalAutoScreenRecordHelper.java */
/* renamed from: com.instabug.bug.b.a */
/* loaded from: classes.dex */
public class C0462a implements AutoScreenRecordingContract {

    /* renamed from: a */
    private static C0462a f62a;

    /* renamed from: a */
    public static C0462a m55a() {
        if (f62a == null) {
            f62a = new C0462a();
        }
        return f62a;
    }

    private C0462a() {
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    @Nullable
    public Uri getAutoScreenRecordingFileUri() {
        return InternalAutoScreenRecorderHelper.getInstance().getAutoScreenRecordingFileUri();
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    public void delete() {
        InternalAutoScreenRecorderHelper.getInstance().delete();
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    public void clear() {
        InternalAutoScreenRecorderHelper.getInstance().clear();
    }

    @Override // com.instabug.library.internal.video.AutoScreenRecordingContract
    public boolean isEnabled() {
        return InternalAutoScreenRecorderHelper.getInstance().isEnabled();
    }
}
