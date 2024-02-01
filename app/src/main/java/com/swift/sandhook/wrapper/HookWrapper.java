package com.swift.sandhook.wrapper;

import android.text.TextUtils;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.SandHookConfig;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;
import com.swift.sandhook.annotation.MethodReflectParams;
import com.swift.sandhook.annotation.Param;
import com.swift.sandhook.annotation.SkipParamCheck;
import com.swift.sandhook.annotation.ThisObject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes2.dex */
public class HookWrapper {
    public static void addHookClass(Class<?>... clsArr) throws HookErrorException {
        addHookClass((ClassLoader) null, clsArr);
    }

    public static void addHookClass(ClassLoader classLoader, Class<?>... clsArr) throws HookErrorException {
        for (Class<?> cls : clsArr) {
            addHookClass(classLoader, cls);
        }
    }

    public static void addHookClass(ClassLoader classLoader, Class<?> cls) throws HookErrorException {
        Class targetHookClass = getTargetHookClass(classLoader, cls);
        if (targetHookClass == null) {
            throw new HookErrorException("error hook wrapper class :" + cls.getName());
        }
        Map<Member, HookEntity> hookMethods = getHookMethods(classLoader, targetHookClass, cls);
        try {
            fillBackupMethod(classLoader, cls, hookMethods);
            Iterator<HookEntity> it = hookMethods.values().iterator();
            while (it.hasNext()) {
                SandHook.hook(it.next());
            }
        } catch (Throwable th) {
            throw new HookErrorException("fillBackupMethod error!", th);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00bb A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x00dd A[ADDED_TO_REGION, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0094 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0040 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void fillBackupMethod(java.lang.ClassLoader r15, java.lang.Class<?> r16, java.util.Map<java.lang.reflect.Member, com.swift.sandhook.wrapper.HookWrapper.HookEntity> r17) {
        /*
            Method dump skipped, instructions count: 229
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.swift.sandhook.wrapper.HookWrapper.fillBackupMethod(java.lang.ClassLoader, java.lang.Class, java.util.Map):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:100:0x01d0  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x01db  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00ee  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0109  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0112  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x015c  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01ae  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01b1  */
    /* JADX WARN: Type inference failed for: r10v3, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r13v8, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r17v0, types: [java.lang.Class] */
    /* JADX WARN: Type inference failed for: r4v11, types: [java.lang.reflect.Constructor] */
    /* JADX WARN: Type inference failed for: r4v12, types: [java.lang.reflect.Constructor] */
    /* JADX WARN: Type inference failed for: r4v16, types: [java.lang.reflect.Method] */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v22, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r9v10, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v16, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v19, types: [java.lang.reflect.Constructor] */
    /* JADX WARN: Type inference failed for: r9v20, types: [java.lang.reflect.Constructor] */
    /* JADX WARN: Type inference failed for: r9v23, types: [java.lang.reflect.Method] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.Map<java.lang.reflect.Member, com.swift.sandhook.wrapper.HookWrapper.HookEntity> getHookMethods(java.lang.ClassLoader r16, java.lang.Class r17, java.lang.Class<?> r18) throws com.swift.sandhook.wrapper.HookErrorException {
        /*
            Method dump skipped, instructions count: 523
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.swift.sandhook.wrapper.HookWrapper.getHookMethods(java.lang.ClassLoader, java.lang.Class, java.lang.Class):java.util.Map");
    }

    private static Class[] parseMethodPars(ClassLoader classLoader, Method method) throws HookErrorException {
        MethodParams methodParams = (MethodParams) method.getAnnotation(MethodParams.class);
        MethodReflectParams methodReflectParams = (MethodReflectParams) method.getAnnotation(MethodReflectParams.class);
        if (methodParams != null) {
            return methodParams.value();
        }
        if (methodReflectParams != null) {
            if (methodReflectParams.value().length == 0) {
                return null;
            }
            Class[] clsArr = new Class[methodReflectParams.value().length];
            for (int i = 0; i < methodReflectParams.value().length; i++) {
                try {
                    clsArr[i] = classNameToClass(methodReflectParams.value()[i], classLoader);
                } catch (ClassNotFoundException e) {
                    throw new HookErrorException("hook method pars error: " + method.getName(), e);
                }
            }
            return clsArr;
        }
        if (getParsCount(method) <= 0) {
            return null;
        }
        if (getParsCount(method) == 1) {
            if (hasThisObject(method)) {
                return parseMethodParsNew(classLoader, method);
            }
            return null;
        }
        return parseMethodParsNew(classLoader, method);
    }

    private static Class[] parseMethodPars(ClassLoader classLoader, Field field) throws HookErrorException {
        MethodParams methodParams = (MethodParams) field.getAnnotation(MethodParams.class);
        MethodReflectParams methodReflectParams = (MethodReflectParams) field.getAnnotation(MethodReflectParams.class);
        if (methodParams != null) {
            return methodParams.value();
        }
        Class[] clsArr = null;
        if (methodReflectParams != null) {
            if (methodReflectParams.value().length == 0) {
                return null;
            }
            clsArr = new Class[methodReflectParams.value().length];
            for (int i = 0; i < methodReflectParams.value().length; i++) {
                try {
                    clsArr[i] = classNameToClass(methodReflectParams.value()[i], classLoader);
                } catch (ClassNotFoundException e) {
                    throw new HookErrorException("hook method pars error: " + field.getName(), e);
                }
            }
        }
        return clsArr;
    }

    private static Class[] parseMethodParsNew(ClassLoader classLoader, Method method) throws HookErrorException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class[] clsArr = null;
        if (parameterTypes != null && parameterTypes.length != 0) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int i = 0;
            for (int i2 = 0; i2 < parameterAnnotations.length; i2++) {
                Class<?> cls = parameterTypes[i2];
                Annotation[] annotationArr = parameterAnnotations[i2];
                try {
                    if (i2 == 0) {
                        if (isThisObject(annotationArr)) {
                            clsArr = new Class[parameterAnnotations.length - 1];
                        } else {
                            clsArr = new Class[parameterAnnotations.length];
                        }
                    }
                    clsArr[i] = getRealParType(classLoader, cls, annotationArr, method.isAnnotationPresent(SkipParamCheck.class));
                    i++;
                } catch (Exception e) {
                    throw new HookErrorException("hook method <" + method.getName() + "> parser pars error", e);
                }
            }
        }
        return clsArr;
    }

    private static Class getRealParType(ClassLoader classLoader, Class cls, Annotation[] annotationArr, boolean z) throws Exception {
        if (annotationArr != null && annotationArr.length != 0) {
            for (Annotation annotation : annotationArr) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    if (TextUtils.isEmpty(param.value())) {
                        return cls;
                    }
                    Class<?> classNameToClass = classNameToClass(param.value(), classLoader);
                    if (z || classNameToClass.equals(cls) || cls.isAssignableFrom(classNameToClass)) {
                        return classNameToClass;
                    }
                    throw new ClassCastException("hook method par cast error!");
                }
            }
        }
        return cls;
    }

    private static boolean hasThisObject(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return false;
        }
        return isThisObject(parameterAnnotations[0]);
    }

    private static boolean isThisObject(Annotation[] annotationArr) {
        if (annotationArr != null && annotationArr.length != 0) {
            for (Annotation annotation : annotationArr) {
                if (annotation instanceof ThisObject) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int getParsCount(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null) {
            return 0;
        }
        return parameterTypes.length;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static Class classNameToClass(String str, ClassLoader classLoader) throws ClassNotFoundException {
        char c;
        switch (str.hashCode()) {
            case -1325958191:
                if (str.equals(MethodReflectParams.DOUBLE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 104431:
                if (str.equals(MethodReflectParams.INT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 3039496:
                if (str.equals(MethodReflectParams.BYTE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3052374:
                if (str.equals(MethodReflectParams.CHAR)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3327612:
                if (str.equals(MethodReflectParams.LONG)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 64711720:
                if (str.equals(MethodReflectParams.BOOLEAN)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 97526364:
                if (str.equals(MethodReflectParams.FLOAT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 109413500:
                if (str.equals(MethodReflectParams.SHORT)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return Boolean.TYPE;
            case 1:
                return Byte.TYPE;
            case 2:
                return Character.TYPE;
            case 3:
                return Double.TYPE;
            case 4:
                return Float.TYPE;
            case 5:
                return Integer.TYPE;
            case 6:
                return Long.TYPE;
            case 7:
                return Short.TYPE;
            default:
                if (str.startsWith(".")) {
                    str = SandHookConfig.SELF_PACKAGE_NAME + str;
                }
                if (classLoader == null) {
                    return Class.forName(str);
                }
                return Class.forName(str, true, classLoader);
        }
    }

    private static boolean samePars(ClassLoader classLoader, Field field, Class[] clsArr) {
        try {
            Class[] parseMethodPars = parseMethodPars(classLoader, field);
            if (parseMethodPars == null && field.isAnnotationPresent(SkipParamCheck.class)) {
                return true;
            }
            if (clsArr == null) {
                clsArr = new Class[0];
            }
            if (parseMethodPars == null) {
                parseMethodPars = new Class[0];
            }
            if (clsArr.length != parseMethodPars.length) {
                return false;
            }
            for (int i = 0; i < clsArr.length; i++) {
                if (clsArr[i] != parseMethodPars[i]) {
                    return false;
                }
            }
            return true;
        } catch (HookErrorException unused) {
            return false;
        }
    }

    public static Class getTargetHookClass(ClassLoader classLoader, Class<?> cls) {
        HookClass hookClass = (HookClass) cls.getAnnotation(HookClass.class);
        HookReflectClass hookReflectClass = (HookReflectClass) cls.getAnnotation(HookReflectClass.class);
        if (hookClass != null) {
            return hookClass.value();
        }
        if (hookReflectClass != null) {
            try {
                String value = hookReflectClass.value();
                if (value.length() == 0) {
                    try {
                        return (Class) cls.getMethod("getHookClass", new Class[0]).invoke(null, new Object[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                if (value.startsWith(".")) {
                    value = SandHookConfig.SELF_PACKAGE_NAME + value;
                }
                if (classLoader == null) {
                    return Class.forName(value);
                }
                return Class.forName(value, true, classLoader);
            } catch (ClassNotFoundException e2) {
                System.err.println(e2);
            }
        }
        return null;
    }

    public static void checkSignature(Member member, Method method, Class[] clsArr) throws HookErrorException {
        Class<?> returnType;
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new HookErrorException("hook method must static! - " + method.getName());
        }
        if (member instanceof Constructor) {
            if (!method.getReturnType().equals(Void.TYPE)) {
                throw new HookErrorException("error return type! - " + method.getName());
            }
        } else if ((member instanceof Method) && (returnType = ((Method) member).getReturnType()) != method.getReturnType() && !returnType.isAssignableFrom(returnType)) {
            throw new HookErrorException("error return type! - " + method.getName());
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null) {
            parameterTypes = new Class[0];
        }
        if (clsArr == null) {
            clsArr = new Class[0];
        }
        if (clsArr.length == 0 && parameterTypes.length == 0) {
            return;
        }
        int i = 1;
        if (!Modifier.isStatic(member.getModifiers())) {
            if (parameterTypes.length == 0) {
                throw new HookErrorException("first par must be this! " + method.getName());
            }
            if (parameterTypes[0] != member.getDeclaringClass() && !parameterTypes[0].isAssignableFrom(member.getDeclaringClass())) {
                throw new HookErrorException("first par must be this! " + method.getName());
            }
            if (parameterTypes.length != clsArr.length + 1) {
                throw new HookErrorException("hook method pars must match the origin method! " + method.getName());
            }
        } else {
            if (parameterTypes.length != clsArr.length) {
                throw new HookErrorException("hook method pars must match the origin method! " + method.getName());
            }
            i = 0;
        }
        for (int i2 = 0; i2 < clsArr.length; i2++) {
            int i3 = i2 + i;
            if (parameterTypes[i3] != clsArr[i2] && !parameterTypes[i3].isAssignableFrom(clsArr[i2])) {
                throw new HookErrorException("hook method pars must match the origin method! " + method.getName());
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class HookEntity {
        public Method backup;
        public boolean backupIsStub;
        public Method hook;
        public boolean hookIsStub;
        public int hookMode;
        public boolean initClass;
        public Class[] pars;
        public boolean resolveDexCache;
        public Member target;

        public HookEntity(Member member) {
            this.hookIsStub = false;
            this.resolveDexCache = true;
            this.backupIsStub = true;
            this.initClass = true;
            this.target = member;
        }

        public HookEntity(Member member, Method method, Method method2) {
            this.hookIsStub = false;
            this.resolveDexCache = true;
            this.backupIsStub = true;
            this.initClass = true;
            this.target = member;
            this.hook = method;
            this.backup = method2;
        }

        public HookEntity(Member member, Method method, Method method2, boolean z) {
            this.hookIsStub = false;
            this.resolveDexCache = true;
            this.backupIsStub = true;
            this.initClass = true;
            this.target = member;
            this.hook = method;
            this.backup = method2;
            this.resolveDexCache = z;
        }

        public boolean isCtor() {
            return this.target instanceof Constructor;
        }

        public Object callOrigin(Object obj, Object... objArr) throws Throwable {
            return SandHook.callOriginMethod(this.backupIsStub, this.target, this.backup, obj, objArr);
        }
    }
}
