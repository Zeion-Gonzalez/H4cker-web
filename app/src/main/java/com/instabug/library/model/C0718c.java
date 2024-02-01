package com.instabug.library.model;

import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.model.C0719d;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.StringUtility;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: UserStep.java */
/* renamed from: com.instabug.library.model.c */
/* loaded from: classes.dex */
public class C0718c implements Cacheable, Serializable {

    /* renamed from: a */
    private long f1121a;

    /* renamed from: b */
    private String f1122b;

    /* renamed from: c */
    private a f1123c;

    /* renamed from: a */
    public static ArrayList<C0718c> m1596a(JSONArray jSONArray) throws JSONException {
        ArrayList<C0718c> arrayList = new ArrayList<>();
        if (jSONArray != null && jSONArray.length() > 0) {
            for (int i = 0; i < jSONArray.length(); i++) {
                C0718c c0718c = new C0718c();
                c0718c.fromJson(jSONArray.getJSONObject(i).toString());
                arrayList.add(c0718c);
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    public static JSONArray m1597a(ArrayList<C0718c> arrayList) {
        JSONArray jSONArray = new JSONArray();
        if (arrayList != null && arrayList.size() > 0) {
            Iterator<C0718c> it = arrayList.iterator();
            while (it.hasNext()) {
                try {
                    jSONArray.put(new JSONObject(it.next().toJson()));
                } catch (JSONException e) {
                    InstabugSDKLogger.m1803v(C0718c.class, e.toString());
                }
            }
        }
        return jSONArray;
    }

    /* renamed from: a */
    public long m1598a() {
        return this.f1121a;
    }

    /* renamed from: a */
    public void m1599a(long j) {
        this.f1121a = j;
    }

    /* renamed from: b */
    public String m1603b() {
        return this.f1122b;
    }

    /* renamed from: a */
    public void m1602a(String str) {
        this.f1122b = str;
    }

    /* renamed from: c */
    public a m1604c() {
        return this.f1123c;
    }

    /* renamed from: a */
    public void m1600a(a aVar) {
        this.f1123c = aVar;
    }

    /* renamed from: a */
    public void m1601a(C0719d.a aVar) {
        switch (aVar) {
            case TAP:
                m1600a(a.TOUCH);
                return;
            case SHAKE:
                m1600a(a.MOTION);
                return;
            case APPLICATION_CREATED:
                m1600a(a.APPLICATION);
                return;
            default:
                m1600a(a.VIEW);
                return;
        }
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("timestamp", m1598a());
        jSONObject.put("message", m1603b());
        jSONObject.put("type", m1604c().toString());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("timestamp")) {
            if (StringUtility.isNumeric(jSONObject.getString("timestamp"))) {
                m1599a(jSONObject.getLong("timestamp"));
            } else {
                try {
                    m1599a(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(jSONObject.getString("timestamp")).getTime());
                } catch (ParseException e) {
                    InstabugSDKLogger.m1800e(C0718c.class, e.getMessage());
                }
            }
        }
        if (jSONObject.has("message")) {
            m1602a(jSONObject.getString("message"));
        }
        if (jSONObject.has("type")) {
            String string = jSONObject.getString("type");
            char c = 65535;
            switch (string.hashCode()) {
                case -1068318794:
                    if (string.equals("motion")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3619493:
                    if (string.equals("view")) {
                        c = 1;
                        break;
                    }
                    break;
                case 110550847:
                    if (string.equals("touch")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1554253136:
                    if (string.equals("application")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    m1600a(a.APPLICATION);
                    return;
                case 1:
                    m1600a(a.VIEW);
                    return;
                case 2:
                    m1600a(a.MOTION);
                    return;
                case 3:
                    m1600a(a.TOUCH);
                    return;
                default:
                    m1600a(a.NOT_AVAILABLE);
                    return;
            }
        }
    }

    public String toString() {
        return "UserStep{timeStamp='" + this.f1121a + "', message='" + this.f1122b + "', type=" + this.f1123c + '}';
    }

    /* compiled from: UserStep.java */
    /* renamed from: com.instabug.library.model.c$a */
    /* loaded from: classes.dex */
    public enum a {
        APPLICATION("application"),
        VIEW("view"),
        MOTION("motion"),
        TOUCH("touch"),
        NOT_AVAILABLE("not_available");


        /* renamed from: f */
        private final String f1131f;

        a(String str) {
            this.f1131f = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.f1131f;
        }
    }
}
