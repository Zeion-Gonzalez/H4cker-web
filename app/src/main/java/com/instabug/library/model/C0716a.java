package com.instabug.library.model;

import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.logging.InstabugLog;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.StringUtility;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ConsoleLog.java */
/* renamed from: com.instabug.library.model.a */
/* loaded from: classes.dex */
public class C0716a implements Cacheable, Serializable {

    /* renamed from: a */
    private String f1118a;

    /* renamed from: b */
    private long f1119b;

    /* renamed from: a */
    public static ArrayList<C0716a> m1584a(JSONArray jSONArray) throws JSONException {
        ArrayList<C0716a> arrayList = new ArrayList<>();
        if (jSONArray != null && jSONArray.length() > 0) {
            for (int i = 0; i < jSONArray.length(); i++) {
                C0716a c0716a = new C0716a();
                c0716a.fromJson(jSONArray.getJSONObject(i).toString());
                arrayList.add(c0716a);
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    public static JSONArray m1585a(ArrayList<C0716a> arrayList) {
        JSONArray jSONArray = new JSONArray();
        if (arrayList != null && arrayList.size() > 0) {
            Iterator<C0716a> it = arrayList.iterator();
            while (it.hasNext()) {
                try {
                    jSONArray.put(new JSONObject(it.next().toJson()));
                } catch (JSONException e) {
                    InstabugSDKLogger.m1803v(C0716a.class, e.toString());
                }
            }
        }
        return jSONArray;
    }

    /* renamed from: a */
    public long m1587a() {
        return this.f1119b;
    }

    /* renamed from: a */
    public void m1588a(long j) {
        this.f1119b = j;
    }

    /* renamed from: b */
    public String m1590b() {
        return this.f1118a;
    }

    /* renamed from: a */
    public void m1589a(String str) {
        this.f1118a = str;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("timestamp", m1587a());
        jSONObject.put("message", m1590b());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("timestamp")) {
            if (StringUtility.isNumeric(jSONObject.getString("timestamp"))) {
                m1588a(jSONObject.getLong("timestamp"));
            } else {
                m1588a(m1586b(jSONObject.getString("timestamp")));
            }
        }
        if (jSONObject.has("message")) {
            m1589a(jSONObject.getString("message"));
        }
    }

    public String toString() {
        return "ConsoleLog{timeStamp='" + this.f1119b + "', message='" + this.f1118a + "'}";
    }

    /* renamed from: b */
    public static long m1586b(String str) {
        try {
            Date parse = new SimpleDateFormat(InstabugLog.LOG_MESSAGE_DATE_FORMAT, Locale.US).parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(2, calendar.get(2));
            calendar2.set(5, calendar.get(5));
            calendar2.set(11, calendar.get(11));
            calendar2.set(12, calendar.get(12));
            calendar2.set(13, calendar.get(13));
            calendar2.set(14, calendar.get(14));
            return calendar2.getTimeInMillis();
        } catch (ParseException e) {
            InstabugSDKLogger.m1804w(C0716a.class, "parsing error happened when trying to parse Console log message date: " + str + ", error message: " + e.getMessage());
            return 0L;
        }
    }
}
