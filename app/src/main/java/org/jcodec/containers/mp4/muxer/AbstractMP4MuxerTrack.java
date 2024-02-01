package org.jcodec.containers.mp4.muxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.common.model.Unit;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ClearApertureBox;
import org.jcodec.containers.mp4.boxes.DataInfoBox;
import org.jcodec.containers.mp4.boxes.DataRefBox;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.EditListBox;
import org.jcodec.containers.mp4.boxes.EncodedPixelBox;
import org.jcodec.containers.mp4.boxes.GenericMediaInfoBox;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.LeafBox;
import org.jcodec.containers.mp4.boxes.MediaInfoBox;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.NameBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.PixelAspectExt;
import org.jcodec.containers.mp4.boxes.ProductionApertureBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.SoundMediaHeaderBox;
import org.jcodec.containers.mp4.boxes.TimecodeMediaInfoBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.boxes.VideoMediaHeaderBox;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;

/* loaded from: classes.dex */
public abstract class AbstractMP4MuxerTrack {
    protected long chunkDuration;
    protected List<Edit> edits;
    protected boolean finished;
    private String name;
    protected Rational tgtChunkDuration;
    protected Unit tgtChunkDurationUnit;
    protected int timescale;
    protected int trackId;
    protected TrackType type;
    protected List<ByteBuffer> curChunk = new ArrayList();
    protected List<SampleToChunkBox.SampleToChunkEntry> samplesInChunks = new ArrayList();
    protected int samplesInLastChunk = -1;
    protected int chunkNo = 0;
    protected List<SampleEntry> sampleEntries = new ArrayList();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Box finish(MovieHeaderBox movieHeaderBox) throws IOException;

    public abstract long getTrackTotalDuration();

    public AbstractMP4MuxerTrack(int trackId, TrackType type, int timescale) {
        this.trackId = trackId;
        this.type = type;
        this.timescale = timescale;
    }

    public void setTgtChunkDuration(Rational duration, Unit unit) {
        this.tgtChunkDuration = duration;
        this.tgtChunkDurationUnit = unit;
    }

    public int getTimescale() {
        return this.timescale;
    }

    public boolean isVideo() {
        return this.type == TrackType.VIDEO;
    }

    public boolean isTimecode() {
        return this.type == TrackType.TIMECODE;
    }

    public boolean isAudio() {
        return this.type == TrackType.SOUND;
    }

    public Size getDisplayDimensions() {
        int width = 0;
        int height = 0;
        if (this.sampleEntries.get(0) instanceof VideoSampleEntry) {
            VideoSampleEntry vse = (VideoSampleEntry) this.sampleEntries.get(0);
            PixelAspectExt paspBox = (PixelAspectExt) Box.findFirst(vse, PixelAspectExt.class, PixelAspectExt.fourcc());
            Rational pasp = paspBox != null ? paspBox.getRational() : new Rational(1, 1);
            width = (pasp.getNum() * vse.getWidth()) / pasp.getDen();
            height = vse.getHeight();
        }
        return new Size(width, height);
    }

    public void tapt(TrakBox trak) {
        Size dd = getDisplayDimensions();
        if (this.type == TrackType.VIDEO) {
            NodeBox tapt = new NodeBox(new Header("tapt"));
            tapt.add(new ClearApertureBox(dd.getWidth(), dd.getHeight()));
            tapt.add(new ProductionApertureBox(dd.getWidth(), dd.getHeight()));
            tapt.add(new EncodedPixelBox(dd.getWidth(), dd.getHeight()));
            trak.add(tapt);
        }
    }

    public void addSampleEntry(SampleEntry se) {
        if (this.finished) {
            throw new IllegalStateException("The muxer track has finished muxing");
        }
        this.sampleEntries.add(se);
    }

    public List<SampleEntry> getEntries() {
        return this.sampleEntries;
    }

    public void setEdits(List<Edit> edits) {
        this.edits = edits;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void putEdits(TrakBox trak) {
        if (this.edits != null) {
            NodeBox edts = new NodeBox(new Header("edts"));
            edts.add(new EditListBox(this.edits));
            trak.add(edts);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void putName(TrakBox trak) {
        if (this.name != null) {
            NodeBox udta = new NodeBox(new Header("udta"));
            udta.add(new NameBox(this.name));
            trak.add(udta);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void mediaHeader(MediaInfoBox minf, TrackType type) {
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

    /* JADX INFO: Access modifiers changed from: protected */
    public void addDref(NodeBox minf) {
        DataInfoBox dinf = new DataInfoBox();
        minf.add(dinf);
        DataRefBox dref = new DataRefBox();
        dinf.add(dref);
        dref.add(new LeafBox(new Header("alis", 0L), ByteBuffer.wrap(new byte[]{0, 0, 0, 1})));
    }
}
