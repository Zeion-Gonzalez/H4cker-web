package org.jcodec.codecs.mpeg12;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.Assert;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public abstract class FixTimestamp {
    protected abstract long doWithTimestamp(int i, long j, boolean z);

    public void fix(File file) throws IOException {
        RandomAccessFile ra;
        RandomAccessFile ra2 = null;
        try {
            ra = new RandomAccessFile(file, "rw");
        } catch (Throwable th) {
            th = th;
        }
        try {
            byte[] tsPkt = new byte[188];
            while (ra.read(tsPkt) == 188) {
                Assert.assertEquals(71, tsPkt[0] & 255);
                int guidFlags = ((tsPkt[1] & 255) << 8) | (tsPkt[2] & 255);
                int guid = guidFlags & 8191;
                int payloadStart = (guidFlags >> 14) & 1;
                if (payloadStart != 0 && guid != 0) {
                    ByteBuffer bb = ByteBuffer.wrap(tsPkt, 4, MPEGConst.GROUP_START_CODE);
                    if ((tsPkt[3] & 32) != 0) {
                        NIOUtils.skip(bb, bb.get() & 255);
                    }
                    if (bb.remaining() >= 10) {
                        int streamId = bb.getInt();
                        if ((streamId >> 8) == 1) {
                            while (bb.hasRemaining() && (streamId < 447 || streamId >= 495)) {
                                streamId = (streamId << 8) | (bb.get() & 255);
                            }
                            if (streamId >= 447 && streamId < 495) {
                                bb.getShort();
                                int b0 = bb.get() & 255;
                                bb.position(bb.position() - 1);
                                if ((b0 & JpegConst.SOF0) == 128) {
                                    fixMpeg2(streamId & 255, bb);
                                } else {
                                    fixMpeg1(streamId & 255, bb);
                                }
                                ra.seek(ra.getFilePointer() - 188);
                                ra.write(tsPkt);
                            }
                        }
                    }
                }
            }
            if (ra != null) {
                ra.close();
            }
        } catch (Throwable th2) {
            th = th2;
            ra2 = ra;
            if (ra2 != null) {
                ra2.close();
            }
            throw th;
        }
    }

    public void fixMpeg1(int streamId, ByteBuffer is) {
        int c;
        int i = is.getInt();
        while (true) {
            c = i & 255;
            if (c != 255) {
                break;
            } else {
                i = is.get();
            }
        }
        if ((c & JpegConst.SOF0) == 64) {
            is.get();
            c = is.get() & 255;
        }
        if ((c & 240) == 32) {
            is.position(is.position() - 1);
            fixTs(streamId, is, true);
        } else if ((c & 240) == 48) {
            is.position(is.position() - 1);
            fixTs(streamId, is, true);
            fixTs(streamId, is, false);
        } else if (c != 15) {
            throw new RuntimeException("Invalid data");
        }
    }

    public long fixTs(int streamId, ByteBuffer is, boolean isPts) {
        byte b0 = is.get();
        byte b1 = is.get();
        byte b2 = is.get();
        byte b3 = is.get();
        byte b4 = is.get();
        long pts = doWithTimestamp(streamId, ((b0 & 14) << 29) | ((b1 & 255) << 22) | (((b2 & 255) >> 1) << 15) | ((b3 & 255) << 7) | ((b4 & 255) >> 1), isPts);
        is.position(is.position() - 5);
        is.put((byte) ((b0 & 240) | (pts >>> 29) | 1));
        is.put((byte) (pts >>> 22));
        is.put((byte) ((pts >>> 14) | 1));
        is.put((byte) (pts >>> 7));
        is.put((byte) ((pts << 1) | 1));
        return pts;
    }

    public void fixMpeg2(int streamId, ByteBuffer is) {
        int i = is.get() & 255;
        int flags2 = is.get() & 255;
        int i2 = is.get() & 255;
        if ((flags2 & JpegConst.SOF0) == 128) {
            fixTs(streamId, is, true);
        } else if ((flags2 & JpegConst.SOF0) == 192) {
            fixTs(streamId, is, true);
            fixTs(streamId, is, false);
        }
    }

    public boolean isVideo(int streamId) {
        return streamId >= 224 && streamId <= 239;
    }

    public boolean isAudio(int streamId) {
        return streamId >= 191 && streamId <= 223;
    }
}
