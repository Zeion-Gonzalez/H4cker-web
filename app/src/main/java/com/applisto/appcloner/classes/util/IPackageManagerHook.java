package com.applisto.appcloner.classes.util;

import android.content.Context;
import android.content.pm.PackageManager;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/* loaded from: classes2.dex */
public abstract class IPackageManagerHook {
    private static final String TAG = IPackageManagerHook.class.getSimpleName();

    protected abstract InvocationHandler getInvocationHandler(Object obj);

    public void install(Context context) throws Exception {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        Field declaredField = packageManager.getClass().getDeclaredField("mPM");
        declaredField.setAccessible(true);
        Object obj = declaredField.get(packageManager);
        Log.m15i(TAG, "install; originalPackageManager: " + obj);
        Object newProxyInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Class.forName("android.content.pm.IPackageManager")}, getInvocationHandler(obj));
        declaredField.set(packageManager, newProxyInstance);
        Field declaredField2 = Class.forName("android.app.ActivityThread").getDeclaredField("sPackageManager");
        declaredField2.setAccessible(true);
        declaredField2.set(null, newProxyInstance);
    }
}
