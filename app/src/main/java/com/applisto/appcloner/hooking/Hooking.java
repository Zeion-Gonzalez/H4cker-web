package com.applisto.appcloner.hooking;

import andhook.lib.AndHook;
import andhook.lib.HookHelper;
import android.content.Context;
import android.os.Build;
import com.applisto.appcloner.classes.Utils;
import com.applisto.appcloner.classes.util.Log;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.SandHookConfig;
import com.swift.sandhook.wrapper.HookErrorException;
import com.swift.sandhook.wrapper.HookWrapper;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
public class Hooking {
    private static final String TAG = Hooking.class.getSimpleName();
    private static ExecutorService sHookExecutor;
    private static boolean sHookingInited;
    private static boolean sUseDelayedHooking;
    private static Boolean sUseSandHook;

    public static void setUseDelayedHooking(boolean z) {
        Log.m15i(TAG, "setUseDelayedHooking; useDelayedHooking: " + z);
        sUseDelayedHooking = z;
    }

    public static synchronized boolean useSandHook() {
        boolean booleanValue;
        synchronized (Hooking.class) {
            if (sUseSandHook == null) {
                sUseSandHook = false;
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean isX86 = Utils.isX86();
                    Log.m15i(TAG, "useSandHook; x86: " + isX86);
                    sUseSandHook = Boolean.valueOf(isX86 ? false : true);
                    if (isX86 && sUseDelayedHooking) {
                        sHookExecutor = Executors.newSingleThreadExecutor();
                        sHookExecutor.submit(new Runnable() { // from class: com.applisto.appcloner.hooking.Hooking.1
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    Thread.sleep(1000L);
                                } catch (InterruptedException unused) {
                                }
                            }
                        });
                    }
                }
            }
            booleanValue = sUseSandHook.booleanValue();
        }
        return booleanValue;
    }

    public static synchronized void initHooking(Context context) {
        synchronized (Hooking.class) {
            if (!sHookingInited) {
                sHookingInited = true;
                if (useSandHook()) {
                    try {
                        Log.m15i(TAG, "initHooking; SandHook");
                        SandHookConfig.SELF_PACKAGE_NAME = context.getPackageName();
                        SandHook.disableVMInline();
                        SandHook.tryDisableProfile(SandHookConfig.SELF_PACKAGE_NAME);
                        SandHook.disableDex2oatInline(false);
                        if (SandHookConfig.SDK_INT >= 28) {
                            SandHook.passApiCheck();
                        }
                        return;
                    } catch (Throwable th) {
                        Log.m21w(TAG, th);
                        sUseSandHook = false;
                    }
                }
                try {
                    Log.m15i(TAG, "initHooking; AndHook");
                    SandHookConfig.SELF_PACKAGE_NAME = context.getPackageName();
                    AndHook.ensureNativeLibraryLoaded(null);
                } catch (Throwable th2) {
                    Log.m21w(TAG, th2);
                }
            }
        }
    }

    public static void addHookClass(Class cls) {
        Log.m15i(TAG, "addHookClass; hookWrapperClass: " + cls);
        try {
            if (useSandHook()) {
                SandHook.addHookClass(cls);
                return;
            }
            Class targetHookClass = HookWrapper.getTargetHookClass(null, cls);
            if (targetHookClass == null) {
                throw new HookErrorException("Failed to find target hook class for " + cls.getName());
            }
            AndHook.ensureClassInitialized(targetHookClass);
            for (final HookWrapper.HookEntity hookEntity : HookWrapper.getHookMethods(null, targetHookClass, cls).values()) {
                AndHook.ensureClassInitialized(hookEntity.target.getDeclaringClass());
                if (hookEntity.target instanceof Method) {
                    Method method = (Method) hookEntity.target;
                    if (Modifier.isStatic(method.getModifiers())) {
                        try {
                            ArrayList arrayList = new ArrayList(Arrays.asList(method.getParameterTypes()));
                            arrayList.add(0, Class.class);
                            hookEntity.hook = hookEntity.hook.getDeclaringClass().getMethod(hookEntity.hook.getName(), (Class[]) arrayList.toArray(new Class[0]));
                        } catch (Exception e) {
                            Log.m21w(TAG, e);
                            System.exit(1);
                        }
                    }
                }
                if (sHookExecutor != null) {
                    sHookExecutor.submit(new Runnable() { // from class: com.applisto.appcloner.hooking.-$$Lambda$Hooking$RIeOXO-ttSLkTwf33vBcEr3zvM0
                        @Override // java.lang.Runnable
                        public final void run() {
                            Hooking.lambda$addHookClass$0(HookWrapper.HookEntity.this);
                        }
                    });
                } else {
                    HookHelper.hook(hookEntity.target, hookEntity.hook);
                }
            }
        } catch (Throwable th) {
            Log.m19w(TAG, "addHookClass; t: " + th + ", cause: " + th.getCause());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$addHookClass$0(HookWrapper.HookEntity hookEntity) {
        Log.m19w(TAG, "addHookClass; hooking using thread executor...");
        HookHelper.hook(hookEntity.target, hookEntity.hook);
    }

    public static <T> T callStaticOrigin(Method method, Object... objArr) throws Throwable {
        if (useSandHook()) {
            return (T) SandHook.callOriginByBackup(method, null, objArr);
        }
        return (T) HookHelper.invokeObjectOrigin(null, objArr);
    }

    public static <T> T callInstanceOrigin(Method method, Object obj, Object... objArr) throws Throwable {
        if (useSandHook()) {
            return (T) SandHook.callOriginByBackup(method, obj, objArr);
        }
        return (T) HookHelper.invokeObjectOrigin(obj, objArr);
    }
}
