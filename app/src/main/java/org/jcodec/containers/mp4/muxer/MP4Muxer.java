package org.jcodec.containers.mp4.muxer;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.EndianBox;
import org.jcodec.containers.mp4.boxes.FileTypeBox;
import org.jcodec.containers.mp4.boxes.FormatBox;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.LeafBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;

/* loaded from: classes.dex */
public class MP4Muxer {
    protected long mdatOffset;
    private int nextTrackId;
    protected SeekableByteChannel out;
    private List<AbstractMP4MuxerTrack> tracks;

    public MP4Muxer(SeekableByteChannel output) throws IOException {
        this(output, Brand.MP4);
    }

    public MP4Muxer(SeekableByteChannel output, Brand brand) throws IOException {
        this(output, brand.getFileTypeBox());
    }

    public MP4Muxer(SeekableByteChannel output, FileTypeBox ftyp) throws IOException {
        this.tracks = new ArrayList();
        this.nextTrackId = 1;
        this.out = output;
        ByteBuffer buf = ByteBuffer.allocate(1024);
        ftyp.write(buf);
        new Header("wide", 8L).write(buf);
        new Header("mdat", 1L).write(buf);
        this.mdatOffset = buf.position();
        buf.putLong(0L);
        buf.flip();
        output.write(buf);
    }

    public FramesMP4MuxerTrack addVideoTrackWithTimecode(String fourcc, Size size, String encoderName, int timescale) {
        TimecodeMP4MuxerTrack timecode = addTimecodeTrack(timescale);
        FramesMP4MuxerTrack track = addTrack(TrackType.VIDEO, timescale);
        track.addSampleEntry(videoSampleEntry(fourcc, size, encoderName));
        track.setTimecode(timecode);
        return track;
    }

    public FramesMP4MuxerTrack addVideoTrack(String fourcc, Size size, String encoderName, int timescale) {
        FramesMP4MuxerTrack track = addTrack(TrackType.VIDEO, timescale);
        track.addSampleEntry(videoSampleEntry(fourcc, size, encoderName));
        return track;
    }

    public static VideoSampleEntry videoSampleEntry(String fourcc, Size size, String encoderName) {
        return new VideoSampleEntry(new Header(fourcc), (short) 0, (short) 0, "jcod", 0, 768, (short) size.getWidth(), (short) size.getHeight(), 72L, 72L, (short) 1, encoderName != null ? encoderName : "jcodec", (short) 24, (short) 1, (short) -1);
    }

    public static AudioSampleEntry audioSampleEntry(String fourcc, int drefId, int sampleSize, int channels, int sampleRate, EndianBox.Endian endian) {
        AudioSampleEntry ase = new AudioSampleEntry(new Header(fourcc, 0L), (short) drefId, (short) channels, (short) 16, sampleRate, (short) 0, 0, SupportMenu.USER_MASK, 0, 1, sampleSize, channels * sampleSize, sampleSize, (short) 1);
        NodeBox wave = new NodeBox(new Header("wave"));
        ase.add(wave);
        wave.add(new FormatBox(fourcc));
        wave.add(new EndianBox(endian));
        wave.add(terminatorAtom());
        return ase;
    }

    public static LeafBox terminatorAtom() {
        return new LeafBox(new Header(new String(new byte[4])), ByteBuffer.allocate(0));
    }

    public TimecodeMP4MuxerTrack addTimecodeTrack(int timescale) {
        SeekableByteChannel seekableByteChannel = this.out;
        int i = this.nextTrackId;
        this.nextTrackId = i + 1;
        TimecodeMP4MuxerTrack track = new TimecodeMP4MuxerTrack(seekableByteChannel, i, timescale);
        this.tracks.add(track);
        return track;
    }

    public FramesMP4MuxerTrack addTrack(TrackType type, int timescale) {
        SeekableByteChannel seekableByteChannel = this.out;
        int i = this.nextTrackId;
        this.nextTrackId = i + 1;
        FramesMP4MuxerTrack track = new FramesMP4MuxerTrack(seekableByteChannel, i, type, timescale);
        this.tracks.add(track);
        return track;
    }

    public PCMMP4MuxerTrack addPCMTrack(int timescale, int sampleDuration, int sampleSize, SampleEntry se) {
        SeekableByteChannel seekableByteChannel = this.out;
        int i = this.nextTrackId;
        this.nextTrackId = i + 1;
        PCMMP4MuxerTrack track = new PCMMP4MuxerTrack(seekableByteChannel, i, TrackType.SOUND, timescale, sampleDuration, sampleSize, se);
        this.tracks.add(track);
        return track;
    }

