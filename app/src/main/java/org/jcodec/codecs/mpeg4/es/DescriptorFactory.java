package org.jcodec.codecs.mpeg4.es;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class DescriptorFactory {
    private static Map<Integer, Class<? extends Descriptor>> map = new HashMap();

    static {
        map.put(Integer.valueOf(C0865ES.tag()), C0865ES.class);
        map.put(Integer.valueOf(C0866SL.tag()), C0866SL.class);
        map.put(Integer.valueOf(DecoderConfig.tag()), DecoderConfig.class);
        map.put(Integer.valueOf(DecoderSpecific.tag()), DecoderSpecific.class);
    }

    public Class<? extends Descriptor> byTag(int tag) {
        return map.get(Integer.valueOf(tag));
    }
}
