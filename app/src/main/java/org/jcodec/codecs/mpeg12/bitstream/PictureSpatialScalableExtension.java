package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class PictureSpatialScalableExtension implements MPEGHeader {
    public int lower_layer_deinterlaced_field_select;
    public int lower_layer_horizontal_offset;
    public int lower_layer_progressive_frame;
    public int lower_layer_temporal_reference;
    public int lower_layer_vertical_offset;
    public int spatial_temporal_weight_code_table_index;

    public static PictureSpatialScalableExtension read(BitReader in) {
        PictureSpatialScalableExtension psse = new PictureSpatialScalableExtension();
        psse.lower_layer_temporal_reference = in.readNBit(10);
        in.read1Bit();
        psse.lower_layer_horizontal_offset = in.readNBit(15);
        in.read1Bit();
        psse.lower_layer_vertical_offset = in.readNBit(15);
        psse.spatial_temporal_weight_code_table_index = in.readNBit(2);
        psse.lower_layer_progressive_frame = in.read1Bit();
        psse.lower_layer_deinterlaced_field_select = in.read1Bit();
        return psse;
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        bw.writeNBit(9, 4);
        bw.writeNBit(this.lower_layer_temporal_reference, 10);
        bw.write1Bit(1);
        bw.writeNBit(this.lower_layer_horizontal_offset, 15);
        bw.write1Bit(1);
        bw.writeNBit(this.lower_layer_vertical_offset, 15);
        bw.writeNBit(this.spatial_temporal_weight_code_table_index, 2);
        bw.write1Bit(this.lower_layer_progressive_frame);
        bw.write1Bit(this.lower_layer_deinterlaced_field_select);
        bw.flush();
    }
}
