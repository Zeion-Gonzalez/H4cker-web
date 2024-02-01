package org.jcodec.containers.mkv;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcodec.containers.mkv.boxes.EbmlBase;
import org.jcodec.containers.mkv.boxes.EbmlBin;
import org.jcodec.containers.mkv.boxes.EbmlDate;
import org.jcodec.containers.mkv.boxes.EbmlFloat;
import org.jcodec.containers.mkv.boxes.EbmlMaster;
import org.jcodec.containers.mkv.boxes.EbmlSint;
import org.jcodec.containers.mkv.boxes.EbmlString;
import org.jcodec.containers.mkv.boxes.EbmlUint;
import org.jcodec.containers.mkv.boxes.EbmlVoid;
import org.jcodec.containers.mkv.boxes.MkvBlock;
import org.jcodec.containers.mkv.boxes.MkvSegment;
import org.jcodec.containers.mkv.util.EbmlUtil;
import org.jcodec.containers.mxf.model.BER;

/* loaded from: classes.dex */
public enum MKVType {
    Void(new byte[]{-20}, EbmlVoid.class),
    CRC32(new byte[]{-65}, EbmlBin.class),
    EBML(new byte[]{26, 69, -33, -93}, EbmlMaster.class),
    EBMLVersion(new byte[]{66, -122}),
    EBMLReadVersion(new byte[]{66, -9}),
    EBMLMaxIDLength(new byte[]{66, -14}),
    EBMLMaxSizeLength(new byte[]{66, -13}),
    DocType(new byte[]{66, -126}, EbmlString.class),
    DocTypeVersion(new byte[]{66, -121}),
    DocTypeReadVersion(new byte[]{66, -123}),
    Segment(new byte[]{24, 83, BER.ASN_LONG_LEN, 103}, MkvSegment.class),
    SeekHead(new byte[]{17, 77, -101, 116}, EbmlMaster.class),
    Seek(new byte[]{77, -69}, EbmlMaster.class),
    SeekID(new byte[]{83, -85}, EbmlBin.class),
    SeekPosition(new byte[]{83, -84}),
    Info(new byte[]{21, 73, -87, 102}, EbmlMaster.class),
    SegmentUID(new byte[]{115, -92}, EbmlBin.class),
    SegmentFilename(new byte[]{115, -124}, EbmlString.class),
    PrevUID(new byte[]{60, -71, 35}, EbmlBin.class),
    PrevFilename(new byte[]{60, -125, -85}, EbmlString.class),
    NextUID(new byte[]{62, -71, 35}, EbmlBin.class),
    NextFilenam(new byte[]{62, -125, -69}, EbmlString.class),
    SegmentFamily(new byte[]{68, 68}, EbmlBin.class),
    ChapterTranslate(new byte[]{105, 36}, EbmlMaster.class),
    ChapterTranslateEditionUID(new byte[]{105, -4}),
    ChapterTranslateCodec(new byte[]{105, -65}),
    ChapterTranslateID(new byte[]{105, -91}, EbmlBin.class),
    TimecodeScale(new byte[]{42, -41, -79}),
    Duration(new byte[]{68, -119}, EbmlFloat.class),
    DateUTC(new byte[]{68, 97}, EbmlDate.class),
    Title(new byte[]{123, -87}, EbmlString.class),
    MuxingApp(new byte[]{77, BER.ASN_LONG_LEN}, EbmlString.class),
    WritingApp(new byte[]{87, 65}, EbmlString.class),
    Cluster(new byte[]{31, 67, -74, 117}, EbmlMaster.class),
    Timecode(new byte[]{-25}),
    SilentTracks(new byte[]{88, 84}, EbmlMaster.class),
    SilentTrackNumber(new byte[]{88, -41}),
    Position(new byte[]{-89}),
    PrevSize(new byte[]{-85}),
    SimpleBlock(new byte[]{-93}, MkvBlock.class),
    BlockGroup(new byte[]{-96}, EbmlMaster.class),
    Block(new byte[]{-95}, MkvBlock.class),
    BlockAdditions(new byte[]{117, -95}, EbmlMaster.class),
    BlockMore(new byte[]{-90}, EbmlMaster.class),
    BlockAddID(new byte[]{-18}),
    BlockAdditional(new byte[]{-91}, EbmlBin.class),
    BlockDuration(new byte[]{-101}),
    ReferencePriority(new byte[]{-6}),
    ReferenceBlock(new byte[]{-5}, EbmlSint.class),
    CodecState(new byte[]{-92}, EbmlBin.class),
    Slices(new byte[]{-114}, EbmlMaster.class),
    TimeSlice(new byte[]{-24}, EbmlMaster.class),
    LaceNumber(new byte[]{-52}),
    Tracks(new byte[]{22, 84, -82, 107}, EbmlMaster.class),
    TrackEntry(new byte[]{-82}, EbmlMaster.class),
    TrackNumber(new byte[]{-41}),
    TrackUID(new byte[]{115, -59}),
    TrackType(new byte[]{-125}),
    FlagEnabled(new byte[]{-71}),
    FlagDefault(new byte[]{-120}),
    FlagForced(new byte[]{85, -86}),
    FlagLacing(new byte[]{-100}),
    MinCache(new byte[]{109, -25}),
    MaxCache(new byte[]{109, -8}),
    DefaultDuration(new byte[]{35, -29, -125}),
    MaxBlockAdditionID(new byte[]{85, -18}),
    Name(new byte[]{83, 110}, EbmlString.class),
    Language(new byte[]{34, -75, -100}, EbmlString.class),
    CodecID(new byte[]{-122}, EbmlString.class),
    CodecPrivate(new byte[]{99, -94}, EbmlBin.class),
    CodecName(new byte[]{37, -122, -120}, EbmlString.class),
    AttachmentLink(new byte[]{116, 70}),
    CodecDecodeAll(new byte[]{-86}),
    TrackOverlay(new byte[]{111, -85}),
    TrackTranslate(new byte[]{102, 36}, EbmlMaster.class),
    TrackTranslateEditionUID(new byte[]{102, -4}),
    TrackTranslateCodec(new byte[]{102, -65}),
    TrackTranslateTrackID(new byte[]{102, -91}, EbmlBin.class),
    Video(new byte[]{-32}, EbmlMaster.class),
    FlagInterlaced(new byte[]{-102}),
    StereoMode(new byte[]{83, -72}),
    AlphaMode(new byte[]{83, -64}),
    PixelWidth(new byte[]{-80}),
    PixelHeight(new byte[]{-70}),
    PixelCropBottom(new byte[]{84, -86}),
    PixelCropTop(new byte[]{84, -69}),
    PixelCropLeft(new byte[]{84, -52}),
    PixelCropRight(new byte[]{84, -35}),
    DisplayWidth(new byte[]{84, -80}),
    DisplayHeight(new byte[]{84, -70}),
    DisplayUnit(new byte[]{84, -78}),
    AspectRatioType(new byte[]{84, -77}),
    ColourSpace(new byte[]{46, -75, 36}, EbmlBin.class),
    Audio(new byte[]{-31}, EbmlMaster.class),
    SamplingFrequency(new byte[]{-75}, EbmlFloat.class),
    OutputSamplingFrequency(new byte[]{120, -75}, EbmlFloat.class),
    Channels(new byte[]{-97}),
    BitDepth(new byte[]{98, 100}),
    TrackOperation(new byte[]{-30}, EbmlMaster.class),
    TrackCombinePlanes(new byte[]{-29}, EbmlMaster.class),
    TrackPlane(new byte[]{-28}, EbmlMaster.class),
    TrackPlaneUID(new byte[]{-27}),
    TrackPlaneType(new byte[]{-26}),
    TrackJoinBlocks(new byte[]{-23}, EbmlMaster.class),
    TrackJoinUID(new byte[]{-19}),
    ContentEncodings(new byte[]{109, BER.ASN_LONG_LEN}, EbmlMaster.class),
    ContentEncoding(new byte[]{98, 64}, EbmlMaster.class),
    ContentEncodingOrder(new byte[]{80, 49}),
    ContentEncodingScope(new byte[]{80, 50}),
    ContentEncodingType(new byte[]{80, 51}),
    ContentCompression(new byte[]{80, 52}, EbmlMaster.class),
    ContentCompAlgo(new byte[]{66, 84}),
    ContentCompSettings(new byte[]{66, 85}, EbmlBin.class),
    ContentEncryption(new byte[]{80, 53}, EbmlMaster.class),
    ContentEncAlgo(new byte[]{71, -31}),
    ContentEncKeyID(new byte[]{71, -30}, EbmlBin.class),
    ContentSignature(new byte[]{71, -29}, EbmlBin.class),
    ContentSigKeyID(new byte[]{71, -28}, EbmlBin.class),
    ContentSigAlgo(new byte[]{71, -27}),
    ContentSigHashAlgo(new byte[]{71, -26}),
    Cues(new byte[]{28, 83, -69, 107}, EbmlMaster.class),
    CuePoint(new byte[]{-69}, EbmlMaster.class),
    CueTime(new byte[]{-77}, EbmlUint.class),
    CueTrackPositions(new byte[]{-73}, EbmlMaster.class),
    CueTrack(new byte[]{-9}, EbmlUint.class),
    CueClusterPosition(new byte[]{-15}, EbmlUint.class),
    CueRelativePosition(new byte[]{-16}),
    CueDuration(new byte[]{-78}),
    CueBlockNumber(new byte[]{83, 120}),
    CueCodecState(new byte[]{-22}),
    CueReference(new byte[]{-37}, EbmlMaster.class),
    CueRefTime(new byte[]{-106}),
    Attachments(new byte[]{25, 65, -92, 105}, EbmlMaster.class),
    AttachedFile(new byte[]{97, -89}, EbmlMaster.class),
    FileDescription(new byte[]{70, 126}, EbmlString.class),
    FileName(new byte[]{70, 110}, EbmlString.class),
    FileMimeType(new byte[]{70, 96}, EbmlString.class),
    FileData(new byte[]{70, 92}, EbmlBin.class),
    FileUID(new byte[]{70, -82}),
    Chapters(new byte[]{16, 67, -89, 112}, EbmlMaster.class),
    EditionEntry(new byte[]{69, -71}, EbmlMaster.class),
    EditionUID(new byte[]{69, -68}),
    EditionFlagHidden(new byte[]{69, -67}),
    EditionFlagDefault(new byte[]{69, -37}),
    EditionFlagOrdered(new byte[]{69, -35}),
    ChapterAtom(new byte[]{-74}, EbmlMaster.class),
    ChapterUID(new byte[]{115, -60}),
    ChapterStringUID(new byte[]{86, 84}, EbmlString.class),
    ChapterTimeStart(new byte[]{-111}),
    ChapterTimeEnd(new byte[]{-110}),
    ChapterFlagHidden(new byte[]{-104}),
    ChapterFlagEnabled(new byte[]{69, -104}),
    ChapterSegmentUID(new byte[]{110, 103}, EbmlBin.class),
    ChapterSegmentEditionUID(new byte[]{110, -68}),
    ChapterPhysicalEquiv(new byte[]{99, -61}),
    ChapterTrack(new byte[]{-113}, EbmlMaster.class),
    ChapterTrackNumber(new byte[]{-119}),
    ChapterDisplay(new byte[]{BER.ASN_LONG_LEN}, EbmlMaster.class),
    ChapString(new byte[]{-123}, EbmlString.class),
    ChapLanguage(new byte[]{67, 124}, EbmlString.class),
    ChapCountry(new byte[]{67, 126}, EbmlString.class),
    ChapProcess(new byte[]{105, 68}, EbmlMaster.class),
    ChapProcessCodecID(new byte[]{105, 85}),
    ChapProcessPrivate(new byte[]{69, 13}, EbmlBin.class),
    ChapProcessCommand(new byte[]{105, 17}, EbmlMaster.class),
    ChapProcessTime(new byte[]{105, 34}),
    ChapProcessData(new byte[]{105, 51}, EbmlBin.class),
    Tags(new byte[]{18, 84, -61, 103}, EbmlMaster.class),
    Tag(new byte[]{115, 115}, EbmlMaster.class),
    Targets(new byte[]{99, -64}, EbmlMaster.class),
    TargetTypeValue(new byte[]{104, -54}),
    TargetType(new byte[]{99, -54}, EbmlString.class),
    TagTrackUID(new byte[]{99, -59}),
    TagEditionUID(new byte[]{99, -55}),
    TagChapterUID(new byte[]{99, -60}),
    TagAttachmentUID(new byte[]{99, -58}),
    SimpleTag(new byte[]{103, -56}, EbmlMaster.class),
    TagName(new byte[]{69, -93}, EbmlString.class),
    TagLanguage(new byte[]{68, 122}, EbmlString.class),
    TagDefault(new byte[]{68, -124}),
    TagString(new byte[]{68, -121}, EbmlString.class),
    TagBinary(new byte[]{68, -123}, EbmlBin.class);

