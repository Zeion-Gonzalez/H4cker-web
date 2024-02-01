package org.jcodec.containers.mps;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Iterator;
import org.jcodec.common.Assert;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mps.psi.PATSection;
import org.jcodec.containers.mps.psi.PMTSection;
import org.jcodec.containers.mps.psi.PSISection;
import org.jcodec.containers.mxf.model.BER;

/* loaded from: classes.dex */
public class MTSUtils {

    /* loaded from: classes.dex */
    public enum StreamType {
        RESERVED(0, false, false),
        VIDEO_MPEG1(1, true, false),
        VIDEO_MPEG2(2, true, false),
        AUDIO_MPEG1(3, false, true),
        AUDIO_MPEG2(4, false, true),
        PRIVATE_SECTION(5, false, false),
        PRIVATE_DATA(6, false, false),
        MHEG(7, false, false),
        DSM_CC(8, false, false),
        ATM_SYNC(9, false, false),
        DSM_CC_A(10, false, false),
        DSM_CC_B(11, false, false),
        DSM_CC_C(12, false, false),
        DSM_CC_D(13, false, false),
        MPEG_AUX(14, false, false),
        AUDIO_AAC_ADTS(15, false, true),
        VIDEO_MPEG4(16, true, false),
        AUDIO_AAC_LATM(17, false, true),
        FLEXMUX_PES(18, false, false),
        FLEXMUX_SEC(19, false, false),
        DSM_CC_SDP(20, false, false),
        META_PES(21, false, false),
        META_SEC(22, false, false),
        DSM_CC_DATA_CAROUSEL(23, false, false),
        DSM_CC_OBJ_CAROUSEL(24, false, false),
        DSM_CC_SDP1(25, false, false),
        IPMP(26, false, false),
        VIDEO_H264(27, true, false),
        AUDIO_AAC_RAW(28, false, true),
        SUBS(29, false, false),
        AUX_3D(30, false, false),
        VIDEO_AVC_SVC(31, true, false),
        VIDEO_AVC_MVC(32, true, false),
        VIDEO_J2K(33, true, false),
        VIDEO_MPEG2_3D(34, true, false),
        VIDEO_H264_3D(35, true, false),
        VIDEO_CAVS(66, false, true),
        IPMP_STREAM(127, false, false),
        AUDIO_AC3(129, false, true),
        AUDIO_DTS(138, false, true);

        private static EnumSet<StreamType> typeEnum = EnumSet.allOf(StreamType.class);
        private boolean audio;
        private int tag;
        private boolean video;

        StreamType(int tag, boolean video, boolean audio) {
            this.tag = tag;
            this.video = video;
            this.audio = audio;
        }

        public static StreamType fromTag(int streamTypeTag) {
            Iterator i$ = typeEnum.iterator();
            while (i$.hasNext()) {
                StreamType streamType = (StreamType) i$.next();
                if (streamType.tag == streamTypeTag) {
                    return streamType;
                }
            }
            return null;
        }

        public int getTag() {
            return this.tag;
        }

        public boolean isVideo() {
            return this.video;
        }

        public boolean isAudio() {
            return this.audio;
        }
    }

    @Deprecated
    public static int parsePAT(ByteBuffer data) {
        PATSection pat = PATSection.parse(data);
        if (pat.getPrograms().size() > 0) {
            return pat.getPrograms().values()[0];
        }
        return -1;
    }

    @Deprecated
    public static PMTSection parsePMT(ByteBuffer data) {
        return PMTSection.parse(data);
    }

    @Deprecated
    public static PSISection parseSection(ByteBuffer data) {
        return PSISection.parse(data);
    }

    private static void parseEsInfo(ByteBuffer read) {
    }

