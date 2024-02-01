package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import com.applisto.appcloner.classes.NotificationOptions;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
public class NotificationOptions extends OnAppExitListener {
    private static final String ACTION_SNOOZE_NOTIFICATION = "com.applisto.appcloner.action.SNOOZE_NOTIFICATION";
    private static final String EXTRA_SNOOZE_ACTION = "snooze_action";
    private static final String TAG = NotificationOptions.class.getSimpleName();
    private static boolean mAllowNotificationsWhenRunning;
    private static boolean mBlockAllNotifications;
    private static Integer mNotificationColor;
    private boolean mHeadsUpNotifications;
    private Icon mIcon;
    private boolean mLocalOnlyNotifications;
    private boolean mNotificationDots;
    private final Set<String> mNotificationFilterSet;
    private String mNotificationLightsColor;
    private String mNotificationLightsPattern;
    private String mNotificationPriority;
    private boolean mNotificationQuietTime;
    private int mNotificationQuietTimeEndHour;
    private int mNotificationQuietTimeEndMinute;
    private int mNotificationQuietTimeStartHour;
    private int mNotificationQuietTimeStartMinute;
    private int mNotificationSnoozeTimeout;
    private String mNotificationSound;
    private int mNotificationTimeout;
    private boolean mNotificationTintStatusBarIcon;
    private String mNotificationVibration;
    private String mNotificationVisibility;
    private String mOngoingNotifications;
    private boolean mRemoveNotificationActions;
    private boolean mRemoveNotificationIcon;
    private boolean mRemoveNotificationPeople;
    private boolean mReplaceNotificationIcon;
    private boolean mRunning;
    private boolean mShowNotificationTime;
    private boolean mSimpleNotifications;
    private String mSingleNotificationCategory;
    private boolean mSingleNotificationGroup;
    private List<Map<String, Object>> mNotificationCategories = new ArrayList();
    private List<Map<String, Object>> mNotificationTextReplacements = new ArrayList();
    private Handler mTimeoutHandler = new Handler();
    private Map<Integer, Runnable> mTimeoutRunnables = new HashMap();
    private Map<Integer, Notification> mSnoozeNotifications = new HashMap();

