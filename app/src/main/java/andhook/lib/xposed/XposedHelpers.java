package andhook.lib.xposed;

import andhook.lib.xposed.XC_MethodHook;
import android.content.res.Resources;
import android.util.Log;
import dalvik.system.DexFile;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipFile;

/* loaded from: classes2.dex */
public final class XposedHelpers {
    private static final String LOGTAG = "XposedHelpers";
    private static final HashMap<String, Field> fieldCache = new HashMap<>();
    private static final HashMap<String, Method> methodCache = new HashMap<>();
    private static final HashMap<String, Constructor<?>> constructorCache = new HashMap<>();
    private static final WeakHashMap<Object, HashMap<String, Object>> additionalFields = new WeakHashMap<>();
    private static final HashMap<String, ThreadLocal<AtomicInteger>> sMethodDepth = new HashMap<>();

    public static Class<?>[] getClassesAsArray(Class<?>... clsArr) {
        return clsArr;
    }

    public static Class<?> findClass(String str, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        try {
            return classLoader.loadClass(str);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundError(e);
        }
    }

    public static Class<?> findClassIfExists(String str, ClassLoader classLoader) {
        try {
            return findClass(str, classLoader);
        } catch (ClassNotFoundError unused) {
            return null;
        }
    }

    public static Field findField(Class<?> cls, String str) {
        String str2 = cls.getName() + '#' + str;
        if (fieldCache.containsKey(str2)) {
            Field field = fieldCache.get(str2);
            if (field != null) {
                return field;
            }
            throw new NoSuchFieldError(str2);
        }
        try {
            Field findFieldRecursiveImpl = findFieldRecursiveImpl(cls, str);
            findFieldRecursiveImpl.setAccessible(true);
            fieldCache.put(str2, findFieldRecursiveImpl);
            return findFieldRecursiveImpl;
        } catch (NoSuchFieldException unused) {
            fieldCache.put(str2, null);
            throw new NoSuchFieldError(str2);
        }
    }

    public static Field findFieldIfExists(Class<?> cls, String str) {
        try {
            return findField(cls, str);
        } catch (NoSuchFieldError unused) {
            return null;
        }
    }

