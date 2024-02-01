package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.Rational;

/* loaded from: classes.dex */
public class GenericSoundEssenceDescriptor extends FileDescriptor {
    private byte audioRefLevel;
    private Rational audioSamplingRate;
    private int channelCount;
    private byte dialNorm;
    private byte electroSpatialFormulation;
    private byte locked;
    private int quantizationBits;
    private C0893UL soundEssenceCompression;

    public GenericSoundEssenceDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.FileDescriptor, org.jcodec.containers.mxf.model.GenericDescriptor, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 15617:
                    this.quantizationBits = _bb.getInt();
                    break;
                case 15618:
                    this.locked = _bb.get();
                    break;
                case 15619:
                    this.audioSamplingRate = new Rational(_bb.getInt(), _bb.getInt());
                    break;
                case 15620:
                    this.audioRefLevel = _bb.get();
                    break;
                case 15621:
                    this.electroSpatialFormulation = _bb.get();
                    break;
                case 15622:
                    this.soundEssenceCompression = C0893UL.read(_bb);
                    break;
                case 15623:
                    this.channelCount = _bb.getInt();
                    break;
                case 15624:
                case 15625:
                case 15626:
                case 15627:
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
                case 15628:
                    this.dialNorm = _bb.get();
                    break;
            }
            it.remove();
        }
    }

    public Rational getAudioSamplingRate() {
        return this.audioSamplingRate;
    }

    public byte getLocked() {
        return this.locked;
    }

    public byte getAudioRefLevel() {
        return this.audioRefLevel;
    }

    public byte getElectroSpatialFormulation() {
        return this.electroSpatialFormulation;
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getQuantizationBits() {
        return this.quantizationBits;
    }

    public byte getDialNorm() {
        return this.dialNorm;
    }

    public C0893UL getSoundEssenceCompression() {
        return this.soundEssenceCompression;
    }
}
