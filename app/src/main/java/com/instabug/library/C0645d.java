package com.instabug.library;

import java.lang.reflect.Method;

/* compiled from: ReflectionUtils.java */
/* renamed from: com.instabug.library.d */
/* loaded from: classes.dex */
public class C0645d {

    /* renamed from: a */
    private static final String f812a = C0645d.class.getSimpleName();

    /* renamed from: a */
    public static Method m1247a(Class cls, String str) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().equals(str)) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    /* renamed from: a */
    public static Method m1248a(Class cls, String str, Class... clsArr) {
        for (Method method : cls.getMethods()) {
            if (method.getName().equals(str) && method.getParameterTypes().length == clsArr.length) {
                for (int i = 0; i < method.getParameterTypes().length && method.getParameterTypes()[i] == clsArr[i]; i++) {
                    if (i == method.getParameterTypes().length - 1) {
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
        }
        return null;
    }
}
