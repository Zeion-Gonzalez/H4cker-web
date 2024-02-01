package com.instabug.library.internal.video;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import com.instabug.library.Instabug;
import com.instabug.library.internal.storage.AttachmentManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/* loaded from: classes.dex */
public class InstabugVideoUtils {
    private static final String TAG = "InstabugVideoUtils";

    @TargetApi(18)
    public String mux(String str, String str2) throws IOException {
        File videoFile = AttachmentManager.getVideoFile(Instabug.getApplicationContext());
        String absolutePath = videoFile.getAbsolutePath();
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(str2);
        MediaExtractor mediaExtractor2 = new MediaExtractor();
        mediaExtractor2.setDataSource(str);
        InstabugSDKLogger.m1802i(this, "Video Extractor Track Count " + mediaExtractor.getTrackCount());
        InstabugSDKLogger.m1802i(this, "Audio Extractor Track Count " + mediaExtractor2.getTrackCount());
        InstabugSDKLogger.m1802i(this, "Video Extractor Track duration " + mediaExtractor.getCachedDuration());
        InstabugSDKLogger.m1802i(this, "Audio Extractor Track duration " + mediaExtractor2.getCachedDuration());
        MediaMuxer mediaMuxer = new MediaMuxer(absolutePath, 0);
        mediaExtractor.selectTrack(0);
        MediaFormat trackFormat = mediaExtractor.getTrackFormat(0);
        int addTrack = mediaMuxer.addTrack(trackFormat);
        mediaExtractor2.selectTrack(0);
        MediaFormat trackFormat2 = mediaExtractor2.getTrackFormat(0);
        int addTrack2 = mediaMuxer.addTrack(trackFormat2);
        InstabugSDKLogger.m1802i(this, "Video Format " + trackFormat.toString());
        InstabugSDKLogger.m1802i(this, "Audio Format " + trackFormat2.toString());
        boolean z = false;
        ByteBuffer allocate = ByteBuffer.allocate(262144);
        ByteBuffer allocate2 = ByteBuffer.allocate(262144);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo bufferInfo2 = new MediaCodec.BufferInfo();
        mediaExtractor.seekTo(0L, 0);
        mediaExtractor2.seekTo(0L, 1);
        mediaMuxer.start();
        int i = 0;
        while (!z) {
            bufferInfo.offset = 100;
            bufferInfo.size = mediaExtractor.readSampleData(allocate, 100);
            if (bufferInfo.size < 0 || bufferInfo2.size < 0) {
                InstabugSDKLogger.m1802i(this, "saw input EOS.");
                bufferInfo.size = 0;
                z = true;
            } else {
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(addTrack, allocate, bufferInfo);
                mediaExtractor.advance();
                int i2 = i + 1;
                InstabugSDKLogger.m1802i("ContentValues", "Frame (" + i2 + ") Video PresentationTimeUs:" + bufferInfo.presentationTimeUs + " Flags:" + bufferInfo.flags + " Size(KB) " + (bufferInfo.size / 1024));
                InstabugSDKLogger.m1802i(this, "Frame (" + i2 + ") Audio PresentationTimeUs:" + bufferInfo2.presentationTimeUs + " Flags:" + bufferInfo2.flags + " Size(KB) " + (bufferInfo2.size / 1024));
                i = i2;
            }
        }
        boolean z2 = false;
        int i3 = 0;
        while (!z2) {
            i3++;
            bufferInfo2.offset = 100;
            bufferInfo2.size = mediaExtractor2.readSampleData(allocate2, 100);
            if (bufferInfo.size < 0 || bufferInfo2.size < 0) {
                InstabugSDKLogger.m1802i(this, "saw input EOS.");
                z2 = true;
                bufferInfo2.size = 0;
            } else {
                bufferInfo2.presentationTimeUs = mediaExtractor2.getSampleTime();
                bufferInfo2.flags = mediaExtractor2.getSampleFlags();
                mediaMuxer.writeSampleData(addTrack2, allocate2, bufferInfo2);
                mediaExtractor2.advance();
                InstabugSDKLogger.m1802i(this, "Frame (" + i + ") Video PresentationTimeUs:" + bufferInfo.presentationTimeUs + " Flags:" + bufferInfo.flags + " Size(KB) " + (bufferInfo.size / 1024));
                InstabugSDKLogger.m1802i(this, "Frame (" + i + ") Audio PresentationTimeUs:" + bufferInfo2.presentationTimeUs + " Flags:" + bufferInfo2.flags + " Size(KB) " + (bufferInfo2.size / 1024));
            }
        }
        mediaMuxer.stop();
        mediaMuxer.release();
        return videoFile.getAbsolutePath();
    }

