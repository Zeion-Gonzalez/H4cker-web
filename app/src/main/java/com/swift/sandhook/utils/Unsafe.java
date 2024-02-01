package com.swift.sandhook.utils;

import com.applisto.appcloner.classes.util.Log;
import com.swift.sandhook.HookLog;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public final class Unsafe {
    private static final String TAG = "Unsafe";
    private static Method arrayBaseOffsetMethod = null;
    private static Method arrayIndexScaleMethod = null;
    private static Method getIntMethod = null;
    private static Method getLongMethod = null;
    private static Class objectArrayClass = Object[].class;
    private static volatile boolean supported = false;
    private static Object unsafe;
    private static Class unsafeClass;

    static {
        try {
            try {
                unsafeClass = Class.forName("sun.misc.Unsafe");
                Field declaredField = unsafeClass.getDeclaredField("theUnsafe");
                declaredField.setAccessible(true);
                unsafe = declaredField.get(null);
            } catch (Exception unused) {
                Log.m19w(TAG, "Unsafe not found o.O");
            }
        } catch (Exception unused2) {
            Field declaredField2 = unsafeClass.getDeclaredField("THE_ONE");
            declaredField2.setAccessible(true);
            unsafe = declaredField2.get(null);
        }
        if (unsafe != null) {
            try {
                arrayBaseOffsetMethod = unsafeClass.getDeclaredMethod("arrayBaseOffset", Class.class);
                arrayIndexScaleMethod = unsafeClass.getDeclaredMethod("arrayIndexScale", Class.class);
                getIntMethod = unsafeClass.getDeclaredMethod("getInt", Object.class, Long.TYPE);
                getLongMethod = unsafeClass.getDeclaredMethod("getLong", Object.class, Long.TYPE);
                supported = true;
            } catch (Exception unused3) {
            }
        }
    }

    public static boolean support() {
        return supported;
    }

    private Unsafe() {
    }

    public static int arrayBaseOffset(Class cls) {
        try {
            return ((Integer) arrayBaseOffsetMethod.invoke(unsafe, cls)).intValue();
        } catch (Exception unused) {
            return 0;
        }
    }

    public static int arrayIndexScale(Class cls) {
        try {
            return ((Integer) arrayIndexScaleMethod.invoke(unsafe, cls)).intValue();
        } catch (Exception unused) {
            return 0;
        }
    }

    public static int getInt(Object obj, long j) {
        try {
            return ((Integer) getIntMethod.invoke(unsafe, obj, Long.valueOf(j))).intValue();
        } catch (Exception unused) {
            return 0;
        }
    }

    public static long getLong(Object obj, long j) {
        try {
            return ((Long) getLongMethod.invoke(unsafe, obj, Long.valueOf(j))).longValue();
        } catch (Exception unused) {
            return 0L;
        }
    }

    public static long getObjectAddress(Object obj) {
        try {
            Object[] objArr = {obj};
            if (arrayIndexScale(objectArrayClass) == 8) {
                return getLong(objArr, arrayBaseOffset(objectArrayClass));
            }
            return 4294967295L & getInt(objArr, arrayBaseOffset(objectArrayClass));
        } catch (Exception e) {
            HookLog.m2121e("get object address error", e);
            return -1L;
        }
    }
}
