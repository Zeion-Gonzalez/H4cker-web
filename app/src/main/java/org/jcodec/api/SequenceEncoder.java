package org.jcodec.api;

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

/* loaded from: classes.dex */
public class SequenceEncoder {

    /* renamed from: ch */
    private SeekableByteChannel f1440ch;
    private int frameNo;
    private MP4Muxer muxer;
    private FramesMP4MuxerTrack outTrack;
    private Picture toEncode;
    private ByteBuffer _out = ByteBuffer.allocate(12441600);
    private H264Encoder encoder = new H264Encoder();
    private Transform transform = ColorUtil.getTransform(ColorSpace.RGB, this.encoder.getSupportedColorSpaces()[0]);
    private ArrayList<ByteBuffer> spsList = new ArrayList<>();
    private ArrayList<ByteBuffer> ppsList = new ArrayList<>();

    public SequenceEncoder(File out) throws IOException {
        this.f1440ch = NIOUtils.writableFileChannel(out);
        this.muxer = new MP4Muxer(this.f1440ch, Brand.MP4);
        this.outTrack = this.muxer.addTrack(TrackType.VIDEO, 25);
    }

    public void encodeNativeFrame(Picture pic) throws IOException {
        if (this.toEncode == null) {
            this.toEncode = Picture.create(pic.getWidth(), pic.getHeight(), this.encoder.getSupportedColorSpaces()[0]);
        }
        this.transform.transform(pic, this.toEncode);
        this._out.clear();
        ByteBuffer result = this.encoder.encodeFrame(this.toEncode, this._out);
        this.spsList.clear();
        this.ppsList.clear();
        H264Utils.wipePS(result, this.spsList, this.ppsList);
        H264Utils.encodeMOVPacket(result);
        this.outTrack.addFrame(new MP4Packet(result, this.frameNo, 25L, 1L, this.frameNo, true, null, this.frameNo, 0));
        this.frameNo++;
    }

    public void finish() throws IOException {
        this.outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(this.spsList, this.ppsList, 4));
        this.muxer.writeHeader();
        NIOUtils.closeQuietly(this.f1440ch);
    }
}
