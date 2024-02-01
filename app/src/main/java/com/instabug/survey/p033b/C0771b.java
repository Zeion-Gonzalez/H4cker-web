package com.instabug.survey.p033b;

import android.content.Context;
import android.content.SharedPreferences;

/* compiled from: PersistableSettings.java */
/* renamed from: com.instabug.survey.b.b */
/* loaded from: classes.dex */
public class C0771b {

    /* renamed from: c */
    private static C0771b f1354c;

    /* renamed from: a */
    private SharedPreferences f1355a;

    /* renamed from: b */
    private SharedPreferences.Editor f1356b;

    private C0771b(Context context) {
        this.f1355a = context.getSharedPreferences("instabug_survey", 0);
        this.f1356b = this.f1355a.edit();
    }

    /* renamed from: a */
    public static void m1975a(Context context) {
        f1354c = new C0771b(context);
    }

    /* renamed from: a */
    public static C0771b m1974a() {
        return f1354c;
    }

    /* renamed from: a */
    public void m1976a(long j) {
        this.f1356b.putLong("last_survey_time", j);
        this.f1356b.apply();
    }

    /* renamed from: b */
    public long m1977b() {
        return this.f1355a.getLong("last_survey_time", 0L);
    }

    /* renamed from: b */
    public void m1978b(long j) {
        this.f1356b.putLong("survey_last_fetch_time", j);
        this.f1356b.apply();
    }

    /* renamed from: c */
    public long m1979c() {
        return this.f1355a.getLong("survey_last_fetch_time", 0L);
    }
}
