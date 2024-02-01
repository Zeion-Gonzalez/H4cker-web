package com.instabug.library.internal.media;

import android.media.MediaRecorder;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;

/* compiled from: AudioRecorder.java */
/* renamed from: com.instabug.library.internal.media.a */
/* loaded from: classes.dex */
public class C0664a {

    /* renamed from: a */
    private final String f841a;

    /* renamed from: b */
    private MediaRecorder f842b = null;

    public C0664a(String str) {
        this.f841a = str;
    }

    /* renamed from: a */
    public void m1293a() {
        this.f842b = new MediaRecorder();
        this.f842b.setAudioSource(1);
        this.f842b.setOutputFormat(2);
        this.f842b.setOutputFile(this.f841a);
        this.f842b.setAudioEncoder(3);
        try {
            this.f842b.prepare();
            this.f842b.start();
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(this, "Recording audio failed", e);
        }
    }

    /* renamed from: b */
    public void m1294b() {
        if (this.f842b != null) {
            try {
                this.f842b.stop();
                this.f842b.reset();
            } catch (RuntimeException e) {
            } finally {
                this.f842b.release();
                this.f842b = null;
            }
        }
    }
}
