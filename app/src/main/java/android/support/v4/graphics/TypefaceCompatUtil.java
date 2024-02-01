package android.support.v4.graphics;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    public static File getTempFile(Context context) {
        String prefix = CACHE_FILE_PREFIX + Process.myPid() + "-" + Process.myTid() + "-";
        for (int i = 0; i < 100; i++) {
            File file = new File(context.getCacheDir(), prefix + i);
            if (file.createNewFile()) {
                return file;
            }
        }
        return null;
    }

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 2 */
    @RequiresApi(19)
    private static ByteBuffer mmap(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            Throwable th = null;
            try {
                FileChannel channel = fis.getChannel();
                long size = channel.size();
                MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, size);
                if (fis == null) {
                    return map;
                }
                if (0 == 0) {
                    fis.close();
                    return map;
                }
                try {
                    fis.close();
                    return map;
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                    return map;
                }
            } catch (Throwable th3) {
                try {
                    throw th3;
                } catch (Throwable th4) {
                    if (fis == null) {
                        throw th4;
                    }
                    if (th3 == null) {
                        fis.close();
                        throw th4;
                    }
                    try {
                        fis.close();
                        throw th4;
                    } catch (Throwable th5) {
                        th3.addSuppressed(th5);
                        throw th4;
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    /* JADX WARN: Unreachable blocks removed: 4, instructions: 4 */
    @RequiresApi(19)
    public static ByteBuffer mmap(Context context, CancellationSignal cancellationSignal, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r", cancellationSignal);
            Throwable th = null;
            try {
                FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
                Throwable th2 = null;
                try {
                    FileChannel channel = fis.getChannel();
                    long size = channel.size();
                    MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, size);
                    if (fis != null) {
                        if (0 != 0) {
                            try {
                                fis.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            fis.close();
                        }
                    }
                    if (pfd == null) {
                        return map;
                    }
                    if (0 == 0) {
                        pfd.close();
                        return map;
                    }
                    try {
                        pfd.close();
                        return map;
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                        return map;
                    }
                } catch (Throwable th5) {
                    try {
                        throw th5;
                    } catch (Throwable th6) {
                        if (fis == null) {
                            throw th6;
                        }
                        if (th5 == null) {
                            fis.close();
                            throw th6;
                        }
                        try {
                            fis.close();
                            throw th6;
                        } catch (Throwable th7) {
                            th5.addSuppressed(th7);
                            throw th6;
                        }
                    }
                }
            } catch (Throwable th8) {
                try {
                    throw th8;
                } catch (Throwable th9) {
                    if (pfd == null) {
                        throw th9;
                    }
                    if (th8 == null) {
                        pfd.close();
                        throw th9;
                    }
                    try {
                        pfd.close();
                        throw th9;
                    } catch (Throwable th10) {
                        th8.addSuppressed(th10);
                        throw th9;
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    @RequiresApi(19)
    public static ByteBuffer copyToDirectBuffer(Context context, Resources res, int id) {
        ByteBuffer byteBuffer = null;
        File tmpFile = getTempFile(context);
        if (tmpFile != null) {
            try {
                if (copyToFile(tmpFile, res, id)) {
                    byteBuffer = mmap(tmpFile);
                }
            } finally {
                tmpFile.delete();
            }
        }
        return byteBuffer;
    }

    public static boolean copyToFile(File file, InputStream is) {
        FileOutputStream os = null;
        try {
            try {
                FileOutputStream os2 = new FileOutputStream(file, false);
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int readLen = is.read(buffer);
                        if (readLen != -1) {
                            os2.write(buffer, 0, readLen);
                        } else {
                            closeQuietly(os2);
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e = e;
                    os = os2;
                    Log.e(TAG, "Error copying resource contents to temp file: " + e.getMessage());
                    closeQuietly(os);
                    return false;
                } catch (Throwable th) {
                    th = th;
                    os = os2;
                    closeQuietly(os);
                    throw th;
                }
            } catch (IOException e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean copyToFile(File file, Resources res, int id) {
        InputStream is = null;
        try {
            is = res.openRawResource(id);
            return copyToFile(file, is);
        } finally {
            closeQuietly(is);
        }
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }
}