    public static int getVideoDuration(String str) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(str);
        return Integer.parseInt(mediaMetadataRetriever.extractMetadata(9));
    }

    public static boolean isDurationMoreThanAutoRecMaxDuration(int i, int i2) {
        return i > i2;
    }

    public static File startTrim(File file, File file2, int i) throws IOException {
        if (!isDurationMoreThanAutoRecMaxDuration(getVideoDuration(file.getPath()), i)) {
            return file;
        }
        int videoDuration = getVideoDuration(file.getPath());
        genVideoUsingMuxer(file.getPath(), file2.getPath(), videoDuration - i, videoDuration, false, true);
        return file2;
    }

    @TargetApi(21)
    private static void genVideoUsingMuxer(String str, String str2, int i, int i2, boolean z, boolean z2) throws IOException {
        int parseInt;
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(str);
        int trackCount = mediaExtractor.getTrackCount();
        MediaMuxer mediaMuxer = new MediaMuxer(str2, 0);
        HashMap hashMap = new HashMap(trackCount);
        int i3 = -1;
        for (int i4 = 0; i4 < trackCount; i4++) {
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(i4);
            String string = trackFormat.getString("mime");
            boolean z3 = false;
            if (string.startsWith("audio/") && z) {
                z3 = true;
            } else if (string.startsWith("video/") && z2) {
                z3 = true;
            }
            if (z3) {
                mediaExtractor.selectTrack(i4);
                hashMap.put(Integer.valueOf(i4), Integer.valueOf(mediaMuxer.addTrack(trackFormat)));
                if (trackFormat.containsKey("max-input-size")) {
                    int integer = trackFormat.getInteger("max-input-size");
                    if (integer <= i3) {
                        integer = i3;
                    }
                    i3 = integer;
                }
            }
        }
        if (i3 < 0) {
            i3 = 4096;
        }
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(str);
        String extractMetadata = mediaMetadataRetriever.extractMetadata(24);
        if (extractMetadata != null && (parseInt = Integer.parseInt(extractMetadata)) >= 0) {
            mediaMuxer.setOrientationHint(parseInt);
        }
        if (i > 0) {
            mediaExtractor.seekTo(i * 1000, 2);
        }
        ByteBuffer allocate = ByteBuffer.allocate(i3);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        try {
            mediaMuxer.start();
            while (true) {
                bufferInfo.offset = 0;
                bufferInfo.size = mediaExtractor.readSampleData(allocate, 0);
                if (bufferInfo.size < 0) {
                    InstabugSDKLogger.m1799d(TAG, "Saw input EOS.");
                    bufferInfo.size = 0;
                    break;
                }
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                if (i2 > 0 && bufferInfo.presentationTimeUs > i2 * 1000) {
                    InstabugSDKLogger.m1799d(TAG, "The current sample is over the trim end time.");
                    break;
                } else {
                    bufferInfo.flags = mediaExtractor.getSampleFlags();
                    mediaMuxer.writeSampleData(((Integer) hashMap.get(Integer.valueOf(mediaExtractor.getSampleTrackIndex()))).intValue(), allocate, bufferInfo);
                    mediaExtractor.advance();
                }
            }
            mediaMuxer.stop();
            new File(str).delete();
        } catch (IllegalStateException e) {
            InstabugSDKLogger.m1804w(TAG, "The source video file is malformed");
        } finally {
            mediaMuxer.release();
        }
    }
}
