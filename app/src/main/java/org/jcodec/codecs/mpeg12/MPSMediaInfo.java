package org.jcodec.codecs.mpeg12;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.Codec;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.ChannelLabel;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.containers.mxf.model.BER;

/* loaded from: classes.dex */
public class MPSMediaInfo extends MPSUtils.PESReader {
    private Map<Integer, MPEGTrackMetadata> infos = new HashMap();
    private int pesTried;
    private PSM psm;

    /* loaded from: classes.dex */
    public static class PSM {
    }

    /* loaded from: classes.dex */
    public class MPEGTimecodeMetadata {
        public MPEGTimecodeMetadata() {
        }

        public String getNumFrames() {
            return null;
        }

        public String isDropFrame() {
            return null;
        }

        public String getStartCounter() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public class MPEGTrackMetadata {
        Codec codec;
        ByteBuffer probeData;
        int streamId;

        public MPEGTrackMetadata(int streamId) {
            this.streamId = streamId;
        }

        public AudioFormat getAudioFormat() {
            return null;
        }

        public ChannelLabel[] getChannelLables() {
            return null;
        }

        public Size getDisplaySize() {
            return null;
        }

        public Size getCodedSize() {
            return null;
        }

        public float getFps() {
            return 0.0f;
        }

        public float getDuration() {
            return 0.0f;
        }

        public String getFourcc() {
            return null;
        }

        public Rational getFpsR() {
            return null;
        }

        public int getNumFrames() {
            return 0;
        }

        public MPEGTimecodeMetadata getTimecode() {
            return null;
        }
    }

    public List<MPEGTrackMetadata> getMediaInfo(File f) throws IOException {
        try {
            new NIOUtils.FileReader() { // from class: org.jcodec.codecs.mpeg12.MPSMediaInfo.1
                @Override // org.jcodec.common.NIOUtils.FileReader
                protected void data(ByteBuffer data, long filePos) {
                    MPSMediaInfo.this.analyseBuffer(data, filePos);
                }

                @Override // org.jcodec.common.NIOUtils.FileReader
                protected void done() {
                }
            }.readFile(f, 65536, (NIOUtils.FileReaderListener) null);
        } catch (MediaInfoDone e) {
            Logger.info("Media info done");
        }
        return getInfos();
    }

    /* loaded from: classes.dex */
    public class MediaInfoDone extends RuntimeException {
        public MediaInfoDone() {
        }
    }

    @Override // org.jcodec.containers.mps.MPSUtils.PESReader
    protected void pes(ByteBuffer pesBuffer, long start, int pesLen, int stream) {
        if (MPSUtils.mediaStream(stream)) {
            MPEGTrackMetadata info = this.infos.get(Integer.valueOf(stream));
            if (info == null) {
                info = new MPEGTrackMetadata(stream);
                this.infos.put(Integer.valueOf(stream), info);
            }
            if (info.probeData == null) {
                info.probeData = NIOUtils.cloneBuffer(pesBuffer);
            }
            int i = this.pesTried + 1;
            this.pesTried = i;
            if (i >= 100) {
                deriveMediaInfo();
                throw new MediaInfoDone();
            }
        }
    }

    private void deriveMediaInfo() {
        Collection<MPEGTrackMetadata> values = this.infos.values();
        for (MPEGTrackMetadata stream : values) {
            int streamId = stream.streamId | 256;
            if (streamId >= 448 && streamId <= 479) {
                stream.codec = Codec.MP2;
            } else if (streamId == 445) {
                ByteBuffer dup = stream.probeData.duplicate();
                MPSUtils.readPESHeader(dup, 0L);
                int type = dup.get() & 255;
                if (type >= 128 && type <= 135) {
                    stream.codec = Codec.AC3;
                } else if ((type >= 136 && type <= 143) || (type >= 152 && type <= 159)) {
                    stream.codec = Codec.DTS;
                } else if (type >= 160 && type <= 175) {
                    stream.codec = Codec.PCM_DVD;
                } else if (type >= 176 && type <= 191) {
                    stream.codec = Codec.TRUEHD;
                } else if (type >= 192 && type <= 207) {
                    stream.codec = Codec.AC3;
                }
            } else if (streamId >= 480 && streamId <= 495) {
                stream.codec = Codec.MPEG2;
            }
        }
    }

    private int[] parseSystem(ByteBuffer pesBuffer) {
        NIOUtils.skip(pesBuffer, 12);
        IntArrayList result = new IntArrayList();
        while (pesBuffer.remaining() >= 3 && (pesBuffer.get(pesBuffer.position()) & BER.ASN_LONG_LEN) == 128) {
            result.add(pesBuffer.get() & 255);
            pesBuffer.getShort();
        }
        return result.toArray();
    }

    private PSM parsePSM(ByteBuffer pesBuffer) {
        pesBuffer.getInt();
        short psmLen = pesBuffer.getShort();
        if (psmLen > 1018) {
            throw new RuntimeException("Invalid PSM");
        }
        pesBuffer.get();
        byte b1 = pesBuffer.get();
        if ((b1 & 1) != 1) {
            throw new RuntimeException("Invalid PSM");
        }
        short psiLen = pesBuffer.getShort();
        NIOUtils.read(pesBuffer, psiLen & 65535);
        short elStreamLen = pesBuffer.getShort();
        parseElStreams(NIOUtils.read(pesBuffer, elStreamLen & 65535));
        pesBuffer.getInt();
        return new PSM();
    }

    private void parseElStreams(ByteBuffer buf) {
        while (buf.hasRemaining()) {
            buf.get();
            buf.get();
            short strInfoLen = buf.getShort();
            NIOUtils.read(buf, 65535 & strInfoLen);
        }
    }

    public List<MPEGTrackMetadata> getInfos() {
        return new ArrayList(this.infos.values());
    }

    public static void main(String[] args) throws IOException {
        new MPSMediaInfo().getMediaInfo(new File(args[0]));
    }

    public static MPSMediaInfo extract(SeekableByteChannel input) {
        return null;
    }

    public List<MPEGTrackMetadata> getAudioTracks() {
        return null;
    }

    public MPEGTrackMetadata getVideoTrack() {
        return null;
    }
}
