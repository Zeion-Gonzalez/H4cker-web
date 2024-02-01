package com.instabug.library.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Patterns;
import com.instabug.library.Instabug;
import com.instabug.library.core.eventbus.coreeventbus.C0640a;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.core.plugin.C0642a;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.logging.InstabugUserEventLogger;
import com.instabug.library.network.Request;
import com.instabug.library.network.p030a.C0725c;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.C0754b;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.UUID;
import org.json.JSONException;

/* compiled from: UserManager.java */
/* renamed from: com.instabug.library.user.a */
/* loaded from: classes.dex */
public class C0750a {
    private C0750a() {
    }

    /* renamed from: a */
    public static void m1780a(final Context context) {
        final String mD5Uuid = SettingsManager.getInstance().getMD5Uuid();
        if (m1795i() && m1794h()) {
            SettingsManager.getInstance().setShouldMakeUUIDMigrationRequest(true);
            try {
                C0725c.m1626a().m1627a(context, SettingsManager.getInstance().getUuid(), mD5Uuid, new Request.Callbacks<String, Throwable>() { // from class: com.instabug.library.user.a.1
                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onSucceeded(String str) {
                        C0750a.m1793g();
                        C0750a.m1786b(mD5Uuid, context);
                        SettingsManager.getInstance().setShouldMakeUUIDMigrationRequest(false);
                    }

                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onFailed(Throwable th) {
                        SettingsManager.getInstance().setShouldMakeUUIDMigrationRequest(true);
                    }
                });
                return;
            } catch (IOException | JSONException e) {
                InstabugSDKLogger.m1801e(C0750a.class, "Something went wrong while do UUID migration request", e);
                return;
            }
        }
        m1793g();
        m1786b(mD5Uuid, context);
    }

    /* renamed from: a */
    public static void m1781a(Context context, String str, String str2) {
        if (str == null || str.isEmpty() || str2 == null || str2.isEmpty()) {
            InstabugSDKLogger.m1804w(C0750a.class, "Empty username or email");
            return;
        }
        if (!SettingsManager.getInstance().getIdentifiedUserEmail().equals(str2)) {
            m1785b(str2);
            m1788c(str2);
            m1782a(str);
            SettingsManager.getInstance().setMD5Uuid(m1790d(str2));
            m1780a(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: g */
    public static void m1793g() {
        SettingsManager.getInstance().setLastContactedAt(0L);
        CacheManager.getInstance().invalidateAllCaches();
        InstabugUserEventLogger.getInstance().clearAll();
        InstabugUserEventLogger.getInstance().clearLoggingData();
    }

    /* renamed from: a */
    public static void m1779a() {
        if (!m1784b().isEmpty() && !m1787c().isEmpty()) {
            C0640a.m1238a(new SDKCoreEvent(SDKCoreEvent.User.TYPE_USER, SDKCoreEvent.User.VALUE_LOGGED_OUT));
            SettingsManager.getInstance().setUserLoggedOut(true);
            SettingsManager.getInstance().setUuid(null);
            SettingsManager.getInstance().setMD5Uuid(null);
            SettingsManager.getInstance().setUsername("");
            m1785b("");
            m1788c("");
            SettingsManager.getInstance().setShouldMakeUUIDMigrationRequest(false);
            m1793g();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public static void m1786b(String str, Context context) {
        C0640a.m1238a(new SDKCoreEvent(SDKCoreEvent.User.TYPE_USER, SDKCoreEvent.User.VALUE_LOGGED_IN));
        SettingsManager.getInstance().setUserLoggedOut(false);
        SettingsManager.getInstance().setMD5Uuid(str);
        CacheManager.getInstance().invalidateAllCaches();
    }

    /* renamed from: h */
    private static boolean m1794h() {
        return SettingsManager.getInstance().isUserLoggedOut();
    }

    @NonNull
    /* renamed from: b */
    public static String m1784b() throws IllegalStateException {
        String identifiedUserEmail = SettingsManager.getInstance().getIdentifiedUserEmail();
        if (identifiedUserEmail.isEmpty()) {
            return SettingsManager.getInstance().getUserEmail();
        }
        return identifiedUserEmail;
    }

    /* renamed from: a */
    public static void m1782a(@NonNull String str) {
        SettingsManager.getInstance().setUsername(str);
    }

    /* renamed from: c */
    public static String m1787c() {
        return SettingsManager.getInstance().getUsername();
    }

    /* renamed from: b */
    public static void m1785b(@NonNull String str) {
        SettingsManager.getInstance().setUserEmail(str);
        if ("".equals(str)) {
            InstabugSDKLogger.m1799d(C0750a.class, "Email set to empty string, enabling user input of email");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
            InstabugSDKLogger.m1804w(C0750a.class, "Invalid email " + str + " passed to setUserEmail, ignoring.");
        }
    }

    /* renamed from: c */
    private static void m1788c(@NonNull String str) {
        SettingsManager.getInstance().setIdentifiedUserEmail(str);
        if ("".equals(str)) {
            InstabugSDKLogger.m1799d(C0750a.class, "Email set to empty string, enabling user input of email");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
            InstabugSDKLogger.m1804w(C0750a.class, "Invalid email " + str + " passed to setIdentifiedUserEmail, ignoring.");
        }
    }

    /* renamed from: d */
    private static String m1790d(@NonNull String str) {
        return C0754b.m1807a(str + SettingsManager.getInstance().getAppToken());
    }

    /* renamed from: i */
    private static boolean m1795i() {
        return C0642a.m1244b() != 0;
    }

    /* renamed from: d */
    public static String m1789d() {
        String mD5Uuid = SettingsManager.getInstance().getMD5Uuid();
        if (mD5Uuid == null || mD5Uuid.isEmpty()) {
            String uuid = SettingsManager.getInstance().getUuid();
            if (uuid == null || uuid.isEmpty()) {
                String uuid2 = UUID.randomUUID().toString();
                SettingsManager.getInstance().setUuid(uuid2);
                return uuid2;
            }
            return uuid;
        }
        return mD5Uuid;
    }

    /* renamed from: e */
    public static void m1791e() {
        if (SettingsManager.getInstance().shouldMakeUUIDMigrationRequest()) {
            m1780a(Instabug.getApplicationContext());
        }
    }
}
