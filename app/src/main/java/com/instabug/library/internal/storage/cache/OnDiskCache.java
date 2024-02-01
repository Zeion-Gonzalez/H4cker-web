package com.instabug.library.internal.storage.cache;

import android.content.Context;
import android.text.TextUtils;
import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class OnDiskCache<V extends Cacheable> extends Cache<String, V> {
    private Class<V> VClass;
    private final File cacheDir;
    private final File cacheFile;
    private final Charset charset;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.instabug.library.internal.storage.cache.Cache
    public /* bridge */ /* synthetic */ Object put(String str, Object obj) {
        return put(str, (String) ((Cacheable) obj));
    }

    public OnDiskCache(Context context, String str, String str2, Class<V> cls) {
        super(str);
        this.VClass = cls;
        this.cacheDir = context.getCacheDir();
        this.cacheFile = new File(this.cacheDir + str2);
        if (!this.cacheFile.exists()) {
            try {
                this.cacheFile.createNewFile();
            } catch (IOException e) {
                InstabugSDKLogger.m1801e(this, "Failed to create", e);
            }
        }
        if (Charset.isSupported("UTF-8")) {
            this.charset = Charset.forName("UTF-8");
        } else {
            this.charset = Charset.defaultCharset();
        }
        if (!checkCacheValidity()) {
            invalidate();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:79:0x00b5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // com.instabug.library.internal.storage.cache.Cache
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<V> getValues() {
        /*
            Method dump skipped, instructions count: 202
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.instabug.library.internal.storage.cache.OnDiskCache.getValues():java.util.List");
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0045, code lost:
    
        r0.fromJson(getValue(r4));
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x004c, code lost:
    
        if (r2 == null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x004e, code lost:
    
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0086, code lost:
    
        r1 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0087, code lost:
    
        com.instabug.library.util.InstabugSDKLogger.m1801e(r7, "Failed to close file reader", r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0092, code lost:
    
        if (r2 == null) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0094, code lost:
    
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00b6, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00b7, code lost:
    
        com.instabug.library.util.InstabugSDKLogger.m1801e(r7, "Failed to close file reader", r0);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0136 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v13, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    @Override // com.instabug.library.internal.storage.cache.Cache
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public V get(java.lang.String r8) {
        /*
            Method dump skipped, instructions count: 330
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.instabug.library.internal.storage.cache.OnDiskCache.get(java.lang.String):com.instabug.library.internal.storage.cache.Cacheable");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0064  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x00da A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v11, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v13, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v15, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v18, types: [java.lang.String] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public V put(java.lang.String r9, V r10) {
        /*
            Method dump skipped, instructions count: 237
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.instabug.library.internal.storage.cache.OnDiskCache.put(java.lang.String, com.instabug.library.internal.storage.cache.Cacheable):com.instabug.library.internal.storage.cache.Cacheable");
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public V delete(String str) {
        V v;
        String readLine;
        if (!this.cacheFile.exists()) {
            InstabugSDKLogger.m1799d(this, "Cache file doesn't exist");
            return null;
        }
        File file = new File(this.cacheDir + "/cache.tmp");
        try {
            v = this.VClass.newInstance();
        } catch (IllegalAccessException e) {
            InstabugSDKLogger.m1801e(this, "IllegalAccessException went wrong while deleting value for key " + str, e);
            v = null;
        } catch (InstantiationException e2) {
            InstabugSDKLogger.m1801e(this, "InstantiationException happened while deleting value for key " + str, e2);
            v = null;
        }
        synchronized (this.cacheFile) {
            try {
                try {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.cacheFile), this.charset));
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), this.charset));
                        do {
                            readLine = bufferedReader.readLine();
                            String key = getKey(readLine);
                            if (key != null) {
                                if (key.equals(str)) {
                                    v.fromJson(getValue(readLine));
                                } else {
                                    bufferedWriter.write(readLine + IOUtils.LINE_SEPARATOR_UNIX);
                                }
                            }
                        } while (readLine != null);
                        bufferedReader.close();
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        if (this.cacheFile.delete() && !file.renameTo(this.cacheFile)) {
                            InstabugSDKLogger.m1800e(this, "Couldn't rename temp cache file");
                        }
                    } catch (IOException e3) {
                        InstabugSDKLogger.m1801e(this, "IOException went wrong while deleting value for key " + str, e3);
                    }
                } catch (OutOfMemoryError e4) {
                    InstabugSDKLogger.m1800e("OOM while deleting value for key " + str, e4.toString());
                }
            } catch (JSONException e5) {
                InstabugSDKLogger.m1801e(this, "JSONException went wrong while deleting value for key " + str, e5);
            }
        }
        return v;
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public long size() {
        long totalSpace;
        if (!this.cacheFile.exists()) {
            InstabugSDKLogger.m1799d(this, "Cache file doesn't exist");
            return -1L;
        }
        synchronized (this.cacheFile) {
            totalSpace = this.cacheFile.getTotalSpace();
        }
        return totalSpace;
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public void invalidate() {
        if (this.cacheFile.exists()) {
            InstabugSDKLogger.m1799d(this, "Cache file  exist");
            synchronized (this.cacheFile) {
                this.cacheFile.delete();
            }
        }
        try {
            this.cacheFile.createNewFile();
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(this, "Failed to create cache file", e);
        }
    }

    public String getValue(String str) {
        if (str == null || str.isEmpty() || !str.contains(":")) {
            return null;
        }
        return str.substring(str.indexOf(":") + 1);
    }

    public String getKey(String str) {
        if (str == null || str.isEmpty() || !str.contains(":")) {
            return null;
        }
        return str.substring(0, str.indexOf(":"));
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [boolean] */
    private boolean checkCacheValidity() {
        BufferedReader bufferedReader;
        boolean z = false;
        ?? exists = this.cacheFile.exists();
        if (exists == 0) {
            InstabugSDKLogger.m1799d(this, "Cache file doesn't exist");
        } else {
            synchronized (this.cacheFile) {
                BufferedReader bufferedReader2 = null;
                try {
                    try {
                        try {
                            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.cacheFile), this.charset));
                            try {
                                String readLine = bufferedReader.readLine();
                                if (readLine != null) {
                                    String value = getValue(readLine);
                                    if (!TextUtils.isEmpty(value)) {
                                        new JSONObject(value);
                                    }
                                }
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e) {
                                        InstabugSDKLogger.m1801e(this, "Failed to close file reader", e);
                                    }
                                }
                            } catch (IOException e2) {
                                e = e2;
                                InstabugSDKLogger.m1801e(this, "IOException went wrong while fetching values", e);
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e3) {
                                        InstabugSDKLogger.m1801e(this, "Failed to close file reader", e3);
                                    }
                                }
                                z = true;
                                return z;
                            } catch (OutOfMemoryError e4) {
                                e = e4;
                                bufferedReader2 = bufferedReader;
                                InstabugSDKLogger.m1800e("OOM while fetching values ", e.toString());
                                if (bufferedReader2 != null) {
                                    try {
                                        bufferedReader2.close();
                                    } catch (IOException e5) {
                                        InstabugSDKLogger.m1801e(this, "Failed to close file reader", e5);
                                    }
                                }
                                z = true;
                                return z;
                            } catch (JSONException e6) {
                                bufferedReader2 = bufferedReader;
                                e = e6;
                                InstabugSDKLogger.m1801e(this, "JSONException went wrong while fetching values", e);
                                if (bufferedReader2 != null) {
                                    try {
                                        bufferedReader2.close();
                                    } catch (IOException e7) {
                                        InstabugSDKLogger.m1801e(this, "Failed to close file reader", e7);
                                    }
                                }
                                return z;
                            }
                        } catch (IOException e8) {
                            e = e8;
                            bufferedReader = null;
                        } catch (OutOfMemoryError e9) {
                            e = e9;
                        } catch (JSONException e10) {
                            e = e10;
                        }
                        z = true;
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader2 = exists;
                        if (bufferedReader2 != null) {
                            try {
                                bufferedReader2.close();
                            } catch (IOException e11) {
                                InstabugSDKLogger.m1801e(this, "Failed to close file reader", e11);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }
        return z;
    }
}
