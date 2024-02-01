package org.jcodec.codecs.h264;

import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.RefPicMarking;
import org.jcodec.codecs.h264.io.model.SliceHeader;

/* loaded from: classes.dex */
public class POCManager {
    private int prevPOCLsb;
    private int prevPOCMsb;

    public int calcPOC(SliceHeader firstSliceHeader, NALUnit firstNu) {
        switch (firstSliceHeader.sps.pic_order_cnt_type) {
            case 0:
                return calcPOC0(firstSliceHeader, firstNu);
            case 1:
                return calcPOC1(firstSliceHeader, firstNu);
            case 2:
                return calcPOC2(firstSliceHeader, firstNu);
            default:
                throw new RuntimeException("POC no!!!");
        }
    }

    private int calcPOC2(SliceHeader firstSliceHeader, NALUnit firstNu) {
        return firstSliceHeader.frame_num << 1;
    }

    private int calcPOC1(SliceHeader firstSliceHeader, NALUnit firstNu) {
        return firstSliceHeader.frame_num << 1;
    }

    private int calcPOC0(SliceHeader firstSliceHeader, NALUnit firstNu) {
        int POCMsb;
        if (firstNu.type == NALUnitType.IDR_SLICE) {
            this.prevPOCLsb = 0;
            this.prevPOCMsb = 0;
        }
        int maxPOCLsbDiv2 = 1 << (firstSliceHeader.sps.log2_max_pic_order_cnt_lsb_minus4 + 3);
        int maxPOCLsb = maxPOCLsbDiv2 << 1;
        int POCLsb = firstSliceHeader.pic_order_cnt_lsb;
        if (POCLsb < this.prevPOCLsb && this.prevPOCLsb - POCLsb >= maxPOCLsbDiv2) {
            POCMsb = this.prevPOCMsb + maxPOCLsb;
        } else if (POCLsb > this.prevPOCLsb && POCLsb - this.prevPOCLsb > maxPOCLsbDiv2) {
            POCMsb = this.prevPOCMsb - maxPOCLsb;
        } else {
            POCMsb = this.prevPOCMsb;
        }
        int POC = POCMsb + POCLsb;
        if (firstNu.nal_ref_idc > 0) {
            if (hasMMCO5(firstSliceHeader, firstNu)) {
                this.prevPOCMsb = 0;
                this.prevPOCLsb = POC;
            } else {
                this.prevPOCMsb = POCMsb;
                this.prevPOCLsb = POCLsb;
            }
        }
        return POC;
    }

    private boolean hasMMCO5(SliceHeader firstSliceHeader, NALUnit firstNu) {
        if (firstNu.type != NALUnitType.IDR_SLICE && firstSliceHeader.refPicMarkingNonIDR != null) {
            RefPicMarking.Instruction[] instructions = firstSliceHeader.refPicMarkingNonIDR.getInstructions();
            for (RefPicMarking.Instruction instruction : instructions) {
                if (instruction.getType() == RefPicMarking.InstrType.CLEAR) {
                    return true;
                }
            }
        }
        return false;
    }
}
