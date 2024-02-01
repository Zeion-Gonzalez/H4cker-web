package andhook.lib.xposed;

import andhook.lib.AndHook;
import andhook.lib.xposed.XC_MethodHook;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public final class XposedBridge {
    public static final String TAG = "AndHook";
    public static final ClassLoader BOOTCLASSLOADER = ClassLoader.getSystemClassLoader();
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private static final ConcurrentHashMap<Member, AdditionalHookInfo> sHookedMethodInfos = new ConcurrentHashMap<>();

    static {
        AndHook.ensureNativeLibraryLoaded(null);
    }

    public static void log(String str) {
        Log.i("AndHook", str);
    }

    public static void log(Throwable th) {
        Log.e("AndHook", Log.getStackTraceString(th));
    }

    public static XC_MethodHook.Unhook hookMethod(Member member, XC_MethodHook xC_MethodHook) {
        if (!(member instanceof Method) && !(member instanceof Constructor)) {
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + member.toString());
        }
        if (Modifier.isAbstract(member.getModifiers())) {
            throw new IllegalArgumentException("Cannot hook abstract methods: " + member.toString());
        }
        AdditionalHookInfo additionalHookInfo = sHookedMethodInfos.get(member);
        if (additionalHookInfo == null) {
            if (Modifier.isStatic(member.getModifiers())) {
                AndHook.ensureClassInitialized(member.getDeclaringClass());
            }
            additionalHookInfo = new AdditionalHookInfo(member, AndHook.backup(member));
            if (additionalHookInfo.slot == -1) {
                throw new RuntimeException("Failed to backup methods: " + member.toString());
            }
            additionalHookInfo.callbacks.add(xC_MethodHook);
            if (!AndHook.hook(member, additionalHookInfo, additionalHookInfo.slot)) {
                throw new RuntimeException("Failed to hook methods: " + member.toString());
            }
            sHookedMethodInfos.put(member, additionalHookInfo);
        } else {
            if (!additionalHookInfo.method.getDeclaringClass().getClassLoader().equals(member.getDeclaringClass().getClassLoader())) {
                throw new RuntimeException("Unexpected same methods within difference CL: " + member.toString());
            }
            additionalHookInfo.callbacks.add(xC_MethodHook);
        }
        xC_MethodHook.getClass();
        return new XC_MethodHook.Unhook(additionalHookInfo.method, additionalHookInfo.slot);
    }

    public static void unhookMethod(Member member, XC_MethodHook xC_MethodHook) {
        AdditionalHookInfo additionalHookInfo = sHookedMethodInfos.get(member);
        if (additionalHookInfo != null) {
            additionalHookInfo.callbacks.remove(xC_MethodHook);
        }
    }

    public static boolean unhookMethod(Member member, int i) {
        boolean restore = AndHook.restore(i, member);
        if (restore) {
            sHookedMethodInfos.remove(member);
        }
        return restore;
    }

    public static HashSet<XC_MethodHook.Unhook> hookAllMethods(Class<?> cls, XC_MethodHook xC_MethodHook) {
        HashSet<XC_MethodHook.Unhook> hashSet = new HashSet<>();
        for (Method method : cls.getDeclaredMethods()) {
            hashSet.add(hookMethod(method, xC_MethodHook));
        }
        return hashSet;
    }

    public static HashSet<XC_MethodHook.Unhook> hookAllMethods(Class<?> cls, String str, XC_MethodHook xC_MethodHook) {
        HashSet<XC_MethodHook.Unhook> hashSet = new HashSet<>();
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().equals(str)) {
                hashSet.add(hookMethod(method, xC_MethodHook));
            }
        }
        return hashSet;
    }

    public static HashSet<XC_MethodHook.Unhook> hookAllConstructors(Class<?> cls, XC_MethodHook xC_MethodHook) {
        HashSet<XC_MethodHook.Unhook> hashSet = new HashSet<>();
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            hashSet.add(hookMethod(constructor, xC_MethodHook));
        }
        return hashSet;
    }

    private static Object handleHookedMethod(Object obj, Object obj2, Object[] objArr) throws Throwable {
        AdditionalHookInfo additionalHookInfo = (AdditionalHookInfo) obj;
        Object[] snapshot = additionalHookInfo.callbacks.getSnapshot();
        int length = snapshot.length;
        if (length == 0) {
            return invokeOriginalMethod(additionalHookInfo.slot, obj2, objArr);
        }
        XC_MethodHook.MethodHookParam methodHookParam = new XC_MethodHook.MethodHookParam();
        methodHookParam.slot = additionalHookInfo.slot;
        methodHookParam.method = additionalHookInfo.method;
        methodHookParam.thisObject = obj2;
        methodHookParam.args = objArr;
        int i = 0;
        while (true) {
            try {
                ((XC_MethodHook) snapshot[i]).beforeHookedMethod(methodHookParam);
                if (methodHookParam.returnEarly) {
                    i++;
                    break;
                }
            } catch (Throwable th) {
                log(th);
                methodHookParam.setResult(null);
                methodHookParam.returnEarly = false;
            }
            i++;
            if (i >= length) {
                break;
            }
        }
        if (!methodHookParam.returnEarly) {
            try {
                methodHookParam.setResult(invokeOriginalMethod(additionalHookInfo.slot, methodHookParam.thisObject, methodHookParam.args));
            } catch (Throwable th2) {
                methodHookParam.setThrowable(th2);
            }
        }
        int i2 = i - 1;
        do {
            Object result = methodHookParam.getResult();
            Throwable throwable = methodHookParam.getThrowable();
            try {
                ((XC_MethodHook) snapshot[i2]).afterHookedMethod(methodHookParam);
            } catch (Throwable th3) {
                log(th3);
                if (throwable == null) {
                    methodHookParam.setResult(result);
                } else {
                    methodHookParam.setThrowable(throwable);
                }
            }
            i2--;
        } while (i2 >= 0);
        if (methodHookParam.hasThrowable()) {
            throw methodHookParam.getThrowable();
        }
        return methodHookParam.getResult();
    }

    public static int getBackupSlot(Member member) {
        AdditionalHookInfo additionalHookInfo = sHookedMethodInfos.get(member);
        if (additionalHookInfo != null) {
            return additionalHookInfo.slot;
        }
        return -1;
    }

    public static Object invokeOriginalMethod(Member member, Object obj, Object[] objArr) {
        int backupSlot = getBackupSlot(member);
        if (backupSlot != -1) {
            return invokeOriginalMethod(backupSlot, obj, objArr);
        }
        return null;
    }

    public static Object invokeOriginalMethod(int i, Object obj, Object[] objArr) {
        return AndHook.invoke(i, obj, objArr);
    }

    /* loaded from: classes2.dex */
    public static final class CopyOnWriteSortedSet<E> {
        private volatile transient Object[] elements = XposedBridge.EMPTY_ARRAY;

        public synchronized boolean add(E e) {
            if (indexOf(e) >= 0) {
                return false;
            }
            Object[] objArr = new Object[this.elements.length + 1];
            System.arraycopy(this.elements, 0, objArr, 0, this.elements.length);
            objArr[this.elements.length] = e;
            Arrays.sort(objArr);
            this.elements = objArr;
            return true;
        }

        public synchronized boolean remove(E e) {
            int indexOf = indexOf(e);
            if (indexOf == -1) {
                return false;
            }
            Object[] objArr = new Object[this.elements.length - 1];
            System.arraycopy(this.elements, 0, objArr, 0, indexOf);
            System.arraycopy(this.elements, indexOf + 1, objArr, indexOf, (this.elements.length - indexOf) - 1);
            this.elements = objArr;
            return true;
        }

        private int indexOf(Object obj) {
            for (int i = 0; i < this.elements.length; i++) {
                if (obj.equals(this.elements[i])) {
                    return i;
                }
            }
            return -1;
        }

        public Object[] getSnapshot() {
            return this.elements;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class AdditionalHookInfo {
        final CopyOnWriteSortedSet<XC_MethodHook> callbacks;
        final Member method;
        final int slot;

        private AdditionalHookInfo(Member member, int i) {
            this.callbacks = new CopyOnWriteSortedSet<>();
            this.method = member;
            this.slot = i;
        }
    }
}
