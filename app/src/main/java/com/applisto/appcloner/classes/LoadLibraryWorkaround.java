package com.applisto.appcloner.classes;

import android.content.Context;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.hooking.Hooking;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.MethodParams;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class LoadLibraryWorkaround {
    private static final String TAG = LoadLibraryWorkaround.class.getSimpleName();
    private static String sOriginalPackageName;
    private static String sPackageName;

    public static void install(Context context, String str) {
        Log.m15i(TAG, "install; originalPackageName: " + str);
        sPackageName = context.getPackageName();
        sOriginalPackageName = str;
        Hooking.initHooking(context);
        Hooking.addHookClass(Hook.class);
        Log.m15i(TAG, "install; hooks installed");
    }

    @HookClass(Runtime.class)
    /* loaded from: classes2.dex */
    public static class Hook {
        @HookMethodBackup("loadLibrary0")
        @MethodParams({ClassLoader.class, String.class})
        static Method loadLibrary0Backup;

        @MethodParams({ClassLoader.class, String.class})
        @HookMethod("loadLibrary0")
        public static void loadLibrary0Hook(Runtime runtime, ClassLoader classLoader, String str) throws Throwable {
            try {
                Hooking.callInstanceOrigin(loadLibrary0Backup, runtime, classLoader, str);
            } catch (Throwable th) {
                Log.m19w(LoadLibraryWorkaround.TAG, th.toString());
                int indexOf = LoadLibraryWorkaround.sPackageName.indexOf(str);
                if (indexOf != -1) {
                    String substring = LoadLibraryWorkaround.sOriginalPackageName.substring(indexOf);
                    Log.m15i(LoadLibraryWorkaround.TAG, "loadLibrary0Hook; new libname: " + substring);
                    Hooking.callInstanceOrigin(loadLibrary0Backup, runtime, classLoader, substring);
                    return;
                }
                throw th;
            }
        }
    }
}
