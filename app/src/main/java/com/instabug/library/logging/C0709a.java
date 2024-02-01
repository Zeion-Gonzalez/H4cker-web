package com.instabug.library.logging;

import android.content.ContentValues;
import com.instabug.library.internal.storage.cache.p028a.C0672a;
import com.instabug.library.internal.storage.cache.p028a.C0674c;
import com.instabug.library.logging.InstabugLog;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.schedulers.Schedulers;
import p045rx.subjects.PublishSubject;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: InstabugLogDbHelper.java */
/* renamed from: com.instabug.library.logging.a */
/* loaded from: classes.dex */
public class C0709a {

    /* renamed from: a */
    private static PublishSubject<List<InstabugLog.C0707b>> f1099a;

    /* renamed from: b */
    private static Subscription f1100b;

    /* renamed from: c */
    private static List<InstabugLog.C0707b> f1101c = new ArrayList();

    C0709a() {
    }

    /* renamed from: e */
    private static void m1564e() {
        f1100b = f1099a.debounce(1L, TimeUnit.SECONDS).observeOn(Schedulers.m2140io()).subscribe((Subscriber<? super List<InstabugLog.C0707b>>) new Subscriber<List<InstabugLog.C0707b>>() { // from class: com.instabug.library.logging.a.1
            @Override // p045rx.Observer
            public void onCompleted() {
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1800e(C0709a.class, "couldn't insert the latest logs");
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(List<InstabugLog.C0707b> list) {
                C0709a.f1101c.clear();
                C0709a.m1561b(list);
            }
        });
    }

    /* renamed from: f */
    private static void m1565f() {
        if (f1100b != null && !f1100b.isUnsubscribed()) {
            f1100b.unsubscribe();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static synchronized void m1558a(InstabugLog.C0707b c0707b) {
        synchronized (C0709a.class) {
            if (f1099a == null) {
                f1099a = PublishSubject.create();
                m1564e();
            } else if (f1100b.isUnsubscribed()) {
                m1564e();
            }
            f1101c.add(c0707b);
            f1099a.onNext(new ArrayList(f1101c));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public static synchronized void m1561b(List<InstabugLog.C0707b> list) {
        synchronized (C0709a.class) {
            C0674c m1311b = C0672a.m1309a().m1311b();
            m1311b.m1322c();
            try {
                for (InstabugLog.C0707b c0707b : list) {
                    if (c0707b != null) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("log_message", c0707b.m1553a());
                        contentValues.put("log_level", c0707b.m1554b().toString());
                        contentValues.put("log_date", String.valueOf(c0707b.m1555c()));
                        m1311b.m1314a("instabug_logs", (String) null, contentValues);
                    }
                }
                m1311b.m1319a("DELETE FROM instabug_logs WHERE log_date IN (SELECT log_date FROM instabug_logs ORDER BY log_date DESC LIMIT -1 OFFSET 1000)");
                m1311b.m1323d();
                m1311b.m1324e();
                m1311b.m1321b();
                m1565f();
            } catch (Throwable th) {
                m1311b.m1324e();
                m1311b.m1321b();
                throw th;
            }
        }
    }

    /* renamed from: g */
    private static void m1566g() {
        m1565f();
        ArrayList arrayList = new ArrayList(f1101c);
        f1101c.clear();
        m1561b(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0083, code lost:
    
        r6.m1550a(new java.text.SimpleDateFormat(com.instabug.library.logging.InstabugLog.LOG_MESSAGE_DATE_FORMAT, java.util.Locale.US).parse(r5).getTime());
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0098, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0099, code lost:
    
        com.instabug.library.util.InstabugSDKLogger.m1800e(com.instabug.library.logging.C0709a.class, r0.getMessage());
     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x001e, code lost:
    
        if (r3.moveToFirst() != false) goto L5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0020, code lost:
    
        r0 = r3.getString(r3.getColumnIndex("log_message"));
        r4 = r3.getString(r3.getColumnIndex("log_level"));
        r5 = r3.getString(r3.getColumnIndex("log_date"));
        r6 = new com.instabug.library.logging.InstabugLog.C0707b();
        r6.m1552a(r0).m1551a(com.instabug.library.logging.InstabugLog.EnumC0706a.valueOf(r4.toUpperCase(java.util.Locale.ENGLISH)));
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0058, code lost:
    
        if (com.instabug.library.util.StringUtility.isNumeric(r5) == false) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x005a, code lost:
    
        r6.m1550a(java.lang.Long.parseLong(r5));
     */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static org.json.JSONArray m1557a() {
        /*
            m1566g()
            org.json.JSONArray r1 = new org.json.JSONArray
            r1.<init>()
            com.instabug.library.internal.storage.cache.a.a r0 = com.instabug.library.internal.storage.cache.p028a.C0672a.m1309a()
            com.instabug.library.internal.storage.cache.a.c r2 = r0.m1311b()
            r2.m1322c()
            java.lang.String r0 = "SELECT * FROM instabug_logs ORDER  BY log_date DESC LIMIT 1000"
            r3 = 0
            android.database.Cursor r3 = r2.m1315a(r0, r3)
            boolean r0 = r3.moveToFirst()     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            if (r0 == 0) goto L6e
        L20:
            java.lang.String r0 = "log_message"
            int r0 = r3.getColumnIndex(r0)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.lang.String r0 = r3.getString(r0)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.lang.String r4 = "log_level"
            int r4 = r3.getColumnIndex(r4)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.lang.String r4 = r3.getString(r4)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.lang.String r5 = "log_date"
            int r5 = r3.getColumnIndex(r5)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.lang.String r5 = r3.getString(r5)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            com.instabug.library.logging.InstabugLog$b r6 = new com.instabug.library.logging.InstabugLog$b     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            r6.<init>()     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            com.instabug.library.logging.InstabugLog$b r0 = r6.m1552a(r0)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.util.Locale r7 = java.util.Locale.ENGLISH     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.lang.String r4 = r4.toUpperCase(r7)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            com.instabug.library.logging.InstabugLog$a r4 = com.instabug.library.logging.InstabugLog.EnumC0706a.valueOf(r4)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            r0.m1551a(r4)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            boolean r0 = com.instabug.library.util.StringUtility.isNumeric(r5)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            if (r0 == 0) goto L83
            long r4 = java.lang.Long.parseLong(r5)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            r6.m1550a(r4)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
        L61:
            org.json.JSONObject r0 = r6.m1556d()     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            r1.put(r0)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            boolean r0 = r3.moveToNext()     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            if (r0 != 0) goto L20
        L6e:
            r2.m1323d()     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            r2.m1324e()
            if (r3 == 0) goto L82
            boolean r0 = r3.isClosed()
            if (r0 != 0) goto L82
            r3.close()
            r2.m1321b()
        L82:
            return r1
        L83:
            java.text.SimpleDateFormat r0 = new java.text.SimpleDateFormat     // Catch: java.text.ParseException -> L98 java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.lang.String r4 = "MM-dd HH:mm:ss.SSS"
            java.util.Locale r7 = java.util.Locale.US     // Catch: java.text.ParseException -> L98 java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            r0.<init>(r4, r7)     // Catch: java.text.ParseException -> L98 java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            java.util.Date r0 = r0.parse(r5)     // Catch: java.text.ParseException -> L98 java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            long r4 = r0.getTime()     // Catch: java.text.ParseException -> L98 java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            r6.m1550a(r4)     // Catch: java.text.ParseException -> L98 java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            goto L61
        L98:
            r0 = move-exception
            java.lang.Class<com.instabug.library.logging.a> r4 = com.instabug.library.logging.C0709a.class
            java.lang.String r0 = r0.getMessage()     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            com.instabug.library.util.InstabugSDKLogger.m1800e(r4, r0)     // Catch: java.lang.Exception -> La3 java.lang.Throwable -> Lb9
            goto L61
        La3:
            r0 = move-exception
            r0.printStackTrace()     // Catch: java.lang.Throwable -> Lb9
            r2.m1324e()
            if (r3 == 0) goto L82
            boolean r0 = r3.isClosed()
            if (r0 != 0) goto L82
            r3.close()
            r2.m1321b()
            goto L82
        Lb9:
            r0 = move-exception
            r2.m1324e()
            if (r3 == 0) goto Lcb
            boolean r1 = r3.isClosed()
            if (r1 != 0) goto Lcb
            r3.close()
            r2.m1321b()
        Lcb:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.instabug.library.logging.C0709a.m1557a():org.json.JSONArray");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public static void m1560b() {
        m1566g();
        C0674c m1311b = C0672a.m1309a().m1311b();
        m1311b.m1322c();
        try {
            m1311b.m1320a("instabug_logs", (String) null, (String[]) null);
            m1311b.m1323d();
        } finally {
            m1311b.m1324e();
            m1311b.m1321b();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public static void m1562c() {
        m1566g();
        C0674c m1311b = C0672a.m1309a().m1311b();
        m1311b.m1322c();
        try {
            m1311b.m1319a("DELETE FROM instabug_logs WHERE log_date NOT IN ( SELECT log_date FROM instabug_logs WHERE log_date ORDER BY log_date DESC LIMIT 1000 )");
            m1311b.m1323d();
        } finally {
            m1311b.m1324e();
            m1311b.m1321b();
        }
    }
}
