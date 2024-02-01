package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SampleSizesBox extends FullBox {
    private int count;
    private int defaultSize;
    private int[] sizes;

    public static String fourcc() {
        return "stsz";
    }

    public SampleSizesBox(int defaultSize, int count) {
        this();
        this.defaultSize = defaultSize;
        this.count = count;
    }

    public SampleSizesBox(int[] sizes) {
        this();
        this.sizes = sizes;
    }

    public SampleSizesBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.defaultSize = input.getInt();
        this.count = input.getInt();
        if (this.defaultSize == 0) {
            this.sizes = new int[this.count];
            for (int i = 0; i < this.count; i++) {
                this.sizes[i] = input.getInt();
            }
        }
    }

    public int getDefaultSize() {
        return this.defaultSize;
    }

    public int[] getSizes() {
        return this.sizes;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.defaultSize);
        if (this.defaultSize == 0) {
            out.putInt(this.sizes.length);
            int[] arr$ = this.sizes;
            for (long size : arr$) {
                out.putInt((int) size);
            }
            return;
        }
        out.putInt(this.count);
    }
}
