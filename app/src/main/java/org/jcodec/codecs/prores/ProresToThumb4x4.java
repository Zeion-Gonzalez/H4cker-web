package org.jcodec.codecs.prores;

import android.support.v4.view.PointerIconCompat;
import java.nio.ByteBuffer;
import org.jcodec.codecs.prores.ProresConsts;
import org.jcodec.common.dct.IDCT4x4;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class ProresToThumb4x4 extends ProresDecoder {
    public static int[] progressive_scan_4x4 = {0, 1, 4, 5, 2, 3, 6, 7, 8, 9, 12, 13, 11, 12, 14, 15};
    public static int[] interlaced_scan_4x4 = {0, 4, 1, 5, 8, 12, 9, 13, 2, 6, 3, 7, 10, 14, 11, 15};
    private static final int[] srcIncLuma = {4, 4, 4, 20, 4, 4, 4, 20};

    @Override // org.jcodec.codecs.prores.ProresDecoder
    protected int[] decodeOnePlane(BitReader bits, int blocksPerSlice, int[] qMat, int[] scan, int mbX, int mbY, int plane) {
        int[] out = new int[blocksPerSlice << 4];
        readDCCoeffs(bits, qMat, out, blocksPerSlice, 16);
        readACCoeffs(bits, qMat, out, blocksPerSlice, scan, 16, 4);
        for (int i = 0; i < blocksPerSlice; i++) {
            IDCT4x4.idct(out, i << 4);
        }
        return out;
    }

    @Override // org.jcodec.codecs.prores.ProresDecoder, org.jcodec.common.VideoDecoder
    public Picture decodeFrame(ByteBuffer data, int[][] target) {
        ProresConsts.FrameHeader fh = readFrameHeader(data);
        int codedWidth = ((fh.width + 15) & (-16)) >> 1;
        int codedHeight = ((fh.height + 15) & (-16)) >> 1;
        int lumaSize = codedWidth * codedHeight;
        int chromaSize = lumaSize >> 1;
        if (target == null || target[0].length < lumaSize || target[1].length < chromaSize || target[2].length < chromaSize) {
            throw new RuntimeException("Provided output picture won't fit into provided buffer");
        }
        if (fh.frameType == 0) {
            decodePicture(data, target, codedWidth, codedHeight, codedWidth >> 3, fh.qMatLuma, fh.qMatChroma, progressive_scan_4x4, 0, fh.chromaType);
        } else {
            decodePicture(data, target, codedWidth, codedHeight >> 1, codedWidth >> 3, fh.qMatLuma, fh.qMatChroma, interlaced_scan_4x4, fh.topFieldFirst ? 1 : 2, fh.chromaType);
            decodePicture(data, target, codedWidth, codedHeight >> 1, codedWidth >> 3, fh.qMatLuma, fh.qMatChroma, interlaced_scan_4x4, fh.topFieldFirst ? 2 : 1, fh.chromaType);
        }
        return new Picture(codedWidth, codedHeight, target, fh.chromaType == 2 ? ColorSpace.YUV422_10 : ColorSpace.YUV444_10);
    }

    @Override // org.jcodec.codecs.prores.ProresDecoder
    protected void putSlice(int[][] result, int lumaStride, int mbX, int mbY, int[] y, int[] u, int[] v, int dist, int shift, int chromaType) {
        int mbPerSlice = y.length >> 6;
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
        int off2 = off + (mbX << 3) + ((mbY << 3) * stride);
        int mb = 0;
        int sOff = 0;
        while (mb < mbPerSlice) {
            int _off = off2;
            for (int line = 0; line < 8; line++) {
                y[_off] = clip(luma[sOff], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 1] = clip(luma[sOff + 1], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 2] = clip(luma[sOff + 2], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 3] = clip(luma[sOff + 3], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 4] = clip(luma[sOff + 16], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 5] = clip(luma[sOff + 17], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 6] = clip(luma[sOff + 18], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 7] = clip(luma[sOff + 19], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                sOff += srcIncLuma[line];
                _off += stride;
            }
            mb++;
            off2 += 8;
        }
    }

    private void putChroma(int[] y, int off, int stride, int mbX, int mbY, int[] chroma, int mbPerSlice, int dist, int shift) {
        int off2 = off + (mbX << 2) + ((mbY << 3) * stride);
        int k = 0;
        int sOff = 0;
        while (k < mbPerSlice) {
            int _off = off2;
            for (int line = 0; line < 8; line++) {
                y[_off] = clip(chroma[sOff], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 1] = clip(chroma[sOff + 1], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 2] = clip(chroma[sOff + 2], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                y[_off + 3] = clip(chroma[sOff + 3], 4, PointerIconCompat.TYPE_ZOOM_OUT);
                sOff += 4;
                _off += stride;
            }
            k++;
            off2 += 4;
        }
    }
}
