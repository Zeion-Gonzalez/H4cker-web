package org.jcodec.codecs.h264.decode;

import org.jcodec.codecs.h264.H264Const;
import org.jcodec.codecs.h264.io.model.Frame;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.PredictionWeightTable;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.io.model.SliceType;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class Prediction {

    /* renamed from: sh */
    private SliceHeader f1453sh;

    public Prediction(SliceHeader sh) {
        this.f1453sh = sh;
    }

    public void mergePrediction(int refIdxL0, int refIdxL1, H264Const.PartPred predType, int comp, int[] pred0, int[] pred1, int off, int stride, int blkW, int blkH, int[] out, Frame[][] refs, Frame thisFrame) {
        int w0;
        int w1;
        int o0;
        int o1;
        PictureParameterSet pps = this.f1453sh.pps;
        if (this.f1453sh.slice_type == SliceType.P) {
            if (pps.weighted_pred_flag && this.f1453sh.pred_weight_table != null) {
                PredictionWeightTable w = this.f1453sh.pred_weight_table;
                weight(pred0, stride, off, blkW, blkH, comp == 0 ? w.luma_log2_weight_denom : w.chroma_log2_weight_denom, comp == 0 ? w.luma_weight[0][refIdxL0] : w.chroma_weight[0][comp - 1][refIdxL0], comp == 0 ? w.luma_offset[0][refIdxL0] : w.chroma_offset[0][comp - 1][refIdxL0], out);
                return;
            } else {
                copyPrediction(pred0, stride, off, blkW, blkH, out);
                return;
            }
        }
        if (!pps.weighted_pred_flag || this.f1453sh.pps.weighted_bipred_idc == 0 || (this.f1453sh.pps.weighted_bipred_idc == 2 && predType != H264Const.PartPred.Bi)) {
            mergeAvg(pred0, pred1, stride, predType, off, blkW, blkH, out);
            return;
        }
        if (this.f1453sh.pps.weighted_bipred_idc == 1) {
            PredictionWeightTable w2 = this.f1453sh.pred_weight_table;
            if (refIdxL0 == -1) {
                w0 = 0;
            } else {
                w0 = comp == 0 ? w2.luma_weight[0][refIdxL0] : w2.chroma_weight[0][comp - 1][refIdxL0];
            }
            if (refIdxL1 == -1) {
                w1 = 0;
            } else {
                w1 = comp == 0 ? w2.luma_weight[1][refIdxL1] : w2.chroma_weight[1][comp - 1][refIdxL1];
            }
            if (refIdxL0 == -1) {
                o0 = 0;
            } else {
                o0 = comp == 0 ? w2.luma_offset[0][refIdxL0] : w2.chroma_offset[0][comp - 1][refIdxL0];
            }
            if (refIdxL1 == -1) {
                o1 = 0;
            } else {
                o1 = comp == 0 ? w2.luma_offset[1][refIdxL1] : w2.chroma_offset[1][comp - 1][refIdxL1];
            }
            mergeWeight(pred0, pred1, stride, predType, off, blkW, blkH, comp == 0 ? w2.luma_log2_weight_denom : w2.chroma_log2_weight_denom, w0, w1, o0, o1, out);
            return;
        }
        int tb = MathUtil.clip(thisFrame.getPOC() - refs[0][refIdxL0].getPOC(), -128, 127);
        int td = MathUtil.clip(refs[1][refIdxL1].getPOC() - refs[0][refIdxL0].getPOC(), -128, 127);
        int w02 = 32;
        int w12 = 32;
        if (td != 0 && refs[0][refIdxL0].isShortTerm() && refs[1][refIdxL1].isShortTerm()) {
            int tx = (Math.abs(td / 2) + 16384) / td;
            int dsf = MathUtil.clip(((tb * tx) + 32) >> 6, -1024, 1023) >> 2;
            if (dsf >= -64 && dsf <= 128) {
                w12 = dsf;
                w02 = 64 - dsf;
            }
        }
        mergeWeight(pred0, pred1, stride, predType, off, blkW, blkH, 5, w02, w12, 0, 0, out);
    }

    private void mergeAvg(int[] blk0, int[] blk1, int stride, H264Const.PartPred p0, int off, int blkW, int blkH, int[] o) {
        if (p0 == H264Const.PartPred.Bi) {
            mergePrediction(blk0, blk1, stride, p0, off, blkW, blkH, o);
        } else if (p0 == H264Const.PartPred.L0) {
            copyPrediction(blk0, stride, off, blkW, blkH, o);
        } else if (p0 == H264Const.PartPred.L1) {
            copyPrediction(blk1, stride, off, blkW, blkH, o);
        }
    }

    private void mergeWeight(int[] blk0, int[] blk1, int stride, H264Const.PartPred partPred, int off, int blkW, int blkH, int logWD, int w0, int w1, int o0, int o1, int[] out) {
        if (partPred == H264Const.PartPred.L0) {
            weight(blk0, stride, off, blkW, blkH, logWD, w0, o0, out);
        } else if (partPred == H264Const.PartPred.L1) {
            weight(blk1, stride, off, blkW, blkH, logWD, w1, o1, out);
        } else if (partPred == H264Const.PartPred.Bi) {
            weightPrediction(blk0, blk1, stride, off, blkW, blkH, logWD, w0, w1, o0, o1, out);
        }
    }

    private void copyPrediction(int[] in, int stride, int off, int blkW, int blkH, int[] o) {
        int i = 0;
        while (i < blkH) {
            int j = 0;
            while (j < blkW) {
                o[off] = in[off];
                j++;
                off++;
            }
            i++;
            off += stride - blkW;
        }
    }

    private void mergePrediction(int[] blk0, int[] blk1, int stride, H264Const.PartPred p0, int off, int blkW, int blkH, int[] o) {
        int i = 0;
        while (i < blkH) {
            int j = 0;
            while (j < blkW) {
                o[off] = ((blk0[off] + blk1[off]) + 1) >> 1;
                j++;
                off++;
            }
            i++;
            off += stride - blkW;
        }
    }

    private void weightPrediction(int[] blk0, int[] blk1, int stride, int off, int blkW, int blkH, int logWD, int w0, int w1, int o0, int o1, int[] out) {
        int dvadva = 1 << logWD;
        int sum = ((o0 + o1) + 1) >> 1;
        int logWDCP1 = logWD + 1;
        int i = 0;
        while (i < blkH) {
            int j = 0;
            while (j < blkW) {
                out[off] = MathUtil.clip(((((blk0[off] * w0) + (blk1[off] * w1)) + dvadva) >> logWDCP1) + sum, 0, 255);
                j++;
                off++;
            }
            i++;
            off += stride - blkW;
        }
    }

    private void weight(int[] blk0, int stride, int off, int blkW, int blkH, int logWD, int w, int o, int[] out) {
        int dva = 1 << (logWD - 1);
        if (logWD >= 1) {
            int i = 0;
            while (i < blkH) {
                int j = 0;
                while (j < blkW) {
                    out[off] = MathUtil.clip((((blk0[off] * w) + dva) >> logWD) + o, 0, 255);
                    j++;
                    off++;
                }
                i++;
                off += stride - blkW;
            }
            return;
        }
        int i2 = 0;
        while (i2 < blkH) {
            int j2 = 0;
            while (j2 < blkW) {
                out[off] = MathUtil.clip((blk0[off] * w) + o, 0, 255);
                j2++;
                off++;
            }
            i2++;
            off += stride - blkW;
        }
    }
}
