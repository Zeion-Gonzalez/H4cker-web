package com.instabug.survey.p034c;

import java.util.Locale;

/* compiled from: LocaleHelper.java */
/* renamed from: com.instabug.survey.c.c */
/* loaded from: classes.dex */
public class C0775c {
    /* renamed from: a */
    public static boolean m1991a() {
        return m1992a(Locale.getDefault());
    }

    /* renamed from: a */
    public static boolean m1992a(Locale locale) {
        byte directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == 1 || directionality == 2;
    }
}
