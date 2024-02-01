package com.instabug.library.bugreporting.model;

import android.support.annotation.DrawableRes;

/* loaded from: classes.dex */
public class ReportCategory {
    private int icon;
    private String label;

    private ReportCategory() {
    }

    public static ReportCategory getInstance() {
        return new ReportCategory();
    }

    public ReportCategory withLabel(String str) {
        this.label = str;
        return this;
    }

    public ReportCategory withIcon(@DrawableRes int i) {
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
