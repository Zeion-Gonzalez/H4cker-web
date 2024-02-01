package com.applisto.appcloner.classes;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.SimpleCrypt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class CloneSettings {
    public static final String PREF_KEY_CLONE_TIMESTAMP = "com.applisto.appcloner.classes.cloneTimestamp";
    private static final String TAG = CloneSettings.class.getSimpleName();
    private static CloneSettings sCloneSettings;
    private File mCloneSettingsFile;
    private JSONObject mJsonObject;

    public static synchronized CloneSettings getInstance(Context context) {
        CloneSettings cloneSettings;
        synchronized (CloneSettings.class) {
            if (sCloneSettings == null) {
                try {
                    Context applicationContext = context.getApplicationContext();
                    if (applicationContext != null) {
                        context = applicationContext;
                    }
                    if (context instanceof Application) {
                        Field declaredField = ContextWrapper.class.getDeclaredField("mBase");
                        declaredField.setAccessible(true);
                        context = (Context) declaredField.get(context);
                    }
                } catch (Throwable th) {
                    Log.m21w(TAG, th);
                }
                sCloneSettings = new CloneSettings(context);
            }
            cloneSettings = sCloneSettings;
        }
        return cloneSettings;
    }

    public CloneSettings(Context context) {
        long parseLong;
        String defaultCloneSettingsJson;
        this.mJsonObject = new JSONObject();
        try {
            this.mCloneSettingsFile = new File(context.getFilesDir(), "cloneSettings.json");
            Bundle bundle = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData;
            String string = bundle.getString("com.applisto.appcloner.originalCloneTimestamp");
            if (TextUtils.isEmpty(string)) {
                parseLong = Long.parseLong(bundle.getString("com.applisto.appcloner.cloneTimestamp"));
            } else {
                parseLong = Long.parseLong(string);
            }
            SharedPreferences appClonerClassesPreferences = Utils.getAppClonerClassesPreferences(context);
            if (appClonerClassesPreferences.getLong(PREF_KEY_CLONE_TIMESTAMP, 0L) != parseLong) {
                if (this.mCloneSettingsFile.delete()) {
                    Log.m15i(TAG, "CloneSettings; deleted old clone settings file");
                }
                appClonerClassesPreferences.edit().putLong(PREF_KEY_CLONE_TIMESTAMP, parseLong).apply();
            }
            if (this.mCloneSettingsFile.exists()) {
                defaultCloneSettingsJson = toString(new FileInputStream(this.mCloneSettingsFile));
            } else {
                defaultCloneSettingsJson = getDefaultCloneSettingsJson();
                try {
                    PrintWriter printWriter = new PrintWriter(this.mCloneSettingsFile, "UTF-8");
                    printWriter.write(defaultCloneSettingsJson);
                    printWriter.close();
                } catch (Exception e) {
                    Log.m21w(TAG, e);
                }
            }
            this.mJsonObject = new JSONObject(defaultCloneSettingsJson);
        } catch (Exception e2) {
            Log.m21w(TAG, e2);
        }
    }

    private CloneSettings() {
        this.mJsonObject = new JSONObject();
        this.mCloneSettingsFile = null;
        this.mJsonObject = new JSONObject();
    }

    public CloneSettings(String str) throws JSONException {
        this.mJsonObject = new JSONObject();
        this.mCloneSettingsFile = null;
        this.mJsonObject = new JSONObject(str);
    }

    public CloneSettings(JSONObject jSONObject) {
        this.mJsonObject = new JSONObject();
        this.mCloneSettingsFile = null;
        this.mJsonObject = jSONObject;
    }

    public File getCloneSettingsFile() {
        return this.mCloneSettingsFile;
    }

    public boolean has(String str) {
        return this.mJsonObject.has(str);
    }

    public String getString(String str, String str2) {
        try {
            return this.mJsonObject.getString(str);
        } catch (Exception unused) {
            return str2;
        }
    }

    public Integer getInteger(String str, Integer num) {
        try {
            return Integer.valueOf(this.mJsonObject.getInt(str));
        } catch (Exception unused) {
            return num;
        }
    }

    public Long getLong(String str, Long l) {
        try {
            return Long.valueOf(this.mJsonObject.getLong(str));
        } catch (Exception unused) {
            return l;
        }
    }

    public Float getFloat(String str, Float f) {
        try {
            return Float.valueOf((float) this.mJsonObject.getDouble(str));
        } catch (Exception unused) {
            return f;
        }
    }

    public Double getDouble(String str, Double d) {
        try {
            return Double.valueOf(this.mJsonObject.getDouble(str));
        } catch (Exception unused) {
            return d;
        }
    }

    public Boolean getBoolean(String str, Boolean bool) {
        try {
            return Boolean.valueOf(this.mJsonObject.getBoolean(str));
        } catch (Exception unused) {
            return bool;
        }
    }

    public CloneSettings forObject(String str) {
        try {
            return new CloneSettings(this.mJsonObject.getJSONObject(str));
        } catch (JSONException unused) {
            return null;
        }
    }

    public List<CloneSettings> forObjectArray(String str) {
        try {
            ArrayList arrayList = new ArrayList();
            JSONArray jSONArray = this.mJsonObject.getJSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(new CloneSettings(jSONArray.getJSONObject(i)));
            }
            return arrayList;
        } catch (Exception unused) {
            return null;
        }
    }

    public List<String> getStringList(String str) {
        try {
            ArrayList arrayList = new ArrayList();
            JSONArray jSONArray = this.mJsonObject.getJSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.getString(i));
            }
            return arrayList;
        } catch (Exception unused) {
            return null;
        }
    }

    public Map<String, String> getStringMap(String str) {
        try {
            HashMap hashMap = new HashMap();
            JSONObject jSONObject = this.mJsonObject.getJSONObject(str);
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                hashMap.put(next, jSONObject.getString(next));
            }
            return hashMap;
        } catch (Exception unused) {
            return null;
        }
    }

    public List<Map<String, Object>> getMapList(String str) {
        try {
            ArrayList arrayList = new ArrayList();
            JSONArray jSONArray = this.mJsonObject.getJSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                HashMap hashMap = new HashMap();
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    hashMap.put(next, jSONObject.get(next));
                }
                arrayList.add(hashMap);
            }
            return arrayList;
        } catch (Exception unused) {
            return null;
        }
    }

    public static String getDefaultCloneSettingsJson() {
        Log.m15i(TAG, "getDefaultCloneSettingsJson; ");
        try {
            InputStream resourceAsStream = CloneSettings.class.getResourceAsStream("cloneSettings.json");
            try {
                return new SimpleCrypt("UYGy723!Po-efjve").decrypt(toString(resourceAsStream));
            } finally {
                try {
                    resourceAsStream.close();
                } catch (Exception unused) {
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException unused2) {
                }
            }
        }
    }

    private static String toString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
                sb.append('\n');
            }
            String sb2 = sb.toString();
            try {
                bufferedReader.close();
            } catch (Exception unused) {
            }
            return sb2;
        } catch (Throwable th) {
            try {
                bufferedReader.close();
            } catch (Exception unused2) {
            }
            throw th;
        }
    }
}
