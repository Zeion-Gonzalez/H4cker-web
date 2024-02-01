package com.instabug.bug.model;

import java.io.Serializable;

/* compiled from: ReportType.java */
/* renamed from: com.instabug.bug.model.b */
/* loaded from: classes.dex */
public enum EnumC0472b implements Serializable {
    BUG("bug"),
    FEEDBACK("feedback"),
    NOT_AVAILABLE("not-available");


    /* renamed from: d */
    private final String f100d;

    EnumC0472b(String str) {
        this.f100d = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.f100d;
    }
}
