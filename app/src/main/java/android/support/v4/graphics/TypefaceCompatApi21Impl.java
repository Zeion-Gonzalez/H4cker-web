package android.support.v4.graphics;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.provider.FontsContractCompat;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RequiresApi(21)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl {
    private static final String TAG = "TypefaceCompatApi21Impl";

    private File getFile(ParcelFileDescriptor fd) {
        try {
            String path = Os.readlink("/proc/self/fd/" + fd.getFd());
            if (OsConstants.S_ISREG(Os.stat(path).st_mode)) {
                return new File(path);
            }
            return null;
        } catch (ErrnoException e) {
            return null;
        }
    }

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 2 */
    @Override // android.support.v4.graphics.TypefaceCompatBaseImpl, android.support.v4.graphics.TypefaceCompat.TypefaceCompatImpl
    public Typeface createFromFontInfo(Context context, CancellationSignal cancellationSignal, @NonNull FontsContractCompat.FontInfo[] fonts, int style) {
        if (fonts.length < 1) {
            return null;
        }
        FontsContractCompat.FontInfo bestFont = findBestInfo(fonts, style);
        ContentResolver resolver = context.getContentResolver();
        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(bestFont.getUri(), "r", cancellationSignal);
            Throwable th = null;
            try {
                File file = getFile(pfd);
                if (file != null && file.canRead()) {
                    Typeface createFromFile = Typeface.createFromFile(file);
                    if (pfd == null) {
                        return createFromFile;
                    }
                    if (0 == 0) {
                        pfd.close();
                        return createFromFile;
                    }
                    try {
                        pfd.close();
                        return createFromFile;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return createFromFile;
                    }
                }
                FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
                Throwable th3 = null;
                try {
                    Typeface createFromInputStream = super.createFromInputStream(context, fis);
                    if (fis != null) {
                        if (0 != 0) {
                            try {
                                fis.close();
                            } catch (Throwable th4) {
                                th3.addSuppressed(th4);
                            }
                        } else {
                            fis.close();
                        }
                    }
                    if (pfd == null) {
                        return createFromInputStream;
                    }
                    if (0 == 0) {
                        pfd.close();
                        return createFromInputStream;
                    }
                    try {
                        pfd.close();
                        return createFromInputStream;
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                        return createFromInputStream;
                    }
                } finally {
                }
            } catch (Throwable th6) {
                try {
                    throw th6;
                } catch (Throwable th7) {
                    if (pfd == null) {
                        throw th7;
                    }
                    if (th6 == null) {
                        pfd.close();
                        throw th7;
                    }
                    try {
                        pfd.close();
                        throw th7;
                    } catch (Throwable th8) {
                        th6.addSuppressed(th8);
                        throw th7;
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
