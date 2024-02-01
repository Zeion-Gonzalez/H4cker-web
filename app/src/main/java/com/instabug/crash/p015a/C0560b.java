package com.instabug.crash.p015a;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.instabug.library.internal.video.AutoScreenRecordingContract;
import com.instabug.library.internal.video.InternalAutoScreenRecorderHelper;

/* compiled from: ExternalAutoScreenRecordHelper.java */
/* renamed from: com.instabug.crash.a.b */
/* loaded from: classes.dex */
public class C0560b implements AutoScreenRecordingContract {

    /* renamed from: a */
    private static C0560b f533a;

    /* renamed from: a */
    public static C0560b m917a() {
        if (f533a == null) {
            f533a = new C0560b();
        }
        return f533a;
    }

    private C0560b() {
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

    /* renamed from: b */
    public void m918b() {
        InternalAutoScreenRecorderHelper.getInstance().start();
    }
}
