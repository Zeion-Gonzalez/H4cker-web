package org.jcodec.containers.mps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.model.Packet;

/* loaded from: classes.dex */
public interface MPEGDemuxer {

    /* loaded from: classes.dex */
    public interface MPEGDemuxerTrack {
        DemuxerTrackMeta getMeta();

        void ignore();

        Packet nextFrame(ByteBuffer byteBuffer) throws IOException;
    }

    List<? extends MPEGDemuxerTrack> getAudioTracks();

    List<? extends MPEGDemuxerTrack> getTracks();

    List<? extends MPEGDemuxerTrack> getVideoTracks();

    void seekByte(long j) throws IOException;
}
