package com.instabug.library.p023c;

import android.content.Context;
import android.support.annotation.Nullable;
import com.instabug.library.C0645d;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* compiled from: InstabugCrash.java */
/* renamed from: com.instabug.library.c.c */
/* loaded from: classes.dex */
public class C0637c {
    /* renamed from: a */
    public static void m1224a(Context context, Throwable th, @Nullable String str) {
        try {
            Method m1247a = C0645d.m1247a(m1223a(), "reportCaughtException");
            if (m1247a != null) {
                m1247a.invoke(null, context, th, str);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    public static void m1225a(Runnable runnable) throws IllegalStateException {
        try {
            Method m1247a = C0645d.m1247a(m1223a(), "setPreSendingRunnable");
            if (m1247a != null) {
                m1247a.invoke(null, runnable);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    private static Class m1223a() throws ClassNotFoundException {
        return Class.forName("com.instabug.crash.InstabugCrash");
    }
}
