package org.jcodec.containers.mps;

import com.instabug.chat.model.Attachment;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import org.jcodec.codecs.mpeg12.MPEGUtil;
import org.jcodec.codecs.mpeg12.bitstream.CopyrightExtension;
import org.jcodec.codecs.mpeg12.bitstream.GOPHeader;
import org.jcodec.codecs.mpeg12.bitstream.PictureCodingExtension;
import org.jcodec.codecs.mpeg12.bitstream.PictureDisplayExtension;
import org.jcodec.codecs.mpeg12.bitstream.PictureHeader;
import org.jcodec.codecs.mpeg12.bitstream.PictureSpatialScalableExtension;
import org.jcodec.codecs.mpeg12.bitstream.PictureTemporalScalableExtension;
import org.jcodec.codecs.mpeg12.bitstream.QuantMatrixExtension;
import org.jcodec.codecs.mpeg12.bitstream.SequenceDisplayExtension;
import org.jcodec.codecs.mpeg12.bitstream.SequenceExtension;
import org.jcodec.codecs.mpeg12.bitstream.SequenceHeader;
import org.jcodec.codecs.mpeg12.bitstream.SequenceScalableExtension;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.tools.MainUtils;
import org.jcodec.containers.mps.MPSDemuxer;

/* loaded from: classes.dex */
public class MPSDump {
    private static final String DUMP_FROM = "dump-from";
    private static final String STOP_AT = "stop-at";

    /* renamed from: ch */
    protected ReadableByteChannel f1547ch;

    public MPSDump(ReadableByteChannel ch) {
        this.f1547ch = ch;
    }

