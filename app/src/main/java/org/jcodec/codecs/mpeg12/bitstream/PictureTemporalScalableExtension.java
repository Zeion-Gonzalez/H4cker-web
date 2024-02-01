package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class PictureTemporalScalableExtension implements MPEGHeader {
    public int backward_temporal_reference;
    public int forward_temporal_reference;
    public int reference_select_code;

    public static PictureTemporalScalableExtension read(BitReader in) {
        PictureTemporalScalableExtension ptse = new PictureTemporalScalableExtension();
        ptse.reference_select_code = in.readNBit(2);
        ptse.forward_temporal_reference = in.readNBit(10);
        in.read1Bit();
        ptse.backward_temporal_reference = in.readNBit(10);
        return ptse;
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(16, 4);
        bw.writeNBit(this.reference_select_code, 2);
        bw.writeNBit(this.forward_temporal_reference, 10);
        bw.write1Bit(1);
        bw.writeNBit(this.backward_temporal_reference, 10);
        bw.flush();
    }
}
