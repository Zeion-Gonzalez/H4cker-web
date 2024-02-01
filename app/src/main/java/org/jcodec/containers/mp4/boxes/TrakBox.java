package org.jcodec.containers.mp4.boxes;

import java.util.List;
import java.util.ListIterator;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.QTTimeUtil;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;

/* loaded from: classes.dex */
public class TrakBox extends NodeBox {
    public static String fourcc() {
        return "trak";
    }

    public TrakBox(Header atom) {
        super(atom);
    }

    public TrakBox() {
        super(new Header(fourcc()));
    }

    public void setDataRef(String url) {
        MediaInfoBox minf = getMdia().getMinf();
        DataInfoBox dinf = minf.getDinf();
        if (dinf == null) {
            dinf = new DataInfoBox();
            minf.add(dinf);
        }
        DataRefBox dref = dinf.getDref();
        UrlBox urlBox = new UrlBox(url);
        if (dref == null) {
            DataRefBox dref2 = new DataRefBox();
            dinf.add(dref2);
            dref2.add(urlBox);
        } else {
            ListIterator<Box> lit = dref.boxes.listIterator();
            while (lit.hasNext()) {
                FullBox box = (FullBox) lit.next();
                if ((box.getFlags() & 1) != 0) {
                    lit.set(urlBox);
                }
            }
        }
    }

    public MediaBox getMdia() {
        return (MediaBox) findFirst(this, MediaBox.class, "mdia");
    }

    public TrackHeaderBox getTrackHeader() {
        return (TrackHeaderBox) findFirst(this, TrackHeaderBox.class, "tkhd");
    }

    public List<Edit> getEdits() {
        EditListBox elst = (EditListBox) findFirst(this, EditListBox.class, "edts", "elst");
        if (elst == null) {
            return null;
        }
        return elst.getEdits();
    }

    public void setEdits(List<Edit> edits) {
        NodeBox edts = (NodeBox) findFirst(this, NodeBox.class, "edts");
        if (edts == null) {
            edts = new NodeBox(new Header("edts"));
            add(edts);
        }
        edts.removeChildren("elst");
        edts.add(new EditListBox(edits));
        getTrackHeader().setDuration(QTTimeUtil.getEditedDuration(this));
    }

    public boolean isVideo() {
        return "vide".equals(getHandlerType());
    }

    public boolean isTimecode() {
        return "tmcd".equals(getHandlerType());
    }

    public String getHandlerType() {
        HandlerBox handlerBox = (HandlerBox) findFirst(this, HandlerBox.class, "mdia", "hdlr");
        if (handlerBox == null) {
            return null;
        }
        return handlerBox.getComponentSubType();
    }

    public boolean isAudio() {
        return "soun".equals(getHandlerType());
    }

    public int getTimescale() {
        return ((MediaHeaderBox) findFirst(this, MediaHeaderBox.class, "mdia", "mdhd")).getTimescale();
    }

    public long rescale(long tv, long ts) {
        return (getTimescale() * tv) / ts;
    }

    public void setDuration(long duration) {
        getTrackHeader().setDuration(duration);
    }

    public long getDuration() {
        return getTrackHeader().getDuration();
    }

    public long getMediaDuration() {
        return ((MediaHeaderBox) findFirst(this, MediaHeaderBox.class, "mdia", "mdhd")).getDuration();
    }

