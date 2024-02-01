package com.instabug.library.logging;

import android.content.Context;
import android.util.Log;
import com.instabug.library.C0629b;
import com.instabug.library.Feature;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.StringUtility;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class InstabugLog {
    private static final String INSTABUG_LOG_TAG = "INSTABUG_LOG_TAG";
    public static final String LOG_MESSAGE_DATE_FORMAT = "MM-dd HH:mm:ss.SSS";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.logging.InstabugLog$a */
    /* loaded from: classes.dex */
    public enum EnumC0706a {
        V("v"),
        D("d"),
        I("i"),
        E("e"),
        W("w"),
        WTF("wtf");


        /* renamed from: g */
        private final String f1094g;

        EnumC0706a(String str) {
            this.f1094g = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.f1094g;
        }
    }

    /* renamed from: v */
    public static void m1548v(String str) {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("logMessage").setType(String.class));
        if (!isInstabugLogsDisabled()) {
            String trimString = StringUtility.trimString(str);
            printInstabugLogs(2, trimString);
            addLog(new C0707b().m1552a(trimString).m1551a(EnumC0706a.V).m1550a(getDate()));
        }
    }

    /* renamed from: d */
    public static void m1545d(String str) {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("logMessage").setType(String.class));
        if (!isInstabugLogsDisabled()) {
            String trimString = StringUtility.trimString(str);
            printInstabugLogs(3, trimString);
            addLog(new C0707b().m1552a(trimString).m1551a(EnumC0706a.D).m1550a(getDate()));
        }
    }

    /* renamed from: i */
    public static void m1547i(String str) {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("logMessage").setType(String.class));
        if (!isInstabugLogsDisabled()) {
            String trimString = StringUtility.trimString(str);
            printInstabugLogs(4, trimString);
            addLog(new C0707b().m1552a(trimString).m1551a(EnumC0706a.I).m1550a(getDate()));
        }
    }

    /* renamed from: e */
    public static void m1546e(String str) {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("logMessage").setType(String.class));
        if (!isInstabugLogsDisabled()) {
            String trimString = StringUtility.trimString(str);
            printInstabugLogs(6, trimString);
            addLog(new C0707b().m1552a(trimString).m1551a(EnumC0706a.E).m1550a(getDate()));
        }
    }

    /* renamed from: w */
    public static void m1549w(String str) {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("logMessage").setType(String.class));
        if (!isInstabugLogsDisabled()) {
            String trimString = StringUtility.trimString(str);
            printInstabugLogs(5, trimString);
            addLog(new C0707b().m1552a(trimString).m1551a(EnumC0706a.W).m1550a(getDate()));
        }
    }

    public static void wtf(String str) {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("logMessage").setType(String.class));
        if (!isInstabugLogsDisabled()) {
            String trimString = StringUtility.trimString(str);
            printInstabugLogs(5, trimString);
            addLog(new C0707b().m1552a(trimString).m1551a(EnumC0706a.WTF).m1550a(getDate()));
        }
    }

    public static void clearLogs() {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter[0]);
        clearLogMessages();
    }

    public static void trimLogs() {
        C0709a.m1562c();
    }

    @Deprecated
    public static String getLogs(Context context) {
        AnalyticsObserver.getInstance().catchDeprecatedLoggingApiUsage(new Api.Parameter[0]);
        return getLogs();
    }

    public static String getLogs() {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter[0]);
        return getLogMessages();
    }

    private static synchronized void addLog(C0707b c0707b) {
        synchronized (InstabugLog.class) {
            C0709a.m1558a(c0707b);
        }
    }

    private static void clearLogMessages() {
        C0709a.m1560b();
    }

    private static String getLogMessages() {
        return C0709a.m1557a().toString();
    }

    private static long getDate() {
        return InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds();
    }

    private static void printInstabugLogs(int i, String str) {
        if (SettingsManager.getInstance().isDebugEnabled()) {
            Log.println(i, INSTABUG_LOG_TAG, str);
        }
    }

    private static boolean isInstabugLogsDisabled() {
        return C0629b.m1160a().m1170b(Feature.INSTABUG_LOGS) == Feature.State.DISABLED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.logging.InstabugLog$b */
    /* loaded from: classes.dex */
    public static class C0707b {

        /* renamed from: a */
        private String f1095a;

        /* renamed from: b */
        private EnumC0706a f1096b;

        /* renamed from: c */
        private long f1097c;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: a */
        public String m1553a() {
            return this.f1095a;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: a */
        public C0707b m1552a(String str) {
            this.f1095a = str;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: b */
        public EnumC0706a m1554b() {
            return this.f1096b;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: a */
        public C0707b m1551a(EnumC0706a enumC0706a) {
            this.f1096b = enumC0706a;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: c */
        public long m1555c() {
            return this.f1097c;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: a */
        public C0707b m1550a(long j) {
            this.f1097c = j;
            return this;
        }

        /* renamed from: d */
        public JSONObject m1556d() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("log_message", m1553a());
                jSONObject.put("log_message_level", m1554b().toString());
                jSONObject.put("log_message_date", m1555c());
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(InstabugLog.class.getSimpleName(), e.getMessage(), e);
            }
            return jSONObject;
        }
    }
}
