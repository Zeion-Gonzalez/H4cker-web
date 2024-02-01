package com.instabug.bug.view.p004b;

import java.io.Serializable;

/* compiled from: Disclaimer.java */
/* renamed from: com.instabug.bug.view.b.a */
/* loaded from: classes.dex */
public class C0498a implements Serializable {
    /* renamed from: a */
    public static String m365a(String str, int i) {
        return str.replaceAll("\\[([^\\]]+)\\]\\(([^\\]]+)\\)", String.format("<font color=\"#%06X\"><a href=\"$2\">$1</a></font>", Integer.valueOf(16777215 & i)));
    }

    /* renamed from: a */
    public static String m364a(String str) {
        return str.replace("#metadata", "instabug://instabug.com/disclaimer");
    }
}