    public boolean isPureRef() {
        DataRefBox dref;
        MediaInfoBox minf = getMdia().getMinf();
        DataInfoBox dinf = minf.getDinf();
        if (dinf == null || (dref = dinf.getDref()) == null) {
            return false;
        }
        for (Box box : dref.boxes) {
            if ((((FullBox) box).getFlags() & 1) != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasDataRef() {
        DataRefBox dref;
        DataInfoBox dinf = getMdia().getMinf().getDinf();
        if (dinf == null || (dref = dinf.getDref()) == null) {
            return false;
        }
        boolean result = false;
        for (Box box : dref.boxes) {
            result |= (((FullBox) box).getFlags() & 1) != 1;
        }
        return result;
    }

    public Rational getPAR() {
        PixelAspectExt pasp = (PixelAspectExt) NodeBox.findFirst(this, PixelAspectExt.class, "mdia", "minf", "stbl", "stsd", null, "pasp");
        return pasp == null ? new Rational(1, 1) : pasp.getRational();
    }

    public void setPAR(Rational par) {
        SampleEntry[] arr$ = getSampleEntries();
        for (SampleEntry sampleEntry : arr$) {
            sampleEntry.removeChildren("pasp");
            sampleEntry.add(new PixelAspectExt(par));
        }
    }

    public SampleEntry[] getSampleEntries() {
        return (SampleEntry[]) NodeBox.findAll(this, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null);
    }

    public void setClipRect(short x, short y, short width, short height) {
        NodeBox clip = (NodeBox) NodeBox.findFirst(this, NodeBox.class, "clip");
        if (clip == null) {
            clip = new NodeBox(new Header("clip"));
            add(clip);
        }
        clip.replace("crgn", new ClipRegionBox(x, y, width, height));
    }

    public long getSampleCount() {
        return ((SampleSizesBox) NodeBox.findFirst(this, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz")).getCount();
    }

    public void setAperture(Size sar, Size dar) {
        removeChildren("tapt");
        NodeBox tapt = new NodeBox(new Header("tapt"));
        tapt.add(new ClearApertureBox(dar.getWidth(), dar.getHeight()));
        tapt.add(new ProductionApertureBox(dar.getWidth(), dar.getHeight()));
        tapt.add(new EncodedPixelBox(sar.getWidth(), sar.getHeight()));
        add(tapt);
    }

    public void setDimensions(Size dd) {
        getTrackHeader().setWidth(dd.getWidth());
        getTrackHeader().setHeight(dd.getHeight());
    }

    public int getFrameCount() {
        SampleSizesBox stsz = (SampleSizesBox) findFirst(this, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        return stsz.getDefaultSize() != 0 ? stsz.getCount() : stsz.getSizes().length;
    }

    public String getName() {
        NameBox nb = (NameBox) Box.findFirst(this, NameBox.class, "udta", "name");
        if (nb == null) {
            return null;
        }
        return nb.getName();
    }

    public void fixMediaTimescale(int ts) {
        MediaHeaderBox mdhd = (MediaHeaderBox) Box.findFirst(this, MediaHeaderBox.class, "mdia", "mdhd");
        int oldTs = mdhd.getTimescale();
        mdhd.setTimescale(ts);
        mdhd.setDuration((ts * mdhd.getDuration()) / oldTs);
        List<Edit> edits = getEdits();
        if (edits != null) {
            for (Edit edit : edits) {
                edit.setMediaTime((ts * edit.getMediaTime()) / oldTs);
            }
        }
        TimeToSampleBox tts = (TimeToSampleBox) Box.findFirst(this, TimeToSampleBox.class, "mdia", "minf", "stbl", "stts");
        TimeToSampleBox.TimeToSampleEntry[] entries = tts.getEntries();
        for (TimeToSampleBox.TimeToSampleEntry tte : entries) {
            tte.setSampleDuration((tte.getSampleDuration() * ts) / oldTs);
        }
    }

    public void setName(String string) {
        NodeBox udta = (NodeBox) findFirst(this, NodeBox.class, "udta");
        if (udta == null) {
            udta = new NodeBox(new Header("udta"));
            add(udta);
        }
        udta.removeChildren("name");
        udta.add(new NameBox(string));
    }

    public Size getCodedSize() {
        SampleEntry se = getSampleEntries()[0];
        if (!(se instanceof VideoSampleEntry)) {
            throw new IllegalArgumentException("Not a video track");
        }
        VideoSampleEntry vse = (VideoSampleEntry) se;
        return new Size(vse.getWidth(), vse.getHeight());
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox
    protected void getModelFields(List<String> model) {
    }
}
