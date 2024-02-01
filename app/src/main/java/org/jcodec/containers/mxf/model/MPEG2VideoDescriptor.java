package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class MPEG2VideoDescriptor extends CDCIEssenceDescriptor {
    private short bPictureCount;
    private int bitRate;
    private byte closedGOP;
    private byte codedContentType;
    private byte constantBFrames;
    private byte identicalGOP;
    private byte lowDelay;
    private short maxGOP;
    private byte profileAndLevel;
    private byte singleSequence;

    public MPEG2VideoDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.CDCIEssenceDescriptor, org.jcodec.containers.mxf.model.GenericPictureEssenceDescriptor, org.jcodec.containers.mxf.model.FileDescriptor, org.jcodec.containers.mxf.model.GenericDescriptor, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 32768:
                    this.singleSequence = _bb.get();
                    break;
                case 32769:
                    this.constantBFrames = _bb.get();
                    break;
                case 32770:
                    this.codedContentType = _bb.get();
                    break;
                case 32771:
                    this.lowDelay = _bb.get();
                    break;
                case 32772:
                    this.closedGOP = _bb.get();
                    break;
                case 32773:
                    this.identicalGOP = _bb.get();
                    break;
                case 32774:
                    this.maxGOP = _bb.getShort();
                    break;
                case 32775:
                    this.bPictureCount = (short) (_bb.get() & 255);
                    break;
                case 32776:
                    this.bitRate = _bb.getInt();
                    break;
                case 32777:
                    this.profileAndLevel = _bb.get();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x + (" + _bb.remaining() + ")", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public byte getSingleSequence() {
        return this.singleSequence;
    }

    public byte getConstantBFrames() {
        return this.constantBFrames;
    }

    public byte getCodedContentType() {
        return this.codedContentType;
    }

    public byte getLowDelay() {
        return this.lowDelay;
    }

    public byte getClosedGOP() {
        return this.closedGOP;
    }

    public byte getIdenticalGOP() {
        return this.identicalGOP;
    }

    public short getMaxGOP() {
        return this.maxGOP;
    }

    public short getbPictureCount() {
        return this.bPictureCount;
    }

    public int getBitRate() {
        return this.bitRate;
    }

    public byte getProfileAndLevel() {
        return this.profileAndLevel;
    }
}