    public final Class<? extends EbmlBase> clazz;

    /* renamed from: id */
    public final byte[] f1537id;
    public static MKVType[] firstLevelHeaders = {SeekHead, Info, Cluster, Tracks, Cues, Attachments, Chapters, Tags, EBMLVersion, EBMLReadVersion, EBMLMaxIDLength, EBMLMaxSizeLength, DocType, DocTypeVersion, DocTypeReadVersion};
    public static final Map<MKVType, Set<MKVType>> children = new HashMap();

    static {
        children.put(EBML, new HashSet(Arrays.asList(EBMLVersion, EBMLReadVersion, EBMLMaxIDLength, EBMLMaxSizeLength, DocType, DocTypeVersion, DocTypeReadVersion)));
        children.put(Segment, new HashSet(Arrays.asList(SeekHead, Info, Cluster, Tracks, Cues, Attachments, Chapters, Tags)));
        children.put(SeekHead, new HashSet(Arrays.asList(Seek)));
        children.put(Seek, new HashSet(Arrays.asList(SeekID, SeekPosition)));
        children.put(Info, new HashSet(Arrays.asList(SegmentUID, SegmentFilename, PrevUID, PrevFilename, NextUID, NextFilenam, SegmentFamily, ChapterTranslate, TimecodeScale, Duration, DateUTC, Title, MuxingApp, WritingApp)));
        children.put(ChapterTranslate, new HashSet(Arrays.asList(ChapterTranslateEditionUID, ChapterTranslateCodec, ChapterTranslateID)));
        children.put(Cluster, new HashSet(Arrays.asList(Timecode, SilentTracks, Position, PrevSize, SimpleBlock, BlockGroup)));
        children.put(SilentTracks, new HashSet(Arrays.asList(SilentTrackNumber)));
        children.put(BlockGroup, new HashSet(Arrays.asList(Block, BlockAdditions, BlockDuration, ReferencePriority, ReferenceBlock, CodecState, Slices)));
        children.put(BlockAdditions, new HashSet(Arrays.asList(BlockMore)));
        children.put(BlockMore, new HashSet(Arrays.asList(BlockAddID, BlockAdditional)));
        children.put(Slices, new HashSet(Arrays.asList(TimeSlice)));
        children.put(TimeSlice, new HashSet(Arrays.asList(LaceNumber)));
        children.put(Tracks, new HashSet(Arrays.asList(TrackEntry)));
        children.put(TrackEntry, new HashSet(Arrays.asList(TrackNumber, TrackUID, TrackType, TrackType, FlagDefault, FlagForced, FlagLacing, MinCache, MaxCache, DefaultDuration, MaxBlockAdditionID, Name, Language, CodecID, CodecPrivate, CodecName, AttachmentLink, CodecDecodeAll, TrackOverlay, TrackTranslate, Video, Audio, TrackOperation, ContentEncodings)));
        children.put(TrackTranslate, new HashSet(Arrays.asList(TrackTranslateEditionUID, TrackTranslateCodec, TrackTranslateTrackID)));
        children.put(Video, new HashSet(Arrays.asList(FlagInterlaced, StereoMode, AlphaMode, PixelWidth, PixelHeight, PixelCropBottom, PixelCropTop, PixelCropLeft, PixelCropRight, DisplayWidth, DisplayHeight, DisplayUnit, AspectRatioType, ColourSpace)));
        children.put(Audio, new HashSet(Arrays.asList(SamplingFrequency, OutputSamplingFrequency, Channels, BitDepth)));
        children.put(TrackOperation, new HashSet(Arrays.asList(TrackCombinePlanes, TrackJoinBlocks)));
        children.put(TrackCombinePlanes, new HashSet(Arrays.asList(TrackPlane)));
        children.put(TrackPlane, new HashSet(Arrays.asList(TrackPlaneUID, TrackPlaneType)));
        children.put(TrackJoinBlocks, new HashSet(Arrays.asList(TrackJoinUID)));
        children.put(ContentEncodings, new HashSet(Arrays.asList(ContentEncoding)));
        children.put(ContentEncoding, new HashSet(Arrays.asList(ContentEncodingOrder, ContentEncodingScope, ContentEncodingType, ContentCompression, ContentEncryption)));
        children.put(ContentCompression, new HashSet(Arrays.asList(ContentCompAlgo, ContentCompSettings)));
        children.put(ContentEncryption, new HashSet(Arrays.asList(ContentEncAlgo, ContentEncKeyID, ContentSignature, ContentSigKeyID, ContentSigAlgo, ContentSigHashAlgo)));
        children.put(Cues, new HashSet(Arrays.asList(CuePoint)));
        children.put(CuePoint, new HashSet(Arrays.asList(CueTime, CueTrackPositions)));
        children.put(CueTrackPositions, new HashSet(Arrays.asList(CueTrack, CueClusterPosition, CueRelativePosition, CueDuration, CueBlockNumber, CueCodecState, CueReference)));
        children.put(CueReference, new HashSet(Arrays.asList(CueRefTime)));
        children.put(Attachments, new HashSet(Arrays.asList(AttachedFile)));
        children.put(AttachedFile, new HashSet(Arrays.asList(FileDescription, FileName, FileMimeType, FileData, FileUID)));
        children.put(Chapters, new HashSet(Arrays.asList(EditionEntry)));
        children.put(EditionEntry, new HashSet(Arrays.asList(EditionUID, EditionFlagHidden, EditionFlagDefault, EditionFlagOrdered, ChapterAtom)));
        children.put(ChapterAtom, new HashSet(Arrays.asList(ChapterUID, ChapterStringUID, ChapterTimeStart, ChapterTimeEnd, ChapterFlagHidden, ChapterFlagEnabled, ChapterSegmentUID, ChapterSegmentEditionUID, ChapterPhysicalEquiv, ChapterTrack, ChapterDisplay, ChapProcess)));
        children.put(ChapterTrack, new HashSet(Arrays.asList(ChapterTrackNumber)));
        children.put(ChapterDisplay, new HashSet(Arrays.asList(ChapString, ChapLanguage, ChapCountry)));
        children.put(ChapProcess, new HashSet(Arrays.asList(ChapProcessCodecID, ChapProcessPrivate, ChapProcessCommand)));
        children.put(ChapProcessCommand, new HashSet(Arrays.asList(ChapProcessTime, ChapProcessData)));
        children.put(Tags, new HashSet(Arrays.asList(Tag)));
        children.put(Tag, new HashSet(Arrays.asList(Targets, SimpleTag)));
        children.put(Targets, new HashSet(Arrays.asList(TargetTypeValue, TargetType, TagTrackUID, TagEditionUID, TagChapterUID, TagAttachmentUID)));
        children.put(SimpleTag, new HashSet(Arrays.asList(TagName, TagLanguage, TagDefault, TagString, TagBinary)));
    }

