package com.instabug.library.logging;

import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.model.NetworkLog;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressFBWarnings({"NM_METHOD_NAMING_CONVENTION"})
/* loaded from: classes.dex */
public class InstabugNetworkLog {
    private NetworkLog networkLog = new NetworkLog();

    public void log(String str, String str2, String str3, String str4, int i) throws IOException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("connection").setType(HttpURLConnection.class), new Api.Parameter().setName("requestBody").setType(String.class), new Api.Parameter().setName("responseBody").setType(String.class));
        this.networkLog.setResponseCode(i);
        this.networkLog.setDate(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds() + "");
        this.networkLog.setMethod(str2);
        this.networkLog.setUrl(str);
        try {
            this.networkLog.setRequest(validateBody(str3));
            this.networkLog.setResponse(validateBody(str4));
            insert();
            InstabugSDKLogger.m1799d(this, "adding network log: " + this.networkLog.toString());
        } catch (IllegalArgumentException e) {
            InstabugSDKLogger.m1799d(this, "Content-type is not allowed to be logged");
            this.networkLog = null;
        }
    }

    @Deprecated
    public void Log(HttpURLConnection httpURLConnection, String str, String str2) throws IOException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("connection").setType(HttpURLConnection.class), new Api.Parameter().setName("requestBody").setType(String.class), new Api.Parameter().setName("responseBody").setType(String.class));
        this.networkLog.setResponseCode(httpURLConnection.getResponseCode());
        this.networkLog.setDate(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds() + "");
        this.networkLog.setMethod(httpURLConnection.getRequestMethod());
        this.networkLog.setUrl(httpURLConnection.getURL().toString());
        try {
            addHeaders(httpURLConnection);
            this.networkLog.setRequest(validateBody(str));
            this.networkLog.setResponse(validateBody(str2));
        } catch (IllegalArgumentException e) {
            InstabugSDKLogger.m1799d(this, "Content-type is not allowed to be logged");
            this.networkLog = null;
            return;
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        insert();
        InstabugSDKLogger.m1799d(this, "adding network log: " + this.networkLog.toString());
    }

    private String validateBody(String str) {
        if (str == null) {
            return null;
        }
        if (str.getBytes(Charset.forName("UTF-8")).length > 1000000) {
            return NetworkLog.LIMIT_ERROR;
        }
        return str;
    }

    public void log(HttpURLConnection httpURLConnection, String str, String str2) throws IOException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("connection").setType(HttpURLConnection.class), new Api.Parameter().setName("requestBody").setType(String.class), new Api.Parameter().setName("responseBody").setType(String.class));
        this.networkLog.setResponseCode(httpURLConnection.getResponseCode());
        this.networkLog.setDate(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds() + "");
        this.networkLog.setMethod(httpURLConnection.getRequestMethod());
        this.networkLog.setUrl(httpURLConnection.getURL().toString());
        try {
            addHeaders(httpURLConnection);
            this.networkLog.setRequest(validateBody(str));
            this.networkLog.setResponse(validateBody(str2));
        } catch (IllegalArgumentException e) {
            InstabugSDKLogger.m1799d(this, "Content-type is not allowed to be logged");
            this.networkLog = null;
            return;
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        insert();
        InstabugSDKLogger.m1799d(this, "adding network log: " + this.networkLog.toString());
    }

    private void addHeaders(HttpURLConnection httpURLConnection) throws JSONException, IllegalArgumentException {
        JSONObject jSONObject = new JSONObject();
        for (String str : httpURLConnection.getHeaderFields().keySet()) {
            if (str != null) {
                for (String str2 : httpURLConnection.getHeaderFields().get(str)) {
                    if (str.equalsIgnoreCase(NetworkLog.CONTENT_TYPE) && !str2.contains(NetworkLog.JSON) && !str2.contains(NetworkLog.XML_1) && !str2.contains(NetworkLog.XML_2) && !str2.contains(NetworkLog.HTML) && !str2.contains(NetworkLog.PLAIN_TEXT)) {
                        throw new IllegalArgumentException();
                    }
                    jSONObject.put(str, str2);
                }
            }
        }
        this.networkLog.setHeaders(jSONObject.toString());
    }

    private void insert() {
        this.networkLog.insert();
    }
}