    public static void main(String[] args) throws IOException {
        FileChannelWrapper ch = null;
        try {
            MainUtils.Cmd cmd = MainUtils.parseArguments(args);
            if (cmd.args.length < 1) {
                MainUtils.printHelp(new HashMap<String, String>() { // from class: org.jcodec.containers.mps.MPSDump.1
                    {
                        put(MPSDump.STOP_AT, "Stop reading at timestamp");
                        put(MPSDump.DUMP_FROM, "Start dumping from timestamp");
                    }
                }, "file name");
                return;
            }
            ch = NIOUtils.readableFileChannel(new File(cmd.args[0]));
            Long dumpAfterPts = cmd.getLongFlag(DUMP_FROM);
            Long stopPts = cmd.getLongFlag(STOP_AT);
            new MPSDump(ch).dump(dumpAfterPts, stopPts);
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    public void dump(Long dumpAfterPts, Long stopPts) throws IOException {
        MPEGVideoAnalyzer analyzer = null;
        ByteBuffer buffer = ByteBuffer.allocate(1048576);
        MPSDemuxer.PESPacket pkt = null;
        int hdrSize = 0;
        long position = 0;
        while (true) {
            long position2 = position - buffer.position();
            fillBuffer(buffer);
            buffer.flip();
            if (buffer.remaining() >= 4) {
                position = position2 + buffer.remaining();
                while (true) {
                    ByteBuffer payload = null;
                    if (pkt != null && pkt.length > 0) {
                        int pesLen = (pkt.length - hdrSize) + 6;
                        if (pesLen <= buffer.remaining()) {
                            payload = NIOUtils.read(buffer, pesLen);
                        }
                    } else {
                        payload = getPesPayload(buffer);
                    }
                    if (payload != null) {
                        if (pkt != null) {
                            logPes(pkt, hdrSize, payload);
                        }
                        if (analyzer != null && pkt != null && pkt.streamId >= 224 && pkt.streamId <= 239) {
                            analyzer.analyzeMpegVideoPacket(payload);
                        }
                        if (buffer.remaining() < 32) {
                            pkt = null;
                            break;
                        }
                        skipToNextPES(buffer);
                        if (buffer.remaining() < 32) {
                            pkt = null;
                            break;
                        }
                        int hdrSize2 = buffer.position();
                        pkt = MPSUtils.readPESHeader(buffer, position - buffer.remaining());
                        hdrSize = buffer.position() - hdrSize2;
                        if (dumpAfterPts != null && pkt.pts >= dumpAfterPts.longValue()) {
                            analyzer = new MPEGVideoAnalyzer();
                        }
                        if (stopPts != null && pkt.pts >= stopPts.longValue()) {
                            return;
                        }
                    }
                }
                buffer = transferRemainder(buffer);
            } else {
                return;
            }
        }
    }

    protected int fillBuffer(ByteBuffer buffer) throws IOException {
        return this.f1547ch.read(buffer);
    }

    protected void logPes(MPSDemuxer.PESPacket pkt, int hdrSize, ByteBuffer payload) {
        System.out.println(pkt.streamId + "(" + (pkt.streamId >= 224 ? Attachment.TYPE_VIDEO : Attachment.TYPE_AUDIO) + ") [" + pkt.pos + ", " + (payload.remaining() + hdrSize) + "], pts: " + pkt.pts + ", dts: " + pkt.dts);
    }

    private ByteBuffer transferRemainder(ByteBuffer buffer) {
        ByteBuffer dup = buffer.duplicate();
        dup.clear();
        while (buffer.hasRemaining()) {
            dup.put(buffer.get());
        }
        return dup;
    }

    private static void skipToNextPES(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            int marker = buffer.duplicate().getInt();
            if (marker < 445 || marker > 511 || marker == 446) {
                buffer.getInt();
                MPEGUtil.gotoNextMarker(buffer);
            } else {
                return;
            }
        }
    }

    private static ByteBuffer getPesPayload(ByteBuffer buffer) {
        ByteBuffer copy = buffer.duplicate();
        ByteBuffer result = buffer.duplicate();
        while (copy.hasRemaining()) {
            int marker = copy.duplicate().getInt();
            if (marker >= 441) {
                result.limit(copy.position());
                buffer.position(copy.position());
                return result;
            }
            copy.getInt();
            MPEGUtil.gotoNextMarker(copy);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MPEGVideoAnalyzer {
        private int bselBufInd;
        private int bselOffset;
        private ByteBuffer bselPayload;
        private int bselStartCode;
        private int curBufInd;
        private int nextStartCode;
        private PictureHeader picHeader;
        private PictureCodingExtension pictureCodingExtension;
        private int prevBufSize;
        private SequenceExtension sequenceExtension;
        private SequenceHeader sequenceHeader;

        private MPEGVideoAnalyzer() {
            this.nextStartCode = -1;
            this.bselPayload = ByteBuffer.allocate(1048576);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void analyzeMpegVideoPacket(ByteBuffer buffer) {
            int pos = buffer.position();
            int bufSize = buffer.remaining();
            while (buffer.hasRemaining()) {
                this.bselPayload.put((byte) (this.nextStartCode >> 24));
                this.nextStartCode = (this.nextStartCode << 8) | (buffer.get() & 255);
                if (this.nextStartCode >= 256 && this.nextStartCode <= 440) {
                    this.bselPayload.flip();
                    this.bselPayload.getInt();
                    if (this.bselStartCode != 0) {
                        if (this.bselBufInd != this.curBufInd) {
                            this.bselOffset -= this.prevBufSize;
                        }
                        dumpBSEl(this.bselStartCode, this.bselOffset, this.bselPayload);
                    }
                    this.bselPayload.clear();
                    this.bselStartCode = this.nextStartCode;
                    this.bselOffset = (buffer.position() - 4) - pos;
                    this.bselBufInd = this.curBufInd;
                }
            }
            this.curBufInd++;
            this.prevBufSize = bufSize;
        }

        private void dumpBSEl(int mark, int offset, ByteBuffer b) {
            System.out.print(String.format("marker: 0x%02x [@%d] ( ", Integer.valueOf(mark), Integer.valueOf(offset)));
            if (mark == 256) {
                dumpPictureHeader(b);
            } else if (mark <= 431) {
                System.out.print(MainUtils.color(String.format("slice @0x%02x", Integer.valueOf(mark - 257)), MainUtils.ANSIColor.BLACK, true));
            } else if (mark == 435) {
                dumpSequenceHeader(b);
            } else if (mark == 437) {
                dumpExtension(b);
            } else if (mark == 440) {
                dumpGroupHeader(b);
            } else {
                System.out.print("--");
            }
            System.out.println(" )");
        }

        private void dumpExtension(ByteBuffer b) {
            BitReader in = new BitReader(b);
            int extType = in.readNBit(4);
            if (this.picHeader == null) {
                if (this.sequenceHeader != null) {
                    switch (extType) {
                        case 1:
                            this.sequenceExtension = SequenceExtension.read(in);
                            dumpSequenceExtension(this.sequenceExtension);
                            return;
                        case 2:
                            dumpSequenceDisplayExtension(SequenceDisplayExtension.read(in));
                            return;
                        case 3:
                        case 4:
                        default:
                            System.out.print(MainUtils.color("extension " + extType, MainUtils.ANSIColor.GREEN, true));
                            return;
                        case 5:
                            dumpSequenceScalableExtension(SequenceScalableExtension.read(in));
                            return;
                    }
                }
                System.out.print(MainUtils.color("dangling extension " + extType, MainUtils.ANSIColor.GREEN, true));
                return;
            }
            switch (extType) {
                case 3:
                    dumpQuantMatrixExtension(QuantMatrixExtension.read(in));
                    return;
                case 4:
                    dumpCopyrightExtension(CopyrightExtension.read(in));
                    return;
                case 5:
                case 6:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                default:
                    System.out.print(MainUtils.color("extension " + extType, MainUtils.ANSIColor.GREEN, true));
                    return;
                case 7:
                    if (this.sequenceHeader != null && this.pictureCodingExtension != null) {
                        dumpPictureDisplayExtension(PictureDisplayExtension.read(in, this.sequenceExtension, this.pictureCodingExtension));
                        return;
                    }
                    return;
                case 8:
                    this.pictureCodingExtension = PictureCodingExtension.read(in);
                    dumpPictureCodingExtension(this.pictureCodingExtension);
                    return;
                case 9:
                    dumpPictureSpatialScalableExtension(PictureSpatialScalableExtension.read(in));
                    return;
                case 16:
                    dumpPictureTemporalScalableExtension(PictureTemporalScalableExtension.read(in));
                    return;
            }
        }

        private void dumpSequenceDisplayExtension(SequenceDisplayExtension read) {
            System.out.print(MainUtils.color("sequence display extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpSequenceScalableExtension(SequenceScalableExtension read) {
            System.out.print(MainUtils.color("sequence scalable extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpSequenceExtension(SequenceExtension read) {
            System.out.print(MainUtils.color("sequence extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpPictureTemporalScalableExtension(PictureTemporalScalableExtension read) {
            System.out.print(MainUtils.color("picture temporal scalable extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpPictureSpatialScalableExtension(PictureSpatialScalableExtension read) {
            System.out.print(MainUtils.color("picture spatial scalable extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpPictureCodingExtension(PictureCodingExtension read) {
            System.out.print(MainUtils.color("picture coding extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpPictureDisplayExtension(PictureDisplayExtension read) {
            System.out.print(MainUtils.color("picture display extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpCopyrightExtension(CopyrightExtension read) {
            System.out.print(MainUtils.color("copyright extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private void dumpQuantMatrixExtension(QuantMatrixExtension read) {
            System.out.print(MainUtils.color("quant matrix extension " + dumpBin(read), MainUtils.ANSIColor.GREEN, true));
        }

        private String dumpBin(Object read) {
            StringBuilder bldr = new StringBuilder();
            bldr.append("<");
            Field[] fields = read.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                if (Modifier.isPublic(fields[i].getModifiers()) && !Modifier.isStatic(fields[i].getModifiers())) {
                    bldr.append(convertName(fields[i].getName()) + ": ");
                    if (fields[i].getType().isPrimitive()) {
                        try {
                            bldr.append(fields[i].get(read));
                        } catch (IllegalAccessException e) {
                        } catch (IllegalArgumentException e2) {
                        }
                    } else {
                        try {
                            Object val = fields[i].get(read);
                            if (val != null) {
                                bldr.append(dumpBin(val));
                            } else {
                                bldr.append("N/A");
                            }
                        } catch (IllegalAccessException e3) {
                        } catch (IllegalArgumentException e4) {
                        }
                    }
                    if (i < fields.length - 1) {
                        bldr.append(",");
                    }
                }
            }
            bldr.append(">");
            return bldr.toString();
        }

        private String convertName(String name) {
            return name.replaceAll("([A-Z])", " $1").replaceFirst("^ ", "").toLowerCase();
        }

        private void dumpGroupHeader(ByteBuffer b) {
            GOPHeader gopHeader = GOPHeader.read(b);
            System.out.print(MainUtils.color("group header <closed:" + gopHeader.isClosedGop() + ",broken link:" + gopHeader.isBrokenLink() + (gopHeader.getTimeCode() != null ? ",timecode:" + gopHeader.getTimeCode().toString() : "") + ">", MainUtils.ANSIColor.MAGENTA, true));
        }

        private void dumpSequenceHeader(ByteBuffer b) {
            this.picHeader = null;
            this.pictureCodingExtension = null;
            this.sequenceExtension = null;
            this.sequenceHeader = SequenceHeader.read(b);
            System.out.print(MainUtils.color("sequence header", MainUtils.ANSIColor.BLUE, true));
        }

        private void dumpPictureHeader(ByteBuffer b) {
            String str;
            this.picHeader = PictureHeader.read(b);
            this.pictureCodingExtension = null;
            PrintStream printStream = System.out;
            StringBuilder append = new StringBuilder().append("picture header <type:");
            if (this.picHeader.picture_coding_type == 1) {
                str = "I";
            } else {
                str = this.picHeader.picture_coding_type == 2 ? "P" : "B";
            }
            printStream.print(MainUtils.color(append.append(str).append(", temp_ref:").append(this.picHeader.temporal_reference).append(">").toString(), MainUtils.ANSIColor.BROWN, true));
        }
    }
}
