package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class QuantMatrixExtension implements MPEGHeader {
    public int[] chroma_intra_quantiser_matrix;
    public int[] chroma_non_intra_quantiser_matrix;
    public int[] intra_quantiser_matrix;
    public int[] non_intra_quantiser_matrix;

    public static QuantMatrixExtension read(BitReader in) {
        QuantMatrixExtension qme = new QuantMatrixExtension();
        if (in.read1Bit() != 0) {
            qme.intra_quantiser_matrix = readQMat(in);
        }
        if (in.read1Bit() != 0) {
            qme.non_intra_quantiser_matrix = readQMat(in);
        }
        if (in.read1Bit() != 0) {
            qme.chroma_intra_quantiser_matrix = readQMat(in);
        }
        if (in.read1Bit() != 0) {
            qme.chroma_non_intra_quantiser_matrix = readQMat(in);
        }
        return qme;
    }

    private static int[] readQMat(BitReader in) {
        int[] qmat = new int[64];
        for (int i = 0; i < 64; i++) {
            qmat[i] = in.readNBit(8);
        }
        return qmat;
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(3, 4);
        bw.write1Bit(this.intra_quantiser_matrix != null ? 1 : 0);
        if (this.intra_quantiser_matrix != null) {
            writeQMat(this.intra_quantiser_matrix, bw);
        }
        bw.write1Bit(this.non_intra_quantiser_matrix != null ? 1 : 0);
        if (this.non_intra_quantiser_matrix != null) {
            writeQMat(this.non_intra_quantiser_matrix, bw);
        }
        bw.write1Bit(this.chroma_intra_quantiser_matrix != null ? 1 : 0);
        if (this.chroma_intra_quantiser_matrix != null) {
            writeQMat(this.chroma_intra_quantiser_matrix, bw);
        }
        bw.write1Bit(this.chroma_non_intra_quantiser_matrix == null ? 0 : 1);
        if (this.chroma_non_intra_quantiser_matrix != null) {
            writeQMat(this.chroma_non_intra_quantiser_matrix, bw);
        }
        bw.flush();
    }

    private void writeQMat(int[] matrix, BitWriter ob) {
        for (int i = 0; i < 64; i++) {
            ob.writeNBit(matrix[i], 8);
        }
    }
}
