package com.instabug.library.network;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.instabug.library.network.NetworkManager;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Request {
    protected static final String BASE_URL = "https://api.instabug.com/api/sdk/v3";
    private ArrayList<RequestParameter> bodyParameters;
    private File downloadedFile;
    private String endPoint;
    private FileToUpload fileToUpload;
    private ArrayList<RequestParameter> headers;
    private RequestMethod requestMethod;
    private NetworkManager.RequestType requestType;
    private String requestUrl;
    private ArrayList<RequestParameter> urlParameters;

    /* loaded from: classes.dex */
    public interface Callbacks<T, K> {
        void onFailed(K k);

        void onSucceeded(T t);
    }

    /* loaded from: classes.dex */
    public enum Endpoint {
        ReportBug("/bugs"),
        AddBugAttachment("/bugs/:bug_token/attachments"),
        ReportCrash("/crashes"),
        AddCrashAttachment("/crashes/:crash_token/attachments"),
        TriggerChat("/chats"),
        SendMessage("/chats/:chat_number/messages"),
        AddMessageAttachment("/chats/:chat_number/messages/:message_id/attachments"),
        SyncChats("/chats/sync"),
        AppSettings("/features"),
        SendSession("/sessions"),
        GetSurveys("/surveys/v3"),
        SubmitSurvey("/surveys/:survey_id/v3/responses"),
        bugLogs("/bugs/:bug_token/state_logs"),
        crashLogs("/crashes/:crash_token/state_logs"),
        chatLogs("/chats/:chat_token/state_logs"),
        MigrateUUID("/migrate_uuid"),
        Analytics("/analytics"),
        PushToken("/push_token"),
        ReportCategories("/application_categories");

        private final String name;

        Endpoint(String str) {
            this.name = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.name;
        }
    }

    /* loaded from: classes.dex */
    public enum RequestMethod {
        Get("GET"),
        Post("POST"),
        put("PUT");

        private final String name;

        RequestMethod(String str) {
            this.name = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.name;
        }
    }

    public Request(Endpoint endpoint, NetworkManager.RequestType requestType) {
        this.endPoint = endpoint.toString();
        this.requestUrl = BASE_URL + getEndpoint();
        this.requestType = requestType;
        initialize();
    }

    public Request(String str, NetworkManager.RequestType requestType) {
        this.requestUrl = str;
        this.requestType = requestType;
        initialize();
    }

    private void initialize() {
        this.urlParameters = new ArrayList<>();
        this.bodyParameters = new ArrayList<>();
        this.headers = new ArrayList<>();
    }

    public String getEndpoint() {
        return this.endPoint;
    }

    public void setEndpoint(String str) {
        this.endPoint = str;
        this.requestUrl = BASE_URL + getEndpoint();
    }

    public String getRequestUrl() {
        return this.requestUrl + getUrlEncodedParameters();
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public NetworkManager.RequestType getRequestType() {
        return this.requestType;
    }

    public void addHeader(RequestParameter requestParameter) {
        this.headers.add(requestParameter);
    }

    public ArrayList<RequestParameter> getHeaders() {
        return this.headers;
    }

    public Request addParameter(String str, Object obj) throws JSONException {
        if (this.requestMethod.equals(RequestMethod.Get)) {
            addRequestUrlParameter(str, obj);
        } else {
            addRequestBodyParameter(str, obj);
        }
        return this;
    }

    public void addRequestUrlParameter(String str, Object obj) throws JSONException {
        this.urlParameters.add(new RequestParameter(str, obj));
    }

    public ArrayList<RequestParameter> setRequestUrlParameters(ArrayList<RequestParameter> arrayList) {
        this.urlParameters = arrayList;
        return arrayList;
    }

    public ArrayList<RequestParameter> getRequestUrlParameters() {
        return this.urlParameters;
    }

    public ArrayList<RequestParameter> clearRequestUrlParameters() {
        this.urlParameters.clear();
        return this.urlParameters;
    }

    public void addRequestBodyParameter(String str, Object obj) throws JSONException {
        this.bodyParameters.add(new RequestParameter(str, obj));
    }

    public ArrayList<RequestParameter> setRequestBodyParameters(ArrayList<RequestParameter> arrayList) {
        this.bodyParameters = arrayList;
        return arrayList;
    }

    public ArrayList<RequestParameter> getRequestBodyParameters() {
        return this.bodyParameters;
    }

    public ArrayList<RequestParameter> clearRequestBodyParameters() {
        this.bodyParameters.clear();
        return this.bodyParameters;
    }

    private String getUrlEncodedParameters() {
        Uri.Builder builder = new Uri.Builder();
        Iterator<RequestParameter> it = this.urlParameters.iterator();
        while (it.hasNext()) {
            RequestParameter next = it.next();
            builder.appendQueryParameter(next.getKey(), next.getValue().toString());
        }
        return builder.toString();
    }

    public String getRequestBody() {
        try {
            JSONObject jSONObject = new JSONObject();
            Iterator<RequestParameter> it = getRequestBodyParameters().iterator();
            while (it.hasNext()) {
                RequestParameter next = it.next();
                jSONObject.put(next.getKey(), next.getValue());
            }
            return jSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public FileToUpload getFileToUpload() {
        return this.fileToUpload;
    }

    public Request setFileToUpload(FileToUpload fileToUpload) {
        this.fileToUpload = fileToUpload;
        return this;
    }

    public File getDownloadedFile() {
        return this.downloadedFile;
    }

    public Request setDownloadedFile(String str) {
        this.downloadedFile = new File(str);
        return this;
    }

    /* loaded from: classes.dex */
    public static class RequestParameter implements Serializable {
        private String key;
        private Object value;

        public RequestParameter(String str, Object obj) {
            this.key = str;
            this.value = obj;
        }

        public Object getValue() {
            return this.value;
        }

        public String getKey() {
            return this.key;
        }
    }

    /* loaded from: classes.dex */
    public static class FileToUpload {
        private String fileName;
        private String filePartName;
        private String filePath;
        private String fileType;

        public FileToUpload(@NonNull String str, @NonNull String str2, @NonNull String str3, @NonNull String str4) {
            this.filePartName = str;
            this.fileName = str2;
            this.filePath = str3;
            this.fileType = str4;
        }

        public String getFilePartName() {
            return this.filePartName;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getFilePath() {
            return this.filePath;
        }

        public String getFileType() {
            return this.fileType;
        }
    }
}
