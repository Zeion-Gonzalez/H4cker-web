package org.jcodec.movtool.streaming.tracks;

import java.util.EnumSet;
import java.util.Iterator;
import org.jcodec.codecs.prores.ProresDecoder;
import org.jcodec.codecs.prores.ProresEncoder;
import org.jcodec.codecs.prores.ProresToThumb2x2;
import org.jcodec.codecs.prores.ProresToThumb4x4;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.model.Size;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class Prores2AVCTrack extends Transcode2AVCTrack {
    public Prores2AVCTrack(VirtualTrack proresTrack, Size frameDim) {
        super(proresTrack, frameDim);
    }

    @Override // org.jcodec.movtool.streaming.tracks.Transcode2AVCTrack
    protected void checkFourCC(VirtualTrack proresTrack) {
        String fourcc = proresTrack.getCodecMeta().getFourcc();
        if (!"ap4h".equals(fourcc)) {
            Iterator i$ = EnumSet.allOf(ProresEncoder.Profile.class).iterator();
            while (i$.hasNext()) {
                ProresEncoder.Profile profile = (ProresEncoder.Profile) i$.next();
                if (profile.fourcc.equals(fourcc)) {
                    return;
                }
            }
            throw new IllegalArgumentException("Input track is not ProRes");
        }
    }

    @Override // org.jcodec.movtool.streaming.tracks.Transcode2AVCTrack
    protected int selectScaleFactor(Size frameDim) {
        if (frameDim.getWidth() >= 960) {
            return 2;
        }
        return frameDim.getWidth() > 480 ? 1 : 0;
    }

    @Override // org.jcodec.movtool.streaming.tracks.Transcode2AVCTrack
    protected VideoDecoder getDecoder(int scaleFactor) {
        switch (scaleFactor) {
            case 0:
                return new ProresDecoder();
            case 1:
                return new ProresToThumb4x4();
            case 2:
                return new ProresToThumb2x2();
            default:
                throw new IllegalArgumentException("Unsupported scale factor: " + scaleFactor);
        }
    }
}
