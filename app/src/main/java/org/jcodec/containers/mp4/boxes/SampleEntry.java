package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SampleEntry extends NodeBox {
    private short drefInd;

    public SampleEntry(Header header) {
        super(header);
    }

    public SampleEntry(Header header, short drefInd) {
        super(header);
        this.drefInd = drefInd;
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        input.getInt();
        input.getShort();
        this.drefInd = input.getShort();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void parseExtensions(ByteBuffer input) {
        super.parse(input);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        out.put(new byte[]{0, 0, 0, 0, 0, 0});
        out.putShort(this.drefInd);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeExtensions(ByteBuffer out) {
        super.doWrite(out);
    }

    public short getDrefInd() {
        return this.drefInd;
    }

    public void setDrefInd(short ind) {
        this.drefInd = ind;
    }

    public void setMediaType(String mediaType) {
        this.header = new Header(mediaType);
    }
}
