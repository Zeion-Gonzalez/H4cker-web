package org.jcodec.containers.mps;

import android.support.v4.view.PointerIconCompat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.Rational;
import org.jcodec.containers.mps.MPSDemuxer;

/* loaded from: classes.dex */
public class MPSUtils {
    public static final int AUDIO_MAX = 479;
    public static final int AUDIO_MIN = 448;
    public static final int PACK = 442;
    public static final int PRIVATE_1 = 445;
    public static final int PRIVATE_2 = 447;
    public static final int PSM = 444;
    public static final int SYSTEM = 443;
    public static final int VIDEO_MAX = 495;
    public static final int VIDEO_MIN = 480;
    public static Class<? extends MPEGMediaDescriptor>[] dMapping = new Class[256];

    /* loaded from: classes.dex */
    public static class MP4TextDescriptor extends MPEGMediaDescriptor {
    }

    public static final boolean mediaStream(int streamId) {
        return (streamId >= m2136$(AUDIO_MIN) && streamId <= m2136$(VIDEO_MAX)) || streamId == m2136$(PRIVATE_1) || streamId == m2136$(PRIVATE_2);
    }

    public static final boolean mediaMarker(int marker) {
        return (marker >= 448 && marker <= 495) || marker == 445 || marker == 447;
    }

    public static final boolean psMarker(int marker) {
        return marker >= 445 && marker <= 495;
    }

    public static boolean videoMarker(int marker) {
        return marker >= 480 && marker <= 495;
    }

    public static final boolean videoStream(int streamId) {
        return streamId >= m2136$(VIDEO_MIN) && streamId <= m2136$(VIDEO_MAX);
    }

    public static boolean audioStream(int streamId) {
        return (streamId >= m2136$(AUDIO_MIN) && streamId <= m2136$(AUDIO_MAX)) || streamId == m2136$(PRIVATE_1) || streamId == m2136$(PRIVATE_2);
    }

    /* renamed from: $ */
    static int m2136$(int marker) {
        return marker & 255;
    }

    /* loaded from: classes.dex */
    public static abstract class PESReader {
        private int lenFieldLeft;
        private boolean pes;
        private int pesLeft;
        private int pesLen;
        private int stream;
        private int marker = -1;
        private long pesFileStart = -1;
        private ByteBuffer pesBuffer = ByteBuffer.allocate(2097152);

        protected abstract void pes(ByteBuffer byteBuffer, long j, int i, int i2);

        public void analyseBuffer(ByteBuffer buf, long pos) {
            int init = buf.position();
            while (buf.hasRemaining()) {
                if (this.pesLeft > 0) {
                    int toRead = Math.min(buf.remaining(), this.pesLeft);
                    this.pesBuffer.put(NIOUtils.read(buf, toRead));
                    this.pesLeft -= toRead;
                    if (this.pesLeft == 0) {
                        long filePos = (buf.position() + pos) - init;
                        pes1(this.pesBuffer, this.pesFileStart, (int) (filePos - this.pesFileStart), this.stream);
                        this.pesFileStart = -1L;
                        this.pes = false;
                        this.stream = -1;
                    }
                } else {
                    int bt = buf.get() & 255;
                    if (this.pes) {
                        this.pesBuffer.put((byte) (this.marker >>> 24));
                    }
                    this.marker = (this.marker << 8) | bt;
                    if (this.marker >= 443 && this.marker <= 495) {
                        long filePos2 = ((buf.position() + pos) - init) - 4;
                        if (this.pes) {
                            pes1(this.pesBuffer, this.pesFileStart, (int) (filePos2 - this.pesFileStart), this.stream);
                        }
                        this.pesFileStart = filePos2;
                        this.pes = true;
                        this.stream = this.marker & 255;
                        this.lenFieldLeft = 2;
                        this.pesLen = 0;
                    } else if (this.marker >= 441 && this.marker <= 511) {
                        if (this.pes) {
                            long filePos3 = ((buf.position() + pos) - init) - 4;
                            pes1(this.pesBuffer, this.pesFileStart, (int) (filePos3 - this.pesFileStart), this.stream);
                        }
                        this.pesFileStart = -1L;
                        this.pes = false;
                        this.stream = -1;
                    } else if (this.lenFieldLeft > 0) {
                        this.pesLen = (this.pesLen << 8) | bt;
                        this.lenFieldLeft--;
                        if (this.lenFieldLeft == 0) {
                            this.pesLeft = this.pesLen;
                            if (this.pesLen != 0) {
                                flushMarker();
                                this.marker = -1;
                            }
                        }
                    }
                }
            }
        }

