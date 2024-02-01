package com.applisto.appcloner.classes;

import android.graphics.Typeface;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.AbstractContentProvider;
import com.applisto.appcloner.hooking.Hooking;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class DefaultFontProvider extends AbstractContentProvider {
    private static final String TAG = DefaultFontProvider.class.getSimpleName();
    private static Typeface sTypeface;

    @Override // com.applisto.appcloner.classes.util.activity.AbstractContentProvider, android.content.ContentProvider
    public boolean onCreate() {
        try {
            sTypeface = Typeface.createFromAsset(getContext().getAssets(), ".fontFile");
            Log.m15i(TAG, "onCreate; sTypeface: " + sTypeface);
            HashMap hashMap = new HashMap();
            hashMap.put("sans-serif", sTypeface);
            hashMap.put("sans-serif-light", sTypeface);
            hashMap.put("sans-serif-condensed", sTypeface);
            hashMap.put("sans-serif-thin", sTypeface);
            hashMap.put("sans-serif-medium", sTypeface);
            Field declaredField = Typeface.class.getDeclaredField("sSystemFontMap");
            declaredField.setAccessible(true);
            HashMap hashMap2 = new HashMap((Map) declaredField.get(null));
            hashMap2.putAll(hashMap);
            declaredField.set(null, hashMap2);
            Hooking.initHooking(getContext());
            Hooking.addHookClass(Hook.class);
            Log.m15i(TAG, "onCreate; hooks installed");
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
        return true;
    }

    @HookReflectClass("android.graphics.Paint")
    /* loaded from: classes2.dex */
    public static class Hook {
        @HookMethodBackup("setTypeface")
        @MethodParams({Typeface.class})
        static Method setTypefaceBackup;

        @MethodParams({Typeface.class})
        @HookMethod("setTypeface")
        public static Typeface setTypefaceHook(Object obj, Typeface typeface) {
            try {
                Hooking.callInstanceOrigin(setTypefaceBackup, obj, DefaultFontProvider.sTypeface);
                return DefaultFontProvider.sTypeface;
            } catch (Throwable th) {
                Log.m21w(DefaultFontProvider.TAG, th);
                return typeface;
            }
        }
    }
}