    public NotificationOptions(CloneSettings cloneSettings) {
        this.mNotificationLightsPattern = "NO_CHANGE";
        this.mNotificationLightsColor = "NO_CHANGE";
        mBlockAllNotifications = cloneSettings.getBoolean("blockAllNotifications", false).booleanValue();
        mAllowNotificationsWhenRunning = cloneSettings.getBoolean("allowNotificationsWhenRunning", false).booleanValue();
        String string = cloneSettings.getString("notificationFilter", null);
        this.mNotificationFilterSet = new HashSet();
        if (!TextUtils.isEmpty(string)) {
            for (String str : string.trim().split(",")) {
                String trim = str.trim();
                if (!TextUtils.isEmpty(trim)) {
                    this.mNotificationFilterSet.add(trim.toLowerCase(Locale.ENGLISH));
                }
            }
        }
        this.mNotificationQuietTime = cloneSettings.getBoolean("notificationQuietTime", false).booleanValue();
        try {
            String[] split = cloneSettings.getString("notificationQuietTimeStart", "21:00").split(":");
            this.mNotificationQuietTimeStartHour = Integer.parseInt(split[0]);
            this.mNotificationQuietTimeStartMinute = Integer.parseInt(split[1]);
            String[] split2 = cloneSettings.getString("notificationQuietTimeEnd", "07:00").split(":");
            this.mNotificationQuietTimeEndHour = Integer.parseInt(split2[0]);
            this.mNotificationQuietTimeEndMinute = Integer.parseInt(split2[1]);
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        if (cloneSettings.getBoolean("notificationColorUseStatusBarColor", false).booleanValue()) {
            mNotificationColor = cloneSettings.getInteger("statusBarColor", null);
        } else {
            mNotificationColor = cloneSettings.getInteger("notificationColor", null);
        }
        this.mNotificationTintStatusBarIcon = cloneSettings.getBoolean("notificationTintStatusBarIcon", false).booleanValue();
        this.mNotificationSound = cloneSettings.getString("notificationSound", "NO_CHANGE");
        this.mNotificationVibration = cloneSettings.getString("notificationVibration", "NO_CHANGE");
        this.mNotificationTimeout = cloneSettings.getInteger("notificationTimeout", 0).intValue();
        this.mNotificationSnoozeTimeout = cloneSettings.getInteger("notificationSnoozeTimeout", 0).intValue();
        this.mHeadsUpNotifications = cloneSettings.getBoolean("headsUpNotifications", false).booleanValue();
        this.mLocalOnlyNotifications = cloneSettings.getBoolean("localOnlyNotifications", false).booleanValue();
        this.mOngoingNotifications = cloneSettings.getString("ongoingNotifications", "NO_CHANGE");
        this.mShowNotificationTime = cloneSettings.getBoolean("showNotificationTime", false).booleanValue();
        CloneSettings forObject = cloneSettings.forObject("defaultNotificationLights");
        if (forObject != null) {
            this.mNotificationLightsPattern = forObject.getString("notificationLightsPattern", "NO_CHANGE");
            this.mNotificationLightsColor = forObject.getString("notificationLightsColor", "NO_CHANGE");
        }
        this.mNotificationVisibility = cloneSettings.getString("notificationVisibility", "NO_CHANGE");
        this.mNotificationPriority = cloneSettings.getString("notificationPriority", "NO_CHANGE");
        this.mReplaceNotificationIcon = cloneSettings.getBoolean("replaceNotificationIcon", false).booleanValue();
        this.mRemoveNotificationIcon = cloneSettings.getBoolean("removeNotificationIcon", false).booleanValue();
        this.mRemoveNotificationActions = cloneSettings.getBoolean("removeNotificationActions", false).booleanValue();
        this.mRemoveNotificationPeople = cloneSettings.getBoolean("removeNotificationPeople", false).booleanValue();
        this.mSimpleNotifications = cloneSettings.getBoolean("simpleNotifications", false).booleanValue();
        this.mSingleNotificationGroup = cloneSettings.getBoolean("singleNotificationGroup", false).booleanValue();
        List<CloneSettings> forObjectArray = cloneSettings.forObjectArray("notificationCategories");
        if (forObjectArray != null) {
            for (CloneSettings cloneSettings2 : forObjectArray) {
                HashMap hashMap = new HashMap();
                hashMap.put("name", cloneSettings2.getString("name", ""));
                hashMap.put("keywords", cloneSettings2.getString("keywords", ""));
                hashMap.put("ignoreCase", cloneSettings2.getBoolean("ignoreCase", true));
                this.mNotificationCategories.add(hashMap);
            }
        }
        this.mSingleNotificationCategory = cloneSettings.getString("singleNotificationCategory", null);
        List<CloneSettings> forObjectArray2 = cloneSettings.forObjectArray("notificationTextReplacements");
        if (forObjectArray2 != null) {
            for (CloneSettings cloneSettings3 : forObjectArray2) {
                HashMap hashMap2 = new HashMap();
                hashMap2.put("replace", cloneSettings3.getString("replace", ""));
                hashMap2.put("with", cloneSettings3.getString("with", ""));
                hashMap2.put("ignoreCase", cloneSettings3.getBoolean("ignoreCase", true));
                hashMap2.put("replaceInTitle", cloneSettings3.getBoolean("replaceInTitle", true));
                hashMap2.put("replaceInContent", cloneSettings3.getBoolean("replaceInContent", true));
                hashMap2.put("replaceInMessages", cloneSettings3.getBoolean("replaceInMessages", true));
                hashMap2.put("replaceInActions", cloneSettings3.getBoolean("replaceInActions", true));
                this.mNotificationTextReplacements.add(hashMap2);
            }
        }
        this.mNotificationDots = cloneSettings.getBoolean("notificationDots", false).booleanValue();
        Log.m15i(TAG, "NotificationOptions; mBlockAllNotifications: " + mBlockAllNotifications);
        Log.m15i(TAG, "NotificationOptions; mAllowNotificationsWhenRunning: " + mAllowNotificationsWhenRunning);
        Log.m15i(TAG, "NotificationOptions; mNotificationFilterSet: " + this.mNotificationFilterSet);
        Log.m15i(TAG, "NotificationOptions; mNotificationQuietTime: " + this.mNotificationQuietTime);
        Log.m15i(TAG, "NotificationOptions; mNotificationQuietTimeStartHour: " + this.mNotificationQuietTimeStartHour);
        Log.m15i(TAG, "NotificationOptions; mNotificationQuietTimeStartMinute: " + this.mNotificationQuietTimeStartMinute);
        Log.m15i(TAG, "NotificationOptions; mNotificationQuietTimeEndHour: " + this.mNotificationQuietTimeEndHour);
        Log.m15i(TAG, "NotificationOptions; mNotificationQuietTimeEndMinute: " + this.mNotificationQuietTimeEndMinute);
        Log.m15i(TAG, "NotificationOptions; mNotificationColor: " + mNotificationColor);
        Log.m15i(TAG, "NotificationOptions; mNotificationTintStatusBarIcon: " + this.mNotificationTintStatusBarIcon);
        Log.m15i(TAG, "NotificationOptions; mNotificationSound: " + this.mNotificationSound);
        Log.m15i(TAG, "NotificationOptions; mNotificationVibration: " + this.mNotificationVibration);
        Log.m15i(TAG, "NotificationOptions; mNotificationTimeout: " + this.mNotificationTimeout);
        Log.m15i(TAG, "NotificationOptions; mNotificationSnoozeTimeout: " + this.mNotificationSnoozeTimeout);
        Log.m15i(TAG, "NotificationOptions; mHeadsUpNotifications: " + this.mHeadsUpNotifications);
        Log.m15i(TAG, "NotificationOptions; mLocalOnlyNotifications: " + this.mLocalOnlyNotifications);
        Log.m15i(TAG, "NotificationOptions; mOngoingNotifications: " + this.mOngoingNotifications);
        Log.m15i(TAG, "NotificationOptions; mShowNotificationTime: " + this.mShowNotificationTime);
        Log.m15i(TAG, "NotificationOptions; mNotificationLightsPattern: " + this.mNotificationLightsPattern);
        Log.m15i(TAG, "NotificationOptions; mNotificationLightsColor: " + this.mNotificationLightsColor);
        Log.m15i(TAG, "NotificationOptions; mNotificationVisibility: " + this.mNotificationVisibility);
        Log.m15i(TAG, "NotificationOptions; mNotificationPriority: " + this.mNotificationPriority);
        Log.m15i(TAG, "NotificationOptions; mReplaceNotificationIcon: " + this.mReplaceNotificationIcon);
        Log.m15i(TAG, "NotificationOptions; mRemoveNotificationIcon: " + this.mRemoveNotificationIcon);
        Log.m15i(TAG, "NotificationOptions; mRemoveNotificationActions: " + this.mRemoveNotificationActions);
        Log.m15i(TAG, "NotificationOptions; mRemoveNotificationPeople: " + this.mRemoveNotificationPeople);
        Log.m15i(TAG, "NotificationOptions; mSimpleNotifications: " + this.mSimpleNotifications);
        Log.m15i(TAG, "NotificationOptions; mSingleNotificationGroup: " + this.mSingleNotificationGroup);
        Log.m15i(TAG, "NotificationOptions; mNotificationCategories: " + this.mNotificationCategories);
        Log.m15i(TAG, "NotificationOptions; mSingleNotificationCategory: " + this.mSingleNotificationCategory);
        Log.m15i(TAG, "NotificationOptions; mNotificationTextReplacements: " + this.mNotificationTextReplacements);
    }

    public void install(final Context context) {
        Log.m15i(TAG, "install; ");
        if (mNotificationColor == null && !mBlockAllNotifications && this.mNotificationFilterSet.isEmpty() && !this.mNotificationQuietTime && "NO_CHANGE".equals(this.mNotificationSound) && "NO_CHANGE".equals(this.mNotificationVibration) && this.mNotificationTimeout == 0 && this.mNotificationSnoozeTimeout == 0 && !this.mHeadsUpNotifications && !this.mLocalOnlyNotifications && "NO_CHANGE".equals(this.mOngoingNotifications) && !this.mShowNotificationTime && "NO_CHANGE".equals(this.mNotificationLightsPattern) && "NO_CHANGE".equals(this.mNotificationLightsColor) && "NO_CHANGE".equals(this.mNotificationVisibility) && "NO_CHANGE".equals(this.mNotificationPriority) && !this.mReplaceNotificationIcon && !this.mRemoveNotificationIcon && !this.mRemoveNotificationActions && !this.mRemoveNotificationPeople && !this.mSimpleNotifications && !this.mSingleNotificationGroup && this.mNotificationCategories.isEmpty() && TextUtils.isEmpty(this.mSingleNotificationCategory) && this.mNotificationTextReplacements.isEmpty()) {
            return;
        }
        onCreate();
        try {
            Field declaredField = NotificationManager.class.getDeclaredField("sService");
            declaredField.setAccessible(true);
            if (declaredField.get(null) != null) {
                Log.m15i(TAG, "onCreate; sService already initialized!!!");
            } else {
                Log.m15i(TAG, "onCreate; sService == null");
            }
            NotificationManager.class.getMethod("getService", new Class[0]).invoke(null, new Object[0]);
            final Object obj = declaredField.get(null);
            declaredField.set(null, Proxy.newProxyInstance(NotificationOptions.class.getClassLoader(), new Class[]{Class.forName("android.app.INotificationManager")}, new InvocationHandler() { // from class: com.applisto.appcloner.classes.-$$Lambda$NotificationOptions$PMv6Pe-FXiJ1ET7L7mZ6zyF_y2c
                @Override // java.lang.reflect.InvocationHandler
                public final Object invoke(Object obj2, Method method, Object[] objArr) {
                    return NotificationOptions.this.lambda$install$1$NotificationOptions(context, obj, obj2, method, objArr);
                }
            }));
            if (this.mNotificationSnoozeTimeout > 0) {
                context.registerReceiver(new C04371(), new IntentFilter(ACTION_SNOOZE_NOTIFICATION));
            }
            if (!this.mReplaceNotificationIcon || Build.VERSION.SDK_INT < 23) {
                return;
            }
            try {
                byte[] readFully = Utils.readFully(context.getAssets().open(".notificationIconFile"), true);
                this.mIcon = Icon.createWithData(readFully, 0, readFully.length);
                Log.m15i(TAG, "install; mIcon: " + this.mIcon);
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
        } catch (Exception e2) {
            Log.m21w(TAG, e2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:267:0x0866  */
    /* JADX WARN: Removed duplicated region for block: B:281:0x08ab  */
    /* JADX WARN: Removed duplicated region for block: B:319:0x0941 A[Catch: Exception -> 0x029e, TryCatch #11 {Exception -> 0x029e, blocks: (B:67:0x01cd, B:73:0x0214, B:83:0x023b, B:89:0x02a9, B:92:0x02bd, B:94:0x02c5, B:101:0x0335, B:103:0x033d, B:104:0x034f, B:106:0x0359, B:107:0x036b, B:109:0x0377, B:110:0x0392, B:112:0x039c, B:113:0x03b7, B:115:0x03c1, B:116:0x03dc, B:118:0x03e6, B:95:0x02d6, B:97:0x02e0, B:98:0x02f0, B:100:0x02fa, B:119:0x0400, B:121:0x0406, B:123:0x0414, B:125:0x041e, B:128:0x045d, B:130:0x0461, B:132:0x0467, B:133:0x0486, B:135:0x048a, B:136:0x04a6, B:138:0x04b0, B:142:0x0500, B:149:0x052e, B:151:0x0535, B:153:0x053f, B:154:0x0559, B:156:0x0563, B:157:0x057e, B:159:0x0588, B:172:0x062e, B:174:0x0638, B:176:0x063c, B:177:0x063e, B:179:0x0657, B:180:0x0660, B:182:0x066a, B:183:0x0671, B:185:0x067b, B:186:0x0682, B:188:0x068c, B:189:0x0691, B:191:0x069b, B:192:0x06a0, B:194:0x06a8, B:196:0x06ae, B:198:0x06b6, B:199:0x06ba, B:201:0x06d3, B:202:0x06d6, B:204:0x06e0, B:205:0x06e5, B:207:0x06ef, B:208:0x06f4, B:210:0x06fe, B:211:0x0704, B:213:0x070e, B:214:0x0714, B:216:0x071e, B:217:0x0724, B:219:0x072e, B:220:0x0733, B:222:0x0737, B:224:0x073d, B:226:0x0768, B:227:0x0771, B:228:0x0775, B:230:0x077b, B:232:0x0787, B:234:0x0793, B:235:0x0797, B:236:0x07ac, B:238:0x07b3, B:239:0x07b8, B:240:0x07c3, B:242:0x07c7, B:244:0x07ef, B:245:0x07fd, B:247:0x0801, B:249:0x0805, B:265:0x0862, B:278:0x08a2, B:279:0x08a7, B:289:0x08c9, B:290:0x08ce, B:317:0x093d, B:319:0x0941, B:321:0x0945, B:322:0x095a, B:324:0x095e, B:338:0x09d6, B:340:0x09dd, B:342:0x09e1, B:316:0x0938, B:263:0x085b, B:160:0x05a3, B:162:0x05ad, B:163:0x05c7, B:165:0x05cf, B:166:0x05e9, B:168:0x05f3, B:169:0x060c, B:171:0x0616, B:148:0x0529, B:139:0x04d3, B:141:0x04dd, B:77:0x022c, B:292:0x08d2, B:294:0x08e7, B:296:0x08ef, B:298:0x08f3, B:300:0x08f9, B:301:0x0905, B:302:0x0908, B:304:0x090e, B:306:0x0916, B:308:0x0919, B:310:0x091f, B:311:0x092b, B:312:0x092e, B:268:0x0867, B:270:0x0876, B:271:0x087b, B:273:0x0883, B:274:0x0888, B:282:0x08ac, B:284:0x08ba, B:285:0x08bf, B:144:0x0504), top: B:410:0x01cd, inners: #3, #9, #15, #16 }] */
    /* JADX WARN: Removed duplicated region for block: B:324:0x095e A[Catch: Exception -> 0x029e, TRY_LEAVE, TryCatch #11 {Exception -> 0x029e, blocks: (B:67:0x01cd, B:73:0x0214, B:83:0x023b, B:89:0x02a9, B:92:0x02bd, B:94:0x02c5, B:101:0x0335, B:103:0x033d, B:104:0x034f, B:106:0x0359, B:107:0x036b, B:109:0x0377, B:110:0x0392, B:112:0x039c, B:113:0x03b7, B:115:0x03c1, B:116:0x03dc, B:118:0x03e6, B:95:0x02d6, B:97:0x02e0, B:98:0x02f0, B:100:0x02fa, B:119:0x0400, B:121:0x0406, B:123:0x0414, B:125:0x041e, B:128:0x045d, B:130:0x0461, B:132:0x0467, B:133:0x0486, B:135:0x048a, B:136:0x04a6, B:138:0x04b0, B:142:0x0500, B:149:0x052e, B:151:0x0535, B:153:0x053f, B:154:0x0559, B:156:0x0563, B:157:0x057e, B:159:0x0588, B:172:0x062e, B:174:0x0638, B:176:0x063c, B:177:0x063e, B:179:0x0657, B:180:0x0660, B:182:0x066a, B:183:0x0671, B:185:0x067b, B:186:0x0682, B:188:0x068c, B:189:0x0691, B:191:0x069b, B:192:0x06a0, B:194:0x06a8, B:196:0x06ae, B:198:0x06b6, B:199:0x06ba, B:201:0x06d3, B:202:0x06d6, B:204:0x06e0, B:205:0x06e5, B:207:0x06ef, B:208:0x06f4, B:210:0x06fe, B:211:0x0704, B:213:0x070e, B:214:0x0714, B:216:0x071e, B:217:0x0724, B:219:0x072e, B:220:0x0733, B:222:0x0737, B:224:0x073d, B:226:0x0768, B:227:0x0771, B:228:0x0775, B:230:0x077b, B:232:0x0787, B:234:0x0793, B:235:0x0797, B:236:0x07ac, B:238:0x07b3, B:239:0x07b8, B:240:0x07c3, B:242:0x07c7, B:244:0x07ef, B:245:0x07fd, B:247:0x0801, B:249:0x0805, B:265:0x0862, B:278:0x08a2, B:279:0x08a7, B:289:0x08c9, B:290:0x08ce, B:317:0x093d, B:319:0x0941, B:321:0x0945, B:322:0x095a, B:324:0x095e, B:338:0x09d6, B:340:0x09dd, B:342:0x09e1, B:316:0x0938, B:263:0x085b, B:160:0x05a3, B:162:0x05ad, B:163:0x05c7, B:165:0x05cf, B:166:0x05e9, B:168:0x05f3, B:169:0x060c, B:171:0x0616, B:148:0x0529, B:139:0x04d3, B:141:0x04dd, B:77:0x022c, B:292:0x08d2, B:294:0x08e7, B:296:0x08ef, B:298:0x08f3, B:300:0x08f9, B:301:0x0905, B:302:0x0908, B:304:0x090e, B:306:0x0916, B:308:0x0919, B:310:0x091f, B:311:0x092b, B:312:0x092e, B:268:0x0867, B:270:0x0876, B:271:0x087b, B:273:0x0883, B:274:0x0888, B:282:0x08ac, B:284:0x08ba, B:285:0x08bf, B:144:0x0504), top: B:410:0x01cd, inners: #3, #9, #15, #16 }] */
    /* JADX WARN: Removed duplicated region for block: B:329:0x0972 A[Catch: Exception -> 0x09d3, TRY_LEAVE, TryCatch #6 {Exception -> 0x09d3, blocks: (B:326:0x0966, B:327:0x096c, B:329:0x0972), top: B:400:0x0966 }] */
    /* JADX WARN: Removed duplicated region for block: B:342:0x09e1 A[Catch: Exception -> 0x029e, TRY_LEAVE, TryCatch #11 {Exception -> 0x029e, blocks: (B:67:0x01cd, B:73:0x0214, B:83:0x023b, B:89:0x02a9, B:92:0x02bd, B:94:0x02c5, B:101:0x0335, B:103:0x033d, B:104:0x034f, B:106:0x0359, B:107:0x036b, B:109:0x0377, B:110:0x0392, B:112:0x039c, B:113:0x03b7, B:115:0x03c1, B:116:0x03dc, B:118:0x03e6, B:95:0x02d6, B:97:0x02e0, B:98:0x02f0, B:100:0x02fa, B:119:0x0400, B:121:0x0406, B:123:0x0414, B:125:0x041e, B:128:0x045d, B:130:0x0461, B:132:0x0467, B:133:0x0486, B:135:0x048a, B:136:0x04a6, B:138:0x04b0, B:142:0x0500, B:149:0x052e, B:151:0x0535, B:153:0x053f, B:154:0x0559, B:156:0x0563, B:157:0x057e, B:159:0x0588, B:172:0x062e, B:174:0x0638, B:176:0x063c, B:177:0x063e, B:179:0x0657, B:180:0x0660, B:182:0x066a, B:183:0x0671, B:185:0x067b, B:186:0x0682, B:188:0x068c, B:189:0x0691, B:191:0x069b, B:192:0x06a0, B:194:0x06a8, B:196:0x06ae, B:198:0x06b6, B:199:0x06ba, B:201:0x06d3, B:202:0x06d6, B:204:0x06e0, B:205:0x06e5, B:207:0x06ef, B:208:0x06f4, B:210:0x06fe, B:211:0x0704, B:213:0x070e, B:214:0x0714, B:216:0x071e, B:217:0x0724, B:219:0x072e, B:220:0x0733, B:222:0x0737, B:224:0x073d, B:226:0x0768, B:227:0x0771, B:228:0x0775, B:230:0x077b, B:232:0x0787, B:234:0x0793, B:235:0x0797, B:236:0x07ac, B:238:0x07b3, B:239:0x07b8, B:240:0x07c3, B:242:0x07c7, B:244:0x07ef, B:245:0x07fd, B:247:0x0801, B:249:0x0805, B:265:0x0862, B:278:0x08a2, B:279:0x08a7, B:289:0x08c9, B:290:0x08ce, B:317:0x093d, B:319:0x0941, B:321:0x0945, B:322:0x095a, B:324:0x095e, B:338:0x09d6, B:340:0x09dd, B:342:0x09e1, B:316:0x0938, B:263:0x085b, B:160:0x05a3, B:162:0x05ad, B:163:0x05c7, B:165:0x05cf, B:166:0x05e9, B:168:0x05f3, B:169:0x060c, B:171:0x0616, B:148:0x0529, B:139:0x04d3, B:141:0x04dd, B:77:0x022c, B:292:0x08d2, B:294:0x08e7, B:296:0x08ef, B:298:0x08f3, B:300:0x08f9, B:301:0x0905, B:302:0x0908, B:304:0x090e, B:306:0x0916, B:308:0x0919, B:310:0x091f, B:311:0x092b, B:312:0x092e, B:268:0x0867, B:270:0x0876, B:271:0x087b, B:273:0x0883, B:274:0x0888, B:282:0x08ac, B:284:0x08ba, B:285:0x08bf, B:144:0x0504), top: B:410:0x01cd, inners: #3, #9, #15, #16 }] */
    /* JADX WARN: Removed duplicated region for block: B:394:0x08d2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public /* synthetic */ java.lang.Object lambda$install$1$NotificationOptions(final android.content.Context r18, java.lang.Object r19, java.lang.Object r20, java.lang.reflect.Method r21, java.lang.Object[] r22) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 2728
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.applisto.appcloner.classes.NotificationOptions.lambda$install$1$NotificationOptions(android.content.Context, java.lang.Object, java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$null$0(int i, Context context) {
        try {
            Log.m15i(TAG, "run; cancelling notification; id: " + i);
            ((NotificationManager) context.getSystemService("notification")).cancel(i);
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.applisto.appcloner.classes.NotificationOptions$1 */
    /* loaded from: classes2.dex */
    public class C04371 extends BroadcastReceiver {
        C04371() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                final int intExtra = intent.getIntExtra("id", 0);
                final Notification notification = (Notification) NotificationOptions.this.mSnoozeNotifications.remove(Integer.valueOf(intExtra));
                if (notification != null) {
                    final NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                    notificationManager.cancel(intExtra);
                    Runnable runnable = (Runnable) NotificationOptions.this.mTimeoutRunnables.get(Integer.valueOf(intExtra));
                    if (runnable != null) {
                        NotificationOptions.this.mTimeoutHandler.removeCallbacks(runnable);
                    }
                    Runnable runnable2 = new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$NotificationOptions$1$3W4-9Dj5LfZQOeUnizMcb6bp1D0
                        @Override // java.lang.Runnable
                        public final void run() {
                            NotificationOptions.C04371.lambda$onReceive$0(notificationManager, intExtra, notification);
                        }
                    };
                    NotificationOptions.this.mTimeoutHandler.postDelayed(runnable2, NotificationOptions.this.mNotificationSnoozeTimeout * 1000);
                    NotificationOptions.this.mTimeoutRunnables.put(Integer.valueOf(intExtra), runnable2);
                }
            } catch (Throwable th) {
                Log.m21w(NotificationOptions.TAG, th);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onReceive$0(NotificationManager notificationManager, int i, Notification notification) {
            try {
                notificationManager.notify(i, notification);
            } catch (Throwable th) {
                Log.m21w(NotificationOptions.TAG, th);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener, com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    public void onActivityCreated(Activity activity) {
        super.onActivityCreated(activity);
        this.mRunning = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        Log.m15i(TAG, "onAppExit; ");
        this.mRunning = false;
    }

    private boolean isAppClonerClassesNotification() {
        boolean z = false;
        for (StackTraceElement stackTraceElement : new Exception().getStackTrace()) {
            String className = stackTraceElement.getClassName();
            if ("android.app.NotificationManager".equals(className)) {
                z = true;
            } else if (z) {
                return className.startsWith(DefaultProvider.PREF_KEY_PREFIX);
            }
        }
        return false;
    }

    private static Bundle getExtras(Notification notification) {
        Bundle bundle;
        try {
            try {
                bundle = notification.extras;
            } catch (Exception e) {
                Log.m21w(TAG, e);
                bundle = null;
            }
        } catch (Throwable unused) {
            Field declaredField = Notification.class.getDeclaredField("extras");
            declaredField.setAccessible(true);
            bundle = (Bundle) declaredField.get(notification);
        }
        return bundle == null ? new Bundle() : bundle;
    }

    private String getNotificationChannelId(Context context, String str) {
        String notificationChannelName = getNotificationChannelName(str);
        if (notificationChannelName == null) {
            return null;
        }
        String str2 = "app_cloner_" + Math.abs(notificationChannelName.hashCode());
        Log.m15i(TAG, "getNotificationChannelId; channelId: " + str2 + ", channelName: " + notificationChannelName);
        ((NotificationManager) context.getSystemService("notification")).createNotificationChannel(new NotificationChannel(str2, notificationChannelName, 3));
        return str2;
    }

    private String getNotificationChannelName(String str) {
        if (!TextUtils.isEmpty(this.mSingleNotificationCategory)) {
            Log.m15i(TAG, "getNotificationChannelName; returning mSingleNotificationCategory: " + this.mSingleNotificationCategory);
            return this.mSingleNotificationCategory;
        }
        for (Map<String, Object> map : this.mNotificationCategories) {
            String str2 = (String) map.get("name");
            if (!TextUtils.isEmpty(str2)) {
                String str3 = (String) map.get("keywords");
                if (TextUtils.isEmpty(str3)) {
                    continue;
                } else {
                    boolean booleanValue = ((Boolean) map.get("ignoreCase")).booleanValue();
                    String lowerCase = booleanValue ? str.toLowerCase(Locale.ENGLISH) : str;
                    Log.m15i(TAG, "getNotificationChannelName; name: " + str2 + ", keywords: " + str3 + ", ignoreCase: " + booleanValue + ", matchText: " + lowerCase);
                    for (String str4 : str3.split(",")) {
                        String trim = str4.trim();
                        if (booleanValue) {
                            trim = trim.toLowerCase(Locale.ENGLISH);
                        }
                        if (lowerCase.contains(trim)) {
                            Log.m15i(TAG, "getNotificationChannelName; found keyword; keyword: " + trim);
                            return str2;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void replaceNotificationText(Notification notification, String str, String str2, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        String str3;
        Bundle bundle;
        Bundle bundle2;
        Bundle bundle3;
        Parcelable[] parcelableArray;
        int i;
        Parcelable[] parcelableArr;
        int i2;
        ArrayList parcelableArrayList;
        Parcelable[] parcelableArray2;
        Log.m15i(TAG, "replaceNotificationText; replace: " + str + ", with: " + str2 + ", ignoreCase: " + z + ", replaceInTitle: " + z2 + ", replaceInContent: " + z3 + ", replaceInMessages: " + z4 + ", replaceInActions: " + z5);
        notification.tickerText = replaceText(notification.tickerText, str, str2, z);
        if (Build.VERSION.SDK_INT >= 19) {
            Bundle extras = getExtras(notification);
            if (z2) {
                CharSequence charSequence = extras.getCharSequence(NotificationCompat.EXTRA_TITLE);
                if (!TextUtils.isEmpty(charSequence)) {
                    CharSequence replaceText = replaceText(charSequence, str, str2, z);
                    if (!TextUtils.isEmpty(replaceText)) {
                        extras.putCharSequence(NotificationCompat.EXTRA_TITLE, replaceText);
                    } else {
                        extras.remove(NotificationCompat.EXTRA_TITLE_BIG);
                    }
                }
                CharSequence charSequence2 = extras.getCharSequence(NotificationCompat.EXTRA_TITLE_BIG);
                if (!TextUtils.isEmpty(charSequence2)) {
                    CharSequence replaceText2 = replaceText(charSequence2, str, str2, z);
                    if (!TextUtils.isEmpty(replaceText2)) {
                        extras.putCharSequence(NotificationCompat.EXTRA_TITLE_BIG, replaceText2);
                    } else {
                        extras.remove(NotificationCompat.EXTRA_TITLE_BIG);
                    }
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    CharSequence charSequence3 = extras.getCharSequence(NotificationCompat.EXTRA_CONVERSATION_TITLE);
                    if (!TextUtils.isEmpty(charSequence3)) {
                        CharSequence replaceText3 = replaceText(charSequence3, str, str2, z);
                        if (!TextUtils.isEmpty(replaceText3)) {
                            extras.putCharSequence(NotificationCompat.EXTRA_CONVERSATION_TITLE, replaceText3);
                        } else {
                            extras.remove(NotificationCompat.EXTRA_CONVERSATION_TITLE);
                        }
                    }
                    CharSequence charSequence4 = extras.getCharSequence("android.hiddenConversationTitle");
                    if (!TextUtils.isEmpty(charSequence4)) {
                        CharSequence replaceText4 = replaceText(charSequence4, str, str2, z);
                        if (!TextUtils.isEmpty(replaceText4)) {
                            extras.putCharSequence("android.hiddenConversationTitle", replaceText4);
                        } else {
                            extras.remove("android.hiddenConversationTitle");
                        }
                    }
                }
            }
            if (z3) {
                CharSequence charSequence5 = extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
                if (!TextUtils.isEmpty(charSequence5)) {
                    CharSequence replaceText5 = replaceText(charSequence5, str, str2, z);
                    if (!TextUtils.isEmpty(replaceText5)) {
                        extras.putCharSequence(NotificationCompat.EXTRA_TEXT, replaceText5);
                    } else {
                        extras.remove(NotificationCompat.EXTRA_TEXT);
                    }
                }
                CharSequence charSequence6 = extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT);
                if (!TextUtils.isEmpty(charSequence6)) {
                    CharSequence replaceText6 = replaceText(charSequence6, str, str2, z);
                    if (!TextUtils.isEmpty(replaceText6)) {
                        extras.putCharSequence(NotificationCompat.EXTRA_SUB_TEXT, replaceText6);
                    } else {
                        extras.remove(NotificationCompat.EXTRA_SUB_TEXT);
                    }
                }
                CharSequence charSequence7 = extras.getCharSequence(NotificationCompat.EXTRA_INFO_TEXT);
                if (!TextUtils.isEmpty(charSequence7)) {
                    CharSequence replaceText7 = replaceText(charSequence7, str, str2, z);
                    if (!TextUtils.isEmpty(replaceText7)) {
                        extras.putCharSequence(NotificationCompat.EXTRA_INFO_TEXT, replaceText7);
                    } else {
                        extras.remove(NotificationCompat.EXTRA_INFO_TEXT);
                    }
                }
                CharSequence charSequence8 = extras.getCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT);
                if (!TextUtils.isEmpty(charSequence8)) {
                    CharSequence replaceText8 = replaceText(charSequence8, str, str2, z);
                    if (!TextUtils.isEmpty(replaceText8)) {
                        extras.putCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT, replaceText8);
                    } else {
                        extras.remove(NotificationCompat.EXTRA_SUMMARY_TEXT);
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    CharSequence charSequence9 = extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT);
                    if (!TextUtils.isEmpty(charSequence9)) {
                        CharSequence replaceText9 = replaceText(charSequence9, str, str2, z);
                        if (!TextUtils.isEmpty(replaceText9)) {
                            extras.putCharSequence(NotificationCompat.EXTRA_BIG_TEXT, replaceText9);
                        } else {
                            extras.remove(NotificationCompat.EXTRA_BIG_TEXT);
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    CharSequence charSequence10 = extras.getCharSequence(NotificationCompat.EXTRA_SELF_DISPLAY_NAME);
                    if (!TextUtils.isEmpty(charSequence10)) {
                        CharSequence replaceText10 = replaceText(charSequence10, str, str2, z);
                        if (!TextUtils.isEmpty(replaceText10)) {
                            extras.putCharSequence(NotificationCompat.EXTRA_SELF_DISPLAY_NAME, replaceText10);
                        } else {
                            extras.remove(NotificationCompat.EXTRA_SELF_DISPLAY_NAME);
                        }
                    }
                }
            }
            String str4 = "text";
            if (z4) {
                CharSequence[] charSequenceArray = extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES);
                if (charSequenceArray != null) {
                    ArrayList arrayList = new ArrayList();
                    int length = charSequenceArray.length;
                    int i3 = 0;
                    while (i3 < length) {
                        int i4 = length;
                        CharSequence charSequence11 = charSequenceArray[i3];
                        CharSequence replaceText11 = replaceText(charSequence11, str, str2, z);
                        if (!TextUtils.isEmpty(replaceText11)) {
                            if (charSequence11 instanceof String) {
                                replaceText11 = replaceText11.toString();
                            }
                            arrayList.add(replaceText11);
                        }
                        i3++;
                        length = i4;
                    }
                    if (!arrayList.isEmpty()) {
                        extras.putCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES, (CharSequence[]) arrayList.toArray(new CharSequence[0]));
                    } else {
                        extras.remove(NotificationCompat.EXTRA_TEXT_LINES);
                    }
                }
                if (Build.VERSION.SDK_INT >= 24 && (parcelableArray2 = extras.getParcelableArray(NotificationCompat.EXTRA_MESSAGES)) != null) {
                    ArrayList arrayList2 = new ArrayList();
                    int length2 = parcelableArray2.length;
                    int i5 = 0;
                    while (i5 < length2) {
                        Parcelable[] parcelableArr2 = parcelableArray2;
                        Bundle bundle4 = (Bundle) parcelableArray2[i5];
                        int i6 = length2;
                        String string = bundle4.getString("sender");
                        if (!TextUtils.isEmpty(string)) {
                            string = "" + ((Object) replaceText(string, str, str2, z));
                            bundle4.putString("sender", string);
                        }
                        String string2 = bundle4.getString("text");
                        if (!TextUtils.isEmpty(string2)) {
                            string2 = "" + ((Object) replaceText(string2, str, str2, z));
                            bundle4.putString("text", string2);
                        }
                        if (!TextUtils.isEmpty(string) || !TextUtils.isEmpty(string2)) {
                            arrayList2.add(bundle4);
                        }
                        i5++;
                        length2 = i6;
                        parcelableArray2 = parcelableArr2;
                    }
                    if (!arrayList2.isEmpty()) {
                        extras.putParcelableArray(NotificationCompat.EXTRA_MESSAGES, (Parcelable[]) arrayList2.toArray(new Parcelable[0]));
                    } else {
                        extras.remove(NotificationCompat.EXTRA_MESSAGES);
                    }
                }
            }
            if (z5 && notification.actions != null) {
                ArrayList arrayList3 = new ArrayList();
                for (Notification.Action action : notification.actions) {
                    action.title = replaceText(action.title, str, str2, z);
                    if (!TextUtils.isEmpty(action.title)) {
                        arrayList3.add(action);
                    }
                }
                if (!arrayList3.isEmpty()) {
                    notification.actions = (Notification.Action[]) arrayList3.toArray(new Notification.Action[0]);
                } else {
                    notification.actions = null;
                }
            }
            try {
                Bundle bundle5 = extras.getBundle("android.wearable.EXTENSIONS");
                if (bundle5 != null) {
                    if (z5 && (parcelableArrayList = bundle5.getParcelableArrayList("actions")) != null) {
                        Iterator it = parcelableArrayList.iterator();
                        while (it.hasNext()) {
                            Notification.Action action2 = (Notification.Action) it.next();
                            action2.title = replaceText(action2.title, str, str2, z);
                            if (TextUtils.isEmpty(action2.title)) {
                                it.remove();
                            }
                        }
                    }
                    Parcelable[] parcelableArray3 = bundle5.getParcelableArray("pages");
                    if (parcelableArray3 != null) {
                        int length3 = parcelableArray3.length;
                        int i7 = 0;
                        while (i7 < length3) {
                            try {
                                Notification notification2 = (Notification) parcelableArray3[i7];
                                parcelableArr = parcelableArray3;
                                str3 = str4;
                                i2 = length3;
                                bundle = extras;
                                i = i7;
                                try {
                                    replaceNotificationText(notification2, str, str2, z, z2, z3, z4, z5);
                                } catch (Exception e) {
                                    e = e;
                                    try {
                                        Log.m21w(TAG, e);
                                        i7 = i + 1;
                                        str4 = str3;
                                        extras = bundle;
                                        length3 = i2;
                                        parcelableArray3 = parcelableArr;
                                    } catch (Exception e2) {
                                        e = e2;
                                        Log.m21w(TAG, e);
                                        bundle2 = bundle.getBundle("android.car.EXTENSIONS");
                                        if (bundle2 != null) {
                                            return;
                                        } else {
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception e3) {
                                e = e3;
                                i = i7;
                                parcelableArr = parcelableArray3;
                                i2 = length3;
                                str3 = str4;
                                bundle = extras;
                            }
                            i7 = i + 1;
                            str4 = str3;
                            extras = bundle;
                            length3 = i2;
                            parcelableArray3 = parcelableArr;
                        }
                    }
                }
                str3 = str4;
                bundle = extras;
            } catch (Exception e4) {
                e = e4;
                str3 = str4;
                bundle = extras;
            }
            try {
                bundle2 = bundle.getBundle("android.car.EXTENSIONS");
                if (bundle2 != null || !z4 || (bundle3 = bundle2.getBundle("car_conversation")) == null || (parcelableArray = bundle3.getParcelableArray("messages")) == null) {
                    return;
                }
                for (Parcelable parcelable : parcelableArray) {
                    Bundle bundle6 = (Bundle) parcelable;
                    CharSequence charSequence12 = bundle6.getCharSequence("author");
                    if (!TextUtils.isEmpty(charSequence12)) {
                        bundle6.putCharSequence("author", replaceText(charSequence12, str, str2, z));
                    }
                    CharSequence charSequence13 = bundle6.getCharSequence(str3);
                    if (!TextUtils.isEmpty(charSequence13)) {
                        bundle6.putCharSequence(str3, replaceText(charSequence13, str, str2, z));
                    }
                }
            } catch (Exception e5) {
                Log.m21w(TAG, e5);
            }
        }
    }

    private CharSequence replaceText(CharSequence charSequence, String str, CharSequence charSequence2, boolean z) {
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        if (TextUtils.isEmpty(str)) {
            return charSequence2;
        }
        while (true) {
            try {
                CharSequence replace = replace(charSequence, str, charSequence2, z);
                if (charSequence.toString().equals(replace.toString())) {
                    break;
                }
                charSequence = replace;
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
        }
        return charSequence;
    }

    private static CharSequence replace(CharSequence charSequence, String str, CharSequence charSequence2, boolean z) {
        int indexOf;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        if (z) {
            indexOf = charSequence.toString().toLowerCase(Locale.ENGLISH).indexOf(str.toLowerCase(Locale.ENGLISH));
        } else {
            indexOf = charSequence.toString().indexOf(str);
        }
        if (indexOf == -1) {
            return charSequence;
        }
        spannableStringBuilder.setSpan(str, indexOf, str.length() + indexOf, 33);
        int spanStart = spannableStringBuilder.getSpanStart(str);
        int spanEnd = spannableStringBuilder.getSpanEnd(str);
        if (spanStart != -1) {
            spannableStringBuilder.replace(spanStart, spanEnd, charSequence2);
        }
        return spannableStringBuilder;
    }
}
