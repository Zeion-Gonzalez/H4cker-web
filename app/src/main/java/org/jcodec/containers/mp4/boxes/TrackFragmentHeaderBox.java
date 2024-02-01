package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class TrackFragmentHeaderBox extends FullBox {
    public static final int FLAG_BASE_DATA_OFFSET = 1;
    public static final int FLAG_DEFAILT_SAMPLE_DURATION = 8;
    public static final int FLAG_DEFAILT_SAMPLE_FLAGS = 32;
    public static final int FLAG_DEFAULT_SAMPLE_SIZE = 16;
    public static final int FLAG_SAMPLE_DESCRIPTION_INDEX = 2;
    private long baseDataOffset;
    private int defaultSampleDuration;
    private int defaultSampleFlags;
    private int defaultSampleSize;
    private int sampleDescriptionIndex;
    private int trackId;

    public static String fourcc() {
        return "tfhd";
    }

    public TrackFragmentHeaderBox() {
        super(new Header(fourcc()));
    }

    public TrackFragmentHeaderBox(int trackId) {
        this();
        this.trackId = trackId;
    }

    protected TrackFragmentHeaderBox(int trackId, long baseDataOffset, int sampleDescriptionIndex, int defaultSampleDuration, int defaultSampleSize, int defaultSampleFlags) {
        super(new Header(fourcc()));
        this.trackId = trackId;
        this.baseDataOffset = baseDataOffset;
        this.sampleDescriptionIndex = sampleDescriptionIndex;
        this.defaultSampleDuration = defaultSampleDuration;
        this.defaultSampleSize = defaultSampleSize;
        this.defaultSampleFlags = defaultSampleFlags;
    }

    public static Factory create(int trackId) {
        return new Factory(trackId);
    }

    public static Factory copy(TrackFragmentHeaderBox other) {
        return new Factory(other);
    }

    /* loaded from: classes.dex */
    public static class Factory {
        private TrackFragmentHeaderBox box;

        protected Factory(int trackId) {
            this.box = new TrackFragmentHeaderBox(trackId);
        }

        public Factory(TrackFragmentHeaderBox other) {
            this.box = new TrackFragmentHeaderBox(other.trackId, other.baseDataOffset, other.sampleDescriptionIndex, other.defaultSampleDuration, other.defaultSampleSize, other.defaultSampleFlags);
            this.box.setFlags(other.getFlags());
            this.box.setVersion(other.getVersion());
        }

        public Factory baseDataOffset(long baseDataOffset) {
            this.box.flags |= 1;
            this.box.baseDataOffset = (int) baseDataOffset;
            return this;
        }

        public Factory sampleDescriptionIndex(long sampleDescriptionIndex) {
            this.box.flags |= 2;
            this.box.sampleDescriptionIndex = (int) sampleDescriptionIndex;
            return this;
        }

        public Factory defaultSampleDuration(long defaultSampleDuration) {
            this.box.flags |= 8;
            this.box.defaultSampleDuration = (int) defaultSampleDuration;
            return this;
        }

        public Factory defaultSampleSize(long defaultSampleSize) {
            this.box.flags |= 16;
            this.box.defaultSampleSize = (int) defaultSampleSize;
            return this;
        }

        public Factory defaultSampleFlags(long defaultSampleFlags) {
            this.box.flags |= 32;
            this.box.defaultSampleFlags = (int) defaultSampleFlags;
            return this;
        }

        public Box create() {
            try {
                return this.box;
            } finally {
                this.box = null;
            }
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.trackId = input.getInt();
        if (isBaseDataOffsetAvailable()) {
            this.baseDataOffset = input.getLong();
        }
        if (isSampleDescriptionIndexAvailable()) {
            this.sampleDescriptionIndex = input.getInt();
        }
        if (isDefaultSampleDurationAvailable()) {
            this.defaultSampleDuration = input.getInt();
        }
        if (isDefaultSampleSizeAvailable()) {
            this.defaultSampleSize = input.getInt();
        }
        if (isDefaultSampleFlagsAvailable()) {
            this.defaultSampleFlags = input.getInt();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.trackId);
        if (isBaseDataOffsetAvailable()) {
            out.putLong(this.baseDataOffset);
        }
        if (isSampleDescriptionIndexAvailable()) {
            out.putInt(this.sampleDescriptionIndex);
        }
        if (isDefaultSampleDurationAvailable()) {
            out.putInt(this.defaultSampleDuration);
        }
        if (isDefaultSampleSizeAvailable()) {
            out.putInt(this.defaultSampleSize);
        }
        if (isDefaultSampleFlagsAvailable()) {
            out.putInt(this.defaultSampleFlags);
        }
    }

    public int getTrackId() {
        return this.trackId;
    }

    public long getBaseDataOffset() {
        return this.baseDataOffset;
    }

    public int getSampleDescriptionIndex() {
        return this.sampleDescriptionIndex;
    }

    public int getDefaultSampleDuration() {
        return this.defaultSampleDuration;
    }

    public int getDefaultSampleSize() {
        return this.defaultSampleSize;
    }

    public int getDefaultSampleFlags() {
        return this.defaultSampleFlags;
    }

    public boolean isBaseDataOffsetAvailable() {
        return (this.flags & 1) != 0;
    }

    public boolean isSampleDescriptionIndexAvailable() {
        return (this.flags & 2) != 0;
    }

    public boolean isDefaultSampleDurationAvailable() {
        return (this.flags & 8) != 0;
    }

    public boolean isDefaultSampleSizeAvailable() {
        return (this.flags & 16) != 0;
    }

    public boolean isDefaultSampleFlagsAvailable() {
        return (this.flags & 32) != 0;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public void setDefaultSampleFlags(int defaultSampleFlags) {
        this.defaultSampleFlags = defaultSampleFlags;
    }
}
