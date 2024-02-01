package com.instabug.library.internal.storage.cache.p028a;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* compiled from: InstabugDbHelper.java */
/* renamed from: com.instabug.library.internal.storage.cache.a.b */
/* loaded from: classes.dex */
public class C0673b extends SQLiteOpenHelper {
    public C0673b(Context context) {
        super(context, "instabug.db", (SQLiteDatabase.CursorFactory) null, 5);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE network_logs (_id INTEGER PRIMARY KEY,url TEXT,request TEXT,method TEXT,response TEXT,status INTEGER,headers TEXT,date TEXT,response_time INTEGER )");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS instabug_logs (log_message TEXT,log_level TEXT,log_date TEXT )");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_events_logs ( event_identifier TEXT,event_logging_count INTEGER )");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS sdk_event ( event_identifier TEXT,time_stamp TEXT,extra_attributes TEXT )");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS sdk_api ( time_stamp TEXT, api_name TEXT, count INTEGER, is_deprecated TEXT, parameters TEXT )");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        m1312a(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        m1312a(sQLiteDatabase);
    }

    /* renamed from: a */
    private void m1312a(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS network_logs");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS instabug_logs");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS user_events_logs");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS sdk_event");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS sdk_api");
        onCreate(sQLiteDatabase);
    }
}
