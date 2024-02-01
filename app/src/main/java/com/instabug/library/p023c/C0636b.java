package com.instabug.library.p023c;

import android.os.Bundle;
import com.instabug.library.C0645d;
import com.instabug.library.OnSdkDismissedCallback;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/* compiled from: InstabugChat.java */
/* renamed from: com.instabug.library.c.b */
/* loaded from: classes.dex */
public class C0636b {
    /* renamed from: a */
    public static void m1209a(Runnable runnable) throws IllegalStateException {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "setNewMessageHandler");
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
    public static int m1206a() throws IllegalStateException {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "getUnreadMessagesCount");
            if (m1247a != null) {
                return ((Integer) m1247a.invoke(null, new Object[0])).intValue();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
        return 0;
    }

    /* renamed from: a */
    public static boolean m1213a(Bundle bundle) {
        try {
            Method m1248a = C0645d.m1248a(m1219c(), "isInstabugNotification", Bundle.class);
            if (m1248a != null) {
                return ((Boolean) m1248a.invoke(null, bundle)).booleanValue();
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
    public static boolean m1214a(Map<String, String> map) {
        try {
            Method m1248a = C0645d.m1248a(m1219c(), "isInstabugNotification", Map.class);
            if (m1248a != null) {
                return ((Boolean) m1248a.invoke(null, map)).booleanValue();
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
    public static void m1216b(Bundle bundle) {
        try {
            Method m1248a = C0645d.m1248a(m1219c(), "showNotification", Bundle.class);
            if (m1248a != null) {
                m1248a.invoke(null, bundle);
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
    public static void m1217b(Map<String, String> map) {
        try {
            Method m1248a = C0645d.m1248a(m1219c(), "showNotification", Map.class);
            if (m1248a != null) {
                m1248a.invoke(null, map);
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
    public static void m1211a(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "enableNotification");
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
    public static void m1210a(String str) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "setPushNotificationRegistrationToken");
            if (m1247a != null) {
                m1247a.invoke(null, str);
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
    public static void m1218b(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "enableConversationSound");
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
    public static void m1220c(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "enableSystemNotificationSound");
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
    public static void m1221d(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "enableInAppNotificationSound");
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
    public static void m1207a(int i) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "setNotificationIcon");
            if (m1247a != null) {
                m1247a.invoke(null, Integer.valueOf(i));
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
    public static void m1212a(boolean z, boolean z2, boolean z3) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "setAttachmentTypesEnabled");
            if (m1247a != null) {
                m1247a.invoke(null, Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3));
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
    public static void m1215b() {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "openNewChat");
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

    /* renamed from: e */
    public static void m1222e(boolean z) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "skipImageAttachmentAnnotation");
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
    public static void m1208a(OnSdkDismissedCallback onSdkDismissedCallback) {
        try {
            Method m1247a = C0645d.m1247a(m1219c(), "setOnSdkDismissedCallback");
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

    /* renamed from: c */
    private static Class m1219c() throws ClassNotFoundException {
        return Class.forName("com.instabug.chat.InstabugChat");
    }
}
