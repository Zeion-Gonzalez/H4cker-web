package com.invoate.amybridges.invoatewebinspector;

import android.app.Application;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

/* loaded from: classes.dex */
public class InstabugHandler extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        new Instabug.Builder(this, "364698467f1bea24677f9919b9fe7160").setInvocationEvent(InstabugInvocationEvent.SHAKE).build();
        Instabug.setShakingThreshold(150);
        Instabug.setEmailFieldRequired(true);
    }
}
