package org.jcodec.codecs.prores;

import android.support.v4.view.PointerIconCompat;
import java.nio.ByteBuffer;
import org.jcodec.codecs.prores.ProresConsts;
import org.jcodec.common.dct.IDCT2x2;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class ProresToThumb2x2 extends ProresDecoder {
    @Override // org.jcodec.codecs.prores.ProresDecoder
    protected int[] decodeOnePlane(BitReader bits, int blocksPerSlice, int[] qMat, int[] scan, int mbX, int mbY, int plane) {
        int[] out = new int[blocksPerSlice << 2];
        readDCCoeffs(bits, qMat, out, blocksPerSlice, 4);
        readACCoeffs(bits, qMat, out, blocksPerSlice, scan, 4, 2);
        for (int i = 0; i < blocksPerSlice; i++) {
            IDCT2x2.idct(out, i << 2);
        }
        return out;
    }

    @Override // org.jcodec.codecs.prores.ProresDecoder, org.jcodec.common.VideoDecoder
    public Picture decodeFrame(ByteBuffer data, int[][] target) {
        ProresConsts.FrameHeader fh = readFrameHeader(data);
        int codedWidth = ((fh.width + 15) & (-16)) >> 2;
        int codedHeight = ((fh.height + 15) & (-16)) >> 2;
        int lumaSize = codedWidth * codedHeight;
        int chromaSize = lumaSize >> 1;
        if (target == null || target[0].length < lumaSize || target[1].length < chromaSize || target[2].length < chromaSize) {
            throw new RuntimeException("Provided output picture won't fit into provided buffer");
        }
        if (fh.frameType == 0) {
            decodePicture(data, target, codedWidth, codedHeight, codedWidth >> 2, fh.qMatLuma, fh.qMatChroma, new int[]{0, 1, 2, 3}, 0, fh.chromaType);
        } else {
            decodePicture(data, target, codedWidth, codedHeight >> 1, codedWidth >> 2, fh.qMatLuma, fh.qMatChroma, new int[]{0, 2, 1, 3}, fh.topFieldFirst ? 1 : 2, fh.chromaType);
            decodePicture(data, target, codedWidth, codedHeight >> 1, codedWidth >> 2, fh.qMatLuma, fh.qMatChroma, new int[]{0, 2, 1, 3}, fh.topFieldFirst ? 2 : 1, fh.chromaType);
        }
        return new Picture(codedWidth, codedHeight, target, fh.chromaType == 2 ? ColorSpace.YUV422_10 : ColorSpace.YUV444_10);
    }

    @Override // org.jcodec.codecs.prores.ProresDecoder
    protected void putSlice(int[][] result, int lumaStride, int mbX, int mbY, int[] y, int[] u, int[] v, int dist, int shift, int chromaType) {
        int mbPerSlice = y.length >> 4;
        int chromaStride = lumaStride >> 1;
        putLuma(result[0], shift * lumaStride, lumaStride << dist, mbX, mbY, y, mbPerSlice, dist, shift);
        if (chromaType == 2) {
            putChroma(result[1], shift * chromaStride, chromaStride << dist, mbX, mbY, u, mbPerSlice, dist, shift);
            putChroma(result[2], shift * chromaStride, chromaStride << dist, mbX, mbY, v, mbPerSlice, dist, shift);
        } else {
            putLuma(result[1], shift * lumaStride, lumaStride << dist, mbX, mbY, u, mbPerSlice, dist, shift);
            putLuma(result[2], shift * lumaStride, lumaStride << dist, mbX, mbY, v, mbPerSlice, dist, shift);
        }
    }

    private void putLuma(int[] y, int off, int stride, int mbX, int mbY, int[] luma, int mbPerSlice, int dist, int shift) {
        int off2 = off + (mbX << 2) + ((mbY << 2) * stride);
        int tstride = stride * 3;
        int sOff = 0;
        for (int k = 0; k < mbPerSlice; k++) {
            y[off2] = clip(luma[sOff], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off2 + 1] = clip(luma[sOff + 1], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off2 + 2] = clip(luma[sOff + 4], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off2 + 3] = clip(luma[sOff + 5], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            int off3 = off2 + stride;
            y[off3] = clip(luma[sOff + 2], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off3 + 1] = clip(luma[sOff + 3], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off3 + 2] = clip(luma[sOff + 6], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off3 + 3] = clip(luma[sOff + 7], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            int off4 = off3 + stride;
            y[off4] = clip(luma[sOff + 8], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off4 + 1] = clip(luma[sOff + 9], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off4 + 2] = clip(luma[sOff + 12], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off4 + 3] = clip(luma[sOff + 13], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            int off5 = off4 + stride;
            y[off5] = clip(luma[sOff + 10], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off5 + 1] = clip(luma[sOff + 11], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off5 + 2] = clip(luma[sOff + 14], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off5 + 3] = clip(luma[sOff + 15], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            off2 = off5 + (4 - tstride);
            sOff += 16;
        }
    }

    private void putChroma(int[] y, int off, int stride, int mbX, int mbY, int[] chroma, int mbPerSlice, int dist, int shift) {
        int tstride = stride * 3;
        int off2 = off + (mbX << 1) + ((mbY << 2) * stride);
        int sOff = 0;
        for (int k = 0; k < mbPerSlice; k++) {
            y[off2] = clip(chroma[sOff], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off2 + 1] = clip(chroma[sOff + 1], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            int off3 = off2 + stride;
            y[off3] = clip(chroma[sOff + 2], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off3 + 1] = clip(chroma[sOff + 3], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            int off4 = off3 + stride;
            y[off4] = clip(chroma[sOff + 4], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off4 + 1] = clip(chroma[sOff + 5], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            int off5 = off4 + stride;
            y[off5] = clip(chroma[sOff + 6], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            y[off5 + 1] = clip(chroma[sOff + 7], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            off2 = off5 + (2 - tstride);
            sOff += 8;
        }
    }
}
