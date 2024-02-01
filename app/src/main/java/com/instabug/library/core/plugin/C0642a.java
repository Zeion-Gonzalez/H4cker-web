package com.instabug.library.core.plugin;

import android.content.Context;
import com.instabug.library.core.plugin.PluginPromptOption;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* compiled from: PluginsManager.java */
/* renamed from: com.instabug.library.core.plugin.a */
/* loaded from: classes.dex */
public class C0642a {

    /* renamed from: a */
    private static List<Plugin> f809a;

    /* renamed from: a */
    public static synchronized void m1242a(Context context) {
        synchronized (C0642a.class) {
            if (f809a == null) {
                f809a = new ArrayList();
                for (String str : new String[]{"com.instabug.crash.CrashPlugin", "com.instabug.survey.SurveyPlugin", "com.instabug.chat.ChatPlugin", "com.instabug.bug.BugPlugin"}) {
                    try {
                        try {
                            Plugin plugin = (Plugin) Class.forName(str).newInstance();
                            plugin.init(context);
                            f809a.add(plugin);
                            InstabugSDKLogger.m1799d(C0642a.class, "pluginClassPath: " + str);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            InstabugSDKLogger.m1800e(C0642a.class, "Can't get: " + str);
                        } catch (IllegalAccessException e2) {
                            e2.printStackTrace();
                        }
                    } catch (InstantiationException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
    }

    /* renamed from: a */
    public static void m1241a() {
        if (f809a == null) {
            InstabugSDKLogger.m1800e(C0642a.class, "PluginsManager.releasePlugins() was called before PluginsManager.init() was called");
        }
        Iterator<Plugin> it = f809a.iterator();
        while (it.hasNext()) {
            it.next().release();
        }
    }

    /* renamed from: b */
    public static long m1244b() {
        if (f809a == null) {
            InstabugSDKLogger.m1800e(C0642a.class, "PluginsManager.getLastActivityTime() was called before PluginsManager.init() was called");
        }
        long j = 0;
        Iterator<Plugin> it = f809a.iterator();
        while (true) {
            long j2 = j;
            if (it.hasNext()) {
                j = it.next().getLastActivityTime();
                if (j <= j2) {
                    j = j2;
                }
            } else {
                return j2;
            }
        }
    }

    /* renamed from: c */
    public static ArrayList<PluginPromptOption> m1245c() {
        ArrayList<PluginPromptOption> arrayList = new ArrayList<>();
        for (Plugin plugin : f809a) {
            InstabugSDKLogger.m1803v(C0642a.class, "plugin: " + plugin.toString());
            ArrayList<PluginPromptOption> promptOptions = plugin.getPromptOptions();
            if (promptOptions != null) {
                arrayList.addAll(promptOptions);
            }
        }
        m1243a(arrayList);
        return arrayList;
    }

    /* renamed from: a */
    private static void m1243a(ArrayList<PluginPromptOption> arrayList) {
        Collections.sort(arrayList, new PluginPromptOption.C0641a());
    }

    /* renamed from: d */
    public static boolean m1246d() {
        if (f809a != null) {
            Iterator<Plugin> it = f809a.iterator();
            while (it.hasNext()) {
                if (it.next().getState() != 0) {
                    return true;
                }
            }
        }
        return SettingsManager.getInstance().isPromptOptionsScreenShown() || SettingsManager.getInstance().isRequestPermissionScreenShown();
    }

    /* renamed from: a */
    public static Plugin m1240a(Class cls) {
        if (f809a != null) {
            for (Plugin plugin : f809a) {
                if (cls.isInstance(plugin)) {
                    return plugin;
                }
            }
        }
        return null;
    }
}
