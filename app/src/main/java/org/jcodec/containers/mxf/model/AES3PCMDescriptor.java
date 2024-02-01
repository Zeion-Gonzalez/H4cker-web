package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class AES3PCMDescriptor extends WaveAudioDescriptor {
    private byte auxBitsMode;
    private short blockStartOffset;
    private ByteBuffer channelStatusMode;
    private byte emphasis;
    private ByteBuffer fixedChannelStatusData;
    private ByteBuffer fixedUserData;
    private ByteBuffer userDataMode;

    public AES3PCMDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.WaveAudioDescriptor, org.jcodec.containers.mxf.model.GenericSoundEssenceDescriptor, org.jcodec.containers.mxf.model.FileDescriptor, org.jcodec.containers.mxf.model.GenericDescriptor, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 15624:
                    this.auxBitsMode = _bb.get();
                    break;
                case 15625:
                case 15626:
                case 15627:
                case 15628:
                case 15630:
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
                case 15629:
                    this.emphasis = _bb.get();
                    break;
                case 15631:
                    this.blockStartOffset = _bb.getShort();
                    break;
                case 15632:
                    this.channelStatusMode = _bb;
                    break;
                case 15633:
                    this.fixedChannelStatusData = _bb;
                    break;
                case 15634:
                    this.userDataMode = _bb;
                    break;
                case 15635:
                    this.fixedUserData = _bb;
                    break;
            }
            it.remove();
        }
    }

    public byte getEmphasis() {
        return this.emphasis;
    }

    public short getBlockStartOffset() {
        return this.blockStartOffset;
    }

    public byte getAuxBitsMode() {
        return this.auxBitsMode;
    }

    public ByteBuffer getChannelStatusMode() {
        return this.channelStatusMode;
    }

    public ByteBuffer getFixedChannelStatusData() {
        return this.fixedChannelStatusData;
    }

    public ByteBuffer getUserDataMode() {
        return this.userDataMode;
    }

    public ByteBuffer getFixedUserData() {
        return this.fixedUserData;
    }
}
