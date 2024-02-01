package org.jcodec.codecs.h264;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jcodec.codecs.h264.decode.SliceHeaderReader;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.io.write.SliceHeaderWriter;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.LeafBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

/* loaded from: classes.dex */
public class H264Utils {
    private static SliceHeaderReader shr = new SliceHeaderReader();
    private static SliceHeaderWriter shw = new SliceHeaderWriter();

    public static ByteBuffer nextNALUnit(ByteBuffer buf) {
        skipToNALUnit(buf);
        return gotoNALUnit(buf);
    }

    public static final void skipToNALUnit(ByteBuffer buf) {
        if (buf.hasRemaining()) {
            int val = -1;
            while (buf.hasRemaining()) {
                val = (val << 8) | (buf.get() & 255);
                if ((16777215 & val) == 1) {
                    buf.position(buf.position());
                    return;
                }
            }
        }
    }

    public static final ByteBuffer gotoNALUnit(ByteBuffer buf) {
        if (!buf.hasRemaining()) {
            return null;
        }
        int from = buf.position();
        ByteBuffer result = buf.slice();
        result.order(ByteOrder.BIG_ENDIAN);
        int val = -1;
        while (buf.hasRemaining()) {
            val = (val << 8) | (buf.get() & 255);
            if ((16777215 & val) == 1) {
                buf.position(buf.position() - (val == 1 ? 4 : 3));
                result.limit(buf.position() - from);
                return result;
            }
        }
        return result;
    }

    public static final void unescapeNAL(ByteBuffer _buf) {
        if (_buf.remaining() >= 2) {
            ByteBuffer in = _buf.duplicate();
            ByteBuffer out = _buf.duplicate();
            byte p1 = in.get();
            out.put(p1);
            byte p2 = in.get();
            out.put(p2);
            while (in.hasRemaining()) {
                byte b = in.get();
                if (p1 != 0 || p2 != 0 || b != 3) {
                    out.put(b);
                }
                p1 = p2;
                p2 = b;
            }
            _buf.limit(out.position());
        }
    }

    public static final void escapeNAL(ByteBuffer src) {
        int[] loc = searchEscapeLocations(src);
        int old = src.limit();
        src.limit(src.limit() + loc.length);
        int newPos = src.limit() - 1;
        int oldPos = old - 1;
        int locIdx = loc.length - 1;
        while (newPos >= src.position()) {
            src.put(newPos, src.get(oldPos));
            if (locIdx >= 0 && loc[locIdx] == oldPos) {
                newPos--;
                src.put(newPos, (byte) 3);
                locIdx--;
            }
            newPos--;
            oldPos--;
        }
    }

    private static int[] searchEscapeLocations(ByteBuffer src) {
        IntArrayList points = new IntArrayList();
        ByteBuffer search = src.duplicate();
        short p = search.getShort();
        while (search.hasRemaining()) {
            byte b = search.get();
            if (p == 0 && (b & (-4)) == 0) {
                points.add(search.position() - 1);
                p = 3;
            }
            p = (short) ((b & 255) | ((short) (p << 8)));
        }
        int[] array = points.toArray();
        return array;
    }

    public static final void escapeNAL(ByteBuffer src, ByteBuffer dst) {
        byte p1 = src.get();
        byte p2 = src.get();
        dst.put(p1);
        dst.put(p2);
        while (src.hasRemaining()) {
            byte b = src.get();
            if (p1 == 0 && p2 == 0 && (b & 255) <= 3) {
                dst.put((byte) 3);
                p2 = 3;
            }
            dst.put(b);
            p1 = p2;
            p2 = b;
        }
    }

    public static int golomb2Signed(int val) {
        int sign = ((val & 1) << 1) - 1;
        return ((val >> 1) + (val & 1)) * sign;
    }

    public static List<ByteBuffer> splitMOVPacket(ByteBuffer buf, AvcCBox avcC) {
        int len;
        List<ByteBuffer> result = new ArrayList<>();
        int nls = avcC.getNalLengthSize();
        ByteBuffer dup = buf.duplicate();
        while (dup.remaining() >= nls && (len = readLen(dup, nls)) != 0) {
            result.add(NIOUtils.read(dup, len));
        }
        return result;
    }

    private static int readLen(ByteBuffer dup, int nls) {
        switch (nls) {
            case 1:
                return dup.get() & 255;
            case 2:
                return dup.getShort() & 65535;
            case 3:
                return ((dup.getShort() & 65535) << 8) | (dup.get() & 255);
            case 4:
                return dup.getInt();
            default:
                throw new IllegalArgumentException("NAL Unit length size can not be " + nls);
        }
    }

