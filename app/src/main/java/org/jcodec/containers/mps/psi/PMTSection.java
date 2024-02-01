package org.jcodec.containers.mps.psi;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.logging.Logger;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.containers.mps.MTSUtils;

/* loaded from: classes.dex */
public class PMTSection extends PSISection {
    private int pcrPid;
    private PMTStream[] streams;
    private Tag[] tags;

    public PMTSection(PSISection psi, int pcrPid, Tag[] tags, PMTStream[] streams) {
        super(psi);
        this.pcrPid = pcrPid;
        this.tags = tags;
        this.streams = streams;
    }

    public int getPcrPid() {
        return this.pcrPid;
    }

    public Tag[] getTags() {
        return this.tags;
    }

    public PMTStream[] getStreams() {
        return this.streams;
    }

    public static PMTSection parse(ByteBuffer data) {
        PSISection psi = PSISection.parse(data);
        int w1 = data.getShort() & 65535;
        int pcrPid = w1 & 8191;
        int w2 = data.getShort() & 65535;
        int programInfoLength = w2 & 4095;
        List<Tag> tags = parseTags(NIOUtils.read(data, programInfoLength));
        List<PMTStream> streams = new ArrayList<>();
        while (data.remaining() > 4) {
            int streamType = data.get() & 255;
            int wn = data.getShort() & 65535;
            int elementaryPid = wn & 8191;
            Logger.info(String.format("Elementary stream: [%d,%d]", Integer.valueOf(streamType), Integer.valueOf(elementaryPid)));
            int wn1 = data.getShort() & 65535;
            int esInfoLength = wn1 & 4095;
            ByteBuffer read = NIOUtils.read(data, esInfoLength);
            streams.add(new PMTStream(streamType, elementaryPid, MPSUtils.parseDescriptors(read)));
        }
        return new PMTSection(psi, pcrPid, (Tag[]) tags.toArray(new Tag[0]), (PMTStream[]) streams.toArray(new PMTStream[0]));
    }

    static List<Tag> parseTags(ByteBuffer bb) {
        List<Tag> tags = new ArrayList<>();
        while (bb.hasRemaining()) {
            int tag = bb.get();
            int tagLen = bb.get();
            Logger.info(String.format("TAG: [0x%x, 0x%x]", Integer.valueOf(tag), Integer.valueOf(tagLen)));
            tags.add(new Tag(tag, NIOUtils.read(bb, tagLen)));
        }
        return tags;
    }

    /* loaded from: classes.dex */
    public static class Tag {
        private ByteBuffer content;
        private int tag;

        public Tag(int tag, ByteBuffer content) {
            this.tag = tag;
            this.content = content;
        }

        public int getTag() {
            return this.tag;
        }

        public ByteBuffer getContent() {
            return this.content;
        }
    }

    /* loaded from: classes.dex */
    public static class PMTStream {
        private List<MPSUtils.MPEGMediaDescriptor> descriptors;
        private int pid;
        private MTSUtils.StreamType streamType;
        private int streamTypeTag;

        public PMTStream(int streamTypeTag, int pid, List<MPSUtils.MPEGMediaDescriptor> descriptors) {
            this.streamTypeTag = streamTypeTag;
            this.pid = pid;
            this.descriptors = descriptors;
            this.streamType = MTSUtils.StreamType.fromTag(streamTypeTag);
        }

        public int getStreamTypeTag() {
            return this.streamTypeTag;
        }

        public MTSUtils.StreamType getStreamType() {
            return this.streamType;
        }

        public int getPid() {
            return this.pid;
        }

        public List<MPSUtils.MPEGMediaDescriptor> getDesctiptors() {
            return this.descriptors;
        }
    }
}
