package org.jcodec.movtool.streaming.tracks;

import java.nio.ByteBuffer;
import org.jcodec.codecs.prores.ProresToThumb2x2;
import org.jcodec.codecs.prores.ProresToThumb4x4;
import org.jcodec.codecs.vpx.NopRateControl;
import org.jcodec.codecs.vpx.VP8Encoder;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.VideoEncoder;
import org.jcodec.common.model.Size;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class Prores2VP8Track extends TranscodeTrack {
    public Prores2VP8Track(VirtualTrack proresTrack, Size frameDim) {
        super(proresTrack, frameDim);
    }

    @Override // org.jcodec.movtool.streaming.tracks.TranscodeTrack
    protected VideoDecoder getDecoder(int scaleFactor) {
        return scaleFactor == 2 ? new ProresToThumb2x2() : new ProresToThumb4x4();
    }

    @Override // org.jcodec.movtool.streaming.tracks.TranscodeTrack
    protected VideoEncoder getEncoder(int rate) {
        return new VP8Encoder(new NopRateControl(12));
    }

    @Override // org.jcodec.movtool.streaming.tracks.TranscodeTrack
    protected int getFrameSize(int mbCount, int rate) {
        return 278528;
    }

    @Override // org.jcodec.movtool.streaming.tracks.TranscodeTrack
    protected void getCodecPrivate(ByteBuffer buf, Size dim) {
    }
}
