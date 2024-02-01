package com.applisto.appcloner.classes.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/* loaded from: classes2.dex */
public abstract class IActivityManagerHook {
    private static final String TAG = IActivityManagerHook.class.getSimpleName();

    protected abstract InvocationHandler getInvocationHandler(Object obj);

    public void install() throws Exception {
        Object obj;
        Class<?> cls = Class.forName("android.app.ActivityManagerNative");
        Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]);
        Log.m15i(TAG, "install; originalActivityManager: " + invoke);
        Object newProxyInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Class.forName("android.app.IActivityManager")}, getInvocationHandler(invoke));
        try {
            Field declaredField = cls.getDeclaredField("gDefault");
            declaredField.setAccessible(true);
            obj = declaredField.get(null);
        } catch (Exception unused) {
            Field declaredField2 = Class.forName("android.app.ActivityManager").getDeclaredField("IActivityManagerSingleton");
            declaredField2.setAccessible(true);
            obj = declaredField2.get(null);
        }
        Field declaredField3 = Class.forName("android.util.Singleton").getDeclaredField("mInstance");
        declaredField3.setAccessible(true);
        declaredField3.set(obj, newProxyInstance);
    }
}
