package org.jcodec.codecs.prores;

import java.nio.ByteBuffer;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.codecs.prores.ProresConsts;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.dct.DCTRef;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.common.tools.ImageOP;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class ProresEncoder {
    private static final int DEFAULT_SLICE_MB_WIDTH = 8;
    private static final int LOG_DEFAULT_SLICE_MB_WIDTH = 3;
    protected Profile profile;
    private int[][] scaledChroma;
    private int[][] scaledLuma;

    /* loaded from: classes.dex */
    public enum Profile {
        PROXY(ProresConsts.QMAT_LUMA_APCO, ProresConsts.QMAT_CHROMA_APCO, "apco", 1000, 4, 8),
        LT(ProresConsts.QMAT_LUMA_APCS, ProresConsts.QMAT_CHROMA_APCS, "apcs", 2100, 1, 9),
        STANDARD(ProresConsts.QMAT_LUMA_APCN, ProresConsts.QMAT_CHROMA_APCN, "apcn", 3500, 1, 6),
        HQ(ProresConsts.QMAT_LUMA_APCH, ProresConsts.QMAT_CHROMA_APCH, "apch", 5400, 1, 6);

        final int bitrate;
        final int firstQp;
        public final String fourcc;
        final int lastQp;
        final int[] qmatChroma;
        final int[] qmatLuma;

        Profile(int[] qmatLuma, int[] qmatChroma, String fourcc, int bitrate, int firstQp, int lastQp) {
            this.qmatLuma = qmatLuma;
            this.qmatChroma = qmatChroma;
            this.fourcc = fourcc;
            this.bitrate = bitrate;
            this.firstQp = firstQp;
            this.lastQp = lastQp;
        }
    }

    public ProresEncoder(Profile profile) {
        this.profile = profile;
        this.scaledLuma = scaleQMat(profile.qmatLuma, 1, 16);
        this.scaledChroma = scaleQMat(profile.qmatChroma, 1, 16);
    }

    private int[][] scaleQMat(int[] qmatLuma, int start, int count) {
        int[][] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = new int[qmatLuma.length];
            for (int j = 0; j < qmatLuma.length; j++) {
                result[i][j] = qmatLuma[j] * (i + start);
            }
        }
        return result;
    }

    public static final void writeCodeword(BitWriter writer, Codebook codebook, int val) {
        int firstExp = (codebook.switchBits + 1) << codebook.riceOrder;
        if (val >= firstExp) {
            int val2 = (val - firstExp) + (1 << codebook.expOrder);
            int exp = MathUtil.log2(val2);
            int zeros = (exp - codebook.expOrder) + codebook.switchBits + 1;
            for (int i = 0; i < zeros; i++) {
                writer.write1Bit(0);
            }
            writer.write1Bit(1);
            writer.writeNBit(val2, exp);
            return;
        }
        if (codebook.riceOrder > 0) {
            for (int i2 = 0; i2 < (val >> codebook.riceOrder); i2++) {
                writer.write1Bit(0);
            }
            writer.write1Bit(1);
            writer.writeNBit(((1 << codebook.riceOrder) - 1) & val, codebook.riceOrder);
            return;
        }
        for (int i3 = 0; i3 < val; i3++) {
            writer.write1Bit(0);
        }
        writer.write1Bit(1);
    }

    private static final int qScale(int[] qMat, int ind, int val) {
        return val / qMat[ind];
    }

    private static final int toGolumb(int val) {
        return (val << 1) ^ (val >> 31);
    }

    private static final int toGolumb(int val, int sign) {
        if (val == 0) {
            return 0;
        }
        return (val << 1) + sign;
    }

    private static final int diffSign(int val, int sign) {
        return (val >> 31) ^ sign;
    }

    public static final int getLevel(int val) {
        int sign = val >> 31;
        return (val ^ sign) - sign;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void writeDCCoeffs(BitWriter bits, int[] qMat, int[] in, int blocksPerSlice) {
        int prevDc = qScale(qMat, 0, in[0] - 16384);
        writeCodeword(bits, ProresConsts.firstDCCodebook, toGolumb(prevDc));
        int code = 5;
        int sign = 0;
        int idx = 64;
        int i = 1;
        while (i < blocksPerSlice) {
            int newDc = qScale(qMat, 0, in[idx] - 16384);
            int delta = newDc - prevDc;
            int newCode = toGolumb(getLevel(delta), diffSign(delta, sign));
            writeCodeword(bits, ProresConsts.dcCodebooks[Math.min(code, 6)], newCode);
            code = newCode;
            sign = delta >> 31;
            prevDc = newDc;
            i++;
            idx += 64;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void writeACCoeffs(BitWriter bits, int[] qMat, int[] in, int blocksPerSlice, int[] scan, int maxCoeff) {
        int prevRun = 4;
        int prevLevel = 2;
        int run = 0;
        for (int i = 1; i < maxCoeff; i++) {
            int indp = scan[i];
            for (int j = 0; j < blocksPerSlice; j++) {
                int val = qScale(qMat, indp, in[(j << 6) + indp]);
                if (val == 0) {
                    run++;
                } else {
                    writeCodeword(bits, ProresConsts.runCodebooks[Math.min(prevRun, 15)], run);
                    prevRun = run;
                    run = 0;
                    int level = getLevel(val);
                    writeCodeword(bits, ProresConsts.levCodebooks[Math.min(prevLevel, 9)], level - 1);
                    prevLevel = level;
                    bits.write1Bit(MathUtil.sign(val));
                }
            }
        }
    }

    static final void encodeOnePlane(BitWriter bits, int blocksPerSlice, int[] qMat, int[] scan, int[] in) {
        writeDCCoeffs(bits, qMat, in, blocksPerSlice);
        writeACCoeffs(bits, qMat, in, blocksPerSlice, scan, 64);
    }

    private void dctOnePlane(int blocksPerSlice, int[] in) {
        for (int i = 0; i < blocksPerSlice; i++) {
            DCTRef.fdct(in, i << 6);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x00ad, code lost:
    
        if (r6 > r13.profile.firstQp) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x00af, code lost:
    
        r6 = r6 - 1;
        r14.position(r12);
        encodeSliceData(r14, r15[r6 - 1], r16[r6 - 1], r17, r18, r5, r6, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00c8, code lost:
    
        if (bits(r7) >= r11) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x00ce, code lost:
    
        if (r6 > r13.profile.firstQp) goto L26;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected int encodeSlice(java.nio.ByteBuffer r14, int[][] r15, int[][] r16, int[] r17, int r18, int r19, int r20, org.jcodec.common.model.Picture r21, int r22, int r23, int r24, boolean r25) {
        /*
            r13 = this;
            r0 = r13
            r1 = r21
            r2 = r19
            r3 = r20
            r4 = r18
            r5 = r25
            org.jcodec.common.model.Picture r5 = r0.splitSlice(r1, r2, r3, r4, r5)
            int r0 = r18 << 2
            r1 = 0
            int[] r1 = r5.getPlaneData(r1)
            r13.dctOnePlane(r0, r1)
            int r0 = r18 << 1
            r1 = 1
            int[] r1 = r5.getPlaneData(r1)
            r13.dctOnePlane(r0, r1)
            int r0 = r18 << 1
            r1 = 2
            int[] r1 = r5.getPlaneData(r1)
            r13.dctOnePlane(r0, r1)
            int r0 = r18 >> 2
            org.jcodec.codecs.prores.ProresEncoder$Profile r1 = r13.profile
            int r1 = r1.bitrate
            int r8 = r0 * r1
            int r0 = r8 >> 3
            int r11 = r8 - r0
            int r0 = r8 >> 3
            int r10 = r8 + r0
            r6 = r22
            r0 = 48
            r14.put(r0)
            java.nio.ByteBuffer r9 = r14.duplicate()
            r0 = 5
            org.jcodec.common.NIOUtils.skip(r14, r0)
            int r12 = r14.position()
            r0 = 3
            int[] r7 = new int[r0]
            int r0 = r6 + (-1)
            r1 = r15[r0]
            int r0 = r6 + (-1)
            r2 = r16[r0]
            r0 = r14
            r3 = r17
            r4 = r18
            encodeSliceData(r0, r1, r2, r3, r4, r5, r6, r7)
            int r0 = bits(r7)
            if (r0 <= r10) goto La3
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.lastQp
            if (r6 >= r0) goto La3
        L6f:
            int r6 = r6 + 1
            r14.position(r12)
            int r0 = r6 + (-1)
            r1 = r15[r0]
            int r0 = r6 + (-1)
            r2 = r16[r0]
            r0 = r14
            r3 = r17
            r4 = r18
            encodeSliceData(r0, r1, r2, r3, r4, r5, r6, r7)
            int r0 = bits(r7)
            if (r0 <= r10) goto L90
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.lastQp
            if (r6 < r0) goto L6f
        L90:
            byte r0 = (byte) r6
            r9.put(r0)
            r0 = 0
            r0 = r7[r0]
            short r0 = (short) r0
            r9.putShort(r0)
            r0 = 1
            r0 = r7[r0]
            short r0 = (short) r0
            r9.putShort(r0)
            return r6
        La3:
            int r0 = bits(r7)
            if (r0 >= r11) goto L90
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.firstQp
            if (r6 <= r0) goto L90
        Laf:
            int r6 = r6 + (-1)
            r14.position(r12)
            int r0 = r6 + (-1)
            r1 = r15[r0]
            int r0 = r6 + (-1)
            r2 = r16[r0]
            r0 = r14
            r3 = r17
            r4 = r18
            encodeSliceData(r0, r1, r2, r3, r4, r5, r6, r7)
            int r0 = bits(r7)
            if (r0 >= r11) goto L90
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.firstQp
            if (r6 > r0) goto Laf
            goto L90
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.codecs.prores.ProresEncoder.encodeSlice(java.nio.ByteBuffer, int[][], int[][], int[], int, int, int, org.jcodec.common.model.Picture, int, int, int, boolean):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int bits(int[] sizes) {
        return ((sizes[0] + sizes[1]) + sizes[2]) << 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final void encodeSliceData(ByteBuffer out, int[] qmatLuma, int[] qmatChroma, int[] scan, int sliceMbCount, Picture striped, int qp, int[] sizes) {
        sizes[0] = onePlane(out, sliceMbCount << 2, qmatLuma, scan, striped.getPlaneData(0));
        sizes[1] = onePlane(out, sliceMbCount << 1, qmatChroma, scan, striped.getPlaneData(1));
        sizes[2] = onePlane(out, sliceMbCount << 1, qmatChroma, scan, striped.getPlaneData(2));
    }

    static final int onePlane(ByteBuffer out, int blocksPerSlice, int[] qmatLuma, int[] scan, int[] data) {
        int rem = out.position();
        BitWriter bits = new BitWriter(out);
        encodeOnePlane(bits, blocksPerSlice, qmatLuma, scan, data);
        bits.flush();
        return out.position() - rem;
    }

    protected void encodePicture(ByteBuffer out, int[][] scaledLuma, int[][] scaledChroma, int[] scan, Picture picture) {
        int mbWidth = (picture.getWidth() + 15) >> 4;
        int mbHeight = (picture.getHeight() + 15) >> 4;
        int qp = this.profile.firstQp;
        int nSlices = calcNSlices(mbWidth, mbHeight);
        writePictureHeader(3, nSlices, out);
        ByteBuffer fork = out.duplicate();
        NIOUtils.skip(out, nSlices << 1);
        int i = 0;
        int[] total = new int[nSlices];
        int mbY = 0;
        while (mbY < mbHeight) {
            int mbX = 0;
            int sliceMbCount = 8;
            int i2 = i;
            while (mbX < mbWidth) {
                while (mbWidth - mbX < sliceMbCount) {
                    sliceMbCount >>= 1;
                }
                int sliceStart = out.position();
                boolean unsafeBottom = picture.getHeight() % 16 != 0 && mbY == mbHeight + (-1);
                boolean unsafeRight = picture.getWidth() % 16 != 0 && mbX + sliceMbCount == mbWidth;
                qp = encodeSlice(out, scaledLuma, scaledChroma, scan, sliceMbCount, mbX, mbY, picture, qp, mbWidth, mbHeight, unsafeBottom || unsafeRight);
                fork.putShort((short) (out.position() - sliceStart));
                total[i2] = (short) (out.position() - sliceStart);
                mbX += sliceMbCount;
                i2++;
            }
            mbY++;
            i = i2;
        }
    }

    public static void writePictureHeader(int logDefaultSliceMbWidth, int nSlices, ByteBuffer out) {
        out.put((byte) 64);
        out.putInt(0);
        out.putShort((short) nSlices);
        out.put((byte) (logDefaultSliceMbWidth << 4));
    }

    private int calcNSlices(int mbWidth, int mbHeight) {
        int nSlices = mbWidth >> 3;
        for (int i = 0; i < 3; i++) {
            nSlices += (mbWidth >> i) & 1;
        }
        return nSlices * mbHeight;
    }

    private Picture splitSlice(Picture result, int mbX, int mbY, int sliceMbCount, boolean unsafe) {
        Picture out = Picture.create(sliceMbCount << 4, 16, ColorSpace.YUV422_10);
        if (unsafe) {
            Picture filled = Picture.create(sliceMbCount << 4, 16, ColorSpace.YUV422_10);
            ImageOP.subImageWithFill(result, filled, new Rect(mbX << 4, mbY << 4, sliceMbCount << 4, 16));
            split(filled, out, 0, 0, sliceMbCount);
        } else {
            split(result, out, mbX, mbY, sliceMbCount);
        }
        return out;
    }

    private void split(Picture in, Picture out, int mbX, int mbY, int sliceMbCount) {
        split(in.getPlaneData(0), out.getPlaneData(0), in.getPlaneWidth(0), mbX, mbY, sliceMbCount, 0);
        split(in.getPlaneData(1), out.getPlaneData(1), in.getPlaneWidth(1), mbX, mbY, sliceMbCount, 1);
        split(in.getPlaneData(2), out.getPlaneData(2), in.getPlaneWidth(2), mbX, mbY, sliceMbCount, 1);
    }

    private int[] split(int[] in, int[] out, int stride, int mbX, int mbY, int sliceMbCount, int chroma) {
        int outOff = 0;
        int off = ((mbY << 4) * stride) + (mbX << (4 - chroma));
        for (int i = 0; i < sliceMbCount; i++) {
            splitBlock(in, stride, off, out, outOff);
            splitBlock(in, stride, off + (stride << 3), out, outOff + (128 >> chroma));
            if (chroma == 0) {
                splitBlock(in, stride, off + 8, out, outOff + 64);
                splitBlock(in, stride, (stride << 3) + off + 8, out, outOff + JpegConst.SOF0);
            }
            outOff += 256 >> chroma;
            off += 16 >> chroma;
        }
        return out;
    }

    private void splitBlock(int[] y, int stride, int off, int[] out, int outOff) {
        int i = 0;
        while (i < 8) {
            int j = 0;
            int outOff2 = outOff;
            int off2 = off;
            while (j < 8) {
                out[outOff2] = y[off2];
                j++;
                outOff2++;
                off2++;
            }
            off = off2 + (stride - 8);
            i++;
            outOff = outOff2;
        }
    }

    public void encodeFrame(ByteBuffer out, Picture... pics) {
        ByteBuffer fork = out.duplicate();
        int[] scan = pics.length > 1 ? ProresConsts.interlaced_scan : ProresConsts.progressive_scan;
        writeFrameHeader(out, new ProresConsts.FrameHeader(0, pics[0].getCroppedWidth(), pics[0].getCroppedHeight() * pics.length, pics.length == 1 ? 0 : 1, true, scan, this.profile.qmatLuma, this.profile.qmatChroma, 2));
        encodePicture(out, this.scaledLuma, this.scaledChroma, scan, pics[0]);
        if (pics.length > 1) {
            encodePicture(out, this.scaledLuma, this.scaledChroma, scan, pics[1]);
        }
        out.flip();
        fork.putInt(out.remaining());
    }

    public static void writeFrameHeader(ByteBuffer outp, ProresConsts.FrameHeader header) {
        outp.putInt(header.payloadSize + 156);
        outp.put(new byte[]{105, 99, 112, 102});
        outp.putShort((short) 148);
        outp.putShort((short) 0);
        outp.put(new byte[]{97, 112, 108, 48});
        outp.putShort((short) header.width);
        outp.putShort((short) header.height);
        outp.put((byte) (header.frameType == 0 ? 131 : 135));
        outp.put(new byte[]{0, 2, 2, 6, 32, 0});
        outp.put((byte) 3);
        writeQMat(outp, header.qMatLuma);
        writeQMat(outp, header.qMatChroma);
    }

    static final void writeQMat(ByteBuffer out, int[] qmat) {
        for (int i = 0; i < 64; i++) {
            out.put((byte) qmat[i]);
        }
    }
}
