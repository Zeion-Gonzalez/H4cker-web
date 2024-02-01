package org.jcodec.codecs.mpeg12;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jcodec.codecs.mpeg12.MPSMediaInfo;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mps.MTSUtils;
import org.jcodec.containers.mps.psi.PMTSection;

/* loaded from: classes.dex */
public class MTSMediaInfo {
    public List<MPSMediaInfo.MPEGTrackMetadata> getMediaInfo(File f) throws IOException {
        FileChannelWrapper ch = null;
        final List<PMTSection> pmtSections = new ArrayList<>();
        final Map<Integer, MPSMediaInfo> pids = new HashMap<>();
        final List<MPSMediaInfo.MPEGTrackMetadata> result = new ArrayList<>();
        try {
            ch = NIOUtils.readableFileChannel(f);
            new MTSUtils.TSReader() { // from class: org.jcodec.codecs.mpeg12.MTSMediaInfo.1
                private ByteBuffer pmtBuffer;
                private boolean pmtDone;
                private int pmtPid = -1;

                @Override // org.jcodec.containers.mps.MTSUtils.TSReader
                protected boolean onPkt(int guid, boolean payloadStart, ByteBuffer tsBuf, long filePos) {
                    if (guid == 0) {
                        this.pmtPid = MTSUtils.parsePAT(tsBuf);
                    } else if (guid == this.pmtPid && !this.pmtDone) {
                        if (this.pmtBuffer == null) {
                            this.pmtBuffer = ByteBuffer.allocate(((tsBuf.duplicate().getInt() >> 8) & 1023) + 3);
                        } else if (this.pmtBuffer.hasRemaining()) {
                            NIOUtils.write(this.pmtBuffer, tsBuf, Math.min(this.pmtBuffer.remaining(), tsBuf.remaining()));
                        }
                        if (!this.pmtBuffer.hasRemaining()) {
                            this.pmtBuffer.flip();
                            PMTSection pmt = MTSUtils.parsePMT(this.pmtBuffer);
                            pmtSections.add(pmt);
                            PMTSection.PMTStream[] arr$ = pmt.getStreams();
                            for (PMTSection.PMTStream stream : arr$) {
                                if (!pids.containsKey(Integer.valueOf(stream.getPid()))) {
                                    pids.put(Integer.valueOf(stream.getPid()), new MPSMediaInfo());
                                }
                            }
                            this.pmtDone = pmt.getSectionNumber() == pmt.getLastSectionNumber();
                            this.pmtBuffer = null;
                        }
                    } else if (pids.containsKey(Integer.valueOf(guid))) {
                        try {
                            ((MPSMediaInfo) pids.get(Integer.valueOf(guid))).analyseBuffer(tsBuf, filePos);
                        } catch (MPSMediaInfo.MediaInfoDone e) {
                            result.addAll(((MPSMediaInfo) pids.get(Integer.valueOf(guid))).getInfos());
                            pids.remove(Integer.valueOf(guid));
                            if (pids.size() == 0) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }.readTsFile(ch);
            return result;
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    public static void main(String[] args) throws IOException {
        List<MPSMediaInfo.MPEGTrackMetadata> info = new MTSMediaInfo().getMediaInfo(new File(args[0]));
        for (MPSMediaInfo.MPEGTrackMetadata stream : info) {
            System.out.println(stream.codec);
        }
    }

    public static MTSMediaInfo extract(SeekableByteChannel input) {
        return null;
    }

    public MPSMediaInfo.MPEGTrackMetadata getVideoTrack() {
        return null;
    }

    public List<MPSMediaInfo.MPEGTrackMetadata> getAudioTracks() {
        return null;
    }
}
