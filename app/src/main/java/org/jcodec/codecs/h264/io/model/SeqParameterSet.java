package org.jcodec.codecs.h264.io.model;

import java.nio.ByteBuffer;
import org.jcodec.codecs.h264.decode.CAVLCReader;
import org.jcodec.codecs.h264.io.model.VUIParameters;
import org.jcodec.codecs.h264.io.write.CAVLCWriter;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.model.ColorSpace;

/* loaded from: classes.dex */
public class SeqParameterSet {
    public int bit_depth_chroma_minus8;
    public int bit_depth_luma_minus8;
    public ColorSpace chroma_format_idc;
    public boolean constraint_set_0_flag;
    public boolean constraint_set_1_flag;
    public boolean constraint_set_2_flag;
    public boolean constraint_set_3_flag;
    public boolean delta_pic_order_always_zero_flag;
    public boolean direct_8x8_inference_flag;
    public boolean field_pic_flag;
    public int frame_crop_bottom_offset;
    public int frame_crop_left_offset;
    public int frame_crop_right_offset;
    public int frame_crop_top_offset;
    public boolean frame_cropping_flag;
    public boolean frame_mbs_only_flag;
    public boolean gaps_in_frame_num_value_allowed_flag;
    public int level_idc;
    public int log2_max_frame_num_minus4;
    public int log2_max_pic_order_cnt_lsb_minus4;
    public boolean mb_adaptive_frame_field_flag;
    public int num_ref_frames;
    public int num_ref_frames_in_pic_order_cnt_cycle;
    public int[] offsetForRefFrame;
    public int offset_for_non_ref_pic;
    public int offset_for_top_to_bottom_field;
    public int pic_height_in_map_units_minus1;
    public int pic_order_cnt_type;
    public int pic_width_in_mbs_minus1;
    public int profile_idc;
    public boolean qpprime_y_zero_transform_bypass_flag;
    public boolean residual_color_transform_flag;
    public ScalingMatrix scalingMatrix;
    public int seq_parameter_set_id;
    public VUIParameters vuiParams;

    public static ColorSpace getColor(int id) {
        switch (id) {
            case 0:
                return ColorSpace.MONO;
            case 1:
                return ColorSpace.YUV420;
            case 2:
                return ColorSpace.YUV422;
            case 3:
                return ColorSpace.YUV444;
            default:
                throw new RuntimeException("Colorspace not supported");
        }
    }

    public static int fromColor(ColorSpace color) {
        switch (color) {
            case MONO:
                return 0;
            case YUV420:
                return 1;
            case YUV422:
                return 2;
            case YUV444:
                return 3;
            default:
                throw new RuntimeException("Colorspace not supported");
        }
    }