    MKVType(byte[] id) {
        this.f1537id = id;
        this.clazz = EbmlUint.class;
    }

    MKVType(byte[] id, Class cls) {
        this.f1537id = id;
        this.clazz = cls;
    }

    public static <T extends EbmlBase> T createByType(MKVType g) {
        try {
            T elem = (T) create(g.clazz, g.f1537id);
            elem.type = g;
            return elem;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            T elem2 = new EbmlBin(g.f1537id);
            return elem2;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            T elem3 = new EbmlBin(g.f1537id);
            return elem3;
        } catch (InstantiationException e3) {
            e3.printStackTrace();
            T elem4 = new EbmlBin(g.f1537id);
            return elem4;
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
            T elem5 = new EbmlBin(g.f1537id);
            return elem5;
        } catch (SecurityException e5) {
            e5.printStackTrace();
            T elem6 = new EbmlBin(g.f1537id);
            return elem6;
        } catch (InvocationTargetException e6) {
            e6.printStackTrace();
            T elem7 = new EbmlBin(g.f1537id);
            return elem7;
        }
    }

    private static <T extends EbmlBase> T create(Class<T> clazz, byte[] id) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> c = clazz.getConstructor(byte[].class);
        return c.newInstance(id);
    }

    public static <T extends EbmlBase> T createById(byte[] id, long offset) {
        MKVType[] arr$ = values();
        for (MKVType t : arr$) {
            if (Arrays.equals(t.f1537id, id)) {
                return (T) createByType(t);
            }
        }
        System.err.println("WARNING: unspecified ebml ID (" + EbmlUtil.toHexString(id) + ") encountered at position 0x" + Long.toHexString(offset).toUpperCase());
        T t2 = new EbmlVoid(id);
        t2.type = Void;
        return t2;
    }

    public static boolean isHeaderFirstByte(byte b) {
        MKVType[] arr$ = values();
        for (MKVType t : arr$) {
            if (t.f1537id[0] == b) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSpecifiedHeader(byte[] b) {
        MKVType[] arr$ = values();
        for (MKVType firstLevelHeader : arr$) {
            if (Arrays.equals(firstLevelHeader.f1537id, b)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFirstLevelHeader(byte[] b) {
        MKVType[] arr$ = firstLevelHeaders;
        for (MKVType firstLevelHeader : arr$) {
            if (Arrays.equals(firstLevelHeader.f1537id, b)) {
                return true;
            }
        }
        return false;
    }

    public static MKVType getParent(MKVType t) {
        for (Map.Entry<MKVType, Set<MKVType>> ent : children.entrySet()) {
            if (ent.getValue().contains(t)) {
                return ent.getKey();
            }
        }
        return null;
    }

    public static boolean possibleChild(EbmlMaster parent, EbmlBase child) {
        if (parent == null) {
            return child.type == EBML || child.type == Segment;
        }
        if (Arrays.equals(child.f1540id, Void.f1537id) || Arrays.equals(child.f1540id, CRC32.f1537id)) {
            return child.offset != parent.dataOffset + ((long) parent.dataLen);
        }
        if (child.type == Void || child.type == CRC32) {
            return true;
        }
        Set<MKVType> candidates = children.get(parent.type);
        return candidates != null && candidates.contains(child.type);
    }

    public static boolean possibleChild(EbmlMaster parent, byte[] typeId) {
        if (parent == null && (Arrays.equals(EBML.f1537id, typeId) || Arrays.equals(Segment.f1537id, typeId))) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        if (Arrays.equals(Void.f1537id, typeId) || Arrays.equals(CRC32.f1537id, typeId)) {
            return true;
        }
        for (MKVType aCandidate : children.get(parent.type)) {
            if (Arrays.equals(aCandidate.f1537id, typeId)) {
                return true;
            }
        }
        return false;
    }

    public static EbmlBase findFirst(EbmlBase master, MKVType... path) {
        List<MKVType> tlist = new LinkedList<>(Arrays.asList(path));
        return findFirstSub(master, tlist);
    }

    public static <T> T findFirst(List<? extends EbmlBase> tree, MKVType... path) {
        List<MKVType> tlist = new LinkedList<>(Arrays.asList(path));
        for (EbmlBase e : tree) {
            T t = (T) findFirstSub(e, tlist);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    private static EbmlBase findFirstSub(EbmlBase elem, List<MKVType> path) {
        if (path.size() != 0 && elem.type.equals(path.get(0))) {
            if (path.size() != 1) {
                MKVType head = path.remove(0);
                EbmlBase result = null;
                if (elem instanceof EbmlMaster) {
                    Iterator<EbmlBase> iter = ((EbmlMaster) elem).children.iterator();
                    while (iter.hasNext() && result == null) {
                        result = findFirstSub(iter.next(), path);
                    }
                }
                path.add(0, head);
                return result;
            }
            return elem;
        }
        return null;
    }

    public static <T> List<T> findList(List<? extends EbmlBase> tree, Class<T> class1, MKVType... path) {
        List<T> result = new LinkedList<>();
        List<MKVType> tlist = new LinkedList<>(Arrays.asList(path));
        if (tlist.size() > 0) {
            for (EbmlBase node : tree) {
                MKVType head = tlist.remove(0);
                if (head == null || head.equals(node.type)) {
                    findSubList(node, tlist, result);
                }
                tlist.add(0, head);
            }
        }
        return result;
    }

    private static <T> void findSubList(EbmlBase element, List<MKVType> path, Collection<T> result) {
        if (path.size() > 0) {
            MKVType head = path.remove(0);
            if (element instanceof EbmlMaster) {
                EbmlMaster nb = (EbmlMaster) element;
                Iterator i$ = nb.children.iterator();
                while (i$.hasNext()) {
                    EbmlBase candidate = i$.next();
                    if (head == null || head.equals(candidate.type)) {
                        findSubList(candidate, path, result);
                    }
                }
            }
            path.add(0, head);
            return;
        }
        result.add(element);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T[] findAll(List<? extends EbmlBase> tree, Class<T> class1, MKVType... path) {
        List<EbmlBase> result = new LinkedList<>();
        List<MKVType> tlist = new LinkedList<>(Arrays.asList(path));
        if (tlist.size() > 0) {
            for (EbmlBase node : tree) {
                MKVType head = tlist.remove(0);
                if (head == null || head.equals(node.type)) {
                    findSub(node, tlist, result);
                }
                tlist.add(0, head);
            }
        }
        return (T[]) result.toArray((Object[]) Array.newInstance((Class<?>) class1, 0));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T[] findAll(EbmlBase master, Class<T> class1, MKVType... path) {
        List<EbmlBase> result = new LinkedList<>();
        List<MKVType> tlist = new LinkedList<>(Arrays.asList(path));
        if (!master.type.equals(tlist.get(0))) {
            return (T[]) result.toArray((Object[]) Array.newInstance((Class<?>) class1, 0));
        }
        tlist.remove(0);
        findSub(master, tlist, result);
        return (T[]) result.toArray((Object[]) Array.newInstance((Class<?>) class1, 0));
    }

    private static void findSub(EbmlBase master, List<MKVType> path, Collection<EbmlBase> result) {
        if (path.size() > 0) {
            MKVType head = path.remove(0);
            if (master instanceof EbmlMaster) {
                EbmlMaster nb = (EbmlMaster) master;
                Iterator i$ = nb.children.iterator();
                while (i$.hasNext()) {
                    EbmlBase candidate = i$.next();
                    if (head == null || head.equals(candidate.type)) {
                        findSub(candidate, path, result);
                    }
                }
            }
            path.add(0, head);
            return;
        }
        result.add(master);
    }
}
