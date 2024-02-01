package org.jcodec.api.android;

import android.graphics.Bitmap;
import java.io.File;
import java.io.IOException;
import org.jcodec.api.JCodecException;
import org.jcodec.api.specific.ContainerAdaptor;
import org.jcodec.common.AndroidUtil;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class FrameGrab extends org.jcodec.api.FrameGrab {
    public FrameGrab(SeekableByteChannel in) throws IOException, JCodecException {
        super(in);
    }

    public FrameGrab(SeekableDemuxerTrack videoTrack, ContainerAdaptor decoder) {
        super(videoTrack, decoder);
    }

    public static Bitmap getFrame(File file, double second) throws IOException, JCodecException {
        FileChannelWrapper ch = null;
        try {
            ch = NIOUtils.readableFileChannel(file);
            return ((FrameGrab) new FrameGrab(ch).seekToSecondPrecise(second)).getFrame();
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    public static Bitmap getFrame(SeekableByteChannel file, double second) throws JCodecException, IOException {
        return ((FrameGrab) new FrameGrab(file).seekToSecondPrecise(second)).getFrame();
    }

    public Bitmap getFrame() throws IOException {
        return AndroidUtil.toBitmap(getNativeFrame());
    }

    public void getFrame(Bitmap bmp) throws IOException {
        Picture picture = getNativeFrame();
        AndroidUtil.toBitmap(picture, bmp);
    }

    public static Bitmap getFrame(File file, int frameNumber) throws IOException, JCodecException {
        FileChannelWrapper ch = null;
        try {
            ch = NIOUtils.readableFileChannel(file);
            return ((FrameGrab) new FrameGrab(ch).seekToFramePrecise(frameNumber)).getFrame();
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    public static Bitmap getFrame(SeekableByteChannel file, int frameNumber) throws JCodecException, IOException {
        return ((FrameGrab) new FrameGrab(file).seekToFramePrecise(frameNumber)).getFrame();
    }

    public static Bitmap getFrame(SeekableDemuxerTrack vt, ContainerAdaptor decoder, int frameNumber) throws IOException, JCodecException {
        return ((FrameGrab) new FrameGrab(vt, decoder).seekToFramePrecise(frameNumber)).getFrame();
    }

    public static Bitmap getFrame(SeekableDemuxerTrack vt, ContainerAdaptor decoder, double second) throws IOException, JCodecException {
        return ((FrameGrab) new FrameGrab(vt, decoder).seekToSecondPrecise(second)).getFrame();
    }

    public static Bitmap getFrameSloppy(SeekableDemuxerTrack vt, ContainerAdaptor decoder, int frameNumber) throws IOException, JCodecException {
        return ((FrameGrab) new FrameGrab(vt, decoder).seekToFrameSloppy(frameNumber)).getFrame();
    }

    public static Bitmap getFrameSloppy(SeekableDemuxerTrack vt, ContainerAdaptor decoder, double second) throws IOException, JCodecException {
        return ((FrameGrab) new FrameGrab(vt, decoder).seekToSecondSloppy(second)).getFrame();
    }
}
