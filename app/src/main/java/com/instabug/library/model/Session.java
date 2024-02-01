package com.instabug.library.model;

import com.instabug.library.internal.storage.cache.Cacheable;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Session implements Cacheable {

    /* renamed from: a */
    private int f1112a;

    /* renamed from: b */
    private long f1113b;

    /* renamed from: c */
    private long f1114c;

    /* renamed from: d */
    private String f1115d;

    /* renamed from: e */
    private String f1116e;

    /* loaded from: classes.dex */
    public enum SessionState {
        START,
        RESUME,
        FINISH
    }

    public Session() {
    }

    public Session(int i, long j, long j2, String str, String str2) {
        this.f1112a = i;
        this.f1113b = j;
        this.f1114c = j2;
        this.f1115d = str;
        this.f1116e = str2;
    }

    /* renamed from: a */
    public int m1579a() {
        return this.f1112a;
    }

    /* renamed from: b */
    public long m1580b() {
        return this.f1113b;
    }

    /* renamed from: c */
    public long m1581c() {
        return this.f1114c;
    }

    /* renamed from: d */
    public String m1582d() {
        return this.f1115d;
    }

    /* renamed from: e */
    public String m1583e() {
        return this.f1116e;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", m1579a()).put("started_at", m1580b()).put(State.KEY_DURATION, m1581c()).put(State.KEY_USER_EVENTS, m1582d()).put(State.KEY_USER_ATTRIBUTES, m1583e());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("id")) {
            this.f1112a = jSONObject.getInt("id");
        }
        if (jSONObject.has("started_at")) {
            this.f1113b = jSONObject.getLong("started_at");
        }
        if (jSONObject.has(State.KEY_DURATION)) {
            this.f1114c = jSONObject.getLong(State.KEY_DURATION);
        }
        if (jSONObject.has(State.KEY_USER_EVENTS)) {
            this.f1115d = jSONObject.getString(State.KEY_USER_EVENTS);
        }
        if (jSONObject.has(State.KEY_USER_ATTRIBUTES)) {
            this.f1116e = jSONObject.getString(State.KEY_USER_ATTRIBUTES);
        }
    }

    public String toString() {
        return "id: " + m1579a() + ", startedAt: " + this.f1113b + ", duration: " + this.f1114c;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Session)) {
            return false;
        }
        Session session = (Session) obj;
        return session.m1579a() == m1579a() && session.m1580b() == m1580b() && session.m1581c() == m1581c() && String.valueOf(session.m1582d()).equals(String.valueOf(m1582d())) && String.valueOf(session.m1583e()).equals(String.valueOf(m1583e()));
    }

    public int hashCode() {
        return m1579a();
    }
}
