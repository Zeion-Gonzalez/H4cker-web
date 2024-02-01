package com.instabug.library.internal.video;

import android.net.Uri;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;

/* loaded from: classes.dex */
public class AutoScreenRecordingFileHolder {
    private static final AutoScreenRecordingFileHolder INSTANCE = new AutoScreenRecordingFileHolder();
    private File autoScreenRecordingFile;

    public static AutoScreenRecordingFileHolder getInstance() {
        return INSTANCE;
    }

    public Uri getAutoScreenRecordingFileUri() {
        if (this.autoScreenRecordingFile != null) {
            return Uri.fromFile(this.autoScreenRecordingFile);
        }
        return null;
    }

    public void setAutoScreenRecordingFile(File file) {
        this.autoScreenRecordingFile = file;
    }

    public boolean delete() {
        if (this.autoScreenRecordingFile == null) {
            return true;
        }
        if (this.autoScreenRecordingFile.delete()) {
            InstabugSDKLogger.m1799d(this, "Screen recording file deleted");
            return true;
        }
        return false;
    }

    public void clear() {
        this.autoScreenRecordingFile = null;
    }
}
