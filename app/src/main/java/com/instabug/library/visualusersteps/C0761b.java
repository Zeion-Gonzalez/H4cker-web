package com.instabug.library.visualusersteps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: VisualUserStep.java */
/* renamed from: com.instabug.library.visualusersteps.b */
/* loaded from: classes.dex */
public class C0761b implements Serializable {

    /* renamed from: a */
    private String f1279a;

    /* renamed from: b */
    private String f1280b;

    /* renamed from: c */
    private String f1281c;

    /* renamed from: d */
    private String f1282d;

    /* renamed from: e */
    private String f1283e;

    /* renamed from: f */
    private long f1284f;

    /* renamed from: g */
    private String f1285g;

    /* renamed from: h */
    private String f1286h;

    private C0761b(a aVar) {
        m1840b(aVar.f1287a);
        m1835g(aVar.f1288b);
        m1842c(aVar.f1289c);
        m1844d(aVar.f1290d);
        m1836h(aVar.f1291e);
        m1838a(aVar.f1292f);
        m1846e(aVar.f1293g);
        m1848f(aVar.f1294h);
    }

    /* renamed from: a */
    public static a m1831a(String str) {
        return new a(str);
    }

    /* renamed from: a */
    public static ArrayList<C0761b> m1834a(JSONArray jSONArray) throws JSONException {
        ArrayList<C0761b> arrayList = new ArrayList<>();
        if (jSONArray != null && jSONArray.length() > 0) {
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(m1832a(jSONArray.getJSONObject(i)));
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    public static C0761b m1832a(JSONObject jSONObject) throws JSONException {
        String str = null;
        String str2 = "unknown";
        if (jSONObject.has("event_type")) {
            str2 = jSONObject.getString("event_type");
        }
        String string = jSONObject.has("screen_name") ? jSONObject.getString("screen_name") : null;
        String string2 = jSONObject.has("screen_identifier") ? jSONObject.getString("screen_identifier") : null;
        String string3 = jSONObject.has("screenshot_identifier") ? jSONObject.getString("screenshot_identifier") : null;
        String string4 = jSONObject.has("date") ? jSONObject.getString("date") : null;
        String string5 = jSONObject.has("parent_screen_identifier") ? jSONObject.getString("parent_screen_identifier") : null;
        String string6 = jSONObject.has("view") ? jSONObject.getString("view") : null;
        if (jSONObject.has(com.instabug.library.model.State.KEY_ORIENTATION)) {
            String string7 = jSONObject.getString(com.instabug.library.model.State.KEY_ORIENTATION);
            char c = 65535;
            switch (string7.hashCode()) {
                case 729267099:
                    if (string7.equals("portrait")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1430647483:
                    if (string7.equals("landscape")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    str = "landscape";
                    break;
                case 1:
                    str = "portrait";
                    break;
            }
        }
        return m1831a(str2).m1863b(string).m1864c(string3).m1860a(Long.parseLong(string4)).m1861a(string5).m1866e(string6).m1867f(str).m1865d(string2).m1862a();
    }

    /* renamed from: a */
    public static String m1833a(ArrayList<C0761b> arrayList) {
        JSONArray jSONArray = new JSONArray();
        if (arrayList != null && arrayList.size() > 0) {
            Iterator<C0761b> it = arrayList.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().m1837a());
            }
        }
        return jSONArray.toString();
    }

    /* renamed from: a */
    public JSONObject m1837a() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("parent_screen_identifier", (m1839b() == null || m1839b().equals("null")) ? JSONObject.NULL : m1839b());
            jSONObject.put("screen_name", (m1841c() == null || m1841c().equals("null")) ? JSONObject.NULL : m1841c());
            jSONObject.put("screenshot_identifier", (m1843d() == null || m1843d().equals("null")) ? JSONObject.NULL : m1843d());
            jSONObject.put("screen_identifier", (m1845e() == null || m1845e().equals("null")) ? JSONObject.NULL : m1845e());
            jSONObject.put("event_type", (m1847f() == null || m1847f().equals("null")) ? JSONObject.NULL : m1847f());
            jSONObject.put("date", m1849g());
            jSONObject.put("view", (m1850h() == null || m1850h().equals("null")) ? JSONObject.NULL : m1850h());
            jSONObject.put(com.instabug.library.model.State.KEY_ORIENTATION, (m1851i() == null || m1851i().equals("null")) ? JSONObject.NULL : m1851i());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    /* renamed from: b */
    public String m1839b() {
        return this.f1279a;
    }

    /* renamed from: b */
    public void m1840b(String str) {
        this.f1279a = str;
    }

    /* renamed from: c */
    public String m1841c() {
        return this.f1280b;
    }

    /* renamed from: g */
    private void m1835g(String str) {
        this.f1280b = str;
    }

    /* renamed from: d */
    public String m1843d() {
        return this.f1281c;
    }

    /* renamed from: c */
    public void m1842c(String str) {
        this.f1281c = str;
    }

    /* renamed from: e */
    public String m1845e() {
        return this.f1282d;
    }

    /* renamed from: d */
    public void m1844d(String str) {
        this.f1282d = str;
    }

    /* renamed from: f */
    public String m1847f() {
        return this.f1283e;
    }

    /* renamed from: h */
    private void m1836h(String str) {
        this.f1283e = str;
    }

    /* renamed from: g */
    public long m1849g() {
        return this.f1284f;
    }

    /* renamed from: a */
    public void m1838a(long j) {
        this.f1284f = j;
    }

    /* renamed from: h */
    public String m1850h() {
        return this.f1285g;
    }

    /* renamed from: e */
    public void m1846e(String str) {
        this.f1285g = str;
    }

    /* renamed from: i */
    public String m1851i() {
        return this.f1286h;
    }

    /* renamed from: f */
    public void m1848f(String str) {
        this.f1286h = str;
    }

    public String toString() {
        return "VisualUserStep{parentScreenId='" + this.f1279a + "', screenName='" + this.f1280b + "', screenshotId='" + this.f1281c + "', screenId='" + this.f1282d + "', eventType='" + this.f1283e + "', date=" + this.f1284f + ", view='" + this.f1285g + "'}";
    }

    /* compiled from: VisualUserStep.java */
    /* renamed from: com.instabug.library.visualusersteps.b$a */
    /* loaded from: classes.dex */
    public static final class a {

        /* renamed from: a */
        private String f1287a;

        /* renamed from: b */
        private String f1288b;

        /* renamed from: c */
        private String f1289c;

        /* renamed from: d */
        private String f1290d;

        /* renamed from: e */
        private String f1291e;

        /* renamed from: f */
        private long f1292f;

        /* renamed from: g */
        private String f1293g;

        /* renamed from: h */
        private String f1294h;

        private a(String str) {
            this.f1292f = System.currentTimeMillis();
            this.f1291e = str;
        }

        /* renamed from: a */
        public a m1861a(String str) {
            this.f1287a = str;
            return this;
        }

        /* renamed from: b */
        public a m1863b(String str) {
            this.f1288b = str;
            return this;
        }

        /* renamed from: c */
        public a m1864c(String str) {
            this.f1289c = str;
            return this;
        }

        /* renamed from: d */
        public a m1865d(String str) {
            this.f1290d = str;
            return this;
        }

        /* renamed from: a */
        public a m1860a(long j) {
            this.f1292f = j;
            return this;
        }

        /* renamed from: e */
        public a m1866e(String str) {
            this.f1293g = str;
            return this;
        }

        /* renamed from: f */
        public a m1867f(String str) {
            this.f1294h = str;
            return this;
        }

        /* renamed from: a */
        public C0761b m1862a() {
            return new C0761b(this);
        }
    }
}
