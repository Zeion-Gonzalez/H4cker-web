package org.jcodec.codecs.h264.io.model;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.codecs.h264.decode.CAVLCReader;
import org.jcodec.codecs.h264.io.write.CAVLCWriter;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class PictureParameterSet {
    public int[] bottom_right;
    public int chroma_qp_index_offset;
    public boolean constrained_intra_pred_flag;
    public boolean deblocking_filter_control_present_flag;
    public boolean entropy_coding_mode_flag;
    public PPSExt extended;
    public int[] num_ref_idx_active_minus1 = new int[2];
    public int num_slice_groups_minus1;
    public int pic_init_qp_minus26;
    public int pic_init_qs_minus26;
    public boolean pic_order_present_flag;
    public int pic_parameter_set_id;
    public boolean redundant_pic_cnt_present_flag;
    public int[] run_length_minus1;
    public int seq_parameter_set_id;
    public boolean slice_group_change_direction_flag;
    public int slice_group_change_rate_minus1;
    public int[] slice_group_id;
    public int slice_group_map_type;
    public int[] top_left;
    public int weighted_bipred_idc;
    public boolean weighted_pred_flag;

    /* loaded from: classes.dex */
    public static class PPSExt {
        public boolean[] pic_scaling_list_present_flag;
        public ScalingMatrix scalindMatrix;
        public int second_chroma_qp_index_offset;
        public boolean transform_8x8_mode_flag;
    }

    public static PictureParameterSet read(ByteBuffer is) {
        int NumberBitsPerSliceGroupId;
        BitReader in = new BitReader(is);
        PictureParameterSet pps = new PictureParameterSet();
        pps.pic_parameter_set_id = CAVLCReader.readUE(in, "PPS: pic_parameter_set_id");
        pps.seq_parameter_set_id = CAVLCReader.readUE(in, "PPS: seq_parameter_set_id");
        pps.entropy_coding_mode_flag = CAVLCReader.readBool(in, "PPS: entropy_coding_mode_flag");
        pps.pic_order_present_flag = CAVLCReader.readBool(in, "PPS: pic_order_present_flag");
        pps.num_slice_groups_minus1 = CAVLCReader.readUE(in, "PPS: num_slice_groups_minus1");
        if (pps.num_slice_groups_minus1 > 0) {
            pps.slice_group_map_type = CAVLCReader.readUE(in, "PPS: slice_group_map_type");
            pps.top_left = new int[pps.num_slice_groups_minus1 + 1];
            pps.bottom_right = new int[pps.num_slice_groups_minus1 + 1];
            pps.run_length_minus1 = new int[pps.num_slice_groups_minus1 + 1];
            if (pps.slice_group_map_type == 0) {
                for (int iGroup = 0; iGroup <= pps.num_slice_groups_minus1; iGroup++) {
                    pps.run_length_minus1[iGroup] = CAVLCReader.readUE(in, "PPS: run_length_minus1");
                }
            } else if (pps.slice_group_map_type == 2) {
                for (int iGroup2 = 0; iGroup2 < pps.num_slice_groups_minus1; iGroup2++) {
                    pps.top_left[iGroup2] = CAVLCReader.readUE(in, "PPS: top_left");
                    pps.bottom_right[iGroup2] = CAVLCReader.readUE(in, "PPS: bottom_right");
                }
            } else if (pps.slice_group_map_type == 3 || pps.slice_group_map_type == 4 || pps.slice_group_map_type == 5) {
                pps.slice_group_change_direction_flag = CAVLCReader.readBool(in, "PPS: slice_group_change_direction_flag");
                pps.slice_group_change_rate_minus1 = CAVLCReader.readUE(in, "PPS: slice_group_change_rate_minus1");
            } else if (pps.slice_group_map_type == 6) {
                if (pps.num_slice_groups_minus1 + 1 > 4) {
                    NumberBitsPerSliceGroupId = 3;
                } else if (pps.num_slice_groups_minus1 + 1 > 2) {
                    NumberBitsPerSliceGroupId = 2;
                } else {
                    NumberBitsPerSliceGroupId = 1;
                }
                int pic_size_in_map_units_minus1 = CAVLCReader.readUE(in, "PPS: pic_size_in_map_units_minus1");
                pps.slice_group_id = new int[pic_size_in_map_units_minus1 + 1];
                for (int i = 0; i <= pic_size_in_map_units_minus1; i++) {
                    pps.slice_group_id[i] = CAVLCReader.readU(in, NumberBitsPerSliceGroupId, "PPS: slice_group_id [" + i + "]f");
                }
            }
        }
        pps.num_ref_idx_active_minus1 = new int[]{CAVLCReader.readUE(in, "PPS: num_ref_idx_l0_active_minus1"), CAVLCReader.readUE(in, "PPS: num_ref_idx_l1_active_minus1")};
        pps.weighted_pred_flag = CAVLCReader.readBool(in, "PPS: weighted_pred_flag");
        pps.weighted_bipred_idc = CAVLCReader.readNBit(in, 2, "PPS: weighted_bipred_idc");
        pps.pic_init_qp_minus26 = CAVLCReader.readSE(in, "PPS: pic_init_qp_minus26");
        pps.pic_init_qs_minus26 = CAVLCReader.readSE(in, "PPS: pic_init_qs_minus26");
        pps.chroma_qp_index_offset = CAVLCReader.readSE(in, "PPS: chroma_qp_index_offset");
        pps.deblocking_filter_control_present_flag = CAVLCReader.readBool(in, "PPS: deblocking_filter_control_present_flag");
        pps.constrained_intra_pred_flag = CAVLCReader.readBool(in, "PPS: constrained_intra_pred_flag");
        pps.redundant_pic_cnt_present_flag = CAVLCReader.readBool(in, "PPS: redundant_pic_cnt_present_flag");
        if (CAVLCReader.moreRBSPData(in)) {
            pps.extended = new PPSExt();
            pps.extended.transform_8x8_mode_flag = CAVLCReader.readBool(in, "PPS: transform_8x8_mode_flag");
            boolean pic_scaling_matrix_present_flag = CAVLCReader.readBool(in, "PPS: pic_scaling_matrix_present_flag");
            if (pic_scaling_matrix_present_flag) {
                int i2 = 0;
                while (true) {
                    if (i2 >= ((pps.extended.transform_8x8_mode_flag ? 1 : 0) * 2) + 6) {
                        break;
                    }
                    boolean pic_scaling_list_present_flag = CAVLCReader.readBool(in, "PPS: pic_scaling_list_present_flag");
                    if (pic_scaling_list_present_flag) {
                        pps.extended.scalindMatrix.ScalingList4x4 = new ScalingList[8];
                        pps.extended.scalindMatrix.ScalingList8x8 = new ScalingList[8];
                        if (i2 < 6) {
                            pps.extended.scalindMatrix.ScalingList4x4[i2] = ScalingList.read(in, 16);
                        } else {
                            pps.extended.scalindMatrix.ScalingList8x8[i2 - 6] = ScalingList.read(in, 64);
                        }
                    }
                    i2++;
                }
            }
            pps.extended.second_chroma_qp_index_offset = CAVLCReader.readSE(in, "PPS: second_chroma_qp_index_offset");
        }
        return pps;
    }

    public void write(ByteBuffer out) {
        int NumberBitsPerSliceGroupId;
        BitWriter writer = new BitWriter(out);
        CAVLCWriter.writeUE(writer, this.pic_parameter_set_id, "PPS: pic_parameter_set_id");
        CAVLCWriter.writeUE(writer, this.seq_parameter_set_id, "PPS: seq_parameter_set_id");
        CAVLCWriter.writeBool(writer, this.entropy_coding_mode_flag, "PPS: entropy_coding_mode_flag");
        CAVLCWriter.writeBool(writer, this.pic_order_present_flag, "PPS: pic_order_present_flag");
        CAVLCWriter.writeUE(writer, this.num_slice_groups_minus1, "PPS: num_slice_groups_minus1");
        if (this.num_slice_groups_minus1 > 0) {
            CAVLCWriter.writeUE(writer, this.slice_group_map_type, "PPS: slice_group_map_type");
            int[] top_left = new int[1];
            int[] bottom_right = new int[1];
            int[] run_length_minus1 = new int[1];
            if (this.slice_group_map_type == 0) {
                for (int iGroup = 0; iGroup <= this.num_slice_groups_minus1; iGroup++) {
                    CAVLCWriter.writeUE(writer, run_length_minus1[iGroup], "PPS: ");
                }
            } else if (this.slice_group_map_type == 2) {
                for (int iGroup2 = 0; iGroup2 < this.num_slice_groups_minus1; iGroup2++) {
                    CAVLCWriter.writeUE(writer, top_left[iGroup2], "PPS: ");
                    CAVLCWriter.writeUE(writer, bottom_right[iGroup2], "PPS: ");
                }
            } else if (this.slice_group_map_type == 3 || this.slice_group_map_type == 4 || this.slice_group_map_type == 5) {
                CAVLCWriter.writeBool(writer, this.slice_group_change_direction_flag, "PPS: slice_group_change_direction_flag");
                CAVLCWriter.writeUE(writer, this.slice_group_change_rate_minus1, "PPS: slice_group_change_rate_minus1");
            } else if (this.slice_group_map_type == 6) {
                if (this.num_slice_groups_minus1 + 1 > 4) {
                    NumberBitsPerSliceGroupId = 3;
                } else if (this.num_slice_groups_minus1 + 1 > 2) {
                    NumberBitsPerSliceGroupId = 2;
                } else {
                    NumberBitsPerSliceGroupId = 1;
                }
                CAVLCWriter.writeUE(writer, this.slice_group_id.length, "PPS: ");
                for (int i = 0; i <= this.slice_group_id.length; i++) {
                    CAVLCWriter.writeU(writer, this.slice_group_id[i], NumberBitsPerSliceGroupId);
                }
            }
        }
        CAVLCWriter.writeUE(writer, this.num_ref_idx_active_minus1[0], "PPS: num_ref_idx_l0_active_minus1");
        CAVLCWriter.writeUE(writer, this.num_ref_idx_active_minus1[1], "PPS: num_ref_idx_l1_active_minus1");
        CAVLCWriter.writeBool(writer, this.weighted_pred_flag, "PPS: weighted_pred_flag");
        CAVLCWriter.writeNBit(writer, this.weighted_bipred_idc, 2, "PPS: weighted_bipred_idc");
        CAVLCWriter.writeSE(writer, this.pic_init_qp_minus26, "PPS: pic_init_qp_minus26");
        CAVLCWriter.writeSE(writer, this.pic_init_qs_minus26, "PPS: pic_init_qs_minus26");
        CAVLCWriter.writeSE(writer, this.chroma_qp_index_offset, "PPS: chroma_qp_index_offset");
        CAVLCWriter.writeBool(writer, this.deblocking_filter_control_present_flag, "PPS: deblocking_filter_control_present_flag");
        CAVLCWriter.writeBool(writer, this.constrained_intra_pred_flag, "PPS: constrained_intra_pred_flag");
        CAVLCWriter.writeBool(writer, this.redundant_pic_cnt_present_flag, "PPS: redundant_pic_cnt_present_flag");
        if (this.extended != null) {
            CAVLCWriter.writeBool(writer, this.extended.transform_8x8_mode_flag, "PPS: transform_8x8_mode_flag");
            CAVLCWriter.writeBool(writer, this.extended.scalindMatrix != null, "PPS: scalindMatrix");
            if (this.extended.scalindMatrix != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= ((this.extended.transform_8x8_mode_flag ? 1 : 0) * 2) + 6) {
                        break;
                    }
                    if (i2 < 6) {
                        CAVLCWriter.writeBool(writer, this.extended.scalindMatrix.ScalingList4x4[i2] != null, "PPS: ");
                        if (this.extended.scalindMatrix.ScalingList4x4[i2] != null) {
                            this.extended.scalindMatrix.ScalingList4x4[i2].write(writer);
                        }
                    } else {
                        CAVLCWriter.writeBool(writer, this.extended.scalindMatrix.ScalingList8x8[i2 + (-6)] != null, "PPS: ");
                        if (this.extended.scalindMatrix.ScalingList8x8[i2 - 6] != null) {
                            this.extended.scalindMatrix.ScalingList8x8[i2 - 6].write(writer);
                        }
                    }
                    i2++;
                }
            }
            CAVLCWriter.writeSE(writer, this.extended.second_chroma_qp_index_offset, "PPS: ");
        }
        CAVLCWriter.writeTrailingBits(writer);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.bottom_right) + 31;
        return (((((((((((((((((((((((((((((((((((((((((((result * 31) + this.chroma_qp_index_offset) * 31) + (this.constrained_intra_pred_flag ? 1231 : 1237)) * 31) + (this.deblocking_filter_control_present_flag ? 1231 : 1237)) * 31) + (this.entropy_coding_mode_flag ? 1231 : 1237)) * 31) + (this.extended == null ? 0 : this.extended.hashCode())) * 31) + this.num_ref_idx_active_minus1[0]) * 31) + this.num_ref_idx_active_minus1[1]) * 31) + this.num_slice_groups_minus1) * 31) + this.pic_init_qp_minus26) * 31) + this.pic_init_qs_minus26) * 31) + (this.pic_order_present_flag ? 1231 : 1237)) * 31) + this.pic_parameter_set_id) * 31) + (this.redundant_pic_cnt_present_flag ? 1231 : 1237)) * 31) + Arrays.hashCode(this.run_length_minus1)) * 31) + this.seq_parameter_set_id) * 31) + (this.slice_group_change_direction_flag ? 1231 : 1237)) * 31) + this.slice_group_change_rate_minus1) * 31) + Arrays.hashCode(this.slice_group_id)) * 31) + this.slice_group_map_type) * 31) + Arrays.hashCode(this.top_left)) * 31) + this.weighted_bipred_idc) * 31) + (this.weighted_pred_flag ? 1231 : 1237);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            PictureParameterSet other = (PictureParameterSet) obj;
            if (Arrays.equals(this.bottom_right, other.bottom_right) && this.chroma_qp_index_offset == other.chroma_qp_index_offset && this.constrained_intra_pred_flag == other.constrained_intra_pred_flag && this.deblocking_filter_control_present_flag == other.deblocking_filter_control_present_flag && this.entropy_coding_mode_flag == other.entropy_coding_mode_flag) {
                if (this.extended == null) {
                    if (other.extended != null) {
                        return false;
                    }
                } else if (!this.extended.equals(other.extended)) {
                    return false;
                }
                return this.num_ref_idx_active_minus1[0] == other.num_ref_idx_active_minus1[0] && this.num_ref_idx_active_minus1[1] == other.num_ref_idx_active_minus1[1] && this.num_slice_groups_minus1 == other.num_slice_groups_minus1 && this.pic_init_qp_minus26 == other.pic_init_qp_minus26 && this.pic_init_qs_minus26 == other.pic_init_qs_minus26 && this.pic_order_present_flag == other.pic_order_present_flag && this.pic_parameter_set_id == other.pic_parameter_set_id && this.redundant_pic_cnt_present_flag == other.redundant_pic_cnt_present_flag && Arrays.equals(this.run_length_minus1, other.run_length_minus1) && this.seq_parameter_set_id == other.seq_parameter_set_id && this.slice_group_change_direction_flag == other.slice_group_change_direction_flag && this.slice_group_change_rate_minus1 == other.slice_group_change_rate_minus1 && Arrays.equals(this.slice_group_id, other.slice_group_id) && this.slice_group_map_type == other.slice_group_map_type && Arrays.equals(this.top_left, other.top_left) && this.weighted_bipred_idc == other.weighted_bipred_idc && this.weighted_pred_flag == other.weighted_pred_flag;
            }
            return false;
        }
        return false;
    }

    public PictureParameterSet copy() {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        write(buf);
        buf.flip();
        return read(buf);
    }
}
