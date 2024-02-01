package com.instabug.library.analytics.util;

import android.content.ContentValues;
import android.database.Cursor;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.internal.storage.cache.p028a.C0672a;
import com.instabug.library.internal.storage.cache.p028a.C0674c;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONException;

/* compiled from: AnalyticsDatabaseHelper.java */
/* renamed from: com.instabug.library.analytics.util.a */
/* loaded from: classes.dex */
public class C0583a {
    /* renamed from: a */
    public static void m1017a() throws JSONException {
        C0674c m1311b = C0672a.m1309a().m1311b();
        m1311b.m1320a("sdk_event", (String) null, (String[]) null);
        m1311b.m1321b();
    }

    /* renamed from: b */
    public static ArrayList<Api> m1019b() throws JSONException {
        ArrayList<Api> arrayList = new ArrayList<>();
        C0674c m1311b = C0672a.m1309a().m1311b();
        Cursor m1316a = m1311b.m1316a("sdk_api", new String[]{"time_stamp", "api_name", "is_deprecated", "count", "parameters"}, null, null, null, null, null);
        if (m1316a.getCount() > 0) {
            m1316a.moveToFirst();
            do {
                Api api = new Api();
                int columnIndex = m1316a.getColumnIndex("api_name");
                int columnIndex2 = m1316a.getColumnIndex("time_stamp");
                int columnIndex3 = m1316a.getColumnIndex("is_deprecated");
                int columnIndex4 = m1316a.getColumnIndex("count");
                int columnIndex5 = m1316a.getColumnIndex("parameters");
                api.setApiName(m1316a.getString(columnIndex));
                api.setTimeStamp(Long.parseLong(m1316a.getString(columnIndex2)));
                api.setDeprecated(Boolean.parseBoolean(m1316a.getString(columnIndex3)));
                api.setCount(m1316a.getInt(columnIndex4));
                api.setParameters(Api.Parameter.fromJson(new JSONArray(m1316a.getString(columnIndex5))));
                arrayList.add(api);
            } while (m1316a.moveToNext());
        }
        m1316a.close();
        m1311b.m1321b();
        return arrayList;
    }

    /* renamed from: c */
    public static void m1020c() throws JSONException {
        C0674c m1311b = C0672a.m1309a().m1311b();
        m1311b.m1320a("sdk_api", (String) null, (String[]) null);
        m1311b.m1321b();
    }

    /* renamed from: a */
    public static void m1018a(Collection<Api> collection, long j) {
        C0674c c0674c = null;
        try {
            try {
                c0674c = C0672a.m1309a().m1311b();
                ContentValues contentValues = new ContentValues();
                for (Api api : collection) {
                    contentValues.put("time_stamp", Long.valueOf(j));
                    contentValues.put("api_name", api.getApiName());
                    contentValues.put("is_deprecated", Boolean.toString(api.isDeprecated()));
                    contentValues.put("count", Integer.valueOf(api.getCount()));
                    if (api.getParameters() != null) {
                        contentValues.put("parameters", Api.Parameter.toJson(api.getParameters()).toString());
                    } else {
                        contentValues.put("parameters", Api.Parameter.toJson(new ArrayList()).toString());
                    }
                    c0674c.m1314a("sdk_api", (String) null, contentValues);
                    contentValues.clear();
                }
                if (c0674c != null) {
                    c0674c.m1321b();
                }
            } catch (Exception e) {
                InstabugSDKLogger.m1800e(C0583a.class, e.toString());
                if (c0674c != null) {
                    c0674c.m1321b();
                }
            }
        } catch (Throwable th) {
            if (c0674c != null) {
                c0674c.m1321b();
            }
            throw th;
        }
    }
}
