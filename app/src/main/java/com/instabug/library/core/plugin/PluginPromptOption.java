package com.instabug.library.core.plugin;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

@SuppressFBWarnings({"SE_BAD_FIELD"})
/* loaded from: classes.dex */
public class PluginPromptOption implements Serializable {
    @DrawableRes
    private int icon;
    private int invocationMode;
    private int notificationCount;
    private OnInvocationListener onInvocationListener;
    private int order;
    private String title;

    /* loaded from: classes.dex */
    public interface OnInvocationListener {
        void onInvoke(Uri uri);
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int i) {
        this.order = i;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    @DrawableRes
    public int getIcon() {
        return this.icon;
    }

    public void setIcon(@DrawableRes int i) {
        this.icon = i;
    }

    public int getNotificationCount() {
        return this.notificationCount;
    }

    public void setNotificationCount(int i) {
        if (i > 99) {
            this.notificationCount = 99;
        } else if (i < 0) {
            this.notificationCount = 0;
        } else {
            this.notificationCount = i;
        }
    }

    public int getInvocationMode() {
        return this.invocationMode;
    }

    public void setInvocationMode(int i) {
        this.invocationMode = i;
    }

    public void invoke(Uri uri) {
        if (this.onInvocationListener != null) {
            this.onInvocationListener.onInvoke(uri);
        }
    }

    public void invoke() {
        invoke(null);
    }

    /* renamed from: com.instabug.library.core.plugin.PluginPromptOption$a */
    /* loaded from: classes.dex */
    public static class C0641a implements Serializable, Comparator<PluginPromptOption> {
        @Override // java.util.Comparator
        /* renamed from: a  reason: merged with bridge method [inline-methods] */
        public int compare(PluginPromptOption pluginPromptOption, PluginPromptOption pluginPromptOption2) {
            return pluginPromptOption.getOrder() - pluginPromptOption2.getOrder();
        }
    }

    @Nullable
    public static PluginPromptOption getPromptOptionByInvocationMode(int i) {
        Iterator<PluginPromptOption> it = C0642a.m1245c().iterator();
        while (it.hasNext()) {
            PluginPromptOption next = it.next();
            if (next.getInvocationMode() == i) {
                return next;
            }
        }
        return null;
    }

    public void setOnInvocationListener(OnInvocationListener onInvocationListener) {
        this.onInvocationListener = onInvocationListener;
    }
}
