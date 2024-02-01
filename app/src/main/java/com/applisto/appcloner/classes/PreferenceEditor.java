package com.applisto.appcloner.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.applisto.appcloner.classes.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
public class PreferenceEditor {
    private static final String TAG = PreferenceEditor.class.getSimpleName();
    private static final String TYPE_BOOLEAN = "Boolean";
    private static final String TYPE_FLOAT = "Float";
    private static final String TYPE_INTEGER = "Integer";
    private static final String TYPE_LONG = "Long";
    private static final String TYPE_STRING = "String";
    private static final String TYPE_STRING_SET = "String set";

    public static String[] getPreferenceFiles(Context context) {
        Log.m15i(TAG, "getPreferenceFiles; ");
        File file = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        Log.m15i(TAG, "getPreferenceFiles; directory: " + file);
        if (file.exists() && file.isDirectory()) {
            String[] list = file.list();
            Log.m15i(TAG, "getPreferenceFiles; files: " + Arrays.toString(list));
            if (list != null) {
                ArrayList arrayList = new ArrayList(Arrays.asList(list));
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    if (((String) it.next()).startsWith("com.applisto.appcloner")) {
                        it.remove();
                    }
                }
                Collections.sort(arrayList, String.CASE_INSENSITIVE_ORDER);
                return (String[]) arrayList.toArray(new String[0]);
            }
        }
        return new String[0];
    }

    public static Map<String, String> getPreferences(Context context, String str) {
        String valueToString;
        Log.m15i(TAG, "getPreferenceKeys; preferenceFile: " + str);
        SharedPreferences sharedPreferences = getSharedPreferences(context, str);
        HashMap hashMap = new HashMap();
        Map<String, ?> all = sharedPreferences.getAll();
        for (String str2 : all.keySet()) {
            if (str2 != null && !str2.startsWith("com.applisto.appcloner") && (valueToString = valueToString(all.get(str2))) != null) {
                hashMap.put(str2, valueToString);
            }
        }
        return hashMap;
    }

    public static void setPreference(Context context, String str, String str2, String str3) {
        Log.m15i(TAG, "getPreferenceValue; preferenceFile: " + str + ", key: " + str2 + ", preference: " + str3);
        SharedPreferences.Editor edit = getSharedPreferences(context, str).edit();
        if (str3 == null) {
            edit.remove(str2);
        } else {
            String[] split = str3.split(":", 2);
            String str4 = split[0];
            String str5 = split[1];
            Log.m15i(TAG, "setPreference; type: " + str4 + ", value: " + str5);
            if (TYPE_STRING.equals(str4)) {
                edit.putString(str2, str5);
            } else if (TYPE_INTEGER.equals(str4)) {
                edit.putInt(str2, Integer.parseInt(str5));
            } else if (TYPE_LONG.equals(str4)) {
                edit.putLong(str2, Long.parseLong(str5));
            } else if (TYPE_FLOAT.equals(str4)) {
                edit.putFloat(str2, Float.parseFloat(str5));
            } else if (TYPE_BOOLEAN.equals(str4)) {
                edit.putBoolean(str2, Boolean.parseBoolean(str5));
            } else if (TYPE_STRING_SET.equals(str4)) {
                edit.putStringSet(str2, new HashSet(Arrays.asList(str5.split(","))));
            }
        }
        edit.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context, String str) {
        return context.getSharedPreferences(str.replace(".xml", ""), 0);
    }

    private static String valueToString(Object obj) {
        if (obj instanceof String) {
            return "String:" + obj;
        }
        if (obj instanceof Integer) {
            return "Integer:" + obj;
        }
        if (obj instanceof Long) {
            return "Long:" + obj;
        }
        if (obj instanceof Float) {
            return "Float:" + obj;
        }
        if (obj instanceof Boolean) {
            return "Boolean:" + obj;
        }
        if (!(obj instanceof Set)) {
            return null;
        }
        return "String set:" + TextUtils.join(",", (Set) obj);
    }
}
