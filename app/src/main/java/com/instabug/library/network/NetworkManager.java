package com.instabug.library.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import com.instabug.library.Instabug;
import com.instabug.library.model.NetworkLog;
import com.instabug.library.network.Request;
import com.instabug.library.user.C0750a;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import p045rx.Observable;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public class NetworkManager {
    public static final String APP_TOKEN = "application_token";
    private static final int DEFAULT_CONNECTION_TIME_OUT = 15000;
    private static final int DEFAULT_READ_TIME_OUT = 10000;
    public static final String UUID = "uuid";

    /* loaded from: classes.dex */
    public enum RequestType {
        NORMAL,
        MULTI_PART,
        FILE_DOWNLOAD
    }

    public static boolean isOnline(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isConnectedOrConnecting()) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            InstabugSDKLogger.m1804w(NetworkManager.class, "Could not read network state. To enable please add the following line in your AndroidManifest.xml <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\"/>\n" + e.getMessage());
        } catch (Exception e2) {
            InstabugSDKLogger.m1801e(NetworkManager.class, "Something went wrong while checking network state", e2);
        }
        return false;
    }

    public Observable<RequestResponse> doRequest(@NonNull final Request request) {
        return Observable.create(new Observable.OnSubscribe<RequestResponse>() { // from class: com.instabug.library.network.NetworkManager.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Subscriber<? super RequestResponse> subscriber) {
                try {
                    subscriber.onStart();
                    InstabugSDKLogger.m1803v(NetworkManager.class, "Starting do request");
                    InstabugSDKLogger.m1803v(NetworkManager.class, "Request Url: " + request.getRequestUrl());
                    InstabugSDKLogger.m1803v(NetworkManager.class, "Request Type: " + request.getRequestMethod().toString());
                    InstabugSDKLogger.m1803v(NetworkManager.class, "Request Body: " + request.getRequestBody());
                    HttpURLConnection httpURLConnection = null;
                    switch (C07212.f1160a[request.getRequestType().ordinal()]) {
                        case 1:
                            httpURLConnection = NetworkManager.this.connectWithNormalType(request);
                            break;
                        case 2:
                            httpURLConnection = NetworkManager.this.connectWithFileDownloadType(request);
                            break;
                        case 3:
                            httpURLConnection = NetworkManager.this.connectWithMultiPartType(request);
                            break;
                    }
                    if (httpURLConnection != null) {
                        if (httpURLConnection.getResponseCode() >= 300) {
                            InstabugSDKLogger.m1800e(NetworkManager.class, "Network request got error");
                            NetworkManager.this.handleServerConnectionError(httpURLConnection);
                        }
                        InstabugSDKLogger.m1803v(NetworkManager.class, "Network request completed successfully");
                        switch (C07212.f1160a[request.getRequestType().ordinal()]) {
                            case 1:
                                subscriber.onNext(NetworkManager.this.handleRequestResponse(httpURLConnection));
                                break;
                            case 2:
                                subscriber.onNext(NetworkManager.this.handleFileDownloadRequestResponse(request, httpURLConnection));
                                break;
                            case 3:
                                subscriber.onNext(NetworkManager.this.handleMultipartRequestResponse(httpURLConnection));
                                break;
                        }
                    }
                    subscriber.onCompleted();
                } catch (C0729d e) {
                    InstabugSDKLogger.m1804w(NetworkManager.class, "Request got error: " + request.getRequestUrl() + ", error message: " + e.getMessage());
                    subscriber.onCompleted();
                } catch (InterruptedIOException e2) {
                    InstabugSDKLogger.m1801e(NetworkManager.class, "Request got error: " + request.getRequestUrl(), e2);
                    subscriber.onError(e2);
                } catch (IOException e3) {
                    InstabugSDKLogger.m1801e(NetworkManager.class, "Request got error: " + request.getRequestUrl(), e3);
                    subscriber.onError(e3);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.network.NetworkManager$2 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C07212 {

        /* renamed from: a */
        static final /* synthetic */ int[] f1160a = new int[RequestType.values().length];

        static {
            try {
                f1160a[RequestType.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f1160a[RequestType.FILE_DOWNLOAD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f1160a[RequestType.MULTI_PART.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public Request buildRequest(@NonNull Context context, @NonNull Request.Endpoint endpoint, @NonNull Request.RequestMethod requestMethod) throws JSONException {
        return buildRequest(context, endpoint, requestMethod, RequestType.NORMAL);
    }

    public Request buildRequest(@NonNull Context context, @NonNull Request.Endpoint endpoint, @NonNull Request.RequestMethod requestMethod, @NonNull RequestType requestType) throws JSONException {
        Request request = new Request(endpoint, requestType);
        request.setRequestMethod(requestMethod);
        return buildRequest(context, request);
    }

    public Request buildRequest(@NonNull Context context, @NonNull String str, @NonNull Request.RequestMethod requestMethod) throws JSONException {
        return buildRequest(context, str, requestMethod, RequestType.NORMAL);
    }

    public Request buildRequest(@NonNull Context context, @NonNull String str, @NonNull Request.RequestMethod requestMethod, @NonNull RequestType requestType) throws JSONException {
        Request request = new Request(str, requestType);
        request.setRequestMethod(requestMethod);
        return buildRequest(context, request);
    }

    public Request buildRequestWithoutUUID(@NonNull Context context, @NonNull Request.Endpoint endpoint, @NonNull Request.RequestMethod requestMethod) throws JSONException {
        Request request = new Request(endpoint, RequestType.NORMAL);
        request.setRequestMethod(requestMethod);
        request.addParameter(APP_TOKEN, Instabug.getAppToken());
        return request;
    }

    private Request buildRequest(@NonNull Context context, Request request) throws JSONException {
        request.addParameter(APP_TOKEN, Instabug.getAppToken());
        request.addParameter(UUID, C0750a.m1789d());
        return request;
    }

    public HttpURLConnection buildConnection(@NonNull Request request) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(request.getRequestUrl()).openConnection();
        httpURLConnection.setRequestProperty("Content-Type", NetworkLog.JSON);
        httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
        httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
        Iterator<Request.RequestParameter> it = request.getHeaders().iterator();
        while (it.hasNext()) {
            Request.RequestParameter next = it.next();
            httpURLConnection.setRequestProperty(next.getKey(), (String) next.getValue());
        }
        httpURLConnection.setDoInput(true);
        return httpURLConnection;
    }

    private HttpURLConnection setURLConnectionDefaultTimeOut(@NonNull HttpURLConnection httpURLConnection) {
        return setURLConnectionTimeOut(httpURLConnection, 10000, DEFAULT_CONNECTION_TIME_OUT);
    }

    public HttpURLConnection setURLConnectionTimeOut(@NonNull HttpURLConnection httpURLConnection, int i, int i2) {
        httpURLConnection.setReadTimeout(i);
        httpURLConnection.setConnectTimeout(i2);
        return httpURLConnection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HttpURLConnection connectWithNormalType(Request request) throws IOException {
        InstabugSDKLogger.m1803v(this, "Connect to: " + request.getRequestUrl() + " with normal type");
        HttpURLConnection buildConnection = buildConnection(request);
        setURLConnectionDefaultTimeOut(buildConnection);
        buildConnection.setRequestMethod(request.getRequestMethod().toString());
        if (request.getRequestMethod() == Request.RequestMethod.Post || request.getRequestMethod() == Request.RequestMethod.put) {
            buildConnection.setDoOutput(true);
            writeRequestBody(buildConnection.getOutputStream(), request.getRequestBody());
        }
        return buildConnection;
    }

    private void writeRequestBody(OutputStream outputStream, String str) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gZIPOutputStream.write(str.getBytes(Charset.forName("UTF8")));
        gZIPOutputStream.close();
        outputStream.write(byteArrayOutputStream.toByteArray());
        byteArrayOutputStream.close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HttpURLConnection connectWithFileDownloadType(Request request) throws IOException {
        InstabugSDKLogger.m1803v(this, "Connect to: " + request.getRequestUrl() + " with fileDownload type");
        return connectWithNormalType(request);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HttpURLConnection connectWithMultiPartType(Request request) throws IOException {
        InstabugSDKLogger.m1803v(this, "Connect to: " + request.getRequestUrl() + " with multiPart type");
        HttpURLConnection buildConnection = buildConnection(request);
        buildConnection.setRequestMethod(request.getRequestMethod().toString());
        buildConnection.setRequestProperty("Connection", "Keep-Alive");
        buildConnection.setRequestProperty("Cache-Control", "no-cache");
        buildConnection.setRequestProperty("Content-Encoding", "");
        C0728c c0728c = new C0728c(buildConnection);
        Iterator<Request.RequestParameter> it = request.getRequestBodyParameters().iterator();
        while (it.hasNext()) {
            Request.RequestParameter next = it.next();
            c0728c.m1640a(next.getKey(), next.getValue().toString());
        }
        Request.FileToUpload fileToUpload = request.getFileToUpload();
        c0728c.m1639a(fileToUpload.getFilePartName(), new File(fileToUpload.getFilePath()), fileToUpload.getFileName(), fileToUpload.getFileType());
        c0728c.m1638a();
        return buildConnection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RequestResponse handleRequestResponse(HttpURLConnection httpURLConnection) throws IOException {
        RequestResponse requestResponse = new RequestResponse();
        int responseCode = httpURLConnection.getResponseCode();
        requestResponse.setResponseCode(responseCode);
        InstabugSDKLogger.m1803v(this, "Request response code: " + responseCode);
        String convertStreamToString = convertStreamToString(httpURLConnection.getInputStream());
        requestResponse.setResponseBody(convertStreamToString);
        InstabugSDKLogger.m1803v(this, "Request response: " + convertStreamToString);
        httpURLConnection.disconnect();
        return requestResponse;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RequestResponse handleMultipartRequestResponse(HttpURLConnection httpURLConnection) throws IOException {
        return handleRequestResponse(httpURLConnection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RequestResponse handleFileDownloadRequestResponse(Request request, HttpURLConnection httpURLConnection) throws IOException {
        RequestResponse requestResponse = new RequestResponse();
        int responseCode = httpURLConnection.getResponseCode();
        requestResponse.setResponseCode(responseCode);
        InstabugSDKLogger.m1803v(this, "File downloader request response code: " + responseCode);
        copyStream(httpURLConnection.getInputStream(), new FileOutputStream(request.getDownloadedFile()));
        requestResponse.setResponseBody(request.getDownloadedFile());
        InstabugSDKLogger.m1803v(this, "File downloader request response: " + request.getDownloadedFile().getPath());
        httpURLConnection.disconnect();
        return requestResponse;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleServerConnectionError(HttpURLConnection httpURLConnection) throws IOException, C0729d {
        int responseCode = httpURLConnection.getResponseCode();
        InputStream errorStream = httpURLConnection.getErrorStream();
        InstabugSDKLogger.m1800e(this, "Error getting Network request response: " + convertStreamToString(errorStream));
        throw new C0729d("responseCode: " + responseCode + IOUtils.LINE_SEPARATOR_UNIX + convertStreamToString(errorStream));
    }

    private String convertStreamToString(InputStream inputStream) {
        Scanner useDelimiter = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        return useDelimiter.hasNext() ? useDelimiter.next() : "";
    }

    private void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        while (true) {
            int read = inputStream.read();
            if (read != -1) {
                outputStream.write(read);
            } else {
                return;
            }
        }
    }
}