    public static SeqParameterSet read(ByteBuffer is) {
        BitReader in = new BitReader(is);
        SeqParameterSet sps = new SeqParameterSet();
        sps.profile_idc = CAVLCReader.readNBit(in, 8, "SPS: profile_idc");
        sps.constraint_set_0_flag = CAVLCReader.readBool(in, "SPS: constraint_set_0_flag");
        sps.constraint_set_1_flag = CAVLCReader.readBool(in, "SPS: constraint_set_1_flag");
        sps.constraint_set_2_flag = CAVLCReader.readBool(in, "SPS: constraint_set_2_flag");
        sps.constraint_set_3_flag = CAVLCReader.readBool(in, "SPS: constraint_set_3_flag");
        CAVLCReader.readNBit(in, 4, "SPS: reserved_zero_4bits");
        sps.level_idc = CAVLCReader.readNBit(in, 8, "SPS: level_idc");
        sps.seq_parameter_set_id = CAVLCReader.readUE(in, "SPS: seq_parameter_set_id");
        if (sps.profile_idc == 100 || sps.profile_idc == 110 || sps.profile_idc == 122 || sps.profile_idc == 144) {
            sps.chroma_format_idc = getColor(CAVLCReader.readUE(in, "SPS: chroma_format_idc"));
            if (sps.chroma_format_idc == ColorSpace.YUV444) {
                sps.residual_color_transform_flag = CAVLCReader.readBool(in, "SPS: residual_color_transform_flag");
            }
            sps.bit_depth_luma_minus8 = CAVLCReader.readUE(in, "SPS: bit_depth_luma_minus8");
            sps.bit_depth_chroma_minus8 = CAVLCReader.readUE(in, "SPS: bit_depth_chroma_minus8");
            sps.qpprime_y_zero_transform_bypass_flag = CAVLCReader.readBool(in, "SPS: qpprime_y_zero_transform_bypass_flag");
            boolean seqScalingMatrixPresent = CAVLCReader.readBool(in, "SPS: seq_scaling_matrix_present_lag");
            if (seqScalingMatrixPresent) {
                readScalingListMatrix(in, sps);
            }
        } else {
            sps.chroma_format_idc = ColorSpace.YUV420;
        }
        sps.log2_max_frame_num_minus4 = CAVLCReader.readUE(in, "SPS: log2_max_frame_num_minus4");
        sps.pic_order_cnt_type = CAVLCReader.readUE(in, "SPS: pic_order_cnt_type");
        if (sps.pic_order_cnt_type == 0) {
            sps.log2_max_pic_order_cnt_lsb_minus4 = CAVLCReader.readUE(in, "SPS: log2_max_pic_order_cnt_lsb_minus4");
        } else if (sps.pic_order_cnt_type == 1) {
            sps.delta_pic_order_always_zero_flag = CAVLCReader.readBool(in, "SPS: delta_pic_order_always_zero_flag");
            sps.offset_for_non_ref_pic = CAVLCReader.readSE(in, "SPS: offset_for_non_ref_pic");
            sps.offset_for_top_to_bottom_field = CAVLCReader.readSE(in, "SPS: offset_for_top_to_bottom_field");
            sps.num_ref_frames_in_pic_order_cnt_cycle = CAVLCReader.readUE(in, "SPS: num_ref_frames_in_pic_order_cnt_cycle");
            sps.offsetForRefFrame = new int[sps.num_ref_frames_in_pic_order_cnt_cycle];
            for (int i = 0; i < sps.num_ref_frames_in_pic_order_cnt_cycle; i++) {
                sps.offsetForRefFrame[i] = CAVLCReader.readSE(in, "SPS: offsetForRefFrame [" + i + "]");
            }
        }
        sps.num_ref_frames = CAVLCReader.readUE(in, "SPS: num_ref_frames");
        sps.gaps_in_frame_num_value_allowed_flag = CAVLCReader.readBool(in, "SPS: gaps_in_frame_num_value_allowed_flag");
        sps.pic_width_in_mbs_minus1 = CAVLCReader.readUE(in, "SPS: pic_width_in_mbs_minus1");
        sps.pic_height_in_map_units_minus1 = CAVLCReader.readUE(in, "SPS: pic_height_in_map_units_minus1");
        sps.frame_mbs_only_flag = CAVLCReader.readBool(in, "SPS: frame_mbs_only_flag");
        if (!sps.frame_mbs_only_flag) {
            sps.mb_adaptive_frame_field_flag = CAVLCReader.readBool(in, "SPS: mb_adaptive_frame_field_flag");
        }
        sps.direct_8x8_inference_flag = CAVLCReader.readBool(in, "SPS: direct_8x8_inference_flag");
        sps.frame_cropping_flag = CAVLCReader.readBool(in, "SPS: frame_cropping_flag");
        if (sps.frame_cropping_flag) {
            sps.frame_crop_left_offset = CAVLCReader.readUE(in, "SPS: frame_crop_left_offset");
            sps.frame_crop_right_offset = CAVLCReader.readUE(in, "SPS: frame_crop_right_offset");
            sps.frame_crop_top_offset = CAVLCReader.readUE(in, "SPS: frame_crop_top_offset");
            sps.frame_crop_bottom_offset = CAVLCReader.readUE(in, "SPS: frame_crop_bottom_offset");
        }
        boolean vui_parameters_present_flag = CAVLCReader.readBool(in, "SPS: vui_parameters_present_flag");
        if (vui_parameters_present_flag) {
            sps.vuiParams = readVUIParameters(in);
        }
        return sps;
    }

