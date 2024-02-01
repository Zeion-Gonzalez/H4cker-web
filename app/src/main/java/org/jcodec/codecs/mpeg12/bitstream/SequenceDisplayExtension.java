package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class SequenceDisplayExtension implements MPEGHeader {
    public ColorDescription colorDescription;
    public int display_horizontal_size;
    public int display_vertical_size;
    public int video_format;

    /* loaded from: classes.dex */
    public static class ColorDescription {
        int colour_primaries;
        int matrix_coefficients;
        int transfer_characteristics;

        public static ColorDescription read(BitReader in) {
            ColorDescription cd = new ColorDescription();
            cd.colour_primaries = in.readNBit(8);
            cd.transfer_characteristics = in.readNBit(8);
            cd.matrix_coefficients = in.readNBit(8);
            return cd;
        }

        public void write(BitWriter out) {
            out.writeNBit(this.colour_primaries, 8);
            out.writeNBit(this.transfer_characteristics, 8);
            out.writeNBit(this.matrix_coefficients, 8);
        }
    }

    public static SequenceDisplayExtension read(BitReader in) {
        SequenceDisplayExtension sde = new SequenceDisplayExtension();
        sde.video_format = in.readNBit(3);
        if (in.read1Bit() == 1) {
            sde.colorDescription = ColorDescription.read(in);
        }
        sde.display_horizontal_size = in.readNBit(14);
        in.read1Bit();
        sde.display_vertical_size = in.readNBit(14);
        return sde;
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(2, 4);
        bw.writeNBit(this.video_format, 3);
        bw.write1Bit(this.colorDescription != null ? 1 : 0);
        if (this.colorDescription != null) {
            this.colorDescription.write(bw);
        }
        bw.writeNBit(this.display_horizontal_size, 14);
        bw.write1Bit(1);
        bw.writeNBit(this.display_vertical_size, 14);
        bw.flush();
    }
}
