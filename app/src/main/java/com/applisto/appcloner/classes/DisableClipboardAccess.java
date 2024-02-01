package com.applisto.appcloner.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.SimpleCrypt;
import com.applisto.appcloner.hooking.Hooking;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class DisableClipboardAccess {
    private static final int CLEAR_CLIPBOARD_TIMEOUT_MILLIS = 30000;
    private static String sClearingClipDataToString;
    private static ClipData sClipData;
    private static boolean sClipboardTimeout;
    private static Context sContext;
    private static boolean sDisableClipboardReadAccess;
    private static boolean sDisableClipboardWriteAccess;
    private static boolean sPersistentClipboard;
    private static SharedPreferences sPreferences;
    private static boolean sPrivateClipboard;
    private static Properties sStringsProperties;
    private String mSetClipboardDataOnStart;
    private static final String TAG = DisableClipboardAccess.class.getSimpleName();
    private static List<ClipboardManager.OnPrimaryClipChangedListener> sListeners = new CopyOnWriteArrayList();
    private static Handler sTimeoutHandler = new Handler();
    private static final ClipData EMPTY_CLIP_DATA = ClipData.newPlainText("", "");

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisableClipboardAccess(CloneSettings cloneSettings) {
        sPrivateClipboard = cloneSettings.getBoolean("privateClipboard", false).booleanValue();
        if (sPrivateClipboard) {
            sPersistentClipboard = cloneSettings.getBoolean("persistentClipboard", false).booleanValue();
        } else {
            sDisableClipboardReadAccess = cloneSettings.getBoolean("disableClipboardReadAccess", false).booleanValue();
            sDisableClipboardWriteAccess = cloneSettings.getBoolean("disableClipboardWriteAccess", false).booleanValue();
        }
        sClipboardTimeout = cloneSettings.getBoolean("clipboardTimeout", false).booleanValue();
        this.mSetClipboardDataOnStart = cloneSettings.getString("setClipboardDataOnStart", "");
        Log.m15i(TAG, "DisableClipboardAccess; mPrivateClipboard: " + sPrivateClipboard + ", mPersistentClipboard: " + sPersistentClipboard + ", mDisableClipboardReadAccess: " + sDisableClipboardReadAccess + ", mDisableClipboardWriteAccess: " + sDisableClipboardWriteAccess + ", mClipboardTimeout: " + sClipboardTimeout);
    }

    public void install(Context context, Properties properties) {
        Log.m15i(TAG, "install; ");
        sContext = context;
        sStringsProperties = properties;
        if (sDisableClipboardReadAccess || sDisableClipboardWriteAccess || sPrivateClipboard || sClipboardTimeout) {
            sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            loadClipboardIfPersistent();
            Hooking.initHooking(context);
            Hooking.addHookClass(Hook1.class);
            Hooking.addHookClass(Hook2.class);
            Log.m15i(TAG, "install; hooks installed");
        }
        if (TextUtils.isEmpty(this.mSetClipboardDataOnStart)) {
            return;
        }
        try {
            ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("", this.mSetClipboardDataOnStart));
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    @HookReflectClass("android.content.ClipboardManager")
    /* loaded from: classes2.dex */
    public static class Hook1 {
        @HookMethodBackup
        @MethodParams({Context.class, Handler.class})
        static Method constructorBackup;

        @MethodParams({Context.class, Handler.class})
        @HookMethod
        public static void constructorHook(ClipboardManager clipboardManager, Context context, Handler handler) throws Throwable {
            Log.m15i(DisableClipboardAccess.TAG, "constructorHook; context: " + context + ", handler: " + handler);
            Hooking.callInstanceOrigin(constructorBackup, clipboardManager, context, handler);
            DisableClipboardAccess.installAndroidClipboardManagerHook(clipboardManager);
        }
    }

    @HookReflectClass("com.samsung.android.content.clipboard.SemClipboardManager")
    /* loaded from: classes2.dex */
    public static class Hook2 {
        @HookMethodBackup
        @MethodParams({Context.class, Handler.class})
        static Method constructorBackup;

        @MethodParams({Context.class, Handler.class})
        @HookMethod
        public static void constructorHook(Object obj, Context context, Handler handler) throws Throwable {
            Log.m15i(DisableClipboardAccess.TAG, "constructorHook; context: " + context + ", handler: " + handler);
            Hooking.callInstanceOrigin(constructorBackup, obj, context, handler);
            DisableClipboardAccess.installSamsungClipboardManagerHook(obj);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void installAndroidClipboardManagerHook(final ClipboardManager clipboardManager) {
        final Object obj;
        Log.m15i(TAG, "installAndroidClipboardManagerHook; clipboardManager: " + clipboardManager);
        try {
            try {
                Field declaredField = clipboardManager.getClass().getDeclaredField("mService");
                declaredField.setAccessible(true);
                obj = declaredField.get(clipboardManager);
            } catch (NoSuchFieldException unused) {
                Field declaredField2 = clipboardManager.getClass().getDeclaredField("sService");
                declaredField2.setAccessible(true);
                obj = declaredField2.get(null);
            }
            Object newProxyInstance = Proxy.newProxyInstance(DisableClipboardAccess.class.getClassLoader(), new Class[]{Class.forName("android.content.IClipboard")}, new InvocationHandler() { // from class: com.applisto.appcloner.classes.-$$Lambda$DisableClipboardAccess$jboIqPywk0o46sztNpKo4f9FKuQ
                @Override // java.lang.reflect.InvocationHandler
                public final Object invoke(Object obj2, Method method, Object[] objArr) {
                    return DisableClipboardAccess.lambda$installAndroidClipboardManagerHook$0(clipboardManager, obj, obj2, method, objArr);
                }
            });
            if (Build.VERSION.SDK_INT >= 26) {
                Field declaredField3 = clipboardManager.getClass().getDeclaredField("mService");
                declaredField3.setAccessible(true);
                declaredField3.set(clipboardManager, newProxyInstance);
            } else {
                Field declaredField4 = clipboardManager.getClass().getDeclaredField("sService");
                declaredField4.setAccessible(true);
                declaredField4.set(null, newProxyInstance);
            }
            Log.m15i(TAG, "installClipboardManagerHook; installed proxy");
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Object lambda$installAndroidClipboardManagerHook$0(ClipboardManager clipboardManager, Object obj, Object obj2, Method method, Object[] objArr) throws Throwable {
        if (sPrivateClipboard) {
            if ("hasClipboardText".equals(method.getName())) {
                Log.m15i(TAG, "invoke; hasClipboardText; ");
                return Boolean.valueOf(sClipData != null);
            }
            if ("hasPrimaryClip".equals(method.getName())) {
                Log.m15i(TAG, "invoke; hasPrimaryClip; ");
                return Boolean.valueOf(sClipData != null);
            }
            if ("getPrimaryClipDescription".equals(method.getName())) {
                Log.m15i(TAG, "invoke; getPrimaryClipDescription; ");
                ClipData clipData = sClipData;
                if (clipData != null) {
                    return cloneParcelable(clipData.getDescription());
                }
                return new ClipDescription("", new String[0]);
            }
            if ("getPrimaryClip".equals(method.getName())) {
                Log.m15i(TAG, "invoke; getPrimaryClip; ");
                ClipData clipData2 = sClipData;
                if (clipData2 != null) {
                    return cloneParcelable(clipData2);
                }
                return new ClipData("", new String[0], new ClipData.Item(""));
            }
            if ("addPrimaryClipChangedListener".equals(method.getName())) {
                Log.m15i(TAG, "invoke; addPrimaryClipChangedListener; ");
                sListeners.add((ClipboardManager.OnPrimaryClipChangedListener) objArr[0]);
                return null;
            }
            if ("removePrimaryClipChangedListener".equals(method.getName())) {
                Log.m15i(TAG, "invoke; removePrimaryClipChangedListener; ");
                sListeners.remove((ClipboardManager.OnPrimaryClipChangedListener) objArr[0]);
                return null;
            }
            if ("setPrimaryClip".equals(method.getName())) {
                Log.m15i(TAG, "invoke; setPrimaryClip; ");
                ClipData clipData3 = (ClipData) objArr[0];
                sClipData = (ClipData) cloneParcelable(clipData3);
                saveClipboardIfPersistent();
                Iterator<ClipboardManager.OnPrimaryClipChangedListener> it = sListeners.iterator();
                while (it.hasNext()) {
                    try {
                        it.next().onPrimaryClipChanged();
                    } catch (Exception e) {
                        Log.m21w(TAG, e);
                    }
                }
                startClipboardTimeout(clipboardManager, clipData3);
                return null;
            }
        }
        if (sDisableClipboardReadAccess) {
            if ("hasClipboardText".equals(method.getName())) {
                Log.m15i(TAG, "invoke; hasClipboardText; disabled clipboard read access");
                return false;
            }
            if ("hasPrimaryClip".equals(method.getName())) {
                Log.m15i(TAG, "invoke; hasPrimaryClip; disabled clipboard read access");
                return false;
            }
            if ("getPrimaryClipDescription".equals(method.getName())) {
                Log.m15i(TAG, "invoke; getPrimaryClipDescription; disabled clipboard read access");
                return new ClipDescription("", new String[0]);
            }
            if ("getPrimaryClip".equals(method.getName())) {
                Log.m15i(TAG, "invoke; getPrimaryClip; disabled clipboard read access");
                return null;
            }
            if ("addPrimaryClipChangedListener".equals(method.getName())) {
                Log.m15i(TAG, "invoke; addPrimaryClipChangedListener; disabled clipboard read access");
                return null;
            }
            if ("removePrimaryClipChangedListener".equals(method.getName())) {
                Log.m15i(TAG, "invoke; removePrimaryClipChangedListener; disabled clipboard read access");
                return null;
            }
        }
        if (sDisableClipboardWriteAccess) {
            if ("setPrimaryClip".equals(method.getName())) {
                Log.m15i(TAG, "invoke; setPrimaryClip; disabled clipboard write access");
                return null;
            }
        } else if (sClipboardTimeout && "setPrimaryClip".equals(method.getName())) {
            startClipboardTimeout(clipboardManager, (ClipData) objArr[0]);
        }
        return method.invoke(obj, objArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void installSamsungClipboardManagerHook(Object obj) {
        Log.m15i(TAG, "installSamsungClipboardManagerHook; semClipboardManager: " + obj);
        try {
            Method declaredMethod = obj.getClass().getDeclaredMethod("getService", new Class[0]);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(obj, new Object[0]);
            Log.m15i(TAG, "installSamsungClipboardManagerHook; originalService: " + invoke);
            Object newProxyInstance = Proxy.newProxyInstance(DisableClipboardAccess.class.getClassLoader(), new Class[]{Class.forName("android.sec.clipboard.IClipboardService")}, new InvocationHandler() { // from class: com.applisto.appcloner.classes.-$$Lambda$DisableClipboardAccess$Owl4Jdj5hx0_3abwcv8tL8eRy8Y
                @Override // java.lang.reflect.InvocationHandler
                public final Object invoke(Object obj2, Method method, Object[] objArr) {
                    return DisableClipboardAccess.lambda$installSamsungClipboardManagerHook$1(invoke, obj2, method, objArr);
                }
            });
            Field declaredField = obj.getClass().getDeclaredField("sService");
            declaredField.setAccessible(true);
            declaredField.set(null, newProxyInstance);
            Log.m15i(TAG, "installSamsungClipboardManagerHook; installed proxy");
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Object lambda$installSamsungClipboardManagerHook$1(Object obj, Object obj2, Method method, Object[] objArr) throws Throwable {
        if ("isEnabled".equals(method.getName())) {
            return false;
        }
        return method.invoke(obj, objArr);
    }

    private static void startClipboardTimeout(final ClipboardManager clipboardManager, ClipData clipData) {
        try {
            if (EMPTY_CLIP_DATA.equals(clipData)) {
                Log.m15i(TAG, "startClipboardTimeout; empty clip data");
                return;
            }
            sTimeoutHandler.removeCallbacksAndMessages(null);
            sTimeoutHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$DisableClipboardAccess$qtrBF8BgD68mRy5e7pcrXJ7DjMA
                @Override // java.lang.Runnable
                public final void run() {
                    DisableClipboardAccess.lambda$startClipboardTimeout$2(clipboardManager);
                }
            }, 100L);
            sTimeoutHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$DisableClipboardAccess$LcZv2Dm7xdFFqUJlZAKJLyma5zQ
                @Override // java.lang.Runnable
                public final void run() {
                    DisableClipboardAccess.clearClipboard(DisableClipboardAccess.sContext, DisableClipboardAccess.sClearingClipDataToString);
                }
            }, 30000L);
            Log.m15i(TAG, "startClipboardTimeout; started clipboard timeout");
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$startClipboardTimeout$2(ClipboardManager clipboardManager) {
        sClearingClipDataToString = "" + clipboardManager.getPrimaryClip();
        long currentTimeMillis = System.currentTimeMillis() + 30000;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(sContext, ClearClipboardReceiver.class));
        intent.setPackage(sContext.getPackageName());
        intent.putExtra("expected_clip_data_to_string", sClearingClipDataToString);
        PendingIntent broadcast = PendingIntent.getBroadcast(sContext, (int) currentTimeMillis, intent, 0);
        AlarmManager alarmManager = (AlarmManager) sContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setAndAllowWhileIdle(0, currentTimeMillis, broadcast);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(0, currentTimeMillis, broadcast);
        } else {
            alarmManager.set(0, currentTimeMillis, broadcast);
        }
    }

    /* loaded from: classes2.dex */
    public static class ClearClipboardReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.m15i(DisableClipboardAccess.TAG, "onReceive; intent: " + intent);
            DisableClipboardAccess.clearClipboard(context, intent.getStringExtra("expected_clip_data_to_string"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void clearClipboard(Context context, String str) {
        Log.m15i(TAG, "clearClipboard; ");
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService("clipboard");
            if (("" + clipboardManager.getPrimaryClip()).equals(str)) {
                Log.m15i(TAG, "clearClipboard; clearing clipboard");
                clipboardManager.setPrimaryClip(EMPTY_CLIP_DATA);
                Toast.makeText(context, sStringsProperties.getProperty("clipboard_timeout_cleared_message"), 0).show();
            } else {
                Log.m15i(TAG, "clearClipboard; not clearing clipboard; found other clip data");
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private static void loadClipboardIfPersistent() {
        if (sPersistentClipboard) {
            try {
                String string = sPreferences.getString("persistent_clip_data", null);
                if (TextUtils.isEmpty(string)) {
                    return;
                }
                byte[] decode = Base64.decode(new SimpleCrypt("gvdshfjry8wehu43").decrypt(string), 0);
                Parcel obtain = Parcel.obtain();
                obtain.unmarshall(decode, 0, decode.length);
                obtain.setDataPosition(0);
                sClipData = (ClipData) ClipData.CREATOR.createFromParcel(obtain);
                Log.m15i(TAG, "saveClipboardIfPersistent; clipboard loaded");
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
        }
    }

    private static void saveClipboardIfPersistent() {
        if (!sPersistentClipboard || sClipData == null) {
            return;
        }
        try {
            Parcel obtain = Parcel.obtain();
            sClipData.writeToParcel(obtain, 0);
            sPreferences.edit().putString("persistent_clip_data", new SimpleCrypt("gvdshfjry8wehu43").encrypt(Base64.encodeToString(obtain.marshall(), 2))).apply();
            Log.m15i(TAG, "saveClipboardIfPersistent; clipboard saved");
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private static <T extends Parcelable> T cloneParcelable(T t) {
        Parcel parcel = null;
        if (t == null) {
            return null;
        }
        try {
            parcel = Parcel.obtain();
            parcel.writeValue(t);
            parcel.setDataPosition(0);
            return (T) parcel.readValue(ClipData.class.getClassLoader());
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }
}
