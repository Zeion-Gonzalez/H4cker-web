package com.instabug.library;

import android.content.Context;
import android.content.SharedPreferences;
import com.instabug.library.Feature;
import com.instabug.library.core.eventbus.coreeventbus.C0640a;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.model.State;
import com.instabug.library.network.Request;
import com.instabug.library.network.p030a.C0723a;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: InstabugFeaturesManager.java */
/* renamed from: com.instabug.library.b */
/* loaded from: classes.dex */
public class C0629b {

    /* renamed from: e */
    private static volatile C0629b f777e;

    /* renamed from: c */
    private ConcurrentHashMap<Feature, Feature.State> f778c = new ConcurrentHashMap<>();

    /* renamed from: d */
    private ConcurrentHashMap<Feature, Boolean> f779d = new ConcurrentHashMap<>();

    /* renamed from: b */
    private static final Object f776b = new Object();

    /* renamed from: a */
    static final Feature.State f775a = Feature.State.ENABLED;

    /* renamed from: a */
    public static C0629b m1160a() {
        if (f777e == null) {
            synchronized (f776b) {
                if (f777e == null) {
                    f777e = new C0629b();
                }
            }
        }
        return f777e;
    }

    private C0629b() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1168a(Feature feature, boolean z) {
        if (this.f779d.containsKey(feature) && this.f779d.get(feature).booleanValue() == z) {
            InstabugSDKLogger.m1799d(this, "Feature " + feature + " availability is already " + z + ", ignoring");
        } else {
            InstabugSDKLogger.m1799d(this, "Setting feature " + feature + " availability to " + z);
            this.f779d.put(feature, Boolean.valueOf(z));
        }
    }

    /* renamed from: a */
    public boolean m1169a(Feature feature) {
        if (this.f779d.containsKey(feature)) {
            InstabugSDKLogger.m1799d(this, "Feature " + feature + " availability is " + this.f779d.get(feature));
            return this.f779d.get(feature).booleanValue();
        }
        InstabugSDKLogger.m1799d(this, "Feature " + feature + " availability not found, returning true");
        return true;
    }

    /* renamed from: b */
    public boolean m1172b() {
        return m1165d(Instabug.getApplicationContext()) > 0;
    }

    /* renamed from: a */
    public void m1167a(Feature feature, Feature.State state) {
        if (this.f778c.containsKey(feature) && this.f778c.get(feature) == state) {
            InstabugSDKLogger.m1799d(this, "Feature " + feature + " state is already " + state + " ignoring");
        } else {
            InstabugSDKLogger.m1799d(this, "Setting " + feature + " state to " + state);
            this.f778c.put(feature, state);
        }
    }

