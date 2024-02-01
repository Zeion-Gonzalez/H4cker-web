package com.instabug.library.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

/* loaded from: classes.dex */
public class VideoManipulationUtils {
    public static Bitmap extractVideoFrame(String str) throws IllegalArgumentException {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(str);
        return mediaMetadataRetriever.getFrameAtTime();
    }

    public static Bitmap extractFirstVideoFrame(String str) throws IllegalArgumentException {
        return extractVideoFrame(str);
    }
}
