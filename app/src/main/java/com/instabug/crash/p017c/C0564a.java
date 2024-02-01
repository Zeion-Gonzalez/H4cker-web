package com.instabug.crash.p017c;

import android.support.annotation.Nullable;
import com.instabug.library.util.InstabugSDKLogger;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ExceptionFormatter.java */
/* renamed from: com.instabug.crash.c.a */
/* loaded from: classes.dex */
public class C0564a {
    /* renamed from: a */
    public static JSONObject m934a(Throwable th, @Nullable String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            String name = th.getClass().getName();
            if (str != null) {
                name = name + "-" + str;
            }
            jSONObject.put("name", name);
            StackTraceElement stackTraceElement = null;
            if (th.getStackTrace() != null && th.getStackTrace().length > 0) {
                stackTraceElement = th.getStackTrace()[0];
            }
            if (stackTraceElement != null && stackTraceElement.getFileName() != null) {
                jSONObject.put("location", stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber());
            } else {
                InstabugSDKLogger.m1804w(C0564a.class, "Incomplete crash stacktrace, if you're using Proguard, add the following line to your configuration file to have file name and line number in your crash report:");
                InstabugSDKLogger.m1804w(C0564a.class, "-keepattributes SourceFile,LineNumberTable");
            }
            jSONObject.put("exception", th.toString());
            if (th.getMessage() != null) {
                jSONObject.put("message", th.getMessage());
            }
            jSONObject.put("stackTrace", m933a(th));
            if (th.getCause() != null) {
                jSONObject.put("cause", m934a(th.getCause(), str));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    /* renamed from: a */
    public static String m933a(Throwable th) {
        String str = th.toString() + IOUtils.LINE_SEPARATOR_UNIX;
        for (StackTraceElement stackTraceElement : th.getStackTrace()) {
            str = str + "\t at " + stackTraceElement.toString() + IOUtils.LINE_SEPARATOR_UNIX;
        }
        return str;
    }
}
