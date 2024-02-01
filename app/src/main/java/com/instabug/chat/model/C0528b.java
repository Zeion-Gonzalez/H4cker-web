package com.instabug.chat.model;

import com.instabug.library.internal.storage.cache.Cacheable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: MessageAction.java */
/* renamed from: com.instabug.chat.model.b */
/* loaded from: classes.dex */
public class C0528b implements Cacheable {

    /* renamed from: a */
    private a f379a;

    /* renamed from: b */
    private String f380b;

    /* renamed from: c */
    private String f381c;

    /* renamed from: a */
    public static ArrayList<C0528b> m671a(JSONArray jSONArray) throws JSONException {
        ArrayList<C0528b> arrayList = new ArrayList<>();
        for (int i = 0; i < jSONArray.length(); i++) {
            C0528b c0528b = new C0528b();
            c0528b.fromJson(jSONArray.getString(i));
            arrayList.add(c0528b);
        }
        return arrayList;
    }

    /* renamed from: a */
    public static JSONArray m672a(ArrayList<C0528b> arrayList) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < arrayList.size()) {
                jSONArray.put(arrayList.get(i2).toJson());
                i = i2 + 1;
            } else {
                return jSONArray;
            }
        }
    }

    /* renamed from: a */
    public a m673a() {
        return this.f379a;
    }

    /* renamed from: a */
    public void m674a(a aVar) {
        this.f379a = aVar;
    }

    /* renamed from: b */
    public String m676b() {
        return this.f380b;
    }

    /* renamed from: a */
    public void m675a(String str) {
        this.f380b = str;
    }

    /* renamed from: c */
    public String m678c() {
        return this.f381c;
    }

    /* renamed from: b */
    public void m677b(String str) {
        this.f381c = str;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("url")) {
            m677b(jSONObject.getString("url"));
        }
        if (jSONObject.has("title")) {
            m675a(jSONObject.getString("title"));
        }
        if (jSONObject.has("type")) {
            String string = jSONObject.getString("type");
            char c = 65535;
            switch (string.hashCode()) {
                case -1377687758:
                    if (string.equals("button")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    m674a(a.BUTTON);
                    return;
                default:
                    m674a(a.NOT_AVAILABLE);
                    return;
            }
        }
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("type", this.f379a.toString());
        jSONObject.put("title", this.f380b);
        jSONObject.put("url", this.f381c);
        return jSONObject.toString();
    }

    public String toString() {
        return "Type: " + m673a() + ", title: " + m676b() + ", url: " + m678c();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof C0528b)) {
            return false;
        }
        C0528b c0528b = (C0528b) obj;
        return String.valueOf(c0528b.m676b()).equals(String.valueOf(m676b())) && String.valueOf(c0528b.m678c()).equals(String.valueOf(m678c())) && c0528b.m673a() == m673a();
    }

    public int hashCode() {
        if (m676b() == null || m678c() == null || m673a() == null) {
            return -1;
        }
        return (String.valueOf(m676b().hashCode()) + String.valueOf(m678c().hashCode()) + String.valueOf(m673a().toString().hashCode())).hashCode();
    }

    /* compiled from: MessageAction.java */
    /* renamed from: com.instabug.chat.model.b$a */
    /* loaded from: classes.dex */
    public enum a {
        BUTTON("button"),
        NOT_AVAILABLE("not-available");


        /* renamed from: c */
        private final String f385c;

        a(String str) {
            this.f385c = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.f385c;
        }
    }
}