        private void flushMarker() {
            this.pesBuffer.put((byte) (this.marker >>> 24));
            this.pesBuffer.put((byte) ((this.marker >>> 16) & 255));
            this.pesBuffer.put((byte) ((this.marker >>> 8) & 255));
            this.pesBuffer.put((byte) (this.marker & 255));
        }

        private void pes1(ByteBuffer pesBuffer, long start, int pesLen, int stream) {
            pesBuffer.flip();
            pes(pesBuffer, start, pesLen, stream);
            pesBuffer.clear();
        }

        public void finishRead() {
            if (this.pesLeft <= 4) {
                flushMarker();
                pes1(this.pesBuffer, this.pesFileStart, this.pesBuffer.position(), this.stream);
            }
        }
    }

    public static MPSDemuxer.PESPacket readPESHeader(ByteBuffer iss, long pos) {
        int streamId = iss.getInt() & 255;
        int len = iss.getShort();
        if (streamId != 191) {
            int b0 = iss.get() & 255;
            if ((b0 & JpegConst.SOF0) == 128) {
                return mpeg2Pes(b0, len, streamId, iss, pos);
            }
            return mpeg1Pes(b0, len, streamId, iss, pos);
        }
        return new MPSDemuxer.PESPacket(null, -1L, streamId, len, pos, -1L);
    }

    public static MPSDemuxer.PESPacket mpeg1Pes(int b0, int len, int streamId, ByteBuffer is, long pos) {
        int c = b0;
        while (c == 255) {
            c = is.get() & 255;
        }
        if ((c & JpegConst.SOF0) == 64) {
            is.get();
            c = is.get() & 255;
        }
        long pts = -1;
        long dts = -1;
        if ((c & 240) == 32) {
            pts = readTs(is, c);
        } else if ((c & 240) == 48) {
            pts = readTs(is, c);
            dts = readTs(is);
        } else if (c != 15) {
            throw new RuntimeException("Invalid data");
        }
        return new MPSDemuxer.PESPacket(null, pts, streamId, len, pos, dts);
    }

    public static long readTs(ByteBuffer is, int c) {
        return ((c & 14) << 29) | ((is.get() & 255) << 22) | (((is.get() & 255) >> 1) << 15) | ((is.get() & 255) << 7) | ((is.get() & 255) >> 1);
    }

    public static MPSDemuxer.PESPacket mpeg2Pes(int b0, int len, int streamId, ByteBuffer is, long pos) {
        int flags2 = is.get() & 255;
        int header_len = is.get() & 255;
        long pts = -1;
        long dts = -1;
        if ((flags2 & JpegConst.SOF0) == 128) {
            pts = readTs(is);
            NIOUtils.skip(is, header_len - 5);
        } else if ((flags2 & JpegConst.SOF0) == 192) {
            pts = readTs(is);
            dts = readTs(is);
            NIOUtils.skip(is, header_len - 10);
        } else {
            NIOUtils.skip(is, header_len);
        }
        return new MPSDemuxer.PESPacket(null, pts, streamId, len, pos, dts);
    }

    public static long readTs(ByteBuffer is) {
        return ((is.get() & 14) << 29) | ((is.get() & 255) << 22) | (((is.get() & 255) >> 1) << 15) | ((is.get() & 255) << 7) | ((is.get() & 255) >> 1);
    }

    public static void writeTs(ByteBuffer is, long ts) {
        is.put((byte) ((ts >> 29) << 1));
        is.put((byte) (ts >> 22));
        is.put((byte) ((ts >> 15) << 1));
        is.put((byte) (ts >> 7));
        is.put((byte) (ts >> 1));
    }

    /* loaded from: classes.dex */
    public static class MPEGMediaDescriptor {
        private int len;
        private int tag;

        public void parse(ByteBuffer buf) {
            this.tag = buf.get() & 255;
            this.len = buf.get() & 255;
        }
    }

    /* loaded from: classes.dex */
    public static class VideoStreamDescriptor extends MPEGMediaDescriptor {
        private int chromaFormat;
        private int constrainedParameter;
        private int frameRateCode;
        private int frameRateExtension;
        Rational[] frameRates = {null, new Rational(24000, PointerIconCompat.TYPE_CONTEXT_MENU), new Rational(24, 1), new Rational(25, 1), new Rational(30000, PointerIconCompat.TYPE_CONTEXT_MENU), new Rational(30, 1), new Rational(50, 1), new Rational(60000, PointerIconCompat.TYPE_CONTEXT_MENU), new Rational(60, 1), null, null, null, null, null, null, null};
        private boolean mpeg1Only;
        private int multipleFrameRate;
        private int profileAndLevel;
        private int stillPicture;

