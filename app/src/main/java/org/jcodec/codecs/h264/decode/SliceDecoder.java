package org.jcodec.codecs.h264.decode;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import org.jcodec.codecs.common.biari.MDecoder;
import org.jcodec.codecs.h264.H264Const;
import org.jcodec.codecs.h264.decode.aso.MapManager;
import org.jcodec.codecs.h264.decode.aso.Mapper;
import org.jcodec.codecs.h264.io.CABAC;
import org.jcodec.codecs.h264.io.CAVLC;
import org.jcodec.codecs.h264.io.model.Frame;
import org.jcodec.codecs.h264.io.model.MBType;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.io.model.SliceType;
import org.jcodec.common.IntObjectMap;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class SliceDecoder {
    private static final int[] NULL_VECTOR = {0, 0, -1};
    private PictureParameterSet activePps;
    private SeqParameterSet activeSps;
    private CABAC cabac;
    private CAVLC[] cavlc;
    private ColorSpace chromaFormat;
    private int[] chromaQpOffset;
    private boolean debug;
    private int[] i4x4PredLeft;
    private int[] i4x4PredTop;
    private IntObjectMap<Frame> lRefs;
    private int leftCBPChroma;
    private int leftCBPLuma;
    private MBType leftMBType;
    private int[][] leftRow;
    private MDecoder mDecoder;
    private Mapper mapper;
    private int[][] mbQps;
    private MBType[] mbTypes;
    private int[][][] mvLeft;
    private int[][][] mvTop;
    private int[][] mvTopLeft;
    private int[][][][] mvs;
    private int[][] nCoeff;
    private int[] numRef;
    private H264Const.PartPred[] predModeLeft;
    private H264Const.PartPred[] predModeTop;
    private Prediction prediction;

    /* renamed from: qp */
    private int f1454qp;
    private Frame[][][] refsUsed;
    private Frame[] sRefs;

    /* renamed from: sh */
    private SliceHeader f1455sh;
    private SliceHeader[] shs;
    private boolean tf8x8Left;
    private boolean[] tf8x8Top;
    private Frame thisFrame;
    private int[] topCBPChroma;
    private int[] topCBPLuma;
    private int[][] topLeft;
    private int[][] topLine;
    private MBType[] topMBType;
    private boolean[] tr8x8Used;
    private boolean transform8x8;

    public SliceDecoder(SeqParameterSet activeSps, PictureParameterSet activePps, int[][] nCoeff, int[][][][] mvs, MBType[] mbTypes, int[][] mbQps, SliceHeader[] shs, boolean[] tr8x8Used, Frame[][][] refsUsed, Frame result, Frame[] sRefs, IntObjectMap<Frame> lRefs) {
        this.activeSps = activeSps;
        this.activePps = activePps;
        this.nCoeff = nCoeff;
        this.mvs = mvs;
        this.mbTypes = mbTypes;
        this.mbQps = mbQps;
        this.shs = shs;
        this.thisFrame = result;
        this.sRefs = sRefs;
        this.lRefs = lRefs;
        this.tr8x8Used = tr8x8Used;
        this.refsUsed = refsUsed;
    }

    public void decode(ByteBuffer segment, NALUnit nalUnit) {
        BitReader in = new BitReader(segment);
        SliceHeaderReader shr = new SliceHeaderReader();
        this.f1455sh = shr.readPart1(in);
        this.f1455sh.sps = this.activeSps;
        this.f1455sh.pps = this.activePps;
        this.cavlc = new CAVLC[]{new CAVLC(this.f1455sh.sps, this.f1455sh.pps, 2, 2), new CAVLC(this.f1455sh.sps, this.f1455sh.pps, 1, 1), new CAVLC(this.f1455sh.sps, this.f1455sh.pps, 1, 1)};
        int mbWidth = this.f1455sh.sps.pic_width_in_mbs_minus1 + 1;
        this.cabac = new CABAC(mbWidth);
        int[] iArr = new int[2];
        iArr[0] = this.f1455sh.pps.chroma_qp_index_offset;
        iArr[1] = this.f1455sh.pps.extended != null ? this.f1455sh.pps.extended.second_chroma_qp_index_offset : this.f1455sh.pps.chroma_qp_index_offset;
        this.chromaQpOffset = iArr;
        this.chromaFormat = this.f1455sh.sps.chroma_format_idc;
        this.transform8x8 = this.f1455sh.pps.extended == null ? false : this.f1455sh.pps.extended.transform_8x8_mode_flag;
        this.i4x4PredLeft = new int[4];
        this.i4x4PredTop = new int[mbWidth << 2];
        this.topMBType = new MBType[mbWidth];
        this.topCBPLuma = new int[mbWidth];
        this.topCBPChroma = new int[mbWidth];
        this.mvTop = (int[][][]) Array.newInstance(Integer.TYPE, 2, (mbWidth << 2) + 1, 3);
        this.mvLeft = (int[][][]) Array.newInstance(Integer.TYPE, 2, 4, 3);
        this.mvTopLeft = (int[][]) Array.newInstance(Integer.TYPE, 2, 3);
        this.leftRow = (int[][]) Array.newInstance(Integer.TYPE, 3, 16);
        this.topLeft = (int[][]) Array.newInstance(Integer.TYPE, 3, 4);
        this.topLine = (int[][]) Array.newInstance(Integer.TYPE, 3, mbWidth << 4);
        this.predModeLeft = new H264Const.PartPred[2];
        this.predModeTop = new H264Const.PartPred[mbWidth << 1];
        this.tf8x8Top = new boolean[mbWidth];
        shr.readPart2(this.f1455sh, nalUnit, this.f1455sh.sps, this.f1455sh.pps, in);
        this.prediction = new Prediction(this.f1455sh);
        this.f1454qp = this.f1455sh.pps.pic_init_qp_minus26 + 26 + this.f1455sh.slice_qp_delta;
        if (this.activePps.entropy_coding_mode_flag) {
            in.terminate();
            int[][] cm = (int[][]) Array.newInstance(Integer.TYPE, 2, 1024);
            this.cabac.initModels(cm, this.f1455sh.slice_type, this.f1455sh.cabac_init_idc, this.f1454qp);
            this.mDecoder = new MDecoder(segment, cm);
        }
        if (this.f1455sh.num_ref_idx_active_override_flag) {
            this.numRef = new int[]{this.f1455sh.num_ref_idx_active_minus1[0] + 1, this.f1455sh.num_ref_idx_active_minus1[1] + 1};
        } else {
            this.numRef = new int[]{this.activePps.num_ref_idx_active_minus1[0] + 1, this.activePps.num_ref_idx_active_minus1[1] + 1};
        }
        debugPrint("============" + this.thisFrame.getPOC() + "============= " + this.f1455sh.slice_type.name());
        Frame[][] refList = null;
        if (this.f1455sh.slice_type == SliceType.P) {
            refList = new Frame[][]{buildRefListP(), null};
        } else if (this.f1455sh.slice_type == SliceType.B) {
            refList = buildRefListB();
        }
        debugPrint("------");
        if (refList != null) {
            for (int l = 0; l < 2; l++) {
                if (refList[l] != null) {
                    for (int i = 0; i < refList[l].length; i++) {
                        if (refList[l][i] != null) {
                            debugPrint("REF[" + l + "][" + i + "]: " + refList[l][i].getPOC());
                        }
                    }
                }
            }
        }
        this.mapper = new MapManager(this.f1455sh.sps, this.f1455sh.pps).getMapper(this.f1455sh);
        Picture mb = Picture.create(16, 16, this.f1455sh.sps.chroma_format_idc);
        boolean mbaffFrameFlag = this.f1455sh.sps.mb_adaptive_frame_field_flag && !this.f1455sh.field_pic_flag;
        boolean prevMbSkipped = false;
        MBType prevMBType = null;
        int i2 = 0;
        while (true) {
            if (this.f1455sh.slice_type.isInter() && !this.activePps.entropy_coding_mode_flag) {
                int mbSkipRun = CAVLCReader.readUE(in, "mb_skip_run");
                int j = 0;
                while (j < mbSkipRun) {
                    int mbAddr = this.mapper.getAddress(i2);
                    debugPrint("---------------------- MB (" + (mbAddr % mbWidth) + "," + (mbAddr / mbWidth) + ") ---------------------");
                    decodeSkip(refList, i2, mb, this.f1455sh.slice_type);
                    this.shs[mbAddr] = this.f1455sh;
                    this.refsUsed[mbAddr] = refList;
                    put(this.thisFrame, mb, this.mapper.getMbX(i2), this.mapper.getMbY(i2));
                    wipe(mb);
                    j++;
                    i2++;
                }
                prevMbSkipped = mbSkipRun > 0;
                prevMBType = null;
                if (!CAVLCReader.moreRBSPData(in)) {
                    return;
                }
            }
            int mbAddr2 = this.mapper.getAddress(i2);
            this.shs[mbAddr2] = this.f1455sh;
            this.refsUsed[mbAddr2] = refList;
            int mbX = mbAddr2 % mbWidth;
            int mbY = mbAddr2 / mbWidth;
            debugPrint("---------------------- MB (" + mbX + "," + mbY + ") ---------------------");
            if (this.f1455sh.slice_type.isIntra() || !this.activePps.entropy_coding_mode_flag || !this.cabac.readMBSkipFlag(this.mDecoder, this.f1455sh.slice_type, this.mapper.leftAvailable(i2), this.mapper.topAvailable(i2), mbX)) {
                boolean mb_field_decoding_flag = false;
                if (mbaffFrameFlag && (i2 % 2 == 0 || (i2 % 2 == 1 && prevMbSkipped))) {
                    mb_field_decoding_flag = CAVLCReader.readBool(in, "mb_field_decoding_flag");
                }
                prevMBType = decode(this.f1455sh.slice_type, i2, in, mb_field_decoding_flag, prevMBType, mb, refList);
            } else {
                decodeSkip(refList, i2, mb, this.f1455sh.slice_type);
                prevMBType = null;
            }
            put(this.thisFrame, mb, mbX, mbY);
            if (!this.activePps.entropy_coding_mode_flag || this.mDecoder.decodeFinalBin() != 1) {
                if (this.activePps.entropy_coding_mode_flag || CAVLCReader.moreRBSPData(in)) {
                    wipe(mb);
                    i2++;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private Frame[] buildRefListP() {
        int frame_num = this.f1455sh.frame_num;
        int maxFrames = 1 << (this.f1455sh.sps.log2_max_frame_num_minus4 + 4);
        Frame[] result = new Frame[this.numRef[0]];
        int refs = 0;
        int i = frame_num - 1;
        while (i >= frame_num - maxFrames && refs < this.numRef[0]) {
            int fn = i < 0 ? i + maxFrames : i;
            if (this.sRefs[fn] != null) {
                result[refs] = this.sRefs[fn] == H264Const.NO_PIC ? null : this.sRefs[fn];
                refs++;
            }
            i--;
        }
        int[] keys = this.lRefs.keys();
        Arrays.sort(keys);
        int i2 = 0;
        while (i2 < keys.length && refs < this.numRef[0]) {
            result[refs] = this.lRefs.get(keys[i2]);
            i2++;
            refs++;
        }
        reorder(result, 0);
        return result;
    }

    private Frame[][] buildRefListB() {
        Frame[] l0 = buildList(Frame.POCDesc, Frame.POCAsc);
        Frame[] l1 = buildList(Frame.POCAsc, Frame.POCDesc);
        if (Arrays.equals(l0, l1) && count(l1) > 1) {
            Frame frame = l1[1];
            l1[1] = l1[0];
            l1[0] = frame;
        }
        Frame[][] result = {(Frame[]) Arrays.copyOf(l0, this.numRef[0]), (Frame[]) Arrays.copyOf(l1, this.numRef[1])};
        reorder(result[0], 0);
        reorder(result[1], 1);
        return result;
    }

    private Frame[] buildList(Comparator<Frame> cmpFwd, Comparator<Frame> cmpInv) {
        Frame[] refs = new Frame[this.sRefs.length + this.lRefs.size()];
        Frame[] fwd = copySort(cmpFwd, this.thisFrame);
        Frame[] inv = copySort(cmpInv, this.thisFrame);
        int nFwd = count(fwd);
        int nInv = count(inv);
        int ref = 0;
        int i = 0;
        while (i < nFwd) {
            refs[ref] = fwd[i];
            i++;
            ref++;
        }
        int i2 = 0;
        while (i2 < nInv) {
            refs[ref] = inv[i2];
            i2++;
            ref++;
        }
        int[] keys = this.lRefs.keys();
        Arrays.sort(keys);
        int i3 = 0;
        while (i3 < keys.length) {
            refs[ref] = this.lRefs.get(keys[i3]);
            i3++;
            ref++;
        }
        return refs;
    }

    private int count(Frame[] arr) {
        for (int nn = 0; nn < arr.length; nn++) {
            if (arr[nn] == null) {
                return nn;
            }
        }
        int nn2 = arr.length;
        return nn2;
    }

    private Frame[] copySort(Comparator<Frame> fwd, Frame dummy) {
        Frame[] copyOf = (Frame[]) Arrays.copyOf(this.sRefs, this.sRefs.length);
        for (int i = 0; i < copyOf.length; i++) {
            if (fwd.compare(dummy, copyOf[i]) > 0) {
                copyOf[i] = null;
            }
        }
        Arrays.sort(copyOf, fwd);
        return copyOf;
    }

    private void reorder(Picture[] result, int list) {
        if (this.f1455sh.refPicReordering[list] != null) {
            int predict = this.f1455sh.frame_num;
            int maxFrames = 1 << (this.f1455sh.sps.log2_max_frame_num_minus4 + 4);
            for (int ind = 0; ind < this.f1455sh.refPicReordering[list][0].length; ind++) {
                switch (this.f1455sh.refPicReordering[list][0][ind]) {
                    case 0:
                        predict = MathUtil.wrap((predict - this.f1455sh.refPicReordering[list][1][ind]) - 1, maxFrames);
                        break;
                    case 1:
                        predict = MathUtil.wrap(this.f1455sh.refPicReordering[list][1][ind] + predict + 1, maxFrames);
                        break;
                    case 2:
                        throw new RuntimeException("long term");
                }
                for (int i = this.numRef[list] - 1; i > ind; i--) {
                    result[i] = result[i - 1];
                }
                result[ind] = this.sRefs[predict];
                int i2 = ind + 1;
                int j = i2;
                while (i2 < this.numRef[list] && result[i2] != null) {
                    if (result[i2] != this.sRefs[predict]) {
                        result[j] = result[i2];
                        j++;
                    }
                    i2++;
                }
            }
        }
    }

    private void wipe(Picture mb) {
        Arrays.fill(mb.getPlaneData(0), 0);
        Arrays.fill(mb.getPlaneData(1), 0);
        Arrays.fill(mb.getPlaneData(2), 0);
    }

    private void collectPredictors(Picture outMB, int mbX) {
        this.topLeft[0][0] = this.topLine[0][(mbX << 4) + 15];
        this.topLeft[0][1] = outMB.getPlaneData(0)[63];
        this.topLeft[0][2] = outMB.getPlaneData(0)[127];
        this.topLeft[0][3] = outMB.getPlaneData(0)[191];
        System.arraycopy(outMB.getPlaneData(0), 240, this.topLine[0], mbX << 4, 16);
        copyCol(outMB.getPlaneData(0), 16, 15, 16, this.leftRow[0]);
        collectChromaPredictors(outMB, mbX);
    }

    private void collectChromaPredictors(Picture outMB, int mbX) {
        this.topLeft[1][0] = this.topLine[1][(mbX << 3) + 7];
        this.topLeft[2][0] = this.topLine[2][(mbX << 3) + 7];
        System.arraycopy(outMB.getPlaneData(1), 56, this.topLine[1], mbX << 3, 8);
        System.arraycopy(outMB.getPlaneData(2), 56, this.topLine[2], mbX << 3, 8);
        copyCol(outMB.getPlaneData(1), 8, 7, 8, this.leftRow[1]);
        copyCol(outMB.getPlaneData(2), 8, 7, 8, this.leftRow[2]);
    }

    private void copyCol(int[] planeData, int n, int off, int stride, int[] out) {
        int i = 0;
        while (i < n) {
            out[i] = planeData[off];
            i++;
            off += stride;
        }
    }

    public MBType decode(SliceType sliceType, int mbAddr, BitReader reader, boolean field, MBType prevMbType, Picture mb, Frame[][] references) {
        if (sliceType == SliceType.I) {
            return decodeMBlockI(mbAddr, reader, field, prevMbType, mb);
        }
        if (sliceType == SliceType.P) {
            return decodeMBlockP(mbAddr, reader, field, prevMbType, mb, references);
        }
        return decodeMBlockB(mbAddr, reader, field, prevMbType, mb, references);
    }

    private MBType decodeMBlockI(int mbIdx, BitReader reader, boolean field, MBType prevMbType, Picture mb) {
        int mbType;
        if (!this.activePps.entropy_coding_mode_flag) {
            mbType = CAVLCReader.readUE(reader, "MB: mb_type");
        } else {
            mbType = this.cabac.readMBTypeI(this.mDecoder, this.leftMBType, this.topMBType[this.mapper.getMbX(mbIdx)], this.mapper.leftAvailable(mbIdx), this.mapper.topAvailable(mbIdx));
        }
        return decodeMBlockIInt(mbType, mbIdx, reader, field, prevMbType, mb);
    }

    private MBType decodeMBlockIInt(int mbType, int mbIdx, BitReader reader, boolean field, MBType prevMbType, Picture mb) {
        MBType mbt;
        if (mbType == 0) {
            decodeMBlockIntraNxN(reader, mbIdx, prevMbType, mb);
            mbt = MBType.I_NxN;
        } else if (mbType >= 1 && mbType <= 24) {
            decodeMBlockIntra16x16(reader, mbType - 1, mbIdx, prevMbType, mb);
            mbt = MBType.I_16x16;
        } else {
            Logger.warn("IPCM macroblock found. Not tested, may cause unpredictable behavior.");
            decodeMBlockIPCM(reader, mbIdx, mb);
            mbt = MBType.I_PCM;
        }
        int xx = this.mapper.getMbX(mbIdx) << 2;
        copyVect(this.mvTopLeft[0], this.mvTop[0][xx + 3]);
        copyVect(this.mvTopLeft[1], this.mvTop[1][xx + 3]);
        saveVect(this.mvTop[0], xx, xx + 4, 0, 0, -1);
        saveVect(this.mvLeft[0], 0, 4, 0, 0, -1);
        saveVect(this.mvTop[1], xx, xx + 4, 0, 0, -1);
        saveVect(this.mvLeft[1], 0, 4, 0, 0, -1);
        return mbt;
    }

    private MBType decodeMBlockP(int mbIdx, BitReader reader, boolean field, MBType prevMbType, Picture mb, Frame[][] references) {
        int mbType;
        if (!this.activePps.entropy_coding_mode_flag) {
            mbType = CAVLCReader.readUE(reader, "MB: mb_type");
        } else {
            mbType = this.cabac.readMBTypeP(this.mDecoder);
        }
        switch (mbType) {
            case 0:
                decodeInter16x16(reader, mb, references, mbIdx, prevMbType, H264Const.PartPred.L0, MBType.P_16x16);
                return MBType.P_16x16;
            case 1:
                decodeInter16x8(reader, mb, references, mbIdx, prevMbType, H264Const.PartPred.L0, H264Const.PartPred.L0, MBType.P_16x8);
                return MBType.P_16x8;
            case 2:
                decodeInter8x16(reader, mb, references, mbIdx, prevMbType, H264Const.PartPred.L0, H264Const.PartPred.L0, MBType.P_8x16);
                return MBType.P_8x16;
            case 3:
                decodeMBInter8x8(reader, mbType, references, mb, SliceType.P, mbIdx, field, prevMbType, false);
                return MBType.P_8x8;
            case 4:
                decodeMBInter8x8(reader, mbType, references, mb, SliceType.P, mbIdx, field, prevMbType, true);
                return MBType.P_8x8ref0;
            default:
                return decodeMBlockIInt(mbType - 5, mbIdx, reader, field, prevMbType, mb);
        }
    }

    private MBType decodeMBlockB(int mbIdx, BitReader reader, boolean field, MBType prevMbType, Picture mb, Frame[][] references) {
        int mbType;
        if (!this.activePps.entropy_coding_mode_flag) {
            mbType = CAVLCReader.readUE(reader, "MB: mb_type");
        } else {
            mbType = this.cabac.readMBTypeB(this.mDecoder, this.leftMBType, this.topMBType[this.mapper.getMbX(mbIdx)], this.mapper.leftAvailable(mbIdx), this.mapper.topAvailable(mbIdx));
        }
        if (mbType >= 23) {
            return decodeMBlockIInt(mbType - 23, mbIdx, reader, field, prevMbType, mb);
        }
        MBType curMBType = H264Const.bMbTypes[mbType];
        if (mbType == 0) {
            decodeMBBiDirect(mbIdx, reader, field, prevMbType, mb, references);
            return curMBType;
        }
        if (mbType <= 3) {
            decodeInter16x16(reader, mb, references, mbIdx, prevMbType, H264Const.bPredModes[mbType][0], curMBType);
            return curMBType;
        }
        if (mbType == 22) {
            decodeMBInter8x8(reader, mbType, references, mb, SliceType.B, mbIdx, field, prevMbType, false);
            return curMBType;
        }
        if ((mbType & 1) == 0) {
            decodeInter16x8(reader, mb, references, mbIdx, prevMbType, H264Const.bPredModes[mbType][0], H264Const.bPredModes[mbType][1], curMBType);
            return curMBType;
        }
        decodeInter8x16(reader, mb, references, mbIdx, prevMbType, H264Const.bPredModes[mbType][0], H264Const.bPredModes[mbType][1], curMBType);
        return curMBType;
    }

    public void put(Picture tgt, Picture decoded, int mbX, int mbY) {
        int[] luma = tgt.getPlaneData(0);
        int stride = tgt.getPlaneWidth(0);
        int[] cb = tgt.getPlaneData(1);
        int[] cr = tgt.getPlaneData(2);
        int strideChroma = tgt.getPlaneWidth(1);
        int dOff = 0;
        for (int i = 0; i < 16; i++) {
            System.arraycopy(decoded.getPlaneData(0), dOff, luma, (((mbY * 16) + i) * stride) + (mbX * 16), 16);
            dOff += 16;
        }
        for (int i2 = 0; i2 < 8; i2++) {
            System.arraycopy(decoded.getPlaneData(1), i2 * 8, cb, (((mbY * 8) + i2) * strideChroma) + (mbX * 8), 8);
        }
        for (int i3 = 0; i3 < 8; i3++) {
            System.arraycopy(decoded.getPlaneData(2), i3 * 8, cr, (((mbY * 8) + i3) * strideChroma) + (mbX * 8), 8);
        }
    }

    public void decodeMBlockIntra16x16(BitReader reader, int mbType, int mbIndex, MBType prevMbType, Picture mb) {
        int mbX = this.mapper.getMbX(mbIndex);
        int mbY = this.mapper.getMbY(mbIndex);
        int address = this.mapper.getAddress(mbIndex);
        int cbpChroma = (mbType / 4) % 3;
        int cbpLuma = (mbType / 12) * 15;
        boolean leftAvailable = this.mapper.leftAvailable(mbIndex);
        boolean topAvailable = this.mapper.topAvailable(mbIndex);
        int chromaPredictionMode = readChromaPredMode(reader, mbX, leftAvailable, topAvailable);
        int mbQPDelta = readMBQpDelta(reader, prevMbType);
        this.f1454qp = ((this.f1454qp + mbQPDelta) + 52) % 52;
        this.mbQps[0][address] = this.f1454qp;
        residualLumaI16x16(reader, leftAvailable, topAvailable, mbX, mbY, mb, cbpLuma);
        Intra16x16PredictionBuilder.predictWithMode(mbType % 4, mb.getPlaneData(0), leftAvailable, topAvailable, this.leftRow[0], this.topLine[0], this.topLeft[0], mbX << 4);
        decodeChroma(reader, cbpChroma, chromaPredictionMode, mbX, mbY, leftAvailable, topAvailable, mb, this.f1454qp, MBType.I_16x16);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        MBType mBType = MBType.I_16x16;
        this.leftMBType = mBType;
        mBTypeArr2[mbX] = mBType;
        mBTypeArr[address] = mBType;
        int[] iArr = this.topCBPLuma;
        this.leftCBPLuma = cbpLuma;
        iArr[mbX] = cbpLuma;
        int[] iArr2 = this.topCBPChroma;
        this.leftCBPChroma = cbpChroma;
        iArr2[mbX] = cbpChroma;
        this.tf8x8Top[mbX] = false;
        this.tf8x8Left = false;
        collectPredictors(mb, mbX);
        saveMvsIntra(mbX, mbY);
    }

    private int readMBQpDelta(BitReader reader, MBType prevMbType) {
        if (!this.activePps.entropy_coding_mode_flag) {
            int mbQPDelta = CAVLCReader.readSE(reader, "mb_qp_delta");
            return mbQPDelta;
        }
        int mbQPDelta2 = this.cabac.readMBQpDelta(this.mDecoder, prevMbType);
        return mbQPDelta2;
    }

    private int readChromaPredMode(BitReader reader, int mbX, boolean leftAvailable, boolean topAvailable) {
        if (!this.activePps.entropy_coding_mode_flag) {
            int chromaPredictionMode = CAVLCReader.readUE(reader, "MBP: intra_chroma_pred_mode");
            return chromaPredictionMode;
        }
        int chromaPredictionMode2 = this.cabac.readIntraChromaPredMode(this.mDecoder, mbX, this.leftMBType, this.topMBType[mbX], leftAvailable, topAvailable);
        return chromaPredictionMode2;
    }

    private void residualLumaI16x16(BitReader reader, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, Picture mb, int cbpLuma) {
        int[] dc = new int[16];
        if (!this.activePps.entropy_coding_mode_flag) {
            this.cavlc[0].readLumaDCBlock(reader, dc, mbX, leftAvailable, this.leftMBType, topAvailable, this.topMBType[mbX], CoeffTransformer.zigzag4x4);
        } else if (this.cabac.readCodedBlockFlagLumaDC(this.mDecoder, mbX, this.leftMBType, this.topMBType[mbX], leftAvailable, topAvailable, MBType.I_16x16) == 1) {
            this.cabac.readCoeffs(this.mDecoder, CABAC.BlockType.LUMA_16_DC, dc, 0, 16, CoeffTransformer.zigzag4x4, H264Const.identityMapping16, H264Const.identityMapping16);
        }
        CoeffTransformer.invDC4x4(dc);
        CoeffTransformer.dequantizeDC4x4(dc, this.f1454qp);
        CoeffTransformer.reorderDC4x4(dc);
        for (int i = 0; i < 16; i++) {
            int[] ac = new int[16];
            int blkOffLeft = H264Const.MB_BLK_OFF_LEFT[i];
            int blkOffTop = H264Const.MB_BLK_OFF_TOP[i];
            int blkX = (mbX << 2) + blkOffLeft;
            int blkY = (mbY << 2) + blkOffTop;
            if (((1 << (i >> 2)) & cbpLuma) != 0) {
                if (!this.activePps.entropy_coding_mode_flag) {
                    this.nCoeff[blkY][blkX] = this.cavlc[0].readACBlock(reader, ac, blkX, blkOffTop, blkOffLeft != 0 || leftAvailable, blkOffLeft == 0 ? this.leftMBType : MBType.I_16x16, blkOffTop != 0 || topAvailable, blkOffTop == 0 ? this.topMBType[mbX] : MBType.I_16x16, 1, 15, CoeffTransformer.zigzag4x4);
                } else if (this.cabac.readCodedBlockFlagLumaAC(this.mDecoder, CABAC.BlockType.LUMA_15_AC, blkX, blkOffTop, 0, this.leftMBType, this.topMBType[mbX], leftAvailable, topAvailable, this.leftCBPLuma, this.topCBPLuma[mbX], cbpLuma, MBType.I_16x16) == 1) {
                    this.nCoeff[blkY][blkX] = this.cabac.readCoeffs(this.mDecoder, CABAC.BlockType.LUMA_15_AC, ac, 1, 15, CoeffTransformer.zigzag4x4, H264Const.identityMapping16, H264Const.identityMapping16);
                }
                CoeffTransformer.dequantizeAC(ac, this.f1454qp);
            } else if (!this.activePps.entropy_coding_mode_flag) {
                this.cavlc[0].setZeroCoeff(blkX, blkOffTop);
            }
            ac[0] = dc[i];
            CoeffTransformer.idct4x4(ac);
            putBlk(mb.getPlaneData(0), ac, 4, blkOffLeft << 2, blkOffTop << 2);
        }
    }

    private void putBlk(int[] planeData, int[] block, int log2stride, int blkX, int blkY) {
        int stride = 1 << log2stride;
        int srcOff = 0;
        int dstOff = (blkY << log2stride) + blkX;
        for (int line = 0; line < 4; line++) {
            planeData[dstOff] = block[srcOff];
            planeData[dstOff + 1] = block[srcOff + 1];
            planeData[dstOff + 2] = block[srcOff + 2];
            planeData[dstOff + 3] = block[srcOff + 3];
            srcOff += 4;
            dstOff += stride;
        }
    }

    private void putBlk8x8(int[] planeData, int[] block, int log2stride, int blkX, int blkY) {
        int stride = 1 << log2stride;
        int srcOff = 0;
        int dstOff = (blkY << log2stride) + blkX;
        for (int line = 0; line < 8; line++) {
            for (int row = 0; row < 8; row++) {
                planeData[dstOff + row] = block[srcOff + row];
            }
            srcOff += 8;
            dstOff += stride;
        }
    }

    public void decodeChroma(BitReader reader, int pattern, int chromaMode, int mbX, int mbY, boolean leftAvailable, boolean topAvailable, Picture mb, int qp, MBType curMbType) {
        if (this.chromaFormat == ColorSpace.MONO) {
            Arrays.fill(mb.getPlaneData(1), 128);
            Arrays.fill(mb.getPlaneData(2), 128);
            return;
        }
        int qp1 = calcQpChroma(qp, this.chromaQpOffset[0]);
        int qp2 = calcQpChroma(qp, this.chromaQpOffset[1]);
        if (pattern != 0) {
            decodeChromaResidual(reader, leftAvailable, topAvailable, mbX, mbY, pattern, mb, qp1, qp2, curMbType);
        } else if (!this.activePps.entropy_coding_mode_flag) {
            this.cavlc[1].setZeroCoeff(mbX << 1, 0);
            this.cavlc[1].setZeroCoeff((mbX << 1) + 1, 1);
            this.cavlc[2].setZeroCoeff(mbX << 1, 0);
            this.cavlc[2].setZeroCoeff((mbX << 1) + 1, 1);
        }
        int addr = ((this.activeSps.pic_width_in_mbs_minus1 + 1) * mbY) + mbX;
        this.mbQps[1][addr] = qp1;
        this.mbQps[2][addr] = qp2;
        ChromaPredictionBuilder.predictWithMode(mb.getPlaneData(1), chromaMode, mbX, leftAvailable, topAvailable, this.leftRow[1], this.topLine[1], this.topLeft[1]);
        ChromaPredictionBuilder.predictWithMode(mb.getPlaneData(2), chromaMode, mbX, leftAvailable, topAvailable, this.leftRow[2], this.topLine[2], this.topLeft[2]);
    }

    private void decodeChromaResidual(BitReader reader, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, int pattern, Picture mb, int crQp1, int crQp2, MBType curMbType) {
        int[] dc1 = new int[(16 >> this.chromaFormat.compWidth[1]) >> this.chromaFormat.compHeight[1]];
        int[] dc2 = new int[(16 >> this.chromaFormat.compWidth[2]) >> this.chromaFormat.compHeight[2]];
        if ((pattern & 3) > 0) {
            chromaDC(reader, mbX, leftAvailable, topAvailable, dc1, 1, crQp1, curMbType);
            chromaDC(reader, mbX, leftAvailable, topAvailable, dc2, 2, crQp2, curMbType);
        }
        chromaAC(reader, leftAvailable, topAvailable, mbX, mbY, mb, dc1, 1, crQp1, curMbType, (pattern & 2) > 0);
        chromaAC(reader, leftAvailable, topAvailable, mbX, mbY, mb, dc2, 2, crQp2, curMbType, (pattern & 2) > 0);
    }

    private void chromaDC(BitReader reader, int mbX, boolean leftAvailable, boolean topAvailable, int[] dc, int comp, int crQp, MBType curMbType) {
        if (!this.activePps.entropy_coding_mode_flag) {
            this.cavlc[comp].readChromaDCBlock(reader, dc, leftAvailable, topAvailable);
        } else if (this.cabac.readCodedBlockFlagChromaDC(this.mDecoder, mbX, comp, this.leftMBType, this.topMBType[mbX], leftAvailable, topAvailable, this.leftCBPChroma, this.topCBPChroma[mbX], curMbType) == 1) {
            this.cabac.readCoeffs(this.mDecoder, CABAC.BlockType.CHROMA_DC, dc, 0, 4, H264Const.identityMapping16, H264Const.identityMapping16, H264Const.identityMapping16);
        }
        CoeffTransformer.invDC2x2(dc);
        CoeffTransformer.dequantizeDC2x2(dc, crQp);
    }

    private void chromaAC(BitReader reader, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, Picture mb, int[] dc, int comp, int crQp, MBType curMbType, boolean codedAC) {
        for (int i = 0; i < dc.length; i++) {
            int[] ac = new int[16];
            int blkOffLeft = H264Const.MB_BLK_OFF_LEFT[i];
            int blkOffTop = H264Const.MB_BLK_OFF_TOP[i];
            int blkX = (mbX << 1) + blkOffLeft;
            int i2 = (mbY << 1) + blkOffTop;
            if (codedAC) {
                if (!this.activePps.entropy_coding_mode_flag) {
                    this.cavlc[comp].readACBlock(reader, ac, blkX, blkOffTop, blkOffLeft != 0 || leftAvailable, blkOffLeft == 0 ? this.leftMBType : curMbType, blkOffTop != 0 || topAvailable, blkOffTop == 0 ? this.topMBType[mbX] : curMbType, 1, 15, CoeffTransformer.zigzag4x4);
                } else if (this.cabac.readCodedBlockFlagChromaAC(this.mDecoder, blkX, blkOffTop, comp, this.leftMBType, this.topMBType[mbX], leftAvailable, topAvailable, this.leftCBPChroma, this.topCBPChroma[mbX], curMbType) == 1) {
                    this.cabac.readCoeffs(this.mDecoder, CABAC.BlockType.CHROMA_AC, ac, 1, 15, CoeffTransformer.zigzag4x4, H264Const.identityMapping16, H264Const.identityMapping16);
                }
                CoeffTransformer.dequantizeAC(ac, crQp);
            } else if (!this.activePps.entropy_coding_mode_flag) {
                this.cavlc[comp].setZeroCoeff(blkX, blkOffTop);
            }
            ac[0] = dc[i];
            CoeffTransformer.idct4x4(ac);
            putBlk(mb.getPlaneData(comp), ac, 3, blkOffLeft << 2, blkOffTop << 2);
        }
    }

    private int calcQpChroma(int qp, int crQpOffset) {
        return H264Const.QP_SCALE_CR[MathUtil.clip(qp + crQpOffset, 0, 51)];
    }

    public void decodeMBlockIntraNxN(BitReader reader, int mbIndex, MBType prevMbType, Picture mb) {
        int[] lumaModes;
        boolean tlAvailable;
        int mbX = this.mapper.getMbX(mbIndex);
        int mbY = this.mapper.getMbY(mbIndex);
        int mbAddr = this.mapper.getAddress(mbIndex);
        boolean leftAvailable = this.mapper.leftAvailable(mbIndex);
        boolean topAvailable = this.mapper.topAvailable(mbIndex);
        boolean topLeftAvailable = this.mapper.topLeftAvailable(mbIndex);
        boolean topRightAvailable = this.mapper.topRightAvailable(mbIndex);
        boolean transform8x8Used = false;
        if (this.transform8x8) {
            transform8x8Used = readTransform8x8Flag(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.tf8x8Left, this.tf8x8Top[mbX]);
        }
        if (!transform8x8Used) {
            lumaModes = new int[16];
            for (int i = 0; i < 16; i++) {
                lumaModes[i] = readPredictionI4x4Block(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], H264Const.MB_BLK_OFF_LEFT[i], H264Const.MB_BLK_OFF_TOP[i], mbX);
            }
        } else {
            lumaModes = new int[4];
            for (int i2 = 0; i2 < 4; i2++) {
                int blkX = (i2 & 1) << 1;
                int blkY = i2 & 2;
                lumaModes[i2] = readPredictionI4x4Block(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], blkX, blkY, mbX);
                this.i4x4PredLeft[blkY + 1] = this.i4x4PredLeft[blkY];
                this.i4x4PredTop[(mbX << 2) + blkX + 1] = this.i4x4PredTop[(mbX << 2) + blkX];
            }
        }
        int chromaMode = readChromaPredMode(reader, mbX, leftAvailable, topAvailable);
        int codedBlockPattern = readCodedBlockPatternIntra(reader, leftAvailable, topAvailable, this.leftCBPLuma | (this.leftCBPChroma << 4), this.topCBPLuma[mbX] | (this.topCBPChroma[mbX] << 4), this.leftMBType, this.topMBType[mbX]);
        int cbpLuma = codedBlockPattern & 15;
        int cbpChroma = codedBlockPattern >> 4;
        if (cbpLuma > 0 || cbpChroma > 0) {
            this.f1454qp = ((this.f1454qp + readMBQpDelta(reader, prevMbType)) + 52) % 52;
        }
        this.mbQps[0][mbAddr] = this.f1454qp;
        residualLuma(reader, leftAvailable, topAvailable, mbX, mbY, mb, codedBlockPattern, MBType.I_NxN, transform8x8Used, this.tf8x8Left, this.tf8x8Top[mbX]);
        if (!transform8x8Used) {
            for (int i3 = 0; i3 < 16; i3++) {
                int blkX2 = (i3 & 3) << 2;
                int blkY2 = i3 & (-4);
                int bi = H264Const.BLK_INV_MAP[i3];
                boolean trAvailable = ((bi == 0 || bi == 1 || bi == 4) && topAvailable) || (bi == 5 && topRightAvailable) || bi == 2 || bi == 6 || bi == 8 || bi == 9 || bi == 10 || bi == 12 || bi == 14;
                Intra4x4PredictionBuilder.predictWithMode(lumaModes[bi], mb.getPlaneData(0), blkX2 == 0 ? leftAvailable : true, blkY2 == 0 ? topAvailable : true, trAvailable, this.leftRow[0], this.topLine[0], this.topLeft[0], mbX << 4, blkX2, blkY2);
            }
        } else {
            int i4 = 0;
            while (i4 < 4) {
                int blkX3 = (i4 & 1) << 1;
                int blkY3 = i4 & 2;
                boolean trAvailable2 = (i4 == 0 && topAvailable) || (i4 == 1 && topRightAvailable) || i4 == 2;
                if (i4 == 0) {
                    tlAvailable = topLeftAvailable;
                } else {
                    tlAvailable = i4 == 1 ? topAvailable : i4 == 2 ? leftAvailable : true;
                }
                Intra8x8PredictionBuilder.predictWithMode(lumaModes[i4], mb.getPlaneData(0), blkX3 == 0 ? leftAvailable : true, blkY3 == 0 ? topAvailable : true, tlAvailable, trAvailable2, this.leftRow[0], this.topLine[0], this.topLeft[0], mbX << 4, blkX3 << 2, blkY3 << 2);
                i4++;
            }
        }
        decodeChroma(reader, cbpChroma, chromaMode, mbX, mbY, leftAvailable, topAvailable, mb, this.f1454qp, MBType.I_NxN);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        MBType mBType = MBType.I_NxN;
        this.leftMBType = mBType;
        mBTypeArr2[mbX] = mBType;
        mBTypeArr[mbAddr] = mBType;
        int[] iArr = this.topCBPLuma;
        this.leftCBPLuma = cbpLuma;
        iArr[mbX] = cbpLuma;
        int[] iArr2 = this.topCBPChroma;
        this.leftCBPChroma = cbpChroma;
        iArr2[mbX] = cbpChroma;
        this.tf8x8Top[mbX] = transform8x8Used;
        this.tf8x8Left = transform8x8Used;
        this.tr8x8Used[mbAddr] = transform8x8Used;
        collectChromaPredictors(mb, mbX);
        saveMvsIntra(mbX, mbY);
    }

    private void saveMvsIntra(int mbX, int mbY) {
        int j = 0;
        int blkOffY = mbY << 2;
        int blkInd = 0;
        while (j < 4) {
            int i = 0;
            int blkOffX = mbX << 2;
            while (i < 4) {
                this.mvs[0][blkOffY][blkOffX] = NULL_VECTOR;
                this.mvs[1][blkOffY][blkOffX] = NULL_VECTOR;
                i++;
                blkOffX++;
                blkInd++;
            }
            j++;
            blkOffY++;
        }
    }

    private void residualLuma(BitReader reader, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, Picture mb, int codedBlockPattern, MBType mbType, boolean transform8x8Used, boolean is8x8Left, boolean is8x8Top) {
        if (!transform8x8Used) {
            residualLuma(reader, leftAvailable, topAvailable, mbX, mbY, mb, codedBlockPattern, mbType);
        } else if (this.activePps.entropy_coding_mode_flag) {
            residualLuma8x8CABAC(reader, leftAvailable, topAvailable, mbX, mbY, mb, codedBlockPattern, mbType, is8x8Left, is8x8Top);
        } else {
            residualLuma8x8CAVLC(reader, leftAvailable, topAvailable, mbX, mbY, mb, codedBlockPattern, mbType);
        }
    }

    private boolean readTransform8x8Flag(BitReader reader, boolean leftAvailable, boolean topAvailable, MBType leftType, MBType topType, boolean is8x8Left, boolean is8x8Top) {
        return !this.activePps.entropy_coding_mode_flag ? CAVLCReader.readBool(reader, "transform_size_8x8_flag") : this.cabac.readTransform8x8Flag(this.mDecoder, leftAvailable, topAvailable, leftType, topType, is8x8Left, is8x8Top);
    }

    protected int readCodedBlockPatternIntra(BitReader reader, boolean leftAvailable, boolean topAvailable, int leftCBP, int topCBP, MBType leftMB, MBType topMB) {
        return !this.activePps.entropy_coding_mode_flag ? H264Const.CODED_BLOCK_PATTERN_INTRA_COLOR[CAVLCReader.readUE(reader, "coded_block_pattern")] : this.cabac.codedBlockPatternIntra(this.mDecoder, leftAvailable, topAvailable, leftCBP, topCBP, leftMB, topMB);
    }

    protected int readCodedBlockPatternInter(BitReader reader, boolean leftAvailable, boolean topAvailable, int leftCBP, int topCBP, MBType leftMB, MBType topMB) {
        return !this.activePps.entropy_coding_mode_flag ? H264Const.CODED_BLOCK_PATTERN_INTER_COLOR[CAVLCReader.readUE(reader, "coded_block_pattern")] : this.cabac.codedBlockPatternIntra(this.mDecoder, leftAvailable, topAvailable, leftCBP, topCBP, leftMB, topMB);
    }

    private void residualLuma(BitReader reader, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, Picture mb, int codedBlockPattern, MBType curMbType) {
        int cbpLuma = codedBlockPattern & 15;
        for (int i = 0; i < 16; i++) {
            int blkOffLeft = H264Const.MB_BLK_OFF_LEFT[i];
            int blkOffTop = H264Const.MB_BLK_OFF_TOP[i];
            int blkX = (mbX << 2) + blkOffLeft;
            int blkY = (mbY << 2) + blkOffTop;
            if (((1 << (i >> 2)) & cbpLuma) == 0) {
                if (!this.activePps.entropy_coding_mode_flag) {
                    this.cavlc[0].setZeroCoeff(blkX, blkOffTop);
                }
            } else {
                int[] ac = new int[16];
                if (!this.activePps.entropy_coding_mode_flag) {
                    this.nCoeff[blkY][blkX] = this.cavlc[0].readACBlock(reader, ac, blkX, blkOffTop, blkOffLeft != 0 || leftAvailable, blkOffLeft == 0 ? this.leftMBType : curMbType, blkOffTop != 0 || topAvailable, blkOffTop == 0 ? this.topMBType[mbX] : curMbType, 0, 16, CoeffTransformer.zigzag4x4);
                } else if (this.cabac.readCodedBlockFlagLumaAC(this.mDecoder, CABAC.BlockType.LUMA_16, blkX, blkOffTop, 0, this.leftMBType, this.topMBType[mbX], leftAvailable, topAvailable, this.leftCBPLuma, this.topCBPLuma[mbX], cbpLuma, curMbType) == 1) {
                    this.nCoeff[blkY][blkX] = this.cabac.readCoeffs(this.mDecoder, CABAC.BlockType.LUMA_16, ac, 0, 16, CoeffTransformer.zigzag4x4, H264Const.identityMapping16, H264Const.identityMapping16);
                }
                CoeffTransformer.dequantizeAC(ac, this.f1454qp);
                CoeffTransformer.idct4x4(ac);
                putBlk(mb.getPlaneData(0), ac, 4, blkOffLeft << 2, blkOffTop << 2);
            }
        }
        if (this.activePps.entropy_coding_mode_flag) {
            this.cabac.setPrevCBP(codedBlockPattern);
        }
    }

    private void residualLuma8x8CABAC(BitReader reader, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, Picture mb, int codedBlockPattern, MBType curMbType, boolean is8x8Left, boolean is8x8Top) {
        int cbpLuma = codedBlockPattern & 15;
        for (int i = 0; i < 4; i++) {
            int blkOffLeft = (i & 1) << 1;
            int blkOffTop = i & 2;
            int blkX = (mbX << 2) + blkOffLeft;
            int blkY = (mbY << 2) + blkOffTop;
            if (((1 << i) & cbpLuma) != 0) {
                int[] ac = new int[64];
                int[] iArr = this.nCoeff[blkY];
                int[] iArr2 = this.nCoeff[blkY + 1];
                int readCoeffs = this.cabac.readCoeffs(this.mDecoder, CABAC.BlockType.LUMA_64, ac, 0, 64, CoeffTransformer.zigzag8x8, H264Const.sig_coeff_map_8x8, H264Const.last_sig_coeff_map_8x8);
                this.nCoeff[blkY + 1][blkX + 1] = readCoeffs;
                iArr2[blkX] = readCoeffs;
                this.nCoeff[blkY][blkX + 1] = readCoeffs;
                iArr[blkX] = readCoeffs;
                this.cabac.setCodedBlock(blkX, blkY);
                this.cabac.setCodedBlock(blkX + 1, blkY);
                this.cabac.setCodedBlock(blkX, blkY + 1);
                this.cabac.setCodedBlock(blkX + 1, blkY + 1);
                CoeffTransformer.dequantizeAC8x8(ac, this.f1454qp);
                CoeffTransformer.idct8x8(ac);
                putBlk8x8(mb.getPlaneData(0), ac, 4, blkOffLeft << 2, blkOffTop << 2);
            }
        }
        this.cabac.setPrevCBP(codedBlockPattern);
    }

    private void residualLuma8x8CAVLC(BitReader reader, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, Picture mb, int codedBlockPattern, MBType curMbType) {
        int cbpLuma = codedBlockPattern & 15;
        for (int i = 0; i < 4; i++) {
            int blk8x8OffLeft = (i & 1) << 1;
            int blk8x8OffTop = i & 2;
            int blkX = (mbX << 2) + blk8x8OffLeft;
            int blkY = (mbY << 2) + blk8x8OffTop;
            if (((1 << i) & cbpLuma) == 0) {
                this.cavlc[0].setZeroCoeff(blkX, blk8x8OffTop);
                this.cavlc[0].setZeroCoeff(blkX + 1, blk8x8OffTop);
                this.cavlc[0].setZeroCoeff(blkX, blk8x8OffTop + 1);
                this.cavlc[0].setZeroCoeff(blkX + 1, blk8x8OffTop + 1);
            } else {
                int[] ac64 = new int[64];
                int coeffs = 0;
                for (int j = 0; j < 4; j++) {
                    int[] ac16 = new int[16];
                    int blkOffLeft = blk8x8OffLeft + (j & 1);
                    int blkOffTop = blk8x8OffTop + (j >> 1);
                    coeffs += this.cavlc[0].readACBlock(reader, ac16, blkX + (j & 1), blkOffTop, blkOffLeft != 0 || leftAvailable, blkOffLeft == 0 ? this.leftMBType : curMbType, blkOffTop != 0 || topAvailable, blkOffTop == 0 ? this.topMBType[mbX] : curMbType, 0, 16, H264Const.identityMapping16);
                    for (int k = 0; k < 16; k++) {
                        ac64[CoeffTransformer.zigzag8x8[(k << 2) + j]] = ac16[k];
                    }
                }
                int[] iArr = this.nCoeff[blkY];
                int[] iArr2 = this.nCoeff[blkY + 1];
                this.nCoeff[blkY + 1][blkX + 1] = coeffs;
                iArr2[blkX] = coeffs;
                this.nCoeff[blkY][blkX + 1] = coeffs;
                iArr[blkX] = coeffs;
                CoeffTransformer.dequantizeAC8x8(ac64, this.f1454qp);
                CoeffTransformer.idct8x8(ac64);
                putBlk8x8(mb.getPlaneData(0), ac64, 4, blk8x8OffLeft << 2, blk8x8OffTop << 2);
            }
        }
    }

    private int readPredictionI4x4Block(BitReader reader, boolean leftAvailable, boolean topAvailable, MBType leftMBType, MBType topMBType, int blkX, int blkY, int mbX) {
        int mode = 2;
        if ((leftAvailable || blkX > 0) && (topAvailable || blkY > 0)) {
            int predModeB = (topMBType == MBType.I_NxN || blkY > 0) ? this.i4x4PredTop[(mbX << 2) + blkX] : 2;
            int predModeA = (leftMBType == MBType.I_NxN || blkX > 0) ? this.i4x4PredLeft[blkY] : 2;
            mode = Math.min(predModeB, predModeA);
        }
        if (!prev4x4PredMode(reader)) {
            int rem_intra4x4_pred_mode = rem4x4PredMode(reader);
            mode = rem_intra4x4_pred_mode + (rem_intra4x4_pred_mode < mode ? 0 : 1);
        }
        this.i4x4PredLeft[blkY] = mode;
        this.i4x4PredTop[(mbX << 2) + blkX] = mode;
        return mode;
    }

    private int rem4x4PredMode(BitReader reader) {
        return !this.activePps.entropy_coding_mode_flag ? CAVLCReader.readNBit(reader, 3, "MB: rem_intra4x4_pred_mode") : this.cabac.rem4x4PredMode(this.mDecoder);
    }

    private boolean prev4x4PredMode(BitReader reader) {
        return !this.activePps.entropy_coding_mode_flag ? CAVLCReader.readBool(reader, "MBP: prev_intra4x4_pred_mode_flag") : this.cabac.prev4x4PredModeFlag(this.mDecoder);
    }

    private void decodeInter16x8(BitReader reader, Picture mb, Frame[][] refs, int mbIdx, MBType prevMbType, H264Const.PartPred p0, H264Const.PartPred p1, MBType curMBType) {
        int mbX = this.mapper.getMbX(mbIdx);
        int mbY = this.mapper.getMbY(mbIdx);
        boolean leftAvailable = this.mapper.leftAvailable(mbIdx);
        boolean topAvailable = this.mapper.topAvailable(mbIdx);
        boolean topLeftAvailable = this.mapper.topLeftAvailable(mbIdx);
        boolean topRightAvailable = this.mapper.topRightAvailable(mbIdx);
        int address = this.mapper.getAddress(mbIdx);
        int xx = mbX << 2;
        int[] refIdx1 = {0, 0};
        int[] refIdx2 = {0, 0};
        int[][][] x = new int[2][];
        for (int list = 0; list < 2; list++) {
            if (p0.usesList(list) && this.numRef[list] > 1) {
                refIdx1[list] = readRefIdx(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[mbX << 1], p0, mbX, 0, 0, 4, 2, list);
            }
            if (p1.usesList(list) && this.numRef[list] > 1) {
                refIdx2[list] = readRefIdx(reader, leftAvailable, true, this.leftMBType, curMBType, this.predModeLeft[1], p0, p1, mbX, 0, 2, 4, 2, list);
            }
        }
        Picture[] mbb = {Picture.create(16, 16, this.chromaFormat), Picture.create(16, 16, this.chromaFormat)};
        for (int list2 = 0; list2 < 2; list2++) {
            predictInter16x8(reader, mbb[list2], refs, mbX, mbY, leftAvailable, topAvailable, topLeftAvailable, topRightAvailable, xx, refIdx1, refIdx2, x, p0, p1, list2);
        }
        this.prediction.mergePrediction(x[0][0][2], x[1][0][2], p0, 0, mbb[0].getPlaneData(0), mbb[1].getPlaneData(0), 0, 16, 16, 8, mb.getPlaneData(0), refs, this.thisFrame);
        this.prediction.mergePrediction(x[0][8][2], x[1][8][2], p1, 0, mbb[0].getPlaneData(0), mbb[1].getPlaneData(0), 128, 16, 16, 8, mb.getPlaneData(0), refs, this.thisFrame);
        this.predModeLeft[0] = p0;
        H264Const.PartPred[] partPredArr = this.predModeLeft;
        this.predModeTop[(mbX << 1) + 1] = p1;
        this.predModeTop[mbX << 1] = p1;
        partPredArr[1] = p1;
        residualInter(reader, mb, refs, leftAvailable, topAvailable, mbX, mbY, x, new H264Const.PartPred[]{p0, p0, p1, p1}, this.mapper.getAddress(mbIdx), prevMbType, curMBType);
        collectPredictors(mb, mbX);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        this.leftMBType = curMBType;
        mBTypeArr2[mbX] = curMBType;
        mBTypeArr[address] = curMBType;
    }

    private void predictInter16x8(BitReader reader, Picture mb, Picture[][] references, int mbX, int mbY, boolean leftAvailable, boolean topAvailable, boolean tlAvailable, boolean trAvailable, int xx, int[] refIdx1, int[] refIdx2, int[][][] x, H264Const.PartPred p0, H264Const.PartPred p1, int list) {
        int blk8x8X = mbX << 1;
        int mvX1 = 0;
        int mvY1 = 0;
        int mvX2 = 0;
        int mvY2 = 0;
        int r1 = -1;
        int r2 = -1;
        if (p0.usesList(list)) {
            int mvdX1 = readMVD(reader, 0, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[blk8x8X], p0, mbX, 0, 0, 4, 2, list);
            int mvdY1 = readMVD(reader, 1, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[blk8x8X], p0, mbX, 0, 0, 4, 2, list);
            int mvpX1 = calcMVPrediction16x8Top(this.mvLeft[list][0], this.mvTop[list][mbX << 2], this.mvTop[list][(mbX << 2) + 4], this.mvTopLeft[list], leftAvailable, topAvailable, trAvailable, tlAvailable, refIdx1[list], 0);
            int mvpY1 = calcMVPrediction16x8Top(this.mvLeft[list][0], this.mvTop[list][mbX << 2], this.mvTop[list][(mbX << 2) + 4], this.mvTopLeft[list], leftAvailable, topAvailable, trAvailable, tlAvailable, refIdx1[list], 1);
            mvX1 = mvdX1 + mvpX1;
            mvY1 = mvdY1 + mvpY1;
            debugPrint("MVP: (" + mvpX1 + ", " + mvpY1 + "), MVD: (" + mvdX1 + ", " + mvdY1 + "), MV: (" + mvX1 + "," + mvY1 + "," + refIdx1[list] + ")");
            BlockInterpolator.getBlockLuma(references[list][refIdx1[list]], mb, 0, (mbX << 6) + mvX1, (mbY << 6) + mvY1, 16, 8);
            r1 = refIdx1[list];
        }
        int[] v1 = new int[3];
        v1[0] = mvX1;
        v1[1] = mvY1;
        v1[2] = r1;
        if (p1.usesList(list)) {
            int mvdX2 = readMVD(reader, 0, leftAvailable, true, this.leftMBType, MBType.P_16x8, this.predModeLeft[1], p0, p1, mbX, 0, 2, 4, 2, list);
            int mvdY2 = readMVD(reader, 1, leftAvailable, true, this.leftMBType, MBType.P_16x8, this.predModeLeft[1], p0, p1, mbX, 0, 2, 4, 2, list);
            int mvpX2 = calcMVPrediction16x8Bottom(this.mvLeft[list][2], v1, null, this.mvLeft[list][1], leftAvailable, true, false, leftAvailable, refIdx2[list], 0);
            int mvpY2 = calcMVPrediction16x8Bottom(this.mvLeft[list][2], v1, null, this.mvLeft[list][1], leftAvailable, true, false, leftAvailable, refIdx2[list], 1);
            mvX2 = mvdX2 + mvpX2;
            mvY2 = mvdY2 + mvpY2;
            debugPrint("MVP: (" + mvpX2 + ", " + mvpY2 + "), MVD: (" + mvdX2 + ", " + mvdY2 + "), MV: (" + mvX2 + "," + mvY2 + "," + refIdx2[list] + ")");
            BlockInterpolator.getBlockLuma(references[list][refIdx2[list]], mb, 128, (mbX << 6) + mvX2, (mbY << 6) + 32 + mvY2, 16, 8);
            r2 = refIdx2[list];
        }
        int[] v2 = new int[3];
        v2[0] = mvX2;
        v2[1] = mvY2;
        v2[2] = r2;
        copyVect(this.mvTopLeft[list], this.mvTop[list][xx + 3]);
        saveVect(this.mvLeft[list], 0, 2, mvX1, mvY1, r1);
        saveVect(this.mvLeft[list], 2, 4, mvX2, mvY2, r2);
        saveVect(this.mvTop[list], xx, xx + 4, mvX2, mvY2, r2);
        int[][] iArr = new int[16];
        iArr[0] = v1;
        iArr[1] = v1;
        iArr[2] = v1;
        iArr[3] = v1;
        iArr[4] = v1;
        iArr[5] = v1;
        iArr[6] = v1;
        iArr[7] = v1;
        iArr[8] = v2;
        iArr[9] = v2;
        iArr[10] = v2;
        iArr[11] = v2;
        iArr[12] = v2;
        iArr[13] = v2;
        iArr[14] = v2;
        iArr[15] = v2;
        x[list] = iArr;
    }

    private void residualInter(BitReader reader, Picture mb, Frame[][] refs, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, int[][][] x, H264Const.PartPred[] pp, int mbAddr, MBType prevMbType, MBType curMbType) {
        int codedBlockPattern = readCodedBlockPatternInter(reader, leftAvailable, topAvailable, this.leftCBPLuma | (this.leftCBPChroma << 4), this.topCBPLuma[mbX] | (this.topCBPChroma[mbX] << 4), this.leftMBType, this.topMBType[mbX]);
        int cbpLuma = codedBlockPattern & 15;
        int cbpChroma = codedBlockPattern >> 4;
        Picture mb1 = Picture.create(16, 16, this.chromaFormat);
        boolean transform8x8Used = false;
        if (cbpLuma != 0 && this.transform8x8) {
            transform8x8Used = readTransform8x8Flag(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.tf8x8Left, this.tf8x8Top[mbX]);
        }
        if (cbpLuma > 0 || cbpChroma > 0) {
            int mbQpDelta = readMBQpDelta(reader, prevMbType);
            this.f1454qp = ((this.f1454qp + mbQpDelta) + 52) % 52;
        }
        this.mbQps[0][mbAddr] = this.f1454qp;
        residualLuma(reader, leftAvailable, topAvailable, mbX, mbY, mb1, codedBlockPattern, curMbType, transform8x8Used, this.tf8x8Left, this.tf8x8Top[mbX]);
        saveMvs(x, mbX, mbY);
        if (this.chromaFormat == ColorSpace.MONO) {
            Arrays.fill(mb.getPlaneData(1), 128);
            Arrays.fill(mb.getPlaneData(2), 128);
        } else {
            decodeChromaInter(reader, cbpChroma, refs, x, pp, leftAvailable, topAvailable, mbX, mbY, mbAddr, this.f1454qp, mb, mb1);
        }
        mergeResidual(mb, mb1);
        int[] iArr = this.topCBPLuma;
        this.leftCBPLuma = cbpLuma;
        iArr[mbX] = cbpLuma;
        int[] iArr2 = this.topCBPChroma;
        this.leftCBPChroma = cbpChroma;
        iArr2[mbX] = cbpChroma;
        this.tf8x8Top[mbX] = transform8x8Used;
        this.tf8x8Left = transform8x8Used;
        this.tr8x8Used[mbAddr] = transform8x8Used;
    }

    private void mergeResidual(Picture mb, Picture mb1) {
        for (int j = 0; j < 3; j++) {
            int[] to = mb.getPlaneData(j);
            int[] from = mb1.getPlaneData(j);
            for (int i = 0; i < to.length; i++) {
                to[i] = MathUtil.clip(to[i] + from[i], 0, 255);
            }
        }
    }

    private void decodeInter8x16(BitReader reader, Picture mb, Frame[][] refs, int mbIdx, MBType prevMbType, H264Const.PartPred p0, H264Const.PartPred p1, MBType curMBType) {
        int mbX = this.mapper.getMbX(mbIdx);
        int mbY = this.mapper.getMbY(mbIdx);
        boolean leftAvailable = this.mapper.leftAvailable(mbIdx);
        boolean topAvailable = this.mapper.topAvailable(mbIdx);
        boolean topLeftAvailable = this.mapper.topLeftAvailable(mbIdx);
        boolean topRightAvailable = this.mapper.topRightAvailable(mbIdx);
        int address = this.mapper.getAddress(mbIdx);
        int[][][] x = new int[2][];
        int[] refIdx1 = {0, 0};
        int[] refIdx2 = {0, 0};
        for (int list = 0; list < 2; list++) {
            if (p0.usesList(list) && this.numRef[list] > 1) {
                refIdx1[list] = readRefIdx(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[mbX << 1], p0, mbX, 0, 0, 2, 4, list);
            }
            if (p1.usesList(list) && this.numRef[list] > 1) {
                refIdx2[list] = readRefIdx(reader, true, topAvailable, curMBType, this.topMBType[mbX], p0, this.predModeTop[(mbX << 1) + 1], p1, mbX, 2, 0, 2, 4, list);
            }
        }
        Picture[] mbb = {Picture.create(16, 16, this.chromaFormat), Picture.create(16, 16, this.chromaFormat)};
        for (int list2 = 0; list2 < 2; list2++) {
            predictInter8x16(reader, mbb[list2], refs, mbX, mbY, leftAvailable, topAvailable, topLeftAvailable, topRightAvailable, x, refIdx1, refIdx2, list2, p0, p1);
        }
        this.prediction.mergePrediction(x[0][0][2], x[1][0][2], p0, 0, mbb[0].getPlaneData(0), mbb[1].getPlaneData(0), 0, 16, 8, 16, mb.getPlaneData(0), refs, this.thisFrame);
        this.prediction.mergePrediction(x[0][2][2], x[1][2][2], p1, 0, mbb[0].getPlaneData(0), mbb[1].getPlaneData(0), 8, 16, 8, 16, mb.getPlaneData(0), refs, this.thisFrame);
        this.predModeTop[mbX << 1] = p0;
        H264Const.PartPred[] partPredArr = this.predModeLeft;
        this.predModeLeft[1] = p1;
        partPredArr[0] = p1;
        this.predModeTop[(mbX << 1) + 1] = p1;
        residualInter(reader, mb, refs, leftAvailable, topAvailable, mbX, mbY, x, new H264Const.PartPred[]{p0, p1, p0, p1}, this.mapper.getAddress(mbIdx), prevMbType, curMBType);
        collectPredictors(mb, mbX);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        this.leftMBType = curMBType;
        mBTypeArr2[mbX] = curMBType;
        mBTypeArr[address] = curMBType;
    }

    private void predictInter8x16(BitReader reader, Picture mb, Picture[][] references, int mbX, int mbY, boolean leftAvailable, boolean topAvailable, boolean tlAvailable, boolean trAvailable, int[][][] x, int[] refIdx1, int[] refIdx2, int list, H264Const.PartPred p0, H264Const.PartPred p1) {
        int xx = mbX << 2;
        int blk8x8X = mbX << 1;
        int mvX1 = 0;
        int mvY1 = 0;
        int r1 = -1;
        int mvX2 = 0;
        int mvY2 = 0;
        int r2 = -1;
        if (p0.usesList(list)) {
            int mvdX1 = readMVD(reader, 0, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[blk8x8X], p0, mbX, 0, 0, 2, 4, list);
            int mvdY1 = readMVD(reader, 1, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[blk8x8X], p0, mbX, 0, 0, 2, 4, list);
            int mvpX1 = calcMVPrediction8x16Left(this.mvLeft[list][0], this.mvTop[list][mbX << 2], this.mvTop[list][(mbX << 2) + 2], this.mvTopLeft[list], leftAvailable, topAvailable, topAvailable, tlAvailable, refIdx1[list], 0);
            int mvpY1 = calcMVPrediction8x16Left(this.mvLeft[list][0], this.mvTop[list][mbX << 2], this.mvTop[list][(mbX << 2) + 2], this.mvTopLeft[list], leftAvailable, topAvailable, topAvailable, tlAvailable, refIdx1[list], 1);
            mvX1 = mvdX1 + mvpX1;
            mvY1 = mvdY1 + mvpY1;
            debugPrint("MVP: (" + mvpX1 + ", " + mvpY1 + "), MVD: (" + mvdX1 + ", " + mvdY1 + "), MV: (" + mvX1 + "," + mvY1 + "," + refIdx1[list] + ")");
            BlockInterpolator.getBlockLuma(references[list][refIdx1[list]], mb, 0, (mbX << 6) + mvX1, (mbY << 6) + mvY1, 8, 16);
            r1 = refIdx1[list];
        }
        int[] v1 = new int[3];
        v1[0] = mvX1;
        v1[1] = mvY1;
        v1[2] = r1;
        if (p1.usesList(list)) {
            int mvdX2 = readMVD(reader, 0, true, topAvailable, MBType.P_8x16, this.topMBType[mbX], p0, this.predModeTop[blk8x8X + 1], p1, mbX, 2, 0, 2, 4, list);
            int mvdY2 = readMVD(reader, 1, true, topAvailable, MBType.P_8x16, this.topMBType[mbX], p0, this.predModeTop[blk8x8X + 1], p1, mbX, 2, 0, 2, 4, list);
            int mvpX2 = calcMVPrediction8x16Right(v1, this.mvTop[list][(mbX << 2) + 2], this.mvTop[list][(mbX << 2) + 4], this.mvTop[list][(mbX << 2) + 1], true, topAvailable, trAvailable, topAvailable, refIdx2[list], 0);
            int mvpY2 = calcMVPrediction8x16Right(v1, this.mvTop[list][(mbX << 2) + 2], this.mvTop[list][(mbX << 2) + 4], this.mvTop[list][(mbX << 2) + 1], true, topAvailable, trAvailable, topAvailable, refIdx2[list], 1);
            mvX2 = mvdX2 + mvpX2;
            mvY2 = mvdY2 + mvpY2;
            debugPrint("MVP: (" + mvpX2 + ", " + mvpY2 + "), MVD: (" + mvdX2 + ", " + mvdY2 + "), MV: (" + mvX2 + "," + mvY2 + "," + refIdx2[list] + ")");
            BlockInterpolator.getBlockLuma(references[list][refIdx2[list]], mb, 8, (mbX << 6) + 32 + mvX2, (mbY << 6) + mvY2, 8, 16);
            r2 = refIdx2[list];
        }
        int[] v2 = new int[3];
        v2[0] = mvX2;
        v2[1] = mvY2;
        v2[2] = r2;
        copyVect(this.mvTopLeft[list], this.mvTop[list][xx + 3]);
        saveVect(this.mvTop[list], xx, xx + 2, mvX1, mvY1, r1);
        saveVect(this.mvTop[list], xx + 2, xx + 4, mvX2, mvY2, r2);
        saveVect(this.mvLeft[list], 0, 4, mvX2, mvY2, r2);
        int[][] iArr = new int[16];
        iArr[0] = v1;
        iArr[1] = v1;
        iArr[2] = v2;
        iArr[3] = v2;
        iArr[4] = v1;
        iArr[5] = v1;
        iArr[6] = v2;
        iArr[7] = v2;
        iArr[8] = v1;
        iArr[9] = v1;
        iArr[10] = v2;
        iArr[11] = v2;
        iArr[12] = v1;
        iArr[13] = v1;
        iArr[14] = v2;
        iArr[15] = v2;
        x[list] = iArr;
    }

    private void decodeInter16x16(BitReader reader, Picture mb, Frame[][] refs, int mbIdx, MBType prevMbType, H264Const.PartPred p0, MBType curMBType) {
        int mbX = this.mapper.getMbX(mbIdx);
        int mbY = this.mapper.getMbY(mbIdx);
        boolean leftAvailable = this.mapper.leftAvailable(mbIdx);
        boolean topAvailable = this.mapper.topAvailable(mbIdx);
        boolean topLeftAvailable = this.mapper.topLeftAvailable(mbIdx);
        boolean topRightAvailable = this.mapper.topRightAvailable(mbIdx);
        int address = this.mapper.getAddress(mbIdx);
        int[][][] x = new int[2][];
        int xx = mbX << 2;
        int[] refIdx = {0, 0};
        for (int list = 0; list < 2; list++) {
            if (p0.usesList(list) && this.numRef[list] > 1) {
                refIdx[list] = readRefIdx(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[mbX << 1], p0, mbX, 0, 0, 4, 4, list);
            }
        }
        Picture[] mbb = {Picture.create(16, 16, this.chromaFormat), Picture.create(16, 16, this.chromaFormat)};
        for (int list2 = 0; list2 < 2; list2++) {
            predictInter16x16(reader, mbb[list2], refs, mbX, mbY, leftAvailable, topAvailable, topLeftAvailable, topRightAvailable, x, xx, refIdx, list2, p0);
        }
        this.prediction.mergePrediction(x[0][0][2], x[1][0][2], p0, 0, mbb[0].getPlaneData(0), mbb[1].getPlaneData(0), 0, 16, 16, 16, mb.getPlaneData(0), refs, this.thisFrame);
        H264Const.PartPred[] partPredArr = this.predModeLeft;
        H264Const.PartPred[] partPredArr2 = this.predModeLeft;
        this.predModeTop[(mbX << 1) + 1] = p0;
        this.predModeTop[mbX << 1] = p0;
        partPredArr2[1] = p0;
        partPredArr[0] = p0;
        residualInter(reader, mb, refs, leftAvailable, topAvailable, mbX, mbY, x, new H264Const.PartPred[]{p0, p0, p0, p0}, this.mapper.getAddress(mbIdx), prevMbType, curMBType);
        collectPredictors(mb, mbX);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        this.leftMBType = curMBType;
        mBTypeArr2[mbX] = curMBType;
        mBTypeArr[address] = curMBType;
    }

    private void predictInter16x16(BitReader reader, Picture mb, Picture[][] references, int mbX, int mbY, boolean leftAvailable, boolean topAvailable, boolean tlAvailable, boolean trAvailable, int[][][] x, int xx, int[] refIdx, int list, H264Const.PartPred curPred) {
        int blk8x8X = mbX << 1;
        int mvX = 0;
        int mvY = 0;
        int r = -1;
        if (curPred.usesList(list)) {
            int mvpX = calcMVPredictionMedian(this.mvLeft[list][0], this.mvTop[list][mbX << 2], this.mvTop[list][(mbX << 2) + 4], this.mvTopLeft[list], leftAvailable, topAvailable, trAvailable, tlAvailable, refIdx[list], 0);
            int mvpY = calcMVPredictionMedian(this.mvLeft[list][0], this.mvTop[list][mbX << 2], this.mvTop[list][(mbX << 2) + 4], this.mvTopLeft[list], leftAvailable, topAvailable, trAvailable, tlAvailable, refIdx[list], 1);
            int mvdX = readMVD(reader, 0, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[blk8x8X], curPred, mbX, 0, 0, 4, 4, list);
            int mvdY = readMVD(reader, 1, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[blk8x8X], curPred, mbX, 0, 0, 4, 4, list);
            int mvX2 = mvdX + mvpX;
            int mvY2 = mvdY + mvpY;
            debugPrint("MVP: (" + mvpX + ", " + mvpY + "), MVD: (" + mvdX + ", " + mvdY + "), MV: (" + mvX2 + "," + mvY2 + "," + refIdx[list] + ")");
            int r2 = refIdx[list];
            BlockInterpolator.getBlockLuma(references[list][r2], mb, 0, (mbX << 6) + mvX2, (mbY << 6) + mvY2, 16, 16);
            r = r2;
            mvY = mvY2;
            mvX = mvX2;
        }
        copyVect(this.mvTopLeft[list], this.mvTop[list][xx + 3]);
        saveVect(this.mvTop[list], xx, xx + 4, mvX, mvY, r);
        saveVect(this.mvLeft[list], 0, 4, mvX, mvY, r);
        int[] v = new int[3];
        v[0] = mvX;
        v[1] = mvY;
        v[2] = r;
        int[][] iArr = new int[16];
        iArr[0] = v;
        iArr[1] = v;
        iArr[2] = v;
        iArr[3] = v;
        iArr[4] = v;
        iArr[5] = v;
        iArr[6] = v;
        iArr[7] = v;
        iArr[8] = v;
        iArr[9] = v;
        iArr[10] = v;
        iArr[11] = v;
        iArr[12] = v;
        iArr[13] = v;
        iArr[14] = v;
        iArr[15] = v;
        x[list] = iArr;
    }

    private int readRefIdx(BitReader reader, boolean leftAvailable, boolean topAvailable, MBType leftType, MBType topType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred curPred, int mbX, int partX, int partY, int partW, int partH, int list) {
        return !this.activePps.entropy_coding_mode_flag ? CAVLCReader.readTE(reader, this.numRef[list] - 1) : this.cabac.readRefIdx(this.mDecoder, leftAvailable, topAvailable, leftType, topType, leftPred, topPred, curPred, mbX, partX, partY, partW, partH, list);
    }

    private void saveVect(int[][] mv, int from, int to, int x, int y, int r) {
        for (int i = from; i < to; i++) {
            mv[i][0] = x;
            mv[i][1] = y;
            mv[i][2] = r;
        }
    }

    public int calcMVPredictionMedian(int[] a, int[] b, int[] c, int[] d, boolean aAvb, boolean bAvb, boolean cAvb, boolean dAvb, int ref, int comp) {
        if (!cAvb) {
            c = d;
            cAvb = dAvb;
        }
        if (aAvb && !bAvb && !cAvb) {
            c = a;
            b = a;
            cAvb = aAvb;
            bAvb = aAvb;
        }
        if (!aAvb) {
            a = NULL_VECTOR;
        }
        if (!bAvb) {
            b = NULL_VECTOR;
        }
        if (!cAvb) {
            c = NULL_VECTOR;
        }
        if (a[2] == ref && b[2] != ref && c[2] != ref) {
            return a[comp];
        }
        if (b[2] == ref && a[2] != ref && c[2] != ref) {
            return b[comp];
        }
        if (c[2] == ref && a[2] != ref && b[2] != ref) {
            return c[comp];
        }
        return (((a[comp] + b[comp]) + c[comp]) - min(a[comp], b[comp], c[comp])) - max(a[comp], b[comp], c[comp]);
    }

    private int max(int x, int x2, int x3) {
        return x > x2 ? x > x3 ? x : x3 : x2 > x3 ? x2 : x3;
    }

    private int min(int x, int x2, int x3) {
        return x < x2 ? x < x3 ? x : x3 : x2 < x3 ? x2 : x3;
    }

    public int calcMVPrediction16x8Top(int[] a, int[] b, int[] c, int[] d, boolean aAvb, boolean bAvb, boolean cAvb, boolean dAvb, int refIdx, int comp) {
        return (bAvb && b[2] == refIdx) ? b[comp] : calcMVPredictionMedian(a, b, c, d, aAvb, bAvb, cAvb, dAvb, refIdx, comp);
    }

    public int calcMVPrediction16x8Bottom(int[] a, int[] b, int[] c, int[] d, boolean aAvb, boolean bAvb, boolean cAvb, boolean dAvb, int refIdx, int comp) {
        return (aAvb && a[2] == refIdx) ? a[comp] : calcMVPredictionMedian(a, b, c, d, aAvb, bAvb, cAvb, dAvb, refIdx, comp);
    }

    public int calcMVPrediction8x16Left(int[] a, int[] b, int[] c, int[] d, boolean aAvb, boolean bAvb, boolean cAvb, boolean dAvb, int refIdx, int comp) {
        return (aAvb && a[2] == refIdx) ? a[comp] : calcMVPredictionMedian(a, b, c, d, aAvb, bAvb, cAvb, dAvb, refIdx, comp);
    }

    public int calcMVPrediction8x16Right(int[] a, int[] b, int[] c, int[] d, boolean aAvb, boolean bAvb, boolean cAvb, boolean dAvb, int refIdx, int comp) {
        int[] lc;
        if (cAvb) {
            lc = c;
        } else {
            lc = dAvb ? d : NULL_VECTOR;
        }
        if (lc[2] == refIdx) {
            return lc[comp];
        }
        return calcMVPredictionMedian(a, b, c, d, aAvb, bAvb, cAvb, dAvb, refIdx, comp);
    }

    public void decodeMBInter8x8(BitReader reader, int mb_type, Frame[][] references, Picture mb, SliceType sliceType, int mbIdx, boolean mb_field_decoding_flag, MBType prevMbType, boolean ref0) {
        boolean noSubMBLessThen8x8;
        MBType curMBType;
        int mbX = this.mapper.getMbX(mbIdx);
        int mbY = this.mapper.getMbY(mbIdx);
        int mbAddr = this.mapper.getAddress(mbIdx);
        boolean leftAvailable = this.mapper.leftAvailable(mbIdx);
        boolean topAvailable = this.mapper.topAvailable(mbIdx);
        boolean topLeftAvailable = this.mapper.topLeftAvailable(mbIdx);
        boolean topRightAvailable = this.mapper.topRightAvailable(mbIdx);
        int[][][] x = (int[][][]) Array.newInstance(Integer.TYPE, 2, 16, 3);
        H264Const.PartPred[] pp = new H264Const.PartPred[4];
        for (int i = 0; i < 16; i++) {
            int[] iArr = x[0][i];
            x[1][i][2] = -1;
            iArr[2] = -1;
        }
        Picture mb1 = Picture.create(16, 16, this.chromaFormat);
        if (sliceType == SliceType.P) {
            noSubMBLessThen8x8 = predict8x8P(reader, references[0], mb1, ref0, mbX, mbY, leftAvailable, topAvailable, topLeftAvailable, topRightAvailable, x, pp);
            curMBType = MBType.P_8x8;
        } else {
            noSubMBLessThen8x8 = predict8x8B(reader, references, mb1, ref0, mbX, mbY, leftAvailable, topAvailable, topLeftAvailable, topRightAvailable, x, pp);
            curMBType = MBType.B_8x8;
        }
        int codedBlockPattern = readCodedBlockPatternInter(reader, leftAvailable, topAvailable, this.leftCBPLuma | (this.leftCBPChroma << 4), this.topCBPLuma[mbX] | (this.topCBPChroma[mbX] << 4), this.leftMBType, this.topMBType[mbX]);
        int cbpLuma = codedBlockPattern & 15;
        int cbpChroma = codedBlockPattern >> 4;
        boolean transform8x8Used = false;
        if (this.transform8x8 && cbpLuma != 0 && noSubMBLessThen8x8) {
            transform8x8Used = readTransform8x8Flag(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.tf8x8Left, this.tf8x8Top[mbX]);
        }
        if (cbpLuma > 0 || cbpChroma > 0) {
            this.f1454qp = ((this.f1454qp + readMBQpDelta(reader, prevMbType)) + 52) % 52;
        }
        this.mbQps[0][mbAddr] = this.f1454qp;
        residualLuma(reader, leftAvailable, topAvailable, mbX, mbY, mb, codedBlockPattern, curMBType, transform8x8Used, this.tf8x8Left, this.tf8x8Top[mbX]);
        saveMvs(x, mbX, mbY);
        decodeChromaInter(reader, codedBlockPattern >> 4, references, x, pp, leftAvailable, topAvailable, mbX, mbY, mbAddr, this.f1454qp, mb, mb1);
        mergeResidual(mb, mb1);
        collectPredictors(mb, mbX);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        this.leftMBType = curMBType;
        mBTypeArr2[mbX] = curMBType;
        mBTypeArr[mbAddr] = curMBType;
        int[] iArr2 = this.topCBPLuma;
        this.leftCBPLuma = cbpLuma;
        iArr2[mbX] = cbpLuma;
        int[] iArr3 = this.topCBPChroma;
        this.leftCBPChroma = cbpChroma;
        iArr3[mbX] = cbpChroma;
        this.tf8x8Top[mbX] = transform8x8Used;
        this.tf8x8Left = transform8x8Used;
        this.tr8x8Used[mbAddr] = transform8x8Used;
    }

    private boolean predict8x8P(BitReader reader, Picture[] references, Picture mb, boolean ref0, int mbX, int mbY, boolean leftAvailable, boolean topAvailable, boolean tlAvailable, boolean topRightAvailable, int[][][] x, H264Const.PartPred[] pp) {
        int[] subMbTypes = new int[4];
        for (int i = 0; i < 4; i++) {
            subMbTypes[i] = readSubMBTypeP(reader);
        }
        Arrays.fill(pp, H264Const.PartPred.L0);
        int blk8x8X = mbX << 1;
        int[] refIdx = new int[4];
        if (this.numRef[0] > 1 && !ref0) {
            refIdx[0] = readRefIdx(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, mbX, 0, 0, 2, 2, 0);
            refIdx[1] = readRefIdx(reader, true, topAvailable, MBType.P_8x8, this.topMBType[mbX], H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, mbX, 2, 0, 2, 2, 0);
            refIdx[2] = readRefIdx(reader, leftAvailable, true, this.leftMBType, MBType.P_8x8, H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, mbX, 0, 2, 2, 2, 0);
            refIdx[3] = readRefIdx(reader, true, true, MBType.P_8x8, MBType.P_8x8, H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, mbX, 2, 2, 2, 2, 0);
        }
        decodeSubMb8x8(reader, subMbTypes[0], references, mbX << 6, mbY << 6, x[0], this.mvTopLeft[0], this.mvTop[0][mbX << 2], this.mvTop[0][(mbX << 2) + 1], this.mvTop[0][(mbX << 2) + 2], this.mvLeft[0][0], this.mvLeft[0][1], tlAvailable, topAvailable, topAvailable, leftAvailable, x[0][0], x[0][1], x[0][4], x[0][5], refIdx[0], mb, 0, 0, 0, mbX, this.leftMBType, this.topMBType[mbX], MBType.P_8x8, H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, 0);
        decodeSubMb8x8(reader, subMbTypes[1], references, (mbX << 6) + 32, mbY << 6, x[0], this.mvTop[0][(mbX << 2) + 1], this.mvTop[0][(mbX << 2) + 2], this.mvTop[0][(mbX << 2) + 3], this.mvTop[0][(mbX << 2) + 4], x[0][1], x[0][5], topAvailable, topAvailable, topRightAvailable, true, x[0][2], x[0][3], x[0][6], x[0][7], refIdx[1], mb, 8, 2, 0, mbX, MBType.P_8x8, this.topMBType[mbX], MBType.P_8x8, H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, 0);
        decodeSubMb8x8(reader, subMbTypes[2], references, mbX << 6, (mbY << 6) + 32, x[0], this.mvLeft[0][1], x[0][4], x[0][5], x[0][6], this.mvLeft[0][2], this.mvLeft[0][3], leftAvailable, true, true, leftAvailable, x[0][8], x[0][9], x[0][12], x[0][13], refIdx[2], mb, 128, 0, 2, mbX, this.leftMBType, MBType.P_8x8, MBType.P_8x8, H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, 0);
        decodeSubMb8x8(reader, subMbTypes[3], references, (mbX << 6) + 32, (mbY << 6) + 32, x[0], x[0][5], x[0][6], x[0][7], null, x[0][9], x[0][13], true, true, false, true, x[0][10], x[0][11], x[0][14], x[0][15], refIdx[3], mb, 136, 2, 2, mbX, MBType.P_8x8, MBType.P_8x8, MBType.P_8x8, H264Const.PartPred.L0, H264Const.PartPred.L0, H264Const.PartPred.L0, 0);
        savePrediction8x8(mbX, x[0], 0);
        H264Const.PartPred[] partPredArr = this.predModeLeft;
        H264Const.PartPred[] partPredArr2 = this.predModeLeft;
        H264Const.PartPred[] partPredArr3 = this.predModeTop;
        H264Const.PartPred partPred = H264Const.PartPred.L0;
        this.predModeTop[blk8x8X + 1] = partPred;
        partPredArr3[blk8x8X] = partPred;
        partPredArr2[1] = partPred;
        partPredArr[0] = partPred;
        return subMbTypes[0] == 0 && subMbTypes[1] == 0 && subMbTypes[2] == 0 && subMbTypes[3] == 0;
    }

    private boolean predict8x8B(BitReader reader, Frame[][] refs, Picture mb, boolean ref0, int mbX, int mbY, boolean leftAvailable, boolean topAvailable, boolean tlAvailable, boolean topRightAvailable, int[][][] x, H264Const.PartPred[] p) {
        int[] subMbTypes = new int[4];
        for (int i = 0; i < 4; i++) {
            subMbTypes[i] = readSubMBTypeB(reader);
            p[i] = H264Const.bPartPredModes[subMbTypes[i]];
        }
        int[][] refIdx = (int[][]) Array.newInstance(Integer.TYPE, 2, 4);
        for (int list = 0; list < 2; list++) {
            if (this.numRef[list] > 1) {
                if (p[0].usesList(list)) {
                    refIdx[list][0] = readRefIdx(reader, leftAvailable, topAvailable, this.leftMBType, this.topMBType[mbX], this.predModeLeft[0], this.predModeTop[mbX << 1], p[0], mbX, 0, 0, 2, 2, list);
                }
                if (p[1].usesList(list)) {
                    refIdx[list][1] = readRefIdx(reader, true, topAvailable, MBType.B_8x8, this.topMBType[mbX], p[0], this.predModeTop[(mbX << 1) + 1], p[1], mbX, 2, 0, 2, 2, list);
                }
                if (p[2].usesList(list)) {
                    refIdx[list][2] = readRefIdx(reader, leftAvailable, true, this.leftMBType, MBType.B_8x8, this.predModeLeft[1], p[0], p[2], mbX, 0, 2, 2, 2, list);
                }
                if (p[3].usesList(list)) {
                    refIdx[list][3] = readRefIdx(reader, true, true, MBType.B_8x8, MBType.B_8x8, p[2], p[1], p[3], mbX, 2, 2, 2, 2, list);
                }
            }
        }
        Picture[] mbb = {Picture.create(16, 16, this.chromaFormat), Picture.create(16, 16, this.chromaFormat)};
        H264Const.PartPred[] _pp = new H264Const.PartPred[4];
        for (int i2 = 0; i2 < 4; i2++) {
            if (p[i2] == H264Const.PartPred.Direct) {
                predictBDirect(refs, mbX, mbY, leftAvailable, topAvailable, tlAvailable, topRightAvailable, x, _pp, mb, H264Const.ARRAY[i2]);
            }
        }
        int blk8x8X = mbX << 1;
        for (int list2 = 0; list2 < 2; list2++) {
            if (p[0].usesList(list2)) {
                decodeSubMb8x8(reader, H264Const.bSubMbTypes[subMbTypes[0]], refs[list2], mbX << 6, mbY << 6, x[list2], this.mvTopLeft[list2], this.mvTop[list2][mbX << 2], this.mvTop[list2][(mbX << 2) + 1], this.mvTop[list2][(mbX << 2) + 2], this.mvLeft[list2][0], this.mvLeft[list2][1], tlAvailable, topAvailable, topAvailable, leftAvailable, x[list2][0], x[list2][1], x[list2][4], x[list2][5], refIdx[list2][0], mbb[list2], 0, 0, 0, mbX, this.leftMBType, this.topMBType[mbX], MBType.B_8x8, this.predModeLeft[0], this.predModeTop[blk8x8X], p[0], list2);
            }
            if (p[1].usesList(list2)) {
                decodeSubMb8x8(reader, H264Const.bSubMbTypes[subMbTypes[1]], refs[list2], (mbX << 6) + 32, mbY << 6, x[list2], this.mvTop[list2][(mbX << 2) + 1], this.mvTop[list2][(mbX << 2) + 2], this.mvTop[list2][(mbX << 2) + 3], this.mvTop[list2][(mbX << 2) + 4], x[list2][1], x[list2][5], topAvailable, topAvailable, topRightAvailable, true, x[list2][2], x[list2][3], x[list2][6], x[list2][7], refIdx[list2][1], mbb[list2], 8, 2, 0, mbX, MBType.B_8x8, this.topMBType[mbX], MBType.B_8x8, p[0], this.predModeTop[blk8x8X + 1], p[1], list2);
            }
            if (p[2].usesList(list2)) {
                decodeSubMb8x8(reader, H264Const.bSubMbTypes[subMbTypes[2]], refs[list2], mbX << 6, (mbY << 6) + 32, x[list2], this.mvLeft[list2][1], x[list2][4], x[list2][5], x[list2][6], this.mvLeft[list2][2], this.mvLeft[list2][3], leftAvailable, true, true, leftAvailable, x[list2][8], x[list2][9], x[list2][12], x[list2][13], refIdx[list2][2], mbb[list2], 128, 0, 2, mbX, this.leftMBType, MBType.B_8x8, MBType.B_8x8, this.predModeLeft[1], p[0], p[2], list2);
            }
            if (p[3].usesList(list2)) {
                decodeSubMb8x8(reader, H264Const.bSubMbTypes[subMbTypes[3]], refs[list2], (mbX << 6) + 32, (mbY << 6) + 32, x[list2], x[list2][5], x[list2][6], x[list2][7], null, x[list2][9], x[list2][13], true, true, false, true, x[list2][10], x[list2][11], x[list2][14], x[list2][15], refIdx[list2][3], mbb[list2], 136, 2, 2, mbX, MBType.B_8x8, MBType.B_8x8, MBType.B_8x8, p[2], p[1], p[3], list2);
            }
        }
        for (int i3 = 0; i3 < 4; i3++) {
            int blk4x4 = H264Const.BLK8x8_BLOCKS[i3][0];
            this.prediction.mergePrediction(x[0][blk4x4][2], x[1][blk4x4][2], p[i3], 0, mbb[0].getPlaneData(0), mbb[1].getPlaneData(0), H264Const.BLK_8x8_MB_OFF_LUMA[i3], 16, 8, 8, mb.getPlaneData(0), refs, this.thisFrame);
        }
        this.predModeLeft[0] = p[1];
        this.predModeTop[blk8x8X] = p[2];
        H264Const.PartPred[] partPredArr = this.predModeLeft;
        H264Const.PartPred partPred = p[3];
        this.predModeTop[blk8x8X + 1] = partPred;
        partPredArr[1] = partPred;
        savePrediction8x8(mbX, x[0], 0);
        savePrediction8x8(mbX, x[1], 1);
        for (int i4 = 0; i4 < 4; i4++) {
            if (p[i4] == H264Const.PartPred.Direct) {
                p[i4] = _pp[i4];
            }
        }
        return H264Const.bSubMbTypes[subMbTypes[0]] == 0 && H264Const.bSubMbTypes[subMbTypes[1]] == 0 && H264Const.bSubMbTypes[subMbTypes[2]] == 0 && H264Const.bSubMbTypes[subMbTypes[3]] == 0;
    }

    private void decodeMBBiDirect(int mbIdx, BitReader reader, boolean field, MBType prevMbType, Picture mb, Frame[][] references) {
        int mbX = this.mapper.getMbX(mbIdx);
        int mbY = this.mapper.getMbY(mbIdx);
        int mbAddr = this.mapper.getAddress(mbIdx);
        boolean lAvb = this.mapper.leftAvailable(mbIdx);
        boolean tAvb = this.mapper.topAvailable(mbIdx);
        boolean tlAvb = this.mapper.topLeftAvailable(mbIdx);
        boolean trAvb = this.mapper.topRightAvailable(mbIdx);
        int[][][] x = (int[][][]) Array.newInstance(Integer.TYPE, 2, 16, 3);
        for (int i = 0; i < 16; i++) {
            int[] iArr = x[0][i];
            x[1][i][2] = -1;
            iArr[2] = -1;
        }
        Picture mb1 = Picture.create(16, 16, this.chromaFormat);
        H264Const.PartPred[] pp = new H264Const.PartPred[4];
        predictBDirect(references, mbX, mbY, lAvb, tAvb, tlAvb, trAvb, x, pp, mb1, H264Const.identityMapping4);
        int codedBlockPattern = readCodedBlockPatternInter(reader, lAvb, tAvb, this.leftCBPLuma | (this.leftCBPChroma << 4), this.topCBPLuma[mbX] | (this.topCBPChroma[mbX] << 4), this.leftMBType, this.topMBType[mbX]);
        int cbpLuma = codedBlockPattern & 15;
        int cbpChroma = codedBlockPattern >> 4;
        boolean transform8x8Used = false;
        if (this.transform8x8 && cbpLuma != 0 && this.activeSps.direct_8x8_inference_flag) {
            transform8x8Used = readTransform8x8Flag(reader, lAvb, tAvb, this.leftMBType, this.topMBType[mbX], this.tf8x8Left, this.tf8x8Top[mbX]);
        }
        if (cbpLuma > 0 || cbpChroma > 0) {
            this.f1454qp = ((this.f1454qp + readMBQpDelta(reader, prevMbType)) + 52) % 52;
        }
        this.mbQps[0][mbAddr] = this.f1454qp;
        residualLuma(reader, lAvb, tAvb, mbX, mbY, mb, codedBlockPattern, MBType.P_8x8, transform8x8Used, this.tf8x8Left, this.tf8x8Top[mbX]);
        savePrediction8x8(mbX, x[0], 0);
        savePrediction8x8(mbX, x[1], 1);
        saveMvs(x, mbX, mbY);
        decodeChromaInter(reader, codedBlockPattern >> 4, references, x, pp, lAvb, tAvb, mbX, mbY, mbAddr, this.f1454qp, mb, mb1);
        mergeResidual(mb, mb1);
        collectPredictors(mb, mbX);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        MBType mBType = MBType.B_Direct_16x16;
        this.leftMBType = mBType;
        mBTypeArr2[mbX] = mBType;
        mBTypeArr[mbAddr] = mBType;
        int[] iArr2 = this.topCBPLuma;
        this.leftCBPLuma = cbpLuma;
        iArr2[mbX] = cbpLuma;
        int[] iArr3 = this.topCBPChroma;
        this.leftCBPChroma = cbpChroma;
        iArr3[mbX] = cbpChroma;
        this.tf8x8Top[mbX] = transform8x8Used;
        this.tf8x8Left = transform8x8Used;
        this.tr8x8Used[mbAddr] = transform8x8Used;
        H264Const.PartPred[] partPredArr = this.predModeLeft;
        H264Const.PartPred[] partPredArr2 = this.predModeLeft;
        H264Const.PartPred partPred = H264Const.PartPred.Direct;
        partPredArr2[1] = partPred;
        partPredArr[0] = partPred;
        this.predModeTop[(mbX << 1) + 1] = partPred;
        this.predModeTop[mbX << 1] = partPred;
    }

    private int readSubMBTypeP(BitReader reader) {
        return !this.activePps.entropy_coding_mode_flag ? CAVLCReader.readUE(reader, "SUB: sub_mb_type") : this.cabac.readSubMbTypeP(this.mDecoder);
    }

    private int readSubMBTypeB(BitReader reader) {
        return !this.activePps.entropy_coding_mode_flag ? CAVLCReader.readUE(reader, "SUB: sub_mb_type") : this.cabac.readSubMbTypeB(this.mDecoder);
    }

    private void savePrediction8x8(int mbX, int[][] x, int list) {
        copyVect(this.mvTopLeft[list], this.mvTop[list][(mbX << 2) + 3]);
        copyVect(this.mvLeft[list][0], x[3]);
        copyVect(this.mvLeft[list][1], x[7]);
        copyVect(this.mvLeft[list][2], x[11]);
        copyVect(this.mvLeft[list][3], x[15]);
        copyVect(this.mvTop[list][mbX << 2], x[12]);
        copyVect(this.mvTop[list][(mbX << 2) + 1], x[13]);
        copyVect(this.mvTop[list][(mbX << 2) + 2], x[14]);
        copyVect(this.mvTop[list][(mbX << 2) + 3], x[15]);
    }

    private void copyVect(int[] to, int[] from) {
        to[0] = from[0];
        to[1] = from[1];
        to[2] = from[2];
    }

    private void decodeSubMb8x8(BitReader reader, int subMbType, Picture[] references, int offX, int offY, int[][] x, int[] tl, int[] t0, int[] t1, int[] tr, int[] l0, int[] l1, boolean tlAvb, boolean tAvb, boolean trAvb, boolean lAvb, int[] x00, int[] x01, int[] x10, int[] x11, int refIdx, Picture mb, int off, int blk8x8X, int blk8x8Y, int mbX, MBType leftMBType, MBType topMBType, MBType curMBType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred partPred, int list) {
        x11[2] = refIdx;
        x10[2] = refIdx;
        x01[2] = refIdx;
        x00[2] = refIdx;
        switch (subMbType) {
            case 0:
                decodeSub8x8(reader, references, offX, offY, tl, t0, tr, l0, tlAvb, tAvb, trAvb, lAvb, x00, x01, x10, x11, refIdx, mb, off, blk8x8X, blk8x8Y, mbX, leftMBType, topMBType, curMBType, leftPred, topPred, partPred, list);
                return;
            case 1:
                decodeSub8x4(reader, references, offX, offY, tl, t0, tr, l0, l1, tlAvb, tAvb, trAvb, lAvb, x00, x01, x10, x11, refIdx, mb, off, blk8x8X, blk8x8Y, mbX, leftMBType, topMBType, curMBType, leftPred, topPred, partPred, list);
                return;
            case 2:
                decodeSub4x8(reader, references, offX, offY, tl, t0, t1, tr, l0, tlAvb, tAvb, trAvb, lAvb, x00, x01, x10, x11, refIdx, mb, off, blk8x8X, blk8x8Y, mbX, leftMBType, topMBType, curMBType, leftPred, topPred, partPred, list);
                return;
            case 3:
                decodeSub4x4(reader, references, offX, offY, tl, t0, t1, tr, l0, l1, tlAvb, tAvb, trAvb, lAvb, x00, x01, x10, x11, refIdx, mb, off, blk8x8X, blk8x8Y, mbX, leftMBType, topMBType, curMBType, leftPred, topPred, partPred, list);
                return;
            default:
                return;
        }
    }

    private void decodeSub8x8(BitReader reader, Picture[] references, int offX, int offY, int[] tl, int[] t0, int[] tr, int[] l0, boolean tlAvb, boolean tAvb, boolean trAvb, boolean lAvb, int[] x00, int[] x01, int[] x10, int[] x11, int refIdx, Picture mb, int off, int blk8x8X, int blk8x8Y, int mbX, MBType leftMBType, MBType topMBType, MBType curMBType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred partPred, int list) {
        int mvdX = readMVD(reader, 0, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 2, 2, list);
        int mvdY = readMVD(reader, 1, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 2, 2, list);
        int mvpX = calcMVPredictionMedian(l0, t0, tr, tl, lAvb, tAvb, trAvb, tlAvb, refIdx, 0);
        int mvpY = calcMVPredictionMedian(l0, t0, tr, tl, lAvb, tAvb, trAvb, tlAvb, refIdx, 1);
        int i = mvdX + mvpX;
        x11[0] = i;
        x10[0] = i;
        x01[0] = i;
        x00[0] = i;
        int i2 = mvpY + mvdY;
        x11[1] = i2;
        x10[1] = i2;
        x01[1] = i2;
        x00[1] = i2;
        debugPrint("MVP: (" + mvpX + ", " + mvpY + "), MVD: (" + mvdX + ", " + mvdY + "), MV: (" + x00[0] + "," + x00[1] + "," + refIdx + ")");
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off, offX + x00[0], offY + x00[1], 8, 8);
    }

    private void decodeSub8x4(BitReader reader, Picture[] references, int offX, int offY, int[] tl, int[] t0, int[] tr, int[] l0, int[] l1, boolean tlAvb, boolean tAvb, boolean trAvb, boolean lAvb, int[] x00, int[] x01, int[] x10, int[] x11, int refIdx, Picture mb, int off, int blk8x8X, int blk8x8Y, int mbX, MBType leftMBType, MBType topMBType, MBType curMBType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred partPred, int list) {
        int mvdX1 = readMVD(reader, 0, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 2, 1, list);
        int mvdY1 = readMVD(reader, 1, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 2, 1, list);
        int mvpX1 = calcMVPredictionMedian(l0, t0, tr, tl, lAvb, tAvb, trAvb, tlAvb, refIdx, 0);
        int mvpY1 = calcMVPredictionMedian(l0, t0, tr, tl, lAvb, tAvb, trAvb, tlAvb, refIdx, 1);
        int i = mvdX1 + mvpX1;
        x01[0] = i;
        x00[0] = i;
        int i2 = mvdY1 + mvpY1;
        x01[1] = i2;
        x00[1] = i2;
        debugPrint("MVP: (" + mvpX1 + ", " + mvpY1 + "), MVD: (" + mvdX1 + ", " + mvdY1 + "), MV: (" + x00[0] + "," + x00[1] + "," + refIdx + ")");
        int mvdX2 = readMVD(reader, 0, lAvb, true, leftMBType, curMBType, leftPred, partPred, partPred, mbX, blk8x8X, blk8x8Y + 1, 2, 1, list);
        int mvdY2 = readMVD(reader, 1, lAvb, true, leftMBType, curMBType, leftPred, partPred, partPred, mbX, blk8x8X, blk8x8Y + 1, 2, 1, list);
        int mvpX2 = calcMVPredictionMedian(l1, x00, NULL_VECTOR, l0, lAvb, true, false, lAvb, refIdx, 0);
        int mvpY2 = calcMVPredictionMedian(l1, x00, NULL_VECTOR, l0, lAvb, true, false, lAvb, refIdx, 1);
        int i3 = mvdX2 + mvpX2;
        x11[0] = i3;
        x10[0] = i3;
        int i4 = mvdY2 + mvpY2;
        x11[1] = i4;
        x10[1] = i4;
        debugPrint("MVP: (" + mvpX2 + ", " + mvpY2 + "), MVD: (" + mvdX2 + ", " + mvdY2 + "), MV: (" + x10[0] + "," + x10[1] + "," + refIdx + ")");
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off, offX + x00[0], offY + x00[1], 8, 4);
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off + (mb.getWidth() * 4), offX + x10[0], x10[1] + offY + 16, 8, 4);
    }

    private void decodeSub4x8(BitReader reader, Picture[] references, int offX, int offY, int[] tl, int[] t0, int[] t1, int[] tr, int[] l0, boolean tlAvb, boolean tAvb, boolean trAvb, boolean lAvb, int[] x00, int[] x01, int[] x10, int[] x11, int refIdx, Picture mb, int off, int blk8x8X, int blk8x8Y, int mbX, MBType leftMBType, MBType topMBType, MBType curMBType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred partPred, int list) {
        int mvdX1 = readMVD(reader, 0, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 1, 2, list);
        int mvdY1 = readMVD(reader, 1, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 1, 2, list);
        int mvpX1 = calcMVPredictionMedian(l0, t0, t1, tl, lAvb, tAvb, tAvb, tlAvb, refIdx, 0);
        int mvpY1 = calcMVPredictionMedian(l0, t0, t1, tl, lAvb, tAvb, tAvb, tlAvb, refIdx, 1);
        int i = mvdX1 + mvpX1;
        x10[0] = i;
        x00[0] = i;
        int i2 = mvdY1 + mvpY1;
        x10[1] = i2;
        x00[1] = i2;
        debugPrint("MVP: (" + mvpX1 + ", " + mvpY1 + "), MVD: (" + mvdX1 + ", " + mvdY1 + "), MV: (" + x00[0] + "," + x00[1] + "," + refIdx + ")");
        int mvdX2 = readMVD(reader, 0, true, tAvb, curMBType, topMBType, partPred, topPred, partPred, mbX, blk8x8X + 1, blk8x8Y, 1, 2, list);
        int mvdY2 = readMVD(reader, 1, true, tAvb, curMBType, topMBType, partPred, topPred, partPred, mbX, blk8x8X + 1, blk8x8Y, 1, 2, list);
        int mvpX2 = calcMVPredictionMedian(x00, t1, tr, t0, true, tAvb, trAvb, tAvb, refIdx, 0);
        int mvpY2 = calcMVPredictionMedian(x00, t1, tr, t0, true, tAvb, trAvb, tAvb, refIdx, 1);
        int i3 = mvdX2 + mvpX2;
        x11[0] = i3;
        x01[0] = i3;
        int i4 = mvdY2 + mvpY2;
        x11[1] = i4;
        x01[1] = i4;
        debugPrint("MVP: (" + mvpX2 + ", " + mvpY2 + "), MVD: (" + mvdX2 + ", " + mvdY2 + "), MV: (" + x01[0] + "," + x01[1] + "," + refIdx + ")");
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off, offX + x00[0], offY + x00[1], 4, 8);
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off + 4, x01[0] + offX + 16, offY + x01[1], 4, 8);
    }

    private void decodeSub4x4(BitReader reader, Picture[] references, int offX, int offY, int[] tl, int[] t0, int[] t1, int[] tr, int[] l0, int[] l1, boolean tlAvb, boolean tAvb, boolean trAvb, boolean lAvb, int[] x00, int[] x01, int[] x10, int[] x11, int refIdx, Picture mb, int off, int blk8x8X, int blk8x8Y, int mbX, MBType leftMBType, MBType topMBType, MBType curMBType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred partPred, int list) {
        int mvdX1 = readMVD(reader, 0, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 1, 1, list);
        int mvdY1 = readMVD(reader, 1, lAvb, tAvb, leftMBType, topMBType, leftPred, topPred, partPred, mbX, blk8x8X, blk8x8Y, 1, 1, list);
        int mvpX1 = calcMVPredictionMedian(l0, t0, t1, tl, lAvb, tAvb, tAvb, tlAvb, refIdx, 0);
        int mvpY1 = calcMVPredictionMedian(l0, t0, t1, tl, lAvb, tAvb, tAvb, tlAvb, refIdx, 1);
        x00[0] = mvdX1 + mvpX1;
        x00[1] = mvdY1 + mvpY1;
        debugPrint("MVP: (" + mvpX1 + ", " + mvpY1 + "), MVD: (" + mvdX1 + ", " + mvdY1 + "), MV: (" + x00[0] + "," + x00[1] + "," + refIdx + ")");
        int mvdX2 = readMVD(reader, 0, true, tAvb, curMBType, topMBType, partPred, topPred, partPred, mbX, blk8x8X + 1, blk8x8Y, 1, 1, list);
        int mvdY2 = readMVD(reader, 1, true, tAvb, curMBType, topMBType, partPred, topPred, partPred, mbX, blk8x8X + 1, blk8x8Y, 1, 1, list);
        int mvpX2 = calcMVPredictionMedian(x00, t1, tr, t0, true, tAvb, trAvb, tAvb, refIdx, 0);
        int mvpY2 = calcMVPredictionMedian(x00, t1, tr, t0, true, tAvb, trAvb, tAvb, refIdx, 1);
        x01[0] = mvdX2 + mvpX2;
        x01[1] = mvdY2 + mvpY2;
        debugPrint("MVP: (" + mvpX2 + ", " + mvpY2 + "), MVD: (" + mvdX2 + ", " + mvdY2 + "), MV: (" + x01[0] + "," + x01[1] + "," + refIdx + ")");
        int mvdX3 = readMVD(reader, 0, lAvb, true, leftMBType, curMBType, leftPred, partPred, partPred, mbX, blk8x8X, blk8x8Y + 1, 1, 1, list);
        int mvdY3 = readMVD(reader, 1, lAvb, true, leftMBType, curMBType, leftPred, partPred, partPred, mbX, blk8x8X, blk8x8Y + 1, 1, 1, list);
        int mvpX3 = calcMVPredictionMedian(l1, x00, x01, l0, lAvb, true, true, lAvb, refIdx, 0);
        int mvpY3 = calcMVPredictionMedian(l1, x00, x01, l0, lAvb, true, true, lAvb, refIdx, 1);
        x10[0] = mvdX3 + mvpX3;
        x10[1] = mvdY3 + mvpY3;
        debugPrint("MVP: (" + mvpX3 + ", " + mvpY3 + "), MVD: (" + mvdX3 + ", " + mvdY3 + "), MV: (" + x10[0] + "," + x10[1] + "," + refIdx + ")");
        int mvdX4 = readMVD(reader, 0, true, true, curMBType, curMBType, partPred, partPred, partPred, mbX, blk8x8X + 1, blk8x8Y + 1, 1, 1, list);
        int mvdY4 = readMVD(reader, 1, true, true, curMBType, curMBType, partPred, partPred, partPred, mbX, blk8x8X + 1, blk8x8Y + 1, 1, 1, list);
        int mvpX4 = calcMVPredictionMedian(x10, x01, NULL_VECTOR, x00, true, true, false, true, refIdx, 0);
        int mvpY4 = calcMVPredictionMedian(x10, x01, NULL_VECTOR, x00, true, true, false, true, refIdx, 1);
        x11[0] = mvdX4 + mvpX4;
        x11[1] = mvdY4 + mvpY4;
        debugPrint("MVP: (" + mvpX4 + ", " + mvpY4 + "), MVD: (" + mvdX4 + ", " + mvdY4 + "), MV: (" + x11[0] + "," + x11[1] + "," + refIdx + ")");
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off, offX + x00[0], offY + x00[1], 4, 4);
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off + 4, x01[0] + offX + 16, offY + x01[1], 4, 4);
        BlockInterpolator.getBlockLuma(references[refIdx], mb, off + (mb.getWidth() * 4), offX + x10[0], x10[1] + offY + 16, 4, 4);
        BlockInterpolator.getBlockLuma(references[refIdx], mb, (mb.getWidth() * 4) + off + 4, x11[0] + offX + 16, x11[1] + offY + 16, 4, 4);
    }

    private int readMVD(BitReader reader, int comp, boolean leftAvailable, boolean topAvailable, MBType leftType, MBType topType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred curPred, int mbX, int partX, int partY, int partW, int partH, int list) {
        return !this.activePps.entropy_coding_mode_flag ? CAVLCReader.readSE(reader, "mvd_l0_x") : this.cabac.readMVD(this.mDecoder, comp, leftAvailable, topAvailable, leftType, topType, leftPred, topPred, curPred, mbX, partX, partY, partW, partH, list);
    }

    public void decodeChromaInter(BitReader reader, int pattern, Frame[][] refs, int[][][] x, H264Const.PartPred[] predType, boolean leftAvailable, boolean topAvailable, int mbX, int mbY, int mbAddr, int qp, Picture mb, Picture mb1) {
        predictChromaInter(refs, x, mbX << 3, mbY << 3, 1, mb1, predType);
        predictChromaInter(refs, x, mbX << 3, mbY << 3, 2, mb1, predType);
        int qp1 = calcQpChroma(qp, this.chromaQpOffset[0]);
        int qp2 = calcQpChroma(qp, this.chromaQpOffset[1]);
        decodeChromaResidual(reader, leftAvailable, topAvailable, mbX, mbY, pattern, mb, qp1, qp2, MBType.P_16x16);
        this.mbQps[1][mbAddr] = qp1;
        this.mbQps[2][mbAddr] = qp2;
    }

    private void saveMvs(int[][][] x, int mbX, int mbY) {
        int j = 0;
        int blkOffY = mbY << 2;
        int blkInd = 0;
        while (j < 4) {
            int i = 0;
            int blkOffX = mbX << 2;
            while (i < 4) {
                this.mvs[0][blkOffY][blkOffX] = x[0][blkInd];
                this.mvs[1][blkOffY][blkOffX] = x[1][blkInd];
                i++;
                blkOffX++;
                blkInd++;
            }
            j++;
            blkOffY++;
        }
    }

    public void predictChromaInter(Frame[][] refs, int[][][] vectors, int x, int y, int comp, Picture mb, H264Const.PartPred[] predType) {
        Picture[] mbb = {Picture.create(16, 16, this.chromaFormat), Picture.create(16, 16, this.chromaFormat)};
        for (int blk8x8 = 0; blk8x8 < 4; blk8x8++) {
            for (int list = 0; list < 2; list++) {
                if (predType[blk8x8].usesList(list)) {
                    for (int blk4x4 = 0; blk4x4 < 4; blk4x4++) {
                        int i = H264Const.BLK_INV_MAP[(blk8x8 << 2) + blk4x4];
                        int[] mv = vectors[list][i];
                        Frame frame = refs[list][mv[2]];
                        int blkPox = (i & 3) << 1;
                        int blkPoy = (i >> 2) << 1;
                        int xx = ((x + blkPox) << 3) + mv[0];
                        int yy = ((y + blkPoy) << 3) + mv[1];
                        BlockInterpolator.getBlockChroma(frame.getPlaneData(comp), frame.getPlaneWidth(comp), frame.getPlaneHeight(comp), mbb[list].getPlaneData(comp), (mb.getPlaneWidth(comp) * blkPoy) + blkPox, mb.getPlaneWidth(comp), xx, yy, 2, 2);
                    }
                }
            }
            int blk4x42 = H264Const.BLK8x8_BLOCKS[blk8x8][0];
            this.prediction.mergePrediction(vectors[0][blk4x42][2], vectors[1][blk4x42][2], predType[blk8x8], comp, mbb[0].getPlaneData(comp), mbb[1].getPlaneData(comp), H264Const.BLK_8x8_MB_OFF_CHROMA[blk8x8], mb.getPlaneWidth(comp), 4, 4, mb.getPlaneData(comp), refs, this.thisFrame);
        }
    }

    public void decodeMBlockIPCM(BitReader reader, int mbIndex, Picture mb) {
        int mbX = this.mapper.getMbX(mbIndex);
        reader.align();
        int[] samplesLuma = new int[256];
        for (int i = 0; i < 256; i++) {
            samplesLuma[i] = reader.readNBit(8);
        }
        int MbWidthC = 16 >> this.chromaFormat.compWidth[1];
        int MbHeightC = 16 >> this.chromaFormat.compHeight[1];
        int[] samplesChroma = new int[MbWidthC * 2 * MbHeightC];
        for (int i2 = 0; i2 < MbWidthC * 2 * MbHeightC; i2++) {
            samplesChroma[i2] = reader.readNBit(8);
        }
        collectPredictors(mb, mbX);
    }

    public void decodeSkip(Frame[][] refs, int mbIdx, Picture mb, SliceType sliceType) {
        int mbX = this.mapper.getMbX(mbIdx);
        int mbY = this.mapper.getMbY(mbIdx);
        int mbAddr = this.mapper.getAddress(mbIdx);
        int[][][] x = (int[][][]) Array.newInstance(Integer.TYPE, 2, 16, 3);
        H264Const.PartPred[] pp = new H264Const.PartPred[4];
        for (int i = 0; i < 16; i++) {
            int[] iArr = x[0][i];
            x[1][i][2] = -1;
            iArr[2] = -1;
        }
        if (sliceType == SliceType.P) {
            predictPSkip(refs, mbX, mbY, this.mapper.leftAvailable(mbIdx), this.mapper.topAvailable(mbIdx), this.mapper.topLeftAvailable(mbIdx), this.mapper.topRightAvailable(mbIdx), x, mb);
            Arrays.fill(pp, H264Const.PartPred.L0);
        } else {
            predictBDirect(refs, mbX, mbY, this.mapper.leftAvailable(mbIdx), this.mapper.topAvailable(mbIdx), this.mapper.topLeftAvailable(mbIdx), this.mapper.topRightAvailable(mbIdx), x, pp, mb, H264Const.identityMapping4);
            savePrediction8x8(mbX, x[0], 0);
            savePrediction8x8(mbX, x[1], 1);
        }
        decodeChromaSkip(refs, x, pp, mbX, mbY, mb);
        collectPredictors(mb, mbX);
        saveMvs(x, mbX, mbY);
        MBType[] mBTypeArr = this.mbTypes;
        MBType[] mBTypeArr2 = this.topMBType;
        this.leftMBType = null;
        mBTypeArr2[mbX] = null;
        mBTypeArr[mbAddr] = null;
        this.mbQps[0][mbAddr] = this.f1454qp;
        this.mbQps[1][mbAddr] = calcQpChroma(this.f1454qp, this.chromaQpOffset[0]);
        this.mbQps[2][mbAddr] = calcQpChroma(this.f1454qp, this.chromaQpOffset[1]);
    }

    public void predictBDirect(Frame[][] refs, int mbX, int mbY, boolean lAvb, boolean tAvb, boolean tlAvb, boolean trAvb, int[][][] x, H264Const.PartPred[] pp, Picture mb, int[] blocks) {
        if (this.f1455sh.direct_spatial_mv_pred_flag) {
            predictBSpatialDirect(refs, mbX, mbY, lAvb, tAvb, tlAvb, trAvb, x, pp, mb, blocks);
        } else {
            predictBTemporalDirect(refs, mbX, mbY, lAvb, tAvb, tlAvb, trAvb, x, pp, mb, blocks);
        }
    }

    private void predictBTemporalDirect(Frame[][] refs, int mbX, int mbY, boolean lAvb, boolean tAvb, boolean tlAvb, boolean trAvb, int[][][] x, H264Const.PartPred[] pp, Picture mb, int[] blocks8x8) {
        Picture mb0 = Picture.create(16, 16, this.chromaFormat);
        Picture mb1 = Picture.create(16, 16, this.chromaFormat);
        int length = blocks8x8.length;
        int i$ = 0;
        while (true) {
            int i$2 = i$;
            if (i$2 < length) {
                int blk8x8 = blocks8x8[i$2];
                int blk4x4_0 = H264Const.BLK8x8_BLOCKS[blk8x8][0];
                pp[blk8x8] = H264Const.PartPred.Bi;
                if (!this.activeSps.direct_8x8_inference_flag) {
                    int[] arr$ = H264Const.BLK8x8_BLOCKS[blk8x8];
                    for (int blk4x4 : arr$) {
                        predTemp4x4(refs, mbX, mbY, x, blk4x4);
                        int blkIndX = blk4x4 & 3;
                        int blkIndY = blk4x4 >> 2;
                        debugPrint("DIRECT_4x4 [" + blkIndY + ", " + blkIndX + "]: (" + x[0][blk4x4][0] + "," + x[0][blk4x4][1] + "," + x[0][blk4x4][2] + "), (" + x[1][blk4x4][0] + "," + x[1][blk4x4][1] + "," + x[1][blk4x4][2] + ")");
                        int blkPredX = (mbX << 6) + (blkIndX << 4);
                        int blkPredY = (mbY << 6) + (blkIndY << 4);
                        BlockInterpolator.getBlockLuma(refs[0][x[0][blk4x4][2]], mb0, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4], blkPredX + x[0][blk4x4][0], blkPredY + x[0][blk4x4][1], 4, 4);
                        BlockInterpolator.getBlockLuma(refs[1][0], mb1, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4], blkPredX + x[1][blk4x4][0], blkPredY + x[1][blk4x4][1], 4, 4);
                    }
                } else {
                    int blk4x4Pred = H264Const.BLK_INV_MAP[blk8x8 * 5];
                    predTemp4x4(refs, mbX, mbY, x, blk4x4Pred);
                    propagatePred(x, blk8x8, blk4x4Pred);
                    int blkIndX2 = blk4x4_0 & 3;
                    int blkIndY2 = blk4x4_0 >> 2;
                    debugPrint("DIRECT_8x8 [" + blkIndY2 + ", " + blkIndX2 + "]: (" + x[0][blk4x4_0][0] + "," + x[0][blk4x4_0][1] + "," + x[0][blk4x4_0][2] + "), (" + x[1][blk4x4_0][0] + "," + x[1][blk4x4_0][1] + "," + x[0][blk4x4_0][2] + ")");
                    int blkPredX2 = (mbX << 6) + (blkIndX2 << 4);
                    int blkPredY2 = (mbY << 6) + (blkIndY2 << 4);
                    BlockInterpolator.getBlockLuma(refs[0][x[0][blk4x4_0][2]], mb0, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4_0], blkPredX2 + x[0][blk4x4_0][0], blkPredY2 + x[0][blk4x4_0][1], 8, 8);
                    BlockInterpolator.getBlockLuma(refs[1][0], mb1, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4_0], blkPredX2 + x[1][blk4x4_0][0], blkPredY2 + x[1][blk4x4_0][1], 8, 8);
                }
                this.prediction.mergePrediction(x[0][blk4x4_0][2], x[1][blk4x4_0][2], H264Const.PartPred.Bi, 0, mb0.getPlaneData(0), mb1.getPlaneData(0), H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4_0], 16, 8, 8, mb.getPlaneData(0), refs, this.thisFrame);
                i$ = i$2 + 1;
            } else {
                return;
            }
        }
    }

    private void predTemp4x4(Frame[][] refs, int mbX, int mbY, int[][][] x, int blk4x4) {
        Frame refL0;
        int refIdxL0;
        int mbWidth = this.activeSps.pic_width_in_mbs_minus1 + 1;
        Frame picCol = refs[1][0];
        int blkIndX = blk4x4 & 3;
        int blkIndY = blk4x4 >> 2;
        int blkPosX = (mbX << 2) + blkIndX;
        int blkPosY = (mbY << 2) + blkIndY;
        int[] mvCol = picCol.getMvs()[0][blkPosY][blkPosX];
        if (mvCol[2] == -1) {
            mvCol = picCol.getMvs()[1][blkPosY][blkPosX];
            if (mvCol[2] == -1) {
                refIdxL0 = 0;
                refL0 = refs[0][0];
            } else {
                refL0 = picCol.getRefsUsed()[(mbY * mbWidth) + mbX][1][mvCol[2]];
                refIdxL0 = findPic(refs[0], refL0);
            }
        } else {
            refL0 = picCol.getRefsUsed()[(mbY * mbWidth) + mbX][0][mvCol[2]];
            refIdxL0 = findPic(refs[0], refL0);
        }
        x[0][blk4x4][2] = refIdxL0;
        x[1][blk4x4][2] = 0;
        int td = MathUtil.clip(picCol.getPOC() - refL0.getPOC(), -128, 127);
        if (!refL0.isShortTerm() || td == 0) {
            x[0][blk4x4][0] = mvCol[0];
            x[0][blk4x4][1] = mvCol[1];
            x[1][blk4x4][0] = 0;
            x[1][blk4x4][1] = 0;
            return;
        }
        int tb = MathUtil.clip(this.thisFrame.getPOC() - refL0.getPOC(), -128, 127);
        int tx = (Math.abs(td / 2) + 16384) / td;
        int dsf = MathUtil.clip(((tb * tx) + 32) >> 6, -1024, 1023);
        x[0][blk4x4][0] = ((mvCol[0] * dsf) + 128) >> 8;
        x[0][blk4x4][1] = ((mvCol[1] * dsf) + 128) >> 8;
        x[1][blk4x4][0] = x[0][blk4x4][0] - mvCol[0];
        x[1][blk4x4][1] = x[0][blk4x4][1] - mvCol[1];
    }

    private int findPic(Frame[] frames, Frame refL0) {
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == refL0) {
                return i;
            }
        }
        Logger.error("RefPicList0 shall contain refPicCol");
        return 0;
    }

    private void predictBSpatialDirect(Frame[][] refs, int mbX, int mbY, boolean lAvb, boolean tAvb, boolean tlAvb, boolean trAvb, int[][][] x, H264Const.PartPred[] pp, Picture mb, int[] blocks8x8) {
        H264Const.PartPred partPred;
        H264Const.PartPred partPred2;
        int[] a0 = this.mvLeft[0][0];
        int[] a1 = this.mvLeft[1][0];
        int[] b0 = this.mvTop[0][mbX << 2];
        int[] b1 = this.mvTop[1][mbX << 2];
        int[] c0 = this.mvTop[0][(mbX << 2) + 4];
        int[] c1 = this.mvTop[1][(mbX << 2) + 4];
        int[] d0 = this.mvTopLeft[0];
        int[] d1 = this.mvTopLeft[1];
        int refIdxL0 = calcRef(a0, b0, c0, d0, lAvb, tAvb, tlAvb, trAvb, mbX);
        int refIdxL1 = calcRef(a1, b1, c1, d1, lAvb, tAvb, tlAvb, trAvb, mbX);
        Picture mb0 = Picture.create(16, 16, this.chromaFormat);
        Picture mb1 = Picture.create(16, 16, this.chromaFormat);
        if (refIdxL0 < 0 && refIdxL1 < 0) {
            int length = blocks8x8.length;
            int i$ = 0;
            while (true) {
                int i$2 = i$;
                if (i$2 < length) {
                    int blk8x8 = blocks8x8[i$2];
                    int[] arr$ = H264Const.BLK8x8_BLOCKS[blk8x8];
                    for (int blk4x4 : arr$) {
                        int[] iArr = x[0][blk4x4];
                        int[] iArr2 = x[0][blk4x4];
                        int[] iArr3 = x[0][blk4x4];
                        int[] iArr4 = x[1][blk4x4];
                        int[] iArr5 = x[1][blk4x4];
                        x[1][blk4x4][2] = 0;
                        iArr5[1] = 0;
                        iArr4[0] = 0;
                        iArr3[2] = 0;
                        iArr2[1] = 0;
                        iArr[0] = 0;
                    }
                    pp[blk8x8] = H264Const.PartPred.Bi;
                    int blkOffX = (blk8x8 & 1) << 5;
                    int blkOffY = (blk8x8 >> 1) << 5;
                    BlockInterpolator.getBlockLuma(refs[0][0], mb0, H264Const.BLK_8x8_MB_OFF_LUMA[blk8x8], (mbX << 6) + blkOffX, (mbY << 6) + blkOffY, 8, 8);
                    BlockInterpolator.getBlockLuma(refs[1][0], mb1, H264Const.BLK_8x8_MB_OFF_LUMA[blk8x8], (mbX << 6) + blkOffX, (mbY << 6) + blkOffY, 8, 8);
                    this.prediction.mergePrediction(0, 0, H264Const.PartPred.Bi, 0, mb0.getPlaneData(0), mb1.getPlaneData(0), H264Const.BLK_8x8_MB_OFF_LUMA[blk8x8], 16, 8, 8, mb.getPlaneData(0), refs, this.thisFrame);
                    debugPrint("DIRECT_8x8 [" + (blk8x8 & 2) + ", " + ((blk8x8 << 1) & 2) + "]: (0,0,0), (0,0,0)");
                    i$ = i$2 + 1;
                } else {
                    return;
                }
            }
        } else {
            int mvX0 = calcMVPredictionMedian(a0, b0, c0, d0, lAvb, tAvb, trAvb, tlAvb, refIdxL0, 0);
            int mvY0 = calcMVPredictionMedian(a0, b0, c0, d0, lAvb, tAvb, trAvb, tlAvb, refIdxL0, 1);
            int mvX1 = calcMVPredictionMedian(a1, b1, c1, d1, lAvb, tAvb, trAvb, tlAvb, refIdxL1, 0);
            int mvY1 = calcMVPredictionMedian(a1, b1, c1, d1, lAvb, tAvb, trAvb, tlAvb, refIdxL1, 1);
            Frame col = refs[1][0];
            if (refIdxL0 < 0 || refIdxL1 < 0) {
                partPred = refIdxL0 >= 0 ? H264Const.PartPred.L0 : H264Const.PartPred.L1;
            } else {
                partPred = H264Const.PartPred.Bi;
            }
            int length2 = blocks8x8.length;
            int i$3 = 0;
            while (true) {
                int i$4 = i$3;
                if (i$4 < length2) {
                    int blk8x82 = blocks8x8[i$4];
                    int blk4x4_0 = H264Const.BLK8x8_BLOCKS[blk8x82][0];
                    if (!this.activeSps.direct_8x8_inference_flag) {
                        int[] arr$2 = H264Const.BLK8x8_BLOCKS[blk8x82];
                        for (int blk4x42 : arr$2) {
                            pred4x4(mbX, mbY, x, pp, refIdxL0, refIdxL1, mvX0, mvY0, mvX1, mvY1, col, partPred, blk4x42);
                            int blkIndX = blk4x42 & 3;
                            int blkIndY = blk4x42 >> 2;
                            debugPrint("DIRECT_4x4 [" + blkIndY + ", " + blkIndX + "]: (" + x[0][blk4x42][0] + "," + x[0][blk4x42][1] + "," + refIdxL0 + "), (" + x[1][blk4x42][0] + "," + x[1][blk4x42][1] + "," + refIdxL1 + ")");
                            int blkPredX = (mbX << 6) + (blkIndX << 4);
                            int blkPredY = (mbY << 6) + (blkIndY << 4);
                            if (refIdxL0 >= 0) {
                                BlockInterpolator.getBlockLuma(refs[0][refIdxL0], mb0, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x42], blkPredX + x[0][blk4x42][0], blkPredY + x[0][blk4x42][1], 4, 4);
                            }
                            if (refIdxL1 >= 0) {
                                BlockInterpolator.getBlockLuma(refs[1][refIdxL1], mb1, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x42], blkPredX + x[1][blk4x42][0], blkPredY + x[1][blk4x42][1], 4, 4);
                            }
                        }
                    } else {
                        int blk4x4Pred = H264Const.BLK_INV_MAP[blk8x82 * 5];
                        pred4x4(mbX, mbY, x, pp, refIdxL0, refIdxL1, mvX0, mvY0, mvX1, mvY1, col, partPred, blk4x4Pred);
                        propagatePred(x, blk8x82, blk4x4Pred);
                        int blkIndX2 = blk4x4_0 & 3;
                        int blkIndY2 = blk4x4_0 >> 2;
                        debugPrint("DIRECT_8x8 [" + blkIndY2 + ", " + blkIndX2 + "]: (" + x[0][blk4x4_0][0] + "," + x[0][blk4x4_0][1] + "," + refIdxL0 + "), (" + x[1][blk4x4_0][0] + "," + x[1][blk4x4_0][1] + "," + refIdxL1 + ")");
                        int blkPredX2 = (mbX << 6) + (blkIndX2 << 4);
                        int blkPredY2 = (mbY << 6) + (blkIndY2 << 4);
                        if (refIdxL0 >= 0) {
                            BlockInterpolator.getBlockLuma(refs[0][refIdxL0], mb0, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4_0], blkPredX2 + x[0][blk4x4_0][0], blkPredY2 + x[0][blk4x4_0][1], 8, 8);
                        }
                        if (refIdxL1 >= 0) {
                            BlockInterpolator.getBlockLuma(refs[1][refIdxL1], mb1, H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4_0], blkPredX2 + x[1][blk4x4_0][0], blkPredY2 + x[1][blk4x4_0][1], 8, 8);
                        }
                    }
                    Prediction prediction = this.prediction;
                    int i = x[0][blk4x4_0][2];
                    int i2 = x[1][blk4x4_0][2];
                    if (refIdxL0 >= 0) {
                        partPred2 = refIdxL1 >= 0 ? H264Const.PartPred.Bi : H264Const.PartPred.L0;
                    } else {
                        partPred2 = H264Const.PartPred.L1;
                    }
                    prediction.mergePrediction(i, i2, partPred2, 0, mb0.getPlaneData(0), mb1.getPlaneData(0), H264Const.BLK_4x4_MB_OFF_LUMA[blk4x4_0], 16, 8, 8, mb.getPlaneData(0), refs, this.thisFrame);
                    i$3 = i$4 + 1;
                } else {
                    return;
                }
            }
        }
    }

    private int calcRef(int[] a0, int[] b0, int[] c0, int[] d0, boolean lAvb, boolean tAvb, boolean tlAvb, boolean trAvb, int mbX) {
        int i = -1;
        int minPos = minPos(lAvb ? a0[2] : -1, tAvb ? b0[2] : -1);
        if (trAvb) {
            i = c0[2];
        } else if (tlAvb) {
            i = d0[2];
        }
        return minPos(minPos, i);
    }

    private void propagatePred(int[][][] x, int blk8x8, int blk4x4Pred) {
        int b0 = H264Const.BLK8x8_BLOCKS[blk8x8][0];
        int b1 = H264Const.BLK8x8_BLOCKS[blk8x8][1];
        int b2 = H264Const.BLK8x8_BLOCKS[blk8x8][2];
        int b3 = H264Const.BLK8x8_BLOCKS[blk8x8][3];
        int[] iArr = x[0][b0];
        int[] iArr2 = x[0][b1];
        int[] iArr3 = x[0][b2];
        int[] iArr4 = x[0][b3];
        int i = x[0][blk4x4Pred][0];
        iArr4[0] = i;
        iArr3[0] = i;
        iArr2[0] = i;
        iArr[0] = i;
        int[] iArr5 = x[0][b0];
        int[] iArr6 = x[0][b1];
        int[] iArr7 = x[0][b2];
        int[] iArr8 = x[0][b3];
        int i2 = x[0][blk4x4Pred][1];
        iArr8[1] = i2;
        iArr7[1] = i2;
        iArr6[1] = i2;
        iArr5[1] = i2;
        int[] iArr9 = x[0][b0];
        int[] iArr10 = x[0][b1];
        int[] iArr11 = x[0][b2];
        int[] iArr12 = x[0][b3];
        int i3 = x[0][blk4x4Pred][2];
        iArr12[2] = i3;
        iArr11[2] = i3;
        iArr10[2] = i3;
        iArr9[2] = i3;
        int[] iArr13 = x[1][b0];
        int[] iArr14 = x[1][b1];
        int[] iArr15 = x[1][b2];
        int[] iArr16 = x[1][b3];
        int i4 = x[1][blk4x4Pred][0];
        iArr16[0] = i4;
        iArr15[0] = i4;
        iArr14[0] = i4;
        iArr13[0] = i4;
        int[] iArr17 = x[1][b0];
        int[] iArr18 = x[1][b1];
        int[] iArr19 = x[1][b2];
        int[] iArr20 = x[1][b3];
        int i5 = x[1][blk4x4Pred][1];
        iArr20[1] = i5;
        iArr19[1] = i5;
        iArr18[1] = i5;
        iArr17[1] = i5;
        int[] iArr21 = x[1][b0];
        int[] iArr22 = x[1][b1];
        int[] iArr23 = x[1][b2];
        int[] iArr24 = x[1][b3];
        int i6 = x[1][blk4x4Pred][2];
        iArr24[2] = i6;
        iArr23[2] = i6;
        iArr22[2] = i6;
        iArr21[2] = i6;
    }

    private void pred4x4(int mbX, int mbY, int[][][] x, H264Const.PartPred[] pp, int refL0, int refL1, int mvX0, int mvY0, int mvX1, int mvY1, Frame col, H264Const.PartPred partPred, int blk4x4) {
        int blkIndX = blk4x4 & 3;
        int blkIndY = blk4x4 >> 2;
        int blkPosX = (mbX << 2) + blkIndX;
        int blkPosY = (mbY << 2) + blkIndY;
        x[0][blk4x4][2] = refL0;
        x[1][blk4x4][2] = refL1;
        int[] mvCol = col.getMvs()[0][blkPosY][blkPosX];
        if (mvCol[2] == -1) {
            mvCol = col.getMvs()[1][blkPosY][blkPosX];
        }
        boolean colZero = col.isShortTerm() && mvCol[2] == 0 && (MathUtil.abs(mvCol[0]) >> 1) == 0 && (MathUtil.abs(mvCol[1]) >> 1) == 0;
        if (refL0 > 0 || !colZero) {
            x[0][blk4x4][0] = mvX0;
            x[0][blk4x4][1] = mvY0;
        }
        if (refL1 > 0 || !colZero) {
            x[1][blk4x4][0] = mvX1;
            x[1][blk4x4][1] = mvY1;
        }
        pp[H264Const.BLK_8x8_IND[blk4x4]] = partPred;
    }

    private int minPos(int a, int b) {
        return (a < 0 || b < 0) ? Math.max(a, b) : Math.min(a, b);
    }

    public void predictPSkip(Frame[][] refs, int mbX, int mbY, boolean lAvb, boolean tAvb, boolean tlAvb, boolean trAvb, int[][][] x, Picture mb) {
        int mvX = 0;
        int mvY = 0;
        if (lAvb && tAvb) {
            int[] b = this.mvTop[0][mbX << 2];
            int[] a = this.mvLeft[0][0];
            if ((a[0] != 0 || a[1] != 0 || a[2] != 0) && (b[0] != 0 || b[1] != 0 || b[2] != 0)) {
                int mvX2 = calcMVPredictionMedian(a, b, this.mvTop[0][(mbX << 2) + 4], this.mvTopLeft[0], lAvb, tAvb, trAvb, tlAvb, 0, 0);
                mvY = calcMVPredictionMedian(a, b, this.mvTop[0][(mbX << 2) + 4], this.mvTopLeft[0], lAvb, tAvb, trAvb, tlAvb, 0, 1);
                mvX = mvX2;
            }
        }
        debugPrint("MV_SKIP: (" + mvX + "," + mvY + ")");
        int blk8x8X = mbX << 1;
        H264Const.PartPred[] partPredArr = this.predModeLeft;
        H264Const.PartPred[] partPredArr2 = this.predModeLeft;
        H264Const.PartPred[] partPredArr3 = this.predModeTop;
        H264Const.PartPred partPred = H264Const.PartPred.L0;
        this.predModeTop[blk8x8X + 1] = partPred;
        partPredArr3[blk8x8X] = partPred;
        partPredArr2[1] = partPred;
        partPredArr[0] = partPred;
        int xx = mbX << 2;
        copyVect(this.mvTopLeft[0], this.mvTop[0][xx + 3]);
        saveVect(this.mvTop[0], xx, xx + 4, mvX, mvY, 0);
        saveVect(this.mvLeft[0], 0, 4, mvX, mvY, 0);
        for (int i = 0; i < 16; i++) {
            x[0][i][0] = mvX;
            x[0][i][1] = mvY;
            x[0][i][2] = 0;
        }
        BlockInterpolator.getBlockLuma(refs[0][0], mb, 0, (mbX << 6) + mvX, (mbY << 6) + mvY, 16, 16);
        this.prediction.mergePrediction(0, 0, H264Const.PartPred.L0, 0, mb.getPlaneData(0), null, 0, 16, 16, 16, mb.getPlaneData(0), refs, this.thisFrame);
    }

    public void decodeChromaSkip(Frame[][] reference, int[][][] vectors, H264Const.PartPred[] pp, int mbX, int mbY, Picture mb) {
        predictChromaInter(reference, vectors, mbX << 3, mbY << 3, 1, mb, pp);
        predictChromaInter(reference, vectors, mbX << 3, mbY << 3, 2, mb, pp);
    }

    private void debugPrint(String str) {
        if (this.debug) {
            Logger.debug(str);
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
