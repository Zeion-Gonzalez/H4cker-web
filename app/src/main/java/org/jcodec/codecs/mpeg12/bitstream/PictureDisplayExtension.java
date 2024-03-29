package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.model.Point;

/* loaded from: classes.dex */
public class PictureDisplayExtension implements MPEGHeader {
    public Point[] frame_centre_offsets;

    public static PictureDisplayExtension read(BitReader bits, SequenceExtension se, PictureCodingExtension pce) {
        PictureDisplayExtension pde = new PictureDisplayExtension();
        pde.frame_centre_offsets = new Point[numberOfFrameCentreOffsets(se, pce)];
        for (int i = 0; i < pde.frame_centre_offsets.length; i++) {
            int frame_centre_horizontal_offset = bits.readNBit(16);
            bits.read1Bit();
            int frame_centre_vertical_offset = bits.readNBit(16);
            bits.read1Bit();
            pde.frame_centre_offsets[i] = new Point(frame_centre_horizontal_offset, frame_centre_vertical_offset);
        }
        return pde;
    }

    private static int numberOfFrameCentreOffsets(SequenceExtension se, PictureCodingExtension pce) {
        if (se == null || pce == null) {
            throw new IllegalArgumentException("PictureDisplayExtension requires SequenceExtension and PictureCodingExtension to be present");
        }
        if (se.progressive_sequence == 1) {
            if (pce.repeat_first_field == 1) {
                return pce.top_field_first == 1 ? 3 : 2;
            }
            return 1;
        }
        if (pce.picture_structure != 3) {
            return 1;
        }
        return pce.repeat_first_field != 1 ? 2 : 3;
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(7, 4);
        Point[] arr$ = this.frame_centre_offsets;
        for (Point point : arr$) {
            bw.writeNBit(point.getX(), 16);
            bw.writeNBit(point.getY(), 16);
        }
        bw.flush();
    }
}
