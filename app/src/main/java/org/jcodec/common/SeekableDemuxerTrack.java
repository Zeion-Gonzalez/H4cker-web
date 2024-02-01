package org.jcodec.common;

/* loaded from: classes.dex */
public interface SeekableDemuxerTrack extends DemuxerTrack {
    long getCurFrame();

    boolean gotoFrame(long j);

    boolean gotoSyncFrame(long j);

    void seek(double d);
}
