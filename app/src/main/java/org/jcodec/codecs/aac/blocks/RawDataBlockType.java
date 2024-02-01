package org.jcodec.codecs.aac.blocks;

import java.util.EnumSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public enum RawDataBlockType {
    TYPE_SCE,
    TYPE_CPE,
    TYPE_CCE,
    TYPE_LFE,
    TYPE_DSE,
    TYPE_PCE,
    TYPE_FIL,
    TYPE_END;

    public static RawDataBlockType fromOrdinal(int ordinal) {
        Iterator i$ = EnumSet.allOf(RawDataBlockType.class).iterator();
        while (i$.hasNext()) {
            RawDataBlockType val = (RawDataBlockType) i$.next();
            if (val.ordinal() == ordinal) {
                return val;
            }
        }
        return null;
    }
}
