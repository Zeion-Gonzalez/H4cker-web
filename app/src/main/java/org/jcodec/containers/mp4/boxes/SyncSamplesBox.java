package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SyncSamplesBox extends FullBox {
    private int[] syncSamples;

    public static String fourcc() {
        return "stss";
    }

    public SyncSamplesBox() {
        super(new Header(fourcc()));
    }

    public SyncSamplesBox(int[] array) {
        this();
        this.syncSamples = array;
    }

    public SyncSamplesBox(Header header) {
        super(header);
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        int len = input.getInt();
        this.syncSamples = new int[len];
        for (int i = 0; i < len; i++) {
            this.syncSamples[i] = input.getInt();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.syncSamples.length);
        for (int i = 0; i < this.syncSamples.length; i++) {
            out.putInt(this.syncSamples[i]);
        }
    }

    public int[] getSyncSamples() {
        return this.syncSamples;
    }
}
