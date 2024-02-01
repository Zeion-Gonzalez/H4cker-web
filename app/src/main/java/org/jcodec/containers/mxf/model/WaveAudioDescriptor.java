package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class WaveAudioDescriptor extends GenericSoundEssenceDescriptor {
    private int avgBps;
    private short blockAlign;
    private C0893UL channelAssignment;
    private int peakChannels;
    private int peakEnvelopeBlockSize;
    private ByteBuffer peakEnvelopeData;
    private int peakEnvelopeFormat;
    private ByteBuffer peakEnvelopeTimestamp;
    private int peakEnvelopeVersion;
    private int peakFrames;
    private ByteBuffer peakOfPeaksPosition;
    private int pointsPerPeakValue;
    private byte sequenceOffset;

    public WaveAudioDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.GenericSoundEssenceDescriptor, org.jcodec.containers.mxf.model.FileDescriptor, org.jcodec.containers.mxf.model.GenericDescriptor, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 15625:
                    this.avgBps = _bb.getInt();
                    break;
                case 15626:
                    this.blockAlign = _bb.getShort();
                    break;
                case 15627:
                    this.sequenceOffset = _bb.get();
                    break;
                case 15657:
                    this.peakEnvelopeVersion = _bb.getInt();
                    break;
                case 15658:
                    this.peakEnvelopeFormat = _bb.getInt();
                    break;
                case 15659:
                    this.pointsPerPeakValue = _bb.getInt();
                    break;
                case 15660:
                    this.peakEnvelopeBlockSize = _bb.getInt();
                    break;
                case 15661:
                    this.peakChannels = _bb.getInt();
                    break;
                case 15662:
                    this.peakFrames = _bb.getInt();
                    break;
                case 15663:
                    this.peakOfPeaksPosition = _bb;
                    break;
                case 15664:
                    this.peakEnvelopeTimestamp = _bb;
                    break;
                case 15665:
                    this.peakEnvelopeData = _bb;
                    break;
                case 15666:
                    this.channelAssignment = C0893UL.read(_bb);
                    break;
                default:
                    System.out.println(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public short getBlockAlign() {
        return this.blockAlign;
    }

    public byte getSequenceOffset() {
        return this.sequenceOffset;
    }

    public int getAvgBps() {
        return this.avgBps;
    }

    public C0893UL getChannelAssignment() {
        return this.channelAssignment;
    }

    public int getPeakEnvelopeVersion() {
        return this.peakEnvelopeVersion;
    }

    public int getPeakEnvelopeFormat() {
        return this.peakEnvelopeFormat;
    }

    public int getPointsPerPeakValue() {
        return this.pointsPerPeakValue;
    }

    public int getPeakEnvelopeBlockSize() {
        return this.peakEnvelopeBlockSize;
    }

    public int getPeakChannels() {
        return this.peakChannels;
    }

    public int getPeakFrames() {
        return this.peakFrames;
    }

    public ByteBuffer getPeakOfPeaksPosition() {
        return this.peakOfPeaksPosition;
    }

    public ByteBuffer getPeakEnvelopeTimestamp() {
        return this.peakEnvelopeTimestamp;
    }

    public ByteBuffer getPeakEnvelopeData() {
        return this.peakEnvelopeData;
    }
}
