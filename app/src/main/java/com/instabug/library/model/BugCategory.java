package com.instabug.library.model;

import android.support.annotation.DrawableRes;

@Deprecated
/* loaded from: classes.dex */
public class BugCategory {
    private int icon;
    private String label;

    private BugCategory() {
    }

    public static BugCategory getInstance() {
        return new BugCategory();
    }

    public BugCategory withLabel(String str) {
        this.label = str;
        return this;
    }

    public BugCategory withIcon(@DrawableRes int i) {
        this.icon = i;
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(@DrawableRes int i) {
        this.icon = i;
    }
}
