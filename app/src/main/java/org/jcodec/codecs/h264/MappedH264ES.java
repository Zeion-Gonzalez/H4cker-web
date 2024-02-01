package org.jcodec.codecs.h264;

import java.nio.ByteBuffer;
import org.jcodec.codecs.h264.decode.SliceHeaderReader;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.RefPicMarking;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.IntObjectMap;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.model.Packet;

/* loaded from: classes.dex */
public class MappedH264ES implements DemuxerTrack {

    /* renamed from: bb */
    private ByteBuffer f1452bb;
    private int prevFrameNum;
    private int prevFrameNumOffset;
    private int prevPicOrderCntLsb;
    private int prevPicOrderCntMsb;
    private IntObjectMap<PictureParameterSet> pps = new IntObjectMap<>();
    private IntObjectMap<SeqParameterSet> sps = new IntObjectMap<>();
    private SliceHeaderReader shr = new SliceHeaderReader();
    private int frameNo = 0;

    public MappedH264ES(ByteBuffer bb) {
        this.f1452bb = bb;
    }

    @Override // org.jcodec.common.DemuxerTrack
    public Packet nextFrame() {
        ByteBuffer result = this.f1452bb.duplicate();
        NALUnit prevNu = null;
        SliceHeader prevSh = null;
        while (true) {
            this.f1452bb.mark();
            ByteBuffer buf = H264Utils.nextNALUnit(this.f1452bb);
            if (buf == null) {
                break;
            }
            NALUnit nu = NALUnit.read(buf);
            if (nu.type == NALUnitType.IDR_SLICE || nu.type == NALUnitType.NON_IDR_SLICE) {
                SliceHeader sh = readSliceHeader(buf, nu);
                if (prevNu != null && prevSh != null && !sameFrame(prevNu, nu, prevSh, sh)) {
                    this.f1452bb.reset();
                    break;
                }
                prevSh = sh;
                prevNu = nu;
            } else if (nu.type == NALUnitType.PPS) {
                PictureParameterSet read = PictureParameterSet.read(buf);
                this.pps.put(read.pic_parameter_set_id, read);
            } else if (nu.type == NALUnitType.SPS) {
                SeqParameterSet read2 = SeqParameterSet.read(buf);
                this.sps.put(read2.seq_parameter_set_id, read2);
            }
        }
        result.limit(this.f1452bb.position());
        if (prevSh == null) {
            return null;
        }
        return detectPoc(result, prevNu, prevSh);
    }

    private SliceHeader readSliceHeader(ByteBuffer buf, NALUnit nu) {
        BitReader br = new BitReader(buf);
        SliceHeader sh = this.shr.readPart1(br);
        PictureParameterSet pp = this.pps.get(sh.pic_parameter_set_id);
        this.shr.readPart2(sh, nu, this.sps.get(pp.seq_parameter_set_id), pp, br);
        return sh;
    }

    private boolean sameFrame(NALUnit nu1, NALUnit nu2, SliceHeader sh1, SliceHeader sh2) {
        if (sh1.pic_parameter_set_id != sh2.pic_parameter_set_id || sh1.frame_num != sh2.frame_num) {
            return false;
        }
        SeqParameterSet sps = sh1.sps;
        if (sps.pic_order_cnt_type == 0 && sh1.pic_order_cnt_lsb != sh2.pic_order_cnt_lsb) {
            return false;
        }
        if (sps.pic_order_cnt_type == 1 && (sh1.delta_pic_order_cnt[0] != sh2.delta_pic_order_cnt[0] || sh1.delta_pic_order_cnt[1] != sh2.delta_pic_order_cnt[1])) {
            return false;
        }
        if ((nu1.nal_ref_idc == 0 || nu2.nal_ref_idc == 0) && nu1.nal_ref_idc != nu2.nal_ref_idc) {
            return false;
        }
        return (nu1.type == NALUnitType.IDR_SLICE) == (nu2.type == NALUnitType.IDR_SLICE) && sh1.idr_pic_id == sh2.idr_pic_id;
    }

    private Packet detectPoc(ByteBuffer result, NALUnit nu, SliceHeader sh) {
        int maxFrameNum = 1 << (sh.sps.log2_max_frame_num_minus4 + 4);
        if (detectGap(sh, maxFrameNum)) {
            issueNonExistingPic(sh, maxFrameNum);
        }
        int absFrameNum = updateFrameNumber(sh.frame_num, maxFrameNum, detectMMCO5(sh.refPicMarkingNonIDR));
        int poc = 0;
        if (nu.type == NALUnitType.NON_IDR_SLICE) {
            poc = calcPoc(absFrameNum, nu, sh);
        }
        long j = absFrameNum;
        int i = this.frameNo;
        this.frameNo = i + 1;
        return new Packet(result, j, 1L, 1L, i, nu.type == NALUnitType.IDR_SLICE, null, poc);
    }

