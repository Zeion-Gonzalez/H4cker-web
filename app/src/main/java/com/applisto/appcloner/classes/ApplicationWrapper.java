package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import com.applisto.appcloner.classes.util.Log;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

@Deprecated
/* loaded from: classes2.dex */
public class ApplicationWrapper extends Application {
    private static final String TAG = ApplicationWrapper.class.getSimpleName();
    private Application mBaseApp;

    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        Application application = this.mBaseApp;
        return application != null ? application : this;
    }

    @Override // android.app.Application
    public void onCreate() {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 128);
            boolean z = applicationInfo.metaData.getBoolean("com.applisto.appcloner.sandboxExternalStorage");
            Log.m15i(TAG, "onCreate; sandboxExternalStorage: " + z);
            if (z) {
                Object loadSecondaryClass = DefaultProvider.loadSecondaryClass("com.applisto.appcloner.classes.secondary.SandboxExternalStorage");
                loadSecondaryClass.getClass().getMethod("init", Context.class).invoke(loadSecondaryClass, this);
            }
            this.mBaseApp = createApplication(applicationInfo);
            final Field declaredField = ContextWrapper.class.getDeclaredField("mBase");
            declaredField.setAccessible(true);
            final ContextWrapper contextWrapper = new ContextWrapper(getBaseContext()) { // from class: com.applisto.appcloner.classes.ApplicationWrapper.1
                @Override // android.content.ContextWrapper, android.content.Context
                public Context getApplicationContext() {
                    return ApplicationWrapper.this.mBaseApp;
                }
            };
            declaredField.set(this.mBaseApp, contextWrapper);
            super.onCreate();
            final Field declaredField2 = Application.class.getDeclaredField("mComponentCallbacks");
            declaredField2.setAccessible(true);
            registerComponentCallbacks(new ComponentCallbacks() { // from class: com.applisto.appcloner.classes.ApplicationWrapper.2
                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(Configuration configuration) {
                    try {
                        Iterator it = ((Collection) declaredField2.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((ComponentCallbacks) it.next()).onConfigurationChanged(configuration);
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }

                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                    try {
                        Iterator it = ((Collection) declaredField2.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((ComponentCallbacks) it.next()).onLowMemory();
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }
            });
            final Field declaredField3 = Application.class.getDeclaredField("mActivityLifecycleCallbacks");
            declaredField3.setAccessible(true);
            registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() { // from class: com.applisto.appcloner.classes.ApplicationWrapper.3
                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    try {
                        Iterator it = ((Collection) declaredField3.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((Application.ActivityLifecycleCallbacks) it.next()).onActivityCreated(activity, bundle);
                        }
                        declaredField.set(activity, contextWrapper);
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityStarted(Activity activity) {
                    try {
                        Iterator it = ((Collection) declaredField3.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((Application.ActivityLifecycleCallbacks) it.next()).onActivityStarted(activity);
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityResumed(Activity activity) {
                    try {
                        Iterator it = ((Collection) declaredField3.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((Application.ActivityLifecycleCallbacks) it.next()).onActivityResumed(activity);
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityPaused(Activity activity) {
                    try {
                        Iterator it = ((Collection) declaredField3.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((Application.ActivityLifecycleCallbacks) it.next()).onActivityPaused(activity);
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityStopped(Activity activity) {
                    try {
                        Iterator it = ((Collection) declaredField3.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((Application.ActivityLifecycleCallbacks) it.next()).onActivityStopped(activity);
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                    try {
                        Iterator it = ((Collection) declaredField3.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((Application.ActivityLifecycleCallbacks) it.next()).onActivitySaveInstanceState(activity, bundle);
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityDestroyed(Activity activity) {
                    try {
                        Iterator it = ((Collection) declaredField3.get(ApplicationWrapper.this.mBaseApp)).iterator();
                        while (it.hasNext()) {
                            ((Application.ActivityLifecycleCallbacks) it.next()).onActivityDestroyed(activity);
                        }
                    } catch (Exception e) {
                        Log.m21w(ApplicationWrapper.TAG, e);
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= 18) {
                final Field declaredField4 = Application.class.getDeclaredField("mAssistCallbacks");
                declaredField4.setAccessible(true);
                registerOnProvideAssistDataListener(new Application.OnProvideAssistDataListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$ApplicationWrapper$JMjZM7ZHXH__w4XaWplbTFBHDOA
                    @Override // android.app.Application.OnProvideAssistDataListener
                    public final void onProvideAssistData(Activity activity, Bundle bundle) {
                        ApplicationWrapper.this.lambda$onCreate$0$ApplicationWrapper(declaredField4, activity, bundle);
                    }
                });
            }
            this.mBaseApp.onCreate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public /* synthetic */ void lambda$onCreate$0$ApplicationWrapper(Field field, Activity activity, Bundle bundle) {
        try {
            Iterator it = ((Collection) field.get(this.mBaseApp)).iterator();
            while (it.hasNext()) {
                ((Application.OnProvideAssistDataListener) it.next()).onProvideAssistData(activity, bundle);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private Application createApplication(ApplicationInfo applicationInfo) throws Exception {
        String string = applicationInfo.metaData.getString("com.applisto.appcloner.applicationClassName", "android.app.Application");
        Log.m15i(TAG, "createApplication; applicationClassName: " + string);
        return (Application) Class.forName(string).newInstance();
    }

    @Override // android.app.Application
    public void onTerminate() {
        Log.m15i(TAG, "onTerminate; ");
        super.onTerminate();
        this.mBaseApp.onTerminate();
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        Log.m15i(TAG, "onConfigurationChanged; newConfig.locale: " + configuration.locale);
        super.onConfigurationChanged(configuration);
        this.mBaseApp.onConfigurationChanged(configuration);
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onLowMemory() {
        Log.m15i(TAG, "onLowMemory; ");
        super.onLowMemory();
        this.mBaseApp.onLowMemory();
    }

    @Override // android.app.Application, android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
        Log.m15i(TAG, "onTrimMemory; level: " + i);
        super.onTrimMemory(i);
        this.mBaseApp.onTrimMemory(i);
    }
}