    /* renamed from: b */
    public Feature.State m1170b(Feature feature) {
        boolean z = m1169a(feature) && m1169a(Feature.INSTABUG);
        InstabugSDKLogger.m1799d(this, "Feature " + feature + " isAvailable = " + z + ", and it's state is " + this.f778c.get(feature));
        if (!z) {
            InstabugSDKLogger.m1799d(this, "Feature " + feature + " isn't available, returning " + Feature.State.DISABLED);
            return Feature.State.DISABLED;
        }
        if (this.f778c.containsKey(feature)) {
            return this.f778c.get(feature);
        }
        InstabugSDKLogger.m1799d(this, "Feature " + feature + " is available, but no specific state is set. Returning " + f775a);
        return f775a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1166a(final Context context) {
        new Thread(new Runnable() { // from class: com.instabug.library.b.1
            @Override // java.lang.Runnable
            public void run() {
                SharedPreferences.Editor edit = context.getSharedPreferences(SettingsManager.INSTABUG_SHARED_PREF_NAME, 0).edit();
                for (Feature feature : C0629b.this.f779d.keySet()) {
                    edit.putBoolean(feature.name() + "AVAIL", ((Boolean) C0629b.this.f779d.get(feature)).booleanValue());
                    InstabugSDKLogger.m1799d(this, "Saved feature " + feature + " availability " + C0629b.this.f779d.get(feature) + " to shared preferences");
                }
                for (Feature feature2 : C0629b.this.f778c.keySet()) {
                    edit.putString(feature2.name() + "STATE", ((Feature.State) C0629b.this.f778c.get(feature2)).name());
                    InstabugSDKLogger.m1799d(this, "Saved feature " + feature2 + " state " + C0629b.this.f778c.get(feature2) + " to shared preferences");
                }
                edit.apply();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public void m1171b(final Context context) {
        new Thread(new Runnable() { // from class: com.instabug.library.b.2
            @Override // java.lang.Runnable
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsManager.INSTABUG_SHARED_PREF_NAME, 0);
                for (Feature feature : Feature.values()) {
                    String str = feature.name() + "AVAIL";
                    boolean z = sharedPreferences.getBoolean(feature.name() + "AVAIL", true);
                    if (sharedPreferences.contains(str)) {
                        C0629b.this.f779d.put(feature, Boolean.valueOf(z));
                        InstabugSDKLogger.m1799d(this, "Feature " + feature + " saved availability " + z + " restored from shared preferences");
                    } else if (!C0629b.this.f779d.containsKey(feature)) {
                        C0629b.this.f779d.putIfAbsent(feature, Boolean.valueOf(z));
                        InstabugSDKLogger.m1799d(this, "Restored feature " + feature + " availability " + z + " from shared preferences");
                    } else {
                        InstabugSDKLogger.m1799d(this, "Not restoring feature " + feature + " availability as it's already set to " + C0629b.this.f779d.get(feature));
                    }
                    if (!C0629b.this.f778c.containsKey(feature)) {
                        Feature.State valueOf = Feature.State.valueOf(sharedPreferences.getString(feature.name() + "STATE", C0629b.f775a.name()));
                        C0629b.this.f778c.putIfAbsent(feature, valueOf);
                        InstabugSDKLogger.m1799d(this, "Restored feature " + feature + " state " + valueOf + " from shared preferences");
                    } else {
                        InstabugSDKLogger.m1799d(this, "Not restoring feature " + feature + " state as it's already set to " + C0629b.this.f778c.get(feature));
                    }
                }
            }
        }).start();
    }

    /* renamed from: c */
    public void m1173c(final Context context) {
        try {
            if (System.currentTimeMillis() - m1165d(context) > 86400000) {
                InstabugSDKLogger.m1799d(this, "lLast fetched at is more than 24h, retrieve it again");
                C0723a.m1620a().m1621a(context, new Request.Callbacks<String, Throwable>() { // from class: com.instabug.library.b.3
                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onSucceeded(String str) {
                        try {
                            C0629b.this.m1162a(System.currentTimeMillis(), context);
                            InstabugSDKLogger.m1799d(C0629b.class, "Features fetched successfully");
                            JSONObject jSONObject = new JSONObject(str);
                            C0629b.this.m1168a(Feature.CRASH_REPORTING, jSONObject.optBoolean("crash_reporting", true));
                            C0629b.this.m1168a(Feature.PUSH_NOTIFICATION, jSONObject.optBoolean("push_notifications", true));
                            C0629b.this.m1168a(Feature.WHITE_LABELING, jSONObject.optBoolean("white_label", true));
                            C0629b.this.m1168a(Feature.IN_APP_MESSAGING, jSONObject.optBoolean("in_app_messaging", true));
                            C0629b.this.m1168a(Feature.MULTIPLE_ATTACHMENTS, jSONObject.optBoolean("multiple_attachments", true));
                            C0629b.this.m1168a(Feature.TRACK_USER_STEPS, jSONObject.optBoolean(State.KEY_USER_STEPS, true));
                            C0629b.this.m1168a(Feature.REPRO_STEPS, jSONObject.optBoolean("repro_steps", true));
                            C0629b.this.m1168a(Feature.CONSOLE_LOGS, jSONObject.optBoolean(State.KEY_CONSOLE_LOG, true));
                            C0629b.this.m1168a(Feature.INSTABUG_LOGS, jSONObject.optBoolean("ibg_log", true));
                            C0629b.this.m1168a(Feature.USER_DATA, jSONObject.optBoolean(State.KEY_USER_DATA, true));
                            C0629b.this.m1168a(Feature.SURVEYS, jSONObject.optBoolean("surveys", true));
                            C0629b.this.m1168a(Feature.VIEW_HIERARCHY, jSONObject.optBoolean("view_hierarchy", true));
                            C0629b.this.m1168a(Feature.USER_EVENTS, jSONObject.optBoolean(State.KEY_USER_EVENTS, true));
                            C0629b.this.m1168a(Feature.DISCLAIMER, jSONObject.optBoolean("disclaimer_text", false));
                            C0640a.m1238a(new SDKCoreEvent(SDKCoreEvent.Feature.TYPE_FEATURES, SDKCoreEvent.Feature.VALUE_FETCHED));
                        } catch (JSONException e) {
                            InstabugSDKLogger.m1801e(C0629b.class, "Something went wrong while parsing fetching features request's response", e);
                        }
                    }

                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onFailed(Throwable th) {
                        InstabugSDKLogger.m1801e(C0629b.class, "Something went wrong while do fetching features request", th);
                    }
                });
            }
        } catch (IOException | JSONException e) {
            InstabugSDKLogger.m1801e(C0629b.class, "Something went wrong while do fetching features request", e);
        }
    }

    /* renamed from: d */
    private long m1165d(Context context) {
        return context.getSharedPreferences(SettingsManager.INSTABUG_SHARED_PREF_NAME, 0).getLong("LAST_FETCHED_AT", 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1162a(long j, Context context) {
        context.getSharedPreferences(SettingsManager.INSTABUG_SHARED_PREF_NAME, 0).edit().putLong("LAST_FETCHED_AT", j).apply();
    }
}
