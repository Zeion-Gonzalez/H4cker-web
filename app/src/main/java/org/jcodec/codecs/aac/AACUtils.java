package org.jcodec.codecs.aac;

import java.nio.ByteBuffer;
import org.jcodec.codecs.mpeg4.mp4.EsdsBox;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.model.ChannelLabel;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.LeafBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;

/* loaded from: classes.dex */
public class AACUtils {
    private static ChannelLabel[][] AAC_DEFAULT_CONFIGS = {null, new ChannelLabel[]{ChannelLabel.MONO}, new ChannelLabel[]{ChannelLabel.STEREO_LEFT, ChannelLabel.STEREO_RIGHT}, new ChannelLabel[]{ChannelLabel.CENTER, ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT}, new ChannelLabel[]{ChannelLabel.CENTER, ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_CENTER}, new ChannelLabel[]{ChannelLabel.CENTER, ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT}, new ChannelLabel[]{ChannelLabel.CENTER, ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT, ChannelLabel.LFE}, new ChannelLabel[]{ChannelLabel.CENTER, ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.SIDE_LEFT, ChannelLabel.SIDE_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT, ChannelLabel.LFE}};

    /* loaded from: classes.dex */
    public static class AudioInfo {
        private AudioFormat format;
        private ChannelLabel[] labels;

        public AudioInfo(AudioFormat format, ChannelLabel[] labels) {
            this.format = format;
            this.labels = labels;
        }

        public AudioFormat getFormat() {
            return this.format;
        }

        public ChannelLabel[] getLabels() {
            return this.labels;
        }
    }

    private static int getObjectType(BitReader reader) {
        int objectType = reader.readNBit(5);
        if (objectType == ObjectType.AOT_ESCAPE.ordinal()) {
            return reader.readNBit(6) + 32;
        }
        return objectType;
    }

    public static AudioInfo parseAudioInfo(ByteBuffer privData) {
        BitReader reader = new BitReader(privData);
        getObjectType(reader);
        int index = reader.readNBit(4);
        int sampleRate = index == 15 ? reader.readNBit(24) : AACConts.AAC_SAMPLE_RATES[index];
        int channelConfig = reader.readNBit(4);
        if (channelConfig == 0 || channelConfig >= AAC_DEFAULT_CONFIGS.length) {
            return null;
        }
        ChannelLabel[] channels = AAC_DEFAULT_CONFIGS[channelConfig];
        return new AudioInfo(new AudioFormat(sampleRate, 16, channels.length, true, false), channels);
    }

    public static AudioInfo getChannels(SampleEntry mp4a) {
        if (!"mp4a".equals(mp4a.getFourcc())) {
            throw new IllegalArgumentException("Not mp4a sample entry");
        }
        LeafBox b = (LeafBox) Box.findFirst(mp4a, LeafBox.class, "esds");
        if (b == null) {
            b = (LeafBox) Box.findFirst(mp4a, LeafBox.class, null, "esds");
        }
        if (b == null) {
            return null;
        }
        EsdsBox esds = new EsdsBox();
        esds.parse(b.getData());
        return parseAudioInfo(esds.getStreamInfo());
    }
}
