package com.instabug.library.model;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import com.instabug.library.internal.storage.cache.p028a.C0672a;
import com.instabug.library.internal.storage.cache.p028a.C0674c;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NetworkLog {
    public static final String CONTENT_TYPE = "content-type";
    public static final String HTML = "text/html";
    public static final String JSON = "application/json";
    public static final String LIMIT_ERROR = "{\"InstabugNetworkLog Error\":\"Response body exceeded limit\"}";
    public static final String PLAIN_TEXT = "text/plain";
    public static final String XML_1 = "application/xml";
    public static final String XML_2 = "text/xml";
    private String date;
    private String headers;
    private String method;
    private String request;
    private String response;
    private int responseCode;
    private long totalDuration;
    private String url;

    public String getDate() {
        return this.date;
    }

    public void setDate(String str) {
        this.date = str;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public String getRequest() {
        return this.request;
    }

    public void setRequest(String str) {
        this.request = str;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String str) {
        this.response = str;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String str) {
        this.method = str;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(int i) {
        this.responseCode = i;
    }

    public String getHeaders() {
        return this.headers;
    }

    public void setHeaders(String str) {
        this.headers = str;
    }

    public long getTotalDuration() {
        return this.totalDuration;
    }

    public void setTotalDuration(long j) {
        this.totalDuration = j;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NetworkLog)) {
            return false;
        }
        NetworkLog networkLog = (NetworkLog) obj;
        if (this.responseCode != networkLog.responseCode) {
            return false;
        }
        if (this.date != null) {
            if (!this.date.equals(networkLog.date)) {
                return false;
            }
        } else if (networkLog.date != null) {
            return false;
        }
        if (this.url != null) {
            if (!this.url.equals(networkLog.url)) {
                return false;
            }
        } else if (networkLog.url != null) {
            return false;
        }
        if (this.request != null) {
            if (!this.request.equals(networkLog.request)) {
                return false;
            }
        } else if (networkLog.request != null) {
            return false;
        }
        if (this.response != null) {
            if (!this.response.equals(networkLog.response)) {
                return false;
            }
        } else if (networkLog.response != null) {
            return false;
        }
        if (this.method != null) {
            if (!this.method.equals(networkLog.method)) {
                return false;
            }
        } else if (networkLog.method != null) {
            return false;
        }
        if (this.totalDuration != networkLog.totalDuration) {
            return false;
        }
        if (this.headers != null) {
            z = this.headers.equals(networkLog.headers);
        } else if (networkLog.headers != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((((((this.method != null ? this.method.hashCode() : 0) + (((this.response != null ? this.response.hashCode() : 0) + (((this.request != null ? this.request.hashCode() : 0) + (((this.url != null ? this.url.hashCode() : 0) + ((this.date != null ? this.date.hashCode() : 0) * 31)) * 31)) * 31)) * 31)) * 31) + this.responseCode) * 31) + (this.headers != null ? this.headers.hashCode() : 0)) * 31) + Long.valueOf(this.totalDuration).hashCode();
    }

    public String toString() {
        return "NetworkLog{date='" + this.date + "', url='" + this.url + "', request='" + this.request + "', method='" + this.method + "', responseCode=" + this.responseCode + ", headers='" + this.headers + "', response='" + this.response + "', totalDuration='" + this.totalDuration + "'}";
    }

    @Deprecated
    public long insert(Context context) {
        return insert();
    }

    public long insert() {
        C0674c m1311b = C0672a.m1309a().m1311b();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("url", getUrl());
            contentValues.put("request", getRequest());
            contentValues.put("method", getMethod());
            contentValues.put("response", getResponse());
            contentValues.put(NotificationCompat.CATEGORY_STATUS, "" + getResponseCode());
            contentValues.put("date", getDate());
            contentValues.put("headers", getHeaders());
            contentValues.put("response_time", Long.valueOf(getTotalDuration()));
            return m1311b.m1314a("network_logs", (String) null, contentValues);
        } finally {
            m1311b.m1321b();
        }
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("date", getDate());
        jSONObject.put("method", getMethod());
        jSONObject.put(NotificationCompat.CATEGORY_STATUS, getResponseCode());
        jSONObject.put("url", getUrl());
        jSONObject.put("response_time", getTotalDuration());
        try {
            jSONObject.put("headers", new JSONObject(getHeaders()));
        } catch (Exception e) {
            jSONObject.put("headers", getHeaders());
        }
        try {
            jSONObject.put("request", new JSONObject(getRequest()));
        } catch (Exception e2) {
            jSONObject.put("request", getRequest());
        }
        try {
            jSONObject.put("response", new JSONObject(getResponse()));
        } catch (Exception e3) {
            jSONObject.put("response", getResponse());
        }
        return jSONObject;
    }
}
