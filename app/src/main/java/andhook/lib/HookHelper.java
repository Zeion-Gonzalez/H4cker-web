package andhook.lib;

import android.util.Log;
import android.util.Pair;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public final class HookHelper {
    public static final String constructorName = "<init>";
    private static final ConcurrentHashMap<Pair<String, String>, Integer> sBackups = new ConcurrentHashMap<>();

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: classes2.dex */
    public @interface Hook {
        Class<?> clazz() default Hook.class;

        String name() default "";

        boolean need_origin() default true;

        String value() default "";
    }

    private static void warnKnownIssue(Method method) {
        if (!Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length < 1 || method.getParameterTypes()[0].isPrimitive()) {
            Log.e("AndHook", "method " + method.getDeclaringClass().getName() + "@" + method.getName() + " must be static and its first argument must be Class<?> or Object!");
        }
    }

    public static void hook(Member member, Method method) {
        if (uniqueKey(method) == null) {
            return;
        }
        warnKnownIssue(method);
        int backup = AndHook.backup(member);
        if (backup == -1 || !saveBackupSlot(Integer.valueOf(backup), method)) {
            return;
        }
        AndHook.hook(member, method, backup);
    }

    public static void hook(Class<?> cls, String str, String str2, Method method) {
        if (uniqueKey(method) == null) {
            return;
        }
        warnKnownIssue(method);
        int backup = AndHook.backup(cls, str, str2);
        if (backup == -1 || !saveBackupSlot(Integer.valueOf(backup), method)) {
            return;
        }
        AndHook.hook(cls, str, str2, method, backup);
    }

    private static Pair<String, String> uniqueKey(Method method) {
        String name = method.getDeclaringClass().getName();
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName());
        sb.append(method.getParameterTypes().length - 1);
        Pair<String, String> create = Pair.create(name, sb.toString());
        if (!sBackups.containsKey(create)) {
            return create;
        }
        Log.e("AndHook", "duplicate key error! already hooked?");
        return null;
    }

    private static boolean saveBackupSlot(Integer num, Method method) {
        Pair<String, String> uniqueKey = uniqueKey(method);
        if (uniqueKey == null) {
            return false;
        }
        sBackups.put(uniqueKey, num);
        Log.i("AndHook", "saved backup for " + ((String) uniqueKey.first) + "@" + ((String) uniqueKey.second) + ", slot = " + num);
        return true;
    }

    private static int getBackupSlot(int i) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String className = stackTraceElement.getClassName();
        if ("com.applisto.appcloner.hooking.Hooking".equals(className)) {
            stackTraceElement = Thread.currentThread().getStackTrace()[5];
            className = stackTraceElement.getClassName();
        }
        Integer num = sBackups.get(Pair.create(className, stackTraceElement.getMethodName() + i));
        if (num == null) {
            Log.e("AndHook", "no backup found for " + className + "@" + stackTraceElement.getMethodName() + "@" + i);
            return -1;
        }
        return num.intValue();
    }

    public static void invokeVoidOrigin(Object obj, Object... objArr) {
        AndHook.invokeVoidMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static boolean invokeBooleanOrigin(Object obj, Object... objArr) {
        return AndHook.invokeBooleanMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static byte invokeByteOrigin(Object obj, Object... objArr) {
        return AndHook.invokeByteMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static short invokeShortOrigin(Object obj, Object... objArr) {
        return AndHook.invokeShortMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static char invokeCharOrigin(Object obj, Object... objArr) {
        return AndHook.invokeCharMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static int invokeIntOrigin(Object obj, Object... objArr) {
        return AndHook.invokeIntMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static long invokeLongOrigin(Object obj, Object... objArr) {
        return AndHook.invokeLongMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static float invokeFloatOrigin(Object obj, Object... objArr) {
        return AndHook.invokeFloatMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static double invokeDoubleOrigin(Object obj, Object... objArr) {
        return AndHook.invokeDoubleMethod(getBackupSlot(objArr.length), obj, objArr);
    }

    public static <T> T invokeObjectOrigin(Object obj, Object... objArr) {
        return (T) AndHook.invoke(getBackupSlot(objArr.length), obj, objArr);
    }

    public static void setObjectField(Object obj, String str, Object obj2) {
        Field findFieldHierarchically = findFieldHierarchically(obj.getClass(), str);
        if (findFieldHierarchically != null) {
            try {
                findFieldHierarchically.set(obj, obj2);
            } catch (Exception e) {
                Log.e("AndHook", "failed to set instance field " + str, e);
            }
        }
    }

    public static void setStaticObjectField(Class<?> cls, String str, Object obj) {
        Field findFieldHierarchically = findFieldHierarchically(cls, str);
        if (findFieldHierarchically != null) {
            try {
                findFieldHierarchically.set(null, obj);
            } catch (Exception e) {
                Log.e("AndHook", "failed to set static field " + str + " for class " + cls.getName(), e);
            }
        }
    }

    public static Class<?> findClass(String str) {
        return findClass(str, AndHook.class.getClassLoader());
    }

    public static Class<?> findClass(String str, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(str);
        } catch (Exception e) {
            Log.e("AndHook", "failed to find class " + str + " on ClassLoader " + classLoader, e);
            return null;
        }
    }

    public static Field findFieldHierarchically(Class<?> cls, String str) {
        Field field = null;
        Class<?> cls2 = cls;
        do {
            try {
                field = cls2.getDeclaredField(str);
            } catch (NoSuchFieldException unused) {
                cls2 = cls2.getSuperclass();
                if (cls2 == null) {
                    break;
                }
            }
        } while (field == null);
        if (field != null) {
            field.setAccessible(true);
        } else {
            Log.e("AndHook", "failed to find field " + str + " of class " + cls.getName());
        }
        return field;
    }

    public static Constructor<?> findConstructorHierarchically(Class<?> cls, Class<?>... clsArr) {
        Constructor<?> constructor = null;
        Class<?> cls2 = cls;
        do {
            try {
                constructor = cls2.getDeclaredConstructor(clsArr);
            } catch (NoSuchMethodException unused) {
                cls2 = cls2.getSuperclass();
                if (cls2 == null) {
                    break;
                }
            }
        } while (constructor == null);
        if (constructor != null) {
            constructor.setAccessible(true);
        } else {
            Log.e("AndHook", "failed to find constructor of class " + cls.getName());
        }
        return constructor;
    }

    public static Method findMethodHierarchically(Class<?> cls, String str, Class<?>... clsArr) {
        Method method = null;
        Class<?> cls2 = cls;
        do {
            try {
                method = cls2.getDeclaredMethod(str, clsArr);
            } catch (NoSuchMethodException unused) {
                cls2 = cls2.getSuperclass();
                if (cls2 == null) {
                    break;
                }
            }
        } while (method == null);
        if (method != null) {
            method.setAccessible(true);
        } else {
            Log.e("AndHook", "failed to find method " + str + " of class " + cls.getName());
        }
        return method;
    }

    private static boolean isConstructor(Class<?> cls, String str) {
        String name = cls.getName();
        if (!str.equals(constructorName)) {
            if (!name.endsWith("." + str)) {
                if (!name.endsWith("$" + str)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void applyHooks(Class<?> cls) {
        applyHooks(cls, cls.getClassLoader());
    }

    public static void applyHooks(Class<?> cls, ClassLoader classLoader) {
        Member findMethodHierarchically;
        for (Method method : cls.getDeclaredMethods()) {
            Hook hook = (Hook) method.getAnnotation(Hook.class);
            if (hook != null) {
                String name = hook.name();
                if (name.isEmpty()) {
                    name = method.getName();
                }
                Class<?> clazz = hook.clazz();
                if (clazz == Hook.class) {
                    try {
                        clazz = classLoader.loadClass(hook.value());
                    } catch (Exception e) {
                        Log.e("AndHook", e.getMessage(), e);
                    }
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length < 1) {
                    Log.e("AndHook", "unexpected args number!");
                } else {
                    Class[] clsArr = new Class[parameterTypes.length - 1];
                    System.arraycopy(parameterTypes, 1, clsArr, 0, clsArr.length);
                    if (isConstructor(clazz, name)) {
                        findMethodHierarchically = findConstructorHierarchically(clazz, clsArr);
                    } else {
                        findMethodHierarchically = findMethodHierarchically(clazz, name, clsArr);
                    }
                    if (findMethodHierarchically != null) {
                        if (Modifier.isStatic(findMethodHierarchically.getModifiers())) {
                            AndHook.ensureClassInitialized(clazz);
                        }
                        if (hook.need_origin()) {
                            hook(findMethodHierarchically, method);
                        } else {
                            AndHook.hookNoBackup(findMethodHierarchically, method);
                        }
                    }
                }
            }
        }
    }
}
