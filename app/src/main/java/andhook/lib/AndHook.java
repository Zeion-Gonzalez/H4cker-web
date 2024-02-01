package andhook.lib;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/* loaded from: classes2.dex */
public final class AndHook {
    private static final String LIB_NAME = "AK";
    public static final String LOG_TAG = "AndHook";
    private static final String PREF_KEY_MODDING_OPTIONS_WARNING_SHOWN = "modding_options_warning_shown";
    public static final String VERSION = "3.6.2";

    public static native int backup(Class<?> cls, String str, String str2);

    public static native int backup(Member member);

    public static native void deoptimizeMethod(Member member);

    public static native void disableLogging(boolean z);

    private static native void dumpClassMethods(Class<?> cls, String str);

    public static native void enableFastDexLoad(boolean z);

    public static native String getVersionInfo();

    public static native boolean hook(Class<?> cls, String str, String str2, Object obj, int i);

    public static native boolean hook(Member member, Object obj, int i);

    private static native boolean initializeClass(Class<?> cls);

    public static native Object invoke(int i, Object obj, Object... objArr);

    public static native void jitCompile(Member member);

    public static native void optimizeMethod(Member member);

    public static native boolean restore(int i, Member member);

    public static native void resumeAll();

    public static native void startDaemons();

    public static native void stopDaemons();

    public static native boolean suspendAll();

    public static void ensureNativeLibraryLoaded(File file) {
        if (Build.VERSION.SDK_INT >= 29) {
            System.out.println("ensureNativeLibraryLoaded; Android 10 not supported");
            try {
                Class<?> cls = Class.forName("android.app.ActivityThread");
                Application application = null;
                if (Build.VERSION.SDK_INT >= 9) {
                    application = (Application) cls.getMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
                } else {
                    Method[] methods = cls.getMethods();
                    int length = methods.length;
                    int i = 0;
                    loop0: while (true) {
                        if (i >= length) {
                            break;
                        }
                        Method method = methods[i];
                        if ("currentActivityThread".equals(method.getName())) {
                            Object invoke = method.invoke(null, new Object[0]);
                            for (Method method2 : cls.getMethods()) {
                                if ("getApplication".equals(method2.getName())) {
                                    application = (Application) method2.invoke(invoke, new Object[0]);
                                    break loop0;
                                }
                            }
                        }
                        i++;
                    }
                }
                if (application != null) {
                    SharedPreferences sharedPreferences = application.getSharedPreferences(com.applisto.appcloner.classes.BuildConfig.APPLICATION_ID, 0);
                    if (sharedPreferences.getBoolean(PREF_KEY_MODDING_OPTIONS_WARNING_SHOWN, false)) {
                        return;
                    }
                    sharedPreferences.edit().putBoolean(PREF_KEY_MODDING_OPTIONS_WARNING_SHOWN, true).apply();
                    Toast.makeText(application, "One or more enabled modding options are no longer supported on Android 10.", 1).show();
                    return;
                }
                return;
            } catch (Throwable unused) {
                return;
            }
        }
        try {
            getVersionInfo();
        } catch (UnsatisfiedLinkError unused2) {
            File file2 = new File(System.getProperty("java.io.tmpdir", "/data/local/tmp/"));
            if (!file2.canWrite() || !file2.canExecute()) {
                Log.w("AndHook", "Missing cache directory " + file2);
            }
            try {
                if (file == null) {
                    System.loadLibrary(LIB_NAME);
                } else {
                    System.load(new File(file, "libAK.so").getAbsolutePath());
                }
            } catch (UnsatisfiedLinkError e) {
                try {
                    if (file == null) {
                        System.loadLibrary("AKCompat");
                    } else {
                        System.load(new File(file, "libAKCompat.so").getAbsolutePath());
                    }
                } catch (UnsatisfiedLinkError unused3) {
                    throw new RuntimeException("Incompatible platform " + Build.VERSION.SDK_INT, e);
                }
            }
        }
    }

