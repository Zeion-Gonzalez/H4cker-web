package org.jcodec.codecs.mpeg12;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jcodec.codecs.mpeg12.MPEGConst;
import org.jcodec.codecs.mpeg12.bitstream.GOPHeader;
import org.jcodec.codecs.mpeg12.bitstream.PictureHeader;
import org.jcodec.codecs.mpeg12.bitstream.SequenceHeader;
import org.jcodec.common.Assert;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.dct.SparseIDCT;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.common.model.Size;

/* loaded from: classes.dex */
public class MPEGDecoder implements VideoDecoder {

    /* renamed from: gh */
    protected GOPHeader f1474gh;
    private Picture[] refFields;
    private Picture[] refFrames;

    /* renamed from: sh */
    protected SequenceHeader f1475sh;

    public MPEGDecoder(SequenceHeader sh, GOPHeader gh) {
        this.refFrames = new Picture[2];
        this.refFields = new Picture[2];
        this.f1475sh = sh;
        this.f1474gh = gh;
    }

    public MPEGDecoder() {
        this.refFrames = new Picture[2];
        this.refFields = new Picture[2];
    }

    /* loaded from: classes.dex */
    public class Context {
        public int codedHeight;
        public int codedWidth;
        public ColorSpace color;
        int[] intra_dc_predictor = new int[3];
        public MPEGConst.MBType lastPredB;
        public int mbHeight;
        int mbNo;
        public int mbWidth;
        public int picHeight;
        public int picWidth;
        public int[][] qMats;
        public int[] scan;

        public Context() {
        }
    }

    @Override // org.jcodec.common.VideoDecoder
    public Picture decodeFrame(ByteBuffer ByteBuffer, int[][] buf) {
        PictureHeader ph;
        PictureHeader ph2 = readHeader(ByteBuffer);
        if ((this.refFrames[0] == null && ph2.picture_coding_type > 1) || (this.refFrames[1] == null && ph2.picture_coding_type > 2)) {
            throw new RuntimeException("Not enough references to decode " + (ph2.picture_coding_type == 1 ? "P" : "B") + " frame");
        }
        Context context = initContext(this.f1475sh, ph2);
        Picture pic = new Picture(context.codedWidth, context.codedHeight, buf, context.color, new Rect(0, 0, context.picWidth, context.picHeight));
        if (ph2.pictureCodingExtension != null && ph2.pictureCodingExtension.picture_structure != 3) {
            decodePicture(context, ph2, ByteBuffer, buf, ph2.pictureCodingExtension.picture_structure - 1, 1);
            ph = readHeader(ByteBuffer);
            decodePicture(initContext(this.f1475sh, ph), ph, ByteBuffer, buf, ph.pictureCodingExtension.picture_structure - 1, 1);
        } else {
            decodePicture(context, ph2, ByteBuffer, buf, 0, 0);
            ph = ph2;
        }
        if (ph.picture_coding_type == 1 || ph.picture_coding_type == 2) {
            Picture unused = this.refFrames[1];
            this.refFrames[1] = this.refFrames[0];
            this.refFrames[0] = copyAndCreateIfNeeded(pic, unused);
        }
        return pic;
    }

    private Picture copyAndCreateIfNeeded(Picture src, Picture dst) {
        if (dst == null || !dst.compatible(src)) {
            dst = src.createCompatible();
        }
        dst.copyFrom(src);
        return dst;
    }

