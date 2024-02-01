package org.jcodec.common;

/* loaded from: classes.dex */
public class AudioFormat {
    private boolean bigEndian;
    private int channelCount;
    private int sampleRate;
    private int sampleSizeInBits;
    private boolean signed;
    public static AudioFormat STEREO_48K_S16_BE = new AudioFormat(48000, 16, 2, true, true);
    public static AudioFormat STEREO_48K_S16_LE = new AudioFormat(48000, 16, 2, true, false);
    public static AudioFormat STEREO_48K_S24_BE = new AudioFormat(48000, 24, 2, true, true);
    public static AudioFormat STEREO_48K_S24_LE = new AudioFormat(48000, 24, 2, true, false);
    public static AudioFormat MONO_48K_S16_BE = new AudioFormat(48000, 16, 1, true, true);
    public static AudioFormat MONO_48K_S16_LE = new AudioFormat(48000, 16, 1, true, false);
    public static AudioFormat MONO_48K_S24_BE = new AudioFormat(48000, 24, 1, true, true);
    public static AudioFormat MONO_48K_S24_LE = new AudioFormat(48000, 24, 1, true, false);
    public static AudioFormat STEREO_44K_S16_BE = new AudioFormat(44100, 16, 2, true, true);
    public static AudioFormat STEREO_44K_S16_LE = new AudioFormat(44100, 16, 2, true, false);
    public static AudioFormat STEREO_44K_S24_BE = new AudioFormat(44100, 24, 2, true, true);
    public static AudioFormat STEREO_44K_S24_LE = new AudioFormat(44100, 24, 2, true, false);
    public static AudioFormat MONO_44K_S16_BE = new AudioFormat(44100, 16, 1, true, true);
    public static AudioFormat MONO_44K_S16_LE = new AudioFormat(44100, 16, 1, true, false);
    public static AudioFormat MONO_44K_S24_BE = new AudioFormat(44100, 24, 1, true, true);
    public static AudioFormat MONO_44K_S24_LE = new AudioFormat(44100, 24, 1, true, false);

    public static AudioFormat STEREO_S16_BE(int rate) {
        return new AudioFormat(rate, 16, 2, true, true);
    }

    public static AudioFormat STEREO_S16_LE(int rate) {
        return new AudioFormat(rate, 16, 2, true, false);
    }

    public static AudioFormat STEREO_S24_BE(int rate) {
        return new AudioFormat(rate, 24, 2, true, true);
    }

    public static AudioFormat STEREO_S24_LE(int rate) {
        return new AudioFormat(rate, 24, 2, true, false);
    }

    public static AudioFormat MONO_S16_BE(int rate) {
        return new AudioFormat(rate, 16, 1, true, true);
    }

    public static AudioFormat MONO_S16_LE(int rate) {
        return new AudioFormat(rate, 16, 1, true, false);
    }

    public static AudioFormat MONO_S24_BE(int rate) {
        return new AudioFormat(rate, 24, 1, true, true);
    }

    public static AudioFormat MONO_S24_LE(int rate) {
        return new AudioFormat(rate, 24, 1, true, false);
    }

    public static AudioFormat NCH_48K_S16_BE(int n) {
        return new AudioFormat(48000, 16, n, true, true);
    }

    public static AudioFormat NCH_48K_S16_LE(int n) {
        return new AudioFormat(48000, 16, n, true, false);
    }

    public static AudioFormat NCH_48K_S24_BE(int n) {
        return new AudioFormat(48000, 24, n, true, true);
    }

    public static AudioFormat NCH_48K_S24_LE(int n) {
        return new AudioFormat(48000, 24, n, true, false);
    }

    public static AudioFormat NCH_44K_S16_BE(int n) {
        return new AudioFormat(44100, 16, n, true, true);
    }

    public static AudioFormat NCH_44K_S16_LE(int n) {
        return new AudioFormat(44100, 16, n, true, false);
    }

    public static AudioFormat NCH_44K_S24_BE(int n) {
        return new AudioFormat(44100, 24, n, true, true);
    }

    public static AudioFormat NCH_44K_S24_LE(int n) {
        return new AudioFormat(44100, 24, n, true, false);
    }

    public AudioFormat(int sampleRate, int sampleSizeInBits, int channelCount, boolean signed, boolean bigEndian) {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channelCount = channelCount;
        this.signed = signed;
        this.bigEndian = bigEndian;
    }

    public AudioFormat(AudioFormat format, int newSampleRate) {
        this(format);
        this.sampleRate = newSampleRate;
    }

    public AudioFormat(AudioFormat format) {
        this(format.sampleRate, format.sampleSizeInBits, format.channelCount, format.signed, format.bigEndian);
    }

    public int getChannels() {
        return this.channelCount;
    }

    public int getSampleSizeInBits() {
        return this.sampleSizeInBits;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public short getFrameSize() {
        return (short) ((this.sampleSizeInBits >> 3) * this.channelCount);
    }

    public int getFrameRate() {
        return this.sampleRate;
    }

    public boolean isBigEndian() {
        return this.bigEndian;
    }

    public boolean isSigned() {
        return this.signed;
    }

    public int bytesToFrames(int bytes) {
        return bytes / ((this.channelCount * this.sampleSizeInBits) >> 3);
    }

    public int framesToBytes(int samples) {
        return ((this.channelCount * this.sampleSizeInBits) >> 3) * samples;
    }

    public int bytesToSamples(int bytes) {
        return bytes / (this.sampleSizeInBits >> 3);
    }

    public int samplesToBytes(int samples) {
        return (this.sampleSizeInBits >> 3) * samples;
    }
}
