package org.jcodec.containers.mp4.boxes;

import com.swift.sandhook.annotation.MethodReflectParams;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.model.ChannelLabel;
import org.jcodec.common.tools.ToJSON;
import org.jcodec.containers.mp4.boxes.EndianBox;
import org.jcodec.containers.mp4.boxes.channel.ChannelUtils;
import org.jcodec.containers.mp4.boxes.channel.Label;

/* loaded from: classes.dex */
public class AudioSampleEntry extends SampleEntry {
    private static Map<Label, ChannelLabel> translationStereo;
    private static Map<Label, ChannelLabel> translationSurround;
    private int bytesPerFrame;
    private int bytesPerPkt;
    private int bytesPerSample;
    private short channelCount;
    private int compressionId;
    private int lpcmFlags;
    private int pktSize;
    private short revision;
    private float sampleRate;
    private short sampleSize;
    private int samplesPerPkt;
    private int vendor;
    private short version;
    public static int kAudioFormatFlagIsFloat = 1;
    public static int kAudioFormatFlagIsBigEndian = 2;
    public static int kAudioFormatFlagIsSignedInteger = 4;
    public static int kAudioFormatFlagIsPacked = 8;
    public static int kAudioFormatFlagIsAlignedHigh = 16;
    public static int kAudioFormatFlagIsNonInterleaved = 32;
    public static int kAudioFormatFlagIsNonMixable = 64;
    private static final MyFactory FACTORY = new MyFactory();
    public static Set<String> pcms = new HashSet();

    static {
        pcms.add("raw ");
        pcms.add("twos");
        pcms.add("sowt");
        pcms.add("fl32");
        pcms.add("fl64");
        pcms.add("in24");
        pcms.add("in32");
        pcms.add("lpcm");
        translationStereo = new HashMap();
        translationSurround = new HashMap();
        translationStereo.put(Label.Left, ChannelLabel.STEREO_LEFT);
        translationStereo.put(Label.Right, ChannelLabel.STEREO_RIGHT);
        translationStereo.put(Label.HeadphonesLeft, ChannelLabel.STEREO_LEFT);
        translationStereo.put(Label.HeadphonesRight, ChannelLabel.STEREO_RIGHT);
        translationStereo.put(Label.LeftTotal, ChannelLabel.STEREO_LEFT);
        translationStereo.put(Label.RightTotal, ChannelLabel.STEREO_RIGHT);
        translationStereo.put(Label.LeftWide, ChannelLabel.STEREO_LEFT);
        translationStereo.put(Label.RightWide, ChannelLabel.STEREO_RIGHT);
        translationSurround.put(Label.Left, ChannelLabel.FRONT_LEFT);
        translationSurround.put(Label.Right, ChannelLabel.FRONT_RIGHT);
        translationSurround.put(Label.LeftCenter, ChannelLabel.FRONT_CENTER_LEFT);
        translationSurround.put(Label.RightCenter, ChannelLabel.FRONT_CENTER_RIGHT);
        translationSurround.put(Label.Center, ChannelLabel.CENTER);
        translationSurround.put(Label.CenterSurround, ChannelLabel.REAR_CENTER);
        translationSurround.put(Label.CenterSurroundDirect, ChannelLabel.REAR_CENTER);
        translationSurround.put(Label.LeftSurround, ChannelLabel.REAR_LEFT);
        translationSurround.put(Label.LeftSurroundDirect, ChannelLabel.REAR_LEFT);
        translationSurround.put(Label.RightSurround, ChannelLabel.REAR_RIGHT);
        translationSurround.put(Label.RightSurroundDirect, ChannelLabel.REAR_RIGHT);
        translationSurround.put(Label.RearSurroundLeft, ChannelLabel.SIDE_LEFT);
        translationSurround.put(Label.RearSurroundRight, ChannelLabel.SIDE_RIGHT);
        translationSurround.put(Label.LFE2, ChannelLabel.LFE);
        translationSurround.put(Label.LFEScreen, ChannelLabel.LFE);
        translationSurround.put(Label.LeftTotal, ChannelLabel.STEREO_LEFT);
        translationSurround.put(Label.RightTotal, ChannelLabel.STEREO_RIGHT);
        translationSurround.put(Label.LeftWide, ChannelLabel.STEREO_LEFT);
        translationSurround.put(Label.RightWide, ChannelLabel.STEREO_RIGHT);
    }