    public static void encodeMOVPacket(ByteBuffer avcFrame) {
        ByteBuffer dup = avcFrame.duplicate();
        ByteBuffer d1 = avcFrame.duplicate();
        int tot = d1.position();
        while (true) {
            ByteBuffer buf = nextNALUnit(dup);
            if (buf != null) {
                d1.position(tot);
                d1.putInt(buf.remaining());
                tot += buf.remaining() + 4;
            } else {
                return;
            }
        }
    }

    public static void wipePS(ByteBuffer in, ByteBuffer out, List<ByteBuffer> spsList, List<ByteBuffer> ppsList) {
        ByteBuffer buf;
        ByteBuffer dup = in.duplicate();
        while (dup.hasRemaining() && (buf = nextNALUnit(dup)) != null) {
            NALUnit nu = NALUnit.read(buf.duplicate());
            if (nu.type == NALUnitType.PPS) {
                if (ppsList != null) {
                    ppsList.add(buf);
                }
            } else if (nu.type == NALUnitType.SPS) {
                if (spsList != null) {
                    spsList.add(buf);
                }
            } else {
                out.putInt(1);
                out.put(buf);
            }
        }
        out.flip();
    }

    public static void wipePS(ByteBuffer in, List<ByteBuffer> spsList, List<ByteBuffer> ppsList) {
        ByteBuffer buf;
        ByteBuffer dup = in.duplicate();
        while (dup.hasRemaining() && (buf = nextNALUnit(dup)) != null) {
            NALUnit nu = NALUnit.read(buf);
            if (nu.type == NALUnitType.PPS) {
                if (ppsList != null) {
                    ppsList.add(buf);
                }
                in.position(dup.position());
            } else if (nu.type == NALUnitType.SPS) {
                if (spsList != null) {
                    spsList.add(buf);
                }
                in.position(dup.position());
            } else if (nu.type == NALUnitType.IDR_SLICE || nu.type == NALUnitType.NON_IDR_SLICE) {
                return;
            }
        }
    }

    public static AvcCBox createAvcC(SeqParameterSet sps, PictureParameterSet pps, int nalLengthSize) {
        ByteBuffer serialSps = ByteBuffer.allocate(512);
        sps.write(serialSps);
        serialSps.flip();
        escapeNAL(serialSps);
        ByteBuffer serialPps = ByteBuffer.allocate(512);
        pps.write(serialPps);
        serialPps.flip();
        escapeNAL(serialPps);
        AvcCBox avcC = new AvcCBox(sps.profile_idc, 0, sps.level_idc, nalLengthSize, Arrays.asList(serialSps), Arrays.asList(serialPps));
        return avcC;
    }

    public static AvcCBox createAvcC(List<SeqParameterSet> initSPS, List<PictureParameterSet> initPPS, int nalLengthSize) {
        List<ByteBuffer> serialSps = new ArrayList<>();
        List<ByteBuffer> serialPps = new ArrayList<>();
        for (SeqParameterSet sps : initSPS) {
            ByteBuffer bb1 = ByteBuffer.allocate(512);
            sps.write(bb1);
            bb1.flip();
            escapeNAL(bb1);
            serialSps.add(bb1);
        }
        for (PictureParameterSet pps : initPPS) {
            ByteBuffer bb12 = ByteBuffer.allocate(512);
            pps.write(bb12);
            bb12.flip();
            escapeNAL(bb12);
            serialPps.add(bb12);
        }
        SeqParameterSet sps2 = initSPS.get(0);
        return new AvcCBox(sps2.profile_idc, 0, sps2.level_idc, nalLengthSize, serialSps, serialPps);
    }

    public static SampleEntry createMOVSampleEntry(List<ByteBuffer> spsList, List<ByteBuffer> ppsList, int nalLengthSize) {
        SeqParameterSet sps = readSPS(NIOUtils.duplicate(spsList.get(0)));
        AvcCBox avcC = new AvcCBox(sps.profile_idc, 0, sps.level_idc, nalLengthSize, spsList, ppsList);
        return createMOVSampleEntry(avcC);
    }

