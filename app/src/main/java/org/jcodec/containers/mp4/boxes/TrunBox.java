package org.jcodec.containers.mp4.boxes;

import android.support.v4.internal.view.SupportMenu;
import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: classes.dex */
public class TrunBox extends FullBox {
    private static final int DATA_OFFSET_AVAILABLE = 1;
    private static final int FIRST_SAMPLE_FLAGS_AVAILABLE = 4;
    private static final int SAMPLE_COMPOSITION_OFFSET_AVAILABLE = 2048;
    private static final int SAMPLE_DURATION_AVAILABLE = 256;
    private static final int SAMPLE_FLAGS_AVAILABLE = 1024;
    private static final int SAMPLE_SIZE_AVAILABLE = 512;
    private int dataOffset;
    private int firstSampleFlags;
    private int[] sampleCompositionOffset;
    private int sampleCount;
    private int[] sampleDuration;
    private int[] sampleFlags;
    private int[] sampleSize;

    public static String fourcc() {
        return "trun";
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    public static Factory create(int sampleCount) {
        return new Factory(sampleCount);
    }

    public static Factory copy(TrunBox other) {
        return new Factory(other);
    }

    public TrunBox() {
        super(new Header(fourcc()));
    }

    protected TrunBox(int sampleCount, int dataOffset, int firstSampleFlags, int[] sampleDuration, int[] sampleSize, int[] sampleFlags, int[] sampleCompositionOffset) {
        this();
        this.sampleCount = sampleCount;
        this.dataOffset = dataOffset;
        this.firstSampleFlags = firstSampleFlags;
        this.sampleDuration = sampleDuration;
        this.sampleSize = sampleSize;
        this.sampleFlags = sampleFlags;
        this.sampleCompositionOffset = sampleCompositionOffset;
    }

    protected TrunBox(int sampleCount) {
        this();
        this.sampleCount = sampleCount;
    }

    /* loaded from: classes.dex */
    public static class Factory {
        private TrunBox box;

        protected Factory(int sampleCount) {
            this.box = new TrunBox(sampleCount);
        }

        public Factory(TrunBox other) {
            this.box = new TrunBox(other.sampleCount, other.dataOffset, other.firstSampleFlags, other.sampleDuration, other.sampleSize, other.sampleFlags, other.sampleCompositionOffset);
            this.box.setFlags(other.getFlags());
            this.box.setVersion(other.getVersion());
        }

        public Factory dataOffset(long dataOffset) {
            this.box.flags |= 1;
            this.box.dataOffset = (int) dataOffset;
            return this;
        }

        public Factory firstSampleFlags(int firstSampleFlags) {
            if (this.box.isSampleFlagsAvailable()) {
                throw new IllegalStateException("Sample flags already set on this object");
            }
            this.box.flags |= 4;
            this.box.firstSampleFlags = firstSampleFlags;
            return this;
        }

        public Factory sampleDuration(int[] sampleDuration) {
            if (sampleDuration.length != this.box.sampleCount) {
                throw new IllegalArgumentException("Argument array length not equal to sampleCount");
            }
            this.box.flags |= 256;
            this.box.sampleDuration = sampleDuration;
            return this;
        }

        public Factory sampleSize(int[] sampleSize) {
            if (sampleSize.length != this.box.sampleCount) {
                throw new IllegalArgumentException("Argument array length not equal to sampleCount");
            }
            this.box.flags |= 512;
            this.box.sampleSize = sampleSize;
            return this;
        }

        public Factory sampleFlags(int[] sampleFlags) {
            if (sampleFlags.length != this.box.sampleCount) {
                throw new IllegalArgumentException("Argument array length not equal to sampleCount");
            }
            if (this.box.isFirstSampleFlagsAvailable()) {
                throw new IllegalStateException("First sample flags already set on this object");
            }
            this.box.flags |= 1024;
            this.box.sampleFlags = sampleFlags;
            return this;
        }

        public Factory sampleCompositionOffset(int[] sampleCompositionOffset) {
            if (sampleCompositionOffset.length != this.box.sampleCount) {
                throw new IllegalArgumentException("Argument array length not equal to sampleCount");
            }
            this.box.flags |= 2048;
            this.box.sampleCompositionOffset = sampleCompositionOffset;
            return this;
        }

        public TrunBox create() {
            try {
                return this.box;
            } finally {
                this.box = null;
            }
        }
    }

    public long getSampleCount() {
        return this.sampleCount & 4294967295L;
    }

    public int getDataOffset() {
        return this.dataOffset;
    }

    public int getFirstSampleFlags() {
        return this.firstSampleFlags;
    }

    public int[] getSampleDuration() {
        return this.sampleDuration;
    }

    public int[] getSampleSize() {
        return this.sampleSize;
    }

    public int[] getSampleFlags() {
        return this.sampleFlags;
    }

    public int[] getSampleCompositionOffset() {
        return this.sampleCompositionOffset;
    }

    public long getSampleDuration(int i) {
        return this.sampleDuration[i] & 4294967295L;
    }

    public long getSampleSize(int i) {
        return this.sampleSize[i] & 4294967295L;
    }

    public int getSampleFlags(int i) {
        return this.sampleFlags[i];
    }

    public long getSampleCompositionOffset(int i) {
        return this.sampleCompositionOffset[i] & 4294967295L;
    }

    public boolean isDataOffsetAvailable() {
        return (this.flags & 1) != 0;
    }

    public boolean isSampleCompositionOffsetAvailable() {
        return (this.flags & 2048) != 0;
    }

    public boolean isSampleFlagsAvailable() {
        return (this.flags & 1024) != 0;
    }

    public boolean isSampleSizeAvailable() {
        return (this.flags & 512) != 0;
    }

    public boolean isSampleDurationAvailable() {
        return (this.flags & 256) != 0;
    }

    public boolean isFirstSampleFlagsAvailable() {
        return (this.flags & 4) != 0;
    }

    public static int flagsGetSampleDependsOn(int flags) {
        return (flags >> 6) & 3;
    }

    public static int flagsGetSampleIsDependedOn(int flags) {
        return (flags >> 8) & 3;
    }

    public static int flagsGetSampleHasRedundancy(int flags) {
        return (flags >> 10) & 3;
    }

    public static int flagsGetSamplePaddingValue(int flags) {
        return (flags >> 12) & 7;
    }

    public static int flagsGetSampleIsDifferentSample(int flags) {
        return (flags >> 15) & 1;
    }

    public static int flagsGetSampleDegradationPriority(int flags) {
        return (flags >> 16) & SupportMenu.USER_MASK;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        if (isSampleFlagsAvailable() && isFirstSampleFlagsAvailable()) {
            throw new RuntimeException("Broken stream");
        }
        this.sampleCount = input.getInt();
        if (isDataOffsetAvailable()) {
            this.dataOffset = input.getInt();
        }
        if (isFirstSampleFlagsAvailable()) {
            this.firstSampleFlags = input.getInt();
        }
        if (isSampleDurationAvailable()) {
            this.sampleDuration = new int[this.sampleCount];
        }
        if (isSampleSizeAvailable()) {
            this.sampleSize = new int[this.sampleCount];
        }
        if (isSampleFlagsAvailable()) {
            this.sampleFlags = new int[this.sampleCount];
        }
        if (isSampleCompositionOffsetAvailable()) {
            this.sampleCompositionOffset = new int[this.sampleCount];
        }
        for (int i = 0; i < this.sampleCount; i++) {
            if (isSampleDurationAvailable()) {
                this.sampleDuration[i] = input.getInt();
            }
            if (isSampleSizeAvailable()) {
                this.sampleSize[i] = input.getInt();
            }
            if (isSampleFlagsAvailable()) {
                this.sampleFlags[i] = input.getInt();
            }
            if (isSampleCompositionOffsetAvailable()) {
                this.sampleCompositionOffset[i] = input.getInt();
            }
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.sampleCount);
        if (isDataOffsetAvailable()) {
            out.putInt(this.dataOffset);
        }
        if (isFirstSampleFlagsAvailable()) {
            out.putInt(this.firstSampleFlags);
        }
        for (int i = 0; i < this.sampleCount; i++) {
            if (isSampleDurationAvailable()) {
                out.putInt(this.sampleDuration[i]);
            }
            if (isSampleSizeAvailable()) {
                out.putInt(this.sampleSize[i]);
            }
            if (isSampleFlagsAvailable()) {
                out.putInt(this.sampleFlags[i]);
            }
            if (isSampleCompositionOffsetAvailable()) {
                out.putInt(this.sampleCompositionOffset[i]);
            }
        }
    }

    protected void getModelFields(List<String> model) {
        model.add("sampleCount");
        if (isDataOffsetAvailable()) {
            model.add("dataOffset");
        }
        if (isFirstSampleFlagsAvailable()) {
            model.add("firstSampleFlags");
        }
        if (isSampleDurationAvailable()) {
            model.add("sampleDuration");
        }
        if (isSampleSizeAvailable()) {
            model.add("sampleSize");
        }
        if (isSampleFlagsAvailable()) {
            model.add("sampleFlags");
        }
        if (isSampleCompositionOffsetAvailable()) {
            model.add("sampleCompositionOffset");
        }
    }
}
