package org.jcodec.codecs.mpeg4.es;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class DecoderConfig extends NodeDescriptor {
    private int avgBitrate;
    private int bufSize;
    private int maxBitrate;
    private int objectType;

    public DecoderConfig(int tag, int size) {
        super(tag, size);
    }

    public DecoderConfig(int objectType, int bufSize, int maxBitrate, int avgBitrate, Descriptor... children) {
        super(tag(), children);
        this.objectType = objectType;
        this.bufSize = bufSize;
        this.maxBitrate = maxBitrate;
        this.avgBitrate = avgBitrate;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg4.es.NodeDescriptor, org.jcodec.codecs.mpeg4.es.Descriptor
    public void parse(ByteBuffer input) {
        this.objectType = input.get() & 255;
        input.get();
        this.bufSize = ((input.get() & 255) << 16) | (input.getShort() & 65535);
        this.maxBitrate = input.getInt();
        this.avgBitrate = input.getInt();
        super.parse(input);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg4.es.NodeDescriptor, org.jcodec.codecs.mpeg4.es.Descriptor
    public void doWrite(ByteBuffer out) {
        out.put((byte) this.objectType);
        out.put((byte) 21);
        out.put((byte) (this.bufSize >> 16));
        out.putShort((short) this.bufSize);
        out.putInt(this.maxBitrate);
        out.putInt(this.avgBitrate);
        super.doWrite(out);
    }

    public static int tag() {
        return 4;
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
}
