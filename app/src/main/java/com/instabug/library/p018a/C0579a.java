package com.instabug.library.p018a;

import com.instabug.library.C0646e;
import com.instabug.library.internal.video.InternalAutoScreenRecorderHelper;
import java.lang.Thread;

/* compiled from: InstabugUncaughtExceptionHandler.java */
/* renamed from: com.instabug.library.a.a */
/* loaded from: classes.dex */
public class C0579a implements Thread.UncaughtExceptionHandler {

    /* renamed from: a */
    Thread.UncaughtExceptionHandler f590a = Thread.getDefaultUncaughtExceptionHandler();

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        if (InternalAutoScreenRecorderHelper.getInstance().isEnabled()) {
            InternalAutoScreenRecorderHelper.getInstance().setCrashOccurred(true);
        }
        C0646e.m1249a().m1267c();
        this.f590a.uncaughtException(thread, th);
    }
}