    private static void readScalingListMatrix(BitReader in, SeqParameterSet sps) {
        sps.scalingMatrix = new ScalingMatrix();
        for (int i = 0; i < 8; i++) {
            boolean seqScalingListPresentFlag = CAVLCReader.readBool(in, "SPS: seqScalingListPresentFlag");
            if (seqScalingListPresentFlag) {
                sps.scalingMatrix.ScalingList4x4 = new ScalingList[8];
                sps.scalingMatrix.ScalingList8x8 = new ScalingList[8];
                if (i < 6) {
                    sps.scalingMatrix.ScalingList4x4[i] = ScalingList.read(in, 16);
                } else {
                    sps.scalingMatrix.ScalingList8x8[i - 6] = ScalingList.read(in, 64);
                }
            }
        }
    }

    private static VUIParameters readVUIParameters(BitReader in) {
        VUIParameters vuip = new VUIParameters();
        vuip.aspect_ratio_info_present_flag = CAVLCReader.readBool(in, "VUI: aspect_ratio_info_present_flag");
        if (vuip.aspect_ratio_info_present_flag) {
            vuip.aspect_ratio = AspectRatio.fromValue(CAVLCReader.readNBit(in, 8, "VUI: aspect_ratio"));
            if (vuip.aspect_ratio == AspectRatio.Extended_SAR) {
                vuip.sar_width = CAVLCReader.readNBit(in, 16, "VUI: sar_width");
                vuip.sar_height = CAVLCReader.readNBit(in, 16, "VUI: sar_height");
            }
        }
        vuip.overscan_info_present_flag = CAVLCReader.readBool(in, "VUI: overscan_info_present_flag");
        if (vuip.overscan_info_present_flag) {
            vuip.overscan_appropriate_flag = CAVLCReader.readBool(in, "VUI: overscan_appropriate_flag");
        }
        vuip.video_signal_type_present_flag = CAVLCReader.readBool(in, "VUI: video_signal_type_present_flag");
        if (vuip.video_signal_type_present_flag) {
            vuip.video_format = CAVLCReader.readNBit(in, 3, "VUI: video_format");
            vuip.video_full_range_flag = CAVLCReader.readBool(in, "VUI: video_full_range_flag");
            vuip.colour_description_present_flag = CAVLCReader.readBool(in, "VUI: colour_description_present_flag");
            if (vuip.colour_description_present_flag) {
                vuip.colour_primaries = CAVLCReader.readNBit(in, 8, "VUI: colour_primaries");
                vuip.transfer_characteristics = CAVLCReader.readNBit(in, 8, "VUI: transfer_characteristics");
                vuip.matrix_coefficients = CAVLCReader.readNBit(in, 8, "VUI: matrix_coefficients");
            }
        }
        vuip.chroma_loc_info_present_flag = CAVLCReader.readBool(in, "VUI: chroma_loc_info_present_flag");
        if (vuip.chroma_loc_info_present_flag) {
            vuip.chroma_sample_loc_type_top_field = CAVLCReader.readUE(in, "VUI chroma_sample_loc_type_top_field");
            vuip.chroma_sample_loc_type_bottom_field = CAVLCReader.readUE(in, "VUI chroma_sample_loc_type_bottom_field");
        }
        vuip.timing_info_present_flag = CAVLCReader.readBool(in, "VUI: timing_info_present_flag");
        if (vuip.timing_info_present_flag) {
            vuip.num_units_in_tick = CAVLCReader.readNBit(in, 32, "VUI: num_units_in_tick");
            vuip.time_scale = CAVLCReader.readNBit(in, 32, "VUI: time_scale");
            vuip.fixed_frame_rate_flag = CAVLCReader.readBool(in, "VUI: fixed_frame_rate_flag");
        }
        boolean nal_hrd_parameters_present_flag = CAVLCReader.readBool(in, "VUI: nal_hrd_parameters_present_flag");
        if (nal_hrd_parameters_present_flag) {
            vuip.nalHRDParams = readHRDParameters(in);
        }
        boolean vcl_hrd_parameters_present_flag = CAVLCReader.readBool(in, "VUI: vcl_hrd_parameters_present_flag");
        if (vcl_hrd_parameters_present_flag) {
            vuip.vclHRDParams = readHRDParameters(in);
        }
        if (nal_hrd_parameters_present_flag || vcl_hrd_parameters_present_flag) {
            vuip.low_delay_hrd_flag = CAVLCReader.readBool(in, "VUI: low_delay_hrd_flag");
        }
        vuip.pic_struct_present_flag = CAVLCReader.readBool(in, "VUI: pic_struct_present_flag");
        boolean bitstream_restriction_flag = CAVLCReader.readBool(in, "VUI: bitstream_restriction_flag");
        if (bitstream_restriction_flag) {
            vuip.bitstreamRestriction = new VUIParameters.BitstreamRestriction();
            vuip.bitstreamRestriction.motion_vectors_over_pic_boundaries_flag = CAVLCReader.readBool(in, "VUI: motion_vectors_over_pic_boundaries_flag");
            vuip.bitstreamRestriction.max_bytes_per_pic_denom = CAVLCReader.readUE(in, "VUI max_bytes_per_pic_denom");
            vuip.bitstreamRestriction.max_bits_per_mb_denom = CAVLCReader.readUE(in, "VUI max_bits_per_mb_denom");
            vuip.bitstreamRestriction.log2_max_mv_length_horizontal = CAVLCReader.readUE(in, "VUI log2_max_mv_length_horizontal");
            vuip.bitstreamRestriction.log2_max_mv_length_vertical = CAVLCReader.readUE(in, "VUI log2_max_mv_length_vertical");
            vuip.bitstreamRestriction.num_reorder_frames = CAVLCReader.readUE(in, "VUI num_reorder_frames");
            vuip.bitstreamRestriction.max_dec_frame_buffering = CAVLCReader.readUE(in, "VUI max_dec_frame_buffering");
        }
        return vuip;
    }

