package com.applisto.appcloner.classes.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
public class MultiProcessPreferences extends ContentProvider {
    public static Uri BASE_URI = null;
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String FLOAT_TYPE = "float";
    private static final String INT_TYPE = "integer";
    private static final String KEY = "key";
    private static final String LONG_TYPE = "long";
    private static final int MATCH_DATA = 65536;
    private static final String META_DATA_KEY_PREFERENCES_NAME = "PREFERENCES_NAME";
    public static String PREFERENCES_AUTHORITY = null;
    private static final String STRING_TYPE = "string";
    private static final String TYPE = "type";
    private static UriMatcher matcher;
    private static final String TAG = MultiProcessPreferences.class.getSimpleName();
    public static String PREFERENCES_NAME = "MultiProcessPreferences";

    /* JADX INFO: Access modifiers changed from: private */
    public static void init(Context context, String str) {
        if (str != null) {
            PREFERENCES_AUTHORITY = str;
        }
        android.util.Log.i(TAG, "init; PREFERENCES_AUTHORITY: " + PREFERENCES_AUTHORITY);
        matcher = new UriMatcher(-1);
        matcher.addURI(PREFERENCES_AUTHORITY, "*/*", 65536);
        BASE_URI = Uri.parse("content://" + PREFERENCES_AUTHORITY);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (matcher != null) {
            return true;
        }
        Context context = getContext();
        init(context, null);
        ProviderInfo resolveContentProvider = context.getPackageManager().resolveContentProvider(PREFERENCES_AUTHORITY, 128);
        if (resolveContentProvider != null && resolveContentProvider.metaData != null) {
            PREFERENCES_NAME = resolveContentProvider.metaData.getString(META_DATA_KEY_PREFERENCES_NAME);
        }
        android.util.Log.i(TAG, "onCreate; PREFERENCES_NAME: " + PREFERENCES_NAME);
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/vnd." + PREFERENCES_AUTHORITY + ".item";
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        if (matcher.match(uri) == 65536) {
            getContext().getSharedPreferences(PREFERENCES_NAME, 0).edit().clear().apply();
            return 0;
        }
        throw new IllegalArgumentException("Unsupported uri " + uri);
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (matcher.match(uri) == 65536) {
            SharedPreferences.Editor edit = getContext().getSharedPreferences(PREFERENCES_NAME, 0).edit();
            for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
                Object value = entry.getValue();
                String key = entry.getKey();
                if (value == null) {
                    edit.remove(key);
                } else if (value instanceof String) {
                    edit.putString(key, (String) value);
                } else if (value instanceof Boolean) {
                    edit.putBoolean(key, ((Boolean) value).booleanValue());
                } else if (value instanceof Long) {
                    edit.putLong(key, ((Long) value).longValue());
                } else if (value instanceof Integer) {
                    edit.putInt(key, ((Integer) value).intValue());
                } else if (value instanceof Float) {
                    edit.putFloat(key, ((Float) value).floatValue());
                } else {
                    throw new IllegalArgumentException("Unsupported type " + uri);
                }
            }
            edit.apply();
            return null;
        }
        throw new IllegalArgumentException("Unsupported uri " + uri);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Object valueOf;
        if (matcher.match(uri) == 65536) {
            String str3 = uri.getPathSegments().get(0);
            String str4 = uri.getPathSegments().get(1);
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{str3});
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFERENCES_NAME, 0);
            if (!sharedPreferences.contains(str3)) {
                return matrixCursor;
            }
            MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
            if (STRING_TYPE.equals(str4)) {
                valueOf = sharedPreferences.getString(str3, null);
            } else if ("boolean".equals(str4)) {
                valueOf = Integer.valueOf(sharedPreferences.getBoolean(str3, false) ? 1 : 0);
            } else if ("long".equals(str4)) {
                valueOf = Long.valueOf(sharedPreferences.getLong(str3, 0L));
            } else if (INT_TYPE.equals(str4)) {
                valueOf = Integer.valueOf(sharedPreferences.getInt(str3, 0));
            } else if ("float".equals(str4)) {
                valueOf = Float.valueOf(sharedPreferences.getFloat(str3, 0.0f));
            } else {
                throw new IllegalArgumentException("Unsupported type " + uri);
            }
            newRow.add(valueOf);
            return matrixCursor;
        }
        throw new IllegalArgumentException("Unsupported uri " + uri);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getStringValue(Cursor cursor, String str) {
        if (cursor == null) {
            return str;
        }
        if (cursor.moveToFirst()) {
            str = cursor.getString(0);
        }
        cursor.close();
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean getBooleanValue(Cursor cursor, boolean z) {
        if (cursor == null) {
            return z;
        }
        if (cursor.moveToFirst()) {
            z = false;
            if (cursor.getInt(0) > 0) {
                z = true;
            }
        }
        cursor.close();
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getIntValue(Cursor cursor, int i) {
        if (cursor == null) {
            return i;
        }
        if (cursor.moveToFirst()) {
            i = cursor.getInt(0);
        }
        cursor.close();
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getLongValue(Cursor cursor, long j) {
        if (cursor == null) {
            return j;
        }
        if (cursor.moveToFirst()) {
            j = cursor.getLong(0);
        }
        cursor.close();
        return j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float getFloatValue(Cursor cursor, float f) {
        if (cursor == null) {
            return f;
        }
        if (cursor.moveToFirst()) {
            f = cursor.getFloat(0);
        }
        cursor.close();
        return f;
    }

    /* loaded from: classes2.dex */
    public static class MultiProcessSharedPreferences implements SharedPreferences {
        private final String mAuthority;
        private final Context mContext;

        public MultiProcessSharedPreferences(Context context) {
            this(context, null);
        }

        public MultiProcessSharedPreferences(Context context, String str) {
            this.mContext = context;
            this.mAuthority = str;
        }

        @Override // android.content.SharedPreferences
        public Editor edit() {
            return new Editor(this.mContext);
        }

        @Override // android.content.SharedPreferences
        public String getString(String str, String str2) {
            return MultiProcessPreferences.getStringValue(this.mContext.getContentResolver().query(getContentUri(str, MultiProcessPreferences.STRING_TYPE), null, null, null, null), str2);
        }

        @Override // android.content.SharedPreferences
        public long getLong(String str, long j) {
            return MultiProcessPreferences.getLongValue(this.mContext.getContentResolver().query(getContentUri(str, "long"), null, null, null, null), j);
        }

        @Override // android.content.SharedPreferences
        public float getFloat(String str, float f) {
            return MultiProcessPreferences.getFloatValue(this.mContext.getContentResolver().query(getContentUri(str, "float"), null, null, null, null), f);
        }

        @Override // android.content.SharedPreferences
        public boolean getBoolean(String str, boolean z) {
            return MultiProcessPreferences.getBooleanValue(this.mContext.getContentResolver().query(getContentUri(str, "boolean"), null, null, null, null), z);
        }

        @Override // android.content.SharedPreferences
        public int getInt(String str, int i) {
            return MultiProcessPreferences.getIntValue(this.mContext.getContentResolver().query(getContentUri(str, MultiProcessPreferences.INT_TYPE), null, null, null, null), i);
        }

        @Override // android.content.SharedPreferences
        public Map<String, ?> getAll() {
            throw new UnsupportedOperationException();
        }

        @Override // android.content.SharedPreferences
        public Set<String> getStringSet(String str, Set<String> set) {
            throw new UnsupportedOperationException();
        }

        @Override // android.content.SharedPreferences
        public boolean contains(String str) {
            throw new UnsupportedOperationException();
        }

        @Override // android.content.SharedPreferences
        public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
            throw new UnsupportedOperationException();
        }

        @Override // android.content.SharedPreferences
        public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
            throw new UnsupportedOperationException();
        }

        /* loaded from: classes2.dex */
        public class Editor implements SharedPreferences.Editor {
            private final Context mContext;
            private final ContentValues mValues;

            private Editor(Context context) {
                this.mValues = new ContentValues();
                this.mContext = context;
            }

            @Override // android.content.SharedPreferences.Editor
            public void apply() {
                this.mContext.getContentResolver().insert(MultiProcessSharedPreferences.this.getContentUri(MultiProcessPreferences.KEY, MultiProcessPreferences.TYPE), this.mValues);
            }

            @Override // android.content.SharedPreferences.Editor
            public boolean commit() {
                apply();
                return true;
            }

            @Override // android.content.SharedPreferences.Editor
            public Editor putString(String str, String str2) {
                this.mValues.put(str, str2);
                return this;
            }

            @Override // android.content.SharedPreferences.Editor
            public Editor putLong(String str, long j) {
                this.mValues.put(str, Long.valueOf(j));
                return this;
            }

            @Override // android.content.SharedPreferences.Editor
            public Editor putBoolean(String str, boolean z) {
                this.mValues.put(str, Boolean.valueOf(z));
                return this;
            }

            @Override // android.content.SharedPreferences.Editor
            public Editor putInt(String str, int i) {
                this.mValues.put(str, Integer.valueOf(i));
                return this;
            }

            @Override // android.content.SharedPreferences.Editor
            public Editor putFloat(String str, float f) {
                this.mValues.put(str, Float.valueOf(f));
                return this;
            }

            @Override // android.content.SharedPreferences.Editor
            public Editor remove(String str) {
                this.mValues.putNull(str);
                return this;
            }

            @Override // android.content.SharedPreferences.Editor
            public Editor clear() {
                this.mContext.getContentResolver().delete(MultiProcessSharedPreferences.this.getContentUri(MultiProcessPreferences.KEY, MultiProcessPreferences.TYPE), null, null);
                return this;
            }

            @Override // android.content.SharedPreferences.Editor
            public SharedPreferences.Editor putStringSet(String str, Set<String> set) {
                throw new UnsupportedOperationException();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Uri getContentUri(String str, String str2) {
            if (MultiProcessPreferences.BASE_URI == null) {
                MultiProcessPreferences.init(this.mContext, this.mAuthority);
            }
            return MultiProcessPreferences.BASE_URI.buildUpon().appendPath(str).appendPath(str2).build();
        }
    }
}