    public List<AbstractMP4MuxerTrack> getTracks() {
        return this.tracks;
    }

    public void writeHeader() throws IOException {
        MovieBox movie = finalizeHeader();
        storeHeader(movie);
    }

    public void storeHeader(MovieBox movie) throws IOException {
        long mdatSize = (this.out.position() - this.mdatOffset) + 8;
        MP4Util.writeMovie(this.out, movie);
        this.out.position(this.mdatOffset);
        NIOUtils.writeLong(this.out, mdatSize);
    }

    public MovieBox finalizeHeader() throws IOException {
        MovieBox movie = new MovieBox();
        MovieHeaderBox mvhd = movieHeader(movie);
        movie.addFirst(mvhd);
        for (AbstractMP4MuxerTrack track : this.tracks) {
            Box trak = track.finish(mvhd);
            if (trak != null) {
                movie.add(trak);
            }
        }
        return movie;
    }

    public AbstractMP4MuxerTrack getVideoTrack() {
        for (AbstractMP4MuxerTrack frameMuxer : this.tracks) {
            if (frameMuxer.isVideo()) {
                return frameMuxer;
            }
        }
        return null;
    }

    public AbstractMP4MuxerTrack getTimecodeTrack() {
        for (AbstractMP4MuxerTrack frameMuxer : this.tracks) {
            if (frameMuxer.isTimecode()) {
                return frameMuxer;
            }
        }
        return null;
    }

    public List<AbstractMP4MuxerTrack> getAudioTracks() {
        ArrayList<AbstractMP4MuxerTrack> result = new ArrayList<>();
        for (AbstractMP4MuxerTrack frameMuxer : this.tracks) {
            if (frameMuxer.isAudio()) {
                result.add(frameMuxer);
            }
        }
        return result;
    }

    private MovieHeaderBox movieHeader(NodeBox movie) {
        int timescale = this.tracks.get(0).getTimescale();
        long duration = this.tracks.get(0).getTrackTotalDuration();
        AbstractMP4MuxerTrack videoTrack = getVideoTrack();
        if (videoTrack != null) {
            timescale = videoTrack.getTimescale();
            duration = videoTrack.getTrackTotalDuration();
        }
        return new MovieHeaderBox(timescale, duration, 1.0f, 1.0f, new Date().getTime(), new Date().getTime(), new int[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824}, this.nextTrackId);
    }

    public static String lookupFourcc(AudioFormat format) {
        if (format.getSampleSizeInBits() == 16 && !format.isBigEndian()) {
            return "sowt";
        }
        if (format.getSampleSizeInBits() == 24) {
            return "in24";
        }
        throw new IllegalArgumentException("Audio format " + format + " is not supported.");
    }

    public PCMMP4MuxerTrack addPCMAudioTrack(AudioFormat format) {
        return addPCMTrack(format.getSampleRate(), 1, (format.getSampleSizeInBits() >> 3) * format.getChannels(), audioSampleEntry(format));
    }

    public static AudioSampleEntry audioSampleEntry(AudioFormat format) {
        return audioSampleEntry(lookupFourcc(format), 1, format.getSampleSizeInBits() >> 3, format.getChannels(), format.getSampleRate(), format.isBigEndian() ? EndianBox.Endian.BIG_ENDIAN : EndianBox.Endian.LITTLE_ENDIAN);
    }

    public FramesMP4MuxerTrack addCompressedAudioTrack(String fourcc, int timescale, int channels, int sampleRate, int samplesPerPkt, Box... extra) {
        FramesMP4MuxerTrack track = addTrack(TrackType.SOUND, timescale);
        AudioSampleEntry ase = new AudioSampleEntry(new Header(fourcc, 0L), (short) 1, (short) channels, (short) 16, sampleRate, (short) 0, 0, 65534, 0, samplesPerPkt, 0, 0, 2, (short) 1);
        NodeBox wave = new NodeBox(new Header("wave"));
        ase.add(wave);
        wave.add(new FormatBox(fourcc));
        for (Box box : extra) {
            wave.add(box);
        }
        wave.add(terminatorAtom());
        track.addSampleEntry(ase);
        return track;
    }
}
