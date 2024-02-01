package org.jcodec.codecs.prores;

import org.jcodec.codecs.prores.ProresEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class DCT2Prores extends ProresEncoder {
    public DCT2Prores(ProresEncoder.Profile profile) {
        super(profile);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x008f, code lost:
    
        if (r6 > r13.profile.firstQp) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0091, code lost:
    
        r6 = r6 - 1;
        r14.position(r12);
        encodeSliceData(r14, r15[r6 - 1], r16[r6 - 1], r17, r18, r5, r6, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00aa, code lost:
    
        if (bits(r7) >= r11) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x00b0, code lost:
    
        if (r6 > r13.profile.firstQp) goto L26;
     */
    @Override // org.jcodec.codecs.prores.ProresEncoder
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
            r4 = r23
            r5 = r18
            org.jcodec.common.model.Picture r5 = r0.sliceData(r1, r2, r3, r4, r5)
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
            byte r0 = (byte) r6
            r14.put(r0)
            java.nio.ByteBuffer r9 = r14.duplicate()
            r0 = 0
            r14.putInt(r0)
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
            if (r0 <= r10) goto L85
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.lastQp
            if (r6 >= r0) goto L85
        L55:
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
            if (r0 <= r10) goto L76
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.lastQp
            if (r6 < r0) goto L55
        L76:
            r0 = 0
            r0 = r7[r0]
            short r0 = (short) r0
            r9.putShort(r0)
            r0 = 1
            r0 = r7[r0]
            short r0 = (short) r0
            r9.putShort(r0)
            return r6
        L85:
            int r0 = bits(r7)
            if (r0 >= r11) goto L76
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.firstQp
            if (r6 <= r0) goto L76
        L91:
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
            if (r0 >= r11) goto L76
            org.jcodec.codecs.prores.ProresEncoder$Profile r0 = r13.profile
            int r0 = r0.firstQp
            if (r6 > r0) goto L91
            goto L76
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.codecs.prores.DCT2Prores.encodeSlice(java.nio.ByteBuffer, int[][], int[][], int[], int, int, int, org.jcodec.common.model.Picture, int, int, int, boolean):int");
    }

    private Picture sliceData(Picture source, int mbX, int mbY, int mbWidth, int sliceMbCount) {
        Picture pic = Picture.create(sliceMbCount << 4, 16, ColorSpace.YUV422_10);
        int[][] out = pic.getData();
        int[][] in = source.getData();
        System.arraycopy(in[0], ((mbY * mbWidth) + mbX) << 8, out[0], 0, out[0].length);
        System.arraycopy(in[1], ((mbY * mbWidth) + mbX) << 7, out[1], 0, out[1].length);
        System.arraycopy(in[2], ((mbY * mbWidth) + mbX) << 7, out[2], 0, out[2].length);
        return pic;
    }
}
