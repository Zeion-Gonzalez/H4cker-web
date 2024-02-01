package com.instabug.survey;

import android.support.annotation.NonNull;
import com.instabug.library.Instabug;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.p033b.C0772c;

/* loaded from: classes.dex */
public class InstabugSurvey {
    public static boolean showValidSurvey() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0766a.m1901a(Instabug.getApplicationContext()).m1916b();
    }

    public static boolean hasValidSurveys() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0766a.m1901a(Instabug.getApplicationContext()).m1918c();
    }

    public static void setPreShowingSurveyRunnable(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("preShowingSurveyRunnable").setType(Runnable.class));
        C0772c.m1981a(runnable);
    }

    public static void setAfterShowingSurveyRunnable(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("afterShowingSurveyRunnable").setType(Runnable.class));
        C0772c.m1985b(runnable);
    }

    public static void setSurveysAutoShowing(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("isSurveysAutoShowing").setType(Boolean.class).setValue(Boolean.valueOf(z)));
        C0772c.m1982a(z);
    }

    public static boolean showSurvey(@NonNull String str) {
        if (str == null || String.valueOf(str).equals("null")) {
            InstabugSDKLogger.m1802i(InstabugSurvey.class.getName(), "Optin survey token is NULL");
            return false;
        }
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("showSurvey").setType(String.class).setValue(str));
        return C0766a.m1901a(Instabug.getApplicationContext()).m1915a(str);
    }

    public static boolean hasRespondToSurvey(@NonNull String str) {
        if (str == null) {
            InstabugSDKLogger.m1802i(InstabugSurvey.class.getName(), "Optin survey token is NULL");
            return false;
        }
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("hasRespondToSurvey").setType(String.class).setValue(str));
        return C0766a.m1901a(Instabug.getApplicationContext()).m1917b(str);
    }
}