    private int updateFrameNumber(int frameNo, int maxFrameNum, boolean mmco5) {
        int frameNumOffset;
        if (this.prevFrameNum > frameNo) {
            frameNumOffset = this.prevFrameNumOffset + maxFrameNum;
        } else {
            frameNumOffset = this.prevFrameNumOffset;
        }
        int absFrameNum = frameNumOffset + frameNo;
        if (mmco5) {
            frameNo = 0;
        }
        this.prevFrameNum = frameNo;
        this.prevFrameNumOffset = frameNumOffset;
        return absFrameNum;
    }

    private void issueNonExistingPic(SliceHeader sh, int maxFrameNum) {
        int nextFrameNum = (this.prevFrameNum + 1) % maxFrameNum;
        this.prevFrameNum = nextFrameNum;
    }

    private boolean detectGap(SliceHeader sh, int maxFrameNum) {
        return (sh.frame_num == this.prevFrameNum || sh.frame_num == (this.prevFrameNum + 1) % maxFrameNum) ? false : true;
    }

    private int calcPoc(int absFrameNum, NALUnit nu, SliceHeader sh) {
        if (sh.sps.pic_order_cnt_type == 0) {
            return calcPOC0(nu, sh);
        }
        if (sh.sps.pic_order_cnt_type == 1) {
            return calcPOC1(absFrameNum, nu, sh);
        }
        return calcPOC2(absFrameNum, nu, sh);
    }

    private int calcPOC2(int absFrameNum, NALUnit nu, SliceHeader sh) {
        return nu.nal_ref_idc == 0 ? (absFrameNum * 2) - 1 : absFrameNum * 2;
    }

    private int calcPOC1(int absFrameNum, NALUnit nu, SliceHeader sh) {
        int expectedPicOrderCnt;
        if (sh.sps.num_ref_frames_in_pic_order_cnt_cycle == 0) {
            absFrameNum = 0;
        }
        if (nu.nal_ref_idc == 0 && absFrameNum > 0) {
            absFrameNum--;
        }
        int expectedDeltaPerPicOrderCntCycle = 0;
        for (int i = 0; i < sh.sps.num_ref_frames_in_pic_order_cnt_cycle; i++) {
            expectedDeltaPerPicOrderCntCycle += sh.sps.offsetForRefFrame[i];
        }
        if (absFrameNum > 0) {
            int picOrderCntCycleCnt = (absFrameNum - 1) / sh.sps.num_ref_frames_in_pic_order_cnt_cycle;
            int frameNumInPicOrderCntCycle = (absFrameNum - 1) % sh.sps.num_ref_frames_in_pic_order_cnt_cycle;
            expectedPicOrderCnt = picOrderCntCycleCnt * expectedDeltaPerPicOrderCntCycle;
            for (int i2 = 0; i2 <= frameNumInPicOrderCntCycle; i2++) {
                expectedPicOrderCnt += sh.sps.offsetForRefFrame[i2];
            }
        } else {
            expectedPicOrderCnt = 0;
        }
        if (nu.nal_ref_idc == 0) {
            expectedPicOrderCnt += sh.sps.offset_for_non_ref_pic;
        }
        return sh.delta_pic_order_cnt[0] + expectedPicOrderCnt;
    }

    private int calcPOC0(NALUnit nu, SliceHeader sh) {
        int picOrderCntMsb;
        int pocCntLsb = sh.pic_order_cnt_lsb;
        int maxPicOrderCntLsb = 1 << (sh.sps.log2_max_pic_order_cnt_lsb_minus4 + 4);
        if (pocCntLsb < this.prevPicOrderCntLsb && this.prevPicOrderCntLsb - pocCntLsb >= maxPicOrderCntLsb / 2) {
            picOrderCntMsb = this.prevPicOrderCntMsb + maxPicOrderCntLsb;
        } else if (pocCntLsb > this.prevPicOrderCntLsb && pocCntLsb - this.prevPicOrderCntLsb > maxPicOrderCntLsb / 2) {
            picOrderCntMsb = this.prevPicOrderCntMsb - maxPicOrderCntLsb;
        } else {
            picOrderCntMsb = this.prevPicOrderCntMsb;
        }
        if (nu.nal_ref_idc != 0) {
            this.prevPicOrderCntMsb = picOrderCntMsb;
            this.prevPicOrderCntLsb = pocCntLsb;
        }
        return picOrderCntMsb + pocCntLsb;
    }

    private boolean detectMMCO5(RefPicMarking refPicMarkingNonIDR) {
        if (refPicMarkingNonIDR == null) {
            return false;
        }
        RefPicMarking.Instruction[] arr$ = refPicMarkingNonIDR.getInstructions();
        for (RefPicMarking.Instruction instr : arr$) {
            if (instr.getType() == RefPicMarking.InstrType.CLEAR) {
                return true;
            }
        }
        return false;
    }

    public SeqParameterSet[] getSps() {
        return this.sps.values(new SeqParameterSet[0]);
    }

    public PictureParameterSet[] getPps() {
        return this.pps.values(new PictureParameterSet[0]);
    }

    @Override // org.jcodec.common.DemuxerTrack
    public DemuxerTrackMeta getMeta() {
        throw new UnsupportedOperationException();
    }
}
