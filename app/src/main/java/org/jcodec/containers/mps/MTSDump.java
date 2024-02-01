package org.jcodec.containers.mps;

import com.instabug.chat.model.Attachment;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jcodec.common.Assert;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.IntIntMap;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.tools.MainUtils;
import org.jcodec.containers.mps.MPSDemuxer;
import org.jcodec.containers.mps.psi.PATSection;
import org.jcodec.containers.mps.psi.PMTSection;

/* loaded from: classes.dex */
public class MTSDump extends MPSDump {
    private static final String DUMP_FROM = "dump-from";
    private static final String STOP_AT = "stop-at";
    private ByteBuffer buf;
    private int globalPayload;
    private int guid;
    private int[] nums;
    private int[] payloads;
    private int[] prevNums;
    private int[] prevPayloads;
    private ByteBuffer tsBuf;
    private int tsNo;

    public MTSDump(ReadableByteChannel ch, int targetGuid) {
        super(ch);
        this.buf = ByteBuffer.allocate(192512);
        this.tsBuf = ByteBuffer.allocate(188);
        this.guid = targetGuid;
        this.buf.position(this.buf.limit());
        this.tsBuf.position(this.tsBuf.limit());
    }

    public static void main(String[] args) throws IOException {
        ReadableByteChannel ch = null;
        try {
            MainUtils.Cmd cmd = MainUtils.parseArguments(args);
            if (cmd.args.length < 1) {
                MainUtils.printHelp(new HashMap<String, String>() { // from class: org.jcodec.containers.mps.MTSDump.1
                    {
                        put(MTSDump.STOP_AT, "Stop reading at timestamp");
                        put(MTSDump.DUMP_FROM, "Start dumping from timestamp");
                    }
                }, "file name", "guid");
                return;
            }
            if (cmd.args.length == 1) {
                System.out.println("MTS programs:");
                dumpProgramPids(NIOUtils.readableFileChannel(new File(cmd.args[0])));
            } else {
                ch = NIOUtils.readableFileChannel(new File(cmd.args[0]));
                Long dumpAfterPts = cmd.getLongFlag(DUMP_FROM);
                Long stopPts = cmd.getLongFlag(STOP_AT);
                new MTSDump(ch, Integer.parseInt(cmd.args[1])).dump(dumpAfterPts, stopPts);
            }
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    private static void dumpProgramPids(ReadableByteChannel readableFileChannel) throws IOException {
        Set<Integer> pids = new HashSet<>();
        ByteBuffer buf = ByteBuffer.allocate(192512);
        readableFileChannel.read(buf);
        buf.flip();
        buf.limit(buf.limit() - (buf.limit() % 188));
        int pmtPid = -1;
        while (buf.hasRemaining()) {
            ByteBuffer tsBuf = NIOUtils.read(buf, 188);
            Assert.assertEquals(71, tsBuf.get() & 255);
            int guidFlags = ((tsBuf.get() & 255) << 8) | (tsBuf.get() & 255);
            int guid = guidFlags & 8191;
            if (guid != 0) {
                pids.add(Integer.valueOf(guid));
            }
            if (guid == 0 || guid == pmtPid) {
                int payloadStart = (guidFlags >> 14) & 1;
                int b0 = tsBuf.get() & 255;
                int i = b0 & 15;
                if ((b0 & 32) != 0) {
                    NIOUtils.skip(tsBuf, tsBuf.get() & 255);
                }
                if (payloadStart == 1) {
                    NIOUtils.skip(tsBuf, tsBuf.get() & 255);
                }
                if (guid == 0) {
                    PATSection pat = PATSection.parse(tsBuf);
                    IntIntMap programs = pat.getPrograms();
                    pmtPid = programs.values()[0];
                } else if (guid == pmtPid) {
                    PMTSection pmt = PMTSection.parse(tsBuf);
                    printPmt(pmt);
                    return;
                }
            }
        }
        for (Integer pid : pids) {
            System.out.println(pid);
        }
    }

    private static void printPmt(PMTSection pmt) {
        PMTSection.PMTStream[] arr$ = pmt.getStreams();
        for (PMTSection.PMTStream pmtStream : arr$) {
            System.out.println(pmtStream.getPid() + ": " + pmtStream.getStreamTypeTag());
        }
    }

    @Override // org.jcodec.containers.mps.MPSDump
    protected void logPes(MPSDemuxer.PESPacket pkt, int hdrSize, ByteBuffer payload) {
        System.out.println(pkt.streamId + "(" + (pkt.streamId >= 224 ? Attachment.TYPE_VIDEO : Attachment.TYPE_AUDIO) + ") [ts#" + mapPos(pkt.pos) + ", " + (payload.remaining() + hdrSize) + "b], pts: " + pkt.pts + ", dts: " + pkt.dts);
    }

    private int mapPos(long pos) {
        int left = this.globalPayload;
        for (int i = this.payloads.length - 1; i >= 0; i--) {
            left -= this.payloads[i];
            if (left <= pos) {
                return this.nums[i];
            }
        }
        if (this.prevPayloads != null) {
            for (int i2 = this.prevPayloads.length - 1; i2 >= 0; i2--) {
                left -= this.prevPayloads[i2];
                if (left <= pos) {
                    return this.prevNums[i2];
                }
            }
        }
        return -1;
    }

    @Override // org.jcodec.containers.mps.MPSDump
    public int fillBuffer(ByteBuffer dst) throws IOException {
        IntArrayList payloads = new IntArrayList();
        IntArrayList nums = new IntArrayList();
        int remaining = dst.remaining();
        try {
            dst.put(NIOUtils.read(this.tsBuf, Math.min(dst.remaining(), this.tsBuf.remaining())));
            while (dst.hasRemaining()) {
                if (!this.buf.hasRemaining()) {
                    ByteBuffer dub = this.buf.duplicate();
                    dub.clear();
                    int read = this.f1547ch.read(dub);
                    if (read == -1) {
                        return dst.remaining() != remaining ? remaining - dst.remaining() : -1;
                    }
                    dub.flip();
                    dub.limit(dub.limit() - (dub.limit() % 188));
                    this.buf = dub;
                }
                this.tsBuf = NIOUtils.read(this.buf, 188);
                Assert.assertEquals(71, this.tsBuf.get() & 255);
                this.tsNo++;
                int guidFlags = ((this.tsBuf.get() & 255) << 8) | (this.tsBuf.get() & 255);
                int guid = guidFlags & 8191;
                if (guid == this.guid) {
                    int i = (guidFlags >> 14) & 1;
                    int b0 = this.tsBuf.get() & 255;
                    int i2 = b0 & 15;
                    if ((b0 & 32) != 0) {
                        NIOUtils.skip(this.tsBuf, this.tsBuf.get() & 255);
                    }
                    this.globalPayload += this.tsBuf.remaining();
                    payloads.add(this.tsBuf.remaining());
                    nums.add(this.tsNo - 1);
                    dst.put(NIOUtils.read(this.tsBuf, Math.min(dst.remaining(), this.tsBuf.remaining())));
                }
            }
            this.prevPayloads = this.payloads;
            this.payloads = payloads.toArray();
            this.prevNums = this.nums;
            this.nums = nums.toArray();
            return remaining - dst.remaining();
        } finally {
            this.prevPayloads = this.payloads;
            this.payloads = payloads.toArray();
            this.prevNums = this.nums;
            this.nums = nums.toArray();
        }
    }
}
