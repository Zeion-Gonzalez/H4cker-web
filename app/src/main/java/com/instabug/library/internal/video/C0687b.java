package com.instabug.library.internal.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.v4.view.PointerIconCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.instabug.library.internal.storage.AttachmentManager;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: RecordingSession.java */
@TargetApi(21)
/* renamed from: com.instabug.library.internal.video.b */
/* loaded from: classes.dex */
public final class C0687b {

    /* renamed from: a */
    private final Context f883a;

    /* renamed from: b */
    private final a f884b;

    /* renamed from: c */
    private final int f885c;

    /* renamed from: d */
    private final Intent f886d;

    /* renamed from: e */
    private final File f887e;

    /* renamed from: f */
    private final MediaProjectionManager f888f;

    /* renamed from: g */
    private MediaRecorder f889g;

    /* renamed from: h */
    private MediaProjection f890h;

    /* renamed from: i */
    private VirtualDisplay f891i;

    /* renamed from: j */
    private String f892j;

    /* renamed from: k */
    private boolean f893k;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: RecordingSession.java */
    /* renamed from: com.instabug.library.internal.video.b$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo1328a();

        /* renamed from: b */
        void mo1329b();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0687b(Context context, a aVar, int i, Intent intent) {
        this.f883a = context;
        this.f884b = aVar;
        this.f885c = i;
        this.f886d = intent;
        this.f887e = AttachmentManager.getAutoScreenRecordingVideosDirectory(context);
        this.f888f = (MediaProjectionManager) context.getSystemService("media_projection");
        m1351e();
    }

    /* renamed from: d */
    private b m1350d() {
        CamcorderProfile camcorderProfile;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) this.f883a.getSystemService("window")).getDefaultDisplay().getRealMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        int i3 = displayMetrics.densityDpi;
        boolean z = this.f883a.getResources().getConfiguration().orientation == 2;
        if (CamcorderProfile.hasProfile(PointerIconCompat.TYPE_CROSSHAIR)) {
            camcorderProfile = CamcorderProfile.get(PointerIconCompat.TYPE_CROSSHAIR);
        } else {
            camcorderProfile = CamcorderProfile.get(0);
        }
        return m1347a(i, i2, i3, z, camcorderProfile != null ? camcorderProfile.videoFrameWidth : -1, camcorderProfile != null ? camcorderProfile.videoFrameHeight : -1, camcorderProfile != null ? camcorderProfile.videoFrameRate : 30, 50);
    }

    /* renamed from: e */
    private void m1351e() {
        if (!this.f887e.exists() && !this.f887e.mkdirs()) {
            InstabugSDKLogger.m1799d(this, "Unable to create output directory.\nCannot record screen.");
            return;
        }
        b m1350d = m1350d();
        this.f889g = new MediaRecorder();
        this.f889g.setVideoSource(2);
        this.f889g.setOutputFormat(2);
        this.f889g.setVideoFrameRate(m1350d.f899c);
        this.f889g.setVideoEncoder(2);
        this.f889g.setVideoSize(m1350d.f897a, m1350d.f898b);
        this.f889g.setVideoEncodingBitRate(8000000);
        this.f892j = AttachmentManager.getAutoScreenRecordingFile(this.f883a).getAbsolutePath();
        this.f889g.setOutputFile(this.f892j);
        try {
            this.f889g.prepare();
            this.f890h = this.f888f.getMediaProjection(this.f885c, this.f886d);
            this.f891i = this.f890h.createVirtualDisplay(SettingsManager.INSTABUG_SHARED_PREF_NAME, m1350d.f897a, m1350d.f898b, m1350d.f900d, 2, this.f889g.getSurface(), null, null);
            this.f889g.start();
            this.f893k = true;
            this.f884b.mo1328a();
            InstabugSDKLogger.m1802i(this, "Screen recording started");
        } catch (IOException e) {
            throw new RuntimeException("Unable to prepare MediaRecorder.", e);
        }
    }

    /* renamed from: f */
    private void m1352f() {
        if (!this.f893k) {
            throw new IllegalStateException("Not running.");
        }
        this.f893k = false;
        try {
            this.f890h.stop();
            this.f889g.stop();
            this.f889g.reset();
            try {
                this.f889g.release();
                this.f891i.release();
                InstabugSDKLogger.m1799d(this, "Screen recording stopped");
            } catch (RuntimeException e) {
                throw e;
            }
        } finally {
            try {
                this.f884b.mo1329b();
            } catch (RuntimeException e2) {
            }
        }
    }

    /* renamed from: a */
    static b m1347a(int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7) {
        int i8 = (i * i7) / 100;
        int i9 = (i2 * i7) / 100;
        if (i4 == -1 && i5 == -1) {
            return new b(i8, i9, i6, i3);
        }
        int i10 = z ? i4 : i5;
        int i11 = z ? i5 : i4;
        if (i10 >= i8 && i11 >= i9) {
            return new b(i8, i9, i6, i3);
        }
        if (z) {
            i10 = (i8 * i11) / i9;
        } else {
            i11 = (i9 * i10) / i8;
        }
        return new b(i10, i11, i6, i3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: RecordingSession.java */
    /* renamed from: com.instabug.library.internal.video.b$b */
    /* loaded from: classes.dex */
    public static final class b {

        /* renamed from: a */
        final int f897a;

        /* renamed from: b */
        final int f898b;

        /* renamed from: c */
        final int f899c;

        /* renamed from: d */
        final int f900d;

        b(int i, int i2, int i3, int i4) {
            this.f897a = i;
            this.f898b = i2;
            this.f899c = i3;
            this.f900d = i4;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1353a() {
        if (this.f893k) {
            m1352f();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public void m1355b() {
        new Thread(new Runnable() { // from class: com.instabug.library.internal.video.b.1
            @Override // java.lang.Runnable
            public void run() {
                if (new File(C0687b.this.f892j).delete()) {
                    InstabugSDKLogger.m1799d(this, "Screen recording file deleted");
                }
            }
        }).start();
    }

    /* renamed from: c */
    public void m1356c() {
        InternalAutoScreenRecorderHelper.getInstance().setAutoScreenRecordingFile(new File(this.f892j));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1354a(final int i) {
        new Thread(new Runnable() { // from class: com.instabug.library.internal.video.b.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    InternalAutoScreenRecorderHelper.getInstance().setAutoScreenRecordingFile(InstabugVideoUtils.startTrim(new File(C0687b.this.f892j), AttachmentManager.getAutoScreenRecordingFile(C0687b.this.f883a), i));
                } catch (IOException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
