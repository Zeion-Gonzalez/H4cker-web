package com.instabug.chat.model;

import com.instabug.library.internal.storage.cache.Cacheable;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ReadMessage.java */
/* renamed from: com.instabug.chat.model.d */
/* loaded from: classes.dex */
public class C0530d implements Cacheable {

    /* renamed from: a */
    private String f389a;

    /* renamed from: b */
    private long f390b;

    /* renamed from: c */
    private String f391c;

    /* renamed from: a */
    public String m685a() {
        return this.f389a;
    }

    /* renamed from: a */
    public void m687a(String str) {
        this.f389a = str;
    }

    /* renamed from: b */
    public long m688b() {
        return this.f390b;
    }

    /* renamed from: a */
    public void m686a(long j) {
        this.f390b = j;
    }

    /* renamed from: c */
    public String m690c() {
        return this.f391c;
    }

    /* renamed from: b */
    public void m689b(String str) {
        this.f391c = str;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("chat_number", m685a()).put("message_id", m690c()).put("read_at", m688b());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("chat_number")) {
            m687a(jSONObject.getString("chat_number"));
        }
        if (jSONObject.has("message_id")) {
            m689b(jSONObject.getString("message_id"));
        }
        if (jSONObject.has("read_at")) {
            m686a(jSONObject.getLong("read_at"));
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof C0530d)) {
            return false;
        }
        C0530d c0530d = (C0530d) obj;
        return String.valueOf(c0530d.m685a()).equals(String.valueOf(m685a())) && String.valueOf(c0530d.m690c()).equals(String.valueOf(m690c())) && c0530d.m688b() == m688b();
    }

    public int hashCode() {
        return this.f389a.hashCode();
    }
}
