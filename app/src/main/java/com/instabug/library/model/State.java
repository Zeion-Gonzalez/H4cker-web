package com.instabug.library.model;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.p000os.Environmenu;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.instabug.library.C0629b;
import com.instabug.library.C0646e;
import com.instabug.library.Feature;
import com.instabug.library.internal.device.C0658a;
import com.instabug.library.internal.device.InstabugDeviceProperties;
import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.internal.storage.cache.UserAttributesCacheManager;
import com.instabug.library.logging.InstabugLog;
import com.instabug.library.logging.InstabugUserEventLogger;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.tracking.C0741d;
import com.instabug.library.user.C0750a;
import com.instabug.library.user.UserEvent;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.visualusersteps.C0761b;
import com.instabug.library.visualusersteps.C0763d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class State implements Cacheable, Serializable {
    public static final String KEY_APP_PACKAGE_NAME = "bundle_id";
    public static final String KEY_APP_VERSION = "app_version";
    public static final String KEY_BATTERY_LEVEL = "battery_level";
    public static final String KEY_BATTERY_STATUS = "battery_state";
    public static final String KEY_CARRIER = "carrier";
    public static final String KEY_CONSOLE_LOG = "console_log";
    public static final String KEY_CURRENT_VIEW = "current_view";
    public static final String KEY_DENSITY = "density";
    public static final String KEY_DEVICE = "device";
    public static final String KEY_DEVICE_ROOTED = "device_rooted";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_INSTABUG_LOG = "instabug_log";
    public static final String KEY_LOCALE = "locale";
    public static final String KEY_MEMORY_FREE = "memory_free";
    public static final String KEY_MEMORY_TOTAL = "memory_total";
    public static final String KEY_MEMORY_USED = "memory_used";
    public static final String KEY_NETWORK_LOGS = "network_log";
    public static final String KEY_ORIENTATION = "orientation";
    public static final String KEY_OS = "os";
    public static final String KEY_REPORTED_AT = "reported_at";
    public static final String KEY_SCREEN_SIZE = "screen_size";
    public static final String KEY_SDK_VERSION = "sdk_version";
    public static final String KEY_STORAGE_FREE = "storage_free";
    public static final String KEY_STORAGE_TOTAL = "storage_total";
    public static final String KEY_STORAGE_USED = "storage_used";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_USER_ATTRIBUTES = "user_attributes";
    public static final String KEY_USER_DATA = "user_data";
    public static final String KEY_USER_EVENTS = "user_events";
    public static final String KEY_USER_STEPS = "user_steps";
    public static final String KEY_VISUAL_USER_STEPS = "user_repro_steps";
    public static final String KEY_WIFI_SSID = "wifi_ssid";
    public static final String KEY_WIFI_STATE = "wifi_state";

    /* renamed from: OS */
    private String f1117OS;
    private String ScreenOrientation;
    private String appPackageName;
    private String appVersion;
    private int batteryLevel;
    private String batteryState;
    private String carrier;
    private ArrayList<C0716a> consoleLog;
    private String currentView;
    private String device;
    private long duration;
    private long freeMemory;
    private long freeStorage;
    private String instabugLog;
    private boolean isDeviceRooted;
    private String locale;
    private String networkLogs;
    private long reportedAt;
    private String screenDensity;
    private String screenSize;
    private String sdkVersion;
    private String tags;
    private long totalMemory;
    private long totalStorage;
    private long usedMemory;
    private long usedStorage;
    private String userAttributes;
    private String userData;
    private String userEmail;
    private String userEvents;
    private ArrayList<C0718c> userSteps;
    private ArrayList<C0761b> visualUserSteps;
    private String wifiSSID;
    private boolean wifiState;

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public State setSdkVersion(String str) {
        this.sdkVersion = str;
        return this;
    }

    public String getLocale() {
        return this.locale;
    }

    public State setLocale(String str) {
        this.locale = str;
        return this;
    }

    public long getDuration() {
        return this.duration;
    }

    public State setDuration(long j) {
        this.duration = j;
        return this;
    }

    public String getDevice() {
        return this.device;
    }

    public State setDevice(String str) {
        this.device = str;
        return this;
    }

    public boolean isDeviceRooted() {
        return this.isDeviceRooted;
    }

    public State setIsDeviceRooted(boolean z) {
        this.isDeviceRooted = z;
        return this;
    }

    public String getOS() {
        return this.f1117OS;
    }

    public State setOS(String str) {
        this.f1117OS = str;
        return this;
    }

    public String getCarrier() {
        return this.carrier;
    }

    public State setCarrier(String str) {
        this.carrier = str;
        return this;
    }

    public String getAppPackageName() {
        return this.appPackageName;
    }

    public State setAppPackageName(String str) {
        this.appPackageName = str;
        return this;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public State setAppVersion(String str) {
        this.appVersion = str;
        return this;
    }

    public int getBatteryLevel() {
        return this.batteryLevel;
    }

    public State setBatteryLevel(int i) {
        this.batteryLevel = i;
        return this;
    }

    public String getBatteryState() {
        return this.batteryState;
    }

    public State setBatteryState(String str) {
        this.batteryState = str;
        return this;
    }

    public boolean isWifiEnable() {
        return this.wifiState;
    }

    public State setWifiState(boolean z) {
        this.wifiState = z;
        return this;
    }

    public String getWifiSSID() {
        return this.wifiSSID;
    }

    public State setWifiSSID(String str) {
        this.wifiSSID = str;
        return this;
    }

    public long getUsedMemory() {
        return this.usedMemory;
    }

    public State setUsedMemory(long j) {
        this.usedMemory = j;
        return this;
    }

    public long getFreeMemory() {
        return this.freeMemory;
    }

    public State setFreeMemory(long j) {
        this.freeMemory = j;
        return this;
    }

    public long getTotalMemory() {
        return this.totalMemory;
    }

    public State setTotalMemory(long j) {
        this.totalMemory = j;
        return this;
    }

    public long getUsedStorage() {
        return this.usedStorage;
    }

    public State setUsedStorage(long j) {
        this.usedStorage = j;
        return this;
    }

    public long getFreeStorage() {
        return this.freeStorage;
    }

    public State setFreeStorage(long j) {
        this.freeStorage = j;
        return this;
    }

    public long getTotalStorage() {
        return this.totalStorage;
    }

    public State setTotalStorage(long j) {
        this.totalStorage = j;
        return this;
    }

    public String getScreenDensity() {
        return this.screenDensity;
    }

    public State setScreenDensity(String str) {
        this.screenDensity = str;
        return this;
    }

    public String getScreenSize() {
        return this.screenSize;
    }

    public State setScreenSize(String str) {
        this.screenSize = str;
        return this;
    }

    public String getScreenOrientation() {
        return this.ScreenOrientation;
    }

    public State setScreenOrientation(String str) {
        this.ScreenOrientation = str;
        return this;
    }

    public String getCurrentView() {
        return this.currentView;
    }

    public State setCurrentView(String str) {
        this.currentView = str;
        return this;
    }

    public String getInstabugLog() {
        return this.instabugLog;
    }

    public State setInstabugLog(String str) {
        this.instabugLog = str;
        return this;
    }

    public void updateConsoleLog() {
        setConsoleLog(Builder.access$000());
    }

    public JSONArray getConsoleLog() {
        return C0716a.m1585a(this.consoleLog);
    }

    public State setConsoleLog(ArrayList<C0716a> arrayList) {
        this.consoleLog = arrayList;
        return this;
    }

    public JSONArray getUserSteps() {
        return C0718c.m1597a(this.userSteps);
    }

    public State setUserSteps(ArrayList<C0718c> arrayList) {
        this.userSteps = arrayList;
        return this;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public State setUserEmail(String str) {
        this.userEmail = str;
        return this;
    }

    public String getUserData() {
        return this.userData;
    }

    public State setUserData(String str) {
        this.userData = str;
        return this;
    }

    public long getReportedAt() {
        return this.reportedAt;
    }

    public State setReportedAt(long j) {
        this.reportedAt = j;
        return this;
    }

    public String getTags() {
        return this.tags;
    }

    public State setTags(String str) {
        this.tags = str;
        return this;
    }

    public String getUserAttributes() {
        return this.userAttributes;
    }

    public State setUserAttributes(String str) {
        this.userAttributes = str;
        return this;
    }

    public String getNetworkLogs() {
        return this.networkLogs;
    }

    public State setNetworkLogs(String str) {
        this.networkLogs = str;
        return this;
    }

    public String getUserEvents() {
        return this.userEvents;
    }

    public State setUserEvents(String str) {
        this.userEvents = str;
        return this;
    }

    public String getVisualUserSteps() {
        return C0761b.m1833a(this.visualUserSteps);
    }

    public State setVisualUserSteps(ArrayList<C0761b> arrayList) {
        this.visualUserSteps = arrayList;
        return this;
    }

    public ArrayList<StateItem> getStateItems() throws JSONException {
        ArrayList<StateItem> arrayList = new ArrayList<>();
        arrayList.add(new StateItem().setKey(KEY_APP_PACKAGE_NAME).setValue(getAppPackageName()));
        arrayList.add(new StateItem().setKey(KEY_APP_VERSION).setValue(getAppVersion()));
        arrayList.add(new StateItem().setKey(KEY_BATTERY_LEVEL).setValue(Integer.valueOf(getBatteryLevel())));
        arrayList.add(new StateItem().setKey(KEY_BATTERY_STATUS).setValue(getBatteryState()));
        arrayList.add(new StateItem().setKey(KEY_CARRIER).setValue(getCarrier()));
        arrayList.add(new StateItem().setKey(KEY_CURRENT_VIEW).setValue(getCurrentView()));
        arrayList.add(new StateItem().setKey(KEY_DENSITY).setValue(getScreenDensity()));
        arrayList.add(new StateItem().setKey(KEY_DEVICE).setValue(getDevice()));
        arrayList.add(new StateItem().setKey(KEY_DEVICE_ROOTED).setValue(Boolean.valueOf(isDeviceRooted())));
        arrayList.add(new StateItem().setKey(KEY_DURATION).setValue(Long.valueOf(getDuration())));
        arrayList.add(new StateItem().setKey("email").setValue(getUserEmail()));
        arrayList.add(new StateItem().setKey(KEY_LOCALE).setValue(getLocale()));
        arrayList.add(new StateItem().setKey(KEY_MEMORY_FREE).setValue(Long.valueOf(getFreeMemory())));
        arrayList.add(new StateItem().setKey(KEY_MEMORY_TOTAL).setValue(Long.valueOf(getTotalMemory())));
        arrayList.add(new StateItem().setKey(KEY_MEMORY_USED).setValue(Long.valueOf(getUsedMemory())));
        arrayList.add(new StateItem().setKey(KEY_ORIENTATION).setValue(getScreenOrientation()));
        arrayList.add(new StateItem().setKey(KEY_OS).setValue(getOS()));
        arrayList.add(new StateItem().setKey(KEY_REPORTED_AT).setValue(Long.valueOf(getReportedAt())));
        arrayList.add(new StateItem().setKey(KEY_SCREEN_SIZE).setValue(getScreenSize()));
        arrayList.add(new StateItem().setKey(KEY_SDK_VERSION).setValue(getSdkVersion()));
        arrayList.add(new StateItem().setKey(KEY_STORAGE_FREE).setValue(Long.valueOf(getFreeStorage())));
        arrayList.add(new StateItem().setKey(KEY_STORAGE_TOTAL).setValue(Long.valueOf(getTotalStorage())));
        arrayList.add(new StateItem().setKey(KEY_STORAGE_USED).setValue(Long.valueOf(getUsedStorage())));
        arrayList.add(new StateItem().setKey(KEY_TAGS).setValue(getTags()));
        arrayList.add(new StateItem().setKey(KEY_WIFI_SSID).setValue(getWifiSSID()));
        arrayList.add(new StateItem().setKey(KEY_WIFI_STATE).setValue(Boolean.valueOf(isWifiEnable())));
        arrayList.add(new StateItem().setKey(KEY_USER_ATTRIBUTES).setValue(getUserAttributes()));
        return arrayList;
    }

    public ArrayList<StateItem> getLogsItems() {
        ArrayList<StateItem> arrayList = new ArrayList<>();
        arrayList.add(new StateItem().setKey(KEY_CONSOLE_LOG).setValue(getConsoleLog().toString()));
        arrayList.add(new StateItem().setKey(KEY_INSTABUG_LOG).setValue(getInstabugLog()));
        arrayList.add(new StateItem().setKey(KEY_USER_DATA).setValue(getUserData()));
        arrayList.add(new StateItem().setKey(KEY_NETWORK_LOGS).setValue(getNetworkLogs()));
        arrayList.add(new StateItem().setKey(KEY_USER_EVENTS).setValue(getUserEvents()));
        if (C0629b.m1160a().m1170b(Feature.TRACK_USER_STEPS) == Feature.State.ENABLED) {
            arrayList.add(new StateItem().setKey(KEY_USER_STEPS).setValue(getUserSteps().toString()));
        }
        if (C0629b.m1160a().m1170b(Feature.REPRO_STEPS) == Feature.State.ENABLED) {
            arrayList.add(new StateItem().setKey(KEY_VISUAL_USER_STEPS).setValue(getVisualUserSteps()));
        }
        return arrayList;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        ArrayList<StateItem> stateItems = getStateItems();
        for (int i = 0; i < stateItems.size(); i++) {
            jSONObject.put(stateItems.get(i).getKey(), stateItems.get(i).getValue());
        }
        ArrayList<StateItem> logsItems = getLogsItems();
        for (int i2 = 0; i2 < logsItems.size(); i2++) {
            jSONObject.put(logsItems.get(i2).getKey(), logsItems.get(i2).getValue());
        }
        InstabugSDKLogger.m1803v(this, jSONObject.toString());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has(KEY_APP_PACKAGE_NAME)) {
            setAppPackageName(jSONObject.getString(KEY_APP_PACKAGE_NAME));
        }
        if (jSONObject.has(KEY_APP_VERSION)) {
            setAppVersion(jSONObject.getString(KEY_APP_VERSION));
        }
        if (jSONObject.has(KEY_BATTERY_LEVEL)) {
            setBatteryLevel(jSONObject.getInt(KEY_BATTERY_LEVEL));
        }
        if (jSONObject.has(KEY_BATTERY_STATUS)) {
            setBatteryState(jSONObject.getString(KEY_BATTERY_STATUS));
        }
        if (jSONObject.has(KEY_CARRIER)) {
            setCarrier(jSONObject.getString(KEY_CARRIER));
        }
        if (jSONObject.has(KEY_CONSOLE_LOG)) {
            setConsoleLog(C0716a.m1584a(new JSONArray(jSONObject.getString(KEY_CONSOLE_LOG))));
        }
        if (jSONObject.has(KEY_CURRENT_VIEW)) {
            setCurrentView(jSONObject.getString(KEY_CURRENT_VIEW));
        }
        if (jSONObject.has(KEY_DENSITY)) {
            setScreenDensity(jSONObject.getString(KEY_DENSITY));
        }
        if (jSONObject.has(KEY_DEVICE)) {
            setDevice(jSONObject.getString(KEY_DEVICE));
        }
        if (jSONObject.has(KEY_DEVICE_ROOTED)) {
            setIsDeviceRooted(jSONObject.getBoolean(KEY_DEVICE_ROOTED));
        }
        if (jSONObject.has(KEY_DURATION)) {
            setDuration(jSONObject.getLong(KEY_DURATION));
        }
        if (jSONObject.has("email")) {
            setUserEmail(jSONObject.getString("email"));
        }
        if (jSONObject.has(KEY_INSTABUG_LOG)) {
            setInstabugLog(jSONObject.getString(KEY_INSTABUG_LOG));
        }
        if (jSONObject.has(KEY_LOCALE)) {
            setLocale(jSONObject.getString(KEY_LOCALE));
        }
        if (jSONObject.has(KEY_MEMORY_FREE)) {
            setFreeMemory(jSONObject.getLong(KEY_MEMORY_FREE));
        }
        if (jSONObject.has(KEY_MEMORY_TOTAL)) {
            setTotalMemory(jSONObject.getLong(KEY_MEMORY_TOTAL));
        }
        if (jSONObject.has(KEY_MEMORY_USED)) {
            setUsedMemory(jSONObject.getLong(KEY_MEMORY_USED));
        }
        if (jSONObject.has(KEY_ORIENTATION)) {
            setScreenOrientation(jSONObject.getString(KEY_ORIENTATION));
        }
        if (jSONObject.has(KEY_OS)) {
            setOS(jSONObject.getString(KEY_OS));
        }
        if (jSONObject.has(KEY_REPORTED_AT)) {
            setReportedAt(jSONObject.getLong(KEY_REPORTED_AT));
        }
        if (jSONObject.has(KEY_SCREEN_SIZE)) {
            setScreenSize(jSONObject.getString(KEY_SCREEN_SIZE));
        }
        if (jSONObject.has(KEY_SDK_VERSION)) {
            setSdkVersion(jSONObject.getString(KEY_SDK_VERSION));
        }
        if (jSONObject.has(KEY_STORAGE_FREE)) {
            setFreeStorage(jSONObject.getLong(KEY_STORAGE_FREE));
        }
        if (jSONObject.has(KEY_STORAGE_TOTAL)) {
            setTotalStorage(jSONObject.getLong(KEY_STORAGE_TOTAL));
        }
        if (jSONObject.has(KEY_STORAGE_USED)) {
            setUsedStorage(jSONObject.getLong(KEY_STORAGE_USED));
        }
        if (jSONObject.has(KEY_TAGS)) {
            setTags(jSONObject.getString(KEY_TAGS));
        }
        if (jSONObject.has(KEY_USER_DATA)) {
            setUserData(jSONObject.getString(KEY_USER_DATA));
        }
        if (jSONObject.has(KEY_USER_STEPS)) {
            setUserSteps(C0718c.m1596a(new JSONArray(jSONObject.getString(KEY_USER_STEPS))));
        }
        if (jSONObject.has(KEY_WIFI_SSID)) {
            setWifiSSID(jSONObject.getString(KEY_WIFI_SSID));
        }
        if (jSONObject.has(KEY_WIFI_STATE)) {
            setWifiState(jSONObject.getBoolean(KEY_WIFI_STATE));
        }
        if (jSONObject.has(KEY_USER_ATTRIBUTES)) {
            setUserAttributes(jSONObject.getString(KEY_USER_ATTRIBUTES));
        }
        if (jSONObject.has(KEY_NETWORK_LOGS)) {
            setNetworkLogs(jSONObject.getString(KEY_NETWORK_LOGS));
        }
        if (jSONObject.has(KEY_USER_EVENTS)) {
            setUserEvents(jSONObject.getString(KEY_USER_EVENTS));
        }
        if (jSONObject.has(KEY_VISUAL_USER_STEPS)) {
            setVisualUserSteps(C0761b.m1834a(new JSONArray(jSONObject.getString(KEY_VISUAL_USER_STEPS))));
        }
    }

    public String toString() {
        try {
            return toJson();
        } catch (JSONException e) {
            e.printStackTrace();
            InstabugSDKLogger.m1801e(this, "Something went wrong while getting state.toString()" + e.getMessage(), e);
            return "error";
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof State)) {
            return false;
        }
        State state = (State) obj;
        return String.valueOf(state.getAppVersion()).equals(String.valueOf(getAppVersion())) && state.getBatteryLevel() == getBatteryLevel() && String.valueOf(state.getBatteryState()).equals(String.valueOf(getBatteryState())) && String.valueOf(state.getCarrier()).equals(String.valueOf(getCarrier())) && String.valueOf(state.getConsoleLog()).equals(String.valueOf(getConsoleLog())) && String.valueOf(state.getCurrentView()).equals(String.valueOf(getCurrentView())) && state.getDuration() == getDuration() && String.valueOf(state.getDevice()).equals(String.valueOf(getDevice())) && state.getFreeMemory() == getFreeMemory() && state.getFreeStorage() == getFreeStorage() && String.valueOf(state.getLocale()).equals(String.valueOf(getLocale())) && String.valueOf(state.getOS()).equals(String.valueOf(getOS())) && state.getReportedAt() == getReportedAt() && String.valueOf(state.getScreenDensity()).equals(String.valueOf(getScreenDensity())) && String.valueOf(state.getScreenOrientation()).equals(String.valueOf(getScreenOrientation())) && String.valueOf(state.getScreenSize()).equals(String.valueOf(getScreenSize())) && String.valueOf(state.getSdkVersion()).equals(String.valueOf(getSdkVersion())) && state.getTotalMemory() == getTotalMemory() && state.getTotalStorage() == getTotalStorage() && String.valueOf(state.getTags()).equals(String.valueOf(getTags())) && state.getUsedMemory() == getUsedMemory() && state.getUsedStorage() == getUsedStorage() && String.valueOf(state.getUserData()).equals(String.valueOf(getUserData())) && String.valueOf(state.getUserEmail()).equals(String.valueOf(getUserEmail())) && String.valueOf(state.getUserSteps()).equals(String.valueOf(getUserSteps())) && String.valueOf(state.getWifiSSID()).equals(String.valueOf(getWifiSSID())) && state.isDeviceRooted() == isDeviceRooted() && state.isWifiEnable() == isWifiEnable() && String.valueOf(state.getInstabugLog()).equals(String.valueOf(getInstabugLog())) && String.valueOf(state.getUserAttributes()).equals(String.valueOf(getUserAttributes())) && String.valueOf(state.getNetworkLogs()).equals(String.valueOf(getNetworkLogs())) && String.valueOf(state.getUserEvents()).equals(String.valueOf(getUserEvents())) && String.valueOf(state.getVisualUserSteps()).equals(String.valueOf(getVisualUserSteps()));
    }

    public int hashCode() {
        return String.valueOf(getReportedAt()).hashCode();
    }

    /* loaded from: classes.dex */
    public static class StateItem<V> implements Serializable {
        String key;
        V value;

        public String getKey() {
            return this.key;
        }

        public StateItem<V> setKey(String str) {
            this.key = str;
            return this;
        }

        public V getValue() {
            return this.value;
        }

        public StateItem<V> setValue(V v) {
            this.value = v;
            return this;
        }

        public String toString() {
            return "key: " + getKey() + ", value: " + getValue();
        }
    }

    /* loaded from: classes.dex */
    public static class Builder implements Serializable {
        static final /* synthetic */ boolean $assertionsDisabled;
        private Context context;

        static {
            $assertionsDisabled = !State.class.desiredAssertionStatus();
        }

        static /* synthetic */ ArrayList access$000() {
            return getConsoleLog();
        }

        public Builder(Context context) {
            this.context = context;
        }

        private static ArrayList<C0716a> getConsoleLog() {
            ArrayList<C0716a> arrayList = new ArrayList<>();
            if (C0629b.m1160a().m1170b(Feature.CONSOLE_LOGS) != Feature.State.ENABLED) {
                return arrayList;
            }
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("logcat -v time -d " + Process.myPid()).getInputStream(), Charset.forName("UTF-8")));
                ArrayList arrayList2 = new ArrayList();
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    arrayList2.add(readLine);
                }
                bufferedReader.close();
                arrayList2.trimToSize();
                for (int size = arrayList2.size() > 700 ? arrayList2.size() - 700 : 0; size < arrayList2.size(); size++) {
                    if (((String) arrayList2.get(size)).length() > 18) {
                        C0716a c0716a = new C0716a();
                        c0716a.m1589a(((String) arrayList2.get(size)).substring(18));
                        c0716a.m1588a(C0716a.m1586b(((String) arrayList2.get(size)).substring(0, 18)));
                        arrayList.add(c0716a);
                    }
                }
                arrayList2.clear();
                return arrayList;
            } catch (IOException e) {
                InstabugSDKLogger.m1801e(Builder.class, "Could not read logcat log", e);
                return arrayList;
            }
        }

        public State build(boolean z) {
            State visualUserSteps = new State().setSdkVersion(getSdkVersion()).setLocale(getLocale()).setDuration(getActiveSessionDuration()).setDevice(getDevice()).setIsDeviceRooted(isDeviceRooted()).setOS(getOS()).setCarrier(getCarrier()).setAppVersion(getAppVersion()).setAppPackageName(getAppPackageName()).setBatteryLevel(getBatteryLevel()).setBatteryState(getBatteryState()).setWifiState(getWifiState()).setWifiSSID(getWifiSSID()).setFreeMemory(getFreeMemory()).setUsedMemory(getUsedMemory()).setTotalMemory(getTotalMemory()).setFreeStorage(getFreeStorage()).setUsedStorage(getUsedStorage()).setTotalStorage(getTotalStorage()).setScreenDensity(getScreenDensity()).setScreenSize(getScreenSize()).setScreenOrientation(getScreenOrientation()).setCurrentView(getCurrentView()).setConsoleLog(getConsoleLog()).setUserSteps(getUserSteps()).setUserEmail(getUserEmail()).setUserData(getUserData()).setReportedAt(getReportedAt()).setTags(getTags()).setUserAttributes(UserAttributesCacheManager.getUserAttributes()).setNetworkLogs(getNetworkLogs()).setUserEvents(getUserEvents()).setVisualUserSteps(getVisualUserSteps());
            if (z) {
                visualUserSteps.setInstabugLog(getInstabugLog());
            }
            return visualUserSteps;
        }

        public State buildInternalState() {
            return new State().setSdkVersion(getSdkVersion()).setLocale(getLocale()).setDuration(getActiveSessionDuration()).setDevice(getDevice()).setIsDeviceRooted(isDeviceRooted()).setOS(getOS()).setCarrier(getCarrier()).setAppVersion(getAppVersion()).setAppPackageName(getAppPackageName()).setBatteryLevel(getBatteryLevel()).setBatteryState(getBatteryState()).setWifiState(getWifiState()).setWifiSSID(getWifiSSID()).setFreeMemory(getFreeMemory()).setUsedMemory(getUsedMemory()).setTotalMemory(getTotalMemory()).setFreeStorage(getFreeStorage()).setUsedStorage(getUsedStorage()).setTotalStorage(getTotalStorage()).setScreenDensity(getScreenDensity()).setScreenSize(getScreenSize()).setScreenOrientation(getScreenOrientation()).setCurrentView(getCurrentView()).setReportedAt(getReportedAt());
        }

        private String getSdkVersion() {
            return "4.11.2";
        }

        private String getLocale() {
            return this.context.getResources().getConfiguration().locale.toString();
        }

        private long getActiveSessionDuration() {
            return C0646e.m1249a().m1266b();
        }

        public String formatSessionDuration(long j) {
            StringBuilder sb = new StringBuilder();
            int i = ((int) j) % 60;
            long j2 = j / 60;
            int i2 = ((int) j2) % 60;
            int i3 = ((int) (j2 / 60)) % 60;
            if (i3 <= 9) {
                sb.append("0");
            }
            sb.append(i3);
            sb.append(":");
            if (i2 <= 9) {
                sb.append("0");
            }
            sb.append(i2);
            sb.append(":");
            if (i <= 9) {
                sb.append("0");
            }
            sb.append(i);
            return sb.toString();
        }

        private String getDevice() {
            return InstabugDeviceProperties.getDeviceType();
        }

        public boolean isDeviceRooted() {
            try {
                return C0658a.m1289a();
            } catch (Exception e) {
                InstabugSDKLogger.m1800e(this, "Something went wrong while checking if device is rooted or not " + e.getMessage());
                return false;
            }
        }

        private String getOS() {
            return "OS Level " + Build.VERSION.SDK_INT;
        }

        private String getCarrier() {
            try {
                return ((TelephonyManager) this.context.getSystemService("phone")).getNetworkOperatorName();
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(this, "Got error while get Carrier", e);
                return "Unknown";
            }
        }

        private String getAppPackageName() {
            return InstabugDeviceProperties.getPackageName(this.context);
        }

        private String getAppVersion() {
            return InstabugDeviceProperties.getAppVersion(this.context);
        }

        private int getBatteryLevel() {
            try {
                Intent registerReceiver = this.context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                if (!$assertionsDisabled && registerReceiver == null) {
                    throw new AssertionError();
                }
                return (int) ((registerReceiver.getIntExtra("level", -1) / registerReceiver.getIntExtra("scale", -1)) * 100.0f);
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(this, "Got error while get battery level", e);
                return -1;
            }
        }

        private String getBatteryState() {
            String str;
            try {
                Intent registerReceiver = this.context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                if (!$assertionsDisabled && registerReceiver == null) {
                    throw new AssertionError();
                }
                int intExtra = registerReceiver.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
                boolean z = intExtra == 2 || intExtra == 5;
                int intExtra2 = registerReceiver.getIntExtra("plugged", -1);
                boolean z2 = intExtra2 == 2;
                boolean z3 = intExtra2 == 1;
                if (!z) {
                    return "Unplugged";
                }
                StringBuilder append = new StringBuilder().append("Charging");
                if (z3) {
                    str = " through AC Charger";
                } else {
                    str = z2 ? " through USB cable" : "";
                }
                return append.append(str).toString();
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(this, "Got error while get battery state", e);
                return "Unknown";
            }
        }

        private boolean getWifiState() {
            try {
                return ((ConnectivityManager) this.context.getSystemService("connectivity")).getNetworkInfo(1).isConnected();
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(this, "Got error while get wifi state", e);
                return false;
            }
        }

        private String getWifiSSID() {
            if (getWifiState()) {
                try {
                    return ((WifiManager) this.context.getSystemService("wifi")).getConnectionInfo().getSSID();
                } catch (SecurityException e) {
                    InstabugSDKLogger.m1800e(this, "Could not read wifi SSID. To enable please add the following line in your AndroidManifest.xml <uses-permission android:name=\"android.permission.ACCESS_WIFI_STATE\"/>");
                    return "Connected";
                }
            }
            return "Not Connected";
        }

        private long getFreeMemory() {
            ActivityManager activityManager = (ActivityManager) this.context.getSystemService("activity");
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return memoryInfo.availMem / 1048576;
        }

        private long getUsedMemory() {
            ActivityManager activityManager = (ActivityManager) this.context.getSystemService("activity");
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return (calculateTotalMemory() - memoryInfo.availMem) / 1048576;
        }

        private long getTotalMemory() {
            long calculateTotalMemory = calculateTotalMemory();
            if (calculateTotalMemory != 0) {
                return calculateTotalMemory / 1048576;
            }
            InstabugSDKLogger.m1800e(this, "Got error while calculate total memory");
            return -1L;
        }

        private long calculateTotalMemory() {
            return Build.VERSION.SDK_INT >= 16 ? calculateTotalMemoryApi16() : calculateTotalMemoryPreApi16();
        }

        @TargetApi(16)
        private long calculateTotalMemoryApi16() {
            ActivityManager activityManager = (ActivityManager) this.context.getSystemService("activity");
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return memoryInfo.totalMem;
        }

        /* JADX WARN: Removed duplicated region for block: B:33:0x0054 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private long calculateTotalMemoryPreApi16() {
            /*
                r6 = this;
                r1 = 0
                java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch: java.io.IOException -> L3f java.lang.Throwable -> L50
                java.lang.String r0 = "/proc/meminfo"
                java.lang.String r3 = "r"
                r2.<init>(r0, r3)     // Catch: java.io.IOException -> L3f java.lang.Throwable -> L50
                java.lang.String r0 = r2.readLine()     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                java.lang.String r1 = ":"
                java.lang.String[] r0 = r0.split(r1)     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                r1 = 1
                r0 = r0[r1]     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                java.lang.String r0 = r0.trim()     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                r1 = 0
                int r3 = r0.length()     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                int r3 = r3 + (-3)
                java.lang.String r0 = r0.substring(r1, r3)     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                java.lang.String r0 = r0.trim()     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                r2.close()     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                long r0 = java.lang.Long.parseLong(r0)     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L62
                r4 = 1024(0x400, double:5.06E-321)
                long r0 = r0 * r4
                if (r2 == 0) goto L39
                r2.close()     // Catch: java.io.IOException -> L3a
            L39:
                return r0
            L3a:
                r2 = move-exception
                r2.printStackTrace()
                goto L39
            L3f:
                r0 = move-exception
            L40:
                r0.printStackTrace()     // Catch: java.lang.Throwable -> L5f
                if (r1 == 0) goto L48
                r1.close()     // Catch: java.io.IOException -> L4b
            L48:
                r0 = 0
                goto L39
            L4b:
                r0 = move-exception
                r0.printStackTrace()
                goto L48
            L50:
                r0 = move-exception
                r2 = r1
            L52:
                if (r2 == 0) goto L57
                r2.close()     // Catch: java.io.IOException -> L58
            L57:
                throw r0
            L58:
                r1 = move-exception
                r1.printStackTrace()
                goto L57
            L5d:
                r0 = move-exception
                goto L52
            L5f:
                r0 = move-exception
                r2 = r1
                goto L52
            L62:
                r0 = move-exception
                r1 = r2
                goto L40
            */
            throw new UnsupportedOperationException("Method not decompiled: com.instabug.library.model.State.Builder.calculateTotalMemoryPreApi16():long");
        }

        private long getFreeStorage() {
            if (externalMemoryAvailable()) {
                return Environment.getExternalStorageDirectory().getUsableSpace() / 1048576;
            }
            InstabugSDKLogger.m1800e(this, "Got error while calculate free storage");
            return -1L;
        }

        private long getUsedStorage() {
            if (externalMemoryAvailable()) {
                return (Environment.getExternalStorageDirectory().getTotalSpace() - Environment.getExternalStorageDirectory().getFreeSpace()) / 1048576;
            }
            InstabugSDKLogger.m1800e(this, "Got error while calculate used storage");
            return -1L;
        }

        private long getTotalStorage() {
            if (externalMemoryAvailable()) {
                return (int) (Environment.getExternalStorageDirectory().getTotalSpace() / 1048576);
            }
            InstabugSDKLogger.m1800e(this, "Got error while calculate total storage");
            return -1L;
        }

        private boolean externalMemoryAvailable() {
            return Environment.getExternalStorageState().equals(Environmenu.MEDIA_MOUNTED);
        }

        private String getScreenDensity() {
            DisplayMetrics displayMetrics = getDisplayMetrics();
            if (displayMetrics.densityDpi < 160) {
                return "ldpi";
            }
            if (displayMetrics.densityDpi < 240) {
                return "mdpi";
            }
            if (displayMetrics.densityDpi < 320) {
                return "hdpi";
            }
            if (displayMetrics.densityDpi < 480) {
                return "xhdpi";
            }
            if (displayMetrics.densityDpi < 640) {
                return "xxhdpi";
            }
            return "xxxhdpi";
        }

        private String getScreenSize() {
            DisplayMetrics displayMetrics = getDisplayMetrics();
            return String.format("%sx%s", Integer.valueOf(displayMetrics.widthPixels), Integer.valueOf(displayMetrics.heightPixels));
        }

        private DisplayMetrics getDisplayMetrics() {
            Display defaultDisplay = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            if (Build.VERSION.SDK_INT >= 17) {
                defaultDisplay.getRealMetrics(displayMetrics);
            }
            return displayMetrics;
        }

        private String getScreenOrientation() {
            return this.context.getResources().getConfiguration().orientation == 2 ? "landscape" : "portrait";
        }

        private String getCurrentView() {
            return C0741d.m1746a().m1755b();
        }

        private String getInstabugLog() {
            if (C0629b.m1160a().m1170b(Feature.INSTABUG_LOGS) == Feature.State.ENABLED) {
                return InstabugLog.getLogs();
            }
            return null;
        }

        private ArrayList<C0718c> getUserSteps() {
            return C0741d.m1746a().m1756c();
        }

        private ArrayList<C0761b> getVisualUserSteps() {
            return C0763d.m1884a().m1897b();
        }

        private String getUserEmail() {
            return C0750a.m1784b();
        }

        private String getUserData() {
            return SettingsManager.getInstance().getUserData();
        }

        private long getReportedAt() {
            return InstabugDateFormatter.getCurrentUTCTimeStampInSeconds();
        }

        private String getTags() {
            return SettingsManager.getInstance().getTagsAsString();
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x00ac, code lost:
        
            r1 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:11:0x00ad, code lost:
        
            r1.printStackTrace();
         */
        /* JADX WARN: Code restructure failed: missing block: B:3:0x0024, code lost:
        
            if (r2.moveToFirst() != false) goto L4;
         */
        /* JADX WARN: Code restructure failed: missing block: B:4:0x0026, code lost:
        
            r1 = new com.instabug.library.model.NetworkLog();
            r1.setRequest(r2.getString(r2.getColumnIndex("request")));
            r1.setResponse(r2.getString(r2.getColumnIndex("response")));
            r1.setMethod(r2.getString(r2.getColumnIndex("method")));
            r1.setUrl(r2.getString(r2.getColumnIndex("url")));
            r1.setResponseCode(r2.getInt(r2.getColumnIndex(android.support.v4.app.NotificationCompat.CATEGORY_STATUS)));
            r1.setDate(r2.getString(r2.getColumnIndex("date")));
            r1.setHeaders(r2.getString(r2.getColumnIndex("headers")));
            r1.setTotalDuration(r2.getInt(r2.getColumnIndex("response_time")));
         */
        /* JADX WARN: Code restructure failed: missing block: B:5:0x0094, code lost:
        
            r9.put(r1.toJsonObject());
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.lang.String getNetworkLogs() {
            /*
                r10 = this;
                r2 = 0
                com.instabug.library.internal.storage.cache.a.a r0 = com.instabug.library.internal.storage.cache.p028a.C0672a.m1309a()
                com.instabug.library.internal.storage.cache.a.c r0 = r0.m1311b()
                java.lang.String r1 = "_id DESC"
                java.lang.String r1 = "100"
                org.json.JSONArray r9 = new org.json.JSONArray
                r9.<init>()
                java.lang.String r1 = "network_logs"
                java.lang.String r7 = "_id DESC"
                java.lang.String r8 = "100"
                r3 = r2
                r4 = r2
                r5 = r2
                r6 = r2
                android.database.Cursor r2 = r0.m1317a(r1, r2, r3, r4, r5, r6, r7, r8)
                boolean r1 = r2.moveToFirst()
                if (r1 == 0) goto La1
            L26:
                com.instabug.library.model.NetworkLog r1 = new com.instabug.library.model.NetworkLog
                r1.<init>()
                java.lang.String r3 = "request"
                int r3 = r2.getColumnIndex(r3)
                java.lang.String r3 = r2.getString(r3)
                r1.setRequest(r3)
                java.lang.String r3 = "response"
                int r3 = r2.getColumnIndex(r3)
                java.lang.String r3 = r2.getString(r3)
                r1.setResponse(r3)
                java.lang.String r3 = "method"
                int r3 = r2.getColumnIndex(r3)
                java.lang.String r3 = r2.getString(r3)
                r1.setMethod(r3)
                java.lang.String r3 = "url"
                int r3 = r2.getColumnIndex(r3)
                java.lang.String r3 = r2.getString(r3)
                r1.setUrl(r3)
                java.lang.String r3 = "status"
                int r3 = r2.getColumnIndex(r3)
                int r3 = r2.getInt(r3)
                r1.setResponseCode(r3)
                java.lang.String r3 = "date"
                int r3 = r2.getColumnIndex(r3)
                java.lang.String r3 = r2.getString(r3)
                r1.setDate(r3)
                java.lang.String r3 = "headers"
                int r3 = r2.getColumnIndex(r3)
                java.lang.String r3 = r2.getString(r3)
                r1.setHeaders(r3)
                java.lang.String r3 = "response_time"
                int r3 = r2.getColumnIndex(r3)
                int r3 = r2.getInt(r3)
                long r4 = (long) r3
                r1.setTotalDuration(r4)
                org.json.JSONObject r1 = r1.toJsonObject()     // Catch: org.json.JSONException -> Lac
                r9.put(r1)     // Catch: org.json.JSONException -> Lac
            L9b:
                boolean r1 = r2.moveToNext()
                if (r1 != 0) goto L26
            La1:
                r2.close()
                r0.m1321b()
                java.lang.String r0 = r9.toString()
                return r0
            Lac:
                r1 = move-exception
                r1.printStackTrace()
                goto L9b
            */
            throw new UnsupportedOperationException("Method not decompiled: com.instabug.library.model.State.Builder.getNetworkLogs():java.lang.String");
        }

        private String getUserEvents() {
            try {
                return UserEvent.toJson(InstabugUserEventLogger.getInstance().getUserEvents()).toString();
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(this, "Got error while parsing user events logs", e);
                return "";
            }
        }

        private String getUserAttributes() {
            HashMap<String, String> all = UserAttributesCacheManager.getAll();
            if (all == null || all.size() == 0) {
                return "{}";
            }
            C0717b c0717b = new C0717b();
            c0717b.m1594a(all);
            try {
                return c0717b.toJson();
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(this, "parsing user attributes got error: " + e.getMessage(), e);
                return "{}";
            }
        }
    }
}
