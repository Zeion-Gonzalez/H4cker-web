package org.jcodec.containers.mp4.boxes;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class AliasBox extends FullBox {
    public static final int AbsolutePath = 2;
    public static final int AppleRemoteAccessDialup = 10;
    public static final int AppleShareServerName = 4;
    public static final int AppleShareUserName = 5;
    public static final int AppleShareZoneName = 3;
    public static final int DirectoryIDs = 1;
    public static final int DirectoryName = 0;
    public static final int DriverName = 6;
    public static final int RevisedAppleShare = 9;
    public static final int UFT16VolumeName = 15;
    public static final int UNIXAbsolutePath = 18;
    public static final int UTF16AbsolutePath = 14;
    public static final int VolumeMountPoint = 19;
    private static Set<Integer> utf16 = new HashSet();
    private int createdLocalDate;
    private String creatorName;
    private List<ExtraField> extra;
    private String fileName;
    private int fileNumber;
    private String fileTypeName;
    private short fsId;
    private short kind;
    private short nlvlFrom;
    private short nlvlTo;
    private int parentDirId;
    private short recordSize;
    private String type;
    private short version;
    private int volumeAttributes;
    private int volumeCreateDate;
    private String volumeName;
    private short volumeSignature;
    private short volumeType;

    public static String fourcc() {
        return "alis";
    }

    static {
        utf16.add(14);
        utf16.add(15);
    }

    /* loaded from: classes.dex */
    public static class ExtraField {
        byte[] data;
        int len;
        short type;

        public ExtraField(short type, int len, byte[] bs) {
            this.type = type;
            this.len = len;
            this.data = bs;
        }

        public String toString() {
            try {
                return new String(this.data, 0, this.len, AliasBox.utf16.contains(Short.valueOf(this.type)) ? "UTF-16" : "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
    }

    public AliasBox() {
        super(new Header(fourcc(), 0L));
    }

    public AliasBox(Header atom) {
        super(atom);
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer is) {
        super.parse(is);
        if ((this.flags & 1) == 0) {
            this.type = NIOUtils.readString(is, 4);
            this.recordSize = is.getShort();
            this.version = is.getShort();
            this.kind = is.getShort();
            this.volumeName = NIOUtils.readPascalString(is, 27);
            this.volumeCreateDate = is.getInt();
            this.volumeSignature = is.getShort();
            this.volumeType = is.getShort();
            this.parentDirId = is.getInt();
            this.fileName = NIOUtils.readPascalString(is, 63);
            this.fileNumber = is.getInt();
            this.createdLocalDate = is.getInt();
            this.fileTypeName = NIOUtils.readString(is, 4);
            this.creatorName = NIOUtils.readString(is, 4);
            this.nlvlFrom = is.getShort();
            this.nlvlTo = is.getShort();
            this.volumeAttributes = is.getInt();
            this.fsId = is.getShort();
            NIOUtils.skip(is, 10);
            this.extra = new ArrayList();
            while (true) {
                short type = is.getShort();
                if (type != -1) {
                    int len = is.getShort();
                    byte[] bs = NIOUtils.toArray(NIOUtils.read(is, (len + 1) & (-2)));
                    if (bs != null) {
                        this.extra.add(new ExtraField(type, len, bs));
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        if ((this.flags & 1) == 0) {
            out.put(JCodecUtil.asciiString(this.type), 0, 4);
            out.putShort(this.recordSize);
            out.putShort(this.version);
            out.putShort(this.kind);
            NIOUtils.writePascalString(out, this.volumeName, 27);
            out.putInt(this.volumeCreateDate);
            out.putShort(this.volumeSignature);
            out.putShort(this.volumeType);
            out.putInt(this.parentDirId);
            NIOUtils.writePascalString(out, this.fileName, 63);
            out.putInt(this.fileNumber);
            out.putInt(this.createdLocalDate);
            out.put(JCodecUtil.asciiString(this.fileTypeName), 0, 4);
            out.put(JCodecUtil.asciiString(this.creatorName), 0, 4);
            out.putShort(this.nlvlFrom);
            out.putShort(this.nlvlTo);
            out.putInt(this.volumeAttributes);
            out.putShort(this.fsId);
            out.put(new byte[10]);
            for (ExtraField extraField : this.extra) {
                out.putShort(extraField.type);
                out.putShort((short) extraField.len);
                out.put(extraField.data);
            }
            out.putShort((short) -1);
            out.putShort((short) 0);
        }
    }

    public int getRecordSize() {
        return this.recordSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public List<ExtraField> getExtra() {
        return this.extra;
    }

    public ExtraField getExtra(int type) {
        for (ExtraField extraField : this.extra) {
            if (extraField.type == type) {
                return extraField;
            }
        }
        return null;
    }

    public boolean isSelfRef() {
        return (this.flags & 1) != 0;
    }

    public static AliasBox createSelfRef() {
        AliasBox alis = new AliasBox();
        alis.setFlags(1);
        return alis;
    }

    public String getUnixPath() {
        ExtraField extraField = getExtra(18);
        if (extraField == null) {
            return null;
        }
        return "/" + extraField.toString();
    }
}
