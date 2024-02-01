package com.instabug.library.network;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import org.apache.commons.io.IOUtils;

/* compiled from: Multipart.java */
/* renamed from: com.instabug.library.network.c */
/* loaded from: classes.dex */
class C0728c {

    /* renamed from: a */
    private final String f1183a = "===" + System.currentTimeMillis() + "===";

    /* renamed from: b */
    private OutputStream f1184b;

    /* renamed from: c */
    private PrintWriter f1185c;

    public C0728c(HttpURLConnection httpURLConnection) throws IOException {
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setChunkedStreamingMode(1048576);
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.f1183a);
        this.f1184b = httpURLConnection.getOutputStream();
        this.f1185c = new PrintWriter((Writer) new OutputStreamWriter(this.f1184b, "UTF-8"), true);
    }

    /* renamed from: a */
    public void m1640a(String str, String str2) {
        this.f1185c.append((CharSequence) "--").append((CharSequence) this.f1183a).append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.append((CharSequence) "Content-Disposition: form-data; name=\"").append((CharSequence) str).append((CharSequence) "\"").append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.append((CharSequence) str2).append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.flush();
    }

    @SuppressFBWarnings({"OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
    /* renamed from: a */
    public void m1639a(String str, File file, String str2, String str3) throws IOException {
        this.f1185c.append((CharSequence) "--").append((CharSequence) this.f1183a).append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.append((CharSequence) "Content-Disposition: file; name=\"").append((CharSequence) str).append((CharSequence) "\"; filename=\"").append((CharSequence) str2).append((CharSequence) "\"").append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.append((CharSequence) "Content-Type: ").append((CharSequence) str3).append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.flush();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bArr = new byte[1048576];
        while (true) {
            int read = fileInputStream.read(bArr);
            if (read != -1) {
                this.f1184b.write(bArr, 0, read);
            } else {
                this.f1184b.flush();
                fileInputStream.close();
                this.f1185c.append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
                this.f1185c.flush();
                return;
            }
        }
    }

    /* renamed from: a */
    public void m1638a() {
        this.f1185c.append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS).flush();
        this.f1185c.append((CharSequence) "--").append((CharSequence) this.f1183a).append((CharSequence) "--").append((CharSequence) IOUtils.LINE_SEPARATOR_WINDOWS);
        this.f1185c.close();
    }
}
