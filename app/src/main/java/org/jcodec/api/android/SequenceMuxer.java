package org.jcodec.api.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.IOException;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

/* loaded from: classes.dex */
public class SequenceMuxer {

    /* renamed from: ch */
    private SeekableByteChannel f1441ch;
    private int frameNo;
    private MP4Muxer muxer;
    private FramesMP4MuxerTrack outTrack;
    private Size size;

    public SequenceMuxer(File out) throws IOException {
        this.f1441ch = NIOUtils.writableFileChannel(out);
        this.muxer = new MP4Muxer(this.f1441ch, Brand.MP4);
        this.outTrack = this.muxer.addTrack(TrackType.VIDEO, 25);
    }

    public void encodeImage(File png) throws IOException {
        if (this.size == null) {
            Bitmap read = BitmapFactory.decodeFile(png.getAbsolutePath());
            this.size = new Size(read.getWidth(), read.getHeight());
        }
        this.outTrack.addFrame(new MP4Packet(NIOUtils.fetchFrom(png), this.frameNo, 25L, 1L, this.frameNo, true, null, this.frameNo, 0));
        this.frameNo++;
    }

    public void finish() throws IOException {
        this.outTrack.addSampleEntry(MP4Muxer.videoSampleEntry("png ", this.size, "JCodec"));
        this.muxer.writeHeader();
        NIOUtils.closeQuietly(this.f1441ch);
    }
}
