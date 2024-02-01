package com.applisto.appcloner.classes;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;
import com.applisto.appcloner.classes.util.IPackageManagerHook;
import com.applisto.appcloner.classes.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.security.cert.X509Certificate;
import org.apache.commons.io.IOUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class Signatures {
    private static final String TAG = Signatures.class.getSimpleName();
    private final boolean mDisableShareActions;
    private final String mFacebookLoginBehavior;
    private final String mTwitterLoginBehavior;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Signatures(CloneSettings cloneSettings) {
        this.mFacebookLoginBehavior = cloneSettings.getString("facebookLoginBehavior", null);
        this.mTwitterLoginBehavior = cloneSettings.getString("twitterLoginBehavior", null);
        this.mDisableShareActions = cloneSettings.getBoolean("disableShareActions", false).booleanValue();
        Log.m15i(TAG, "Signatures; mFacebookLoginBehavior: " + this.mFacebookLoginBehavior + ", mTwitterLoginBehavior: " + this.mTwitterLoginBehavior + ", mDisableShareActions: " + this.mDisableShareActions);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void install(Context context, Context context2, final String str, final String str2, Bundle bundle) {
        Log.m15i(TAG, "install; packageName: " + str + ", originalPackageName: " + str2);
        try {
            final Integer valueOf = Integer.valueOf(context2.getPackageManager().getApplicationInfo(str, 128).uid);
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = context.getPackageManager().getApplicationInfo(str2, 0);
            } catch (Exception unused) {
            }
            final int i = applicationInfo != null ? applicationInfo.uid : 0;
            String string = bundle.getString("com.applisto.appcloner.originalSignatures");
            Log.m15i(TAG, "install; signaturesString: " + string);
            final Signature[] unmarshallSignatures = unmarshallSignatures(string);
            if (unmarshallSignatures != null) {
                Log.m15i(TAG, "install; uid: " + valueOf + ", originalUid: " + i + ", originalSignatures:\n" + signaturesToString(unmarshallSignatures));
                new IPackageManagerHook() { // from class: com.applisto.appcloner.classes.Signatures.1
                    @Override // com.applisto.appcloner.classes.util.IPackageManagerHook
                    protected InvocationHandler getInvocationHandler(final Object obj) {
                        return new InvocationHandler() { // from class: com.applisto.appcloner.classes.Signatures.1.1
                            @Override // java.lang.reflect.InvocationHandler
                            public Object invoke(Object obj2, Method method, Object[] objArr) throws Throwable {
                                String name;
                                ResolveInfo resolveInfo;
                                try {
                                    String name2 = method.getName();
                                    if ("checkSignatures".equals(name2)) {
                                        if (str.equals(objArr[0])) {
                                            objArr[0] = str2;
                                        } else if (valueOf.equals(objArr[0])) {
                                            objArr[0] = Integer.valueOf(i);
                                        }
                                        if (str.equals(objArr[1])) {
                                            objArr[1] = str2;
                                        } else if (valueOf.equals(objArr[1])) {
                                            objArr[1] = Integer.valueOf(i);
                                        }
                                    } else if ("getInstallerPackageName".equals(name2)) {
                                        return new String(Base64.decode("Y29tLmFuZHJvaWQudmVuZGluZw==", 0));
                                    }
                                } catch (Exception e) {
                                    Log.m21w(Signatures.TAG, e);
                                }
                                Object invoke = method.invoke(obj, objArr);
                                try {
                                    name = method.getName();
                                } catch (Exception e2) {
                                    Log.m21w(Signatures.TAG, e2);
                                }
                                if ("getPackageInfo".equals(name)) {
                                    if (invoke != null) {
                                        PackageInfo packageInfo = (PackageInfo) invoke;
                                        if (!"WEB_ONLY".equals(Signatures.this.mFacebookLoginBehavior) || (!"com.facebook.katana".equals(packageInfo.packageName) && !"com.facebook.lite".equals(packageInfo.packageName) && !"com.facebook.services".equals(packageInfo.packageName))) {
                                            if ("WEB_ONLY".equals(Signatures.this.mTwitterLoginBehavior) && "com.twitter.android".equals(packageInfo.packageName)) {
                                                Log.m15i(Signatures.TAG, "invoke; getPackageInfo; returning null for Twitter package");
                                                return null;
                                            }
                                            if (str.equals(packageInfo.packageName) && packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                                                packageInfo.signatures = unmarshallSignatures;
                                            }
                                        } else {
                                            Log.m15i(Signatures.TAG, "invoke; getPackageInfo; returning null for Facebook packages");
                                            return null;
                                        }
                                    }
                                } else if (!"getApplicationInfo".equals(name)) {
                                    if ("queryIntentActivities".equals(name)) {
                                        try {
                                            if (!Signatures.this.mDisableShareActions) {
                                                if ("WEB_ONLY".equals(Signatures.this.mFacebookLoginBehavior) || "WEB_ONLY_ALT".equals(Signatures.this.mFacebookLoginBehavior)) {
                                                    Iterator<ResolveInfo> it = getResolveInfos(invoke).iterator();
                                                    while (it.hasNext()) {
                                                        ResolveInfo next = it.next();
                                                        if (next != null && next.toString().contains("com.facebook.katana/.ProxyAuth")) {
                                                            it.remove();
                                                        }
                                                    }
                                                }
                                            } else {
                                                getResolveInfos(invoke).clear();
                                            }
                                        } catch (Throwable th) {
                                            Log.m21w(Signatures.TAG, th);
                                        }
                                    } else if ("resolveIntent".equals(name)) {
                                        try {
                                            if (("WEB_ONLY".equals(Signatures.this.mFacebookLoginBehavior) || "WEB_ONLY_ALT".equals(Signatures.this.mFacebookLoginBehavior)) && (resolveInfo = (ResolveInfo) invoke) != null && resolveInfo.toString().contains("com.facebook.katana/.ProxyAuth")) {
                                                Log.m15i(Signatures.TAG, "invoke; resolveIntent; returning no resolved intent for Facebook");
                                                return null;
                                            }
                                        } catch (Throwable th2) {
                                            Log.m21w(Signatures.TAG, th2);
                                        }
                                    }
                                    Log.m21w(Signatures.TAG, e2);
                                } else if (invoke != null) {
                                    ApplicationInfo applicationInfo2 = (ApplicationInfo) invoke;
                                    if (!"WEB_ONLY".equals(Signatures.this.mFacebookLoginBehavior) || (!"com.facebook.katana".equals(applicationInfo2.packageName) && !"com.facebook.lite".equals(applicationInfo2.packageName) && !"com.facebook.services".equals(applicationInfo2.packageName))) {
                                        if ("WEB_ONLY".equals(Signatures.this.mTwitterLoginBehavior) && "com.twitter.android".equals(applicationInfo2.packageName)) {
                                            Log.m15i(Signatures.TAG, "invoke; getApplicationInfo; returning null for Twitter package");
                                            return null;
                                        }
                                        applicationInfo2.flags &= -3;
                                    } else {
                                        Log.m15i(Signatures.TAG, "invoke; getApplicationInfo; returning null for Facebook packages");
                                        return null;
                                    }
                                }
                                return invoke;
                            }

                            private List<ResolveInfo> getResolveInfos(Object obj2) {
                                try {
                                    if ("android.content.pm.ParceledListSlice".equals(obj2.getClass().getName())) {
                                        Field declaredField = (Build.VERSION.SDK_INT >= 26 ? obj2.getClass().getSuperclass() : obj2.getClass()).getDeclaredField("mList");
                                        declaredField.setAccessible(true);
                                        return (List) declaredField.get(obj2);
                                    }
                                    return (List) obj2;
                                } catch (Exception e) {
                                    Log.m21w(Signatures.TAG, e);
                                    return Collections.EMPTY_LIST;
                                }
                            }
                        };
                    }
                }.install(context2);
            }
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    private static Signature[] unmarshallSignatures(String str) {
        try {
            byte[] decode = Base64.decode(str, 2);
            Parcel obtain = Parcel.obtain();
            try {
                obtain.unmarshall(decode, 0, decode.length);
                obtain.setDataPosition(0);
                Parcelable[] readParcelableArray = obtain.readParcelableArray(Signatures.class.getClassLoader());
                Signature[] signatureArr = new Signature[readParcelableArray.length];
                System.arraycopy(readParcelableArray, 0, signatureArr, 0, readParcelableArray.length);
                return signatureArr;
            } finally {
                obtain.recycle();
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return null;
        }
    }

    private static String signaturesToString(Signature[] signatureArr) {
        ArrayList arrayList = new ArrayList();
        if (signatureArr != null) {
            for (Signature signature : signatureArr) {
                try {
                    arrayList.add("  " + Base64.encodeToString(X509Certificate.getInstance(signature.toByteArray()).getEncoded(), 2));
                } catch (Exception e) {
                    Log.m21w(TAG, e);
                }
            }
        }
        return TextUtils.join(IOUtils.LINE_SEPARATOR_UNIX, arrayList);
    }
}
