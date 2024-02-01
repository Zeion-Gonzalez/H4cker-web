package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class TimecodeSampleEntry extends SampleEntry {
    private static final MyFactory FACTORY = new MyFactory();
    public static final int FLAG_24HOURMAX = 2;
    public static final int FLAG_COUNTER = 8;
    public static final int FLAG_DROPFRAME = 1;
    public static final int FLAG_NEGATIVETIMEOK = 4;
    private int flags;
    private int frameDuration;
    private byte numFrames;
    private int timescale;

    public TimecodeSampleEntry(Header header) {
        super(header);
        this.factory = FACTORY;
    }

    public TimecodeSampleEntry() {
        super(new Header("tmcd"));
        this.factory = FACTORY;
    }

    public TimecodeSampleEntry(int flags, int timescale, int frameDuration, int numFrames) {
        super(new Header("tmcd"));
        this.flags = flags;
        this.timescale = timescale;
        this.frameDuration = frameDuration;
        this.numFrames = (byte) numFrames;
    }

    @Override // org.jcodec.containers.mp4.boxes.SampleEntry, org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        NIOUtils.skip(input, 4);
        this.flags = input.getInt();
        this.timescale = input.getInt();
        this.frameDuration = input.getInt();
        this.numFrames = input.get();
        NIOUtils.skip(input, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.SampleEntry, org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(0);
        out.putInt(this.flags);
        out.putInt(this.timescale);
        out.putInt(this.frameDuration);
        out.put(this.numFrames);
        out.put((byte) -49);
    }

    /* loaded from: classes.dex */
    public static class MyFactory extends BoxFactory {
        private Map<String, Class<? extends Box>> mappings = new HashMap();

        @Override // org.jcodec.containers.mp4.boxes.BoxFactory
        public Class<? extends Box> toClass(String fourcc) {
            return this.mappings.get(fourcc);
        }
    }

    public int getFlags() {
        return this.flags;
    }

    public int getTimescale() {
        return this.timescale;
    }

    public int getFrameDuration() {
        return this.frameDuration;
    }

    public byte getNumFrames() {
        return this.numFrames;
    }

    public boolean isDropFrame() {
        return (this.flags & 1) != 0;
    }
}
