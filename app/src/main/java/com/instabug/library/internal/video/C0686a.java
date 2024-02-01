package com.instabug.library.internal.video;

import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

/* compiled from: InstabugSequenceEncoder.java */
/* renamed from: com.instabug.library.internal.video.a */
/* loaded from: classes.dex */
public class C0686a {

    /* renamed from: a */
    private SeekableByteChannel f873a;

    /* renamed from: b */
    private Picture f874b;

    /* renamed from: g */
    private FramesMP4MuxerTrack f879g;

    /* renamed from: i */
    private int f881i;

    /* renamed from: j */
    private MP4Muxer f882j;

    /* renamed from: h */
    private ByteBuffer f880h = ByteBuffer.allocate(12441600);

    /* renamed from: d */
    private H264Encoder f876d = new H264Encoder();

    /* renamed from: c */
    private Transform f875c = ColorUtil.getTransform(ColorSpace.RGB, this.f876d.getSupportedColorSpaces()[0]);

    /* renamed from: e */
    private ArrayList<ByteBuffer> f877e = new ArrayList<>();

    /* renamed from: f */
    private ArrayList<ByteBuffer> f878f = new ArrayList<>();

    public C0686a(File file) throws IOException {
        this.f873a = NIOUtils.writableFileChannel(file);
        this.f882j = new MP4Muxer(this.f873a, Brand.MP4);
        this.f879g = this.f882j.addTrack(TrackType.VIDEO, 8);
    }

    /* renamed from: a */
    public void m1346a(Picture picture) throws IOException {
        if (this.f874b == null) {
            this.f874b = Picture.create(picture.getWidth(), picture.getHeight(), this.f876d.getSupportedColorSpaces()[0]);
        }
        this.f875c.transform(picture, this.f874b);
        this.f880h.clear();
        ByteBuffer encodeFrame = this.f876d.encodeFrame(this.f874b, this.f880h);
        this.f877e.clear();
        this.f878f.clear();
        H264Utils.wipePS(encodeFrame, this.f877e, this.f878f);
        H264Utils.encodeMOVPacket(encodeFrame);
        this.f879g.addFrame(new MP4Packet(encodeFrame, this.f881i * 2, 8L, 1L, this.f881i, true, null, this.f881i * 2, 0));
        this.f881i++;
    }

    /* renamed from: a */
    public void m1345a() throws IOException, OutOfMemoryError {
        try {
            this.f879g.addSampleEntry(H264Utils.createMOVSampleEntry(this.f877e, this.f878f, 4));
            this.f882j.writeHeader();
        } catch (IndexOutOfBoundsException e) {
            InstabugSDKLogger.wtf(this, "Something went wrong while generating video", e);
        }
        NIOUtils.closeQuietly(this.f873a);
    }
}
