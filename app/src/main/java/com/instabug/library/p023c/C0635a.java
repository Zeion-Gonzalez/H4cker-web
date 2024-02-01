package com.instabug.library.p023c;

import com.instabug.library.C0645d;
import com.instabug.library.OnSdkDismissedCallback;
import com.instabug.library.bugreporting.model.ReportCategory;
import com.instabug.library.extendedbugreport.ExtendedBugReport;
import com.instabug.library.model.BugCategory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/* compiled from: InstabugBugReporting.java */
/* renamed from: com.instabug.library.c.a */
/* loaded from: classes.dex */
public class C0635a {
    /* renamed from: a */
    public static void m1195a(Runnable runnable) throws IllegalStateException {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setPreSendingRunnable");
            if (m1247a != null) {
                m1247a.invoke(null, runnable);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    public static void m1192a(OnSdkDismissedCallback onSdkDismissedCallback) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setLegacyOnSdkDismissedCallback");
            if (m1247a != null) {
                m1247a.invoke(null, onSdkDismissedCallback);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    public static void m1197a(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setShouldSkipInitialScreenshotAnnotation");
            if (m1247a != null) {
                m1247a.invoke(null, Boolean.valueOf(z));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: b */
    public static void m1201b(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setEmailFieldRequired");
            if (m1247a != null) {
                m1247a.invoke(null, Boolean.valueOf(z));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: c */
    public static void m1203c(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setCommentFieldRequired");
            if (m1247a != null) {
                m1247a.invoke(null, Boolean.valueOf(z));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: d */
    public static void m1204d(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setEmailFieldVisibility");
            if (m1247a != null) {
                m1247a.invoke(null, Boolean.valueOf(z));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    public static void m1198a(boolean z, boolean z2, boolean z3, boolean z4) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setAttachmentTypesEnabled");
            if (m1247a != null) {
                m1247a.invoke(null, Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    public static void m1196a(List<BugCategory> list) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setLegacyBugCategories");
            if (m1247a != null) {
                m1247a.invoke(null, list);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: b */
    public static void m1200b(List<ReportCategory> list) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setLegacyReportCategories");
            if (m1247a != null) {
                m1247a.invoke(null, list);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: e */
    public static void m1205e(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setSuccessDialogEnabled");
            if (m1247a != null) {
                m1247a.invoke(null, Boolean.valueOf(z));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    public static Runnable m1191a() {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "getPreReportRunnable");
            if (m1247a != null) {
                return (Runnable) m1247a.invoke(null, new Object[0]);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /* renamed from: a */
    public static void m1194a(CharSequence charSequence, boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "addExtraReportField");
            if (m1247a != null) {
                m1247a.invoke(null, charSequence, Boolean.valueOf(z));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: b */
    public static void m1199b() {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "clearExtraReportFields");
            if (m1247a != null) {
                m1247a.invoke(null, new Object[0]);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: a */
    public static void m1193a(ExtendedBugReport.State state) {
        try {
            Method m1247a = C0645d.m1247a(m1202c(), "setExtendedBugReportState");
            if (m1247a != null) {
                m1247a.invoke(null, state);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    /* renamed from: c */
    private static Class m1202c() throws ClassNotFoundException {
        return Class.forName("com.instabug.bug.InstabugBugReporting");
    }
}
