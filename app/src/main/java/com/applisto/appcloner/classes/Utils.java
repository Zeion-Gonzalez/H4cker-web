package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.MultiProcessPreferences;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* loaded from: classes2.dex */
public class Utils {
    private static final String APP_CLONER_NOTIFICATION_CHANNEL_ID = "AppCloner";
    private static final String APP_CLONER_NOTIFICATION_CHANNEL_ID_HIGH_IMPORTANCE = "AppClonerHighImportance";
    private static final String APP_CLONER_NOTIFICATION_CHANNEL_NAME = "App Cloner";
    private static final String APP_CLONER_NOTIFICATION_CHANNEL_NAME_HIGH_IMPORTANCE = "App Cloner High Importance";
    private static final String TAG = Utils.class.getSimpleName();
    public static Application sApplication;
    public static Boolean sDevDevice;
    private static boolean sNotificationChannelCreated;
    private static boolean sNotificationChannelCreatedHighImportance;
    private static Icon sNotificationIcon;
    private static SharedPreferences sPreferences;
    public static Context sSecondaryClassLoaderContext;
    private static ClassLoader secondaryClassLoader;

    public static AlertDialog.Builder getDialogBuilder(Context context) {
        ContextThemeWrapper contextThemeWrapper;
        if (!(context instanceof Activity)) {
            Activity runningActivity = getRunningActivity();
            Log.m15i(TAG, "getDialogBuilder; runningActivity: " + runningActivity);
            context = runningActivity != null ? runningActivity : context.getApplicationContext();
        }
        Log.m15i(TAG, "getDialogBuilder; context: " + context);
        final boolean z = getActivityContext(context) instanceof Activity;
        if (Build.VERSION.SDK_INT >= 21) {
            contextThemeWrapper = new ContextThemeWrapper(context, 16974123);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(context, 16974120);
        }
        return new AlertDialog.Builder(contextThemeWrapper) { // from class: com.applisto.appcloner.classes.Utils.1
            @Override // android.app.AlertDialog.Builder
            public AlertDialog create() {
                AlertDialog create = super.create();
                if (!z) {
                    Log.m15i(Utils.TAG, "create; setting TYPE_SYSTEM_ALERT");
                    create.getWindow().setType(2003);
                } else {
                    Log.m15i(Utils.TAG, "create; not setting TYPE_SYSTEM_ALERT");
                }
                return create;
            }
        };
    }

    public static AlertDialog showDialog(Context context, CharSequence charSequence, CharSequence charSequence2) {
        try {
            AlertDialog.Builder dialogBuilder = getDialogBuilder(context);
            if (!TextUtils.isEmpty(charSequence)) {
                dialogBuilder.setTitle(charSequence);
            }
            return dialogBuilder.setMessage(charSequence2).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
        } catch (Throwable th) {
            Log.m21w(TAG, th);
            try {
                Toast.makeText(context, charSequence2, 1).show();
            } catch (Throwable th2) {
                Log.m21w(TAG, th2);
            }
            return null;
        }
    }

    public static Application getApplication() {
        Class<?> cls;
        Application application = sApplication;
        if (application != null) {
            return application;
        }
        try {
            cls = Class.forName("android.app.ActivityThread");
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        if (Build.VERSION.SDK_INT >= 9) {
            sApplication = (Application) cls.getMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
            return sApplication;
        }
        for (Method method : cls.getMethods()) {
            if ("currentActivityThread".equals(method.getName())) {
                Object invoke = method.invoke(null, new Object[0]);
                for (Method method2 : cls.getMethods()) {
                    if ("getApplication".equals(method2.getName())) {
                        sApplication = (Application) method2.invoke(invoke, new Object[0]);
                        return sApplication;
                    }
                }
            }
        }
        return null;
    }

    public static Activity getRunningActivity() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Object invoke = cls.getMethod("currentActivityThread", new Class[0]).invoke(null, new Object[0]);
            Field declaredField = cls.getDeclaredField("mActivities");
            declaredField.setAccessible(true);
            for (Object obj : ((Map) declaredField.get(invoke)).values()) {
                Class<?> cls2 = obj.getClass();
                Field declaredField2 = cls2.getDeclaredField("paused");
                declaredField2.setAccessible(true);
                if (!declaredField2.getBoolean(obj)) {
                    Field declaredField3 = cls2.getDeclaredField("activity");
                    declaredField3.setAccessible(true);
                    return (Activity) declaredField3.get(obj);
                }
            }
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
        return null;
    }