    private static Field findFieldRecursiveImpl(Class<?> cls, String str) throws NoSuchFieldException {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException e) {
            while (true) {
                cls = cls.getSuperclass();
                if (cls == null || cls.equals(Object.class)) {
                    break;
                }
                try {
                    return cls.getDeclaredField(str);
                } catch (NoSuchFieldException unused) {
                }
            }
            throw e;
        }
    }

    public static Field findFirstFieldByExactType(Class<?> cls, Class<?> cls2) {
        Class<?> cls3 = cls;
        do {
            for (Field field : cls3.getDeclaredFields()) {
                if (field.getType() == cls2) {
                    field.setAccessible(true);
                    return field;
                }
            }
            cls3 = cls3.getSuperclass();
        } while (cls3 != null);
        throw new NoSuchFieldError("Field of type " + cls2.getName() + " in class " + cls.getName());
    }

    public static XC_MethodHook.Unhook findAndHookMethod(Class<?> cls, String str, Object... objArr) {
        if (objArr.length == 0 || !(objArr[objArr.length - 1] instanceof XC_MethodHook)) {
            throw new IllegalArgumentException("no callback defined");
        }
        return XposedBridge.hookMethod(findMethodExact(cls, str, getParameterClasses(cls.getClassLoader(), objArr)), (XC_MethodHook) objArr[objArr.length - 1]);
    }

    public static XC_MethodHook.Unhook findAndHookMethod(String str, ClassLoader classLoader, String str2, Object... objArr) {
        return findAndHookMethod(findClass(str, classLoader), str2, objArr);
    }

    public static Method findMethodExact(Class<?> cls, String str, Object... objArr) {
        return findMethodExact(cls, str, getParameterClasses(cls.getClassLoader(), objArr));
    }

    public static Method findMethodExactIfExists(Class<?> cls, String str, Object... objArr) {
        try {
            return findMethodExact(cls, str, objArr);
        } catch (ClassNotFoundError | NoSuchMethodError unused) {
            return null;
        }
    }

    public static Method findMethodExact(String str, ClassLoader classLoader, String str2, Object... objArr) {
        return findMethodExact(findClass(str, classLoader), str2, getParameterClasses(classLoader, objArr));
    }

    public static Method findMethodExactIfExists(String str, ClassLoader classLoader, String str2, Object... objArr) {
        try {
            return findMethodExact(str, classLoader, str2, objArr);
        } catch (ClassNotFoundError | NoSuchMethodError unused) {
            return null;
        }
    }

    public static Method findMethodExact(Class<?> cls, String str, Class<?>... clsArr) {
        String str2 = cls.getName() + '#' + str + getParametersString(clsArr) + "#exact";
        if (methodCache.containsKey(str2)) {
            Method method = methodCache.get(str2);
            if (method != null) {
                return method;
            }
            throw new NoSuchMethodError(str2);
        }
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            methodCache.put(str2, declaredMethod);
            return declaredMethod;
        } catch (NoSuchMethodException unused) {
            methodCache.put(str2, null);
            throw new NoSuchMethodError(str2);
        }
    }

    public static Method[] findMethodsByExactParameters(Class<?> cls, Class<?> cls2, Class<?>... clsArr) {
        boolean z;
        LinkedList linkedList = new LinkedList();
        for (Method method : cls.getDeclaredMethods()) {
            if (cls2 == null || cls2 == method.getReturnType()) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (clsArr.length == parameterTypes.length) {
                    int i = 0;
                    while (true) {
                        if (i >= clsArr.length) {
                            z = true;
                            break;
                        }
                        if (clsArr[i] != parameterTypes[i]) {
                            z = false;
                            break;
                        }
                        i++;
                    }
                    if (z) {
                        method.setAccessible(true);
                        linkedList.add(method);
                    }
                }
            }
        }
        return (Method[]) linkedList.toArray(new Method[linkedList.size()]);
    }

    public static Method findMethodBestMatch(Class<?> cls, String str, Class<?>... clsArr) {
        Method method;
        String str2 = cls.getName() + '#' + str + getParametersString(clsArr) + "#bestmatch";
        if (methodCache.containsKey(str2)) {
            Method method2 = methodCache.get(str2);
            if (method2 != null) {
                return method2;
            }
            throw new NoSuchMethodError(str2);
        }
        try {
            Method findMethodExact = findMethodExact(cls, str, clsArr);
            methodCache.put(str2, findMethodExact);
            return findMethodExact;
        } catch (NoSuchMethodError unused) {
            Method method3 = null;
            boolean z = true;
            while (true) {
                method = method3;
                for (Method method4 : cls.getDeclaredMethods()) {
                    if ((z || !Modifier.isPrivate(method4.getModifiers())) && method4.getName().equals(str) && ClassUtils.isAssignable(clsArr, method4.getParameterTypes(), true) && (method == null || MemberUtils.compareParameterTypes(method4.getParameterTypes(), method.getParameterTypes(), clsArr) < 0)) {
                        method = method4;
                    }
                }
                cls = cls.getSuperclass();
                if (cls == null) {
                    break;
                }
                method3 = method;
                z = false;
            }
            if (method != null) {
                method.setAccessible(true);
                methodCache.put(str2, method);
                return method;
            }
            NoSuchMethodError noSuchMethodError = new NoSuchMethodError(str2);
            methodCache.put(str2, null);
            throw noSuchMethodError;
        }
    }

    public static Method findMethodBestMatch(Class<?> cls, String str, Object... objArr) {
        return findMethodBestMatch(cls, str, getParameterTypes(objArr));
    }

    public static Method findMethodBestMatch(Class<?> cls, String str, Class<?>[] clsArr, Object[] objArr) {
        Class<?>[] clsArr2 = null;
        for (int i = 0; i < clsArr.length; i++) {
            if (clsArr[i] == null) {
                if (clsArr2 == null) {
                    clsArr2 = getParameterTypes(objArr);
                }
                clsArr[i] = clsArr2[i];
            }
        }
        return findMethodBestMatch(cls, str, clsArr);
    }

    public static Class<?>[] getParameterTypes(Object... objArr) {
        Class<?>[] clsArr = new Class[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            clsArr[i] = objArr[i] != null ? objArr[i].getClass() : null;
        }
        return clsArr;
    }

    private static Class<?>[] getParameterClasses(ClassLoader classLoader, Object[] objArr) {
        Class<?>[] clsArr = null;
        for (int length = objArr.length - 1; length >= 0; length--) {
            Object obj = objArr[length];
            if (obj == null) {
                throw new ClassNotFoundError("parameter type must not be null", null);
            }
            if (!(obj instanceof XC_MethodHook)) {
                if (clsArr == null) {
                    clsArr = new Class[length + 1];
                }
                if (obj instanceof Class) {
                    clsArr[length] = (Class) obj;
                } else if (obj instanceof String) {
                    clsArr[length] = findClass((String) obj, classLoader);
                } else {
                    throw new ClassNotFoundError("parameter type must either be specified as Class or String", null);
                }
            }
        }
        return clsArr == null ? new Class[0] : clsArr;
    }

    private static String getParametersString(Class<?>... clsArr) {
        StringBuilder sb = new StringBuilder("(");
        boolean z = true;
        for (Class<?> cls : clsArr) {
            if (z) {
                z = false;
            } else {
                sb.append(",");
            }
            if (cls != null) {
                sb.append(cls.getCanonicalName());
            } else {
                sb.append("null");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static Constructor<?> findConstructorExact(Class<?> cls, Object... objArr) {
        return findConstructorExact(cls, getParameterClasses(cls.getClassLoader(), objArr));
    }

    public static Constructor<?> findConstructorExactIfExists(Class<?> cls, Object... objArr) {
        try {
            return findConstructorExact(cls, objArr);
        } catch (ClassNotFoundError | NoSuchMethodError unused) {
            return null;
        }
    }

    public static Constructor<?> findConstructorExact(String str, ClassLoader classLoader, Object... objArr) {
        return findConstructorExact(findClass(str, classLoader), getParameterClasses(classLoader, objArr));
    }

    public static Constructor<?> findConstructorExactIfExists(String str, ClassLoader classLoader, Object... objArr) {
        try {
            return findConstructorExact(str, classLoader, objArr);
        } catch (ClassNotFoundError | NoSuchMethodError unused) {
            return null;
        }
    }

    public static Constructor<?> findConstructorExact(Class<?> cls, Class<?>... clsArr) {
        String str = cls.getName() + getParametersString(clsArr) + "#exact";
        if (constructorCache.containsKey(str)) {
            Constructor<?> constructor = constructorCache.get(str);
            if (constructor != null) {
                return constructor;
            }
            throw new NoSuchMethodError(str);
        }
        try {
            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(clsArr);
            declaredConstructor.setAccessible(true);
            constructorCache.put(str, declaredConstructor);
            return declaredConstructor;
        } catch (NoSuchMethodException unused) {
            constructorCache.put(str, null);
            throw new NoSuchMethodError(str);
        }
    }

    public static XC_MethodHook.Unhook findAndHookConstructor(Class<?> cls, Object... objArr) {
        if (objArr.length == 0 || !(objArr[objArr.length - 1] instanceof XC_MethodHook)) {
            throw new IllegalArgumentException("no callback defined");
        }
        return XposedBridge.hookMethod(findConstructorExact(cls, getParameterClasses(cls.getClassLoader(), objArr)), (XC_MethodHook) objArr[objArr.length - 1]);
    }

    public static XC_MethodHook.Unhook findAndHookConstructor(String str, ClassLoader classLoader, Object... objArr) {
        return findAndHookConstructor(findClass(str, classLoader), objArr);
    }

    public static Constructor<?> findConstructorBestMatch(Class<?> cls, Class<?>... clsArr) {
        String str = cls.getName() + getParametersString(clsArr) + "#bestmatch";
        if (constructorCache.containsKey(str)) {
            Constructor<?> constructor = constructorCache.get(str);
            if (constructor != null) {
                return constructor;
            }
            throw new NoSuchMethodError(str);
        }
        try {
            Constructor<?> findConstructorExact = findConstructorExact(cls, clsArr);
            constructorCache.put(str, findConstructorExact);
            return findConstructorExact;
        } catch (NoSuchMethodError unused) {
            Constructor<?> constructor2 = null;
            for (Constructor<?> constructor3 : cls.getDeclaredConstructors()) {
                if (ClassUtils.isAssignable(clsArr, constructor3.getParameterTypes(), true) && (constructor2 == null || MemberUtils.compareParameterTypes(constructor3.getParameterTypes(), constructor2.getParameterTypes(), clsArr) < 0)) {
                    constructor2 = constructor3;
                }
            }
            if (constructor2 != null) {
                constructor2.setAccessible(true);
                constructorCache.put(str, constructor2);
                return constructor2;
            }
            constructorCache.put(str, null);
            throw new NoSuchMethodError(str);
        }
    }

    public static Constructor<?> findConstructorBestMatch(Class<?> cls, Object... objArr) {
        return findConstructorBestMatch(cls, getParameterTypes(objArr));
    }

    public static Constructor<?> findConstructorBestMatch(Class<?> cls, Class<?>[] clsArr, Object[] objArr) {
        Class<?>[] clsArr2 = null;
        for (int i = 0; i < clsArr.length; i++) {
            if (clsArr[i] == null) {
                if (clsArr2 == null) {
                    clsArr2 = getParameterTypes(objArr);
                }
                clsArr[i] = clsArr2[i];
            }
        }
        return findConstructorBestMatch(cls, clsArr);
    }

    /* loaded from: classes2.dex */
    public static final class ClassNotFoundError extends Error {
        private static final long serialVersionUID = -1070936889459514628L;

        public ClassNotFoundError(Throwable th) {
            super(th);
        }

        public ClassNotFoundError(String str, Throwable th) {
            super(str, th);
        }
    }

    public static int getFirstParameterIndexByType(Member member, Class<?> cls) {
        Class<?>[] parameterTypes = member instanceof Method ? ((Method) member).getParameterTypes() : ((Constructor) member).getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == cls) {
                return i;
            }
        }
        throw new NoSuchFieldError("No parameter of type " + cls + " found in " + member);
    }

    public static int getParameterIndexByType(Member member, Class<?> cls) {
        Class<?>[] parameterTypes = member instanceof Method ? ((Method) member).getParameterTypes() : ((Constructor) member).getParameterTypes();
        int i = -1;
        for (int i2 = 0; i2 < parameterTypes.length; i2++) {
            if (parameterTypes[i2] == cls) {
                if (i != -1) {
                    throw new NoSuchFieldError("More than one parameter of type " + cls + " found in " + member);
                }
                i = i2;
            }
        }
        if (i != -1) {
            return i;
        }
        throw new NoSuchFieldError("No parameter of type " + cls + " found in " + member);
    }

    public static void setObjectField(Object obj, String str, Object obj2) {
        try {
            findField(obj.getClass(), str).set(obj, obj2);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setBooleanField(Object obj, String str, boolean z) {
        try {
            findField(obj.getClass(), str).setBoolean(obj, z);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setByteField(Object obj, String str, byte b) {
        try {
            findField(obj.getClass(), str).setByte(obj, b);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setCharField(Object obj, String str, char c) {
        try {
            findField(obj.getClass(), str).setChar(obj, c);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setDoubleField(Object obj, String str, double d) {
        try {
            findField(obj.getClass(), str).setDouble(obj, d);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setFloatField(Object obj, String str, float f) {
        try {
            findField(obj.getClass(), str).setFloat(obj, f);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setIntField(Object obj, String str, int i) {
        try {
            findField(obj.getClass(), str).setInt(obj, i);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setLongField(Object obj, String str, long j) {
        try {
            findField(obj.getClass(), str).setLong(obj, j);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setShortField(Object obj, String str, short s) {
        try {
            findField(obj.getClass(), str).setShort(obj, s);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static Object getObjectField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).get(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static Object getSurroundingThis(Object obj) {
        return getObjectField(obj, "this$0");
    }

    public static boolean getBooleanField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getBoolean(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static byte getByteField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getByte(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static char getCharField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getChar(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static double getDoubleField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getDouble(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static float getFloatField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getFloat(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static int getIntField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getInt(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static long getLongField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getLong(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static short getShortField(Object obj, String str) {
        try {
            return findField(obj.getClass(), str).getShort(obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticObjectField(Class<?> cls, String str, Object obj) {
        try {
            findField(cls, str).set(null, obj);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticBooleanField(Class<?> cls, String str, boolean z) {
        try {
            findField(cls, str).setBoolean(null, z);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticByteField(Class<?> cls, String str, byte b) {
        try {
            findField(cls, str).setByte(null, b);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticCharField(Class<?> cls, String str, char c) {
        try {
            findField(cls, str).setChar(null, c);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticDoubleField(Class<?> cls, String str, double d) {
        try {
            findField(cls, str).setDouble(null, d);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticFloatField(Class<?> cls, String str, float f) {
        try {
            findField(cls, str).setFloat(null, f);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticIntField(Class<?> cls, String str, int i) {
        try {
            findField(cls, str).setInt(null, i);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticLongField(Class<?> cls, String str, long j) {
        try {
            findField(cls, str).setLong(null, j);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static void setStaticShortField(Class<?> cls, String str, short s) {
        try {
            findField(cls, str).setShort(null, s);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static Object getStaticObjectField(Class<?> cls, String str) {
        try {
            return findField(cls, str).get(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static boolean getStaticBooleanField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getBoolean(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static byte getStaticByteField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getByte(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static char getStaticCharField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getChar(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static double getStaticDoubleField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getDouble(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static float getStaticFloatField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getFloat(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static int getStaticIntField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getInt(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static long getStaticLongField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getLong(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static short getStaticShortField(Class<?> cls, String str) {
        try {
            return findField(cls, str).getShort(null);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        }
    }

    public static Object callMethod(Object obj, String str, Object... objArr) {
        try {
            return findMethodBestMatch(obj.getClass(), str, objArr).invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        } catch (InvocationTargetException e3) {
            throw new InvocationTargetError(e3.getCause());
        }
    }

    public static Object callMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        try {
            return findMethodBestMatch(obj.getClass(), str, clsArr, objArr).invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        } catch (InvocationTargetException e3) {
            throw new InvocationTargetError(e3.getCause());
        }
    }

    public static Object callStaticMethod(Class<?> cls, String str, Object... objArr) {
        try {
            return findMethodBestMatch(cls, str, objArr).invoke(null, objArr);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        } catch (InvocationTargetException e3) {
            throw new InvocationTargetError(e3.getCause());
        }
    }

    public static Object callStaticMethod(Class<?> cls, String str, Class<?>[] clsArr, Object... objArr) {
        try {
            return findMethodBestMatch(cls, str, clsArr, objArr).invoke(null, objArr);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        } catch (InvocationTargetException e3) {
            throw new InvocationTargetError(e3.getCause());
        }
    }

    /* loaded from: classes2.dex */
    public static final class InvocationTargetError extends Error {
        private static final long serialVersionUID = -1070936889459514628L;

        public InvocationTargetError(Throwable th) {
            super(th);
        }
    }

    public static Object newInstance(Class<?> cls, Object... objArr) {
        try {
            return findConstructorBestMatch(cls, objArr).newInstance(objArr);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        } catch (InstantiationException e3) {
            throw new InstantiationError(e3.getMessage());
        } catch (InvocationTargetException e4) {
            throw new InvocationTargetError(e4.getCause());
        }
    }

    public static Object newInstance(Class<?> cls, Class<?>[] clsArr, Object... objArr) {
        try {
            return findConstructorBestMatch(cls, clsArr, objArr).newInstance(objArr);
        } catch (IllegalAccessException e) {
            Log.w(LOGTAG, e);
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        } catch (InstantiationException e3) {
            throw new InstantiationError(e3.getMessage());
        } catch (InvocationTargetException e4) {
            throw new InvocationTargetError(e4.getCause());
        }
    }

    public static Object setAdditionalInstanceField(Object obj, String str, Object obj2) {
        HashMap<String, Object> hashMap;
        Object put;
        if (obj == null) {
            throw new NullPointerException("object must not be null");
        }
        if (str == null) {
            throw new NullPointerException("key must not be null");
        }
        synchronized (additionalFields) {
            hashMap = additionalFields.get(obj);
            if (hashMap == null) {
                hashMap = new HashMap<>();
                additionalFields.put(obj, hashMap);
            }
        }
        synchronized (hashMap) {
            put = hashMap.put(str, obj2);
        }
        return put;
    }

    public static Object getAdditionalInstanceField(Object obj, String str) {
        Object obj2;
        if (obj == null) {
            throw new NullPointerException("object must not be null");
        }
        if (str == null) {
            throw new NullPointerException("key must not be null");
        }
        synchronized (additionalFields) {
            HashMap<String, Object> hashMap = additionalFields.get(obj);
            if (hashMap == null) {
                return null;
            }
            synchronized (hashMap) {
                obj2 = hashMap.get(str);
            }
            return obj2;
        }
    }

    public static Object removeAdditionalInstanceField(Object obj, String str) {
        Object remove;
        if (obj == null) {
            throw new NullPointerException("object must not be null");
        }
        if (str == null) {
            throw new NullPointerException("key must not be null");
        }
        synchronized (additionalFields) {
            HashMap<String, Object> hashMap = additionalFields.get(obj);
            if (hashMap == null) {
                return null;
            }
            synchronized (hashMap) {
                remove = hashMap.remove(str);
            }
            return remove;
        }
    }

    public static Object setAdditionalStaticField(Object obj, String str, Object obj2) {
        return setAdditionalInstanceField(obj.getClass(), str, obj2);
    }

    public static Object getAdditionalStaticField(Object obj, String str) {
        return getAdditionalInstanceField(obj.getClass(), str);
    }

    public static Object removeAdditionalStaticField(Object obj, String str) {
        return removeAdditionalInstanceField(obj.getClass(), str);
    }

    public static Object setAdditionalStaticField(Class<?> cls, String str, Object obj) {
        return setAdditionalInstanceField(cls, str, obj);
    }

    public static Object getAdditionalStaticField(Class<?> cls, String str) {
        return getAdditionalInstanceField(cls, str);
    }

    public static Object removeAdditionalStaticField(Class<?> cls, String str) {
        return removeAdditionalInstanceField(cls, str);
    }

    public static byte[] assetAsByteArray(Resources resources, String str) throws IOException {
        return inputStreamToByteArray(resources.getAssets().open(str));
    }

    static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    static void closeSilently(DexFile dexFile) {
        if (dexFile != null) {
            try {
                dexFile.close();
            } catch (IOException unused) {
            }
        }
    }

    static void closeSilently(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException unused) {
            }
        }
    }

    public static String getMD5Sum(String str) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(str);
            byte[] bArr = new byte[8192];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read > 0) {
                    messageDigest.update(bArr, 0, read);
                } else {
                    fileInputStream.close();
                    return new BigInteger(1, messageDigest.digest()).toString(16);
                }
            }
        } catch (NoSuchAlgorithmException unused) {
            return "";
        }
    }

    public static int incrementMethodDepth(String str) {
        return getMethodDepthCounter(str).get().incrementAndGet();
    }

    public static int decrementMethodDepth(String str) {
        return getMethodDepthCounter(str).get().decrementAndGet();
    }

    public static int getMethodDepth(String str) {
        return getMethodDepthCounter(str).get().get();
    }

    private static ThreadLocal<AtomicInteger> getMethodDepthCounter(String str) {
        ThreadLocal<AtomicInteger> threadLocal;
        synchronized (sMethodDepth) {
            threadLocal = sMethodDepth.get(str);
            if (threadLocal == null) {
                threadLocal = new ThreadLocal<AtomicInteger>() { // from class: andhook.lib.xposed.XposedHelpers.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // java.lang.ThreadLocal
                    public AtomicInteger initialValue() {
                        return new AtomicInteger();
                    }
                };
                sMethodDepth.put(str, threadLocal);
            }
        }
        return threadLocal;
    }

    static boolean fileContains(File file, String str) throws IOException {
        boolean z;
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
            while (true) {
                try {
                    String readLine = bufferedReader2.readLine();
                    if (readLine == null) {
                        z = false;
                        break;
                    }
                    if (readLine.contains(str)) {
                        z = true;
                        break;
                    }
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = bufferedReader2;
                    closeSilently(bufferedReader);
                    throw th;
                }
            }
            closeSilently(bufferedReader2);
            return z;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    static Method getOverriddenMethod(Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers)) {
            String name = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<? super Object> superclass = method.getDeclaringClass().getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
                try {
                    Method declaredMethod = superclass.getDeclaredMethod(name, parameterTypes);
                    int modifiers2 = declaredMethod.getModifiers();
                    if (!Modifier.isPrivate(modifiers2)) {
                        if (!Modifier.isAbstract(modifiers2)) {
                            return declaredMethod;
                        }
                    }
                    return null;
                } catch (NoSuchMethodException unused) {
                }
            }
        }
        return null;
    }

    static HashSet<Method> getOverriddenMethods(Class<?> cls) {
        HashSet<Method> hashSet = new HashSet<>();
        for (Method method : cls.getDeclaredMethods()) {
            Method overriddenMethod = getOverriddenMethod(method);
            if (overriddenMethod != null) {
                hashSet.add(overriddenMethod);
            }
        }
        return hashSet;
    }
}
