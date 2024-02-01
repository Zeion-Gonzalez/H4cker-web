package com.swift.sandhook.utils;

import com.applisto.appcloner.classes.util.Log;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class ReflectionUtils {
    static Method addWhiteListMethod;
    public static Method forNameMethod;
    public static Method getMethodMethod;
    static Object vmRuntime;
    static Class vmRuntimeClass;

    static {
        try {
            getMethodMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            vmRuntimeClass = (Class) forNameMethod.invoke(null, "dalvik.system.VMRuntime");
            addWhiteListMethod = (Method) getMethodMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            vmRuntime = ((Method) getMethodMethod.invoke(vmRuntimeClass, "getRuntime", null)).invoke(null, new Object[0]);
        } catch (Exception e) {
            Log.m14e("ReflectionUtils", "error get methods", e);
        }
    }

    public static boolean passApiCheck() {
        try {
            addReflectionWhiteList("Landroid/", "Lcom/android/", "Ljava/lang/", "Ldalvik/system/", "Llibcore/io/");
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public static void addReflectionWhiteList(String... strArr) throws Throwable {
        addWhiteListMethod.invoke(vmRuntime, strArr);
    }
}
