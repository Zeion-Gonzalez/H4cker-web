package com.applisto.appcloner.classes;

import android.content.Context;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.hooking.Hooking;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.MethodParams;
import java.lang.Thread;

/* loaded from: classes2.dex */
public class CrashHandler {
    public static final int CRASH_HANDLER_NOTIFICATION_ID = 1621363246;
    private static final String TAG = CrashHandler.class.getSimpleName();
    private boolean mAppBundle;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mExceptionHandler = new Thread.UncaughtExceptionHandler() { // from class: com.applisto.appcloner.classes.CrashHandler.1
        /* JADX WARN: Removed duplicated region for block: B:28:0x00b7 A[Catch: Exception -> 0x00ce, TryCatch #0 {Exception -> 0x00ce, blocks: (B:2:0x0000, B:4:0x000f, B:6:0x0017, B:8:0x0029, B:9:0x003b, B:11:0x004e, B:13:0x005a, B:16:0x0063, B:18:0x006b, B:26:0x0084, B:28:0x00b7, B:29:0x00c3, B:22:0x0077, B:10:0x0040), top: B:37:0x0000 }] */
        @Override // java.lang.Thread.UncaughtExceptionHandler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void uncaughtException(java.lang.Thread r4, java.lang.Throwable r5) {
            /*
                r3 = this;
                java.lang.String r4 = com.applisto.appcloner.classes.CrashHandler.access$000()     // Catch: java.lang.Exception -> Lce
                com.applisto.appcloner.classes.util.Log.m21w(r4, r5)     // Catch: java.lang.Exception -> Lce
                com.applisto.appcloner.classes.CrashHandler r4 = com.applisto.appcloner.classes.CrashHandler.this     // Catch: java.lang.Exception -> Lce
                boolean r4 = com.applisto.appcloner.classes.CrashHandler.access$100(r4)     // Catch: java.lang.Exception -> Lce
                if (r4 == 0) goto L17
                com.applisto.appcloner.classes.CrashHandler r4 = com.applisto.appcloner.classes.CrashHandler.this     // Catch: java.lang.Exception -> Lce
                boolean r4 = com.applisto.appcloner.classes.CrashHandler.access$200(r4)     // Catch: java.lang.Exception -> Lce
                if (r4 == 0) goto Ld6
            L17:
                com.applisto.appcloner.classes.CrashHandler r4 = com.applisto.appcloner.classes.CrashHandler.this     // Catch: java.lang.Exception -> Lce
                android.content.Context r4 = com.applisto.appcloner.classes.CrashHandler.access$300(r4)     // Catch: java.lang.Exception -> Lce
                java.lang.String r4 = com.applisto.appcloner.classes.Utils.getAppName(r4)     // Catch: java.lang.Exception -> Lce
                com.applisto.appcloner.classes.CrashHandler r0 = com.applisto.appcloner.classes.CrashHandler.this     // Catch: java.lang.Exception -> Lce
                boolean r0 = com.applisto.appcloner.classes.CrashHandler.access$100(r0)     // Catch: java.lang.Exception -> Lce
                if (r0 == 0) goto L40
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> Lce
                r0.<init>()     // Catch: java.lang.Exception -> Lce
                java.lang.String r1 = "Ignored "
                r0.append(r1)     // Catch: java.lang.Exception -> Lce
                r0.append(r4)     // Catch: java.lang.Exception -> Lce
                java.lang.String r4 = " crash"
                r0.append(r4)     // Catch: java.lang.Exception -> Lce
            L3b:
                java.lang.String r4 = r0.toString()     // Catch: java.lang.Exception -> Lce
                goto L4e
            L40:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> Lce
                r0.<init>()     // Catch: java.lang.Exception -> Lce
                r0.append(r4)     // Catch: java.lang.Exception -> Lce
                java.lang.String r4 = " crashed"
                r0.append(r4)     // Catch: java.lang.Exception -> Lce
                goto L3b
            L4e:
                java.lang.String r5 = com.applisto.appcloner.classes.util.Log.getStackTraceString(r5)     // Catch: java.lang.Exception -> Lce
                java.lang.String r0 = "dlopen failed"
                boolean r0 = r5.contains(r0)     // Catch: java.lang.Exception -> Lce
                if (r0 != 0) goto L82
                java.lang.String r0 = "java.lang.UnsatisfiedLinkError"
                boolean r0 = r5.contains(r0)     // Catch: java.lang.Exception -> Lce
                if (r0 == 0) goto L63
                goto L82
            L63:
                java.lang.String r0 = "java.lang.NullPointerException: Attempt to read from field 'java.lang.String android.content.pm.PackageItemInfo.packageName' on a null object reference"
                boolean r0 = r5.contains(r0)     // Catch: java.lang.Exception -> Lce
                if (r0 == 0) goto L77
                com.applisto.appcloner.classes.CrashHandler r5 = com.applisto.appcloner.classes.CrashHandler.this     // Catch: java.lang.Exception -> Lce
                boolean r5 = com.applisto.appcloner.classes.CrashHandler.access$100(r5)     // Catch: java.lang.Exception -> Lce
                if (r5 == 0) goto L74
                return
            L74:
                java.lang.String r5 = "Please try enabling 'Ignore crashes' under 'Cloning options'."
                goto L84
            L77:
                java.lang.String r0 = "java.lang.ClassNotFoundException: Didn't find class"
                boolean r0 = r5.contains(r0)     // Catch: java.lang.Exception -> Lce
                if (r0 == 0) goto L84
                java.lang.String r5 = "Please try enabling 'Increase compatibility' under 'Cloning options' or use the manifest cloning mode."
                goto L84
            L82:
                java.lang.String r5 = "Please try enabling 'Skip native libraries' under 'Cloning options' or use the manifest cloning mode."
            L84:
                com.applisto.appcloner.classes.CrashHandler r0 = com.applisto.appcloner.classes.CrashHandler.this     // Catch: java.lang.Exception -> Lce
                android.content.Context r0 = com.applisto.appcloner.classes.CrashHandler.access$300(r0)     // Catch: java.lang.Exception -> Lce
                java.lang.String r1 = "notification"
                java.lang.Object r0 = r0.getSystemService(r1)     // Catch: java.lang.Exception -> Lce
                android.app.NotificationManager r0 = (android.app.NotificationManager) r0     // Catch: java.lang.Exception -> Lce
                android.app.Notification$Builder r1 = new android.app.Notification$Builder     // Catch: java.lang.Exception -> Lce
                com.applisto.appcloner.classes.CrashHandler r2 = com.applisto.appcloner.classes.CrashHandler.this     // Catch: java.lang.Exception -> Lce
                android.content.Context r2 = com.applisto.appcloner.classes.CrashHandler.access$300(r2)     // Catch: java.lang.Exception -> Lce
                r1.<init>(r2)     // Catch: java.lang.Exception -> Lce
                android.app.Notification$Builder r4 = r1.setContentTitle(r4)     // Catch: java.lang.Exception -> Lce
                android.app.Notification$Builder r4 = r4.setContentText(r5)     // Catch: java.lang.Exception -> Lce
                long r1 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Exception -> Lce
                android.app.Notification$Builder r4 = r4.setWhen(r1)     // Catch: java.lang.Exception -> Lce
                r1 = 1
                com.applisto.appcloner.classes.Utils.setSmallNotificationIcon(r4, r1)     // Catch: java.lang.Exception -> Lce
                int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Exception -> Lce
                r2 = 16
                if (r1 < r2) goto Lc3
                android.app.Notification$BigTextStyle r1 = new android.app.Notification$BigTextStyle     // Catch: java.lang.Exception -> Lce
                r1.<init>()     // Catch: java.lang.Exception -> Lce
                android.app.Notification$BigTextStyle r5 = r1.bigText(r5)     // Catch: java.lang.Exception -> Lce
                r4.setStyle(r5)     // Catch: java.lang.Exception -> Lce
            Lc3:
                r5 = 1621363246(0x60a40a2e, float:9.456249E19)
                android.app.Notification r4 = r4.getNotification()     // Catch: java.lang.Exception -> Lce
                r0.notify(r5, r4)     // Catch: java.lang.Exception -> Lce
                goto Ld6
            Lce:
                r4 = move-exception
                java.lang.String r5 = com.applisto.appcloner.classes.CrashHandler.access$000()
                com.applisto.appcloner.classes.util.Log.m21w(r5, r4)
            Ld6:
                com.applisto.appcloner.classes.CrashHandler r4 = com.applisto.appcloner.classes.CrashHandler.this
                boolean r4 = com.applisto.appcloner.classes.CrashHandler.access$100(r4)
                if (r4 != 0) goto Le2
                r4 = 0
                java.lang.System.exit(r4)
            Le2:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.applisto.appcloner.classes.CrashHandler.C04221.uncaughtException(java.lang.Thread, java.lang.Throwable):void");
        }
    };
    private final boolean mIgnoreCrashes;
    private final boolean mIgnoreCrashesShowCrashMessages;

    public CrashHandler(CloneSettings cloneSettings) {
        this.mIgnoreCrashes = cloneSettings.getBoolean("ignoreCrashes", false).booleanValue();
        this.mIgnoreCrashesShowCrashMessages = cloneSettings.getBoolean("ignoreCrashesShowCrashMessages", false).booleanValue();
        Log.m15i(TAG, "CrashHandler; mIgnoreCrashes: " + this.mIgnoreCrashes + ", mIgnoreCrashesShowCrashMessages: " + this.mIgnoreCrashesShowCrashMessages);
    }

    public void install(Context context) {
        this.mContext = context;
        try {
            this.mAppBundle = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getBoolean("com.applisto.appcloner.appBundle");
            Log.m15i(TAG, "install; mAppBundle: " + this.mAppBundle);
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        try {
            Thread.setDefaultUncaughtExceptionHandler(this.mExceptionHandler);
            Hooking.initHooking(context);
            Hooking.addHookClass(Hook.class);
            Log.m15i(TAG, "install; hooks installed");
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    @HookClass(Thread.class)
    /* loaded from: classes2.dex */
    public static class Hook {
        @MethodParams({Thread.UncaughtExceptionHandler.class})
        @HookMethod("setDefaultUncaughtExceptionHandler")
        public static void setDefaultUncaughtExceptionHandlerHook(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            Log.m15i(CrashHandler.TAG, "setDefaultUncaughtExceptionHandlerHook; ");
        }

        public static void setDefaultUncaughtExceptionHandlerHook(Class cls, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            Log.m15i(CrashHandler.TAG, "setDefaultUncaughtExceptionHandlerHook; ");
        }
    }
}
