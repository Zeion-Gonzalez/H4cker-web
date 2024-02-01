package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class DataRefBox extends NodeBox {
    private static final MyFactory FACTORY = new MyFactory();

    public static String fourcc() {
        return "dref";
    }

    public DataRefBox() {
        this(new Header(fourcc()));
    }

    private DataRefBox(Header atom) {
        super(atom);
        this.factory = FACTORY;
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        input.getInt();
        input.getInt();
        super.parse(input);
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        out.putInt(0);
        out.putInt(this.boxes.size());
        super.doWrite(out);
    }

    /* loaded from: classes.dex */
    public static class MyFactory extends BoxFactory {
        private Map<String, Class<? extends Box>> mappings = new HashMap();

        public MyFactory() {
            this.mappings.put(UrlBox.fourcc(), UrlBox.class);
            this.mappings.put(AliasBox.fourcc(), AliasBox.class);
            this.mappings.put("cios", AliasBox.class);
        }

        @Override // org.jcodec.containers.mp4.boxes.BoxFactory
        public Class<? extends Box> toClass(String fourcc) {
            return this.mappings.get(fourcc);
        }
    }
}
