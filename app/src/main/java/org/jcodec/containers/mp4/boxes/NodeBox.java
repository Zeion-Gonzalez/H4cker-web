package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.tools.ToJSON;

/* loaded from: classes.dex */
public class NodeBox extends Box {
    private static final int MAX_BOX_SIZE = 134217728;
    protected List<Box> boxes;
    protected BoxFactory factory;

    public NodeBox(Header atom) {
        super(atom);
        this.boxes = new LinkedList();
        this.factory = BoxFactory.getDefault();
    }

    public NodeBox(NodeBox other) {
        super(other);
        this.boxes = new LinkedList();
        this.factory = BoxFactory.getDefault();
        this.boxes = other.boxes;
        this.factory = other.factory;
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        while (input.remaining() >= 8) {
            Box child = parseChildBox(input, this.factory);
            if (child != null) {
                this.boxes.add(child);
            }
        }
    }

    public static Box parseChildBox(ByteBuffer input, BoxFactory factory) {
        Header childAtom;
        ByteBuffer fork = input.duplicate();
        while (input.remaining() >= 4 && fork.getInt() == 0) {
            input.getInt();
        }
        if (input.remaining() >= 4 && (childAtom = Header.read(input)) != null && input.remaining() >= childAtom.getBodySize()) {
            return parseBox(NIOUtils.read(input, (int) childAtom.getBodySize()), childAtom, factory);
        }
        return null;
    }

    public static Box newBox(Header header, BoxFactory factory) {
        Class<? extends Box> claz = factory.toClass(header.getFourcc());
        if (claz == null) {
            return new LeafBox(header);
        }
        try {
            try {
                return claz.getConstructor(Header.class).newInstance(header);
            } catch (NoSuchMethodException e) {
                return claz.newInstance();
            }
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    public static Box parseBox(ByteBuffer input, Header childAtom, BoxFactory factory) {
        Box box = newBox(childAtom, factory);
        if (childAtom.getBodySize() >= 134217728) {
            return new LeafBox(new Header("free", 8L));
        }
        box.parse(input);
        return box;
    }

    public List<Box> getBoxes() {
        return this.boxes;
    }

    public void add(Box box) {
        this.boxes.add(box);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        for (Box box : this.boxes) {
            box.write(out);
        }
    }

    public void addFirst(MovieHeaderBox box) {
        this.boxes.add(0, box);
    }

    public void replace(String fourcc, Box box) {
        removeChildren(fourcc);
        add(box);
    }

    public void replace(Box box) {
        removeChildren(box.getFourcc());
        add(box);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.Box
    public void dump(StringBuilder sb) {
        sb.append("{\"tag\":\"" + this.header.getFourcc() + "\",");
        List<String> fields = new ArrayList<>(0);
        collectModel(getClass(), fields);
        ToJSON.fieldsToJSON(this, sb, (String[]) fields.toArray(new String[0]));
        sb.append("\"boxes\": [");
        dumpBoxes(sb);
        sb.append("]");
        sb.append("}");
    }

    protected void getModelFields(List<String> model) {
    }

    protected void dumpBoxes(StringBuilder sb) {
        for (int i = 0; i < this.boxes.size(); i++) {
            this.boxes.get(i).dump(sb);
            if (i < this.boxes.size() - 1) {
                sb.append(",");
            }
        }
    }

    public void removeChildren(String... fourcc) {
        Iterator<Box> it = this.boxes.iterator();
        while (it.hasNext()) {
            Box box = it.next();
            String fcc = box.getFourcc();
            int len$ = fourcc.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    String cand = fourcc[i$];
                    if (!cand.equals(fcc)) {
                        i$++;
                    } else {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }
}