    public static SampleEntry createMOVSampleEntry(AvcCBox avcC) {
        SeqParameterSet sps = SeqParameterSet.read(avcC.getSpsList().get(0).duplicate());
        int codedWidth = (sps.pic_width_in_mbs_minus1 + 1) << 4;
        int codedHeight = getPicHeightInMbs(sps) << 4;
        int width = sps.frame_cropping_flag ? codedWidth - ((sps.frame_crop_right_offset + sps.frame_crop_left_offset) << sps.chroma_format_idc.compWidth[1]) : codedWidth;
        int height = sps.frame_cropping_flag ? codedHeight - ((sps.frame_crop_bottom_offset + sps.frame_crop_top_offset) << sps.chroma_format_idc.compHeight[1]) : codedHeight;
        Size size = new Size(width, height);
        SampleEntry se = MP4Muxer.videoSampleEntry("avc1", size, "JCodec");
        se.add(avcC);
        return se;
    }

    public static SampleEntry createMOVSampleEntry(SeqParameterSet initSPS, PictureParameterSet initPPS, int nalLengthSize) {
        ByteBuffer bb1 = ByteBuffer.allocate(512);
        ByteBuffer bb2 = ByteBuffer.allocate(512);
        initSPS.write(bb1);
        initPPS.write(bb2);
        bb1.flip();
        bb2.flip();
        return createMOVSampleEntry(Arrays.asList(bb1), Arrays.asList(bb2), nalLengthSize);
    }

    public static boolean idrSlice(ByteBuffer _data) {
        ByteBuffer segment;
        ByteBuffer data = _data.duplicate();
        do {
            segment = nextNALUnit(data);
            if (segment == null) {
                return false;
            }
        } while (NALUnit.read(segment).type != NALUnitType.IDR_SLICE);
        return true;
    }

    public static boolean idrSlice(List<ByteBuffer> _data) {
        for (ByteBuffer segment : _data) {
            if (NALUnit.read(segment.duplicate()).type == NALUnitType.IDR_SLICE) {
                return true;
            }
        }
        return false;
    }

    public static void saveRawFrame(ByteBuffer data, AvcCBox avcC, File f) throws IOException {
        SeekableByteChannel raw = NIOUtils.writableFileChannel(f);
        saveStreamParams(avcC, raw);
        raw.write(data.duplicate());
        raw.close();
    }