    private static HRDParameters readHRDParameters(BitReader in) {
        HRDParameters hrd = new HRDParameters();
        hrd.cpb_cnt_minus1 = CAVLCReader.readUE(in, "SPS: cpb_cnt_minus1");
        hrd.bit_rate_scale = CAVLCReader.readNBit(in, 4, "HRD: bit_rate_scale");
        hrd.cpb_size_scale = CAVLCReader.readNBit(in, 4, "HRD: cpb_size_scale");
        hrd.bit_rate_value_minus1 = new int[hrd.cpb_cnt_minus1 + 1];
        hrd.cpb_size_value_minus1 = new int[hrd.cpb_cnt_minus1 + 1];
        hrd.cbr_flag = new boolean[hrd.cpb_cnt_minus1 + 1];
        for (int SchedSelIdx = 0; SchedSelIdx <= hrd.cpb_cnt_minus1; SchedSelIdx++) {
            hrd.bit_rate_value_minus1[SchedSelIdx] = CAVLCReader.readUE(in, "HRD: bit_rate_value_minus1");
            hrd.cpb_size_value_minus1[SchedSelIdx] = CAVLCReader.readUE(in, "HRD: cpb_size_value_minus1");
            hrd.cbr_flag[SchedSelIdx] = CAVLCReader.readBool(in, "HRD: cbr_flag");
        }
        hrd.initial_cpb_removal_delay_length_minus1 = CAVLCReader.readNBit(in, 5, "HRD: initial_cpb_removal_delay_length_minus1");
        hrd.cpb_removal_delay_length_minus1 = CAVLCReader.readNBit(in, 5, "HRD: cpb_removal_delay_length_minus1");
        hrd.dpb_output_delay_length_minus1 = CAVLCReader.readNBit(in, 5, "HRD: dpb_output_delay_length_minus1");
        hrd.time_offset_length = CAVLCReader.readNBit(in, 5, "HRD: time_offset_length");
        return hrd;
    }

