package com.instabug.library.core.plugin;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class Plugin {
    public static final int STATE_BACKGROUND = 0;
    public static final int STATE_FOREGROUND = 1;
    public static final int STATE_PROCESSING_ATTACHMENT = 2;
    protected WeakReference<Context> contextWeakReference;
    protected int state = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PluginState {
    }

    public abstract long getLastActivityTime();

    public abstract void release();

    @CallSuper
    public void init(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

    public ArrayList<PluginPromptOption> getPromptOptions() {
        return null;
    }

    public void setState(int i) {
        this.state = i;
    }

    public int getState() {
        return this.state;
    }

    @Nullable
    public Context getAppContext() {
        if (this.contextWeakReference == null) {
            return null;
        }
        return this.contextWeakReference.get();
    }

    public boolean isAppContextAvailable() {
        return (this.contextWeakReference == null || this.contextWeakReference.get() == null) ? false : true;
    }

    public void invoke(PluginPromptOption pluginPromptOption) {
    }
}