    public static int hook(Member member, Object obj) {
        int backup = backup(member);
        if (backup == -1 || hook(member, obj, backup)) {
            return backup;
        }
        return -1;
    }

    public static int hook(Class<?> cls, String str, String str2, Object obj) {
        int backup = backup(cls, str, str2);
        if (backup == -1 || hook(cls, str, str2, obj, backup)) {
            return backup;
        }
        return -1;
    }

    public static void hookNoBackup(Member member, Object obj) {
        hook(member, obj, -1);
    }

    public static void hookNoBackup(Class<?> cls, String str, String str2, Object obj) {
        hook(cls, str, str2, obj, -1);
    }

    public static boolean ensureClassInitialized(Class<?> cls) {
        if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) {
            Log.w("AndHook", "interface or abstract class `" + cls.getName() + "` cannot be initialized!");
            return false;
        }
        return initializeClass(cls);
    }

    public static void dumpClassMethods(Class<?> cls) {
        dumpClassMethods(cls, null);
    }

    public static void dumpClassMethods(String str) {
        dumpClassMethods(null, str);
    }

    public static void invokeVoidMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            Dalvik.invokeVoidMethod(i, obj, objArr);
        } else {
            invoke(i, obj, objArr);
        }
    }

    public static boolean invokeBooleanMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeBooleanMethod(i, obj, objArr);
        }
        return ((Boolean) invoke(i, obj, objArr)).booleanValue();
    }

    public static byte invokeByteMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeByteMethod(i, obj, objArr);
        }
        return ((Byte) invoke(i, obj, objArr)).byteValue();
    }

    public static short invokeShortMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeShortMethod(i, obj, objArr);
        }
        return ((Short) invoke(i, obj, objArr)).shortValue();
    }

    public static char invokeCharMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeCharMethod(i, obj, objArr);
        }
        return ((Character) invoke(i, obj, objArr)).charValue();
    }

    public static int invokeIntMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeIntMethod(i, obj, objArr);
        }
        return ((Integer) invoke(i, obj, objArr)).intValue();
    }

    public static long invokeLongMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeLongMethod(i, obj, objArr);
        }
        return ((Long) invoke(i, obj, objArr)).longValue();
    }

    public static float invokeFloatMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeFloatMethod(i, obj, objArr);
        }
        return ((Float) invoke(i, obj, objArr)).floatValue();
    }

    public static double invokeDoubleMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return Dalvik.invokeDoubleMethod(i, obj, objArr);
        }
        return ((Double) invoke(i, obj, objArr)).doubleValue();
    }

    public static <T> T invokeObjectMethod(int i, Object obj, Object... objArr) {
        if (Build.VERSION.SDK_INT <= 20) {
            return (T) Dalvik.invokeObjectMethod(i, obj, objArr);
        }
        return (T) invoke(i, obj, objArr);
    }

    public static <T> T invokeMethod(int i, Object obj, Object... objArr) {
        return (T) invoke(i, obj, objArr);
    }

    /* loaded from: classes2.dex */
    private static final class Dalvik {
        public static native boolean invokeBooleanMethod(int i, Object obj, Object... objArr);

        public static native byte invokeByteMethod(int i, Object obj, Object... objArr);

        public static native char invokeCharMethod(int i, Object obj, Object... objArr);

        public static native double invokeDoubleMethod(int i, Object obj, Object... objArr);

        public static native float invokeFloatMethod(int i, Object obj, Object... objArr);

        public static native int invokeIntMethod(int i, Object obj, Object... objArr);

        public static native long invokeLongMethod(int i, Object obj, Object... objArr);

        public static native Object invokeObjectMethod(int i, Object obj, Object... objArr);

        public static native short invokeShortMethod(int i, Object obj, Object... objArr);

        public static native void invokeVoidMethod(int i, Object obj, Object... objArr);

        private Dalvik() {
        }
    }
}