    public void write(ByteBuffer out) {
        BitWriter writer = new BitWriter(out);
        CAVLCWriter.writeNBit(writer, this.profile_idc, 8, "SPS: profile_idc");
        CAVLCWriter.writeBool(writer, this.constraint_set_0_flag, "SPS: constraint_set_0_flag");
        CAVLCWriter.writeBool(writer, this.constraint_set_1_flag, "SPS: constraint_set_1_flag");
        CAVLCWriter.writeBool(writer, this.constraint_set_2_flag, "SPS: constraint_set_2_flag");
        CAVLCWriter.writeBool(writer, this.constraint_set_3_flag, "SPS: constraint_set_3_flag");
        CAVLCWriter.writeNBit(writer, 0L, 4, "SPS: reserved");
        CAVLCWriter.writeNBit(writer, this.level_idc, 8, "SPS: level_idc");
        CAVLCWriter.writeUE(writer, this.seq_parameter_set_id, "SPS: seq_parameter_set_id");
        if (this.profile_idc == 100 || this.profile_idc == 110 || this.profile_idc == 122 || this.profile_idc == 144) {
            CAVLCWriter.writeUE(writer, fromColor(this.chroma_format_idc), "SPS: chroma_format_idc");
            if (this.chroma_format_idc == ColorSpace.YUV444) {
                CAVLCWriter.writeBool(writer, this.residual_color_transform_flag, "SPS: residual_color_transform_flag");
            }
            CAVLCWriter.writeUE(writer, this.bit_depth_luma_minus8, "SPS: ");
            CAVLCWriter.writeUE(writer, this.bit_depth_chroma_minus8, "SPS: ");
            CAVLCWriter.writeBool(writer, this.qpprime_y_zero_transform_bypass_flag, "SPS: qpprime_y_zero_transform_bypass_flag");
            CAVLCWriter.writeBool(writer, this.scalingMatrix != null, "SPS: ");
            if (this.scalingMatrix != null) {
                for (int i = 0; i < 8; i++) {
                    if (i < 6) {
                        CAVLCWriter.writeBool(writer, this.scalingMatrix.ScalingList4x4[i] != null, "SPS: ");
                        if (this.scalingMatrix.ScalingList4x4[i] != null) {
                            this.scalingMatrix.ScalingList4x4[i].write(writer);
                        }
                    } else {
                        CAVLCWriter.writeBool(writer, this.scalingMatrix.ScalingList8x8[i + (-6)] != null, "SPS: ");
                        if (this.scalingMatrix.ScalingList8x8[i - 6] != null) {
                            this.scalingMatrix.ScalingList8x8[i - 6].write(writer);
                        }
                    }
                }
            }
        }
        CAVLCWriter.writeUE(writer, this.log2_max_frame_num_minus4, "SPS: log2_max_frame_num_minus4");
        CAVLCWriter.writeUE(writer, this.pic_order_cnt_type, "SPS: pic_order_cnt_type");
        if (this.pic_order_cnt_type == 0) {
            CAVLCWriter.writeUE(writer, this.log2_max_pic_order_cnt_lsb_minus4, "SPS: log2_max_pic_order_cnt_lsb_minus4");
        } else if (this.pic_order_cnt_type == 1) {
            CAVLCWriter.writeBool(writer, this.delta_pic_order_always_zero_flag, "SPS: delta_pic_order_always_zero_flag");
            CAVLCWriter.writeSE(writer, this.offset_for_non_ref_pic, "SPS: offset_for_non_ref_pic");
            CAVLCWriter.writeSE(writer, this.offset_for_top_to_bottom_field, "SPS: offset_for_top_to_bottom_field");
            CAVLCWriter.writeUE(writer, this.offsetForRefFrame.length, "SPS: ");
            for (int i2 = 0; i2 < this.offsetForRefFrame.length; i2++) {
                CAVLCWriter.writeSE(writer, this.offsetForRefFrame[i2], "SPS: ");
            }
        }
        CAVLCWriter.writeUE(writer, this.num_ref_frames, "SPS: num_ref_frames");
        CAVLCWriter.writeBool(writer, this.gaps_in_frame_num_value_allowed_flag, "SPS: gaps_in_frame_num_value_allowed_flag");
        CAVLCWriter.writeUE(writer, this.pic_width_in_mbs_minus1, "SPS: pic_width_in_mbs_minus1");
        CAVLCWriter.writeUE(writer, this.pic_height_in_map_units_minus1, "SPS: pic_height_in_map_units_minus1");
        CAVLCWriter.writeBool(writer, this.frame_mbs_only_flag, "SPS: frame_mbs_only_flag");
        if (!this.frame_mbs_only_flag) {
            CAVLCWriter.writeBool(writer, this.mb_adaptive_frame_field_flag, "SPS: mb_adaptive_frame_field_flag");
        }
        CAVLCWriter.writeBool(writer, this.direct_8x8_inference_flag, "SPS: direct_8x8_inference_flag");
        CAVLCWriter.writeBool(writer, this.frame_cropping_flag, "SPS: frame_cropping_flag");
        if (this.frame_cropping_flag) {
            CAVLCWriter.writeUE(writer, this.frame_crop_left_offset, "SPS: frame_crop_left_offset");
            CAVLCWriter.writeUE(writer, this.frame_crop_right_offset, "SPS: frame_crop_right_offset");
            CAVLCWriter.writeUE(writer, this.frame_crop_top_offset, "SPS: frame_crop_top_offset");
            CAVLCWriter.writeUE(writer, this.frame_crop_bottom_offset, "SPS: frame_crop_bottom_offset");
        }
        CAVLCWriter.writeBool(writer, this.vuiParams != null, "SPS: ");
        if (this.vuiParams != null) {
            writeVUIParameters(this.vuiParams, writer);
        }
        CAVLCWriter.writeTrailingBits(writer);
    }

