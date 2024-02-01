package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class SequenceScalableExtension implements MPEGHeader {
    public static final int DATA_PARTITIONING = 0;
    public static final int SNR_SCALABILITY = 2;
    public static final int SPATIAL_SCALABILITY = 1;
    public static final int TEMPORAL_SCALABILITY = 3;
    public int horizontal_subsampling_factor_m;
    public int horizontal_subsampling_factor_n;
    public int layer_id;
    public int lower_layer_prediction_horizontal_size;
    public int lower_layer_prediction_vertical_size;
    public int mux_to_progressive_sequence;
    public int picture_mux_enable;
    public int picture_mux_factor;
    public int picture_mux_order;
    public int scalable_mode;
    public int vertical_subsampling_factor_m;
    public int vertical_subsampling_factor_n;

    public static SequenceScalableExtension read(BitReader in) {
        SequenceScalableExtension sse = new SequenceScalableExtension();
        sse.scalable_mode = in.readNBit(2);
        sse.layer_id = in.readNBit(4);
        if (sse.scalable_mode == 1) {
            sse.lower_layer_prediction_horizontal_size = in.readNBit(14);
            in.read1Bit();
            sse.lower_layer_prediction_vertical_size = in.readNBit(14);
            sse.horizontal_subsampling_factor_m = in.readNBit(5);
            sse.horizontal_subsampling_factor_n = in.readNBit(5);
            sse.vertical_subsampling_factor_m = in.readNBit(5);
            sse.vertical_subsampling_factor_n = in.readNBit(5);
        }
        if (sse.scalable_mode == 3) {
            sse.picture_mux_enable = in.read1Bit();
            if (sse.picture_mux_enable != 0) {
                sse.mux_to_progressive_sequence = in.read1Bit();
            }
            sse.picture_mux_order = in.readNBit(3);
            sse.picture_mux_factor = in.readNBit(3);
        }
        return sse;
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(5, 4);
        bw.writeNBit(this.scalable_mode, 2);
        bw.writeNBit(this.layer_id, 4);
        if (this.scalable_mode == 1) {
            bw.writeNBit(this.lower_layer_prediction_horizontal_size, 14);
            bw.write1Bit(1);
            bw.writeNBit(this.lower_layer_prediction_vertical_size, 14);
            bw.writeNBit(this.horizontal_subsampling_factor_m, 5);
            bw.writeNBit(this.horizontal_subsampling_factor_n, 5);
            bw.writeNBit(this.vertical_subsampling_factor_m, 5);
            bw.writeNBit(this.vertical_subsampling_factor_n, 5);
        }
        if (this.scalable_mode == 3) {
            bw.write1Bit(this.picture_mux_enable);
            if (this.picture_mux_enable != 0) {
                bw.write1Bit(this.mux_to_progressive_sequence);
            }
            bw.writeNBit(this.picture_mux_order, 3);
            bw.writeNBit(this.picture_mux_factor, 3);
        }
        bw.flush();
    }
}
