package org.jcodec.containers.mp4;

import java.nio.ByteBuffer;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.TapeTimecode;

/* loaded from: classes.dex */
public class MP4Packet extends Packet {
    private int entryNo;
    private long fileOff;
    private long mediaPts;
    private boolean psync;
    private int size;

    public MP4Packet(ByteBuffer data, long pts, long timescale, long duration, long frameNo, boolean iframe, TapeTimecode tapeTimecode, long mediaPts, int entryNo) {
        super(data, pts, timescale, duration, frameNo, iframe, tapeTimecode);
        this.mediaPts = mediaPts;
        this.entryNo = entryNo;
    }

    public MP4Packet(ByteBuffer data, long pts, long timescale, long duration, long frameNo, boolean iframe, TapeTimecode tapeTimecode, long mediaPts, int entryNo, long fileOff, int size, boolean psync) {
        super(data, pts, timescale, duration, frameNo, iframe, tapeTimecode);
        this.mediaPts = mediaPts;
        this.entryNo = entryNo;
        this.fileOff = fileOff;
        this.size = size;
        this.psync = psync;
    }

    public MP4Packet(MP4Packet packet, ByteBuffer frm) {
        super(packet, frm);
        this.mediaPts = packet.mediaPts;
        this.entryNo = packet.entryNo;
    }

    public MP4Packet(MP4Packet packet, TapeTimecode timecode) {
        super(packet, timecode);
        this.mediaPts = packet.mediaPts;
        this.entryNo = packet.entryNo;
    }

    public MP4Packet(Packet packet, long mediaPts, int entryNo) {
        super(packet);
        this.mediaPts = mediaPts;
        this.entryNo = entryNo;
    }

    public MP4Packet(MP4Packet packet) {
        super(packet);
        this.mediaPts = packet.mediaPts;
        this.entryNo = packet.entryNo;
    }

    public int getEntryNo() {
        return this.entryNo;
    }

    public long getMediaPts() {
        return this.mediaPts;
    }

    public long getFileOff() {
        return this.fileOff;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isPsync() {
        return this.psync;
    }
}