    private void writeVUIParameters(VUIParameters vuip, BitWriter writer) {
        CAVLCWriter.writeBool(writer, vuip.aspect_ratio_info_present_flag, "VUI: aspect_ratio_info_present_flag");
        if (vuip.aspect_ratio_info_present_flag) {
            CAVLCWriter.writeNBit(writer, vuip.aspect_ratio.getValue(), 8, "VUI: aspect_ratio");
            if (vuip.aspect_ratio == AspectRatio.Extended_SAR) {
                CAVLCWriter.writeNBit(writer, vuip.sar_width, 16, "VUI: sar_width");
                CAVLCWriter.writeNBit(writer, vuip.sar_height, 16, "VUI: sar_height");
            }
        }
        CAVLCWriter.writeBool(writer, vuip.overscan_info_present_flag, "VUI: overscan_info_present_flag");
        if (vuip.overscan_info_present_flag) {
            CAVLCWriter.writeBool(writer, vuip.overscan_appropriate_flag, "VUI: overscan_appropriate_flag");
        }
        CAVLCWriter.writeBool(writer, vuip.video_signal_type_present_flag, "VUI: video_signal_type_present_flag");
        if (vuip.video_signal_type_present_flag) {
            CAVLCWriter.writeNBit(writer, vuip.video_format, 3, "VUI: video_format");
            CAVLCWriter.writeBool(writer, vuip.video_full_range_flag, "VUI: video_full_range_flag");
            CAVLCWriter.writeBool(writer, vuip.colour_description_present_flag, "VUI: colour_description_present_flag");
            if (vuip.colour_description_present_flag) {
                CAVLCWriter.writeNBit(writer, vuip.colour_primaries, 8, "VUI: colour_primaries");
                CAVLCWriter.writeNBit(writer, vuip.transfer_characteristics, 8, "VUI: transfer_characteristics");
                CAVLCWriter.writeNBit(writer, vuip.matrix_coefficients, 8, "VUI: matrix_coefficients");
            }
        }
        CAVLCWriter.writeBool(writer, vuip.chroma_loc_info_present_flag, "VUI: chroma_loc_info_present_flag");
        if (vuip.chroma_loc_info_present_flag) {
            CAVLCWriter.writeUE(writer, vuip.chroma_sample_loc_type_top_field, "VUI: chroma_sample_loc_type_top_field");
            CAVLCWriter.writeUE(writer, vuip.chroma_sample_loc_type_bottom_field, "VUI: chroma_sample_loc_type_bottom_field");
        }
        CAVLCWriter.writeBool(writer, vuip.timing_info_present_flag, "VUI: timing_info_present_flag");
        if (vuip.timing_info_present_flag) {
            CAVLCWriter.writeNBit(writer, vuip.num_units_in_tick, 32, "VUI: num_units_in_tick");
            CAVLCWriter.writeNBit(writer, vuip.time_scale, 32, "VUI: time_scale");
            CAVLCWriter.writeBool(writer, vuip.fixed_frame_rate_flag, "VUI: fixed_frame_rate_flag");
        }
        CAVLCWriter.writeBool(writer, vuip.nalHRDParams != null, "VUI: ");
        if (vuip.nalHRDParams != null) {
            writeHRDParameters(vuip.nalHRDParams, writer);
        }
        CAVLCWriter.writeBool(writer, vuip.vclHRDParams != null, "VUI: ");
        if (vuip.vclHRDParams != null) {
            writeHRDParameters(vuip.vclHRDParams, writer);
        }
        if (vuip.nalHRDParams != null || vuip.vclHRDParams != null) {
            CAVLCWriter.writeBool(writer, vuip.low_delay_hrd_flag, "VUI: low_delay_hrd_flag");
        }
        CAVLCWriter.writeBool(writer, vuip.pic_struct_present_flag, "VUI: pic_struct_present_flag");
        CAVLCWriter.writeBool(writer, vuip.bitstreamRestriction != null, "VUI: ");
        if (vuip.bitstreamRestriction != null) {
            CAVLCWriter.writeBool(writer, vuip.bitstreamRestriction.motion_vectors_over_pic_boundaries_flag, "VUI: motion_vectors_over_pic_boundaries_flag");
            CAVLCWriter.writeUE(writer, vuip.bitstreamRestriction.max_bytes_per_pic_denom, "VUI: max_bytes_per_pic_denom");
            CAVLCWriter.writeUE(writer, vuip.bitstreamRestriction.max_bits_per_mb_denom, "VUI: max_bits_per_mb_denom");
            CAVLCWriter.writeUE(writer, vuip.bitstreamRestriction.log2_max_mv_length_horizontal, "VUI: log2_max_mv_length_horizontal");
            CAVLCWriter.writeUE(writer, vuip.bitstreamRestriction.log2_max_mv_length_vertical, "VUI: log2_max_mv_length_vertical");
            CAVLCWriter.writeUE(writer, vuip.bitstreamRestriction.num_reorder_frames, "VUI: num_reorder_frames");
            CAVLCWriter.writeUE(writer, vuip.bitstreamRestriction.max_dec_frame_buffering, "VUI: max_dec_frame_buffering");
        }
    }

