package org.jcodec.codecs.mpeg4.mp4;

import java.nio.ByteBuffer;
import org.jcodec.codecs.aac.ADTSParser;
import org.jcodec.codecs.mpeg4.es.C0865ES;
import org.jcodec.codecs.mpeg4.es.C0866SL;
import org.jcodec.codecs.mpeg4.es.DecoderConfig;
import org.jcodec.codecs.mpeg4.es.DecoderSpecific;
import org.jcodec.codecs.mpeg4.es.Descriptor;
import org.jcodec.common.io.BitWriter;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.FullBox;
import org.jcodec.containers.mp4.boxes.Header;

/* loaded from: classes.dex */
public class EsdsBox extends FullBox {
    private int avgBitrate;
    private int bufSize;
    private int maxBitrate;
    private int objectType;
    private ByteBuffer streamInfo;
    private int trackId;

    public static String fourcc() {
        return "esds";
    }

    public EsdsBox(Header atom) {
        super(atom);
    }

    public EsdsBox() {
        super(new Header(fourcc()));
    }

    public EsdsBox(ByteBuffer streamInfo, int objectType, int bufSize, int maxBitrate, int avgBitrate, int trackId) {
        super(new Header(fourcc()));
        this.objectType = objectType;
        this.bufSize = bufSize;
        this.maxBitrate = maxBitrate;
        this.avgBitrate = avgBitrate;
        this.trackId = trackId;
        this.streamInfo = streamInfo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        if (this.streamInfo != null && this.streamInfo.remaining() > 0) {
            new C0865ES(this.trackId, new DecoderConfig(this.objectType, this.bufSize, this.maxBitrate, this.avgBitrate, new DecoderSpecific(this.streamInfo)), new C0866SL()).write(out);
        } else {
            new C0865ES(this.trackId, new DecoderConfig(this.objectType, this.bufSize, this.maxBitrate, this.avgBitrate, new Descriptor[0]), new C0866SL()).write(out);
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        C0865ES es = (C0865ES) Descriptor.read(input);
        this.trackId = es.getTrackId();
        DecoderConfig decoderConfig = (DecoderConfig) Descriptor.find(es, DecoderConfig.class, DecoderConfig.tag());
        this.objectType = decoderConfig.getObjectType();
        this.bufSize = decoderConfig.getBufSize();
        this.maxBitrate = decoderConfig.getMaxBitrate();
        this.avgBitrate = decoderConfig.getAvgBitrate();
        DecoderSpecific decoderSpecific = (DecoderSpecific) Descriptor.find(decoderConfig, DecoderSpecific.class, DecoderSpecific.tag());
        this.streamInfo = decoderSpecific.getData();
    }

    public ByteBuffer getStreamInfo() {
        return this.streamInfo;
    }

    public int getObjectType() {
        return this.objectType;
    }

    public int getBufSize() {
        return this.bufSize;
    }

    public int getMaxBitrate() {
        return this.maxBitrate;
    }

    public int getAvgBitrate() {
        return this.avgBitrate;
    }

    public int getTrackId() {
        return this.trackId;
    }

    public static Box fromADTS(ADTSParser.Header hdr) {
        ByteBuffer si = ByteBuffer.allocate(2);
        BitWriter wr = new BitWriter(si);
        wr.writeNBit(hdr.getObjectType(), 5);
        wr.writeNBit(hdr.getSamplingIndex(), 4);
        wr.writeNBit(hdr.getChanConfig(), 4);
        wr.flush();
        si.clear();
        return new EsdsBox(si, hdr.getObjectType() << 5, 0, 210750, 133350, 2);
    }
}
