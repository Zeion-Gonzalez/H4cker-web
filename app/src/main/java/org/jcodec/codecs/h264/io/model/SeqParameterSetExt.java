package org.jcodec.codecs.h264.io.model;

import java.nio.ByteBuffer;
import org.jcodec.codecs.h264.decode.CAVLCReader;
import org.jcodec.codecs.h264.io.write.CAVLCWriter;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class SeqParameterSetExt {
    public boolean additional_extension_flag;
    public boolean alpha_incr_flag;
    public int alpha_opaque_value;
    public int alpha_transparent_value;
    public int aux_format_idc;
    public int bit_depth_aux_minus8;
    public int seq_parameter_set_id;

    public static SeqParameterSetExt read(ByteBuffer is) {
        BitReader in = new BitReader(is);
        SeqParameterSetExt spse = new SeqParameterSetExt();
        spse.seq_parameter_set_id = CAVLCReader.readUE(in, "SPSE: seq_parameter_set_id");
        spse.aux_format_idc = CAVLCReader.readUE(in, "SPSE: aux_format_idc");
        if (spse.aux_format_idc != 0) {
            spse.bit_depth_aux_minus8 = CAVLCReader.readUE(in, "SPSE: bit_depth_aux_minus8");
            spse.alpha_incr_flag = CAVLCReader.readBool(in, "SPSE: alpha_incr_flag");
            spse.alpha_opaque_value = CAVLCReader.readU(in, spse.bit_depth_aux_minus8 + 9, "SPSE: alpha_opaque_value");
            spse.alpha_transparent_value = CAVLCReader.readU(in, spse.bit_depth_aux_minus8 + 9, "SPSE: alpha_transparent_value");
        }
        spse.additional_extension_flag = CAVLCReader.readBool(in, "SPSE: additional_extension_flag");
        return spse;
    }

    public void write(ByteBuffer out) {
        BitWriter writer = new BitWriter(out);
        CAVLCWriter.writeTrailingBits(writer);
    }
}
