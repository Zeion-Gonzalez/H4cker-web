package com.instabug.bug;

import android.content.Context;
import android.content.Intent;
import com.instabug.bug.view.BugReportingActivity;

/* compiled from: BugReportingActivityLauncher.java */
/* renamed from: com.instabug.bug.b */
/* loaded from: classes.dex */
public class C0461b {
    /* renamed from: a */
    public static Intent m51a(Context context) {
        Intent intent = new Intent(context, BugReportingActivity.class);
        intent.putExtra("com.instabug.library.process", 162);
        intent.setFlags(268435456);
        intent.addFlags(65536);
        return intent;
    }

    /* renamed from: b */
    public static Intent m52b(Context context) {
        Intent intent = new Intent(context, BugReportingActivity.class);
        intent.putExtra("com.instabug.library.process", 161);
        intent.setFlags(268435456);
        intent.addFlags(65536);
        return intent;
    }

    /* renamed from: c */
    public static Intent m53c(Context context) {
        Intent intent = new Intent(context, BugReportingActivity.class);
        intent.putExtra("com.instabug.library.process", 167);
        intent.addFlags(65536);
        intent.addFlags(268435456);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public static Intent m54d(Context context) {
        Intent intent = new Intent(context, BugReportingActivity.class);
        intent.putExtra("com.instabug.library.process", 169);
        intent.addFlags(65536);
        return intent;
    }
}
