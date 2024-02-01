package org.jcodec.codecs.prores;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.codecs.prores.ProresConsts;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class ProresFix {
    static final void readDCCoeffs(BitReader bits, int[] out, int blocksPerSlice) {
        out[0] = ProresDecoder.readCodeword(bits, ProresConsts.firstDCCodebook);
        if (out[0] < 0) {
            throw new RuntimeException("First DC coeff damaged");
        }
        int code = 5;
        int idx = 64;
        int i = 1;
        while (i < blocksPerSlice) {
            code = ProresDecoder.readCodeword(bits, ProresConsts.dcCodebooks[Math.min(code, 6)]);
            if (code < 0) {
                throw new RuntimeException("DC coeff damaged");
            }
            out[idx] = code;
            i++;
            idx += 64;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x003a, code lost:
    
        throw new java.lang.RuntimeException("Run codeword damaged");
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x005c, code lost:
    
        throw new java.lang.RuntimeException("Level codeword damaged");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static final void readACCoeffs(org.jcodec.common.io.BitReader r10, int[] r11, int r12, int[] r13) {
        /*
            r6 = 4
            r2 = 2
            int r0 = r12 + (-1)
            int r3 = org.jcodec.common.tools.MathUtil.log2(r12)
            r8 = 64
            int r4 = r8 << r3
            r5 = r0
        Ld:
            int r8 = r10.remaining()
            r9 = 32
            if (r8 > r9) goto L1d
            r8 = 24
            int r8 = r10.checkNBit(r8)
            if (r8 == 0) goto L72
        L1d:
            org.jcodec.codecs.prores.Codebook[] r8 = org.jcodec.codecs.prores.ProresConsts.runCodebooks
            r9 = 15
            int r9 = java.lang.Math.min(r6, r9)
            r8 = r8[r9]
            int r6 = org.jcodec.codecs.prores.ProresDecoder.readCodeword(r10, r8)
            if (r6 < 0) goto L33
            int r8 = r4 - r5
            int r8 = r8 + (-1)
            if (r6 < r8) goto L3b
        L33:
            java.lang.RuntimeException r8 = new java.lang.RuntimeException
            java.lang.String r9 = "Run codeword damaged"
            r8.<init>(r9)
            throw r8
        L3b:
            int r8 = r6 + 1
            int r5 = r5 + r8
            org.jcodec.codecs.prores.Codebook[] r8 = org.jcodec.codecs.prores.ProresConsts.levCodebooks
            r9 = 9
            int r9 = java.lang.Math.min(r2, r9)
            r8 = r8[r9]
            int r8 = org.jcodec.codecs.prores.ProresDecoder.readCodeword(r10, r8)
            int r2 = r8 + 1
            if (r2 < 0) goto L55
            r8 = 65535(0xffff, float:9.1834E-41)
            if (r2 <= r8) goto L5d
        L55:
            java.lang.RuntimeException r8 = new java.lang.RuntimeException
            java.lang.String r9 = "Level codeword damaged"
            r8.<init>(r9)
            throw r8
        L5d:
            int r8 = r10.read1Bit()
            int r7 = -r8
            int r1 = r5 >> r3
            r8 = r5 & r0
            int r8 = r8 << 6
            r9 = r13[r1]
            int r8 = r8 + r9
            int r9 = org.jcodec.common.tools.MathUtil.toSigned(r2, r7)
            r11[r8] = r9
            goto Ld
        L72:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.codecs.prores.ProresFix.readACCoeffs(org.jcodec.common.io.BitReader, int[], int, int[]):void");
    }

    static final void writeDCCoeffs(BitWriter bits, int[] in, int blocksPerSlice) {
        ProresEncoder.writeCodeword(bits, ProresConsts.firstDCCodebook, in[0]);
        int code = 5;
        int idx = 64;
        int i = 1;
        while (i < blocksPerSlice) {
            ProresEncoder.writeCodeword(bits, ProresConsts.dcCodebooks[Math.min(code, 6)], in[idx]);
            code = in[idx];
            i++;
            idx += 64;
        }
    }

    static final void writeACCoeffs(BitWriter bits, int[] in, int blocksPerSlice, int[] scan) {
        int prevRun = 4;
        int prevLevel = 2;
        int run = 0;
        for (int i = 1; i < 64; i++) {
            int indp = scan[i];
            for (int j = 0; j < blocksPerSlice; j++) {
                int val = in[(j << 6) + indp];
                if (val == 0) {
                    run++;
                } else {
                    ProresEncoder.writeCodeword(bits, ProresConsts.runCodebooks[Math.min(prevRun, 15)], run);
                    prevRun = run;
                    run = 0;
                    int level = ProresEncoder.getLevel(val);
                    ProresEncoder.writeCodeword(bits, ProresConsts.levCodebooks[Math.min(prevLevel, 9)], level - 1);
                    prevLevel = level;
                    bits.write1Bit(MathUtil.sign(val));
                }
            }
        }
    }

    static void copyCoeff(BitReader ib, BitWriter ob, int blocksPerSlice, int[] scan) {
        int[] out = new int[blocksPerSlice << 6];
        try {
            readDCCoeffs(ib, out, blocksPerSlice);
            readACCoeffs(ib, out, blocksPerSlice, scan);
        } catch (RuntimeException e) {
        }
        writeDCCoeffs(ob, out, blocksPerSlice);
        writeACCoeffs(ob, out, blocksPerSlice, scan);
        ob.flush();
    }

    public static ByteBuffer transcode(ByteBuffer inBuf, ByteBuffer _outBuf) {
        ByteBuffer outBuf = _outBuf.slice();
        ByteBuffer fork = outBuf.duplicate();
        ProresConsts.FrameHeader fh = ProresDecoder.readFrameHeader(inBuf);
        ProresEncoder.writeFrameHeader(outBuf, fh);
        if (fh.frameType == 0) {
            transcodePicture(inBuf, outBuf, fh);
        } else {
            transcodePicture(inBuf, outBuf, fh);
            transcodePicture(inBuf, outBuf, fh);
        }
        ProresEncoder.writeFrameHeader(fork, fh);
        outBuf.flip();
        return outBuf;
    }

    private static void transcodePicture(ByteBuffer inBuf, ByteBuffer outBuf, ProresConsts.FrameHeader fh) {
        ProresConsts.PictureHeader ph = ProresDecoder.readPictureHeader(inBuf);
        ProresEncoder.writePictureHeader(ph.log2SliceMbWidth, ph.sliceSizes.length, outBuf);
        ByteBuffer fork = outBuf.duplicate();
        outBuf.position(outBuf.position() + (ph.sliceSizes.length << 1));
        int mbWidth = (fh.width + 15) >> 4;
        int sliceMbCount = 1 << ph.log2SliceMbWidth;
        int mbX = 0;
        for (int i = 0; i < ph.sliceSizes.length; i++) {
            while (mbWidth - mbX < sliceMbCount) {
                sliceMbCount >>= 1;
            }
            int savedPoint = outBuf.position();
            transcodeSlice(inBuf, outBuf, sliceMbCount, ph.sliceSizes[i], fh);
            fork.putShort((short) (outBuf.position() - savedPoint));
            mbX += sliceMbCount;
            if (mbX == mbWidth) {
                sliceMbCount = 1 << ph.log2SliceMbWidth;
                mbX = 0;
            }
        }
    }

    private static void transcodeSlice(ByteBuffer inBuf, ByteBuffer outBuf, int sliceMbCount, short sliceSize, ProresConsts.FrameHeader fh) {
        int hdrSize = (inBuf.get() & 255) >> 3;
        int qScaleOrig = inBuf.get() & 255;
        int yDataSize = inBuf.getShort();
        int uDataSize = inBuf.getShort();
        int vDataSize = ((sliceSize - uDataSize) - yDataSize) - hdrSize;
        outBuf.put((byte) 48);
        outBuf.put((byte) qScaleOrig);
        ByteBuffer beforeSizes = outBuf.duplicate();
        outBuf.putInt(0);
        int beforeY = outBuf.position();
        copyCoeff(ProresDecoder.bitstream(inBuf, yDataSize), new BitWriter(outBuf), sliceMbCount << 2, fh.scan);
        int beforeCb = outBuf.position();
        copyCoeff(ProresDecoder.bitstream(inBuf, uDataSize), new BitWriter(outBuf), sliceMbCount << 1, fh.scan);
        int beforeCr = outBuf.position();
        copyCoeff(ProresDecoder.bitstream(inBuf, vDataSize), new BitWriter(outBuf), sliceMbCount << 1, fh.scan);
        beforeSizes.putShort((short) (beforeCb - beforeY));
        beforeSizes.putShort((short) (beforeCr - beforeCb));
    }

    public static List<String> check(ByteBuffer data) {
        List<String> messages = new ArrayList<>();
        data.getInt();
        if (!"icpf".equals(ProresDecoder.readSig(data))) {
            messages.add("[ERROR] Missing ProRes signature (icpf).");
        } else {
            short headerSize = data.getShort();
            if (headerSize > 148) {
                messages.add("[ERROR] Wrong ProRes frame header.");
            } else {
                data.getShort();
                data.getInt();
                short width = data.getShort();
                short height = data.getShort();
                if (width < 0 || width > 10000 || height < 0 || height > 10000) {
                    messages.add("[ERROR] Wrong ProRes frame header, invalid image size [" + ((int) width) + "x" + ((int) height) + "].");
                } else {
                    int flags1 = data.get();
                    data.position((data.position() + headerSize) - 13);
                    if (((flags1 >> 2) & 3) == 0) {
                        checkPicture(data, width, height, messages);
                    } else {
                        checkPicture(data, width, height / 2, messages);
                        checkPicture(data, width, height / 2, messages);
                    }
                }
            }
        }
        return messages;
    }

    private static void checkPicture(ByteBuffer data, int width, int height, List<String> messages) {
        ProresConsts.PictureHeader ph = ProresDecoder.readPictureHeader(data);
        int mbWidth = (width + 15) >> 4;
        int i = (height + 15) >> 4;
        int sliceMbCount = 1 << ph.log2SliceMbWidth;
        int mbX = 0;
        int mbY = 0;
        for (int i2 = 0; i2 < ph.sliceSizes.length; i2++) {
            while (mbWidth - mbX < sliceMbCount) {
                sliceMbCount >>= 1;
            }
            try {
                checkSlice(NIOUtils.read(data, ph.sliceSizes[i2]), sliceMbCount);
            } catch (Exception e) {
                messages.add("[ERROR] Slice data corrupt: mbX = " + mbX + ", mbY = " + mbY + ". " + e.getMessage());
            }
            mbX += sliceMbCount;
            if (mbX == mbWidth) {
                sliceMbCount = 1 << ph.log2SliceMbWidth;
                mbX = 0;
                mbY++;
            }
        }
    }

    private static void checkSlice(ByteBuffer sliceData, int sliceMbCount) {
        int sliceSize = sliceData.remaining();
        int hdrSize = (sliceData.get() & 255) >> 3;
        int i = sliceData.get() & 255;
        int yDataSize = sliceData.getShort();
        int uDataSize = sliceData.getShort();
        int vDataSize = ((sliceSize - uDataSize) - yDataSize) - hdrSize;
        checkCoeff(ProresDecoder.bitstream(sliceData, yDataSize), sliceMbCount << 2);
        checkCoeff(ProresDecoder.bitstream(sliceData, uDataSize), sliceMbCount << 1);
        checkCoeff(ProresDecoder.bitstream(sliceData, vDataSize), sliceMbCount << 1);
    }

    private static void checkCoeff(BitReader ib, int blocksPerSlice) {
        int[] scan = new int[64];
        int[] out = new int[blocksPerSlice << 6];
        readDCCoeffs(ib, out, blocksPerSlice);
        readACCoeffs(ib, out, blocksPerSlice, scan);
    }
}