    public static PMTSection.PMTStream[] getProgramGuids(File src) throws IOException {
        SeekableByteChannel ch = null;
        try {
            ch = NIOUtils.readableFileChannel(src);
            return getProgramGuids(ch);
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    public static PMTSection.PMTStream[] getProgramGuids(SeekableByteChannel in) throws IOException {
        PMTExtractor ex = new PMTExtractor();
        ex.readTsFile(in);
        PMTSection pmt = ex.getPmt();
        return pmt.getStreams();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PMTExtractor extends TSReader {
        private PMTSection pmt;
        private int pmtGuid;

        private PMTExtractor() {
            this.pmtGuid = -1;
        }

        @Override // org.jcodec.containers.mps.MTSUtils.TSReader
        protected boolean onPkt(int guid, boolean payloadStart, ByteBuffer tsBuf, long filePos) {
            if (guid == 0) {
                this.pmtGuid = MTSUtils.parsePAT(tsBuf);
            } else if (this.pmtGuid != -1 && guid == this.pmtGuid) {
                this.pmt = MTSUtils.parsePMT(tsBuf);
                return false;
            }
            return true;
        }

        public PMTSection getPmt() {
            return this.pmt;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class TSReader {
        public static final int BUFFER_SIZE = 96256;

        protected abstract boolean onPkt(int i, boolean z, ByteBuffer byteBuffer, long j);

        public void readTsFile(SeekableByteChannel ch) throws IOException {
            ch.position(0L);
            ByteBuffer buf = ByteBuffer.allocate(96256);
            long pos = ch.position();
            while (ch.read(buf) != -1) {
                buf.flip();
                while (buf.hasRemaining()) {
                    ByteBuffer tsBuf = NIOUtils.read(buf, 188);
                    pos += 188;
                    Assert.assertEquals(71, tsBuf.get() & 255);
                    int guidFlags = ((tsBuf.get() & 255) << 8) | (tsBuf.get() & 255);
                    int guid = guidFlags & 8191;
                    int payloadStart = (guidFlags >> 14) & 1;
                    int b0 = tsBuf.get() & 255;
                    int i = b0 & 15;
                    if ((b0 & 32) != 0) {
                        NIOUtils.skip(tsBuf, tsBuf.get() & 255);
                    }
                    boolean sectionSyntax = payloadStart == 1 && (NIOUtils.getRel(tsBuf, NIOUtils.getRel(tsBuf, 0) + 2) & BER.ASN_LONG_LEN) == 128;
                    if (sectionSyntax) {
                        NIOUtils.skip(tsBuf, tsBuf.get() & 255);
                    }
                    if (!onPkt(guid, payloadStart == 1, tsBuf, pos - tsBuf.remaining())) {
                        return;
                    }
                }
                buf.flip();
                pos = ch.position();
            }
        }
    }

    public static int getVideoPid(File src) throws IOException {
        PMTSection.PMTStream[] arr$ = getProgramGuids(src);
        for (PMTSection.PMTStream stream : arr$) {
            if (stream.getStreamType().isVideo()) {
                return stream.getPid();
            }
        }
        throw new RuntimeException("No video stream");
    }

    public static int getAudioPid(File src) throws IOException {
        PMTSection.PMTStream[] arr$ = getProgramGuids(src);
        for (PMTSection.PMTStream stream : arr$) {
            if (stream.getStreamType().isVideo()) {
                return stream.getPid();
            }
        }
        throw new RuntimeException("No video stream");
    }

    public static int[] getMediaPids(SeekableByteChannel src) throws IOException {
        return filterMediaPids(getProgramGuids(src));
    }

    public static int[] getMediaPids(File src) throws IOException {
        return filterMediaPids(getProgramGuids(src));
    }

    private static int[] filterMediaPids(PMTSection.PMTStream[] programs) {
        IntArrayList result = new IntArrayList();
        for (PMTSection.PMTStream stream : programs) {
            if (stream.getStreamType().isVideo() || stream.getStreamType().isAudio()) {
                result.add(stream.getPid());
            }
        }
        return result.toArray();
    }
}
