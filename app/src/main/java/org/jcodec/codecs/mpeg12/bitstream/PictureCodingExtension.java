package org.jcodec.codecs.mpeg12.bitstream;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class PictureCodingExtension implements MPEGHeader {
    public static final int Bottom_Field = 2;
    public static final int Frame = 3;
    public static final int Top_Field = 1;
    public int alternate_scan;
    public int chroma_420_type;
    public CompositeDisplay compositeDisplay;
    public int concealment_motion_vectors;
    public int[][] f_code = (int[][]) Array.newInstance(Integer.TYPE, 2, 2);
    public int frame_pred_frame_dct;
    public int intra_dc_precision;
    public int intra_vlc_format;
    public int picture_structure;
    public int progressive_frame;
    public int q_scale_type;
    public int repeat_first_field;
    public int top_field_first;

    /* loaded from: classes.dex */
    public static class CompositeDisplay {
        public int burst_amplitude;
        public int field_sequence;
        public int sub_carrier;
        public int sub_carrier_phase;
        public int v_axis;

        public static CompositeDisplay read(BitReader in) {
            CompositeDisplay cd = new CompositeDisplay();
            cd.v_axis = in.read1Bit();
            cd.field_sequence = in.readNBit(3);
            cd.sub_carrier = in.read1Bit();
            cd.burst_amplitude = in.readNBit(7);
            cd.sub_carrier_phase = in.readNBit(8);
            return cd;
        }

        public void write(BitWriter out) {
            out.write1Bit(this.v_axis);
            out.writeNBit(this.field_sequence, 3);
            out.write1Bit(this.sub_carrier);
            out.writeNBit(this.burst_amplitude, 7);
            out.writeNBit(this.sub_carrier_phase, 8);
        }
    }

    public static PictureCodingExtension read(BitReader in) {
        PictureCodingExtension pce = new PictureCodingExtension();
        pce.f_code[0][0] = in.readNBit(4);
        pce.f_code[0][1] = in.readNBit(4);
        pce.f_code[1][0] = in.readNBit(4);
        pce.f_code[1][1] = in.readNBit(4);
        pce.intra_dc_precision = in.readNBit(2);
        pce.picture_structure = in.readNBit(2);
        pce.top_field_first = in.read1Bit();
        pce.frame_pred_frame_dct = in.read1Bit();
        pce.concealment_motion_vectors = in.read1Bit();
        pce.q_scale_type = in.read1Bit();
        pce.intra_vlc_format = in.read1Bit();
        pce.alternate_scan = in.read1Bit();
        pce.repeat_first_field = in.read1Bit();
        pce.chroma_420_type = in.read1Bit();
        pce.progressive_frame = in.read1Bit();
        if (in.read1Bit() != 0) {
            pce.compositeDisplay = CompositeDisplay.read(in);
        }
        return pce;
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(8, 4);
        bw.writeNBit(this.f_code[0][0], 4);
        bw.writeNBit(this.f_code[0][1], 4);
        bw.writeNBit(this.f_code[1][0], 4);
        bw.writeNBit(this.f_code[1][1], 4);
        bw.writeNBit(this.intra_dc_precision, 2);
        bw.writeNBit(this.picture_structure, 2);
        bw.write1Bit(this.top_field_first);
        bw.write1Bit(this.frame_pred_frame_dct);
        bw.write1Bit(this.concealment_motion_vectors);
        bw.write1Bit(this.q_scale_type);
        bw.write1Bit(this.intra_vlc_format);
        bw.write1Bit(this.alternate_scan);
        bw.write1Bit(this.repeat_first_field);
        bw.write1Bit(this.chroma_420_type);
        bw.write1Bit(this.progressive_frame);
        bw.write1Bit(this.compositeDisplay == null ? 0 : 1);
        if (this.compositeDisplay != null) {
            this.compositeDisplay.write(bw);
        }
        bw.flush();
    }
}
