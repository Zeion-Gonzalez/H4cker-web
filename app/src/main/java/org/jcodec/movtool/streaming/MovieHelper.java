package org.jcodec.movtool.streaming;

import andhook.lib.xposed.callbacks.XCallback;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.LongArrayList;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.ChannelBox;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ClearApertureBox;
import org.jcodec.containers.mp4.boxes.CompositionOffsetsBox;
import org.jcodec.containers.mp4.boxes.DataInfoBox;
import org.jcodec.containers.mp4.boxes.DataRefBox;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.EncodedPixelBox;
import org.jcodec.containers.mp4.boxes.GenericMediaInfoBox;
import org.jcodec.containers.mp4.boxes.HandlerBox;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.LeafBox;
import org.jcodec.containers.mp4.boxes.MediaBox;
import org.jcodec.containers.mp4.boxes.MediaHeaderBox;
import org.jcodec.containers.mp4.boxes.MediaInfoBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.PixelAspectExt;
import org.jcodec.containers.mp4.boxes.ProductionApertureBox;
import org.jcodec.containers.mp4.boxes.SampleDescriptionBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.SoundMediaHeaderBox;
import org.jcodec.containers.mp4.boxes.SyncSamplesBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TimecodeMediaInfoBox;
import org.jcodec.containers.mp4.boxes.TrackHeaderBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.boxes.VideoMediaHeaderBox;
import org.jcodec.containers.mp4.boxes.channel.ChannelUtils;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.movtool.streaming.VirtualMP4Movie;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class MovieHelper {
    private static final int MEBABYTE = 1048576;
    private static int[] timescales = {XCallback.PRIORITY_HIGHEST, 12000, 15000, 24000, 25000, 30000, 50000, 41000, 48000, 96000};

    public static ByteBuffer produceHeader(VirtualMP4Movie.PacketChunk[] chunks, VirtualTrack[] tracks, long dataSize, Brand brand) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(6291456);
        MovieBox movie = new MovieBox();
        double[] trackDurations = calcTrackDurations(chunks, tracks);
        long movieDur = calcMovieDuration(tracks, 1000, trackDurations);
        movie.add(movieHeader(movie, tracks.length, movieDur, 1000));
        for (int trackId = 0; trackId < tracks.length; trackId++) {
            VirtualTrack track = tracks[trackId];
            CodecMeta codecMeta = track.getCodecMeta();
            boolean pcm = (codecMeta instanceof AudioCodecMeta) && ((AudioCodecMeta) codecMeta).isPCM();
            int trackTimescale = track.getPreferredTimescale();
            if (trackTimescale <= 0) {
                if (pcm) {
                    trackTimescale = getPCMTs((AudioCodecMeta) codecMeta, chunks, trackId);
                } else {
                    trackTimescale = chooseTimescale(chunks, trackId);
                }
            } else if (trackTimescale < 100) {
                trackTimescale *= 1000;
            } else if (trackTimescale < 1000) {
                trackTimescale *= 100;
            } else if (trackTimescale < 10000) {
                trackTimescale *= 10;
            }
            long totalDur = (long) (trackTimescale * trackDurations[trackId]);
            TrakBox trak = new TrakBox();
            Size dd = new Size(0, 0);
            Size sd = new Size(0, 0);
            if (codecMeta instanceof VideoCodecMeta) {
                VideoCodecMeta meta = (VideoCodecMeta) codecMeta;
                Rational pasp = meta.getPasp();
                if (pasp == null) {
                    dd = meta.getSize();
                    sd = dd;
                } else {
                    sd = meta.getSize();
                    dd = new Size(pasp.multiplyS(sd.getWidth()), sd.getHeight());
                }
            }
            TrackHeaderBox tkhd = new TrackHeaderBox(trackId + 1, movieDur, dd.getWidth(), dd.getHeight(), new Date().getTime(), new Date().getTime(), 1.0f, (short) 0, 0L, new int[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824});
            tkhd.setFlags(15);
            trak.add(tkhd);
            MediaBox media = new MediaBox();
            trak.add(media);
            media.add(new MediaHeaderBox(trackTimescale, totalDur, 0, new Date().getTime(), new Date().getTime(), 0));
            TrackType tt = codecMeta instanceof AudioCodecMeta ? TrackType.SOUND : TrackType.VIDEO;
            if (tt == TrackType.VIDEO) {
                NodeBox tapt = new NodeBox(new Header("tapt"));
                tapt.add(new ClearApertureBox(dd.getWidth(), dd.getHeight()));
                tapt.add(new ProductionApertureBox(dd.getWidth(), dd.getHeight()));
                tapt.add(new EncodedPixelBox(sd.getWidth(), sd.getHeight()));
                trak.add(tapt);
            }
            HandlerBox hdlr = new HandlerBox("mhlr", tt.getHandler(), "appl", 0, 0);
            media.add(hdlr);
            MediaInfoBox minf = new MediaInfoBox();
            media.add(minf);
            mediaHeader(minf, tt);
            minf.add(new HandlerBox("dhlr", "url ", "appl", 0, 0));
            addDref(minf);
            NodeBox stbl = new NodeBox(new Header("stbl"));
            minf.add(stbl);
            stbl.add(new SampleDescriptionBox(toSampleEntry(codecMeta)));
            if (pcm) {
                populateStblPCM(stbl, chunks, trackId, codecMeta);
            } else {
                populateStblGeneric(stbl, chunks, trackId, codecMeta, trackTimescale);
            }
            addEdits(trak, track, 1000, trackTimescale);
            movie.add(trak);
        }
        brand.getFileTypeBox().write(buf);
        movie.write(buf);
        new Header("mdat", dataSize).write(buf);
        buf.flip();
        return buf;
    }

    private static SampleEntry toSampleEntry(CodecMeta se) {
        SampleEntry vse;
        Rational pasp = null;
        if ("avc1".equals(se.getFourcc())) {
            AvcCBox avcC = H264Utils.parseAVCC(se.getCodecPrivate().duplicate());
            vse = H264Utils.createMOVSampleEntry(avcC);
            pasp = ((VideoCodecMeta) se).getPasp();
        } else if (se instanceof VideoCodecMeta) {
            VideoCodecMeta ss = (VideoCodecMeta) se;
            pasp = ss.getPasp();
            vse = MP4Muxer.videoSampleEntry(se.getFourcc(), ss.getSize(), "JCodec");
        } else {
            AudioCodecMeta ss2 = (AudioCodecMeta) se;
            vse = MP4Muxer.audioSampleEntry(se.getFourcc(), 0, ss2.getSampleSize(), ss2.getChannelCount(), ss2.getSampleRate(), ss2.getEndian());
            ChannelBox chan = new ChannelBox();
            ChannelUtils.setLabels(ss2.getChannelLabels(), chan);
            vse.add(chan);
        }
        if (pasp != null) {
            vse.add(new PixelAspectExt(pasp));
        }
        return vse;
    }

    private static int chooseTimescale(VirtualMP4Movie.PacketChunk[] chunks, int trackId) {
        for (int ch = 0; ch < chunks.length; ch++) {
            if (chunks[ch].getTrackNo() == trackId) {
                double dur = chunks[ch].getPacket().getDuration();
                double min = Double.MAX_VALUE;
                int minTs = -1;
                for (int ts = 0; ts < timescales.length; ts++) {
                    double dd = timescales[ts] * dur;
                    double diff = dd - ((int) dd);
                    if (diff < min) {
                        minTs = ts;
                        min = diff;
                    }
                }
                return timescales[minTs];
            }
        }
        return 0;
    }

    private static void addEdits(TrakBox trak, VirtualTrack track, int defaultTimescale, int trackTimescale) {
        VirtualTrack.VirtualEdit[] edits = track.getEdits();
        if (edits != null) {
            List<Edit> result = new ArrayList<>();
            for (VirtualTrack.VirtualEdit virtualEdit : edits) {
                result.add(new Edit((int) (virtualEdit.getDuration() * defaultTimescale), (int) (virtualEdit.getIn() * trackTimescale), 1.0f));
            }
            trak.setEdits(result);
        }
    }

    private static long calcMovieDuration(VirtualTrack[] tracks, int defaultTimescale, double[] dur) {
        long movieDur = 0;
        for (int trackId = 0; trackId < tracks.length; trackId++) {
            movieDur = Math.max(movieDur, (long) (defaultTimescale * dur[trackId]));
        }
        return movieDur;
    }

    private static double[] calcTrackDurations(VirtualMP4Movie.PacketChunk[] chunks, VirtualTrack[] tracks) {
        double[] dur = new double[tracks.length];
        Arrays.fill(dur, -1.0d);
        int n = 0;
        for (int chunkId = chunks.length - 1; chunkId >= 0 && n < dur.length; chunkId--) {
            VirtualMP4Movie.PacketChunk chunk = chunks[chunkId];
            int track = chunk.getTrackNo();
            if (dur[track] == -1.0d) {
                dur[track] = chunk.getPacket().getPts() + chunk.getPacket().getDuration();
                n++;
            }
        }
        return dur;
    }

    private static void populateStblGeneric(NodeBox stbl, VirtualMP4Movie.PacketChunk[] chunks, int trackId, CodecMeta se, int timescale) throws IOException {
        LongArrayList stco = new LongArrayList(256000);
        IntArrayList stsz = new IntArrayList(256000);
        List<TimeToSampleBox.TimeToSampleEntry> stts = new ArrayList<>();
        IntArrayList stss = new IntArrayList(4096);
        int prevDur = 0;
        int prevCount = -1;
        boolean allKey = true;
        List<CompositionOffsetsBox.Entry> compositionOffsets = new ArrayList<>();
        long ptsEstimate = 0;
        int lastCompositionSamples = 0;
        int lastCompositionOffset = 0;
        for (VirtualMP4Movie.PacketChunk chunk : chunks) {
            if (chunk.getTrackNo() == trackId) {
                stco.add(chunk.getPos());
                stsz.add(Math.max(0, chunk.getDataLen()));
                int dur = (int) Math.round(chunk.getPacket().getDuration() * timescale);
                if (dur != prevDur) {
                    if (prevCount != -1) {
                        stts.add(new TimeToSampleBox.TimeToSampleEntry(prevCount, prevDur));
                    }
                    prevDur = dur;
                    prevCount = 0;
                }
                prevCount++;
                boolean key = chunk.getPacket().isKeyframe();
                allKey &= key;
                if (key) {
                    stss.add(chunk.getPacket().getFrameNo() + 1);
                }
                long pts = Math.round(chunk.getPacket().getPts() * timescale);
                int compositionOffset = (int) (pts - ptsEstimate);
                if (compositionOffset != lastCompositionOffset) {
                    if (lastCompositionSamples > 0) {
                        compositionOffsets.add(new CompositionOffsetsBox.Entry(lastCompositionSamples, lastCompositionOffset));
                    }
                    lastCompositionOffset = compositionOffset;
                    lastCompositionSamples = 0;
                }
                lastCompositionSamples++;
                ptsEstimate += dur;
            }
        }
        if (compositionOffsets.size() > 0) {
            compositionOffsets.add(new CompositionOffsetsBox.Entry(lastCompositionSamples, lastCompositionOffset));
        }
        if (prevCount > 0) {
            stts.add(new TimeToSampleBox.TimeToSampleEntry(prevCount, prevDur));
        }
        if (!allKey) {
            stbl.add(new SyncSamplesBox(stss.toArray()));
        }
        stbl.add(new ChunkOffsets64Box(stco.toArray()));
        stbl.add(new SampleToChunkBox(new SampleToChunkBox.SampleToChunkEntry[]{new SampleToChunkBox.SampleToChunkEntry(1L, 1, 1)}));
        stbl.add(new SampleSizesBox(stsz.toArray()));
        stbl.add(new TimeToSampleBox((TimeToSampleBox.TimeToSampleEntry[]) stts.toArray(new TimeToSampleBox.TimeToSampleEntry[0])));
        compositionOffsets(compositionOffsets, stbl);
    }

    private static void compositionOffsets(List<CompositionOffsetsBox.Entry> compositionOffsets, NodeBox stbl) {
        if (compositionOffsets.size() > 0) {
            int min = FramesMP4MuxerTrack.minOffset(compositionOffsets);
            for (CompositionOffsetsBox.Entry entry : compositionOffsets) {
                entry.offset -= min;
            }
            stbl.add(new CompositionOffsetsBox((CompositionOffsetsBox.Entry[]) compositionOffsets.toArray(new CompositionOffsetsBox.Entry[0])));
        }
    }

    private static void populateStblPCM(NodeBox stbl, VirtualMP4Movie.PacketChunk[] chunks, int trackId, CodecMeta se) throws IOException {
        AudioCodecMeta ase = (AudioCodecMeta) se;
        int frameSize = ase.getFrameSize();
        LongArrayList stco = new LongArrayList(256000);
        List<SampleToChunkBox.SampleToChunkEntry> stsc = new ArrayList<>();
        int stscCount = -1;
        int stscFirstChunk = -1;
        int totalFrames = 0;
        int stscCurChunk = 1;
        for (VirtualMP4Movie.PacketChunk chunk : chunks) {
            if (chunk.getTrackNo() == trackId) {
                stco.add(chunk.getPos());
                int framesPerChunk = chunk.getDataLen() / frameSize;
                if (framesPerChunk != stscCount) {
                    if (stscCount != -1) {
                        stsc.add(new SampleToChunkBox.SampleToChunkEntry(stscFirstChunk, stscCount, 1));
                    }
                    stscFirstChunk = stscCurChunk;
                    stscCount = framesPerChunk;
                }
                stscCurChunk++;
                totalFrames += framesPerChunk;
            }
        }
        if (stscCount != -1) {
            stsc.add(new SampleToChunkBox.SampleToChunkEntry(stscFirstChunk, stscCount, 1));
        }
        stbl.add(new ChunkOffsets64Box(stco.toArray()));
        stbl.add(new SampleToChunkBox((SampleToChunkBox.SampleToChunkEntry[]) stsc.toArray(new SampleToChunkBox.SampleToChunkEntry[0])));
        stbl.add(new SampleSizesBox(ase.getFrameSize(), totalFrames));
        stbl.add(new TimeToSampleBox(new TimeToSampleBox.TimeToSampleEntry[]{new TimeToSampleBox.TimeToSampleEntry(totalFrames, 1)}));
    }

    private static int getPCMTs(AudioCodecMeta se, VirtualMP4Movie.PacketChunk[] chunks, int trackId) throws IOException {
        for (int chunkNo = 0; chunkNo < chunks.length; chunkNo++) {
            if (chunks[chunkNo].getTrackNo() == trackId) {
                return (int) Math.round(chunks[chunkNo].getDataLen() / (se.getFrameSize() * chunks[chunkNo].getPacket().getDuration()));
            }
        }
        throw new RuntimeException("Crap");
    }

    private static void mediaHeader(MediaInfoBox minf, TrackType type) {
        switch (type) {
            case VIDEO:
                VideoMediaHeaderBox vmhd = new VideoMediaHeaderBox(0, 0, 0, 0);
                vmhd.setFlags(1);
                minf.add(vmhd);
                return;
            case SOUND:
                SoundMediaHeaderBox smhd = new SoundMediaHeaderBox();
                smhd.setFlags(1);
                minf.add(smhd);
                return;
            case TIMECODE:
                NodeBox gmhd = new NodeBox(new Header("gmhd"));
                gmhd.add(new GenericMediaInfoBox());
                NodeBox tmcd = new NodeBox(new Header("tmcd"));
                gmhd.add(tmcd);
                tmcd.add(new TimecodeMediaInfoBox((short) 0, (short) 0, (short) 12, new short[]{0, 0, 0}, new short[]{255, 255, 255}, "Lucida Grande"));
                minf.add(gmhd);
                return;
            default:
                throw new IllegalStateException("Handler " + type.getHandler() + " not supported");
        }
    }

    private static void addDref(NodeBox minf) {
        DataInfoBox dinf = new DataInfoBox();
        minf.add(dinf);
        DataRefBox dref = new DataRefBox();
        dinf.add(dref);
        dref.add(new LeafBox(new Header("alis", 0L), ByteBuffer.wrap(new byte[]{0, 0, 0, 1})));
    }

    private static MovieHeaderBox movieHeader(NodeBox movie, int nTracks, long duration, int timescale) {
        return new MovieHeaderBox(timescale, duration, 1.0f, 1.0f, new Date().getTime(), new Date().getTime(), new int[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824}, nTracks + 1);
    }
}
