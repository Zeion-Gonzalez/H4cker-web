package com.swift.sandhook;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class SandHookMethodResolver {
    public static Field artMethodField = null;
    public static boolean canResolvedInJava = false;
    public static Field dexCacheField = null;
    public static int dexMethodIndex = 0;
    public static Field dexMethodIndexField = null;
    public static long entryPointFromCompiledCode = 0;
    public static long entryPointFromInterpreter = 0;
    public static Field fieldEntryPointFromCompiledCode = null;
    public static Field fieldEntryPointFromInterpreter = null;
    public static boolean isArtMethod = false;
    public static long resolvedMethodsAddress;
    public static Field resolvedMethodsField;
    public static Object testArtMethod;
    public static Method testMethod;

    public static void init() {
        testMethod = SandHook.testOffsetMethod1;
        checkSupport();
    }

    private static void checkSupport() {
        try {
            artMethodField = SandHook.getField(Method.class, "artMethod");
            testArtMethod = artMethodField.get(testMethod);
            if (SandHook.hasJavaArtMethod() && testArtMethod.getClass() == SandHook.artMethodClass) {
                checkSupportForArtMethod();
                isArtMethod = true;
            } else if (testArtMethod instanceof Long) {
                checkSupportForArtMethodId();
                isArtMethod = false;
            } else {
                canResolvedInJava = false;
            }
        } catch (Exception unused) {
        }
    }

    public static long getArtMethod(Member member) {
        Field field = artMethodField;
        if (field == null) {
            return 0L;
        }
        try {
            return ((Long) field.get(member)).longValue();
        } catch (IllegalAccessException unused) {
            return 0L;
        }
    }

    private static void checkSupportForArtMethod() throws Exception {
        try {
            dexMethodIndexField = SandHook.getField(SandHook.artMethodClass, "dexMethodIndex");
        } catch (NoSuchFieldException unused) {
            dexMethodIndexField = SandHook.getField(SandHook.artMethodClass, "methodDexIndex");
        }
        dexCacheField = SandHook.getField(Class.class, "dexCache");
        Object obj = dexCacheField.get(testMethod.getDeclaringClass());
        resolvedMethodsField = SandHook.getField(obj.getClass(), "resolvedMethods");
        if (resolvedMethodsField.get(obj) instanceof Object[]) {
            canResolvedInJava = true;
        }
        try {
            try {
                dexMethodIndex = ((Integer) dexMethodIndexField.get(testArtMethod)).intValue();
            } catch (Throwable unused2) {
                return;
            }
        } catch (Throwable unused3) {
        }
        try {
            fieldEntryPointFromCompiledCode = SandHook.getField(SandHook.artMethodClass, "entryPointFromQuickCompiledCode");
        } catch (Throwable unused4) {
            fieldEntryPointFromCompiledCode = SandHook.getField(SandHook.artMethodClass, "entryPointFromCompiledCode");
        }
        if (fieldEntryPointFromCompiledCode.getType() == Integer.TYPE) {
            entryPointFromCompiledCode = fieldEntryPointFromCompiledCode.getInt(testArtMethod);
        } else if (fieldEntryPointFromCompiledCode.getType() == Long.TYPE) {
            entryPointFromCompiledCode = fieldEntryPointFromCompiledCode.getLong(testArtMethod);
        }
        fieldEntryPointFromInterpreter = SandHook.getField(SandHook.artMethodClass, "entryPointFromInterpreter");
        if (fieldEntryPointFromInterpreter.getType() == Integer.TYPE) {
            entryPointFromInterpreter = fieldEntryPointFromInterpreter.getInt(testArtMethod);
        } else if (fieldEntryPointFromCompiledCode.getType() == Long.TYPE) {
            entryPointFromInterpreter = fieldEntryPointFromInterpreter.getLong(testArtMethod);
        }
    }

    private static void checkSupportForArtMethodId() throws Exception {
        dexMethodIndexField = SandHook.getField(Method.class, "dexMethodIndex");
        dexMethodIndex = ((Integer) dexMethodIndexField.get(testMethod)).intValue();
        dexCacheField = SandHook.getField(Class.class, "dexCache");
        Object obj = dexCacheField.get(testMethod.getDeclaringClass());
        resolvedMethodsField = SandHook.getField(obj.getClass(), "resolvedMethods");
        Object obj2 = resolvedMethodsField.get(obj);
        if (obj2 instanceof Long) {
            canResolvedInJava = false;
            resolvedMethodsAddress = ((Long) obj2).longValue();
        } else if (obj2 instanceof long[]) {
            canResolvedInJava = true;
        } else if (obj2 instanceof int[]) {
            canResolvedInJava = true;
        }
    }

    public static void resolveMethod(Method method, Method method2) {
        if (canResolvedInJava && artMethodField != null) {
            try {
                resolveInJava(method, method2);
                return;
            } catch (Exception unused) {
                resolveInNative(method, method2);
                return;
            }
        }
        resolveInNative(method, method2);
    }

    private static void resolveInJava(Method method, Method method2) throws Exception {
        Object obj = dexCacheField.get(method.getDeclaringClass());
        if (isArtMethod) {
            Object obj2 = artMethodField.get(method2);
            ((Object[]) resolvedMethodsField.get(obj))[((Integer) dexMethodIndexField.get(obj2)).intValue()] = obj2;
            return;
        }
        int intValue = ((Integer) dexMethodIndexField.get(method2)).intValue();
        Object obj3 = resolvedMethodsField.get(obj);
        if (obj3 instanceof long[]) {
            ((long[]) obj3)[intValue] = ((Long) artMethodField.get(method2)).longValue();
        } else {
            if (obj3 instanceof int[]) {
                ((int[]) obj3)[intValue] = Long.valueOf(((Long) artMethodField.get(method2)).longValue()).intValue();
                return;
            }
            throw new UnsupportedOperationException("un support");
        }
    }

    private static void resolveInNative(Method method, Method method2) {
        SandHook.ensureMethodCached(method, method2);
    }
}
