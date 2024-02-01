package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.codecs.mpeg12.MPEGConst;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class SequenceHeader implements MPEGHeader {
    public static final int Sequence_Display_Extension = 2;
    public static final int Sequence_Extension = 1;
    public static final int Sequence_Scalable_Extension = 5;
    private static boolean hasExtensions;
    public int aspect_ratio_information;
    public int bit_rate;
    public int constrained_parameters_flag;
    public int frame_rate_code;
    public int horizontal_size;
    public int[] intra_quantiser_matrix;
    public int[] non_intra_quantiser_matrix;
    public SequenceDisplayExtension sequenceDisplayExtension;
    public SequenceExtension sequenceExtension;
    public SequenceScalableExtension sequenceScalableExtension;
    public int vbv_buffer_size_value;
    public int vertical_size;

    public SequenceHeader(int horizontal_size, int vertical_size, int aspect_ratio_information, int frame_rate_code, int bit_rate, int vbv_buffer_size_value, int constrained_parameters_flag, int[] intra_quantiser_matrix, int[] non_intra_quantiser_matrix) {
        this.horizontal_size = horizontal_size;
        this.vertical_size = vertical_size;
        this.aspect_ratio_information = aspect_ratio_information;
        this.frame_rate_code = frame_rate_code;
        this.bit_rate = bit_rate;
        this.vbv_buffer_size_value = vbv_buffer_size_value;
        this.constrained_parameters_flag = constrained_parameters_flag;
        this.intra_quantiser_matrix = intra_quantiser_matrix;
        this.non_intra_quantiser_matrix = non_intra_quantiser_matrix;
    }

    private SequenceHeader() {
    }

    public static SequenceHeader read(ByteBuffer bb) {
        BitReader in = new BitReader(bb);
        SequenceHeader sh = new SequenceHeader();
        sh.horizontal_size = in.readNBit(12);
        sh.vertical_size = in.readNBit(12);
        sh.aspect_ratio_information = in.readNBit(4);
        sh.frame_rate_code = in.readNBit(4);
        sh.bit_rate = in.readNBit(18);
        in.read1Bit();
        sh.vbv_buffer_size_value = in.readNBit(10);
        sh.constrained_parameters_flag = in.read1Bit();
        if (in.read1Bit() != 0) {
            sh.intra_quantiser_matrix = new int[64];
            for (int i = 0; i < 64; i++) {
                sh.intra_quantiser_matrix[i] = in.readNBit(8);
            }
        }
        if (in.read1Bit() != 0) {
            sh.non_intra_quantiser_matrix = new int[64];
            for (int i2 = 0; i2 < 64; i2++) {
                sh.non_intra_quantiser_matrix[i2] = in.readNBit(8);
            }
        }
        return sh;
    }

    public static void readExtension(ByteBuffer bb, SequenceHeader sh) {
        hasExtensions = true;
        BitReader in = new BitReader(bb);
        int extType = in.readNBit(4);
        switch (extType) {
            case 1:
                sh.sequenceExtension = SequenceExtension.read(in);
                return;
            case 2:
                sh.sequenceDisplayExtension = SequenceDisplayExtension.read(in);
                return;
            case 3:
            case 4:
            default:
                throw new RuntimeException("Unsupported extension: " + extType);
            case 5:
                sh.sequenceScalableExtension = SequenceScalableExtension.read(in);
                return;
        }
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(this.horizontal_size, 12);
        bw.writeNBit(this.vertical_size, 12);
        bw.writeNBit(this.aspect_ratio_information, 4);
        bw.writeNBit(this.frame_rate_code, 4);
        bw.writeNBit(this.bit_rate, 18);
        bw.write1Bit(1);
        bw.writeNBit(this.vbv_buffer_size_value, 10);
        bw.write1Bit(this.constrained_parameters_flag);
        bw.write1Bit(this.intra_quantiser_matrix != null ? 1 : 0);
        if (this.intra_quantiser_matrix != null) {
            for (int i = 0; i < 64; i++) {
                bw.writeNBit(this.intra_quantiser_matrix[i], 8);
            }
        }
        bw.write1Bit(this.non_intra_quantiser_matrix == null ? 0 : 1);
        if (this.non_intra_quantiser_matrix != null) {
            for (int i2 = 0; i2 < 64; i2++) {
                bw.writeNBit(this.non_intra_quantiser_matrix[i2], 8);
            }
        }
        bw.flush();
        writeExtensions(bb);
    }

    private void writeExtensions(ByteBuffer out) {
        if (this.sequenceExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.sequenceExtension.write(out);
        }
        if (this.sequenceScalableExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.sequenceScalableExtension.write(out);
        }
        if (this.sequenceDisplayExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.sequenceDisplayExtension.write(out);
        }
    }

    public boolean hasExtensions() {
        return hasExtensions;
    }

    public void copyExtensions(SequenceHeader sh) {
        this.sequenceExtension = sh.sequenceExtension;
        this.sequenceScalableExtension = sh.sequenceScalableExtension;
        this.sequenceDisplayExtension = sh.sequenceDisplayExtension;
    }
}
