package org.jcodec.codecs.h264;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.codecs.h264.decode.SliceDecoder;
import org.jcodec.codecs.h264.decode.SliceHeaderReader;
import org.jcodec.codecs.h264.decode.deblock.DeblockingFilter;
import org.jcodec.codecs.h264.io.model.Frame;
import org.jcodec.codecs.h264.io.model.MBType;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.RefPicMarking;
import org.jcodec.codecs.h264.io.model.RefPicMarkingIDR;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.common.IntObjectMap;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Rect;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class H264Decoder implements VideoDecoder {
    private boolean debug;
    private IntObjectMap<Frame> lRefs;
    private Frame[] sRefs;
    private IntObjectMap<SeqParameterSet> sps = new IntObjectMap<>();
    private IntObjectMap<PictureParameterSet> pps = new IntObjectMap<>();
    private List<Frame> pictureBuffer = new ArrayList();
    private POCManager poc = new POCManager();

    @Override // org.jcodec.common.VideoDecoder
    public Frame decodeFrame(ByteBuffer data, int[][] buffer) {
        return new FrameDecoder().decodeFrame(H264Utils.splitFrame(data), buffer);
    }

    public Frame decodeFrame(List<ByteBuffer> nalUnits, int[][] buffer) {
        return new FrameDecoder().decodeFrame(nalUnits, buffer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class FrameDecoder {
        private PictureParameterSet activePps;
        private SeqParameterSet activeSps;
        private SliceDecoder decoder;
        private DeblockingFilter filter;
        private NALUnit firstNu;
        private SliceHeader firstSliceHeader;
        private int[][][][] mvs;
        private SliceHeaderReader shr;

        FrameDecoder() {
        }

        public Frame decodeFrame(List<ByteBuffer> nalUnits, int[][] buffer) {
            Frame result = null;
            for (ByteBuffer nalUnit : nalUnits) {
                NALUnit marker = NALUnit.read(nalUnit);
                H264Utils.unescapeNAL(nalUnit);
                switch (marker.type) {
                    case NON_IDR_SLICE:
                    case IDR_SLICE:
                        if (result == null) {
                            result = init(buffer, nalUnit, marker);
                        }
                        this.decoder.decode(nalUnit, marker);
                        break;
                    case SPS:
                        SeqParameterSet _sps = SeqParameterSet.read(nalUnit);
                        H264Decoder.this.sps.put(_sps.seq_parameter_set_id, _sps);
                        break;
                    case PPS:
                        PictureParameterSet _pps = PictureParameterSet.read(nalUnit);
                        H264Decoder.this.pps.put(_pps.pic_parameter_set_id, _pps);
                        break;
                }
            }
            this.filter.deblockFrame(result);
            updateReferences(result);
            return result;
        }

        private void updateReferences(Frame picture) {
            if (this.firstNu.nal_ref_idc != 0) {
                if (this.firstNu.type == NALUnitType.IDR_SLICE) {
                    performIDRMarking(this.firstSliceHeader.refPicMarkingIDR, picture);
                } else {
                    performMarking(this.firstSliceHeader.refPicMarkingNonIDR, picture);
                }
            }
        }

        private Frame init(int[][] buffer, ByteBuffer segment, NALUnit marker) {
            this.firstNu = marker;
            this.shr = new SliceHeaderReader();
            BitReader br = new BitReader(segment.duplicate());
            this.firstSliceHeader = this.shr.readPart1(br);
            this.activePps = (PictureParameterSet) H264Decoder.this.pps.get(this.firstSliceHeader.pic_parameter_set_id);
            this.activeSps = (SeqParameterSet) H264Decoder.this.sps.get(this.activePps.seq_parameter_set_id);
            this.shr.readPart2(this.firstSliceHeader, marker, this.activeSps, this.activePps, br);
            int picWidthInMbs = this.activeSps.pic_width_in_mbs_minus1 + 1;
            int picHeightInMbs = H264Utils.getPicHeightInMbs(this.activeSps);
            int[][] nCoeff = (int[][]) Array.newInstance(Integer.TYPE, picHeightInMbs << 2, picWidthInMbs << 2);
            this.mvs = (int[][][][]) Array.newInstance(Integer.TYPE, 2, picHeightInMbs << 2, picWidthInMbs << 2, 3);
            MBType[] mbTypes = new MBType[picHeightInMbs * picWidthInMbs];
            boolean[] tr8x8Used = new boolean[picHeightInMbs * picWidthInMbs];
            int[][] mbQps = (int[][]) Array.newInstance(Integer.TYPE, 3, picHeightInMbs * picWidthInMbs);
            SliceHeader[] shs = new SliceHeader[picHeightInMbs * picWidthInMbs];
            Frame[][][] refsUsed = new Frame[picHeightInMbs * picWidthInMbs][];
            if (H264Decoder.this.sRefs == null) {
                H264Decoder.this.sRefs = new Frame[1 << (this.firstSliceHeader.sps.log2_max_frame_num_minus4 + 4)];
                H264Decoder.this.lRefs = new IntObjectMap();
            }
            Frame result = H264Decoder.createFrame(this.activeSps, buffer, this.firstSliceHeader.frame_num, this.mvs, refsUsed, H264Decoder.this.poc.calcPOC(this.firstSliceHeader, this.firstNu));
            this.decoder = new SliceDecoder(this.activeSps, this.activePps, nCoeff, this.mvs, mbTypes, mbQps, shs, tr8x8Used, refsUsed, result, H264Decoder.this.sRefs, H264Decoder.this.lRefs);
            this.decoder.setDebug(H264Decoder.this.debug);
            this.filter = new DeblockingFilter(picWidthInMbs, this.activeSps.bit_depth_chroma_minus8 + 8, nCoeff, this.mvs, mbTypes, mbQps, shs, tr8x8Used, refsUsed);
            return result;
        }

        public void performIDRMarking(RefPicMarkingIDR refPicMarkingIDR, Frame picture) {
            clearAll();
            H264Decoder.this.pictureBuffer.clear();
            Frame saved = saveRef(picture);
            if (refPicMarkingIDR.isUseForlongTerm()) {
                H264Decoder.this.lRefs.put(0, saved);
                saved.setShortTerm(false);
            } else {
                H264Decoder.this.sRefs[this.firstSliceHeader.frame_num] = saved;
            }
        }

        private Frame saveRef(Frame decoded) {
            Frame frame;
            if (H264Decoder.this.pictureBuffer.size() > 0) {
                frame = (Frame) H264Decoder.this.pictureBuffer.remove(0);
            } else {
                frame = Frame.createFrame(decoded);
            }
            frame.copyFrom(decoded);
            return frame;
        }

        private void releaseRef(Frame picture) {
            if (picture != null) {
                H264Decoder.this.pictureBuffer.add(picture);
            }
        }

        public void clearAll() {
            for (int i = 0; i < H264Decoder.this.sRefs.length; i++) {
                releaseRef(H264Decoder.this.sRefs[i]);
                H264Decoder.this.sRefs[i] = null;
            }
            int[] keys = H264Decoder.this.lRefs.keys();
            for (int i2 : keys) {
                releaseRef((Frame) H264Decoder.this.lRefs.get(i2));
            }
            H264Decoder.this.lRefs.clear();
        }

        public void performMarking(RefPicMarking refPicMarking, Frame picture) {
            Frame saved = saveRef(picture);
            if (refPicMarking != null) {
                RefPicMarking.Instruction[] arr$ = refPicMarking.getInstructions();
                for (RefPicMarking.Instruction instr : arr$) {
                    switch (instr.getType()) {
                        case REMOVE_SHORT:
                            unrefShortTerm(instr.getArg1());
                            break;
                        case REMOVE_LONG:
                            unrefLongTerm(instr.getArg1());
                            break;
                        case CONVERT_INTO_LONG:
                            convert(instr.getArg1(), instr.getArg2());
                            break;
                        case TRUNK_LONG:
                            truncateLongTerm(instr.getArg1() - 1);
                            break;
                        case CLEAR:
                            clearAll();
                            break;
                        case MARK_LONG:
                            saveLong(saved, instr.getArg1());
                            saved = null;
                            break;
                    }
                }
            }
            if (saved != null) {
                saveShort(saved);
            }
            int maxFrames = 1 << (this.activeSps.log2_max_frame_num_minus4 + 4);
            if (refPicMarking == null) {
                int maxShort = Math.max(1, this.activeSps.num_ref_frames - H264Decoder.this.lRefs.size());
                int min = Integer.MAX_VALUE;
                int num = 0;
                int minFn = 0;
                for (int i = 0; i < H264Decoder.this.sRefs.length; i++) {
                    if (H264Decoder.this.sRefs[i] != null) {
                        int fnWrap = unwrap(this.firstSliceHeader.frame_num, H264Decoder.this.sRefs[i].getFrameNo(), maxFrames);
                        if (fnWrap < min) {
                            min = fnWrap;
                            minFn = H264Decoder.this.sRefs[i].getFrameNo();
                        }
                        num++;
                    }
                }
                if (num > maxShort) {
                    releaseRef(H264Decoder.this.sRefs[minFn]);
                    H264Decoder.this.sRefs[minFn] = null;
                }
            }
        }

        private int unwrap(int thisFrameNo, int refFrameNo, int maxFrames) {
            return refFrameNo > thisFrameNo ? refFrameNo - maxFrames : refFrameNo;
        }

        private void saveShort(Frame saved) {
            H264Decoder.this.sRefs[this.firstSliceHeader.frame_num] = saved;
        }

        private void saveLong(Frame saved, int longNo) {
            Frame prev = (Frame) H264Decoder.this.lRefs.get(longNo);
            if (prev != null) {
                releaseRef(prev);
            }
            saved.setShortTerm(false);
            H264Decoder.this.lRefs.put(longNo, saved);
        }

        private void truncateLongTerm(int maxLongNo) {
            int[] keys = H264Decoder.this.lRefs.keys();
            for (int i = 0; i < keys.length; i++) {
                if (keys[i] > maxLongNo) {
                    releaseRef((Frame) H264Decoder.this.lRefs.get(keys[i]));
                    H264Decoder.this.lRefs.remove(keys[i]);
                }
            }
        }

        private void convert(int shortNo, int longNo) {
            int ind = MathUtil.wrap(this.firstSliceHeader.frame_num - shortNo, 1 << (this.firstSliceHeader.sps.log2_max_frame_num_minus4 + 4));
            releaseRef((Frame) H264Decoder.this.lRefs.get(longNo));
            H264Decoder.this.lRefs.put(longNo, H264Decoder.this.sRefs[ind]);
            H264Decoder.this.sRefs[ind] = null;
            ((Frame) H264Decoder.this.lRefs.get(longNo)).setShortTerm(false);
        }

        private void unrefLongTerm(int longNo) {
            releaseRef((Frame) H264Decoder.this.lRefs.get(longNo));
            H264Decoder.this.lRefs.remove(longNo);
        }

        private void unrefShortTerm(int shortNo) {
            int ind = MathUtil.wrap(this.firstSliceHeader.frame_num - shortNo, 1 << (this.firstSliceHeader.sps.log2_max_frame_num_minus4 + 4));
            releaseRef(H264Decoder.this.sRefs[ind]);
            H264Decoder.this.sRefs[ind] = null;
        }
    }

    public static Frame createFrame(SeqParameterSet sps, int[][] buffer, int frame_num, int[][][][] mvs, Frame[][][] refsUsed, int POC) {
        int width = (sps.pic_width_in_mbs_minus1 + 1) << 4;
        int height = H264Utils.getPicHeightInMbs(sps) << 4;
        Rect crop = null;
        if (sps.frame_cropping_flag) {
            int sX = sps.frame_crop_left_offset << 1;
            int sY = sps.frame_crop_top_offset << 1;
            int w = (width - (sps.frame_crop_right_offset << 1)) - sX;
            int h = (height - (sps.frame_crop_bottom_offset << 1)) - sY;
            crop = new Rect(sX, sY, w, h);
        }
        return new Frame(width, height, buffer, ColorSpace.YUV420, crop, frame_num, mvs, refsUsed, POC);
    }

    public void addSps(List<ByteBuffer> spsList) {
        for (ByteBuffer byteBuffer : spsList) {
            ByteBuffer dup = byteBuffer.duplicate();
            H264Utils.unescapeNAL(dup);
            SeqParameterSet s = SeqParameterSet.read(dup);
            this.sps.put(s.seq_parameter_set_id, s);
        }
    }

    public void addPps(List<ByteBuffer> ppsList) {
        for (ByteBuffer byteBuffer : ppsList) {
            ByteBuffer dup = byteBuffer.duplicate();
            H264Utils.unescapeNAL(dup);
            PictureParameterSet p = PictureParameterSet.read(dup);
            this.pps.put(p.pic_parameter_set_id, p);
        }
    }

    @Override // org.jcodec.common.VideoDecoder
    public int probe(ByteBuffer data) {
        boolean validSps = false;
        boolean validPps = false;
        boolean validSh = false;
        for (ByteBuffer nalUnit : H264Utils.splitFrame(data.duplicate())) {
            NALUnit marker = NALUnit.read(nalUnit);
            if (marker.type == NALUnitType.IDR_SLICE || marker.type == NALUnitType.NON_IDR_SLICE) {
                BitReader reader = new BitReader(nalUnit);
                validSh = validSh(new SliceHeaderReader().readPart1(reader));
                break;
            }
            if (marker.type == NALUnitType.SPS) {
                validSps = validSps(SeqParameterSet.read(nalUnit));
            } else if (marker.type == NALUnitType.PPS) {
                validPps = validPps(PictureParameterSet.read(nalUnit));
            }
        }
        return (validPps ? 20 : 0) + (validSps ? 20 : 0) + (validSh ? 60 : 0);
    }

    private boolean validSh(SliceHeader sh) {
        return sh.first_mb_in_slice == 0 && sh.slice_type != null && sh.pic_parameter_set_id < 2;
    }

    private boolean validSps(SeqParameterSet sps) {
        return sps.bit_depth_chroma_minus8 < 4 && sps.bit_depth_luma_minus8 < 4 && sps.chroma_format_idc != null && sps.seq_parameter_set_id < 2 && sps.pic_order_cnt_type <= 2;
    }

    private boolean validPps(PictureParameterSet pps) {
        return pps.pic_init_qp_minus26 <= 26 && pps.seq_parameter_set_id <= 2 && pps.pic_parameter_set_id <= 2;
    }

    public void setDebug(boolean b) {
        this.debug = b;
    }
}