    private void writeHRDParameters(HRDParameters hrd, BitWriter writer) {
        CAVLCWriter.writeUE(writer, hrd.cpb_cnt_minus1, "HRD: cpb_cnt_minus1");
        CAVLCWriter.writeNBit(writer, hrd.bit_rate_scale, 4, "HRD: bit_rate_scale");
        CAVLCWriter.writeNBit(writer, hrd.cpb_size_scale, 4, "HRD: cpb_size_scale");
        for (int SchedSelIdx = 0; SchedSelIdx <= hrd.cpb_cnt_minus1; SchedSelIdx++) {
            CAVLCWriter.writeUE(writer, hrd.bit_rate_value_minus1[SchedSelIdx], "HRD: ");
            CAVLCWriter.writeUE(writer, hrd.cpb_size_value_minus1[SchedSelIdx], "HRD: ");
            CAVLCWriter.writeBool(writer, hrd.cbr_flag[SchedSelIdx], "HRD: ");
        }
        CAVLCWriter.writeNBit(writer, hrd.initial_cpb_removal_delay_length_minus1, 5, "HRD: initial_cpb_removal_delay_length_minus1");
        CAVLCWriter.writeNBit(writer, hrd.cpb_removal_delay_length_minus1, 5, "HRD: cpb_removal_delay_length_minus1");
        CAVLCWriter.writeNBit(writer, hrd.dpb_output_delay_length_minus1, 5, "HRD: dpb_output_delay_length_minus1");
        CAVLCWriter.writeNBit(writer, hrd.time_offset_length, 5, "HRD: time_offset_length");
    }

    public SeqParameterSet copy() {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        write(buf);
        buf.flip();
        return read(buf);
    }
}
