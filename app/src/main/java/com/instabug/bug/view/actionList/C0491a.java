package com.instabug.bug.view.actionList;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import java.io.Serializable;

/* compiled from: ActionItem.java */
/* renamed from: com.instabug.bug.view.actionList.a */
/* loaded from: classes.dex */
public class C0491a implements Serializable {

    /* renamed from: a */
    private String f202a;

    /* renamed from: b */
    private int f203b;

    /* renamed from: c */
    private transient Runnable f204c;

    public C0491a(String str, @DrawableRes int i, @NonNull Runnable runnable) {
        this.f202a = str;
        this.f204c = runnable;
        this.f203b = i;
    }

    @NonNull
    /* renamed from: a */
    public Runnable m344a() {
        return this.f204c;
    }

    /* renamed from: b */
    public String m345b() {
        return this.f202a;
    }

    /* renamed from: c */
    public int m346c() {
        return this.f203b;
    }

    /* renamed from: d */
    public boolean m347d() {
        return this.f203b > 0;
    }
}
