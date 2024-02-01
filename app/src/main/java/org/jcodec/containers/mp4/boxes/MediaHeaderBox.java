package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.containers.mp4.TimeUtil;

/* loaded from: classes.dex */
public class MediaHeaderBox extends FullBox {
    private long created;
    private long duration;
    private int language;
    private long modified;
    private int quality;
    private int timescale;

    public static String fourcc() {
        return "mdhd";
    }

    public MediaHeaderBox(int timescale, long duration, int language, long created, long modified, int quality) {
        super(new Header(fourcc()));
        this.timescale = timescale;
        this.duration = duration;
        this.language = language;
        this.created = created;
        this.modified = modified;
        this.quality = quality;
    }

    public MediaHeaderBox() {
        super(new Header(fourcc()));
    }

    public int getTimescale() {
        return this.timescale;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getCreated() {
        return this.created;
    }

    public long getModified() {
        return this.modified;
    }

    public int getLanguage() {
        return this.language;
    }

    public int getQuality() {
        return this.quality;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTimescale(int timescale) {
        this.timescale = timescale;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        if (this.version == 0) {
            this.created = TimeUtil.fromMovTime(input.getInt());
            this.modified = TimeUtil.fromMovTime(input.getInt());
            this.timescale = input.getInt();
            this.duration = input.getInt();
            return;
        }
        if (this.version == 1) {
            this.created = TimeUtil.fromMovTime((int) input.getLong());
            this.modified = TimeUtil.fromMovTime((int) input.getLong());
            this.timescale = input.getInt();
            this.duration = input.getLong();
            return;
        }
        throw new RuntimeException("Unsupported version");
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(TimeUtil.toMovTime(this.created));
        out.putInt(TimeUtil.toMovTime(this.modified));
        out.putInt(this.timescale);
        out.putInt((int) this.duration);
        out.putShort((short) this.language);
        out.putShort((short) this.quality);
    }
}
