package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class TrackExtendsBox extends FullBox {
    private int defaultSampleBytes;
    private int defaultSampleDescriptionIndex;
    private int defaultSampleDuration;
    private int defaultSampleFlags;
    private int trackId;

    public TrackExtendsBox() {
        super(new Header(fourcc()));
    }

    public static String fourcc() {
        return "trex";
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.trackId = input.getInt();
        this.defaultSampleDescriptionIndex = input.getInt();
        this.defaultSampleDuration = input.getInt();
        this.defaultSampleBytes = input.getInt();
        this.defaultSampleFlags = input.getInt();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.trackId);
        out.putInt(this.defaultSampleDescriptionIndex);
        out.putInt(this.defaultSampleDuration);
        out.putInt(this.defaultSampleBytes);
        out.putInt(this.defaultSampleFlags);
    }

    public int getTrackId() {
        return this.trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getDefaultSampleDescriptionIndex() {
        return this.defaultSampleDescriptionIndex;
    }

    public void setDefaultSampleDescriptionIndex(int defaultSampleDescriptionIndex) {
        this.defaultSampleDescriptionIndex = defaultSampleDescriptionIndex;
    }

    public int getDefaultSampleDuration() {
        return this.defaultSampleDuration;
    }

    public void setDefaultSampleDuration(int defaultSampleDuration) {
        this.defaultSampleDuration = defaultSampleDuration;
    }

    public int getDefaultSampleBytes() {
        return this.defaultSampleBytes;
    }

    public void setDefaultSampleBytes(int defaultSampleBytes) {
        this.defaultSampleBytes = defaultSampleBytes;
    }

    public int getDefaultSampleFlags() {
        return this.defaultSampleFlags;
    }

    public void setDefaultSampleFlags(int defaultSampleFlags) {
        this.defaultSampleFlags = defaultSampleFlags;
    }
}
