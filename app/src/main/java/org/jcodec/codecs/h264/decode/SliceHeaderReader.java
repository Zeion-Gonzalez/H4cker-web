package org.jcodec.codecs.h264.decode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.PredictionWeightTable;
import org.jcodec.codecs.h264.io.model.RefPicMarking;
import org.jcodec.codecs.h264.io.model.RefPicMarkingIDR;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.io.model.SliceType;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.model.ColorSpace;

/* loaded from: classes.dex */
public class SliceHeaderReader {
    public SliceHeader readPart1(BitReader in) {
        SliceHeader sh = new SliceHeader();
        sh.first_mb_in_slice = CAVLCReader.readUE(in, "SH: first_mb_in_slice");
        int sh_type = CAVLCReader.readUE(in, "SH: slice_type");
        sh.slice_type = SliceType.fromValue(sh_type % 5);
        sh.slice_type_restr = sh_type / 5 > 0;
        sh.pic_parameter_set_id = CAVLCReader.readUE(in, "SH: pic_parameter_set_id");
        return sh;
    }

    public SliceHeader readPart2(SliceHeader sh, NALUnit nalUnit, SeqParameterSet sps, PictureParameterSet pps, BitReader in) {
        sh.pps = pps;
        sh.sps = sps;
        sh.frame_num = CAVLCReader.readU(in, sps.log2_max_frame_num_minus4 + 4, "SH: frame_num");
        if (!sps.frame_mbs_only_flag) {
            sh.field_pic_flag = CAVLCReader.readBool(in, "SH: field_pic_flag");
            if (sh.field_pic_flag) {
                sh.bottom_field_flag = CAVLCReader.readBool(in, "SH: bottom_field_flag");
            }
        }
        if (nalUnit.type == NALUnitType.IDR_SLICE) {
            sh.idr_pic_id = CAVLCReader.readUE(in, "SH: idr_pic_id");
        }
        if (sps.pic_order_cnt_type == 0) {
            sh.pic_order_cnt_lsb = CAVLCReader.readU(in, sps.log2_max_pic_order_cnt_lsb_minus4 + 4, "SH: pic_order_cnt_lsb");
            if (pps.pic_order_present_flag && !sps.field_pic_flag) {
                sh.delta_pic_order_cnt_bottom = CAVLCReader.readSE(in, "SH: delta_pic_order_cnt_bottom");
            }
        }
        sh.delta_pic_order_cnt = new int[2];
        if (sps.pic_order_cnt_type == 1 && !sps.delta_pic_order_always_zero_flag) {
            sh.delta_pic_order_cnt[0] = CAVLCReader.readSE(in, "SH: delta_pic_order_cnt[0]");
            if (pps.pic_order_present_flag && !sps.field_pic_flag) {
                sh.delta_pic_order_cnt[1] = CAVLCReader.readSE(in, "SH: delta_pic_order_cnt[1]");
            }
        }
        if (pps.redundant_pic_cnt_present_flag) {
            sh.redundant_pic_cnt = CAVLCReader.readUE(in, "SH: redundant_pic_cnt");
        }
        if (sh.slice_type == SliceType.B) {
            sh.direct_spatial_mv_pred_flag = CAVLCReader.readBool(in, "SH: direct_spatial_mv_pred_flag");
        }
        if (sh.slice_type == SliceType.P || sh.slice_type == SliceType.SP || sh.slice_type == SliceType.B) {
            sh.num_ref_idx_active_override_flag = CAVLCReader.readBool(in, "SH: num_ref_idx_active_override_flag");
            if (sh.num_ref_idx_active_override_flag) {
                sh.num_ref_idx_active_minus1[0] = CAVLCReader.readUE(in, "SH: num_ref_idx_l0_active_minus1");
                if (sh.slice_type == SliceType.B) {
                    sh.num_ref_idx_active_minus1[1] = CAVLCReader.readUE(in, "SH: num_ref_idx_l1_active_minus1");
                }
            }
        }
        readRefPicListReordering(sh, in);
        if ((pps.weighted_pred_flag && (sh.slice_type == SliceType.P || sh.slice_type == SliceType.SP)) || (pps.weighted_bipred_idc == 1 && sh.slice_type == SliceType.B)) {
            readPredWeightTable(sps, pps, sh, in);
        }
        if (nalUnit.nal_ref_idc != 0) {
            readDecoderPicMarking(nalUnit, sh, in);
        }
        if (pps.entropy_coding_mode_flag && sh.slice_type.isInter()) {
            sh.cabac_init_idc = CAVLCReader.readUE(in, "SH: cabac_init_idc");
        }
        sh.slice_qp_delta = CAVLCReader.readSE(in, "SH: slice_qp_delta");
        if (sh.slice_type == SliceType.SP || sh.slice_type == SliceType.SI) {
            if (sh.slice_type == SliceType.SP) {
                sh.sp_for_switch_flag = CAVLCReader.readBool(in, "SH: sp_for_switch_flag");
            }
            sh.slice_qs_delta = CAVLCReader.readSE(in, "SH: slice_qs_delta");
        }
        if (pps.deblocking_filter_control_present_flag) {
            sh.disable_deblocking_filter_idc = CAVLCReader.readUE(in, "SH: disable_deblocking_filter_idc");
            if (sh.disable_deblocking_filter_idc != 1) {
                sh.slice_alpha_c0_offset_div2 = CAVLCReader.readSE(in, "SH: slice_alpha_c0_offset_div2");
                sh.slice_beta_offset_div2 = CAVLCReader.readSE(in, "SH: slice_beta_offset_div2");
            }
        }
        if (pps.num_slice_groups_minus1 > 0 && pps.slice_group_map_type >= 3 && pps.slice_group_map_type <= 5) {
            int len = (H264Utils.getPicHeightInMbs(sps) * (sps.pic_width_in_mbs_minus1 + 1)) / (pps.slice_group_change_rate_minus1 + 1);
            if ((H264Utils.getPicHeightInMbs(sps) * (sps.pic_width_in_mbs_minus1 + 1)) % (pps.slice_group_change_rate_minus1 + 1) > 0) {
                len++;
            }
            sh.slice_group_change_cycle = CAVLCReader.readU(in, CeilLog2(len + 1), "SH: slice_group_change_cycle");
        }
        return sh;
    }