        @Override // org.jcodec.containers.mps.MPSUtils.MPEGMediaDescriptor
        public void parse(ByteBuffer buf) {
            super.parse(buf);
            int b0 = buf.get() & 255;
            this.multipleFrameRate = (b0 >> 7) & 1;
            this.frameRateCode = (b0 >> 3) & 15;
            this.mpeg1Only = ((b0 >> 2) & 1) == 0;
            this.constrainedParameter = (b0 >> 1) & 1;
            this.stillPicture = b0 & 1;
            if (!this.mpeg1Only) {
                this.profileAndLevel = buf.get() & 255;
                int b1 = buf.get() & 255;
                this.chromaFormat = b1 >> 6;
                this.frameRateExtension = (b1 >> 5) & 1;
            }
        }

        public Rational getFrameRate() {
            return this.frameRates[this.frameRateCode];
        }
    }

    /* loaded from: classes.dex */
    public static class AudioStreamDescriptor extends MPEGMediaDescriptor {
        @Override // org.jcodec.containers.mps.MPSUtils.MPEGMediaDescriptor
        public void parse(ByteBuffer buf) {
            super.parse(buf);
            int b0 = buf.get() & 255;
            int i = (b0 >> 7) & 1;
            int i2 = (b0 >> 6) & 1;
            int i3 = (b0 >> 5) & 3;
            int i4 = (b0 >> 3) & 1;
        }
    }

    /* loaded from: classes.dex */
    public static class ISO639LanguageDescriptor extends MPEGMediaDescriptor {
        @Override // org.jcodec.containers.mps.MPSUtils.MPEGMediaDescriptor
        public void parse(ByteBuffer buf) {
            super.parse(buf);
            while (buf.remaining() >= 4) {
                buf.getInt();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Mpeg4VideoDescriptor extends MPEGMediaDescriptor {
        private int profileLevel;

        @Override // org.jcodec.containers.mps.MPSUtils.MPEGMediaDescriptor
        public void parse(ByteBuffer buf) {
            super.parse(buf);
            this.profileLevel = buf.get() & 255;
        }
    }

    /* loaded from: classes.dex */
    public static class Mpeg4AudioDescriptor extends MPEGMediaDescriptor {
        private int profileLevel;

        @Override // org.jcodec.containers.mps.MPSUtils.MPEGMediaDescriptor
        public void parse(ByteBuffer buf) {
            super.parse(buf);
            this.profileLevel = buf.get() & 255;
        }
    }

    /* loaded from: classes.dex */
    public static class AVCVideoDescriptor extends MPEGMediaDescriptor {
        private int flags;
        private int level;
        private int profileIdc;

        @Override // org.jcodec.containers.mps.MPSUtils.MPEGMediaDescriptor
        public void parse(ByteBuffer buf) {
            super.parse(buf);
            this.profileIdc = buf.get() & 255;
            this.flags = buf.get() & 255;
            this.level = buf.get() & 255;
        }
    }

    /* loaded from: classes.dex */
    public static class AACAudioDescriptor extends MPEGMediaDescriptor {
        private int channel;
        private int flags;
        private int profile;

        @Override // org.jcodec.containers.mps.MPSUtils.MPEGMediaDescriptor
        public void parse(ByteBuffer buf) {
            super.parse(buf);
            this.profile = buf.get() & 255;
            this.channel = buf.get() & 255;
            this.flags = buf.get() & 255;
        }
    }

    static {
        dMapping[2] = VideoStreamDescriptor.class;
        dMapping[3] = AudioStreamDescriptor.class;
        dMapping[10] = ISO639LanguageDescriptor.class;
        dMapping[27] = Mpeg4VideoDescriptor.class;
        dMapping[28] = Mpeg4AudioDescriptor.class;
        dMapping[40] = AVCVideoDescriptor.class;
        dMapping[43] = AACAudioDescriptor.class;
    }

    public static List<MPEGMediaDescriptor> parseDescriptors(ByteBuffer bb) {
        List<MPEGMediaDescriptor> result = new ArrayList<>();
        while (bb.remaining() >= 2) {
            int tag = bb.get() & 255;
            ByteBuffer buf = NIOUtils.read(bb, bb.get() & 255);
            if (dMapping[tag] != null) {
                try {
                    dMapping[tag].newInstance().parse(buf);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }
}
