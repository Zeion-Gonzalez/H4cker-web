package com.instabug.library.internal.device;

import android.os.Build;
import com.instabug.library.util.InstabugSDKLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/* compiled from: InstabugRootChecker.java */
@SuppressFBWarnings({"DMI_HARDCODED_ABSOLUTE_FILENAME"})
/* renamed from: com.instabug.library.internal.device.a */
/* loaded from: classes.dex */
public class C0658a {
    /* renamed from: a */
    public static boolean m1289a() {
        return m1290b() || m1291c() || m1292d();
    }

    /* renamed from: b */
    public static boolean m1290b() {
        String str = Build.TAGS;
        return str != null && str.contains("test-keys");
    }

    /* renamed from: c */
    public static boolean m1291c() {
        try {
            return new File("/system/app/Superuser.apk").exists();
        } catch (Exception e) {
            return false;
        }
    }

    /* renamed from: d */
    public static boolean m1292d() {
        ArrayList arrayList = new ArrayList();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"}).getInputStream(), Charset.forName("UTF-8")));
            while (true) {
                try {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine != null) {
                            InstabugSDKLogger.m1799d(C0658a.class, "SHELL --> Line received: " + readLine);
                            arrayList.add(readLine);
                        } else {
                            try {
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } finally {
                    try {
                        bufferedReader.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
            bufferedReader.close();
            InstabugSDKLogger.m1799d(C0658a.class, "SHELL --> Full response was: " + arrayList);
            return arrayList.size() != 0;
        } catch (Exception e4) {
            return false;
        }
    }
}
