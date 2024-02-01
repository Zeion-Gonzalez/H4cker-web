package com.instabug.library.p023c;

import android.support.annotation.NonNull;
import com.instabug.library.C0645d;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* compiled from: InstabugSurvey.java */
/* renamed from: com.instabug.library.c.d */
/* loaded from: classes.dex */
public class C0638d {
    /* renamed from: a */
    public static boolean m1228a() {
        try {
            Method m1247a = C0645d.m1247a(m1233c(), "showValidSurvey");
            if (m1247a != null) {
                return ((Boolean) m1247a.invoke(null, new Object[0])).booleanValue();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
        return false;
    }

    /* renamed from: b */
    public static boolean m1231b() {
        try {
            Method m1247a = C0645d.m1247a(m1233c(), "hasValidSurveys");
            if (m1247a != null) {
                return ((Boolean) m1247a.invoke(null, new Object[0])).booleanValue();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
        return false;
    }

    /* renamed from: a */
    public static void m1226a(Runnable runnable) {
        try {
            Method m1247a = C0645d.m1247a(m1233c(), "setPreShowingSurveyRunnable");
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

    /* renamed from: b */
    public static void m1230b(Runnable runnable) {
        try {
            Method m1247a = C0645d.m1247a(m1233c(), "setAfterShowingSurveyRunnable");
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
    public static void m1227a(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1233c(), "setSurveysAutoShowing");
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
    public static boolean m1229a(@NonNull String str) {
        try {
            Method m1247a = C0645d.m1247a(m1233c(), "showSurvey");
            if (m1247a != null) {
                return ((Boolean) m1247a.invoke(null, str)).booleanValue();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
        return false;
    }

    /* renamed from: b */
    public static boolean m1232b(@NonNull String str) {
        try {
            Method m1247a = C0645d.m1247a(m1233c(), "hasRespondToSurvey");
            if (m1247a != null) {
                return ((Boolean) m1247a.invoke(null, str)).booleanValue();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
        return false;
    }

    /* renamed from: c */
    private static Class m1233c() throws ClassNotFoundException {
        return Class.forName("com.instabug.survey.InstabugSurvey");
    }
}
