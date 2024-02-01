package org.jcodec.containers.mkv.boxes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import org.jcodec.common.ByteArrayList;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mkv.MKVType;
import org.jcodec.containers.mkv.util.EbmlUtil;
import org.jcodec.containers.mxf.model.BER;

/* loaded from: classes.dex */
public class MkvBlock extends EbmlBin {
    private static final String EBML = "EBML";
    private static final String FIXED = "Fixed";
    private static final int MAX_BLOCK_HEADER_SIZE = 512;
    private static final String XIPH = "Xiph";
    public long absoluteTimecode;
    public boolean discardable;
    public int[] frameOffsets;
    public int[] frameSizes;
    public ByteBuffer[] frames;
    public int headerSize;
    public boolean keyFrame;
    public String lacing;
    public boolean lacingPresent;
    public int timecode;
    public long trackNumber;

    public static MkvBlock copy(MkvBlock old) {
        MkvBlock be = new MkvBlock(old.f1540id);
        be.trackNumber = old.trackNumber;
        be.timecode = old.timecode;
        be.absoluteTimecode = old.absoluteTimecode;
        be.keyFrame = old.keyFrame;
        be.headerSize = old.headerSize;
        be.lacing = old.lacing;
        be.discardable = old.discardable;
        be.lacingPresent = old.lacingPresent;
        be.frameOffsets = new int[old.frameOffsets.length];
        be.frameSizes = new int[old.frameSizes.length];
        be.dataOffset = old.dataOffset;
        be.offset = old.offset;
        be.type = old.type;
        System.arraycopy(old.frameOffsets, 0, be.frameOffsets, 0, be.frameOffsets.length);
        System.arraycopy(old.frameSizes, 0, be.frameSizes, 0, be.frameSizes.length);
        return be;
    }

    public static MkvBlock keyFrame(long trackNumber, int timecode, ByteBuffer frame) {
        MkvBlock be = new MkvBlock(MKVType.SimpleBlock.f1537id);
        be.frames = new ByteBuffer[]{frame};
        be.frameSizes = new int[]{frame.limit()};
        be.keyFrame = true;
        be.trackNumber = trackNumber;
        be.timecode = timecode;
        return be;
    }

