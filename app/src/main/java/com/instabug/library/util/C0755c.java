package com.instabug.library.util;

import android.content.Context;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import com.instabug.chat.model.Attachment;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;

/* compiled from: MicUtils.java */
/* renamed from: com.instabug.library.util.c */
/* loaded from: classes.dex */
public class C0755c {
    /* renamed from: a */
    public static void m1808a(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Attachment.TYPE_AUDIO);
        audioManager.setMode(0);
        if (!audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(true);
        }
    }

    /* renamed from: b */
    public static void m1810b(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Attachment.TYPE_AUDIO);
        audioManager.setMode(0);
        if (audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(false);
        }
    }

    /* renamed from: a */
    public static boolean m1809a() {
        return ContextCompat.checkSelfPermission(InstabugInternalTrackingDelegate.getInstance().getCurrentActivity(), "android.permission.RECORD_AUDIO") == 0;
    }
}