    public static String getAppName(Context context) {
        String str;
        try {
            str = context.getApplicationInfo().packageName;
        } catch (Exception e) {
            Log.m21w(TAG, e);
            str = "app";
        }
        try {
            CharSequence loadLabel = context.getApplicationInfo().loadLabel(context.getPackageManager());
            return !TextUtils.isEmpty(loadLabel) ? loadLabel.toString() : str;
        } catch (Exception e2) {
            Log.m21w(TAG, e2);
            return str;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString("com.applisto.appcloner.versionName");
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return null;
        }
    }

    public static int dp2px(Context context, float f) {
        return dp2px(context.getResources().getDisplayMetrics(), f);
    }

    public static int dp2px(DisplayMetrics displayMetrics, float f) {
        return (int) (f * (displayMetrics.densityDpi / 160.0f));
    }

    public static void keepDialogOpenOnOrientationChange(Dialog dialog) {
        try {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = -2;
            layoutParams.height = -2;
            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    public static int getTargetSdkVersion(Context context) {
        return getTargetSdkVersion(context, context.getPackageName());
    }

    public static int getTargetSdkVersion(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).targetSdkVersion;
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return -1;
        }
    }

    public static Context getActivityContext(Context context) {
        return ((context instanceof Activity) || !(context instanceof ContextThemeWrapper)) ? context : getActivityContext(((ContextThemeWrapper) context).getBaseContext());
    }

    public static String toString(InputStream inputStream, String str) throws IOException {
        return new String(readFully(inputStream, false), str);
    }