    public MkvBlock(byte[] type) {
        super(type);
        if (!Arrays.equals(MKVType.SimpleBlock.f1537id, type) && !Arrays.equals(MKVType.Block.f1537id, type)) {
            throw new IllegalArgumentException("Block initiated with invalid id: " + EbmlUtil.toHexString(type));
        }
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBin
    public void read(SeekableByteChannel is) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(100);
        is.read(bb);
        bb.flip();
        read(bb);
        is.position(this.dataOffset + this.dataLen);
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBin
    public void read(ByteBuffer source) {
        ByteBuffer bb = source.slice();
        this.trackNumber = ebmlDecode(bb);
        int tcPart1 = bb.get() & 255;
        int tcPart2 = bb.get() & 255;
        this.timecode = (short) ((((short) tcPart1) << 8) | ((short) tcPart2));
        int flags = bb.get() & 255;
        this.keyFrame = (flags & 128) > 0;
        this.discardable = (flags & 1) > 0;
        int laceFlags = flags & 6;
        this.lacingPresent = laceFlags != 0;
        if (this.lacingPresent) {
            int lacesCount = bb.get() & 255;
            this.frameSizes = new int[lacesCount + 1];
            if (laceFlags == 2) {
                this.lacing = XIPH;
                this.headerSize = readXiphLaceSizes(bb, this.frameSizes, this.dataLen, bb.position());
            } else if (laceFlags == 6) {
                this.lacing = EBML;
                this.headerSize = readEBMLLaceSizes(bb, this.frameSizes, this.dataLen, bb.position());
            } else if (laceFlags == 4) {
                this.lacing = FIXED;
                this.headerSize = bb.position();
                int aLaceSize = (this.dataLen - this.headerSize) / (lacesCount + 1);
                Arrays.fill(this.frameSizes, aLaceSize);
            } else {
                throw new RuntimeException("Unsupported lacing type flag.");
            }
            turnSizesToFrameOffsets(this.frameSizes);
            return;
        }
        this.lacing = "";
        int frameOffset = bb.position();
        this.frameOffsets = new int[1];
        this.frameOffsets[0] = frameOffset;
        this.headerSize = bb.position();
        this.frameSizes = new int[1];
        this.frameSizes[0] = this.dataLen - this.headerSize;
    }

    private void turnSizesToFrameOffsets(int[] sizes) {
        this.frameOffsets = new int[sizes.length];
        this.frameOffsets[0] = this.headerSize;
        for (int i = 1; i < sizes.length; i++) {
            this.frameOffsets[i] = this.frameOffsets[i - 1] + sizes[i - 1];
        }
    }

    public static int readXiphLaceSizes(ByteBuffer bb, int[] sizes, int size, int preLacingHeaderSize) {
        int startPos = bb.position();
        int lastIndex = sizes.length - 1;
        sizes[lastIndex] = size;
        for (int l = 0; l < lastIndex; l++) {
            int laceSize = 255;
            while (laceSize == 255) {
                laceSize = bb.get() & 255;
                sizes[l] = sizes[l] + laceSize;
            }
            sizes[lastIndex] = sizes[lastIndex] - sizes[l];
        }
        int headerSize = (bb.position() - startPos) + preLacingHeaderSize;
        sizes[lastIndex] = sizes[lastIndex] - headerSize;
        return headerSize;
    }

    public static int readEBMLLaceSizes(ByteBuffer source, int[] sizes, int size, int preLacingHeaderSize) {
        int lastIndex = sizes.length - 1;
        sizes[lastIndex] = size;
        int startPos = source.position();
        sizes[0] = (int) ebmlDecode(source);
        sizes[lastIndex] = sizes[lastIndex] - sizes[0];
        int laceSize = sizes[0];
        for (int l = 1; l < lastIndex; l++) {
            long laceSizeDiff = ebmlDecodeSigned(source);
            laceSize = (int) (laceSize + laceSizeDiff);
            sizes[l] = laceSize;
            sizes[lastIndex] = sizes[lastIndex] - sizes[l];
        }
        int headerSize = (source.position() - startPos) + preLacingHeaderSize;
        sizes[lastIndex] = sizes[lastIndex] - headerSize;
        return headerSize;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{dataOffset: ").append(this.dataOffset);
        sb.append(", trackNumber: ").append(this.trackNumber);
        sb.append(", timecode: ").append(this.timecode);
        sb.append(", keyFrame: ").append(this.keyFrame);
        sb.append(", headerSize: ").append(this.headerSize);
        sb.append(", lacing: ").append(this.lacing);
        for (int i = 0; i < this.frameSizes.length; i++) {
            sb.append(", frame[").append(i).append("]  offset ").append(this.frameOffsets[i]).append(" size ").append(this.frameSizes[i]);
        }
        sb.append(" }");
        return sb.toString();
    }

    public ByteBuffer[] getFrames(ByteBuffer source) throws IOException {
        ByteBuffer[] frames = new ByteBuffer[this.frameSizes.length];
        for (int i = 0; i < this.frameSizes.length; i++) {
            if (this.frameOffsets[i] > source.limit()) {
                System.err.println("frame offset: " + this.frameOffsets[i] + " limit: " + source.limit());
            }
            source.position(this.frameOffsets[i]);
            ByteBuffer bb = source.slice();
            bb.limit(this.frameSizes[i]);
            frames[i] = bb;
        }
        return frames;
    }

    public void readFrames(ByteBuffer source) throws IOException {
        this.frames = getFrames(source);
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBin, org.jcodec.containers.mkv.boxes.EbmlBase
    public ByteBuffer getData() {
        int dataSize = getDataSize();
        ByteBuffer bb = ByteBuffer.allocate(EbmlUtil.ebmlLength(dataSize) + dataSize + this.f1540id.length);
        bb.put(this.f1540id);
        bb.put(EbmlUtil.ebmlEncode(dataSize));
        bb.put(EbmlUtil.ebmlEncode(this.trackNumber));
        bb.put((byte) ((this.timecode >>> 8) & 255));
        bb.put((byte) (this.timecode & 255));
        byte flags = 0;
        if (XIPH.equals(this.lacing)) {
            flags = 2;
        } else if (EBML.equals(this.lacing)) {
            flags = 6;
        } else if (FIXED.equals(this.lacing)) {
            flags = 4;
        }
        if (this.discardable) {
            flags = (byte) (flags | 1);
        }
        if (this.keyFrame) {
            flags = (byte) (flags | BER.ASN_LONG_LEN);
        }
        bb.put(flags);
        if ((flags & 6) != 0) {
            bb.put((byte) ((this.frames.length - 1) & 255));
            bb.put(muxLacingInfo());
        }
        ByteBuffer[] arr$ = this.frames;
        for (ByteBuffer frame : arr$) {
            bb.put(frame);
        }
        bb.flip();
        return bb;
    }

    public void seekAndReadContent(FileChannel source) throws IOException {
        this.data = ByteBuffer.allocate(this.dataLen);
        source.position(this.dataOffset);
        source.read(this.data);
        this.data.flip();
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBin, org.jcodec.containers.mkv.boxes.EbmlBase
    public long size() {
        long size = getDataSize();
        return size + EbmlUtil.ebmlLength(size) + this.f1540id.length;
    }

    public int getDataSize() {
        int size = 0;
        int[] arr$ = this.frameSizes;
        for (long fsize : arr$) {
            size = (int) (size + fsize);
        }
        if (this.lacingPresent) {
            size = size + muxLacingInfo().length + 1;
        }
        return size + 3 + EbmlUtil.ebmlLength(this.trackNumber);
    }

    private byte[] muxLacingInfo() {
        if (EBML.equals(this.lacing)) {
            return muxEbmlLacing(this.frameSizes);
        }
        if (XIPH.equals(this.lacing)) {
            return muxXiphLacing(this.frameSizes);
        }
        if (FIXED.equals(this.lacing)) {
            return new byte[0];
        }
        return null;
    }

    public static long ebmlDecode(ByteBuffer bb) {
        byte firstByte = bb.get();
        int length = EbmlUtil.computeLength(firstByte);
        if (length == 0) {
            throw new RuntimeException("Invalid ebml integer size.");
        }
        long value = (255 >>> length) & firstByte;
        for (int length2 = length - 1; length2 > 0; length2--) {
            value = (value << 8) | (bb.get() & 255);
        }
        return value;
    }

    public static long ebmlDecodeSigned(ByteBuffer source) {
        byte firstByte = source.get();
        int size = EbmlUtil.computeLength(firstByte);
        if (size == 0) {
            throw new RuntimeException("Invalid ebml integer size.");
        }
        long value = (255 >>> size) & firstByte;
        for (int remaining = size - 1; remaining > 0; remaining--) {
            value = (value << 8) | (source.get() & 255);
        }
        return value - EbmlSint.signedComplement[size];
    }

    public static long[] calcEbmlLacingDiffs(int[] laceSizes) {
        int lacesCount = laceSizes.length - 1;
        long[] out = new long[lacesCount];
        out[0] = laceSizes[0];
        for (int i = 1; i < lacesCount; i++) {
            out[i] = laceSizes[i] - laceSizes[i - 1];
        }
        return out;
    }

    public static byte[] muxEbmlLacing(int[] laceSizes) {
        ByteArrayList bytes = new ByteArrayList();
        long[] laceSizeDiffs = calcEbmlLacingDiffs(laceSizes);
        bytes.addAll(EbmlUtil.ebmlEncode(laceSizeDiffs[0]));
        for (int i = 1; i < laceSizeDiffs.length; i++) {
            bytes.addAll(EbmlSint.convertToBytes(laceSizeDiffs[i]));
        }
        return bytes.toArray();
    }

    public static byte[] muxXiphLacing(int[] laceSizes) {
        ByteArrayList bytes = new ByteArrayList();
        for (int i = 0; i < laceSizes.length - 1; i++) {
            long laceSize = laceSizes[i];
            while (laceSize >= 255) {
                bytes.add((byte) -1);
                laceSize -= 255;
            }
            bytes.add((byte) laceSize);
        }
        return bytes.toArray();
    }
}