    private PictureHeader readHeader(ByteBuffer buffer) {
        PictureHeader ph = null;
        ByteBuffer fork = buffer.duplicate();
        while (true) {
            ByteBuffer segment = MPEGUtil.nextSegment(fork);
            if (segment == null) {
                break;
            }
            int code = segment.getInt() & 255;
            if (code == 179) {
                SequenceHeader newSh = SequenceHeader.read(segment);
                if (this.f1475sh != null) {
                    newSh.copyExtensions(this.f1475sh);
                }
                this.f1475sh = newSh;
            } else if (code == 184) {
                this.f1474gh = GOPHeader.read(segment);
            } else if (code == 0) {
                ph = PictureHeader.read(segment);
            } else if (code == 181) {
                int extType = segment.get(4) >> 4;
                if (extType == 1 || extType == 5 || extType == 2) {
                    SequenceHeader.readExtension(segment, this.f1475sh);
                } else {
                    PictureHeader.readExtension(segment, ph, this.f1475sh);
                }
            } else if (code != 178) {
                break;
            }
            buffer.position(fork.position());
        }
        return ph;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Context initContext(SequenceHeader sh, PictureHeader ph) {
        Context context = new Context();
        context.codedWidth = (sh.horizontal_size + 15) & (-16);
        context.codedHeight = getCodedHeight(sh, ph);
        context.mbWidth = (sh.horizontal_size + 15) >> 4;
        context.mbHeight = (sh.vertical_size + 15) >> 4;
        context.picWidth = sh.horizontal_size;
        context.picHeight = sh.vertical_size;
        int chromaFormat = 1;
        if (sh.sequenceExtension != null) {
            chromaFormat = sh.sequenceExtension.chroma_format;
        }
        context.color = getColor(chromaFormat);
        context.scan = MPEGConst.scan[ph.pictureCodingExtension == null ? 0 : ph.pictureCodingExtension.alternate_scan];
        int[] inter = sh.non_intra_quantiser_matrix == null ? zigzag(MPEGConst.defaultQMatInter, context.scan) : sh.non_intra_quantiser_matrix;
        int[] intra = sh.intra_quantiser_matrix == null ? zigzag(MPEGConst.defaultQMatIntra, context.scan) : sh.intra_quantiser_matrix;
        context.qMats = new int[][]{inter, inter, intra, intra};
        if (ph.quantMatrixExtension != null) {
            if (ph.quantMatrixExtension.non_intra_quantiser_matrix != null) {
                context.qMats[0] = ph.quantMatrixExtension.non_intra_quantiser_matrix;
            }
            if (ph.quantMatrixExtension.chroma_non_intra_quantiser_matrix != null) {
                context.qMats[1] = ph.quantMatrixExtension.chroma_non_intra_quantiser_matrix;
            }
            if (ph.quantMatrixExtension.intra_quantiser_matrix != null) {
                context.qMats[2] = ph.quantMatrixExtension.intra_quantiser_matrix;
            }
            if (ph.quantMatrixExtension.chroma_intra_quantiser_matrix != null) {
                context.qMats[3] = ph.quantMatrixExtension.chroma_intra_quantiser_matrix;
            }
        }
        return context;
    }

    private int[] zigzag(int[] array, int[] scan) {
        int[] result = new int[64];
        for (int i = 0; i < scan.length; i++) {
            result[i] = array[scan[i]];
        }
        return result;
    }

    public static int getCodedHeight(SequenceHeader sh, PictureHeader ph) {
        int field = (ph.pictureCodingExtension == null || ph.pictureCodingExtension.picture_structure == 3) ? 0 : 1;
        return (((sh.vertical_size >> field) + 15) & (-16)) << field;
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x00ce, code lost:
    
        r10 = new org.jcodec.common.model.Picture(r14.codedWidth, r14.codedHeight, r17, r14.color);
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00de, code lost:
    
        if (r15.picture_coding_type == 1) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00e3, code lost:
    
        if (r15.picture_coding_type != 2) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00e7, code lost:
    
        if (r15.pictureCodingExtension == null) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00ee, code lost:
    
        if (r15.pictureCodingExtension.picture_structure == 3) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00f0, code lost:
    
        r13.refFields[r15.pictureCodingExtension.picture_structure - 1] = copyAndCreateIfNeeded(r10, r13.refFields[r15.pictureCodingExtension.picture_structure - 1]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0108, code lost:
    
        return r10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.jcodec.common.model.Picture decodePicture(org.jcodec.codecs.mpeg12.MPEGDecoder.Context r14, org.jcodec.codecs.mpeg12.bitstream.PictureHeader r15, java.nio.ByteBuffer r16, int[][] r17, int r18, int r19) {
        /*
            Method dump skipped, instructions count: 265
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.codecs.mpeg12.MPEGDecoder.decodePicture(org.jcodec.codecs.mpeg12.MPEGDecoder$Context, org.jcodec.codecs.mpeg12.bitstream.PictureHeader, java.nio.ByteBuffer, int[][], int, int):org.jcodec.common.model.Picture");
    }

    private ColorSpace getColor(int chromaFormat) {
        switch (chromaFormat) {
            case 1:
                return ColorSpace.YUV420;
            case 2:
                return ColorSpace.YUV422;
            case 3:
                return ColorSpace.YUV444;
            default:
                return null;
        }
    }

    public void decodeSlice(PictureHeader ph, int verticalPos, Context context, int[][] buf, BitReader in, int vertOff, int vertStep) throws IOException {
        int stride = context.codedWidth;
        resetDCPredictors(context, ph);
        int mbRow = verticalPos - 1;
        if (this.f1475sh.vertical_size > 2800) {
            mbRow += in.readNBit(3) << 7;
        }
        if (this.f1475sh.sequenceScalableExtension != null && this.f1475sh.sequenceScalableExtension.scalable_mode == 0) {
            in.readNBit(7);
        }
        int qScaleCode = in.readNBit(5);
        if (in.read1Bit() == 1) {
            in.read1Bit();
            in.skip(7);
            while (in.read1Bit() == 1) {
                in.readNBit(8);
            }
        }
        MPEGPred pred = new MPEGPred(ph.pictureCodingExtension != null ? ph.pictureCodingExtension.f_code : new int[][]{new int[]{ph.forward_f_code, ph.forward_f_code}, new int[]{ph.backward_f_code, ph.backward_f_code}}, this.f1475sh.sequenceExtension != null ? this.f1475sh.sequenceExtension.chroma_format : 1, ph.pictureCodingExtension == null || ph.pictureCodingExtension.top_field_first != 0);
        int[] ctx = {qScaleCode};
        int prevAddr = (context.mbWidth * mbRow) - 1;
        while (in.checkNBit(23) != 0) {
            prevAddr = decodeMacroblock(ph, context, prevAddr, ctx, buf, stride, in, vertOff, vertStep, pred);
            context.mbNo++;
        }
    }

    private void resetDCPredictors(Context context, PictureHeader ph) {
        int rval = 128;
        if (ph.pictureCodingExtension != null) {
            rval = 1 << (ph.pictureCodingExtension.intra_dc_precision + 7);
        }
        int[] iArr = context.intra_dc_predictor;
        int[] iArr2 = context.intra_dc_predictor;
        context.intra_dc_predictor[2] = rval;
        iArr2[1] = rval;
        iArr[0] = rval;
    }

    public int decodeMacroblock(PictureHeader ph, Context context, int prevAddr, int[] qScaleCode, int[][] buf, int stride, BitReader bits, int vertOff, int vertStep, MPEGPred pred) {
        int i;
        int mbAddr = prevAddr;
        while (bits.checkNBit(11) == 8) {
            bits.skip(11);
            mbAddr += 33;
        }
        int mbAddr2 = mbAddr + MPEGConst.vlcAddressIncrement.readVLC(bits) + 1;
        int chromaFormat = 1;
        if (this.f1475sh.sequenceExtension != null) {
            chromaFormat = this.f1475sh.sequenceExtension.chroma_format;
        }
        for (int i2 = prevAddr + 1; i2 < mbAddr2; i2++) {
            int[][] predFwd = {new int[256], new int[1 << (chromaFormat + 5)], new int[1 << (chromaFormat + 5)]};
            int mbX = i2 % context.mbWidth;
            int mbY = i2 / context.mbWidth;
            if (ph.picture_coding_type == 2) {
                pred.reset();
            }
            mvZero(context, ph, pred, mbX, mbY, predFwd);
            put(predFwd, buf, stride, chromaFormat, mbX, mbY, context.codedWidth, context.codedHeight >> vertStep, vertOff, vertStep);
        }
        VLC vlcMBType = MPEGConst.vlcMBType(ph.picture_coding_type, this.f1475sh.sequenceScalableExtension);
        MPEGConst.MBType[] mbTypeVal = MPEGConst.mbTypeVal(ph.picture_coding_type, this.f1475sh.sequenceScalableExtension);
        MPEGConst.MBType mbType = mbTypeVal[vlcMBType.readVLC(bits)];
        if (mbType.macroblock_intra != 1 || mbAddr2 - prevAddr > 1) {
            resetDCPredictors(context, ph);
        }
        int spatial_temporal_weight_code = 0;
        if (mbType.spatial_temporal_weight_code_flag == 1 && ph.pictureSpatialScalableExtension != null && ph.pictureSpatialScalableExtension.spatial_temporal_weight_code_table_index != 0) {
            spatial_temporal_weight_code = bits.readNBit(2);
        }
        int motion_type = -1;
        if (mbType.macroblock_motion_forward != 0 || mbType.macroblock_motion_backward != 0) {
            if (ph.pictureCodingExtension == null || (ph.pictureCodingExtension.picture_structure == 3 && ph.pictureCodingExtension.frame_pred_frame_dct == 1)) {
                motion_type = 2;
            } else {
                motion_type = bits.readNBit(2);
            }
        }
        int dctType = 0;
        if (ph.pictureCodingExtension != null && ph.pictureCodingExtension.picture_structure == 3 && ph.pictureCodingExtension.frame_pred_frame_dct == 0 && (mbType.macroblock_intra != 0 || mbType.macroblock_pattern != 0)) {
            dctType = bits.read1Bit();
        }
        if (mbType.macroblock_quant != 0) {
            qScaleCode[0] = bits.readNBit(5);
        }
        boolean concealmentMv = (ph.pictureCodingExtension == null || ph.pictureCodingExtension.concealment_motion_vectors == 0) ? false : true;
        int[][] predFwd2 = null;
        int mbX2 = mbAddr2 % context.mbWidth;
        int mbY2 = mbAddr2 / context.mbWidth;
        if (mbType.macroblock_intra == 1) {
            if (!concealmentMv) {
                pred.reset();
            }
        } else if (mbType.macroblock_motion_forward != 0) {
            int refIdx = ph.picture_coding_type == 2 ? 0 : 1;
            predFwd2 = new int[][]{new int[256], new int[1 << (chromaFormat + 5)], new int[1 << (chromaFormat + 5)]};
            if (ph.pictureCodingExtension == null || ph.pictureCodingExtension.picture_structure == 3) {
                pred.predictInFrame(this.refFrames[refIdx], mbX2 << 4, mbY2 << 4, predFwd2, bits, motion_type, 0, spatial_temporal_weight_code);
            } else if (ph.picture_coding_type == 2) {
                pred.predictInField(this.refFields, mbX2 << 4, mbY2 << 4, predFwd2, bits, motion_type, 0, ph.pictureCodingExtension.picture_structure - 1);
            } else {
                pred.predictInField(new Picture[]{this.refFrames[refIdx], this.refFrames[refIdx]}, mbX2 << 4, mbY2 << 4, predFwd2, bits, motion_type, 0, ph.pictureCodingExtension.picture_structure - 1);
            }
        } else if (ph.picture_coding_type == 2) {
            predFwd2 = new int[][]{new int[256], new int[1 << (chromaFormat + 5)], new int[1 << (chromaFormat + 5)]};
            pred.reset();
            mvZero(context, ph, pred, mbX2, mbY2, predFwd2);
        }
        int[][] predBack = null;
        if (mbType.macroblock_motion_backward != 0) {
            predBack = new int[][]{new int[256], new int[1 << (chromaFormat + 5)], new int[1 << (chromaFormat + 5)]};
            if (ph.pictureCodingExtension == null || ph.pictureCodingExtension.picture_structure == 3) {
                pred.predictInFrame(this.refFrames[0], mbX2 << 4, mbY2 << 4, predBack, bits, motion_type, 1, spatial_temporal_weight_code);
            } else {
                pred.predictInField(new Picture[]{this.refFrames[0], this.refFrames[0]}, mbX2 << 4, mbY2 << 4, predBack, bits, motion_type, 1, ph.pictureCodingExtension.picture_structure - 1);
            }
        }
        context.lastPredB = mbType;
        int[][] pp = mbType.macroblock_intra == 1 ? new int[][]{new int[256], new int[1 << (chromaFormat + 5)], new int[1 << (chromaFormat + 5)]} : buildPred(predFwd2, predBack);
        if (mbType.macroblock_intra != 0 && concealmentMv) {
            Assert.assertEquals(1, bits.read1Bit());
        }
        int cbp = mbType.macroblock_intra == 1 ? 4095 : 0;
        if (mbType.macroblock_pattern != 0) {
            cbp = readCbPattern(bits);
        }
        VLC vlcCoeff = MPEGConst.vlcCoeff0;
        if (ph.pictureCodingExtension != null && mbType.macroblock_intra == 1 && ph.pictureCodingExtension.intra_vlc_format == 1) {
            vlcCoeff = MPEGConst.vlcCoeff1;
        }
        int[] qScaleTab = (ph.pictureCodingExtension == null || ph.pictureCodingExtension.q_scale_type != 1) ? MPEGConst.qScaleTab1 : MPEGConst.qScaleTab2;
        int qScale = qScaleTab[qScaleCode[0]];
        int intra_dc_mult = 8;
        if (ph.pictureCodingExtension != null) {
            intra_dc_mult = 8 >> ph.pictureCodingExtension.intra_dc_precision;
        }
        if (chromaFormat == 1) {
            i = 0;
        } else {
            i = chromaFormat == 2 ? 2 : 6;
        }
        int blkCount = i + 6;
        int[] block = new int[64];
        int i3 = 0;
        int cbpMask = 1 << (blkCount - 1);
        while (i3 < blkCount) {
            if ((cbp & cbpMask) != 0) {
                int[] qmat = context.qMats[(i3 >= 4 ? 1 : 0) + (mbType.macroblock_intra << 1)];
                if (mbType.macroblock_intra == 1) {
                    blockIntra(bits, vlcCoeff, block, context.intra_dc_predictor, i3, context.scan, (this.f1475sh.hasExtensions() || ph.hasExtensions()) ? 12 : 8, intra_dc_mult, qScale, qmat);
                } else {
                    blockInter(bits, vlcCoeff, block, context.scan, (this.f1475sh.hasExtensions() || ph.hasExtensions()) ? 12 : 8, qScale, qmat);
                }
                mapBlock(block, pp[MPEGConst.BLOCK_TO_CC[i3]], i3, dctType, chromaFormat);
            }
            i3++;
            cbpMask >>= 1;
        }
        put(pp, buf, stride, chromaFormat, mbX2, mbY2, context.codedWidth, context.codedHeight >> vertStep, vertOff, vertStep);
        return mbAddr2;
    }

    protected void mapBlock(int[] block, int[] out, int blkIdx, int dctType, int chromaFormat) {
        int stepVert = (chromaFormat == 1 && (blkIdx == 4 || blkIdx == 5)) ? 0 : dctType;
        int log2stride = blkIdx < 4 ? 4 : 4 - MPEGConst.SQUEEZE_X[chromaFormat];
        int blkIdxExt = blkIdx + (dctType << 4);
        int x = MPEGConst.BLOCK_POS_X[blkIdxExt];
        int y = MPEGConst.BLOCK_POS_Y[blkIdxExt];
        int off = (y << log2stride) + x;
        int stride = 1 << (log2stride + stepVert);
        int i = 0;
        int coeff = 0;
        while (i < 8) {
            out[off] = out[off] + block[coeff];
            int i2 = off + 1;
            out[i2] = out[i2] + block[coeff + 1];
            int i3 = off + 2;
            out[i3] = out[i3] + block[coeff + 2];
            int i4 = off + 3;
            out[i4] = out[i4] + block[coeff + 3];
            int i5 = off + 4;
            out[i5] = out[i5] + block[coeff + 4];
            int i6 = off + 5;
            out[i6] = out[i6] + block[coeff + 5];
            int i7 = off + 6;
            out[i7] = out[i7] + block[coeff + 6];
            int i8 = off + 7;
            out[i8] = out[i8] + block[coeff + 7];
            off += stride;
            i++;
            coeff += 8;
        }
    }

    private static final int[][] buildPred(int[][] predFwd, int[][] predBack) {
        if (predFwd != null && predBack != null) {
            avgPred(predFwd, predBack);
            return predFwd;
        }
        if (predFwd == null) {
            if (predBack != null) {
                return predBack;
            }
            throw new RuntimeException("Omited pred in B-frames --> invalid");
        }
        return predFwd;
    }

    private static final void avgPred(int[][] predFwd, int[][] predBack) {
        for (int i = 0; i < predFwd.length; i++) {
            for (int j = 0; j < predFwd[i].length; j += 4) {
                predFwd[i][j] = ((predFwd[i][j] + predBack[i][j]) + 1) >> 1;
                predFwd[i][j + 1] = ((predFwd[i][j + 1] + predBack[i][j + 1]) + 1) >> 1;
                predFwd[i][j + 2] = ((predFwd[i][j + 2] + predBack[i][j + 2]) + 1) >> 1;
                predFwd[i][j + 3] = ((predFwd[i][j + 3] + predBack[i][j + 3]) + 1) >> 1;
            }
        }
    }

    private void mvZero(Context context, PictureHeader ph, MPEGPred pred, int mbX, int mbY, int[][] mbPix) {
        if (ph.picture_coding_type == 2) {
            pred.predict16x16NoMV(this.refFrames[0], mbX << 4, mbY << 4, ph.pictureCodingExtension == null ? 3 : ph.pictureCodingExtension.picture_structure, 0, mbPix);
            return;
        }
        int[][] pp = mbPix;
        if (context.lastPredB.macroblock_motion_backward == 1) {
            pred.predict16x16NoMV(this.refFrames[0], mbX << 4, mbY << 4, ph.pictureCodingExtension == null ? 3 : ph.pictureCodingExtension.picture_structure, 1, pp);
            pp = new int[][]{new int[mbPix[0].length], new int[mbPix[1].length], new int[mbPix[2].length]};
        }
        if (context.lastPredB.macroblock_motion_forward == 1) {
            pred.predict16x16NoMV(this.refFrames[1], mbX << 4, mbY << 4, ph.pictureCodingExtension == null ? 3 : ph.pictureCodingExtension.picture_structure, 0, pp);
            if (mbPix != pp) {
                avgPred(mbPix, pp);
            }
        }
    }

    protected void put(int[][] mbPix, int[][] buf, int stride, int chromaFormat, int mbX, int mbY, int width, int height, int vertOff, int vertStep) {
        int chromaStride = (((1 << MPEGConst.SQUEEZE_X[chromaFormat]) + stride) - 1) >> MPEGConst.SQUEEZE_X[chromaFormat];
        int chromaMBW = 4 - MPEGConst.SQUEEZE_X[chromaFormat];
        int chromaMBH = 4 - MPEGConst.SQUEEZE_Y[chromaFormat];
        putSub(buf[0], (mbX << 4) + ((mbY << 4) * (stride << vertStep)) + (vertOff * stride), stride << vertStep, mbPix[0], 4, 4);
        putSub(buf[1], (mbX << chromaMBW) + ((mbY << chromaMBH) * (chromaStride << vertStep)) + (vertOff * chromaStride), chromaStride << vertStep, mbPix[1], chromaMBW, chromaMBH);
        putSub(buf[2], (mbX << chromaMBW) + ((mbY << chromaMBH) * (chromaStride << vertStep)) + (vertOff * chromaStride), chromaStride << vertStep, mbPix[2], chromaMBW, chromaMBH);
    }

    private final void putSub(int[] big, int off, int stride, int[] block, int mbW, int mbH) {
        int blOff = 0;
        if (mbW == 3) {
            for (int i = 0; i < (1 << mbH); i++) {
                big[off] = clip(block[blOff]);
                big[off + 1] = clip(block[blOff + 1]);
                big[off + 2] = clip(block[blOff + 2]);
                big[off + 3] = clip(block[blOff + 3]);
                big[off + 4] = clip(block[blOff + 4]);
                big[off + 5] = clip(block[blOff + 5]);
                big[off + 6] = clip(block[blOff + 6]);
                big[off + 7] = clip(block[blOff + 7]);
                blOff += 8;
                off += stride;
            }
            return;
        }
        for (int i2 = 0; i2 < (1 << mbH); i2++) {
            big[off] = clip(block[blOff]);
            big[off + 1] = clip(block[blOff + 1]);
            big[off + 2] = clip(block[blOff + 2]);
            big[off + 3] = clip(block[blOff + 3]);
            big[off + 4] = clip(block[blOff + 4]);
            big[off + 5] = clip(block[blOff + 5]);
            big[off + 6] = clip(block[blOff + 6]);
            big[off + 7] = clip(block[blOff + 7]);
            big[off + 8] = clip(block[blOff + 8]);
            big[off + 9] = clip(block[blOff + 9]);
            big[off + 10] = clip(block[blOff + 10]);
            big[off + 11] = clip(block[blOff + 11]);
            big[off + 12] = clip(block[blOff + 12]);
            big[off + 13] = clip(block[blOff + 13]);
            big[off + 14] = clip(block[blOff + 14]);
            big[off + 15] = clip(block[blOff + 15]);
            blOff += 16;
            off += stride;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int clip(int val) {
        if (val < 0) {
            return 0;
        }
        if (val > 255) {
            return 255;
        }
        return val;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int quantInter(int level, int quant) {
        return (((level << 1) + 1) * quant) >> 5;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int quantInterSigned(int level, int quant) {
        return level >= 0 ? quantInter(level, quant) : -quantInter(-level, quant);
    }

    protected void blockIntra(BitReader bits, VLC vlcCoeff, int[] block, int[] intra_dc_predictor, int blkIdx, int[] scan, int escSize, int intra_dc_mult, int qScale, int[] qmat) {
        int readVLC;
        int level;
        int cc = MPEGConst.BLOCK_TO_CC[blkIdx];
        int size = (cc == 0 ? MPEGConst.vlcDCSizeLuma : MPEGConst.vlcDCSizeChroma).readVLC(bits);
        int delta = size != 0 ? mpegSigned(bits, size) : 0;
        intra_dc_predictor[cc] = intra_dc_predictor[cc] + delta;
        int dc = intra_dc_predictor[cc] * intra_dc_mult;
        SparseIDCT.start(block, dc);
        int idx = 0;
        while (idx < 64 && (readVLC = vlcCoeff.readVLC(bits)) != 2048) {
            if (readVLC == 2049) {
                idx += bits.readNBit(6) + 1;
                int level2 = twosSigned(bits, escSize) * qScale * qmat[idx];
                level = level2 >= 0 ? level2 >> 4 : -((-level2) >> 4);
            } else {
                idx += (readVLC >> 6) + 1;
                level = toSigned((((readVLC & 63) * qScale) * qmat[idx]) >> 4, bits.read1Bit());
            }
            SparseIDCT.coeff(block, scan[idx], level);
        }
        SparseIDCT.finish(block);
    }

    protected void blockInter(BitReader bits, VLC vlcCoeff, int[] block, int[] scan, int escSize, int qScale, int[] qmat) {
        int readVLC;
        int ac;
        int idx = -1;
        if (vlcCoeff == MPEGConst.vlcCoeff0 && bits.checkNBit(1) == 1) {
            bits.read1Bit();
            int dc = toSigned(quantInter(1, qmat[0] * qScale), bits.read1Bit());
            SparseIDCT.start(block, dc);
            idx = (-1) + 1;
        } else {
            SparseIDCT.start(block, 0);
        }
        while (idx < 64 && (readVLC = vlcCoeff.readVLC(bits)) != 2048) {
            if (readVLC == 2049) {
                idx += bits.readNBit(6) + 1;
                ac = quantInterSigned(twosSigned(bits, escSize), qmat[idx] * qScale);
            } else {
                idx += (readVLC >> 6) + 1;
                ac = toSigned(quantInter(readVLC & 63, qmat[idx] * qScale), bits.read1Bit());
            }
            SparseIDCT.coeff(block, scan[idx], ac);
        }
        SparseIDCT.finish(block);
    }

    public static final int twosSigned(BitReader bits, int size) {
        int shift = 32 - size;
        return (bits.readNBit(size) << shift) >> shift;
    }

    public static final int mpegSigned(BitReader bits, int size) {
        int val = bits.readNBit(size);
        int sign = (val >>> (size - 1)) ^ 1;
        return (val + sign) - (sign << size);
    }

    public static final int toSigned(int val, int s) {
        int sign = (s << 31) >> 31;
        return (val ^ sign) - sign;
    }

    private final int readCbPattern(BitReader bits) {
        int cbp420 = MPEGConst.vlcCBP.readVLC(bits);
        if (this.f1475sh.sequenceExtension != null && this.f1475sh.sequenceExtension.chroma_format != 1) {
            if (this.f1475sh.sequenceExtension.chroma_format == 2) {
                return (cbp420 << 2) | bits.readNBit(2);
            }
            if (this.f1475sh.sequenceExtension.chroma_format == 3) {
                return (cbp420 << 6) | bits.readNBit(6);
            }
            throw new RuntimeException("Unsupported chroma format: " + this.f1475sh.sequenceExtension.chroma_format);
        }
        return cbp420;
    }

    @Override // org.jcodec.common.VideoDecoder
    public int probe(ByteBuffer data) {
        ByteBuffer data2 = data.duplicate();
        data2.order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < 2 && MPEGUtil.gotoNextMarker(data2) != null && data2.hasRemaining(); i++) {
            int marker = data2.getInt();
            if (marker == 256 || (marker >= 432 && marker <= 440)) {
                return 50 - (i * 10);
            }
            if (marker > 256 && marker < 432) {
                return 20 - (i * 10);
            }
        }
        return 0;
    }

    public static Size getSize(ByteBuffer data) {
        SequenceHeader sh = getSequenceHeader(data.duplicate());
        return new Size(sh.horizontal_size, sh.vertical_size);
    }

    private static SequenceHeader getSequenceHeader(ByteBuffer data) {
        ByteBuffer segment = MPEGUtil.nextSegment(data);
        while (segment != null) {
            int marker = segment.getInt();
            if (marker == 435) {
                return SequenceHeader.read(segment);
            }
            segment = MPEGUtil.nextSegment(data);
        }
        return null;
    }
}