    public AudioSampleEntry(Header atom) {
        super(atom);
        this.factory = FACTORY;
    }

    public AudioSampleEntry(Header header, short drefInd, short channelCount, short sampleSize, int sampleRate, short revision, int vendor, int compressionId, int pktSize, int samplesPerPkt, int bytesPerPkt, int bytesPerFrame, int bytesPerSample, short version) {
        super(header, drefInd);
        this.channelCount = channelCount;
        this.sampleSize = sampleSize;
        this.sampleRate = sampleRate;
        this.revision = revision;
        this.vendor = vendor;
        this.compressionId = compressionId;
        this.pktSize = pktSize;
        this.samplesPerPkt = samplesPerPkt;
        this.bytesPerPkt = bytesPerPkt;
        this.bytesPerFrame = bytesPerFrame;
        this.bytesPerSample = bytesPerSample;
        this.version = version;
    }

    @Override // org.jcodec.containers.mp4.boxes.SampleEntry, org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.version = input.getShort();
        this.revision = input.getShort();
        this.vendor = input.getInt();
        this.channelCount = input.getShort();
        this.sampleSize = input.getShort();
        this.compressionId = input.getShort();
        this.pktSize = input.getShort();
        long sr = input.getInt() & 4294967295L;
        this.sampleRate = ((float) sr) / 65536.0f;
        if (this.version == 1) {
            this.samplesPerPkt = input.getInt();
            this.bytesPerPkt = input.getInt();
            this.bytesPerFrame = input.getInt();
            this.bytesPerSample = input.getInt();
        } else if (this.version == 2) {
            input.getInt();
            this.sampleRate = (float) Double.longBitsToDouble(input.getLong());
            this.channelCount = (short) input.getInt();
            input.getInt();
            this.sampleSize = (short) input.getInt();
            this.lpcmFlags = input.getInt();
            this.bytesPerFrame = input.getInt();
            this.samplesPerPkt = input.getInt();
        }
        parseExtensions(input);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.SampleEntry, org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putShort(this.version);
        out.putShort(this.revision);
        out.putInt(this.vendor);
        if (this.version < 2) {
            out.putShort(this.channelCount);
            if (this.version == 0) {
                out.putShort(this.sampleSize);
            } else {
                out.putShort((short) 16);
            }
            out.putShort((short) this.compressionId);
            out.putShort((short) this.pktSize);
            out.putInt((int) Math.round(this.sampleRate * 65536.0d));
            if (this.version == 1) {
                out.putInt(this.samplesPerPkt);
                out.putInt(this.bytesPerPkt);
                out.putInt(this.bytesPerFrame);
                out.putInt(this.bytesPerSample);
            }
        } else if (this.version == 2) {
            out.putShort((short) 3);
            out.putShort((short) 16);
            out.putShort((short) -2);
            out.putShort((short) 0);
            out.putInt(65536);
            out.putInt(72);
            out.putLong(Double.doubleToLongBits(this.sampleRate));
            out.putInt(this.channelCount);
            out.putInt(2130706432);
            out.putInt(this.sampleSize);
            out.putInt(this.lpcmFlags);
            out.putInt(this.bytesPerFrame);
            out.putInt(this.samplesPerPkt);
        }
        writeExtensions(out);
    }

    public short getChannelCount() {
        return this.channelCount;
    }

    public int calcFrameSize() {
        return (this.version == 0 || this.bytesPerFrame == 0) ? (this.sampleSize >> 3) * this.channelCount : this.bytesPerFrame;
    }

    public int calcSampleSize() {
        return calcFrameSize() / this.channelCount;
    }

    public short getSampleSize() {
        return this.sampleSize;
    }

    public float getSampleRate() {
        return this.sampleRate;
    }

    public int getBytesPerFrame() {
        return this.bytesPerFrame;
    }

    public int getBytesPerSample() {
        return this.bytesPerSample;
    }

    public short getVersion() {
        return this.version;
    }

    /* loaded from: classes.dex */
    public static class MyFactory extends BoxFactory {
        private Map<String, Class<? extends Box>> mappings = new HashMap();

        public MyFactory() {
            this.mappings.put(WaveExtension.fourcc(), WaveExtension.class);
            this.mappings.put(ChannelBox.fourcc(), ChannelBox.class);
            this.mappings.put("esds", LeafBox.class);
        }

        @Override // org.jcodec.containers.mp4.boxes.BoxFactory
        public Class<? extends Box> toClass(String fourcc) {
            return this.mappings.get(fourcc);
        }
    }

    public EndianBox.Endian getEndian() {
        EndianBox endianBox = (EndianBox) Box.findFirst(this, EndianBox.class, WaveExtension.fourcc(), EndianBox.fourcc());
        if (endianBox == null) {
            if ("twos".equals(this.header.getFourcc())) {
                return EndianBox.Endian.BIG_ENDIAN;
            }
            if ("lpcm".equals(this.header.getFourcc())) {
                return (this.lpcmFlags & kAudioFormatFlagIsBigEndian) != 0 ? EndianBox.Endian.BIG_ENDIAN : EndianBox.Endian.LITTLE_ENDIAN;
            }
            if ("sowt".equals(this.header.getFourcc())) {
                return EndianBox.Endian.LITTLE_ENDIAN;
            }
            return EndianBox.Endian.BIG_ENDIAN;
        }
        return endianBox.getEndian();
    }

    public boolean isFloat() {
        return "fl32".equals(this.header.getFourcc()) || "fl64".equals(this.header.getFourcc()) || ("lpcm".equals(this.header.getFourcc()) && (this.lpcmFlags & kAudioFormatFlagIsFloat) != 0);
    }

    public boolean isPCM() {
        return pcms.contains(this.header.getFourcc());
    }

    public AudioFormat getFormat() {
        return new AudioFormat((int) this.sampleRate, calcSampleSize() << 3, this.channelCount, true, getEndian() == EndianBox.Endian.BIG_ENDIAN);
    }

    public ChannelLabel[] getLabels() {
        ChannelBox channelBox = (ChannelBox) Box.findFirst(this, ChannelBox.class, "chan");
        if (channelBox != null) {
            Label[] labels = ChannelUtils.getLabels(channelBox);
            if (this.channelCount == 2) {
                return translate(translationStereo, labels);
            }
            return translate(translationSurround, labels);
        }
        switch (this.channelCount) {
            case 1:
                return new ChannelLabel[]{ChannelLabel.MONO};
            case 2:
                return new ChannelLabel[]{ChannelLabel.STEREO_LEFT, ChannelLabel.STEREO_RIGHT};
            case 3:
            case 4:
            case 5:
            default:
                ChannelLabel[] lbl = new ChannelLabel[this.channelCount];
                Arrays.fill(lbl, ChannelLabel.MONO);
                return lbl;
            case 6:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.CENTER, ChannelLabel.LFE, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT};
        }
    }

    private ChannelLabel[] translate(Map<Label, ChannelLabel> translation, Label[] labels) {
        ChannelLabel[] result = new ChannelLabel[labels.length];
        int len$ = labels.length;
        int i$ = 0;
        int i = 0;
        while (i$ < len$) {
            Label label = labels[i$];
            result[i] = translation.get(label);
            i$++;
            i++;
        }
        return result;
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox
    protected void getModelFields(List<String> list) {
        ToJSON.allFieldsExcept(getClass(), "endian", MethodReflectParams.FLOAT, "format", "labels");
    }
}
