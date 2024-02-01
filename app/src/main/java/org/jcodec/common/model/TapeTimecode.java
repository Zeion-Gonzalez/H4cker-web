package org.jcodec.common.model;

/* loaded from: classes.dex */
public class TapeTimecode {
    private boolean dropFrame;
    private byte frame;
    private short hour;
    private byte minute;
    private byte second;

    public TapeTimecode(short hour, byte minute, byte second, byte frame, boolean dropFrame) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.frame = frame;
        this.dropFrame = dropFrame;
    }

    public short getHour() {
        return this.hour;
    }

    public byte getMinute() {
        return this.minute;
    }

    public byte getSecond() {
        return this.second;
    }

    public byte getFrame() {
        return this.frame;
    }

    public boolean isDropFrame() {
        return this.dropFrame;
    }

    public String toString() {
        return String.format("%02d:%02d:%02d", Short.valueOf(this.hour), Byte.valueOf(this.minute), Byte.valueOf(this.second)) + (this.dropFrame ? ";" : ":") + String.format("%02d", Byte.valueOf(this.frame));
    }
}
