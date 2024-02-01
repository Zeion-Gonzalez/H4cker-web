package com.swift.sandhook.utils;

import android.support.v4.app.NotificationCompat;
import com.swift.sandhook.SandHookConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/* loaded from: classes2.dex */
public class ClassStatusUtils {
    static Field fieldStatusOfClass;

    public static long toUnsignedLong(int i) {
        return i & 4294967295L;
    }

    static {
        try {
            fieldStatusOfClass = Class.class.getDeclaredField(NotificationCompat.CATEGORY_STATUS);
            fieldStatusOfClass.setAccessible(true);
        } catch (NoSuchFieldException unused) {
        }
    }

    public static int getClassStatus(Class cls, boolean z) {
        int i = 0;
        if (cls == null) {
            return 0;
        }
        try {
            i = fieldStatusOfClass.getInt(cls);
        } catch (Throwable unused) {
        }
        return z ? (int) (toUnsignedLong(i) >> 28) : i;
    }

    public static boolean isInitialized(Class cls) {
        if (fieldStatusOfClass == null) {
            return true;
        }
        return SandHookConfig.SDK_INT >= 28 ? getClassStatus(cls, true) == 14 : SandHookConfig.SDK_INT == 27 ? getClassStatus(cls, false) == 11 : getClassStatus(cls, false) == 10;
    }

    public static boolean isStaticAndNoInited(Member member) {
        if (member == null || (member instanceof Constructor)) {
            return false;
        }
        return Modifier.isStatic(member.getModifiers()) && !isInitialized(member.getDeclaringClass());
    }
}