    public static byte[] readFully(InputStream inputStream, boolean z) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            if (z) {
                try {
                    inputStream.close();
                } catch (Exception unused) {
                }
            }
            return byteArray;
        } catch (Throwable th) {
            if (z) {
                try {
                    inputStream.close();
                } catch (Exception unused2) {
                }
            }
            throw th;
        }
    }

    public static boolean deleteDirectory(File file) {
        File[] listFiles;
        File[] listFiles2;
        int i;
        boolean z = true;
        if (file.getPath().contains("/cache/oat/")) {
            return true;
        }
        if (file.exists() && file.isDirectory() && (listFiles2 = file.listFiles()) != null) {
            int length = listFiles2.length;
            boolean z2 = true;
            while (i < length) {
                File file2 = listFiles2[i];
                if (file2.isDirectory()) {
                    i = deleteDirectory(file2) ? i + 1 : 0;
                    z2 = false;
                } else {
                    if (deleteFile(file2)) {
                    }
                    z2 = false;
                }
            }
            z = z2;
        }
        if (file.delete() || (listFiles = file.listFiles()) == null || listFiles.length <= 0) {
            return z;
        }
        for (File file3 : listFiles) {
            file3.deleteOnExit();
        }
        return false;
    }

    public static boolean deleteFile(File file) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
            try {
                byte[] bArr = new byte[102400];
                for (int length = (int) file.length(); length > 0; length -= bArr.length) {
                    randomAccessFile.write(bArr, 0, Math.min(bArr.length, length));
                }
                randomAccessFile.close();
            } catch (Throwable th) {
                randomAccessFile.close();
                throw th;
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        int length2 = file.getName().length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length2; i++) {
            sb.append('A');
        }
        File file2 = new File(file.getParent(), sb.toString());
        if (file.renameTo(file2)) {
            return file2.delete();
        }
        return file.delete();
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return;
            } else {
                outputStream.write(bArr, 0, read);
            }
        }
    }

    public static void forceMkdir(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                return;
            }
            throw new IOException("File " + file + " exists and is not a directory. Unable to create directory.");
        }
        if (file.mkdirs() || file.isDirectory()) {
            return;
        }
        throw new IOException("Unable to create directory " + file);
    }

    public static List<ViewParent> getViewRoots() {
        Object obj;
        List<ViewParent> list;
        ArrayList arrayList = new ArrayList();
        try {
            if (Build.VERSION.SDK_INT >= 17) {
                obj = Class.forName("android.view.WindowManagerGlobal").getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            } else {
                Field declaredField = Class.forName("android.view.WindowManagerImpl").getDeclaredField("sWindowManager");
                declaredField.setAccessible(true);
                obj = declaredField.get(null);
            }
            Field declaredField2 = obj.getClass().getDeclaredField("mRoots");
            declaredField2.setAccessible(true);
            Field declaredField3 = Class.forName("android.view.ViewRootImpl").getDeclaredField("mStopped");
            declaredField3.setAccessible(true);
            if (Build.VERSION.SDK_INT >= 17) {
                Object obj2 = declaredField2.get(obj);
                if (obj2 != null) {
                    if (obj2.getClass().isArray()) {
                        list = Arrays.asList((ViewParent[]) obj2);
                    } else {
                        list = (List) obj2;
                    }
                    for (ViewParent viewParent : list) {
                        if (!((Boolean) declaredField3.get(viewParent)).booleanValue()) {
                            arrayList.add(viewParent);
                        }
                    }
                }
            } else {
                for (ViewParent viewParent2 : (ViewParent[]) declaredField2.get(obj)) {
                    if (!((Boolean) declaredField3.get(viewParent2)).booleanValue()) {
                        arrayList.add(viewParent2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x00aa A[Catch: all -> 0x00a6, TRY_ENTER, TRY_LEAVE, TryCatch #1 {all -> 0x00a6, blocks: (B:13:0x0025, B:15:0x002b, B:17:0x0059, B:18:0x0088, B:25:0x00aa, B:27:0x00d3, B:32:0x00fd, B:33:0x0100, B:26:0x00b4), top: B:47:0x0025, outer: #0, inners: #2 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static synchronized java.lang.ClassLoader getSecondaryClassLoader() throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 273
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.applisto.appcloner.classes.Utils.getSecondaryClassLoader():java.lang.ClassLoader");
    }

    public static String getBuildSerial() {
        String str = Build.SERIAL;
        if (!"unknown".equals(str)) {
            return str;
        }
        try {
            return Settings.Secure.getString(getApplication().getContentResolver(), "android_id");
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return str;
        }
    }

    public static synchronized boolean isDevDevice() {
        boolean booleanValue;
        boolean z;
        synchronized (Utils.class) {
            if (sDevDevice == null) {
                String buildSerial = getBuildSerial();
                if (!"6129001759".equals(buildSerial) && !"ce011711c4259a1205".equals(buildSerial) && !"ZX1G522SR8".equals(buildSerial) && !"9SAYF6EAWKQO6TTC".equals(buildSerial) && !"022AQQ7N36083999".equals(buildSerial) && !"015d2578341ff40f".equals(buildSerial) && !"049ed51a251d4fa1".equals(buildSerial) && !"112141000751".equals(buildSerial) && !"504KPWQ0034257".equals(buildSerial) && !"G090ME067423036J".equals(buildSerial) && !"0324517083444".equals(buildSerial) && !"OZH6OVS8AISCDQSK".equals(buildSerial) && !"CB512B8AKQ".equals(buildSerial) && !"HT64EBN02546".equals(buildSerial) && !"e40cd6bf0704".equals(buildSerial) && !"WCR7N18328001594".equals(buildSerial) && !"f083b076".equals(buildSerial) && !"ce12160c3c1ce51904".equals(buildSerial) && !"HT79S1A03867".equals(buildSerial) && !"71888131273a816b".equals(buildSerial) && !"02157df2b40d042d".equals(buildSerial) && !"EBF119FE021D35AC2D31".equals(buildSerial) && !"2881930614047ece".equals(buildSerial) && !"R58M22SH41J".equals(buildSerial) && !"85689811geb0".equals(buildSerial) && !isEmulator()) {
                    z = false;
                    sDevDevice = Boolean.valueOf(z);
                }
                z = true;
                sDevDevice = Boolean.valueOf(z);
            }
            booleanValue = sDevDevice.booleanValue();
        }
        return booleanValue;
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk".equals(Build.PRODUCT);
    }

    public static void setSmallNotificationIcon(Notification.Builder builder) {
        setSmallNotificationIcon(builder, false);
    }

    public static void setSmallNotificationIcon(Notification.Builder builder, boolean z) {
        NotificationManager notificationManager;
        NotificationManager notificationManager2;
        if (Build.VERSION.SDK_INT >= 23) {
            Icon notificationIcon = getNotificationIcon();
            if (notificationIcon != null) {
                builder.setSmallIcon(notificationIcon);
            } else {
                builder.setSmallIcon(17301569);
            }
            builder.setColor(-16537100);
        } else {
            builder.setSmallIcon(17301569);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                if (z) {
                    if (!sNotificationChannelCreatedHighImportance) {
                        NotificationChannel notificationChannel = new NotificationChannel(APP_CLONER_NOTIFICATION_CHANNEL_ID_HIGH_IMPORTANCE, APP_CLONER_NOTIFICATION_CHANNEL_NAME_HIGH_IMPORTANCE, 4);
                        Application application = getApplication();
                        if (application != null && (notificationManager2 = (NotificationManager) application.getSystemService("notification")) != null) {
                            notificationManager2.createNotificationChannel(notificationChannel);
                            sNotificationChannelCreatedHighImportance = true;
                            Log.m15i(TAG, "setSmallNotificationIcon; notification channel created: AppClonerHighImportance");
                        }
                    }
                    builder.setChannelId(APP_CLONER_NOTIFICATION_CHANNEL_ID_HIGH_IMPORTANCE);
                    builder.setPriority(1);
                    return;
                }
                if (!sNotificationChannelCreated) {
                    NotificationChannel notificationChannel2 = new NotificationChannel(APP_CLONER_NOTIFICATION_CHANNEL_ID, APP_CLONER_NOTIFICATION_CHANNEL_NAME, 2);
                    Application application2 = getApplication();
                    if (application2 != null && (notificationManager = (NotificationManager) application2.getSystemService("notification")) != null) {
                        notificationManager.createNotificationChannel(notificationChannel2);
                        sNotificationChannelCreated = true;
                        Log.m15i(TAG, "setSmallNotificationIcon; notification channel created: AppCloner");
                    }
                }
                builder.setChannelId(APP_CLONER_NOTIFICATION_CHANNEL_ID);
                return;
            } catch (Throwable th) {
                Log.m21w(TAG, th);
                return;
            }
        }
        if (!z || Build.VERSION.SDK_INT < 16) {
            return;
        }
        builder.setPriority(1);
    }

    private static Icon getNotificationIcon() {
        if (sNotificationIcon == null) {
            try {
                byte[] decode = Base64.decode("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\n", 0);
                sNotificationIcon = Icon.createWithBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
        }
        return sNotificationIcon;
    }

    public static Intent getLaunchIntent(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(str);
        if (launchIntentForPackage != null) {
            return launchIntentForPackage;
        }
        Intent fallbackLaunchIntent = getFallbackLaunchIntent(packageManager, str);
        return (fallbackLaunchIntent == null && isAndroidTv(context)) ? packageManager.getLeanbackLaunchIntentForPackage(str) : fallbackLaunchIntent;
    }

    private static Intent getFallbackLaunchIntent(PackageManager packageManager, String str) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setPackage(str);
        if (intent.resolveActivity(packageManager) == null) {
            return null;
        }
        return intent;
    }

    public static boolean isAndroidTv(Context context) {
        try {
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService("uimode");
            if (uiModeManager != null) {
                return uiModeManager.getCurrentModeType() == 4;
            }
            return false;
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return false;
        }
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static String getPackageSeededRandomString(Context context, String str, int i) {
        if (i == 0) {
            return String.format("%08x", Long.valueOf(longHash(context.getPackageName() + str)));
        }
        return String.format("%08x", Long.valueOf(longHash(context.getPackageName() + i + str)));
    }

    private static long longHash(String str) {
        long j = 1125899906842597L;
        for (int i = 0; i < str.length(); i++) {
            j = (j * 31) + str.charAt(i);
        }
        return j;
    }

    public static void closeAndroidPieApiCompatibilityDialog() {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                Class<?> cls = Class.forName("android.app.ActivityThread");
                Method declaredMethod = cls.getDeclaredMethod("currentActivityThread", new Class[0]);
                declaredMethod.setAccessible(true);
                Object invoke = declaredMethod.invoke(null, new Object[0]);
                Field declaredField = cls.getDeclaredField("mHiddenApiWarningShown");
                declaredField.setAccessible(true);
                declaredField.setBoolean(invoke, true);
            }
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    public static void allowHiddenApiAccess() {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                Method declaredMethod = Class.class.getDeclaredMethod("forName", String.class);
                Method declaredMethod2 = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
                Class cls = (Class) declaredMethod.invoke(null, "dalvik.system.VMRuntime");
                ((Method) declaredMethod2.invoke(cls, "setHiddenApiExemptions", new Class[]{String[].class})).invoke(((Method) declaredMethod2.invoke(cls, "getRuntime", null)).invoke(null, new Object[0]), new String[]{"L"});
            }
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    public static boolean checkCaller(Context context) {
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(Binder.getCallingUid());
        return packagesForUid != null && packagesForUid.length == 1 && "com.applisto.appcloner".equals(packagesForUid[0]);
    }

    public static Point getRealScreenSize(Display display) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                i = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(display, new Object[0])).intValue();
                i2 = ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(display, new Object[0])).intValue();
            } catch (Exception unused) {
            }
        }
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point point = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, point);
                i = point.x;
                i2 = point.y;
            } catch (Exception unused2) {
            }
        }
        return new Point(i, i2);
    }

    public static int getApplicationVersionCode(Context context) {
        return getApplicationVersionCode(context, context.getPackageName());
    }

    public static int getApplicationVersionCode(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0).versionCode;
        } catch (Exception unused) {
            return -1;
        }
    }

    public static Enumeration<InetAddress> getWifiInetAddresses() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nextElement = networkInterfaces.nextElement();
                if ("wlan0".equals(nextElement.getName())) {
                    return nextElement.getInetAddresses();
                }
            }
            Enumeration<NetworkInterface> networkInterfaces2 = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces2.hasMoreElements()) {
                NetworkInterface nextElement2 = networkInterfaces2.nextElement();
                if ("mlan0".equals(nextElement2.getName())) {
                    return nextElement2.getInetAddresses();
                }
            }
            return null;
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return null;
        }
    }

    public static <T extends InetAddress> T getWifiInetAddress(Class<T> cls) {
        Enumeration<InetAddress> wifiInetAddresses = getWifiInetAddresses();
        if (wifiInetAddresses == null) {
            return null;
        }
        while (wifiInetAddresses.hasMoreElements()) {
            T t = (T) wifiInetAddresses.nextElement();
            if (t.getClass() == cls) {
                return t;
            }
        }
        return null;
    }

    public static void showNotificationMessage(Context context, CharSequence charSequence, boolean z) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager != null) {
            Notification.Builder contentText = new Notification.Builder(context).setContentTitle(getAppName(context)).setContentText(charSequence);
            if (Build.VERSION.SDK_INT >= 21) {
                contentText.setVisibility(-1);
            }
            setSmallNotificationIcon(contentText, z);
            if (Build.VERSION.SDK_INT >= 16) {
                contentText.setStyle(new Notification.BigTextStyle().bigText(charSequence));
            }
            notificationManager.notify(charSequence.hashCode(), contentText.getNotification());
            return;
        }
        Toast.makeText(context, charSequence, 1).show();
    }

    public static Properties getStringsProperties() {
        Properties properties = new Properties();
        try {
            InputStream resourceAsStream = Utils.class.getResourceAsStream("strings.properties");
            try {
                properties.load(resourceAsStream);
                try {
                    resourceAsStream.close();
                } catch (Exception unused) {
                }
            } finally {
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        return properties;
    }

    public static synchronized SharedPreferences getAppClonerClassesPreferences(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (Utils.class) {
            if (sPreferences == null) {
                sPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, 0);
            }
            sharedPreferences = sPreferences;
        }
        return sharedPreferences;
    }

    public static SharedPreferences getPublicSharedPreferences(Context context) {
        return new MultiProcessPreferences.MultiProcessSharedPreferences(context, DefaultProvider.sAppClonerPackageName + ".provider.PublicPreferences");
    }

    public static List<String> getAbis() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Arrays.asList(Build.SUPPORTED_ABIS);
        }
        return Collections.singletonList(Build.CPU_ABI);
    }

    public static String getPrimaryAbi() {
        List<String> abis = getAbis();
        if (abis.size() > 0) {
            return abis.get(0);
        }
        return null;
    }

    public static boolean isX86() {
        String primaryAbi = getPrimaryAbi();
        return "x86".equals(primaryAbi) || "x86_64".equals(primaryAbi);
    }
}
