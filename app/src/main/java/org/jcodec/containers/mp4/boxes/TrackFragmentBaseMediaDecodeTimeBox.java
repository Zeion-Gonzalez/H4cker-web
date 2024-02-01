package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class TrackFragmentBaseMediaDecodeTimeBox extends FullBox {
    private long baseMediaDecodeTime;

    public TrackFragmentBaseMediaDecodeTimeBox() {
        super(new Header(fourcc()));
    }

    public TrackFragmentBaseMediaDecodeTimeBox(long baseMediaDecodeTime) {
        this();
        this.baseMediaDecodeTime = baseMediaDecodeTime;
        if (this.baseMediaDecodeTime > 2147483647L) {
            this.version = (byte) 1;
        }
    }

    public static String fourcc() {
        return "tfdt";
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        if (this.version == 0) {
            this.baseMediaDecodeTime = input.getInt();
        } else {
            if (this.version == 1) {
                this.baseMediaDecodeTime = input.getLong();
                return;
            }
            throw new RuntimeException("Unsupported tfdt version");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        if (this.version == 0) {
            out.putInt((int) this.baseMediaDecodeTime);
        } else {
            if (this.version == 1) {
                out.putLong(this.baseMediaDecodeTime);
                return;
            }
            throw new RuntimeException("Unsupported tfdt version");
        }
    }

    public long getBaseMediaDecodeTime() {
        return this.baseMediaDecodeTime;
    }

    public static Factory copy(TrackFragmentBaseMediaDecodeTimeBox other) {
        return new Factory(other);
    }

    /* loaded from: classes.dex */
    public static class Factory {
        private TrackFragmentBaseMediaDecodeTimeBox box;

        protected Factory(TrackFragmentBaseMediaDecodeTimeBox other) {
            this.box = new TrackFragmentBaseMediaDecodeTimeBox(other.baseMediaDecodeTime);
            this.box.version = other.version;
            this.box.flags = other.flags;
        }

        public Factory baseMediaDecodeTime(long val) {
            this.box.baseMediaDecodeTime = val;
            return this;
        }

        public TrackFragmentBaseMediaDecodeTimeBox create() {
            try {
                return this.box;
            } finally {
                this.box = null;
            }
        }
    }
}
