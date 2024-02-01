package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.containers.mp4.TimeUtil;

/* loaded from: classes.dex */
public class TrackHeaderBox extends FullBox {
    private long altGroup;
    private long created;
    private long duration;
    private float height;
    private short layer;
    private int[] matrix;
    private long modified;
    private int trackId;
    private float volume;
    private float width;

    public static String fourcc() {
        return "tkhd";
    }

    public TrackHeaderBox(int trackId, long duration, float width, float height, long created, long modified, float volume, short layer, long altGroup, int[] matrix) {
        super(new Header(fourcc()));
        this.trackId = trackId;
        this.duration = duration;
        this.width = width;
        this.height = height;
        this.created = created;
        this.modified = modified;
        this.volume = volume;
        this.layer = layer;
        this.altGroup = altGroup;
        this.matrix = matrix;
    }

    public TrackHeaderBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        if (this.version == 0) {
            this.created = TimeUtil.fromMovTime(input.getInt());
            this.modified = TimeUtil.fromMovTime(input.getInt());
        } else {
            this.created = TimeUtil.fromMovTime((int) input.getLong());
            this.modified = TimeUtil.fromMovTime((int) input.getLong());
        }
        this.trackId = input.getInt();
        input.getInt();
        if (this.version == 0) {
            this.duration = input.getInt();
        } else {
            this.duration = input.getLong();
        }
        input.getInt();
        input.getInt();
        this.layer = input.getShort();
        this.altGroup = input.getShort();
        this.volume = readVolume(input);
        input.getShort();
        readMatrix(input);
        this.width = input.getInt() / 65536.0f;
        this.height = input.getInt() / 65536.0f;
    }

    private void readMatrix(ByteBuffer input) {
        this.matrix = new int[9];
        for (int i = 0; i < 9; i++) {
            this.matrix[i] = input.getInt();
        }
    }

    private float readVolume(ByteBuffer input) {
        return (float) (input.getShort() / 256.0d);
    }

    public int getNo() {
        return this.trackId;
    }

    public long getDuration() {
        return this.duration;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(TimeUtil.toMovTime(this.created));
        out.putInt(TimeUtil.toMovTime(this.modified));
        out.putInt(this.trackId);
        out.putInt(0);
        out.putInt((int) this.duration);
        out.putInt(0);
        out.putInt(0);
        out.putShort(this.layer);
        out.putShort((short) this.altGroup);
        writeVolume(out);
        out.putShort((short) 0);
        writeMatrix(out);
        out.putInt((int) (this.width * 65536.0f));
        out.putInt((int) (this.height * 65536.0f));
    }

    private void writeMatrix(ByteBuffer out) {
        for (int i = 0; i < 9; i++) {
            out.putInt(this.matrix[i]);
        }
    }

    private void writeVolume(ByteBuffer out) {
        out.putShort((short) (this.volume * 256.0d));
    }

    public int getTrackId() {
        return this.trackId;
    }

    public long getCreated() {
        return this.created;
    }

    public long getModified() {
        return this.modified;
    }

    public float getVolume() {
        return this.volume;
    }

    public short getLayer() {
        return this.layer;
    }

    public long getAltGroup() {
        return this.altGroup;
    }

    public int[] getMatrix() {
        return this.matrix;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setNo(int no) {
        this.trackId = no;
    }
}