    private static int CeilLog2(int uiVal) {
        int uiTmp = uiVal - 1;
        int uiRet = 0;
        while (uiTmp != 0) {
            uiTmp >>= 1;
            uiRet++;
        }
        return uiRet;
    }

    private static void readDecoderPicMarking(NALUnit nalUnit, SliceHeader sh, BitReader in) {
        int memory_management_control_operation;
        if (nalUnit.type == NALUnitType.IDR_SLICE) {
            boolean no_output_of_prior_pics_flag = CAVLCReader.readBool(in, "SH: no_output_of_prior_pics_flag");
            boolean long_term_reference_flag = CAVLCReader.readBool(in, "SH: long_term_reference_flag");
            sh.refPicMarkingIDR = new RefPicMarkingIDR(no_output_of_prior_pics_flag, long_term_reference_flag);
            return;
        }
        boolean adaptive_ref_pic_marking_mode_flag = CAVLCReader.readBool(in, "SH: adaptive_ref_pic_marking_mode_flag");
        if (adaptive_ref_pic_marking_mode_flag) {
            ArrayList<RefPicMarking.Instruction> mmops = new ArrayList<>();
            do {
                memory_management_control_operation = CAVLCReader.readUE(in, "SH: memory_management_control_operation");
                RefPicMarking.Instruction instr = null;
                switch (memory_management_control_operation) {
                    case 1:
                        instr = new RefPicMarking.Instruction(RefPicMarking.InstrType.REMOVE_SHORT, CAVLCReader.readUE(in, "SH: difference_of_pic_nums_minus1") + 1, 0);
                        break;
                    case 2:
                        instr = new RefPicMarking.Instruction(RefPicMarking.InstrType.REMOVE_LONG, CAVLCReader.readUE(in, "SH: long_term_pic_num"), 0);
                        break;
                    case 3:
                        instr = new RefPicMarking.Instruction(RefPicMarking.InstrType.CONVERT_INTO_LONG, CAVLCReader.readUE(in, "SH: difference_of_pic_nums_minus1") + 1, CAVLCReader.readUE(in, "SH: long_term_frame_idx"));
                        break;
                    case 4:
                        instr = new RefPicMarking.Instruction(RefPicMarking.InstrType.TRUNK_LONG, CAVLCReader.readUE(in, "SH: max_long_term_frame_idx_plus1") - 1, 0);
                        break;
                    case 5:
                        instr = new RefPicMarking.Instruction(RefPicMarking.InstrType.CLEAR, 0, 0);
                        break;
                    case 6:
                        instr = new RefPicMarking.Instruction(RefPicMarking.InstrType.MARK_LONG, CAVLCReader.readUE(in, "SH: long_term_frame_idx"), 0);
                        break;
                }
                if (instr != null) {
                    mmops.add(instr);
                }
            } while (memory_management_control_operation != 0);
            sh.refPicMarkingNonIDR = new RefPicMarking((RefPicMarking.Instruction[]) mmops.toArray(new RefPicMarking.Instruction[0]));
        }
    }

