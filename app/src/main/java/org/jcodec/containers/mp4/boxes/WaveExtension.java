package org.jcodec.containers.mp4.boxes;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class WaveExtension extends NodeBox {
    private static final MyFactory FACTORY = new MyFactory();

    public static String fourcc() {
        return "wave";
    }

    public WaveExtension(Header atom) {
        super(atom);
        this.factory = FACTORY;
    }

    /* loaded from: classes.dex */
    public static class MyFactory extends BoxFactory {
        private Map<String, Class<? extends Box>> mappings = new HashMap();

        public MyFactory() {
            this.mappings.put(FormatBox.fourcc(), FormatBox.class);
            this.mappings.put(EndianBox.fourcc(), EndianBox.class);
        }

        @Override // org.jcodec.containers.mp4.boxes.BoxFactory
        public Class<? extends Box> toClass(String fourcc) {
            return this.mappings.get(fourcc);
        }
    }
}
