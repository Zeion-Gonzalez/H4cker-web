package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;
import org.jcodec.containers.mp4.TimeUtil;

/* loaded from: classes.dex */
public class MovieHeaderBox extends FullBox {
    private long created;
    private long duration;
    private int[] matrix;
    private long modified;
    private int nextTrackId;
    private float rate;
    private int timescale;
    private float volume;

    public static String fourcc() {
        return "mvhd";
    }

    public MovieHeaderBox(int timescale, long duration, float rate, float volume, long created, long modified, int[] matrix, int nextTrackId) {
        super(new Header(fourcc()));
        this.timescale = timescale;
        this.duration = duration;
        this.rate = rate;
        this.volume = volume;
        this.created = created;
        this.modified = modified;
        this.matrix = matrix;
        this.nextTrackId = nextTrackId;
    }

    public MovieHeaderBox() {
        super(new Header(fourcc()));
    }

    public int getTimescale() {
        return this.timescale;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getNextTrackId() {
        return this.nextTrackId;
    }

    public float getRate() {
        return this.rate;
    }

    public float getVolume() {
        return this.volume;
    }

    public long getCreated() {
        return this.created;
    }

    public long getModified() {
        return this.modified;
    }

    public int[] getMatrix() {
        return this.matrix;
    }

    public void setTimescale(int newTs) {
        this.timescale = newTs;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setNextTrackId(int nextTrackId) {
        this.nextTrackId = nextTrackId;
    }

    private int[] readMatrix(ByteBuffer input) {
        int[] matrix = new int[9];
        for (int i = 0; i < 9; i++) {
            matrix[i] = input.getInt();
        }
        return matrix;
    }

    private float readVolume(ByteBuffer input) {
        return input.getShort() / 256.0f;
    }

    private float readRate(ByteBuffer input) {
        return input.getInt() / 65536.0f;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        if (this.version == 0) {
            this.created = TimeUtil.fromMovTime(input.getInt());
            this.modified = TimeUtil.fromMovTime(input.getInt());
            this.timescale = input.getInt();
            this.duration = input.getInt();
        } else if (this.version == 1) {
            this.created = TimeUtil.fromMovTime((int) input.getLong());
            this.modified = TimeUtil.fromMovTime((int) input.getLong());
            this.timescale = input.getInt();
            this.duration = input.getLong();
        } else {
            throw new RuntimeException("Unsupported version");
        }
        this.rate = readRate(input);
        this.volume = readVolume(input);
        NIOUtils.skip(input, 10);
        this.matrix = readMatrix(input);
        NIOUtils.skip(input, 24);
        this.nextTrackId = input.getInt();
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(TimeUtil.toMovTime(this.created));
        out.putInt(TimeUtil.toMovTime(this.modified));
        out.putInt(this.timescale);
        out.putInt((int) this.duration);
        writeFixed1616(out, this.rate);
        writeFixed88(out, this.volume);
        out.put(new byte[10]);
        writeMatrix(out);
        out.put(new byte[24]);
        out.putInt(this.nextTrackId);
    }

    private void writeMatrix(ByteBuffer out) {
        for (int i = 0; i < Math.min(9, this.matrix.length); i++) {
            out.putInt(this.matrix[i]);
        }
        for (int i2 = Math.min(9, this.matrix.length); i2 < 9; i2++) {
            out.putInt(0);
        }
    }

    private void writeFixed88(ByteBuffer out, float volume) {
        out.putShort((short) (volume * 256.0d));
    }

    private void writeFixed1616(ByteBuffer out, float rate) {
        out.putInt((int) (rate * 65536.0d));
    }
}
