package com.applisto.appcloner.classes;

import android.text.TextUtils;
import com.applisto.appcloner.classes.util.Log;
import java.lang.reflect.Field;

/* loaded from: classes2.dex */
public class FacebookLoginBehavior {
    private static final String TAG = FacebookLoginBehavior.class.getSimpleName();
    private final String mFacebookLoginBehavior;

    public FacebookLoginBehavior(CloneSettings cloneSettings) {
        this.mFacebookLoginBehavior = cloneSettings.getString("facebookLoginBehavior", null);
        Log.m15i(TAG, "FacebookLoginBehavior; mFacebookLoginBehavior: " + this.mFacebookLoginBehavior);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void install() {
        Log.m15i(TAG, "install; ");
        if (TextUtils.isEmpty(this.mFacebookLoginBehavior)) {
            return;
        }
        try {
            for (Object[] objArr : Class.forName("com.facebook.login.LoginBehavior").getEnumConstants()) {
                if ("WEB_ONLY".equals(this.mFacebookLoginBehavior) || "WEB_ONLY_ALT".equals(this.mFacebookLoginBehavior)) {
                    setField(objArr, "allowsGetTokenAuth", false);
                    setField(objArr, "allowsKatanaAuth", false);
                    setField(objArr, "allowsWebViewAuth", true);
                    setField(objArr, "allowsDeviceAuth", false);
                    setField(objArr, "allowsCustomTabAuth", true);
                    setField(objArr, "allowsFacebookLiteAuth", false);
                }
            }
        } catch (ClassNotFoundException unused) {
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    private static void setField(Object obj, String str, Object obj2) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(str);
            declaredField.setAccessible(true);
            declaredField.set(obj, obj2);
        } catch (Exception unused) {
        }
    }
}