    private static void readPredWeightTable(SeqParameterSet sps, PictureParameterSet pps, SliceHeader sh, BitReader in) {
        sh.pred_weight_table = new PredictionWeightTable();
        int[] numRefsMinus1 = sh.num_ref_idx_active_override_flag ? sh.num_ref_idx_active_minus1 : pps.num_ref_idx_active_minus1;
        int[] nr = {numRefsMinus1[0] + 1, numRefsMinus1[1] + 1};
        sh.pred_weight_table.luma_log2_weight_denom = CAVLCReader.readUE(in, "SH: luma_log2_weight_denom");
        if (sps.chroma_format_idc != ColorSpace.MONO) {
            sh.pred_weight_table.chroma_log2_weight_denom = CAVLCReader.readUE(in, "SH: chroma_log2_weight_denom");
        }
        int defaultLW = 1 << sh.pred_weight_table.luma_log2_weight_denom;
        int defaultCW = 1 << sh.pred_weight_table.chroma_log2_weight_denom;
        for (int list = 0; list < 2; list++) {
            sh.pred_weight_table.luma_weight[list] = new int[nr[list]];
            sh.pred_weight_table.luma_offset[list] = new int[nr[list]];
            sh.pred_weight_table.chroma_weight[list] = (int[][]) Array.newInstance(Integer.TYPE, 2, nr[list]);
            sh.pred_weight_table.chroma_offset[list] = (int[][]) Array.newInstance(Integer.TYPE, 2, nr[list]);
            for (int i = 0; i < nr[list]; i++) {
                sh.pred_weight_table.luma_weight[list][i] = defaultLW;
                sh.pred_weight_table.luma_offset[list][i] = 0;
                sh.pred_weight_table.chroma_weight[list][0][i] = defaultCW;
                sh.pred_weight_table.chroma_offset[list][0][i] = 0;
                sh.pred_weight_table.chroma_weight[list][1][i] = defaultCW;
                sh.pred_weight_table.chroma_offset[list][1][i] = 0;
            }
        }
        readWeightOffset(sps, pps, sh, in, nr, 0);
        if (sh.slice_type == SliceType.B) {
            readWeightOffset(sps, pps, sh, in, nr, 1);
        }
    }

    private static void readWeightOffset(SeqParameterSet sps, PictureParameterSet pps, SliceHeader sh, BitReader in, int[] numRefs, int list) {
        for (int i = 0; i < numRefs[list]; i++) {
            boolean luma_weight_l0_flag = CAVLCReader.readBool(in, "SH: luma_weight_l0_flag");
            if (luma_weight_l0_flag) {
                sh.pred_weight_table.luma_weight[list][i] = CAVLCReader.readSE(in, "SH: weight");
                sh.pred_weight_table.luma_offset[list][i] = CAVLCReader.readSE(in, "SH: offset");
            }
            if (sps.chroma_format_idc != ColorSpace.MONO) {
                boolean chroma_weight_l0_flag = CAVLCReader.readBool(in, "SH: chroma_weight_l0_flag");
                if (chroma_weight_l0_flag) {
                    sh.pred_weight_table.chroma_weight[list][0][i] = CAVLCReader.readSE(in, "SH: weight");
                    sh.pred_weight_table.chroma_offset[list][0][i] = CAVLCReader.readSE(in, "SH: offset");
                    sh.pred_weight_table.chroma_weight[list][1][i] = CAVLCReader.readSE(in, "SH: weight");
                    sh.pred_weight_table.chroma_offset[list][1][i] = CAVLCReader.readSE(in, "SH: offset");
                }
            }
        }
    }

    private static void readRefPicListReordering(SliceHeader sh, BitReader in) {
        sh.refPicReordering = new int[2][];
        if (sh.slice_type.isInter()) {
            boolean ref_pic_list_reordering_flag_l0 = CAVLCReader.readBool(in, "SH: ref_pic_list_reordering_flag_l0");
            if (ref_pic_list_reordering_flag_l0) {
                sh.refPicReordering[0] = readReorderingEntries(in);
            }
        }
        if (sh.slice_type == SliceType.B) {
            boolean ref_pic_list_reordering_flag_l1 = CAVLCReader.readBool(in, "SH: ref_pic_list_reordering_flag_l1");
            if (ref_pic_list_reordering_flag_l1) {
                sh.refPicReordering[1] = readReorderingEntries(in);
            }
        }
    }

    private static int[][] readReorderingEntries(BitReader in) {
        IntArrayList ops = new IntArrayList();
        IntArrayList args = new IntArrayList();
        while (true) {
            int idc = CAVLCReader.readUE(in, "SH: reordering_of_pic_nums_idc");
            if (idc != 3) {
                ops.add(idc);
                args.add(CAVLCReader.readUE(in, "SH: abs_diff_pic_num_minus1"));
            } else {
                return new int[][]{ops.toArray(), args.toArray()};
            }
        }
    }
}
