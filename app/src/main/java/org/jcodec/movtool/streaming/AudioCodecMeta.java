package org.jcodec.movtool.streaming;

import java.nio.ByteBuffer;
import org.jcodec.common.AudioFormat;
import org.jcodec.containers.mp4.boxes.EndianBox;
import org.jcodec.containers.mp4.boxes.channel.Label;

/* loaded from: classes.dex */
public class AudioCodecMeta extends CodecMeta {
    private int channelCount;
    private EndianBox.Endian endian;
    private Label[] labels;
    private boolean pcm;
    private int sampleRate;
    private int sampleSize;

    public AudioCodecMeta(String fourcc, int sampleSize, int channelCount, int sampleRate, EndianBox.Endian endian, boolean pcm, Label[] labels, ByteBuffer codecPrivate) {
        super(fourcc, codecPrivate);
        this.sampleSize = sampleSize;
        this.channelCount = channelCount;
        this.sampleRate = sampleRate;
        this.endian = endian;
        this.pcm = pcm;
        this.labels = labels;
    }

    public AudioCodecMeta(String fourcc, ByteBuffer codecPrivate, AudioFormat format, boolean pcm, Label[] labels) {
        super(fourcc, codecPrivate);
        this.sampleSize = format.getSampleSizeInBits() >> 3;
        this.channelCount = format.getChannels();
        this.sampleRate = format.getSampleRate();
        this.endian = format.isBigEndian() ? EndianBox.Endian.BIG_ENDIAN : EndianBox.Endian.LITTLE_ENDIAN;
        this.pcm = pcm;
        this.labels = labels;
    }

    public int getFrameSize() {
        return this.sampleSize * this.channelCount;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public EndianBox.Endian getEndian() {
        return this.endian;
    }

    public boolean isPCM() {
        return this.pcm;
    }

    public AudioFormat getFormat() {
        return new AudioFormat(this.sampleRate, this.sampleSize << 3, this.channelCount, true, this.endian == EndianBox.Endian.BIG_ENDIAN);
    }

    public Label[] getChannelLabels() {
        return this.labels;
    }
}
