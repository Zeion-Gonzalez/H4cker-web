package com.instabug.library.analytics;

import android.content.Context;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.analytics.util.C0583a;
import com.instabug.library.analytics.util.C0584b;
import com.instabug.library.core.eventbus.SessionStateEventBus;
import com.instabug.library.model.Session;
import com.instabug.library.settings.SettingsManager;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import p045rx.functions.Action1;

@SuppressFBWarnings({"URF_UNREAD_FIELD", "DLS_DEAD_LOCAL_STORE", "DLS_DEAD_LOCAL_STORE"})
/* loaded from: classes.dex */
public class AnalyticsObserver {
    private static AnalyticsObserver INSTANCE = null;
    private static final String LAST_UPLOADED_AT = "analytics_last_uploaded";
    private Action1<Session.SessionState> sessionStateChangedAction = new Action1<Session.SessionState>() { // from class: com.instabug.library.analytics.AnalyticsObserver.1
        @Override // p045rx.functions.Action1
        /* renamed from: a  reason: merged with bridge method [inline-methods] */
        public void call(Session.SessionState sessionState) {
            AnalyticsObserver.this.handleAPIsUsageWithSessionStateChanged(sessionState);
            C0584b.m1021a();
        }
    };
    private LinkedHashMap<String, Api> loggingApisLinkedHashMap = new LinkedHashMap<>();
    private List<Api> sdkApisArrayList = Collections.synchronizedList(new ArrayList());

    public static AnalyticsObserver getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AnalyticsObserver();
        }
        return INSTANCE;
    }

    private AnalyticsObserver() {
        SessionStateEventBus.getInstance().subscribe(this.sessionStateChangedAction);
    }

    public void catchApiUsage(Api.Parameter... parameterArr) {
        catchApiUsage(getCallerMethodName(), false, parameterArr);
    }

    public void catchDeprecatedApiUsage(Api.Parameter... parameterArr) {
        catchApiUsage(getCallerMethodName(), true, parameterArr);
    }

    public void catchLoggingApiUsage(Api.Parameter... parameterArr) {
        catchLoggingApiUsage(getCallerMethodName(), false, parameterArr);
    }

    public void catchDeprecatedLoggingApiUsage(Api.Parameter... parameterArr) {
        catchLoggingApiUsage(getCallerMethodName(), true, parameterArr);
    }

    private void catchApiUsage(String str, boolean z, Api.Parameter... parameterArr) {
        this.sdkApisArrayList.add(createApiUsageInfo(str, z, parameterArr));
    }

    private void catchLoggingApiUsage(String str, boolean z, Api.Parameter... parameterArr) {
        if (this.loggingApisLinkedHashMap.containsKey(str)) {
            Api api = this.loggingApisLinkedHashMap.get(str);
            api.incrementCount();
            this.loggingApisLinkedHashMap.put(str, api);
            return;
        }
        this.loggingApisLinkedHashMap.put(str, createApiUsageInfo(str, z, parameterArr));
    }

    private Api createApiUsageInfo(String str, boolean z, Api.Parameter... parameterArr) {
        ArrayList<Api.Parameter> arrayList;
        Api api = new Api();
        api.setApiName(str);
        api.setDeprecated(z);
        if (parameterArr != null) {
            arrayList = new ArrayList<>(Arrays.asList(parameterArr));
        } else {
            arrayList = new ArrayList<>();
        }
        api.setParameters(arrayList);
        return api;
    }

    private String getCallerMethodName() {
        return Thread.currentThread().getStackTrace()[4].getMethodName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAPIsUsageWithSessionStateChanged(Session.SessionState sessionState) {
        long sessionStartedAt = SettingsManager.getInstance().getSessionStartedAt();
        if (sessionState == Session.SessionState.FINISH) {
            C0583a.m1018a(this.sdkApisArrayList, sessionStartedAt);
            C0583a.m1018a(this.loggingApisLinkedHashMap.values(), sessionStartedAt);
            this.sdkApisArrayList.clear();
            this.loggingApisLinkedHashMap.clear();
        }
    }

    public static long getLastUploadedAt(Context context) {
        return context.getSharedPreferences(SettingsManager.INSTABUG_SHARED_PREF_NAME, 0).getLong(LAST_UPLOADED_AT, 0L);
    }

    public static void setLastUploadedAt(long j, Context context) {
        context.getSharedPreferences(SettingsManager.INSTABUG_SHARED_PREF_NAME, 0).edit().putLong(LAST_UPLOADED_AT, j).apply();
    }
}
