package com.applisto.appcloner.classes;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener;
import java.lang.reflect.Field;

/* loaded from: classes2.dex */
public class WhatsAppSupport extends ActivityLifecycleListener {
    private static final String TAG = WhatsAppSupport.class.getSimpleName();
    private Handler mHandler = new Handler();
    private String mOriginalPackageName;
    private String mVersionName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void install(String str) {
        Log.m15i(TAG, "install; originalPackageName: " + str);
        this.mOriginalPackageName = str;
        onCreate();
    }

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected void onActivityCreated(Activity activity) {
        install(activity);
    }

    private void install(final Context context) {
        Log.m15i(TAG, "install; context: " + context);
        this.mVersionName = Utils.getVersionName(context);
        if (this.mVersionName == null) {
            this.mVersionName = "";
        }
        try {
            Field declaredField = ContextWrapper.class.getDeclaredField("mBase");
            declaredField.setAccessible(true);
            Context context2 = context;
            while (context instanceof ContextWrapper) {
                context2 = context;
                context = (Context) declaredField.get(context);
            }
            declaredField.set(context2, new ContextWrapper(context) { // from class: com.applisto.appcloner.classes.WhatsAppSupport.1
                @Override // android.content.ContextWrapper, android.content.Context
                public String getPackageCodePath() {
                    try {
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(WhatsAppSupport.this.mOriginalPackageName, 0);
                        if (!WhatsAppSupport.this.mVersionName.equals(packageInfo.versionName)) {
                            WhatsAppSupport.this.showOriginalWhatsAppRequiredMessage(context);
                        }
                        String str = packageInfo.applicationInfo.publicSourceDir;
                        Log.m15i(WhatsAppSupport.TAG, "getPackageCodePath; publicSourceDir: " + str);
                        return str;
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.m21w(WhatsAppSupport.TAG, e);
                        WhatsAppSupport.this.showOriginalWhatsAppRequiredMessage(context);
                        return super.getPackageCodePath();
                    } catch (Exception e2) {
                        Log.m21w(WhatsAppSupport.TAG, e2);
                        return super.getPackageCodePath();
                    }
                }
            });
            Log.m15i(TAG, "installed; installed context wrapper");
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showOriginalWhatsAppRequiredMessage(final Context context) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$WhatsAppSupport$nQb_axsFTr5UUaDO3OsT7labUCw
            @Override // java.lang.Runnable
            public final void run() {
                WhatsAppSupport.this.lambda$showOriginalWhatsAppRequiredMessage$0$WhatsAppSupport(context);
            }
        }, 2000L);
    }

    public /* synthetic */ void lambda$showOriginalWhatsAppRequiredMessage$0$WhatsAppSupport(Context context) {
        try {
            Utils.showDialog(context, "WhatsApp", "The original WhatsApp app " + this.mVersionName + " must be installed during the registration process.");
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }
}