    public static void saveStreamParams(AvcCBox avcC, SeekableByteChannel raw) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1024);
        for (ByteBuffer byteBuffer : avcC.getSpsList()) {
            raw.write(ByteBuffer.wrap(new byte[]{0, 0, 0, 1, 103}));
            escapeNAL(byteBuffer.duplicate(), bb);
            bb.flip();
            raw.write(bb);
            bb.clear();
        }
        for (ByteBuffer byteBuffer2 : avcC.getPpsList()) {
            raw.write(ByteBuffer.wrap(new byte[]{0, 0, 0, 1, 104}));
            escapeNAL(byteBuffer2.duplicate(), bb);
            bb.flip();
            raw.write(bb);
            bb.clear();
        }
    }

    public static int getPicHeightInMbs(SeqParameterSet sps) {
        int picHeightInMbs = (sps.pic_height_in_map_units_minus1 + 1) << (sps.frame_mbs_only_flag ? 0 : 1);
        return picHeightInMbs;
    }

    public static List<ByteBuffer> splitFrame(ByteBuffer frame) {
        ArrayList<ByteBuffer> result = new ArrayList<>();
        while (true) {
            ByteBuffer segment = nextNALUnit(frame);
            if (segment != null) {
                result.add(segment);
            } else {
                return result;
            }
        }
    }

    public static void joinNALUnits(List<ByteBuffer> nalUnits, ByteBuffer out) {
        for (ByteBuffer nal : nalUnits) {
            out.putInt(1);
            out.put(nal.duplicate());
        }
    }

    public static ByteBuffer getAvcCData(AvcCBox avcC) {
        ByteBuffer bb = ByteBuffer.allocate(2048);
        avcC.doWrite(bb);
        bb.flip();
        return bb;
    }

    public static AvcCBox parseAVCC(VideoSampleEntry vse) {
        Box lb = (Box) Box.findFirst(vse, Box.class, "avcC");
        return lb instanceof AvcCBox ? (AvcCBox) lb : parseAVCC(((LeafBox) lb).getData().duplicate());
    }

    public static AvcCBox parseAVCC(ByteBuffer bb) {
        AvcCBox avcC = new AvcCBox();
        avcC.parse(bb);
        return avcC;
    }

    public static ByteBuffer writeSPS(SeqParameterSet sps, int approxSize) {
        ByteBuffer output = ByteBuffer.allocate(approxSize + 8);
        sps.write(output);
        output.flip();
        escapeNAL(output);
        return output;
    }

    public static SeqParameterSet readSPS(ByteBuffer data) {
        ByteBuffer input = NIOUtils.duplicate(data);
        unescapeNAL(input);
        SeqParameterSet sps = SeqParameterSet.read(input);
        return sps;
    }

    public static ByteBuffer writePPS(PictureParameterSet pps, int approxSize) {
        ByteBuffer output = ByteBuffer.allocate(approxSize + 8);
        pps.write(output);
        output.flip();
        escapeNAL(output);
        return output;
    }

    public static PictureParameterSet readPPS(ByteBuffer data) {
        ByteBuffer input = NIOUtils.duplicate(data);
        unescapeNAL(input);
        PictureParameterSet pps = PictureParameterSet.read(input);
        return pps;
    }

    public static PictureParameterSet findPPS(List<PictureParameterSet> ppss, int id) {
        for (PictureParameterSet pps : ppss) {
            if (pps.pic_parameter_set_id == id) {
                return pps;
            }
        }
        return null;
    }

    public static SeqParameterSet findSPS(List<SeqParameterSet> spss, int id) {
        for (SeqParameterSet sps : spss) {
            if (sps.seq_parameter_set_id == id) {
                return sps;
            }
        }
        return null;
    }

    /* loaded from: classes.dex */
    public static abstract class SliceHeaderTweaker {
        private List<PictureParameterSet> pps;
        private List<SeqParameterSet> sps;

        protected abstract void tweak(SliceHeader sliceHeader);

        public SliceHeaderTweaker() {
        }

        public SliceHeaderTweaker(List<ByteBuffer> spsList, List<ByteBuffer> ppsList) {
            this.sps = H264Utils.readSPS(spsList);
            this.pps = H264Utils.readPPS(ppsList);
        }

        public SliceHeader run(ByteBuffer is, ByteBuffer os, NALUnit nu) {
            ByteBuffer nal = os.duplicate();
            H264Utils.unescapeNAL(is);
            BitReader reader = new BitReader(is);
            SliceHeader sh = H264Utils.shr.readPart1(reader);
            PictureParameterSet pp = H264Utils.findPPS(this.pps, sh.pic_parameter_set_id);
            return part2(is, os, nu, H264Utils.findSPS(this.sps, pp.pic_parameter_set_id), pp, nal, reader, sh);
        }

        public SliceHeader run(ByteBuffer is, ByteBuffer os, NALUnit nu, SeqParameterSet sps, PictureParameterSet pps) {
            ByteBuffer nal = os.duplicate();
            H264Utils.unescapeNAL(is);
            BitReader reader = new BitReader(is);
            SliceHeader sh = H264Utils.shr.readPart1(reader);
            return part2(is, os, nu, sps, pps, nal, reader, sh);
        }

        private SliceHeader part2(ByteBuffer is, ByteBuffer os, NALUnit nu, SeqParameterSet sps, PictureParameterSet pps, ByteBuffer nal, BitReader reader, SliceHeader sh) {
            BitWriter writer = new BitWriter(os);
            H264Utils.shr.readPart2(sh, nu, sps, pps, reader);
            tweak(sh);
            H264Utils.shw.write(sh, nu.type == NALUnitType.IDR_SLICE, nu.nal_ref_idc, writer);
            if (pps.entropy_coding_mode_flag) {
                copyDataCABAC(is, os, reader, writer);
            } else {
                copyDataCAVLC(is, os, reader, writer);
            }
            nal.limit(os.position());
            H264Utils.escapeNAL(nal);
            os.position(nal.limit());
            return sh;
        }

        private void copyDataCAVLC(ByteBuffer is, ByteBuffer os, BitReader reader, BitWriter writer) {
            int wLeft = 8 - writer.curBit();
            if (wLeft != 0) {
                writer.writeNBit(reader.readNBit(wLeft), wLeft);
            }
            writer.flush();
            int shift = reader.curBit();
            if (shift != 0) {
                int mShift = 8 - shift;
                int inp = reader.readNBit(mShift);
                reader.stop();
                while (is.hasRemaining()) {
                    int out = inp << shift;
                    inp = is.get() & 255;
                    os.put((byte) (out | (inp >> mShift)));
                }
                os.put((byte) (inp << shift));
                return;
            }
            reader.stop();
            os.put(is);
        }

        private void copyDataCABAC(ByteBuffer is, ByteBuffer os, BitReader reader, BitWriter writer) {
            long bp = reader.curBit();
            if (bp != 0) {
                long rem = reader.readNBit(8 - ((int) bp));
                if (((long) ((1 << ((int) (8 - bp))) - 1)) != rem) {
                    throw new RuntimeException("Invalid CABAC padding");
                }
            }
            if (writer.curBit() != 0) {
                writer.writeNBit(255, 8 - writer.curBit());
            }
            writer.flush();
            reader.stop();
            os.put(is);
        }
    }

    public static Size getPicSize(SeqParameterSet sps) {
        int w = (sps.pic_width_in_mbs_minus1 + 1) << 4;
        int h = getPicHeightInMbs(sps) << 4;
        if (sps.frame_cropping_flag) {
            w -= (sps.frame_crop_left_offset + sps.frame_crop_right_offset) << sps.chroma_format_idc.compWidth[1];
            h -= (sps.frame_crop_top_offset + sps.frame_crop_bottom_offset) << sps.chroma_format_idc.compHeight[1];
        }
        return new Size(w, h);
    }

    public static List<SeqParameterSet> readSPS(List<ByteBuffer> spsList) {
        List<SeqParameterSet> result = new ArrayList<>();
        for (ByteBuffer byteBuffer : spsList) {
            result.add(readSPS(NIOUtils.duplicate(byteBuffer)));
        }
        return result;
    }

    public static List<PictureParameterSet> readPPS(List<ByteBuffer> ppsList) {
        List<PictureParameterSet> result = new ArrayList<>();
        for (ByteBuffer byteBuffer : ppsList) {
            result.add(readPPS(NIOUtils.duplicate(byteBuffer)));
        }
        return result;
    }

    public static List<ByteBuffer> writePPS(List<PictureParameterSet> allPps) {
        List<ByteBuffer> result = new ArrayList<>();
        for (PictureParameterSet pps : allPps) {
            result.add(writePPS(pps, 64));
        }
        return result;
    }

    public static List<ByteBuffer> writeSPS(List<SeqParameterSet> allSps) {
        List<ByteBuffer> result = new ArrayList<>();
        for (SeqParameterSet sps : allSps) {
            result.add(writeSPS(sps, 256));
        }
        return result;
    }

    public static void dumpFrame(FileChannelWrapper ch, SeqParameterSet[] values, PictureParameterSet[] values2, List<ByteBuffer> nalUnits) throws IOException {
        for (SeqParameterSet sps : values) {
            NIOUtils.writeInt(ch, 1);
            NIOUtils.writeByte(ch, (byte) 103);
            ch.write(writeSPS(sps, 128));
        }
        for (PictureParameterSet pps : values2) {
            NIOUtils.writeInt(ch, 1);
            NIOUtils.writeByte(ch, (byte) 104);
            ch.write(writePPS(pps, 256));
        }
        for (ByteBuffer byteBuffer : nalUnits) {
            NIOUtils.writeInt(ch, 1);
            ch.write(byteBuffer.duplicate());
        }
    }

    public static void toNAL(ByteBuffer codecPrivate, SeqParameterSet sps, PictureParameterSet pps) {
        ByteBuffer bb1 = ByteBuffer.allocate(512);
        ByteBuffer bb2 = ByteBuffer.allocate(512);
        sps.write(bb1);
        pps.write(bb2);
        bb1.flip();
        bb2.flip();
        putNAL(codecPrivate, bb1, 103);
        putNAL(codecPrivate, bb2, 104);
    }

    public static void toNAL(ByteBuffer codecPrivate, List<ByteBuffer> spsList2, List<ByteBuffer> ppsList2) {
        for (ByteBuffer byteBuffer : spsList2) {
            putNAL(codecPrivate, byteBuffer, 103);
        }
        for (ByteBuffer byteBuffer2 : ppsList2) {
            putNAL(codecPrivate, byteBuffer2, 104);
        }
    }

    private static void putNAL(ByteBuffer codecPrivate, ByteBuffer byteBuffer, int nalType) {
        ByteBuffer dst = ByteBuffer.allocate(byteBuffer.remaining() * 2);
        escapeNAL(byteBuffer, dst);
        dst.flip();
        codecPrivate.putInt(1);
        codecPrivate.put((byte) nalType);
        codecPrivate.put(dst);
    }
}
